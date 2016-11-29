package net.ifao.plugins.editor.testcase;


import ifaoplugin.Util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import net.ifao.xml.Xml;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.internal.Workbench;


/** 
 * Main class TestcaseData encapsultes the Data of the TestcaseClass. 
 * The TestcaseClass depends on the automatically generated TestcaseAdapter, 
 * which contains the GUI. So this class contains the Data and is the 
 * interface for the "world" 
 * 
 * <p> 
 * Copyright &copy; 2007, i:FAO 
 * 
 * @author brod 
 */
public class TestcaseData
   extends Testcase
{
   // ---------------------------------------------------------------
   // static members
   // ---------------------------------------------------------------

   static final String _sBaseDir4Components = "ruletest";// "componetstest";

   // ---------------------------------------------------------------
   // private members
   // ---------------------------------------------------------------

   private String _sTscFile = "";
   private String _sCaseType;
   private File _classesDirectory;
   private Class _clsAnnotationTitle;
   private Class _clsAnnotationParam;
   private String _sTestTemplatesXml;
   private Xml _xmlTestData;

   private java.util.Hashtable<String, String> _htInit;
   private java.util.Hashtable<String, String> _htValid;
   private java.util.Hashtable<String, ArrayList<TAnnotation>> _htCode = new java.util.Hashtable<String, ArrayList<TAnnotation>>();

   private TestcaseRunner _testcaseRunner = null;
   private TestcasePage _rootTestcasePage;
   private Class _abstractUIPlugin;

   // ---------------------------------------------------------------
   // Constructor
   // ---------------------------------------------------------------

   /** 
    * Constructor TestcaseData with the following parameters: 
    * 
    * @param pBusinessRulesPage The TestcasePage-BusinessRulesPage 
    * @param pParent The parent class (GUI) 
    * @param pAbstractUIPlugin The Abstract user interface 
    * @param pFile The FileName (for this testcase) 
    * @param pbIsDirectTsc If the Button src is activated 
    * 
    * @author brod 
    */
   public TestcaseData(TestcasePage pBusinessRulesPage, Composite pParent, Class pAbstractUIPlugin,
                       File pFile, boolean pbIsDirectTsc)
   {
      super(pBusinessRulesPage, pParent, pAbstractUIPlugin, pFile);
      _rootTestcasePage = pBusinessRulesPage;
      _abstractUIPlugin = pAbstractUIPlugin;

      _sTscFile = pFile.getAbsolutePath().replaceAll("\\\\", "/");

      // file has to be the tsc-File
      if (!_sTscFile.endsWith(".tsc")) {
         throw new RuntimeException("Invalid File selected. File has to end with tsc");
      }

      int iJUnitTest = _sTscFile.indexOf("/jUnitTest/");
      if (iJUnitTest < 0) {
         throw new RuntimeException(
               "Invalid File selected. File has to be located within jUnitTest directory");
      }

      String sBase = _sTscFile.substring(0, iJUnitTest);

      _classesDirectory = new File(sBase + "/classes");
      if (!_classesDirectory.exists()) {
         _classesDirectory = new File(sBase + "/bin");
      }

      _sCaseType = getBaseClassType(_sTscFile.substring(iJUnitTest + 11, _sTscFile.length() - 4)
            .replaceAll("\\/", "."));

      _sTestTemplatesXml = sBase + "/jUnitTest/net/ifao/" + _sBaseDir4Components
            + "/data/TestTemplates" + (_sCaseType.startsWith("-") ? "" : "_" + _sCaseType) + ".xml";

      try {
         _clsAnnotationTitle = Util.getCompiledClass(_classesDirectory.getAbsolutePath(),
               "net.ifao.ruletest.framework.annotation.Title");
         _clsAnnotationParam = Util.getCompiledClass(_classesDirectory.getAbsolutePath(),
               "net.ifao.ruletest.framework.annotation.Param");
      }
      catch (ClassNotFoundException e1) {
         _clsAnnotationTitle = null;
         _clsAnnotationParam = null;
      }
      initLists();

      getCLabelType().setText(_sCaseType);

      getButtonTCSrc().setEnabled(pbIsDirectTsc);

      reload();

   }

   /** 
    * method main for test purpose. 
    * 
    * @param pArgs Arguments 
    * 
    * @author brod 
    */
   public static void main(String[] pArgs)
   {
      display("BusinessRules", null);
   }

   /** 
    * method display (for test purpose) form the main method 
    * 
    * @param psTitle The title string 
    * @param pAbstractUIPlugin The Abstract user interface 
    * 
    * @author brod 
    */
   public static void display(String psTitle, Class pAbstractUIPlugin)
   {
      Shell sShell = new Shell();
      sShell.setText(psTitle);
      GridLayout gridLayout = new GridLayout();
      gridLayout.horizontalSpacing = 0;
      gridLayout.marginWidth = 0;
      gridLayout.marginHeight = 0;
      gridLayout.verticalSpacing = 0;
      sShell.setLayout(gridLayout);
      Display display = Display.getDefault();
      String sFile = "";
      sFile = "net/ifao/arctic/agents/amadeus/xml/pnr/business/CancelActiveSegmentsRule.tsc";
      sFile = "net/ifao/arctic/agents/amadeus/xml/pnr/elements/PnrElementAccountingInfo.tsc";
      sFile = "net/ifao/arctic/agents/hrs/wsdl/framework/business/PrepareCancellation.tsc";
      TestcaseData testcaseData = new TestcaseData(null, sShell, pAbstractUIPlugin, new File(
            "C:/arctic/eclipse/main/jUnitTest/" + sFile), true);
      sShell.setImage(testcaseData.getIcon("rule.gif"));
      sShell.pack();
      sShell.open();
      while (!sShell.isDisposed()) {
         if (!display.readAndDispatch())
            display.sleep();
      }
      display.dispose();
   }

   MessageBox messageBox;

   // ---------------------------------------------------------------
   // protected methods
   // ---------------------------------------------------------------

   /** 
    * Method reload to reload the values 
    * 
    * @author brod 
    */
   protected void reload()
   {
      if (_sTscFile == null)
         return;

      Xml xmlNew = loadXml(_sTscFile);
      String xmlString = getXmlString(false);
      if (xmlNew.toString().equals(xmlString)) {
         return;
      }

      if (_rootTestcasePage != null && _rootTestcasePage.isDirty()) {

         if (messageBox != null)
            return;

         messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
         messageBox.setText(_sTscFile.substring(_sTscFile.lastIndexOf("/") + 1) + " Changed");
         messageBox.setMessage("The file has been changed on the file system. Do you want\n"
               + "to load the changes within the TestcaseEditor?");

         if (messageBox.open() != SWT.YES) {
            messageBox = null;
            return;
         }
         messageBox = null;

      }

      // clear the list of expanded
      clearExpanded();
      // remember the current state 
      rememberExpanded(getTreeTestcases().getItems());

      _xmlTestData = xmlNew;
      String testObjectClass = getTestObjectClass();
      _xmlTestData.setAttribute("testObjectClass", testObjectClass);
      initTreeTestcases(getTreeTestcases());
      // set the title
      if (testObjectClass.indexOf(".") > 0) {
         String sName = testObjectClass.substring(testObjectClass.lastIndexOf(".") + 1);
         if (testObjectClass.indexOf(".agents.") > 0) {
            String sPackage = testObjectClass
                  .substring(testObjectClass.lastIndexOf(".agents.") + 8);
            if (sPackage.indexOf(".") > 0) {
               sName += " (" + Util.getCamelCase(sPackage.substring(0, sPackage.indexOf(".")));
               sPackage = sPackage.substring(sPackage.indexOf(".") + 1);
               if (sPackage.indexOf(".") > 0) {
                  sName += Util.getCamelCase(sPackage.substring(0, sPackage.indexOf(".")));
               }
               sName += ")";
            }
         }
         getCLabelTitle().setText(sName);
      }

      clickTreeTestcases(getTreeTestcases());
      if (_rootTestcasePage != null)
         _rootTestcasePage.setDirty(false);

      // clear the list of expanded
      clearExpanded();

   }

   /** 
    * The method getXmlString returns a XmlString which accords 
    * to the values of the current tree 
    * 
    * @param pbGetRoot if the xmlString should contain 
    * the root element 
    * @return The XmlString (which represents the current state 
    * of the treeItems 
    * 
    * @author brod 
    */
   public String getXmlString(boolean pbGetRoot)
   {
      Xml xmlRoot = new Xml("<Test />");
      Xml xmlSave = xmlRoot.getFirstObject();
      xmlSave.setAttribute("testObjectClass", getTestObjectClass());
      // loop through tree
      TreeItem[] items = getTreeTestcases().getItems();

      for (int i = 0; i < items.length; i++) {
         addItems(items[i], xmlSave);
      }
      if (pbGetRoot)
         return xmlRoot.toString();
      return xmlSave.toString();
   }

   // ---------------------------------------------------------------
   // private methods
   // ---------------------------------------------------------------

   /** 
    * The method getBaseClassType contains a list of all known 
    * BaseClass Types. This should be enhanced if there is 
    * a new BaseClassTyp 
    * 
    * @param psName The name of the Class 
    * @return The text which should be displayed 
    * 
    * @author brod 
    */
   private String getBaseClassType(String psName)
   {

      if (psName.endsWith(".ConsistencyRule")) {
         return "ConsistencyRule";
      }
      if (psName.endsWith(".PnrElementBase")) {
         return "BusinessElement";
      }
      if (psName.endsWith(".Testable")) {
         return "Testable";
      }
      if (psName.endsWith(".ResponseReader")) {
         return "ResponseReader";
      }
      if (psName.endsWith(".TransformerHandler")) {
         return "TransformerHandler";
      }
      if (psName.endsWith(".PnrElementComparator")) {
         return "PnrElementComparator";
      }
      if (psName.endsWith(".DataHistory")) {
         return "DataHistory";
      }

      try {

         Class<?> compiledClass = Util
               .getCompiledClass(_classesDirectory.getAbsolutePath(), psName);

         String baseClassType = getBaseClassType(compiledClass.getSuperclass().getName());

         if (!baseClassType.endsWith("-"))
            return baseClassType;

         // try interfaces
         Class[] interfaces = compiledClass.getInterfaces();
         for (int i = 0; i < interfaces.length; i++) {
            baseClassType = getBaseClassType(interfaces[i].getName());

            if (!baseClassType.endsWith("-"))
               return baseClassType;
         }

      }
      catch (Exception e) {}

      return "-unkown Type-";

   }

   /** 
    * The method getInitArrayList returns a list of available InitClasses 
    * for a specific directory. E.g. 
    * <code>/jUnitTest/net/ifao/ruletest/initialize</code> 
    * 
    * @param psDirectory The name of the directory 
    * @return a Hashtable with String 
    * 
    * @author brod 
    */
   private java.util.Hashtable<String, String> getInitArrayList(String psDirectory)
   {
      java.util.Hashtable<String, String> lst = new java.util.Hashtable<String, String>();
      if (_sTscFile.indexOf("/jUnitTest/") > 0) {
         addToArrayList(lst, new File(_sTscFile.substring(0, _sTscFile.indexOf("/jUnitTest/") + 11)
               + "net/ifao/" + _sBaseDir4Components + "/" + psDirectory));

      }
      return lst;
   }

   /** 
    * The private method addToArrayList is called from the getInitArrayList, 
    * which adds Annotataion of javaFiles to an arraylist 
    * 
    * @param phtList Hashtable with entries 
    * @param pDirectory The directory which has to be analysed 
    * 
    * @author brod 
    */
   private void addToArrayList(java.util.Hashtable<String, String> phtList, File pDirectory)
   {
      if (!pDirectory.exists())
         return;
      if (pDirectory.isDirectory()) {
         File[] files = pDirectory.listFiles();
         for (int i = 0; i < files.length; i++) {
            addToArrayList(phtList, files[i]);
         }
      } else {

         String sName = pDirectory.getAbsolutePath().replaceAll("\\\\", ".").replaceAll("\\/", ".");

         if (sName.endsWith(".java")) {
            sName = sName.substring(sName.lastIndexOf(".jUnitTest.") + 11, sName.length() - 5);

            if (_clsAnnotationTitle != null) {
               String sPath = pDirectory.getAbsolutePath();
               sPath = sPath.substring(0, sPath.indexOf("jUnitTest"));

               try {
                  Class compiledClass = Util.getCompiledClass(_classesDirectory.getAbsolutePath(),
                        sName);
                  while (compiledClass != null) {

                     Annotation[] annotations = compiledClass.getAnnotations();

                     TAnnotation annot;
                     for (int i = 0; i < annotations.length; i++)
                        if ((annot = addAnnotation(sName, annotations[i])) != null) {

                           phtList.put(annot.value, annot.desc);
                        }
                     // System.out.println(">>> " + compiledClass.getName());
                     Method[] methods = compiledClass.getDeclaredMethods();
                     for (int j = 0; j < methods.length; j++) {
                        Annotation[] annMethods = methods[j].getAnnotations();
                        for (int k = 0; k < annMethods.length; k++) {
                           addAnnotation(sName, annMethods[k]);
                        }
                     }
                     compiledClass = compiledClass.getSuperclass();
                  }

               }
               catch (java.lang.LinkageError e) {
                  e.printStackTrace();
               }
               catch (Exception e) {
                  // make nothing
               }
            }
         }
      }

   }

   /** 
    * The method addAnnotation associates a Annotations to a javaFile 
    * (Within the private member <code>_htCode</code>) 
    * 
    * @param psFileName The name of the javaFile 
    * @param pAnnotation The according annotation 
    * @return new TAnnotation object 
    * 
    * @author brod 
    */
   private TAnnotation addAnnotation(String psFileName, Annotation pAnnotation)
   {
      Class<? extends Annotation> annotationType = pAnnotation.annotationType();
      if (annotationType.getName().equals(_clsAnnotationTitle.getName())
            || annotationType.getName().equals(_clsAnnotationParam.getName())) {

         String value = getAnnotationTypeGetMethod(pAnnotation, annotationType, "value");
         String description = getAnnotationTypeGetMethod(pAnnotation, annotationType, "description");
         String components = getAnnotationTypeGetMethod(pAnnotation, annotationType, "components");

         TAnnotation tAnnot = new TAnnotation(value, description, components);
         // ("value=\"" + value + "\", description=\"" + description + "\"");

         if (tAnnot.components.indexOf(_sCaseType) < 0)
            return null;

         ArrayList<TAnnotation> lst = _htCode.get(psFileName);
         if (lst == null) {
            lst = new ArrayList<TAnnotation>();
            _htCode.put(psFileName, lst);
         }
         if (annotationType.getName().equals(_clsAnnotationTitle.getName()))
            lst.clear();
         lst.add(tAnnot);
         return tAnnot;

      }
      return null;

   }

   /** 
    * The method getAnnotationTypeGetMethod returns a specific value of the 
    * pAnnotation (per reflection) 
    * 
    * @param pAnnotation The Annotation 
    * @param pClass The class (which contains the method) 
    * @param psMethodName The methodName of the class 
    * @return The related String ("" of there was an exception) 
    * 
    * @author brod 
    */
   private String getAnnotationTypeGetMethod(Annotation pAnnotation,
                                             Class<? extends Annotation> pClass, String psMethodName)
   {
      try {
         return (String) pClass.getMethod(psMethodName, new Class[0]).invoke(pAnnotation,
               new Object[0]);
      }
      catch (Exception e) {}
      return "";
   }

   // ---------------------------------------------------------------
   // Override methods
   // ---------------------------------------------------------------

   /** 
    * Override method initLists 
    * 
    * @author brod 
    */
   @Override
   void initLists()
   {
      _htInit = getInitArrayList("initialize");
      _htValid = getInitArrayList("validate");

   }

   /** 
    * Override method htCodeGet 
    * 
    * @param psAttribute param attribute 
    * @return 
    * 
    * @author brod 
    */
   @Override
   ArrayList<TAnnotation> htCodeGet(String psAttribute)
   {
      return _htCode.get(psAttribute);
   }

   /** 
    * Override method getInitItems 
    * 
    * @return 
    * 
    * @author brod 
    */
   @Override
   Hashtable<String, String> getInitItems()
   {
      return _htInit;
   }

   /** 
    * Override method getValidItems 
    * 
    * @return 
    * 
    * @author brod 
    */
   @Override
   Hashtable<String, String> getValidItems()
   {

      return _htValid;
   }

   /** 
    * Override method getClass 
    * 
    * @param psExpCase param expCase 
    * @param pbSearchInInit param pbSearchInInit 
    * @return 
    * 
    * @author brod 
    */
   @Override
   String getClass(String psExpCase, boolean pbSearchInInit)
   {
      Object[] keys = _htCode.keySet().toArray();
      for (int i = 0; i < keys.length; i++) {
         if (!pbSearchInInit
               && keys[i].toString().startsWith("net.ifao." + _sBaseDir4Components + ".initialize")) {
            continue;
         }
         if (pbSearchInInit
               && !keys[i].toString()
                     .startsWith("net.ifao." + _sBaseDir4Components + ".initialize")) {
            continue;
         }
         ArrayList<TAnnotation> name = _htCode.get(keys[i]);
         if (name.get(0).value.equals(psExpCase)) {
            return keys[i].toString();
         }
      }
      return null;
   }

   /** 
    * Override method loadXml 
    * 
    * @return 
    * 
    * @author brod 
    */
   @Override
   Xml loadXml()
   {
      return loadXml(_sTestTemplatesXml);
   }

   /** 
    * Override method loadXml 
    * 
    * @param psPath param psPath 
    * @return 
    * 
    * @author brod 
    */
   private Xml loadXml(String psPath)
   {
      Xml loadXml = null;
      try {
         loadXml = (new Xml(new File(psPath))).getFirstObject();
      }
      catch (Exception e) {
         loadXml = null;
      }
      if (loadXml == null) {
         // make empty object
         loadXml = (new Xml("<Test />")).getFirstObject();
      }
      return loadXml;
   }

   /** 
    * Override method getValueText 
    * 
    * @param pXmlValue param xmlValue 
    * @return 
    * 
    * @author brod 
    */
   @Override
   String getValueText(Xml pXmlValue)
   {
      String sValue = pXmlValue.getAttribute("class");
      ArrayList<TAnnotation> name = _htCode.get(sValue);
      if (name != null && name.size() > 0) {
         return name.get(0).value;
      }
      return sValue;
   }

   /** 
    * Override method getTestTemplatesXml 
    * 
    * @return 
    * 
    * @author brod 
    */
   @Override
   String getTestTemplatesXml()
   {
      return _sTestTemplatesXml;
   }

   /** 
    * Override method getTscPath 
    * 
    * @return 
    * 
    * @author brod 
    */
   @Override
   String getTscPath()
   {
      return _sTscFile;
   }

   /** 
    * Override method runFile 
    * 
    * @param psFile2Run param psFile2Run 
    * @param pbDeleteFile2Run param pbDeleteFile2Run 
    * 
    * @author brod 
    */
   @Override
   protected void runFile(String psFile2Run, boolean pbDeleteFile2Run)
   {
      if (_rootTestcasePage != null && _rootTestcasePage.isDirty()) {
         _rootTestcasePage.doSave(null);
      }

      if (_testcaseRunner != null && _testcaseRunner.isAlive()) {
         MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
         messageBox.setText("JUnit-Process");
         messageBox.setMessage("There is anothe JUnitProcess running.\n"
               + "Please try again if other process has finished.");
         messageBox.open();

      } else {
         Display display = getParent().getDisplay();
         // Save TestObject Before Running A JUnit Test
         display.syncExec(new Runnable()
         {
            @Override
            @SuppressWarnings("restriction")
            public void run()
            {
               try {
                  Workbench.getInstance().saveAllEditors(true);
               }
               catch (Exception ex) {}
            }
         });

         JUnitWait unitWait = new JUnitWait(_abstractUIPlugin, psFile2Run);
         unitWait.show();

         _testcaseRunner = new TestcaseRunner(display, psFile2Run, pbDeleteFile2Run, unitWait);
         _testcaseRunner.start();
      }
   }

   /** 
    * Override method getXmlTestCases 
    * 
    * @return 
    * 
    * @author brod 
    */
   @Override
   Xml[] getXmlTestCases()
   {
      if (_xmlTestData == null)
         return new Xml[0];

      return _xmlTestData.getObjects("TestCase");
   }

   /** 
    * Override method getTestObjectClass 
    * 
    * @return 
    * 
    * @author brod 
    */
   @Override
   String getTestObjectClass()
   {
      int iNetIfao = _sTscFile.indexOf("/net/ifao/");
      if (iNetIfao > 0 && _sTscFile.indexOf(".", iNetIfao) > 0) {
         return _sTscFile.substring(iNetIfao + 1, _sTscFile.lastIndexOf("."))
               .replaceAll("\\/", ".");
      }
      return "";
   }

   /** 
    * Override method clickButtonTCSrc 
    * 
    * @param pButtonTCSrc param pButtonTCSrc 
    * 
    * @author brod 
    */
   @Override
   protected void clickButtonTCSrc(Button pButtonTCSrc)
   {
      _rootTestcasePage.openSrc();

   }

}
