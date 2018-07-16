package schemagenerator.gui;


import java.io.*;
import java.util.*;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.Generator;
import schemagenerator.actions.*;

import org.eclipse.swt.widgets.Label;


public class SwtGalileoWsdl
   extends SwtBase
{

   private Text textAreaGalileo = null;
   private Composite compositeGalileoHelp = null;
   private Label labelGalileoHelp = null;
   Text textGalileoHelpDirectory = null;
   private Button buttonGalileoHelpDirectory = null;
   Table table = null;
   private Label label = null;
   Text textTemp = null;
   private Button buttonClear = null;
   private Composite composite = null;
   private Button buttonSelectAll = null;
   private Button buttonClearAll = null;
   private Label labelUpdateText = null;

   public SwtGalileoWsdl(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.grabExcessVerticalSpace = false;
      // this.setLayout(new GridLayout());
      this.setSize(new Point(491, 233));
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      textAreaGalileo = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaGalileo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaGalileo.setLayoutData(gridData1);
      textAreaGalileo.setText("Enter the \'TransactionHelp\' url and press the Load button. "
            + "The realted versions will be loaded. After this select the "
            + "required requests and press the 'Start' Button.");
      this.setLayout(gridLayout);
      createCompositeGalileoHelp();
      setSize(new Point(482, 305));
   }

   /**
    * This method initializes compositeGalileoHelp	
    *
    */
   private void createCompositeGalileoHelp()
   {
      GridData gridData12 = new GridData();
      gridData12.grabExcessHorizontalSpace = true;
      gridData12.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData11 = new GridData();
      gridData11.horizontalSpan = 3;
      gridData11.grabExcessVerticalSpace = true;
      gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData11.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData11.grabExcessHorizontalSpace = true;
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      GridData gridData16 = new GridData();
      gridData16.grabExcessHorizontalSpace = true;
      gridData16.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout7 = new GridLayout();
      gridLayout7.numColumns = 3;
      compositeGalileoHelp = new Composite(this, SWT.NONE);
      compositeGalileoHelp.setLayout(gridLayout7);
      compositeGalileoHelp.setLayoutData(gridData);
      labelGalileoHelp = new Label(compositeGalileoHelp, SWT.NONE);
      labelGalileoHelp.setText("TransactionHelp Url");
      textGalileoHelpDirectory = new Text(compositeGalileoHelp, SWT.BORDER | SWT.SINGLE);
      textGalileoHelpDirectory
            .setText("http://testws.galileo.com/GWSSample/Help/GWSHelp/mergedProjects/TRANSACTIONHELP/main_site_map.htm");
      textGalileoHelpDirectory.setLayoutData(gridData16);
      buttonGalileoHelpDirectory = new Button(compositeGalileoHelp, SWT.NONE);
      buttonGalileoHelpDirectory.setText("Load");
      label = new Label(compositeGalileoHelp, SWT.NONE);
      label.setText("Temporary Directory");
      textTemp = new Text(compositeGalileoHelp, SWT.BORDER);
      textTemp.setText("C:/temp/ImportGalileoHelp");
      textTemp.setLayoutData(gridData12);
      buttonClear = new Button(compositeGalileoHelp, SWT.NONE);
      buttonClear.setText("Clear");
      buttonClear.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            String sTemp = textTemp.getText();
            Utils.clearDirectory(sTemp);
         }
      });
      table = new Table(compositeGalileoHelp, SWT.CHECK);
      table.setHeaderVisible(true);
      table.setLinesVisible(true);
      table.setLayoutData(gridData11);

      final TableEditor editor = new TableEditor(table);
      // The editor must have the same size as the cell and must
      // not be any smaller than 50 pixels.
      editor.horizontalAlignment = SWT.RIGHT;
      editor.grabHorizontal = true;
      // editor.minimumWidth = 50;
      // editing the second column
      final int EDITABLECOLUMN = 2;

      table.addSelectionListener(new SelectionAdapter()
      {
         public void widgetSelected(SelectionEvent e)
         {
            // Clean up any previous editor control
            Control oldEditor = editor.getEditor();
            if (oldEditor != null)
               oldEditor.dispose();

            // Identify the selected row
            TableItem item = (TableItem) e.item;
            if (item == null)
               return;

            // The control that will be the editor must be a child of the
            // Table
            Text newEditor = new Text(table, SWT.NONE | SWT.RIGHT);
            newEditor.setText(item.getText(EDITABLECOLUMN));
            newEditor.addModifyListener(new ModifyListener()
            {
               public void modifyText(ModifyEvent me)
               {
                  Text text = (Text) editor.getEditor();
                  editor.getItem().setText(EDITABLECOLUMN, text.getText());
               }
            });
            newEditor.selectAll();
            newEditor.setFocus();
            editor.setEditor(newEditor, item, EDITABLECOLUMN);
         }
      });


      createComposite();
      TableColumn tableColumn = new TableColumn(table, SWT.NONE);
      tableColumn.setText("Name");
      TableColumn tableColumn1 = new TableColumn(table, SWT.RIGHT);
      tableColumn1.setText("Installed Version");
      TableColumn tableColumn2 = new TableColumn(table, SWT.RIGHT);
      tableColumn2.setText("Available Version");
      buttonGalileoHelpDirectory.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            Hashtable<String, String[]> htVersions =
               ImportGalileoWsdl.getLastVersions(textGalileoHelpDirectory.getText(), true);
            String[] array = htVersions.keySet().toArray(new String[0]);
            Arrays.sort(array);
            table.removeAll();
            for (String element : array) {
               String[] sVersion = htVersions.get(element);
               if ((sVersion != null) && (sVersion.length == 2)) {
                  TableItem tableItem = new TableItem(table, SWT.LEFT);
                  tableItem.setText(0, element);
                  String sOld = sVersion[0].replaceAll("_", ".");
                  tableItem.setText(1, sOld.startsWith("+") ? sOld.substring(1) : sOld);
                  String sNew = sVersion[1].replaceAll("_", ".");
                  tableItem.setText(2, sNew);
                  if ((sOld.indexOf("-") >= 0) || sOld.equals(sNew)) {
                     if (sOld.indexOf("-") < 0) {
                        tableItem.setBackground(new Color(table.getDisplay(), 220, 255, 220));
                        tableItem.setChecked(true);
                     } else {
                        tableItem.setChecked(false);
                     }
                  } else {
                     tableItem.setChecked(true);
                     if (sOld.startsWith("+")) {
                        tableItem.setBackground(new Color(table.getDisplay(), 255, 200, 200));
                     } else {
                        tableItem.setBackground(new Color(table.getDisplay(), 255, 255, 200));
                     }
                  }
               }
            }
            for (int i = 0; i < table.getColumnCount(); i++) {
               table.getColumn(i).pack();
            }
         }
      });
   }

   @Override
   public void start(Generator generator)
   {
      // get all selected tables
      TableItem[] items = table.getItems();
      HashSet<String> hsSelected = new HashSet<String>();
      for (TableItem item : items) {
         String sOldVersion = item.getText(1);
         boolean bChecked = item.getChecked();
         String sRequest = item.getText(0);
         if (sOldVersion.indexOf("-") < 0) {
            // there is an old version
            hsSelected.add(sRequest + "_" + item.getText(bChecked ? 2 : 1).replaceAll("\\.", "_"));
         }
         if (bChecked) {
            hsSelected.add(sRequest + "_" + item.getText(2).replaceAll("\\.", "_"));
         }
      }
      try {
         ImportGalileoWsdl.startToImport(hsSelected, textGalileoHelpDirectory.getText(),
               textTemp.getText());
         infoFinished("Creation of Galileo helpFiles finished.");
      }
      catch (Exception ex) {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         ex.printStackTrace(new PrintStream(byteArrayOutputStream));
         errorMsg("ERROR in Galileo Import\n" + byteArrayOutputStream.toString());
         ex.printStackTrace();
         return;
      }

   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Galileo");
      String tempDir = createObject.getAttribute("tempDir");
      if (tempDir.length() == 0) {
         tempDir = "C:/temp/ImportGalileoHelp";
      }
      textTemp.setText(tempDir);
   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Galileo");
      createObject.setAttribute("tempDir", textTemp.getText());

   }

   /**
    * This method initializes composite	
    *
    */
   private void createComposite()
   {
      GridLayout gridLayout1 = new GridLayout();
      gridLayout1.numColumns = 3;
      composite = new Composite(compositeGalileoHelp, SWT.NONE);
      composite.setLayout(gridLayout1);
      labelUpdateText = new Label(composite, SWT.NONE);
      labelUpdateText.setText("update version of select requests");
      buttonSelectAll = new Button(composite, SWT.NONE);
      buttonSelectAll.setText("select All");
      buttonSelectAll.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            TableItem[] items = table.getItems();
            for (TableItem item : items) {
               item.setChecked(true);
            }
         }
      });
      buttonClearAll = new Button(composite, SWT.NONE);
      buttonClearAll.setText("clear All");
      buttonClearAll.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            TableItem[] items = table.getItems();
            for (TableItem item : items) {
               item.setChecked(false);
            }
         }
      });
   }

   //   @Override
   //   public void setActive(boolean b)
   //   {
   //      textAreaGalileo.setEnabled(b);
   //      textGalileoHelpDirectory.setEnabled(b);
   //      buttonGalileoHelpDirectory.setEnabled(b);
   //   }

} //  @jve:decl-index=0:visual-constraint="10,10"
