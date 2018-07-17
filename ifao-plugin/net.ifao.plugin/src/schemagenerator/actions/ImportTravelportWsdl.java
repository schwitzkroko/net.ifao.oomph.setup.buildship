package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;
import java.util.*;

import schemagenerator.actions.wsdl.WsdlToJava;

import net.ifao.xml.XmlObject;


/**
 * This class implements an ImportTravelportWsdl method
 * <p>
 * Copyright &copy; 2013, i:FAO
 *
 * @author brod
 */
public class ImportTravelportWsdl
{

   /**
    * starts a to import.
    *
    * @param pfArcticRootDirectory arctic root directory File
    * @throws IOException
    *
    * @author brod
    */
   public static void startToImport(File pfArcticRootDirectory)
      throws IOException
   {
      // list the files
      // create a default schema files
      File travelportRoot = Util.getProviderDataFile(pfArcticRootDirectory, "com/travelport");
      if (!travelportRoot.exists()) {
         throw new FileNotFoundException("File " + travelportRoot.getAbsolutePath() + " not found");
      }

      XmlObject schema = new XmlObject("<xs:schema />").getFirstObject();
      schema.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
      schema.setAttribute("elementFormDefault", "qualified");
      schema.setAttribute("attributeFormDefault", "unqualified");

      // create a binding file (to identify jaxB)
      XmlObject bindings;
      bindings = new XmlObject("<jxb:bindings xmlns:jxb=\"http://java.sun.com/xml/ns/jaxb\" "
            + "xmlns=\"http://java.sun.com/xml/ns/jaxb\" "
            + "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
            + "xmlns:jaxb=\"http://java.sun.com/xml/ns/jaxb\" "
            + "xmlns:xjc=\"http://java.sun.com/xml/ns/jaxb/xjc\" "
            + "jaxb:version=\"1.0\" jaxb:extensionBindingPrefixes=\"xjc\" />").getFirstObject();
      bindings.createObject("jxb:bindings", "schemaLocation", "data.xsd", true)
            .createObject("jxb:schemaBindings")
            .createObject("jxb:package", "name", "com.travelport", true);

      // define Objects elements, which cause errors during build
      HashSet<String> hsObjectElements = new HashSet<String>();
      hsObjectElements.add("ProfileDeleteTagRsp");

      Hashtable<String, List<String>> htSpecificNamspacePackages =
         new Hashtable<String, List<String>>();
      // list subfiles
      for (File subFile : travelportRoot.listFiles()) {
         if (subFile.isDirectory()) {
            addImportXsd(schema, bindings, subFile, "", pfArcticRootDirectory.getAbsolutePath(),
                  hsObjectElements, false, htSpecificNamspacePackages);
         }
      }
      HashSet<String> hsJavaFiles = new HashSet<String>();
      for (File subFile : travelportRoot.listFiles()) {
         if (subFile.isDirectory()) {
            addImportWsdl(subFile, pfArcticRootDirectory.getAbsolutePath(), hsObjectElements,
                  htSpecificNamspacePackages, hsJavaFiles);
         }
      }

      // write the files
      Util.writeToFile(new File(travelportRoot, "data.xsd").getAbsolutePath(), schema.toString());
      Util.writeToFile(new File(travelportRoot, "bindings.xjb ").getAbsolutePath(),
            bindings.toString());
      Util.writeToFile(new File(travelportRoot, "service/Connect.java").getAbsolutePath(),
            getConnectJava(hsJavaFiles));
   }

   /**
    * returns a connect java string
    *
    * @param phsJavaFiles java files Hash Set of strings
    * @return the connect java code
    *
    * @author brod
    */
   private static String getConnectJava(HashSet<String> phsJavaFiles)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("package com.travelport.service;\n\nimport net.ifao.common.*;\n\n");
      sb.append("public class Connect {\n");
      sb.append("   ICommunication _pHttpCommunication;\n");
      sb.append("   public Connect(ICommunication pHttpCommunication){\n");
      sb.append("      _pHttpCommunication = pHttpCommunication;\n");
      sb.append("   }\n");
      for (String sJavaFile : phsJavaFiles) {
         sb.append("\n");
         sb.append("   public " + sJavaFile + " get" + sJavaFile + "() {\n");
         sb.append("      return new " + sJavaFile + "(_pHttpCommunication);\n");
         sb.append("   }\n");
      }
      sb.append("}\n");
      return sb.toString();
   }

   /**
    * adds an wsdl file
    *
    * @param pfFile file
    * @param psRoot root String
    * @param phsObjectElements Hash Set of strings
    * @param phtSpecificNamspacePackages specific namspace packages Hashtable of strings and lists of strings
    * @param phsJavaFiles java files Hash Set of strings
    * @throws IOException
    *
    * @author brod
    */
   private static void addImportWsdl(File pfFile, String psRoot, HashSet<String> phsObjectElements,
                                     Hashtable<String, List<String>> phtSpecificNamspacePackages,
                                     HashSet<String> phsJavaFiles)
                                        throws IOException
   {
      String psPackage = "com.travelport";

      if (pfFile.isDirectory()) {
         // create the java classes
         for (File file : pfFile.listFiles()) {
            String sFileName = file.getName();
            if (sFileName.endsWith(".wsdl") && !sFileName.contains("UProfile")) {
               String psUrl = "localhost";
               WsdlToJava.createJavaClasses(file,
                     Util.getProviderDataRootDirectory(psRoot, "com/travelport"), psPackage, psUrl,
                     phsObjectElements, phtSpecificNamspacePackages, phsJavaFiles);
            }
         }
      }
   }

   /**
    * adds an import xsd.
    *
    * @param pXsdSchemaObject xsd schema object object Xml Object
    * @param pXmlBindingsObject xml bindings object object Xml Object
    * @param pFile file object
    * @param psDirectory directory String
    * @param psRoot root String
    * @param phsObjectElements phs object elements Hash Set of strings
    * @param pbFixBindings true, if the ImportTravelportWsdl fix bindings boolean
    * @param phtSpecificNamspacePackages pht specific namspace packages Hashtable of strings and lists of strings
    * @throws IOException
    *
    * @author brod
    */
   private static void addImportXsd(XmlObject pXsdSchemaObject, XmlObject pXmlBindingsObject,
                                    File pFile, String psDirectory, String psRoot,
                                    HashSet<String> phsObjectElements, boolean pbFixBindings,
                                    Hashtable<String, List<String>> phtSpecificNamspacePackages)
                                       throws IOException
   {
      String psPackage = "com.travelport";

      String sSubFileName = pFile.getName();
      if (pFile.isDirectory()) {
         boolean bFixBindings = pbFixBindings;
         // get the amount of file with the same 'suffixName'
         int iCount = 0;
         String shortNameForThis = getShortName(sSubFileName);
         if (!bFixBindings && shortNameForThis.length() > 0) {
            for (File file : pFile.getParentFile().listFiles()) {
               String sName = getShortName(file.getName());
               if (!file.isDirectory() || !sName.equals(shortNameForThis)) {
                  continue;
               }
               iCount++;
            }
            // if there is only one file
            bFixBindings = iCount == 1;
         }
         // add the imports
         for (File file : pFile.listFiles()) {
            if (!file.getName().endsWith(".wsdl")) {
               addImportXsd(pXsdSchemaObject, pXmlBindingsObject, file,
                     psDirectory + sSubFileName + "/", psRoot, phsObjectElements, bFixBindings,
                     phtSpecificNamspacePackages);
            }
         }
      } else if (sSubFileName.endsWith(".xsd")) {
         // load the schema file
         XmlObject xsdFile = new XmlObject(pFile).getFirstObject();

         String targetNameSpace = xsdFile.getAttribute("targetNamespace");
         if (sSubFileName.contains("ReqRsp.") || sSubFileName.contains("GDSQueue.")
               || sSubFileName.contains("System.") || sSubFileName.contains("Terminal.")
               || sSubFileName.contains("Util.")) {
            XmlObject xsImport = pXsdSchemaObject.createObject("xs:import", "schemaLocation",
                  psDirectory + sSubFileName, true);
            if (!sSubFileName.contains("Common")) {
               // add to the top
               pXsdSchemaObject.deleteObjects(xsImport);
               pXsdSchemaObject.addObject(xsImport, false, 0);
            }
            xsImport.setAttribute("namespace", targetNameSpace);
         }

         if (pbFixBindings) {
            String sPackage = targetNameSpace.toLowerCase();
            for (int i = 0; i < 3; i++) {
               sPackage = sPackage.substring(sPackage.indexOf("/") + 1);
            }
            sPackage = psPackage + "." + sPackage.replaceAll("[\\\\/]", ".");
            if (sPackage.endsWith(".")) {
               sPackage = sPackage.substring(0, sPackage.length() - 1);
            }
            // correct the last entry
            String sLastEntry = sPackage.substring(sPackage.lastIndexOf(".") + 1);
            String shortName = getShortName(sLastEntry);
            if (shortName.length() > 0) {

               //               String sPackageNew =
               //                  sPackage.replace(sLastEntry, shortName) + "."
               //                        + sSubFileName.substring(0, sSubFileName.lastIndexOf(".")).toLowerCase();
               String sPackageNew = sPackage;
               List<String> list = phtSpecificNamspacePackages.get(sPackage);
               if (list == null) {
                  list = new ArrayList<String>();
                  phtSpecificNamspacePackages.put(sPackage, list);
               }

               if (sSubFileName.equalsIgnoreCase(shortName + ".xsd")) {
                  sPackageNew = sPackage.replace(sLastEntry, shortName.toLowerCase());
                  // create the entry
                  XmlObject schemaLocation = pXmlBindingsObject.createObject("jxb:bindings",
                        "schemaLocation", psDirectory + sSubFileName, true);
                  schemaLocation.setAttribute("namespace", targetNameSpace);
                  schemaLocation.createObject("jxb:schemaBindings").createObject("jxb:package",
                        "name", sPackageNew, true);
                  list.clear();
               }
               if (list.size() == 0) {
                  list.add(sPackageNew);
               }

            }
         }

         // correct file
         correctXsdFile(xsdFile, null);
         Util.writeToFile(pFile, xsdFile.toString().getBytes("UTF-8"));

      }

   }

   private static void correctXsdFile(XmlObject xmlObject, XmlObject parent)
      throws IOException
   {
      String type = xmlObject.getAttribute("type");
      if (xmlObject.getName().endsWith("element") && type.length() > 0
            && xmlObject.getObject("complexType") == null && !type.contains(":")) {
         if (parent != null && parent.findSubObject("complexType", "name", type) != null) {
            xmlObject.createObject("xs:complexType").createObject("xs:complexContent")
                  .createObject("xs:extension").setAttribute("base", type);
            xmlObject.setAttribute("type", null);
         }
      }

      String maxOccurs = xmlObject.getAttribute("maxOccurs");
      if (maxOccurs.matches("\\d+")) {
         if (Integer.parseInt(maxOccurs) >= 5000) {
            xmlObject.setAttribute("maxOccurs", "4999");
         }
      }
      for (XmlObject subObject : xmlObject.getObjects("")) {
         correctXsdFile(subObject, xmlObject);
      }
   }

   /**
    * returns a short name.
    *
    * @param psFileName file name String
    * @return the short name
    *
    * @author brod
    */
   private static String getShortName(String psFileName)
   {
      String[] split = psFileName.split("\\_");
      if (split.length >= 3) {
         return split[0];
      }
      return "";
   }
}
