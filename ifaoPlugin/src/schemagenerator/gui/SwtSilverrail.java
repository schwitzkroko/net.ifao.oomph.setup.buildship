package schemagenerator.gui;


import ifaoplugin.*;

import java.io.*;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.Generator;
import schemagenerator.actions.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;


public class SwtSilverrail
   extends SwtBase
{

   private Text textAreaInfo = null;
   private Composite compositeSilverrailHelp = null;
   private Label label = null;
   private Text wsdlShopping = null;
   private Label label1 = null;
   private Text wsdlBooking = null;
   private Label label2 = null;
   private Text wsdlPublishing = null;
   private Text textAreaConsole = null;

   public SwtSilverrail(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData11 = new GridData();
      gridData11.grabExcessVerticalSpace = true;
      gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData11.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData11.grabExcessHorizontalSpace = true;
      GridData gridData41 = new GridData();
      gridData41.grabExcessHorizontalSpace = true;
      gridData41.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData21 = new GridData();
      gridData21.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData21.grabExcessHorizontalSpace = true;
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      textAreaInfo = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaInfo.setLayoutData(gridData41);
      textAreaInfo.setText("Please enter the URLs of the Silverrail WSDLs");
      // neccessary
      this.setLayout(gridLayout);
      Label filler1 = new Label(this, SWT.NONE);
      createCompositeSilverrailHelp();
      setSize(new Point(482, 305));
      Label filler = new Label(this, SWT.NONE);
      textAreaConsole = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
      textAreaConsole.setLayoutData(gridData11);
   }

   /**
    * This method initializes compositeSilverrailHelp	
    *
    */
   private void createCompositeSilverrailHelp()
   {
      GridData gridData6 = new GridData();
      gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData6.grabExcessHorizontalSpace = true;
      GridData gridData4 = new GridData();
      gridData4.grabExcessHorizontalSpace = true;
      gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData13 = new GridData();
      gridData13.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData13.grabExcessHorizontalSpace = true;
      GridData gridData10 = new GridData();
      gridData10.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData9 = new GridData();
      gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData9.grabExcessHorizontalSpace = true;
      GridData gridData8 = new GridData();
      gridData8.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData8.grabExcessHorizontalSpace = true;
      GridData gridData7 = new GridData();
      gridData7.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData7.grabExcessHorizontalSpace = true;
      GridData gridData5 = new GridData();
      gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData3 = new GridData();
      gridData3.grabExcessHorizontalSpace = true;
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData2 = new GridData();
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout7 = new GridLayout();
      gridLayout7.numColumns = 2;
      GridData gridData14 = new GridData();
      gridData14.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData14.grabExcessHorizontalSpace = true;

      compositeSilverrailHelp = new Composite(this, SWT.NONE);
      compositeSilverrailHelp.setLayout(gridLayout7);
      compositeSilverrailHelp.setLayoutData(gridData13);
      label = new Label(compositeSilverrailHelp, SWT.NONE);
      label.setText("Shopping service");
      wsdlShopping = new Text(compositeSilverrailHelp, SWT.BORDER);
      wsdlShopping.setLayoutData(gridData4);
      label1 = new Label(compositeSilverrailHelp, SWT.NONE);
      label1.setText("Booking service");
      wsdlBooking = new Text(compositeSilverrailHelp, SWT.BORDER);
      wsdlBooking.setLayoutData(gridData6);
      label2 = new Label(compositeSilverrailHelp, SWT.NONE);
      label2.setText("Publishing service");
      wsdlPublishing = new Text(compositeSilverrailHelp, SWT.BORDER);
      wsdlPublishing.setLayoutData(gridData14);
   }

   /**
    * Here the creation of the Silverrail schemas starts. Class ImportSilverrail will be used for
    * this purpose
    * @see schemagenerator.gui.SwtBase#start(schemagenerator.Generator)
    *
    * @author kaufmann
    */
   @Override
   public void start(Generator generator)
   {
      textAreaConsole.setText("");
      OutputStream swtConsoleStream = new SwtOutputStream(textAreaConsole);

      PrintStream pOut = new PrintStream(swtConsoleStream);
      Util.setOutput(pOut);
      try {
         String sDirectoryProviderData =
            Util.getProviderDataRootDirectory(generator.sBaseArctic, "net/railgds");

         // remove old schemas/bindings/interface classes
         Util.clearDirectoryExceptCVS(new File(sDirectoryProviderData, "net/railgds"));

         ImportSilverrail silverrail = new ImportSilverrail(pOut, "net.railgds");

         // handle the shopping service
         silverrail.importWsdl(wsdlShopping.getText(), "Shopping", sDirectoryProviderData,
               generator.sBaseArctic);

         // handle the booking service
         silverrail.importWsdl(wsdlBooking.getText(), "Booking", sDirectoryProviderData,
               generator.sBaseArctic);

         // handle the PublishedData service
         silverrail.importWsdl(wsdlPublishing.getText(), "Publishing", sDirectoryProviderData,
               generator.sBaseArctic);

         infoFinished("Schema files have been created within directory\n"
               + new File(sDirectoryProviderData, "net/railgds").getAbsolutePath(), "net.railgds");
      }
      catch (Exception e) {
         // should never happen ... just in case
         errorMsg(e.getLocalizedMessage());
         e.printStackTrace(new PrintStream(swtConsoleStream));
      }
      finally {
         try {
            swtConsoleStream.close();
         }
         catch (IOException e) {
            // stream already closed
         }
      }
      Util.removeOutput();

   }

   /**
    * Loads the values used last from SchemaGenerator.xml
    * @see schemagenerator.gui.SwtBase#loadValuesFrom(net.ifao.xml.XmlObject)
    *
    * @author kaufmann
    */
   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      XmlObject silverrail = settings.createObject("Silverrail");

      String sShopping = silverrail.getAttribute("shopping");
      if (sShopping.length() == 0) {
         sShopping = "https://xml-cert.railgds.net/shopping-ws/services/Shopping/v2?WSDL";
      }
      wsdlShopping.setText(sShopping);

      String sBooking = silverrail.getAttribute("booking");
      if (sBooking.length() == 0) {
         sBooking = "https://xml-cert.railgds.net/booking-ws/services/Booking/v2?WSDL";
      }
      wsdlBooking.setText(sBooking);

      String sPublishing = silverrail.getAttribute("publishing");
      if (sPublishing.length() == 0) {
         sPublishing = "https://xml-cert.railgds.net/pub-ws/publishing.wsdl";
      }
      wsdlPublishing.setText(sPublishing);
   }

   /**
    * Stores the values entered in the GUI in SchemaGenerator.xml
    * @see schemagenerator.gui.SwtBase#saveValuesTo(net.ifao.xml.XmlObject)
    *
    * @author kaufmann
    */
   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject silverrail = settings.createObject("Silverrail");
      silverrail.setAttribute("shopping", wsdlShopping.getText());
      silverrail.setAttribute("booking", wsdlBooking.getText());
      silverrail.setAttribute("publishing", wsdlPublishing.getText());
   }

} //  @jve:decl-index=0:visual-constraint="10,10"
