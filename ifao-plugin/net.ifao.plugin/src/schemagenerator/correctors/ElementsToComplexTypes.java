package schemagenerator.correctors;


import ifaoplugin.*;

import java.io.PrintStream;
import java.util.HashSet;

import net.ifao.xml.XmlObject;


public class ElementsToComplexTypes
{

   private static void changeElementsToComplexTypes(PrintStream _out, XmlObject pXmlObject,
                                                    XmlObject element,
                                                    HashSet<String> phsCorrectedTypes)
   {
      // loop recursive through elements
      for (XmlObject xmlObject : element.getObjects("")) {
         changeElementsToComplexTypes(_out, pXmlObject, xmlObject, phsCorrectedTypes);
      }

      XmlObject parent = element.getParent();
      // check if the element is on root level first element
      if (parent == null || parent.getName().equals("schema")) {
         _out.println("- " + element.getAttribute("name"));
      } else if (element.getName().equals("element")) {
         // correct maxOccurs
         String sMaxOccurs = element.getAttribute("maxOccurs");
         if (sMaxOccurs.length() > 0) {
            try {
               if (Integer.parseInt(sMaxOccurs) >= 5000) {
                  element.setAttribute("maxOccurs", "unbounded");
               }
            }
            catch (Exception ex) {
               // ignore this
            }
         }

         String sName = element.getAttribute("name");
         XmlObject complexType = element.getObject("complexType");
         if (complexType != null && sName.length() > 0) {
            String sFind = "Type";
            int iCount = 1;
            XmlObject subObject;
            while ((subObject = findComplexName("complexType", pXmlObject, sName + sFind)) != null
                  && iCount < 99) {
               if (matches(subObject, complexType)) {
                  break;
               }
               iCount++;
               sFind = "Type" + iCount;
            }
            if (iCount > 1) {
               phsCorrectedTypes.add(sName + "Type");
               phsCorrectedTypes.add(sName + sFind);
            }
            sName += sFind;
            element.setAttribute("type", sName);
            element.deleteObjects("");
            if (subObject == null) {
               XmlObject newComplexType =
                  pXmlObject.createObject(element.getNameSpace() + ":complexType", "name", sName,
                        true);
               for (XmlObject xmlObject : complexType.deleteObjects("")) {
                  changeElementsToComplexTypes(_out, pXmlObject, xmlObject, phsCorrectedTypes);
                  newComplexType.addObject(xmlObject);
               }
            }
         }
      }

   }

   private static XmlObject findComplexName(String sType, XmlObject schemaXsd, String string)
   {
      XmlObject[] objects = schemaXsd.getObjects(sType);
      for (XmlObject xmlObject : objects) {
         if (xmlObject.getAttribute("name").equalsIgnoreCase(string)) {
            return xmlObject;
         }
      }

      return null;
   }

   private static String getFirstElementNameWhichRefers(XmlObject pXml,
                                                        String sMultiCorrectedTypes,
                                                        String psParentName)
   {

      if (pXml.getAttribute("type").equals(sMultiCorrectedTypes))
         return psParentName;

      String sName = pXml.getAttribute("name");
      String sParentName = sName.length() > 0 ? sName : psParentName;

      for (XmlObject xmlObject : pXml.getObjects("")) {
         String firstElementNameWhichRefers =
            getFirstElementNameWhichRefers(xmlObject, sMultiCorrectedTypes, sParentName);
         if (firstElementNameWhichRefers.length() > 0)
            return firstElementNameWhichRefers;
      }
      return "";
   }

   private static boolean matches(XmlObject subObject, XmlObject complexType)
   {
      StringBuilder sb1 = new StringBuilder();
      StringBuilder sb2 = new StringBuilder();
      for (XmlObject sub : subObject.copy().getObjects("")) {
         sb1.append(CommonClassCorrector.withoutAnnotation(sub).toString());
      }
      for (XmlObject sub : complexType.copy().getObjects("")) {
         sb2.append(CommonClassCorrector.withoutAnnotation(sub).toString());
      }
      return sb1.toString().equals(sb2.toString());
   }

   private static void renameTypes(XmlObject schemaXsd, String sMultiCorrectedTypes, String sNewName)
   {
      if (schemaXsd.getName().endsWith("Type")) {
         if (schemaXsd.getAttribute("name").equals(sMultiCorrectedTypes)) {
            schemaXsd.setAttribute("name", sNewName);
         }
      } else if (schemaXsd.getAttribute("type").equals(sMultiCorrectedTypes)) {
         schemaXsd.setAttribute("type", sNewName);
      }
      for (XmlObject xmlObject : schemaXsd.getObjects("")) {
         renameTypes(xmlObject, sMultiCorrectedTypes, sNewName);
      }
   }

   public static void start(PrintStream _out, XmlObject SchemaXsd)
   {
      HashSet<String> hsCorrectedTypes = new HashSet<String>();

      changeElementsToComplexTypes(_out, SchemaXsd, SchemaXsd, hsCorrectedTypes);

      for (String sMultiCorrectedTypes : hsCorrectedTypes.toArray(new String[0])) {
         String sElementName = getFirstElementNameWhichRefers(SchemaXsd, sMultiCorrectedTypes, "");
         if (sElementName.length() > 0) {
            String sNewName =
               Util.camelCase(sMultiCorrectedTypes.replaceAll("Type[0-9]*", ""))
                     + Util.camelCase(sElementName);
            if (hsCorrectedTypes.add(sNewName)) {
               _out.println("- Rename " + sMultiCorrectedTypes + "->" + sNewName);
               renameTypes(SchemaXsd, sMultiCorrectedTypes, sNewName);
            }
         }
      }
   }

}
