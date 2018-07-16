package net.ifao.tools.schemaconversion.localtoglobalelements;


import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import static net.ifao.tools.schemaconversion.localtoglobalelements.Util.readFile;
import static net.ifao.tools.schemaconversion.localtoglobalelements.Util.writeFile;
import static net.ifao.tools.schemaconversion.localtoglobalelements.Util.traverseElementTree;


/**
 * <p>This class converts a schema with local elements to a schema with global elements.
 * <p>Three output files are generated.
 * <ol>
 *   <li>A schema with local elements, which should be compared to the original schema with local elements
 *       in order to test the parsing of the original schema (both files should be equal)
 *   <li>A schema with global elements
 *   <li>A binding file, which refers to the schema with global elements and which is needed by Castor
 * </ol>
 * <p>Do not forget to change the file name 2) to data.xsd and the file name 3) to dataBinding.xsd before
 * starting the library generation with Castor.
 * <p>&copy; 2010 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public class Start
{

   // version and date
   private static final String VERSION = "1.1";
   private static final String DATE = "15-JUN-2010";

   // file names
   private static final String FILE_INPUT_LOCAL_ELEMENT = "data.xsd";
   private static final String FILE_OUTPUT_LOCAL_ELEMENT = "local_element_data.xsd";
   private static final String FILE_OUTPUT_GLOBAL_ELEMENT = "global_element_data.xsd";
   private static final String FILE_OUTPUT_GLOBAL_CASTOR_BINDING = "global_element_dataBinding.xsd";


   //------------------ Methods ---------------------------------------------------
   //------------------------------------------------------------------------------

   /**
    * <p>This method runs the analyser
    * @param args command line arguments
    * @author Jochen Pinder
    * @throws IOException
    */
   public static void main(String[] args)
      throws IOException
   {

      PrintStream out = System.out;
      out.println();
      out.println("loc2glob " + VERSION + ' ' + DATE);

      // Get the working directory
      if (args.length != 1) {
         out.println("");
         out.println("Usage:");
         out.println("   loc2glob <working_directory>");
         System.exit(1);
      }
      if (!poweredfare(args[0], out)) {
         out.println("ERROR: No root element found");
         System.exit(1);
      }

   } // end main


   public static boolean poweredfare(String sWorkingDir, PrintStream out)
      throws IOException
   {

      ///
      // Start: Read the input file
      ///
      out.println();
      out.println("Reading the local element schema '" + FILE_INPUT_LOCAL_ELEMENT + "' ...");

      LocalElementSchemaParser localElementParser = new LocalElementSchemaParser();
      StatisticElementVisitor statisticElementVisitor = new StatisticElementVisitor();

      ArrayList<String> lstLineLocalElement =
         readFile(sWorkingDir + "\\" + FILE_INPUT_LOCAL_ELEMENT);
      TElement tRootElement = localElementParser.parse(lstLineLocalElement);

      if (tRootElement == null) {
         out.println("ERROR: No root element found");
         System.exit(1);
      }

      traverseElementTree(tRootElement, statisticElementVisitor);

      int bComplexElementLocalCount = statisticElementVisitor.getComplexElementCount();
      int bSimpleElementLocalCount = statisticElementVisitor.getSimpleElementCount();
      out.println(" -> root element: " + tRootElement.getName());
      out.println(" -> " + bComplexElementLocalCount + " complex elements read");
      out.println(" -> " + bSimpleElementLocalCount + " simple elements read");
      out.println(" -> " + (bComplexElementLocalCount + bSimpleElementLocalCount)
            + " total elements read");
      out.println(" -> done");

      ///
      // End: Read the input file
      ///


      ///
      // Start: Generate the local element schema
      ///
      out.println();
      out.println("Generating the local element schema '" + FILE_OUTPUT_LOCAL_ELEMENT + "' ...");

      LocalElementGenerationVisitor localElementGenerationVisitor =
         new LocalElementGenerationVisitor();

      localElementGenerationVisitor.commence();
      traverseElementTree(tRootElement, localElementGenerationVisitor);
      localElementGenerationVisitor.terminate();

      writeFile(localElementGenerationVisitor.getGeneratedData(), sWorkingDir + "\\"
            + FILE_OUTPUT_LOCAL_ELEMENT, false);

      out.println(" -> done");

      ///
      // End: Generate the local element schema
      ///


      ///
      // Start: Check for double elements
      ///
      out.println();
      out.println("Check for double elements ...");

      DoubleElementCheckVisitor doubleElementCheckVisitor = new DoubleElementCheckVisitor();

      traverseElementTree(tRootElement, doubleElementCheckVisitor);
      doubleElementCheckVisitor.terminate();

      DoubleElementMarkVisitor doubleElementMarkVisitor =
         new DoubleElementMarkVisitor(doubleElementCheckVisitor.getDifferentElementNameSet());

      traverseElementTree(tRootElement, doubleElementMarkVisitor);

      out.println(" -> " + doubleElementCheckVisitor.getIdenticalElementNameSet().size()
            + " double identical elements found with all of them having the same definition");
      out.println(" -> "
            + doubleElementCheckVisitor.getDifferentElementNameSet().size()
            + " double differnet elements found with some or all of them having different definitions");
      Iterator<String> it = doubleElementCheckVisitor.getDifferentElementNameSet().iterator();
      while (it.hasNext()) {
         out.println("     + " + it.next());
      }
      out.println(" -> done");

      ///
      // End: Check for double elements
      ///


      ///
      // Start: Generate the global element schema
      ///
      out.println();
      out.println("Generating the global element schema '" + FILE_OUTPUT_GLOBAL_ELEMENT + "' ...");

      GlobalElementGenerationVisitor globalElementGenerationVisitor =
         new GlobalElementGenerationVisitor();

      globalElementGenerationVisitor.commence();
      traverseElementTree(tRootElement, globalElementGenerationVisitor);
      globalElementGenerationVisitor.terminate();

      writeFile(globalElementGenerationVisitor.getGeneratedData(), sWorkingDir + "\\"
            + FILE_OUTPUT_GLOBAL_ELEMENT, false);

      int bComplexElementGlobalCount = globalElementGenerationVisitor.getComplexElementCount();
      int bSimpleElementGlobalCount = globalElementGenerationVisitor.getSimpleElementCount();
      out.println(" -> " + bComplexElementGlobalCount + " complex global elements generated");
      out.println(" -> " + bSimpleElementGlobalCount + " simple global elements generated");
      out.println(" -> " + (bComplexElementGlobalCount + bSimpleElementGlobalCount)
            + " total global elements generated");
      out.println(" -> done");

      ///
      // End: Generate the global element schema
      ///


      ///
      // Start: Generate the global element castor binding
      ///
      out.println();
      out.println("Generating the global element castor binding '"
            + FILE_OUTPUT_GLOBAL_CASTOR_BINDING + "' ...");

      GlobalElementCastorBindingGenerationVisitor globalElementCastorBindingGenerationVisitor =
         new GlobalElementCastorBindingGenerationVisitor();

      globalElementCastorBindingGenerationVisitor.commence();
      traverseElementTree(tRootElement, globalElementCastorBindingGenerationVisitor);
      globalElementCastorBindingGenerationVisitor.terminate();

      writeFile(globalElementCastorBindingGenerationVisitor.getGeneratedData(), sWorkingDir + "\\"
            + FILE_OUTPUT_GLOBAL_CASTOR_BINDING, false);

      int bComplexGlobalElementCount =
         globalElementCastorBindingGenerationVisitor.getComplexGlobalElementBindingCount();
      int bComplexLocalElementCount =
         globalElementCastorBindingGenerationVisitor.getComplexLocalElementBindingCount();
      out.println(" -> " + bComplexGlobalElementCount
            + " complex global element bindings generated");
      out.println(" -> " + bComplexLocalElementCount + " complex local element bindings generated");
      out.println(" -> " + (bComplexGlobalElementCount + bComplexLocalElementCount)
            + " total complex element bindings generated");
      out.println(" -> done");

      ///
      // End: Generate the global element castor binding
      ///
      return true;
   } // end main

}
