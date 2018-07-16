package schemagenerator.gui;


import ifaoplugin.*;

import java.io.*;
import java.util.StringTokenizer;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.Generator;
import schemagenerator.actions.jaxb.ImportDirectory;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;


public class SwtJaxBDirectory
   extends SwtBase
{

   private Text textArea = null;
   private Composite compositeImportDirectory = null;
   private Text textImportDirectory = null;
   private Button buttonImportDirectory = null;
   private Composite compositeCompleteFill = null;
   private Label labelImportDirectory = null;
   private Label label = null;
   private Label labelPackage = null;
   private Text textAreaConsole = null;

   public SwtJaxBDirectory(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData11 = new GridData();
      gridData11.grabExcessHorizontalSpace = true;
      gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData11.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData11.grabExcessVerticalSpace = true;
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      textArea = new Text(this, SWT.WRAP);
      textArea.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textArea.setLayoutData(gridData);
      textArea
            .setText("All xsd files of the following directory will be analysed and data.xsd and bindings.xjb will be written.");
      createCompositeCompleteFill();
      setSize(new Point(836, 232));
      setLayout(new GridLayout());
      textAreaConsole = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
      textAreaConsole.setLayoutData(gridData11);
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
      buttonImportDirectory.setText("Open");
      buttonImportDirectory.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            openDirectory(textImportDirectory);
            analysePackage();
         }
      });
   }

   /**
    * This method initializes compositeCompleteFill	
    *
    */
   private void createCompositeCompleteFill()
   {
      GridData gridData3 = new GridData();
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData3.grabExcessHorizontalSpace = true;
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
      label = new Label(compositeCompleteFill, SWT.NONE);
      label.setText("Package");
      labelPackage = new Label(compositeCompleteFill, SWT.NONE);
      labelPackage.setText("");
      labelPackage.setLayoutData(gridData3);
   }

   @Override
   public void start(Generator generator)
   {
      String sPackage = labelPackage.getText();
      if (sPackage.length() == 0) {
         errorMsg("No Package defined");
         return;
      }
      String sImportDir = textImportDirectory.getText();

      if (sImportDir.endsWith("\\") || sImportDir.endsWith("/")) {
         sImportDir = sImportDir.substring(0, sImportDir.length() - 1);
      }

      if (sImportDir.length() > 0) {
         File importDir = new File(sImportDir);
         if (!importDir.exists()) {
            info("ERROR with directory\n" + sImportDir + " not exist");
            return;
         }
         File rootDirectory =
            new File(Util.getProviderDataRootDirectory(generator.sBaseArctic,
                  sPackage.replace('.', '/')));
         if (importDir.getAbsolutePath().startsWith(rootDirectory.getAbsolutePath())) {
            OutputStream swtConsoleStream = new SwtOutputStream(textAreaConsole);

            PrintStream pOut = new PrintStream(swtConsoleStream);
            try {
               new ImportDirectory(pOut).importXsdFiles(rootDirectory, sPackage);
               infoFinished("Related data.xsd file(s) generated", sPackage);
            }
            catch (Exception e) {
               errorMsg("Exception:" + e.getLocalizedMessage());
               e.printStackTrace();
            }
            finally {
               pOut.close();
            }
         } else {
            errorMsg("Directory has to be within root directory\n"
                  + rootDirectory.getAbsolutePath());
         }

      } else {
         errorMsg("Please enter a valid directory\n");
      }

   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("DirectoryJaxB");
      textImportDirectory.setText(createObject.getAttribute("importDir"));
      analysePackage();
   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("DirectoryJaxB");
      createObject.setAttribute("importDir", textImportDirectory.getText());
   }

   private void analysePackage()
   {
      StringBuilder sbPackage = new StringBuilder();
      StringTokenizer st = new StringTokenizer(textImportDirectory.getText(), "\\/");
      while (st.hasMoreTokens()) {
         if (st.nextToken().equals("lib")) {
            if (st.hasMoreElements() && st.nextToken().equals("providerdata")) {
               while (st.hasMoreTokens()) {
                  if (sbPackage.length() > 0) {
                     sbPackage.append(".");
                  }
                  sbPackage.append(st.nextToken());
               }
            }
         }
      }
      labelPackage.setText(sbPackage.toString());
   }
} //  @jve:decl-index=0:visual-constraint="10,10"
