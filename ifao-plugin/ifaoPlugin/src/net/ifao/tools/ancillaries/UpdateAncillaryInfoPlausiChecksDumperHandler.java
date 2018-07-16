package net.ifao.tools.ancillaries;


import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import ifaoplugin.ArcticClassLoader;
import ifaoplugin.JsonToTree;
import ifaoplugin.Util;


/**
 * Dumps the plausi checks of UpdateAncillaryInfo to a Dialog
 *
 * <p>
 * Copyright &copy; 2014, i:FAO Group GmbH
 * @author kaufmann
 */
public class UpdateAncillaryInfoPlausiChecksDumperHandler
   extends AbstractHandler
{

   private static final String CHECKER = "net.ifao.arctic.agents.newcib.wsdl.ancillary.plausi.Checker";
   private static final String CHECK_SPACE = "net.ifao.arctic.agents.newcib.wsdl.ancillary.plausi.CheckSpace";
   private static final String ANCILLARY_PLAUSI_CHECK_ROOT_SPACE =
      "net.ifao.arctic.agents.newcib.wsdl.ancillary.plausi.AncillaryPlausiCheckRootSpace";


   /**
    * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    *
    * @author kaufmann
    */
   @Override
   public Object execute(ExecutionEvent pEvent)
      throws ExecutionException
   {
      try {
         String sClassesPath = Util.getProjectOutputPath();
         /////////////////////////////////////////////////////////
         // for development:                                    //
         // sClassesPath = "c:/java/arctic/mainline_1/classes"; //
         /////////////////////////////////////////////////////////

         if (sClassesPath == null) {
            Util.displayTextInDialog("ERROR", "\n No arctic project found/selected \n");
         } else {
            ClassLoader loader = new ArcticClassLoader(getClass().getClassLoader(), Arrays.asList(new File(sClassesPath)));

            Class<?> accessUtilClass = null;
            try {
               accessUtilClass = loader.loadClass("util.Access");
            }
            catch (ClassNotFoundException pException1) {
               pException1.printStackTrace();
            }

            Method getField = accessUtilClass.getMethod("getField", new Class[]{ Object.class, String.class });


            Class<?> ancillaryPlausiCheckRootSpaceClass = loader.loadClass(ANCILLARY_PLAUSI_CHECK_ROOT_SPACE);

            Constructor<?> ancillaryPlausiCheckRootSpaceConstructor =
               ancillaryPlausiCheckRootSpaceClass.getConstructor(int.class, int.class);

            /* AncillaryPlausiCheckRootSpace*/Object root = ancillaryPlausiCheckRootSpaceConstructor.newInstance(0, 0);

            StringBuilder sb = new StringBuilder();

            sb.append("{").append(analyseCheckSpace(root, loader, getField)).append("}");

            showResult(sb.toString(), loader);
         }
      }
      catch (Exception e) {
         Util.displayExceptionInDialog("ERROR", e, null);
      }
      return null;
   }

   /**
    * Shows the result of the checker as tree in a dialog
    *
    * @param psResultString result of the checker as JSON string
    *
    * @author kaufmann
    * @throws InstantiationException
    * @throws SecurityException
    * @throws NoSuchMethodException
    * @throws InvocationTargetException
    * @throws IllegalArgumentException
    * @throws IllegalAccessException
    * @throws ClassNotFoundException
    */
   private void showResult(final String psResultString, ClassLoader pLoader)
      throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException, InstantiationException
   {
      Util.displayDialog("UpdateAncillaryInfoPlausiChecksDumper", new Util.IDialogContentBuilder()
      {
         @Override
         public void createContent(Composite pContainer)
         {
            pContainer.setLayout(new GridLayout(1, false));

            Text text = new Text(pContainer, SWT.BORDER | SWT.WRAP);
            GridData gd_text = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
            //            gd_text.widthHint = 536;
            text.setLayoutData(gd_text);
            text.setText("This compilation shows all Ancillary plausi checks during CIB Ancillary import.\n" + "Generation Time: "
                  + new java.util.Date(System.currentTimeMillis()).toString() + "\n");

            JsonToTree.buildTree(pContainer, "UpdateAncillaryInfoPlausiChecks", psResultString);
         }
      });

      // this is the original output (without tree)
      //      Util.displayTextInDialog("UpdateAncillaryInfoPlausiChecksDumper",
      //            "This compilation shows all Ancillary plausi checks during CIB Ancillary import.\n"
      //                  + "Generation Time: " + new java.util.Date(System.currentTimeMillis()).toString()
      //                  + "\n\n" + Util.getFormattedJSonOutput("UpdateAncillaryInfoPlausiChecks: {"+psResultString+"}", pLoader) + "\n");

   }


   /**
    * Analyses the plausi checks
    *
    * @param space root of the plausi checks
    * @param pLoader class loader
    * @param pGetField
    * @return
    * @throws Exception
    *
    * @author kaufmann
    */
   private String analyseCheckSpace(/*AncillaryPlausiCheckSpace*/Object space, ClassLoader pLoader, Method pGetField)
      throws Exception
   {
      StringBuilder sb = new StringBuilder();
      sb.append(space.getClass().getSimpleName());
      String checkSpace_scope = getCheckSpaceAnnotation_scope(space, pLoader);
      if (checkSpace_scope == null) {
         sb.append(": {checkers: ");
      } else {
         sb.append(": {scope: ").append(checkSpace_scope);
         sb.append(", checkers: ");
      }
      sb.append(getCheckers(space, pLoader, pGetField));
      List/*<AncillaryPlausiCheckSpace>*/ subSpaces =
         (List/*<AncillaryPlausiCheckSpace>*/) pGetField.invoke(null, space, "_subSpaces");
      if (subSpaces.size() > 0) {
         sb.append(", subspaces: [");
         StringBuilder sbSubSpaces = new StringBuilder();
         for (/*AncillaryPlausiCheckSpace*/Object subSpace : subSpaces) {
            sbSubSpaces.append(analyseCheckSpace(subSpace, pLoader, pGetField));
            sbSubSpaces.append(", ");
         }
         if (sbSubSpaces.length() > 2) {
            sbSubSpaces.setLength(sbSubSpaces.length() - 2);
         }
         sb.append(sbSubSpaces);
         sb.append("]");
      }
      sb.append("}");
      return sb.toString();
   }


   /**
    * Gets the value of attribute scope of annotation CheckSpace
    *
    * @param space AncillaryPlausiCheckSpace
    * @param pLoader class loader
    * @return value of CheckSpace/@scope, if present, otherwise null
    * @throws ClassNotFoundException
    * @throws IllegalAccessException
    * @throws IllegalArgumentException
    * @throws InvocationTargetException
    * @throws NoSuchMethodException
    * @throws SecurityException
    *
    * @author kaufmann
    */
   private String getCheckSpaceAnnotation_scope(/*AncillaryPlausiCheckSpace*/Object space, ClassLoader pLoader)
      throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException
   {
      @SuppressWarnings("unchecked")
      Class<Annotation> checkSpaceAnnotationClass = (Class<Annotation>) pLoader.loadClass(CHECK_SPACE);
      /*CheckSpace*/Annotation annotation = space.getClass().getAnnotation(checkSpaceAnnotationClass);
      return (String) (annotation == null ? null
            : annotation.getClass().getMethod("scope", new Class<?>[]{}).invoke(annotation, new Object[]{}));
   }


   /**
    * Gets the value of attribute checks of annotation Checker
    *
    * @param checker AncillaryPlausiCheckChecker
    * @param pLoader class loader
    * @return value of Checker/@checks, if present, otherwise null
    * @throws ClassNotFoundException
    * @throws IllegalAccessException
    * @throws IllegalArgumentException
    * @throws InvocationTargetException
    * @throws NoSuchMethodException
    * @throws SecurityException
    *
    * @author kaufmann
    */
   private String getCheckerAnnotation(/*AncillaryPlausiCheckChecker<? extends AncillaryPlausiCheckSpace>*/Object checker,
                                       ClassLoader pLoader)
      throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException
   {
      @SuppressWarnings("unchecked")
      Class<Annotation> checkerAnnotationClass = (Class<Annotation>) pLoader.loadClass(CHECKER);
      /*Checker*/Annotation annotation = checker.getClass().getAnnotation(checkerAnnotationClass);
      return (String) (annotation == null ? null
            : annotation.getClass().getMethod("checks", new Class<?>[]{}).invoke(annotation, new Object[]{}));
   }


   /**
    * Gets the checkers for a check space
    *
    * @param space AncillaryPlausiCheckSpace
    * @param pLoader class loader
    * @param pGetField Method to read private members
    * @return Checkers as JSON string
    * @throws Exception
    *
    * @author kaufmann
    */
   private String getCheckers(/*AncillaryPlausiCheckSpace*/Object space, ClassLoader pLoader, Method pGetField)
      throws Exception
   {
      StringBuilder sb = new StringBuilder();
      List/*<AncillaryPlausiCheckChecker<? extends AncillaryPlausiCheckSpace>>*/ checkers =
         (List/*<AncillaryPlausiCheckChecker<? extends AncillaryPlausiCheckSpace>>*/) pGetField.invoke(null, space,
               "_plausiCheckers");
      sb.append("[");
      for (/*AncillaryPlausiCheckChecker<? extends AncillaryPlausiCheckSpace>*/Object checker : checkers) {
         sb.append(checker.getClass().getSimpleName());
         String checkerAnnotation = getCheckerAnnotation(checker, pLoader);
         if (checkerAnnotation != null) {
            sb.append(": {checks: ").append(checkerAnnotation).append("}");
         }
         sb.append(", ");

      }
      if (sb.length() > 2) {
         sb.setLength(sb.length() - 2);
      }
      sb.append("]");
      return sb.toString();
   }


}
