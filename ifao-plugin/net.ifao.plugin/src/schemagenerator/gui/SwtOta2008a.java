package schemagenerator.gui;


import ifaoplugin.*;

import java.io.*;

import net.ifao.xml.*;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.*;
import schemagenerator.actions.jaxb.*;


public class SwtOta2008a
   extends SwtBase
{

   private final static String IMPORT_DIR = "net/ifao/providerdata/ota/ota2008a";
   private final static String sPackage = "net.ifao.providerdata.ota.ota2008a";

   private Text textArea = null;

   private Text textAreaCorrections = null;

   public SwtOta2008a(Composite parent, int style)
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
      textArea.setText("All schema files in directory " + IMPORT_DIR
            + " will be analysed and corresponding data.xsd files are written.");
      textAreaCorrections.setText("");
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

      if (sPackage.length() == 0) {
         errorMsg("No Package defined");
         return;
      }
      String sImportDir = getImportDir();

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
            new File(Util.getProviderDataRootDirectory(generator.sBaseArctic, IMPORT_DIR));
         if (importDir.getAbsolutePath().startsWith(rootDirectory.getAbsolutePath())) {
            OutputStream swtConsoleStream = new SwtOutputStream(textAreaCorrections);

            PrintStream pOut = new PrintStream(swtConsoleStream);
            try {
               new ImportDirectory(pOut).importXsdFiles(rootDirectory, sPackage, false);
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
   {}

   @Override
   public void saveValuesTo(XmlObject settings)
   {}

} //  @jve:decl-index=0:visual-constraint="23,-32"
