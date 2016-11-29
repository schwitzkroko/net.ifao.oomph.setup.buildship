package net.ifao.tools.dtdinfo.swt;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;


public abstract class DtdInfoFrame
{

   protected Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"
   private Menu menuBar = null;
   private Menu submenuOpen = null;
   protected SashForm sashForm = null;
   protected SashForm sashForm2 = null;
   private Composite composite = null;
   private Label labelAgent = null;
   private Label labelProvider = null;
   private TabFolder tabFolder = null;
   protected Tree treeRequest = null;
   protected Tree treeResponse = null;
   private Composite composite3 = null;
   protected Button checkBoxMandatory = null;
   protected Label labelItem = null;
   protected Label labelItemComment = null;
   private Label labelPnrElement = null;
   protected Combo comboPnrElement = null;
   protected Combo comboPnrAttribute = null;
   private Button buttonUse = null;
   protected Button checkBoxSelfDefined = null;
   protected Text textAreaPnr = null;
   private Button buttonDetail = null;
   private Composite composite1 = null;
   private Label labelTransformRules = null;
   protected Text textAreaTransformRules = null;
   private Composite composite2 = null;
   private Label labelProviderRef = null;
   protected Text textAreaProviderRef = null;
   private Label labelDateUser = null;
   protected Text textDateUser = null;
   private Button buttonUsedPnrElements = null;
   private Button buttonNotSupported = null;
   private Button buttonAdditionalDocument = null;
   protected Text textProvider = null;
   protected Text textAgent = null;
   private SashForm sashForm1 = null;
   private Composite composite4 = null;
   private Label labelSourceCode = null;
   private SashForm sashForm3 = null;
   private Composite compositeOldSourceCode = null;
   private Composite composite6 = null;
   private Label labelOldCode = null;
   private Label labelNewCode = null;
   protected Text textAreaOldCode = null;
   protected Text textAreaNewCode = null;
   private Button checkBoxViewOldSourceCode = null;

   /**
    * This method initializes sashForm	
    *
    */
   private void createSashForm()
   {
      GridData gridData = new GridData();
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData.grabExcessHorizontalSpace = true;
      gridData.grabExcessVerticalSpace = true;
      gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      sashForm = new SashForm(sShell, SWT.NONE);
      sashForm.setLayoutData(gridData);
      createComposite();
      createSashForm2();
   }

   /**
    * This method initializes sashForm2	
    *
    */
   private void createSashForm2()
   {
      sashForm2 = new SashForm(sashForm, SWT.NONE);
      sashForm2.setOrientation(SWT.VERTICAL);
      createComposite3();
      createSashForm1();
      createComposite4();
   }

   /**
    * This method initializes composite	
    *
    */
   private void createComposite()
   {
      GridData gridData3 = new GridData();
      gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData3.grabExcessHorizontalSpace = true;
      GridData gridData2 = new GridData();
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData20 = new GridData();
      gridData20.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      composite = new Composite(sashForm, SWT.BORDER);
      composite.setLayout(gridLayout);
      labelProvider = new Label(composite, SWT.NONE);
      labelProvider.setText("Provider");
      textProvider = new Text(composite, SWT.BORDER);
      textProvider.setEditable(false);
      textProvider.setLayoutData(gridData2);
      labelAgent = new Label(composite, SWT.NONE);
      labelAgent.setText("Agent");
      textAgent = new Text(composite, SWT.BORDER);
      textAgent.setEditable(false);
      textAgent.setLayoutData(gridData3);
      createTabFolder();
      Label filler2 = new Label(composite, SWT.NONE);
      Label filler1 = new Label(composite, SWT.NONE);
      buttonUsedPnrElements = new Button(composite, SWT.NONE);
      buttonUsedPnrElements.setText("Used Pnr Elements");
      buttonUsedPnrElements.setLayoutData(gridData20);
   }

   /**
    * This method initializes tabFolder	
    *
    */
   private void createTabFolder()
   {
      GridData gridData1 = new GridData();
      gridData1.horizontalSpan = 2;
      gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData1.grabExcessHorizontalSpace = true;
      gridData1.grabExcessVerticalSpace = true;
      gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      tabFolder = new TabFolder(composite, SWT.NONE);
      tabFolder.setLayoutData(gridData1);
      TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
      tabItem.setText("Request");
      TabItem tabItem1 = new TabItem(tabFolder, SWT.NONE);
      tabItem1.setText("Response");
      treeRequest = new Tree(tabFolder, SWT.NONE);
      treeRequest.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
      treeResponse = new Tree(tabFolder, SWT.NONE);
      treeResponse.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

      tabItem1.setControl(treeResponse);
      tabItem.setControl(treeRequest);
   }

   /**
    * This method initializes composite3	
    *
    */
   private void createComposite3()
   {
      GridData gridData13 = new GridData();
      gridData13.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData12 = new GridData();
      gridData12.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData9 = new GridData();
      gridData9.horizontalSpan = 2;
      gridData9.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData9.grabExcessHorizontalSpace = true;
      gridData9.grabExcessVerticalSpace = true;
      gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData8 = new GridData();
      gridData8.horizontalSpan = 3;
      gridData8.grabExcessHorizontalSpace = true;
      gridData8.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData7 = new GridData();
      gridData7.horizontalSpan = 3;
      gridData7.grabExcessHorizontalSpace = true;
      gridData7.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData6 = new GridData();
      gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData6.horizontalSpan = 3;
      gridData6.grabExcessHorizontalSpace = true;
      GridData gridData5 = new GridData();
      gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData5.horizontalSpan = 3;
      gridData5.grabExcessHorizontalSpace = true;
      GridData gridData4 = new GridData();
      gridData4.grabExcessHorizontalSpace = true;
      gridData4.horizontalSpan = 3;
      gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout1 = new GridLayout();
      gridLayout1.numColumns = 3;
      gridLayout1.makeColumnsEqualWidth = false;
      composite3 = new Composite(sashForm2, SWT.BORDER);
      composite3.setLayout(gridLayout1);
      labelItem = new Label(composite3, SWT.NONE);
      labelItem.setText("");
      labelItem.setBackground(Display.getCurrent().getSystemColor(
            SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
      labelItem.setLayoutData(gridData4);
      labelItemComment = new Label(composite3, SWT.NONE);
      labelItemComment.setText("");
      labelItemComment
            .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      labelItemComment.setLayoutData(gridData5);
      checkBoxMandatory = new Button(composite3, SWT.CHECK);
      checkBoxMandatory.setText("Mandatory ?");
      checkBoxMandatory.setLayoutData(gridData6);
      checkBoxMandatory.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            textChanged();
         }
      });
      labelPnrElement = new Label(composite3, SWT.CENTER | SWT.BORDER);
      labelPnrElement.setText("PnrElement");
      labelPnrElement.setLayoutData(gridData7);
      createComboPnrElement();
      createComboPnrAttribute();
      buttonUse = new Button(composite3, SWT.NONE);
      buttonUse.setText("Use");
      buttonUse.setLayoutData(gridData12);
      buttonUse.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            String sNew = comboPnrElement.getText() + "." + comboPnrAttribute.getText();
            boolean bUse = sNew.contains("[");
            sNew = sNew.replaceAll("[\\[\\] ]", "");
            textAreaPnr.setText(sNew);
            textChanged();
            if (bUse) {
               showDetail();
            }
         }
      });
      checkBoxSelfDefined = new Button(composite3, SWT.CHECK);
      checkBoxSelfDefined.setText("Create self defined method");
      checkBoxSelfDefined.setLayoutData(gridData8);
      checkBoxSelfDefined.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            textChanged();
         }
      });
      textAreaPnr = new Text(composite3, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
      textAreaPnr.setLayoutData(gridData9);
      textAreaPnr.addModifyListener(new org.eclipse.swt.events.ModifyListener()
      {
         public void modifyText(org.eclipse.swt.events.ModifyEvent e)
         {
            textChanged();
         }
      });
      buttonDetail = new Button(composite3, SWT.NONE);
      buttonDetail.setText("Detail ...");
      buttonDetail.setLayoutData(gridData13);
      buttonDetail.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            showDetail();
         }
      });
   }

   protected abstract void showDetail();

   abstract void textChanged();

   /**
    * This method initializes comboPnrElement	
    *
    */
   private void createComboPnrElement()
   {
      GridData gridData10 = new GridData();
      gridData10.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData10.grabExcessHorizontalSpace = true;
      comboPnrElement = new Combo(composite3, SWT.NONE);
      comboPnrElement.setLayoutData(gridData10);
      comboPnrElement.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            updateCombo();
         }

         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
         {}
      });
   }


   /**
    * This method initializes comboPnrAttribute	
    *
    */
   private void createComboPnrAttribute()
   {
      GridData gridData11 = new GridData();
      gridData11.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData11.grabExcessHorizontalSpace = true;
      comboPnrAttribute = new Combo(composite3, SWT.NONE);
      comboPnrAttribute.setLayoutData(gridData11);
   }

   /**
    * This method initializes composite1	
    *
    */
   private void createComposite1()
   {
      GridData gridData15 = new GridData();
      gridData15.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData15.grabExcessHorizontalSpace = true;
      gridData15.grabExcessVerticalSpace = true;
      gridData15.horizontalSpan = 3;
      gridData15.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData14 = new GridData();
      gridData14.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData14.grabExcessHorizontalSpace = true;
      GridLayout gridLayout2 = new GridLayout();
      gridLayout2.numColumns = 4;
      composite1 = new Composite(sashForm1, SWT.BORDER);
      composite1.setLayout(gridLayout2);
      buttonNotSupported = new Button(composite1, SWT.NONE);
      buttonNotSupported.setText("not supported");
      buttonNotSupported.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            textAreaTransformRules.setText("not supported");
         }
      });
      labelTransformRules = new Label(composite1, SWT.CENTER | SWT.BORDER);
      labelTransformRules.setText("TransformRules");
      labelTransformRules.setLayoutData(gridData14);
      buttonAdditionalDocument = new Button(composite1, SWT.NONE);
      buttonAdditionalDocument.setText("Additional document");
      buttonAdditionalDocument.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            String sText =
               textAreaTransformRules.getText() + "<a href=\"doc/ReservationChange_"
                     + labelItem.getText() + ".html\">Additional Document for "
                     + labelItem.getText() + "</a>";
            textAreaTransformRules.setText(sText);
         }
      });
      Label filler4 = new Label(composite1, SWT.NONE);
      textAreaTransformRules =
         new Text(composite1, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
      textAreaTransformRules.setLayoutData(gridData15);
      textAreaTransformRules.addModifyListener(new org.eclipse.swt.events.ModifyListener()
      {
         public void modifyText(org.eclipse.swt.events.ModifyEvent e)
         {
            textChanged();
            buttonNotSupported.setEnabled(textAreaTransformRules.getText().length() == 0);
         }
      });
   }

   /**
    * This method initializes composite2	
    *
    */
   private void createComposite2()
   {
      GridData gridData19 = new GridData();
      gridData19.grabExcessHorizontalSpace = true;
      gridData19.horizontalSpan = 3;
      gridData19.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData18 = new GridData();
      gridData18.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData18.horizontalSpan = 3;
      GridLayout gridLayout3 = new GridLayout();
      gridLayout3.numColumns = 3;
      GridData gridData17 = new GridData();
      gridData17.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData17.grabExcessHorizontalSpace = true;
      gridData17.grabExcessVerticalSpace = true;
      gridData17.horizontalSpan = 3;
      gridData17.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridData gridData16 = new GridData();
      gridData16.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData16.grabExcessHorizontalSpace = true;
      composite2 = new Composite(sashForm1, SWT.BORDER);
      composite2.setLayout(gridLayout3);
      Label filler = new Label(composite2, SWT.NONE);
      labelProviderRef = new Label(composite2, SWT.CENTER | SWT.BORDER);
      labelProviderRef.setText("ProviderRef");
      labelProviderRef.setLayoutData(gridData16);
      Label filler3 = new Label(composite2, SWT.NONE);
      textAreaProviderRef = new Text(composite2, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
      textAreaProviderRef.setLayoutData(gridData17);
      textAreaProviderRef.addModifyListener(new org.eclipse.swt.events.ModifyListener()
      {
         public void modifyText(org.eclipse.swt.events.ModifyEvent e)
         {
            textChanged();
         }
      });
      labelDateUser = new Label(composite2, SWT.CENTER | SWT.BORDER);
      labelDateUser.setText("DateUser");
      labelDateUser.setLayoutData(gridData18);
      textDateUser = new Text(composite2, SWT.BORDER);
      textDateUser.setEditable(false);
      textDateUser.setLayoutData(gridData19);
   }


   /**
    * This method initializes sShell
    */
   void createSShell()
   {
      GridLayout gridLayout4 = new GridLayout();
      gridLayout4.numColumns = 1;
      sShell = new Shell();
      sShell.setText("Shell");
      createSashForm();
      sShell.setLayout(gridLayout4);
      sShell.setSize(new Point(800, 600));
      menuBar = new Menu(sShell, SWT.BAR);
      MenuItem submenuItemFile = new MenuItem(menuBar, SWT.CASCADE);
      submenuItemFile.setText("File");
      submenuOpen = new Menu(submenuItemFile);
      MenuItem pushOpen = new MenuItem(submenuOpen, SWT.PUSH);
      pushOpen.setText("Open");
      pushOpen.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            menuOpen();
         }

         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
         {}
      });
      MenuItem separator3 = new MenuItem(submenuOpen, SWT.SEPARATOR);
      MenuItem pushSave = new MenuItem(submenuOpen, SWT.PUSH);
      pushSave.setText("Save");
      pushSave.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            menuSave();
         }

         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
         {}
      });
      MenuItem pushClose = new MenuItem(submenuOpen, SWT.PUSH);
      pushClose.setText("Close");
      pushClose.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            menuClose();
         }

         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
         {}
      });
      MenuItem separator1 = new MenuItem(submenuOpen, SWT.SEPARATOR);
      MenuItem pushProperties = new MenuItem(submenuOpen, SWT.PUSH);
      pushProperties.setText("Properties ...");
      pushProperties.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            menuProperties();
         }

         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
         {}
      });
      MenuItem separator2 = new MenuItem(submenuOpen, SWT.SEPARATOR);
      MenuItem pushExist = new MenuItem(submenuOpen, SWT.PUSH);
      pushExist.setText("Exit");
      pushExist.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            exit();
         }

         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
         {}
      });
      submenuItemFile.setMenu(submenuOpen);
      sShell.setMenuBar(menuBar);
      sShell.addShellListener(new org.eclipse.swt.events.ShellAdapter()
      {
         @Override
         public void shellClosed(org.eclipse.swt.events.ShellEvent e)
         {
            exit();
         }
      });
   }

   protected abstract void menuProperties();

   protected abstract void menuOpen();

   protected abstract void menuClose();

   abstract void menuSave();

   abstract void exit();

   abstract void updateCombo();

   /**
    * This method initializes sashForm1	
    *
    */
   private void createSashForm1()
   {
      sashForm1 = new SashForm(sashForm2, SWT.NONE);
      createComposite1();
      createComposite2();
   }

   /**
    * This method initializes composite4	
    *
    */
   private void createComposite4()
   {
      GridData gridData27 = new GridData();
      GridLayout gridLayout6 = new GridLayout();
      gridLayout6.numColumns = 1;
      GridData gridData24 = new GridData();
      gridData24.grabExcessHorizontalSpace = true;
      gridData24.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      composite4 = new Composite(sashForm2, SWT.BORDER);
      labelSourceCode = new Label(composite4, SWT.CENTER | SWT.BORDER);
      labelSourceCode.setText("Generated sourceCode");
      labelSourceCode.setLayoutData(gridData24);
      createSashForm3();
      composite4.setLayout(gridLayout6);
      checkBoxViewOldSourceCode = new Button(composite4, SWT.CHECK);
      checkBoxViewOldSourceCode.setText("View old Sourcode");
      checkBoxViewOldSourceCode.setLayoutData(gridData27);
      checkBoxViewOldSourceCode.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            compositeOldSourceCode.setVisible(checkBoxViewOldSourceCode.getSelection());
            // sashForm3.redraw();
            sashForm3.layout();
            
         }
      });
   }

   /**
    * This method initializes sashForm3	
    *
    */
   private void createSashForm3()
   {
      GridData gridData23 = new GridData();
      gridData23.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData23.grabExcessHorizontalSpace = true;
      gridData23.grabExcessVerticalSpace = true;
      gridData23.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      sashForm3 = new SashForm(composite4, SWT.NONE);
      createCompositeOldSourceCode();
      sashForm3.setLayoutData(gridData23);
      createComposite6();
   }

   /**
    * This method initializes compositeOldSourceCode	
    *
    */
   private void createCompositeOldSourceCode()
   {
      GridData gridData25 = new GridData();
      gridData25.horizontalAlignment = org.eclipse.swt.layout.GridData.CENTER;
      GridData gridData22 = new GridData();
      gridData22.grabExcessHorizontalSpace = true;
      gridData22.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData22.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData22.grabExcessVerticalSpace = true;
      GridLayout gridLayout5 = new GridLayout();
      gridLayout5.numColumns = 1;
      compositeOldSourceCode = new Composite(sashForm3, SWT.BORDER);
      compositeOldSourceCode.setVisible(false);
      compositeOldSourceCode.setLayout(gridLayout5);
      labelOldCode = new Label(compositeOldSourceCode, SWT.NONE);
      labelOldCode.setText("old SourceCode");
      labelOldCode.setLayoutData(gridData25);
      textAreaOldCode =
         new Text(compositeOldSourceCode, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
      textAreaOldCode.setLayoutData(gridData22);
   }

   /**
    * This method initializes composite6	
    *
    */
   private void createComposite6()
   {
      GridData gridData26 = new GridData();
      gridData26.horizontalAlignment = org.eclipse.swt.layout.GridData.CENTER;
      GridData gridData21 = new GridData();
      gridData21.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData21.grabExcessHorizontalSpace = true;
      gridData21.grabExcessVerticalSpace = true;
      gridData21.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      composite6 = new Composite(sashForm3, SWT.BORDER);
      composite6.setLayout(new GridLayout());
      labelNewCode = new Label(composite6, SWT.NONE);
      labelNewCode.setText("new SourceCode");
      labelNewCode.setLayoutData(gridData26);
      textAreaNewCode = new Text(composite6, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
      textAreaNewCode.setLayoutData(gridData21);
   }

}
