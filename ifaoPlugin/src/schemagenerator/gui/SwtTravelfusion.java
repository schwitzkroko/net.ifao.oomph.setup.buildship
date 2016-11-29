package schemagenerator.gui;


import ifaoplugin.*;

import java.io.File;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.GridData;

import schemagenerator.*;
import schemagenerator.actions.ImportTravelfusionSchemas;

import org.eclipse.swt.widgets.Label;


public class SwtTravelfusion
   extends SwtBase
{

   private Text textAreaTravelfusion = null;
   private Composite compositeFill = null;
   private Label labelUrl = null;
   private Text textUrl = null;
   private Label labelRequest = null;
   private Text textRequest = null;
   private Label labelResponse = null;
   private Text textResponse = null;

   public SwtTravelfusion(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      textAreaTravelfusion = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaTravelfusion.setBackground(Display.getCurrent().getSystemColor(
            SWT.COLOR_INFO_BACKGROUND));
      textAreaTravelfusion.setLayoutData(gridData);
      textAreaTravelfusion
            .setText("The schemas from Travelfusion will be loaded into a temporary directory and the "
                  + "related data.xsd and dataBinding.xml will be written into the correct "
                  + "directories below\n" + "travelfusion/xml");
      createCompositeFill();
      setSize(new Point(300, 200));
      setLayout(new GridLayout());
   }

   @Override
   public void start(Generator generator)
   {
      String psTF_URL = textUrl.getText();
      String pbTF_Request = textRequest.getText();
      String pbTF_Response = textResponse.getText();
      String sTFProviderdataPath =
         Util.getProviderDataPath(generator.sBaseArctic, "net/ifao/providerdata/travelfusion/xml");
      if (sTFProviderdataPath.length() > 0) {

         // check the existence of the directory containing the provider data of travelfusion
         if (!(new File(sTFProviderdataPath)).exists()) {
            errorMsg("ERROR in Arctic Base Path\n" + sTFProviderdataPath + " does not exist");

            return;
         }
         sTFProviderdataPath += "/";

         // start the tool to generate the schemas
         try {
            ImportTravelfusionSchemas.startBuild(sTFProviderdataPath, psTF_URL, pbTF_Request,
                  pbTF_Response);
         }
         catch (Exception ex) {
            errorMsg("ERROR\n" + ex.getMessage());
            ex.printStackTrace();
         }
      }


   }

   /**
    * This method initializes compositeFill	
    *
    */
   private void createCompositeFill()
   {
      GridData gridData4 = new GridData();
      gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData4.grabExcessHorizontalSpace = true;
      GridData gridData3 = new GridData();
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData3.grabExcessHorizontalSpace = true;
      GridData gridData2 = new GridData();
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      compositeFill = new Composite(this, SWT.NONE);
      compositeFill.setLayoutData(gridData1);
      compositeFill.setLayout(gridLayout);
      labelUrl = new Label(compositeFill, SWT.NONE);
      labelUrl.setText("URL:");
      textUrl = new Text(compositeFill, SWT.BORDER);
      textUrl.setLayoutData(gridData2);
      labelRequest = new Label(compositeFill, SWT.NONE);
      labelRequest.setText("Request Schema:");
      textRequest = new Text(compositeFill, SWT.BORDER);
      textRequest.setLayoutData(gridData3);
      labelResponse = new Label(compositeFill, SWT.NONE);
      labelResponse.setText("Response Schema:");
      textResponse = new Text(compositeFill, SWT.BORDER);
      textResponse.setLayoutData(gridData4);
   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Travelfusion");
      String url = createObject.getAttribute("url");
      if (url.length() == 0) {
         url = "http://admin.travelfusion.com/xmlspec/schema/";
      }
      textUrl.setText(url);
      String request = createObject.getAttribute("request");
      if (request.length() == 0) {
         request = "requests/GeneralRequest.xsd";
      }
      textRequest.setText(request);
      String response = createObject.getAttribute("response");
      if (response.length() == 0) {
         response = "responses/GeneralResponse.xsd";
      }
      textResponse.setText(response);
   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      XmlObject createObject = settings.createObject("Travelfusion");
      createObject.setAttribute("url", textUrl.getText());
      createObject.setAttribute("request", textRequest.getText());
      createObject.setAttribute("response", textResponse.getText());
   }

   //   @Override
   //   public void setActive(boolean b)
   //   {
   //      textAreaTravelfusion.setEnabled(b);
   //      textRequest.setEnabled(b);
   //      textResponse.setEnabled(b);
   //      textUrl.setEnabled(b);
   //   }
}
