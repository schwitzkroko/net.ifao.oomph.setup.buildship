package schemagenerator.actions.jaxb;


import ifaoplugin.*;

import java.io.*;
import java.util.*;

import schemagenerator.actions.*;

import net.ifao.xml.XmlObject;


public class ImportWsdl
   extends ImportDirectory
{


   public ImportWsdl(PrintStream pOut)
   {
      super(pOut);
   }

   public XmlObject importWsdl(String psUrl, String psRoot, String psPackage, boolean pbClearFirst)
      throws Exception
   {
      initOutput();
      try {
         String sPackageDirectory = psPackage.replaceAll("\\.", "/");
         if (pbClearFirst) {
            clearDirectory(new File(psRoot, sPackageDirectory));
         }
         String psService = "";

         File wsdlFile = new File(psRoot, sPackageDirectory + "/data.wsdl");
         XmlObject wsdl;
         if (wsdlFile.exists()) {
            wsdl = new XmlObject(wsdlFile).getFirstObject();
         } else {
            println("read from url " + psUrl);
            UrlConnection connect = new UrlConnection(psUrl);
            wsdl = new XmlObject(connect.getContent()).getFirstObject();

            // write the schema
            XmlObject types = wsdl.getObject("types");
            XmlObject schema = null;
            if (types != null) {
               schema = types.getObject("schema");
            } else {
               String sWsdlNs = wsdl.getAttribute("targetNamespace");

               schema =
                  new XmlObject("<xs:schema targetNamespace=\"" + sWsdlNs + "\" "
                        + "xmlns=\"http://www.w3.org/2001/XMLSchema\" "
                        + "elementFormDefault=\"qualified\" "
                        + "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"> ").getFirstObject();
               XmlObject importXml = wsdl.getObject("import");
               if (importXml != null) {
                  String sUrl = importXml.getAttribute("location");
                  if (!sUrl.contains("/")) {
                     sUrl = psUrl.substring(0, psUrl.lastIndexOf("/") + 1) + sUrl;
                  }
                  UrlConnection connectSchema = new UrlConnection(sUrl);
                  println("read from url " + sUrl);

                  XmlObject schema2 = new XmlObject(connectSchema.getContent()).getFirstObject();
                  writeSchema(schema2, psRoot, psPackage);

                  String targetNamespace =
                     getPackage(psPackage, schema2.getAttribute("targetNamespace"));
                  schema.createObject("import", "namespace",
                        schema2.getAttribute("targetNamespace"), true).setAttribute(
                        "schemaLocation",
                        "../" + targetNamespace.substring(psPackage.length() + 1) + "/data.xsd");

                  createDummySchemaFromWsdl(wsdl, schema);

               }
               // psService = ".service";
            }
            if (schema != null) {
               String sNameSpace = schema.getNameSpace();
               if (sNameSpace.length() > 0) {
                  String sNs = wsdl.getAttribute("xmlns:" + sNameSpace);
                  if (sNs.length() > 0) {
                     schema.setAttribute("xmlns:" + sNameSpace, sNs);
                  }
               } else {
                  sNameSpace = "xs";
                  schema.setNameSpace(sNameSpace);
                  schema.setAttribute("xmlns:" + sNameSpace, "http://www.w3.org/2001/XMLSchema");
               }

               Util.writeToFile(wsdlFile, wsdl.toString().getBytes());

               writeSchema(schema, psRoot, psPackage);
            } else {
               println("Error: not schema found within " + psUrl);
            }

         }
         Wsdl2Java.createJavaClasses(wsdl, psRoot, psPackage, psUrl, psService);
         return wsdl;
      }
      catch (Exception ex) {
         throw ex;
      }
      finally {
         removeOutput();
      }

   }

   private void createDummySchemaFromWsdl(XmlObject wsdl, XmlObject schema)
   {
      HashSet<String> hsNS = new HashSet<String>();
      // get the messageTypes
      XmlObject[] messages = wsdl.getObjects("message");
      for (XmlObject message : messages) {
         XmlObject[] parts = message.getObjects("part");
         XmlObject element =
            schema.createObject("element", "name", message.getAttribute("name"), true);
         XmlObject sequence = element.createObject("complexType").createObject("sequence");
         if (parts.length > 0) {
            for (XmlObject part : parts) {
               XmlObject subElement =
                  sequence.createObject("element", "name", part.getAttribute("name"), true);
               String sType = part.getAttribute("type");
               if (sType.startsWith("xsd:")) {
                  sType = "xs" + sType.substring(3);
               }
               subElement.setAttribute("type", sType);
               if (sType.contains(":")) {
                  hsNS.add(sType.substring(0, sType.indexOf(":")));
               }
            }
         }
      }
      for (String sNs : hsNS) {
         String xmlns = "xmlns:" + sNs;
         String sWsdlNs = wsdl.getAttribute(xmlns);
         if (sWsdlNs.length() > 0) {
            schema.setAttribute(xmlns, sWsdlNs);
         }

      }
   }

   private void clearDirectory(File file)
   {
      if (file.exists()) {
         if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File listFile : listFiles) {
               if (listFile.getName().equalsIgnoreCase("CVS")) {
                  // ignore cvs directories
               } else {
                  clearDirectory(listFile);
               }
            }
         } else {
            file.delete();
         }
      }
   }

   private void writeSchema(XmlObject schema, String psRoot, String psPackage)
      throws IOException
   {
      String sPackage = getPackage(psPackage, schema.getAttribute("targetNamespace"));
      String sDirectory = sPackage.replaceAll("\\.", "/");

      XmlObject jxb = new XmlObject("<jxb:bindings />").getFirstObject();

      jxb.setAttribute("xmlns:jxb", "http://java.sun.com/xml/ns/jaxb");
      jxb.setAttribute("xmlns", "http://java.sun.com/xml/ns/jaxb");
      jxb.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
      jxb.setAttribute("xmlns:jaxb", "http://java.sun.com/xml/ns/jaxb");
      jxb.setAttribute("xmlns:xjc", "http://java.sun.com/xml/ns/jaxb/xjc");
      jxb.setAttribute("jaxb:version", "1.0");
      jxb.setAttribute("jaxb:extensionBindingPrefixes", "xjc");

      // get the schema
      writeSchema(schema, psRoot, psPackage, new Hashtable<String, String>(), "data.xsd", jxb);

      // correct the schemaLocations
      XmlObject[] bindings = jxb.getObjects("bindings");
      for (XmlObject bind : bindings) {
         StringTokenizer stId = new StringTokenizer(bind.getAttribute("id"), " ");
         if (stId.countTokens() >= 2) {
            String sPckg = stId.nextToken();
            String sSchemaName = stId.nextToken();
            String relDirectory = getRelDirectory(sPackage, sPckg);
            bind.setAttribute("schemaLocation", relDirectory + sSchemaName);
         }
         bind.setAttribute("id", null);
      }
      Util.writeToFile(new File(psRoot, sDirectory + "/bindings.xjb"), jxb.toString().getBytes());
   }

   private void writeSchema(XmlObject schema, String psRoot, String psPackage,
                            Hashtable<String, String> hsLocations, String psFileName, XmlObject jxb)
      throws IOException
   {
      String schemaTargetNamespace = schema.getAttribute("targetNamespace");
      String sPackage = getPackage(psPackage, schemaTargetNamespace);
      String sDirectory = sPackage.replaceAll("\\.", "/");
      if (sDirectory.length() > 0) {
         sDirectory += "/";
      }

      // correct the imports
      XmlObject[] imports = schema.getObjects("import");
      for (XmlObject xmlObjectImport : imports) {
         String sUrl = xmlObjectImport.getAttribute("schemaLocation");
         String sSchemaXsdName = hsLocations.get(sUrl);
         if (sSchemaXsdName == null) {
            if (sUrl.endsWith("data.xsd")) {
               sSchemaXsdName = "data.xsd";
            } else {
               sSchemaXsdName = "Schema.xsd";
            }
            String content;
            if (sUrl.contains(":")) {
               UrlConnection connect = new UrlConnection(sUrl);
               println("read from url " + sUrl);
               content = connect.getContent();
            } else {
               content = Utils.readFile(new File(psRoot, sDirectory + sUrl));
            }
            XmlObject schema2 = new XmlObject(content).getFirstObject();
            String sId = schema2.getAttribute("id");
            if (sId.length() > 0) {
               sSchemaXsdName = sId + ".xsd";
            }
            writeSchema(schema2, psRoot, psPackage, hsLocations, sSchemaXsdName, jxb);
         }
         // correct the schemaLocation
         String sPackage2 = getPackage(psPackage, xmlObjectImport.getAttribute("namespace"));
         xmlObjectImport.setAttribute("schemaLocation", getRelDirectory(sPackage, sPackage2)
               + sSchemaXsdName);
      }

      correctDoubleEntries(schema);

      correctSoapEntries(schema, schema);

      Util.writeToFile(new File(psRoot, sDirectory + psFileName), schema.toString().getBytes());

      XmlObject bindings =
         jxb.createObject("jxb:bindings", "id", sPackage + " " + psFileName, true);
      bindings.createObject("jxb:schemaBindings").createObject("jxb:package")
            .setAttribute("name", sPackage);
      correctBindings(bindings, schema);
   }

   private void correctDoubleEntries(XmlObject schema)
   {
      // JaxB Maps the double to a 'java-double' which may not contain null values
      // this is a problem for NewCib ... so map to decimal, which will be mapped
      // BigDecimal 
      // By the way: It is also recommended to use BigDecimal to avoid floating
      // point errors.      
      String sType = schema.getAttribute("type");
      if (sType.equals("double") || sType.endsWith(":double")) {
         if (!schema.getAttribute("name").equals("double")) {
            schema.setAttribute("type", sType.substring(0, sType.indexOf(":") + 1) + "decimal");
         }
      }
      // search also within the subObjects
      for (XmlObject subObject : schema.getObjects("")) {
         correctDoubleEntries(subObject);
      }
   }

   private void correctSoapEntries(XmlObject pXsdObject, XmlObject pSchema)
   {
      String nameSpace = pXsdObject.getNameSpace();
      if (nameSpace.length() > 0) {
         nameSpace += ":";
      }
      String sType = pXsdObject.getAttribute("type");
      if (sType.startsWith("soapenc:")) {
         if (nameSpace.length() > 0) {
            pXsdObject.setAttribute("type", nameSpace + sType.substring(sType.indexOf(":") + 1));
         } else {
            pXsdObject.setAttribute("type", sType.substring(sType.indexOf(":") + 1));
         }
      } else if (!sType.contains(":") && (sType.length() > 0)) {
         pXsdObject.setAttribute("type", nameSpace + sType);
      }
      String sBase = pXsdObject.getAttribute("base");
      if (!sBase.contains(":") && (sBase.length() > 0)) {
         pXsdObject.setAttribute("base", nameSpace + sBase);
      }
      if (pXsdObject.getName().equals("complexType")) {
         XmlObject complexContent = pXsdObject.getObject("complexContent");
         if (complexContent != null) {
            XmlObject restriction = complexContent.getObject("restriction");
            if (restriction != null) {
               if (restriction.getAttribute("base").equalsIgnoreCase("soapenc:Array")) {
                  XmlObject attribute = restriction.getObject("attribute");
                  if (attribute != null) {
                     String arrayType = attribute.getAttribute("arrayType");
                     if ((arrayType.length() > 0) && arrayType.contains("[")) {
                        XmlObject item =
                           pXsdObject.createObject(nameSpace + "sequence").createObject(
                                 nameSpace + "element", "name", "item", true);
                        item.setAttribute("minOccurs", "0");
                        item.setAttribute("maxOccurs", "unbounded");

                        String sItemType = arrayType.substring(0, arrayType.indexOf("["));
                        XmlObject subObject =
                           pSchema.findSubObject("complexType", "name",
                                 sItemType.substring(sItemType.indexOf(":") + 1));
                        if ((subObject != null)
                              && subObject.getAttribute("abstract").equalsIgnoreCase("true")) {
                           item.createObject("complexType").createObject("complexContent")
                                 .createObject("extension").setAttribute("base", sItemType);
                        } else {
                           item.setAttribute("type", sItemType);
                        }

                        pXsdObject.deleteObjects(complexContent);
                     }
                  }
               }
            }
         }
      }
      // loop through subobjects
      XmlObject[] subobjects = pXsdObject.getObjects("");
      for (XmlObject xsdObject : subobjects) {
         correctSoapEntries(xsdObject, pSchema);
      }
   }

   private String getRelDirectory(String sPackage, String sPackage2)
   {
      StringBuilder sb = new StringBuilder();
      StringTokenizer st1 = new StringTokenizer(sPackage, ".");
      StringTokenizer st2 = new StringTokenizer(sPackage2, ".");
      while (st1.hasMoreTokens() && st2.hasMoreTokens()) {
         String s1 = st1.nextToken();
         String s2 = st2.nextToken();
         if (!s1.equals(s2)) {
            sb.append("../");
            sb.append(s2);
            sb.append("/");
            break;
         }
      }
      while (st1.hasMoreTokens()) {
         st1.nextToken();
         sb.insert(0, "../");
      }
      while (st2.hasMoreTokens()) {
         sb.append(st2.nextToken());
         sb.append("/");
      }
      return sb.toString();
   }


}
