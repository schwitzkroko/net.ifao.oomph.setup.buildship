package net.ifao.tools.ancillaries;


import ifaoplugin.*;
import net.ifao.util.*;

import org.eclipse.core.commands.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


/**
 * Starts the DependencySummarizeRunner in (a selectable) arctic project and displays the result 
 * as tree in a dialog.
 *
 * <p>
 * Copyright &copy; 2014, i:FAO Group GmbH
 * @author kaufmann
 */
public class DependencySummarizerHandler
   extends AbstractHandler
{

   private static final String DEPENDENCY_SUMMARIZE_RUNNER =
      "net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.DependencySummarizeRunner";

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
         Util.displayTextInDialog(sClassesPath, "\n No arctic project found/selected \n");
      } else {
         String sArcticPath = sClassesPath.substring(0, sClassesPath.lastIndexOf("/"));
         String sConfigPath = sClassesPath.substring(0, sClassesPath.lastIndexOf("/")) + "/conf";
         try {
            String sExecutionResult =
               Execute.startAndGetWholeOutput(DEPENDENCY_SUMMARIZE_RUNNER, sArcticPath,
                     sConfigPath, false);
            final String sResult =
               sExecutionResult.indexOf("{Dependencies:") >= 0 ? sExecutionResult
                     .substring(sExecutionResult.indexOf("{Dependencies:")) : sExecutionResult;

            Util.displayDialog("Dependency Summarizer Result", new Util.IDialogContentBuilder()
            {
               @Override
               public void createContent(Composite pContainer)
               {
                  pContainer.setLayout(new GridLayout(1, false));

                  Text text = new Text(pContainer, SWT.BORDER | SWT.WRAP);
                  GridData gd_text = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
                  text.setLayoutData(gd_text);
                  text.setText("The following output has been created by the DependencySummarizeRunner:\n"
                        + sResult);

                  JsonToTree.buildTree(pContainer, "Dependency Summarizer Result", sResult);
               }
            });
         }
         catch (Exception ex) {
            Util.showException(ex);
         }

         // the following code does not work, because the oracle driver behaves strange with the 
         // own class loader....(error during creation of the connection)
         //
         //
         //         ClassLoader loader = new Util.MyClassLoader(getClass().getClassLoader(), sClassesPath);
         //         String sConfigPath = sClassesPath.substring(0, sClassesPath.lastIndexOf('/')) + "/conf";
         //
         //         try {
         //            Class<?> dependencySummarizeRunnerClass =
         //               loader.loadClass("net.ifao.arctic.agents.newcib.wsdl.ancillary.dependency.DependencySummarizeRunner");
         //
         //            Method runSummarizerMethod =
         //               dependencySummarizeRunnerClass.getMethod("runSummarizer", new Class[]{ String.class });
         //
         //            String sResult = (String) runSummarizerMethod.invoke(null, sConfigPath);
         //            Util.displayTextInDialog("Summarizer Result", sResult);
         //         }
         //         catch (Exception pException) {
         //            Util.displayExceptionInDialog("Error running the summarizer", pException, "");
         //         }
      }

      return null;
   }

}
