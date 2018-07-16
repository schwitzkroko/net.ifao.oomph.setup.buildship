package dtdinfo.gui;


import ifaoplugin.Util;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

import javax.swing.*;

import net.ifao.xml.XmlObject;


/**
 * Class DtdAreaPanel
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
public class DtdAreaPanel
   extends JPanel
{

   /**
    *
    */
   private static final long serialVersionUID = 1439162974915536185L;
   private static final String EMPTY = "- empty -";
   JTree tree;
   DtdPnrElementInfosDialog parentDialog;
   MyNode node; //  @jve:decl-index=0:
   XmlObject schema;
   File fileArcticPnrElementInfos = null;
   private JScrollPane jScrollPane = null;
   private JTextPane jTextArea = null;
   private JPanel jPanelLeft = null;
   private JPanel jPanelRight = null;

   private GridLayout gridLayoutRight = new GridLayout();
   private GridLayout gridLayoutLeft = new GridLayout();
   private JSplitPane jSplitPane = null;
   private JPanel jPanelSouth = null;
   private JPanel jPanelButtons = null;
   private JButton jButtonExit = null;
   private JButton jButtonCancel = null;
   private JTabbedPane jTabbedPane = null;
   private JPanel jPanel4Params = null;
   private JPanel jPanel4ParamsFiller = null;

   /**
    * This is the default constructor
    *
    * @param pParentDialog
    */
   public DtdAreaPanel(DtdPnrElementInfosDialog pParentDialog)
   {
      super();

      parentDialog = pParentDialog;

      String sPath = parentDialog.sPath;

      fileArcticPnrElementInfos = Util.getConfFile(sPath, "ArcticPnrElementInfos.xml");

      initialize();
   }

   /**
    * This method initializes this
    *
    */
   private void initialize()
   {
      this.setLayout(new BorderLayout());
      this.setSize(500, 227);
      this.add(getJPanelSouth(), java.awt.BorderLayout.SOUTH);
      this.add(getJTabbedPane(), BorderLayout.CENTER);
   }


   /**
    * Method setObject
    *
    *
    * @author Andreas Brod
    *
    * @param pNode
    * @param pSchema
    * @param resetItems
    */
   public void setObject(MyNode pNode, XmlObject pSchema, boolean resetItems)
   {
      node = pNode;
      tree = parentDialog.getTree();
      schema = pSchema;

      reload(resetItems);

   }

   /**
    * Method makebutton
    *
    * @param lst
    * @param button
    * @author Andreas Brod
    */
   protected void makebutton(List<Component> lst, Component button)
   {
      lst.add(button);
   }

   /**
    * Method reload
    * @author Andreas Brod
    *
    * @param resetItems
    */
   public void reload(boolean resetItems)
   {
      XmlObject object = (node == null) ? new XmlObject("<Element />") : node.getXmlObject();

      setText(object.toString());

      if (!resetItems) {
         return;
      }

      XmlObject element = schema.findSubObject("element", "name", object.getName());

      if (element == null) {
         element = new XmlObject("<Element />");
      }

      XmlObject[] attribute = element.createObject("complexType").getObjects("attribute");

      int count = Math.max(1, attribute.length);

      int height = count * 20;

      jPanelRight.removeAll();
      jPanelLeft.removeAll();

      Dimension dimension = new Dimension(100, height);

      jPanelRight.setPreferredSize(dimension);
      jPanelLeft.setPreferredSize(dimension);

      gridLayoutRight.setRows(count);
      gridLayoutLeft.setRows(count);
      gridLayoutRight.setColumns(1);
      gridLayoutLeft.setColumns(1);

      for (int i = 0; i < attribute.length; i++) {
         if (attribute[i] != null) {
            JLabel jLabel = new JLabel();
            String sAttr = attribute[i].getAttribute("name");

            jLabel.setText(sAttr);
            jPanelLeft.add(jLabel, null);

            List<String> hs = new Vector<String>();
            String sType = attribute[i].getAttribute("type");

            String sToolTip =
               attribute[i].createObject("annotation").createObject("documentation").getCData();

            if (sToolTip.length() == 0) {
               sToolTip = sAttr;
            }

            if (attribute[i].getAttribute("use").equals("required")) {
               Font f = jLabel.getFont();

               f = new Font(f.getName(), Font.BOLD, f.getSize());

               jLabel.setFont(f);

               // jLabel.setText(sAttr + "*");

               sToolTip += " (Mandatory)";
            } else {
               Font f = jLabel.getFont();

               f = new Font(f.getName(), Font.PLAIN, f.getSize());

               jLabel.setFont(f);
               jLabel.setEnabled(false);

            }

            jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel.setToolTipText(sToolTip);

            if (!attribute[i].getAttribute("use").equals("required")) {}

            if (sType.startsWith("xs:bool")) {
               if (!attribute[i].getAttribute("use").equals("required")) {
                  hs.add(EMPTY);
               }

               hs.add("true");
               hs.add("false");
            } else {
               XmlObject subElement = schema.findSubObject("simpleType", "name", sType);

               if (subElement != null) {
                  if (!attribute[i].getAttribute("use").equals("required")) {
                     hs.add(EMPTY);
                  }

                  XmlObject[] items =
                     subElement.createObject("restriction").getObjects("enumeration");

                  for (XmlObject item : items) {
                     hs.add(item.getAttribute("value"));
                  }

               }
            }

            if (hs.size() == 0) {
               JTextField jText = new MyTextField(object, sAttr);

               jPanelRight.add(jText, null);
            } else {
               JComboBox jCombo = new MyComboBox(hs, object, sAttr);

               jPanelRight.add(jCombo, null);

            }
         }
      }
   }

   /**
    * Method setText
    *
    * @param sText
    * @author Andreas Brod
    */
   private void setText(String sText)
   {
      int position = jTextArea.getCaretPosition();
      DocContainer doc = DocContainer.getXmlDocument(sText);

      jTextArea.setContentType("text/plain");
      jTextArea.setStyledDocument(doc);

      try {
         jTextArea.setCaretPosition(position);
      }
      catch (RuntimeException e) {
         jTextArea.setCaretPosition(0);
      }

   }

   /**
    * Class MyTextField
    *
    * <p>
    * Copyright &copy; 2002, i:FAO, AG.
    * @author Andreas Brod
    */
   class MyTextField
      extends JTextField
   {

      /**
       *
       */
      private static final long serialVersionUID = -2821567421741717286L;
      XmlObject object;
      String sAttribute;

      /**
       * Constructor MyTextField
       *
       * @param pObject
       * @param psAttribute
       */
      public MyTextField(XmlObject pObject, String psAttribute)
      {
         object = pObject;
         sAttribute = psAttribute;

         setText(object.getAttribute(sAttribute));
         setCaretPosition(0);
         addKeyListener(new java.awt.event.KeyAdapter()
         {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e)
            {
               object.setAttribute(sAttribute, getText());
               reloadNode(false);
            }
         });
      }
   }

   /**
    * Class MyComboBox
    *
    * <p>
    * Copyright &copy; 2002, i:FAO, AG.
    * @author Andreas Brod
    */
   class MyComboBox
      extends JComboBox
   {

      /**
       *
       */
      private static final long serialVersionUID = 3451033844409567431L;
      XmlObject object;
      String sAttribute;

      /**
       * Constructor MyComboBox
       *
       * @param elements
       * @param pObject
       * @param psAttribute
       */
      public MyComboBox(List<String> elements, XmlObject pObject, String psAttribute)
      {
         object = pObject;
         sAttribute = psAttribute;

         boolean bFound = false;
         String sVal = object.getAttribute(sAttribute);

         if (sVal.length() == 0) {
            sVal = EMPTY;
         }
         // Copy the lements
         String[] items = new String[elements.size()];
         items = elements.toArray(items);
         Arrays.sort(items);
         for (String sElement : items) {
            addItem(sElement);

            if (sVal.equals(sElement)) {
               bFound = true;
            }
         }

         if (!bFound) {
            addItem(sVal);
         }

         setSelectedItem(sVal);

         addItemListener(new java.awt.event.ItemListener()
         {
            @Override
            public void itemStateChanged(java.awt.event.ItemEvent e)
            {
               if (getSelectedItem() != null) {
                  String sVal1 = (String) getSelectedItem();

                  if (sVal1.equals(EMPTY)) {
                     sVal1 = null;
                  }

                  object.setAttribute(sAttribute, sVal1);
                  reloadNode(false);
               }
            }
         });
      }
   }

   /**
    * This method initializes jScrollPane
    *
    * @return javax.swing.JScrollPane
    */
   private JScrollPane getJScrollPane()
   {
      if (jScrollPane == null) {
         jScrollPane = new JScrollPane();

         jScrollPane.setEnabled(false);
         jScrollPane.setViewportView(getJTextArea());
         jScrollPane.setPreferredSize(new Dimension(300, 100));
      }

      return jScrollPane;
   }

   /**
    * This method initializes jTextArea
    *
    * @return javax.swing.JTextArea
    */
   private JTextPane getJTextArea()
   {
      if (jTextArea == null) {
         jTextArea = new JTextPane();

         jTextArea.setText("");
         jTextArea.addFocusListener(new java.awt.event.FocusAdapter()
         {
            @Override
            public void focusLost(java.awt.event.FocusEvent e)
            {
               applyText();
            }
         });
      }

      return jTextArea;
   }

   /**
    * This method initializes jPanelLeft
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanelLeft()
   {
      if (jPanelLeft == null) {
         jPanelLeft = new JPanel();

         jPanelLeft.setLayout(gridLayoutLeft);
         jPanelLeft.setPreferredSize(new Dimension(100, 1));
         jPanelLeft.setBackground(Color.WHITE);
      }

      return jPanelLeft;
   }

   /**
    * This method initializes jPanelRight
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanelRight()
   {
      if (jPanelRight == null) {
         jPanelRight = new JPanel();

         jPanelRight.setLayout(gridLayoutRight);
         jPanelRight.setPreferredSize(new Dimension(100, 1));
         jPanelRight.setBackground(Color.WHITE);
      }

      return jPanelRight;
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

         jSplitPane.setDividerLocation(200);
         jSplitPane.setLeftComponent(getJPanelLeft());
         jSplitPane.setRightComponent(getJPanelRight());
      }

      return jSplitPane;
   }

   /**
    * This method initializes jPanelSouth
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanelSouth()
   {
      if (jPanelSouth == null) {
         jPanelSouth = new JPanel();

         jPanelSouth.setLayout(new BorderLayout());
         jPanelSouth.add(getJPanelButtons(), java.awt.BorderLayout.EAST);
      }

      return jPanelSouth;
   }

   /**
    * This method initializes jPanelButtons
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanelButtons()
   {
      if (jPanelButtons == null) {
         jPanelButtons = new JPanel();

         jPanelButtons.add(getJButtonExit(), null);
         jPanelButtons.add(getJButtonCancel(), null);
      }

      return jPanelButtons;
   }

   public void applyText()
   {
      node.setXmlObject(jTextArea.getText());
      reloadNode(true);

   }

   /**
    * Method reloadNode
    *
    * @param resetItems
    * @author Andreas Brod
    */
   private void reloadNode(boolean resetItems)
   {
      node.reload();
      parentDialog.reload(false, resetItems);
   }

   /**
    * This method initializes jButtonExit
    *
    * @return javax.swing.JButton
    */
   private JButton getJButtonExit()
   {
      if (jButtonExit == null) {
         jButtonExit = new JButton();

         jButtonExit.setText("OK");
         jButtonExit.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               if (parentDialog != null) {
                  parentDialog.copyParameters();
                  parentDialog.setVisible(false);
               }
            }
         });
      }

      return jButtonExit;
   }

   /**
   * This method initializes jButtonCancel	
   * 	
   * @return javax.swing.JButton	
   */
   private JButton getJButtonCancel()
   {
      if (jButtonCancel == null) {
         jButtonCancel = new JButton();
         jButtonCancel.setText("Cancel");
         jButtonCancel.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               if (parentDialog != null) {
                  // just clost the window
                  parentDialog.setVisible(false);
               }
            }
         });
      }
      return jButtonCancel;
   }

   /**
    * This method initializes jTabbedPane	
    * 	
    * @return javax.swing.JTabbedPane	
    */
   private JTabbedPane getJTabbedPane()
   {
      if (jTabbedPane == null) {
         jTabbedPane = new JTabbedPane();
         jTabbedPane.addTab("Parameters", null, getJPanel4Params(), null);
         jTabbedPane.addTab("Xml-View", null, getJScrollPane(), null);
      }
      return jTabbedPane;
   }

   /**
    * This method initializes jPanel4Params	
    * 	
    * @return javax.swing.JPanel	
    */
   private JPanel getJPanel4Params()
   {
      if (jPanel4Params == null) {
         jPanel4Params = new JPanel();
         jPanel4Params.setLayout(new BorderLayout());
         jPanel4Params.add(getJSplitPane(), BorderLayout.NORTH);
         jPanel4Params.add(getJPanel4ParamsFiller(), BorderLayout.CENTER);
      }
      return jPanel4Params;
   }

   /**
    * This method initializes jPanel4ParamsFiller	
    * 	
    * @return javax.swing.JPanel	
    */
   private JPanel getJPanel4ParamsFiller()
   {
      if (jPanel4ParamsFiller == null) {
         jPanel4ParamsFiller = new JPanel();
         jPanel4ParamsFiller.setLayout(new GridBagLayout());
      }
      return jPanel4ParamsFiller;
   }
} //  @jve:decl-index=0:visual-constraint="10,10"

