package schemagenerator.gui;


import java.io.File;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import schemagenerator.Generator;
import schemagenerator.actions.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;


public class SwtCompleteDirectory
   extends SwtBase
{

   private Text textArea = null;
   private Composite compositeImportDirectory = null;
   private Text textImportDirectory = null;
   private Button buttonImportDirectory = null;
   private Composite compositeCompleteFill = null;
   private Label labelImportDirectory = null;
   private Label labelDataXsd = null;
   private Button checkBoxDataXsd = null;
   private Label labelSimpleTypes = null;
   private Button checkBoxIgnoreSimpleTypes = null;
   private Label labelCreatePackages = null;
   private Button checkBoxCreatePackages = null;
   private Label labelDateHandler = null;
   private Button checkBoxDateHandler = null;

   public SwtCompleteDirectory(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      textArea = new Text(this, SWT.WRAP);
      textArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textArea.setLayoutData(gridData);
      textArea
            .setText("All xsd files of the following directory will be analysed and data.xsd and databinding.xml will be written within the same directory (if create packages is activated, for each xsd file an own package will be created).\nIf 'analyse data.xsd' is activated, only data.xsd will be analysed and the related databinding.xml will be written.\nIf 'xs:dateTime handler' is activated, xs:dateTime elements/attributes will be handled by a specific field handler which removes the millisecond and timezone information from the element's/attribute's value. This is done by adding special instructions to the binding file. This might lead to added XSI namespace and type attributes for the element!");
      createCompositeCompleteFill();
      setSize(new Point(836, 232));
      setLayout(new GridLayout());
   }

   /**
    * This method initializes compositeImportDirectory	
    *
    */
   private void createCompositeImportDirectory()
   {
      GridData gridData1 = new GridData();
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData1.grabExcessHorizontalSpace = true;
      GridData gridData13 = new GridData();
      gridData13.horizontalAlignment = GridData.FILL;
      gridData13.grabExcessHorizontalSpace = true;
      GridLayout gridLayout5 = new GridLayout();
      gridLayout5.numColumns = 2;
      compositeImportDirectory = new Composite(compositeCompleteFill, SWT.NONE);
      compositeImportDirectory.setLayout(gridLayout5);
      compositeImportDirectory.setLayoutData(gridData1);
      textImportDirectory = new Text(compositeImportDirectory, SWT.BORDER);
      textImportDirectory.setLayoutData(gridData13);
      buttonImportDirectory = new Button(compositeImportDirectory, SWT.NONE);
      buttonImportDirectory.setText("Open");
      buttonImportDirectory.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            openDirectory(textImportDirectory);
         }
      });
   }

   /**
    * This method initializes compositeCompleteFill	
    *
    */
   private void createCompositeCompleteFill()
   {
      GridData gridData2 = new GridData();
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout4 = new GridLayout();
      gridLayout4.numColumns = 2;
      compositeCompleteFill = new Composite(this, SWT.NONE);
      compositeCompleteFill.setLayout(gridLayout4);
      compositeCompleteFill.setLayoutData(gridData2);
      labelImportDirectory = new Label(compositeCompleteFill, SWT.NONE);
      labelImportDirectory.setText("Import Directory");
      createCompositeImportDirectory();
      labelDataXsd = new Label(compositeCompleteFill, SWT.NONE);
      labelDataXsd.setText("data.xsd");
      checkBoxDataXsd = new Button(compositeCompleteFill, SWT.CHECK);
      checkBoxDataXsd.setText("analyse data.xsd");
      labelSimpleTypes = new Label(compositeCompleteFill, SWT.NONE);
      labelSimpleTypes.setText("Simple Types");
      checkBoxIgnoreSimpleTypes = new Button(compositeCompleteFill, SWT.CHECK);
      checkBoxIgnoreSimpleTypes.setSelection(true);
      checkBoxIgnoreSimpleTypes.setText("ignore Simple Types");
      labelCreatePackages = new Label(compositeCompleteFill, SWT.NONE);
      labelCreatePackages.setText("Create packages");
      checkBoxCreatePackages = new Button(compositeCompleteFill, SWT.CHECK);
      checkBoxCreatePackages.setText("make packages for each xsd file ");
      labelDateHandler = new Label(compositeCompleteFill, SWT.NONE);
      labelDateHandler.setText("xs:dateTime handler");
      checkBoxDateHandler = new Button(compositeCompleteFill, SWT.CHECK);
      checkBoxDateHandler
            .setText("use net.ifao.util.castor.fieldhandler.DateTimeHandlerIgnoreMillisAndTimezone for xs:dateTime elements/attributes in the binding file");
      checkBoxDateHandler.setSelection(true);
   }

   @Override
   public void start(Generator generator)
   {
      String sImportDir = textImportDirectory.getText();
      boolean bIgnoreSimpleTypes = checkBoxIgnoreSimpleTypes.getSelection();
      boolean bCheckBoxXsd = checkBoxDataXsd.getSelection();
      boolean bCreatePackages = checkBoxCreatePackages.getSelection();
      boolean bDateTimeHandler = checkBoxDateHandler.getSelection();


      if (sImportDir.endsWith("\\") || sImportDir.endsWith("/")) {
         sImportDir = sImportDir.substring(0, sImportDir.length() - 1);
      }

      if (sImportDir.length() > 0) {
         if (!(new File(sImportDir)).exists()) {
            info("ERROR with directory\n" + sImportDir + " not exist");

            return;
         }

         if (bCheckBoxXsd) {
            String sAll = "";

            if (!(new File(sImportDir + File.separator + "data.xsd")).exists()) {
               if (!getBoolean("ERROR with data.xsd\n" + sImportDir
                     + " don't contain data.xsd, continue within subdirectories ?")) {
                  return;
               }

               sAll = "all! ";
            }

            ImportXml.startDataBinding(sImportDir, bIgnoreSimpleTypes, true, "", true,
                  bDateTimeHandler);

            if (bIgnoreSimpleTypes) {
               infoFinished("Creation of " + sAll + sImportDir + File.separator
                     + "dataBinding.xml finished.");
            } else {
               infoFinished("Modification of " + sAll + sImportDir + File.separator
                     + "data.xsd and creation of " + sImportDir + File.separator
                     + "dataBinding.xml finished.");

            }
         } else {
            if (bCreatePackages) {
               ImportToPackages.importDirectory(sImportDir, bIgnoreSimpleTypes, bDateTimeHandler);
               // ImportXml.startBuild(sImportDir, bIgnoreSimpleTypes);
               infoFinished("Creation of " + sImportDir + File.separator + "data.xsd's and "
                     + sImportDir + File.separator + "dataBinding.xml's finished.");
            } else {
               ImportXml.startBuild(sImportDir, bIgnoreSimpleTypes, true, "", true,
                     bDateTimeHandler);
               infoFinished("Creation of " + sImportDir + File.separator + "data.xsd and "
                     + sImportDir + File.separator + "dataBinding.xml finished.");
            }
         }

      } else {
         errorMsg("Please enter a valid directory\n");
      }

   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Directory");
      textImportDirectory.setText(createObject.getAttribute("importDir"));
      checkBoxDataXsd.setSelection(createObject.getAttribute("dataXsd").equalsIgnoreCase("yes"));
      checkBoxIgnoreSimpleTypes.setSelection(!createObject.getAttribute("simpleTypes")
            .equalsIgnoreCase("no"));
      checkBoxDateHandler.setSelection(!createObject.getAttribute("dateTimeHandler")
            .equalsIgnoreCase("no"));
   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Directory");
      createObject.setAttribute("importDir", textImportDirectory.getText());
      createObject.setAttribute("dataXsd", checkBoxDataXsd.getSelection() ? "yes" : "no");
      createObject.setAttribute("simpleTypes", checkBoxIgnoreSimpleTypes.getSelection() ? "yes"
            : "no");
      createObject.setAttribute("dateTimeHandler", checkBoxDateHandler.getSelection() ? "yes"
            : "no");
   }

   //   @Override
   //   public void setActive(boolean b)
   //   {
   //      textArea.setEnabled(b);
   //      textImportDirectory.setEnabled(b);
   //      checkBoxDataXsd.setEnabled(b);
   //      checkBoxIgnoreSimpleTypes.setEnabled(b);
   //      buttonImportDirectory.setEnabled(b);
   //   }
} //  @jve:decl-index=0:visual-constraint="10,10"
