package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.regex.*;

import schemagenerator.actions.sabre.ReadSabre;
import net.ifao.util.Url;
import net.ifao.xml.XmlObject;


/**
 * TODO (brod) add comment for class ImportSabre
 *
 * <p>
 * Copyright &copy; 2007, i:FAO
 *
 * @author brod
 */
public class ImportSabre
{

   /**
    * TODO (brod) add comment for method importWsdl
    *
    * @param psArcticBase TODO (brod) add text for param psArcticBase
    * @param psWsdlUrl TODO (brod) add text for param psWsdlUrl
    * @param pbCreateSabreProviderDataJar TODO (brod) add text for param pbCreateSabreProviderDataJar
    * @param psDefaultSabreVersion TODO (ernst) add text for param psDefaultSabreVersion
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    * @param psSoapAction 
    * @throws IOException 
    */
   public static String importWsdl(String psArcticBase, String psWsdlUrl, String psSoapAction)
      throws Exception
   {
      StringBuilder sb = new StringBuilder();
      String sArcticBase = psArcticBase.replace('\\', '/');
      if (!sArcticBase.endsWith("/")) {
         sArcticBase += "/";
      }
      String sSabreBaseDir = Util.getProviderDataPath(sArcticBase, "net/ifao/providerdata/sabre");

      Pattern pattern = Pattern.compile("(.*)LLS([0-9.]*)R");

      String sSchemaLocation = psWsdlUrl.substring(psWsdlUrl.lastIndexOf("/") + 1);
      String sSabreVersion;
      String sBaseName;

      Matcher matcher = pattern.matcher(sSchemaLocation);
      if (matcher.find()) {
         sSabreVersion = matcher.group(2);
         if (sSabreVersion.length() == 0) {
            sSabreVersion = "1.0.1";
         }
         if (sSabreVersion.startsWith("1")) {
            sSabreVersion = "2003A.TsabreXML" + sSabreVersion;
         }
         sBaseName = matcher.group(1) + "LLS";
      } else {
         pattern = Pattern.compile("(.*?)_v?([0-9._]+)[.]wsdl");
         matcher = pattern.matcher(sSchemaLocation);
         if (matcher.find()) {
            sSabreVersion = matcher.group(2).replace('_', '.');
            if (sSabreVersion.length() == 0) {
               sSabreVersion = "1.0.1";
            }
            if (sSabreVersion.startsWith("1")) {
               sSabreVersion = "2003A.TsabreXML" + sSabreVersion;
            }
            sBaseName = matcher.group(1) + "LLS";
         } else {
            pattern = Pattern.compile("(.*)RQRS_v([0-9\\-]+)[.]xsd");
            matcher = pattern.matcher(sSchemaLocation);
            if (matcher.find()) {
               String sName = matcher.group(1).toLowerCase();
               importXsd(sb, psWsdlUrl, sSabreBaseDir + "/xsd/" + sName,
                     "net.ifao.providerdata.sabre.xsd." + sName, matcher.group(2), psSoapAction);
            } else {
               throw new RuntimeException("LLS[version]R not found within the pathname");
            }
            return sb.toString();
         }
      }

      // get the info file
      String sFileXml = sSabreBaseDir + "/Info.xml";
      XmlObject xmlInfo = null;
      xmlInfo = new XmlObject(new File(sFileXml)).getFirstObject();

      if (xmlInfo == null) {
         xmlInfo = new XmlObject("<Info />").getFirstObject();
      }

      XmlObject xmlInfoWsdl = null;
      for (XmlObject wsdl : xmlInfo.getObjects("Wsdl")) {
         if (wsdl.getAttribute("schemaLocation").startsWith(sBaseName)) {
            xmlInfoWsdl = wsdl;
            break;
         }
      }
      if (xmlInfoWsdl == null) {
         xmlInfoWsdl = xmlInfo.createObject("Wsdl", "schemaLocation", sSchemaLocation, true);
      } else {
         xmlInfoWsdl.setAttribute("schemaLocation", sSchemaLocation);
      }

      xmlInfoWsdl.deleteObjects("Includes");

      XmlObject xmlHistory =
         xmlInfoWsdl.createObject("History").createObject("Build", "url", psWsdlUrl, true);
      xmlHistory.setAttribute("date",
            new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
      xmlHistory.setAttribute("user", System.getenv("USERNAME").toUpperCase());


      // set main attributes into info file
      xmlInfoWsdl.setAttribute("defaultVersion", sSabreVersion);
      xmlInfoWsdl.setAttribute("docURL", null);

      xmlInfo.sortObjects();
      Util.writeToFile(sFileXml, xmlInfo.toString());
      sb.append("Modify:" + sFileXml + "\n");

      String sRet =
         "BaseDir:" + sSabreBaseDir + "\n"
               + sb.toString().replaceAll(Pattern.quote(sArcticBase), "");

      ReadSabre readSabre = new ReadSabre(System.out);
      readSabre.updateDataXsds(sArcticBase);
      return sRet;
   }

   private static void importXsd(StringBuilder pSb, String psWsdlUrl, String psDirectory,
                                 String psPackage, String psVersion, String psSoapAction)
      throws IOException
   {

      if (psSoapAction.length() == 0) {
         throw new RuntimeException("Soap action is missing for xsd schema file");
      }

      Url url = new Url(psWsdlUrl);
      String sSchema = new String(url.getBytes());
      int pos = sSchema.indexOf(">") + 1;
      sSchema =
         sSchema.substring(0, pos) + "\n<!-- OriginalFile: " + psWsdlUrl + " -->"
               + sSchema.substring(pos);

      String sVersion = psVersion.replaceAll("-", ".");

      sSchema =
         sSchema.replaceAll(
               Pattern.quote(":attribute name=\"Version\" type=\"xs:string\" use=\"required\""),
               ":attribute name=\"Version\" type=\"xs:string\" default=\"" + sVersion + "\"");
      // create the schemaInfo content
      XmlObject schema = new XmlObject(sSchema).getFirstObject();
      // get the first element
      XmlObject element = schema.getObject("element");
      if (element != null) {
         XmlObject complexType =
            schema.createObject("xs:element", "name", "SchemaInfo", true).createObject(
                  "xs:complexType");

         String sService = psSoapAction;
         if (sService.endsWith("RQ")) {
            sService = sService.substring(0, sService.length() - 2) + "Service";
         }

         complexType.createObject("xs:attribute", "name", "wsdl", true).setAttribute("default",
               psWsdlUrl);
         complexType.createObject("xs:attribute", "name", "version", true).setAttribute("default",
               sVersion);
         complexType.createObject("xs:attribute", "name", "soapAction", true).setAttribute(
               "default", psSoapAction);
         complexType.createObject("xs:attribute", "name", "service", true).setAttribute("default",
               sService);

         // set string for the attributes
         for (XmlObject attrib : complexType.getObjects("")) {
            attrib.setAttribute("type", "xs:string");
         }
         // change the schema
         sSchema = schema.toString();
      }

      Util.writeToFile(new File(psDirectory, "data.xsd"), sSchema.getBytes());

      // create bindings file
      StringBuilder sb = new StringBuilder();
      sb.append("<jxb:bindings xmlns:jxb=\"http://java.sun.com/xml/ns/jaxb\" xmlns=\"http://java.sun.com/xml/ns/jaxb\" "
            + "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:jaxb=\"http://java.sun.com/xml/ns/jaxb\" "
            + "xmlns:xjc=\"http://java.sun.com/xml/ns/jaxb/xjc\" jaxb:version=\"1.0\" "
            + "jaxb:extensionBindingPrefixes=\"xjc\">\n");
      sb.append("<jxb:bindings schemaLocation=\"data.xsd\">\n");
      sb.append("<jxb:schemaBindings>\n");
      sb.append("<jxb:package name=\"" + psPackage + "\" />\n");
      sb.append("</jxb:schemaBindings>\n");
      sb.append("</jxb:bindings>\n");
      sb.append("</jxb:bindings>\n");

      Util.writeToFile(new File(psDirectory, "bindings.xjb"), sb.toString().getBytes());

   }
}
