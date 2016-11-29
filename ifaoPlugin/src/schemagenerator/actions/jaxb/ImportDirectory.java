package schemagenerator.actions.jaxb;


import ifaoplugin.*;

import java.io.*;
import java.util.*;

import net.ifao.xml.*;
import schemagenerator.actions.*;


public class ImportDirectory
{

   private PrintStream _pOut;

   public ImportDirectory(PrintStream pOut)
   {
      _pOut = pOut;
   }

   public void importXsdFiles(File pRootDirectory, String psRootPackage)
      throws Exception
   {
      importXsdFiles(pRootDirectory, psRootPackage, true);
   }

   public void importXsdFiles(File pRootDirectory, String psRootPackage,
                              boolean pbUseTargetNamespace)
      throws Exception
   {
      boolean pbCreateWithinOneDirectory = !pbUseTargetNamespace;
      Hashtable<File, XmlObject> htFiles = new Hashtable<File, XmlObject>();
      Hashtable<File, String> hsFilePackage = new Hashtable<File, String>();
      String sPackageDir = psRootPackage.replaceAll("\\.", "/");
      File pInputDirectory = new File(pRootDirectory, sPackageDir);
      if (pInputDirectory.isDirectory()) {
         File[] listFiles = pInputDirectory.listFiles();
         for (File file : listFiles) {
            String sFileName = file.getName();
            if (sFileName.endsWith(".xsd") && !sFileName.startsWith("data.")) {
               // found a new xsd file
               htFiles.put(file, new XmlObject(file).getFirstObject());
            }
         }
      }

      if (htFiles.size() == 0) {
         throw new RuntimeException("No files found within " + pInputDirectory.getAbsolutePath());
      }

      // find value attribute (which make problems)
      Hashtable<File, XmlObject[]> correctionValues = findValueAttributes(htFiles);

      XmlObject jxbRoot = new XmlObject("<jxb:bindings />").getFirstObject();
      jxbRoot.setAttribute("xmlns:jxb", "http://java.sun.com/xml/ns/jaxb");
      jxbRoot.setAttribute("xmlns", "http://java.sun.com/xml/ns/jaxb");
      jxbRoot.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
      jxbRoot.setAttribute("xmlns:jaxb", "http://java.sun.com/xml/ns/jaxb");
      jxbRoot.setAttribute("xmlns:xjc", "http://java.sun.com/xml/ns/jaxb/xjc");
      jxbRoot.setAttribute("jaxb:version", "1.0");
      jxbRoot.setAttribute("jaxb:extensionBindingPrefixes", "xjc");
      jxbRoot.createObject("jxb:bindings", "schemaLocation", "data.xsd", true)
            .createObject("jxb:schemaBindings").createObject("jxb:package")
            .setAttribute("name", psRootPackage);

      XmlObject xsdObjectRoot = new XmlObject("<xs:schema />").getFirstObject();
      xsdObjectRoot.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
      xsdObjectRoot.setAttribute("elementFormDefault", "qualified");

      // loop over the found files
      for (File file : htFiles.keySet()) {
         // get the related xml object
         XmlObject xsdObject = htFiles.get(file);
         if (pbCreateWithinOneDirectory) {
            for (String sAttribute : new String[]{ "targetNamespace", "xmlns" }) {
               String sRootAttribute = xsdObjectRoot.getAttribute(sAttribute);
               String sNewAttribute = xsdObject.getAttribute(sAttribute);
               if (sRootAttribute.length() == 0) {
                  xsdObjectRoot.setAttribute(sAttribute, sNewAttribute);
               } else if ((sNewAttribute.length() > 0) && !sNewAttribute.equals(sRootAttribute)) {
                  throw new RuntimeException("Attention file '" + file.getName() + "' contains "
                        + sAttribute + ":" + sNewAttribute + " ... but should be " + sRootAttribute);
               }
            }
            xsdObjectRoot.createObject("xs:include", "schemaLocation", file.getName(), true);
         }

         String sFileName = file.getName();
         println("Read " + sFileName);
         if (htFiles.size() == 1) {
            sFileName = "";
         }
         String sDir = getDir(sFileName, '/');
         String sUp = "";
         int iCount = new StringTokenizer(sDir, "/").countTokens();
         for (int i = 0; i < iCount; i++) {
            sUp += "../";
         }

         XmlObject jxb = new XmlObject("<jxb:bindings />").getFirstObject();
         jxb.setAttribute("xmlns:jxb", "http://java.sun.com/xml/ns/jaxb");
         jxb.setAttribute("xmlns", "http://java.sun.com/xml/ns/jaxb");
         jxb.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
         jxb.setAttribute("xmlns:jaxb", "http://java.sun.com/xml/ns/jaxb");
         jxb.setAttribute("xmlns:xjc", "http://java.sun.com/xml/ns/jaxb/xjc");
         jxb.setAttribute("jaxb:version", "1.0");
         jxb.setAttribute("jaxb:extensionBindingPrefixes", "xjc");

         XmlObject bindings;
         if (pbCreateWithinOneDirectory) {
            bindings = jxb.createObject("jxb:bindings", "schemaLocation", file.getName(), true);
            jxbRoot.addObject(bindings);
         } else {
            bindings = jxb.createObject("jxb:bindings", "schemaLocation", "data.xsd", true);
            bindings
                  .createObject("jxb:schemaBindings")
                  .createObject("jxb:package")
                  .setAttribute(
                        "name",
                        getPackage(pInputDirectory, sFileName, psRootPackage, hsFilePackage,
                              pbUseTargetNamespace));
         }

         EnumBindingCorrector enumCorrector = new EnumBindingCorrector(xsdObject, bindings);
         enumCorrector.addBindingsForSimpleTypeEnums();

         // add correction object
         XmlObject[] xmlObjects = correctionValues.get(file);
         if (xmlObjects != null) {
            bindings.addObjects(xmlObjects);
         }

         XmlObject[] imports = xsdObject.getObjects("import");
         for (XmlObject import1 : imports) {
            createBindingWithPackage(pInputDirectory, jxb, sUp, import1, psRootPackage,
                  hsFilePackage, correctionValues, pbUseTargetNamespace);
         }
         imports = xsdObject.getObjects("include");
         for (XmlObject import1 : imports) {
            createBindingWithPackage(pInputDirectory, jxb, sUp, import1, psRootPackage,
                  hsFilePackage, correctionValues, pbUseTargetNamespace);
         }

         correctBindings(bindings, xsdObject);
         if (!pbCreateWithinOneDirectory) {
            File fileDataXsd = new File(file.getParentFile(), sDir + "/data.xsd");
            println("Write file " + fileDataXsd.getAbsolutePath());
            Utils.writeFile(fileDataXsd, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                  + new String((xsdObject.toString()).getBytes(), "UTF-8"));
            File fileBindingsXjb = new File(file.getParentFile(), sDir + "/bindings.xjb");
            println("Write file " + fileBindingsXjb.getAbsolutePath());
            Utils.writeFile(fileBindingsXjb, jxb.toString());
         }
      }
      if (pbCreateWithinOneDirectory) {
         // remove files within subdirectories
         File[] listFiles = pInputDirectory.listFiles();
         for (File directory : listFiles) {
            if (directory.isDirectory()) {
               clear(directory);
            }
         }
         File fileDataXsd = new File(pInputDirectory, "data.xsd");
         println("Write file " + fileDataXsd.getAbsolutePath());
         Utils.writeFile(fileDataXsd, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
               + new String((xsdObjectRoot.toString()).getBytes(), "UTF-8"));
         File fileBindingsXjb = new File(pInputDirectory, "bindings.xjb");
         println("Write file " + fileBindingsXjb.getAbsolutePath());
         Utils.writeFile(fileBindingsXjb, jxbRoot.toString());

      }
   }

   private void clear(File directory)
   {
      if (directory.isDirectory()) {
         File[] listFiles = directory.listFiles();
         for (File file : listFiles) {
            if (file.isDirectory()) {
               clear(file);
            } else if (file.getName().equalsIgnoreCase("data.xsd")
                  || file.getName().endsWith(".xjb")) {
               file.delete();
            }
         }
      }
   }

   private Hashtable<File, XmlObject[]> findValueAttributes(Hashtable<File, XmlObject> htFiles)
   {
      Hashtable<File, XmlObject[]> correctionValues = new Hashtable<File, XmlObject[]>();
      for (File file : htFiles.keySet()) {
         XmlObject xsdObject = htFiles.get(file);
         XmlObject bindings = new XmlObject("<bindings/>").getFirstObject();
         findValueAttributes("", xsdObject, bindings);
         // if there are any corrections
         XmlObject[] correctionObject = bindings.getObjects("");
         if (correctionObject.length > 0) {
            correctionValues.put(file, correctionObject);
         }
      }
      return correctionValues;

   }

   private void findValueAttributes(String psPath, XmlObject xsdObject, XmlObject bindings)
   {
      String sPath = psPath + "//" + xsdObject.getNameSpace() + ":" + xsdObject.getName();
      String sName = xsdObject.getAttribute("name");

      if (sPath.endsWith(":schema")) {
         // ignore root
         sPath = "";
      } else {
         if (sName.length() > 0) {
            sPath += "[@name='" + sName + "']";
         } else {
            String sBase = xsdObject.getAttribute("base");
            if (sBase.length() > 0) {
               sPath += "[@base='" + sBase + "']";
            }
         }
      }

      if (xsdObject.getName().equals("attribute") && sName.equalsIgnoreCase("Value")) {

         // sPath = 
         // //xs:complexType[@name='AccommodationClass']
         // .//xs:simpleContent
         // .//xs:extension[@base='AccommodationClassEnum']
         // .//xs:attribute[@name='Value']

         // value attribute found
         if (sPath.contains(":extension") && xsdObject.getAttribute("type").contains(":")) {
            // get the nodes
            String[] nodes = sPath.split("\\n");
            XmlObject bind = bindings;
            for (String sNode : nodes) {
               // create sub object
               bind = bind.createObject("jxb:bindings", "node", sNode, true);
            }
            bind.createObject("jxb:property", "name", "ExtensionValue", true);
         }
      } else {
         if (sPath.length() > 0) {
            sPath += "\n.";
         }
         XmlObject[] objects = xsdObject.getObjects("");
         for (XmlObject xmlObject : objects) {
            findValueAttributes(sPath, xmlObject, bindings);
         }
      }
   }

   public void createBindingsFile(File pDataXsd)
      throws IOException
   {
      XmlObject jxb = new XmlObject("<jxb:bindings />").getFirstObject();
      jxb.setAttribute("xmlns:jxb", "http://java.sun.com/xml/ns/jaxb");
      jxb.setAttribute("xmlns", "http://java.sun.com/xml/ns/jaxb");
      jxb.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
      jxb.setAttribute("xmlns:jaxb", "http://java.sun.com/xml/ns/jaxb");
      jxb.setAttribute("xmlns:xjc", "http://java.sun.com/xml/ns/jaxb/xjc");
      jxb.setAttribute("jaxb:version", "1.0");
      jxb.setAttribute("jaxb:extensionBindingPrefixes", "xjc");
      XmlObject bindings = jxb.createObject("jxb:bindings", "schemaLocation", "data.xsd", true);
      char[] carrPackage = pDataXsd.getParent().toCharArray();
      for (int i = 0; i < carrPackage.length; i++) {
         if ((carrPackage[i] == '\\') || (carrPackage[i] == '/')) {
            carrPackage[i] = '.';
         }
      }
      String sPackage = new String(carrPackage);
      sPackage = sPackage.substring(sPackage.lastIndexOf(".net.ifao.") + 1);

      bindings.createObject("jxb:schemaBindings").createObject("jxb:package")
            .setAttribute("name", sPackage);

      XmlObject xsdObject = new XmlObject(pDataXsd).getFirstObject();

      correctBindings(bindings, xsdObject);

      File fileBindingsXjb = new File(pDataXsd.getParentFile(), "bindings.xjb");
      println("Write file " + fileBindingsXjb.getAbsolutePath());
      Utils.writeFile(fileBindingsXjb, jxb.toString());
   }

   protected void println(String psText)
   {
      _pOut.println(psText);
   }

   private void createBindingWithPackage(File pRootDirectory, XmlObject jxb, String sUp,
                                         XmlObject import1, String psRootPackage,
                                         Hashtable<File, String> hsFilePackage,
                                         Hashtable<File, XmlObject[]> correctionValues,
                                         boolean pbUseTargetNamespace)
   {
      String schemaLocation = import1.getAttribute("schemaLocation");
      if (schemaLocation.length() > 0) {

         String sPackage =
            getPackage(pRootDirectory, schemaLocation, psRootPackage, hsFilePackage,
                  pbUseTargetNamespace);
         String sSchemaLocation = sUp + getDir(schemaLocation, '/') + "/data.xsd";
         import1.setAttribute("schemaLocation", sSchemaLocation);
         XmlObject scLocation =
            jxb.createObject("jxb:bindings", "schemaLocation", sSchemaLocation, true);
         XmlObject schemaBindings = scLocation.createObject("jxb:schemaBindings");
         schemaBindings.createObject("jxb:package").setAttribute("name", sPackage);

         XmlObject[] xmlObjects = correctionValues.get(new File(pRootDirectory, schemaLocation));
         if (xmlObjects != null) {
            scLocation.addObjects(xmlObjects);
         }
      }

   }

   private String getPackage(File pRootDirectory, String schemaLocation, String psRootPackage,
                             Hashtable<File, String> hsFilePackage, boolean pbUseTargetNamespace)
   {
      if (schemaLocation.length() == 0) {
         return psRootPackage;
      }
      File xsdFile = new File(pRootDirectory, schemaLocation);
      String sPackage = hsFilePackage.get(xsdFile);
      if (sPackage != null) {
         return sPackage;
      }
      sPackage = psRootPackage + "." + getDir(schemaLocation, '.');
      if (pbUseTargetNamespace) {
         try {
            XmlObject xmlObject = new XmlObject(xsdFile).getFirstObject();
            String targetNamespace = xmlObject.getAttribute("targetNamespace");
            if (targetNamespace.length() > 0) {
               sPackage = getPackage(psRootPackage, targetNamespace);
            }
         }
         catch (FileNotFoundException e) {
            // file has to exist
            e.printStackTrace();
         }
      }
      hsFilePackage.put(xsdFile, sPackage);

      return sPackage;
   }

   private String getDir(String psName, char pcDelimiter)
   {
      StringBuilder sb = new StringBuilder();
      StringTokenizer st = new StringTokenizer(psName.toLowerCase(), "_.\\/");
      while (st.hasMoreTokens()) {
         String s = st.nextToken();
         try {
            Integer.parseInt(s);
            s = "p" + s;
         }
         catch (NumberFormatException e) {
            // no number
         }
         if (st.hasMoreTokens() || !s.equals("xsd")) {
            if (sb.length() > 0) {
               sb.append(pcDelimiter);
            }
            sb.append(s);
         }
      }
      return sb.toString();
   }


   protected String getPackage(String psPackage, String targetNamespace)
   {
      String sPackage = psPackage;
      StringTokenizer st = new StringTokenizer(targetNamespace, "\\/");
      if (st.countTokens() > 0) {
         String sFirstToken = st.nextToken();
         if (sFirstToken.endsWith(":")) {
            if (st.hasMoreTokens()) {
               // consume the next token
               st.nextToken();
            }
         }
         while (st.hasMoreTokens()) {
            sPackage += ".";
            String sNextToken = st.nextToken();
            if ((sNextToken.charAt(0) >= '0') && (sNextToken.charAt(0) <= '9')) {
               sPackage += "p";
            }
            sPackage += sNextToken;
         }
      }
      return sPackage.toLowerCase();
   }

   protected void correctBindings(XmlObject bindings, XmlObject schema)
   {
      // get all elements
      HashSet<String> hsElementNames = new HashSet<String>();
      XmlObject[] xmlObjects = schema.getObjects("element");

      for (XmlObject element : xmlObjects) {
         hsElementNames.add(element.getAttribute("name").toUpperCase());
      }

      // get the ComplexType Elements
      xmlObjects = schema.getObjects("complexType");

      for (XmlObject complexType : xmlObjects) {
         correctBindings(complexType, complexType.getAttribute("name"), bindings, hsElementNames);
      }

   }

   private void correctBindings(XmlObject xmlObject, String psName, XmlObject bindings,
                                HashSet<String> hsElementNames)
   {
      XmlObject[] objects = xmlObject.getObjects("");
      for (XmlObject object : objects) {
         if (object.getName().equals("element")) {
            String sElementName = psName + object.getAttribute("name");
            if (hsElementNames.contains(sElementName.toUpperCase())) {
               // this would create the same elementName within JaxB
               // ... so we require a special binding
               bindings
                     .createObject("jxb:bindings", "node",
                           "xs:complexType[@name='" + psName + "']", true)
                     .createObject("jxb:bindings", "node",
                           ".//xs:element[@name='" + object.getAttribute("name") + "']", true)
                     .createObject("jxb:class", "name", psName + object.getAttribute("name"), true);
            }
         } else {
            correctBindings(object, psName, bindings, hsElementNames);
         }
      }
   }

   public void initOutput()
   {
      Util.setOutput(_pOut);
   }

   public void removeOutput()
   {
      Util.removeOutput();
   }

}
