package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;
import java.util.*;

import schemagenerator.correctors.*;
import net.ifao.util.ZipItem;
import net.ifao.xml.XmlObject;


/**
 * This class implements an ImportAmadeusWsdl builder
 * <p>
 * Copyright &copy; 2013, i:FAO
 *
 * @author brod
 */
public class ImportAmadeusWsdl

{
   private PrintStream _outputStream;
   private String _sBaseArctic;

   private final String AMA = "ama";

   /**
    * This is the constructor for the class ImportAmadeusWsdl, with the following parameters:
    *
    * @param psBaseArctic base arctic String
    * @param pOutputStream output stream object Print Stream
    *
    * @author brod
    */
   public ImportAmadeusWsdl(String psBaseArctic, PrintStream pOutputStream)
   {
      _outputStream = pOutputStream;
      _sBaseArctic = psBaseArctic;
   }

   private void addImports(File canonicalFile, HashSet<File> hashSet, XmlObject bindingsXjb,
                           String psPackage)
      throws IOException
   {
      if (hashSet.add(canonicalFile)) {
         XmlObject xsd = new XmlObject(canonicalFile).getFirstObject();
         for (XmlObject imports : xsd.getObjects("import")) {
            String schemaLocation = imports.getAttribute("schemaLocation");
            File canonicalFile2 =
               new File(canonicalFile.getParentFile(), schemaLocation).getCanonicalFile();
            XmlObject bindings =
               bindingsXjb.createObject("jxb:bindings", "schemaLocation", schemaLocation, true);
            String sNs = imports.getAttribute("namespace").toLowerCase();
            String psPackage2 = getPackageName(canonicalFile2, sNs);
            if (!bindingsXjb.toString().contains("jxb:package name=\"" + psPackage2 + "\"")) {
               bindings.createObject("jxb:schemaBindings").createObject("jxb:package")
                     .setAttribute("name", psPackage2);
            }
            addImports(canonicalFile2, hashSet, bindingsXjb, psPackage2);
         }
         for (XmlObject imports : xsd.getObjects("include")) {
            String schemaLocation = imports.getAttribute("schemaLocation");
            File canonicalFile2 =
               new File(canonicalFile.getParentFile(), schemaLocation).getCanonicalFile();
            addImports(canonicalFile2, hashSet, bindingsXjb, psPackage);
         }
      }

   }


   private void createDataXsdFile(File rootDir)
      throws IOException
   {
      XmlObject dataXsd =
         new XmlObject(
               "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" />")
               .getFirstObject();
      XmlObject bindingsXjb =
         new XmlObject(
               "<jxb:bindings xmlns:jxb=\"http://java.sun.com/xml/ns/jaxb\" xmlns=\"http://java.sun.com/xml/ns/jaxb\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:jaxb=\"http://java.sun.com/xml/ns/jaxb\" xmlns:xjc=\"http://java.sun.com/xml/ns/jaxb/xjc\" jaxb:version=\"1.0\" jaxb:extensionBindingPrefixes=\"xjc\"/>")
               .getFirstObject();

      createDataXsdFile(rootDir, dataXsd, bindingsXjb, "com.amadeus.xml", "./");

      write(new File(rootDir, "data.xsd"), dataXsd.toString().getBytes());
      write(new File(rootDir, "bindings.xjb"), bindingsXjb.toString().getBytes());
   }

   private void createDataXsdFile(File rootDir, XmlObject dataXsd, XmlObject bindingsXjb,
                                  String psPackage, String psDirectory)
      throws FileNotFoundException
   {
      // get the schema file
      for (File subFile : rootDir.listFiles()) {
         String sFileName = subFile.getName();
         if (subFile.isDirectory()) {
            createDataXsdFile(subFile, dataXsd, bindingsXjb,
                  psPackage + "." + sFileName.toLowerCase(), psDirectory + sFileName + "/");
         } else if (sFileName.endsWith(".xsd")) {
            String xmlns = new XmlObject(subFile).getFirstObject().getAttribute("xmlns");
            if (xmlns.length() > 0) {
               dataXsd.createObject("xs:import", "schemaLocation", psDirectory + sFileName, true)
                     .setAttribute("namespace", xmlns);
            }

            XmlObject bindings =
               bindingsXjb.createObject("jxb:bindings", "schemaLocation", psDirectory + sFileName,
                     true);
            bindings.createObject("jxb:schemaBindings").createObject("jxb:package")
                  .setAttribute("name", psPackage);
         }
      }
   }

   private void createSpecificDataXsdFile(File rootDir)
      throws IOException
   {
      XmlObject dataXsd =
         new XmlObject(
               "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" />")
               .getFirstObject();
      XmlObject bindingsXjb =
         new XmlObject(
               "<jxb:bindings xmlns:jxb=\"http://java.sun.com/xml/ns/jaxb\" xmlns=\"http://java.sun.com/xml/ns/jaxb\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:jaxb=\"http://java.sun.com/xml/ns/jaxb\" xmlns:xjc=\"http://java.sun.com/xml/ns/jaxb/xjc\" jaxb:version=\"1.0\" jaxb:extensionBindingPrefixes=\"xjc\"/>")
               .getFirstObject();

      File[] listFiles = rootDir.listFiles();
      // search files within this directory
      for (File file : listFiles) {
         String sName = file.getName();
         if (file.isDirectory()) {
            createSpecificDataXsdFile(file);
         } else if (sName.endsWith(".xsd")) {
            XmlObject xsd = new XmlObject(file).getFirstObject();
            String xmlns = xsd.getAttribute("xmlns");
            if (xmlns.length() > 0) {
               dataXsd.createObject("xs:import", "schemaLocation", sName, true).setAttribute(
                     "namespace", xmlns);
               XmlObject bindings =
                  bindingsXjb.createObject("jxb:bindings", "schemaLocation", sName, true);
               String psPackage = getPackageName(file, xmlns);
               if (!bindingsXjb.toString().contains("jxb:package name=\"" + psPackage + "\"")) {
                  bindings.createObject("jxb:schemaBindings").createObject("jxb:package")
                        .setAttribute("name", psPackage);
               }
               addImports(file.getCanonicalFile(), new HashSet<File>(), bindingsXjb, psPackage);

            }
         }
      }

      if (dataXsd.getObjects("").length > 0) {
         write(new File(rootDir, "data.xsd"), dataXsd.toString().getBytes());
         write(new File(rootDir, "bindings.xjb"), bindingsXjb.toString().getBytes());
      }
   }

   private void deleteOldSchemaFiles(File file)
   {
      if (file.exists()) {
         if (file.isDirectory()) {
            for (File f : file.listFiles()) {
               deleteOldSchemaFiles(f);
            }
         } else {
            String sName = file.getName();
            if (sName.contains(".x") || sName.endsWith(".java")) {
               file.delete();
            }
         }
      }
   }

   private void extractFilesFromZip(File rootDir, ZipItem[] parrUrls)
      throws IOException
   {
      for (ZipItem url : parrUrls) {
         File newFile = new File(rootDir, url.getName());
         if (url.isDirectory()) {
            extractFilesFromZip(newFile, url.listFiles());
         } else if (url.getName().endsWith(".xsd")) {
            _outputStream.println("Extract " + newFile.getAbsolutePath());
            write(newFile, url.getBytes());
         }
      }
   }


   private String getPackageName(File canonicalFile2, String psNs)
      throws IOException
   {
      String replaceAll = canonicalFile2.getParentFile().getCanonicalPath();
      // replaceAll = replaceAll.substring(0, replaceAll.lastIndexOf("."));
      replaceAll = replaceAll.replaceAll("[\\\\\\/]", ".").toLowerCase();
      return getPackageName(replaceAll.substring(replaceAll.indexOf("com.amadeus")));
   }

   private static String getPackageName(String sName)
   {
      String sPackageName = "";
      for (String sItem : sName.toLowerCase().replaceAll("[_]", "").split("[.]")) {
         if (sItem.length() > 0) {
            if (sItem.compareTo("0") > 0 && sItem.compareTo("99999") <= 0) {
               sItem = "p" + sItem;
            }
            if (!sPackageName.contains("." + sItem)) {
               if (sPackageName.length() > 0) {
                  sPackageName += ".";
               }
               sPackageName += sItem;
            }
         }
      }
      return sPackageName;
   }


   /**
    * @param args
    * @throws IOException
    */
   public void run()
      throws IOException
   {
      File rootDir = Util.getProviderDataFile(_sBaseArctic, "com/amadeus/xml");

      File file = new File(rootDir, "amadeusWSschemas.zip");

      deleteOldSchemaFiles(rootDir);

      // extract the schema files from the amadeus zip file
      CommonClassCorrector commonClassCorrector = new CommonClassCorrector(_outputStream);

      List<ZipItem> lstZipDirectories = new ArrayList<>();
      ZipItem amadeusWSschemasZip = new ZipItem(file);
      for (ZipItem url : amadeusWSschemasZip.listFiles()) {
         if (url.getName().matches("[^_]+_[^_]+_[0-9A-Z]+_[0-9A-Z]+_[0-9A-Z]+\\.xsd")) {
            // get the schema.xsd file
            XmlObject schemaXsd =
               new XmlObject(new ByteArrayInputStream(url.getBytes())).getFirstObject();
            // change elements to complex types
            commonClassCorrector.changeElementsToComplexTypes(schemaXsd);
            // write schema.xsd files
            writeSchemaXsdFile(rootDir, url.getName(), schemaXsd);
         } else if (url.isDirectory()) {
            lstZipDirectories.add(url);
         }
      }


      // move specific common types out the the default schema files
      commonClassCorrector.checkForCommonTypes(rootDir, "http://xml.amadeus.com/");

      // finish by creating the data.xsd file
      createDataXsdFile(rootDir);

      // don't forget the other schema files
      File amaFile = new File(rootDir, AMA);
      extractFilesFromZip(amaFile, lstZipDirectories.toArray(new ZipItem[0]));

      createSpecificDataXsdFile(amaFile);

      createSoapActionFile(rootDir, _outputStream);
   }

   private static void createSoapActionFile(File rootDir, PrintStream pStreamLog)
      throws IOException
   {

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream javaCode = new PrintStream(out);
      javaCode.println("package com.amadeus.xml.soap;\n");
      javaCode.println("import java.util.*;\n");
      javaCode.println("public class SoapActionMapping {\n");
      javaCode.println("   private static final List<SoapActionMapping> _lst;");
      javaCode.println("   static {");
      javaCode.println("      _lst = new ArrayList<SoapActionMapping>();");

      File file = new File(rootDir, "amadeusWSschemas.zip");
      Hashtable<String, String> htElements = new Hashtable<String, String>();
      Hashtable<String, XmlObject> htWsdlObject = new Hashtable<String, XmlObject>();

      ZipItem amadeusWSschemasZip = new ZipItem(file);
      // import the schemas (form the zip file) into the hashTables
      importSchemas(amadeusWSschemasZip, htElements, htWsdlObject);

      // first of all, make the imports
      for (String sFileName : htWsdlObject.keySet()) {
         pStreamLog.println(sFileName);
         XmlObject wsdl = htWsdlObject.get(sFileName);
         for (XmlObject xmlImport : wsdl.getObjects("import")) {
            // get the location
            String sLocation = xmlImport.getAttribute("location");
            // get the containing objects
            XmlObject xmlObject = htWsdlObject.get(sLocation);
            // if found
            if (xmlObject != null) {
               pStreamLog.println("Import->" + sLocation);
               for (XmlObject subObject : xmlObject.getObjects("")) {
                  if (!subObject.getName().contains("import")) {
                     wsdl.addObject(subObject);
                  }
               }
            }
         }
         // get the PortType (for the wsdl object)
         for (XmlObject service : wsdl.getObjects("service")) {
            String sService = service.getAttribute("name");
            pStreamLog.println(" Service:" + sService);
            // search the ports
            for (XmlObject port : service.getObjects("port")) {
               String sBinding = getName(port.getAttribute("binding"));
               pStreamLog.println("  Binding:" + sBinding);
               String sLocation = port.createObject("address").getAttribute("location");
               pStreamLog.println("  Location:" + sLocation);

               XmlObject binding = wsdl.findSubObject("binding", "name", sBinding);
               // find the binding
               if (binding != null) {
                  XmlObject portType =
                     wsdl.findSubObject("portType", "name", getName(binding.getAttribute("type")));
                  // ... and the related port type
                  if (portType != null) {
                     // get the operations
                     for (XmlObject operation : binding.getObjects("operation")) {
                        String sSoapAction =
                           operation.createObject("operation").getAttribute("soapAction");
                        String sOperation = operation.getAttribute("name");
                        pStreamLog.println("   operation:" + sOperation);
                        pStreamLog.println("     SoapAction:" + sSoapAction);
                        // search the portType operation
                        XmlObject ptOperation =
                           portType.findSubObject("operation", "name", sOperation);
                        if (ptOperation != null) {
                           // find the message
                           String sMessage =
                              getName(ptOperation.createObject("input").getAttribute("message"));
                           pStreamLog.println("     Message:" + sMessage);
                           XmlObject message = wsdl.findSubObject("message", "name", sMessage);
                           if (message != null) {
                              // and extact the element
                              String sElement =
                                 getName(message.createObject("part").getAttribute("element"));
                              pStreamLog.println("     Element:" + sElement);
                              String sFile = htElements.get(sElement);
                              pStreamLog.println("     File:" + sFile);
                              // print entry to java code
                              javaCode.println("      _lst.add(0, new SoapActionMapping(\""
                                    + sElement + "\", \"" + sSoapAction + "\",\n            \""
                                    + sLocation + "\", \"" + sService + "\",\n            \""
                                    + sBinding + "\", \"" + sOperation + "\", \"" + sMessage
                                    + "\"));");
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      javaCode.println("   }");
      javaCode.println("");
      javaCode
            .println("   public static SoapActionMapping getSoapActionMapping(String psElement, String psSoapContext)");
      javaCode.println("   {");
      javaCode.println("      for (SoapActionMapping sa:_lst) {");
      javaCode.println("         if (sa.element.equals(psElement)) {");
      javaCode
            .println("            if (psSoapContext.length()==0 || sa.soapAction.contains(psSoapContext)) {");
      javaCode.println("              return sa;");
      javaCode.println("            }");
      javaCode.println("         }");
      javaCode.println("      }");
      javaCode.println("      return null;");
      javaCode.println("   }");
      javaCode.println("");
      javaCode
            .println("   private String element, soapAction, location, service, binding, operation, message;");
      javaCode.println("");
      javaCode
            .println("   private SoapActionMapping(String psElement, String psSoapAction, String psLocation, "
                  + "String psService, String psBinding, String psOperation, String psMessage)");
      javaCode.println("   {");
      javaCode
            .println("      element=psElement;\n      soapAction=psSoapAction;\n      location=psLocation;\n      service=psService;\n      "
                  + "binding=psBinding;\n      operation=psOperation;\n      message=psMessage;");
      javaCode.println("   }");
      javaCode.println("");
      javaCode.println("   public String getElement()");
      javaCode.println("   {");
      javaCode.println("      return element;");
      javaCode.println("   }");
      javaCode.println("");
      javaCode.println("   public String getSoapAction()");
      javaCode.println("   {");
      javaCode.println("      return soapAction;");
      javaCode.println("   }");
      javaCode.println("");
      javaCode.println("   public String getLocation()");
      javaCode.println("   {");
      javaCode.println("      return location;");
      javaCode.println("   }");
      javaCode.println("");
      javaCode.println("   public String getService()");
      javaCode.println("   {");
      javaCode.println("      return service;");
      javaCode.println("   }");
      javaCode.println("");
      javaCode.println("   public String getBinding()");
      javaCode.println("   {");
      javaCode.println("      return binding;");
      javaCode.println("   }");
      javaCode.println("");
      javaCode.println("   public String getOperation()");
      javaCode.println("   {");
      javaCode.println("      return operation;");
      javaCode.println("   }");
      javaCode.println("");
      javaCode.println("   public String getMessage()");
      javaCode.println("   {");
      javaCode.println("      return message;");
      javaCode.println("   }");
      javaCode.println("");
      javaCode.println("   @Override");
      javaCode.println("   public String toString()");
      javaCode.println("   {");
      javaCode.println("      StringBuilder sb = new StringBuilder();");
      javaCode.println("      sb.append(\"<\").append(element);");
      javaCode
            .println("      sb.append(\" soapAction=\\\"\").append(soapAction).append(\"\\\"\");");
      javaCode.println("      sb.append(\" location=\\\"\").append(location).append(\"\\\"\");");
      javaCode.println("      sb.append(\" service=\\\"\").append(service).append(\"\\\"\");");
      javaCode.println("      sb.append(\" binding=\\\"\").append(binding).append(\"\\\"\");");
      javaCode.println("      sb.append(\" operation=\\\"\").append(operation).append(\"\\\"\");");
      javaCode.println("      sb.append(\" message=\\\"\").append(message).append(\"\\\"\");");
      javaCode.println("      sb.append(\" />\");");
      javaCode.println("      return sb.toString();");
      javaCode.println("   }");
      javaCode.println("}");
      Util.writeToFile(new File(rootDir, "soap/SoapActionMapping.java"), out.toByteArray());
   }

   private static void importSchemas(ZipItem amadeusWSschemasZip,
                                     Hashtable<String, String> htElements,
                                     Hashtable<String, XmlObject> htWsdlObject)
   {
      for (ZipItem url : amadeusWSschemasZip.listFiles()) {
         String sUrlName = url.getName();
         if (sUrlName.endsWith(".xsd")) {
            // get the schema.xsd file
            XmlObject schemaXsd =
               new XmlObject(new ByteArrayInputStream(url.getBytes())).getFirstObject();
            // add the elements
            for (XmlObject element : schemaXsd.getObjects("element")) {
               htElements.put(element.getAttribute("name"), url.getFullName());
            }

         } else if (sUrlName.endsWith(".wsdl")) {
            // get the schema.xsd file
            htWsdlObject.put(sUrlName,
                  new XmlObject(new ByteArrayInputStream(url.getBytes())).getFirstObject());
         } else if (url.isDirectory()) {
            importSchemas(url, htElements, htWsdlObject);
         }
      }

   }

   private static String getName(String psAttribute)
   {
      if (psAttribute.contains(":")) {
         return psAttribute.substring(psAttribute.indexOf(":") + 1);
      }
      return psAttribute;
   }

   public static void write(File pFile, byte[] psContent)
      throws IOException
   {
      Util.writeToFile(pFile, psContent);
   }

   private void writeSchemaXsdFile(File sInstallToDirectory, String psFileName, XmlObject schemaXsd)
      throws IOException
   {
      _outputStream.println("Import " + psFileName);
      String[] split = psFileName.toLowerCase().split("_");
      if (split.length > 1) {
         write(new File(sInstallToDirectory, split[0] + "/" + split[1] + "/schema.xsd"), schemaXsd
               .toString().getBytes());
      }
   }
}
