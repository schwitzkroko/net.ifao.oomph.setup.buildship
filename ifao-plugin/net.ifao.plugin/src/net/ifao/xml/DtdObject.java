package net.ifao.xml;


/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
import ifaoplugin.*;

import java.util.*;

import java.io.*;


/** 
 * Class DtdObject 
 * 
 * <p> 
 * Copyright &copy; 2002, i:FAO, AG. 
 * @author Andreas Brod 
 */
public class DtdObject
{
   Vector<String> list = new Vector<String>();
   StringBuffer textBuffer = new StringBuffer();

   /** 
    * Constructor DtdObject 
    * 
    * <p> TODO rename f to another name
    * @param f 
    * 
    */
   public DtdObject(File f)
   {
      try {
         FileInputStream fin = new FileInputStream(f);

         read(fin);
         fin.close();
      }
      catch (IOException ex) {}
   }

   /** 
    * Constructor DtdObject 
    * 
    * <p> TODO rename s to another name
    * @param s 
    * 
    */
   public DtdObject(String s)
   {
      read(new ByteArrayInputStream(s.getBytes()));
   }

   /** 
    * Constructor DtdObject 
    * 
    * @param pin 
    * 
    */
   public DtdObject(InputStream pin)
   {
      read(pin);
   }

   /** 
    * Method read 
    * 
    * @param pin 
    * 
    * @author Andreas Brod 
    */
   private void read(InputStream pin)
   {
      BufferedReader br = new BufferedReader(new InputStreamReader(pin));

      try {

         // int i = pin.read();
         boolean an = false;
         StringBuffer sLine = new StringBuffer();
         char lastChar = 'x';
         char[] cArray = new char[4000];
         int anz = 0;

         while ((anz = br.read(cArray)) > 0) {

            // textBuffer.append(new String(cArray, 0, anz) + "\n");
            textBuffer.append(new String(cArray, 0, anz));

            for (int i = 0; i < anz; i++) {

               char c = cArray[i];

               if (c <= ' ') {
                  c = ' ';
               }

               if (c == '<') {
                  an = true;
               }

               if (an) {
                  if ((c > ' ') || (lastChar != ' ')) {
                     sLine.append(c);
                  }
               }

               if (c == '>') {
                  String s = sLine.toString();

                  if (!s.startsWith("<!--")) {
                     list.add(s);
                  }

                  sLine = new StringBuffer();
                  an = false;
               }

               lastChar = c;
            }
         }
      }
      catch (IOException ex) {}
   }

   /** 
    * Method getRemark 
    * 
    * <p> TODO rename sElement to psElement
    * @param sElement 
    * @return 
    * 
    * @author $author$ 
    */
   public String getRemark(String sElement)
   {
      String sText = getText();

      sElement = "<!ELEMENT " + sElement.trim() + " ";

      int i = sText.indexOf(sElement);

      if ((i > 0) && (sText.lastIndexOf("<!--", i) > 0)) {
         try {
            sText = sText.substring(sText.lastIndexOf("<!--", i) + 3, sText.lastIndexOf("-->", i));
         }
         catch (Exception ex) {
            return "";
         }

         return sText;
      }

      return "";
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
      StringBuffer s = new StringBuffer();

      for (int i = 0; i < list.size(); i++) {
         s.append(list.get(i) + "\n");
      }

      return s.toString();
   }

   /** 
    * Method getText 
    * 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public String getText()
   {
      return textBuffer.toString();
   }

   /** 
    * Method getAttribute 
    * 
    * <p> TODO rename name to psName
    * @param name 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public String getAttribute(String name)
   {
      name = name.trim();

      StringBuffer s = new StringBuffer();

      // s.append(getATTLIST(name)+"\n");
      // s.append(getELEMENT(name)+"\n");
      s.append(getXmlELEMENT(name, "") + "\n");

      return s.toString();
   }

   /** 
    * Method isMandatory 
    * 
    * <p> TODO rename sName to psName, sAttribute to psAttribute
    * @param sName 
    * @param sAttribute 
    * @return 
    * 
    * @author $author$ 
    */
   public boolean isMandatory(String sName, String sAttribute)
   {
      String sText = getText() + ">";

      String sElement = "<!ELEMENT " + sName.trim() + " ";

      int i = sText.indexOf(sElement);

      if (i > 0) {
         sText = sText.substring(i, sText.indexOf(">", i)) + " ";
         sText = Util.replaceString(sText, "(", " ");
         sText = Util.replaceString(sText, ")", "");
         sText = Util.replaceString(sText, ",", " ");
         sAttribute = " " + sAttribute;

         if (sText.indexOf(sAttribute + "+") > 0) {
            return true;
         } else if (sText.indexOf(sAttribute + "?") > 0) {
            return false;
         } else if (sText.indexOf(sAttribute + "*") > 0) {
            return false;
         } else if (sText.indexOf(sAttribute + " ") > 0) {
            return true;
         }
      }

      return false;
   }

   /** 
    * Method isMandatory 
    * 
    * <p> TODO rename sName to psName, sAttribute to psAttribute
    * @param sName 
    * @param sAttribute 
    * @return 
    * 
    * @author $author$ 
    */
   public boolean isList(String sName, String sAttribute)
   {
      String sText = getText() + ">";

      String sElement = "<!ELEMENT " + sName.trim() + " ";

      int i = sText.indexOf(sElement);

      if (i > 0) {
         sText = sText.substring(i, sText.indexOf(">", i)) + " ";
         sText = Util.replaceString(sText, "(", " ");
         sText = Util.replaceString(sText, ")", "");
         sText = Util.replaceString(sText, ",", " ");
         sText = Util.replaceString(sText, "|", " ");
         sAttribute = " " + sAttribute;

         if (sText.indexOf(sAttribute + "+") > 0) {
            return true;
         } else if (sText.indexOf(sAttribute + "?") > 0) {
            return false;
         } else if (sText.indexOf(sAttribute + "*") > 0) {
            return true;
         } else if (sText.indexOf(sAttribute + " ") > 0) {
            return false;
         }
      }

      return false;
   }

   /** 
    * Method getXmlObject 
    * 
    * <p> TODO rename name to psName
    * @param name 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public XmlObject getXmlObject(String name)
   {
      return new XmlObject(getAttribute(name));
   }

   /** 
    * Method getATTLIST 
    * 
    * <p> TODO rename name to psName
    * @param name 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private String getATTLIST(String name)
   {
      for (int i = 0; i < list.size(); i++) {
         String s1 = list.get(i);

         if (s1.startsWith("<!ATTLIST " + name + " ")) {
            return s1;
         }
      }

      return "";
   }

   /** 
    * Method getENTITY 
    * 
    * <p> TODO rename name to psName
    * @param name 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private String getENTITY(String name)
   {
      for (int i = 0; i < list.size(); i++) {
         String s1 = list.get(i);

         if (s1.startsWith("<!ENTITY % " + name + " \"")) {
            s1 = s1.substring(s1.indexOf("\"") + 1);

            if (s1.indexOf("\"") > 0) {
               s1 = s1.substring(0, s1.indexOf("\""));

               return s1;
            }
         }
      }

      return "";
   }

   /** 
    * Method getXmlATTLIST 
    * 
    * <p> TODO rename name to psName
    * @param name 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private String getXmlATTLIST(String name)
   {
      String s = getATTLIST(name) + "   ";

      s = s.substring(s.indexOf(" ") + 1);
      s = s.substring(s.indexOf(" ") + 1);

      StringBuffer[] sAtt = { new StringBuffer(), new StringBuffer(), new StringBuffer() };
      StringBuffer sRet = new StringBuffer();
      int i = 0;
      char cEnd = ' ';

      for (int j = 0; j < s.length(); j++) {
         char c = s.charAt(j);

         if (c == cEnd) {
            if (sAtt[i].length() > 0) {
               sAtt[i].append(c);

               i++;

               if (i == 3) {
                  String sAtt0 = sAtt[0].toString().trim();
                  String sAtt1 = sAtt[1].toString().trim();
                  String sAtt2 = sAtt[2].toString().trim();

                  sRet.append(sAtt0 + "=");

                  String sText = "";

                  if (sAtt2.startsWith("\"")) {
                     sText = "_" + sAtt[2].substring(1, sAtt[2].length() - 1);
                  }

                  {
                     if (sAtt2.startsWith("#REQUIRED")) {
                        sRet.append("\"!");
                     } else {
                        sRet.append("\"?");
                     }

                     if (sAtt1.startsWith("%Boolean;")) {
                        sRet.append("yes");
                     } else if (sAtt1.startsWith("%Byte;") || sAtt1.startsWith("%Short;")
                           || sAtt1.startsWith("%Integer;") || sAtt1.startsWith("%Long;")) {
                        sRet.append("0");
                     } else if (sAtt1.startsWith("%Float;") || sAtt1.startsWith("%Double;")) {
                        sRet.append("0.0");
                     } else if (sAtt1.startsWith("%Character;")) {
                        sRet.append("X");
                     } else if (sAtt1.startsWith("%String;")) {
                        sRet.append("abc");
                     } else if (sAtt1.startsWith("%Date;")) {
                        sRet.append("%DATE(7)%");
                     } else if (sAtt1.startsWith("%DateTime;")) {
                        sRet.append("%DATETIME(7)%");
                     } else if (sAtt1.startsWith("%Duration;")) {
                        sRet.append("P0DT0H0M0S");
                     } else if (sAtt1.startsWith("%EncryptedNumber;")) {
                        sRet.append("***");
                     } else if (sAtt1.startsWith("%Version;")) {
                        sRet.append("1.0");
                     } else if (sAtt1.startsWith("%Text;")) {
                        sRet.append("abc");
                     } else if (sAtt1.startsWith("%") && sAtt1.endsWith(";")) {
                        sRet.append(this.getENTITY(sAtt1.substring(1, sAtt1.length() - 1)));
                     }
                  }

                  sRet.append(sText + "\" ");

                  for (i = 0; i < 3; i++) {
                     sAtt[i].delete(0, sAtt[i].length());
                  }

                  i = 0;
               }

               cEnd = ' ';
            }
         } else {
            sAtt[i].append(c);

            if ((cEnd == ' ') && (c == '&')) {
               cEnd = ';';
            }

            if ((cEnd == ' ') && (c == '\"')) {
               cEnd = '\"';
            }
         }
      }

      return sRet.toString().trim();
   }

   /** 
    * Method getELEMENT 
    * 
    * <p> TODO rename name to psName
    * @param name 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private String getELEMENT(String name)
   {
      for (int i = 0; i < list.size(); i++) {
         String s1 = list.get(i);

         if (s1.startsWith("<!ELEMENT " + name + " ")) {

            return s1;
         }
      }

      return "";
   }

   /** 
    * Method getXmlELEMENT 
    * 
    * <p> TODO rename name to psName, sList to psList
    * @param name 
    * @param sList 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private String getXmlELEMENT(String name, String sList)
   {
      if (sList.indexOf("," + name + ",") >= 0) {
         return "";
      }

      sList += "," + name + ",";

      String sRet = "<" + name + " " + getXmlATTLIST(name);
      String s = getELEMENT(name);

      if (s.indexOf("(") < 0) {
         sRet += "/>\n";
      } else {
         if (s.indexOf("|") > 0) {
            sRet += " _choice=\"true\"";
         }
         sRet += ">\n";
         s = s.substring(s.indexOf("(") + 1);

         int j = s.length() - 1;

         while ((j > 0) && (s.charAt(j) != ')')) {
            j--;
         }

         s = s.substring(0, j + 1);

         StringTokenizer st = new StringTokenizer(s, " <>()*?!+,|");

         while (st.hasMoreTokens()) {
            sRet += getXmlELEMENT(st.nextToken(), sList);
         }

         sRet += "</" + name + ">\n";
      }

      return sRet;
   }

   /** 
    * Class AttList 
    * 
    * <p> 
    * Copyright &copy; 2002, i:FAO, AG. 
    * @author Andreas Brod 
    */
   class AttList
   {
      int start = 0;
      String s;

      /**
       * Constructor AttList
       *
       * @param ps
       */
      public AttList(String ps)
      {
         s = ps;
      }

      /**
       * Method getNextItem
       *
       * @return
       * @author Andreas Brod
       */
      public String getNextItem()
      {
         start++;

         if (start >= s.length()) {
            return s;
         }

         return s;
      }
   }

}
