package schemagenerator.gui;


import java.awt.datatransfer.*;
import java.io.PrintStream;
import java.util.*;
import net.ifao.xml.*;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.*;
import schemagenerator.actions.*;
import schemagenerator.actions.ImportSabreWsdlInfo.Data;
import schemagenerator.actions.ImportSabreWsdlInfo.DataUpdater;


public class SwtSabreWsdl
   extends SwtBase
{

   private Text textAreaSabre = null;
   private Table table = null;
   private SwtSabre _swtSabre;
   private Composite composite = null;
   private Button button = null;
   private Button checkBox = null;
   private Thread thread = null;
   private Text text_1;
   private SchemaGeneratorSwt _schemaGeneratorSwt;

   public SwtSabreWsdl(Composite parent, int style, SwtSabre pSwtSabre)
   {
      super(parent, style);
      initialize();
      _swtSabre = pSwtSabre;
   }

   private void initialize()
   {
      GridData gridData3 = new GridData();
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
      GridData gridData2 = new GridData();
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData2.grabExcessVerticalSpace = true;
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalSpan = 2;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      this.setLayout(gridLayout);
      setSize(new Point(300, 200));
      textAreaSabre = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaSabre.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaSabre.setLayoutData(gridData);
      textAreaSabre.setText("Press start to validate the currently used "
            + "WSDL versions within Sabre (this can take a while).\n"
            + "If you select a WebService, it's wsdl url will be entered "
            + "on the Sabre import page !");
      table = new Table(this, SWT.FULL_SELECTION);
      table.setHeaderVisible(true);
      table.setLayoutData(gridData2);
      table.setLinesVisible(true);

      TableColumn tableColumn = new TableColumn(table, SWT.NONE);
      tableColumn.setText("WebService");
      TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
      tableColumn1.setText("Version used");
      TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
      tableColumn2.setText("Version available");
      TableColumn tableColumn3 = new TableColumn(table, SWT.NONE);
      tableColumn3.setText("Used WSDL File");
      createComposite();
      button = new Button(this, SWT.NONE);
      button.setText("Copy to Clipboard");
      button.setLayoutData(gridData3);
      button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {

            String sRet = "";
            boolean selection = checkBox.getSelection();
            if (selection) {
               sRet = "|| ";
            }
            TableColumn[] columns = table.getColumns();
            for (int i = 0; i < columns.length; i++) {
               if (i > 0) {
                  if (selection) {
                     sRet += " || ";
                  } else {
                     sRet += " \t ";
                  }
               }
               sRet += columns[i].getText();
            }
            if (selection) {
               sRet += " ||";
            }
            sRet += "\n";
            TableItem[] items = table.getItems();
            for (TableItem item : items) {
               if (selection) {
                  sRet += "| ";
               }
               for (int j = 0; j < columns.length; j++) {
                  if (j > 0) {
                     if (selection) {
                        sRet += " | ";
                     } else {
                        sRet += "\t";
                     }
                  }
                  if (!selection && (j == 1 || j == 2)) {
                     sRet += "v";
                  }
                  String sText = item.getText(j);
                  if (selection && sText.startsWith("http") && sText.indexOf("://") > 0) {
                     sText = "[" + sText.substring(sText.lastIndexOf("/") + 1) + "|" + sText + "]";
                  }
                  sRet += sText;
               }
               if (selection) {
                  sRet += " |";
               }
               sRet += "\n";
            }
            sRet += "Verions, marked with * are fix-coded from SabreUtil\\!\n";
            StringSelection stringSelection = new StringSelection(sRet);
            Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
         }


      });
      table.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            TableItem[] selection = table.getSelection();
            if (selection.length > 0) {
               String sVersionNew = selection[0].getText(2) + " ";
               sVersionNew = sVersionNew.substring(0, sVersionNew.indexOf(" ")).trim();
               String sWsdl = selection[0].getText(3);
               int iEnd = sWsdl.indexOf("RQ.wsdl");
               int iStart = sWsdl.lastIndexOf("LS") + 2;
               if (iEnd > 0 && iStart < iEnd) {
                  if (sVersionNew.equals("1.0.1")) {
                     sVersionNew = "";
                  }
                  sWsdl = sWsdl.substring(0, iStart) + sVersionNew + sWsdl.substring(iEnd);
               }
               _swtSabre.setWsdlFile(sWsdl);
            }
         }
      });
   }

   @Override
   public void start(Generator generator)
   {
      if (thread == null || !thread.isAlive()) {
         table.removeAll();
         final PrintStream out = new PrintStream(new SwtOutputStream(text_1));

         thread = new Thread()
         {
            @Override
            public void run()
            {
               try {
                  DataUpdater dataUpdater = new ImportSabreWsdlInfo.DataUpdater()
                  {

                     @Override
                     public boolean addData(final Data pData)
                     {
                        if (isDisposed()) {
                           return false;
                        }
                        getDisplay().syncExec(new Runnable()
                        {
                           @Override
                           public void run()
                           {
                              TableItem tableItem = new TableItem(table, SWT.NONE);
                              for (int j = 0; j < 4; j++) {
                                 tableItem.setText(j, pData.getText(j));
                              }
                              checkColors(tableItem);
                              TableColumn[] columns = table.getColumns();
                              for (int i = 0; i < columns.length - 1; i++) {
                                 columns[i].pack();
                              }
                           }
                        });
                        return true;
                     }
                  };
                  ImportSabreWsdlInfo.getVersions(dataUpdater, out);
                  out.close();
               }
               catch (Exception ex) {
                  // catch any exception
               }
               // set active manually
               _schemaGeneratorSwt.setActive(true);
            }
         };
         thread.start();
      }
   }


   private void checkColors(TableItem tableItem)
   {
      // TODO Auto-generated method stub
      int f1 = getVersion(tableItem.getText(1));
      int f2 = getVersion(tableItem.getText(2));
      int diff = f2 - f1;
      if (diff > 200) {
         tableItem.setBackground(new Color(tableItem.getDisplay(), 255, 200, 200));
      } else if (diff > 100) {
         tableItem.setBackground(new Color(tableItem.getDisplay(), 255, 255, 200));
      } else if (diff == 0) {
         tableItem.setBackground(new Color(tableItem.getDisplay(), 200, 255, 200));
      }
   }

   private int getVersion(String text)
   {
      StringTokenizer st = new StringTokenizer(text, ". ()");
      int i = 0;
      for (int j = 0; j < 3; j++) {
         i = i * 100;
         try {
            i += Integer.parseInt(st.nextToken());
         }
         catch (Exception ex) {
            // invalid number
         }
      }
      return i;
   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {

   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {

   }

   /**
    * This method initializes composite
    *
    */
   private void createComposite()
   {

      text_1 = new Text(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
      text_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
      composite = new Composite(this, SWT.NONE);
      composite.setLayout(new GridLayout());
      checkBox = new Button(composite, SWT.CHECK);
      checkBox.setText("copy table as wiki markup");
      checkBox.setSelection(true);
   }

   //   @Override
   //   public void setActive(boolean b)
   //   {
   //      textAreaSabre.setEnabled(b);
   //      textSabreWsdl.setEnabled(b);
   //      checkBoxCreateJar.setEnabled(b);
   //   }

   @Override
   public void setActive(SchemaGeneratorSwt schemaGeneratorSwt)
   {
      _schemaGeneratorSwt = schemaGeneratorSwt;
   }

}
