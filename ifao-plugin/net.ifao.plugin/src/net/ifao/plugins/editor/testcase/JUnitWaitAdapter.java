package net.ifao.plugins.editor.testcase;


import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import java.util.Hashtable;
import org.eclipse.jface.resource.*;


/**
 * The class JUnitWaitAdapter was automatically genereated with Xml2Swt
 * <p>
 * Date: 24.November 2006<br>
 * sourceXml:
 * <pre>
 *   <font color='blue'>&lt;<font color='#983000'>SWT</font> <font color='#983000'>xmlns:xsi</font><font color='blue'>="</font><font color='black'>http://www.w3.org/2001/XMLSchema-instance</font><font color='blue'>"</font> <font color='#983000'>xsi:noNamespaceSchemaLocation</font><font color='blue'>="</font><font color='black'>..\..\Swt.xsd</font><font color='blue'>"</font> <font color='#983000'>package</font><font color='blue'>="</font><font color='black'>net.ifao.plugins.editor.testcase</font><font color='blue'>"</font>&gt;
 *     <font color='blue'>&lt;<font color='#983000'>Shell</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Execute JUnit</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_WIDGET_BACKGROUND</font><font color='blue'>"</font> <font color='#983000'>modal</font><font color='blue'>="</font><font color='black'>false</font><font color='blue'>"</font>&gt;
 *       <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font>&gt;
 *         <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Package:</font><font color='blue'>"</font> /&gt;</font>
 *         <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Package</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>enabled</font><font color='blue'>="</font><font color='black'>false</font><font color='blue'>"</font> /&gt;</font>
 *         <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Testcase:</font><font color='blue'>"</font> /&gt;</font>
 *         <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Testcase</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>enabled</font><font color='blue'>="</font><font color='black'>false</font><font color='blue'>"</font> /&gt;</font>
 *         <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>colSpan</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Detail</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *         <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>colSpan</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>3</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font>&gt;
 *           <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>ExamDiff</font><font color='blue'>"</font> <font color='#983000'>enabled</font><font color='blue'>="</font><font color='black'>false</font><font color='blue'>"</font> <font color='#983000'>image</font><font color='blue'>="</font><font color='black'>examdiff.gif</font><font color='blue'>"</font> /&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>OK</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>OK</font><font color='blue'>"</font> <font color='#983000'>enabled</font><font color='blue'>="</font><font color='black'>false</font><font color='blue'>"</font> /&gt;</font>
 *         &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *       &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *     &lt;/<font color='#983000'>Shell</font>&gt;</font>
 *   &lt;/<font color='#983000'>SWT</font>&gt;</font>
 * 
 * </pre>
 * <p>
 * Copyright &copy; 2006, i:FAO
 * 
 * @author generator
 */

public abstract class JUnitWaitAdapter
{
   protected Shell sShell = null;
   private Class _abstractUIPlugin;

   /**
    * Constructor for JUnitWaitAdapter
    * 
    * @param pAbstractUIPlugin InterfaceClass for AbstractUIPlugin
    **/
   public JUnitWaitAdapter(Class pAbstractUIPlugin)
   {
      _abstractUIPlugin = pAbstractUIPlugin;
      sShell =
         new Shell(Display.getCurrent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.TITLE
               | SWT.CLOSE | SWT.RESIZE);
      sShell.setText("Execute JUnit");
      GridLayout gridLayout = new GridLayout();
      gridLayout.horizontalSpacing = 0;
      gridLayout.marginWidth = 0;
      gridLayout.marginHeight = 0;
      gridLayout.verticalSpacing = 0;
      sShell.setLayout(gridLayout);
   }

   /**
    * Method initAdapter should be called within child constructor and
    * initalizes the GUI with it's componenets
    * 
    **/
   protected void initAdapter()
   {
      Color backgound = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
      sShell.setBackground(backgound);
      createComposite1(sShell, backgound);
   }

   /**
    * Method show displays this JUnitWait and waits
    * until finished.
    * 
    **/
   public void show()
   {
      Display display = Display.getDefault();
      sShell.pack();
      sShell.open();

   }

   /**
    * Method close, closes this shell.
    * 
    **/
   public void close()
   {
      if (!sShell.isDisposed()) {
         sShell.close();
         sShell.dispose();
      }
   }

   // get hashtable for Images
   private Hashtable<String, Image> htImages = new Hashtable<String, Image>();

   /**
    * Method getIcon returns an image, which has to be located
    * whithin the /icons/ directory
    * 
    * @param sName The name of the Icon
    * @return The related Image
    * 
    **/
   public Image getIcon(String sName)
   {
      Image image = htImages.get(sName);
      if (image == null) {
         Class[] classes = { String.class };
         Object[] args = { "/icons/" + sName };
         ImageDescriptor descriptor;
         try {
            descriptor =
               (ImageDescriptor) _abstractUIPlugin.getMethod("getImageDescriptor", classes).invoke(
                     _abstractUIPlugin, args);
         }
         catch (Exception e) {
            descriptor = null;
         }
         if (descriptor == null) {
            image = new Image(sShell.getDisplay(), "icons/" + sName);
         } else {
            image = descriptor.createImage();
         }
         htImages.put(sName, image);
      }
      return image;
   }

   /**
    * Method createComposite1 creates a Composite and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Composite-object
    **/
   private Composite createComposite1(Composite parent, Color backgound)
   {
      Composite value = new Composite(parent, SWT.NONE);
      // create a GridLayout for the Composite
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      value.setLayout(gridLayout);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.verticalAlignment = GridData.FILL;
      value.setLayoutData(gridData);
      value.setBackground(backgound);
      createLabel1(value, backgound);
      createTextPackage(value, backgound);
      createLabel2(value, backgound);
      createTextTestcase(value, backgound);
      createTextDetail(value, backgound);
      createComposite2(value, backgound);

      return value;
   } // finished createComposite1

   /**
    * Method createLabel1 creates a Label and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Label-object
    **/
   private Label createLabel1(Composite parent, Color backgound)
   {
      Label value = new Label(parent, SWT.NONE);
      value.setText("Package:");
      value.setBackground(backgound);

      return value;
   } // finished createLabel1

   // +---+---+---+---+---+---+---+
   // | P | a | c | k | a | g | e |
   // +---+---+---+---+---+---+---+

   /**
    * Method initTextPackage initalizes Package.
    * This method may/should be overwritten if neccessary.
    * 
    * @param pPackage Package of type Text
    **/
   protected void initTextPackage(Text pPackage)
   {}

   private Text _TextPackage = null; // private member

   /**
    * Method getText_TextPackage returns the _TextPackage-object
    * 
    * @return _TextPackage-object of type Text
    **/
   public Text getTextPackage()
   {
      return _TextPackage;
   }

   /**
    * Method createTextPackage creates a Text and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Text-object
    **/
   private Text createTextPackage(Composite parent, Color backgound)
   {
      _TextPackage = new Text(parent, SWT.BORDER);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = false;
      gridData.verticalAlignment = GridData.BEGINNING;
      _TextPackage.setLayoutData(gridData);
      _TextPackage.setEnabled(false);
      // call the Init method (which could be overridden
      initTextPackage(_TextPackage);

      // add a Modify-Listener for this modify
      _TextPackage.addModifyListener(new org.eclipse.swt.events.ModifyListener()
      {
         @Override
         public void modifyText(org.eclipse.swt.events.ModifyEvent e)
         {
            modifyTextPackage(_TextPackage);
         }
      });

      return _TextPackage;
   } // finished createTextPackage

   protected abstract void modifyTextPackage(Text pTextPackage);

   /**
    * Method createLabel2 creates a Label and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Label-object
    **/
   private Label createLabel2(Composite parent, Color backgound)
   {
      Label value = new Label(parent, SWT.NONE);
      value.setText("Testcase:");
      value.setBackground(backgound);

      return value;
   } // finished createLabel2

   // +---+---+---+---+---+---+---+---+
   // | T | e | s | t | c | a | s | e |
   // +---+---+---+---+---+---+---+---+

   /**
    * Method initTextTestcase initalizes Testcase.
    * This method may/should be overwritten if neccessary.
    * 
    * @param pTestcase Testcase of type Text
    **/
   protected void initTextTestcase(Text pTestcase)
   {}

   private Text _TextTestcase = null; // private member

   /**
    * Method getText_TextTestcase returns the _TextTestcase-object
    * 
    * @return _TextTestcase-object of type Text
    **/
   public Text getTextTestcase()
   {
      return _TextTestcase;
   }

   /**
    * Method createTextTestcase creates a Text and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Text-object
    **/
   private Text createTextTestcase(Composite parent, Color backgound)
   {
      _TextTestcase = new Text(parent, SWT.BORDER);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = false;
      gridData.verticalAlignment = GridData.BEGINNING;
      _TextTestcase.setLayoutData(gridData);
      _TextTestcase.setEnabled(false);
      // call the Init method (which could be overridden
      initTextTestcase(_TextTestcase);

      // add a Modify-Listener for this modify
      _TextTestcase.addModifyListener(new org.eclipse.swt.events.ModifyListener()
      {
         @Override
         public void modifyText(org.eclipse.swt.events.ModifyEvent e)
         {
            modifyTextTestcase(_TextTestcase);
         }
      });

      return _TextTestcase;
   } // finished createTextTestcase

   protected abstract void modifyTextTestcase(Text pTextTestcase);

   // +---+---+---+---+---+---+
   // | D | e | t | a | i | l |
   // +---+---+---+---+---+---+

   /**
    * Method initTextDetail initalizes Detail.
    * This method may/should be overwritten if neccessary.
    * 
    * @param pDetail Detail of type Text
    **/
   protected void initTextDetail(Text pDetail)
   {}

   private Text _TextDetail = null; // private member

   /**
    * Method getText_TextDetail returns the _TextDetail-object
    * 
    * @return _TextDetail-object of type Text
    **/
   public Text getTextDetail()
   {
      return _TextDetail;
   }

   /**
    * Method createTextDetail creates a Text and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Text-object
    **/
   private Text createTextDetail(Composite parent, Color backgound)
   {
      _TextDetail = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.verticalAlignment = GridData.FILL;
      gridData.minimumHeight = 60;
      gridData.horizontalSpan = 2;
      _TextDetail.setLayoutData(gridData);
      // call the Init method (which could be overridden
      initTextDetail(_TextDetail);

      // add a Modify-Listener for this modify
      _TextDetail.addModifyListener(new org.eclipse.swt.events.ModifyListener()
      {
         @Override
         public void modifyText(org.eclipse.swt.events.ModifyEvent e)
         {
            modifyTextDetail(_TextDetail);
         }
      });

      return _TextDetail;
   } // finished createTextDetail

   protected abstract void modifyTextDetail(Text pTextDetail);

   /**
    * Method createComposite2 creates a Composite and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Composite-object
    **/
   private Composite createComposite2(Composite parent, Color backgound)
   {
      Composite value = new Composite(parent, SWT.NONE);
      // create a GridLayout for the Composite
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
      value.setLayout(gridLayout);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = false;
      gridData.verticalAlignment = GridData.BEGINNING;
      gridData.horizontalSpan = 2;
      value.setLayoutData(gridData);
      value.setBackground(backgound);
      createButtonExamDiff(value, backgound);
      createComposite3(value, backgound);
      createButtonOK(value, backgound);

      return value;
   } // finished createComposite2

   // +---+---+---+---+---+---+---+---+
   // | E | x | a | m | D | i | f | f |
   // +---+---+---+---+---+---+---+---+

   /**
    * Method initButtonExamDiff initalizes ExamDiff.
    * This method may/should be overwritten if neccessary.
    * 
    * @param pExamDiff ExamDiff of type Button
    **/
   protected void initButtonExamDiff(Button pExamDiff)
   {}

   private Button _ButtonExamDiff = null; // private member

   /**
    * Method getButton_ButtonExamDiff returns the _ButtonExamDiff-object
    * 
    * @return _ButtonExamDiff-object of type Button
    **/
   public Button getButtonExamDiff()
   {
      return _ButtonExamDiff;
   }

   /**
    * Method createButtonExamDiff creates a Button and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Button-object
    **/
   private Button createButtonExamDiff(Composite parent, Color backgound)
   {
      _ButtonExamDiff = new Button(parent, SWT.NONE);
      _ButtonExamDiff.setEnabled(false);
      _ButtonExamDiff.setImage(getIcon("examdiff.gif"));
      _ButtonExamDiff.setBackground(backgound);
      // call the Init method (which could be overridden
      initButtonExamDiff(_ButtonExamDiff);

      // add a Selection-Listener for this click
      _ButtonExamDiff.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            clickButtonExamDiff(_ButtonExamDiff);
         }

         @Override
         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
         {}
      });

      return _ButtonExamDiff;
   } // finished createButtonExamDiff

   protected abstract void clickButtonExamDiff(Button pButtonExamDiff);

   /**
    * Method createComposite3 creates a Composite and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Composite-object
    **/
   private Composite createComposite3(Composite parent, Color backgound)
   {
      Composite value = new Composite(parent, SWT.NONE);
      // create a GridLayout for the Composite
      GridLayout gridLayout = new GridLayout();
      value.setLayout(gridLayout);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.verticalAlignment = GridData.FILL;
      value.setLayoutData(gridData);
      value.setBackground(backgound);

      return value;
   } // finished createComposite3

   // +---+---+
   // | O | K |
   // +---+---+

   /**
    * Method initButtonOK initalizes OK.
    * This method may/should be overwritten if neccessary.
    * 
    * @param pOK OK of type Button
    **/
   protected void initButtonOK(Button pOK)
   {}

   private Button _ButtonOK = null; // private member

   /**
    * Method getButton_ButtonOK returns the _ButtonOK-object
    * 
    * @return _ButtonOK-object of type Button
    **/
   public Button getButtonOK()
   {
      return _ButtonOK;
   }

   /**
    * Method createButtonOK creates a Button and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Button-object
    **/
   private Button createButtonOK(Composite parent, Color backgound)
   {
      _ButtonOK = new Button(parent, SWT.NONE);
      _ButtonOK.setEnabled(false);
      _ButtonOK.setText("OK");
      _ButtonOK.setBackground(backgound);
      // call the Init method (which could be overridden
      initButtonOK(_ButtonOK);

      // add a Selection-Listener for this click
      _ButtonOK.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            clickButtonOK(_ButtonOK);
         }

         @Override
         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
         {}
      });

      return _ButtonOK;
   } // finished createButtonOK

   protected abstract void clickButtonOK(Button pButtonOK);
}
