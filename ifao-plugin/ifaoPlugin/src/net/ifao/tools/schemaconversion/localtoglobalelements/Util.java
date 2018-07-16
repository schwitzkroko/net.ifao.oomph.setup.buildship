package net.ifao.tools.schemaconversion.localtoglobalelements;


import static java.lang.Character.isUpperCase;
import static java.lang.Character.toUpperCase;

import java.util.ArrayList;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.IOException;


/**
 * <p>This class contains utility methods.
 * <p>&copy; 2010 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public class Util
{

   //------------------ Methods ----------------------------------------------------
   // ------------------------------------------------------------------------------

   /** 
    * <p>This method reads a file into a list of strings.
    * <p>One string is created for every line. 
    * @param psFileName file name 
    * @return list of strings
    * @author Jochen Pinder 
    * @throws IOException 
    */
   public static ArrayList<String> readFile(String psFileName)
      throws IOException
   {

      ArrayList<String> lstLine = new ArrayList<String>();
      String sLine;

      LineNumberReader f = new LineNumberReader(new FileReader(psFileName));

      while ((sLine = f.readLine()) != null) {
         lstLine.add(sLine);
      }

      f.close();

      return lstLine;

   } // end readFile

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method writes a list of strings into a file.
    * <p>Every string is written into one line. 
    * @param plstLine list of strings 
    * @param psFileName file name 
    * @param pbAppend if true and the file already exists, the contents of the string is appended to 
    *        the file. Otherwise the file is newly created  
    * @author Jochen Pinder 
    * @throws IOException 
    */
   public static void writeFile(ArrayList<String> plstLine, String psFileName, boolean pbAppend)
      throws IOException
   {

      FileWriter f = new FileWriter(psFileName, pbAppend);

      for (String sString : plstLine) {
         f.write(sString);
         f.write("\r\n");
      }

      f.close();
   }

   //------------------------------------------------------------------------------

   /**
    * <p>This method traverses the schema element.
    * @author Jochen Pinder
    * @param ptElement schema element
    * @param pvisitor visitor
    */
   public static void traverseElementTree(TElement ptElement, IElementVisitor pvisitor)
   {

      // Initialise the visit
      pvisitor.init(ptElement);

      // Perform the visit before visiting the child schema elements
      pvisitor.visitBeforeChildren(ptElement);

      // Loop over all schema child elements
      if (pvisitor.visitChildren(ptElement)) {
         for (TElement tChildElement : ptElement.getChildElementList()) {

            // Traverse the next schema sub-element
            traverseElementTree(tChildElement, pvisitor);
         }
      }

      // Perform the visit after visiting the child schema elements
      pvisitor.visitAfterChildren(ptElement);

      // Finish the visit
      pvisitor.finish(ptElement);

   } // end traverseElementTree

   // ------------------------------------------------------------------------------

   /**
    * <p>This method converts a string to an integer by using the method
    * <code>Integer.parseInt(String s)</code>. If the exception NumberFormatException is thrown,
    * the integer 0 is returned.
    * <p>For example:
    * <pre>
    *    sInput = &quot;38&quot;  =&gt;  38
    *    sInput = &quot;3A&quot;  =&gt;  0
    * </pre>
    * @param psInput string to be converted
    * @return created integer
    * @author Jochen Pinder
    */
   public static int stringToInt(String psInput)
   {
      try {
         return (Integer.parseInt(psInput));
      }
      catch (Exception ex) {
         return 0;
      }
   }

   //------------------------------------------------------------------------------

   /**
    * <p>This method converts the first character of the given string to upper case.
    * @param psString given string
    * @return given string with the first character as upper case
    * @author Jochen Pinder
    */
   public static String firstCharToUpperCase(String psString)
   {

      if (isUpperCase(psString.charAt(0)))
         return psString;

      return (toUpperCase(psString.charAt(0)) + psString.substring(1));
   }
}
