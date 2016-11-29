package net.ifao.tools.ancillaries;


import java.lang.reflect.*;

import ifaoplugin.*;
import ifaoplugin.Util.IDialogContentBuilder;

import org.eclipse.core.commands.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


/**
 * Dumps the RuleProcessor's truth table in (a selectable) arctic project as simple text and as
 * Wiki text in a Dialog.
 *
 * <p>
 * Copyright &copy; 2014, i:FAO Group GmbH
 * @author kaufmann
 */
public class TruthTableDumperHandler
   extends AbstractHandler
{
   private static final String TRUTH_TABLE_DUMPER_CLASS =
      "net.ifao.arctic.agents.common.ruleprocessor.ancillary.domain.serialize.truthtable.TruthTableDumper";
   private static final String SIMPLE_TEXT_FORMATTER_CLASS =
      "net.ifao.arctic.agents.common.ruleprocessor.ancillary.domain.serialize.truthtable.TruthTableDumper$SimpleTextFormatter";
   private static final String WIKI_MARKUP_FORMATTER_CLASS =
      "net.ifao.arctic.agents.common.ruleprocessor.ancillary.domain.serialize.truthtable.TruthTableDumper$WikiMarkUpFormatter";

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

         ClassLoader loader = new ArcticClassLoader(getClass().getClassLoader(), sClassesPath);
         String sConfigPath = sClassesPath.substring(0, sClassesPath.lastIndexOf("/")) + "/conf";

         try {
            Util.setArcticConfiguration(sConfigPath, loader);
            
            // create WIKI formatter
            Class wikiMarkUpFormatterClass = loader.loadClass(WIKI_MARKUP_FORMATTER_CLASS);
            Object wikiMarkUpFormatter = wikiMarkUpFormatterClass.newInstance();

            // create SimpleText formatter
            Class simpleTextFormatterClass = loader.loadClass(SIMPLE_TEXT_FORMATTER_CLASS);
            Object simpleTextFormatter = simpleTextFormatterClass.newInstance();

            // get the interface for the output formatters, defined within the TruthTableDumper
            Class<?> truthTableDumperClass = loader.loadClass(TRUTH_TABLE_DUMPER_CLASS);
            Class<?> outputFormatterInterface = null;
            Class<?>[] declaredClasses = truthTableDumperClass.getDeclaredClasses();
            for (Class<?> declaredClass : declaredClasses) {
               if (declaredClass.getName().contains("OutputFormatter")) {
                  outputFormatterInterface = declaredClass;
                  break;
               }
            }

            // start the dump as simple text
            StringBuilder sb = new StringBuilder();
            Object truthTableDumper =
               truthTableDumperClass.getConstructor(outputFormatterInterface,
                     java.lang.StringBuilder.class).newInstance(simpleTextFormatter, sb);
            truthTableDumperClass.getMethod("run", new Class<?>[]{}).invoke(truthTableDumper,
                  new Object[]{});
            String sSimpleTextDump = sb.toString();

            // start the dump as wiki text
            sb.setLength(0);
            truthTableDumper =
               truthTableDumperClass.getConstructor(outputFormatterInterface,
                     java.lang.StringBuilder.class).newInstance(wikiMarkUpFormatter, sb);
            truthTableDumperClass.getMethod("run", new Class<?>[]{}).invoke(truthTableDumper,
                  new Object[]{});
            String sWikiMarkUpDump = sb.toString();

            // show the results
            displayResults(sSimpleTextDump, sWikiMarkUpDump);
         }
         catch (InstantiationException | IllegalAccessException | ClassNotFoundException
               | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
               | SecurityException pException) {
            Util.displayExceptionInDialog(sClassesPath, pException,
                  "Error finding the TruthTableDumper or its children!\n\n");
         }
      }
      return null;
   }

   /**
    * Creates the dialog with the dumps in SimpleText format and WikiMarkUp format
    *
    * @param psSimpleTextDump result of the dump in simple text format
    * @param psWikiMarkUpDump result of the dump in WikiMarkUp format
    *
    * @author kaufmann
    */
   private void displayResults(final String psSimpleTextDump, final String psWikiMarkUpDump)
   {
      Util.displayDialog("Truth Table Dump", new IDialogContentBuilder()
      {
         @Override
         public void createContent(Composite pContainer)
         {
            pContainer.setLayout(new FillLayout(SWT.HORIZONTAL|SWT.VERTICAL));
            
            TabFolder tabFolder = new TabFolder(pContainer, SWT.NONE);
            
            addTab(tabFolder, "Simple Text", psSimpleTextDump);
            addTab(tabFolder, "Wiki Markup", psWikiMarkUpDump);
         }

         private void addTab(TabFolder pTabFolder, String psTabTitle, String psTabContent)
         {
            TabItem tabItem = new TabItem(pTabFolder, SWT.NONE);
            tabItem.setText(psTabTitle);
            Text text = new Text(pTabFolder, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
            text.setFont(new Font(Display.getDefault(), "Courier", 10, SWT.NONE));
            text.setText(psTabContent);
            tabItem.setControl(text);
            
         }
      });
   }

}
