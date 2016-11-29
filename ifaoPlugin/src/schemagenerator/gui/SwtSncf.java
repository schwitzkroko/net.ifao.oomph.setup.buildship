package schemagenerator.gui;


import net.ifao.xml.*;

import schemagenerator.*;
import schemagenerator.actions.*;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Font;


/**
 * Class SwtSncf
 *
 * <p>
 * Copyright &copy; 2008, i:FAO Group GmbH
 * @author kaufmann
 */
public class SwtSncf
   extends SwtBase
{

   private Text textAreaInfo = null;
   private Composite compositeMain = null;
   private Label labelURL_SNCF = null;
   private Text textURL_SNCF = null;
   private Text textAreaInfoWebServices = null;
   private Label labelJavaCommunication = null;
   private Text textJavaCommunication = null;
   private Button buttonOpen = null;
   private Text textAreaInfoJavaCommunication = null;
   private Button buttonDefaultURL = null;

   public static final String[] RIVA_SERVICES = new String[]{ "AQ", "BA", "CC", "DD", "EJ", "HP",
         "QP", "RE", "TC" };

   /**
    * Constructor SwtSncf
    *
    * @param pParent
    * @param pStyle
    *
    * @author kaufmann
    */
   public SwtSncf(Composite pParent, int pStyle)
   {
      super(pParent, pStyle);
      initialize();
   }

   /**
    * This method initializes this
    * 
    */
   private void initialize()
   {
      GridData gridData5 = new GridData();
      gridData5.grabExcessHorizontalSpace = true;
      gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      textAreaInfo = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaInfo.setLayoutData(gridData5);
      textAreaInfo
            .setText("Please enter the base URL of SNCF's WebServices and the path and filename of the JavaCommunication ");
      this.setLayout(new GridLayout());
      createCompositeMain();

   }

   /**
    * Method loadValuesFrom
    * overrides @see schemagenerator.gui.SwtBase#loadValuesFrom(net.ifao.xml.XmlObject)
    *
    * @param pSettings
    *
    * @author kaufmann
    */
   @Override
   public void loadValuesFrom(XmlObject pSettings)
   {
      XmlObject sncf = pSettings.createObject("SNCF");
      textURL_SNCF.setText(sncf.getAttribute("baseUrl"));
      textJavaCommunication.setText(sncf.getAttribute("javaClass"));
   }

   /**
    * Method saveValuesTo
    * overrides @see schemagenerator.gui.SwtBase#saveValuesTo(net.ifao.xml.XmlObject)
    *
    * @param pSettings
    *
    * @author kaufmann
    */
   @Override
   public void saveValuesTo(XmlObject pSettings)
   {
      XmlObject sncf = pSettings.createObject("SNCF");
      sncf.setAttribute("baseUrl", textURL_SNCF.getText());
      sncf.setAttribute("javaClass", textJavaCommunication.getText());
   }

   /**
    * Method start
    * overrides @see schemagenerator.gui.SwtBase#start(schemagenerator.Generator)
    *
    * @param pGenerator
    *
    * @author kaufmann
    */
   @Override
   public void start(Generator pGenerator)
   {
      ImportSncf importSncf = new ImportSncf(textURL_SNCF.getText(), textJavaCommunication
            .getText(), pGenerator.sBaseArctic);
      if (importSncf.getLastError() != null) {
         errorMsg(importSncf.getLastError());
         return;
      }

      importSncf.startGeneration();
      if (importSncf.getLastError() != null) {
         errorMsg(importSncf.getLastError());
         return;
      }

      if (importSncf.getResult().length() > 0) {
         displayText(importSncf.getResult());
      }
   }

   /**
    * This method initializes compositeMain	
    *
    */
   private void createCompositeMain()
   {
      GridData gridData21 = new GridData();
      GridData gridData12 = new GridData();
      gridData12.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData11 = new GridData();
      gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData11.grabExcessHorizontalSpace = false;
      GridData gridData4 = new GridData();
      gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData4.horizontalSpan = 3;
      gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
      gridData4.grabExcessVerticalSpace = true;
      gridData4.grabExcessHorizontalSpace = true;
      GridData gridData3 = new GridData();
      gridData3.grabExcessVerticalSpace = true;
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData3.grabExcessHorizontalSpace = true;
      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData2.grabExcessHorizontalSpace = true;
      GridData gridData1 = new GridData();
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData1.grabExcessVerticalSpace = true;
      gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
      gridData1.grabExcessHorizontalSpace = true;
      GridData gridData = new GridData();
      gridData.horizontalSpan = 3;
      gridData.grabExcessHorizontalSpace = true;
      gridData.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
      gridData.grabExcessVerticalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
      compositeMain = new Composite(this, SWT.NONE);
      compositeMain.setLayout(gridLayout);
      compositeMain.setLayoutData(gridData1);
      labelURL_SNCF = new Label(compositeMain, SWT.NONE);
      labelURL_SNCF.setText("Base-URL SNCF:");
      textURL_SNCF = new Text(compositeMain, SWT.BORDER);
      textURL_SNCF.setEditable(true);
      textURL_SNCF.setLayoutData(gridData2);
      textURL_SNCF.addModifyListener(new org.eclipse.swt.events.ModifyListener()
      {
         @Override
         public void modifyText(org.eclipse.swt.events.ModifyEvent e)
         {
            if (textAreaInfoWebServices != null) {
               StringBuilder sbInfo = new StringBuilder();
               sbInfo.append("The following web services will be accessed:");
               String sUrl = textURL_SNCF.getText();
               if (sUrl.length() > 0) {
                  for (String sService : RIVA_SERVICES) {
                     sbInfo.append("\n").append(sService).append(" - ").append(sUrl).append("/")
                           .append(sService).append("?wsdl");
                  }
               } else {
                  for (String sService : RIVA_SERVICES) {
                     sbInfo.append("\n").append(sService);
                  }
               }
               textAreaInfoWebServices.setText(sbInfo.toString());
            }
         }
      });
      buttonDefaultURL = new Button(compositeMain, SWT.NONE);
      buttonDefaultURL.setText("Default");
      buttonDefaultURL
            .setToolTipText("Set Base-URL SNCF to \"http://integration.wdi-vsct.com:1181/sbt/4_0/services\"");
      buttonDefaultURL.setLayoutData(gridData12);
      buttonDefaultURL.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            textURL_SNCF.setText("http://integration.wdi-vsct.com:1181/sbt/4_0/services");
         }
      });
      textAreaInfoWebServices = new Text(compositeMain, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
      textAreaInfoWebServices.setBackground(Display.getCurrent().getSystemColor(
            SWT.COLOR_WIDGET_LIGHT_SHADOW));
      textAreaInfoWebServices.setEditable(false);
      StringBuilder sbDefaultInfoWebServices = new StringBuilder(100);
      sbDefaultInfoWebServices.append("The following web services will be accessed:");
      for (String sService : RIVA_SERVICES) {
         sbDefaultInfoWebServices.append("\n").append(sService);
      }
      textAreaInfoWebServices.setText(sbDefaultInfoWebServices.toString());
      textAreaInfoWebServices.setFont(new Font(Display.getDefault(), "Courier New", 8, SWT.NORMAL));
      textAreaInfoWebServices.setForeground(Display.getCurrent().getSystemColor(
            SWT.COLOR_WIDGET_NORMAL_SHADOW));
      textAreaInfoWebServices.setLayoutData(gridData);
      labelJavaCommunication = new Label(compositeMain, SWT.NONE);
      labelJavaCommunication.setText("Java Communication:");
      labelJavaCommunication.setLayoutData(gridData21);
      textJavaCommunication = new Text(compositeMain, SWT.BORDER);
      textJavaCommunication.setLayoutData(gridData3);
      textJavaCommunication.addModifyListener(new org.eclipse.swt.events.ModifyListener()
      {
         @Override
         public void modifyText(org.eclipse.swt.events.ModifyEvent e)
         {
            if (textAreaInfoJavaCommunication != null) {
               StringBuilder sbInfo = new StringBuilder();
               sbInfo.append("Additionally the following classes will be created:");
               String sJavaCommunication = textJavaCommunication.getText();
               if (sJavaCommunication.length() > 0) {
                  for (String sService : RIVA_SERVICES) {
                     sbInfo.append("\n").append(sService).append(" - ").append(
                           ImportSncf.getJavaCommunicationName(sJavaCommunication, sService));
                  }
               } else {
                  for (String sService : RIVA_SERVICES) {
                     sbInfo.append("\n").append(sService);
                  }
               }
               textAreaInfoJavaCommunication.setText(sbInfo.toString());
            }
         }
      });
      buttonOpen = new Button(compositeMain, SWT.PUSH);
      buttonOpen.setText("Open");
      buttonOpen.setLayoutData(gridData11);
      buttonOpen.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            openFile(textJavaCommunication, "*.java");
         }
      });
      textAreaInfoJavaCommunication = new Text(compositeMain, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
      textAreaInfoJavaCommunication.setBackground(Display.getCurrent().getSystemColor(
            SWT.COLOR_WIDGET_LIGHT_SHADOW));
      textAreaInfoJavaCommunication.setEditable(false);
      StringBuilder sbDefaultInfoJavaCommunication = new StringBuilder(100);
      for (String sService : RIVA_SERVICES) {
         sbDefaultInfoJavaCommunication.append("\n").append(sService);
      }
      textAreaInfoJavaCommunication.setText(sbDefaultInfoJavaCommunication.toString());
      textAreaInfoJavaCommunication.setFont(new Font(Display.getDefault(), "Courier New", 8,
            SWT.NORMAL));
      textAreaInfoJavaCommunication.setForeground(Display.getCurrent().getSystemColor(
            SWT.COLOR_WIDGET_NORMAL_SHADOW));
      textAreaInfoJavaCommunication.setLayoutData(gridData4);
   }

}
