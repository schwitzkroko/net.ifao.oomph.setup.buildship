package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;

import net.ifao.xml.*;


public class ImportCib
{

   public static void start(File pfDirectory, StringBuilder psbText)
   {
      // scan for cibDataFile
      if (pfDirectory.exists() && pfDirectory.isDirectory()) {
         psbText.append("Search for cib schema file within directory\n> "
               + pfDirectory.getAbsolutePath() + "\n");
         File[] listFiles = pfDirectory.listFiles();
         for (int i = 0; i < listFiles.length; i++) {
            String sName = listFiles[i].getName();
            if (sName.endsWith(".xsd") && sName.contains("cib")) {
               // create a dataXsd
               psbText.append("read File\n> " + sName + "\n");
               File dataXsd = new File(pfDirectory, "data.xsd");
               convertFile(listFiles[i], dataXsd);
               return;
            }
         }
      } else {
         psbText.append("Invalid directory\n" + pfDirectory.getAbsolutePath() + "\n");
      }
   }

   private static void convertFile(File file, File file2)
   {
      try {
         // read the xmlObject
         XmlObject xmlObject = new XmlObject(file).getFirstObject();

         // remove the imports
         xmlObject.deleteObjects("import");

         correctXml(xmlObject, xmlObject);

         Util.writeToFile(file2.getAbsolutePath(), xmlObject.toString());


         String sBinding = WsdlObject.getDataBinding(xmlObject, null, "", true, true);

         Util.writeToFile(new File(file2.getParentFile(), "dataBinding.xml").getAbsolutePath(),
               sBinding);

      }
      catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private static void correctXml(XmlObject xmlObject, XmlObject root)
   {
      String sObjectType = xmlObject.getName();
      String sName = xmlObject.getAttribute("name");
      String sType = xmlObject.getAttribute("type");
      String sBase = xmlObject.getAttribute("base");

      // convert soapenc
      if (sType.startsWith("soapenc:")) {
         sType = sType.substring(8); // "tns:" + 
         xmlObject.setAttribute("type", sType);
      }
      // array correction
      if (sObjectType.equals("restriction") && sBase.startsWith("soapenc:")) {
         XmlObject[] attribute = xmlObject.deleteObjects("attribute");
         if (attribute.length > 0) {
            String sArrayType = attribute[0].getAttribute("arrayType");
            if (sArrayType.endsWith("[]")) {
               sArrayType = sArrayType.substring(0, sArrayType.length() - 2);
            }
            xmlObject.setAttribute("base", sArrayType);
            xmlObject.setName("extension");
         }
      }

      XmlObject[] subObjects = xmlObject.getObjects("");
      for (int i = 0; i < subObjects.length; i++) {
         correctXml(subObjects[i], root);
      }

      // post correction
      if (sObjectType.equals("complexContent")) {
         XmlObject extension = xmlObject.getObject("extension");
         if (extension != null) {
            String sResBase = extension.getAttribute("base");
            if (sResBase.equals("string")) {
               xmlObject.setName("simpleContent");
            } else if (sResBase.startsWith("tns:")) {
               XmlObject[] findSubObjects =
                  root.findSubObjects("simpleType", "name", sResBase.substring(4));
               if (findSubObjects != null && findSubObjects.length > 0) {
                  xmlObject.setName("simpleContent");
               }
            }
         }
      }
      if (sObjectType.equals("element") && sType.contains(":ArrayOf")) {
         sType = sType.replaceAll("\\:ArrayOf", ":");
         if (sType.endsWith(":String")) {
            sType = "string";
         }
         xmlObject.setAttribute("type", sType);
         xmlObject.setAttribute("maxOccurs", "unbounded");
      }
      if (sObjectType.equals("complexType") && sName.startsWith("ArrayOf")) {
         XmlObject complexContent = xmlObject.getObject("complexContent");
         if (complexContent != null) {
            XmlObject extension = complexContent.getObject("extension");
            String sExtBase = extension.getAttribute("base");
            if (sExtBase.length() > 0) {
               xmlObject.setName("element");
               xmlObject.deleteObjects(complexContent);
               xmlObject.setAttribute("type", sExtBase);
            }
         }
      }
   }

}
