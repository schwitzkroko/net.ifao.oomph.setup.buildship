package net.ifao.plugins.editor.testcase;


import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;

import net.ifao.plugins.dialog.InputCombo;
import net.ifao.xml.Xml;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;


/**
 * The class Testcase contains the main Implementation for the TestCaseAdapter
 * (which is automatically generated)
 * 
 * <p>
 * Copyright &copy; 2007, i:FAO
 * 
 * @author brod
 */
public abstract class Testcase
   extends TestcaseAdapter
{

   private Class _abstractUIPlugin;

   private TreeItem _selectedTreeItem = null;

   private TestcasePage _rootTestcasePage;

   /**
    * Constructor Testcase with the following parameters:
    * 
    * @param pBusinessRulesPage The TestcasePage-BusinessRulesPage
    * @param pParent The parent class (GUI)
    * @param pAbstractUIPlugin The Abstract user interface
    * @param pFile The FileName (for this testcase)
    * 
    * @author brod
    */
   public Testcase(TestcasePage pBusinessRulesPage, Composite pParent, Class pAbstractUIPlugin,
                   File pFile)
   {
      super(pParent, pAbstractUIPlugin);
      _rootTestcasePage = pBusinessRulesPage;
      _abstractUIPlugin = pAbstractUIPlugin;

      initAdapter(pParent);

      getTreeTestcases().addKeyListener(new KeyListener()
      {

         @Override
         public void keyPressed(KeyEvent e)
         {
            if (e.character == 127 && e.stateMask == SWT.NONE) {
               // react to delete
               if (getButtonTCDelete().isEnabled()) {
                  clickButtonTCDelete(getButtonTCDelete());
               }
            } else if (e.character == 3 && e.stateMask == SWT.CTRL) {
               // react to ctrl-c
               if (getButtonTCCopy().isEnabled()) {
                  clickButtonTCCopy(getButtonTCCopy());
               }
            } else if (e.character == 22 && e.stateMask == SWT.CTRL) {
               // react to ctrl-v
               if (getButtonTCPaste().isEnabled()) {
                  clickButtonTCPaste(getButtonTCPaste());
               }
            }
         }

         @Override
         public void keyReleased(KeyEvent e)
         {
            //make nothing

         }

      });

   }

   // ---------------------------------------------------------------------------
   // Abstract methods
   // ---------------------------------------------------------------------------
   abstract Hashtable<String, String> getValidItems();

   abstract Hashtable<String, String> getInitItems();

   abstract String getClass(String preCase, boolean pbSearchInInit);

   abstract void initLists();

   abstract String getTscPath();

   abstract String getTestTemplatesXml();

   abstract ArrayList<TAnnotation> htCodeGet(String attribute);

   abstract String getValueText(Xml xmlValue);

   abstract Xml loadXml();

   abstract void runFile(String psFile2Run, boolean pbDeleteFile2Run);

   abstract String getTestObjectClass();

   abstract Xml[] getXmlTestCases();

   // ---------------------------------------------------------------------------
   // 'public' methods
   // ---------------------------------------------------------------------------

   /**
    * The method setChange sets the dirty flag, which indicates eclipse,
    * that this file is changed.
    * 
    * @author brod
    */
   public void setChange()
   {
      if (_rootTestcasePage != null) {
         _rootTestcasePage.setDirty(true);
      }
   }

   // ---------------------------------------------------------------------------
   // 'private' methods
   // ---------------------------------------------------------------------------

   /**
    * method getItem returns the xml-value of the treeItem
    * 
    * @param pItem The  TreeItem
    * @return according XmlObject
    * 
    * @author brod
    */
   private Xml getItem(TreeItem pItem)
   {
      Xml itemObject = ((Xml) pItem.getData()).copy();
      // remove subobjects
      itemObject.deleteObjects("");
      // remove description
      itemObject.setAttribute("desc", null);

      // loop over the subitems
      TreeItem[] items = pItem.getItems();
      for (TreeItem item : items) {
         addItems(item, itemObject);
      }
      return itemObject;

   }

   /**
    * method addItems adds:
    * 
    * @param pItem The TreeItem which contains the xml
    * @param pXml The XmlObject which is added
    * 
    * @author brod
    */
   protected void addItems(TreeItem pItem, Xml pXml)
   {
      // add this object
      pXml.addObject(getItem(pItem));

   }

   /**
    * The method getPath returns the name of the Path (containing
    * the parent data)
    * 
    * @param pItem The TreeItem
    * @return According path
    * 
    * @author brod
    */
   protected String getPath(TreeItem pItem)
   {
      if (pItem == null) {
         return "---";
      }
      Xml data = (Xml) pItem.getData();
      String sItem = "";
      sItem = data.getAttribute("name") + data.getAttribute("class");
      Xml parentData = data.getParent();
      while (parentData != null) {
         if (!sItem.startsWith(".")) {
            sItem += ".";
         }
         sItem = parentData.getAttribute("name") + parentData.getAttribute("class") + "." + sItem;
         parentData = parentData.getParent();
      }
      return sItem;

   }

   /**
    * method selectIfPath returns if the path is selected
    * 
    * @param pRootTree The root element
    * @param pItem The TreeItem
    * @param psPath The pathString
    * @return true if path is selected
    * 
    * @author brod
    */
   private boolean selectIfPath(Tree pRootTree, TreeItem pItem, String psPath)
   {
      if (psPath.equals(getPath(pItem))) {
         pRootTree.setSelection(pItem);
         return true;
      }
      TreeItem[] items = pItem.getItems();
      boolean bOk = false;
      for (int i = 0; !bOk && i < items.length; i++) {
         bOk = selectIfPath(pRootTree, items[i], psPath);
      }

      return bOk;
   }

   /**
    * The method setTestCase sets a testcase
    * 
    * @param pItem The TreeItem
    * @param pXmlTestCase The XmlObject containing the description
    * 
    * @author brod
    */
   private void setTestCase(TreeItem pItem, Xml pXmlTestCase)
   {
      pItem.setText(pXmlTestCase.getAttribute("name") + " - "
            + pXmlTestCase.getAttribute("description"));

   }

   /**
    * method addTestCase adds a xmlTestcase Object to a new
    * TreeItem
    * 
    * @param pXmlTestCase The Xml TestCase
    * @param pRootItem TreeItem to which the new element is added
    * @param piIndex The Index (if <0 it will be added to the end)
    * @return new TreeItem object which was added
    * 
    * @author brod
    */
   private TreeItem addTestCase(Xml pXmlTestCase, Tree pRootItem, int piIndex)
   {
      TreeItem item;
      if (piIndex < 0) {
         item = new TreeItem(pRootItem, SWT.NONE);
      } else {
         item = new TreeItem(pRootItem, SWT.NONE, piIndex);
      }
      setTestCase(item, pXmlTestCase);
      item.setText(pXmlTestCase.getAttribute("name") + " - "
            + pXmlTestCase.getAttribute("description"));
      setEnabledTestCase(item, pXmlTestCase);
      item.setData(pXmlTestCase);

      // get the Preconditions
      Xml[] precondition = pXmlTestCase.getObjects("Precondition");
      for (Xml element : precondition) {
         addSubItem(item, element);
      }
      // get the Expectations
      Xml[] expectation = pXmlTestCase.getObjects("Expectation");
      for (Xml element : expectation) {
         addSubItem(item, element);
      }
      setExpanded(item);
      return item;
   }

   /**
    * @param pItem
    * 
    */
   private void setExpanded(TreeItem pItem)
   {
      pItem.setExpanded(!hsNotExpanded.contains(getPath(pItem)));
   }

   HashSet<String> hsNotExpanded = new HashSet<String>();

   /**
    * 
    */
   protected void clearExpanded()
   {
      hsNotExpanded.clear();
   }

   /**
    * @param pItems
    * 
    */
   protected void rememberExpanded(TreeItem[] pItems)
   {
      for (TreeItem item : pItems) {
         if (item.getExpanded()) {
            TreeItem[] items = item.getItems();
            rememberExpanded(items);
         } else {
            hsNotExpanded.add(getPath(item));
         }

      }
   }

   /**
    * The method setEnabledTestCase enebles the testcase
    * 
    * @param pItem The TreeItem
    * @param pXmlTestCase according XmlTestcase object
    * 
    * @author brod
    */
   private void setEnabledTestCase(TreeItem pItem, Xml pXmlTestCase)
   {
      boolean bDisabled =
         pXmlTestCase.getAttribute("enabled").equalsIgnoreCase("no")
               || pXmlTestCase.getAttribute("enabled").equalsIgnoreCase("false");
      if (bDisabled) {
         pItem.setImage(getIcon("testcaseDisabled.gif"));
      } else {
         pItem.setImage(getIcon("testcase.gif"));
      }
      if (pItem == _selectedTreeItem) {
         getMenuItemDisableTestcase().setEnabled(!bDisabled);
         MenuItem menuItemActivateTestcase = getMenuItemActivateTestcase();
         if (bDisabled) {
            menuItemActivateTestcase.setText("Activate this testcase");
            menuItemActivateTestcase.setEnabled(true);
            getMenuItemActivateAllTestcases().setEnabled(true);
         } else if (containsOtherEnabledItems()) {
            menuItemActivateTestcase.setText("Activate only this testcase");
            menuItemActivateTestcase.setEnabled(true);
            getMenuItemActivateAllTestcases().setEnabled(true);
         } else {
            menuItemActivateTestcase.setText("Activate this testcases");
            menuItemActivateTestcase.setEnabled(false);
         }
      }

   }

   /**
    * The method containsOtherEnabledItems returns if there are
    * other enabled items (than the selected one)
    * 
    * @return if contains other EnabledItems
    * 
    * @author brod
    */
   private boolean containsOtherEnabledItems()
   {
      TreeItem[] items = getTreeTestcases().getItems();
      for (TreeItem item : items) {
         if (item != _selectedTreeItem) {
            Xml xml = (Xml) item.getData();
            if (!xml.getAttribute("enabled").equals("false")) {
               return true;
            }
         }
      }
      return false;

   }

   /**
    * The method addSubItem adds a new SubItem element
    * 
    * @param pRootItem The TreeItem
    * @param pXmlObject The Xml object
    * @return The new  TreeItem
    * 
    * @author brod
    */
   private TreeItem addSubItem(TreeItem pRootItem, Xml pXmlObject)
   {
      int iIndex = pRootItem.getItemCount();
      if (pXmlObject.getName().equals("Expectation")) {
         // make nothing
      } else {
         // search for last precondition
         iIndex = 0;
         while (iIndex < pRootItem.getItemCount()
               && ((Xml) pRootItem.getItem(iIndex).getData()).getName().equals("Precondition")) {
            iIndex++;
         }
      }
      TreeItem subItem = new TreeItem(pRootItem, SWT.NONE, iIndex);
      subItem.setText(getValueText(pXmlObject));
      subItem.setImage(pXmlObject.getName().equals("Precondition") ? getIcon("precondition.gif")
            : getIcon("validator.gif"));
      subItem.setData(pXmlObject);
      setExpanded(pRootItem);

      // init empty elements
      if (pXmlObject != null) {
         String attribute = pXmlObject.getAttribute("class");
         ArrayList<TAnnotation> name = htCodeGet(attribute);

         if (name != null && name.size() > 0) {
            pXmlObject.setAttribute("desc", (name.get(0)).desc);
            ArrayList<TAnnotation> lst = new ArrayList<TAnnotation>();
            for (int i = 1; i < name.size(); i++) {
               lst.add(name.get(i));
            }
            if (lst.size() > 0) {
               String sPreCase;

               for (int i = 0; i < lst.size(); i++) {
                  sPreCase = lst.get(i).value;
                  Xml value = pXmlObject.findSubObject("Value", "name", sPreCase);
                  if (value == null) {
                     value = (new Xml("<Value />")).getFirstObject();
                     value.setAttribute("name", sPreCase);
                     pXmlObject.addObject(value);
                  }
                  value.setAttribute("desc", lst.get(i).desc);
               }
               // set all values
               Xml[] values = pXmlObject.getObjects("Value");
               for (int i = 0; i < values.length; i++) {

                  TreeItem valItem = new TreeItem(subItem, SWT.NONE, i);
                  valItem.setText(values[i].getAttribute("name"));
                  valItem.setData(values[i]);
                  validateDetail(valItem, values[i], values[i].getCData());

               }
            }
         }
      }

      setExpanded(subItem);

      return subItem;

   }

   /**
    * The method getSelectedTreeItemIndex returns the index of the
    * selected TreeItem
    * 
    * @return index of the selected TreeItem
    * 
    * @author brod
    */
   private int getSelectedTreeItemIndex()
   {
      TreeItem[] items = getTreeTestcases().getItems();
      for (int i = 0; i < items.length; i++) {
         if (items[i] == _selectedTreeItem) {
            return i;
         }
      }
      return -1;
   }

   /**
    * The method moveTreeItem moves a TreeItem
    * 
    * @param piDirection "+-" value which moves the Index
    * 
    * @author brod
    */
   private void moveTreeItem(int piDirection)
   {
      if (_selectedTreeItem == null) {
         return;
      }
      int iIndex = getSelectedTreeItemIndex();
      if (iIndex >= 0) {
         iIndex += piDirection;
         if (iIndex < 0) {
            iIndex = 0;
         } else if (iIndex >= getTreeTestcases().getItemCount()) {
            iIndex = -1;
         }
      }

      Xml xmlData = (Xml) _selectedTreeItem.getData();
      TreeItem item = addTestCase(xmlData, getTreeTestcases(), iIndex);

      _selectedTreeItem.dispose();
      getTreeTestcases().setSelection(item);
      clickTreeTestcases(getTreeTestcases());
      setChange();
   }

   /**
    * The method setTextValues sets the values of an XmlObject to the
    * text fields (class and desc)
    * 
    * @param pXmlObject The Xml object (with attributes class and desc)
    * 
    * @author brod
    */
   private void setTextValues(Xml pXmlObject)
   {
      getTextCompClass().setText(pXmlObject.getAttribute("class"));
      getTextCompDesc().setText(pXmlObject.getAttribute("desc"));
   }

   /**
    * The method validateDetail validates the detail and sets
    * related Icons (within the TreeItem object)
    * 
    * @param pItem The TreeItem
    * @param pXml The XmlObject
    * @param psText The containing text
    * 
    * @author brod
    */
   private void validateDetail(TreeItem pItem, Xml pXml, String psText)
   {
      if (pXml.getName().equals("Value")) {
         Image img;
         if (psText.startsWith("[") && psText.endsWith("]")) {
            img = getIcon("valueRed.gif");
         } else if (psText.length() == 0) {
            img = getIcon("value_empty.gif");
         } else {
            img = getIcon("value.gif");
         }
         if (img == pItem.getImage()) {
            // make nothing
         } else {
            pItem.setImage(img);
         }
      }

   }

   // ---------------------------------------------------------------------------
   // Methods, which override Adapter Methods
   // ---------------------------------------------------------------------------

   /**
    * Override method initTreeTestcases
    * 
    * @param pTestcases set param pTestcases
    * 
    * @author brod
    */
   @Override
   protected void initTreeTestcases(Tree pTestcases)
   {
      String sPathSelected = getPath(_selectedTreeItem);

      // load the data
      pTestcases.removeAll();

      Xml[] testCase = getXmlTestCases();
      for (Xml element : testCase) {
         // set the root Menu
         addTestCase(element, pTestcases, -1);
      }

      // find selected testcase
      TreeItem[] items = pTestcases.getItems();
      for (TreeItem item : items) {
         selectIfPath(pTestcases, item, sPathSelected);
      }

   }

   /**
    * Override method clickTreeTestcases
    * 
    * @param pTreeTestcases set param pTreeTestcases
    * 
    * @author brod
    */
   @Override
   protected void clickTreeTestcases(Tree pTreeTestcases)
   {
      // add click functionallity
      TreeItem[] selection = pTreeTestcases.getSelection();
      getTreeTestcases().setMenu(null);
      getButtonTCCopy().setEnabled(false);
      getButtonTCUp().setEnabled(false);
      getButtonTCDown().setEnabled(false);

      String sDetail = "";
      if (selection.length > 0) {
         _selectedTreeItem = selection[0];

         // get the itemId
         int iSelected = -1;
         TreeItem[] items = pTreeTestcases.getItems();
         for (int i = 0; iSelected < 0 && i < items.length; i++) {
            if (items[i] == _selectedTreeItem) {
               iSelected = i;
            }
         }

         getButtonTCUp().setEnabled(iSelected > 0);
         getButtonTCDown().setEnabled(iSelected >= 0 && iSelected + 1 < items.length);

         // selected get data
         try {
            Xml xml = (Xml) _selectedTreeItem.getData();

            // set the add Button
            getButtonTCDelete().setEnabled(true);

            if (xml == null) {
               setGroupDetailPage(0);
               getButtonTCDelete().setEnabled(false);

            } else if (xml.getName().equals("TestCase")) {
               setGroupDetailPage(1);
               getTextNameCase().setText(xml.getAttribute("name"));
               getTextDescriptionCase().setText(xml.getAttribute("description"));

               // activate menu
               getTreeTestcases().setMenu(getMenuTestcaseMenu());

               setEnabledTestCase(_selectedTreeItem, xml);

               getButtonTCCopy().setEnabled(true);

            } else if (xml.getName().equals("Expectation")) {
               setGroupDetailPage(2);
               setTextValues(xml);
               sDetail = xml.getParent().getAttribute("name");

            } else if (xml.getName().equals("Precondition")) {
               setGroupDetailPage(2);
               setTextValues(xml);
               sDetail = xml.getParent().getAttribute("name");
            } else if (xml.getName().equals("Value")) {
               setGroupDetailPage(3);
               getCLabelDetailText().setText("Value for " + xml.getAttribute("name"));
               getTextDetailDesc().setText(xml.getAttribute("desc"));
               getTextDetail().setText(xml.getCData());
               getButtonTCDelete().setEnabled(false);
               sDetail = xml.getParent().getParent().getAttribute("name");
            }

         }
         catch (Exception e) {}
      } else {
         setGroupDetailPage(0);
         getButtonTCDelete().setEnabled(false);

      }
      if (sDetail.length() == 0) {
         sDetail = "Testcase detail";
      } else {
         sDetail = "Testcase: " + sDetail;
      }
      getGroupDetail().setText(sDetail);
   }

   /**
    * Override method clickMenuItemMenuPre
    * 
    * @param pMenuItemMenuPre set param pMenuItemMenuPre
    * 
    * @author brod
    */
   @Override
   protected void clickMenuItemMenuPre(MenuItem pMenuItemMenuPre)
   {
      initLists();

      InputCombo testcase =
         new InputCombo(_abstractUIPlugin, getInitItems(), "Select Precondition-Class");
      testcase.show();

      String sPreCase = testcase.getReturnValue();
      if (sPreCase.length() > 0) {

         // add click functionallity
         Xml testCase = (Xml) _selectedTreeItem.getData();
         if (testCase.getName().equals("TestCase")) {
            Xml precondition = (new Xml("<Precondition />")).getFirstObject();
            precondition.setAttribute("class", getClass(sPreCase, true));
            addSubItem(_selectedTreeItem, precondition);
            setChange();
         }
      }
   }

   /**
    * Override method clickMenuItemMenuValid
    * 
    * @param pMenuItemMenuValid set param pMenuItemMenuValid
    * 
    * @author brod
    */
   @Override
   protected void clickMenuItemMenuValid(MenuItem pMenuItemMenuValid)
   {
      initLists();
      InputCombo testcase =
         new InputCombo(_abstractUIPlugin, getValidItems(), "Select Expectation-Class");
      testcase.show();

      String sExpCase = testcase.getReturnValue();
      if (sExpCase.length() > 0) { // add click functionallity
         Xml testCase = (Xml) _selectedTreeItem.getData();
         if (testCase.getName().equals("TestCase")) {
            Xml precondition = (new Xml("<Expectation />")).getFirstObject();
            precondition.setAttribute("class", getClass(sExpCase, false));
            addSubItem(_selectedTreeItem, precondition);
            setChange();
         }
      }
   }

   /**
    * Override method clickButtonTCAdd
    * 
    * @param pButtonTCAdd set param pButtonTCAdd
    * 
    * @author brod
    */
   @Override
   protected void clickButtonTCAdd(Button pButtonTCAdd)
   {
      // add click functionallity
      AddTestcase testcase = new AddTestcase(_abstractUIPlugin, loadXml());
      testcase.show();

      String sTestCase = testcase.getTestCase();
      if (sTestCase.length() > 0) {
         Xml xml = testcase.getSelectedTemplate();
         if (xml == null) {
            xml = (new Xml("<TestCase />")).getFirstObject();
         }
         xml.setAttribute("name", sTestCase);
         TreeItem item = addTestCase(xml, getTreeTestcases(), -1);
         getTreeTestcases().setSelection(item);
         clickTreeTestcases(getTreeTestcases());
         setChange();
      }
   }

   /**
    * Override method clickButtonTCDelete
    * 
    * @param pButtonTCDelete set param pButtonTCDelete
    * 
    * @author brod
    */
   @Override
   protected void clickButtonTCDelete(Button pButtonTCDelete)
   {
      if (_selectedTreeItem == null || _selectedTreeItem.isDisposed()) {
         return;
      }
      // add click functionallity
      Xml Xml = (Xml) _selectedTreeItem.getData();
      if (Xml.getName().equals("TestCase")) {
         _selectedTreeItem.dispose();
         setChange();
         clickTreeTestcases(getTreeTestcases());
      } else if (Xml.getName().equals("Precondition") || Xml.getName().equals("Expectation")) {
         Xml.getParent().deleteObjects(Xml);
         _selectedTreeItem.dispose();
         setChange();
      }
   }

   /**
    * Override method clickButtonTCCopy
    * 
    * @param pButtonTCCopy set param pButtonTCCopy
    * 
    * @author brod
    */
   @Override
   protected void clickButtonTCCopy(Button pButtonTCCopy)
   {
      // add click functionallity
      Clipboard systemClip = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
      StringSelection cont = new StringSelection(getItem(_selectedTreeItem).toString());
      systemClip.setContents(cont, null);
   }

   /**
    * Override method clickButtonTCPaste
    * 
    * @param pButtonTCPast set param pButtonTCPast
    * 
    * @author brod
    */
   @Override
   protected void clickButtonTCPaste(Button pButtonTCPast)
   {
      // add click functionallity
      Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable transferData = systemClipboard.getContents(null);
      for (DataFlavor dataFlavor : transferData.getTransferDataFlavors()) {
         Object content;
         try {
            content = transferData.getTransferData(dataFlavor);
            if (content instanceof String) {
               Xml xml = (new Xml((String) content)).getFirstObject();
               if (xml.getName().equalsIgnoreCase("TestCase")) {
                  xml.setAttribute("name", "Copy of " + xml.getAttribute("name"));
                  int iIndex = getSelectedTreeItemIndex();
                  if (iIndex >= 0) {
                     iIndex++;
                  }
                  TreeItem item = addTestCase(xml, getTreeTestcases(), iIndex);
                  getTreeTestcases().setSelection(item);
                  clickTreeTestcases(getTreeTestcases());
                  setChange();
               }

               return;
            }
         }
         catch (Exception e) {}
      }
   }

   /**
    * Override method modifyTextNameCase
    * 
    * @param pTextNameCase set param pTextNameCase
    * 
    * @author brod
    */
   @Override
   protected void modifyTextNameCase(Text pTextNameCase)
   {
      // add modify functionallity
      Xml Xml = (Xml) _selectedTreeItem.getData();
      if (Xml.setAttribute("name", pTextNameCase.getText())) {
         setChange();
      }

      setTestCase(_selectedTreeItem, Xml);
   }

   /**
    * Override method modifyTextDescriptionCase
    * 
    * @param pTextDescriptionCase set param pTextDescriptionCase
    * 
    * @author brod
    */
   @Override
   protected void modifyTextDescriptionCase(Text pTextDescriptionCase)
   {
      Xml Xml = (Xml) _selectedTreeItem.getData();
      if (Xml.setAttribute("description", pTextDescriptionCase.getText())) {
         setChange();
      }

      setTestCase(_selectedTreeItem, Xml);
   }

   /**
    * Override method modifyTextDetail
    * 
    * @param pTextDetail set param pTextDetail
    * 
    * @author brod
    */
   @Override
   protected void modifyTextDetail(Text pTextDetail)
   {
      Xml xml = (Xml) _selectedTreeItem.getData();
      if (xml != null && xml.getName().equals("Value")) {
         String sText = pTextDetail.getText();
         if (!xml.getCData().equals(sText)) {
            validateDetail(_selectedTreeItem, xml, sText);
            xml.setCData(sText);
            setChange();
         }
      }

   }

   /**
    * Override method initTextCompDesc
    * 
    * @param pTextCompDesc set param pTextCompDesc
    * 
    * @author brod
    */
   @Override
   protected void initTextCompDesc(Text pTextCompDesc)
   {
      pTextCompDesc.setEditable(false);
      // pTextCompDesc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
   }

   /**
    * Override method modifyTextCompDesc
    * 
    * @param pTextCompDesc set param pTextCompDesc
    * 
    * @author brod
    */
   @Override
   protected void modifyTextCompDesc(Text pTextCompDesc)
   {
      // make nothing

   }

   /**
    * Override method modifyTextCompClass
    * 
    * @param pTextCompClass set param pTextCompClass
    * 
    * @author brod
    */
   @Override
   protected void modifyTextCompClass(Text pTextCompClass)
   {
      // make nothing

   }

   /**
    * Override method clickMenuItemMenuAdd2Template
    * 
    * @param pMenuItemMenuAdd2Template set param pMenuItemMenuAdd2Template
    * 
    * @author brod
    */
   @Override
   protected void clickMenuItemMenuAdd2Template(MenuItem pMenuItemMenuAdd2Template)
   {
      Xml testCases = loadXml();
      Xml addItem = getItem(_selectedTreeItem);
      String sName = addItem.getAttribute("name");

      // try to find if already available
      boolean bOk = true;

      Xml[] testCase = testCases.getObjects("TestCase");
      for (int i = 0; bOk && i < testCase.length; i++) {
         if (testCase[i].getAttribute("name").equals(sName)) {
            testCases.deleteObjects(testCase[i]);
            bOk = false;
         }
      }

      MessageBox messageBox =
         new MessageBox(getShell(), (bOk ? SWT.ICON_QUESTION : SWT.ICON_WARNING) | SWT.YES | SWT.NO);
      if (bOk) {
         messageBox.setText("Append " + sName);
         messageBox.setMessage("Do you really want to add Testcase '" + sName
               + "' to the Template-file.");
      } else {
         messageBox.setText("Replace " + sName);
         messageBox.setMessage("Do you really want to replace Testcase '" + sName
               + "' within the Template-file.");

      }
      if (messageBox.open() == SWT.YES) {

         testCases.addObject(addItem);
         try {
            File f = new File(getTestTemplatesXml());
            if (!f.getParentFile().exists()) {
               f.getParentFile().mkdirs();
            }
            FileWriter writer = new FileWriter(f);
            writer.write(testCases.toString());
            writer.close();
         }
         catch (IOException e) {}
      }
   }

   public static void showError(Exception ex, Shell shell)
   {
      try {
         MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         ex.printStackTrace(new PrintStream(out));
         messageBox.setText("Exception " + ex.getLocalizedMessage() + "\n" + out.toString());
         messageBox.open();
      }
      catch (Exception e) {
         // could not display error
      }
   }

   /**
    * Override method clickButtonTCRun
    * 
    * @param pButtonTCRun set param pButtonTCRun
    * 
    * @author brod
    */
   @Override
   protected void clickButtonTCRun(Button pButtonTCRun)
   {
      runFile(getTscPath(), false);
   }

   /**
    * Override method clickButtonTCUp
    * 
    * @param pButtonTCUp set param pButtonTCUp
    * 
    * @author brod
    */
   @Override
   protected void clickButtonTCUp(Button pButtonTCUp)
   {
      moveTreeItem(-1);
   }

   /**
    * Override method clickButtonTCDown
    * 
    * @param pButtonTCDown set param pButtonTCDown
    * 
    * @author brod
    */
   @Override
   protected void clickButtonTCDown(Button pButtonTCDown)
   {
      moveTreeItem(+2);
   }

   /**
    * Override method clickMenuItemMenuRunTestcase
    * 
    * @param pMenuItemMenuRunTestcase set param pMenuItemMenuRunTestcase
    * 
    * @author brod
    */
   @Override
   protected void clickMenuItemMenuRunTestcase(MenuItem pMenuItemMenuRunTestcase)
   {
      if (_selectedTreeItem != null) {

         Xml xmlRoot = new Xml("<Test />");
         Xml xmlSave = xmlRoot.getFirstObject();
         xmlSave.setAttribute("testObjectClass", getTestObjectClass());

         addItems(_selectedTreeItem, xmlSave);

         File f = new File(getTscPath().substring(0, getTscPath().lastIndexOf(".")) + "_test.tsc");
         try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write(xmlRoot.toString());
            writer.flush();
            writer.close();
            runFile(f.getAbsolutePath().replaceAll("\\\\", "/"), true);
         }
         catch (IOException e) {
            e.printStackTrace();
         }
      }

   }

   /**
    * Override method modifyTextDetailDesc
    * 
    * @param pTextDetailDesc set param pTextDetailDesc
    * 
    * @author brod
    */
   @Override
   protected void modifyTextDetailDesc(Text pTextDetailDesc)
   {
      // Make nothing

   }

   /**
    * Override method clickMenuItemActivateTestcase
    * 
    * @param pMenuItemActivateTestcase set param pMenuItemActivateTestcase
    * 
    * @author brod
    */
   @Override
   protected void clickMenuItemActivateTestcase(MenuItem pMenuItemActivateTestcase)
   {
      if (_selectedTreeItem == null || _selectedTreeItem.isDisposed()) {
         return;
      }

      // add click functionallity
      Xml testCase = (Xml) _selectedTreeItem.getData();
      if (testCase.getName().equals("TestCase")) {
         // disable all others
         String sDefault = containsOtherEnabledItems() ? "false" : "true";

         TreeItem[] items = getTreeTestcases().getItems();
         for (TreeItem item : items) {
            if (item != _selectedTreeItem) {
               Xml xml = (Xml) item.getData();
               xml.setAttribute("enabled", sDefault);
               setEnabledTestCase(item, xml);
            }
         }
         testCase.setAttribute("enabled", "true");
         setEnabledTestCase(_selectedTreeItem, testCase);
         setChange();
      }
   }

   /**
    * Override method clickMenuItemDisableTestcase
    * 
    * @param pMenuItemDisableTestcase set param pMenuItemDisableTestcase
    * 
    * @author brod
    */
   @Override
   protected void clickMenuItemDisableTestcase(MenuItem pMenuItemDisableTestcase)
   {
      if (_selectedTreeItem == null || _selectedTreeItem.isDisposed()) {
         return;
      }

      // add click functionallity
      Xml testCase = (Xml) _selectedTreeItem.getData();
      if (testCase.getName().equals("TestCase")) {
         testCase.setAttribute("enabled", "false");
         setEnabledTestCase(_selectedTreeItem, testCase);
         setChange();
      }

   }

   /**
    * Override method clickMenuItemActivateAllTestcases
    * 
    * @param pMenuItemActivateAllTestcases set param pMenuItemActivateAllTestcases
    * 
    * @author brod
    */
   @Override
   protected void clickMenuItemActivateAllTestcases(MenuItem pMenuItemActivateAllTestcases)
   {
      TreeItem[] items = getTreeTestcases().getItems();
      for (TreeItem item : items) {
         Xml xml = (Xml) item.getData();
         xml.setAttribute("enabled", "true");
         setEnabledTestCase(item, xml);
      }
      setChange();

   }

}
