package net.ifao.plugins.tools;


import java.io.*;
import java.util.*;
import java.util.List;


/**
 * The class CodeFormatterApplication is an application with which
 * you can format a java-sourcecode in a IFAO-relevant style
 * 
 * <p>
 * Copyright &copy; 2006, i:FAO
 * 
 * @author brod
 */
class CodeFormatterApplicationV1
   extends CodeFormatterApplication
{


   /**
    * The Method readBlock reads interally the SourceCode (recursively)
    * 
    * @param psClassName The name of this java-class
    * @param piDeep The deep level (for brackets)
    * @param reader The InputStrem with the data
    * @param psEndBlock  The type of the endBlock (e.g. "}" if a
    * {...} has to be read)
    * @param piStatus The status 0=default, 1=comment, 2=String/char
    * @return  The read String
    * 
    * @author brod
    */
   @Override
   String readBlock(String psClassName, int piDeep, InputStream reader, String psEndBlock,
                    int piStatus, int iMaxDeep, boolean bInterface)
   {
      StringBuffer sb = new StringBuffer();
      int i, iLast = 0;
      String sLast = "";
      String sLastComment = "";
      String sParams = "";
      String sMethod = "";
      int iLastPos = 0;
      try {
         while ((i = reader.read()) >= 0) {
            char c = (char) i;

            sb.append(c);
            if ((piStatus == 2) && (iLast == '\\')) {
               // comment ignore \"
               i = ' ';
            } else if (psEndBlock.equals(sLast + (char) i)) {
               return sb.toString();
            } else if (piStatus >= 1) {
               // ignore everything until end
            } else if (((i == '\'') || (i == '\"')) && (iLast != '\\')) {
               sb.append(readBlock(psClassName, piDeep, reader, "" + (char) i, 2, iMaxDeep,
                     bInterface));
            } else if (i == '(') {
               sMethod = sb.substring(iLastPos, sb.length() - 1).toString().trim();
               sParams = readBlock(psClassName, piDeep, reader, ")", 0, iMaxDeep, bInterface);
               sb.append(sParams);

            } else if (i == '{') {
               if ((piDeep < 2) && (sMethod.indexOf("=") < 0)) {
                  modifyComment(sb, iLastPos, sMethod, sLastComment, psClassName, sParams);
               }

               String sNewSub =
                  readBlock(psClassName, piDeep + 1, reader, "}", 0, iMaxDeep, bInterface);
               sb.append(sNewSub);
               iLastPos = sb.length();
               sLastComment = "";
               sParams = "";
               sMethod = "";

            } else if ((i == '/') && (iLast == '/')) {
               sb.append(readBlock(psClassName, piDeep, reader, "\n", 1, iMaxDeep, bInterface));
               iLastPos = sb.length();
               i = ' ';
               sLastComment = "";
            } else if ((i == '*') && (iLast == '/')) {
               sLastComment = readBlock(psClassName, piDeep, reader, "*/", 1, iMaxDeep, bInterface);
               sb.append(sLastComment);
               sLastComment = "/*" + sLastComment;
               i = ' ';
            } else if (i == ';') {
               iLastPos = sb.length();
            }


            if (psEndBlock.length() == 2) {
               sLast = "" + (char) i;
            }
            iLast = i;
         }
      }
      catch (IOException e) {
         e.printStackTrace();

      }
      return sb.toString();
   }

   /**
    * The method modifyComment creates / modifies a comment and puts it
    * into the StringBuffer
    * 
    * @param psbText The StringBuffer with the current Test
    * @param piLastPos The LastPossition (at which the comment starts)
    * @param psMethod the name of the method/class
    * @param psLastComment The 'original' Comment
    * @param psClassName the name of the class (for class and constructor)
    * @param psParams The params of the method
    * 
    * @author brod
    */
   private void modifyComment(StringBuffer psbText, int piLastPos, String psMethod,
                              String psLastComment, String psClassName, String psParams)
   {
      int iEndPos = piLastPos;
      String sCmd = psbText.substring(piLastPos);
      String sThrows = "";
      if ((sCmd.indexOf(")") > 0)
            && (sCmd.substring(sCmd.lastIndexOf(")")).indexOf(" throws ") > 0)) {
         sThrows = sCmd.substring(sCmd.lastIndexOf("throws"));
         if (sThrows.indexOf("{") > 0) {
            sThrows = sThrows.substring(0, sThrows.indexOf("{")).trim();
         }
      }

      String sMethodName = "";
      sMethodName = psMethod.substring(psMethod.lastIndexOf(" ") + 1);
      String sRetType = "";
      sRetType = " " + psMethod.substring(0, psMethod.lastIndexOf(" ") + 1).trim();
      sRetType = sRetType.substring(sRetType.lastIndexOf(" ") + 1).trim();
      boolean bHasReturnValue = true;
      if (psLastComment.length() == 0) {
         if ((sCmd.indexOf("class") >= 0) && (sMethodName.length() == 0)) {
            psLastComment =
               "/**\n * TODO (" + sAuthor + ") add comment for class " + psClassName + "\n *\n"
                     + " * <p>\n" + " * Copyright &copy; "
                     + (new GregorianCalendar()).get(Calendar.YEAR) + ", i:FAO\n" + " *\n"
                     + " * @author " + sAuthor + "\n */";

         } else if (sMethodName.equals(psClassName)) {
            psLastComment =
               "/**\n * TODO (" + sAuthor + ") add comment for Constructor " + sMethodName
                     + "\n *\n * @author " + sAuthor + "\n */";
            bHasReturnValue = false;
         } else {
            psLastComment =
               "/**\n * TODO (" + sAuthor + ") add comment for method " + sMethodName
                     + "\n *\n * @author " + sAuthor + "\n */";
         }
      } else {
         piLastPos = psbText.lastIndexOf(psLastComment);
         iEndPos = piLastPos + psLastComment.length();
      }

      // go to first next character
      while ((iEndPos < psbText.length()) && (" \t\n\r".indexOf(psbText.charAt(iEndPos)) >= 0)) {
         iEndPos++;
      }

      // go to last first character
      while ((piLastPos > 1) && (" \t\n\r".indexOf(psbText.charAt(piLastPos - 1)) >= 0)) {
         piLastPos--;
      }

      // extract comment in lines
      StringTokenizer st = new StringTokenizer(psLastComment, "\n");

      List<String> lstElements = new Vector<String>();
      List<String> lstTokens = new Vector<String>();
      StringBuffer sbLast = null;
      Hashtable<String, StringBuffer> htTokens = new Hashtable<String, StringBuffer>();
      String sLineLast = "";
      boolean bLastComment = false;
      while (st.hasMoreTokens()) {
         String sLine = st.nextToken().replaceAll("\t", "   ").trim() + " ";
         if ((sLine.length() == 1) || sLine.endsWith("*/ ")) {
            // make nothing
         } else if (sLine.startsWith("* <p> TODO rename ")) {
            bLastComment = true;
         } else if (sLine.startsWith("* TODO rename ") && sLineLast.trim().endsWith("<p>")) {
            lstElements.remove(lstElements.size() - 1);
            bLastComment = true;
         } else if (sLine.startsWith("* @")) {
            String sToken = sLine.substring(3, sLine.indexOf(" ", 3));

            if (sToken.equals("param")) {
               sToken = sLine.substring(3, sLine.indexOf(" ", 4 + sToken.length()));
            } else if (sToken.equals("return") || sToken.equals("throws")) {
               // make nothing
            } else if (!lstTokens.contains(sToken)) {
               lstTokens.add(sToken);
            }
            sbLast = new StringBuffer();
            sbLast.append(sLine + "\n");
            htTokens.put(sToken, sbLast);
         } else if (sbLast != null) {
            if (!sLine.trim().equals("*")) {
               sbLast.append(sLine + "\n");
            }
         } else if (!bLastComment) {
            lstElements.add(sLine);
         }
         sLineLast = sLine;
      }
      psLastComment = "\n \n ";
      for (Object element : lstElements) {
         String sElement = (String) element;
         psLastComment += " " + sElement + "\n";
      }

      String sToDos = "";
      String sDocParams = "";

      // add parameters
      st = new StringTokenizer(psParams.replaceAll("\t", " "), ",()");
      while (st.hasMoreTokens()) {
         String sLine2 = st.nextToken().trim();
         if (sLine2.indexOf(" ") > 0) {
            String sParamType = sLine2.substring(0, sLine2.indexOf(" "));
            String sParamName = sLine2.substring(sLine2.lastIndexOf(" ") + 1);
            String sParam = "param " + sParamName;
            if (htTokens.get(sParam) != null) {
               sDocParams += " " + htTokens.get(sParam);
            } else {
               sDocParams +=
                  " * @" + sParam + " TODO (" + sAuthor + ") add text for " + sParam + "\n";
            }
            if ((sParamName.length() > 0) && (sParamType.length() > 0)) {

               String sPre = "p";
               if (sParamType.startsWith("byte")) {
                  sPre += "y";
               } else if (sParamType.startsWith("short")) {
                  sPre += "t";
               } else if ((sParamType.charAt(0) >= 'a') && (sParamType.charAt(0) <= 'z')) {
                  sPre += sParamType.charAt(0);
               } else if (sParamType.equals("String")) {
                  sPre += "s";
               }
               // sParamName2 has to start uppercase

               if (sParamName.length() > 2) {

                  if (!sParamName.startsWith(sPre)) {

                     String sParamName2 = sParamName;
                     while ((sParamName2.length() > 0) && (sParamName2.charAt(0) >= 'a')
                           && (sParamName2.charAt(0) <= 'z')) {
                        sParamName2 = sParamName2.substring(1);
                     }
                     if (sParamName2.length() == 0) {
                        sParamName2 = sParamName;
                     }

                     sParamName2 =
                        sParamName2.substring(0, 1).toUpperCase() + sParamName2.substring(1);

                     sParamName2 = sPre + sParamName2;

                     if (sToDos.length() > 0) {
                        sToDos += ", ";
                     }
                     sToDos += sParamName + " to " + sParamName2;
                  }
               } else {
                  sToDos += sParamName + " to another name";
               }
            }
         }
      }

      // add return value
      if ((sRetType.length() > 0) && !sRetType.equals("void") && !sMethodName.equals(psClassName)) {
         if (htTokens.get("return") != null) {
            sDocParams += htTokens.get("return");
         } else if (bHasReturnValue) {
            sDocParams += " * @return TODO (" + sAuthor + ") add text for returnValue\n";
         }
      }
      // add throws value
      if (sThrows.length() > 0) {
         if (htTokens.get("throws") != null) {
            sDocParams += htTokens.get("throws");
         } else {
            sDocParams += " * @" + sThrows + "\n";
         }
      }

      if (sDocParams.length() > 0) {
         sDocParams += " * \n";
      }


      // add todos
      if (sToDos.length() > 0) {
         psLastComment += " * <p> TODO rename " + sToDos + "\n";
      }
      psLastComment += sDocParams;


      // add additional elements
      for (Object element : lstTokens) {
         String sElement = (String) element;
         if (sElement.length() > 0) {
            psLastComment += " " + htTokens.get(sElement);
         }
      }
      // add additional elements

      psLastComment += " */\n";


      psbText.replace(piLastPos, iEndPos, psLastComment);

   }

   /**
    * The main Method
    * @param psArgs the arguments
    * 
    */
   public static void main(String[] psArgs)
   {
      if (psArgs.length != 2) {
         System.out.println("Syntax " + CodeFormatterApplicationV1.class.getName()
               + " <RootDir> <FileName>");
         System.out.println("  whereas FileName is relative to RootDir.");
         System.out.println("  e.g. " + CodeFormatterApplicationV1.class.getName()
               + "\n     C:\\arctic\\eclipse\\main" + "\n     src\\net\\ifao\\util\\Base64.java");
         System.exit(1);
         return;
      }
      String sRoot = psArgs[0];
      String sFileName = sRoot + "/" + psArgs[1];

      File f = new File(sFileName);
      formatCode(f, sRoot, sFileName, false);
   }


}
