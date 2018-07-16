package schemagenerator.gui;


import net.ifao.xml.*;

import schemagenerator.*;
import schemagenerator.actions.*;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;


/**
 * Class SwtCaro
 *
 * <p>
 * Copyright &copy; 2008, i:FAO Group GmbH
 * @author kaufmann
 */
public class SwtCaro
   extends SwtBase
{

   private Text textAreaInfo = null;
   private Composite composite = null;
   private Label labelWsdl = null;
   private Text textWSDL_URL = null;
   private Button ButtonBrowseWSDL_URL = null;
   private Label labelJavaCommunication = null;
   private Text textJavaCommunication = null;
   private Button buttonOpenJavaCommunication = null;

   /**
    * Constructor SwtCaro
    *
    * @param pParent
    * @param pStyle
    *
    * @author kaufmann
    */
   public SwtCaro(Composite pParent, int pStyle)
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
      this.setLayout(new GridLayout());
      textAreaInfo = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaInfo.setLayoutData(gridData);
      textAreaInfo
            .setText("Enter the URL to the Caro WSDL and the path and filename of the JavaCommunication");
      createComposite();

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
      XmlObject caro = pSettings.createObject("Caro");
      textJavaCommunication.setText(caro.getAttribute("javaClass"));
      textWSDL_URL.setText("https://82.198.201.167/cytric/wsdl/interface_caro.wsdl");
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
      XmlObject caro = pSettings.createObject("Caro");
      caro.setAttribute("javaClass", textJavaCommunication.getText());
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

      ImportCaro importCaro = new ImportCaro(textWSDL_URL.getText(), textJavaCommunication
            .getText(), pGenerator.sBaseArctic);
      if (importCaro.getLastError() != null) {
         errorMsg(importCaro.getLastError());
         return;
      }

      importCaro.startGeneration();
      if (importCaro.getLastError() != null) {
         errorMsg(importCaro.getLastError());
         return;
      }

      if (importCaro.getResult().length() > 0) {
         displayText(importCaro.getResult());
      }
   }

   /**
    * This method initializes composite	
    *
    */
   private void createComposite()
   {
      GridData gridData2 = new GridData();
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData4 = new GridData();
      gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData4.grabExcessHorizontalSpace = true;
      GridData gridData3 = new GridData();
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
      composite = new Composite(this, SWT.NONE);
      composite.setLayout(gridLayout);
      composite.setLayoutData(gridData1);
      labelWsdl = new Label(composite, SWT.NONE);
      labelWsdl.setText("WSDL:");
      textWSDL_URL = new Text(composite, SWT.BORDER);
      textWSDL_URL.setLayoutData(gridData4);
      ButtonBrowseWSDL_URL = new Button(composite, SWT.NONE);
      ButtonBrowseWSDL_URL.setText("Open");
      ButtonBrowseWSDL_URL.setLayoutData(gridData3);
      ButtonBrowseWSDL_URL.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            openFile(textWSDL_URL, "*.wsdl");
         }
      });
      labelJavaCommunication = new Label(composite, SWT.NONE);
      labelJavaCommunication.setText("Java Communication:");
      textJavaCommunication = new Text(composite, SWT.BORDER);
      textJavaCommunication.setLayoutData(gridData2);
      buttonOpenJavaCommunication = new Button(composite, SWT.NONE);
      buttonOpenJavaCommunication.setText("Open");
      buttonOpenJavaCommunication
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
            {
               @Override
               public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
               {
                  openFile(textJavaCommunication, "*.java");
               }
            });
   }

}
