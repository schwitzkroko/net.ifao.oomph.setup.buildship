package schemagenerator.actions.sabre;


import ifaoplugin.*;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.*;

import net.ifao.util.*;
import net.ifao.xml.*;


/**
 * This class implements a ReadSabre.
 * <p> TODO (brod) ... add detailed information for ReadSabre
 * <p>
 * Copyright &copy; 2013, i:FAO
 *
 * @author brod
 */
public class ReadSabre
{
   private Pattern[] namePattern =
      { Pattern.compile("(.*)LLS([0-9\\.]*)RQRS.xsd"),
            Pattern.compile("(.*)RQRS_v([0-9\\.]+?)[.]xsd"),
            Pattern.compile("(.*?)_([0-9_]+?)[.]xsd") };
   private PrintStream out;

   private static final String PROVIDERPACKAGEBASE_SABRE = "net.ifao.providerdata.sabre";

   /**
    * This is the constructor for the class ReadSabre, with the following parameters:
    * <p> TODO (brod) ... add detailed information for ReadSabre
    *
    *
    * @param pOut TODO (brod) out object Print Stream
    *
    * @author brod
    */
   public ReadSabre(PrintStream pOut)
   {
      out = pOut;
   }


   /**
    * updates the data xsds.
    * <p>
    * TODO (brod)  ... add detailed information for method updateDataXsds
    *
    * @param psArcticRootPath TODO (brod) arctic root path String
    * @throws IOException
    *
    * @author brod
    */
   public void updateDataXsds(String psArcticRootPath)
      throws IOException
   {
      File dirRoot = Util.getProviderDataFile(psArcticRootPath, "net/ifao/providerdata/sabre");

      Hashtable<File, String> htFiles = getFilesStoredInInfoXml(dirRoot);

      for (File file : htFiles.keySet()) {
         String sFileName = file.getName();
         for (Pattern npattern : namePattern) {
            Matcher matcher = npattern.matcher(sFileName);
            if (matcher.find()) {
               XmlObject xsd = new XmlObject(file).getFirstObject();
               if (xsd == null) {
                  continue;
               }

               String sElementName = matcher.group(1);
               String sRelDir = "wsdl/";
               String parentDirectory = file.getParentFile().getAbsolutePath().replace("\\", "/");
               int iWsdl = parentDirectory.indexOf("wsdl/");
               if (iWsdl > 0) {
                  sRelDir += parentDirectory.substring(iWsdl + 5) + "/";
                  sElementName =
                     parentDirectory.substring(iWsdl + 5).replaceAll("\\W", "") + "."
                           + sElementName;
               }

               String sClassName = sElementName.replaceAll("[_-]", "").toLowerCase();
               String sDirName = sClassName.replace(".", "/");


               String sBackDir = sRelDir;
               for (int i = 0; i < sClassName.split("\\.").length; i++) {
                  sBackDir = "../" + sBackDir;
               }


               String sTargetNamespace = "http://providerdata.ifao.net/sabre/" + sDirName;
               String attribute = xsd.getAttribute("targetNamespace");
               if (attribute.length() > 0) {
                  sTargetNamespace = attribute;
               }

               XmlObject dataXsd =
                  new XmlObject("<xs:schema elementFormDefault=\"qualified\" "
                        + "targetNamespace=\"" + sTargetNamespace + "\" "
                        + "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" " + "xmlns:tns=\""
                        + sTargetNamespace + "\" />").getFirstObject();

               XmlObject bindingsXjb =
                  new XmlObject("<jxb:bindings xmlns:jxb=\"http://java.sun.com/xml/ns/jaxb\" "
                        + "xmlns=\"http://java.sun.com/xml/ns/jaxb\" "
                        + "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
                        + "xmlns:jaxb=\"http://java.sun.com/xml/ns/jaxb\" "
                        + "xmlns:xjc=\"http://java.sun.com/xml/ns/jaxb/xjc\" jaxb:version=\"1.0\" "
                        + "jaxb:extensionBindingPrefixes=\"xjc\" />").getFirstObject();

               dataXsd.createObject("xs:include", "schemaLocation", sBackDir + sFileName, true);
               // imp.setAttribute("namespace", attribute);

               XmlObject bindingsSchemaLocation =
                  bindingsXjb.createObject("jxb:bindings", "schemaLocation", sBackDir + sFileName,
                        true);
               bindingsSchemaLocation.createObject("jxb:schemaBindings")
                     .createObject("jxb:package")
                     .setAttribute("name", "net.ifao.providerdata.sabre." + sClassName);

               XmlObject[] includes = xsd.getObjects("include");
               String sUrl = htFiles.get(file);
               if (includes.length == 0) {
                  XmlObject element = xsd.findSubObject("element", "name", sElementName + "RQ");
                  if (element != null) {
                     addElement(dirRoot, element, dataXsd, sUrl);
                  }
               } else
                  for (XmlObject include : includes) {
                     String sSubFile = include.getAttribute("schemaLocation");
                     bindingsSchemaLocation =
                        bindingsXjb.createObject("jxb:bindings", "schemaLocation", sBackDir
                              + sSubFile, true);

                     File fileXsd = new File(dirRoot, "wsdl/" + sSubFile);
                     XmlObject xml = new XmlObject(fileXsd).getFirstObject();

                     correctImports(dirRoot, sBackDir, sRelDir, xml, bindingsXjb,
                           "net.ifao.providerdata.sabre." + sClassName);

                     correctXmlFile(xml, xml, "", "", bindingsSchemaLocation);

                     XmlObject element = xml.getObject("element");
                     addElement(dirRoot, element, dataXsd, sUrl);
                  }

               Util.writeToFile(new File(dirRoot, sDirName + "/data.xsd"), dataXsd.toString()
                     .getBytes());
               Util.writeToFile(new File(dirRoot, sDirName + "/bindings.xjb"), bindingsXjb
                     .toString().getBytes());

               break;
            }
         }
      }

      correctDataBindings(dirRoot);
   }

   private void correctDataBindings(File dirRoot)
   {
      Hashtable<File, String> htPackages = new Hashtable<File, String>();

      // 1. get all data.xsd files
      getDataXsdFiles(dirRoot, htPackages);

      // 2. loop again over all bindings files
      correctImportsWithinBindingFiles(dirRoot, htPackages);
   }


   private void correctImportsWithinBindingFiles(File dirRoot, Hashtable<File, String> htPackages)
   {
      for (File subFile : dirRoot.listFiles()) {
         if (subFile.isDirectory()) {
            correctImportsWithinBindingFiles(subFile, htPackages);
         } else if (subFile.getName().equals("bindings.xjb")) {
            try {
               XmlObject bindings = new XmlObject(subFile).getFirstObject().getObject("bindings");
               if (bindings != null) {
                  // get the first bindings
                  File canonicalFile =
                     new File(subFile.getParentFile(), bindings.getAttribute("schemaLocation"))
                           .getCanonicalFile();
                  if (canonicalFile.exists()) {
                     HashSet<File> hsImportFiles = new HashSet<File>();
                     getImportFiles(canonicalFile, hsImportFiles, "import");
                     getImportFiles(canonicalFile, hsImportFiles, "include");
                     boolean bChanged = false;
                     for (File file : hsImportFiles) {
                        String sPackage = htPackages.get(file);
                        if (sPackage == null)
                           continue;
                        Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
                        Path path2 =
                           FileSystems.getDefault().getPath(
                                 subFile.getParentFile().getAbsolutePath());
                        String sRelPath = path2.relativize(path).toString().replace("\\", "/");
                        XmlObject findSubObject =
                           bindings.findSubObject("bindings", "schemaLocation", sRelPath);
                        if (findSubObject == null) {
                           findSubObject =
                              bindings.createObject("jxb:bindings", "schemaLocation", sRelPath,
                                    true);
                           bChanged = true;
                        }
                        XmlObject schemaBindings = findSubObject.getObject("schemaBindings");
                        if (schemaBindings == null) {
                           schemaBindings = findSubObject.createObject("jxb:schemaBindings");
                           bChanged = true;
                        }
                        schemaBindings.createObject("jxb:package").setAttribute("name", sPackage);

                     }
                     if (bChanged) {
                        Util.writeToFile(subFile, bindings.toString().getBytes());
                     }
                  }
               }
            }
            catch (Exception e) {
               // could not read the file
            }
         }
      }

   }

   private void getImportFiles(File canonicalFile, HashSet<File> hsImportFiles, String psType)
      throws IOException
   {
      XmlObject[] imports = new XmlObject(canonicalFile).getFirstObject().getObjects(psType);
      for (XmlObject xmlObject : imports) {
         String schemaLocation = xmlObject.getAttribute("schemaLocation");
         File file = new File(canonicalFile.getParentFile(), schemaLocation).getCanonicalFile();
         if (file.exists()) {
            if (hsImportFiles.add(file)) {
               getImportFiles(file, hsImportFiles, "import");
               getImportFiles(file, hsImportFiles, "include");
            }
         }
      }
   }


   private void getDataXsdFiles(File dirRoot, Hashtable<File, String> htPackages)
   {
      for (File subFile : dirRoot.listFiles()) {
         if (subFile.isDirectory()) {
            getDataXsdFiles(subFile, htPackages);
         } else if (subFile.getName().equals("bindings.xjb")) {
            try {
               XmlObject bindings = new XmlObject(subFile).getFirstObject().getObject("bindings");
               if (bindings != null) {
                  // get the first bindings
                  File canonicalFile =
                     new File(subFile.getParentFile(), bindings.getAttribute("schemaLocation"))
                           .getCanonicalFile();
                  if (canonicalFile.exists()) {
                     String sPackage =
                        bindings.getObject("schemaBindings").getObject("package")
                              .getAttribute("name");
                     htPackages.put(canonicalFile, sPackage);
                  }
               }
            }
            catch (Exception e) {
               // could not read the file
            }
         }
      }
   }

   private boolean addElement(File dirRoot, XmlObject element, XmlObject dataXsd, String sUrl)
      throws MalformedURLException, FileNotFoundException
   {
      if (element != null) {
         String sName = element.getAttribute("name");
         // create only for requests
         if (sName.endsWith("RQ")) {
            XmlObject xsComplexType =
               dataXsd.createObject("xs:element", "name", "SchemaInfo", true).createObject(
                     "xs:complexType");

            String sWsdlUrl = new Url(sUrl).getName();
            File wsdlFile = new File(dirRoot, "wsdl/" + sWsdlUrl);
            XmlObject xmlWsdl;
            if (wsdlFile.exists()) {
               xmlWsdl = new XmlObject(wsdlFile).getFirstObject();
            } else {
               xmlWsdl = null;
            }
            String sWsdlName = wsdlFile.getName();
            Pattern pattern = Pattern.compile("(.*)LLS([0-9\\.]*)R.*");
            Matcher m = pattern.matcher(sWsdlName);
            if (!m.find()) {
               pattern = Pattern.compile("(.*?)[RQS]*?_v?([0-9._]+)[.].*");
               m = pattern.matcher(sWsdlName);
               if (!m.find()) {
                  return true;
               }
            }
            create(xsComplexType, "wsdl", sUrl);
            String sVersion = m.group(2).replace('_', '.');
            if (sVersion.length() == 0) {
               sVersion = "1.0.1";
            }
            if (sVersion.startsWith("1"))
               sVersion = "2003A.TsabreXML" + sVersion;
            create(xsComplexType, "version", sVersion);

            String sSoapAction = m.group(1).replaceAll("\\.0-9", "") + "LLSRQ";
            if (!sWsdlName.contains("LLS") && xmlWsdl != null) {
               try {
                  XmlObject service = xmlWsdl.getObject("service");

                  XmlObject port = service.getObject("port");
                  String sBinding = port.getAttribute("binding");
                  XmlObject binding =
                     xmlWsdl.findSubObject("binding", "name",
                           sBinding.substring(sBinding.lastIndexOf(":") + 1));
                  XmlObject operation = binding.getObject("operation");
                  sSoapAction = operation.getObject("operation").getAttribute("soapAction");
                  if (sSoapAction.equalsIgnoreCase("OTA")) {
                     sSoapAction = m.group(1).replaceAll("OTA_", "");
                  }
               }
               catch (Exception ex) {
                  // avoid nullpointer etc.
               }
            }
            create(xsComplexType, "soapAction", sSoapAction);
            create(xsComplexType, "service", m.group(1).replaceAll("OTA_", ""));
         }
      }
      return false;
   }


   private void correctImports(File rootDir, String sBackDir, String psWsdlDir, XmlObject xml,
                               XmlObject bindingsXjb, String psPackageName)
      throws FileNotFoundException
   {
      for (XmlObject xmlObject : xml.getObjects("")) {
         if (xmlObject.getName().startsWith("import")) {
            String schemaLocation = xmlObject.getAttribute("schemaLocation");
            if (schemaLocation.length() > 0) {

               XmlObject findSubObject =
                  bindingsXjb.findSubObject("jxb:bindings", "schemaLocation", sBackDir
                        + schemaLocation);
               if (findSubObject == null) {
                  findSubObject =
                     bindingsXjb.createObject("jxb:bindings", "schemaLocation", sBackDir
                           + schemaLocation, true);

                  StringBuffer sMultiNameBuf = new StringBuffer();
                  String sMultiName = "";

                  /*#######################################################################################*/
                  /*# Create one package name for each XSD starting with "STL_" (in bindings.xjb) - Start #*/
                  /*#######################################################################################*/
                  String sNameSpace = xmlObject.getAttribute("namespace");
                  /*temporary: "schemaLocation.equals("STL_For_SabreProtocol_v.1.2.0.xsd") &&"*/
                  if (schemaLocation.toUpperCase().startsWith("STL_") && sNameSpace != null) {
                     if (sNameSpace.startsWith("http://")) {
                        sNameSpace = sNameSpace.substring(7);
                        String[] split = sNameSpace.split("/");

                        for (int i = 0; i < split.length; i++) {
                           String sNameSpacePart = split[i];
                           if (!sNameSpacePart.contains(".")) {
                              sMultiNameBuf.append(sNameSpacePart.toLowerCase()).append(".");
                           } else {
                              String[] split2 = sNameSpacePart.split("\\.");
                              for (int j = split2.length; j > 0; j--) {
                                 sMultiNameBuf.append(split2[j - 1].toLowerCase()).append(".");
                              }
                           }
                        }
                     }
                     sMultiName = sMultiNameBuf.toString();
                     sMultiName = sMultiName.replaceAll("_", "");
                     if (sMultiName.endsWith(".")) {
                        sMultiName = sMultiName.substring(0, sMultiName.length() - 1);
                     }
                     findSubObject.createObject("jxb:schemaBindings").createObject("jxb:package")
                           .setAttribute("name", PROVIDERPACKAGEBASE_SABRE + "." + sMultiName);
                  } else {

                     sMultiName = schemaLocation.toLowerCase();
                     if (sMultiName.contains("_v.")) {
                        sMultiName = sMultiName.substring(0, sMultiName.indexOf("_v."));
                     }
                     if (sMultiName.startsWith("STL_")) {
                        sMultiName = sMultiName.substring(4);
                     }
                     sMultiName = sMultiName.replaceAll("[\\-_0-9]", "");
                     sMultiName = sMultiName.replaceAll("for", "");

                     findSubObject.createObject("jxb:schemaBindings").createObject("jxb:package")
                           .setAttribute("name", psPackageName + "." + sMultiName);
                  }
                  /*######################################################################################*/
                  /*# Create one package name for each XSD starting with "STL_" (in bindings.xjb)- End   #*/
                  /*######################################################################################*/

                  File subFile = new File(rootDir, psWsdlDir + schemaLocation);
                  if (subFile.exists()) {
                     correctImports(rootDir, sBackDir, psWsdlDir,
                           new XmlObject(subFile).getFirstObject(), bindingsXjb, psPackageName);
                  } else {
                     System.out.println(subFile.getAbsolutePath() + " does not exist");
                  }

               }
            }
         }
      }

   }

   /**
    * creates this ReadSabre.
    * <p>
    * TODO (brod)  ... add detailed information for method create
    *
    * <p> TODO rename xsComplexType to pComplexType, string to psString, sSubFile to psSubFile
    * @param xsComplexType TODO (brod) complex type object Xml Object
    * @param string TODO (brod) string
    * @param sSubFile TODO (brod) sub file String
    *
    * @author brod
    */
   private void create(XmlObject xsComplexType, String string, String sSubFile)
   {
      XmlObject x = xsComplexType.createObject("xs:attribute", "name", string, true);
      x.setAttribute("default", sSubFile);
      x.setAttribute("type", "xs:string");

   }

   /**
    * returns a files stored in info xml.
    * <p>
    * TODO (brod)  ... add detailed information for method getFilesStoredInInfoXml
    *
    * <p> TODO rename rootDir to pDir
    * @param rootDir TODO (brod) directory object File
    * @return TODO (brod) the files stored in info xml
    * @throws IOException
    *
    * @author brod
    */
   private Hashtable<File, String> getFilesStoredInInfoXml(File rootDir)
      throws IOException
   {
      File dirWsdl = new File(rootDir, "wsdl");
      Hashtable<File, String> htFiles = new Hashtable<>();

      File fileInfoXml = new File(rootDir, "Info.xml");
      out.println("Read " + fileInfoXml.getAbsolutePath());
      XmlObject info = new XmlObject(fileInfoXml).getFirstObject();

      for (XmlObject wsdl : info.getObjects("Wsdl")) {
         XmlObject[] builds = wsdl.createObject("History").getObjects("Build");
         String sUrlWsdl = builds[builds.length - 1].getAttribute("url");
         removeOldFiles(sUrlWsdl, dirWsdl);
         List<File> lstFoundFiles = new ArrayList<>();
         loadUrl(sUrlWsdl, "", dirWsdl, lstFoundFiles);
         for (File file : lstFoundFiles) {
            htFiles.put(file, sUrlWsdl);
         }
      }

      return htFiles;
   }

   /**
    * removes the old files.
    * <p>
    * TODO (brod)  ... add detailed information for method removeOldFiles
    *
    * <p> TODO rename dirWsdl to pWsdl
    * @param psUrlWsdl TODO (brod) url wsdl String
    * @param dirWsdl TODO (brod) wsdl object File
    *
    * @author brod
    */
   private void removeOldFiles(String psUrlWsdl, File dirWsdl)
   {
      String sFileName = psUrlWsdl.substring(psUrlWsdl.lastIndexOf("/") + 1);
      if (sFileName.contains("LLS") && dirWsdl.exists()) {
         String sVersion = sFileName.substring(sFileName.indexOf("LLS"));
         sVersion = sVersion.substring(0, sVersion.indexOf("R") + 1);
         sFileName = sFileName.substring(0, sFileName.indexOf("LLS") + 3);
         if (sVersion.length() > 0) {
            for (File file : dirWsdl.listFiles()) {
               String name = file.getName();
               if (name.startsWith(sFileName)) {
                  if (!name.contains(sVersion)) {
                     out.println("Delete " + file.getAbsolutePath());
                     file.delete();
                  }
               }
            }
         }
      }
   }

   /**
    * loads an url.
    * <p>
    * TODO (brod)  ... add detailed information for method loadUrl
    *
    * <p> TODO rename dirWsdl to pWsdl
    * @param psUrl TODO (brod) url String
    * @param dirWsdl TODO (brod) wsdl object File
    * @param plstFoundFiles TODO (brod) found files List of files
    * @throws IOException
    *
    * @author brod
    */
   private void loadUrl(String psUrl, String psFileName, File dirWsdl, List<File> plstFoundFiles)
      throws IOException
   {
      psUrl += psFileName;
      Url url = new Url(psUrl);
      String sFileName;
      if (psFileName.length() > 0) {
         sFileName = psFileName;
      } else {
         sFileName = url.getName();
      }
      out.println("URL:" + psUrl);
      File file = new File(dirWsdl, sFileName);
      if (!file.exists()) {
         out.println("... load from url");
         Util.writeToFile(file, url.getBytes());
      }
      XmlObject schema = null;
      String sXmlObject = Util.loadFromFile(file);
      if (sXmlObject.length() == 0) {
         // ignore
      } else if (sFileName.endsWith(".wsdl")) {
         schema =
            new XmlObject(sXmlObject).getFirstObject().createObject("types").createObject("schema");
      } else if (sFileName.endsWith(".xsd")) {
         if (plstFoundFiles.contains(file)) {
            // file already loaded
            return;
         }
         schema = new XmlObject(sXmlObject).getFirstObject();
         plstFoundFiles.add(file);
         String sVersion = "";
         if (sFileName.endsWith("RQ.xsd")) {
            Matcher matcher = Pattern.compile("LLS([0-9.]*)R").matcher(sFileName);
            if (matcher.find()) {
               sVersion = matcher.group(1);
            }
            if (sVersion.length() == 0) {
               sVersion = "1.0.1";
            }
            if (sVersion.startsWith("1")) {
               sVersion = "2003A.TsabreXML" + sVersion;
            }
         }
         // try to fix the schema
         if (fixBugXmlObject(schema, "", sVersion)) {
            out.println("... corrected " + file.getName());
            Util.writeToFile(file, schema.toString().getBytes());
         }
      }
      String sBaseUrl = psUrl.substring(0, psUrl.lastIndexOf("/") + 1);
      loadImport(sBaseUrl, schema, file.getParentFile(), plstFoundFiles);
   }

   /**
    * loads an import.
    * <p>
    * TODO (brod)  ... add detailed information for method loadImport
    *
    * <p> TODO rename schema to pSchema, dirWsdl to pWsdl
    * @param psUrl TODO (brod) url String
    * @param schema TODO (brod) schema object Xml Object
    * @param dirWsdl TODO (brod) wsdl object File
    * @param plstFoundFiles TODO (brod) found files List of files
    * @throws IOException
    *
    * @author brod
    */
   private void loadImport(String psUrl, XmlObject schema, File dirWsdl, List<File> plstFoundFiles)
      throws IOException
   {
      if (schema == null) {
         return;
      }
      XmlObject[] imports = schema.getObjects("");
      for (XmlObject imp : imports) {
         if (imp.getName().startsWith("i")) {
            String schemaLocation = imp.getAttribute("schemaLocation");
            if (schemaLocation.length() > 0) {
               String namespace = imp.getAttribute("namespace");
               if (namespace.contains(".sabre.")) {
                  loadUrl(psUrl, schemaLocation, dirWsdl, plstFoundFiles);
               } else {
                  loadUrl(psUrl, schemaLocation, dirWsdl, plstFoundFiles);
               }
            }
         }
      }
   }

   /**
    * corrects a xml file.
    * <p>
    * TODO (brod)  ... add detailed information for method correctXmlFile
    *
    * <p> TODO rename root to pRoot, xml to pXml, bindingsSchemaLocation to pSchemaLocation
    * @param root TODO (brod) root object Xml Object
    * @param xml TODO (brod) xml object Xml Object
    * @param psName TODO (brod) name String
    * @param psParentName TODO (brod) parent name String
    * @param bindingsSchemaLocation TODO (brod) schema location object Xml Object
    *
    * @author brod
    */
   private void correctXmlFile(XmlObject root, XmlObject xml, String psName, String psParentName,
                               XmlObject bindingsSchemaLocation)
   {
      String sName = xml.getAttribute("name");
      boolean bAvail = false;
      if (sName.length() > 0) {
         bAvail = psName.contains("[@name='" + sName + "']");
         psName += "//xs:" + xml.getName() + "[@name='" + sName + "']";
      }
      if (bAvail) {
         XmlObject loc = bindingsSchemaLocation;
         if (xml.getName().equalsIgnoreCase("attribute")) {
            // loc = loc.createObject("bindings", "node", psName, true);
            String psStart = "";
            for (String s : psName.split("/")) {
               if (s.trim().length() == 0) {
                  continue;
               }
               s = psStart + "//" + s;
               psStart = ".";
               loc = loc.createObject("bindings", "node", s, true);
            }
            loc.createObject("property", "name", psParentName + sName, true);
         } else {
            if (xml.getName().equalsIgnoreCase("element")) {
               loc = loc.createObject("bindings", "node", psName + "/xs:complexType", true);
            } else {
               loc = loc.createObject("bindings", "node", psName, true);
            }

            loc.createObject("class", "name", psParentName + sName, true);
         }
      }

      if (sName.length() > 0) {
         psParentName = sName;
      }
      for (XmlObject xml2 : xml.getObjects("")) {
         correctXmlFile(root, xml2, psName, psParentName, bindingsSchemaLocation);
      }
   }

   /**
    * Method fixBugXmlObject
    *
    * During schema generation several xml tags are changed in this method.
    * The reason is normally, that Sabre does not abide by its schema, e.g. the schema
    * tells that a specific response XML object is mandatory, but Sabre does not return
    * it in the response.
    *
    * @param pXmlObject XmlObject in which the bugs are to be fixed.
    * @param psParentName The name of the parent XmlObject.
    *
    * @author ernst
    */
   private static boolean fixBugXmlObject(XmlObject pXmlObject, String psParentName,
                                          String psVersion)
   {
      boolean bReplace = false;

      if (psVersion.length() > 0) {
         for (XmlObject element : pXmlObject.getObjects("element")) {
            XmlObject complexType = element.getObject("complexType");
            if (complexType != null) {
               for (XmlObject attribute : complexType.getObjects("attribute")) {
                  if (attribute.getAttribute("name").equals("Version")) {
                     if (attribute.getAttribute("fixed").length() == 0) {
                        attribute.setAttribute("use", "optional");
                        attribute.setAttribute("default", psVersion);
                        bReplace = true;
                     }
                     break;
                  }
               }
            }
         }

      }
      String sName = pXmlObject.getAttribute("name");

      if (psParentName.equals("Commission") && sName.equals("Percentage")) {
         pXmlObject.setAttribute("name", "Percent");
         bReplace = true;
      }

      // Make AirItineraryPricingInfo optional, as Sabre does not deliver it.
      // (DT 24057)
      if (sName.equals("AirItineraryPricingInfo")) {
         if (isFoundInParentSequence(pXmlObject, "DisplayPriceQuoteRS")) {
            pXmlObject.setAttribute("minOccurs", "0");
            bReplace = true;
         }
      }

      // recurse to sub elements
      XmlObject[] subObjects = pXmlObject.getObjects("");
      for (XmlObject subObject : subObjects) {
         if (fixBugXmlObject(subObject, sName.length() > 0 ? sName : psParentName, "")) {
            bReplace = true;
         }
      }
      return bReplace;
   }

   /**
    * Method isFoundInParentSequence
    *
    * Checks, whether the given parent name can be found in the parent hierarchy
    * of the given xml object.
    *
    * @param pXmlObject The xml object in which the parent is to be searched.
    * @param psParentToFind The parent name to be searched.
    * @return true, if the ReadSabre is found in parent sequence
    *
    * @author ernst
    */
   private static boolean isFoundInParentSequence(XmlObject pXmlObject, String psParentToFind)
   {
      boolean bIsFoundInParentSequence = false;
      String name = new String();
      int count = 0;

      if (pXmlObject != null) {
         XmlObject xmlO = pXmlObject.getParent();
         while (xmlO != null) {
            name = xmlO.getAttribute("name");
            if (name != null) {
               if (name.equals(psParentToFind)) {
                  bIsFoundInParentSequence = true;
                  break;
               }
            }
            if (count++ > 100) {
               break;
            }
            xmlO = xmlO.getParent();
         }
      }

      return bIsFoundInParentSequence;
   }

}
