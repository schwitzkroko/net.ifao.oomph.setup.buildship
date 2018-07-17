package net.ifao.plugins.editor.testcase;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.*;
import java.util.Hashtable;
import org.eclipse.jface.resource.*;

/** 
 * The class TestcaseAdapter was automatically genereated with Xml2Swt
 * <p>
 * Date: 20.August 2007
 * sourceXml:
 * <pre>
 *   <font color='blue'>&lt;<font color='#983000'>SWT</font> <font color='#983000'>xmlns:xsi</font><font color='blue'>="</font><font color='black'>http://www.w3.org/2001/XMLSchema-instance</font><font color='blue'>"</font> <font color='#983000'>xsi:noNamespaceSchemaLocation</font><font color='blue'>="</font><font color='black'>..\..\Swt.xsd</font><font color='blue'>"</font> <font color='#983000'>package</font><font color='blue'>="</font><font color='black'>net.ifao.plugins.editor.testcase</font><font color='blue'>"</font>&gt;
 *     <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *       <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font>&gt;
 *         <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Title</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>TestObject:</font><font color='blue'>"</font> <font color='#983000'>font</font><font color='blue'>="</font><font color='black'>HEADER_FONT</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *         <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Type</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Type:</font><font color='blue'>"</font> <font color='#983000'>font</font><font color='blue'>="</font><font color='black'>BANNER_FONT</font><font color='blue'>"</font> /&gt;</font>
 *       &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *       <font color='blue'>&lt;<font color='#983000'>SashForm</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>sashDirection</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font>&gt;
 *         <font color='blue'>&lt;<font color='#983000'>Group</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Testcases</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>font</font><font color='blue'>="</font><font color='black'>BANNER_FONT</font><font color='blue'>"</font>&gt;
 *           <font color='blue'>&lt;<font color='#983000'>Tree</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Testcases</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font>&gt;
 *             <font color='blue'>&lt;<font color='#983000'>Menu</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>TestcaseMenu</font><font color='blue'>"</font>&gt;
 *               <font color='blue'>&lt;<font color='#983000'>MenuItem</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>MenuPre</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Add new precondition ...</font><font color='blue'>"</font> <font color='#983000'>image</font><font color='blue'>="</font><font color='black'>precondition.gif</font><font color='blue'>"</font> /&gt;</font>
 *               <font color='blue'>&lt;<font color='#983000'>MenuItem</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>MenuValid</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Add new expecation ...</font><font color='blue'>"</font> <font color='#983000'>image</font><font color='blue'>="</font><font color='black'>validator.gif</font><font color='blue'>"</font> /&gt;</font>
 *               <font color='blue'>&lt;<font color='#983000'>MenuItem</font> /&gt;</font>
 *               <font color='blue'>&lt;<font color='#983000'>MenuItem</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>ActivateAllTestcases</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Activate all testcases</font><font color='blue'>"</font> <font color='#983000'>image</font><font color='blue'>="</font><font color='black'>testcase.gif</font><font color='blue'>"</font> /&gt;</font>
 *               <font color='blue'>&lt;<font color='#983000'>MenuItem</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>ActivateTestcase</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Activate testcase</font><font color='blue'>"</font> <font color='#983000'>image</font><font color='blue'>="</font><font color='black'>testcase.gif</font><font color='blue'>"</font> /&gt;</font>
 *               <font color='blue'>&lt;<font color='#983000'>MenuItem</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>DisableTestcase</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Disable testcase</font><font color='blue'>"</font> <font color='#983000'>image</font><font color='blue'>="</font><font color='black'>testcaseDisabled.gif</font><font color='blue'>"</font> /&gt;</font>
 *               <font color='blue'>&lt;<font color='#983000'>MenuItem</font> /&gt;</font>
 *               <font color='blue'>&lt;<font color='#983000'>MenuItem</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>MenuRunTestcase</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Run as JUnit-Test</font><font color='blue'>"</font> <font color='#983000'>image</font><font color='blue'>="</font><font color='black'>junit.gif</font><font color='blue'>"</font> /&gt;</font>
 *               <font color='blue'>&lt;<font color='#983000'>MenuItem</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>MenuAdd2Template</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Add Testcase to Template</font><font color='blue'>"</font> /&gt;</font>
 *             &lt;/<font color='#983000'>Menu</font>&gt;</font>
 *           &lt;/<font color='#983000'>Tree</font>&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>VERTICAL</font><font color='blue'>"</font>&gt;
 *             <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>TCAdd</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Add ...</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>TCDelete</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Delete</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>enabled</font><font color='blue'>="</font><font color='black'>false</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>TCCopy</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Copy</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>enabled</font><font color='blue'>="</font><font color='black'>false</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>TCPaste</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Paste</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>TCUp</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Up</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>TCDown</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Down</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>TCRun</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Run</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>image</font><font color='blue'>="</font><font color='black'>junit.gif</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>TCSrc</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Src</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>image</font><font color='blue'>="</font><font color='black'>javaSrc.gif</font><font color='blue'>"</font> /&gt;</font>
 *           &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *         &lt;/<font color='#983000'>Group</font>&gt;</font>
 *         <font color='blue'>&lt;<font color='#983000'>Group</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Detail</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Testcase detail</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>font</font><font color='blue'>="</font><font color='black'>BANNER_FONT</font><font color='blue'>"</font>&gt;
 *           <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font>&gt;
 *             <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Set the properties for selected Testcase</font><font color='blue'>"</font> <font color='#983000'>colSpan</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Name:</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>NameCase</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Description:</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>colSpan</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>DescriptionCase</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>colSpan</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> /&gt;</font>
 *           &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font>&gt;
 *             <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Class:</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>CompClass</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>NONE</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>colSpan</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Description:</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>colSpan</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>CompDesc</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_OUT</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_WIDGET_LIGHT_SHADOW</font><font color='blue'>"</font> /&gt;</font>
 *           &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font>&gt;
 *             <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>DetailText</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Detail Value ...</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font>&gt;
 *               <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>DetailDesc</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_OUT</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_WIDGET_LIGHT_SHADOW</font><font color='blue'>"</font> /&gt;</font>
 *             &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Detail</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *           &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *         &lt;/<font color='#983000'>Group</font>&gt;</font>
 *       &lt;/<font color='#983000'>SashForm</font>&gt;</font>
 *     &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *   &lt;/<font color='#983000'>SWT</font>&gt;</font>
 * 
 * </pre>
 * <p> 
 * Copyright &copy; 2007, i:FAO
 * 
 * @author generator
 */
public abstract class TestcaseAdapter extends Composite
{
  private Class _abstractUIPlugin;

  /**
   * Constructor for TestcaseAdapter
   * 
   * @param parent The parent Composite on which the gui has to be created
   * @param pAbstractUIPlugin InterfaceClass for AbstractUIPlugin
   **/
  public TestcaseAdapter(Composite parent, Class pAbstractUIPlugin) {
    super(parent, SWT.NONE);
    _abstractUIPlugin = pAbstractUIPlugin;
  }

  /**
   * Method initAdapter should be called within child constructor and 
   * initalizes the GUI with it's componenets
   * 
   **/
  protected void initAdapter(Composite parent) {
    Color backgound=parent.getBackground();
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    this.setLayout(gridLayout);
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    this.setLayoutData(gridData);
    this.setBackground(backgound);
    createComposite1(this, backgound);
  }
   // write addIcon
   private Hashtable<String, Image> htImages = new Hashtable<String, Image>();

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
            image = new Image(getShell().getDisplay(), "icons/" + sName);
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
    createComposite2(value, backgound);
    createSashForm1(value, backgound);
    
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
      gridLayout.numColumns = 2;
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    value.setBackground(backgound);
    createCLabelTitle(value, backgound);
    createCLabelType(value, backgound);
    
    return value;
  } // finished createComposite2
  
  // +---+---+---+---+---+
  // | T | i | t | l | e |
  // +---+---+---+---+---+
  
  /**
   * Method initCLabelTitle initalizes Title.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTitle Title of type CLabel
   **/
  protected void initCLabelTitle(CLabel pTitle) {}
  
  private CLabel _CLabelTitle = null; // private member
  /**
   * Method getCLabel_CLabelTitle returns the _CLabelTitle-object
   * 
   * @return _CLabelTitle-object of type CLabel
   **/
  public CLabel getCLabelTitle() {
    return _CLabelTitle;
  }
  
  /**
   * Method createCLabelTitle creates a CLabel and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created CLabel-object
   **/
  private CLabel createCLabelTitle(Composite parent, Color backgound) {
    _CLabelTitle = new CLabel(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _CLabelTitle.setLayoutData(gridData);
    _CLabelTitle.setText("TestObject:");
    _CLabelTitle.setFont(JFaceResources.getFontRegistry().get(JFaceResources.HEADER_FONT));
    _CLabelTitle.setBackground(backgound);
    // call the Init method (which could be overridden
    initCLabelTitle(_CLabelTitle);
    
    return _CLabelTitle;
  } // finished createCLabelTitle
  
  // +---+---+---+---+
  // | T | y | p | e |
  // +---+---+---+---+
  
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
    _CLabelType.setText("Type:");
    _CLabelType.setFont(JFaceResources.getFontRegistry().get(JFaceResources.BANNER_FONT));
    _CLabelType.setBackground(backgound);
    // call the Init method (which could be overridden
    initCLabelType(_CLabelType);
    
    return _CLabelType;
  } // finished createCLabelType
  
  /**
   * Method createSashForm1 creates a SashForm and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created SashForm-object
   **/
  private SashForm createSashForm1(Composite parent, Color backgound) {
    SashForm value = new SashForm(parent, SWT.NONE | SWT.HORIZONTAL);
    value.SASH_WIDTH=3;
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    value.setBackground(backgound);
    createGroup1(value, backgound);
    createGroupDetail(value, backgound);
    
    return value;
  } // finished createSashForm1
  
  /**
   * Method createGroup1 creates a Group and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Group-object
   **/
  private Group createGroup1(Composite parent, Color backgound) {
    Group value = new Group(parent, SWT.NONE);
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
    value.setText("Testcases");
    value.setFont(JFaceResources.getFontRegistry().get(JFaceResources.BANNER_FONT));
    value.setBackground(backgound);
    createTreeTestcases(value, backgound);
    createComposite3(value, backgound);
    
    return value;
  } // finished createGroup1
  
  // +---+---+---+---+---+---+---+---+---+
  // | T | e | s | t | c | a | s | e | s |
  // +---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initTreeTestcases initalizes Testcases.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTestcases Testcases of type Tree
   **/
  protected void initTreeTestcases(Tree pTestcases) {}
  
  private Tree _TreeTestcases = null; // private member
  /**
   * Method getTree_TreeTestcases returns the _TreeTestcases-object
   * 
   * @return _TreeTestcases-object of type Tree
   **/
  public Tree getTreeTestcases() {
    return _TreeTestcases;
  }
  
  /**
   * Method createTreeTestcases creates a Tree and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Tree-object
   **/
  private Tree createTreeTestcases(Composite parent, Color backgound) {
    _TreeTestcases = new Tree(parent, SWT.BORDER);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    _TreeTestcases.setLayoutData(gridData);
    // call the Init method (which could be overridden
    initTreeTestcases(_TreeTestcases);
    
    // add a Tree-Listener for this treeCollapsed treeExpanded
    _TreeTestcases.addTreeListener(new org.eclipse.swt.events.TreeListener()
    {
       public void treeCollapsed(org.eclipse.swt.events.TreeEvent e)
       {
         treeCollapsedTreeTestcases(e);
       }
       public void treeExpanded(org.eclipse.swt.events.TreeEvent e)
       {
         treeExpandedTreeTestcases(e);
       }
    });
    
    // add a Key-Listener for this keyPressed
    _TreeTestcases.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTreeTestcases(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Selection-Listener for this click
    _TreeTestcases.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickTreeTestcases(_TreeTestcases);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    _TreeTestcases.setMenu(createMenuTestcaseMenu(_TreeTestcases, backgound));
    
    return _TreeTestcases;
  } // finished createTreeTestcases
  protected void treeCollapsedTreeTestcases(org.eclipse.swt.events.TreeEvent e) {}
  protected void treeExpandedTreeTestcases(org.eclipse.swt.events.TreeEvent e) {}
  protected void keyPressedTreeTestcases(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL)  && e.keyCode == 'a') {
      getTreeTestcases().selectAll();
   }
  }
  protected abstract void clickTreeTestcases(Tree pTreeTestcases);
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+
  // | T | e | s | t | c | a | s | e | M | e | n | u |
  // +---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initMenuTestcaseMenu initalizes TestcaseMenu.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTestcaseMenu TestcaseMenu of type Menu
   **/
  protected void initMenuTestcaseMenu(Menu pTestcaseMenu) {}
  
  private Menu _MenuTestcaseMenu = null; // private member
  /**
   * Method getMenu_MenuTestcaseMenu returns the _MenuTestcaseMenu-object
   * 
   * @return _MenuTestcaseMenu-object of type Menu
   **/
  public Menu getMenuTestcaseMenu() {
    return _MenuTestcaseMenu;
  }
  
  /**
   * Method createMenuTestcaseMenu creates a Menu and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Menu-object
   **/
  private Menu createMenuTestcaseMenu(Composite parent, Color backgound) {
    _MenuTestcaseMenu = new Menu(parent);
    // call the Init method (which could be overridden
    initMenuTestcaseMenu(_MenuTestcaseMenu);
    createMenuItemMenuPre(_MenuTestcaseMenu, backgound);
    createMenuItemMenuValid(_MenuTestcaseMenu, backgound);
    createMenuItem1(_MenuTestcaseMenu, backgound);
    createMenuItemActivateAllTestcases(_MenuTestcaseMenu, backgound);
    createMenuItemActivateTestcase(_MenuTestcaseMenu, backgound);
    createMenuItemDisableTestcase(_MenuTestcaseMenu, backgound);
    createMenuItem2(_MenuTestcaseMenu, backgound);
    createMenuItemMenuRunTestcase(_MenuTestcaseMenu, backgound);
    createMenuItemMenuAdd2Template(_MenuTestcaseMenu, backgound);
    
    return _MenuTestcaseMenu;
  } // finished createMenuTestcaseMenu
  
  // +---+---+---+---+---+---+---+
  // | M | e | n | u | P | r | e |
  // +---+---+---+---+---+---+---+
  
  /**
   * Method initMenuItemMenuPre initalizes MenuPre.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pMenuPre MenuPre of type MenuItem
   **/
  protected void initMenuItemMenuPre(MenuItem pMenuPre) {}
  
  private MenuItem _MenuItemMenuPre = null; // private member
  /**
   * Method getMenuItem_MenuItemMenuPre returns the _MenuItemMenuPre-object
   * 
   * @return _MenuItemMenuPre-object of type MenuItem
   **/
  public MenuItem getMenuItemMenuPre() {
    return _MenuItemMenuPre;
  }
  
  /**
   * Method createMenuItemMenuPre creates a MenuItem and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created MenuItem-object
   **/
  private MenuItem createMenuItemMenuPre(Menu parent, Color backgound) {
    _MenuItemMenuPre = new MenuItem(parent, SWT.NONE);
    _MenuItemMenuPre.setText("Add new precondition ...");
    _MenuItemMenuPre.setImage(getIcon("precondition.gif"));
    // call the Init method (which could be overridden
    initMenuItemMenuPre(_MenuItemMenuPre);
    
    // add a Selection-Listener for this click
    _MenuItemMenuPre.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickMenuItemMenuPre(_MenuItemMenuPre);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _MenuItemMenuPre;
  } // finished createMenuItemMenuPre
  protected abstract void clickMenuItemMenuPre(MenuItem pMenuItemMenuPre);
  
  // +---+---+---+---+---+---+---+---+---+
  // | M | e | n | u | V | a | l | i | d |
  // +---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initMenuItemMenuValid initalizes MenuValid.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pMenuValid MenuValid of type MenuItem
   **/
  protected void initMenuItemMenuValid(MenuItem pMenuValid) {}
  
  private MenuItem _MenuItemMenuValid = null; // private member
  /**
   * Method getMenuItem_MenuItemMenuValid returns the _MenuItemMenuValid-object
   * 
   * @return _MenuItemMenuValid-object of type MenuItem
   **/
  public MenuItem getMenuItemMenuValid() {
    return _MenuItemMenuValid;
  }
  
  /**
   * Method createMenuItemMenuValid creates a MenuItem and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created MenuItem-object
   **/
  private MenuItem createMenuItemMenuValid(Menu parent, Color backgound) {
    _MenuItemMenuValid = new MenuItem(parent, SWT.NONE);
    _MenuItemMenuValid.setText("Add new expecation ...");
    _MenuItemMenuValid.setImage(getIcon("validator.gif"));
    // call the Init method (which could be overridden
    initMenuItemMenuValid(_MenuItemMenuValid);
    
    // add a Selection-Listener for this click
    _MenuItemMenuValid.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickMenuItemMenuValid(_MenuItemMenuValid);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _MenuItemMenuValid;
  } // finished createMenuItemMenuValid
  protected abstract void clickMenuItemMenuValid(MenuItem pMenuItemMenuValid);
  
  /**
   * Method createMenuItem1 creates a MenuItem and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created MenuItem-object
   **/
  private MenuItem createMenuItem1(Menu parent, Color backgound) {
    MenuItem value = new MenuItem(parent, SWT.SEPARATOR);
    
    return value;
  } // finished createMenuItem1
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | A | c | t | i | v | a | t | e | A | l | l | T | e | s | t | c | a | s | e | s |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initMenuItemActivateAllTestcases initalizes ActivateAllTestcases.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pActivateAllTestcases ActivateAllTestcases of type MenuItem
   **/
  protected void initMenuItemActivateAllTestcases(MenuItem pActivateAllTestcases) {}
  
  private MenuItem _MenuItemActivateAllTestcases = null; // private member
  /**
   * Method getMenuItem_MenuItemActivateAllTestcases returns the _MenuItemActivateAllTestcases-object
   * 
   * @return _MenuItemActivateAllTestcases-object of type MenuItem
   **/
  public MenuItem getMenuItemActivateAllTestcases() {
    return _MenuItemActivateAllTestcases;
  }
  
  /**
   * Method createMenuItemActivateAllTestcases creates a MenuItem and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created MenuItem-object
   **/
  private MenuItem createMenuItemActivateAllTestcases(Menu parent, Color backgound) {
    _MenuItemActivateAllTestcases = new MenuItem(parent, SWT.NONE);
    _MenuItemActivateAllTestcases.setText("Activate all testcases");
    _MenuItemActivateAllTestcases.setImage(getIcon("testcase.gif"));
    // call the Init method (which could be overridden
    initMenuItemActivateAllTestcases(_MenuItemActivateAllTestcases);
    
    // add a Selection-Listener for this click
    _MenuItemActivateAllTestcases.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickMenuItemActivateAllTestcases(_MenuItemActivateAllTestcases);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _MenuItemActivateAllTestcases;
  } // finished createMenuItemActivateAllTestcases
  protected abstract void clickMenuItemActivateAllTestcases(MenuItem pMenuItemActivateAllTestcases);
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | A | c | t | i | v | a | t | e | T | e | s | t | c | a | s | e |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initMenuItemActivateTestcase initalizes ActivateTestcase.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pActivateTestcase ActivateTestcase of type MenuItem
   **/
  protected void initMenuItemActivateTestcase(MenuItem pActivateTestcase) {}
  
  private MenuItem _MenuItemActivateTestcase = null; // private member
  /**
   * Method getMenuItem_MenuItemActivateTestcase returns the _MenuItemActivateTestcase-object
   * 
   * @return _MenuItemActivateTestcase-object of type MenuItem
   **/
  public MenuItem getMenuItemActivateTestcase() {
    return _MenuItemActivateTestcase;
  }
  
  /**
   * Method createMenuItemActivateTestcase creates a MenuItem and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created MenuItem-object
   **/
  private MenuItem createMenuItemActivateTestcase(Menu parent, Color backgound) {
    _MenuItemActivateTestcase = new MenuItem(parent, SWT.NONE);
    _MenuItemActivateTestcase.setText("Activate testcase");
    _MenuItemActivateTestcase.setImage(getIcon("testcase.gif"));
    // call the Init method (which could be overridden
    initMenuItemActivateTestcase(_MenuItemActivateTestcase);
    
    // add a Selection-Listener for this click
    _MenuItemActivateTestcase.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickMenuItemActivateTestcase(_MenuItemActivateTestcase);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _MenuItemActivateTestcase;
  } // finished createMenuItemActivateTestcase
  protected abstract void clickMenuItemActivateTestcase(MenuItem pMenuItemActivateTestcase);
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | D | i | s | a | b | l | e | T | e | s | t | c | a | s | e |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initMenuItemDisableTestcase initalizes DisableTestcase.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDisableTestcase DisableTestcase of type MenuItem
   **/
  protected void initMenuItemDisableTestcase(MenuItem pDisableTestcase) {}
  
  private MenuItem _MenuItemDisableTestcase = null; // private member
  /**
   * Method getMenuItem_MenuItemDisableTestcase returns the _MenuItemDisableTestcase-object
   * 
   * @return _MenuItemDisableTestcase-object of type MenuItem
   **/
  public MenuItem getMenuItemDisableTestcase() {
    return _MenuItemDisableTestcase;
  }
  
  /**
   * Method createMenuItemDisableTestcase creates a MenuItem and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created MenuItem-object
   **/
  private MenuItem createMenuItemDisableTestcase(Menu parent, Color backgound) {
    _MenuItemDisableTestcase = new MenuItem(parent, SWT.NONE);
    _MenuItemDisableTestcase.setText("Disable testcase");
    _MenuItemDisableTestcase.setImage(getIcon("testcaseDisabled.gif"));
    // call the Init method (which could be overridden
    initMenuItemDisableTestcase(_MenuItemDisableTestcase);
    
    // add a Selection-Listener for this click
    _MenuItemDisableTestcase.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickMenuItemDisableTestcase(_MenuItemDisableTestcase);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _MenuItemDisableTestcase;
  } // finished createMenuItemDisableTestcase
  protected abstract void clickMenuItemDisableTestcase(MenuItem pMenuItemDisableTestcase);
  
  /**
   * Method createMenuItem2 creates a MenuItem and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created MenuItem-object
   **/
  private MenuItem createMenuItem2(Menu parent, Color backgound) {
    MenuItem value = new MenuItem(parent, SWT.SEPARATOR);
    
    return value;
  } // finished createMenuItem2
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | M | e | n | u | R | u | n | T | e | s | t | c | a | s | e |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initMenuItemMenuRunTestcase initalizes MenuRunTestcase.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pMenuRunTestcase MenuRunTestcase of type MenuItem
   **/
  protected void initMenuItemMenuRunTestcase(MenuItem pMenuRunTestcase) {}
  
  private MenuItem _MenuItemMenuRunTestcase = null; // private member
  /**
   * Method getMenuItem_MenuItemMenuRunTestcase returns the _MenuItemMenuRunTestcase-object
   * 
   * @return _MenuItemMenuRunTestcase-object of type MenuItem
   **/
  public MenuItem getMenuItemMenuRunTestcase() {
    return _MenuItemMenuRunTestcase;
  }
  
  /**
   * Method createMenuItemMenuRunTestcase creates a MenuItem and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created MenuItem-object
   **/
  private MenuItem createMenuItemMenuRunTestcase(Menu parent, Color backgound) {
    _MenuItemMenuRunTestcase = new MenuItem(parent, SWT.NONE);
    _MenuItemMenuRunTestcase.setText("Run as JUnit-Test");
    _MenuItemMenuRunTestcase.setImage(getIcon("junit.gif"));
    // call the Init method (which could be overridden
    initMenuItemMenuRunTestcase(_MenuItemMenuRunTestcase);
    
    // add a Selection-Listener for this click
    _MenuItemMenuRunTestcase.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickMenuItemMenuRunTestcase(_MenuItemMenuRunTestcase);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _MenuItemMenuRunTestcase;
  } // finished createMenuItemMenuRunTestcase
  protected abstract void clickMenuItemMenuRunTestcase(MenuItem pMenuItemMenuRunTestcase);
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | M | e | n | u | A | d | d | 2 | T | e | m | p | l | a | t | e |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initMenuItemMenuAdd2Template initalizes MenuAdd2Template.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pMenuAdd2Template MenuAdd2Template of type MenuItem
   **/
  protected void initMenuItemMenuAdd2Template(MenuItem pMenuAdd2Template) {}
  
  private MenuItem _MenuItemMenuAdd2Template = null; // private member
  /**
   * Method getMenuItem_MenuItemMenuAdd2Template returns the _MenuItemMenuAdd2Template-object
   * 
   * @return _MenuItemMenuAdd2Template-object of type MenuItem
   **/
  public MenuItem getMenuItemMenuAdd2Template() {
    return _MenuItemMenuAdd2Template;
  }
  
  /**
   * Method createMenuItemMenuAdd2Template creates a MenuItem and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created MenuItem-object
   **/
  private MenuItem createMenuItemMenuAdd2Template(Menu parent, Color backgound) {
    _MenuItemMenuAdd2Template = new MenuItem(parent, SWT.NONE);
    _MenuItemMenuAdd2Template.setText("Add Testcase to Template");
    // call the Init method (which could be overridden
    initMenuItemMenuAdd2Template(_MenuItemMenuAdd2Template);
    
    // add a Selection-Listener for this click
    _MenuItemMenuAdd2Template.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickMenuItemMenuAdd2Template(_MenuItemMenuAdd2Template);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _MenuItemMenuAdd2Template;
  } // finished createMenuItemMenuAdd2Template
  protected abstract void clickMenuItemMenuAdd2Template(MenuItem pMenuItemMenuAdd2Template);
  
  /**
   * Method createComposite3 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite3(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.NONE);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = false;
    gridData.horizontalAlignment = GridData.BEGINNING;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    value.setBackground(backgound);
    createButtonTCAdd(value, backgound);
    createButtonTCDelete(value, backgound);
    createButtonTCCopy(value, backgound);
    createButtonTCPaste(value, backgound);
    createButtonTCUp(value, backgound);
    createButtonTCDown(value, backgound);
    createComposite4(value, backgound);
    createButtonTCRun(value, backgound);
    createButtonTCSrc(value, backgound);
    
    return value;
  } // finished createComposite3
  
  // +---+---+---+---+---+
  // | T | C | A | d | d |
  // +---+---+---+---+---+
  
  /**
   * Method initButtonTCAdd initalizes TCAdd.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTCAdd TCAdd of type Button
   **/
  protected void initButtonTCAdd(Button pTCAdd) {}
  
  private Button _ButtonTCAdd = null; // private member
  /**
   * Method getButton_ButtonTCAdd returns the _ButtonTCAdd-object
   * 
   * @return _ButtonTCAdd-object of type Button
   **/
  public Button getButtonTCAdd() {
    return _ButtonTCAdd;
  }
  
  /**
   * Method createButtonTCAdd creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonTCAdd(Composite parent, Color backgound) {
    _ButtonTCAdd = new Button(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ButtonTCAdd.setLayoutData(gridData);
    _ButtonTCAdd.setText("Add ...");
    _ButtonTCAdd.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonTCAdd(_ButtonTCAdd);
    
    // add a Selection-Listener for this click
    _ButtonTCAdd.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonTCAdd(_ButtonTCAdd);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonTCAdd;
  } // finished createButtonTCAdd
  protected abstract void clickButtonTCAdd(Button pButtonTCAdd);
  
  // +---+---+---+---+---+---+---+---+
  // | T | C | D | e | l | e | t | e |
  // +---+---+---+---+---+---+---+---+
  
  /**
   * Method initButtonTCDelete initalizes TCDelete.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTCDelete TCDelete of type Button
   **/
  protected void initButtonTCDelete(Button pTCDelete) {}
  
  private Button _ButtonTCDelete = null; // private member
  /**
   * Method getButton_ButtonTCDelete returns the _ButtonTCDelete-object
   * 
   * @return _ButtonTCDelete-object of type Button
   **/
  public Button getButtonTCDelete() {
    return _ButtonTCDelete;
  }
  
  /**
   * Method createButtonTCDelete creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonTCDelete(Composite parent, Color backgound) {
    _ButtonTCDelete = new Button(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ButtonTCDelete.setLayoutData(gridData);
    _ButtonTCDelete.setEnabled(false);
    _ButtonTCDelete.setText("Delete");
    _ButtonTCDelete.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonTCDelete(_ButtonTCDelete);
    
    // add a Selection-Listener for this click
    _ButtonTCDelete.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonTCDelete(_ButtonTCDelete);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonTCDelete;
  } // finished createButtonTCDelete
  protected abstract void clickButtonTCDelete(Button pButtonTCDelete);
  
  // +---+---+---+---+---+---+
  // | T | C | C | o | p | y |
  // +---+---+---+---+---+---+
  
  /**
   * Method initButtonTCCopy initalizes TCCopy.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTCCopy TCCopy of type Button
   **/
  protected void initButtonTCCopy(Button pTCCopy) {}
  
  private Button _ButtonTCCopy = null; // private member
  /**
   * Method getButton_ButtonTCCopy returns the _ButtonTCCopy-object
   * 
   * @return _ButtonTCCopy-object of type Button
   **/
  public Button getButtonTCCopy() {
    return _ButtonTCCopy;
  }
  
  /**
   * Method createButtonTCCopy creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonTCCopy(Composite parent, Color backgound) {
    _ButtonTCCopy = new Button(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ButtonTCCopy.setLayoutData(gridData);
    _ButtonTCCopy.setEnabled(false);
    _ButtonTCCopy.setText("Copy");
    _ButtonTCCopy.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonTCCopy(_ButtonTCCopy);
    
    // add a Selection-Listener for this click
    _ButtonTCCopy.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonTCCopy(_ButtonTCCopy);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonTCCopy;
  } // finished createButtonTCCopy
  protected abstract void clickButtonTCCopy(Button pButtonTCCopy);
  
  // +---+---+---+---+---+---+---+
  // | T | C | P | a | s | t | e |
  // +---+---+---+---+---+---+---+
  
  /**
   * Method initButtonTCPaste initalizes TCPaste.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTCPaste TCPaste of type Button
   **/
  protected void initButtonTCPaste(Button pTCPaste) {}
  
  private Button _ButtonTCPaste = null; // private member
  /**
   * Method getButton_ButtonTCPaste returns the _ButtonTCPaste-object
   * 
   * @return _ButtonTCPaste-object of type Button
   **/
  public Button getButtonTCPaste() {
    return _ButtonTCPaste;
  }
  
  /**
   * Method createButtonTCPaste creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonTCPaste(Composite parent, Color backgound) {
    _ButtonTCPaste = new Button(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ButtonTCPaste.setLayoutData(gridData);
    _ButtonTCPaste.setText("Paste");
    _ButtonTCPaste.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonTCPaste(_ButtonTCPaste);
    
    // add a Selection-Listener for this click
    _ButtonTCPaste.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonTCPaste(_ButtonTCPaste);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonTCPaste;
  } // finished createButtonTCPaste
  protected abstract void clickButtonTCPaste(Button pButtonTCPaste);
  
  // +---+---+---+---+
  // | T | C | U | p |
  // +---+---+---+---+
  
  /**
   * Method initButtonTCUp initalizes TCUp.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTCUp TCUp of type Button
   **/
  protected void initButtonTCUp(Button pTCUp) {}
  
  private Button _ButtonTCUp = null; // private member
  /**
   * Method getButton_ButtonTCUp returns the _ButtonTCUp-object
   * 
   * @return _ButtonTCUp-object of type Button
   **/
  public Button getButtonTCUp() {
    return _ButtonTCUp;
  }
  
  /**
   * Method createButtonTCUp creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonTCUp(Composite parent, Color backgound) {
    _ButtonTCUp = new Button(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ButtonTCUp.setLayoutData(gridData);
    _ButtonTCUp.setText("Up");
    _ButtonTCUp.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonTCUp(_ButtonTCUp);
    
    // add a Selection-Listener for this click
    _ButtonTCUp.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonTCUp(_ButtonTCUp);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonTCUp;
  } // finished createButtonTCUp
  protected abstract void clickButtonTCUp(Button pButtonTCUp);
  
  // +---+---+---+---+---+---+
  // | T | C | D | o | w | n |
  // +---+---+---+---+---+---+
  
  /**
   * Method initButtonTCDown initalizes TCDown.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTCDown TCDown of type Button
   **/
  protected void initButtonTCDown(Button pTCDown) {}
  
  private Button _ButtonTCDown = null; // private member
  /**
   * Method getButton_ButtonTCDown returns the _ButtonTCDown-object
   * 
   * @return _ButtonTCDown-object of type Button
   **/
  public Button getButtonTCDown() {
    return _ButtonTCDown;
  }
  
  /**
   * Method createButtonTCDown creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonTCDown(Composite parent, Color backgound) {
    _ButtonTCDown = new Button(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ButtonTCDown.setLayoutData(gridData);
    _ButtonTCDown.setText("Down");
    _ButtonTCDown.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonTCDown(_ButtonTCDown);
    
    // add a Selection-Listener for this click
    _ButtonTCDown.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonTCDown(_ButtonTCDown);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonTCDown;
  } // finished createButtonTCDown
  protected abstract void clickButtonTCDown(Button pButtonTCDown);
  
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
  } // finished createComposite4
  
  // +---+---+---+---+---+
  // | T | C | R | u | n |
  // +---+---+---+---+---+
  
  /**
   * Method initButtonTCRun initalizes TCRun.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTCRun TCRun of type Button
   **/
  protected void initButtonTCRun(Button pTCRun) {}
  
  private Button _ButtonTCRun = null; // private member
  /**
   * Method getButton_ButtonTCRun returns the _ButtonTCRun-object
   * 
   * @return _ButtonTCRun-object of type Button
   **/
  public Button getButtonTCRun() {
    return _ButtonTCRun;
  }
  
  /**
   * Method createButtonTCRun creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonTCRun(Composite parent, Color backgound) {
    _ButtonTCRun = new Button(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ButtonTCRun.setLayoutData(gridData);
    _ButtonTCRun.setText("Run");
    _ButtonTCRun.setImage(getIcon("junit.gif"));
    _ButtonTCRun.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonTCRun(_ButtonTCRun);
    
    // add a Selection-Listener for this click
    _ButtonTCRun.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonTCRun(_ButtonTCRun);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonTCRun;
  } // finished createButtonTCRun
  protected abstract void clickButtonTCRun(Button pButtonTCRun);
  
  // +---+---+---+---+---+
  // | T | C | S | r | c |
  // +---+---+---+---+---+
  
  /**
   * Method initButtonTCSrc initalizes TCSrc.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTCSrc TCSrc of type Button
   **/
  protected void initButtonTCSrc(Button pTCSrc) {}
  
  private Button _ButtonTCSrc = null; // private member
  /**
   * Method getButton_ButtonTCSrc returns the _ButtonTCSrc-object
   * 
   * @return _ButtonTCSrc-object of type Button
   **/
  public Button getButtonTCSrc() {
    return _ButtonTCSrc;
  }
  
  /**
   * Method createButtonTCSrc creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonTCSrc(Composite parent, Color backgound) {
    _ButtonTCSrc = new Button(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ButtonTCSrc.setLayoutData(gridData);
    _ButtonTCSrc.setText("Src");
    _ButtonTCSrc.setImage(getIcon("javaSrc.gif"));
    _ButtonTCSrc.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonTCSrc(_ButtonTCSrc);
    
    // add a Selection-Listener for this click
    _ButtonTCSrc.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonTCSrc(_ButtonTCSrc);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonTCSrc;
  } // finished createButtonTCSrc
  protected abstract void clickButtonTCSrc(Button pButtonTCSrc);
  
  // +---+---+---+---+---+---+
  // | D | e | t | a | i | l |
  // +---+---+---+---+---+---+
  
  /**
   * Method initGroupDetail initalizes Detail.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDetail Detail of type Group
   **/
  protected void initGroupDetail(Group pDetail) {}
  
  private Group _GroupDetail = null; // private member
  /**
   * Method getGroup_GroupDetail returns the _GroupDetail-object
   * 
   * @return _GroupDetail-object of type Group
   **/
  public Group getGroupDetail() {
    return _GroupDetail;
  }
  
  /**
   * Method createGroupDetail creates a Group and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Group-object
   **/
  private Group createGroupDetail(Composite parent, Color backgound) {
    _GroupDetail = new Group(parent, SWT.NONE);
    // create a StackLayout for the Composite
    _GroupDetail.setLayout(_GroupDetailLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    _GroupDetail.setLayoutData(gridData);
    _GroupDetail.setText("Testcase detail");
    _GroupDetail.setFont(JFaceResources.getFontRegistry().get(JFaceResources.BANNER_FONT));
    _GroupDetail.setBackground(backgound);
    // call the Init method (which could be overridden
    initGroupDetail(_GroupDetail);
    createComposite5(_GroupDetail, backgound);
    createComposite6(_GroupDetail, backgound);
    createComposite7(_GroupDetail, backgound);
    createComposite8(_GroupDetail, backgound);
    
    return _GroupDetail;
  } // finished createGroupDetail

  StackLayout _GroupDetailLayout = new StackLayout();


  public void setGroupDetailPage(int piPage)
  {
     if (_GroupDetail == null)
         return;
     Control[] children = _GroupDetail.getChildren();
     if (piPage >= 0 && piPage < children.length) {
        _GroupDetailLayout.topControl = children[piPage];
        _GroupDetail.layout();
     }
  }
  
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
    createLabel2(value, backgound);
    createTextNameCase(value, backgound);
    createLabel3(value, backgound);
    createTextDescriptionCase(value, backgound);
    
    return value;
  } // finished createComposite6
  
  /**
   * Method createLabel1 creates a Label and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Label-object
   **/
  private Label createLabel1(Composite parent, Color backgound) {
    Label value = new Label(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.horizontalSpan = 2;
    value.setLayoutData(gridData);
    value.setText("Set the properties for selected Testcase");
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
  private Label createLabel2(Composite parent, Color backgound) {
    Label value = new Label(parent, SWT.NONE);
    value.setText("Name:");
    value.setBackground(backgound);
    
    return value;
  } // finished createLabel2
  
  // +---+---+---+---+---+---+---+---+
  // | N | a | m | e | C | a | s | e |
  // +---+---+---+---+---+---+---+---+
  
  /**
   * Method initTextNameCase initalizes NameCase.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pNameCase NameCase of type Text
   **/
  protected void initTextNameCase(Text pNameCase) {}
  
  private Text _TextNameCase = null; // private member
  /**
   * Method getText_TextNameCase returns the _TextNameCase-object
   * 
   * @return _TextNameCase-object of type Text
   **/
  public Text getTextNameCase() {
    return _TextNameCase;
  }
  
  /**
   * Method createTextNameCase creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextNameCase(Composite parent, Color backgound) {
    _TextNameCase = new Text(parent, SWT.BORDER);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _TextNameCase.setLayoutData(gridData);
    // call the Init method (which could be overridden
    initTextNameCase(_TextNameCase);
    
    // add a Key-Listener for this keyPressed
    _TextNameCase.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTextNameCase(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Modify-Listener for this modify
    _TextNameCase.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextNameCase(_TextNameCase);
       }
    });
    
    return _TextNameCase;
  } // finished createTextNameCase
  protected void keyPressedTextNameCase(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextNameCase().selectAll();
   }
  }
  protected abstract void modifyTextNameCase(Text pTextNameCase);
  
  /**
   * Method createLabel3 creates a Label and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Label-object
   **/
  private Label createLabel3(Composite parent, Color backgound) {
    Label value = new Label(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    gridData.horizontalSpan = 2;
    value.setLayoutData(gridData);
    value.setText("Description:");
    value.setBackground(backgound);
    
    return value;
  } // finished createLabel3
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | D | e | s | c | r | i | p | t | i | o | n | C | a | s | e |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initTextDescriptionCase initalizes DescriptionCase.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDescriptionCase DescriptionCase of type Text
   **/
  protected void initTextDescriptionCase(Text pDescriptionCase) {}
  
  private Text _TextDescriptionCase = null; // private member
  /**
   * Method getText_TextDescriptionCase returns the _TextDescriptionCase-object
   * 
   * @return _TextDescriptionCase-object of type Text
   **/
  public Text getTextDescriptionCase() {
    return _TextDescriptionCase;
  }
  
  /**
   * Method createTextDescriptionCase creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextDescriptionCase(Composite parent, Color backgound) {
    _TextDescriptionCase = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.minimumHeight = 60;
    gridData.horizontalSpan = 2;
    _TextDescriptionCase.setLayoutData(gridData);
    // call the Init method (which could be overridden
    initTextDescriptionCase(_TextDescriptionCase);
    
    // add a Key-Listener for this keyPressed
    _TextDescriptionCase.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTextDescriptionCase(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Modify-Listener for this modify
    _TextDescriptionCase.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextDescriptionCase(_TextDescriptionCase);
       }
    });
    
    return _TextDescriptionCase;
  } // finished createTextDescriptionCase
  protected void keyPressedTextDescriptionCase(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextDescriptionCase().selectAll();
   }
  }
  protected abstract void modifyTextDescriptionCase(Text pTextDescriptionCase);
  
  /**
   * Method createComposite7 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite7(Composite parent, Color backgound) {
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
    createLabel4(value, backgound);
    createTextCompClass(value, backgound);
    createLabel5(value, backgound);
    createTextCompDesc(value, backgound);
    
    return value;
  } // finished createComposite7
  
  /**
   * Method createLabel4 creates a Label and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Label-object
   **/
  private Label createLabel4(Composite parent, Color backgound) {
    Label value = new Label(parent, SWT.NONE);
    value.setText("Class:");
    value.setBackground(backgound);
    
    return value;
  } // finished createLabel4
  
  // +---+---+---+---+---+---+---+---+---+
  // | C | o | m | p | C | l | a | s | s |
  // +---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initTextCompClass initalizes CompClass.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pCompClass CompClass of type Text
   **/
  protected void initTextCompClass(Text pCompClass) {}
  
  private Text _TextCompClass = null; // private member
  /**
   * Method getText_TextCompClass returns the _TextCompClass-object
   * 
   * @return _TextCompClass-object of type Text
   **/
  public Text getTextCompClass() {
    return _TextCompClass;
  }
  
  /**
   * Method createTextCompClass creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextCompClass(Composite parent, Color backgound) {
    _TextCompClass = new Text(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _TextCompClass.setLayoutData(gridData);
    // call the Init method (which could be overridden
    initTextCompClass(_TextCompClass);
    
    // add a Key-Listener for this keyPressed
    _TextCompClass.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTextCompClass(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Modify-Listener for this modify
    _TextCompClass.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextCompClass(_TextCompClass);
       }
    });
    
    return _TextCompClass;
  } // finished createTextCompClass
  protected void keyPressedTextCompClass(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextCompClass().selectAll();
   }
  }
  protected abstract void modifyTextCompClass(Text pTextCompClass);
  
  /**
   * Method createLabel5 creates a Label and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Label-object
   **/
  private Label createLabel5(Composite parent, Color backgound) {
    Label value = new Label(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.horizontalSpan = 2;
    value.setLayoutData(gridData);
    value.setText("Description:");
    value.setBackground(backgound);
    
    return value;
  } // finished createLabel5
  
  // +---+---+---+---+---+---+---+---+
  // | C | o | m | p | D | e | s | c |
  // +---+---+---+---+---+---+---+---+
  
  /**
   * Method initTextCompDesc initalizes CompDesc.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pCompDesc CompDesc of type Text
   **/
  protected void initTextCompDesc(Text pCompDesc) {}
  
  private Text _TextCompDesc = null; // private member
  /**
   * Method getText_TextCompDesc returns the _TextCompDesc-object
   * 
   * @return _TextCompDesc-object of type Text
   **/
  public Text getTextCompDesc() {
    return _TextCompDesc;
  }
  
  /**
   * Method createTextCompDesc creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextCompDesc(Composite parent, Color backgound) {
    _TextCompDesc = new Text(parent, SWT.SHADOW_ETCHED_OUT | SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.minimumHeight = 60;
    gridData.horizontalSpan = 2;
    _TextCompDesc.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
    _TextCompDesc.setBackground(backgound);
    // call the Init method (which could be overridden
    initTextCompDesc(_TextCompDesc);
    
    // add a Key-Listener for this keyPressed
    _TextCompDesc.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTextCompDesc(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Modify-Listener for this modify
    _TextCompDesc.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextCompDesc(_TextCompDesc);
       }
    });
    
    return _TextCompDesc;
  } // finished createTextCompDesc
  protected void keyPressedTextCompDesc(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextCompDesc().selectAll();
   }
  }
  protected abstract void modifyTextCompDesc(Text pTextCompDesc);
  
  /**
   * Method createComposite8 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite8(Composite parent, Color backgound) {
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
    createCLabelDetailText(value, backgound);
    createComposite9(value, backgound);
    createTextDetail(value, backgound);
    
    return value;
  } // finished createComposite8
  
  // +---+---+---+---+---+---+---+---+---+---+
  // | D | e | t | a | i | l | T | e | x | t |
  // +---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initCLabelDetailText initalizes DetailText.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDetailText DetailText of type CLabel
   **/
  protected void initCLabelDetailText(CLabel pDetailText) {}
  
  private CLabel _CLabelDetailText = null; // private member
  /**
   * Method getCLabel_CLabelDetailText returns the _CLabelDetailText-object
   * 
   * @return _CLabelDetailText-object of type CLabel
   **/
  public CLabel getCLabelDetailText() {
    return _CLabelDetailText;
  }
  
  /**
   * Method createCLabelDetailText creates a CLabel and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created CLabel-object
   **/
  private CLabel createCLabelDetailText(Composite parent, Color backgound) {
    _CLabelDetailText = new CLabel(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _CLabelDetailText.setLayoutData(gridData);
    _CLabelDetailText.setText("Detail Value ...");
    _CLabelDetailText.setBackground(backgound);
    // call the Init method (which could be overridden
    initCLabelDetailText(_CLabelDetailText);
    
    return _CLabelDetailText;
  } // finished createCLabelDetailText
  
  /**
   * Method createComposite9 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite9(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.NONE);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    value.setBackground(backgound);
    createTextDetailDesc(value, backgound);
    
    return value;
  } // finished createComposite9
  
  // +---+---+---+---+---+---+---+---+---+---+
  // | D | e | t | a | i | l | D | e | s | c |
  // +---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initTextDetailDesc initalizes DetailDesc.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDetailDesc DetailDesc of type Text
   **/
  protected void initTextDetailDesc(Text pDetailDesc) {}
  
  private Text _TextDetailDesc = null; // private member
  /**
   * Method getText_TextDetailDesc returns the _TextDetailDesc-object
   * 
   * @return _TextDetailDesc-object of type Text
   **/
  public Text getTextDetailDesc() {
    return _TextDetailDesc;
  }
  
  /**
   * Method createTextDetailDesc creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextDetailDesc(Composite parent, Color backgound) {
    _TextDetailDesc = new Text(parent, SWT.SHADOW_ETCHED_OUT | SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.minimumHeight = 60;
    _TextDetailDesc.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
    _TextDetailDesc.setBackground(backgound);
    // call the Init method (which could be overridden
    initTextDetailDesc(_TextDetailDesc);
    
    // add a Key-Listener for this keyPressed
    _TextDetailDesc.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTextDetailDesc(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Modify-Listener for this modify
    _TextDetailDesc.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextDetailDesc(_TextDetailDesc);
       }
    });
    
    return _TextDetailDesc;
  } // finished createTextDetailDesc
  protected void keyPressedTextDetailDesc(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextDetailDesc().selectAll();
   }
  }
  protected abstract void modifyTextDetailDesc(Text pTextDetailDesc);
  
  // +---+---+---+---+---+---+
  // | D | e | t | a | i | l |
  // +---+---+---+---+---+---+
  
  /**
   * Method initTextDetail initalizes Detail.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDetail Detail of type Text
   **/
  protected void initTextDetail(Text pDetail) {}
  
  private Text _TextDetail = null; // private member
  /**
   * Method getText_TextDetail returns the _TextDetail-object
   * 
   * @return _TextDetail-object of type Text
   **/
  public Text getTextDetail() {
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
  private Text createTextDetail(Composite parent, Color backgound) {
    _TextDetail = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.minimumHeight = 60;
    _TextDetail.setLayoutData(gridData);
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
       {
       }
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
  protected void keyPressedTextDetail(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextDetail().selectAll();
   }
  }
  protected abstract void modifyTextDetail(Text pTextDetail);
}
