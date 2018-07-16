package schemagenerator.actions;


import ifaoplugin.Util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ifao.xml.XmlObject;


// It is not legal to have more than one <jaxb:schemaBindings> per namespace, so it is impossible to have two schemas in the same target namespace compiled into different Java packages
public class ImportWsdlFile
{

   private static final String[] RESERVED = { "abstract", "continue", "for", "new", "switch",
         "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private",
         "this", "break", "double", "implements", "protected", "throw", "byte", "else", "import",
         "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch",
         "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class",
         "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while" };

   File _targetDirectory;
   String _sPackage;

   private String[] _sVersionIndicators;

   public ImportWsdlFile(File targetDirectory, String psPackage, String[] psVersionIndicators)
   {
      this._targetDirectory = targetDirectory;
      this._sPackage = psPackage;
      _sVersionIndicators = psVersionIndicators;
   }

   public void importUrl(String psUrl)
      throws IOException
   {
      String sDirectory = psUrl.substring(0, psUrl.lastIndexOf("/") + 1);
      Matcher matcher = Pattern.compile("(https?://.*?/).*").matcher(psUrl);
      if (matcher.find()) {
         sDirectory = matcher.group(1);
      }
      Hashtable<File, XmlObject> hsFiles = new Hashtable<File, XmlObject>();
      importUrl(psUrl, _targetDirectory, sDirectory, hsFiles, true);

      // open data.xsd file
      File fileDataXsd = new File(_targetDirectory, "data.xsd");
      XmlObject dataXsd;
      if (fileDataXsd.exists()) {
         dataXsd = new XmlObject(fileDataXsd).getFirstObject();
      } else {
         dataXsd =
            new XmlObject(
                  "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\"/>")
                  .getFirstObject();
      }
      File fileBindings = new File(_targetDirectory, "bindings.xjb");
      XmlObject dataBindings;
      if (fileBindings.exists()) {
         dataBindings = new XmlObject(fileBindings).getFirstObject();
      } else {
         dataBindings = new XmlObject("<jxb:bindings />").getFirstObject();
         dataBindings.setAttribute("xmlns:jxb", "http://java.sun.com/xml/ns/jaxb");
         dataBindings.setAttribute("xmlns", "http://java.sun.com/xml/ns/jaxb");
         dataBindings.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
         dataBindings.setAttribute("xmlns:jaxb", "http://java.sun.com/xml/ns/jaxb");
         dataBindings.setAttribute("xmlns:xjc", "http://java.sun.com/xml/ns/jaxb/xjc");
         dataBindings.setAttribute("jaxb:version", "1.0");
         dataBindings.setAttribute("jaxb:extensionBindingPrefixes", "xjc");
      }
      createSchemaXsdFiles(hsFiles, dataXsd, dataBindings);

      for (File f : hsFiles.keySet()) {
         XmlObject firstObject = hsFiles.get(f);
         String sName = f.getName();
         if (sName.endsWith("wsdl.xsd")) {
            String targetNamespace = firstObject.getAttribute("targetNamespace");
            if (targetNamespace.length() == 0) {
               String nameSpace = firstObject.getNameSpace();
               if (nameSpace.length() > 0) {
                  targetNamespace = firstObject.getAttribute("xmlns:" + nameSpace);
               }
            }
            if (targetNamespace.length() > 0) {
               dataXsd.createObject("xs:import", "schemaLocation",
                     getRelativePath(_targetDirectory, f), true).setAttribute("namespace",
                     targetNamespace);
            }
         }
         if (sName.endsWith(".xsd")) {
            correctSchemaFile(firstObject);
         }

         Util.writeToFile(f, firstObject.toString().getBytes("UTF-8"));
      }
      Util.writeToFile(fileDataXsd, dataXsd.toString().getBytes("UTF-8"));
      Util.writeToFile(fileBindings, dataBindings.toString().getBytes("UTF-8"));

      // add url to wsdlImport
      File wsdlImport = new File(_targetDirectory, "wsdl.info.xml");
      XmlObject xmlWsdlImport;
      if (wsdlImport.exists()) {
         xmlWsdlImport = new XmlObject(wsdlImport).getFirstObject();
      } else {
         xmlWsdlImport = new XmlObject("<import />").getFirstObject();
      }
      XmlObject pattern = xmlWsdlImport.createObject("pattern");
      pattern.deleteObjects();
      String sId = psUrl.substring(psUrl.lastIndexOf("/") + 1);
      for (String sVersionPattern : _sVersionIndicators) {
         sId = sId.replaceAll(sVersionPattern, "");
         pattern.createObject("version", "value", sVersionPattern, true);
      }
      XmlObject xmlUrl = xmlWsdlImport.createObject("url", "id", sId, true);
      xmlUrl.setAttribute("name", psUrl);
      xmlUrl.setAttribute("time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
      xmlUrl.setAttribute("user", Utils.getUser());
      Util.writeToFile(wsdlImport, xmlWsdlImport.toString().getBytes("UTF-8"));
   }

   private void createSchemaXsdFiles(Hashtable<File, XmlObject> hsFiles, XmlObject dataXsd,
                                     XmlObject dataBindings)
      throws UnsupportedEncodingException, IOException
   {
      Hashtable<File, String> htNameSpaces = new Hashtable<File, String>();
      for (File file : hsFiles.keySet()) {
         File parentFile = file.getParentFile();
         String targetNamespace = htNameSpaces.get(parentFile);
         if (targetNamespace == null) {
            targetNamespace = hsFiles.get(file).getAttribute("targetNamespace");
            htNameSpaces.put(parentFile, targetNamespace);
         }
      }
      for (File folder : htNameSpaces.keySet()) {
         String targetNamespace = htNameSpaces.get(folder);

         File schemaXsd = new File(folder, "schema.xsd");
         XmlObject xmlSchemaXsd;
         if (schemaXsd.exists()) {
            xmlSchemaXsd = new XmlObject(schemaXsd).getFirstObject();
         } else {
            xmlSchemaXsd =
               new XmlObject("<xsd:schema targetNamespace=\"" + targetNamespace + "\" "
                     + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
                     // + "xmlns=\"" + targetNamespace + "\" "
                     + "elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\" />")
                     .getFirstObject();
         }
         for (File file : hsFiles.keySet()) {
            if (file.getParentFile().equals(folder)) {
               String sName = file.getName();
               if (sName.endsWith(".xsd")) {
                  xmlSchemaXsd.createObject("xsd:include", "schemaLocation", sName, true);
               }
            }
         }
         Util.writeToFile(schemaXsd, xmlSchemaXsd.toString().getBytes("UTF-8"));

         String relativePath = getRelativePath(_targetDirectory, schemaXsd);
         String sPackage = getPackage(_sPackage.replace(".", "/") + "/" + relativePath);
         dataBindings.createObject("jxb:bindings", "schemaLocation", relativePath, true)
               .createObject("jxb:schemaBindings").createObject("jxb:package")
               .setAttribute("name", sPackage);

         dataXsd.createObject("xs:import", "schemaLocation", relativePath, true).setAttribute(
               "namespace", targetNamespace);
      }
   }

   private void correctSchemaFile(XmlObject firstObject)
   {
      for (XmlObject simpleType : firstObject.getObjects("simpleType")) {
         XmlObject restriction = simpleType.getObject("restriction");
         if (restriction != null) {
            if (restriction.getAttribute("base").endsWith(":token")) {
               restriction.deleteObjects();
               restriction.setAttribute("base", restriction.getNameSpace() + ":string");
            }
         }
      }
   }

   private String getPackage(String psPath)
   {
      int lastIndexOf = psPath.lastIndexOf(".");
      if (lastIndexOf >= 0)
         psPath = psPath.substring(0, lastIndexOf);

      for (String sVersionIndicator : _sVersionIndicators) {
         psPath = psPath.toLowerCase().replaceAll(sVersionIndicator, "");
      }

      StringTokenizer st = new StringTokenizer(psPath, "/");
      StringBuilder sbPackage = new StringBuilder();
      while (st.hasMoreTokens()) {
         StringBuilder sbToken = new StringBuilder();
         for (char c : st.nextToken().toCharArray()) {
            if (String.valueOf(c).matches("\\w")) {
               sbToken.append(c);
            } else {
               sbToken.append("_");
            }
         }
         String s = sbToken.toString();
         while (s.contains("__"))
            s = s.replace("__", "_");
         while (s.startsWith("_"))
            s = s.substring(1);
         // remove versions
         while (s.endsWith("_")) {
            s = s.substring(0, s.length() - 1);
         }
         if (s.length() > 0) {
            if (s.matches("\\d.*"))
               s = "_" + s;
            for (String sReserved : RESERVED) {
               if (s.equalsIgnoreCase(sReserved)) {
                  s += "_";
                  break;
               }
            }
            if (sbPackage.length() > 0)
               sbPackage.append(".");
            sbPackage.append(s);
         }
      }
      String sPackage = sbPackage.toString();
      if (sPackage.endsWith(".schema"))
         sPackage = sPackage.substring(0, sPackage.length() - 7);
      else if (sPackage.endsWith(".wsdl"))
         sPackage = sPackage.substring(0, sPackage.length() - 5);
      return sPackage;
   }

   private File importUrl(String psUrl, File targetDirectory, String sBaseDirectory,
                          Hashtable<File, XmlObject> htFiles, boolean pbRemoveVersion)
      throws IOException
   {
      System.out.println("load url " + psUrl);
      XmlObject xmlObject = new XmlObject(Util.loadFromUrl(psUrl)).getFirstObject();

      String sFileName;
      if (psUrl.startsWith(sBaseDirectory)) {
         sFileName = psUrl.substring(sBaseDirectory.length());
      } else {
         // remove all port information
         sFileName = (sBaseDirectory + psUrl).replaceAll("[:\\?\\*]", "").replace("[\\.]", "/");
         while (sFileName.contains("//")) {
            sFileName = sFileName.replace("//", "/");
         }
      }
      // replace last number
      for (String sVersionIndicator : _sVersionIndicators) {
         sFileName = sFileName.replaceAll(sVersionIndicator, "");
      }

      File file = newFile(targetDirectory, xmlObject, sFileName);

      if (htFiles.get(file) == null) {
         htFiles.put(file, xmlObject);

         // if the file is a schema (Wsdl) file
         int iCount = 0;
         if (xmlObject.getName().equals("definitions")) {
            typesLoop: for (XmlObject types : xmlObject.getObjects("types")) {
               for (XmlObject schema : types.getObjects("schema")) {
                  iCount++;
                  String sXsdName = sFileName.substring(sFileName.lastIndexOf("/") + 1);
                  if (iCount > 1)
                     sXsdName += "#" + iCount;
                  sXsdName += ".xsd";

                  File xsdFile = new File(file.getParentFile(), sXsdName);
                  if (htFiles.get(xsdFile) == null) {
                     if (schema.getAttribute("targetNamespace").length() == 0) {
                        schema.setAttribute("targetNamespace",
                              xmlObject.getAttribute("targetNamespace"));
                     }
                     htFiles.put(xsdFile, schema);
                     Hashtable<String, String> hsSchemaNameSpaces =
                        getNameSpaces(schema, new Hashtable<String, String>());
                     addNotReferencedNameSpaced(schema, hsSchemaNameSpaces);
                     Hashtable<String, String> hsWsdlNameSpaces =
                        getNameSpaces(xmlObject, new Hashtable<String, String>());
                     // set all empty namespaces
                     for (Entry<String, String> entry : hsSchemaNameSpaces.entrySet()) {
                        if (entry.getValue().length() == 0) {
                           String sOriginal = hsWsdlNameSpaces.get(entry.getKey());
                           if (sOriginal != null && sOriginal.length() > 0) {
                              schema.setAttribute("xmlns:" + entry.getKey(), sOriginal);
                           }
                        }
                     }
                     importSchema(xsdFile, schema, psUrl.substring(0, psUrl.lastIndexOf("/")),
                           targetDirectory, sBaseDirectory, htFiles);
                  }
                  break typesLoop;
               }
            }
            // add Schema information
            addSchemaInformation(xmlObject, htFiles, psUrl);
         } else if (xmlObject.getName().equals("schema")) {
            importSchema(file, xmlObject, psUrl.substring(0, psUrl.lastIndexOf("/") + 1),
                  targetDirectory, sBaseDirectory, htFiles);
         }
      }
      return file;

   }

   private void addNotReferencedNameSpaced(XmlObject xmlObject,
                                           Hashtable<String, String> hsSchemaNameSpaces)
   {
      String nameSpace = xmlObject.getNameSpace();
      if (nameSpace.length() > 0) {
         if (hsSchemaNameSpaces.get(nameSpace) == null) {
            hsSchemaNameSpaces.put(nameSpace, "");
         }
      }
      Pattern compile = Pattern.compile("(.+?):.*");
      for (String sAttribute : new String[]{ "type" }) {
         Matcher matcher = compile.matcher(xmlObject.getAttribute(sAttribute));
         if (matcher.find()) {
            nameSpace = matcher.group(1);
            if (hsSchemaNameSpaces.get(nameSpace) == null) {
               hsSchemaNameSpaces.put(nameSpace, "");
            }
         }
      }
      for (XmlObject subObject : xmlObject.getObjects("")) {
         addNotReferencedNameSpaced(subObject, hsSchemaNameSpaces);
      }
   }

   private void addSchemaInformation(XmlObject xmlObject, Hashtable<File, XmlObject> htFiles,
                                     String psUrl)
   {
      // search all services
      for (XmlObject service : xmlObject.getObjects("service")) {
         for (XmlObject port : service.getObjects("port")) {
            //   <wsdl:service name="...Service">
            //     <wsdl:port binding="ns:...Binding" name="...Port">
            //       <soap:address location="..." />
            //     </wsdl:port>
            //   </wsdl:service>
            String sBinding = getAttribute(port.getAttribute("binding"));
            XmlObject binding = xmlObject.findSubObject("binding", "name", sBinding);
            // find the binding
            if (binding != null) {
               for (XmlObject operation : binding.getObjects("operation")) {
                  //   <wsdl:binding name="...Binding" type="ns:...PortType">
                  //     <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http" />
                  //     <wsdl:operation name="...">
                  //       <soap:operation soapAction="..." />
                  //       <wsdl:input>
                  String operationName = getAttribute(operation.getAttribute("name"));
                  String sSoapAction = "";
                  while (operation != null) {
                     sSoapAction = operation.getAttribute("soapAction");
                     if (sSoapAction.length() > 0)
                        break;
                     operation = operation.getObject("operation");
                  }
                  // operation found
                  if (sSoapAction.length() > 0 && operationName.length() > 0) {
                     XmlObject portType =
                        xmlObject.findSubObject("portType", "name",
                              getAttribute(binding.getAttribute("type")));
                     //   <wsdl:portType name="...PortType">
                     //     <wsdl:operation name="...">
                     //       <wsdl:input message="ns:...RQ" />
                     //       <wsdl:output message="ns:...RS" />
                     //       ...
                     //     </wsdl:operation>
                     //   </wsdl:portType>

                     // portType found
                     if (portType != null) {
                        XmlObject findSubObject =
                           portType.findSubObject("operation", "name", operationName);
                        XmlObject input = findSubObject.getObject("input");
                        if (input != null) {
                           String sMessage = getAttribute(input.getAttribute("message"));
                           XmlObject message =
                              xmlObject.findSubObjectAndIgnoreCase("message", "name", sMessage);
                           if (message != null) {
                              //   <wsdl:message name="...RQ">
                              //     ...
                              //     <wsdl:part element="ns:...RQ" name="body" />
                              //   </wsdl:message>
                              XmlObject body = message.findSubObject("part", "name", "body");
                              if (body != null) {
                                 String sElement = body.getAttribute("element");
                                 if (sElement.contains(":")) {
                                    String sNs = sElement.substring(0, sElement.indexOf(":"));
                                    String sMainElement =
                                       sElement.substring(sElement.indexOf(":") + 1);
                                    String sMainNs = xmlObject.getAttribute("xmlns:" + sNs);
                                    // search for a schema file
                                    for (XmlObject xsdObjects : htFiles.values()) {
                                       if (xsdObjects.getAttribute("targetNamespace").equals(
                                             sMainNs)) {
                                          // search if element exists within this file
                                          if (xsdObjects.findSubObject("element", "name",
                                                sMainElement) != null) {
                                             addSchemaInfoElement(xsdObjects, psUrl, sSoapAction,
                                                   service);
                                             System.out.println(sMainElement);
                                             break;
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void addSchemaInfoElement(XmlObject subObject, String psUrl, String sSoapAction,
                                     XmlObject service)
   {
      String sNsPre = subObject.getNameSpace();
      if (sNsPre.length() > 0)
         sNsPre += ":";
      XmlObject schemaInfo = subObject.findSubObject("element", "name", "SchemaInfo");
      if (schemaInfo == null) {
         schemaInfo = subObject.createObject(sNsPre + "element", "name", "SchemaInfo", true);
         XmlObject complexType = schemaInfo.createObject(sNsPre + "complexType");
         createAttribute(complexType, sNsPre, "wsdl", psUrl);
         createAttribute(complexType, sNsPre, "version", subObject.getAttribute("version"));
         createAttribute(complexType, sNsPre, "soapAction", sSoapAction);
         createAttribute(complexType, sNsPre, "service", service.getAttribute("name"));
      }

   }

   private void createAttribute(XmlObject complexType, String sNsPre, String psName,
                                String psDefault)
   {
      XmlObject attrWsdl = complexType.createObject(sNsPre + "attribute", "name", psName, true);
      attrWsdl.setAttribute("default", psDefault);
      attrWsdl.setAttribute("type", sNsPre + "string");

   }

   private String getAttribute(String attribute)
   {
      if (attribute.contains(":"))
         return attribute.substring(attribute.indexOf(":") + 1);
      return attribute;
   }

   private File newFile(File targetDirectory, XmlObject xmlObject, String psFileName)
   {
      String sTargetNamespace = xmlObject.getAttribute("targetNamespace");
      Matcher matcher = Pattern.compile("https?://([^/]+?)(/.*)").matcher(sTargetNamespace);
      StringBuilder sbPath = new StringBuilder();
      if (matcher.find()) {
         String sServer = matcher.group(1);
         if (sServer.contains(":")) {
            sServer = sServer.substring(0, sServer.indexOf(":"));
         }
         StringTokenizer st = new StringTokenizer(sServer, ".");
         while (st.hasMoreTokens()) {
            if (sbPath.length() > 0) {
               sbPath.insert(0, "/");
            }
            sbPath.insert(0, st.nextToken());
         }
         sTargetNamespace = matcher.group(2);
      }
      StringTokenizer st = new StringTokenizer(sTargetNamespace, "/");
      while (st.hasMoreTokens()) {
         if (sbPath.length() > 0) {
            sbPath.append("/");
         }
         sbPath.append(st.nextToken());
      }

      String sPrefix = psFileName.substring(psFileName.lastIndexOf("/") + 1);

      for (String sVersionIndicator : _sVersionIndicators) {
         sPrefix = sPrefix.replaceAll(sVersionIndicator, "");
      }

      //      if (sbPath.length() > 0) {
      //         sbPath.append("/");
      //      }
      //      sbPath.append(sPrefix);
      String sFileName = getPackage(sbPath.toString()).replace(".", "/") + "/";
      sFileName += sPrefix;
      for (String sItem : _sPackage.split("[./]")) {
         if (sFileName.startsWith(sItem + "/")) {
            sFileName = sFileName.substring(sFileName.indexOf("/") + 1);
         } else {
            break;
         }
      }
      return new File(targetDirectory, sFileName);
   }

   private Hashtable<String, String> getNameSpaces(XmlObject schema,
                                                   Hashtable<String, String> hsNameSpaces)
   {
      String nameSpace = schema.getNameSpace();
      String sRef = hsNameSpaces.get(nameSpace);
      if (sRef == null) {
         hsNameSpaces.put(nameSpace, "");
      }
      for (String sAttribute : schema.getAttributeNames(true)) {
         if (sAttribute.startsWith("xmlns:")) {
            hsNameSpaces.put(sAttribute.substring(sAttribute.indexOf(":") + 1),
                  schema.getAttribute(sAttribute));
         }
      }
      for (XmlObject subObject : schema.getObjects("")) {
         getNameSpaces(subObject, hsNameSpaces);
      }
      return hsNameSpaces;
   }

   private void importSchema(File xsdFile, XmlObject schema, String psBaseUrl,
                             File targetDirectory, String sBaseDirectory,
                             Hashtable<File, XmlObject> htFiles)
      throws IOException
   {
      for (XmlObject xmlImport : schema.getObjects("")) {
         if (!(xmlImport.getName().equals("import") || xmlImport.getName().equals("include"))) {
            continue;
         }
         String schemaLocation = xmlImport.getAttribute("schemaLocation");
         String sUrl = psBaseUrl;
         if (schemaLocation.length() == 0) {
            continue;
         } else if (schemaLocation.matches("https?://.*")) {
            sUrl = schemaLocation;
         } else if (schemaLocation.startsWith("/")) {
            Matcher matcher = Pattern.compile("(https?://.*?)/.*").matcher(psBaseUrl);
            if (matcher.find()) {
               sUrl = matcher.group(1) + schemaLocation;
            } else {
               continue;
            }
         } else {
            while (schemaLocation.startsWith("../")) {
               sUrl = sUrl.substring(0, sUrl.lastIndexOf("/"));
               schemaLocation = schemaLocation.substring(3);
            }
            if (!sUrl.endsWith("/"))
               sUrl += "/";
            sUrl += schemaLocation;
         }
         boolean bRemoveVersion = sUrl.matches(Pattern.quote(psBaseUrl) + "/[^/]+");
         File importUrl = importUrl(sUrl, targetDirectory, sBaseDirectory, htFiles, bRemoveVersion);
         String relativePath = getRelativePath(xsdFile.getParentFile(), importUrl);
         System.out.println(">>> " + xsdFile.getParentFile() + " >>> " + relativePath);
         xmlImport.setAttribute("schemaLocation", relativePath);

         //         // check for namespace
         //         String namespace = xmlImport.getAttribute("namespace");
         //         if (namespace.length() > 0) {
         //            int iCount = 0;
         //            for (String sAttribute : schema.getAttributeNames(true)) {
         //               if (sAttribute.startsWith("xmlns:")
         //                     && schema.getAttribute(sAttribute).equals(namespace)) {
         //                  iCount = 1;
         //                  break;
         //               }
         //            }
         //            if (iCount < 1) {
         //               do {
         //                  iCount++;
         //               }
         //               while (schema.getAttribute("xmlns:ns" + iCount).length() > 0);
         //               schema.setAttribute("xmlns:ns" + iCount, namespace);
         //            }
         //         }
      }
   }

   private String getRelativePath(File parentFile, File importUrl)
   {
      Path path1, path2;
      try {
         path1 = Paths.get(parentFile.getCanonicalPath());
         path2 = Paths.get(importUrl.getCanonicalPath());
      }
      catch (IOException e) {
         path1 = Paths.get(parentFile.getAbsolutePath());
         path2 = Paths.get(importUrl.getAbsolutePath());
      }
      String path = path1.relativize(path2).toString().replace("\\", "/");
      if (!path.startsWith(".") && !path.startsWith("/")) {
         path = "./" + path;
      }
      return path;
   }

}
