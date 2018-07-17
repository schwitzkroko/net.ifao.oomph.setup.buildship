package net.ifao.tools.schemaconversion.localtoglobalelements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;
import static net.ifao.tools.schemaconversion.localtoglobalelements.Util.stringToInt;


/**
 * <p>This class contains the local element schema parser.
 * <p>&copy; 2010 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public class LocalElementSchemaParser
{

   // Patterns
   private static final String PAT_ELEMENT_START_ROOT = "^\\s*<xs:element name=\"(\\w+)\">";
   private static final String PAT_ELEMENT_START =
      "^\\s*<xs:element name=\"(\\w+)\" minOccurs=\"([0-9]+)\" maxOccurs=\"([0-9]+)\">";
   private static final String PAT_ELEMENT_END = "^\\s*</xs:element>";
   private static final String PAT_ELEMENT_COMPLETE =
      "^\\s*<xs:element name=\"(\\w+)\" minOccurs=\"([0-9]+)\" maxOccurs=\"([0-9]+)\" type=\"(xs:\\w+)\" />";
   private static final String PAT_ELEMENT_UNKNWON = "xs:element";
   private static final String PAT_SEQUENCE_START = "^\\s*<xs:sequence>";
   private static final String PAT_SEQUENCE_END = "^\\s*</xs:sequence>";


   // Pattern objects
   private Pattern _patElementStartRoot;
   private Pattern _patElementStart;
   private Pattern _patElementEnd;
   private Pattern _patElementComplete;
   private Pattern _patElementUnknown;
   private Pattern _patSequenceStart;
   private Pattern _patSequenceEnd;


   //------------------ Methods ---------------------------------------------------
   //------------------------------------------------------------------------------

   /**
    * <p>Constructor.
    */
   public LocalElementSchemaParser()
   {
      _patElementStartRoot = compile(PAT_ELEMENT_START_ROOT);
      _patElementStart = compile(PAT_ELEMENT_START);
      _patElementEnd = compile(PAT_ELEMENT_END);
      _patElementComplete = compile(PAT_ELEMENT_COMPLETE);
      _patElementUnknown = compile(PAT_ELEMENT_UNKNWON);
      _patSequenceStart = compile(PAT_SEQUENCE_START);
      _patSequenceEnd = compile(PAT_SEQUENCE_END);
   }

   //------------------------------------------------------------------------------

   /**
    * <p>This method parses the local element schema.
    * @param plstLineLocalElement local element schema
    * @return root element of the local element schema
    * @author Jochen Pinder
    * @throws IOException
    */
   public TElement parse(ArrayList<String> plstLineLocalElement)
      throws IOException
   {

      // Parse the root element with all sub-elements
      TElement tRootElement = parseNextElement(plstLineLocalElement.iterator());

      return tRootElement;

   } // end parse

   //------------------------------------------------------------------------------

   /**
    * <p>This method parses the next schema element.
    * @param pitLineLocalElement local element schema iterator
    * @return next schema element
    * @author Jochen Pinder
    * @throws IOException
    */
   public TElement parseNextElement(Iterator<String> pitLineLocalElement)
      throws IOException
   {

      Matcher matcher;
      TElement tElement = null;

      // Loop over the next lines of the local element schema until the next element has been read.
      while (pitLineLocalElement.hasNext()) {

         // Get the next line
         String sLine = pitLineLocalElement.next();

         // Check for the start of the complex type root schema element
         matcher = _patElementStartRoot.matcher(sLine);
         if (matcher.find() && matcher.groupCount() == 1) {
            tElement = new TElement(matcher.group(1));

            continue;
         }

         // Check for the start of the next complex type non-root schema element
         matcher = _patElementStart.matcher(sLine);
         if (matcher.find() && matcher.groupCount() == 3) {
            tElement =
               new TElement(matcher.group(1), stringToInt(matcher.group(2)),
                     stringToInt(matcher.group(3)));

            continue;
         }

         // Check for the end of the next complex type schema element
         matcher = _patElementEnd.matcher(sLine);
         if (matcher.find()) {
            return tElement;
         }

         // Check for a complete next simple type schema element
         matcher = _patElementComplete.matcher(sLine);
         if (matcher.find() && matcher.groupCount() == 4) {
            tElement =
               new TElement(matcher.group(1), stringToInt(matcher.group(2)),
                     stringToInt(matcher.group(3)), matcher.group(4));

            return tElement;
         }

         // Check for an unknown next schema element
         matcher = _patElementUnknown.matcher(sLine);
         if (matcher.find()) {
            System.out.println("ERROR: Could not interpret line '" + sLine + "'");
            throw new IOException("ERROR: Could not interpret line '" + sLine + "'");
         }

         // Check for the start of a sequence
         matcher = _patSequenceStart.matcher(sLine);
         if (matcher.find()) {
            while (true) {
               TElement tChildElement = parseNextElement(pitLineLocalElement);

               if (tChildElement != null) {
                  tElement.addChildElement(tChildElement);
                  tChildElement.setParentElement(tElement);

                  continue;
               }

               break;
            }
         }

         // Check for the end of a sequence
         matcher = _patSequenceEnd.matcher(sLine);
         if (matcher.find()) {
            return null;
         }
      }

      // From here: No schema element has been found.
      return null;

   } // end parse
}
