package net.ifao.plugins.dialog;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.*;
import java.util.Hashtable;
import org.eclipse.jface.resource.*;

/** 
 * The class InputTextAdapter was automatically genereated with Xml2Swt
 * <p>
 * Date: 09.November 2006<br>
 * sourceXml:
 * <pre>
 *   <font color='blue'>&lt;<font color='#983000'>SWT</font> <font color='#983000'>xmlns:xsi</font><font color='blue'>="</font><font color='black'>http://www.w3.org/2001/XMLSchema-instance</font><font color='blue'>"</font> <font color='#983000'>xsi:noNamespaceSchemaLocation</font><font color='blue'>="</font><font color='black'>..\Swt.xsd</font><font color='blue'>"</font> <font color='#983000'>package</font><font color='blue'>="</font><font color='black'>net.ifao.tools.plugins.dialog</font><font color='blue'>"</font>&gt;
 *     <font color='blue'>&lt;<font color='#983000'>Shell</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Select</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *       <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *         <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *           <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Header</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Enter your text</font><font color='blue'>"</font> <font color='#983000'>font</font><font color='blue'>="</font><font color='black'>HEADER_FONT</font><font color='blue'>"</font> /&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Type</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>...</font><font color='blue'>"</font> /&gt;</font>
 *         &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *         <font color='blue'>&lt;<font color='#983000'>Canvas</font> <font color='#983000'>image</font><font color='blue'>="</font><font color='black'>testcase_big.png</font><font color='blue'>"</font> /&gt;</font>
 *       &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *       <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_OUT</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_WIDGET_BACKGROUND</font><font color='blue'>"</font>&gt;
 *         <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Input</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>BORDER</font><font color='blue'>"</font> /&gt;</font>
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

public abstract class InputTextAdapter
{
  private Shell sShell = null;
  private Class<?> _abstractUIPlugin;

  /**
   * Constructor for InputTextAdapter
   * 
   * @param pAbstractUIPlugin InterfaceClass for AbstractUIPlugin
   **/
  public InputTextAdapter(Class<?> pAbstractUIPlugin) {
    _abstractUIPlugin = pAbstractUIPlugin;
    sShell = new Shell(Display.getCurrent(), SWT.APPLICATION_MODAL | SWT.TITLE | SWT.CLOSE | SWT.RESIZE);
    sShell.setText("Select");
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
  protected void initAdapter() {
    Color backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    sShell.setBackground(backgound);
    createComposite1(sShell, backgound);
    createComposite3(sShell, backgound);
    createComposite4(sShell, backgound);
  }
  /**
   * Method show displays this InputText and waits
   * until finished.
   * 
   **/
  public void show() {
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
  public void close() {
    sShell.close();
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
         Class<?>[] classes = { String.class };
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
  private Composite createComposite1(Composite parent, Color backgound) {
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
  private Composite createComposite2(Composite parent, Color backgound) {
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
    createCLabelHeader(value, backgound);
    createCLabelType(value, backgound);
    
    return value;
  } // finished createComposite2
  
  // +---+ +---+ +---+ +---+ +---+ +---+ 
  // | H | | e | | a | | d | | e | | r | 
  // +---+ +---+ +---+ +---+ +---+ +---+ 
  
  /**
   * Method initCLabelHeader initalizes Header.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pHeader Header of type CLabel
   **/
  protected void initCLabelHeader(CLabel pHeader) {}
  
  private CLabel _CLabelHeader = null; // private member
  /**
   * Method getCLabel_CLabelHeader returns the _CLabelHeader-object
   * 
   * @return _CLabelHeader-object of type CLabel
   **/
  public CLabel getCLabelHeader() {
    return _CLabelHeader;
  }
  
  /**
   * Method createCLabelHeader creates a CLabel and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created CLabel-object
   **/
  private CLabel createCLabelHeader(Composite parent, Color backgound) {
    _CLabelHeader = new CLabel(parent, SWT.NONE);
    _CLabelHeader.setText("Enter your text");
    _CLabelHeader.setFont(JFaceResources.getFontRegistry().get(JFaceResources.HEADER_FONT));
    _CLabelHeader.setBackground(backgound);
    // call the Init method (which could be overridden
    initCLabelHeader(_CLabelHeader);
    
    return _CLabelHeader;
  } // finished createCLabelHeader
  
  // +---+ +---+ +---+ +---+ 
  // | T | | y | | p | | e | 
  // +---+ +---+ +---+ +---+ 
  
  /**
   * Method initCLabelType initalizes Type.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pType Type of type CLabel
   **/
  protected void initCLabelType(CLabel pType) {}
  
  private CLabel _CLabelType = null; // private member
  /**
   * Method getCLabel_CLabelType returns the _CLabelType-object
   * 
   * @return _CLabelType-object of type CLabel
   **/
  public CLabel getCLabelType() {
    return _CLabelType;
  }
  
  /**
   * Method createCLabelType creates a CLabel and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created CLabel-object
   **/
  private CLabel createCLabelType(Composite parent, Color backgound) {
    _CLabelType = new CLabel(parent, SWT.NONE);
    _CLabelType.setText("...");
    _CLabelType.setBackground(backgound);
    // call the Init method (which could be overridden
    initCLabelType(_CLabelType);
    
    return _CLabelType;
  } // finished createCLabelType
  
  /**
   * Method createCanvas1 creates a Canvas and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Canvas-object
   **/
  private Canvas createCanvas1(Composite parent, Color backgound) {
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
  private Composite createComposite3(Composite parent, Color backgound) {
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
    createTextInput(value, backgound);
    
    return value;
  } // finished createComposite3
  
  // +---+ +---+ +---+ +---+ +---+ 
  // | I | | n | | p | | u | | t | 
  // +---+ +---+ +---+ +---+ +---+ 
  
  /**
   * Method initTextInput initalizes Input.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pInput Input of type Text
   **/
  protected void initTextInput(Text pInput) {}
  
  private Text _TextInput = null; // private member
  /**
   * Method getText_TextInput returns the _TextInput-object
   * 
   * @return _TextInput-object of type Text
   **/
  public Text getTextInput() {
    return _TextInput;
  }
  
  /**
   * Method createTextInput creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextInput(Composite parent, Color backgound) {
    _TextInput = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.minimumHeight = 60;
    _TextInput.setLayoutData(gridData);
    // call the Init method (which could be overridden
    initTextInput(_TextInput);
    
    // add a Modify-Listener for this modify
    _TextInput.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextInput(_TextInput);
       }
    });
    
    return _TextInput;
  } // finished createTextInput
  protected abstract void modifyTextInput(Text pTextInput);
  
  /**
   * Method createComposite4 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite4(Composite parent, Color backgound) {
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
    createComposite5(value, backgound);
    createComposite6(value, backgound);
    
    return value;
  } // finished createComposite4
  
  /**
   * Method createComposite5 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite5(Composite parent, Color backgound) {
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
  } // finished createComposite5
  
  /**
   * Method createComposite6 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite6(Composite parent, Color backgound) {
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
  } // finished createComposite6
  
  // +---+ +---+ +---+ +---+ +---+ +---+ 
  // | F | | i | | n | | i | | s | | h | 
  // +---+ +---+ +---+ +---+ +---+ +---+ 
  
  /**
   * Method initButtonFinish initalizes Finish.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pFinish Finish of type Button
   **/
  protected void initButtonFinish(Button pFinish) {}
  
  private Button _ButtonFinish = null; // private member
  /**
   * Method getButton_ButtonFinish returns the _ButtonFinish-object
   * 
   * @return _ButtonFinish-object of type Button
   **/
  public Button getButtonFinish() {
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
  private Button createButtonFinish(Composite parent, Color backgound) {
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
    
    // add a Selection-Listener for this click
    _ButtonFinish.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonFinish(_ButtonFinish);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonFinish;
  } // finished createButtonFinish
  protected abstract void clickButtonFinish(Button pButtonFinish);
  
  // +---+ +---+ +---+ +---+ +---+ +---+ 
  // | C | | a | | n | | c | | e | | l | 
  // +---+ +---+ +---+ +---+ +---+ +---+ 
  
  /**
   * Method initButtonCancel initalizes Cancel.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pCancel Cancel of type Button
   **/
  protected void initButtonCancel(Button pCancel) {}
  
  private Button _ButtonCancel = null; // private member
  /**
   * Method getButton_ButtonCancel returns the _ButtonCancel-object
   * 
   * @return _ButtonCancel-object of type Button
   **/
  public Button getButtonCancel() {
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
  private Button createButtonCancel(Composite parent, Color backgound) {
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
    
    // add a Selection-Listener for this click
    _ButtonCancel.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonCancel(_ButtonCancel);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonCancel;
  } // finished createButtonCancel
  protected abstract void clickButtonCancel(Button pButtonCancel);
}
