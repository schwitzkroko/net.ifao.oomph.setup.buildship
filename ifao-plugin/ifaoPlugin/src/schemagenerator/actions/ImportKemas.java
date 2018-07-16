package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;

import net.ifao.xml.XmlObject;


public class ImportKemas
{

   public static String copyWsdl2Xsd(File baseDirectory)
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(out);
      try {
         for (File file : baseDirectory.listFiles()) {
            if (file.getName().endsWith(".wsdl")) {
               printStream.println("read Wsdl File " + file.getName());
               XmlObject wsdl = new XmlObject(file).getFirstObject();

               printStream.println("extract Schema");
               XmlObject schema = wsdl.getObject("types").getObject("schema");

               printStream.println("correct Schema");
               String sNs = correctSchemaNameSpace(schema);
               correctSchema(schema, sNs);
               // write schema to file
               printStream.println("write data.xsd");
               Util.writeToFile(new File(file.getParent(), "data.xsd"), schema.toString()
                     .getBytes());
               printStream.println("write bindings.xjb");
               Util.writeToFile(new File(file.getParent(), "bindings.xjb"), getBindingFile()
                     .getBytes());
            }
         }
      }
      catch (IOException e) {
         e.printStackTrace(printStream);
      }
      return out.toString();
   }

   private static String getBindingFile()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("<jxb:bindings xmlns:jxb=\"http://java.sun.com/xml/ns/jaxb\"\n");
      sb.append(" xmlns=\"http://java.sun.com/xml/ns/jaxb\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n");
      sb.append(" xmlns:jaxb=\"http://java.sun.com/xml/ns/jaxb\" xmlns:xjc=\"http://java.sun.com/xml/ns/jaxb/xjc\"\n");
      sb.append(" jaxb:version=\"1.0\" jaxb:extensionBindingPrefixes=\"xjc\">\n");
      sb.append(" <jxb:bindings schemaLocation=\"data.xsd\">\n");
      sb.append("    <jxb:schemaBindings>\n");
      sb.append("       <jxb:package name=\"de.kemas.carpoolnet\" />\n");
      sb.append("    </jxb:schemaBindings>\n");
      sb.append(" </jxb:bindings>\n");
      sb.append(" <jaxb:globalBindings>\n");
      sb.append("    <xjc:javaType adapter=\"net.ifao.util.serialize.JaxBDateAdapter\" name=\"java.util.Date\" xmlType=\"xs:date\" />\n");
      sb.append(" </jaxb:globalBindings>\n");
      sb.append("</jxb:bindings>\n");
      return sb.toString();
   }

   private static String correctSchemaNameSpace(XmlObject schema)
   {
      String targetNamespace = schema.getAttribute("targetNamespace");

      for (String sName : schema.getAttributeNames(true)) {
         if (sName.startsWith("xmlns:") && schema.getAttribute(sName).equals(targetNamespace)) {
            return sName.substring(sName.indexOf(":") + 1);
         }
      }
      // not found ... so add a new one
      schema.setAttribute("xmlns:ns1", targetNamespace);
      return "ns1";
   }

   private static void correctSchema(XmlObject schema, String sNs)
   {
      String sNameSpace = schema.getAttribute("type");
      if (sNameSpace.equals("date") || sNameSpace.equals("string") || sNameSpace.equals("float")) {
         schema.setAttribute("type", schema.getNameSpace() + ":" + sNameSpace);
      }

      if (schema.getAttribute("name").equals(schema.getAttribute("type"))) {
         String name = schema.getAttribute("name");
         if (name.length() > 0) {
            schema.setAttribute("name", null);
            schema.setAttribute("type", null);
            schema.setAttribute("ref", sNs + ":" + name);
         }
      }

      // loop recursively
      XmlObject[] subObjects = schema.getObjects("");
      for (XmlObject xmlObject : subObjects) {
         correctSchema(xmlObject, sNs);
      }

   }

}
