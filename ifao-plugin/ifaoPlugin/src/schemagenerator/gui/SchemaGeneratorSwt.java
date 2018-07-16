package schemagenerator.gui;


import java.util.ArrayList;

import ifaoplugin.*;
import net.ifao.xml.*;

import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.*;


public class SchemaGeneratorSwt
{


   public Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"
   private Composite compositeTop = null;
   private Composite compositeBottom = null;
   private Label labelDir = null;
   private Text textDir = null;
   private Composite compositeButtons = null;
   private Button buttonStart = null;
   private Button buttonEnd = null;
   public TabFolder mainTabFolder = null;
   public ArrayList<TabFolder> lstTabFolders = new ArrayList<TabFolder>();
   private Button dir;

   private SchemaGeneratorSwt getThis()
   {
      return this;
   }

   public void addTab(String string, Composite swtCompleteDirectory)
   {
      TabItem tabItem2 = new TabItem(tabFolder(), SWT.NONE);
      tabItem2.setText(string);
      tabItem2.setControl(swtCompleteDirectory);
   }

   /**
    * This method initializes compositeTop
    *
    */
   private void createCompositeTop()
   {
      GridData gridData3 = new GridData();
      gridData3.grabExcessHorizontalSpace = true;
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      compositeTop = new Composite(sShell, SWT.NONE);
      compositeTop.setLayoutData(gridData);
      compositeTop.setLayout(gridLayout);
      labelDir = new Label(compositeTop, SWT.NONE);
      labelDir.setText("New Arctic Base Directory");
      textDir = new Text(compositeTop, SWT.BORDER);
      textDir.setLayoutData(gridData3);
      dir = new Button(compositeTop, SWT.NONE);
      dir.setImage(UtilSwt.getImage("schemagenerator/fold.gif", sShell.getDisplay()));
      dir.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            DirectoryDialog fd = new DirectoryDialog(sShell, SWT.OPEN);
            fd.setText("Open");
            fd.setMessage("Please select arctic root directory");

            fd.setFilterPath(textDir.getText());
            String selected = fd.open();
            if (selected != null && selected.length() > 0) {
               selected = selected.replaceAll("\\/", "\\");
               if (!selected.endsWith("\\")) {
                  selected += "\\";
               }
               if (!Util.getConfFile(selected, "ArcticPnrElementInfos.xml").exists()) {
                  MessageDialog.openError(sShell, "Error", "Not a valid arctic directory");
               } else {
                  textDir.setText(selected);
               }
            }
         }
      });
   }

   /**
    * This method initializes compositeBottom
    *
    */
   private void createCompositeBottom()
   {
      GridLayout gridLayout3 = new GridLayout();
      gridLayout3.horizontalSpacing = 0;
      gridLayout3.marginWidth = 0;
      gridLayout3.verticalSpacing = 0;
      gridLayout3.marginHeight = 0;
      GridData gridData1 = new GridData();
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      compositeBottom = new Composite(sShell, SWT.BORDER);
      createCompositeButtons();
      compositeBottom.setLayout(gridLayout3);
      compositeBottom.setLayoutData(gridData1);
   }

   /**
    * This method initializes compositeButtons
    *
    */
   private void createCompositeButtons()
   {
      GridData gridData5 = new GridData();
      GridLayout gridLayout1 = new GridLayout();
      gridLayout1.numColumns = 2;
      GridData gridData4 = new GridData();
      gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
      gridData4.grabExcessHorizontalSpace = true;
      compositeButtons = new Composite(compositeBottom, SWT.NONE);
      compositeButtons.setLayoutData(gridData4);
      compositeButtons.setLayout(gridLayout1);
      buttonStart = new Button(compositeButtons, SWT.NONE);
      buttonStart.setText("Start");
      buttonStart.setLayoutData(gridData5);
      buttonStart.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            Generator generator = new Generator(textDir.getText());
            generator.start(getThis());
         }
      });
      buttonEnd = new Button(compositeButtons, SWT.NONE);
      buttonEnd.setText("Exit");
      buttonEnd.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            sShell.dispose();
         }
      });
   }

   /**
    * This method initializes sShell
    */
   public void createSShell()
   {
      GridLayout gridLayout2 = new GridLayout();
      gridLayout2.verticalSpacing = 0;
      gridLayout2.marginHeight = 0;
      gridLayout2.marginWidth = 0;
      gridLayout2.numColumns = 1;
      gridLayout2.horizontalSpacing = 0;
      sShell = new Shell();
      sShell.setImage(UtilSwt.getImage("schemagenerator/builderBig.png", sShell.getDisplay()));
      sShell.setText("Arctic Schema Generator " + Util.getVERSION());
      createCompositeTop();
      createTabFolder();
      sShell.setLayout(gridLayout2);
      createCompositeBottom();
      sShell.setSize(new Point(800, 500));
   }

   /**
    * This method initializes tabFolder
    *
    */
   private void createTabFolder()
   {
      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = GridData.FILL;
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.grabExcessVerticalSpace = true;
      gridData2.verticalAlignment = GridData.FILL;
      mainTabFolder = new TabFolder(sShell, SWT.MULTI);
      mainTabFolder.setLayoutData(gridData2);
      createAdditionalTabFolder("Common");
   }

   public void createAdditionalTabFolder(String psText)
   {
      TabItem tabItem2 = new TabItem(mainTabFolder, SWT.NONE);
      tabItem2.setText(psText);

      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = GridData.FILL;
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.grabExcessVerticalSpace = true;
      gridData2.verticalAlignment = GridData.FILL;
      TabFolder tabFolder = new TabFolder(mainTabFolder, SWT.MULTI);
      tabFolder.setLayoutData(gridData2);
      lstTabFolders.add(tabFolder);

      tabItem2.setControl(tabFolder);
   }

   public void loadValuesFrom(XmlObject settings)
   {
      String sBaseDir = settings.getAttribute("baseDir");
      textDir.setText(sBaseDir);
      for (int j = 0; j < lstTabFolders.size(); j++) {
         Control[] tabList = lstTabFolders.get(j).getChildren();
         for (Control element : tabList) {
            if (element instanceof SwtBase) {
               ((SwtBase) element).setBaseDir(sBaseDir);
               ((SwtBase) element).loadValuesFrom(settings);
            }
         }

      }
   }

   public void saveValuesTo(XmlObject settings)
   {
      settings.setAttribute("baseDir", textDir.getText());
      for (int j = 0; j < lstTabFolders.size(); j++) {
         Control[] tabList = lstTabFolders.get(j).getChildren();
         for (Control element : tabList) {
            if (element instanceof SwtBase) {
               ((SwtBase) element).saveValuesTo(settings);
            }
         }
      }

   }

   public void start(Generator pGenerator)
   {
      try {
         TabItem[] selection = mainTabFolder.getSelection();
         TabFolder tabFolder = (TabFolder) selection[0].getControl();
         SwtBase control = (SwtBase) tabFolder.getSelection()[0].getControl();
         control.start(pGenerator);
         control.setActive(this);
      }
      catch (Exception ex) {
         // avoid nullpointer and other exceptions
         ex.printStackTrace();
         setActive(true);
      }


   }

   public void setActive(final boolean b)
   {
      sShell.getDisplay().syncExec(new Runnable()
      {
         @Override
         public void run()
         {
            textDir.setEnabled(b);
            for (int j = 0; j < lstTabFolders.size(); j++) {
               lstTabFolders.get(j).setEnabled(b);
               buttonStart.setEnabled(b);
               Control[] tabList = lstTabFolders.get(j).getChildren();
               for (Control element : tabList) {
                  if (element instanceof SwtBase) {
                     ((SwtBase) element).setActive(b);
                  }

               }
            }
         }
      });
   }

   public TabFolder tabFolder()
   {
      return lstTabFolders.get(lstTabFolders.size() - 1);
   }
}
