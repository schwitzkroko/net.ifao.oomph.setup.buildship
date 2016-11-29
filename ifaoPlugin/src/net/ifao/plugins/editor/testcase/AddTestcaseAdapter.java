package net.ifao.plugins.editor.testcase;


import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.*;
import java.util.Hashtable;
import org.eclipse.jface.resource.*;


/** 
 * The class AddTestcaseAdapter was automatically genereated with Xml2Swt
 * <p>
 * Date: 28.November 2006<br>
 * sourceXml:
 * <pre>
 *   <font color='blue'>&lt;<font color='#983000'>SWT</font> <font color='#983000'>xmlns:xsi</font><font color='blue'>="</font><font color='black'>http://www.w3.org/2001/XMLSchema-instance</font><font color='blue'>"</font> <font color='#983000'>xsi:noNamespaceSchemaLocation</font><font color='blue'>="</font><font color='black'>..\..\Swt.xsd</font><font color='blue'>"</font> <font color='#983000'>package</font><font color='blue'>="</font><font color='black'>net.ifao.plugins.editor.testcase</font><font color='blue'>"</font>&gt;
 *     <font color='blue'>&lt;<font color='#983000'>Shell</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Add TestCase</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *       <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *         <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *           <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Create a new Testcase</font><font color='blue'>"</font> <font color='#983000'>font</font><font color='blue'>="</font><font color='black'>HEADER_FONT</font><font color='blue'>"</font> /&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Select a new Template or create from existing one</font><font color='blue'>"</font> /&gt;</font>
 *         &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *         <font color='blue'>&lt;<font color='#983000'>Canvas</font> <font color='#983000'>image</font><font color='blue'>="</font><font color='black'>testcase_big.png</font><font color='blue'>"</font> /&gt;</font>
 *       &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *       <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_OUT</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_WIDGET_BACKGROUND</font><font color='blue'>"</font>&gt;
 *         <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_IN</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_WIDGET_LIGHT_SHADOW</font><font color='blue'>"</font>&gt;
 *           <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Name:</font><font color='blue'>"</font> /&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>TestCaseName</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>BORDER</font><font color='blue'>"</font> /&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Check</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Use from existing template:</font><font color='blue'>"</font> <font color='#983000'>checked</font><font color='blue'>="</font><font color='black'>false</font><font color='blue'>"</font> <font color='#983000'>colSpan</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>type</font><font color='blue'>="</font><font color='black'>CHECK</font><font color='blue'>"</font> /&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>List</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Templates</font><font color='blue'>"</font> <font color='#983000'>colSpan</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>BORDER</font><font color='blue'>"</font> <font color='#983000'>enabled</font><font color='blue'>="</font><font color='black'>false</font><font color='blue'>"</font> /&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Detail</font><font color='blue'>"</font> <font color='#983000'>colSpan</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_OUT</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_WIDGET_LIGHT_SHADOW</font><font color='blue'>"</font> /&gt;</font>
 *         &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *       &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *       <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_WIDGET_BACKGROUND</font><font color='blue'>"</font>&gt;
 *         <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *         <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font>&gt;
 *           <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Finish</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>OK</font><font color='blue'>"</font> <font color='#983000'>enabled</font><font color='blue'>="</font><font color='black'>false</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Cancel</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Cancel</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
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

public abstract class AddTestcaseAdapter
{
   private Shell sShell = null;
   private Class _abstractUIPlugin;

   /**
    * Constructor for AddTestcaseAdapter
    * 
    * @param pAbstractUIPlugin InterfaceClass for AbstractUIPlugin
    **/
   public AddTestcaseAdapter(Class pAbstractUIPlugin)
   {
      _abstractUIPlugin = pAbstractUIPlugin;
      sShell = new Shell(Display.getCurrent(), SWT.APPLICATION_MODAL | SWT.TITLE | SWT.CLOSE
            | SWT.RESIZE);
      sShell.setText("Add TestCase");
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
      Color backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
      sShell.setBackground(backgound);
      createComposite1(sShell, backgound);
      createComposite3(sShell, backgound);
      createComposite5(sShell, backgound);
   }

   /**
    * Method show displays this AddTestcase and waits
    * until finished.
    * 
    **/
   public void show()
   {
      Display display = Display.getDefault();
      sShell.pack();
      sShell.open();
      while (!sShell.isDisposed()) {
         if (!display.readAndDispatch())
            display.sleep();
      }
      sShell.dispose();

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
            descriptor = (ImageDescriptor) _abstractUIPlugin.getMethod("getImageDescriptor",
                  classes).invoke(_abstractUIPlugin, args);
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
      gridData.grabExcessVerticalSpace = false;
      gridData.verticalAlignment = GridData.BEGINNING;
      value.setLayoutData(gridData);
      // reset Background
      backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
      value.setBackground(backgound);
      createComposite2(value, backgound);
      createCanvas1(value, backgound);

      return value;
   } // finished createComposite1

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
      value.setLayout(gridLayout);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.verticalAlignment = GridData.FILL;
      value.setLayoutData(gridData);
      // reset Background
      backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
      value.setBackground(backgound);
      createLabel1(value, backgound);
      createLabel2(value, backgound);

      return value;
   } // finished createComposite2

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
      value.setText("Create a new Testcase");
      value.setFont(JFaceResources.getFontRegistry().get(JFaceResources.HEADER_FONT));
      value.setBackground(backgound);

      return value;
   } // finished createLabel1

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
      value.setText("Select a new Template or create from existing one");
      value.setBackground(backgound);

      return value;
   } // finished createLabel2

   /**
    * Method createCanvas1 creates a Canvas and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Canvas-object
    **/
   private Canvas createCanvas1(Composite parent, Color backgound)
   {
      Canvas value = new Canvas(parent, SWT.NONE);
      value.setBackgroundImage(getIcon("testcase_big.png"));
      value.setBackground(backgound);

      return value;
   } // finished createCanvas1

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
      Composite value = new Composite(parent, SWT.SHADOW_ETCHED_OUT | SWT.BORDER);
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
      // reset Background
      backgound = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
      value.setBackground(backgound);
      createComposite4(value, backgound);

      return value;
   } // finished createComposite3

   /**
    * Method createComposite4 creates a Composite and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Composite-object
    **/
   private Composite createComposite4(Composite parent, Color backgound)
   {
      Composite value = new Composite(parent, SWT.SHADOW_ETCHED_IN | SWT.BORDER);
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
      // reset Background
      backgound = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
      value.setBackground(backgound);
      createLabel3(value, backgound);
      createTextTestCaseName(value, backgound);
      createButtonCheck(value, backgound);
      createListTemplates(value, backgound);
      createTextDetail(value, backgound);

      return value;
   } // finished createComposite4

   /**
    * Method createLabel3 creates a Label and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Label-object
    **/
   private Label createLabel3(Composite parent, Color backgound)
   {
      Label value = new Label(parent, SWT.NONE);
      value.setText("Name:");
      value.setBackground(backgound);

      return value;
   } // finished createLabel3

   // +---+---+---+---+---+---+---+---+---+---+---+---+
   // | T | e | s | t | C | a | s | e | N | a | m | e |
   // +---+---+---+---+---+---+---+---+---+---+---+---+

   /**
    * Method initTextTestCaseName initalizes TestCaseName.
    * This method may/should be overwritten if neccessary.
    * 
    * @param pTestCaseName TestCaseName of type Text
    **/
   protected void initTextTestCaseName(Text pTestCaseName)
   {}

   private Text _TextTestCaseName = null; // private member

   /**
    * Method getText_TextTestCaseName returns the _TextTestCaseName-object
    * 
    * @return _TextTestCaseName-object of type Text
    **/
   public Text getTextTestCaseName()
   {
      return _TextTestCaseName;
   }

   /**
    * Method createTextTestCaseName creates a Text and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Text-object
    **/
   private Text createTextTestCaseName(Composite parent, Color backgound)
   {
      _TextTestCaseName = new Text(parent, SWT.BORDER);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = false;
      gridData.verticalAlignment = GridData.BEGINNING;
      _TextTestCaseName.setLayoutData(gridData);
      // call the Init method (which could be overridden
      initTextTestCaseName(_TextTestCaseName);

      // add a Key-Listener for this keyPressed
      _TextTestCaseName.addKeyListener(new org.eclipse.swt.events.KeyListener()
      {
         public void keyPressed(org.eclipse.swt.events.KeyEvent e)
         {
            keyPressedTextTestCaseName(e);
         }

         public void keyReleased(org.eclipse.swt.events.KeyEvent e)
         {}
      });

      // add a Modify-Listener for this modify
      _TextTestCaseName.addModifyListener(new org.eclipse.swt.events.ModifyListener()
      {
         public void modifyText(org.eclipse.swt.events.ModifyEvent e)
         {
            modifyTextTestCaseName(_TextTestCaseName);
         }
      });

      return _TextTestCaseName;
   } // finished createTextTestCaseName

   protected void keyPressedTextTestCaseName(org.eclipse.swt.events.KeyEvent e)
   {}

   protected abstract void modifyTextTestCaseName(Text pTextTestCaseName);

   // +---+---+---+---+---+
   // | C | h | e | c | k |
   // +---+---+---+---+---+

   /**
    * Method initButtonCheck initalizes Check.
    * This method may/should be overwritten if neccessary.
    * 
    * @param pCheck Check of type Button
    **/
   protected void initButtonCheck(Button pCheck)
   {}

   private Button _ButtonCheck = null; // private member

   /**
    * Method getButton_ButtonCheck returns the _ButtonCheck-object
    * 
    * @return _ButtonCheck-object of type Button
    **/
   public Button getButtonCheck()
   {
      return _ButtonCheck;
   }

   /**
    * Method createButtonCheck creates a Button and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Button-object
    **/
   private Button createButtonCheck(Composite parent, Color backgound)
   {
      _ButtonCheck = new Button(parent, SWT.NONE | SWT.CHECK);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.horizontalSpan = 2;
      _ButtonCheck.setLayoutData(gridData);
      _ButtonCheck.setText("Use from existing template:");
      _ButtonCheck.setBackground(backgound);
      // call the Init method (which could be overridden
      initButtonCheck(_ButtonCheck);

      // add a Key-Listener for this keyPressed
      _ButtonCheck.addKeyListener(new org.eclipse.swt.events.KeyListener()
      {
         public void keyPressed(org.eclipse.swt.events.KeyEvent e)
         {
            keyPressedButtonCheck(e);
         }

         public void keyReleased(org.eclipse.swt.events.KeyEvent e)
         {}
      });

      // add a Selection-Listener for this click
      _ButtonCheck.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            clickButtonCheck(_ButtonCheck);
         }

         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
         {}
      });

      return _ButtonCheck;
   } // finished createButtonCheck

   protected void keyPressedButtonCheck(org.eclipse.swt.events.KeyEvent e)
   {}

   protected abstract void clickButtonCheck(Button pButtonCheck);

   // +---+---+---+---+---+---+---+---+---+
   // | T | e | m | p | l | a | t | e | s |
   // +---+---+---+---+---+---+---+---+---+

   /**
    * Method initListTemplates initalizes Templates.
    * This method may/should be overwritten if neccessary.
    * 
    * @param pTemplates Templates of type List
    **/
   protected void initListTemplates(List pTemplates)
   {}

   private List _ListTemplates = null; // private member

   /**
    * Method getList_ListTemplates returns the _ListTemplates-object
    * 
    * @return _ListTemplates-object of type List
    **/
   public List getListTemplates()
   {
      return _ListTemplates;
   }

   /**
    * Method createListTemplates creates a List and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created List-object
    **/
   private List createListTemplates(Composite parent, Color backgound)
   {
      _ListTemplates = new List(parent, SWT.BORDER);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.verticalAlignment = GridData.FILL;
      gridData.horizontalSpan = 2;
      _ListTemplates.setLayoutData(gridData);
      _ListTemplates.setEnabled(false);
      // call the Init method (which could be overridden
      initListTemplates(_ListTemplates);

      // add a Key-Listener for this keyPressed
      _ListTemplates.addKeyListener(new org.eclipse.swt.events.KeyListener()
      {
         public void keyPressed(org.eclipse.swt.events.KeyEvent e)
         {
            keyPressedListTemplates(e);
         }

         public void keyReleased(org.eclipse.swt.events.KeyEvent e)
         {}
      });

      // add a Selection-Listener for this click
      _ListTemplates.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            clickListTemplates(_ListTemplates);
         }

         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
         {}
      });

      return _ListTemplates;
   } // finished createListTemplates

   protected void keyPressedListTemplates(org.eclipse.swt.events.KeyEvent e)
   {}

   protected abstract void clickListTemplates(List pListTemplates);

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
      _TextDetail = new Text(parent, SWT.SHADOW_ETCHED_OUT | SWT.BORDER | SWT.MULTI | SWT.V_SCROLL
            | SWT.H_SCROLL);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.verticalAlignment = GridData.FILL;
      gridData.minimumHeight = 60;
      gridData.horizontalSpan = 2;
      _TextDetail.setLayoutData(gridData);
      // reset Background
      backgound = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
      _TextDetail.setBackground(backgound);
      // call the Init method (which could be overridden
      initTextDetail(_TextDetail);

      // add a Key-Listener for this keyPressed
      _TextDetail.addKeyListener(new org.eclipse.swt.events.KeyListener()
      {
         public void keyPressed(org.eclipse.swt.events.KeyEvent e)
         {
            keyPressedTextDetail(e);
         }

         public void keyReleased(org.eclipse.swt.events.KeyEvent e)
         {}
      });

      // add a Modify-Listener for this modify
      _TextDetail.addModifyListener(new org.eclipse.swt.events.ModifyListener()
      {
         public void modifyText(org.eclipse.swt.events.ModifyEvent e)
         {
            modifyTextDetail(_TextDetail);
         }
      });

      return _TextDetail;
   } // finished createTextDetail

   protected void keyPressedTextDetail(org.eclipse.swt.events.KeyEvent e)
   {}

   protected abstract void modifyTextDetail(Text pTextDetail);

   /**
    * Method createComposite5 creates a Composite and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Composite-object
    **/
   private Composite createComposite5(Composite parent, Color backgound)
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
      gridData.grabExcessVerticalSpace = false;
      gridData.verticalAlignment = GridData.BEGINNING;
      value.setLayoutData(gridData);
      // reset Background
      backgound = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
      value.setBackground(backgound);
      createComposite6(value, backgound);
      createComposite7(value, backgound);

      return value;
   } // finished createComposite5

   /**
    * Method createComposite6 creates a Composite and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Composite-object
    **/
   private Composite createComposite6(Composite parent, Color backgound)
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
   } // finished createComposite6

   /**
    * Method createComposite7 creates a Composite and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Composite-object
    **/
   private Composite createComposite7(Composite parent, Color backgound)
   {
      Composite value = new Composite(parent, SWT.NONE);
      // create a GridLayout for the Composite
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      gridLayout.makeColumnsEqualWidth = true;
      value.setLayout(gridLayout);
      value.setBackground(backgound);
      createButtonFinish(value, backgound);
      createButtonCancel(value, backgound);

      return value;
   } // finished createComposite7

   // +---+---+---+---+---+---+
   // | F | i | n | i | s | h |
   // +---+---+---+---+---+---+

   /**
    * Method initButtonFinish initalizes Finish.
    * This method may/should be overwritten if neccessary.
    * 
    * @param pFinish Finish of type Button
    **/
   protected void initButtonFinish(Button pFinish)
   {}

   private Button _ButtonFinish = null; // private member

   /**
    * Method getButton_ButtonFinish returns the _ButtonFinish-object
    * 
    * @return _ButtonFinish-object of type Button
    **/
   public Button getButtonFinish()
   {
      return _ButtonFinish;
   }

   /**
    * Method createButtonFinish creates a Button and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Button-object
    **/
   private Button createButtonFinish(Composite parent, Color backgound)
   {
      _ButtonFinish = new Button(parent, SWT.NONE);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = false;
      gridData.verticalAlignment = GridData.BEGINNING;
      _ButtonFinish.setLayoutData(gridData);
      _ButtonFinish.setEnabled(false);
      _ButtonFinish.setText("OK");
      _ButtonFinish.setBackground(backgound);
      // call the Init method (which could be overridden
      initButtonFinish(_ButtonFinish);

      // add a Key-Listener for this keyPressed
      _ButtonFinish.addKeyListener(new org.eclipse.swt.events.KeyListener()
      {
         public void keyPressed(org.eclipse.swt.events.KeyEvent e)
         {
            keyPressedButtonFinish(e);
         }

         public void keyReleased(org.eclipse.swt.events.KeyEvent e)
         {}
      });

      // add a Selection-Listener for this click
      _ButtonFinish.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            clickButtonFinish(_ButtonFinish);
         }

         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
         {}
      });

      return _ButtonFinish;
   } // finished createButtonFinish

   protected void keyPressedButtonFinish(org.eclipse.swt.events.KeyEvent e)
   {}

   protected abstract void clickButtonFinish(Button pButtonFinish);

   // +---+---+---+---+---+---+
   // | C | a | n | c | e | l |
   // +---+---+---+---+---+---+

   /**
    * Method initButtonCancel initalizes Cancel.
    * This method may/should be overwritten if neccessary.
    * 
    * @param pCancel Cancel of type Button
    **/
   protected void initButtonCancel(Button pCancel)
   {}

   private Button _ButtonCancel = null; // private member

   /**
    * Method getButton_ButtonCancel returns the _ButtonCancel-object
    * 
    * @return _ButtonCancel-object of type Button
    **/
   public Button getButtonCancel()
   {
      return _ButtonCancel;
   }

   /**
    * Method createButtonCancel creates a Button and adds this
    * to the parent-object
    * 
    * @param parent The parent Composite
    * @param backgound The default background-color
    * @return The created Button-object
    **/
   private Button createButtonCancel(Composite parent, Color backgound)
   {
      _ButtonCancel = new Button(parent, SWT.NONE);
      // create GridData which will be used for the layout
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = false;
      gridData.verticalAlignment = GridData.BEGINNING;
      _ButtonCancel.setLayoutData(gridData);
      _ButtonCancel.setText("Cancel");
      _ButtonCancel.setBackground(backgound);
      // call the Init method (which could be overridden
      initButtonCancel(_ButtonCancel);

      // add a Key-Listener for this keyPressed
      _ButtonCancel.addKeyListener(new org.eclipse.swt.events.KeyListener()
      {
         public void keyPressed(org.eclipse.swt.events.KeyEvent e)
         {
            keyPressedButtonCancel(e);
         }

         public void keyReleased(org.eclipse.swt.events.KeyEvent e)
         {}
      });

      // add a Selection-Listener for this click
      _ButtonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
      {
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            clickButtonCancel(_ButtonCancel);
         }

         public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
         {}
      });

      return _ButtonCancel;
   } // finished createButtonCancel

   protected void keyPressedButtonCancel(org.eclipse.swt.events.KeyEvent e)
   {}

   protected abstract void clickButtonCancel(Button pButtonCancel);
}
