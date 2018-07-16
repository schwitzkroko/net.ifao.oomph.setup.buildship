package net.ifao.xml;


import java.io.*;
import java.util.*;


/** 
 * The Class Xml is a base Class for Xml-Formatting
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
public class Xml
   implements Comparable<Xml>
{

   private String sWhiteSpaceTag = " \n\r\t/=\">";
   private String sName = "";
   InputStream inStream;
   char iLast = 0;
   int iLevel = 0;
   boolean bFinished = false;
   Hashtable<String, String> htAttrib = new Hashtable<String, String>();
   List<Xml> subObjects = new ArrayList<Xml>();
   private String sCData = "";
   private boolean bHasData = false;
   private Xml parent;
   private String sTextPre = "";
   private String sUTF = "";

   public Xml(File f)
      throws FileNotFoundException
   {
      this(readFile(f));
   }

   private static String readFile(File f)
   {
      StringBuffer sb = new StringBuffer();
      try {
         FileReader reader = new FileReader(f);
         char[] chars = new char[4096];
         int anz;
         while ((anz = reader.read(chars)) >= 0) {
            sb.append(chars, 0, anz);
         }
      }
      catch (IOException e1) {}
      return sb.toString();
   }

   public Xml(String s)
   {
      this(-1, null, new ByteArrayInputStream(("<*>" + s + "</*>").getBytes()));
   }

   private String readUntil(String s, boolean ignoreWhitespaces)
   {
      StringBuffer sRet = new StringBuffer();
      read();

      while (!bFinished && s.indexOf(iLast) < 0) {
         if (ignoreWhitespaces) {
            if (" \t\n\r".indexOf(iLast) < 0) {
               sRet.append(iLast);
               ignoreWhitespaces = false;
            }
         } else {
            sRet.append(iLast);
         }
         read();
      }
      return getText(sRet);

   }

   private void read()
   {
      try {
         int i = inStream.read();
         bFinished = i < 0;
         iLast = (char) i;
      }
      catch (IOException e) {
         bFinished = true;
      }
      if (!bFinished)
         bHasData = true;
   }

   private String readUntilWord(String psEndText)
   {
      StringBuffer sRet = new StringBuffer();
      read();
      char[] csEndText = psEndText.toCharArray();
      // get a array as big as the searchtext
      char[] csLastChars = new char[psEndText.length()];
      // get the last character
      int iSearchLen = psEndText.length() - 1;

      while (!bFinished) {
         // add new character the StringBuffer
         sRet.append(iLast);

         // push the iLast character to the array
         for (int i = 0; i < csLastChars.length - 1; i++) {
            csLastChars[i] = csLastChars[i + 1];
         }
         // the the lastchar
         csLastChars[iSearchLen] = iLast;
         boolean bFound = true;
         for (int i = 0; bFound && i < csEndText.length; i++) {
            bFound = csEndText[i] == csLastChars[i];
         }

         if (bFound) {
            return getText(sRet);
         }
         // read next entry
         read();
      }

      return getText(sRet);

   }

   private String getText(StringBuffer sb)
   {
      if (sUTF.length() > 0) {
         try {
            return new String(sb.toString().getBytes(), sUTF);
         }
         catch (UnsupportedEncodingException e) {}
      }
      return sb.toString();
   }

   private Xml(int piLevel, Xml pParent, InputStream in)
   {
      parent = pParent;
      inStream = in;
      iLevel = piLevel;
      if (pParent != null) {
         sUTF = pParent.sUTF;
      }
      // Xml has to start with "<"
      sTextPre = readUntil("<", true);

      sName = readUntil(sWhiteSpaceTag, false);
      if (sName.length() == 0 && iLast == '/') {
         sName = "/" + readUntil(sWhiteSpaceTag, false);
      }

      if (sName.startsWith("!--")) {
         bHasData = true;
         sName += iLast;
         if (!sName.endsWith("-->")) {
            sName += readUntilWord("-->");
         }
         return;
      }

      if (sName.startsWith("![CDATA[")) {
         bHasData = true;
         sTextPre += "<" + sName + iLast;
         if (!sTextPre.endsWith("]]>")) {
            sTextPre += readUntilWord("]]>");
         }
         sName = "";
         return;
      }
      // get until the endTag
      while (!bFinished && "/>".indexOf(iLast) < 0) {
         // get the attributes
         String sAttrib = readUntil("\"/>", true);
         if (sAttrib.endsWith("=")) {
            setAttribute(sAttrib.substring(0, sAttrib.length() - 1), readUntil("\"", false));
         }
      }
      if (sName.startsWith("?") || sName.startsWith("!")) {
         if (sName.equalsIgnoreCase("?xml")) {
            String encode = getAttribute("encoding");
            if (encode.equalsIgnoreCase("UTF-8") || encode.equalsIgnoreCase("UTF8")) {
               setUTF("UTF-8");
            }
         }
         return;
      }

      if (iLast == '/') {
         // there is no end-Tag
         readUntil(">", false);
      } else if (sName.startsWith("/")) {
         // make nothing
      } else if (iLast == '>') {
         // get SubObjects
         Xml subObject = new Xml(iLevel + 1, this, in);
         sCData += subObject.sTextPre;
         while (subObject.hasData() && !subObject.sName.startsWith("/" + sName)) {
            if (subObject.sName.length() > 0) {
               addObject(subObject);
            }
            subObject = new Xml(iLevel + 1, this, in);
         }

      }


   }


   private void setUTF(String string)
   {
      sUTF = string;
      if (parent != null)
         parent.setUTF(sUTF);

   }

   private boolean hasData()
   {
      return bHasData;
   }

   public int compareTo(Xml arg0)
   {
      return toString().compareTo(arg0.toString());
   }

   public Xml getFirstObject()
   {
      for (int i = 0; i < subObjects.size(); i++) {
         String sName1 = subObjects.get(i).sName;
         if (sName1.startsWith("?") || sName1.startsWith("!")) {
            continue;
         }
         subObjects.get(i).setLevel(0);
         return subObjects.get(i);
      }
      return null;

   }

   public boolean setAttribute(String psName, String psValue)
   {
      String sValue = htAttrib.get(psName);
      if (psValue == null) {
         if (sValue != null) {
            htAttrib.remove(psName);
            return true;
         }
         return false;
      }
      psValue =
         psValue.replaceAll("\"", "&quot;").replaceAll("\\r\\n", "<br>").replaceAll("\\n", "<br>");
      if (!psValue.equals(sValue)) {
         htAttrib.put(psName, psValue);
         return true;
      }
      return false;
   }

   public String getAttribute(String string)
   {
      String sRet = htAttrib.get(string);
      if (sRet == null)
         return "";
      sRet = sRet.replaceAll("&quot;", "\"").replaceAll("<br>", "\n");

      return sRet;
   }

   public void deleteObjects(String string)
   {
      if (string == null || string.length() == 0) {
         subObjects.clear();
         return;
      }
      Xml[] objects = getObjects(string);

      for (int i = 0; i < objects.length; i++) {
         subObjects.remove(objects[i]);
      }

   }

   public void addObject(Xml itemObject)
   {
      itemObject.setLevel(iLevel + 1);
      subObjects.add(itemObject);
   }

   private void setLevel(int piValue)
   {
      iLevel = piValue;
      for (int i = 0; i < subObjects.size(); i++) {
         subObjects.get(i).setLevel(piValue + 1);
      }
   }

   public Xml copy()
   {
      return new Xml(toString()).getFirstObject();
   }

   public Xml[] getObjects(String string)
   {
      List<Xml> lstFound = new ArrayList<Xml>();
      Object[] objects = subObjects.toArray();
      for (int i = 0; i < objects.length; i++) {
         if (string.length() == 0 || ((Xml) objects[i]).sName.equals(string)) {
            lstFound.add((Xml) objects[i]);
         }
      }
      return getArray(lstFound);
   }

   private Xml[] getArray(List<Xml> lst)
   {
      Xml[] ret = new Xml[lst.size()];
      for (int i = 0; i < ret.length; i++) {
         ret[i] = lst.get(i);
      }
      return ret;
   }

   public String getName()
   {
      return sName;
   }

   public Xml findSubObject(String string, String string2, String preCase)
   {

      Xml[] objects = getObjects(string);
      for (int i = 0; i < objects.length; i++) {
         if (objects[i].getAttribute(string2).equals(preCase)) {
            return objects[i];
         }
      }
      return null;
   }

   public String getCData()
   {
      if (sCData.startsWith("<![CDATA[") && sCData.endsWith("]]>")) {
         return getNoHtml(sCData.substring(9, sCData.length() - 3));
      }
      return sCData;
   }

   public Xml getParent()
   {
      return parent;
   }

   public void deleteObjects(Xml Xml)
   {
      subObjects.remove(Xml);

   }

   public void setCData(String text)
   {
      if (text.startsWith("<![CDATA[") && text.endsWith("]]>")) {
         // make nothing
      } else {
         if (text.indexOf("\n") >= 0 || text.indexOf("<") >= 0 || text.indexOf(">") >= 0) {
            text = "<![CDATA[" + getHtml(text) + "]]>";
         }
      }
      sCData = text;

   }

   private String getNoHtml(String sHtml)
   {
      return sHtml.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
   }

   private String getHtml(String sHtml)
   {
      return sHtml.replaceAll("\\<", "&lt;").replaceAll("\\>", "&gt;");
   }


   @Override
   public String toString()
   {
      // ensure, that UTF-8 encoding is enabled
      if (iLevel < 0) {
         Xml xmlParams = createObject("?xml", false);
         xmlParams.setAttribute("encoding", "UTF-8");
         xmlParams.setAttribute("version", "1.0");
      }
      StringBuffer sRet = new StringBuffer();
      toString(sRet);
      try {
         if (iLevel < 0)
            return new String(sRet.toString().getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException e) {}
      return sRet.toString();
   }

   private Xml createObject(String string, boolean append)
   {
      Xml[] objects = getObjects(string);
      if (objects.length > 0) {
         return objects[0];
      }
      Xml firstObject = null;
      if (string.startsWith("?")) {
         firstObject = (new Xml("<" + string + " ?>")).getObjects(string)[0];
      } else {
         string = "<" + string + " />";
         firstObject = (new Xml(string)).getFirstObject();
      }
      firstObject.setLevel(iLevel + 1);
      if (append)
         subObjects.add(firstObject);
      else
         subObjects.add(0, firstObject);
      return firstObject;
   }

   public void toString(StringBuffer sRet)
   {

      char[] cDeep = new char[Math.max(iLevel, 0) * 2];
      Arrays.fill(cDeep, ' ');
      String sDeep = new String(cDeep);
      if (sName.length() > 0) {
         if (iLevel >= 0) {
            sRet.append(sDeep);
            sRet.append("<" + sName);
            Object[] attribs = htAttrib.keySet().toArray();
            for (int i = 0; i < attribs.length; i++) {
               sRet.append(" " + attribs[i] + "=\"" + htAttrib.get(attribs[i]) + "\"");
            }

            if (sCData.length() > 0 || subObjects.size() > 0) {
               sRet.append(">" + sCData);
               if (subObjects.size() > 0) {
                  sRet.append("\n");
                  for (int i = 0; i < subObjects.size(); i++) {
                     subObjects.get(i).toString(sRet);
                  }
                  sRet.append(sDeep);
               }
               sRet.append("</" + sName + ">");
            } else {
               if (sName.endsWith(">")) {
                  // make nothing
               } else if (sName.startsWith("?")) {
                  sRet.append("?>");

               } else {
                  sRet.append("/>");
               }
            }
            sRet.append("\n");
         } else {
            for (int i = 0; i < subObjects.size(); i++) {
               subObjects.get(i).toString(sRet);
            }

         }
      } else {
         sRet.append(sCData);
      }
   }

   public static void main(String[] args)
   {

      try {
         Xml xml = new Xml(new File("H:\\move\\brod\\ResponseReaderFareFinder.tsc"));

         FileWriter writer = new FileWriter("H:\\move\\brod\\ResponseReaderFareFinder.out");
         writer.write(xml.toString());
         writer.close();
      }
      catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public void setAsRoot()
   {
      setLevel(-1);

   }

   public Xml createObject(String string)
   {

      return createObject(string, true);
   }

   public String[] getAttributeNames()
   {
      // TODO Auto-generated method stub
      Object[] objects = htAttrib.keySet().toArray();
      Arrays.sort(objects);
      String[] ret = new String[objects.length];
      for (int i = 0; i < ret.length; i++) {
         ret[i] = (String) objects[i];
      }
      return ret;
   }

}
