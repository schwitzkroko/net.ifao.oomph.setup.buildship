package schemagenerator.gui;


import ifaoplugin.*;

import java.io.*;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.Generator;
import schemagenerator.actions.jaxb.ImportWsdl;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;


public class SwtGermanwings
   extends SwtBase
{

   private Text textAreaInfo = null;
   private Composite compositeGalileoHelp = null;
   private Label label = null;
   private Text wsdlSessionManager = null;
   private Label label1 = null;
   private Text wsdlBookingManager = null;
   private Text textAreaConsole = null;
   private Label label2 = null;
   private Text wsdlContentManager = null;

   public SwtGermanwings(Composite parent, int style)
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
      textAreaInfo
            .setText("Please select the related URL (wsdl) files for German wings communication");
      // neccessary
      this.setLayout(gridLayout);
      Label filler1 = new Label(this, SWT.NONE);
      createCompositeGalileoHelp();
      setSize(new Point(482, 305));
      Label filler = new Label(this, SWT.NONE);
      textAreaConsole = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
      textAreaConsole.setLayoutData(gridData11);
   }

   /**
    * This method initializes compositeGalileoHelp	
    *
    */
   private void createCompositeGalileoHelp()
   {
      GridData gridData12 = new GridData();
      gridData12.grabExcessVerticalSpace = true;
      gridData12.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
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
      compositeGalileoHelp = new Composite(this, SWT.NONE);
      compositeGalileoHelp.setLayout(gridLayout7);
      compositeGalileoHelp.setLayoutData(gridData13);
      label = new Label(compositeGalileoHelp, SWT.NONE);
      label.setText("Session Manager");
      wsdlSessionManager = new Text(compositeGalileoHelp, SWT.BORDER);
      wsdlSessionManager.setLayoutData(gridData4);
      label1 = new Label(compositeGalileoHelp, SWT.NONE);
      label1.setText("Booking Manager");
      wsdlBookingManager = new Text(compositeGalileoHelp, SWT.BORDER);
      wsdlBookingManager.setLayoutData(gridData6);
      label2 = new Label(compositeGalileoHelp, SWT.NONE);
      label2.setText("Content Manager");
      wsdlContentManager = new Text(compositeGalileoHelp, SWT.BORDER);
      wsdlContentManager.setLayoutData(gridData12);
   }

   @Override
   public void start(Generator generator)
   {
      textAreaConsole.setText("");
      OutputStream swtConsoleStream = new SwtOutputStream(textAreaConsole);

      PrintStream pOut = new PrintStream(swtConsoleStream);
      Util.setOutput(pOut);
      try {
         String sDirectoryProviderData =
            Util.getProviderDataRootDirectory(generator.sBaseArctic, "com/germanwings");

         ImportWsdl wsdlUtil = new ImportWsdl(pOut);

         wsdlUtil.importWsdl(wsdlSessionManager.getText(), sDirectoryProviderData,
               "com.germanwings.session", true);

         wsdlUtil.importWsdl(wsdlBookingManager.getText(), sDirectoryProviderData,
               "com.germanwings.booking", true);

         wsdlUtil.importWsdl(wsdlContentManager.getText(), sDirectoryProviderData,
               "com.germanwings.content", true);

         infoFinished("Schema files have been created within directory\n"
               + new File(sDirectoryProviderData, "com/germanwings").getAbsolutePath(),
               "com.germanwings");
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

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Germanwings");

      String sessionManager = createObject.getAttribute("sessionManager");
      if (sessionManager.length() == 0) {
         sessionManager = "https://newskies-test.germanwings.com/SessionManager.svc?WSDL";
      }
      wsdlSessionManager.setText(sessionManager);

      String bookingManager = createObject.getAttribute("bookingManager");
      if ((bookingManager.length() == 0) || bookingManager.equals(sessionManager)) {
         bookingManager = "https://newskies-test.germanwings.com/BookingManager.svc?WSDL";
      }
      wsdlBookingManager.setText(bookingManager);

      String contentManager = createObject.getAttribute("contentManager");
      if ((contentManager.length() == 0) || contentManager.equals(sessionManager)) {
         contentManager = "https://newskies-test.germanwings.com/ContentManager.svc?WSDL";
      }
      wsdlContentManager.setText(contentManager);

   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Germanwings");
      createObject.setAttribute("sessionManager", wsdlSessionManager.getText());
      createObject.setAttribute("bookingManager", wsdlBookingManager.getText());
      createObject.setAttribute("contentManager", wsdlContentManager.getText());
   }

} //  @jve:decl-index=0:visual-constraint="10,10"
