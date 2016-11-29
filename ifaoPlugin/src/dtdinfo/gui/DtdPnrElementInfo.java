package dtdinfo.gui;


import ifaoplugin.Util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.*;

import net.ifao.dialogs.swing.ConfimDialog;
import net.ifao.xml.XmlObject;
import dtdinfo.DtdGenerator;


/**
 * Class DtdPnrElementInfo
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
public class DtdPnrElementInfo
   extends JDialog
{
   /**
   * 
   */
   private static final long serialVersionUID = -4606625962773192781L;
   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel jPanel1 = new JPanel();
   JPanel jPanel2 = new JPanel();
   JPanel jPanelPro = new JPanel();
   JPanel jPanelElem = new JPanel();
   JPanel jPanelParam = new JPanel();
   BorderLayout borderLayout2 = new BorderLayout();
   JPanel jPanel6 = new JPanel();
   JButton jButtonDelete = new JButton();
   BorderLayout borderLayout3 = new BorderLayout();
   String sDefaPath;
   XmlObject arcticPnrElementInfos;
   Frame parentFrame;

   /**
    * Constructor DtdPnrElementInfo
    * arcticPnrElementInfos
    * @param frame
    * @param title
    * @param modal
    * @param sPath
    * @param pArcticPnrElementInfos
    */
   public DtdPnrElementInfo(Frame frame, String title, boolean modal, String sPath,
                            XmlObject pArcticPnrElementInfos)
   {
      super(frame, title, modal);
      parentFrame = frame;

      if (sPath.endsWith("\\")) {
         sPath = sPath.substring(0, sPath.length() - 1);
      }

      sDefaPath = sPath;
      arcticPnrElementInfos = pArcticPnrElementInfos;

      try {
         jbInit();
         initAdditional();
         pack();
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }

      if (getHeight() > frame.getHeight()) {
         setSize(getWidth() + 30, frame.getHeight());
      }

      setLocation(
            (int) Math.max(0, frame.getLocation().getX() + (frame.getWidth() - getWidth()) / 2),
            (int) Math.max(0, frame.getLocation().getY() + (frame.getHeight() - getHeight()) / 2));
   }

   Hashtable<String, JComponent> elementRef = new Hashtable<String, JComponent>();
   String sProvider = null, sElement = null, sParam = null;
   JPanel jPanelPro2 = new JPanel();
   JPanel jPanelPro1 = new JPanel();
   GridLayout gridLayout4 = new GridLayout();
   GridLayout gridLayout5 = new GridLayout();
   BorderLayout borderLayout4 = new BorderLayout();
   BorderLayout borderLayout5 = new BorderLayout();
   JPanel jPanelElem1 = new JPanel();
   JPanel jPanelElem2 = new JPanel();
   GridLayout gridLayout1 = new GridLayout();
   GridLayout gridLayout2 = new GridLayout();
   BorderLayout borderLayout6 = new BorderLayout();
   JPanel jPanelPara1 = new JPanel();
   JPanel jPanelPara2 = new JPanel();
   GridLayout gridLayout3 = new GridLayout();
   GridLayout gridLayout6 = new GridLayout();
   JLabel jLabel1 = new JLabel();
   JLabel jLabel2 = new JLabel();
   JLabel jLabel3 = new JLabel();
   JScrollPane jScrollPane1 = new JScrollPane();
   JButton jButtonUse = new JButton();
   JLabel jLabel4 = new JLabel();
   JButton jButtonIgnore = new JButton();

   /**
    * Method initAdditional
    * @author Andreas Brod
    */
   private void initAdditional()
   {
      try {
         XmlObject xsd =
            new XmlObject(Util.loadFromFile(Util
                  .getConfFile(sDefaPath, "ArcticPnrElementInfos.xsd")));
         XmlObject schema = xsd.getObject("schema");
         XmlObject[] elements = schema.getObjects("element");
         XmlObject[] simple = schema.getObjects("simpleType");
         Hashtable<String, String> ht = new Hashtable<String, String>();

         ht.put("xs:boolean", " true false");

         for (XmlObject element : simple) {
            String sName = element.getAttribute("name");
            String sEnums = "";
            XmlObject[] enums = element.getObject("restriction").getObjects("enumeration");

            for (XmlObject enum1 : enums) {
               sEnums += " " + enum1.getAttribute("value");
            }

            ht.put(sName, sEnums.trim());
         }

         XmlObject pnrElementInfos = null;
         XmlObject pnrElementInfo = null;
         XmlObject pnrElementPara = null;

         // get related elements
         for (XmlObject element : elements) {
            if (element.getAttribute("name").equals("PnrElementInfos")) {
               pnrElementInfos = element;
            }

            if (element.getAttribute("name").equals("PnrElementInfo")) {
               pnrElementInfo = element;
            }

            if (element.getAttribute("name").equals("PnrElementParamInfo")) {
               pnrElementPara = element;
            }
         }
         if (pnrElementInfos != null) {
            add(jPanelPro1, jPanelPro2, "Pro", "provider", pnrElementInfos.getObject("complexType")
                  .getObjects("attribute"), ht);
         }

         if (pnrElementInfo != null) {
            add(jPanelElem1, jPanelElem2, "Elem", "type", pnrElementInfo.getObject("complexType")
                  .getObjects("attribute"), ht);
         }
         if (pnrElementPara != null) {
            add(jPanelPara1, jPanelPara2, "Param", "id", pnrElementPara.getObject("complexType")
                  .getObjects("attribute"), ht);
         }

      }
      catch (Exception ex) {}
   }

   /**
    * Method add
    *
    *
    * @param jPan1
    * @param jPan2
    * @param sType
    * @param sDisabled
    * @param attributes
    * @param ht
    * @author Andreas Brod
    */
   private void add(JPanel jPan1, JPanel jPan2, String sType, String sDisabled,
                    XmlObject[] attributes, Hashtable<String, String> ht)
   {

      /*
       * gl.setColumns(2);
       * gl.setRows(1 + attributes.length);
       */
      int iLines = attributes.length;

      for (XmlObject attribute : attributes) {
         String sName = attribute.getAttribute("name");
         boolean bDisabled = sName.equals(sDisabled);
         String sToolTip =
            attribute.createObject("annotation").createObject("documentation").getCData();

         if (sToolTip.length() == 0) {
            sToolTip = sName;
         }

         String sKey = sType + "." + sName;

         if (attribute.getAttribute("use").equals("required")) {
            sName += "*";
            sToolTip += " (Mandatory)";
         }

         JLabel jl = new JLabel(sName + " :");

         jl.setHorizontalAlignment(SwingConstants.RIGHT);
         jl.setToolTipText(sToolTip);
         jl.setEnabled(!bDisabled);

         String sEnum = ht.get(attribute.getAttribute("type"));

         jPan1.add(jl);

         if (sEnum != null) {
            JComboBox j2 = new JComboBox();
            StringTokenizer st = new StringTokenizer(sEnum, " ");
            Vector<String> list = new Vector<String>();

            j2.addItem("- NULL -");

            boolean bEnum = false;

            while (st.hasMoreTokens()) {
               String sToken = st.nextToken();

               list.add(sToken);

               if (sToken.equals("ENUMERATION")) {
                  bEnum = true;
               }
            }

            Object[] o = list.toArray();

            Arrays.sort(o);

            for (Object element : o) {
               j2.addItem(element.toString());
            }

            jPan2.add(j2);
            elementRef.put(sKey, j2);

            if (bDisabled) {
               j2.setEnabled(false);
            }

            if (bEnum) {
               jl = new JLabel(" ");

               jl.setHorizontalAlignment(SwingConstants.RIGHT);
               jPan1.add(jl);

               JTextField jText = new JTextField("");

               jPan2.add(jText);

               j2.addActionListener(new ComoboListener(j2, jText));

               elementRef.put(sKey + ".ENUM", jText);

               iLines++;

            }
         } else {
            JTextField j2 = new JTextField("");

            jPan2.add(j2);
            elementRef.put(sKey, j2);

            if (bDisabled) {
               j2.setEnabled(false);
            }
         }
      }

      GridLayout gl = (GridLayout) jPan1.getLayout();

      gl.setColumns(1);
      gl.setRows(iLines);
      gl.setHgap(2);

      gl = (GridLayout) jPan2.getLayout();

      gl.setColumns(1);
      gl.setRows(iLines);
      gl.setHgap(2);

   }

   /**
    * Class ComoboListener
    *
    * <p>
    * Copyright &copy; 2002, i:FAO, AG.
    * @author Andreas Brod
    */
   class ComoboListener
      implements java.awt.event.ActionListener
   {
      JTextField adaptee;
      JComboBox box;

      /**
       * Constructor ComoboListener
       *
       * @param box
       * @param adaptee
       */
      ComoboListener(JComboBox box, JTextField adaptee)
      {
         this.adaptee = adaptee;
         this.box = box;
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
         String sItem = (String) box.getSelectedItem();

         if ((sItem != null) && sItem.equals("ENUMERATION")) {
            adaptee.setEnabled(true);
            adaptee.setBackground(Color.white);
         } else {
            adaptee.setEnabled(false);
            adaptee.setBackground(jPanel1.getBackground());

            // adaptee.setText("");
         }

      }

   }

   /**
    * Method load
    *
    * @param psProvider
    * @param psElement
    * @param psParam
    * @author Andreas Brod
    * @param psPath
    * @param sDefaultHint
    */
   public void load(String psProvider, String psElement, String psParam, String psPath,
                    String sDefaultHint)
   {
      this.sProvider = psProvider;
      this.sElement = psElement;
      this.sParam = psParam;

      if (psPath.startsWith("\\")) {
         psPath = psPath.substring(1);
      }

      if (psPath.startsWith("src\\")) {
         psPath = psPath.substring(4);
      }

      char[] cPackage = psPath.toCharArray();

      for (int i = 0; i < cPackage.length; i++) {
         if (cPackage[i] == '\\') {
            cPackage[i] = '.';
         }
      }

      XmlObject[] allPnrElementInfos =
         arcticPnrElementInfos.createObject("Arctic").getObjects("PnrElementInfos");

      XmlObject xPnrElementInfos =
         arcticPnrElementInfos.createObject("Arctic").createObject("PnrElementInfos", "provider",
               psProvider, true);

      XmlObject xPnrElementInfo =
         xPnrElementInfos.createObject("PnrElementInfo", "type", psElement, true);

      // fill with defaults
      Vector<XmlObject> lstAttribute = new Vector<XmlObject>();

      // search over all providers
      for (XmlObject allPnrElementInfo2 : allPnrElementInfos) {

         // search for related PnrElement (sElement) for this provider
         XmlObject allPnrElementInfo =
            allPnrElementInfo2.createObject("PnrElementInfo", "type", psElement, false);

         // if found ...
         if (allPnrElementInfo != null) {

            // ... search for Param (sParam) for this Element
            XmlObject allPnrElementParamInfo =
               allPnrElementInfo.createObject("PnrElementParamInfo", "id", psParam, false);

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
         xPnrElementInfo.setAttribute("name", DtdGenerator.getUnFormatedProvider(psElement));
         xPnrElementInfo.setAttribute("category", "STANDARD");
      }

      // get/create the param...
      XmlObject xPnrElementParamInfo =
         xPnrElementInfo.createObject("PnrElementParamInfo", "id", psParam, true);

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
         xPnrElementParamInfo.setAttribute("name", DtdGenerator.getUnFormatedProvider(psParam));
         xPnrElementParamInfo.setAttribute("type", "FREE_TEXT");
         xPnrElementParamInfo.setAttribute("hint", sDefaultHint);

      }

      xPnrElementInfo.setAttribute("className", (new String(cPackage))
            + ".framework.elements.Element" + DtdGenerator.getUnFormatedProvider(psElement));

      for (String sKey : elementRef.keySet()) {
         String sKey1 = sKey.substring(0, sKey.indexOf("."));
         String sKey2 = sKey.substring(sKey.indexOf(".") + 1);
         String sVal = "";

         if (sKey1.equals("Pro")) {
            sVal = xPnrElementInfos.getAttribute(sKey2);
         } else if (sKey1.equals("Elem")) {
            sVal = xPnrElementInfo.getAttribute(sKey2);
         } else if (sKey1.equals("Param")) {
            if (sKey2.endsWith(".ENUM")) {

               XmlObject[] list = xPnrElementParamInfo.getObjects("PnrElementParamEnum");

               for (XmlObject element : list) {
                  if (sVal.length() > 0) {
                     sVal += ",";
                  }

                  String sName = element.getAttribute("name");
                  String sValue = element.getAttribute("value");

                  if ((sName.length() == 0) || sName.equals(sValue)) {
                     sVal += sValue;
                  } else {
                     sVal += sName + "=" + sValue;

                  }
               }
            } else {
               sVal = xPnrElementParamInfo.getAttribute(sKey2);
            }
         }

         JComponent jc = elementRef.get(sKey);

         if (jc instanceof JTextField) {
            ((JTextField) jc).setText(sVal);
         } else if (jc instanceof JComboBox) {
            JComboBox box = (JComboBox) jc;

            if (sVal.length() == 0) {
               sVal = "- NULL -";
            }

            int iId = -1;

            for (int j = 0; (iId < 0) && (j < box.getItemCount()); j++) {
               if (box.getItemAt(j).equals(sVal)) {
                  iId = j;
               }
            }

            if (iId < 0) {
               box.addItem(sVal);
               box.setSelectedItem(sVal);
            } else {
               box.setSelectedIndex(iId);
            }
         }
      }

      setVisible(true);
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
      panel1.setLayout(borderLayout1);
      jPanel2.setLayout(borderLayout2);
      jButtonDelete.setToolTipText("");
      jButtonDelete.setText("Delete");
      jButtonDelete.addActionListener(new DtdPnrElementInfo_jButtonDelete_actionAdapter(this));
      jPanel1.setLayout(borderLayout3);
      jPanelPro.setLayout(borderLayout4);
      jPanelElem.setLayout(borderLayout5);
      jPanelParam.setLayout(borderLayout6);
      jPanelPro.setBorder(BorderFactory.createLoweredBevelBorder());
      jPanelElem.setBorder(BorderFactory.createLoweredBevelBorder());
      jPanelParam.setBorder(BorderFactory.createLoweredBevelBorder());
      jPanelPro1.setLayout(gridLayout4);
      jPanelPro2.setLayout(gridLayout5);
      jPanelElem1.setLayout(gridLayout1);
      jPanelElem2.setLayout(gridLayout2);
      jPanelPara1.setLayout(gridLayout3);
      jPanelPara2.setLayout(gridLayout6);
      borderLayout4.setHgap(5);
      borderLayout4.setVgap(5);
      borderLayout5.setHgap(5);
      borderLayout5.setVgap(5);
      borderLayout6.setHgap(5);
      borderLayout6.setVgap(5);
      jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel1.setText("Provider");
      jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel2.setText("Element");
      jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel3.setText("Attribute");
      jButtonUse.setText("Use");
      jButtonUse.addActionListener(new DtdPnrElementInfo_jButtonUse_actionAdapter(this));
      jLabel4.setText("Set Attribute within ArcticPnrElementInfos.xml");
      jButtonIgnore.setText("Cancel");
      jButtonIgnore.addActionListener(new DtdPnrElementInfo_jButtonIgnore_actionAdapter(this));
      getContentPane().add(panel1);
      panel1.add(jPanel1, BorderLayout.SOUTH);
      jPanel1.add(jPanel6, BorderLayout.EAST);
      jPanel6.add(jLabel4, null);
      jPanel6.add(jButtonUse, null);
      jPanel6.add(jButtonDelete, null);
      jPanel6.add(jButtonIgnore, null);
      jPanelPro.add(jPanelPro1, BorderLayout.WEST);
      jPanelPro.add(jPanelPro2, BorderLayout.CENTER);
      jPanelPro.add(jLabel1, BorderLayout.NORTH);
      jPanel2.add(jPanelElem, BorderLayout.CENTER);
      jPanel2.add(jPanelPro, BorderLayout.NORTH);
      jPanelElem.add(jPanelElem1, BorderLayout.WEST);
      jPanelElem.add(jPanelElem2, BorderLayout.CENTER);
      jPanelElem.add(jLabel2, BorderLayout.NORTH);
      jPanel2.add(jPanelParam, BorderLayout.SOUTH);
      jPanelParam.add(jPanelPara1, BorderLayout.WEST);
      jPanelParam.add(jPanelPara2, BorderLayout.CENTER);
      jPanelParam.add(jLabel3, BorderLayout.NORTH);
      panel1.add(jScrollPane1, BorderLayout.CENTER);
      jScrollPane1.getViewport().add(jPanel2, null);
   }

   /**
    * Method jButtonDelete_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jButtonDelete_actionPerformed(ActionEvent e)
   {
      if (!ConfimDialog.getBoolean(parentFrame, "Do you really want to delete " + sElement + "."
            + sParam + " in ArcticPnrElementInfo.xml")) {
         return;
      }
      if (sProvider == null) {
         return;
      }

      XmlObject xPro =
         arcticPnrElementInfos.createObject("Arctic").createObject("PnrElementInfos", "provider",
               sProvider, true);

      XmlObject xElem = xPro.createObject("PnrElementInfo", "type", sElement, true);

      XmlObject xParam = xElem.createObject("PnrElementParamInfo", "id", sParam, true);

      xElem.deleteObjects(xParam);

      this.setVisible(false);
   }

   /**
    * Method jButtonUse_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jButtonUse_actionPerformed(ActionEvent e)
   {
      if (sProvider == null) {
         return;
      }

      XmlObject xPro =
         arcticPnrElementInfos.createObject("Arctic").createObject("PnrElementInfos", "provider",
               sProvider, true);

      XmlObject xElem = xPro.createObject("PnrElementInfo", "type", sElement, true);

      XmlObject xTypes = xElem.createObject("PnrElementRequestTypes");

      xElem.deleteObjects(xTypes);

      XmlObject xParam = xElem.createObject("PnrElementParamInfo", "id", sParam, true);

      for (String sKey : elementRef.keySet()) {
         String sKey1 = sKey.substring(0, sKey.indexOf("."));
         String sKey2 = sKey.substring(sKey.indexOf(".") + 1);
         XmlObject o = null;

         if (sKey1.equals("Pro")) {
            o = xPro;
         } else if (sKey1.equals("Elem")) {
            o = xElem;
         } else if (sKey1.equals("Param")) {
            o = xParam;
         }

         String sText = "";
         JComponent jc = elementRef.get(sKey);

         if (jc instanceof JTextField) {
            sText = ((JTextField) jc).getText();
         } else if (jc instanceof JComboBox) {
            JComboBox box = (JComboBox) jc;

            sText = (String) box.getSelectedItem();
         }

         if ((sText == null) || (sText.length() == 0) || sText.equals("- NULL -")) {
            sText = null;
         }

         if (sKey2.endsWith(".ENUM") && (o != null)) {
            if (sText != null) {
               o.deleteObjects("PnrElementParamEnum");

               StringTokenizer st = new StringTokenizer(sText, ",");

               while (st.hasMoreTokens()) {
                  String sVal = st.nextToken().trim();
                  String sName = "";

                  if (sVal.indexOf("=") > 0) {
                     sName = sVal.substring(0, sVal.indexOf("="));
                     sVal = sVal.substring(sVal.indexOf("=") + 1);
                  }

                  XmlObject paramEnum = (new XmlObject("<PnrElementParamEnum />")).getFirstObject();

                  o.addObject(paramEnum);

                  if (sName.length() > 0) {
                     paramEnum.setAttribute("name", sName);
                  }

                  paramEnum.setAttribute("value", sVal);
               }
            }
         } else if (o != null) {
            o.setAttribute(sKey2, sText);
         }
      }

      // validate xTypes
      XmlObject xType = xTypes.createObject("PnrElementRequestType");

      if (xType.getAttribute("type").length() == 0) {
         xType.setAttribute("type", "HTTPPOST");
      }

      xElem.addObject(xTypes);

      setVisible(false);
   }

   /**
    * @param e  ActionEvent
    */
   void jButtonIgnore_actionPerformed(ActionEvent e)
   {
      setVisible(false);
   }
}


/**
 * Class DtdPnrElementInfo_jButtonDelete_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdPnrElementInfo_jButtonDelete_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdPnrElementInfo adaptee;

   /**
    * Constructor DtdPnrElementInfo_jButtonDelete_actionAdapter
    *
    * @param adaptee
    */
   DtdPnrElementInfo_jButtonDelete_actionAdapter(DtdPnrElementInfo adaptee)
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
      adaptee.jButtonDelete_actionPerformed(e);
   }
}


/**
 * Class DtdPnrElementInfo_jButtonUse_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdPnrElementInfo_jButtonUse_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdPnrElementInfo adaptee;

   /**
    * Constructor DtdPnrElementInfo_jButtonUse_actionAdapter
    *
    * @param adaptee
    */
   DtdPnrElementInfo_jButtonUse_actionAdapter(DtdPnrElementInfo adaptee)
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
      adaptee.jButtonUse_actionPerformed(e);
   }
}


class DtdPnrElementInfo_jButtonIgnore_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdPnrElementInfo adaptee;

   DtdPnrElementInfo_jButtonIgnore_actionAdapter(DtdPnrElementInfo adaptee)
   {
      this.adaptee = adaptee;
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      adaptee.jButtonIgnore_actionPerformed(e);
   }
}
