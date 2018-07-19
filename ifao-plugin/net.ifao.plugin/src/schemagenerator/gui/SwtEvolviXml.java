package schemagenerator.gui;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import net.ifao.xml.XmlObject;
import schemagenerator.Generator;
import schemagenerator.actions.ImportEvolviXml;


/**
 * Class SwtEvolviXml
 *
 * <p>
 * Copyright &copy; 2009, i:FAO Group GmbH
 * @author kaufmann
 */
public class SwtEvolviXml
   extends SwtBase
{

   private Text textAreaInfo = null;
   private Composite compositeMain = null;
   private Label labelURL_EvolviXml = null;
   private Text textURL_EvolviXml = null;
   private Button buttonDefaultURL = null;
   private Text textAreaInfoWebservices = null;
   private Label labelJavaCommunication = null;
   private Text textJavaCommunication = null;
   private Button buttonOpen = null;

   public static final String[] EVOLVI_SERVICES = new String[]{ "EvRailApi", "ReferenceData" };

   /**
    * Constructor SwtSncf
    *
    * @param pParent
    * @param pStyle
    *
    * @author kaufmann
    */
   public SwtEvolviXml(Composite pParent, int pStyle)
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
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      textAreaInfo = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaInfo
            .setText("Please enter the base URL of EvolviXML's WebServices and the path and filename of the JavaCommunication");
      textAreaInfo.setLayoutData(gridData);
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
      XmlObject evolviXml = pSettings.createObject("EVOLVI_XML");
      textURL_EvolviXml.setText(evolviXml.getAttribute("baseUrl"));
      textJavaCommunication.setText(evolviXml.getAttribute("javaClass"));
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
      XmlObject evolviXml = pSettings.createObject("EVOLVI_XML");
      evolviXml.setAttribute("baseUrl", textURL_EvolviXml.getText());
      evolviXml.setAttribute("javaClass", textJavaCommunication.getText());
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
      ImportEvolviXml importEvolviXml =
         new ImportEvolviXml(textURL_EvolviXml.getText(), textJavaCommunication.getText(), pGenerator.sBaseArctic);
      if (importEvolviXml.getLastError() != null) {
         errorMsg(importEvolviXml.getLastError());
         return;
      }

      importEvolviXml.startGeneration();
      if (importEvolviXml.getLastError() != null) {
         errorMsg(importEvolviXml.getLastError());
         return;
      }

      if (importEvolviXml.getResult().length() > 0) {
         displayText(importEvolviXml.getResult());
      }

   }

   /**
    * This method initializes compositeMain
    *
    */
   private void createCompositeMain()
   {
      GridData gridData5 = new GridData();
      gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData4 = new GridData();
      gridData4.grabExcessHorizontalSpace = true;
      gridData4.grabExcessVerticalSpace = true;
      gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData3 = new GridData();
      gridData3.grabExcessHorizontalSpace = true;
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
      gridData3.horizontalSpan = 3;
      gridData3.grabExcessVerticalSpace = true;
      GridData gridData2 = new GridData();
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
      gridData1.grabExcessVerticalSpace = true;
      compositeMain = new Composite(this, SWT.NONE);
      compositeMain.setLayoutData(gridData1);
      compositeMain.setLayout(gridLayout);
      labelURL_EvolviXml = new Label(compositeMain, SWT.NONE);
      labelURL_EvolviXml.setText("Base-URL Evolvi Xml:");
      textURL_EvolviXml = new Text(compositeMain, SWT.BORDER);
      textURL_EvolviXml.setLayoutData(gridData2);
      textURL_EvolviXml.addModifyListener(new org.eclipse.swt.events.ModifyListener()
      {
         @Override
         public void modifyText(org.eclipse.swt.events.ModifyEvent e)
         {
            if (textAreaInfoWebservices != null) {
               StringBuilder sbInfo = new StringBuilder();
               sbInfo.append("The following web services will be accessed:");
               String sUrl = textURL_EvolviXml.getText();
               if (sUrl.length() > 0) {
                  for (String sService : EVOLVI_SERVICES) {
                     sbInfo.append("\n").append(sService).append(" - ").append(sUrl).append("/").append(sService)
                           .append(".svc?singleWsdl");
                  }
               } else {
                  for (String sService : EVOLVI_SERVICES) {
                     sbInfo.append("\n").append(sService);
                  }
               }
               textAreaInfoWebservices.setText(sbInfo.toString());
            }
         }
      });
      buttonDefaultURL = new Button(compositeMain, SWT.NONE);
      buttonDefaultURL.setText("Default");
      textAreaInfoWebservices = new Text(compositeMain, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
      textAreaInfoWebservices.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
      textAreaInfoWebservices.setEditable(false);
      textAreaInfoWebservices.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
      StringBuilder sbDefaultInfoWebServices = new StringBuilder(100);
      sbDefaultInfoWebServices.append("The following web services will be accessed:");
      for (String sService : EVOLVI_SERVICES) {
         sbDefaultInfoWebServices.append("\n").append(sService);
      }
      textAreaInfoWebservices.setText(sbDefaultInfoWebServices.toString());
      textAreaInfoWebservices.setFont(new Font(Display.getDefault(), "Courier New", 8, SWT.NORMAL));
      textAreaInfoWebservices.setLayoutData(gridData3);
      labelJavaCommunication = new Label(compositeMain, SWT.NONE);
      labelJavaCommunication.setText("JavaCommunication:");
      textJavaCommunication = new Text(compositeMain, SWT.BORDER);
      textJavaCommunication.setLayoutData(gridData4);
      buttonOpen = new Button(compositeMain, SWT.NONE);
      buttonOpen.setText("Open");
      buttonOpen.setLayoutData(gridData5);
      buttonOpen.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            openFile(textJavaCommunication, "*.java");
         }
      });
      buttonDefaultURL.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            textURL_EvolviXml.setText("http://ifao.usertest12.evolvi.co.uk/webservices");
         }
      });
   }

}
