package schemagenerator.gui;

import net.ifao.xml.*;
import schemagenerator.*;
import schemagenerator.actions.*;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;

/**
 * Class SwtBibeWsdl
 *
 * <p>
 * Copyright &copy; 2009, i:FAO Group GmbH
 * @author kaufmann
 */
public class SwtBibeWsdl
   extends SwtBase
{
   private Text textAreaInfo = null;
   private Composite compositeMain = null;
   private Label labelBibeWsdl = null;
   private Text textBibeWsdl = null;
   private Label labelJavaCommunication = null;
   private Text textJavaCommunication = null;
   private Button buttonOpenWsdl = null;
   private Button buttonOpenJavaCommunication = null;

   /**
    * Constructor SwtBibeWsdl
    *
    * @param pParent
    * @param pStyle
    *
    * @author kaufmann
    */
   public SwtBibeWsdl(Composite pParent, int pStyle)
   {
      super(pParent, pStyle);
		initialize();
   }

   /**
    * This method initializes this
    * 
    */
   private void initialize() {
        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 1;
        GridData gridData3 = new GridData();
        gridData3.horizontalAlignment = GridData.FILL;
        gridData3.grabExcessHorizontalSpace = true;
        gridData3.verticalAlignment = GridData.CENTER;
        this.setLayout(gridLayout1);
        this.setSize(new Point(478, 89));
        textAreaInfo = new Text(this, SWT.MULTI | SWT.WRAP);
        textAreaInfo.setText("Please enter path and filename of Bibe's WSDL and path and filename of the JavaCommunication");
        textAreaInfo.setLayoutData(gridData3);
        textAreaInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
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
      XmlObject bibeWsdl = pSettings.createObject("BibeWsdl");
      textBibeWsdl.setText(bibeWsdl.getAttribute("wsdlFile"));
      textJavaCommunication.setText(bibeWsdl.getAttribute("javaCommunication"));
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
      XmlObject bibeWsdl = pSettings.createObject("BibeWsdl");
      bibeWsdl.deleteObjects("");
      bibeWsdl.setAttribute("wsdlFile", textBibeWsdl.getText());
      bibeWsdl.setAttribute("javaCommunication", textJavaCommunication.getText());
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
      try {
         ImportBibeWsdl importBibeWsdl = new ImportBibeWsdl(pGenerator.sBaseArctic, textBibeWsdl.getText(),
               textJavaCommunication.getText());
         importBibeWsdl.startGeneration();
         if (importBibeWsdl.getLastError() != null) {
            errorMsg(importBibeWsdl.getLastError());
            return;
         }

         String sResult = importBibeWsdl.getResult();
         if(sResult.length()>0) {
            displayText(sResult);
         }
      }
      catch (Exception pException) {
         errorMsg(pException.getMessage());
      }
   }

   /**
    * This method initializes compositeMain	
    *
    */
   private void createCompositeMain()
   {
      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = GridData.FILL;
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.verticalAlignment = GridData.CENTER;
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.verticalAlignment = GridData.FILL;
      gridData1.grabExcessVerticalSpace = true;
      gridData1.horizontalAlignment = GridData.FILL;
      GridData gridData = new GridData();
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessHorizontalSpace = true;
      gridData.verticalAlignment = GridData.CENTER;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
      compositeMain = new Composite(this, SWT.NONE);
      compositeMain.setLayout(gridLayout);
      compositeMain.setLayoutData(gridData1);
      labelBibeWsdl = new Label(compositeMain, SWT.NONE);
      labelBibeWsdl.setText("Bibe WSDL:");
      textBibeWsdl = new Text(compositeMain, SWT.BORDER);
      textBibeWsdl.setLayoutData(gridData);
      buttonOpenWsdl = new Button(compositeMain, SWT.NONE);
      buttonOpenWsdl.setText("Open");
      buttonOpenWsdl.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            openFile(textBibeWsdl, "*.wsdl, *.*");
         }
      });
      labelJavaCommunication = new Label(compositeMain, SWT.NONE);
      labelJavaCommunication.setText("Java Communication:");
      textJavaCommunication = new Text(compositeMain, SWT.BORDER);
      textJavaCommunication.setLayoutData(gridData2);
      buttonOpenJavaCommunication = new Button(compositeMain, SWT.NONE);
      buttonOpenJavaCommunication.setText("Open");
      buttonOpenJavaCommunication.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            openFile(textJavaCommunication, "*.java, *.*");
         }
      });
   }

}  //  @jve:decl-index=0:visual-constraint="0,0"
