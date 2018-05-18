package net.ifao.tools.ancillaries;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import ifaoplugin.ArcticClassLoader;
import ifaoplugin.Util;


/**
 * Dumps the RuleProcessorLanguage in (a selectable) arctic project as table in a Dialog.
 *
 * <p>
 * Copyright &copy; 2014, i:FAO Group GmbH
 * @author kaufmann
 */
public class RuleProcessorLanguageDumperHandler
   extends AbstractHandler
{
   private static final String ROOT_CLASS_LOCATOR =
      "net.ifao.arctic.agents.common.ruleprocessor.ancillary.domain.serialize.RootClassLocator";

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

         ClassLoader loader = new ArcticClassLoader(getClass().getClassLoader(), Arrays.asList(new File(sClassesPath)));

         try {
            StringBuilder sbResult = new StringBuilder();
            dumpLanguage(loader, null, loader.loadClass(ROOT_CLASS_LOCATOR).newInstance(), sbResult);

            showResultAsTable(sbResult.toString());
         }
         catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException
               | SecurityException | IllegalArgumentException | InvocationTargetException pException) {
            Util.displayExceptionInDialog(sClassesPath, pException, "Error finding the RootClassLocator or its children!\n\n");
         }
      }
      return null;
   }

   /**
    * Dumps (recursively) the language of the rule processor
    *
    * @param loader Class loader finding arctic classes
    * @param psNameSpace current name space
    * @param locator RootClassLocator
    * @param psbResult Stringbuilder containing the results when finished
    * @throws NoSuchMethodException
    * @throws SecurityException
    * @throws IllegalAccessException
    * @throws IllegalArgumentException
    * @throws InvocationTargetException
    * @throws ClassNotFoundException
    *
    * @author kaufmann
    */
   private static void dumpLanguage(ClassLoader loader, String psNameSpace, Object locator, StringBuilder psbResult)
      throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, ClassNotFoundException

   {
      // get helper for private methods/members
      Class<?> accessUtilClass = loader.loadClass("util.Access");

      Method getField = accessUtilClass.getMethod("getField", new Class[]{ Object.class, String.class });
      Method executeMethod =
         accessUtilClass.getMethod("executeMethod", new Class[]{ Object.class, String.class, Object[].class });

      //      Map<String, Class<?>> mapClassFromName = (Map<String, Class<?>>) Access.getField(locator, "_mapClassFromName");
      @SuppressWarnings("unchecked")
      Map<String, Class<?>> mapClassFromName = (Map<String, Class<?>>) getField.invoke(null, locator, "_mapClassFromName");
      //      Map<String, Object> locators = (Map<String, Object>) Access.getField(locator, "_locators");
      @SuppressWarnings("unchecked")
      Map<String, Object> locators = (Map<String, Object>) getField.invoke(null, locator, "_locators");

      String prefix = psNameSpace == null ? "" : psNameSpace + ":";

      //      String thisNameSpace = prefix + Access.executeMethod(locator, "getNamespace", new Object[] {});
      String thisNameSpace = prefix + executeMethod.invoke(null, locator, "getNamespace", new Object[]{});

      if (mapClassFromName.size() > 0) {
         for (String s : mapClassFromName.keySet()) {
            psbResult.append(thisNameSpace).append(":").append(s).append(" (").append(mapClassFromName.get(s).getName())
                  .append(")\n");
         }
      }

      for (String s : locators.keySet()) {
         dumpLanguage(loader, thisNameSpace, locators.get(s), psbResult);
      }
   }

   /**
    * Creates a table to show the dumped results
    *
    * @param psResult result of the dump; expected format of each line: the URN is followed by a space
    * and then the class name in parenthesis. Example:
    * ancillary:fact:payment&nbsp;(net.ifao.arctic.agents.common.ruleprocessor.ancillary.domain.resource.fact.Payment)
    *
    * @author kaufmann
    */
   private void showResultAsTable(final String psResult)
   {
      Util.displayDialog("RuleProcessor language", new Util.IDialogContentBuilder()
      {

         @Override
         public void createContent(Composite pContainer)
         {
            // Create a table
            Table table = new Table(pContainer, SWT.FULL_SELECTION | SWT.BORDER);
            table.setLayoutData(new GridData(GridData.FILL_BOTH));

            // Create two columns and show
            TableColumn column1 = new TableColumn(table, SWT.LEFT);
            column1.setText("URN");

            TableColumn column2 = new TableColumn(table, SWT.LEFT);
            column2.setText("Class");

            table.setHeaderVisible(true);

            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, "RootClassLocator");
            item.setText(1, ROOT_CLASS_LOCATOR);

            Pattern pattern = Pattern.compile("(.*?) \\((.*?)\\)");
            Matcher matcher = pattern.matcher(psResult);
            while (matcher.find()) {
               item = new TableItem(table, SWT.NONE);
               item.setText(0, matcher.group(1));
               item.setText(1, matcher.group(2));
            }

            column1.pack();
            column2.pack();
         }
      });
   }

}
