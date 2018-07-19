package dtdinfo.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import dtdinfo.DtdData;
import dtdinfo.DtdGenerator;
import dtdinfo.DtdMain;
import ifaoplugin.Util;
import net.ifao.dialogs.swing.ConfimDialog;
import net.ifao.dialogs.swing.FileChooserDialog;
import net.ifao.dialogs.swing.InputString;
import net.ifao.xml.XmlObject;


/**
 * Class DtdFrame
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
public class DtdFrame
   extends JFrame
{

   /**
    *
    */
   private static final long serialVersionUID = 4560145955276419752L;

   public static final String DOC_HEF = "<a href=\""; //  @jve:decl-index=0:
   public static final String DOC_REF = DOC_HEF + "doc/"; //  @jve:decl-index=0:

   boolean bChanged = false;
   JPanel contentPane;
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel jPanel1 = new JPanel();
   JPanel jPanel2 = new JPanel();
   JPanel jPanel3 = new JPanel();
   JPanel jPanel4 = new JPanel();
   JPanel jPanel5 = new JPanel();
   GridLayout gridLayout1 = new GridLayout();
   JLabel jLabel1 = new JLabel();
   JTextField jArcticPath = new JTextField();
   JButton jSearch = new JButton();
   JLabel jLabel2 = new JLabel();
   JTextField jUser = new JTextField();
   JLabel jLabel3 = new JLabel();
   JComboBox jComboAgent = new JComboBox();
   BorderLayout borderLayout2 = new BorderLayout();
   BorderLayout borderLayout3 = new BorderLayout();
   BorderLayout borderLayout4 = new BorderLayout();
   JSplitPane jSplitPane1 = new JSplitPane();
   JPanel jPanel6 = new JPanel();
   BorderLayout borderLayout5 = new BorderLayout();
   TitledBorder titledBorder1;
   TitledBorder titledBorder2;
   JPanel jPanel7 = new JPanel();
   JComboBox jComboRequest = new JComboBox();
   JButton jButtonLoad = new JButton();
   BorderLayout borderLayout6 = new BorderLayout();
   JPanel jPanel8 = new JPanel();
   BorderLayout borderLayout7 = new BorderLayout();
   JComboBox jRequestResponse = new JComboBox();
   JScrollPane jScrollPane1 = new JScrollPane();
   private DtdData data = null;
   DtdTree jTree1 = new DtdTree(this);

   public static Dimension dimension0 = new Dimension(63, 16);
   JPanel jPanel9 = new JPanel();
   JPanel jPanel10 = new JPanel();
   BorderLayout borderLayout8 = new BorderLayout();
   JTextField jTxtTitle = new JTextField();
   JTextField jTxtComment = new JTextField();
   BorderLayout borderLayout9 = new BorderLayout();
   JPanel jPanel12 = new JPanel();
   BorderLayout borderLayout10 = new BorderLayout();
   JLabel jLabel7 = new JLabel();
   JTextField jTxtDate = new JTextField();
   BorderLayout borderLayout13 = new BorderLayout();
   JSplitPane jSplitPane2 = new JSplitPane();
   JPanel jPanel11 = new JPanel();
   JPanel jPanel13 = new JPanel();
   JLabel jLabel5 = new JLabel();
   JLabel jLabel6 = new JLabel();
   JTextArea jTxtRules = new JTextArea();
   JTextArea jTxtRef = new JTextArea();
   BorderLayout borderLayout11 = new BorderLayout();
   BorderLayout borderLayout12 = new BorderLayout();
   BorderLayout borderLayout14 = new BorderLayout();
   JScrollPane jScrollPane2 = new JScrollPane();
   JScrollPane jScrollPane3 = new JScrollPane();
   TitledBorder titledBorder3;
   JSplitPane jSplitPane3 = new JSplitPane();
   JPanel jPanel17 = new JPanel();
   JLabel jLabel8 = new JLabel();
   JScrollPane jScrollPane4 = new JScrollPane();
   JTextArea jPNRElement = new JTextArea();
   BorderLayout borderLayout15 = new BorderLayout();

   // Construct the frame
   JFrame parentFrame;
   boolean active = true;
   JCheckBox jCheckBoxMandatory = new JCheckBox();

   JPanel jPanel18 = new JPanel();
   JPanel jPanel19 = new JPanel();
   JComboBox jComboPnrElement = new JComboBox();
   JComboBox jComboPnrAttribute = new JComboBox();
   JButton jButtonUsePnr = new JButton();
   BorderLayout borderLayout16 = new BorderLayout();
   BorderLayout borderLayout17 = new BorderLayout();

   private String sLastComboRequest = "";

   /**
    * Constructor DtdFrame
    *
    * @param pParentFrame
    */
   public DtdFrame(JFrame pParentFrame)
   {
      setIconImage(Util.getImageIcon("dtdinfo/dtdinfo.png").getImage());

      parentFrame = pParentFrame;

      // enableEvents(AWTEvent.WINDOW_EVENT_MASK);

      try {
         jbInit();
         jPanelCombo.setVisible(false);

         pack();
      }
      catch (Exception e) {
         e.printStackTrace();
      }

      init();
   }

   public void init()
   {
      initData();
      setPath();

      jRequestResponse.removeAllItems();
      jRequestResponse.addItem("REQUEST");
      jRequestResponse.addItem("RESPONSE");
      jRequestResponse.setSelectedIndex(0);

      jButtonLoad_actionPerformed(null);
   }

   @Override
   public void setVisible(boolean bVisible)
   {
      super.setVisible(bVisible);
      if (bVisible) {
         File f = Util.getConfFile(data.getPath(), "Arctic.xsd");
         if (f != null && !f.exists()) {
            getJDialogSettings().setVisible(true);
            setPath();
         }

      }
   }

   /**
    * Method getThisFrame
    *
    * @return
    * @author Andreas Brod
    */
   private JFrame getThisFrame()
   {
      return this;
   }

   // Component initialization

   /**
    * Method initData
    * @author $author$
    */
   private void initData()
   {
      data = new DtdData(true);
      jTree1.init(data);
      setSize(new Dimension(data.getSetting("width"), data.getSetting("height")));
      setLocation(data.getSetting("left"), data.getSetting("top"));
      jSplitPane1.setDividerLocation(data.getSetting("r1"));
      jSplitPane2.setDividerLocation(data.getSetting("r2"));
      jSplitPane3.setDividerLocation(data.getSetting("r3"));
      jUser.setText(data.getUser());
   }

   /**
    * Method jbInit
    *
    * @throws Exception
    * @author Andreas Brod
    */
   private void jbInit()
      throws Exception
   {
      jComboPnrElement.setPreferredSize(new java.awt.Dimension(150, 20));
      jComboPnrAttribute.setPreferredSize(new java.awt.Dimension(150, 20));
      jPanel22.setLayout(new BorderLayout());

      jPanel22.add(jButtonPnrList, BorderLayout.EAST);
      contentPane = (JPanel) this.getContentPane();
      titledBorder1 = new TitledBorder("");
      titledBorder2 = new TitledBorder("");
      titledBorder3 = new TitledBorder("");

      titledBorder4 = new TitledBorder("");

      contentPane.setLayout(borderLayout1);

      this.setTitle("DTD Info " + Util.getVERSION());
      this.setJMenuBar(getJJMenuBar());
      jPanel2.setLayout(gridLayout1);
      jLabel1.setMaximumSize(dimension0);
      jLabel1.setText("arctic Path");
      jArcticPath.addActionListener(new DtdFrame_jArcticPath_actionAdapter(this));

      // jSearch.setMaximumSize(new Dimension(75, 20));
      // jSearch.setMinimumSize(new Dimension(75, 20));
      // jSearch.setPreferredSize(new Dimension(75, 20));
      jSearch.setText("Open ...");
      jSearch.setToolTipText("... select the directory, where arctic-root is located");
      jSearch.addActionListener(new DtdFrame_jSearch_actionAdapter(this));
      gridLayout1.setColumns(1);
      gridLayout1.setHgap(5);
      gridLayout1.setRows(3);
      gridLayout1.setVgap(5);

      jLabel1.setMaximumSize(dimension0);
      jLabel1.setMinimumSize(dimension0);
      jLabel1.setPreferredSize(dimension0);
      jLabel2.setMaximumSize(dimension0);
      jLabel2.setMinimumSize(dimension0);
      jLabel2.setPreferredSize(dimension0);
      jLabel2.setText("User");
      jLabel3.setMaximumSize(dimension0);
      jLabel3.setMinimumSize(dimension0);
      jLabel3.setPreferredSize(dimension0);
      jLabel3.setText("Agent");
      jPanel3.setLayout(borderLayout2);
      jPanel4.setLayout(borderLayout3);
      jPanel5.setLayout(borderLayout4);
      jArcticPath.setText("");
      jArcticPath.setEditable(false);
      jArcticPath.setPreferredSize(new Dimension(200, 20));
      jPanel1.setLayout(borderLayout5);
      borderLayout5.setHgap(5);
      borderLayout5.setVgap(5);
      jSplitPane1.setBorder(null);
      jSplitPane1.setDebugGraphicsOptions(0);
      jSplitPane1.setBottomComponent(jPanel6);
      jSplitPane1.setContinuousLayout(false);

      // jUser.setMaximumSize(new Dimension(150, 2147483647));
      // jUser.setMinimumSize(new Dimension(150, 20));
      // jUser.setPreferredSize(new Dimension(150, 20));
      jUser.setText("");
      jPanel1.setBorder(titledBorder2);
      borderLayout2.setHgap(5);
      borderLayout3.setHgap(5);
      borderLayout4.setHgap(5);

      // jButtonLoad.setMaximumSize(new Dimension(62, 20));
      // jButtonLoad.setMinimumSize(new Dimension(62, 20));
      // jButtonLoad.setPreferredSize(new Dimension(62, 20));
      jButtonLoad.setText("Load");
      jButtonLoad.setToolTipText("... load the data for this Agent");

      setForeground4Selection(Color.red);
      jButtonLoad.addActionListener(new DtdFrame_jButtonLoad_actionAdapter(this));
      jPanel7.setLayout(borderLayout6);
      borderLayout6.setHgap(5);
      jPanel8.setLayout(borderLayout7);
      jPanel8.setVisible(false);

      // jRequest.setMinimumSize(new Dimension(31, 20));
      // jRequest.setPreferredSize(new Dimension(31, 20));
      // jAgentList.setMinimumSize(new Dimension(31, 20));
      // jAgentList.setPreferredSize(new Dimension(31, 20));
      jComboAgent.addActionListener(new DtdFrame_jAgentList_actionAdapter(this));
      jRequestResponse.addActionListener(new DtdFrame_jRequestResponse_actionAdapter(this));
      jPanel6.setLayout(borderLayout8);
      jPanel9.setLayout(borderLayout9);
      jPanel10.setLayout(borderLayout10);
      jLabel7.setEnabled(true);
      jLabel7.setForeground(Color.black);
      jLabel7.setRequestFocusEnabled(true);
      jLabel7.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel7.setText("Date (User)");
      jPanel12.setLayout(borderLayout13);
      jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
      jLabel5.setEnabled(true);
      jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel5.setText("Provider Ref");
      jLabel6.setEnabled(true);
      jLabel6.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel6.setText("Transform Rules");
      jPanel11.setLayout(borderLayout11);
      jPanel13.setLayout(borderLayout12);
      jTxtTitle.setBackground(new Color(100, 80, 200));
      jTxtTitle.setFont(new java.awt.Font("Dialog", 1, 12));
      jTxtTitle.setForeground(Color.WHITE);
      jTxtTitle.setBorder(BorderFactory.createRaisedBevelBorder());
      jTxtTitle.setText("");
      jTxtComment.setBackground(Color.orange);
      jTxtComment.setBorder(BorderFactory.createRaisedBevelBorder());
      jTxtComment.setText("");
      jTxtRules.setFont(new java.awt.Font("Dialog", 0, 12));
      jTxtRules.setBorder(BorderFactory.createRaisedBevelBorder());
      jTxtRules.setToolTipText("");
      jTxtRules.setText("");
      jTxtRules.setLineWrap(true);
      jTxtRules.setWrapStyleWord(true);
      jTxtRules.addFocusListener(new DtdFrame_jTxtRules_focusAdapter(this));
      jTxtRules.addKeyListener(new DtdFrame_jTxtRules_keyAdapter(this));
      jTxtRef.setFont(new java.awt.Font("Dialog", 0, 12));
      jTxtRef.setBorder(BorderFactory.createRaisedBevelBorder());
      jTxtRef.setText("");
      jTxtRef.setLineWrap(true);
      jTxtRef.setWrapStyleWord(true);
      jTxtRef.addFocusListener(new DtdFrame_jTxtRef_focusAdapter(this));
      jTxtRef.addKeyListener(new DtdFrame_jTxtRef_keyAdapter(this));
      jTxtDate.setEnabled(false);
      jTxtDate.setDisabledTextColor(Color.gray);
      jTxtDate.setEditable(true);
      jTxtDate.setText("");
      jScrollPane2.setBorder(null);
      jScrollPane3.setBorder(null);
      borderLayout10.setHgap(0);
      borderLayout10.setVgap(0);
      jLabel8.setEnabled(true);
      jLabel8.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel8.setText("PNR Element");
      jPanel17.setLayout(borderLayout15);
      jSplitPane3.setOrientation(JSplitPane.VERTICAL_SPLIT);
      jPNRElement.setBorder(BorderFactory.createRaisedBevelBorder());
      jPNRElement.setText("");
      jPNRElement.addFocusListener(new DtdFrame_jPNRElement_focusAdapter(this));
      jPNRElement.addKeyListener(new DtdFrame_jPNRElement_keyAdapter(this));
      jCheckBoxMandatory.setText("Mandatory ?");
      jCheckBoxMandatory.setToolTipText("Mandatory for this Agent");

      jCheckBoxMandatory.addItemListener(new java.awt.event.ItemListener()
      {
         @Override
         public void itemStateChanged(java.awt.event.ItemEvent e)
         {
            if (jCheckBoxMandatory.isEnabled() && jCheckBoxMandatory.isSelected()) {
               jTextFieldMandatory.setEnabled(true);
            } else {
               jTextFieldMandatory.setText("");
               jTextFieldMandatory.setEnabled(false);
            }
         }
      });
      jButtonUsePnr.setEnabled(true);
      jButtonUsePnr.setText("Use");
      jButtonUsePnr.setToolTipText("use/create this business-element");

      jButtonUsePnr.addActionListener(new DtdFrame_jButtonUsePnr_actionAdapter(this));
      jPanel19.setLayout(borderLayout16);
      jPanel18.setLayout(borderLayout17);
      jComboPnrElement.addActionListener(new DtdFrame_jComboPnrElement_actionAdapter(this));
      jButtonEdit.setText("Detail ...");
      jButtonEdit.setToolTipText("display/edit this business-element");

      jButtonEdit.addActionListener(new DtdFrame_jButtonEdit_actionAdapter(this));
      jPanel20.setLayout(borderLayout18);
      jPanelCombo.setLayout(borderLayout19);
      jPanel21.setLayout(borderLayout20);
      jCheckBoxSelfDef.setText("Create self defined method");
      jCheckBoxProvider.setText("Provider");
      jCheckBoxProvider.addActionListener(new DtdFrame_jCheckBoxProvider_actionAdapter(this));

      jButtonPnrList.setText("UsedPnrElements");
      jButtonPnrList.setToolTipText("... display the currently use Pnr Elements");
      jButtonPnrList.setEnabled(false);
      jButtonPnrList.addActionListener(new DtdFrame_jButtonPnrList_actionAdapter(this));

      jButtonNotSupported.setBorder(BorderFactory.createEtchedBorder());
      jButtonNotSupported.setText("not supported");
      jButtonNotSupported.setToolTipText("copy the text 'not supported' to the textbox");
      jButtonNotSupported.addActionListener(new DtdFrame_jButtonNotSupported_actionAdapter(this));

      jButtonAdditionalDocument.setBorder(BorderFactory.createEtchedBorder());
      jButtonAdditionalDocument.setText("Additional Document");
      jButtonAdditionalDocument.setToolTipText("copy the text 'not supported' to the textbox");
      jButtonAdditionalDocument.addActionListener(new DtdFrame_jButtonAdditionalDocument_actionAdapter(this));


      jPanel24.setLayout(borderLayout21);
      jPanel2.add(jPanel5, null);

      jPanel4.add(jLabel2, BorderLayout.WEST);
      jPanel4.add(jUser, BorderLayout.CENTER);
      jSplitPane1.add(jPanel6, JSplitPane.RIGHT);
      jPanel6.add(jPanel9, BorderLayout.NORTH);
      jPanel9.add(jTxtTitle, BorderLayout.NORTH);
      jPanel9.add(jTxtComment, BorderLayout.CENTER);
      jPanel9.add(getJPanel14(), BorderLayout.SOUTH);
      jPanel6.add(jPanel10, BorderLayout.CENTER);
      jPanel10.add(jPanel12, BorderLayout.SOUTH);
      jPanel12.add(jLabel7, BorderLayout.NORTH);
      jPanel12.add(jTxtDate, BorderLayout.CENTER);
      jPanel10.add(jSplitPane2, BorderLayout.CENTER);
      jSplitPane2.add(jPanel13, JSplitPane.BOTTOM);
      jPanel13.add(jLabel5, BorderLayout.NORTH);
      jPanel5.add(jLabel3, BorderLayout.WEST);
      jPanel5.add(jComboAgent, BorderLayout.CENTER);
      contentPane.add(jSplitPane1, BorderLayout.CENTER);
      jSplitPane1.add(jPanel1, JSplitPane.LEFT);
      jPanel1.add(jPanel2, BorderLayout.NORTH);

      jPanel3.add(jLabel1, BorderLayout.WEST);
      jPanel3.add(jArcticPath, BorderLayout.CENTER);
      jPanel3.add(jSearch, BorderLayout.EAST);
      jPanel2.add(jPanel7, null);
      jPanel7.add(jComboRequest, BorderLayout.CENTER);
      jComboRequest.addActionListener(new DtdFrame_jRequest2_actionAdapter(this));
      jPanel7.add(jButtonLoad, BorderLayout.EAST);
      jPanel7.add(jCheckBoxProvider, BorderLayout.WEST);

      jPanel2.add(panel);
      panel.setBackground(new Color(100, 200, 80));
      jLabelAgent.setForeground(Color.WHITE);
      panel.add(jLabelAgent);
      jPanel1.add(jPanel8, BorderLayout.CENTER);
      jPanel8.add(jRequestResponse, BorderLayout.NORTH);
      jPanel8.add(jScrollPane1, BorderLayout.CENTER);
      jPanel1.add(jPanel21, BorderLayout.SOUTH);
      jPanel21.add(jPanel22, BorderLayout.CENTER);
      jScrollPane1.setViewportView(jTree1);
      jPanel17.add(jScrollPane4, BorderLayout.CENTER);
      jPanel17.add(jPanel18, BorderLayout.EAST);
      jPanel18.add(jPanel20, BorderLayout.SOUTH);
      jPanel20.add(jButtonEdit, BorderLayout.SOUTH);
      jPanel17.add(jPanel19, BorderLayout.NORTH);
      jPanel19.add(jLabel8, BorderLayout.NORTH);
      jPanel19.add(jPanelCombo, BorderLayout.CENTER);
      jPanelCombo.add(jButtonUsePnr, BorderLayout.EAST);
      jPanelCombo.add(jCheckBoxSelfDef, BorderLayout.SOUTH);
      jPanelCombo.add(getJSplitPane4Combo(), java.awt.BorderLayout.CENTER);
      jSplitPane3.add(jPanel11, JSplitPane.RIGHT);
      jScrollPane4.setViewportView(jPNRElement);
      jSplitPane3.add(jPanel17, JSplitPane.LEFT);
      jPanel11.add(jScrollPane2, BorderLayout.CENTER);
      jPanel11.add(jPanel24, BorderLayout.NORTH);
      jPanel24.add(jLabel6, BorderLayout.CENTER);
      jPanel24.add(jButtonNotSupported, BorderLayout.WEST);
      jPanel24.add(jButtonAdditionalDocument, BorderLayout.EAST);
      jScrollPane2.setViewportView(jTxtRules);
      jPanel13.add(jScrollPane3, BorderLayout.CENTER);
      jSplitPane2.add(jSplitPane3, JSplitPane.TOP);
      jScrollPane3.setViewportView(jTxtRef);
   }

   // Overridden so we can exit when window is closed

   /**
    * Method processWindowEvent
    *
    * @param e
    * @author Andreas Brod
    */
   @Override
   protected void processWindowEvent(WindowEvent e)
   {

      if (e.getID() == WindowEvent.WINDOW_CLOSING) {
         closeDtdInfo();
      } else {
         super.processWindowEvent(e);

      }
   }

   /**
    * Method jArcticPath_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jArcticPath_actionPerformed(ActionEvent e)
   {}

   /**
    * Method jSearch_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jSearch_actionPerformed(ActionEvent e)
   {
      String sFileName = FileChooserDialog.getDirectory(jArcticPath.getText());

      if (sFileName.length() > 0) {
         jArcticPath.setText(sFileName);

         String sNewPath = jArcticPath.getText().trim();
         if (!sNewPath.equals(data.getPath())) {
            data.setPath(sNewPath);
            setPath();
            jPanel8.setVisible(false);
         }
      }
   }

   /**
    * Method setPath
    * @author Andreas Brod
    */
   private void setPath()
   {
      String sPath = data.getPath();
      jArcticPath.setText(sPath);

      String sTitle = getTitle();
      if (sTitle.indexOf("-") < 0) {
         sTitle += " -";
      }
      setTitle(sTitle.substring(0, sTitle.indexOf("-")) + "- " + sPath);

      String sProvider = jCheckBoxProvider.isSelected() ? data.getStringSetting("provider") : "";

      DefaultComboBoxModel model = new DefaultComboBoxModel(data.getRequests(sProvider));

      jComboAgent.setModel(model);

      if (jComboAgent.getModel().getSize() > 0) {
         int i = 0;
         String sData = data.getStringSetting("agent");

         if (sData.length() > 0) {
            for (int j = 0; (i == 0) && (j < model.getSize()); j++) {
               if (sData.equals(model.getElementAt(j))) {
                  i = j;
               }
            }
         }

         jComboAgent.setSelectedIndex(i);
         setRequest(jComboAgent.getModel().getElementAt(i).toString());
      }
   }

   /**
    * Method setRequest
    *
    * @param s1
    * @author Andreas Brod
    */
   void setRequest(String s1)
   {
      String sProvider = jCheckBoxProvider.isSelected() ? data.getStringSetting("provider") : "";

      DefaultComboBoxModel model = new DefaultComboBoxModel(data.getProvider(s1, true, sProvider));

      jComboRequest.setModel(model);

      int i = -1;
      String sData = data.getStringSetting("provider");

      if (sData.length() > 0) {
         for (int j = 0; (i < 0) && (j < model.getSize()); j++) {
            if (sData.equals(model.getElementAt(j))) {
               i = j;
            }
         }
      }

      if (i >= 0) {
         jComboRequest.setSelectedIndex(i);
      }
      // TODO: rese

   }

   /**
    * Method getRequest
    *
    * @return
    * @author Andreas Brod
    */
   private String getRequest(boolean pbGetFromCombo)
   {
      if (pbGetFromCombo) {
         sLastComboRequest = jComboRequest.getSelectedItem().toString();

         if (sLastComboRequest.startsWith("-")) {
            sLastComboRequest = "";
         }
      }

      return sLastComboRequest;
   }

   /**
    * Method jAgentList_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jAgentList_actionPerformed(ActionEvent e)
   {
      setRequest(jComboAgent.getSelectedItem().toString());
      setForeground4Selection(Color.red);
   }

   /**
    * Method jAgentList_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jRequest2_actionPerformed(ActionEvent e)
   {
      setForeground4Selection(Color.red);
   }

   /**
    * Method getArcticPnrElementInfos
    *
    * @param sSfx
    *
    * @return
    * @author Andreas Brod
    */
   private String getArcticPnrElementInfos(String sSfx)
   {
      return Util.getConfFile(jArcticPath.getText(), "ArcticPnrElementInfos." + sSfx).getAbsolutePath();
   }

   /**
    * Class LoadThread
    *
    * <p>
    * Copyright &copy; 2002, i:FAO, AG.
    * @author $author$
    */
   class LoadThread
      extends Object
   {

      /**
       * Method run
       * @author $author$
       */
      public void start()
      {
         jTree1.storePath();

         String sRequest = getRequest(true);
         String sAgent = getAgentListItem(true);

         jLabelAgent.setText(sAgent + " " + sRequest);
         jTree1.loadModel(jRequestResponse.getSelectedIndex() == 0, jArcticPath.getText() + DtdData.SRCPATH + "\\" + sRequest,
               sAgent);

         // save ArcticPnrElementInfos

         arcticPnrElementInfos = new XmlObject(Util.loadFromFile(getArcticPnrElementInfos("xml")));


         dtdPnrElementInfosDialog = null;

         loadProviderCombo(false);

         XmlObject infos =
            arcticPnrElementInfos.createObject("Arctic").createObject("PnrElementInfos", "provider", getProvider(), true);

         Util.writeToFile("Generator\\ArcticPnrElementInfos.info", infos.toString());

         loadProviderCombo(false);
         jMenuItemGenerate.setEnabled(true);
         jMenuItemSave.setEnabled(true);
         jMenuItemSaveHtml.setEnabled(true);
         jTree1.reStorePath();
      }

   }

   /**
    * Method jButtonLoad_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jButtonLoad_actionPerformed(ActionEvent e)
   {
      if (jComboRequest.getSelectedItem() != null) {

         if (bChanged) {
            if (!ConfimDialog.getBoolean(this, "There are changed values !\nDo you really want to load \""
                  + jComboRequest.getSelectedItem() + "\" ?\n(your changes will be lost)")) {
               return;
            }
         }

         (new LoadThread()).start();
         setChanged(false);
         setBuildButton(true);

         setForeground4Selection(Color.black);
         jButtonPnrList.setEnabled(true);
         jMenuItemSave.setEnabled(true);
         jMenuItemSaveHtml.setEnabled(true);

         jPanel6.setVisible(true);
         jPanel8.setVisible(true);
      }
   }

   Hashtable<String, Vector<String>> htPnrInfo = new Hashtable<>();
   XmlObject arcticPnrElementInfos = null;
   JPanel jPanel20 = new JPanel();
   JButton jButtonEdit = new JButton();
   BorderLayout borderLayout18 = new BorderLayout();

   /**
    * Method loadProviderCombo
    * @author Andreas Brod
    *
    * @param bCorrectClassName
    */
   private void loadProviderCombo(boolean bCorrectClassName)
   {
      htPnrInfo = new Hashtable<>();

      String sRequest = getRequest(false);

      // load the related Framework base Agent, which contains the
      // provider Indicator (for ArcticPnrElementsInfo).
      String sFramework = jArcticPath.getText() + "\\src\\net\\ifao\\arctic\\agents\\" + sRequest + "\\framework";

      File f = new File(sFramework);

      sFramework = "";

      if (f.exists()) {
         File[] files = f.listFiles();

         for (File file : files) {
            if (file.getName().endsWith("AgentFramework.java")) {
               sFramework = Util.loadFromFile(file.getAbsolutePath());
            }
         }
      }

      // Convert the path to package
      sRequest = "." + sRequest.replaceAll("\\\\", ".") + ".";

      // search Package in Agents.xml
      try {
         String sProvider = "SMARTAGENT";

         if (sRequest.length() > 2) {

            String sAgents = Util.loadFromFile(Util.getConfFile(jArcticPath.getText(), "Agents.xml").getAbsolutePath());

            if (sFramework.indexOf("  return PnrEnumProviderType.") > 0) {
               sProvider = sFramework.substring(sFramework.indexOf("  return PnrEnumProviderType.")) + "\n;";
               sProvider =
                  sProvider.substring(sProvider.indexOf(".") + 1, Math.min(sProvider.indexOf("\n"), sProvider.indexOf(";")));
            } else if (sAgents.indexOf(sRequest) > 0) {
               sAgents = sAgents.substring(sAgents.indexOf(sRequest));
               sAgents = sAgents.substring(sAgents.indexOf(" providerType="));
               sAgents = sAgents.substring(sAgents.indexOf("\"") + 1);

               sProvider = sAgents.substring(0, sAgents.indexOf("\""));

            }
         }

         jMenuProvider.setText(sProvider);
         jMenuProvider.setVisible(true);
         jMenuItemPnrElements.setEnabled(true);

         XmlObject arctic = arcticPnrElementInfos.getObject("Arctic");
         XmlObject[] pnrElementInfos = arctic.getObjects("PnrElementInfos");

         for (XmlObject pnrElementInfo2 : pnrElementInfos) {
            String sProv = pnrElementInfo2.getAttribute("provider");

            XmlObject[] pnrElementInfo = pnrElementInfo2.getObjects("PnrElementInfo");

            for (XmlObject element : pnrElementInfo) {
               String sType = element.getAttribute("type");

               if (!sProv.equals(sProvider)) {
                  sType = "[ " + sType + " ]";
               } else {

                  // validate the className
                  if (bCorrectClassName && element.getAttribute("code").equals("0")
                        && (element.getAttribute("className").length() == 0)) {
                     element.setAttribute("className", "net.ifao.arctic.agents" + sRequest + "framework.elements.Element"
                           + DtdGenerator.getUnFormatedProvider(sType));

                  }
               }

               XmlObject[] params = element.getObjects("PnrElementParamInfo");
               Vector<String> list = htPnrInfo.get(sType);

               if (list == null) {
                  list = new Vector<>();

                  htPnrInfo.put(sType, list);
               }

               for (XmlObject param : params) {
                  String sParam = param.getAttribute("id");

                  if (!list.contains(sParam) && !list.contains("[ " + sParam + " ]")) {
                     if (!sProv.equals(sProvider)) {
                        sParam = "[ " + sParam + " ]";
                     }

                     list.add(sParam);
                  }
               }
            }
         }
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }

      // get the 'correct' PnrElementTable
      Object[] keys = htPnrInfo.keySet().toArray();

      Arrays.sort(keys);
      jComboPnrAttribute.removeAllItems();
      jComboPnrElement.removeAllItems();

      for (Object key : keys) {
         String sKey = key.toString();

         if (!sKey.startsWith("[ ") || (htPnrInfo.get(sKey.substring(2, sKey.length() - 2)) == null)) {
            jComboPnrElement.addItem(sKey);
         }
      }

      jPanelCombo.setVisible(keys.length > 0);

      if (keys.length > 0) {
         jComboPnrElement.setSelectedIndex(0);
         jComboPnrElement_actionPerformed(null);
      }

   }

   /**
    * Method selectJComboPnrInfo
    *
    * @param sText
    * @author Andreas Brod
    */
   public void selectJComboPnrInfo(String sText)
   {
      StringTokenizer st = new StringTokenizer(sText, " \\/.");
      JComboBox jBox = jComboPnrElement;

      while (st.hasMoreTokens()) {
         String sItem = st.nextToken();

         for (int i = 0; i < jBox.getItemCount(); i++) {
            if (jBox.getItemAt(i).equals(sItem) || jBox.getItemAt(i).equals("[ " + sItem + " ]")) {
               jBox.setSelectedIndex(i);

               jBox = jComboPnrAttribute;
            }
         }
      }
   }

   /**
    * Method jComboPnrElement_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jComboPnrElement_actionPerformed(ActionEvent e)
   {

      jComboPnrAttribute.removeAllItems();

      HashSet<String> elements = new HashSet<>();

      String sElement = (String) jComboPnrElement.getSelectedItem();

      if (sElement != null) {
         Vector<String> list = htPnrInfo.get(sElement);

         if (list != null) {
            for (int i = 0; i < list.size(); i++) {
               elements.add(list.get(i));
            }
         }

         if (!sElement.startsWith("[")) {
            sElement = "[ " + sElement + " ]";
            list = htPnrInfo.get(sElement);

            if (list != null) {
               for (int i = 0; i < list.size(); i++) {
                  String sAttr = list.get(i);

                  if (sAttr.endsWith("]")) {
                     sAttr = sAttr.substring(1, sAttr.length() - 1).trim();
                  }

                  if (!elements.contains("[ " + sAttr + " ]") && !elements.contains(sAttr)) {
                     elements.add("[ " + sAttr + " ]");
                  }
               }
            }

         }

         Object[] eList = elements.toArray();

         Arrays.sort(eList);

         for (Object element : eList) {
            jComboPnrAttribute.addItem(element);
         }
      }
   }

   /**
    * Method jRequestResponse_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jRequestResponse_actionPerformed(ActionEvent e)
   {
      jTree1.setModel(jRequestResponse.getSelectedIndex() == 0);
   }

   /**
    * Method getPnrElements
    *
    * @return
    * @author $author$
    */
   private String getPnrElements()
   {
      return jPNRElement.getText().trim();
   }

   /**
    * Method updateNotSupportedButton
    * @author Andreas Brod
    */
   void updateNotSupportedButton()
   {
      jButtonNotSupported.setVisible(jTxtRules.getText().length() == 0);
      jButtonAdditionalDocument.setVisible(jTxtRules.getText().length() == 0);
   }

   /**
    * Method textChanged
    * @author $author$
    */
   void textChanged()
   {

      if ((jTxtRules.getText().trim() + jTxtRef.getText().trim() + getPnrElements()).length() > 0) {
         GregorianCalendar cal = new GregorianCalendar();

         jTxtDate.setText(cal.get(Calendar.DATE) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR) + " - "
               + jUser.getText().trim());
      } else {
         jTxtDate.setText("---");
      }
      setChanged(true);
   }

   Color colorBack = null;

   private String sSelectedtAgent = "";

   /**
    * Method setChanged
    *
    * @param pbChanged
    * @author Andreas Brod
    */
   void setChanged(boolean pbChanged)
   {
      if (bChanged != pbChanged) {
         if (colorBack == null) {
            colorBack = jMenuItemExit.getForeground();
         }

         if (!pbChanged) {
            jMenuItemExit.setForeground(colorBack);
         } else {
            jMenuItemExit.setForeground(Color.red);

         }

      }

      updateNotSupportedButton();

      bChanged = pbChanged;
   }

   /**
    * Method setBuildButton
    *
    * @param pbEnabled
    * @author Andreas Brod
    */
   void setBuildButton(boolean pbEnabled)
   {
      if (colorBack == null) {
         colorBack = jMenuItemSave.getForeground();
      }

      if (pbEnabled) {
         jMenuItemSave.setForeground(colorBack);
      } else {
         jMenuItemSave.setForeground(Color.red);
      }
   }

   /**
    * Method jTxtRules_keyReleased
    *
    * @param e
    * @author $author$
    */
   void jTxtRules_keyReleased(KeyEvent e)
   {
      textChanged();
   }

   /**
    * Method jTxtRef_keyReleased
    *
    * @param e
    * @author $author$
    */
   void jTxtRef_keyReleased(KeyEvent e)
   {
      textChanged();
   }

   public String save(boolean pbSave, StringBuffer sbFiles, XmlObject pArcticPnrElementInfos, Set<File> phsFiles)
   {
      DtdMain.startWaitThread("Save HTML");

      jTree1.storePath();

      String sSourceList = getSourceList(data, getRequest(false), getAgentListItem(false), jTree1, pbSave, sbFiles,
            pArcticPnrElementInfos, phsFiles);

      setChanged(false);
      jRequestResponse.setSelectedIndex(jTree1.getOldRequestResponseIndex());
      jTree1.reStorePath();

      DtdMain.stopWaitThread();

      return sSourceList;
   }

   /**
    * Method getSourceList
    *
    * @param dtdData
    * @param sRoot
    * @param sAgentName
    *
    * @return
    * @author Andreas Brod
    * @param pArcticPnrElementInfos
    * @param phsFiles
    */
   public static String getSourceList(DtdData dtdData, String sRoot, String sAgentName, boolean pbSave, StringBuffer sbFiles,
                                      XmlObject pArcticPnrElementInfos, Set<File> phsFiles)
   {

      DtdTree jTree1 = new DtdTree(null);
      jTree1.init(dtdData);
      String sBasePath = sRoot + sAgentName.substring(0, sAgentName.indexOf("\\agents\\") + 8);

      String sPath = sAgentName.substring(sAgentName.indexOf("\\agents\\") + 8, sAgentName.lastIndexOf("\\"));

      sAgentName = sAgentName.substring(sAgentName.lastIndexOf("\\") + 1);

      jTree1.loadModel(false, sBasePath + sPath, sAgentName);

      return getSourceList(null, sPath, sAgentName, jTree1, pbSave, sbFiles, pArcticPnrElementInfos, phsFiles);
   }

   /**
    * Method getSourceList
    *
    * @param data
    * @param sPath
    * @param sAgentName
    * @param jTree1
    *
    * @return
    * @author Andreas Brod
    * @param pArcticPnrElementInfos
    * @param phsFiles
    */
   public static String getSourceList(DtdData data, String sPath, String sAgentName, DtdTree jTree1, boolean pbSave,
                                      StringBuffer sbFiles, XmlObject pArcticPnrElementInfos, Set<File> phsFiles)
   {
      XmlObject xmlData = new XmlObject("<" + sAgentName + " />").getFirstObject();
      DtdMain.startWaitThread("Build HTML");
      jTree1.storePath();

      String sSourceList = "";

      String sAgentPath = sPath + "\\" + sAgentName;

      jTree1.setModel(false);

      String sResponse = jTree1.getHtml(sAgentPath);
      xmlData.addObject(jTree1.getXml(sAgentPath));

      sSourceList = "<RESPONSE>\n" + jTree1.getSourceInfo() + "</RESPONSE>\n";

      jTree1.setModel(true);

      String sRequest = jTree1.getHtml(sAgentPath);
      xmlData.addObject(jTree1.getXml(sAgentPath));

      sSourceList += "<REQUEST>\n" + jTree1.getSourceInfo() + "</REQUEST>\n";

      if (pbSave && (data != null)) {

         sbFiles.append(data.writeList(sRequest, sResponse, pArcticPnrElementInfos, xmlData, phsFiles));
      }

      return sSourceList;
   }

   /**
    * Method closeDtdInfo
    * @author Andreas Brod
    */
   void closeDtdInfo()
   {
      if (bChanged) {
         if (ConfimDialog.getBoolean(this, "Do you really want to exit without saving ?")) {
            bChanged = false;
         }
      }

      if (!bChanged) {
         active = false;

         saveSettings();

         setVisible(false);
      }
   }

   /**
    * Method saveSettings
    * @author $author$
    */
   private void saveSettings()
   {
      data.setSettings(jUser.getText().trim(), (int) this.getLocation().getX(), (int) this.getLocation().getY(), this.getWidth(),
            this.getHeight(), jSplitPane1.getDividerLocation(), jSplitPane2.getDividerLocation(),
            jSplitPane3.getDividerLocation(), getAgentListItem(false), getRequest(false), jArcticPath.getText());

   }

   /**
    * Method isActive
    *
    * @return
    * @author $author$
    */
   @Override
   public boolean isActive()
   {
      return active;
   }

   /**
    * Method jPNRElement_keyReleased
    *
    * @param e
    * @author Andreas Brod
    */
   void jPNRElement_keyReleased(KeyEvent e)
   {
      if (e != null) {
         char key = e.getKeyChar();

         if ((key >= 'a') && (key <= 'z')) {
            int pos = jPNRElement.getCaretPosition();

            jPNRElement.setText(jPNRElement.getText().toUpperCase());
            jPNRElement.setCaretPosition(pos);
         }
      }

      textChanged();
   }

   /**
    * Method jPNRElement_focusLost
    *
    * @param e
    * @author Rainer Kaufmann
    */
   void jPNRElement_focusLost(FocusEvent e)
   {
      spacesAroundSlashes(jPNRElement);
   }

   /**
    * Method jTxtRules_focusLost
    *
    * @param e
    * @author Rainer Kaufmann
    */
   void jTxtRules_focusLost(FocusEvent e)
   {
      spacesAroundSlashes(jTxtRules);
   }

   /**
    * Method jTxtRef_focusLost
    *
    * @param e
    * @author Rainer Kaufmann
    */
   void jTxtRef_focusLost(FocusEvent e)
   {
      spacesAroundSlashes(jTxtRef);
   }

   /**
    * Method spacesAroundSlashes
    * When leaving the pTextArea all slashes ("/") will be surrounded by spaces
    * except in closing HTML tags
    *
    * @param pTextArea the textarea to change
    * @author Rainer Kaufmann
    */
   void spacesAroundSlashes(JTextArea pTextArea)
   {

      String sText = pTextArea.getText();
      // get the text of TextArea2
      StringBuffer sb = new StringBuffer(sText);

      // only if the text of the textarea is at least 3 characters long
      if ((sb.length() > 2) && (sText.indexOf(DtdFrame.DOC_HEF) < 0)) {

         // position of the slash to inspect
         int nextSlash = sb.indexOf("/");

         // position one character behind the slash to inspect
         int nextIndex = 0;

         // as long as slashes are found
         while (nextSlash > -1) {

            // if the slash is at the beginning and not followed by a space,
            // insert a space behind the slash
            if (nextSlash == 0) {
               if (!Character.isWhitespace(sb.charAt(nextSlash + 1))) {
                  sb.insert(nextSlash + 1, ' ');
               }
            } else if (nextSlash == (sb.length() - 1)) {

               // if the slash is at the end and there is no space in front
               // and no "<" in front, insert a space at the slash position
               if ((!Character.isWhitespace(sb.charAt(nextSlash - 1))) && (sb.charAt(nextSlash - 1) != '<')) {
                  sb.insert(nextSlash, ' ');
               }
            } else {

               // if the slash is somewhere inside the text
               // and only if it is not part of a HTML tag
               if (sb.charAt(nextSlash - 1) != '<') {

                  // insert a space behind if not already there
                  if (!Character.isWhitespace(sb.charAt(nextSlash + 1))) {
                     sb.insert(nextSlash + 1, ' ');
                  }

                  // insert a space in front if not already there
                  if (!Character.isWhitespace(sb.charAt(nextSlash - 1))) {
                     sb.insert(nextSlash, ' ');
                  }
               }
            }

            // go to the position behind the slash
            nextIndex = sb.indexOf("/", nextIndex) + 1;

            // find the next slash
            nextSlash = sb.indexOf("/", nextIndex);
         }

         // write the changes back to the textArea2
         pTextArea.setText(sb.toString());

         // get the changes to the internal vector
         //        setText();


      }
   }

   /**
    * Method jButtonUsePnr_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jButtonUsePnr_actionPerformed(ActionEvent e)
   {
      try {
         boolean bEdit = false;
         String sNew = jComboPnrElement.getSelectedItem().toString();

         if (sNew.startsWith("[")) {
            sNew = sNew.substring(1, sNew.length() - 1).trim();
            bEdit = true;
         }

         String sAttr = jComboPnrAttribute.getSelectedItem().toString();

         if (sAttr.endsWith("]")) {
            sAttr = sAttr.substring(1, sAttr.length() - 1).trim();
            bEdit = true;
         }

         sNew += "." + sAttr;

         if (!sNew.startsWith(".")) {
            jPNRElement.setText(sNew);
            jCheckBoxSelfDef.setSelected(false);
            jPNRElement_keyReleased(null);
         }

         if (bEdit) {
            jButtonEdit_actionPerformed(e);
         }
      }
      catch (Exception ex) {}
   }

   /**
    * Method getProvider
    *
    * @return
    * @author Andreas Brod
    */
   private String getProvider()
   {
      String sProvider = jMenuProvider.getText();
      if (sProvider.indexOf(":") < 0) {
         sProvider = ":" + sProvider;
      }

      sProvider = sProvider.substring(sProvider.indexOf(":") + 1) + ":";

      return sProvider.substring(0, sProvider.indexOf(":")).trim();

   }

   /**
    * Method getAgentListItem
    *
    * @return
    * @author Andreas Brod
    */
   private String getAgentListItem(boolean pbSelectedtAgent)
   {
      if (pbSelectedtAgent) {
         sSelectedtAgent = jComboAgent.getSelectedItem().toString();
      }

      return sSelectedtAgent;
   }

   void start2Generate(boolean pbGenerateJavaFiles, Set<File> phsFiles)
   {
      boolean orignalFormatXmlOutput = XmlObject.getBFormatXmlOutput();

      XmlObject.setBFormatXmlOutput(false);

      StringBuffer sb = new StringBuffer();

      setBuildButton(true);

      // build Html
      String sProvider = getProvider();

      // get the PNR-elements
      Hashtable<String, List<String>> htElements = data.getPnrElements();

      if (htElements.size() == 0) {
         save(true, sb, getProviderElementInfo(), phsFiles);
      } else if (sProvider.length() > 0) {
         sb.append(DtdGenerator.generate(this, jArcticPath.getText(), DtdData.SRCPATH + "\\" + getRequest(false), sProvider,
               getAgentListItem(false), htElements, arcticPnrElementInfos, jUser.getText(), pbGenerateJavaFiles,
               new Hashtable<String, String>(), phsFiles));

      }
      DtdResultPage.show(this, "Result", true, sb.toString());

      XmlObject.setBFormatXmlOutput(orignalFormatXmlOutput);
   }

   public XmlObject getProviderElementInfo()
   {
      XmlObject pnrElementInfos = null;
      try {
         pnrElementInfos = arcticPnrElementInfos.getObject("Arctic").findSubObject("PnrElementInfos", "provider", getProvider());
      }
      catch (RuntimeException e) {
         // make nothing
      }
      return pnrElementInfos;
   }

   DtdPnrElementInfosDialog dtdPnrElementInfosDialog = null;
   JPanel jPanelCombo = new JPanel();
   BorderLayout borderLayout19 = new BorderLayout();
   JPanel jPanel21 = new JPanel();
   JPanel jPanel22 = new JPanel();
   BorderLayout borderLayout20 = new BorderLayout();
   JCheckBox jCheckBoxSelfDef = new JCheckBox();
   JCheckBox jCheckBoxProvider = new JCheckBox();
   JButton jButtonPnrList = new JButton();
   JPanel jPanel24 = new JPanel();
   JButton jButtonNotSupported = new JButton();
   JButton jButtonAdditionalDocument = new JButton();
   BorderLayout borderLayout21 = new BorderLayout();
   TitledBorder titledBorder4;
   private JSplitPane jSplitPane4Combo = null;
   private JMenuBar jJMenuBar = null;
   private JMenu jMenuFile = null;
   private JMenuItem jMenuItemOpen = null;
   private JMenuItem jMenuItemSave = null;
   private JMenuItem jMenuItemSaveHtml = null;
   private JMenuItem jMenuItemExit = null;
   private JMenu jMenuTools = null;
   private JMenuItem jMenuItemGenerate = null;
   private JMenu jMenuProvider = null;
   private JMenuItem jMenuItemPnrElements = null;
   private JMenuItem jMenuItemToDo = null;
   private JMenu jMenuHelp = null;
   private JMenuItem jMenuItemHistory = null;
   private JMenuItem jMenuItemNew = null;
   private JDialog jDialogSettings = null; //  @jve:decl-index=0:visual-constraint="51,645"
   private JPanel jContentDialog = null;
   private JPanel jPanel = null;
   private JPanel jPanelClose = null;
   private JButton jButtonCloseSettings = null;

   private JMenuItem jMenuItemSchema = null;

   private JPanel jPanel14 = null;

   JTextField jTextFieldMandatory = null;

   JLabel jLabelMandatory = null;
   private final JPanel panel = new JPanel();
   private final JLabel jLabelAgent = new JLabel("");

   /**
    * Method showDtdPnrElementInfosDialog
    *
    * @param sProvider
    * @param sPnr
    * @param sAttr
    * @author Andreas Brod
    */
   private void showDtdPnrElementInfosDialog(String sProvider, String sPnr, String sAttr, boolean pbModal)
   {
      if (dtdPnrElementInfosDialog == null) {
         Hashtable<String, List<String>> htOtherElements = new Hashtable<>();
         Object[] keys = htPnrInfo.keySet().toArray();
         for (Object key : keys) {
            String sKey = key.toString();
            Object[] elements = htPnrInfo.get(sKey).toArray();
            List<String> newList = new ArrayList<>();
            htOtherElements.put(sKey, newList);
            for (Object element : elements) {
               newList.add(element.toString());
            }
         }
         dtdPnrElementInfosDialog = new DtdPnrElementInfosDialog(this,
               jArcticPath.getText() + DtdData.SRCPATH + "\\" + getRequest(false), pbModal, htOtherElements);
      }
      if (dtdPnrElementInfosDialog.isVisible()) {
         dtdPnrElementInfosDialog.setVisible(false);
      }
      dtdPnrElementInfosDialog.setModal(pbModal);
      dtdPnrElementInfosDialog.load(this, arcticPnrElementInfos, sProvider, sPnr, sAttr, jTxtComment.getText().trim());
      dtdPnrElementInfosDialog.setVisible(true);

   }

   /**
    * Method jButtonEdit_actionPerformed
    *
    * @param e
    * @author $author$
    */
   void jButtonEdit_actionPerformed(ActionEvent e)
   {
      try {
         String sPnrElement = getPnrElements();

         setBuildButton(false);

         if (sPnrElement.indexOf("\n") > 0) {
            sPnrElement = InputString.getString(this, "Select Element", sPnrElement);
         }

         if (sPnrElement.length() == 0) {
            return;
         }

         String sPnr = DtdData.getFormatedPnrElement(sPnrElement);
         String sAttr = sPnr.substring(sPnr.indexOf(".") + 1).trim();

         sPnr = sPnr.substring(0, sPnr.indexOf(".")).trim();

         String sProvider = getProvider();

         if ((sProvider.length() > 0) && (sPnr.length() > 0) && (arcticPnrElementInfos != null)) {

            showDtdPnrElementInfosDialog(sProvider, sPnr, sAttr, true);

            /*
             * if (dtdPnrElementInfo == null) {
             *   dtdPnrElementInfo =
             *       new DtdPnrElementInfo(this, "PnrElementInfo", true,
             *                             jArcticPath.getText(),
             *                             arcticPnrElementInfos);
             * }
             *
             * dtdPnrElementInfo.load(sProvider, sPnr, sAttr,
             *                      DtdData.SRCPATH + "\\" + getRequest(),
             *                      jTxtComment.getText().trim());
             */

            loadProviderCombo(false);
            selectJComboPnrInfo(getPnrElements());

            if ((jComboPnrAttribute.getSelectedItem() == null)
                  || (jPNRElement.getText().indexOf("." + jComboPnrAttribute.getSelectedItem().toString()) <= 0)) {
               jPNRElement.setText("");
            } else {}
         }
      }
      catch (Exception ex) {}
   }

   void toDoList()
   {

      // search through directory

      File f = new File(jArcticPath.getText() + DtdData.SRCPATH + "\\" + getRequest(false) + "\\framework");

      StringBuffer sb = new StringBuffer();

      fillToDo(sb, f);

      String sToDo = sb.toString();

      if (sToDo.length() > 0) {
         DtdResultPage.show(this, "Result", true, sToDo);

      }
   }


   /**
    * Method fillToDo
    *
    * @param sb
    * @param f
    * @author $author$
    */
   private void fillToDo(StringBuffer sb, File f)
   {
      String sAbs = f.getAbsolutePath();

      if (f.exists()) {
         String sName = f.getName();

         if (f.isDirectory() && !sName.startsWith(".")) {
            File[] files = f.listFiles();

            for (File file : files) {
               fillToDo(sb, file);
            }
         } else if (sName.endsWith(".java")) {
            String sToDo = DtdGenerator.getToDoInfo(Util.loadFromFile(sAbs));

            if (sToDo.length() > 0) {
               sb.append("--- " + sName + " ---\n");
               sb.append(sToDo);
            }
         }
      }
   }

   /**
    * Method jCheckBoxProvider_actionPerformed
    *
    * @param e
    * @author $author$
    */
   void jCheckBoxProvider_actionPerformed(ActionEvent e)
   {
      Color colorSave = jButtonLoad.getForeground();

      // test
      saveSettings();
      setPath();

      setForeground4Selection(colorSave);
   }

   private void setForeground4Selection(Color colorSave)
   {
      jButtonLoad.setForeground(colorSave);
      jComboAgent.setForeground(colorSave);
      jComboRequest.setForeground(colorSave);
      jLabel3.setForeground(colorSave);
      jCheckBoxProvider.setForeground(colorSave);
   }

   void newAgent()
   {
      DtdNewAgent newAgent = new DtdNewAgent(this, jArcticPath.getText());

      newAgent.setVisible(true);

      if (newAgent.created) {
         data.setPath(jArcticPath.getText().trim());
         setPath();

      }
   }

   /**
    * Method jButtonPnrList_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jButtonPnrList_actionPerformed(ActionEvent e)
   {
      String sPnrElement = "";
      Hashtable<String, List<String>> htElements = data.getPnrElements();

      String[] find = { "=true ", "=false " };
      String[] direction = { " <- ", " -> " };
      String[] type = { "(Request)", "(Response)" };
      String sAbstract = "";

      for (int j = 0; j < find.length; j++) {
         if (sPnrElement.length() > 0) {
            sPnrElement += "\n\n";
         }

         Enumeration<String> enumElements = htElements.keys();

         while (enumElements.hasMoreElements()) {
            String sKey = enumElements.nextElement();
            List<String> elements = htElements.get(sKey);

            List<String> foundElements = new Vector<>();

            for (int i = 0; i < elements.size(); i++) {
               String sElement = elements.get(i);

               if (sElement.indexOf(find[j]) >= 0) {
                  sElement = sElement.replaceFirst(find[j], direction[j]);

                  foundElements.add("- " + sElement + "\n");

                  if ((sAbstract.length() == 0) && (sElement.indexOf("*") > 0)) {
                     sAbstract = "\nwith * marked elements contain self defined methods\n";
                  }
               }

            }

            if (foundElements.size() > 0) {
               char[] line = (sKey + " for " + type[j]).toCharArray();

               sPnrElement += (new String(line)) + "\n";

               Arrays.fill(line, '=');

               sPnrElement += (new String(line)) + "\n";

               Object[] oFoundElements = foundElements.toArray();

               Arrays.sort(oFoundElements);

               for (Object oFoundElement : oFoundElements) {
                  sPnrElement += oFoundElement;
               }
            }

            if (!sPnrElement.endsWith("\n\n")) {
               sPnrElement += "\n";
            }
         }
      }

      DtdResultPage.show(this, "Result", true, sPnrElement + sAbstract);

   }

   /**
    * Method jButtonNotSupported_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jButtonNotSupported_actionPerformed(ActionEvent e)
   {
      if (jTxtRules.getText().length() == 0) {
         jTxtRules.setText("not supported");
         textChanged();
      }
   }

   /**
    * This method initializes jSplitPane4Combo
    *
    * @return javax.swing.JSplitPane
    */
   private JSplitPane getJSplitPane4Combo()
   {
      if (jSplitPane4Combo == null) {
         jSplitPane4Combo = new JSplitPane();

         jSplitPane4Combo.setDividerLocation(0.5d);
         jSplitPane4Combo.setDividerSize(5);
         jSplitPane4Combo.setLeftComponent(jComboPnrElement);
         jSplitPane4Combo.setRightComponent(jComboPnrAttribute);
      }

      return jSplitPane4Combo;
   }

   /**
    * This method initializes jJMenuBar
    *
    * @return javax.swing.JMenuBar
    */
   private JMenuBar getJJMenuBar()
   {
      if (jJMenuBar == null) {
         jJMenuBar = new JMenuBar();
         jJMenuBar.add(getJMenuFile());
         jJMenuBar.add(getJMenuTools());
         jJMenuBar.add(getJMenuProvider());
         jJMenuBar.add(getJMenuHelp());
      }
      return jJMenuBar;
   }

   /**
    * This method initializes jMenuFile
    *
    * @return javax.swing.JMenu
    */
   private JMenu getJMenuFile()
   {
      if (jMenuFile == null) {
         jMenuFile = new JMenu();
         jMenuFile.setText("File");
         jMenuFile.setMnemonic('F');
         jMenuFile.add(getJMenuItemSave());
         jMenuFile.add(getJMenuItemSaveHtml());
         jMenuFile.addSeparator();
         jMenuFile.add(getJMenuItemOpen());
         jMenuFile.addSeparator();
         jMenuFile.add(getJMenuItemExit());
      }
      return jMenuFile;
   }

   /**
    * This method initializes jMenuItemOpen
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItemOpen()
   {
      if (jMenuItemOpen == null) {
         jMenuItemOpen = new JMenuItem();
         jMenuItemOpen.setText("Properties ...");
         jMenuItemOpen.setMnemonic('P');
         jMenuItemOpen.setToolTipText("Open the DtdInfo properties");
         jMenuItemOpen.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               getJDialogSettings().setVisible(true);
            }
         });
      }
      return jMenuItemOpen;
   }

   /**
    * This method initializes jMenuItemSave
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItemSave()
   {
      if (jMenuItemSave == null) {
         jMenuItemSave = new JMenuItem();
         jMenuItemSave.setText("Save");
         jMenuItemSave.setMnemonic('S');

         jMenuItemSave.setToolTipText("Save the current agent");
         jMenuItemSave.setEnabled(false);
         jMenuItemSave.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               Set<File> hsFiles = new HashSet<>();
               start2Generate(false, hsFiles);
               refresh(hsFiles);
            }
         });
      }
      return jMenuItemSave;
   }

   protected void refresh(Set<File> hsFiles)
   {
      //      for (File file : hsFiles) {
      //         String sPath = file.getAbsolutePath();
      //         if (sPath.endsWith(".java")) {
      //            int iPos =
      //               sPath.indexOf(File.separator + "src" + File.separator + "net" + File.separator
      //                     + "ifao");
      //            if (iPos > 0) {
      //               String sEclipsePath = sPath.substring(0, iPos);
      //               String sClassName = sPath.substring(iPos + 1);
      //               net.ifao.plugins.tools.CodeFormatterApplication.formatCode(file, sEclipsePath,
      //                     sClassName, false);
      //               System.out.println("Format: " + sPath);
      //            }
      //         }
      //      }

   }

   private JMenuItem getJMenuItemSaveHtml()
   {
      if (jMenuItemSaveHtml == null) {
         jMenuItemSaveHtml = new JMenuItem();
         jMenuItemSaveHtml.setText("Save only HTML");
         jMenuItemSaveHtml.setMnemonic('H');
         jMenuItemSaveHtml.setToolTipText("Save the HTML of current agent");
         jMenuItemSaveHtml.setEnabled(false);
         jMenuItemSaveHtml.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               Set<File> hsFiles = new HashSet<>();
               save(true, new StringBuffer(), getProviderElementInfo(), hsFiles);
               refresh(hsFiles);

            }
         });
      }
      return jMenuItemSaveHtml;
   }

   /**
    * This method initializes jMenuItemExit
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItemExit()
   {
      if (jMenuItemExit == null) {
         jMenuItemExit = new JMenuItem();
         jMenuItemExit.setText("Exit");
         jMenuItemExit.setMnemonic('x');
         jMenuItemExit.setToolTipText("Exit DtdInfo");
         jMenuItemExit.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               closeDtdInfo();
            }
         });
      }
      return jMenuItemExit;
   }

   /**
    * This method initializes jMenuTools
    *
    * @return javax.swing.JMenu
    */
   private JMenu getJMenuTools()
   {
      if (jMenuTools == null) {
         jMenuTools = new JMenu();
         jMenuTools.setText("Tools");
         jMenuTools.setMnemonic('T');
         jMenuTools.add(getJMenuItemGenerate());
         jMenuTools.add(getJMenuItemNew());
      }
      return jMenuTools;
   }

   /**
    * This method initializes jMenuItemGenerate
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItemGenerate()
   {
      if (jMenuItemGenerate == null) {
         jMenuItemGenerate = new JMenuItem();
         jMenuItemGenerate.setToolTipText("... save and start the generation of r2a Classes");
         jMenuItemGenerate.setText("Generate r2a Classes");
         jMenuItemGenerate.setMnemonic('G');
         jMenuItemGenerate.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               try {
                  Set<File> hsFiles = new HashSet<>();
                  start2Generate(true, hsFiles);
                  refresh(hsFiles);

               }
               catch (Exception ex) {
                  Util.showException(ex);
               }
            }
         });
      }
      return jMenuItemGenerate;
   }

   /**
    * This method initializes jMenuProvider
    *
    * @return javax.swing.JMenu
    */
   private JMenu getJMenuProvider()
   {
      if (jMenuProvider == null) {
         jMenuProvider = new JMenu();
         jMenuProvider.setText("Provider");
         jMenuProvider.setMnemonic('P');
         jMenuProvider.setToolTipText("This provider is currently loaded");
         jMenuProvider.add(getJMenuItemPnrElements());
         jMenuProvider.add(getJMenuItemToDo());
         jMenuProvider.add(getJMenuItemSchema());

         jMenuProvider.setVisible(false);
      }
      return jMenuProvider;
   }

   /**
    * This method initializes jMenuItemPnrElements
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItemPnrElements()
   {
      if (jMenuItemPnrElements == null) {
         jMenuItemPnrElements = new JMenuItem();
         jMenuItemPnrElements.setEnabled(false);
         jMenuItemPnrElements.setToolTipText("Display / Edit business elements");
         jMenuItemPnrElements.setText("Business Elements ...");
         jMenuItemPnrElements.setMnemonic('E');
         jMenuItemPnrElements.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               showDtdPnrElementInfosDialog(getProvider(), "", "", false);
            }
         });
      }
      return jMenuItemPnrElements;
   }

   /**
    * This method initializes jMenuItemToDo
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItemToDo()
   {
      if (jMenuItemToDo == null) {
         jMenuItemToDo = new JMenuItem();
         jMenuItemToDo.setText("to do ?");
         jMenuItemToDo.setMnemonic('?');
         jMenuItemToDo.setToolTipText("Display a toDo List for this provider");
         jMenuItemToDo.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               toDoList();
            }
         });
      }
      return jMenuItemToDo;
   }

   /**
    * This method initializes jMenuHelp
    *
    * @return javax.swing.JMenu
    */
   private JMenu getJMenuHelp()
   {
      if (jMenuHelp == null) {
         jMenuHelp = new JMenu();
         jMenuHelp.setText("Help");
         jMenuHelp.setMnemonic('H');
         jMenuHelp.add(getJMenuItemHistory());
      }
      return jMenuHelp;
   }

   /**
    * This method initializes jMenuItemHistory
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItemHistory()
   {
      if (jMenuItemHistory == null) {
         jMenuItemHistory = new JMenuItem();
         jMenuItemHistory.setText("History");
         jMenuItemHistory.setMnemonic('H');
         jMenuItemHistory.setToolTipText("Display the History of DTDInfo");
         jMenuItemHistory.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               DtdResultPage.show(getThisFrame(), "History", true, Util.loadFromFile("Generator\\History.txt"));

            }
         });
      }
      return jMenuItemHistory;
   }

   /**
    * This method initializes jMenuItemNew
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItemNew()
   {
      if (jMenuItemNew == null) {
         jMenuItemNew = new JMenuItem();
         jMenuItemNew.setText("Create new agent ...");
         jMenuItemNew.setMnemonic('C');
         jMenuItemNew.setToolTipText("... create a new new arctic agent");
         jMenuItemNew.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               newAgent();
            }
         });
      }
      return jMenuItemNew;
   }

   /**
    * This method initializes jDialogSettings
    *
    * @return javax.swing.JDialog
    */
   private JDialog getJDialogSettings()
   {
      if (jDialogSettings == null) {
         jDialogSettings = new JDialog();
         jDialogSettings.setModal(true);
         jDialogSettings.setSize(new Dimension(350, 150));
         jDialogSettings.setLocation(data.getSetting("left"), data.getSetting("top"));
         jDialogSettings.setTitle("Settings");
         jDialogSettings.setContentPane(getJContentDialog());
      }
      return jDialogSettings;
   }

   /**
    * This method initializes jContentDialog
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJContentDialog()
   {
      if (jContentDialog == null) {
         jContentDialog = new JPanel();
         jContentDialog.setLayout(new BorderLayout());
         jContentDialog.setPreferredSize(new Dimension(500, 100));
         jContentDialog.add(getJPanel(), BorderLayout.NORTH);
         jContentDialog.add(getJPanelClose(), BorderLayout.SOUTH);
      }
      return jContentDialog;
   }

   /**
    * This method initializes jPanel
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel()
   {
      if (jPanel == null) {
         GridLayout gridLayout = new GridLayout();
         gridLayout.setRows(2);
         gridLayout.setColumns(1);
         jPanel = new JPanel();
         jPanel.setLayout(gridLayout);
         jPanel.add(jPanel3, null);
         jPanel.add(jPanel4, null);
      }
      return jPanel;
   }

   /**
    * This method initializes jPanelClose
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanelClose()
   {
      if (jPanelClose == null) {
         jPanelClose = new JPanel();
         jPanelClose.setLayout(new BorderLayout());
         jPanelClose.add(getJButtonCloseSettings(), BorderLayout.EAST);
      }
      return jPanelClose;
   }

   /**
    * This method initializes jButtonCloseSettings
    *
    * @return javax.swing.JButton
    */
   private JButton getJButtonCloseSettings()
   {
      if (jButtonCloseSettings == null) {
         jButtonCloseSettings = new JButton();
         jButtonCloseSettings.setText("Close");
         jButtonCloseSettings.setToolTipText("Close the settings");
         jButtonCloseSettings.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               setPath();
               jDialogSettings.setVisible(false);
            }
         });
      }
      return jButtonCloseSettings;
   }

   /**
    * @param e  ActionEvent
    */
   public void jButtonAdditionalDocument_actionPerformed(ActionEvent e)
   {
      String agentListItem = getAgentListItem(false);
      if ((jTxtRules.getText().length() == 0) && (agentListItem != null)) {
         char[] cs = jTxtTitle.getText().toCharArray();
         String sName = agentListItem + "_";
         for (char element : cs) {
            if (((element >= 'A') && (element <= 'Z')) || ((element >= 'a') && (element <= 'z'))
                  || ((element >= '0') && (element <= '9')) || (element == '_')) {
               sName += element;
            }
         }
         if (sName.length() > 0) {
            jTxtRules.setText(DOC_REF + sName + ".html\">Additional Document for " + jTxtTitle.getText() + "</a>");
            textChanged();
         }
      }

   }

   /**
    * This method initializes jMenuItemSchema
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItemSchema()
   {
      if (jMenuItemSchema == null) {
         jMenuItemSchema = new JMenuItem();
         jMenuItemSchema.setText("Schema Generator");
         jMenuItemSchema.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               new ClipboardOwner()
               {
                  public void set(String s)
                  {
                     StringSelection stringSelection = new StringSelection(s);
                     Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                     clipboard.setContents(stringSelection, this);

                  }

                  @Override
                  public void lostOwnership(Clipboard clipboard, Transferable contents)
                  {
                     // TODO Auto-generated method stub

                  }
               }.set(jTree1.getSchema());


               JOptionPane.showMessageDialog(parentFrame, "Schema copied to clipboard.");
            }
         });
      }
      return jMenuItemSchema;
   }

   /**
    * This method initializes jPanel14
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel14()
   {
      if (jPanel14 == null) {
         jLabelMandatory = new JLabel();
         jLabelMandatory.setText("throw AgentException");
         FlowLayout flowLayout = new FlowLayout();
         flowLayout.setAlignment(FlowLayout.LEFT);
         flowLayout.setVgap(0);
         jPanel14 = new JPanel();
         jPanel14.setLayout(flowLayout);
         jPanel14.add(jCheckBoxMandatory, null);
         jPanel14.add(jLabelMandatory, null);
         jPanel14.add(getJTextFieldMandatory(), null);
      }
      return jPanel14;
   }


   /**
    * This method initializes jTextFieldMandatory
    *
    * @return javax.swing.JTextField
    */
   private JTextField getJTextFieldMandatory()
   {
      if (jTextFieldMandatory == null) {
         jTextFieldMandatory = new JTextField();
         jTextFieldMandatory.setPreferredSize(new Dimension(120, 20));
      }
      return jTextFieldMandatory;
   }


}


/**
 * Class DtdFrame_jArcticPath_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdFrame_jArcticPath_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jArcticPath_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jArcticPath_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
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
      adaptee.jArcticPath_actionPerformed(e);
   }
}


/**
 * Class DtdFrame_jSearch_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdFrame_jSearch_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jSearch_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jSearch_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
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
      adaptee.jSearch_actionPerformed(e);
   }
}


/**
 * Class DtdFrame_jRequest2_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdFrame_jRequest2_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jRequest2_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jRequest2_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
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
      adaptee.jRequest2_actionPerformed(e);
   }
}


/**
 * Class DtdFrame_jAgentList_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdFrame_jAgentList_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jAgentList_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jAgentList_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
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
      adaptee.jAgentList_actionPerformed(e);
   }
}


/**
 * Class DtdFrame_jButtonLoad_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdFrame_jButtonLoad_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jButtonLoad_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jButtonLoad_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
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
      adaptee.jButtonLoad_actionPerformed(e);
   }
}


/**
 * Class DtdFrame_jRequestResponse_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdFrame_jRequestResponse_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jRequestResponse_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jRequestResponse_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
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
      adaptee.jRequestResponse_actionPerformed(e);
   }
}


/**
 * Class DtdFrame_jTxtRules_keyAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
class DtdFrame_jTxtRules_keyAdapter
   extends java.awt.event.KeyAdapter
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jTxtRules_keyAdapter
    *
    * @param adaptee
    */
   DtdFrame_jTxtRules_keyAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method keyReleased
    *
    * @param e
    * @author $author$
    */
   @Override
   public void keyReleased(KeyEvent e)
   {
      adaptee.jTxtRules_keyReleased(e);
   }
}


/**
 * Class DtdFrame_jTxtRef_keyAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
class DtdFrame_jTxtRef_keyAdapter
   extends java.awt.event.KeyAdapter
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jTxtRef_keyAdapter
    *
    * @param adaptee
    */
   DtdFrame_jTxtRef_keyAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method keyReleased
    *
    * @param e
    * @author $author$
    */
   @Override
   public void keyReleased(KeyEvent e)
   {
      adaptee.jTxtRef_keyReleased(e);
   }
}


/**
 * Class DtdFrame_jPNRElement_keyAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdFrame_jPNRElement_keyAdapter
   extends java.awt.event.KeyAdapter
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jPNRElement_keyAdapter
    *
    * @param adaptee
    */
   DtdFrame_jPNRElement_keyAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method keyReleased
    *
    * @param e
    * @author Andreas Brod
    */
   @Override
   public void keyReleased(KeyEvent e)
   {
      adaptee.jPNRElement_keyReleased(e);
   }
}


/**
 * Class DtdFrame_jPNRElement_focusAdapter
 *
 * <p>
 * Copyright &copy; 2004, i:FAO, AG.
 * @author Rainer Kaufmann
 */
class DtdFrame_jPNRElement_focusAdapter
   extends java.awt.event.FocusAdapter
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jPNRElement_focusAdapter
    *
    * @param adaptee
    */
   DtdFrame_jPNRElement_focusAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method focusLost
    *
    * @param e
    * @author Rainer Kaufmann
    */
   @Override
   public void focusLost(FocusEvent e)
   {
      adaptee.jPNRElement_focusLost(e);
   }
}


/**
 * Class DtdFrame_jTxtRules_focusAdapter
 *
 * <p>
 * Copyright &copy; 2004, i:FAO, AG.
 * @author Rainer Kaufmann
 */
class DtdFrame_jTxtRules_focusAdapter
   extends java.awt.event.FocusAdapter
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jTxtRules_focusAdapter
    *
    * @param adaptee
    */
   DtdFrame_jTxtRules_focusAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method focusLost
    *
    * @param e
    * @author Rainer Kaufmann
    */
   @Override
   public void focusLost(FocusEvent e)
   {
      adaptee.jTxtRules_focusLost(e);
   }
}


/**
 * Class DtdFrame_jTxtRef_focusAdapter
 *
 * <p>
 * Copyright &copy; 2004, i:FAO, AG.
 * @author Rainer Kaufmann
 */
class DtdFrame_jTxtRef_focusAdapter
   extends java.awt.event.FocusAdapter
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jTxtRef_focusAdapter
    *
    * @param adaptee
    */
   DtdFrame_jTxtRef_focusAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method focusLost
    *
    * @param e
    * @author Rainer Kaufmann
    */
   @Override
   public void focusLost(FocusEvent e)
   {
      adaptee.jTxtRef_focusLost(e);
   }
}


/**
 * Class DtdFrame_jComboPnrElement_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdFrame_jComboPnrElement_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jComboPnrElement_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jComboPnrElement_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
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
      adaptee.jComboPnrElement_actionPerformed(e);
   }
}


/**
 * Class DtdFrame_jButtonUsePnr_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdFrame_jButtonUsePnr_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jButtonUsePnr_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jButtonUsePnr_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
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
      adaptee.jButtonUsePnr_actionPerformed(e);
   }
}


/**
 * Class DtdFrame_jButtonEdit_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
class DtdFrame_jButtonEdit_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jButtonEdit_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jButtonEdit_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method actionPerformed
    *
    * @param e
    * @author $author$
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      adaptee.jButtonEdit_actionPerformed(e);
   }
}


/**
 * Class DtdFrame_jCheckBoxProvider_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
class DtdFrame_jCheckBoxProvider_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jCheckBoxProvider_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jCheckBoxProvider_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method actionPerformed
    *
    * @param e
    * @author $author$
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      adaptee.jCheckBoxProvider_actionPerformed(e);
   }
}


/**
 * Class DtdFrame_jButtonPnrList_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdFrame_jButtonPnrList_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jButtonPnrList_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jButtonPnrList_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
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
      adaptee.jButtonPnrList_actionPerformed(e);
   }
}


/**
 * Class DtdFrame_jButtonNotSupported_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdFrame_jButtonNotSupported_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jButtonNotSupported_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jButtonNotSupported_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
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
      adaptee.jButtonNotSupported_actionPerformed(e);
   }
}


/**
 * Class DtdFrame_jButtonNotSupported_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdFrame_jButtonAdditionalDocument_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdFrame adaptee;

   /**
    * Constructor DtdFrame_jButtonNotSupported_actionAdapter
    *
    * @param adaptee
    */
   DtdFrame_jButtonAdditionalDocument_actionAdapter(DtdFrame adaptee)
   {
      this.adaptee = adaptee;
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
      adaptee.jButtonAdditionalDocument_actionPerformed(e);
   }
}
