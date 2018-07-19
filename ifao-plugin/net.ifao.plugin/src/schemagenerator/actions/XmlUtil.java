package schemagenerator.actions;


import java.io.*;
import java.util.*;

import net.ifao.xml.XmlObject;


/** 
 * The class XmlUtil contains additional utility methods to
 * handle xml object (and schema) 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author brod 
 */
public class XmlUtil
{

   private static boolean bCreateAnnotations = false;

   /** 
    * private class Element 
    * 
    * <p> 
    * Copyright &copy; 2010, i:FAO 
    * 
    * @author brod 
    */
   private static class Element
   {
      /**
       * private Comprator to sort elements
       */
      private Comparator<Object> comp = new Comparator<Object>()
      {

         @Override
         public int compare(Object o1, Object o2)
         {
            return o1.toString().compareTo(o2.toString());
         }

      };


      String sName;
      List<String> lstAttributes = new ArrayList<String>();
      List<Element> lstElement = new ArrayList<Element>();
      private int iDeep;

      /**
       * internal constructor
       * 
       * @param pXmlSchema XmlObject with (root) schema
       * @param pXmlElement XmlObject with element
       * @param piDeep the deep level
       */
      Element(XmlObject pXmlSchema, XmlObject pXmlElement, int piDeep)
      {
         iDeep = piDeep;
         sName = pXmlElement.getAttribute("name") + pXmlElement.getAttribute("ref");
         List<XmlObject> subElement = getAttributes(pXmlSchema, pXmlElement);
         Collections.sort(subElement, comp);
         for (int i = 0; i < subElement.size(); i++) {
            lstAttributes.add(subElement.get(i).getAttribute("name"));
         }

         subElement = getElements(pXmlSchema, pXmlElement);
         Collections.sort(subElement, comp);
         for (int i = 0; i < subElement.size(); i++) {
            lstElement.add(new Element(pXmlSchema, subElement.get(i), piDeep + 1));
         }
      }

      /**
       * default constructor
       * 
       * @param pXmlSchema XmlObject with (root) schema
       * @param pXmlElement XmlObject with element
       */
      Element(XmlObject pXmlSchema, XmlObject pXmlElement)
      {
         this(pXmlSchema, pXmlElement, 0);
      }

      /**
       * This method returns the attributes of an element
       * 
       * @param pXmlSchema XmlObject with (root) schema
       * @param pXmlElement XmlObject with element
       * @return list of attributes
       */
      private List<XmlObject> getAttributes(XmlObject pXmlSchema, XmlObject pXmlElement)
      {
         List<XmlObject> xml = new ArrayList<XmlObject>();

         // add the attributes
         XmlObject complexType =
            pXmlElement.getName().equals("complexType") ? pXmlElement : pXmlElement
                  .getObject("complexType");
         if (complexType != null) {
            XmlObject[] objects = complexType.getObjects("attribute");
            for (int i = 0; i < objects.length; i++) {
               xml.add(objects[i]);
            }
         }

         String sType = getType(pXmlElement);
         if (sType.length() > 0) {
            XmlObject findSubObject = pXmlSchema.findSubObject("complexType", "name", sType);
            if (findSubObject == null)
               return xml;
            // add complex Content attributes
            try {
               XmlObject[] attributes =
                  pXmlElement.getObject("complexType").getObject("complexContent").getObject(
                        "extension").getObjects("attribute");
               for (int i = 0; i < attributes.length; i++) {
                  xml.add(attributes[i]);
               }
            }
            catch (Exception ex) {

            }
            pXmlElement = findSubObject;
         } else {
            String sRef = pXmlElement.getAttribute("ref");
            if (sRef.length() > 0) {
               XmlObject findSubObject = pXmlSchema.findSubObject("element", "name", sRef);
               if (findSubObject == null)
                  return xml;
               pXmlElement = findSubObject;
            } else {
               pXmlElement = null;
            }
         }

         if (pXmlElement != null) {
            complexType =
               pXmlElement.getName().equals("complexType") ? pXmlElement : pXmlElement
                     .getObject("complexType");
            if (complexType != null) {
               XmlObject[] objects = complexType.getObjects("attribute");
               for (int i = 0; i < objects.length; i++) {
                  xml.add(objects[i]);
               }
            }
         }
         return xml;
      }

      /**
       * @see java.lang.Object#toString()
       */
      @Override
      public String toString()
      {

         String sDeep = "";
         for (int i = 0; i < iDeep; i++) {
            sDeep += "  ";
         }
         String sRet = sDeep + sName;
         for (int i = 0; i < lstAttributes.size(); i++) {
            sRet += "\n" + sDeep + "  # " + lstAttributes.get(i);
         }
         for (int i = 0; i < lstElement.size(); i++) {
            sRet += "\n" + lstElement.get(i).toString();
         }
         return sRet;
      }

      /**
       * The methods corrects the element with another
       * element.
       * 
       * @param pOtherElement OtherElement
       */
      public void correctWith(Element pOtherElement)
      {
         // the name has to match
         if (getElementName().equals(pOtherElement.getElementName())) {
            // 1. validate if all attributes exist
            for (int i = 0; i < lstAttributes.size(); i++) {
               String sAttributeName = lstAttributes.get(i);
               if (!pOtherElement.containsAttribute(sAttributeName)) {
                  lstAttributes.set(i, "-" + sAttributeName);
               }
            }
            // 2. validate new attributes 
            for (int i = 0; i < pOtherElement.lstAttributes.size(); i++) {
               String sAttributeName = pOtherElement.lstAttributes.get(i);
               if (!containsAttribute(sAttributeName)) {
                  lstAttributes.add("+" + sAttributeName);
               }
            }

            // get the elements
            for (int i = 0; i < lstElement.size(); i++) {
               Element element = lstElement.get(i);
               if (pOtherElement.containsElement(element) == null) {
                  element.setMark("-");
               }
            }
            // 2. validate new attributes 
            for (int i = 0; i < pOtherElement.lstElement.size(); i++) {
               Element element = pOtherElement.lstElement.get(i);
               Element containsElement = containsElement(element);
               if (containsElement == null) {
                  element.setMark("+");
                  lstElement.add(element);
               } else {
                  containsElement.correctWith(element);
               }
            }
         }
      }

      /**
       * This method sets a specific marker to all attributes 
       * and elements
       * @param psMarker Marker
       */
      private void setMark(String psMarker)
      {
         sName = psMarker + getElementName();
         for (int i = 0; i < lstAttributes.size(); i++) {
            lstAttributes.set(i, psMarker + lstAttributes.get(i));
         }
         for (int i = 0; i < lstElement.size(); i++) {
            lstElement.get(i).setMark(psMarker);
         }
      }

      /**
       * @param pElementToFind ElementToFind
       * @return found element
       */
      private Element containsElement(Element pElementToFind)
      {
         for (int i = 0; i < lstElement.size(); i++) {
            if (lstElement.get(i).getElementName().equals(pElementToFind.getElementName())) {
               return lstElement.get(i);
            }
         }
         return null;
      }

      /**
       * @param psAttributeName AttributeName
       * @return true, if there is an attribute of the related name
       */
      private boolean containsAttribute(String psAttributeName)
      {
         return lstAttributes.contains(psAttributeName);
      }

      /**
       * This method corrects the schema
       * @param pXmlSchema XmlSchema
       * @param pXmlElement XmlElement
       * @param psPath Path
       * @param pLogStream LogStream
       */
      public void correctToSchema(XmlObject pXmlSchema, XmlObject pXmlElement, String psPath,
                                  PrintStream pLogStream)
      {
         // get all attributes
         List<XmlObject> attributes = getAttributes(pXmlSchema, pXmlElement);
         // correct to optional
         for (int i = 0; i < attributes.size(); i++) {
            XmlObject attribute = attributes.get(i);
            if (lstAttributes.contains("-" + attribute.getAttribute("name"))) {
               if (!attribute.getAttribute("use").equalsIgnoreCase("optional")) {
                  attribute.setAttribute("use", "optional");

                  if (bCreateAnnotations)
                     attribute.createObject("xsd:annotation", 0).createObject("xsd:documentation")
                           .setCData("set to optional because NOT available within DTD");

                  pLogStream.println("changed attribute " + psPath + "/"
                        + pXmlElement.getAttribute("name") + "/@" + attribute.getAttribute("name")
                        + ".use=\"optional\" (not found within DTD)");
               }
            }
         }
         // add new attibutes
         for (int i = 0; i < lstAttributes.size(); i++) {
            if (lstAttributes.get(i).startsWith("+")) {
               XmlObject complexType =
                  (pXmlElement.getName().equals("complexType")) ? pXmlElement : null;
               if (complexType == null) {
                  String sType = getType(pXmlElement);
                  if (sType.length() > 0) {
                     complexType = pXmlSchema.findSubObject("complexType", "name", sType);
                  }
               }
               if (complexType == null) {
                  complexType = pXmlElement.createObject("xsd:complexType");
               }
               XmlObject attribute =
                  complexType.createObject("xsd:attribute", "name", lstAttributes.get(i).substring(
                        1), true);
               if (bCreateAnnotations)
                  attribute.createObject("xsd:annotation", 0).createObject("xsd:documentation")
                        .setCData("added, because available within DTD");
               attribute.setAttribute("use", "optional");
               attribute.setAttribute("type", "xsd:string");

               pLogStream.println("added attribute " + psPath + "/"
                     + pXmlElement.getAttribute("name") + "/@" + attribute.getAttribute("name")
                     + ".type=\"xsd:string\" (from DTD)");
            }
         }
         // get the elements
         List<XmlObject> elements = getElements(pXmlSchema, pXmlElement);
         for (int i = 0; i < elements.size(); i++) {
            // find the elements
            XmlObject xmlElement = elements.get(i);
            String sElementName = xmlElement.getAttribute("name") + xmlElement.getAttribute("ref");
            for (int j = 0; j < lstElement.size(); j++) {
               if (lstElement.get(j).getElementName().equals(sElementName)) {
                  Element eFound = lstElement.get(j);
                  eFound.correctToSchema(pXmlSchema, xmlElement, psPath + "/"
                        + pXmlElement.getAttribute("name"), pLogStream);
               }
            }
         }

      }

      /**
       * @return the name of the current element (without + or -) 
       */
      private String getElementName()
      {
         if (sName.startsWith("+") || sName.startsWith("-")) {
            return sName.substring(1);
         }
         return sName;
      }
   }

   /** 
    * The method getElements returns the found (all) sub elements
    * of a specfic element 
    * 
    * @param pXmlSchema schema
    * @param pXmlElement element
    * @return list of elements
    * 
    * @author brod 
    */
   private static List<XmlObject> getElements(XmlObject pXmlSchema, XmlObject pXmlElement)
   {
      List<XmlObject> xml = new ArrayList<XmlObject>();
      String sType = getType(pXmlElement);
      if (sType.length() > 0) {
         XmlObject findSubObject = pXmlSchema.findSubObject("complexType", "name", sType);
         if (findSubObject == null)
            return xml;
         pXmlElement = findSubObject;
      } else {
         String sRef = pXmlElement.getAttribute("ref");
         if (sRef.length() > 0) {
            XmlObject findSubObject = pXmlSchema.findSubObject("element", "name", sRef);
            if (findSubObject == null)
               return xml;
            pXmlElement = findSubObject;
         }
      }
      XmlObject[] objects = pXmlElement.getObjects("");
      for (int i = 0; i < objects.length; i++) {
         if (objects[i].getName().equals("element")) {
            xml.add(objects[i]);
         } else {
            xml.addAll(getElements(pXmlSchema, objects[i]));
         }
      }
      return xml;
   }

   /** 
    * This method returns the Type of an element 
    * 
    * @param pXmlElement element
    * @return type of this element (or the value of the
    * /complexType/complexContent/extension/@base)
    * 
    * @author brod 
    */
   private static String getType(XmlObject pXmlElement)
   {
      String sType = pXmlElement.getAttribute("type");
      if (sType.length() == 0)
         try {
            XmlObject complexType = pXmlElement.getObject("complexType");
            if (complexType != null) {
               XmlObject complexContent = complexType.getObject("complexContent");
               if (complexContent != null) {
                  XmlObject extension = complexContent.getObject("extension");
                  if (extension != null)
                     return extension.getAttribute("base");
               }
            }
         }
         catch (Exception ex) {

         }
      return sType;
   }

   /** 
    * This method merges schemas, whereas the second schema is
    * merged into the first schema 
    * 
    * @param pSchema1 Schema1 (taget schema)
    * @param pSchema2 Schema2 (schema which has to be merged into the
    * first schema)
    * @param psElementName ElementName (of the root element)
    * @param psPath Path as string (root filename)
    * @param pLogStream LogStream
    * 
    * @author brod 
    */
   public static void mergeSchemas(XmlObject pSchema1, XmlObject pSchema2, String psElementName,
                                   String psPath, PrintStream pLogStream)
   {
      XmlObject element1 = findName(pSchema1, "element", psElementName);
      Element e1 = new Element(pSchema1, element1);

      XmlObject element2 = findName(pSchema2, "element", psElementName);
      Element e2 = new Element(pSchema2, element2);

      e1.correctWith(e2);

      e1.correctToSchema(pSchema1, element1, psPath, pLogStream);

      correctExtensions(pSchema1, element1);
   }

   /** 
    * private method to correct the extensions (specific attributes,
    * which are defined within 'sub' 
    * 
    * @param pSchema1 Schema1
    * @param pElement Element
    * 
    * @author brod 
    */
   private static void correctExtensions(XmlObject pSchema1, XmlObject pElement)
   {
      // remove attributes, which are available within extentions (inherrited
      // objects)
      try {
         XmlObject extension =
            pElement.getObject("complexType").getObject("complexContent").getObject("extension");
         String sType = getType(pElement);
         if (sType.length() > 0) {
            XmlObject complexType = pSchema1.findSubObject("complexType", "name", sType);
            if (complexType != null) {
               XmlObject[] attribute = extension.deleteObjects("attribute");
               for (int i = 0; i < attribute.length; i++) {
                  String sName = attribute[i].getAttribute("name");
                  if (complexType.findSubObject("attribute", "name", sName) != null) {
                     extension.deleteObjects(attribute[i]);
                  } else {
                     complexType.addObject(attribute[i]);
                  }
               }
            }
         }
      }
      catch (Exception ex) {

      }
      // validate the subObjects
      List<XmlObject> objects = getElements(pSchema1, pElement);

      for (int i = 0; i < objects.size(); i++) {
         correctExtensions(pSchema1, objects.get(i));
      }
   }

   /** 
    * private method to find a Name 
    * 
    * @param pSchema XmlObject of Schema
    * @param psType Type
    * @param psElementName ElementName
    * @return the found xml object with the requested name
    * 
    * @author brod 
    */
   private static XmlObject findName(XmlObject pSchema, String psType, String psElementName)
   {
      XmlObject findSubObject = pSchema.findSubObject(psType, "name", psElementName);
      return findSubObject;
   }

   //   private static void listStructure(XmlObject schema, XmlObject element, String psDeep)
   //   {
   //      if (element != null) {
   //         Comparator<Object> comp = new Comparator<Object>()
   //         {
   //
   //            @Override
   //            public int compare(Object o1, Object o2)
   //            {
   //               return o1.toString().compareTo(o2.toString());
   //            }
   //
   //         };
   //         System.out.println(psDeep + "- " + element.getAttribute("name")
   //               + element.getAttribute("ref"));
   //         List<XmlObject> subElement = getAttributes(schema, element);
   //         Collections.sort(subElement, comp);
   //         for (int i = 0; i < subElement.size(); i++) {
   //            System.out.println(psDeep + "  # " + subElement.get(i).getAttribute("name"));
   //         }
   //
   //         subElement = getElements(schema, element);
   //         Collections.sort(subElement, comp);
   //         for (int i = 0; i < subElement.size(); i++) {
   //            listStructure(schema, subElement.get(i), psDeep + "  ");
   //         }
   //      }
   //   }

}
