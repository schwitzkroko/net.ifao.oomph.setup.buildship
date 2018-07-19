package schemagenerator.gui;


import java.io.*;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.Generator;
import schemagenerator.actions.ImportTravelportWsdl;


public class SwtTravelport
   extends SwtBase
{

   private Text textAreaTravelport = null;
   private Composite compositeTravelportHelp_1;

   public SwtTravelport(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.grabExcessVerticalSpace = false;
      this.setSize(new Point(491, 233));
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      textAreaTravelport = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaTravelport.setBackground(Display.getCurrent().getSystemColor(
            SWT.COLOR_INFO_BACKGROUND));
      textAreaTravelport.setLayoutData(gridData1);
      textAreaTravelport
            .setText("If you click on the 'start' button, the available schema and wsdl files within the directory "
                  + "com/travelport will be analysed and related schema files will be created. So you have to\n"
                  + "1.) clear the directory 'com/travelport'\n"
                  + "2.) copy the content of the latest Travelport schema and wsdl files\n"
                  + "3.) Press the 'Start' button (which will create missing files)\n"
                  + "4.) Start the BuildTravelportProviderData.bat (if this does not exist start BuildAllBatchFiles.bat first)");
      setLayout(gridLayout);
      createCompositeTravelportHelp();
      setSize(new Point(482, 305));
   }

   /**
    * This method initializes compositeTravelportHelp
    *
    */
   private void createCompositeTravelportHelp()
   {
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      GridLayout gridLayout7 = new GridLayout();
      compositeTravelportHelp_1 = new Composite(this, SWT.NONE);
      compositeTravelportHelp_1.setLayout(gridLayout7);
      compositeTravelportHelp_1.setLayoutData(gridData);
      createComposite();
   }

   @Override
   public void start(Generator generator)
   {

      try {
         ImportTravelportWsdl.startToImport(new File(getBaseDir()));

         //         ImportTravelportWsdl.startToImport(hsSelected, textTravelportHelpDirectory.getText(),
         //               textTemp.getText());
         infoFinished("Creation of Travelport helpFiles finished.\n", "com/travelport");
      }
      catch (Exception ex) {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         ex.printStackTrace(new PrintStream(byteArrayOutputStream));
         errorMsg("ERROR in Travelport Import\n" + byteArrayOutputStream.toString());
         ex.printStackTrace();
         return;
      }

   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Travelport");

   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Travelport");


   }

   /**
    * This method initializes composite
    *
    */
   private void createComposite()
   {}


} //  @jve:decl-index=0:visual-constraint="10,10"
