package net.ifao.xml;


import java.io.*;
import java.util.*;


/**
 * The Class XmlObject is a base Class for Xml-Formatting
 * 
 * <pre>
 *  _     _              .      ___    _                                .
 *  `.   /   , _ , _     |    .'   `.  \ ___       .     ___     ___   _/_
 *    \,'    |' `|' `.   |    |     |  |/   \      \   .'   `  .'   `   |
 *   ,'\     |   |   |   |    |     |  |    `      |   |----'  |        |
 *  /   \    /   '   /  /\__   `.__.'  `___,'  /`  |   `.___,   `._.'   \__/
 *                                             \___/`
 * </pre>
 * 
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
public class XmlObject
   implements Comparable<XmlObject>
{

   // private static members
   static String FONT1 = "<font color='#983000'>";
   static String FONT2 = "<font color='blue'>";
   static String FONT3 = "<font color='black'>";

   static String FONT_END = "</font>";
   static String BLANK = " "; // "&nbsp;";
   static String RETURN = "\r\n"; // "<br>\r\n";

   // ------------------------------------------------------------------
   // ____ ___ ____ ___ _ ____    _  _ ____ ___ _  _ ____ ___  ____
   // [__   |  |__|  |  | |       |\/| |___  |  |__| |  | |  \ [__
   // ___]  |  |  |  |  | |___    |  | |___  |  |  | |__| |__/ ___]
   //
   // ------------------------------------------------------------------
   private static boolean bFormatXmlOutput = false;

   /**
    * @param pbFormatXmlOutput the bFormatXmlOutput to set
    * 
    */
   public static void setBFormatXmlOutput(boolean pbFormatXmlOutput)
   {
      bFormatXmlOutput = pbFormatXmlOutput;
   }

   /**
    * The method getBFormatXmlOutput return the status of the
    * bFormatXmlOutput-member
    * 
    * @return bFormatXmlOutput
    * 
    * @author brod
    */
   public static boolean getBFormatXmlOutput()
   {
      return bFormatXmlOutput;
   }

   // private members
   private StringStream _inStream;
   private boolean _bOpen = true;
   private XmlName _sName;
   private String _sLine = "";
   private String _sCData = "";

   private Vector<XmlObject> _lstXmlObjects = new Vector<XmlObject>();
   private Vector<XmlAttribute> _lstAttributes = new Vector<XmlAttribute>();
   private int _piDeep = 0;
   private XmlObject _xmlParent = null;

   // -----------------------------------------------------------------------------------
   // ____ ___ ____ ____ ___    ____ ____ _  _ ____ ___ ____ _  _ ____ ___ ____ ____ ____
   // [__   |  |__| |__/  |     |    |  | |\ | [__   |  |__/ |  | |     |  |  | |__/ [__
   // ___]  |  |  | |  \  |     |___ |__| | \| ___]  |  |  \ |__| |___  |  |__| |  \ ___]
   //
   // -----------------------------------------------------------------------------------

   /**
    * Constructor XmlObject
    * 
    * @param pFile
    * @throws FileNotFoundException
    * 
    */
   public XmlObject(File pFile)
      throws FileNotFoundException
   {
      this(new StringStream(pFile));
   }

   /**
    * Constructor XmlObject
    * 
    * @param pInputStream
    * 
    * @author brod
    */
   public XmlObject(InputStream pInputStream)
   {
      this(new StringStream(pInputStream));
   }

   /**
    * Constructor XmlObject
    * 
    * @param psXmlObject
    * 
    */
   public XmlObject(String psXmlObject)
   {
      this(new StringStream(psXmlObject));
   }

   /**
    * Constructor XmlObject
    * 
    * @param pInputStream
    * 
    */
   private XmlObject(StringStream pInputStream)
   {
      this(null, pInputStream, "root", 0);
   }

   /**
    * This is the main Constructor for XmlObject, to which all other
    * constructors refer !
    * 
    * @param pParent
    * @param pStream
    * @param psLine
    * @param piDeep
    * 
    */
   private XmlObject(XmlObject pParent, StringStream pStream, String psLine, int piDeep)
   {
      _xmlParent = pParent;
      _inStream = pStream;
      _piDeep = piDeep;

      _sLine = psLine;

      StringRead stringRead = new StringRead(psLine);

      String[] comment = { "!--" };
      // 1. Suche den Namen
      _sName = new XmlName(stringRead.readUntil(" \t\n\r\f\b/+", comment));

      if (_sName.getFullName().startsWith("![CDATA")) {
         _sCData = _sName.getFullName().substring(8) + stringRead.readUntil2("]]");
         _sName = new XmlName("![CDATA");
      } else {

         String[] noSpecialSearch = new String[0];
         String aName = stringRead.readUntil("/=", noSpecialSearch);
         char aChar = stringRead.find("\"'");
         String aVal = stringRead.readUntil("" + aChar, noSpecialSearch);

         while (aName.length() > 0) {
            _lstAttributes.add(new XmlAttribute(aName, aVal));

            aName = stringRead.readUntil("/=", noSpecialSearch);
            aChar = stringRead.find("\"'");
            aVal = stringRead.readUntil("" + aChar, noSpecialSearch);
         }

         if (psLine.endsWith("/") || psLine.startsWith("/")) {
            // ignore this
         } else if (psLine.startsWith("!")) {
            // ignore this
         } else if (psLine.startsWith("?")) {
            // ignore this
         } else {
            String s = " ";

            while ((s.length() > 0) && !s.startsWith("/")) {
               if (s.trim().length() > 0) {
                  XmlObject xmlObject = new XmlObject(this, _inStream, s, _piDeep + 1);

                  addObject(xmlObject);

                  _bOpen = xmlObject._bOpen;

               }

               _sCData += readUntilStartTag(" \u00A0\t\n\r\f\b");

               s = readNextTag();
            }
         }
      }

   }

   // ---------------------------------------------------------------------------
   // ____ _  _ ___     ____ ____ _  _ ____ ___ ____ _  _ ____ ___ ____ ____ ____
   // |___ |\ | |  \    |    |  | |\ | [__   |  |__/ |  | |     |  |  | |__/ [__
   // |___ | \| |__/    |___ |__| | \| ___]  |  |  \ |__| |___  |  |__| |  \ ___]
   //
   // ---------------------------------------------------------------------------

   /**
    * Method addElementObject
    * 
    * @param pXmlObject
    * 
    * @author $author$
    */
   public void addElementObject(XmlObject pXmlObject)
   {
      addElementObject(pXmlObject, -1);
   }

   /**
    * Method addElementObject
    * 
    * @param xmlObject
    * @param piPos
    * 
    * @author Andreas Brod
    */
   public void addElementObject(XmlObject pXmlObject, int piPos)
   {
      XmlObject xmlObject;
      if (pXmlObject.isRoot()) {
         if (pXmlObject.getFirstObject() == null) {
            if ((pXmlObject._lstXmlObjects.size() == 1)
                  && pXmlObject._lstXmlObjects.get(0).getName().equals("!--")) {
               xmlObject = pXmlObject._lstXmlObjects.get(0);
            } else {
               return;
            }
         } else {
            xmlObject = pXmlObject.getFirstObject();
         }
      } else {
         xmlObject = pXmlObject;
      }

      if ((piPos >= 0) && (piPos <= _lstXmlObjects.size())) {
         _lstXmlObjects.add(piPos, xmlObject);

         return;
      }

      String sName = xmlObject.getName();
      int iLast = -1;

      for (int i = _lstXmlObjects.size() - 2; (iLast < 0) && (i >= 0); i--) {
         if (_lstXmlObjects.get(i).getName().equals(sName)) {
            iLast = i;
         }
      }

      if (iLast >= 0) {
         _lstXmlObjects.add(iLast + 1, xmlObject);
      } else {
         _lstXmlObjects.add(xmlObject);
      }

      xmlObject._piDeep = _piDeep + 1;
   }

   /**
    * Method addObject
    * 
    * @param pXmlObject
    * 
    * @author Andreas Brod
    */
   public void addObject(XmlObject pXmlObject)
   {
      addObject(pXmlObject, false);
   }

   /**
    * Method addObject
    * 
    * @param pXmlObject
    * @param pbGroup
    * 
    * @author Andreas Brod
    */
   public void addObject(XmlObject pXmlObject, boolean pbGroup)
   {
      addObject(pXmlObject, pbGroup, -1);
   }

   /**
    * method to add an Object
    * 
    * @param xmlObject XmlObject
    * @param pbGroup Group flag
    * @param piIndex the index (at which position the xml object
    * has to be added)
    * 
    * @author brod
    */
   public void addObject(XmlObject pXmlObject, boolean pbGroup, int piIndex)
   {
      if (pXmlObject == null) {
         return;
      }
      XmlObject xmlObject;
      if (pXmlObject.isRoot()) {
         xmlObject = pXmlObject.getFirstObject();
      } else {
         xmlObject = pXmlObject;
      }

      if (xmlObject != null) {
         xmlObject._xmlParent = this;
         xmlObject._piDeep = _piDeep + 1;

         if (xmlObject.getName().startsWith("![CDATA")) {
            _sCData = xmlObject._sCData;
         } else {

            int iLast = piIndex;

            if (pbGroup && (iLast < 0)) {
               String sName = xmlObject.getName();
               boolean bExact = false;
               // search location to add
               for (int i = 0; i < _lstXmlObjects.size(); i++) {
                  int compareTo = _lstXmlObjects.get(i).getName().compareTo(sName);
                  if (compareTo == 0) {
                     iLast = i + 1;
                     bExact = true;
                  } else if (bExact && iLast >= 0) {
                     break;
                  }
                  if (compareTo <= 0) {
                     iLast = i + 1;
                  }
               }
            }

            if (iLast < 0) {
               _lstXmlObjects.add(xmlObject);
            } else {
               _lstXmlObjects.add(iLast, xmlObject);
            }
         }
      }
   }

   /**
    * Method addObjects
    * 
    * @param parrXmlObjects
    * 
    * @author Andreas Brod
    */
   public void addObjects(XmlObject[] parrXmlObjects)
   {
      for (XmlObject pXmlObject : parrXmlObjects) {
         addObject(pXmlObject, false);
      }
   }

   /**
    * Method compareTo
    * 
    * @param pXmlObject
    * @return
    * 
    * @author $author$
    */
   @Override
   public int compareTo(XmlObject pXmlObject)
   {
      return getName().compareTo(pXmlObject.getName());
   }

   /**
    * Method copy
    * 
    * @return
    * 
    * @author $author$
    */
   public XmlObject copy()
   {
      return (new XmlObject(toString())).getFirstObject();
   }

   /**
    * Method copyFrom
    * 
    * @param pXmlObject
    * 
    * @author Andreas Brod
    */
   public void copyFrom(XmlObject pXmlObject)
   {
      String[] attribs = pXmlObject.getAttributeNames();

      for (String attrib : attribs) {
         setAttribute(attrib);
      }

      XmlObject[] subs = pXmlObject.getObjects("");

      for (XmlObject sub : subs) {
         addObject(sub);
      }

   }

   /**
    * Method countObjects return the amount of ALL subobjects for this
    * XmlObject
    * 
    * @return The amount of XmlObjects
    * 
    * @author brod
    */
   public int countObjects()
   {
      return _lstXmlObjects.size();
   }

   /**
    * Method createObject
    * 
    * @param psName
    * @return
    * 
    * @author Andreas Brod
    */
   public XmlObject createObject(String psName)
   {
      XmlObject xmlObject = getObject(psName);

      if (getObject(psName) == null) {
         xmlObject = new XmlObject("<" + psName + " />").getFirstObject();

         addObject(xmlObject);
      }

      return xmlObject;
   }

   /**
    * Method createObject
    * 
    * @param psName
    * @param piIndex Index
    * @return
    * 
    * @author Andreas Brod
    */
   public XmlObject createObject(String psName, int piIndex)
   {
      XmlObject xmlObject = getObject(psName);

      if (getObject(psName) == null) {
         xmlObject = new XmlObject("<" + psName + " />").getFirstObject();
         addObject(xmlObject, false, piIndex);
      }

      return xmlObject;
   }

   /**
    * Method createObject
    * 
    * @param psName
    * @param psAttribute
    * @param psValue
    * @param pbCreate
    * @return
    * 
    * @author Andreas Brod
    */
   public XmlObject createObject(String psName, String psAttribute, String psValue, boolean pbCreate)
   {
      XmlObject[] objs = getObjects(psName);

      for (XmlObject obj : objs) {
         if (obj.getAttribute(psAttribute).equals(psValue)) {
            return obj;
         }
      }

      if (!pbCreate) {
         return null;
      }

      XmlObject o =
         (new XmlObject("<" + psName + " " + psAttribute + "=\"" + psValue + "\" />"))
               .getFirstObject();

      addObject(o, true);

      return o;
   }

   /**
    * Method deleteObjects
    * 
    * @param psName
    * @return deleted objects
    * 
    * @author $author$
    */
   public XmlObject[] deleteObjects(String psName)
   {
      int i = 0;
      List<XmlObject> deletedObjects = new ArrayList<XmlObject>();

      while (i < _lstXmlObjects.size()) {
         XmlObject xmlObject = _lstXmlObjects.get(i);

         if (xmlObject.getName().equals(psName) || (psName.length() == 0)) {
            deletedObjects.add(_lstXmlObjects.remove(i));
         } else {
            i++;
         }
      }
      return deletedObjects.toArray(new XmlObject[deletedObjects.size()]);
   }

   /**
    * Method deleteObjects
    * 
    * @param pXmlObject
    * @return
    * 
    * @author Andreas Brod
    */
   public int deleteObjects(XmlObject pXmlObject)
   {
      if (pXmlObject == null) {
         return -1;
      }
      int id = -1;
      int i = 0;

      while (i < _lstXmlObjects.size()) {
         XmlObject xmlObject = _lstXmlObjects.get(i);

         if (xmlObject == pXmlObject) {
            id = i;

            _lstXmlObjects.remove(i);
         } else {
            i++;
         }
      }

      return id;
   }

   /**
    * Method findSubObject
    * 
    * @param psNameToFind
    * @return
    * 
    * @author $author$
    */
   public XmlObject findSubObject(String psNameToFind)
   {
      if (getName().equals(psNameToFind)) {
         return this;
      }

      for (int i = 0; i < _lstXmlObjects.size(); i++) {
         XmlObject xmlObject = _lstXmlObjects.get(i).findSubObject(psNameToFind);

         if (xmlObject != null) {
            return xmlObject;
         }
      }

      return null;
   }

   /**
    * Method findSubObject
    * 
    * @param psObjects
    * @param psAttribute
    * @param psValue
    * @return
    * 
    * @author Andreas Brod
    */
   public XmlObject findSubObject(String psObjects, String psAttribute, String psValue)
   {
      XmlObject[] objects = getObjects(psObjects);

      for (XmlObject object : objects) {
         if (object.getAttribute(psAttribute).equals(psValue)) {
            return object;
         }
      }

      return null;

   }

   /**
    * Method findSubObject
    * 
    * @param psObjects
    * @param psAttribute
    * @param psValue
    * @return
    * 
    * @author Andreas Brod
    */
   public XmlObject findSubObjectAndIgnoreCase(String psObjects, String psAttribute, String psValue)
   {
      XmlObject[] objects = getObjects(psObjects);

      for (XmlObject object : objects) {
         if (object.getAttribute(psAttribute).equalsIgnoreCase(psValue)) {
            return object;
         }
      }

      return null;

   }

   /**
    * Method findSubObjects
    * 
    * @param psObjects
    * @param psAttribute
    * @param psValue
    * @return
    * 
    * @author Andreas Brod
    */
   public XmlObject[] findSubObjects(String psObjects, String psAttribute, String psValue)
   {
      List<XmlObject> lst = new Vector<XmlObject>();
      XmlObject[] objects = getObjects(psObjects);

      for (XmlObject object : objects) {
         if (object.getAttribute(psAttribute).equals(psValue)) {
            lst.add(object);
         }
      }

      XmlObject[] res = new XmlObject[lst.size()];

      for (int j = 0; j < lst.size(); j++) {
         res[j] = lst.get(j);
      }

      return res;

   }

   /**
    * Method getAttribute
    * 
    * @param psAttributeName
    * @return
    * 
    * @author Andreas Brod
    */
   public String getAttribute(String psAttributeName)
   {
      for (int i = 0; i < _lstAttributes.size(); i++) {
         XmlAttribute a = _lstAttributes.get(i);

         if (a.matches(psAttributeName)) {
            return a._sValue;
         }
      }

      for (int i = 0; i < _lstXmlObjects.size(); i++) {
         XmlObject a = _lstXmlObjects.get(i);

         if (a.getName().equals(psAttributeName)) {
            return a.getCData();
         }
      }

      return "";
   }

   /**
    * Method getAttributeNames
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   public String[] getAttributeNames()
   {
      return getAttributeNames(false);
   }

   /**
    * Method getAttributeNames
    * 
    * @param pbName
    * @return
    * 
    * @author Andreas Brod
    */
   public String[] getAttributeNames(boolean pbName)
   {
      String[] oString = new String[_lstAttributes.size()];

      for (int i = 0; i < _lstAttributes.size(); i++) {
         XmlAttribute xmlObject = _lstAttributes.get(i);

         oString[i] = pbName ? xmlObject.getName() : xmlObject.toString();
      }

      return oString;
   }

   /**
    * Method getCData
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   public String getCData()
   {
      return _sCData;
   }

   /**
    * Method getCData
    * 
    * @param psName
    * @return
    * 
    * @author Andreas Brod
    */
   public String getCData(String psName)
   {
      XmlObject obj = getObject(psName);

      if (obj == null) {
         return "";
      }

      return obj._sCData;
   }

   /**
    * Method getFirstObject
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   public XmlObject getFirstObject()
   {
      if (_lstXmlObjects.size() == 0) {
         return null;
      }

      for (int i = 0; i < _lstXmlObjects.size(); i++) {
         XmlObject o = _lstXmlObjects.get(i);

         if (!o.getName().startsWith("!") && !o.getName().startsWith("?")) {
            return o;
         }
      }

      return null;
   }

   /**
    * Method toString
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   public String getHtmlString()
   {
      StringBuffer s = new StringBuffer();

      toHtmlStringBuffer(s);

      return "<pre><font face='Arial' size='8pt'>" + s.toString() + "</font></pre>";
   }

   /**
    * Method getIntAttribute
    * 
    * @param psAttributeName
    * @return
    * 
    * @author Andreas Brod
    */
   public int getIntAttribute(String psAttributeName)
   {
      try {
         return Integer.parseInt(getAttribute(psAttributeName));
      }
      catch (NumberFormatException ex) {
         return 0;
      }
   }

   /**
    * Method getLocation
    * 
    * @return
    * 
    * @author brod
    */
   public int getLocation()
   {
      if (_xmlParent != null) {
         XmlObject[] objects = _xmlParent.getObjects(this.getName());
         for (int i = 0; i < objects.length; i++) {
            if (objects[i] == this) {
               return i + 1;
            }
         }
      }
      return 0;
   }

   /**
    * Method getName
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   public String getName()
   {
      return _sName.getName();
   }

   /**
    * Method getNameSpace
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   public String getNameSpace()
   {
      return _sName.getNameSpace();
   }

   /**
    * Method getName
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   public String getFullName()
   {
      return _sName.getFullName();
   }

   /**
    * Method getObject
    * 
    * @param psName
    * @return
    * 
    * @author Andreas Brod
    */
   public XmlObject getObject(String psName)
   {
      for (int i = 0; i < _lstXmlObjects.size(); i++) {
         XmlObject xmlObject = _lstXmlObjects.get(i);

         String objName = psName.indexOf(":") > 0 ? xmlObject.getFullName() : xmlObject.getName();
         if (objName.equals(psName)) {
            return xmlObject;
         }
      }

      return null;
   }

   /**
    * Method getObjectNames
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   public String[] getObjectNames()
   {
      Hashtable<String, String> ht = new Hashtable<String, String>();
      int anz = 0;

      for (int i = 0; i < _lstXmlObjects.size(); i++) {
         XmlObject xmlObject = _lstXmlObjects.get(i);

         if (xmlObject != null) {
            ht.put(xmlObject.getName(), "");
         }

      }

      Enumeration<String> e = ht.keys();

      while (e.hasMoreElements()) {
         e.nextElement();

         anz++;
      }

      e = ht.keys();

      String[] oString = new String[anz];
      int i = 0;

      while (e.hasMoreElements()) {
         oString[i] = e.nextElement();

         i++;
      }

      return oString;
   }

   /**
    * Method getObjects
    * 
    * @param psObjectsName
    * @return
    * 
    * @author Andreas Brod
    */
   public XmlObject[] getObjects(String psObjectsName)
   {
      int anz = 0;

      String sObjectsName = psObjectsName;
      if (sObjectsName.indexOf(":") > 0) {
         sObjectsName = sObjectsName.substring(sObjectsName.indexOf(":") + 1);
      }
      for (int i = 0; i < _lstXmlObjects.size(); i++) {
         XmlObject xmlObject = _lstXmlObjects.get(i);

         if (((xmlObject != null) && xmlObject.getName().equals(sObjectsName))
               || (sObjectsName.length() == 0)) {
            anz++;
         }
      }

      if (anz == 0) {
         return new XmlObject[0];
      }

      XmlObject[] ret = new XmlObject[anz];

      anz = 0;

      for (int i = 0; i < _lstXmlObjects.size(); i++) {
         XmlObject xmlObject = _lstXmlObjects.get(i);

         if (((xmlObject != null) && xmlObject.getName().equals(sObjectsName))
               || (sObjectsName.length() == 0)) {
            ret[anz] = xmlObject;

            anz++;
         }
      }

      return ret;
   }

   /**
    * Method getParent
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   public XmlObject getParent()
   {
      return _xmlParent;
   }

   /**
    * Method isRoot
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   public boolean isRoot()
   {
      return getName().equals("root");
   }

   /**
    * Method readNextTag
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   public String readNextTag()
   {
      return readUntil('>', "");
   }

   /**
    * Method readUntil
    * 
    * @param pcUntil
    * @param psIgnoreAtStart
    * @return
    * 
    * @author Andreas Brod
    */
   public String readUntil(char pcUntil, String psIgnoreAtStart)
   {
      if (!_bOpen) {
         return "";
      }

      StringBuffer sBuffer = new StringBuffer("");
      boolean bOK = false;

      char i = 0;

      while ((i = _inStream.read()) > 0) {
         char c1 = i;

         if (pcUntil == c1) {
            String st = sBuffer.toString();

            if (st.startsWith("!--") && !st.endsWith("--")) {

               // make nothing
            } else if (st.startsWith("![CDATA[") && !st.endsWith("]]")) {

               // make nothing
            } else {
               return st;
            }
         }

         if (bOK || (psIgnoreAtStart.indexOf(c1) < 0)) {
            sBuffer.append(c1);

            bOK = true;
         }

      }

      _bOpen = false;

      return sBuffer.toString();
   }

   /**
    * Method readUntilStartTag
    * 
    * 
    * @param psIgnoreAtStart
    * @return
    * 
    * @author Andreas Brod
    */
   public String readUntilStartTag(String psIgnoreAtStart)
   {
      return readUntil('<', psIgnoreAtStart);
   }

   /**
    * Method replaceObject
    * 
    * @param pXmlObjectFrom
    * @param pXmlObjectWith
    * 
    * @author Andreas Brod
    */
   public void replaceObject(XmlObject pXmlObjectFrom, XmlObject pXmlObjectWith)
   {
      int i = 0;

      while (i < _lstXmlObjects.size()) {
         XmlObject xmlObject = _lstXmlObjects.get(i);

         if (xmlObject == pXmlObjectFrom) {
            _lstXmlObjects.remove(i);
            _lstXmlObjects.add(i, pXmlObjectWith);

            return;
         }
         i++;
      }
   }

   /**
    * Method setAttribute
    * 
    * @param psValue
    * 
    * @author $author$
    */
   public void setAttribute(String psValue)
   {
      int i = psValue.indexOf("=\"");

      if (i < 0) {
         return;
      }

      setAttribute(psValue.substring(0, i), psValue.substring(i + 2, psValue.length() - 1));
   }

   /**
    * Method setAttribute
    * 
    * @param psName
    * @param psValue
    * @return true if changed
    * 
    * @author Andreas Brod
    */
   public boolean setAttribute(String psName, String psValue)
   {
      return setAttribute(psName, psValue, true);
   }

   /**
    * Method setAttribute
    * 
    * @param psName
    * @param psValue
    * @param pbAppend true, if the XmlObject should be appended
    * @return true if changed
    * 
    * @author Andreas Brod
    */
   public boolean setAttribute(String psName, String psValue, boolean pbAppend)
   {
      for (int i = 0; i < _lstAttributes.size(); i++) {
         XmlAttribute a = _lstAttributes.get(i);

         if (a.matches(psName)) {
            if (psValue == null) {
               _lstAttributes.remove(i);

               return true;
            }

            String sValOld = a._sValue;

            a.setValue(psValue);

            return !sValOld.equals(psValue);
         }
      }

      if (psValue != null) {
         if (pbAppend) {
            _lstAttributes.add(new XmlAttribute(psName, psValue));
         } else {
            _lstAttributes.add(0, new XmlAttribute(psName, psValue));
         }
         return true;
      }
      return false;
   }

   /**
    * Method setCData
    * 
    * @param psData
    * 
    * @author Andreas Brod
    */
   public void setCData(String psData)
   {
      _sCData = psData;
   }

   /**
    * Method getNameSpace
    * 
    * 
    * @param psNameSpace
    * 
    * @author Andreas Brod
    */
   public void setNameSpace(String psNameSpace)
   {
      _sName.setNameSpace(psNameSpace);

      XmlObject[] subs = getObjects("");

      for (XmlObject sub : subs) {
         sub.setNameSpace(psNameSpace);
      }
   }

   /**
    * @param pXmlObject the parent to set
    * 
    */
   public void setParent(XmlObject pXmlObject)
   {
      _xmlParent = pXmlObject;
   }

   /**
    * Method sortObjects
    * @author $author$
    */
   public void sortObjects()
   {
      if (_lstXmlObjects.size() <= 1) {
         return;
      }

      XmlObject[] objs = getObjects("");

      Arrays.sort(objs);
      _lstXmlObjects.clear();

      for (XmlObject obj : objs) {
         _lstXmlObjects.add(obj);
      }
   }

   /**
    * Method sortSubObject
    * 
    * @param psObjects
    * @param psAttribute
    * @param pSortList
    * 
    * @author Andreas Brod
    */
   public void sortSubObject(String psObjects, String psAttribute, List<String> pSortList)
   {
      for (int j = 0; j < pSortList.size(); j++) {
         String psValue = pSortList.get(j);

         for (int i = 0; i < _lstXmlObjects.size() - 1;) {
            if (_lstXmlObjects.get(i).getAttribute(psAttribute).equals(psValue)) {
               XmlObject item = _lstXmlObjects.get(i);

               _lstXmlObjects.remove(i);
               _lstXmlObjects.add(item);
            } else {
               i++;
            }
         }
      }

   }

   /**
    * Method toHtmlStringBuffer
    * 
    * @param pStringBuffer
    * 
    * @author Andreas Brod
    */
   public void toHtmlStringBuffer(StringBuffer pStringBuffer)
   {
      if (_sLine.startsWith("/")) {
         return;
      }

      String sDeep = "";

      for (int i = 0; i < _piDeep; i++) {
         sDeep += BLANK + BLANK;
      }

      if (_sLine.startsWith("!")) {
         pStringBuffer.append(sDeep + FONT3 + "&lt;" + _sLine + "&gt;" + FONT_END + RETURN);

         return;
      }

      if (_sLine.startsWith("?")) {
         pStringBuffer.append(sDeep + FONT3 + "&lt;" + _sLine + "&gt;" + FONT_END + RETURN);

         return;
      }

      pStringBuffer.append(sDeep + FONT2 + "&lt;" + FONT1 + _sName + FONT_END);

      for (int i = 0; i < _lstAttributes.size(); i++) {

         // s.append("\n"+sDeep + blank+blank+ ((XmlAttribute) attr.get(i)).getHtmlString());
         pStringBuffer.append(BLANK + _lstAttributes.get(i).getHtmlString());
      }

      if (_lstXmlObjects.size() > 0) {
         pStringBuffer.append("&gt;" + RETURN);

         for (int i = 0; i < _lstXmlObjects.size(); i++) {
            _lstXmlObjects.get(i).toHtmlStringBuffer(pStringBuffer);
         }

         pStringBuffer.append(sDeep + "&lt;/" + FONT1 + _sName + FONT_END + "&gt;");
      } else if (_sCData.length() > 0) {
         pStringBuffer.append("&gt;" + _sCData + "&lt;/" + FONT1 + _sName + FONT_END + "&gt;");
      } else {
         pStringBuffer.append(" /&gt;");
      }

      pStringBuffer.append(FONT_END + RETURN);
   }

   /**
    * Method toString
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   @Override
   public String toString()
   {
      _piDeep = 0;

      StringBuffer s = new StringBuffer();

      toStringBuffer(s, false);

      return s.toString().trim();

   }

   /**
    * Method toStringBuffer
    * 
    * @param pStringBuffer
    * @param pbNewLine
    * 
    * @author Andreas Brod
    */
   private void toStringBuffer(StringBuffer pStringBuffer, boolean pbNewLine)
   {
      if (_sLine.startsWith("/")) {
         return;
      }

      String sEnd = (!bFormatXmlOutput || pbNewLine) ? "\n" : "";
      String sDeep = "";
      String sAdd = bFormatXmlOutput ? "    " : "  ";

      for (int i = 0; i < _piDeep; i++) {
         sDeep += sAdd;
      }

      if (_sLine.startsWith("!")) {
         pStringBuffer.append(sDeep + "<" + _sLine + ">" + sEnd);

         return;
      }

      if (_sLine.startsWith("?")) {
         pStringBuffer.append(sDeep + "<" + _sLine + ">" + sEnd);

         return;
      }

      if (getName().equals("root")) {
         for (int i = 0; i < _lstXmlObjects.size(); i++) {
            XmlObject next = _lstXmlObjects.get(i);

            next._piDeep = _piDeep + 1;

            next.toStringBuffer(pStringBuffer, i + 1 < _lstXmlObjects.size());

            // s.append(list.get(i).toString());
         }

      } else {

         pStringBuffer.append(sDeep + "<" + _sName);

         for (int i = 0; i < _lstAttributes.size(); i++) {

            // s.append("\n"+sDeep + "  "+ attr.get(i).toString());
            pStringBuffer.append(" " + _lstAttributes.get(i).toString());
         }

         if (_sCData.length() > 0) {
            String sCData = _sCData;
            if (sCData.contains("\n") || sCData.contains("\r") || sCData.contains("<")
                  || sCData.contains(">")) {
               sCData = "<![CDATA[" + sCData + "]]>";
            }
            pStringBuffer.append(">" + sCData + "</" + _sName + ">" + sEnd);
         } else if (_lstXmlObjects.size() > 0) {
            pStringBuffer.append(">\n");

            for (int i = 0; i < _lstXmlObjects.size(); i++) {
               if (_lstXmlObjects.get(i) != null) {
                  XmlObject next = _lstXmlObjects.get(i);

                  next._piDeep = _piDeep + 1;

                  next.toStringBuffer(pStringBuffer, i + 1 < _lstXmlObjects.size());
               }

               // s.append(list.get(i).toString());
            }

            if (!bFormatXmlOutput) {
               pStringBuffer.append(sDeep);
            }

            pStringBuffer.append("</" + _sName + ">" + sEnd);
         } else {
            pStringBuffer.append(" />" + sEnd);
         }
      }
   }

   // --------------------------------------------------------------------------------
   // ____ ___  ___  _ ___ _ ____ _  _ ____ _       ____ _    ____ ____ ____ ____ ____
   // |__| |  \ |  \ |  |  | |  | |\ | |__| |       |    |    |__| [__  [__  |___ [__
   // |  | |__/ |__/ |  |  | |__| | \| |  | |___    |___ |___ |  | ___] ___] |___ ___]
   //
   // --------------------------------------------------------------------------------

   /**
    * Class StringRead
    * 
    * <p>
    * Copyright &copy; 2002, i:FAO, AG.
    * @author Andreas Brod
    */
   class StringRead
   {
      private String _sText;
      private int _iStart = 0;

      /**
       * Constructor StringRead
       * 
       * @param psString
       * 
       */
      public StringRead(String psString)
      {
         _sText = psString;
      }

      /**
       * Method find
       * 
       * @param psText
       * @return found text
       * 
       * @author Andreas Brod
       */
      public char find(String psText)
      {
         while (_iStart < _sText.length()) {
            char c = _sText.charAt(_iStart);

            _iStart++;

            if (psText.indexOf(c) >= 0) {
               return c;
            }
         }

         return ' ';
      }

      /**
       * Method readUntil
       * 
       * @param psEnd
       * @param parrBreak string array of breaks
       * @return
       * 
       * @author Andreas Brod
       */
      public String readUntil(String psEnd, String[] parrBreak)
      {
         StringBuffer sbRet = new StringBuffer();

         while (_iStart < _sText.length()) {
            char c = _sText.charAt(_iStart);

            _iStart++;

            if (psEnd.indexOf(c) >= 0) {
               return sbRet.toString().trim();
            }

            sbRet.append(c);
            for (String element : parrBreak) {
               if (sbRet.indexOf(element) == 0) {
                  return sbRet.toString().trim();
               }
            }
         }

         return sbRet.toString().trim();
      }

      /**
       * Method readUntil2
       * 
       * @param psEnd
       * @return
       * 
       * @author Andreas Brod
       */
      public String readUntil2(String psEnd)
      {
         StringBuffer sbRet = new StringBuffer();
         char c1 = psEnd.charAt(0);
         char c2 = psEnd.charAt(1);
         char cLast = ' ';

         while (_iStart < _sText.length()) {
            char c = _sText.charAt(_iStart);

            _iStart++;

            if ((c1 == cLast) && (c2 == c)) {
               String sRet = sbRet.toString();
               return sRet.substring(0, sRet.length() - 1);
            }

            sbRet.append(c);

            cLast = c;
         }

         return sbRet.toString().trim();
      }
   }

   /**
    * Class XmlAttribute
    * 
    * <p>
    * Copyright &copy; 2002, i:FAO, AG.
    * @author Andreas Brod
    */
   class XmlAttribute
   {
      private XmlName _sXmlName;
      String _sValue;

      /**
       * Constructor XmlAttribute
       * 
       * @param psAttributeName
       * @param psValue
       * @return xml attribute
       * 
       */
      public XmlAttribute(String psAttributeName, String psValue)
      {
         _sXmlName = new XmlName(psAttributeName);
         _sValue = psValue;
      }

      /**
       * Method getHtmlString
       * 
       * @return
       * 
       * @author Andreas Brod
       */
      public String getHtmlString()
      {
         if (_sValue.length() > 0) {
            return FONT1 + _sXmlName.toString() + FONT_END + FONT2 + "=\"" + FONT_END + FONT3
                  + _sValue + FONT_END + FONT2 + "\"" + FONT_END;
         }

         return FONT1 + _sXmlName.toString() + FONT_END + FONT2 + "=\"\"" + FONT_END;
      }

      /**
       * Method getName
       * 
       * @return
       * 
       * @author Andreas Brod
       */
      public String getName()
      {
         return _sXmlName.toString();
      }

      /**
       * Method matches
       * 
       * @param psText
       * @return
       * 
       * @author Andreas Brod
       */
      public boolean matches(String psText)
      {
         if ((psText.indexOf(":") > 0) && _sXmlName.getFullName().equals(psText)) {
            return true;
         }

         return _sXmlName.getName().equals(psText);
      }

      /**
       * Method setValue
       * 
       * @param psValue
       * 
       * @author Andreas Brod
       */
      public void setValue(String psValue)
      {
         _sValue = psValue;
      }

      /**
       * Method toString
       * 
       * @return
       * 
       * @author Andreas Brod
       */
      @Override
      public String toString()
      {
         return _sXmlName.toString() + "=\"" + _sValue + "\"";
      }

   }

   /**
    * Class XmlName
    * 
    * <p>
    * Copyright &copy; 2002, i:FAO, AG.
    * @author Andreas Brod
    */
   class XmlName
   {
      private String _sXmlName = "";
      private String _sNameSpace = "";

      /**
       * Constructor XmlName
       * 
       * @param psName
       * 
       */
      public XmlName(String psName)
      {
         if (psName.indexOf(":") > 0) {
            _sNameSpace = psName.substring(0, psName.indexOf(":"));
            _sXmlName = psName.substring(psName.indexOf(":") + 1);
         } else {
            _sXmlName = psName;
         }
      }

      /**
       * xmls a name.
       * 
       * @param psNameSpace name space String
       * @param psName name String
       * 
       * @author Brod
       */
      public XmlName(String psNameSpace, String psName)
      {
         _sXmlName = psName;
         _sNameSpace = psNameSpace;
      }

      /**
       * Method getFullName
       * 
       * @return
       * 
       * @author Andreas Brod
       */
      public String getFullName()
      {
         if (_sNameSpace.length() > 0) {
            return _sNameSpace + ":" + _sXmlName;
         }

         return _sXmlName;
      }

      /**
       * Method getName
       * 
       * @return
       * 
       * @author Andreas Brod
       */
      public String getName()
      {
         return _sXmlName;
      }

      /**
       * Method getNameSpace
       * 
       * @return
       * 
       * @author Andreas Brod
       */
      public String getNameSpace()
      {
         return _sNameSpace;
      }

      /**
       * Method setNameSpace
       * 
       * @param psNameSpace
       * 
       * @author Andreas Brod
       */
      public void setNameSpace(String psNameSpace)
      {
         _sNameSpace = psNameSpace;
      }

      /**
       * Method toString
       * 
       * @return
       * 
       * @author Andreas Brod
       */
      @Override
      public String toString()
      {
         return getFullName();
      }
   }

   /**
    * This method changes the Name
    * 
    * @param psName Name
    * 
    * @author brod
    */
   public void setName(String psName)
   {

      _sName = new XmlName(psName);
   }

   /**
    * sets a name.
    * 
    * @param psNameSpace name space String
    * @param psName name String
    * 
    * @author Brod
    */
   public void setName(String psNameSpace, String psName)
   {

      _sName = new XmlName(psNameSpace, psName);
   }

   /**
    * adds a comment.
    * 
    * @param psKey key String
    * 
    * @author Brod
    */
   public void addComment(String psKey)
   {
      _lstXmlObjects.addAll(new XmlObject("<!-- " + psKey + " -->")._lstXmlObjects);
   }

   /**
    * adds an object.
    * 
    * @param piIndex index int
    * @param pElement element object Xml Object
    * 
    * @author Brod
    */
   public void addObject(int piIndex, XmlObject pElement)
   {
      _lstXmlObjects.add(piIndex, pElement);
   }

   /**
    * delete all subobjects
    */
   public void deleteObjects()
   {
      _lstXmlObjects.clear();
   }

}


/**
 * Class StringStream is internally used to handle the diffenent input
 * possibilites. And handles correctly the uft-8 convertion.
 * If Buffers the complete text/file/stream into a formated charArray,
 * which can be read with the method <code>read()</code>.
 * 
 * <p>
 * Copyright &copy; 2006, i:FAO
 * 
 * @author brod
 */
class StringStream
{

   /**
    * The default (internal) method getUTF8Bytes which converts the
    * utf8-characters.
    * 
    * @param psText The input text
    * @return the 'formated' utf8 conform text.
    * 
    * @author brod
    */
   private static char[] getUTF8Bytes(String psText)
   {
      try {
         int iStart = psText.indexOf("encoding=\"UTF-8\"");
         if (iStart > 0) {
            String s1 =
               new String(psText.substring(psText.indexOf("?>", iStart) + 2).getBytes(), "UTF-8");
            while ((s1.length() > 0) && (s1.charAt(0) <= 32)) {
               s1 = s1.substring(1);
            }
            return s1.toCharArray();
         }
      }
      catch (Exception e) {
         // ignore this
      }
      return psText.toCharArray();
   }

   private char[] _charArray;

   private int _iCount = 0;

   /**
    * Constructor for StringStream with a File
    * 
    * @param pInputFile The inputFile
    * @return
    * @throws FileNotFoundException
    * 
    * @author brod
    */
   public StringStream(File pInputFile)
      throws FileNotFoundException
   {
      this(new FileInputStream(pInputFile));
   }

   /**
    * Constructor for StringStream with an InputStream
    * 
    * @param pInputStream The inputStream
    * @return
    * 
    * @author brod
    */
   public StringStream(InputStream pInputStream)
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try {
         byte[] b = new byte[4096];
         int anz;
         while ((anz = pInputStream.read(b)) >= 0) {
            out.write(b, 0, anz);
         }
         pInputStream.close();
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      _charArray = getUTF8Bytes(out.toString());

   }

   /**
    * Constructor for StringStream with a 'normal' StringText
    * 
    * @param psText The default Text
    * @return
    * 
    * @author brod
    */
   public StringStream(String psText)
   {
      _charArray = getUTF8Bytes(psText);
   }

   /**
    * The method read reads the next char until end. In case of end #0
    * will be returned
    * 
    * @return the next characted (0 indicates the end).
    * 
    * @author brod
    */
   public char read()
   {

      if (_iCount < _charArray.length) {
         _iCount++;
         return _charArray[_iCount - 1];
      }
      return (char) 0;
   }

}
