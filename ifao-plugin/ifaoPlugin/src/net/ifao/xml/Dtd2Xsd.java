package net.ifao.xml;


import java.io.*;
import java.util.StringTokenizer;


/** 
 * The class Dtd2Xsd can be used, to convert a dtd file into a
 * xsd (schema) file. 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author brod 
 */
public class Dtd2Xsd
{

   XmlObject xsd;
   private StringBuilder _sDtd = new StringBuilder();

   /** 
    * Constructor for Dtd2Xsd  
    * 
    * @param pInputStream InputStream with the dtd content
    * 
    * @author brod 
    */
   public Dtd2Xsd(InputStream pInputStream)
   {
      xsd =
         new XmlObject(
               "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" />")
               .getFirstObject();
      // read the stream
      BufferedInputStream bufferedInputStream = new BufferedInputStream(pInputStream);
      int i;
      try {
         StringBuffer sbLine = new StringBuffer();
         boolean bTag = false;
         while ((i = bufferedInputStream.read()) > 0) {
            _sDtd.append((char) i);
            if (i <= 32) {
               i = 32;
            }

            if ((i == '<') && !bTag) {
               bTag = true;
            } else if ((i == '>') && bTag) {
               bTag = false;
               addTag(sbLine.toString());
               sbLine.setLength(0);
            } else if (bTag) {
               sbLine.append((char) i);
            }
         }
      }
      catch (IOException e) {
         // should never happen
         e.printStackTrace();
      }
   }

   /** 
    * private method addTag 
    * 
    * @param psLine line with the elements or attributes
    * 
    * @author brod 
    */
   private void addTag(String psLine)
   {
      StringTokenizer st = new StringTokenizer(psLine, " ");
      String sFirstToken = st.nextToken();
      if (sFirstToken.equals("!ELEMENT")) {
         XmlObject element = xsd.createObject("xsd:element", "name", st.nextToken(), true);
         if (psLine.contains("(") && psLine.contains(")")) {
            addElements(element, psLine.substring(psLine.indexOf("(") + 1, psLine.lastIndexOf(")"))
                  .replaceAll(" ", ""));
         }
      } else if (sFirstToken.equals("!ATTLIST")) {
         XmlObject element = xsd.createObject("xsd:element", "name", st.nextToken(), true);
         while (st.hasMoreTokens()) {
            String sName = st.nextToken();
            if (st.hasMoreTokens()) {
               String sType = st.nextToken();
               if (st.hasMoreTokens()) {
                  String sOccurs = st.nextToken();
                  addAttribute(element, sName, sType, sOccurs);
               }
            }
         }
      }
   }

   /** 
    * Private method to add an attribute 
    * 
    * @param pXmlElement XmlObject (which has to be enhanced)
    * @param psName Name of the attribute
    * @param psType Type of the attributes
    * @param psOccurs Occurs text
    * 
    * @author brod 
    */
   private void addAttribute(XmlObject pXmlElement, String psName, String psType, String psOccurs)
   {
      XmlObject attribute =
         pXmlElement.createObject("xsd:complexType").createObject("xsd:attribute", "name", psName,
               true);
      if (psOccurs.equalsIgnoreCase("#IMPLIED")) {
         attribute.setAttribute("use", "optional");
      } else if (psOccurs.equalsIgnoreCase("#REQUIRED")) {
         attribute.setAttribute("use", "required");
      }

      attribute.setAttribute("type", "xsd:string");
   }

   /** 
    * private method to add elements 
    * 
    * @param pXmlElement xml object element (which has to be enhanced)
    * @param psLine line with the elements
    * @return new created xml object
    * 
    * @author brod 
    */
   private XmlObject addElements(XmlObject pXmlElement, String psLine)
   {
      XmlObject xmlReturnObject = null;
      char[] array = (psLine + " ").toCharArray();
      XmlObject complexType =
         pXmlElement.getName().equals("element") ? pXmlElement.createObject("xsd:complexType")
               : pXmlElement;

      String sElement = "";
      int iDeep = 0;
      char cType = ',';
      // get the elements
      for (int i = 0; i < array.length; i++) {
         boolean bEnd = i + 1 == array.length;
         if (array[i] == '(') {
            iDeep++;
         }
         if (array[i] == ')') {
            iDeep--;
         }
         if (((iDeep == 0) && (array[i] == ',')) || (bEnd && (cType == ','))) {
            cType = ',';
            xmlReturnObject = complexType.createObject("xsd:sequence");
            addElement(xmlReturnObject, sElement);
            sElement = "";
         } else if (((iDeep == 0) && (array[i] == '|')) || (bEnd && (cType == '|'))) {
            cType = '|';
            xmlReturnObject = complexType.createObject("xsd:choice");
            addElement(xmlReturnObject, sElement);
            sElement = "";
         } else {
            sElement += array[i];
         }
      }
      return xmlReturnObject;
   }

   /** 
    * private method to add an element 
    * 
    * @param pXmlObject xml object (e.g. sequence)
    * @param psElement element which has to be added
    * @author brod 
    */
   private void addElement(XmlObject pXmlObject, String psElement)
   {
      if (psElement.length() == 0) {
         return;
      }
      String sName;
      String sMin = "";
      String sMax = "";
      if (psElement.endsWith("+")) {
         sName = psElement.substring(0, psElement.length() - 1);
         sMin = "1";
         sMax = "unbounded";
      } else if (psElement.endsWith("*")) {
         sName = psElement.substring(0, psElement.length() - 1);
         sMin = "0";
         sMax = "unbounded";
      } else if (psElement.endsWith("?")) {
         sName = psElement.substring(0, psElement.length() - 1);
         sMin = "0";
         sMax = "1";
      } else {
         sName = psElement;
      }

      XmlObject element;
      if (sName.contains("(")) {
         element = addElements(pXmlObject, sName);
      } else {
         element = pXmlObject.createObject("xsd:element", "ref", sName, true);
      }
      if (element != null) {
         if (sMin.length() > 0) {
            element.setAttribute("minOccurs", sMin);
         }
         if (sMax.length() > 0) {
            element.setAttribute("maxOccurs", sMax);
         }
      }
   }

   /** 
    * This method returns the related Xsd object 
    * 
    * @return replaced xsd object
    * 
    * @author brod 
    */
   public XmlObject getXsd()
   {
      return xsd;
   }

   /** 
    * @return the dtd as string
    * 
    * @author brod 
    */
   public String getDtd()
   {
      return _sDtd.toString();
   }

}
