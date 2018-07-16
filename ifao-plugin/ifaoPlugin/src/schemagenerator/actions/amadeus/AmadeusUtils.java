package schemagenerator.actions.amadeus;


import java.io.*;
import java.net.*;
import java.util.*;

import net.ifao.xml.XmlObject;
import schemagenerator.actions.Utils;


/**
 * This class is a AmadeusUtilities class
 *
 * <p>
 * Copyright &copy; 2010, i:FAO
 *
 * @author brod
 */
public class AmadeusUtils
{

   /**
    * Method addTag
    * Macht angegebene in der Datei Correct.txt Korrekturen
    * [poweredpnr\pnrreply\data.xsd]
    * path=PoweredPNR_PNRReply/originDestinationDetails/itineraryInfo/typicalCarData/locationInfo
    * tag=maxOccurs
    * value=5
    * action=ADD
    * @param fileName
    * @param path
    * @param sTag
    * @param sValue
    *
    * @author schesler
    * @param pOut
    */
   private static void addTag(File psFileName, String path, String sTag, String sValue,
                              boolean pbReplace, PrintStream pOut)
   {
      StringTokenizer st = new StringTokenizer(path, "/");
      String sElement = st.hasMoreTokens() ? st.nextToken() : "";
      try {
         boolean bChange = false;
         XmlObject schema = new XmlObject(psFileName);
         XmlObject xmlObject = schema.getFirstObject();
         XmlObject findSubObject = xmlObject;
         XmlObject lastElement = null;
         while (st.hasMoreTokens()
               && (findSubObject = findSubObject.findSubObject("element", "name", sElement)) != null) {
            lastElement = findSubObject;
            findSubObject = findSubObject.getObject("complexType").getObject("sequence");
            sElement = st.nextToken();
         }
         if (findSubObject != null) {
            lastElement = findSubObject.findSubObject("element", "name", sElement);
         }
         if (findSubObject != null && lastElement != null) {
            if (pbReplace || lastElement.getAttribute(sTag).length() > 0) {
               bChange = lastElement.setAttribute(sTag, sValue);
            }
         }

         if (bChange) {
            String sCorrected = "Corrected " + path + ".";
            pOut.println(sCorrected);
            sCorrected = "<!-- " + sCorrected + " (defined within Correct.txt) -->";
            String sFile = schema.toString();
            if (!sFile.contains(sCorrected)) {
               int iPos = sFile.indexOf("<xs:");
               if (iPos > 0) {
                  sFile = sFile.substring(0, iPos) + sCorrected + "\n" + sFile.substring(iPos);
               } else {
                  sFile = sCorrected + "\n" + sFile;
               }
            }
            FileWriter writer = new FileWriter(psFileName);
            writer.write(sFile);
            writer.flush();
            writer.close();
         }
      }
      catch (Exception e) {
         // should never happen
      }

   }

   /**
    * This method copies a File
    *
    * @param pSourceFile source file
    * @param pTagetFile target file
    *
    * @author brod
    */
   static void copyFile(File pSourceFile, File pTagetFile)
   {
      if (pSourceFile.exists()) {
         try {
            FileInputStream in = new FileInputStream(pSourceFile);
            FileOutputStream out = new FileOutputStream(pTagetFile);
            byte[] bytes = new byte[4096];
            int count;
            while ((count = in.read(bytes)) > 0) {
               out.write(bytes, 0, count);
            }
            in.close();
            out.close();
         }
         catch (IOException e) {
            // should never happen ... but has to be catched
         }
      }
   }

   /**
    * This method deletes a File (pay attention, that if this is
    * a directory all files will be deleted)
    *
    * @param pFileToDelete FileToDelete
    *
    * @author brod
    */
   static void deleteFile(File pFileToDelete)
   {
      if (pFileToDelete.exists()) {
         if (pFileToDelete.isDirectory()) {
            File[] listFiles = pFileToDelete.listFiles();
            for (File listFile : listFiles) {
               deleteFile(listFile);
            }
         }
         pFileToDelete.delete();
      }
   }

   /**
    * This method extracts the Html from a line (all
    * special elements will be ignored)
    *
    * @param psLine html line
    * @return line without html code
    *
    * @author brod
    */
   static String extractHtml(String psLine)
   {
      StringBuilder sbRet = new StringBuilder();
      char[] charArray = psLine.toCharArray();
      boolean bOn = true;
      boolean bNewLine = false;
      for (char element : charArray) {
         if (element == '<') {
            bOn = false;
         } else if (element == '>') {
            bOn = true;
         } else if (bOn) {
            if (element == '\n' || element == '\r') {
               if (bNewLine) {
                  sbRet.append('\n');
                  bNewLine = false;
               }
            } else if (element == ' ' || element == '\t') {
               if (bNewLine) {
                  sbRet.append(' ');
               }
            } else {
               sbRet.append(element);
               bNewLine = true;
            }
         }
      }
      return sbRet.toString().replaceAll("&nbsp;", " ").replaceAll("&gt;", ">")
            .replaceAll("&lt;", "<").trim();
   }

   /**
    * This  method extracts Xml within a line
    *
    * @param psLine html Line
    * @return extracted xml elements
    *
    * @author brod
    */
   static String extractXml(String psLine)
   {
      StringBuilder sbRet = new StringBuilder();
      StringBuilder sbNonRet = new StringBuilder();
      char[] charArray = psLine.toCharArray();
      boolean bOn = true;
      String sLink = "";
      for (char element : charArray) {
         if (element == '<') {
            bOn = false;
            if (sLink.length() > 0 && !sLink.equals("#")) {
               int iStart = sbRet.lastIndexOf("&lt;");
               if (iStart >= 0) {
                  int iEnd1 = sbRet.indexOf("/&gt;", iStart);
                  int iEnd2 = sbRet.indexOf("&gt;", iStart);
                  int iEnd;
                  if (iEnd1 > 0 && iEnd1 < iEnd2) {
                     iEnd = iEnd1;
                  } else {
                     iEnd = iEnd2;
                  }
                  if (iEnd > 0 && iEnd > iStart) {
                     sbRet.insert(iEnd, " href=\"" + sLink + "\"");
                  } else {
                     sbRet.append(" href=\"" + sLink + "\"");
                  }
               }
            }
         } else if (element == '>') {
            bOn = true;
            sbNonRet.append(element);
            List<String> tags = getTags(sbNonRet.toString(), "href", "");
            if (tags.size() == 0) {
               tags = getTags(sbNonRet.toString(), "HREF", "");
            }
            if (tags.size() > 0) {
               sLink = tags.get(0);
            } else {
               sLink = "";
            }
            sbNonRet.setLength(0);
         } else if (bOn) {
            sbRet.append(element);
         } else {
            sbNonRet.append(element);
         }
      }
      // replace chracters between the elements
      charArray =
         sbRet.toString().replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&nbsp;", " ")
               .trim().toCharArray();
      sbRet.setLength(0);
      bOn = false;
      for (char element : charArray) {
         if (element == '<') {
            bOn = true;
            sbRet.append("<");
         } else if (element == '>') {
            bOn = false;
            sbRet.append(">");
         } else if (bOn) {
            sbRet.append(element);
         }
      }
      return sbRet.toString();
   }

   /**
    * This method returns the lines of a text
    *
    * @param psPage plain text
    * @return List of lines
    *
    * @author brod
    */
   static List<String> getLines(String psPage)
   {
      StringTokenizer st = new StringTokenizer(psPage, "\n\r");
      List<String> lst = new ArrayList<String>();
      while (st.hasMoreTokens()) {
         lst.add(st.nextToken());
      }
      return lst;
   }

   /**
    * This method returns the RelativePath
    *
    * @param psOldPath OldPath
    * @param psNewPath NewPath
    * @return RelativePath
    *
    * @author brod
    */
   static String getRelativePath(String psOldPath, String psNewPath)
   {
      StringTokenizer stOld = new StringTokenizer(psOldPath, "/\\");
      StringTokenizer stNew = new StringTokenizer(psNewPath, "/\\");
      List<String> lst = new ArrayList<String>();
      while (stOld.hasMoreTokens()) {
         String nextToken = stOld.nextToken();
         if (stOld.hasMoreTokens()) {
            lst.add(nextToken);
         }
      }
      // remove last token
      while (stNew.hasMoreTokens()) {
         String nextToken = stNew.nextToken();
         if (nextToken.equals(".")) {
            // make nothing
         } else if (nextToken.equals("..")) {
            if (lst.size() > 0) {
               lst.remove(lst.size() - 1);
            }
         } else {
            lst.add(nextToken);
         }
      }
      StringBuilder sbRet = new StringBuilder();
      for (int i = 0; i < lst.size(); i++) {
         if (i > 0) {
            sbRet.append("/");
         }
         sbRet.append(lst.get(i));
      }
      return sbRet.toString();
   }

   /**
    * This method returns the Tags of
    *
    * @param psHtmlPage HtmlPage
    * @param psTag Tag
    * @param psPattern Matching Pattern
    * @return list of tags
    *
    * @author brod
    */
   static List<String> getTags(String psHtmlPage, String psTag, String psPattern)
   {

      List<String> lst = new ArrayList<String>();
      String sStartTag = " " + psTag + "=";
      int iPos = psHtmlPage.indexOf(sStartTag);
      while (iPos > 0) {
         iPos += sStartTag.length();
         // get the next two characters
         String sNext = psHtmlPage.substring(iPos, iPos + 2);
         String sEndTag;
         if (sNext.startsWith("\"")) {
            iPos++;
            sEndTag = "\"";
         } else if (sNext.startsWith("'")) {
            iPos++;
            sEndTag = "'";
         } else if (sNext.startsWith("\\\"")) {
            iPos += 2;
            sEndTag = "\\\"";
         } else {
            sEndTag = ">";
         }

         String sValue = psHtmlPage.substring(iPos, psHtmlPage.indexOf(sEndTag, iPos));
         if (psPattern.length() == 0 || sValue.contains(psPattern)) {
            if (!lst.contains(sValue)) {
               lst.add(sValue);
            }
         }
         iPos = psHtmlPage.indexOf(sStartTag, iPos);
      }
      return lst;
   }

   /**
    * This method returns a list of Tags
    *
    * @param psHtmlPage HtmlPage
    * @param psStartTag StartTag
    * @param psEndTag EndTag
    * @param psPattern Matching Pattern
    * @return list of tags
    *
    * @author brod
    */
   static List<String> getTags(String psHtmlPage, String psStartTag, String psEndTag,
                               String psPattern)
   {
      List<String> lst = new ArrayList<String>();
      int iPos = psHtmlPage.indexOf(psStartTag);
      while (iPos > 0) {
         iPos += psStartTag.length();

         String sValue = psHtmlPage.substring(iPos, psHtmlPage.indexOf(psEndTag, iPos));
         if (psPattern.length() == 0 || sValue.contains(psPattern)) {
            if (!lst.contains(sValue)) {
               lst.add(sValue);
            }
         }
         iPos = psHtmlPage.indexOf(psStartTag, iPos);
      }
      return lst;
   }

   /**
    * Method startCorrect
    * Sucht die richtige Stelle, wo was geändert muss
    *
    * @param psFileName Path zur Datei Correct.txt
    * @param pOut PrintStream (for output)
    *
    * @author schesler
    */
   public static void startCorrect(String psFileName, PrintStream pOut)
   {
      File sFileName = null;
      String sPath = "", sTag = "", sValue = "";
      BufferedReader reader;
      try {
         File cfgFile = new File(psFileName);
         reader = new BufferedReader(new FileReader(cfgFile));
         String sLine;
         while ((sLine = reader.readLine()) != null) {
            if (sLine.startsWith("[")) {
               sFileName = new File(cfgFile.getParent(), sLine.substring(1, sLine.length() - 1));
            }
            final int iPos = sLine.indexOf("=");
            if (iPos > 0) {
               String sPre = sLine.substring(0, iPos);
               String sPost = sLine.substring(iPos + 1);
               if (sPre.equalsIgnoreCase("path")) {
                  sPath = sPost;
               }
               if (sPre.equalsIgnoreCase("tag")) {
                  sTag = sPost;
               }
               if (sPre.equalsIgnoreCase("value")) {
                  sValue = sPost;
               }
               if (sPre.equalsIgnoreCase("action")) {
                  if (sPost.equalsIgnoreCase("add")) {
                     addTag(sFileName, sPath, sTag, sValue, false, pOut);
                  }
                  if (sPost.equalsIgnoreCase("Replace")) {
                     addTag(sFileName, sPath, sTag, sValue, true, pOut);
                  }
                  sFileName = null;
                  sPath = "";
                  sTag = "";
                  sValue = "";
               }
            }

         }
         reader.close();
      }
      catch (IOException e) {}

   }


   /**
    * static months
    *
    * @author brod
    */
   static String[] sMonth = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT",
         "NOV", "DEC" };

   /**
    * Method addTag
    * Macht angegebene in der Datei Correct.txt Korrekturen
    * [poweredpnr\pnrreply\data.xsd]
    * path=PoweredPNR_PNRReply/originDestinationDetails/itineraryInfo/typicalCarData/locationInfo
    * tag=maxOccurs
    * value=5
    * action=ADD
    * @param fileName
    * @param path
    * @param sTag
    * @param sValue
    *
    * @author schesler
    * @param pOut
    */
   private static void addTag(String fileName, String path, String sTag, String sValue,
                              boolean pbReplace, PrintStream pOut)
   {
      StringTokenizer st = new StringTokenizer(path, "/");
      String sElement = st.hasMoreTokens() ? st.nextToken() : "";
      StringBuffer sbFile = new StringBuffer();
      try {
         BufferedReader reader = new BufferedReader(new FileReader(fileName));
         String sLine;
         boolean bChange = false;
         while ((sLine = reader.readLine()) != null) {
            final int iPos = sLine.indexOf(" name=\"" + sElement + "\"");
            if (iPos > 0) {
               // search next Token
               if (st.hasMoreTokens()) {
                  sElement = st.nextToken();
               } else {
                  // found element
                  sElement = "";
                  if (sLine.indexOf(sTag) < 0) {
                     // inseret Tag
                     sLine =
                        sLine.substring(0, iPos) + " " + sTag + "=\"" + sValue + "\""
                              + sLine.substring(iPos);
                     bChange = true;
                  } else if (pbReplace) {
                     sLine =
                        sLine.replaceAll(sTag + "=\"" + "[0-9]*" + "\"", sTag + "=\"" + sValue
                              + "\"");
                     bChange = true;
                  }
               }
            }
            sbFile.append(sLine + "\n");
         }
         reader.close();

         if (bChange) {
            String sCorrected = "Corrected " + path + ".";
            pOut.println(sCorrected);
            sCorrected = "<!-- " + sCorrected + " (defined within Correct.txt) -->";
            String sFile = sbFile.toString();
            if (!sFile.contains(sCorrected)) {
               int iPos = sFile.indexOf("<xs:");
               if (iPos > 0) {
                  sFile = sFile.substring(0, iPos) + sCorrected + "\n" + sFile.substring(iPos);
               } else {
                  sFile = sCorrected + "\n" + sFile;
               }
            }
            FileWriter writer = new FileWriter(fileName);
            writer.write(sFile);
            writer.flush();
            writer.close();
         }
      }
      catch (IOException e) {}

   }

   /**
    * Method compareXmlObjects
    *
    * @param pXmlObject1
    * @param pXmlObject2
    * @param pOut PrintStream for output
    *
    * @author Andreas Brod
    */
   private static void compareXmlObjects(XmlObject pXmlObject1, XmlObject pXmlObject2,
                                         PrintStream pOut)
   {
      XmlObject[] list1 = pXmlObject1.getObjects("");
      XmlObject[] list2 = pXmlObject2.getObjects("");

      // get the SubObjects from o1 and validate is theses are available
      for (XmlObject element : list1) {
         XmlObject find2 = pXmlObject2.getObject(element.getName());

         if (find2 != null) {
            compareXmlObjects(element, find2, pOut);
         } else {

            // set minOccurs = 0
            pOut.println("Element : " + element.getName() + " not known in example");
            element.setAttribute("minOccurs", "0");
         }
      }

      // get the SubObjects from o2 and add these if not avail
      for (XmlObject element : list2) {
         XmlObject find1 = pXmlObject1.getObject(element.getName());

         if (find1 == null) {
            pOut.println("Element : " + element.getName() + " not known in help-files");
            initXmlObject(element);
            pXmlObject1.addObject(element);
         }
      }

   }

   /**
    * Method delete
    *
    * @param psFile
    *
    * @author Andreas Brod
    */
   public static void delete(String psFile)
   {
      File f = new File(psFile);

      if (f.isDirectory()) {
         File[] files = f.listFiles();

         for (File file : files) {
            delete(file.getAbsolutePath());
         }
      }

      if (f.exists()) {
         f.delete();
      }
   }

   /**
    * Method getJavaCDir
    *
    * @param psDir
    * @return
    *
    * @author Andreas Brod
    */
   public static String getJavaCDir(String psDir)
   {
      String sRet = "";
      File f = new File(psDir);

      if (f.isDirectory()) {
         File[] files = f.listFiles();

         for (int i = 0; sRet.length() == 0 && i < files.length; i++) {
            sRet = getJavaCDir(files[i].getAbsolutePath());
         }
      } else if (f.getName().equalsIgnoreCase("javac.exe")) {
         return f.getParentFile().getAbsolutePath();
      }

      return sRet;
   }

   /**
    * Method getMiddlePart returns the middle between start and end-string.
    * In there are "link"-tags within the 'middle' these are added into the
    * links-object.
    *
    * @param psIn
    * @param pLinks
    * @param psStart
    * @param psEnd
    * @return
    *
    * @author Andreas Brod
    */
   public static String getMiddlePart(String psIn, Vector<String> pLinks, String psStart,
                                      String psEnd)
   {
      String sRet = "";
      StringTokenizer st = new StringTokenizer(psIn, "\n\r");
      int on = 0;

      while (st.hasMoreTokens()) {
         String s = st.nextToken().trim();

         if (on == 0 && s.indexOf(psStart) >= 0) {
            on = 1;
         } else if (on == 1 && s.indexOf(psEnd) >= 0) {
            on = 2;
         } else if (on == 1) {
            if (s.length() > 0) {
               StringBuffer s1 = new StringBuffer();
               String sLink = "";

               for (int i = 0; i < s.length(); i++) {
                  char c = s.charAt(i);

                  switch (c) {

                     case '<':
                        sLink = "" + c;
                        break;

                     case '>':
                        pLinks.add(sLink + ">");

                        sLink = "";
                        break;

                     default:
                        if (sLink.length() > 0) {
                           sLink += c;
                        } else {
                           s1.append(c);
                        }
                  }
               }

               sRet += s1.toString() + "\n";
            }
         }
      }

      return replaceHtml(sRet);
   }

   /**
    * Method getNumber
    *
    * @param psText
    * @param psSearch
    * @return
    *
    * @author Andreas Brod
    */
   public static int getNumber(String psText, String psSearch)
   {
      int max = -1;

      if (psText.indexOf(psSearch) > 0) {
         String sRepetition =
            psText.substring(psText.indexOf("=", psText.indexOf(psSearch)) + 1).trim();
         int i = 0;

         while (i < sRepetition.length() && sRepetition.charAt(i) >= '0'
               && sRepetition.charAt(i) <= '9') {
            i++;
         }

         try {
            max = Integer.parseInt(sRepetition.substring(0, i));
         }
         catch (NumberFormatException ex) {
            max = 0;
         }
      }

      return max;
   }

   /**
    * Method getInputStream
    *
    * @param psURL
    * @return
    * @throws MalformedURLException
    *
    * @author Andreas Brod
    */
   public static InputStream getURLInputStream(String psURL)
      throws MalformedURLException, IOException
   {
      URL url = new URL(psURL);
      InputStream in = url.openStream();

      return in;
   }

   /**
    * Method initXmlObject
    *
    * @param pXmlObject
    *
    * @author Andreas Brod
    */
   private static void initXmlObject(XmlObject pXmlObject)
   {
      XmlObject[] sub = pXmlObject.getObjects("");

      pXmlObject.setAttribute("minOccurs", "0");

      if (sub.length > 0) {
         pXmlObject.setAttribute("maxOccurs", "99");
         pXmlObject.setAttribute("type", "node");
         pXmlObject.setAttribute("comment", "New Node from example");

         for (XmlObject element : sub) {
            initXmlObject(element);
         }
      } else {
         pXmlObject.setAttribute("maxOccurs", "1");
         pXmlObject.setAttribute("type", "string");
         pXmlObject.setAttribute("comment", "New Attribute from example");
      }
   }

   /**
    * Method loads File
    *
    * @param pFile
    *
    * @return loaded file
    *
    * @author Andreas Brod
    */
   public static String loadFile(File pFile)
   {
      String sRet = "";

      try {
         BufferedReader br = new BufferedReader(new FileReader(pFile));
         String sLine;

         while ((sLine = br.readLine()) != null) {
            sRet += sLine + "\n";
         }
      }
      catch (IOException ex) {}

      return sRet;
   }

   /**
    * Method replaceFirst
    *
    * @param psText
    * @param psKey
    * @param psValue
    * @return
    *
    * @author Andreas Brod
    */
   public static String replaceFirst(String psText, String psKey, String psValue)
   {
      if (psText.indexOf(psKey) >= 0) {
         int i = psText.indexOf(psKey);
         String s0 = "";

         if (i > 0) {
            s0 += psText.substring(0, i);
         }

         s0 += psValue;
         i += psKey.length();

         //   if (i < psText.length()) {
         //      ;
         //   }

         psText = s0 + psText.substring(i + 1);

      }

      return psText;
   }

   /**
    * Method sReplace
    *
    * @param psText
    * @return
    *
    * @author Andreas Brod
    */
   private static String replaceHtml(String psText)
   {
      psText = psText.replaceAll("&lt;", "<");
      psText = psText.replaceAll("&gt;", ">");
      psText = psText.replaceAll("&nbsp;", " ");

      return psText;
   }

   /**
    * Method writeToFile
    *
    * @param psFileName
    * @param psText
    *
    * @author Andreas Brod
    */
   public static void writeToFile(String psFileName, String psText)
   {
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(psFileName));

         bw.write(psText);
         bw.close();
      }
      catch (IOException ex) {}

   }

   /**
    * method xcopy
    *
    * @param psFrom file from
    * @param psTo file to
    *
    * @author brod
    */
   public static void xcopy(String psFrom, String psTo)
   {
      File file = new File(psFrom);
      if (file.exists() && !file.isDirectory()) {
         // form has to exist
         try {
            // copy the file
            String sContent = Utils.readFile(file);
            File fileTo = new File(psTo);
            Utils.writeFile(fileTo, sContent);

         }
         catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

}
