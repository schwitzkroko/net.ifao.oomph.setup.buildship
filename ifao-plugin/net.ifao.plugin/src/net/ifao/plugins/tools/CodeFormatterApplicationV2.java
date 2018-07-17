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
class CodeFormatterApplicationV2
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
    * @param piMaxDeep
    */
   @Override
   String readBlock(String psClassName, int piDeep, InputStream reader, String psEndBlock,
                    int piStatus, int piMaxDeep, boolean pbInterface)
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
               sb.append(readBlock(psClassName, piDeep, reader, "" + (char) i, 2, piMaxDeep,
                     pbInterface));
            } else if (i == '(') {
               sMethod = sb.substring(iLastPos, sb.length() - 1).toString().trim();
               sParams = readBlock(psClassName, piDeep, reader, ")", 0, piMaxDeep, pbInterface);
               sb.append(sParams);

            } else if ((i == '{')
                  || ((sMethod.contains(" abstract ") || pbInterface) && (i == ';'))) {
               String sCmd = sb.substring(iLastPos);
               if (sCmd.contains("*/")) {
                  sCmd = sCmd.substring(sCmd.lastIndexOf("*/"));
               }
               String sClassName = null;
               if (sCmd.contains("class ")) {
                  sClassName = getType(sCmd, "class");
               }
               boolean bInterface = sCmd.contains("interface ");
               if (bInterface) {
                  sClassName = getType(sCmd, "interface");
               }
               if (sClassName == null) {
                  sClassName = psClassName;
               }
               if ((piDeep < piMaxDeep) && (sMethod.indexOf("=") < 0)) {
                  modifyComment(sb, iLastPos, sMethod, sLastComment, sClassName, sParams);
               }

               if (i == '{') {
                  int iMaxDeep = piMaxDeep;
                  if (!sClassName.equals(psClassName)) {
                     iMaxDeep++;
                  }
                  String sNewSub =
                     readBlock(psClassName, piDeep + 1, reader, "}", 0, iMaxDeep, bInterface);
                  sb.append(sNewSub);
               }
               iLastPos = sb.length();
               sLastComment = "";
               sParams = "";
               sMethod = "";

            } else if ((i == '/') && (iLast == '/')) {
               sb.append(readBlock(psClassName, piDeep, reader, "\n", 1, piMaxDeep, pbInterface));
               iLastPos = sb.length();
               i = ' ';
               sLastComment = "";
            } else if ((i == '*') && (iLast == '/')) {
               sLastComment =
                  readBlock(psClassName, piDeep, reader, "*/", 1, piMaxDeep, pbInterface);
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
    * This private method returns a type.
    * <p> TODO (brod)  ... add detailed information
    * 
    * <p> TODO rename sCmd to psCmd
    * @param sCmd TODO (brod) cmd String
    * @param psType TODO (brod) type String
    * @return TODO (brod) the type
    * 
    * @author brod
    */
   private String getType(String sCmd, String psType)
   {
      int iPos = sCmd.indexOf(psType + " ");
      if (iPos > 0) {
         char[] charArray = sCmd.substring(iPos + psType.length() + 1).toCharArray();
         StringBuilder sb = new StringBuilder();
         for (char element : charArray) {
            if (element > ' ') {
               sb.append(element);
            } else if (sb.length() > 0) {
               break;
            }
         }
         return sb.toString();
      }
      return null;
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
      int iLastPos = piLastPos;
      int iEndPos = iLastPos;
      String sCmd = psbText.substring(iLastPos);
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
      String sLastComment = psLastComment;
      String sTodo = "TODO (" + sAuthor + ")";
      if (sLastComment.length() == 0) {
         boolean bInterface = sCmd.indexOf("interface") >= 0;
         if (((sCmd.indexOf("class") >= 0) || bInterface) && (sMethodName.length() == 0)) {
            if (sCmd.contains(" " + psClassName)) {
               sLastComment =
                  "/**\n * This " + (bInterface ? "interface" : "class") + " implements "
                        + a(psClassName) + ".\n";
               String sExtends = getType(sCmd, "extends");
               if (sExtends != null) {
                  sLastComment +=
                     " * The " + psClassName + " extends the class " + sExtends + ".\n";
               }
               sLastComment +=
                  " * <p> " + sTodo + " ... add detailed information for " + psClassName + "\n"
                        + " * <p>\n" + " * Copyright &copy; "
                        + (new GregorianCalendar()).get(Calendar.YEAR) + ", i:FAO\n" + " *\n";
               if (sExtends != null) {
                  sLastComment += " * @see " + sExtends + "\n *\n";
               }
               sLastComment += " * @author " + sAuthor + "\n */";
            } else {
               sLastComment =
                  "/**\n * This is an inner class of " + a(psClassName) + "\n * <p> " + sTodo
                        + " ... add detailed information for " + psClassName + "\n" + " * <p>\n"
                        + " * Copyright &copy; " + (new GregorianCalendar()).get(Calendar.YEAR)
                        + ", i:FAO\n" + " *\n" + " * @author " + sAuthor + "\n */";
            }

         } else if (sMethodName.equals(psClassName)) {
            sLastComment = "/**\n * This is the constructor for the class " + sMethodName;
            if (psParams.length() > 2) {
               sLastComment += ", with the following parameters:";
            } else {
               sLastComment += ".";
            }
            sLastComment +=
               "\n * <p> " + sTodo + " ... add detailed information for " + psClassName
                     + "\n *\n *\n * @author " + sAuthor + "\n */";
            bHasReturnValue = false;
         } else {
            String methodInfo = getMethodInfo(sMethodName, true, sRetType, psClassName, true, "");
            if (!methodInfo.contains(" ")) {
               methodInfo += " this " + psClassName;
            }
            sLastComment = "/**\n * " + methodInfo + ".\n";
            if (sCmd.contains(" abstract ")) {
               sLastComment +=
                  " * It is defined abstract, so each class (which extends a " + psClassName
                        + "\n * has to implement this method).\n";
            }

            sLastComment +=
               " * <p>\n * " + sTodo + "  ... add detailed information for method " + sMethodName
                     + "\n * \n * @author " + sAuthor + "\n */";
         }
      } else {
         iLastPos = psbText.lastIndexOf(sLastComment);
         iEndPos = iLastPos + sLastComment.length();
      }

      // go to first next character
      while ((iEndPos < psbText.length()) && (" \t\n\r".indexOf(psbText.charAt(iEndPos)) >= 0)) {
         iEndPos++;
      }

      // go to last first character
      while ((iLastPos > 1) && (" \t\n\r".indexOf(psbText.charAt(iLastPos - 1)) >= 0)) {
         iLastPos--;
      }

      // extract comment in lines
      StringTokenizer st = new StringTokenizer(sLastComment, "\n");

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
      sLastComment = "\n \n ";
      for (Object element : lstElements) {
         String sElement = (String) element;
         sLastComment += " " + sElement + "\n";
      }

      String sToDos = "";
      String sDocParams = "";

      // pay attention to params
      char[] paramsArray = psParams.toCharArray();
      String sParams = "";
      int iLevel = 0;
      for (int i = 0; i < paramsArray.length; i++) {
         if ((iLevel > 0) && (paramsArray[i] <= ' ')) {
            // ignore
         } else {
            if (paramsArray[i] == '<') {
               iLevel++;
            } else if (paramsArray[i] == '>') {
               iLevel--;
            } else if ((paramsArray[i] == ',') && (iLevel > 0)) {
               paramsArray[i] = ';';
            }
            sParams += paramsArray[i];
         }
      }

      // add parameters
      st = new StringTokenizer(sParams.replaceAll("\t", " "), ",()");
      while (st.hasMoreTokens()) {
         String sLine2 = st.nextToken().trim();
         if (sLine2.indexOf(" ") > 0) {
            String sParamType = sLine2.substring(0, sLine2.indexOf(" "));
            String sParamName = sLine2.substring(sLine2.lastIndexOf(" ") + 1);
            String sParam = "param " + sParamName;

            String sAlterName;
            if ((sParamName.length() > 0) && (sParamType.length() > 0)) {
               sAlterName = getAlterName(sParamType, sParamName);
               if (!sAlterName.equals(sParamName)) {
                  if (sToDos.length() > 0) {
                     sToDos += ", ";
                  }
                  sToDos += sParamName + " to " + sAlterName;
               }
               // reset the value
               if (sAlterName.equals("another name")) {
                  sAlterName = sParamName;
               }
            } else {
               sAlterName = sParamName;
            }

            StringBuffer sOldValue = htTokens.get(sParam);
            if ((sOldValue != null) && !sOldValue.toString().contains(sTodo)) {
               sDocParams += " " + sOldValue.toString();
            } else {
               String sAddType = "";
               String sType = sParamType;
               if (sType.contains("<")) {
                  char[] charArray = sType.toCharArray();
                  sType = " ";
                  int iCharArrayLevel = 0;
                  for (int j = 0; j < charArray.length; j++) {
                     char element = charArray[j];
                     if (element == '<') {
                        iCharArrayLevel++;
                        charArray[j] = ' ';
                        sType += " #";
                     } else if ((iCharArrayLevel > 0) && ((element == ',') || (element == ';'))) {
                        charArray[j] = ' ';
                        sType += " *";
                     } else if (element == '>') {
                        iCharArrayLevel--;
                        charArray[j] = ' ';
                     } else if ((iCharArrayLevel > 0) && (element >= 'A') && (element <= 'Z')) {
                        // make lower
                        charArray[j] += 32;
                     }
                     sType += charArray[j];
                  }
               }
               StringTokenizer lowerCase = getLowerCase(sType);
               sType = "";
               boolean bNextMultiple = false;
               while (lowerCase.hasMoreTokens()) {
                  String nextToken = lowerCase.nextToken();
                  if (nextToken.equals("?")) {
                     nextToken = "any type";
                  }
                  if (bNextMultiple && !nextToken.endsWith("s")) {
                     nextToken += "s";
                  }
                  bNextMultiple = false;
                  if (nextToken.equals("#")) {
                     nextToken = "of";
                     bNextMultiple = true;
                  }

                  if (nextToken.equals("*")) {
                     nextToken = "and";
                     bNextMultiple = true;
                  }
                  sType += " " + nextToken;
               }
               if (!sParamName.toUpperCase().contains(sParamType.toUpperCase())) {
                  if (!sParamType.contains("[]")) {
                     sAddType = sType;
                  }
               }
               sDocParams +=
                  " * @"
                        + sParam
                        + " "
                        + sTodo
                        + " "
                        + getMethodInfo(sAlterName, false, sType.trim(), psClassName, false,
                              sAddType) + "\n";
            }
         }
      }

      // add return value
      if ((sRetType.length() > 0) && !sRetType.equals("void") && !sMethodName.equals(psClassName)) {
         if (htTokens.get("return") != null) {
            sDocParams += htTokens.get("return");
         } else if (bHasReturnValue) {

            sDocParams +=
               " * @return " + sTodo + " "
                     + getMethodInfo(sMethodName, false, sRetType, psClassName, false, "").trim()
                     + "\n";
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
         sLastComment += " * <p> TODO rename " + sToDos + "\n";
      }
      sLastComment += sDocParams;

      // add additional elements
      for (Object element : lstTokens) {
         String sElement = (String) element;
         if (sElement.length() > 0) {
            sLastComment += " " + htTokens.get(sElement);
         }
      }
      // add additional elements

      sLastComment += " */\n";

      psbText.replace(iLastPos, iEndPos, sLastComment);

   }

   /**
    * This private method aes this CodeFormatterApplicationV2.
    * <p> TODO (brod)  ... add detailed information
    * 
    * @param psClassName TODO (brod) class name String
    * @return TODO (brod) a object
    * 
    * @author brod
    */
   private String a(String psClassName)
   {
      if (psClassName.length() > 0) {
         if (psClassName.endsWith("s")) {
            return "the " + psClassName;
         }
         return "a" + ("aeiou".contains(psClassName.toLowerCase().substring(0, 1)) ? "n " : " ")
               + psClassName;
      }
      return "";

   }

   /**
    * This private method returns a alter name.
    * <p> TODO (brod)  ... add detailed information
    * 
    * <p> TODO rename sParamType to psParamType, sParamName to psParamName
    * @param sParamType TODO (brod) param type String
    * @param sParamName TODO (brod) param name String
    * @return TODO (brod) the alter name
    * 
    * @author brod
    */
   private String getAlterName(String sParamType, String sParamName)
   {
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
      if (sParamType.contains("[]")) {
         sPre += "arr";
      }
      // sParamName2 has to start uppercase

      String sParamName2 = sParamName;
      if (sParamName.length() > 2) {

         if (!sParamName.startsWith(sPre)) {

            while ((sParamName2.length() > 0) && (sParamName2.charAt(0) >= 'a')
                  && (sParamName2.charAt(0) <= 'z')) {
               sParamName2 = sParamName2.substring(1);
            }
            if (sParamName2.length() == 0) {
               sParamName2 = sParamName;
            }

            sParamName2 = sParamName2.substring(0, 1).toUpperCase() + sParamName2.substring(1);

            sParamName2 = sPre + sParamName2;
         }
      } else {
         sParamName2 = "another name";
      }
      return sParamName2;
   }

   /**
    * This private method returns a method info.
    * <p> TODO (brod)  ... add detailed information
    * 
    * @param psMethodName TODO (brod) method name String
    * @param pbCorrectGet TODO (brod) If this is true, correct get
    * @param psRetType TODO (brod) ret type String
    * @param psClassName TODO (brod) class name String
    * @param pbMethodHeader TODO (brod) If this is true, method header
    * @return TODO (brod) the method info
    * 
    * @author brod
    * @param sAddType
    */
   private String getMethodInfo(String psMethodName, boolean pbCorrectGet, String psRetType,
                                String psClassName, boolean pbMethodHeader, String sAddType)
   {
      List<StringBuilder> lst = new ArrayList<StringBuilder>();

      StringTokenizer st = getLowerCase(psMethodName);
      while (st.hasMoreTokens()) {
         lst.add(new StringBuilder(st.nextToken()));
      }
      if (psRetType.contains("[]") && (lst.size() > 1)) {
         st = getLowerCase(psRetType.substring(0, psRetType.indexOf("[")));
         StringBuilder sb = new StringBuilder();
         lst.add(1, sb);
         while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(" ");
         }
         sb.append("array of");
         StringBuilder sbLast = lst.get(lst.size() - 1);
         if (!sbLast.toString().endsWith("s")) {
            sbLast.append("s");
         }
      }
      if (lst.size() == 0) {
         // not defined
      } else {
         StringBuilder sb0 = lst.get(0);
         String s0 = sb0.toString();
         if (pbCorrectGet) {
            if (s0.equals("get")) {
               s0 = "returns";
               lst.set(0, new StringBuilder(s0));
            } else if (s0.equals("to")) {
               lst.set(0, new StringBuilder("converts this object to"));
            }

            if (lst.size() > 1) {
               StringBuilder sb1 = lst.get(1);
               String s1 = sb1.toString() + " ";
               if (psMethodName.endsWith("s")) {
                  sb1.insert(0, "the ");
               } else if ("aeiou".contains(s1.substring(0, 1).toLowerCase())) {
                  sb1.insert(0, "an ");
               } else {
                  sb1.insert(0, "a ");
               }
            }
            if (s0.endsWith("i") || s0.endsWith("a") || s0.endsWith("o") || s0.endsWith("u")) {
               sb0.append("es");
            } else if (s0.endsWith("y")) {
               sb0.setLength(0);
               sb0.append(s0.substring(0, s0.length() - 1) + "ies");
            } else if (!s0.endsWith("s")) {
               sb0.append("s");
            }
         } else {
            if (s0.equals("get")) {
               sb0.setLength(0);
               sb0.append("the");
            }
            if (s0.length() == 1) {
               lst.add(new StringBuilder("object"));
            }
            if (((s0.length() <= 2) && s0.startsWith("p")) || s0.equals("parr")
                  || s0.equals("plst")) {
               sb0.setLength(0);
            }

         }
      }
      if (psRetType.trim().equalsIgnoreCase("boolean")) {
         lst.add(0, new StringBuilder("!" + psClassName));
         if (pbMethodHeader) {
            lst.add(0, new StringBuilder("Returns if the"));
         } else {
            lst.add(0, new StringBuilder("true, if the"));
         }
      }

      StringBuilder sb = new StringBuilder();

      for (StringBuilder stringBuilder : lst) {
         st = new StringTokenizer(stringBuilder.toString().trim(), " ");
         while (st.hasMoreTokens()) {
            if (sb.length() > 0) {
               sb.append(" ");
            }
            String sText = st.nextToken();
            if (sText.startsWith("!")) {
               sText = sText.substring(1);
            } else {
               sText = sText.toLowerCase();
            }
            if (sText.equals("conf")) {
               sb.append("configuration");
            } else if (sText.equals("int")) {
               sb.append("integer");
            } else if (sText.equals("param")) {
               sb.append("parameter");
            } else if (sText.equals("params")) {
               sb.append("parameters");
            } else if (sText.equals("dir")) {
               sb.append("directory");
            } else if (sText.equals("4")) {
               sb.append("for");
            } else {
               sb.append(sText);
            }
         }
      }
      if ((sAddType != null) && (sAddType.length() > 0)) {
         sb.append(sAddType);
      }
      String sText = sb.toString();
      // post correction
      if (sText.contains("object Hashtable of ")) {
         // OLD: parameters object Hashtable of strings and strings
         sText = "table with " + sText.replaceFirst("object Hashtable of ", "which map ");
         // NEW: Hashtable of parameters which map strings and strings
      } else if (sText.contains("object List of ")) {
         // OLD: parameters object Hashtable of strings and strings
         sText = "list of " + sText.replaceFirst("object Hashtable of ", "which contain ");
         // NEW: Hashtable of parameters which map strings and strings
      } else if (sText.contains("object ArrayList of ")) {
         // OLD: parameters object Hashtable of strings and strings
         sText = "list of " + sText.replaceFirst("object Hashtable of ", "which contain ");
         // NEW: Hashtable of parameters which map strings and strings
      }
      return sText;
   }

   /**
    * This private method returns a lower case.
    * <p> TODO (brod)  ... add detailed information
    * 
    * <p> TODO rename sMethodName to psMethodName
    * @param sMethodName TODO (brod) method name String
    * @return TODO (brod) the lower case
    * 
    * @author brod
    */
   private StringTokenizer getLowerCase(String sMethodName)
   {
      StringBuilder sb = new StringBuilder();
      char[] charArray = sMethodName.toCharArray();
      boolean bLastCharWasLowercase = false;
      for (char element : charArray) {
         boolean bLow = (element >= 'a') && (element <= 'z');
         if (bLastCharWasLowercase && !bLow) {
            // add a new entry
            sb.append(" ");
         }
         sb.append(element);
         bLastCharWasLowercase = bLow;
      }
      return new StringTokenizer(sb.toString(), " ");
   }

   /**
    * The main Method
    * <p> TODO rename psArgs to parrArgs
    * @param psArgs the arguments
    * 
    */
   public static void main(String[] psArgs)
   {
      psArgs =
         new String[]{ "c:\\ifao\\Workspace",
               "/arcticTools/src/net/ifao/common/tomcat/ColorSchema.java" };
      if (psArgs.length != 2) {
         System.out.println("Syntax " + CodeFormatterApplicationV2.class.getName()
               + " <RootDir> <FileName>");
         System.out.println("  whereas FileName is relative to RootDir.");
         System.out.println("  e.g. " + CodeFormatterApplicationV2.class.getName()
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
