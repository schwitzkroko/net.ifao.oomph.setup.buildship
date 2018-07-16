package schemagenerator.gui;

import java.util.*;

import net.ifao.xml.*;
import schemagenerator.*;
import schemagenerator.actions.*;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;

/**
 * Class SwtNvsResponseMsgs
 *
 * <p>
 * Copyright &copy; 2010, i:FAO Group GmbH
 * @author kaufmann
 */
public class SwtNvsResponseMsgs
   extends SwtBase
{

   private Text textInfo = null;
   private Composite composite = null;
   private Label labelDirectory = null;
   private Text textDirectory = null;
   private Button buttonAdd = null;
   private Button buttonRemove = null;
   private Composite compositeSelectedDirs = null;
   private Label labelSelectedDirs = null;
   private List listSelectedDirs = null;

   /**
    * Constructor SwtNvsResponseMsgs
    *
    * @param pParent
    * @param pStyle
    *
    * @author kaufmann
    */
   public SwtNvsResponseMsgs(Composite pParent, int pStyle)
   {
      super(pParent, pStyle);
      initialize();
   }
 

   /**
    * This method initializes this
    * 
    */
   private void initialize() {
        GridData gridData5 = new GridData();
        gridData5.grabExcessHorizontalSpace = true;
        gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        this.setLayout(gridLayout);
        textInfo = new Text(this, SWT.MULTI | SWT.WRAP);
        textInfo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        textInfo.setLayoutData(gridData5);
        textInfo.setText("Please add the directories below net/ifao/providerdata/bahn/nvs/response/ you need to create.");
        createComposite();
        createCompositeSelectedDirs();
   		
   }

   /**
    * @see schemagenerator.gui.SwtBase#loadValuesFrom(net.ifao.xml.XmlObject)
    *
    * @author kaufmann
    */
   @Override
   public void loadValuesFrom(XmlObject pSettings)
   {
      XmlObject nvsResponseMsgs = pSettings.createObject("NvsResponseMsgs");
      listSelectedDirs.removeAll();
      for(XmlObject directory : nvsResponseMsgs.getObjects("Directory")) {
         listSelectedDirs.add(directory.getAttribute("name"));
      }
   }

   /**
    * @see schemagenerator.gui.SwtBase#saveValuesTo(net.ifao.xml.XmlObject)
    *
    * @author kaufmann
    */
   @Override
   public void saveValuesTo(XmlObject pSettings)
   {
      XmlObject nvsResponseMsgs = pSettings.createObject("NvsResponseMsgs");
      nvsResponseMsgs.deleteObjects("");
      for(String sDirectory : listSelectedDirs.getItems()) {
         nvsResponseMsgs.createObject("Directory", "name", sDirectory, true);
      }
   }

   /**
    * @see schemagenerator.gui.SwtBase#start(schemagenerator.Generator)
    *
    * @author kaufmann
    */
   @Override
   public void start(Generator pGenerator)
   {
      try {
         ImportNvsResponseMsgs importNvsResponseMsgs =
            new ImportNvsResponseMsgs(pGenerator.sBaseArctic, listSelectedDirs.getItems());
         importNvsResponseMsgs.startGeneration();
         String sResult = importNvsResponseMsgs.getResult();
         if (sResult.length() > 0) {
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
      GridData gridData1 = new GridData();
      gridData1.horizontalAlignment = GridData.FILL;
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.verticalAlignment = GridData.CENTER;
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout1 = new GridLayout();
      gridLayout1.numColumns = 4;
      composite = new Composite(this, SWT.NONE);
      composite.setLayout(gridLayout1);
      composite.setLayoutData(gridData);
      labelDirectory = new Label(composite, SWT.NONE);
      labelDirectory.setText("Directory");
      textDirectory = new Text(composite, SWT.BORDER);
      textDirectory.setLayoutData(gridData1);
      buttonAdd = new Button(composite, SWT.NONE);
      buttonAdd.setText("Add");
      buttonAdd.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            String sDirectory = textDirectory.getText().trim();
            if (sDirectory.length() == 0) {
               return;
            }
            for (String sSelectedDirectory : listSelectedDirs.getItems()) {
               if (sSelectedDirectory.equalsIgnoreCase(sDirectory)) {
                  return;
               }
            }
            listSelectedDirs.add(sDirectory);
            // sort the items of the list
            String[] sItems = listSelectedDirs.getItems();
            Arrays.sort(sItems);
            listSelectedDirs.setItems(sItems);
         }
      });
      buttonRemove = new Button(composite, SWT.NONE);
      buttonRemove.setText("Remove");
      buttonRemove.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            String sDirectory = textDirectory.getText().trim();
            if (sDirectory.length() == 0 || listSelectedDirs.getItemCount() == 0) {
               return;
            }
            for (String sSelectedDirectory : listSelectedDirs.getItems()) {
               if (sSelectedDirectory.equalsIgnoreCase(sDirectory)) {
                  listSelectedDirs.remove(sSelectedDirectory);
                  break;
               }
            }
         }
      });
   }


   /**
    * This method initializes compositeSelectedDirs	
    *
    */
   private void createCompositeSelectedDirs()
   {
      GridData gridData4 = new GridData();
      gridData4.horizontalAlignment = GridData.FILL;
      gridData4.grabExcessHorizontalSpace = true;
      gridData4.grabExcessVerticalSpace = true;
      gridData4.verticalAlignment = GridData.FILL;
      GridData gridData3 = new GridData();
      gridData3.horizontalAlignment = GridData.FILL;
      gridData3.grabExcessHorizontalSpace = true;
      gridData3.verticalAlignment = GridData.CENTER;
      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = GridData.FILL;
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.grabExcessVerticalSpace = true;
      gridData2.verticalAlignment = GridData.FILL;
      compositeSelectedDirs = new Composite(this, SWT.NONE);
      compositeSelectedDirs.setLayout(new GridLayout());
      compositeSelectedDirs.setLayoutData(gridData2);
      labelSelectedDirs = new Label(compositeSelectedDirs, SWT.NONE);
      labelSelectedDirs.setText("Selected Directories:");
      labelSelectedDirs.setLayoutData(gridData3);
      listSelectedDirs = new List(compositeSelectedDirs, SWT.V_SCROLL | SWT.BORDER);
      listSelectedDirs.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
      listSelectedDirs.setLayoutData(gridData4);
      listSelectedDirs.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            String[] sSelectedItems = listSelectedDirs.getSelection();
            if (sSelectedItems != null && sSelectedItems.length > 0) {
               textDirectory.setText(sSelectedItems[0]);
            }
         }
      });
   }

}
