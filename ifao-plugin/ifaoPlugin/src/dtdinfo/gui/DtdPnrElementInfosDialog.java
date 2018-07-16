package dtdinfo.gui;


import ifaoplugin.Util;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import net.ifao.dialogs.swing.InputString;
import net.ifao.xml.XmlObject;
import dtdinfo.DtdGenerator;


/**
 * Class DtdPnrElementInfosDialog
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
public class DtdPnrElementInfosDialog
   extends JDialog
{

   /**
    *
    */
   private static final long serialVersionUID = -1095538241592890523L;

   private XmlObject arcticRootObject = null;
   private XmlObject arcticPnrElementInfos = null;
   /**
    *
    */
   private JPanel jContentPane = null;
   private JSplitPane jSplitPane = null;
   private JScrollPane jScrollPaneLeft = null;
   private JScrollPane jScrollPane2 = null;
   private DtdAreaPanel jPanel = null;
   XmlObject schema;
   private JTree jTreeElement = null;

   private boolean _resetItems = true;

   /**
    * Method getTree
    *
    * @return
    * @author Andreas Brod
    */
   public JTree getTree()
   {
      return jTreeElement;
   }

   /**
    * This method initializes jSplitPane
    *
    * @return javax.swing.JSplitPane
    */
   private JSplitPane getJSplitPane()
   {
      if (jSplitPane == null) {
         jSplitPane = new JSplitPane();

         jSplitPane.setLeftComponent(getJScrollPaneLeft());
         jSplitPane.setRightComponent(getJScrollPane2());
      }

      return jSplitPane;
   }

   /**
    * This method initializes jScrollPaneLeft
    *
    * @return javax.swing.JScrollPane
    */
   private JScrollPane getJScrollPaneLeft()
   {
      if (jScrollPaneLeft == null) {
         jScrollPaneLeft = new JScrollPane();

         jScrollPaneLeft.setViewportView(getJTreeElement());
      }

      return jScrollPaneLeft;
   }

   /**
    * This method initializes jScrollPane2
    *
    * @return javax.swing.JScrollPane
    */
   private JScrollPane getJScrollPane2()
   {
      if (jScrollPane2 == null) {
         jScrollPane2 = new JScrollPane();

         jScrollPane2.setViewportView(getJPanel());
      }

      return jScrollPane2;
   }

   /**
    * This method initializes jPanel
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel()
   {
      if (jPanel == null) {
         jPanel = new DtdAreaPanel(this);
      }

      return jPanel;
   }

   /**
    *     This method initializes jTreeElement
    *
    *     @return javax.swing.JTree
    */
   protected JTree getJTreeElement()
   {
      if (jTreeElement == null) {
         jTreeElement = new JTree();

         jTreeElement.setCellRenderer(new MyTreeCellRenderer());

         jTreeElement.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
         {
            @Override
            public void valueChanged(javax.swing.event.TreeSelectionEvent e)
            {
               reloadAttribute(e);
            }
         });
         jTreeElement.addMouseListener(new java.awt.event.MouseAdapter()
         {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e)
            {
               if (e.isPopupTrigger()) {
                  showPopUp(e);
               }
            }
         });
      }

      return jTreeElement;
   }

   /**
    * Class MyTreeCellRenderer
    *
    * <p>
    * Copyright &copy; 2002, i:FAO, AG.
    * @author Andreas Brod
    */
   class MyTreeCellRenderer
      extends DefaultTreeCellRenderer
   {

      /**
       *
       */
      private static final long serialVersionUID = -6871895272116655920L;
      Icon icon1 = Util.getImageIcon("dtdinfo/FolderO_Text.jpg");
      Icon icon2 = Util.getImageIcon("dtdinfo/Text.jpg");

      /**
       * Method getTreeCellRendererComponent
       *
       * @param tree
       * @param value
       * @param sel
       * @param expanded
       * @param leaf
       * @param row
       * @param pbHasFocus
       *
       * @return
       * @author Andreas Brod
       */
      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                    boolean expanded, boolean leaf, int row,
                                                    boolean pbHasFocus)
      {

         super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, pbHasFocus);

         if (value instanceof MyNode) {
            String sType = ((MyNode) value).getXmlObject().getName();

            if (sType.equalsIgnoreCase("PnrElementParamInfo")) {
               setIcon(icon2);
            } else if (sType.equalsIgnoreCase("PnrElementInfo")) {
               setIcon(icon1);
            }

         }

         return this;
      }


   }

   /**
    * Method showPopUp
    *
    * @param me
    * @author Andreas Brod
    */
   private void showPopUp(java.awt.event.MouseEvent me)
   {
      if (jTreeElement.getSelectionPath() == null) {
         return;
      }

      JPopupMenu popmen = new JPopupMenu();

      MyNode node = (MyNode) jTreeElement.getSelectionPath().getLastPathComponent();

      if (node == null) {
         return;
      }

      Icon deleteIcon = Util.getImageIcon("dtdinfo/WZDELETE.jpg");
      Icon okIcon = Util.getImageIcon("dtdinfo/WZYES.jpg");

      XmlObject parent =
         (node.getParent() != null) ? ((MyNode) node.getParent()).getXmlObject() : null;
      XmlObject object = node.getXmlObject();

      JMenuItem entryHead = new JMenuItem("Delete " + node.toString(), deleteIcon);

      if (parent == null) {
         entryHead = new JMenuItem(node.toString());
      } else {
         entryHead.setSelectedIcon(entryHead.getIcon());
         entryHead.addActionListener(new AddActionListener(parent, object, node.toString(), true));
      }

      popmen.add(entryHead);
      popmen.addSeparator();

      XmlObject element = schema.findSubObject("element", "name", object.getName());

      XmlObject[] elements =
         element.createObject("complexType").createObject("sequence").getObjects("element");

      for (XmlObject element2 : elements) {
         String sName = element2.getAttribute("ref");

         boolean unbounded = element2.getAttribute("maxOccurs").equals("unbounded");
         int iCount = 999999;

         if (!unbounded) {
            try {
               iCount = Integer.parseInt(element2.getAttribute("maxOccurs"));
            }
            catch (NumberFormatException e) {
               iCount = 1;
            }
         }

         XmlObject[] subs = object.getObjects(sName);

         if (unbounded || (subs.length < iCount)) {
            String sType = (iCount == 1) ? "Create " : "Add new ";

            JMenuItem item = new JMenuItem(sType + sName, okIcon);

            item.addActionListener(new AddActionListener(null, object, sName, false));
            popmen.add(item);

         }
      }


      popmen.show(me.getComponent(), me.getX(), me.getY());

   }

   /**
    * Class AddActionListener
    *
    * <p>
    * Copyright &copy; 2002, i:FAO, AG.
    * @author Andreas Brod
    */
   private class AddActionListener
      implements ActionListener
   {
      XmlObject parent;
      XmlObject object;
      String sName;
      boolean delete;

      /**
       * Constructor AddActionListener
       *
       * @param pParent
       * @param pObject
       * @param psName
       * @param pDelete
       */
      public AddActionListener(XmlObject pParent, XmlObject pObject, String psName, boolean pDelete)
      {
         object = pObject;
         sName = psName;
         delete = pDelete;
         parent = pParent;
      }

      /**
       * Method actionPerformed
       *
       * @param e
       * @author Andreas Brod
       */
      @Override
      public void actionPerformed(ActionEvent e)
      {
         if (delete) {
            delete();
         } else {
            addElement();
         }
      }

      /**
       * Method delete
       * @author Andreas Brod
       */
      private void delete()
      {
         parent.deleteObjects(object);
         reload(true, true);
      }

      /**
       * Method addElement
       * @author Andreas Brod
       */
      private void addElement()
      {

         String sId = "";
         String sIdUpper = "";
         String sIdDefa = "";
         String sValDefa = "";
         Object[] comboElements = new Object[0];

         if (sName.equalsIgnoreCase("PnrElementInfo")) {
            sId = "name";
            sIdUpper = "type";
            sIdDefa = "category code";
            sValDefa = "STANDARD 0";
            comboElements = otherElements.keySet().toArray();
         }

         if (sName.equalsIgnoreCase("PnrElementParamInfo")) {
            sId = "name";
            sIdUpper = "id";
            sIdDefa = "type code";
            sValDefa = "FREE_TEXT 0";
            String sObjectName = object.getAttribute("name");
            List<String> list = otherElements.get(sObjectName);
            if (list == null) {
               list = otherElements.get("[ " + sObjectName + " ]");
            }
            if (list != null) {
               comboElements = list.toArray();
               for (int i = 0; i < comboElements.length; i++) {
                  if (!comboElements[i].toString().startsWith("[")) {
                     comboElements[i] = "[ " + comboElements[i].toString() + " ]";
                  }
               }
            }
         }

         if (sName.equalsIgnoreCase("PnrElementParamEnum")) {
            sId = "name";
            sIdUpper = "value";
         }

         if (sName.equalsIgnoreCase("PnrSplitparameterRule")) {
            sIdDefa = "typeFrom";
            sValDefa = object.getParent().getAttribute("type");
         }


         if (sName.equalsIgnoreCase("PnrSplitparameterParam")) {
            sIdDefa = "idFrom default";
            sValDefa = object.getParent().getAttribute("id") + " *";
         }

         XmlObject newObject = null;

         if (sId.length() > 0) {
            String sDefaultList = "";
            Arrays.sort(comboElements);
            for (Object comboElement : comboElements) {
               if (sDefaultList.length() > 0) {
                  sDefaultList += "\n";
               }
               String sElement = comboElement.toString().trim();
               if (sElement.startsWith("[") && sElement.endsWith("]")) {
                  sDefaultList += sElement.substring(1, sElement.length() - 1).trim();
               }
            }
            String sVal = InputString.getString(null, sName + "." + sId, sDefaultList);

            if (sVal.length() > 0) {
               newObject = object.createObject(sName, sId, sVal, true);

               if (sIdUpper.length() > 0) {
                  newObject.setAttribute(sIdUpper, DtdGenerator.getCamelCaseName(sVal));
               }

            }
         } else {
            newObject = object.createObject(sName);
         }

         if (newObject != null) {
            StringTokenizer st = new StringTokenizer(sIdDefa, " ");
            StringTokenizer stVal = new StringTokenizer(sValDefa, " ");

            while (st.hasMoreTokens() && stVal.hasMoreTokens()) {
               newObject.setAttribute(st.nextToken(), stVal.nextToken());
            }
         }

         reload(delete, true);
      }

   }

   /**
    * Method reload
    *
    * @author Andreas Brod
    *
    * @param useParent
    * @param resetItems
    */
   public void reload(boolean useParent, boolean resetItems)
   {
      TreePath path = jTreeElement.getSelectionPath();
      DefaultTreeModel model = (DefaultTreeModel) jTreeElement.getModel();
      MyNode node = (MyNode) jTreeElement.getSelectionPath().getLastPathComponent();

      if (useParent && (node.parent != null)) {
         path = jTreeElement.getSelectionPath().getParentPath();
         node = node.parent;
      }

      _resetItems = resetItems;

      node.reload();
      model.reload();
      jTreeElement.setSelectionPath(path);
      jTreeElement.expandPath(path);
      jTreeElement.invalidate();
      jTreeElement.validate();
      jTreeElement.repaint();

      _resetItems = true;

   }


   /**
    * Method reloadAttribute
    *
    * @param e
    * @author Andreas Brod
    */
   private void reloadAttribute(javax.swing.event.TreeSelectionEvent e)
   {
      if (e.getNewLeadSelectionPath() != null) {
         MyNode source = (MyNode) e.getNewLeadSelectionPath().getLastPathComponent();

         if (source != null) {
            jPanel.setObject(source, schema, _resetItems);
            pack();
         }
      }
   }

   /**
    * Method setNodes
    *
    * @param jTree
    * @author Andreas Brod
    */
   private void setNodes(JTree jTree, XmlObject xPnrElementInfos)
   {
      MyNode tn = new MyNode(xPnrElementInfos, null);
      DefaultTreeModel treeModel = new DefaultTreeModel(tn);
      TreeModelListener listener = new MyTreeModelListener();

      treeModel.addTreeModelListener(listener);

      jTree.setModel(treeModel);

      jSplitPane.setDividerLocation((int) (getJTreeElement().getPreferredSize().getWidth() + 40));
      pack();

   }


   String sPath, sPackage;

   private Hashtable<String, List<String>> otherElements = new Hashtable<String, List<String>>();


   /**
    * This is the default constructor
    *
    *
    * @param owner
    * @param sDefaPath
    */
   public DtdPnrElementInfosDialog(Frame owner, String sDefaPath, boolean pbModal,
                                   Hashtable<String, java.util.List<String>> pOtherElements)
   {
      super(owner, pbModal);
      setIconImage(Util.getImageIcon("dtdinfo/dtdinfo.png").getImage());

      otherElements = pOtherElements;
      if (sDefaPath.endsWith("\\") || sDefaPath.endsWith("/")) {
         sDefaPath = sDefaPath.substring(0, sDefaPath.length() - 1);
      }

      if (sDefaPath.indexOf("\\src\\") > 0) {
         sPath = sDefaPath.substring(0, sDefaPath.indexOf("\\src\\"));

         sPackage = sDefaPath.substring(sDefaPath.indexOf("\\src\\") + 5);

      } else {
         sPath = sDefaPath;
         sPackage = "";
      }

      schema =
         (new XmlObject(Util.loadFromFile(Util.getConfFile(sPath, "ArcticPnrElementInfos.xsd"))))
               .getFirstObject();

      initialize();

   }

   /**
    * Method load
    *
    *
    * @param owner
    * @param arcticPnrElementInfos
    * @param sProvider
    * @param sElement
    * @param sParam
    * @param sDefaultHint
    * @author Andreas Brod
    */
   public void load(Frame owner, XmlObject pArcticPnrElementInfos, String sProvider,
                    String sElement, String sParam, String sDefaultHint)
   {
      if (owner != null) {
         setLocation(owner.getLocation());
         pack();

         int w = this.getWidth() - getJContentPane().getWidth();
         int h = this.getHeight() - getJContentPane().getHeight();

         getJContentPane().setPreferredSize(
               new Dimension(owner.getWidth() - w, owner.getHeight() - h));
      }

      char[] cPackage = sPackage.toCharArray();

      for (int i = 0; i < cPackage.length; i++) {
         if (cPackage[i] == '\\') {
            cPackage[i] = '.';
         }
      }

      arcticPnrElementInfos = pArcticPnrElementInfos.createObject("Arctic");
      arcticRootObject = arcticPnrElementInfos.copy();

      // ensure provider is created
      arcticRootObject.createObject("PnrElementInfos", "provider", sProvider, true);

      XmlObject[] allPnrElementInfos = arcticRootObject.getObjects("PnrElementInfos");

      XmlObject xPnrElementInfos =
         arcticRootObject.createObject("PnrElementInfos", "provider", sProvider, true);

      XmlObject xPnrElementInfo;

      if (sElement.length() == 0) {
         xPnrElementInfo = xPnrElementInfos.createObject("PnrElementInfo");
         sElement = xPnrElementInfo.getAttribute("type");
      } else {
         xPnrElementInfo = xPnrElementInfos.createObject("PnrElementInfo", "type", sElement, true);
      }

      // fill with defaults
      Vector<XmlObject> lstAttribute = new Vector<XmlObject>();

      // search over all providers
      for (XmlObject allPnrElementInfo2 : allPnrElementInfos) {

         // search for related PnrElement (sElement) for this provider
         XmlObject allPnrElementInfo =
            allPnrElementInfo2.createObject("PnrElementInfo", "type", sElement, false);

         // if found ...
         if (allPnrElementInfo != null) {

            // ... search for Param (sParam) for this Element
            XmlObject allPnrElementParamInfo;

            if (sParam.length() == 0) {
               allPnrElementParamInfo = null;

            } else {
               allPnrElementParamInfo =
                  allPnrElementInfo.createObject("PnrElementParamInfo", "id", sParam, false);
            }

            // if the para is found add this to the list
            if (allPnrElementParamInfo != null) {
               lstAttribute.add(allPnrElementParamInfo);
            }

            // if the default attribute is not filled ...
            if (xPnrElementInfo.getAttribute("code").length() == 0) {

               // fill with values from the first element
               String[] sNames = allPnrElementInfo.getAttributeNames(true);

               for (String sName : sNames) {
                  if (xPnrElementInfo.getAttribute(sName).length() == 0) {
                     xPnrElementInfo.setAttribute(sName, allPnrElementInfo.getAttribute(sName));
                  }
               }
            }
         }
      }

      // fill element with defaults ... if not found
      if (xPnrElementInfo.getAttribute("code").length() == 0) {
         xPnrElementInfo.setAttribute("code", "0");
         xPnrElementInfo.setAttribute("name", DtdGenerator.getUnFormatedProvider(sElement));
         xPnrElementInfo.setAttribute("category", "STANDARD");
      }

      // get/create the param...
      XmlObject xPnrElementParamInfo;

      // first try to create PnrElementParamInfo
      if (sParam.length() == 0) {
         xPnrElementParamInfo = xPnrElementInfo.createObject("PnrElementParamInfo");
         sParam = xPnrElementParamInfo.getAttribute("id");
      } else {

         xPnrElementParamInfo =
            xPnrElementInfo.createObject("PnrElementParamInfo", "id", sParam, true);
      }

      // validate if PnrElementRequestTypes are aviable (after PnrElementParamInfo)
      if (xPnrElementInfo.getObject("PnrElementRequestTypes") == null) {
         xPnrElementInfo.createObject("PnrElementRequestTypes").createObject(
               "PnrElementRequestType", "type", "HTTPPOST", true);
      }

      // if the attribute is not set
      if (xPnrElementParamInfo.getAttribute("code").length() == 0) {

         // ... try to load the defaults.
         for (int i = 0; i < lstAttribute.size(); i++) {
            XmlObject info = lstAttribute.get(i);
            String[] sNames = info.getAttributeNames(true);

            for (String sName : sNames) {
               if (xPnrElementParamInfo.getAttribute(sName).length() == 0) {
                  xPnrElementParamInfo.setAttribute(sName, info.getAttribute(sName));
               }
            }

         }
      }

      // if the attribute is still not set
      if (xPnrElementParamInfo.getAttribute("code").length() == 0) {
         xPnrElementParamInfo.setAttribute("code", "0");
         xPnrElementParamInfo.setAttribute("name", DtdGenerator.getUnFormatedProvider(sParam));
         xPnrElementParamInfo.setAttribute("type", "FREE_TEXT");
         xPnrElementParamInfo.setAttribute("hint", sDefaultHint);

      }

      xPnrElementInfo.setAttribute("className", (new String(cPackage))
            + ".framework.elements.Element" + DtdGenerator.getUnFormatedProvider(sElement));


      setNodes(jTreeElement, xPnrElementInfos);

      DefaultTreeModel model = (DefaultTreeModel) jTreeElement.getModel();

      try {
         MyNode node = (MyNode) model.getRoot();

         node = node.find("PnrElementInfo", "type", sElement);
         node = node.find("PnrElementParamInfo", "id", sParam);

         TreePath tp = new TreePath(model.getPathToRoot(node));

         jTreeElement.scrollPathToVisible(tp);
         jTreeElement.addSelectionPath(tp);
      }
      catch (RuntimeException e) {

         // Make nothing
      }
   }

   /**
    * This method initializes this
    *
    */
   private void initialize()
   {
      this.setTitle("Editor for PnrElementInfos");
      this.setContentPane(getJContentPane());
      this.addWindowListener(new java.awt.event.WindowAdapter()
      {
         @Override
         public void windowClosing(java.awt.event.WindowEvent e)
         {
            setVisible(false);
         }
      });
   }

   /**
    * This method initializes jContentPane
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJContentPane()
   {
      if (jContentPane == null) {
         jContentPane = new JPanel();

         jContentPane.setLayout(new BorderLayout());
         jContentPane.add(getJSplitPane(), java.awt.BorderLayout.CENTER);
         jSplitPane.setDividerLocation((int) getJScrollPaneLeft().getPreferredSize().getWidth());

      }

      return jContentPane;
   }

   public void copyParameters()
   {
      arcticPnrElementInfos.deleteObjects("");
      arcticPnrElementInfos.addObjects(arcticRootObject.getObjects(""));

   }
} // @jve:decl-index=0:visual-constraint="10,10"


/**
 * Class MyNode
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class MyNode
   implements TreeNode
{

   XmlObject object;
   MyNode parent;
   Vector<MyNode> children = new Vector<MyNode>();

   /**
    * Constructor MyNode
    *
    * @param pObject
    * @param pParent
    */
   public MyNode(XmlObject pObject, MyNode pParent)
   {
      object = pObject;
      parent = pParent;

      reload();
   }

   /**
    * Method find
    *
    * @param sName
    * @param sAttr
    * @param sVal
    *
    * @return
    * @author Andreas Brod
    */
   public MyNode find(String sName, String sAttr, String sVal)
   {
      for (int i = 0; i < children.size(); i++) {
         XmlObject sub = children.get(i).getXmlObject();

         if (sub.getName().equals(sName) && sub.getAttribute(sAttr).equals(sVal)) {
            return children.get(i);
         }
      }

      return null;
   }

   /**
    * Method setXmlObject
    *
    * @param sText
    * @author Andreas Brod
    */
   public void setXmlObject(String sText)
   {
      setXmlObject((new XmlObject(sText)).getFirstObject());
      reload();
   }

   /**
    * Method setXmlObject
    *
    * @param objectNew
    * @author Andreas Brod
    */
   private void setXmlObject(XmlObject objectNew)
   {

      if (parent != null) {
         parent.getXmlObject().replaceObject(object, objectNew);
      }

      object = objectNew;

   }

   /**
    * Method reload
    * @author Andreas Brod
    */
   public void reload()
   {
      children.removeAllElements();

      XmlObject[] subs = object.getObjects("");

      for (XmlObject sub : subs) {
         children.add(new MyNode(sub, this));
      }
   }

   /**
    * Method getChildCount
    *
    * @return
    * @author Andreas Brod
    */
   @Override
   public int getChildCount()
   {

      return children.size();
   }

   /**
    * Method getAllowsChildren
    *
    * @return
    * @author Andreas Brod
    */
   @Override
   public boolean getAllowsChildren()
   {

      return getChildCount() > 0;
   }

   /**
    * Method isLeaf
    *
    * @return
    * @author Andreas Brod
    */
   @Override
   public boolean isLeaf()
   {

      return getChildCount() == 0;
   }

   /**
    * Method children
    *
    * @return
    * @author Andreas Brod
    */
   @Override
   public Enumeration<MyNode> children()
   {

      return children.elements();
   }

   /**
    * Method getParent
    *
    * @return
    * @author Andreas Brod
    */
   @Override
   public TreeNode getParent()
   {

      return parent;
   }

   /**
    * Method getChildAt
    *
    * @param childIndex
    *
    * @return
    * @author Andreas Brod
    */
   @Override
   public TreeNode getChildAt(int childIndex)
   {

      return children.get(childIndex);
   }

   /**
    * Method getIndex
    *
    * @param node
    *
    * @return
    * @author Andreas Brod
    */
   @Override
   public int getIndex(TreeNode node)
   {

      return children.indexOf(node);
   }

   /**
    * Method toString
    *
    * @return
    * @author Andreas Brod
    */
   @Override
   public String toString()
   {

      return getName(object);
   }

   /**
    * Method getName
    *
    * @param object
    *
    * @return
    * @author Andreas Brod
    */
   public static String getName(XmlObject object)
   {
      String sAttribute = object.getAttribute("id");

      if (sAttribute.length() == 0) {
         sAttribute = object.getAttribute("provider");

         if (sAttribute.length() == 0) {
            sAttribute = object.getAttribute("type");

            if (sAttribute.length() == 0) {
               sAttribute = object.getAttribute("name");
            }
         }
      }

      if (sAttribute.length() == 0) {
         sAttribute = object.getName();
      }

      return sAttribute;

   }

   /**
    * Method getXml
    *
    * @return
    * @author Andreas Brod
    */
   public String getXml()
   {
      return object.toString();
   }

   /**
    * Method getXmlObject
    *
    * @return
    * @author Andreas Brod
    */
   public XmlObject getXmlObject()
   {
      return object;
   }
}


/**
 * Class MyTreeModelListener
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class MyTreeModelListener
   implements TreeModelListener
{

   /**
    * Method treeNodesChanged
    *
    * @param e
    * @author Andreas Brod
    */
   @Override
   public void treeNodesChanged(TreeModelEvent e)
   {
      print(e, "treeNodesChanged");
   }

   /**
    * Method print
    *
    * @param e
    * @param sMethod
    * @author Andreas Brod
    */
   private void print(TreeModelEvent e, String sMethod)
   {
      TreeNode node;

      node = (TreeNode) (e.getTreePath().getLastPathComponent());

      /*
       * If the event lists children, then the changed
       * node is the child of the node we've already
       * gotten.  Otherwise, the changed node and the
       * specified node are the same.
       */
      try {
         int index = e.getChildIndices()[0];

         node = node.getChildAt(index);
      }
      catch (NullPointerException exc) {}

      System.out.println("The user has finished editing the node. " + sMethod);
      System.out.println("New value: " + node.toString());
   }

   /**
    * Method treeNodesInserted
    *
    * @param e
    * @author Andreas Brod
    */
   @Override
   public void treeNodesInserted(TreeModelEvent e)
   {
      print(e, "treeNodesInserted");
   }

   /**
    * Method treeNodesRemoved
    *
    * @param e
    * @author Andreas Brod
    */
   @Override
   public void treeNodesRemoved(TreeModelEvent e)
   {
      print(e, "treeNodesRemoved");
   }

   /**
    * Method treeStructureChanged
    *
    * @param e
    * @author Andreas Brod
    */
   @Override
   public void treeStructureChanged(TreeModelEvent e)
   {
      print(e, "treeNodesChanged");
   }
}
