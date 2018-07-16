package net.ifao.tools.ancillaries;


import ifaoplugin.*;
import net.ifao.util.*;

import org.eclipse.core.commands.*;


/**
 * Starts the Doublets Test (class net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.TestRunner)
 * in (a selectable) arctic project and displays the result in a dialog.
 *
 * <p>
 * Copyright &copy; 2014, i:FAO Group GmbH
 * @author kaufmann
 */
public class DoubletsTestHandler
   extends AbstractHandler
{
   private static final String DOUBLETS_TEST_RUNNER =
      "net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.TestRunner";

   /**
    * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    *
    * @author kaufmann
    */
   @Override
   public Object execute(ExecutionEvent pEvent)
      throws ExecutionException
   {
      String sClassesPath = Util.getProjectOutputPath();
      /////////////////////////////////////////////////////////
      // for development:                                    //
      // sClassesPath = "c:/java/arctic/mainline_1/classes"; //
      /////////////////////////////////////////////////////////

      if (sClassesPath == null) {
         Util.displayTextInDialog("ERROR", "\n No arctic project found/selected \n");
      } else {
         String sArcticPath = sClassesPath.substring(0, sClassesPath.lastIndexOf("/"));
         String sConfigPath = sClassesPath.substring(0, sClassesPath.lastIndexOf("/")) + "/conf";
         try {
            String sExecutionResult =
               Execute
                     .startAndGetWholeOutput(DOUBLETS_TEST_RUNNER, sArcticPath, sConfigPath, false);
            String sResult =
               sExecutionResult.substring(sExecutionResult
                     .indexOf("***********************************"));
            Util.displayTextInDialog("Doublets Test Result", sResult);
         }
         catch (Exception ex) {
            Util.showException(ex);
         }
      }


      // the following code does not work, because the oracle driver behaves strange with the 
      // own class loader....(error during creation of the connection)
      //
      //
      //         ClassLoader loader = new Util.MyClassLoader(getClass().getClassLoader(), sClassesPath);
      //
      //         try {
      //            // get helper for private methods
      //            Class<?> accessUtilClass = loader.loadClass("util.Access");
      //
      //            Method executeMethod =
      //               accessUtilClass.getMethod("executeMethod", new Class[]{ Object.class, String.class,
      //                     Object[].class });
      //
      //            // set arctic configuration
      //            Util.setArcticConfiguration(sConfigPath, loader);
      //            
      //            //      // Creates a InputConfigurationGenerator which generates all possible Input-Facts for this test.
      //            //      InputConfigurationGenerator inputConfigurationGenerator = InputConfigurationGeneratorGermanwings.createNew();
      //            Class<?> inputConfigurationGeneratorInterface =
      //               loader.loadClass("net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.doublets.InputConfigurationGenerator");
      //            Class<?> inputConfigurationGeneratorClass =
      //               loader.loadClass("net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.doublets.InputConfigurationGeneratorGermanwings");
      //            Object inputConfigurationGenerator =
      //               inputConfigurationGeneratorClass.getMethod("createNew", new Class[]{}).invoke(null,
      //                     new Object[]{});
      //
      //            Object testRunner =
      //               loader.loadClass(
      //                     "net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.TestRunner")
      //                     .newInstance();
      //            Object context =
      //               executeMethod.invoke(null, testRunner, "getTestRunnerContext",
      //                     new Object[]{ sConfigPath });
      //            Class<?> contextInterface =
      //               loader.loadClass("net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.doublets.DoubletsTest$Context");
      //
      //
      //            Class<?> doubletsTestBuilderClass =
      //               loader.loadClass("net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.doublets.DoubletsTest$Builder");
      //            Object doubletsTestBuilder =
      //               doubletsTestBuilderClass.getConstructor(contextInterface,
      //                     inputConfigurationGeneratorInterface).newInstance(context,
      //                     inputConfigurationGenerator);
      //            
      //            Class<?> inputFactSetValidatorBundleInterface = loader.loadClass("net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.doublets.InputFactSetValidatorBundle");
      //            Class<?> commonInputFactSetValidatorBundleClass = loader.loadClass("net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.doublets.CommonInputFactSetValidatorBundle");
      //            Object commonInputFactSetValidatorBundle = commonInputFactSetValidatorBundleClass.getMethod("createNew",  new Class[]{}).invoke(null, new Object[]{});
      //            doubletsTestBuilderClass.getMethod("setValidatorBundle", inputFactSetValidatorBundleInterface).invoke(doubletsTestBuilder, commonInputFactSetValidatorBundle);
      //
      //            Class<?> commonAncillariesFilterBundleInterface = loader.loadClass("net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.doublets.AncillaryTestFilterBundle");
      //            Class<?> commonAncillariesFilterBundleClass = loader.loadClass("net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.doublets.CommonAncillariesFilterBundle");
      //            Object commonAncillariesFilterBundle = commonAncillariesFilterBundleClass.getMethod("createNew",  new Class[]{}).invoke(null, new Object[]{});
      //            doubletsTestBuilderClass.getMethod("setFilterBundle", commonAncillariesFilterBundleInterface).invoke(doubletsTestBuilder, commonAncillariesFilterBundle);
      //
      //            Object doubletsTest = doubletsTestBuilderClass.getMethod("build", new Class<?>[] {}).invoke(doubletsTestBuilder, new Object[] {});
      //
      //            Object doublettesTestReport = doubletsTest.getClass().getMethod("runTest", new Class<?>[] {}).invoke(doubletsTest, new Object[] {});
      //            
      //            Object doubletsAsJSONObjectsFormatter = loader.loadClass("net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.doublets.DoubletsTestReportToStringFormatter$DoubletsAsJSONObjectsFormatter").newInstance();
      //            Class<?> doubletsTestReportToStringFormatterInterface = loader.loadClass("net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.doublets.DoubletsTestReportToStringFormatter");
      //            doublettesTestReport.getClass().getMethod("setToStringFormatter", doubletsTestReportToStringFormatterInterface).invoke(doublettesTestReport, doubletsAsJSONObjectsFormatter);
      //            
      //            Util.displayTextInDialog("Doublets Test", doublettesTestReport.toString());
      //
      //
      //            //      // Create and run the test.
      //            //      DoubletsTest doubletsTest = new DoubletsTest.Builder(new Context() {
      //            //         
      //            //         @Override
      //            //         public void needDatabaseConnection() throws IOException, ClassNotFoundException {
      //            //             DBConnectionPool.initDBConnection(new File("c:/java/arctic/mainline_1/conf"));
      //            //         }
      //            //         
      //            //         @Override
      //            //         public void finishedDatabaseOperations() {
      //            //               DBConnectionPool.stopThreads();
      //            //         }
      //            //         
      //            //      }, inputConfigurationGenerator)
      //            //      .setValidatorBundle(CommonInputFactSetValidatorBundle.createNew())
      //            //      .setFilterBundle(CommonAncillariesFilterBundle.createNew())
      //            //      .build();
      //            //      DoublettesTestReport testResult = doubletsTest.runTest();
      //            //      
      //            //      // Formatting and output of the test report.
      //            //      testResult.setToStringFormatter(new DoubletsTestReportToStringFormatter.DoubletsAsJSONObjectsFormatter());
      //            //      System.out.println(testResult.toString());
      //            //
      //
      //
      //         }
      //         catch (Exception e) {
      //            Util.displayExceptionInDialog("ERROR", e, "");
      //         }
      //      }
      return null;
   }

}
