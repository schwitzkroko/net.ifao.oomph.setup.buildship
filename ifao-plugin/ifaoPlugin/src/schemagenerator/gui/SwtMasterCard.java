package schemagenerator.gui;


import ifaoplugin.*;

import java.io.File;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import schemagenerator.Generator;
import schemagenerator.actions.ImportToPackages;
import schemagenerator.actions.ImportXml;
import schemagenerator.correctors.*;


public class SwtMasterCard
   extends SwtBase
{

   private final static String IMPORT_DIR = "net/ifao/providerdata/mastercard";

   private ICorrector _corrector = new MasterCardCorrector();

   private Text textArea = null;

   private Text textAreaCorrections = null;

   public SwtMasterCard(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData = new GridData();
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessHorizontalSpace = true;
      gridData.grabExcessVerticalSpace = true;
      gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData4 = new GridData();
      gridData4.grabExcessHorizontalSpace = true;
      gridData4.verticalAlignment = GridData.CENTER;
      gridData4.horizontalAlignment = GridData.FILL;
      textArea = new Text(this, SWT.MULTI | SWT.WRAP);
      textArea.setLayoutData(gridData4);
      textAreaCorrections = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
      textAreaCorrections.setLayoutData(gridData);
      this.setSize(new Point(393, 238));
      GridLayout gridLayout = new GridLayout();
      gridLayout.makeColumnsEqualWidth = false;
      String description =
         "All xsd files of the project's directory \"" + IMPORT_DIR + "\" will be analysed "
               + "and data.xsd and databinding.xml will be written within the same directory.";
      textArea.setText(description);
      String sCorrectionsText =
         "The following corrections are applied:\n" + _corrector.getCorrectionSummary();
      textAreaCorrections.setText(sCorrectionsText);
      this.setLayout(gridLayout);
   }

   private String getImportDir()
   {
      String baseDir = Generator.getSettings().getAttribute("baseDir");
      return Util.getProviderDataPath(baseDir, IMPORT_DIR);
   }

   @Override
   public void start(Generator generator)
   {
      String sImportDir = getImportDir();
      boolean bIgnoreSimpleTypes = true;
      boolean bCheckBoxXsd = false;
      boolean bCreatePackages = false;
      boolean bDateTimeHandler = true;


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

            if (!(new File(sImportDir, "data.xsd")).exists()) {
               if (!getBoolean("ERROR with data.xsd\n" + sImportDir
                     + " don't contain data.xsd, continue within subdirectories ?")) {
                  return;
               }

               sAll = "all! ";
            }

            ImportXml.startDataBinding(sImportDir, bIgnoreSimpleTypes, true, "", false,
                  bDateTimeHandler);

            if (bIgnoreSimpleTypes) {
               infoFinished("Creation of " + sAll + sImportDir + File.separator
                     + "dataBinding.xml finished.", IMPORT_DIR);
            } else {
               infoFinished("Modification of " + sAll + sImportDir + File.separator
                     + "data.xsd and creation of " + sImportDir + File.separator
                     + "dataBinding.xml finished.", IMPORT_DIR);

            }
         } else {
            if (bCreatePackages) {
               ImportToPackages.importDirectory(sImportDir, bIgnoreSimpleTypes, bDateTimeHandler);
               // ImportXml.startBuild(sImportDir, bIgnoreSimpleTypes);
               infoFinished("Creation of " + sImportDir + File.separator + "data.xsd's and "
                     + sImportDir + File.separator + "dataBinding.xml's finished.", IMPORT_DIR);
            } else {
               ImportXml.startBuild(sImportDir, bIgnoreSimpleTypes, true, _corrector, "", false,
                     bDateTimeHandler);
               infoFinished("Creation of " + sImportDir + File.separator + "data.xsd and "
                     + sImportDir + File.separator + "dataBinding.xml finished.", IMPORT_DIR);
            }
         }

      } else {
         errorMsg("Please enter a valid directory\n");
      }

   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {}

   @Override
   public void saveValuesTo(XmlObject settings)
   {}

} //  @jve:decl-index=0:visual-constraint="23,-32"
