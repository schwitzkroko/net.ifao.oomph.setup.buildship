package schemagenerator.gui;

import java.util.*;

import net.ifao.xml.*;
import schemagenerator.*;
import schemagenerator.actions.*;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;

/**
 * Class SwtNvs
 *
 * <p>
 * Copyright &copy; 2008, i:FAO Group GmbH
 * @author kaufmann
 */
public class SwtNvs
   extends SwtBase
{

   private Text textAreaInfo = null;
   private Composite composite = null;
   private Label labelServiceCatalog = null;
   private Text textServiceCatalog = null;
   private Button buttonOpen = null;
   private Label labelActionService = null;
   private Text textActionService = null;
   private Button buttonAddActionService = null;
   private Button buttonRemoveActionService = null;
   private Composite compositeSelectedActionServices = null;
   private Label labelSelectedActionServices = null;
   private List listSelectedActionServices = null;
   /**
    * Constructor SwtNvs
    *
    * @param pParent
    * @param pStyle
    *
    * @author kaufmann
    */
   public SwtNvs(Composite pParent, int pStyle)
   {
      super(pParent, pStyle);
		initialize();
   }

   /**
    * This method initializes this
    * 
    */
   private void initialize() {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        this.setLayout(new GridLayout());
        textAreaInfo = new Text(this, SWT.MULTI | SWT.WRAP);
        textAreaInfo.setText("Please select the service catalog and add the action/service combinations you need to create. The action/service combinations must be entered using the following format: \"[A-Z]{3}\\d+\".");
        textAreaInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        textAreaInfo.setLayoutData(gridData);
        createComposite();
        createCompositeSelectedActionServices();
   		
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
      XmlObject nvs = pSettings.createObject("Nvs");
      textServiceCatalog.setText(nvs.getAttribute("serviceCatalog"));
      listSelectedActionServices.removeAll();
      for(XmlObject actionService : nvs.getObjects("ActionService")) {
         listSelectedActionServices.add(actionService.getAttribute("code"));
      }
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
      XmlObject nvs = pSettings.createObject("Nvs");
      nvs.deleteObjects("");
      nvs.setAttribute("serviceCatalog", textServiceCatalog.getText());
      for(String sActionService : listSelectedActionServices.getItems()) {
         nvs.createObject("ActionService", "code", sActionService, true);
      }
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
         ImportNvs importNvs = new ImportNvs(pGenerator.sBaseArctic, textServiceCatalog.getText(),
               listSelectedActionServices.getItems());
         importNvs.startGeneration();
         String sResult = importNvs.getResult();
         if(sResult.length()>0) {
            displayText(sResult);
         }
      }
      catch (Exception pException) {
         errorMsg(pException.getMessage());
      }
   }

   /**
    * This method initializes composite	
    *
    */
   private void createComposite()
   {
      GridData gridData5 = new GridData();
      gridData5.grabExcessHorizontalSpace = true;
      gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData4 = new GridData();
      gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData4.grabExcessVerticalSpace = false;
      gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData4.grabExcessHorizontalSpace = true;
      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalSpan = 2;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 4;
      composite = new Composite(this, SWT.NONE);
      composite.setLayout(gridLayout);
      composite.setLayoutData(gridData4);
      labelServiceCatalog = new Label(composite, SWT.NONE);
      labelServiceCatalog.setText("Service Catalog:");
      textServiceCatalog = new Text(composite, SWT.BORDER);
      textServiceCatalog.setToolTipText("Please enter the filename of the service catalog");
      textServiceCatalog.setLayoutData(gridData1);
      buttonOpen = new Button(composite, SWT.NONE);
      buttonOpen.setText("Open");
      buttonOpen.setLayoutData(gridData2);
      buttonOpen.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            openFile(textServiceCatalog, "*.xml, *.*");
         }
      });
      labelActionService = new Label(composite, SWT.NONE);
      labelActionService.setText("Action/Service:");
      textActionService = new Text(composite, SWT.BORDER);
      textActionService.setToolTipText("Please enter a combination of Action and ServiceId, e.g. ANG10000, DBU1000, ...");
      textActionService.setLayoutData(gridData5);
      buttonAddActionService = new Button(composite, SWT.NONE);
      buttonAddActionService.setText("Add");
      buttonAddActionService.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            String sActionService = textActionService.getText().trim().toUpperCase();
            if (sActionService.length() == 0) {
               return;
            }
            if(!sActionService.matches("[A-Z]{3}\\d+")) {
               errorMsg("The combination of action and service must have the following format: \"[A-Z]{3}\\d+\"");
               return;
            }
            for (String sSelectedActionService : listSelectedActionServices.getItems()) {
               if (sSelectedActionService.equals(sActionService)) {
                  return;
               }
            }
            listSelectedActionServices.add(sActionService);
            // sort the items of the list
            String[] sItems = listSelectedActionServices.getItems();
            Arrays.sort(sItems);
            listSelectedActionServices.setItems(sItems);
         }
      });
      buttonRemoveActionService = new Button(composite, SWT.NONE);
      buttonRemoveActionService.setText("Remove");
      buttonRemoveActionService.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            String sActionService = textActionService.getText().trim();
            if (sActionService.length() == 0) {
               return;
            }
            try {
               listSelectedActionServices.remove(sActionService);
            }
            catch (Exception ex) {}
         }
      });
   }

   /**
    * This method initializes compositeSelectedActionServices	
    *
    */
   private void createCompositeSelectedActionServices()
   {
      GridData gridData7 = new GridData();
      gridData7.grabExcessHorizontalSpace = true;
      gridData7.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData7.grabExcessVerticalSpace = true;
      gridData7.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData6 = new GridData();
      gridData6.grabExcessHorizontalSpace = true;
      gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData3 = new GridData();
      gridData3.grabExcessHorizontalSpace = true;
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData3.grabExcessVerticalSpace = true;
      compositeSelectedActionServices = new Composite(this, SWT.NONE);
      compositeSelectedActionServices.setLayout(new GridLayout());
      compositeSelectedActionServices.setLayoutData(gridData3);
      labelSelectedActionServices = new Label(compositeSelectedActionServices, SWT.NONE);
      labelSelectedActionServices.setText("Selected Action/Service combinations:");
      labelSelectedActionServices.setLayoutData(gridData6);
      listSelectedActionServices = new List(compositeSelectedActionServices, SWT.BORDER | SWT.V_SCROLL);
      listSelectedActionServices.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
      listSelectedActionServices.setLayoutData(gridData7);
      listSelectedActionServices.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            String[] sSelectedItems = listSelectedActionServices.getSelection();
            if (sSelectedItems != null && sSelectedItems.length > 0) {
               textActionService.setText(sSelectedItems[0]);
            }
         }
      });
   }

}
