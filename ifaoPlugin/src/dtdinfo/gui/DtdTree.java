package dtdinfo.gui;


import ifaoplugin.Util;

import java.awt.Component;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.text.JTextComponent;
import javax.swing.tree.*;

import net.ifao.xml.DtdObject;
import net.ifao.xml.XmlObject;
import dtdinfo.*;


/** 
 * Class DtdTree 
 * 
 * <p> 
 * Copyright &copy; 2002, i:FAO, AG. 
 * @author Andreas Brod 
 */
public class DtdTree
   extends JTree
{

   /** 
    * 
    */
   private static final long serialVersionUID = -5006060142402464142L;
   private DefaultMutableTreeNode root;
   private DefaultTreeModel treeModel;

   private DtdData data;
   private DtdObject selected;
   private boolean bRequestActive;
   private DtdFrame frame;

   private XmlObject request, response;

   static String[] dim = { "F8", "F4", "F0", "EB", "E8", "E4", "E0", "DB" };

   /** 
    * TODO (brod) add comment for method 
    * 
    * @author brod 
    */
   static String[] cols = { "FFFFF0", dim[0] + dim[0] + dim[1], dim[4] + dim[4] + dim[5],
         dim[1] + dim[1] + dim[2], dim[5] + dim[5] + dim[6], dim[2] + dim[2] + dim[3],
         dim[6] + dim[6] + dim[7], dim[3] + dim[3] + dim[4] };

   // "D0D0B0", "A4A4C1", "D4D4F1", "B4B4D1", "E4E4FF", "C4C4E1", "F4F4FF";

   /** 
    * Method getTableStart 
    * 
    * <p> TODO rename deep to piDeep
    * @param deep 
    * @return 
    * 
    * @author Andreas Brod 
    */
   protected static final String getTableStart(int deep)
   {
      deep = Math.min(deep, cols.length - 2);

      String sData = "Data" + deep;

      if (deep < 0) {
         sData = "Header";
      }

      return "<table class='" + sData + "' "
            + "border='0' cellpadding='5'><colgroup><col style='width:20%'>"
            + "<col style='width:20%'><col style='width:30%'><col style='width:30%'>"
            + "</colgroup>";
   }

   /** 
    * Constructor DtdTree 
    * 
    * @param pData 
    * @param pFrame 
    * 
    */
   public DtdTree(DtdFrame pFrame)
   {
      frame = pFrame;
   }

   public void init(DtdData pData)
   {
      data = pData;

      root = new DefaultMutableTreeNode("Arctic");

      // new TreeObject(data,"Arctic - Root Object for new arctic",false);
      treeModel = new DefaultTreeModel(root);

      addTreeSelectionListener(new javax.swing.event.TreeSelectionListener()
      {
         @Override
         public void valueChanged(TreeSelectionEvent e)
         {
            treevalueChanged(e);
         }
      });
      setCellRenderer(new MyRenderer());

      // removeAll();
      setModel(treeModel);
   }

   /** 
    * Method treevalueChanged 
    * 
    * <p> TODO rename e to another name
    * @param e 
    * 
    * @author Andreas Brod 
    */
   private void treevalueChanged(TreeSelectionEvent e)
   {
      TreePath path = e.getNewLeadSelectionPath();

      if (path == null) {
         return;
      }

      /*
       * String sPath = "";
       *
       * for (int i = 0; i < path.getPathCount(); i++) {
       *   if (path.getPathComponent(i) instanceof TreeObject) {
       *       if (sPath.length() > 0) {
       *           sPath += ".";
       *       }
       *
       *       sPath += ((TreeObject) path.getPathComponent(i)).getText(false);
       *   }
       * }
       */

      if (path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
         DefaultMutableTreeNode treeObject = (DefaultMutableTreeNode) path.getLastPathComponent();

         setTxtTitle(treeObject);
      }

      if (frame != null) {
         frame.jTxtRules.requestFocus();
      }
   }

   /** 
    * Method setTxtTitle 
    * 
    * <p> TODO rename treeObject to pObject
    * @param treeObject 
    * 
    * @author Andreas Brod 
    */
   private void setTxtTitle(DefaultMutableTreeNode treeObject)
   {
      if (frame == null) {
         return;
      }

      setTxtTitle(treeObject, frame.jTxtTitle, frame.jTxtComment, frame.jCheckBoxMandatory,
            frame.jLabelMandatory, frame.jTextFieldMandatory, frame.jCheckBoxSelfDef,
            frame.jPNRElement, frame.jTxtRules, frame.jTxtRef, frame.jTxtDate);
      frame.updateNotSupportedButton();
   }

   /** 
    * Method loadModel 
    * 
    * <p> TODO rename bRequest to pbRequest, sPath to psPath, sName to psName
    * @param bRequest 
    * @param sPath 
    * @param sName 
    * 
    * @author Andreas Brod 
    */
   public void loadModel(boolean bRequest, String sPath, String sName)
   {
      DtdMain.startWaitThread("Load Model");

      request = data.getRequest(sName).getFirstObject();
      response = data.getResponse(sName).getFirstObject();

      if (request != null) {
         request.addObject(new XmlObject("<FixSettings />"));
         correctFor(request, "");
      }

      if (response != null) {
         response.addObject(new XmlObject("<FixSettings />"));
      }

      data.loadList(sPath + "\\" + sName);

      setModel(bRequest);

      sLastTitle = null;

      DtdMain.stopWaitThread();

      // root.expand(treeModel,this);


   }

   private void correctFor(XmlObject pRequest, String psFor)
   {
      String sFor = psFor;
      if (pRequest.getAttribute("for").length() > 0) {
         sFor = pRequest.getName();
      }
      XmlObject[] objects = pRequest.getObjects("");
      for (XmlObject object : objects) {
         correctFor(object, sFor);
      }
   }

   /** 
    * TODO (brod) add comment for method getSchema 
    * 
    * @return TODO (brod) add text for returnValue
    * 
    * @author brod 
    */
   public String getSchema()
   {
      String sRet =
         "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
               + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
               + "elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\n";
      sRet += "<xs:element name=\"Arctic\">\n";
      sRet += "<xs:annotation>\n";
      sRet += "   <xs:documentation>Comment describing your root element</xs:documentation>\n";
      sRet += "</xs:annotation>\n";
      sRet += "<xs:complexType>\n";
      sRet += "  <xs:sequence>\n";
      sRet += "      <xs:element name=\"Request\">\n";
      sRet += "         <xs:complexType>\n";
      sRet += "            <xs:sequence>\n";
      sRet +=
         "               <xs:element name=\"" + request.getName() + "\" type=\"Req"
               + request.getName() + "\"/>\n";
      sRet += "            </xs:sequence>\n";
      sRet += "         </xs:complexType>\n";
      sRet += "      </xs:element>\n";
      sRet += "      <xs:element name=\"Response\">\n";
      sRet += "         <xs:complexType>\n";
      sRet += "            <xs:sequence>\n";
      sRet +=
         "               <xs:element name=\"" + response.getName() + "\" type=\"Res"
               + response.getName() + "\"/>\n";
      sRet += "            </xs:sequence>\n";
      sRet += "         </xs:complexType>\n";
      sRet += "      </xs:element>\n";
      sRet += "   </xs:sequence>\n";
      sRet += "  </xs:complexType>\n";
      sRet += "  </xs:element>\n";


      getRemarks(data.getRequest().getRemark("Request"));
      getRemarks(data.getResponse().getRemark("Response"));
      StringBuffer sb = new StringBuffer();
      getSchema(data.getRequest(), request, "Req", sb);
      getSchema(data.getResponse(), response, "Res", sb);
      sRet += sb.toString();
      sRet += "</xs:schema>\n";
      return sRet;
   }

   /** 
    * TODO (brod) add comment for method getRemarks 
    * 
    * <p> TODO rename sText to psText
    * @param sText TODO (brod) add text for param sText
    * @return TODO (brod) add text for returnValue
    * 
    * @author brod 
    */
   private Hashtable<String, StringBuffer> getRemarks(String sText)
   {
      Hashtable<String, StringBuffer> ht = new Hashtable<String, StringBuffer>();
      BufferedReader reader = new BufferedReader(new StringReader(sText));
      String sLine;
      StringBuffer sb = null;
      try {
         while ((sLine = reader.readLine()) != null) {
            // replace tags
            sLine = sLine.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

            StringTokenizer st = new StringTokenizer(sLine, " \t");
            if (st.countTokens() > 2) {
               String s1 = st.nextToken();
               String s2 = st.nextToken();
               if (s2.startsWith("-")) {
                  sb = ht.get(s1);
                  if (sb == null) {
                     sb = new StringBuffer();
                     ht.put(s1, sb);
                  }
                  if (s2.length() > 1) {
                     sb.append(s2.substring(1) + " ");
                  }
               } else if (s1.endsWith("-")) {
                  s1 = s1.substring(0, s1.length() - 1);
                  if (s1.length() > 0) {
                     sb = ht.get(s1);
                     if (sb == null) {
                        sb = new StringBuffer();
                        ht.put(s1, sb);
                     }
                     sb.append(s2 + " ");
                  }
               } else if (sb != null) {
                  sb.append(s1 + " ");
                  sb.append(s2 + " ");

               }
            }
            if (sb != null) {
               while (st.hasMoreTokens()) {
                  sb.append(st.nextToken() + " ");
               }
               sb.append("\n");
            }
         }
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      // finally correct the remarks
      Enumeration<String> enumeration = ht.keys();
      while (enumeration.hasMoreElements()) {
         String key = enumeration.nextElement();
         String element = ht.get(key).toString();

         int iEnd = element.length();
         if (iEnd > 1) {
            while ((iEnd > 1) && (element.charAt(iEnd - 1) <= ' ')) {
               iEnd--;
            }
            ht.put(key, new StringBuffer(element.substring(0, iEnd)));
         }


      }
      return ht;
   }

   /** 
    * TODO (brod) add comment for method getSchema 
    * 
    * <p> TODO rename dtd to pDtdx to another name, sType to psType, sbResponse to pResponse
    * @param dtd TODO (brod) add text for param dtd
    * @param x TODO (brod) add text for param x
    * @param sType TODO (brod) add text for param sType
    * @param sbResponse TODO (brod) add text for param sbResponse
    * @param parentRemarks TODO (brod) add text for param parentRemarks
    * 
    * @author brod 
    */
   private void getSchema(DtdObject dtd, XmlObject x, String sType, StringBuffer sbResponse)
   {
      String sName = x.getName();

      boolean bSimpleContent = false;
      String sLine1 = "  <xs:complexType name=\"" + sType + sName + "\">\n";
      if (sbResponse.indexOf(sLine1) >= 0) {
         return;
      }

      String sRet = sLine1;
      Hashtable<String, StringBuffer> remarks = getRemarks(dtd.getRemark(sName));

      XmlObject[] objects = x.getObjects("");
      if ((objects.length == 1) && objects[0].getName().equalsIgnoreCase("%Text;")) {
         sRet += "    <xs:simpleContent>\n";
         sRet += "      <xs:extension base=\"xs:string\">\n";
         bSimpleContent = true;

      } else if (objects.length > 0) {
         sRet += "    <xs:sequence>\n";
         // get sub objects
         for (XmlObject object : objects) {
            String sOName = object.getName();
            if (sOName.equalsIgnoreCase("FixSettings")) {
               continue;
            }
            if (sOName.equalsIgnoreCase("%Text;")) {
               continue;
            }

            sRet += "      <xs:element name=\"" + sOName + "\" type=\"" + sType + sOName + "\"";
            if (dtd.isMandatory(sName, sOName)) {
               sRet += " minOccurs=\"1\"";
            } else {
               sRet += " minOccurs=\"0\"";
            }
            if (dtd.isList(sName, sOName)) {
               sRet += " maxOccurs=\"unbounded\"";
            } else {
               sRet += " maxOccurs=\"1\"";
            }

            StringBuffer sbParent = remarks.get(sOName);
            if ((sbParent != null) && (sbParent.length() > 0)) {
               sRet += ">\n";
               sRet += "      <xs:annotation>\n";
               sRet += "        <xs:documentation>" + sbParent.toString() + "</xs:documentation>\n";
               sRet += "      </xs:annotation>\n";
               sRet += "    </xs:element>\n";
            } else {
               sRet += "/>\n";
            }
         }
         sRet += "    </xs:sequence>\n";
      }
      // add the attributes
      String[] attributeNames = x.getAttributeNames(true);
      for (String attributeName : attributeNames) {

         sRet += "    <xs:attribute name=\"" + attributeName + "\"";
         String sTypes = x.getAttribute(attributeName);

         String sDefault = "";
         if (sTypes.indexOf(")_") > 0) {
            sDefault = sTypes.substring(sTypes.lastIndexOf(")_") + 2);
            sTypes = sTypes.substring(0, sTypes.lastIndexOf(")_") + 1);
         } else if ((sTypes.indexOf("_") > 0) && (sTypes.indexOf("(") < 0)) {
            sDefault = sTypes.substring(sTypes.lastIndexOf("_") + 1);
            sTypes = sTypes.substring(0, sTypes.lastIndexOf("_"));
         }
         List<String> lst = new ArrayList<String>();

         StringBuffer sb = remarks.get(attributeName);

         if (sTypes.endsWith("abc")) {
            sRet += " type=\"xs:string\"";
            if (sDefault.length() > 0) {
               sRet += " default=\"" + sDefault + "\"";
            }
         } else if (sTypes.indexOf("%DATE(") >= 0) {
            sRet += " type=\"xs:date\"";
         } else if (sTypes.indexOf("%DATETIME(") >= 0) {
            sRet += " type=\"xs:dateTime\"";
         } else if (sTypes.indexOf("yes") > 0) {
            sRet += " type=\"xs:boolean\"";
            if (sDefault.equalsIgnoreCase("yes")) {
               sRet += " default=\"true\"";
            }
            if (sDefault.equalsIgnoreCase("no")) {
               sRet += " default=\"false\"";
            }
         } else if (sTypes.indexOf("P0DT0") > 0) {
            sRet += " type=\"xs:duration\"";
         } else if (sTypes.indexOf("***") > 0) {
            sRet += " type=\"xs:string\"";
            if (sDefault.length() > 0) {
               sRet += " default=\"" + sDefault + "\"";
            }
         } else if (sTypes.endsWith("0.0")) {
            sRet += " type=\"xs:float\"";
         } else if (sTypes.endsWith("0")) {
            sRet += " type=\"xs:integer\"";
            if (sDefault.length() > 0) {
               sRet += " default=\"" + sDefault + "\"";
            }
         } else if (sTypes.startsWith("?(") || sTypes.startsWith("!(")) {
            StringTokenizer st =
               new StringTokenizer(sTypes.substring(3, sTypes.lastIndexOf(")")), "|");
            while (st.hasMoreTokens()) {
               lst.add(st.nextToken().trim());
            }
            if (sDefault.length() > 0) {
               sRet += " default=\"" + sDefault + "\"";
            }

         } else if (sb != null) {
            sb.insert(0, sTypes + "\n");
            System.err.println(sTypes);
         }

         if (sTypes.startsWith("!")) {
            sRet += " use=\"required\"";
         } else {
            sRet += " use=\"optional\"";
         }

         sRet += ">\n";
         if (sb != null) {
            sRet += "      <xs:annotation>\n";
            sRet += "        <xs:documentation>" + sb.toString() + "</xs:documentation>\n";
            sRet += "      </xs:annotation>\n";
         }
         if (lst.size() > 0) {
            sRet += "      <xs:simpleType>\n";
            sRet += "        <xs:restriction base=\"xs:NMTOKEN\">\n";
            for (String element : lst) {
               sRet += "          <xs:enumeration value=\"" + element + "\"/>\n";

            }
            sRet += "        </xs:restriction>\n";
            sRet += "      </xs:simpleType>\n";

         }
         sRet += "    </xs:attribute>\n";
      }
      if (bSimpleContent) {
         sRet += "    </xs:extension></xs:simpleContent>\n";
      }

      sRet += "  </xs:complexType>\n";
      for (XmlObject object : objects) {
         String sOName = object.getName();
         if (sOName.equalsIgnoreCase("FixSettings")) {
            continue;
         }
         if (sOName.equalsIgnoreCase("%Text;")) {
            continue;
         }
         getSchema(dtd, object, sType, sbResponse);
      }
      sbResponse.append(sRet);

   }

   TreePath tpOldPath = null;
   boolean bOldRequestActive = false;

   /** 
    * Method storePath 
    * @author Andreas Brod 
    */
   public void storePath()
   {
      tpOldPath = getSelectionPath();
      bOldRequestActive = bRequestActive;
   }

   /** 
    * Method getOldRequestResponseIndex 
    * 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public int getOldRequestResponseIndex()
   {
      return bOldRequestActive ? 0 : 1;

   }

   /** 
    * Method reStorePath 
    * @author Andreas Brod 
    */
   public void reStorePath()
   {
      if (bOldRequestActive != bRequestActive) {
         setModel(bOldRequestActive);
      }

      if (tpOldPath != null) {

         tpOldPath = getPath(tpOldPath);

         setSelectionPath(tpOldPath);
         scrollPathToVisible(tpOldPath);
      }

   }

   /** 
    * Method getPath 
    * 
    * @param pPath 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private TreePath getPath(TreePath pPath)
   {
      String sPath = pPath.toString();

      for (int i = 0; i < getRowCount(); i++) {
         TreePath tpPath = getPathForRow(i);
         String s = tpPath.toString();

         if (s.equalsIgnoreCase(sPath)) {
            return tpPath;
         }
      }
      if (getRowCount() > 0) {
         return getPathForRow(0);
      }

      return null;
   }

   /** 
    * Method setModel 
    * 
    * <p> TODO rename bRequest to pbRequest
    * @param bRequest 
    * 
    * @author Andreas Brod 
    */
   public void setModel(boolean bRequest)
   {
      bRequestActive = bRequest;
      selected = bRequest ? data.getRequest() : data.getResponse();

      XmlObject o = bRequest ? request : response;

      root.removeAllChildren();
      addChildren("-", root, o, "", bRequest);
      treeModel.reload(root);

      TreeNode[] path = treeModel.getPathToRoot(root);

      expandPath(new TreePath(path));

      Enumeration<?> enumChildren = root.children();

      while (enumChildren.hasMoreElements()) {
         Object treeObject = enumChildren.nextElement();

         if (treeObject instanceof TreeObject) {
            ((TreeObject) treeObject).expand(treeModel, this);
         }
      }

      repaint();
   }

   /** 
    * Method addChildren 
    * 
    * 
    * <p> TODO rename sParentName to psParentName, toAdd to pAddo to another name, sComment to psComment, bRequest to pbRequest
    * @param sParentName 
    * @param toAdd 
    * @param o 
    * @param sComment 
    * @param bRequest 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private TreeObject addChildren(String sParentName, DefaultMutableTreeNode toAdd, XmlObject o,
                                  String sComment, boolean bRequest)
   {
      if (o == null) {
         return null;
      }

      StringTokenizer st = new StringTokenizer(selected.getRemark(o.getName()), "\n\r");
      Vector<String> list = new Vector<String>();

      while (st.hasMoreTokens()) {
         list.add(st.nextToken().trim());
      }

      if (sComment.length() == 0) {
         sComment = o.getName();
      }

      if (selected.isMandatory(sParentName, o.getName())) {
         if (sComment.indexOf("-") > 0) {
            sComment =
               sComment.substring(0, sComment.indexOf("-")).trim() + "* "
                     + sComment.substring(sComment.indexOf("-"));
         } else {
            sComment = sComment.trim() + "*";
         }
      }

      TreeObject node = new TreeObject(data, sComment, bRequest);

      toAdd.add(node);

      String[] names = o.getAttributeNames(true);
      boolean active = false;

      for (String name2 : names) {
         String sName = name2;
         String sVal = o.getAttribute(sName);
         String sRemark = "";

         for (int j = 0; j < list.size(); j++) {
            if (list.get(j).startsWith(sName + " ")) {
               sRemark = list.get(j);

               if (sRemark.indexOf("-") > 0) {
                  sRemark = sRemark.substring(sRemark.indexOf("-"));
               }

               break;
            }
         }


         if (sVal.startsWith("!")) {
            sName = sName + "* " + sRemark;
         } else {
            sName = sName + " " + sRemark;
         }


         TreeObject node2 = new TreeObject(data, sName, bRequest);

         node.add(node2);

         if (!active && node2.isMarked()) {
            active = true;
         }
      }

      XmlObject[] subObjects = o.getObjects("");

      for (XmlObject subObject : subObjects) {
         String sName = subObject.getName();

         for (int j = 0; j < list.size(); j++) {
            if (list.get(j).startsWith(sName + " ")) {
               sName = list.get(j);
            }
         }

         if (addChildren(o.getName(), node, subObject, sName, bRequest).isMarked()) {
            active = true;
         }
      }

      // if (active){
      // }
      return node;
   }

   // SETTINGS
   TreeObject sLastTitle = null;

   /** 
    * Method setTxtTitle 
    * 
    * 
    * <p> TODO rename treeObject to pObject, title to pTitle, comment to pCommentcb to another name, cbOwn to pOwnc2 to another namec3 to another namec4 to another name
    * @param treeObject 
    * @param title 
    * @param comment 
    * @param cb 
    * @param cbOwn 
    * @param pPNRElement 
    * @param c2 
    * @param c3 
    * @param c4 
    * 
    * @author Andreas Brod 
    */
   private void setTxtTitle(DefaultMutableTreeNode treeObject, JTextComponent title,
                            JTextComponent comment, JCheckBox cb, JLabel labelMandatory,
                            JTextComponent agentExceptionMandatory, JCheckBox cbOwn,
                            JTextComponent pPNRElement, JTextComponent c2, JTextComponent c3,
                            JTextComponent c4)
   {
      String sMandatory = (cb.isEnabled() && cb.isSelected()) ? "!" : "";
      if (sMandatory.startsWith("!")) {
         try {
            String sText = agentExceptionMandatory.getText();
            int iNumber = Integer.parseInt(sText);
            if (iNumber > 0) {
               sMandatory += "MandatoryException(" + iNumber + ")";
            }
         }
         catch (Exception ex) {
            // invalid number
         }
      }

      String sOwn = (cbOwn.isEnabled() && cbOwn.isSelected()) ? "*" : "";

      if (sLastTitle != null) {
         sLastTitle.setValue(sMandatory + pPNRElement.getText() + sOwn, c2.getText(), c3.getText(),
               c4.getText());
      }

      if (treeObject instanceof TreeObject) {
         sLastTitle = (TreeObject) treeObject;

         String sText = sLastTitle.getText(true);

         if (sText.endsWith("!")) {
            sText = sText.substring(sText.length() - 1);
         }

         String s0 = sLastTitle.getValue(0);
         String sMandatoryException = "";

         boolean bMandatory = s0.startsWith("!");
         if (bMandatory) {
            s0 = s0.substring(1);
            if (s0.indexOf("(") >= 0) {
               sMandatoryException = s0.substring(s0.indexOf("(") + 1, s0.indexOf(")"));
               s0 = s0.substring(s0.indexOf(")") + 1).trim();
            }
         }

         title.setText(sText);
         comment.setText(sLastTitle.getComment());

         if (sText.endsWith("*")) {
            cb.setSelected(true);
            cb.setEnabled(false);
            labelMandatory.setEnabled(false);
            agentExceptionMandatory.setEnabled(false);
            agentExceptionMandatory.setText("");
         } else {
            cb.setSelected(bMandatory);
            cb.setEnabled(true);
            labelMandatory.setEnabled(true);
            agentExceptionMandatory.setEnabled(true);
            agentExceptionMandatory.setEnabled(bMandatory);
            agentExceptionMandatory.setText(sMandatoryException);
         }


         if (s0.indexOf("*") >= 0) {
            s0 = Util.replaceString(s0, "*", "");

            cbOwn.setSelected(true);
         } else {
            cbOwn.setSelected(false);
         }

         pPNRElement.setText(s0);
         c2.setText(sLastTitle.getValue(1));
         c3.setText(sLastTitle.getValue(2));
         c4.setText(sLastTitle.getValue(3));
      } else {
         sLastTitle = null;

         title.setText("");
         comment.setText("");

         cb.setSelected(false);
         cbOwn.setSelected(false);
         pPNRElement.setText("");
         c2.setText("");
         c3.setText("");
      }

      if (frame != null) {
         frame.selectJComboPnrInfo(pPNRElement.getText());
      }

      repaint();
   }

   public XmlObject getXml(String psAgentPath)
   {
      setTxtTitle(root);
      TreeObject.reset();

      XmlObject ret =
         new XmlObject((bRequestActive ? "<Request />" : "<Response />")).getFirstObject();
      Enumeration<?> enumChildren = root.children();

      while (enumChildren.hasMoreElements()) {
         Object treeObject = enumChildren.nextElement();

         if (treeObject instanceof TreeObject) {
            ret.addObject(((TreeObject) treeObject).getXml(0));
         }
      }

      String sAgentName = psAgentPath;

      if (sAgentName.indexOf("\\") > 0) {
         sAgentName = sAgentName.substring(sAgentName.lastIndexOf("\\") + 1);
      }
      ret.setAttribute("agentPath", sAgentName);

      return ret;
   }

   /** 
    * Method getHtml 
    * 
    * 
    * <p> TODO rename sAgentPath to psAgentPath
    * @param sAgentPath 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public String getHtml(String sAgentPath)
   {
      setTxtTitle(root);

      TreeObject.reset();

      String sRet = "<html><HEAD><style type='text/css'>\n<!--\n";

      sRet += "TABLE\n";
      sRet += "{\n";
      sRet += "    BORDER-BOTTOM: black thin solid;\n";
      sRet += "    BORDER-LEFT: black thin solid;\n";
      sRet += "    BORDER-RIGHT: black thin solid;\n";
      sRet += "    MARGIN-BOTTOM: 5pt;\n";
      sRet += "}\n";
      sRet += ".Header\n";
      sRet += "{\n";
      sRet += "    BACKGROUND-COLOR: linen;\n";
      sRet += "    BORDER-BOTTOM: black thin double;\n";
      sRet += "    BORDER-LEFT: black thin double;\n";
      sRet += "    BORDER-RIGHT: black thin double;\n";
      sRet += "    BORDER-TOP: black thin double;\n";
      sRet += "    CURSOR: text;\n";
      sRet += "    FONT-SIZE: 10pt;\n";
      sRet += "    FONT-WEIGHT: bolder;\n";
      sRet += "    MARGIN-BOTTOM: 5pt;\n";
      sRet += "    WIDTH: 100%\n";
      sRet += "}\n";
      sRet += "TD { font:10pt Arial;}\n";
      sRet += "Body { font:12pt Arial;}\n";

      sRet += "tr.LINE\n";
      sRet += "{ BACKGROUND-COLOR:#" + cols[0] + " }\n";

      for (int i = 0; i < cols.length - 1; i++) {
         sRet += "TABLE.Data" + i + "\n";
         sRet += "{ BACKGROUND-COLOR:#" + cols[i + 1] + ";WIDTH: 100% }\n";
      }

      sRet += "-->\n</style>\n<script language='JavaScript'>\n<!--\n";
      sRet += "function expandCollapse(branch, div)\n";
      sRet += "{\n";
      sRet += "        var objBranch = document.getElementById(branch).style;\n";
      sRet += "        var objText   = document.getElementById(div).innerHTML;\n";
      sRet += "\n";
      sRet += "        if (objBranch.display == 'block')\n";
      sRet += "        {\n";
      sRet += "           objBranch.display = 'none';\n";
      sRet += "           document.getElementById(div).innerHTML = objText.replace('-','+');\n";
      sRet += "        }\n";
      sRet += "        else\n";
      sRet += "        {\n";
      sRet += "           objBranch.display = 'block';\n";
      sRet += "           document.getElementById(div).innerHTML = objText.replace('+','-');\n";
      sRet += "        }\n";
      sRet += "}\n";
      sRet += "-->\n</script>\n";

      sRet += "</HEAD><body>";

      String sTable = getTableStart(0);
      String sText = "";

      Enumeration<?> enumChildren = root.children();

      while (enumChildren.hasMoreElements()) {
         Object treeObject = enumChildren.nextElement();

         if (treeObject instanceof TreeObject) {
            sText += "<b>" + ((TreeObject) treeObject).getText(true) + "</b> ";
            sTable += ((TreeObject) treeObject).getHtml(0);
         }
      }

      String sAgentName = sAgentPath;

      if (sAgentName.indexOf("\\") > 0) {
         sAgentName = sAgentName.substring(sAgentName.lastIndexOf("\\") + 1);
      }

      sRet +=
         "<a href='" + sAgentName + ".html'>back to Agent <i>" + sAgentName + "</i></a><br><br>\n";

      sRet +=
         "The following table contains the structure of " + "<i>Arctic"
               + (bRequestActive ? "Request" : "Response") + ".dtd</i> for " + sAgentPath
               + ".<br><br>\n";

      sRet += sTable + "</table>";
      sRet += TreeObject.getFootNotes();
      sRet += "<body></html>";

      return sRet;
   }

   /** 
    * Method getSourceInfo 
    * 
    * @return 
    * 
    * @author $author$ 
    */
   public String getSourceInfo()
   {
      String sText = "";

      Enumeration<?> enumChildren = root.children();

      while (enumChildren.hasMoreElements()) {
         Object treeObject = enumChildren.nextElement();

         if (treeObject instanceof TreeObject) {
            sText += ((TreeObject) treeObject).getSourceInfo();
         }
      }

      return sText;
   }

   /** 
    * Method isMarked 
    * 
    * <p> TODO rename o to another name
    * @param o 
    * @return 
    * 
    * @author $author$ 
    */
   private boolean isMarked(Object o)
   {
      if (o instanceof TreeObject) {
         return ((TreeObject) o).isMarked();
      }

      return false;
   }

   /** 
    * Method containsText 
    * 
    * <p> TODO rename o to another name
    * @param o 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private boolean containsText(Object o)
   {
      if (o instanceof TreeObject) {
         return ((TreeObject) o).containsText();
      }

      return false;
   }

   /** 
    * Method containsPnrElement 
    * 
    * <p> TODO rename o to another name
    * @param o 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private boolean containsPnrElement(Object o)
   {
      if (o instanceof TreeObject) {
         return ((TreeObject) o).containsPnrElement();
      }

      return false;
   }

   /** 
    * Method hasCustomizedTransformerFunc 
    * 
    * <p> TODO rename o to another name
    * @param o 
    * @return 
    * 
    * @author Winfried Wunder 
    */
   private boolean hasCustomizedTransformerFunc(Object o)
   {
      if (o instanceof TreeObject) {
         return ((TreeObject) o).hasCustomizedTransformerFunc();
      }

      return false;
   }

   /** 
    * Method isNotSupported 
    * 
    * <p> TODO rename o to another name
    * @param o 
    * @return 
    * 
    * @author Winfried Wunder 
    */
   private boolean isNotSupported(Object o)
   {
      if (o instanceof TreeObject) {
         return ((TreeObject) o).isNotSupported();
      }

      return false;

   }

   /** 
    * Class MyRenderer 
    * 
    * <p> 
    * Copyright &copy; 2002, i:FAO, AG. 
    * @author Andreas Brod 
    * @version $Revision$ $Date$ 
    */
   private class MyRenderer
      extends DefaultTreeCellRenderer
   {
      /**
       * 
       */
      private static final long serialVersionUID = -5636798689948663652L;
      ImageIcon tutorialIcon;
      ImageIcon folderIcon;
      ImageIcon folderOIcon;
      ImageIcon folderCIcon;
      ImageIcon folderXIcon;
      ImageIcon textIcon;
      ImageIcon emptyIcon;

      ImageIcon folderO_NotSupported;
      ImageIcon folderC_NotSupported;
      ImageIcon folderO_Text;
      ImageIcon folderC_Text;
      ImageIcon empty_NotSupported;

      ImageIcon textIconFunc;
      ImageIcon folderO_Text_Func;
      ImageIcon folderC_Text_Func;


      /**
       * Constructor MyRenderer
       */
      public MyRenderer()
      {
         tutorialIcon = Util.getImageIcon("dtdinfo/middle.jpg");
         folderIcon = Util.getImageIcon("dtdinfo/folderM.jpg");
         folderXIcon = Util.getImageIcon("dtdinfo/FolderX.jpg");
         textIcon = Util.getImageIcon("dtdinfo/Text.jpg");
         emptyIcon = Util.getImageIcon("dtdinfo/Empty.jpg");
         folderOIcon = Util.getImageIcon("dtdinfo/FolderO.jpg");
         folderCIcon = Util.getImageIcon("dtdinfo/FolderC.jpg");
         folderO_NotSupported = Util.getImageIcon("dtdinfo/FolderO_NotSupported2.jpg");
         folderC_NotSupported = Util.getImageIcon("dtdinfo/FolderC_NotSupported2.jpg");
         folderO_Text = Util.getImageIcon("dtdinfo/FolderO_Text.jpg");
         folderC_Text = Util.getImageIcon("dtdinfo/FolderC_Text.jpg");
         empty_NotSupported = Util.getImageIcon("dtdinfo/Empty_NotSupported.jpg");

         textIconFunc = Util.getImageIcon("dtdinfo/Text_Func.jpg");
         folderO_Text_Func = Util.getImageIcon("dtdinfo/FolderO_Text_Func.jpg");
         folderC_Text_Func = Util.getImageIcon("dtdinfo/FolderC_Text_Func.jpg");

      }

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
       */
      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                    boolean expanded, boolean leaf, int row,
                                                    boolean pbHasFocus)
      {

         super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, pbHasFocus);

         if (isMarked(value)) {
            if (leaf) {
               if (containsPnrElement(value)) {

                  if (hasCustomizedTransformerFunc(value)) {
                     setIcon(textIconFunc);
                  } else {
                     setIcon(textIcon);
                  }
               } else {
                  setIcon(tutorialIcon);
               }
            } else {
               if (!containsText(value)) {
                  setIcon(expanded ? folderXIcon : folderIcon);
               } else {
                  if (isNotSupported(value)) {
                     setIcon(expanded ? folderO_NotSupported : folderC_NotSupported);
                  } else {
                     if (containsPnrElement(value)) {
                        if (hasCustomizedTransformerFunc(value)) {
                           setIcon(expanded ? folderO_Text_Func : folderC_Text_Func);
                        } else {
                           setIcon(expanded ? folderO_Text : folderC_Text);
                        }

                     } else {
                        setIcon(expanded ? folderOIcon : folderCIcon);

                     }
                  }
               }
            }

            setToolTipText(null);
         } else {
            if (leaf) {
               if (containsPnrElement(value)) {
                  if (hasCustomizedTransformerFunc(value)) {
                     setIcon(textIconFunc);
                  } else {
                     setIcon(textIcon);
                  }
               } else {
                  if (isNotSupported(value)) {
                     setIcon(empty_NotSupported);

                  } else {
                     setIcon(emptyIcon);
                  }
               }
            } else {
               if (isNotSupported(value)) {
                  setIcon(expanded ? folderO_NotSupported : folderC_NotSupported);
               } else {
                  if (containsPnrElement(value)) {
                     if (hasCustomizedTransformerFunc(value)) {
                        setIcon(expanded ? folderO_Text_Func : folderC_Text_Func);

                     } else {
                        setIcon(expanded ? folderO_Text : folderC_Text);

                     }

                  } else {
                     setIcon(expanded ? folderOIcon : folderCIcon);

                  }
               }
            }

            setToolTipText(null);
         }

         return this;
      }
   }

}


/** 
 * Class TreeObject 
 * 
 * <p> 
 * Copyright &copy; 2002, i:FAO, AG. 
 * @author Andreas Brod 
 */
class TreeObject
   extends DefaultMutableTreeNode
{
   /**
    * 
    */
   private static final long serialVersionUID = -3178312468454114346L;
   String sText, sComment;
   boolean bRequest;
   int iMarked = -1;
   DtdData data;

   static int id = 1;
   static Hashtable<String, String> _footNotes = new Hashtable<String, String>();

   /** 
    * Method reset 
    * @author $author$ 
    */
   public static void reset()
   {
      id = 0;

      _footNotes.clear();
   }

   /** 
    * Method getFootNotes 
    * 
    * @return 
    * 
    * @author $author$ 
    */
   public static String getFootNotes()
   {
      Enumeration<String> keys = _footNotes.keys();
      String sRet = "";

      sRet += "<hr>\n";
      sRet +=
         "<font size='-2'>With * marked fields are mandatory. With (*) "
               + "marked fields are mandatory only for this Agent</font>\n";

      if (keys.hasMoreElements()) {
         sRet +=
            "<hr>\nAdditional Information<br><br>\n"
                  + "<table border='1' class='Header' width='100%'>"
                  + "<colgroup><col style='width:20%'><col style='width:80%'></colgroup>";

         while (keys.hasMoreElements()) {
            sRet += _footNotes.get(keys.nextElement()) + "\n";
         }

         sRet += "\n<table>";
      }

      return sRet;
   }

   /** 
    * Constructor TreeObject 
    * 
    * 
    * @param pData 
    * @param psText 
    * @param pbRequest 
    * @return TODO (brod) add text for returnValue
    * 
    */
   public TreeObject(DtdData pData, String psText, boolean pbRequest)
   {
      data = pData;

      if (psText.indexOf("-") < 0) {
         psText += "- ";
      }

      bRequest = pbRequest;

      sText = psText.substring(0, psText.indexOf("-")).trim();
      sComment = psText.substring(psText.indexOf("-") + 1).trim();

      setAllowsChildren(true);
   }

   String sPath = "";

   /** 
    * Method getIntPath 
    * 
    * 
    * <p> TODO rename bWithAttribute to pbWithAttribute
    * @param bWithAttribute 
    * @return 
    * 
    * @author $author$ 
    */
   private String getIntPath(boolean bWithAttribute)
   {
      if (sPath.startsWith("true ") || sPath.startsWith("false ")) {
         if (!bWithAttribute) {
            sPath = "";
         }
      } else {
         if (bWithAttribute) {
            sPath = "";
         }
      }

      if (sPath.length() > 0) {
         return sPath;
      }

      sPath = "";

      TreeNode[] path = getPath();

      for (int i = 0; i < path.length; i++) {
         if (path[i] instanceof TreeObject) {
            if (sPath.length() > 0) {
               sPath += ".";
            }

            sPath += ((TreeObject) path[i]).getText(i + 1 >= path.length);
         }
      }

      if (bWithAttribute) {
         sPath = bRequest + " " + sPath.trim();

         if (sPath.endsWith("*")) {
            sPath = sPath.substring(0, sPath.length() - 1);
         }
      }

      return sPath;
   }

   /** 
    * Method toString 
    * 
    * @return 
    * 
    * @author Andreas Brod 
    */
   @Override
   public String toString()
   {
      return sText;
   }

   /** 
    * Method getText 
    * 
    * 
    * <p> TODO rename exact to pbExact
    * @param exact 
    * @return 
    * 
    * @author $author$ 
    */
   public String getText(boolean exact)
   {
      if (!exact && sText.endsWith("*")) {
         return sText.substring(0, sText.length() - 1);
      }

      return sText;
   }

   /** 
    * Method getComment 
    * 
    * @return 
    * 
    * @author $author$ 
    */
   public String getComment()
   {
      return sComment;
   }

   /** 
    * Method setValue 
    * 
    * <p> TODO rename s1 to another names2 to another names3 to another names4 to another name
    * @param s1 
    * @param s2 
    * @param s3 
    * @param s4 
    * @return 
    * 
    * @author $author$ 
    */
   public String setValue(String s1, String s2, String s3, String s4)
   {
      iMarked = (s1.length() == 0) ? 1 : 0;

      String s =
         setValue(s1) + "</td>\n<td>" + setValue(s2) + "</td>\n<td>" + setValue(s3)
               + "</td><!-- <td>" + setValue(s4) + "</td> -->\n</tr>";

      data.put(getIntPath(true), s);

      return s;
   }

   /** 
    * Method setValue 
    * 
    * <p> TODO rename s to another name
    * @param s 
    * @return 
    * 
    * @author $author$ 
    */
   private String setValue(String s)
   {
      s = s.trim();

      if (s.length() == 0) {
         s = "&nbsp;";
      }

      s = s.replaceAll("\n", "<br>");

      int a = s.indexOf("{");
      while (a >= 0) {
         int b = s.indexOf("}", a);
         String sAdd = "";

         if (b > a) {
            sAdd = addFootNote(s.substring(a + 1, b));
         } else {
            sAdd = addFootNote(s.substring(a + 1)) + '}';
         }

         s = s.substring(0, a) + sAdd + s.substring(a + 1);
         a = s.indexOf("{", a + sAdd.length());
      }

      s = DtdMain.strTran("}", "-->", s);

      return s;
   }

   /** 
    * Method addFootNote 
    * 
    * <p> TODO rename s1 to another name
    * @param s1 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private String addFootNote(String s1)
   {
      String sAdd = "REF";
      int b = s1.indexOf(";");

      if (b > 0) {
         sAdd = s1.substring(0, b);
         s1 = s1.substring(b + 1);
      }

      id++;

      String sName = "fNote" + id;

      _footNotes.put(sName, "<tr align='left' valign='top'><td><a name='" + sName + "' href='#t"
            + sName + "'><b>" + sAdd + "<b></a></td><td halign='top'>" + s1 + "</td></tr>");

      s1 = "<a href='#" + sName + "' name='t" + sName + "'>" + sAdd + "</a><!--";

      return s1;
   }

   String[] sValues = new String[4];

   /** 
    * Method getValue 
    * 
    * <p> TODO rename iPos to piPos
    * @param iPos 
    * @return 
    * 
    * @author $author$ 
    */
   public String getValue(int iPos)
   {
      if (iPos == 0) {
         String sIntPath = getIntPath(true);
         String sDataIntPath = data.get(sIntPath);
         StringValues sVal = new StringValues(sDataIntPath);

         sValues[0] = sVal.getNext();
         sValues[1] = sVal.getNext();
         sValues[2] = sVal.getNext();
         if (sVal.hasNext()) {
            sValues[3] = sVal.getNext();
         } else {
            sValues[3] = sValues[2];
            sValues[2] = sValues[1];
            sValues[1] = sValues[0];
            sValues[0] = "";
         }

         for (int i = 0; i < sValues.length; i++) {
            String s = sValues[i];

            if (sValues[i].equals("&nbsp;")) {
               s = "";
            }

            s = DtdMain.strTran("&nbsp;", " ", s);
            s = DtdMain.strTran("<br>", "\n", s);

            if (i < 3) {
               int a = s.indexOf("<a href='#fNote");

               while (a >= 0) {
                  int b = s.indexOf("</a>", a);

                  if (b > a) {
                     s = s.substring(0, a) + s.substring(b + 4);
                     a = s.indexOf("<a href='#fNote");
                  } else {
                     a = -1;
                  }
               }

               s = DtdMain.strTran("<!--", "{", s);
               s = DtdMain.strTran("-->", "}", s);
            }

            sValues[i] = s.trim();
         }
         if (sText.startsWith("_choice")) {
            sValues[1] =
               "not supported - This attribute indicates, that each subElement "
                     + "can occur only once.";
         }
      }

      return sValues[iPos];
   }

   /** 
    * Method expand 
    * 
    * <p> TODO rename treeModel to pModel, jTree to pTree
    * @param treeModel 
    * @param jTree 
    * 
    * @author $author$ 
    */
   public void expand(DefaultTreeModel treeModel, JTree jTree)
   {
      TreeNode[] path = treeModel.getPathToRoot(this);

      if (!isMarked()) {
         jTree.expandPath(new TreePath(path));

         if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
               TreeNode tn = getChildAt(i);

               if (tn instanceof TreeObject) {
                  ((TreeObject) tn).expand(treeModel, jTree);
               }
            }
         }
      } else {
         jTree.collapsePath(new TreePath(path));
      }
   }

   public XmlObject getXml(int deep)
   {
      id++;

      XmlObject ret = new XmlObject("<Item />").getFirstObject();

      String sPath1 = getIntPath(false);

      // ret.setAttribute("path", sPath);

      String s0 = getValue(0).replaceAll(">", "&gt;").replaceAll("<", "&lt;");
      if (s0.length() > 0) {
         ret.createObject("Elements").setCData(s0);
      }
      String s1 = getValue(1).replaceAll(">", "&gt;").replaceAll("<", "&lt;");
      if (s1.length() > 0) {
         ret.createObject("TransformRules").setCData(s1);
      }
      String s2 = getValue(2).replaceAll(">", "&gt;").replaceAll("<", "&lt;");
      if (s2.length() > 0) {
         ret.createObject("ProviderRef").setCData(s2);
      }
      String s3 = getValue(3).replaceAll(">", "&gt;").replaceAll("<", "&lt;");
      if (s3.length() > 0) {
         ret.createObject("Change").setCData(s3);
      }
      String sName = "." + sPath1;
      sName = sName.substring(sName.lastIndexOf(".") + 1);

      if (sName.indexOf(" ") > 0) {
         sName = sName.substring(sName.lastIndexOf(" ") + 1);
      }

      if (s0.startsWith("!") && !sName.endsWith("*")) {
         ret.setAttribute("mandatory", "true");
      }
      ret.setAttribute("name", sName);

      if (getChildCount() > 0) {
         if (isMarked()) {
            ret.setAttribute("closed", "true");
         }
         for (int i = 0; i < getChildCount(); i++) {
            TreeNode tn = getChildAt(i);

            if (tn instanceof TreeObject) {
               ret.addObject(((TreeObject) tn).getXml(deep + 1));
            }
         }
      }

      return ret;

   }

   /** 
    * Method getHtml 
    * 
    * <p> TODO rename deep to piDeep
    * @param deep 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public String getHtml(int deep)
   {
      id++;

      String sId = "_" + id;
      String s = setValue(getValue(0), getValue(1), getValue(2), getValue(3));
      String sIntPath = getIntPath(false);
      String sName = "." + sIntPath;

      sName = sName.substring(sName.lastIndexOf(".") + 1);

      if (sName.indexOf(" ") > 0) {
         sName = sName.substring(sName.lastIndexOf(" ") + 1);
      }

      String sName0 = sName;

      if (s.startsWith("!") && !sName0.endsWith("*")) {
         sName0 += "(*)";
         s = DtdData.MANDATORY + s.substring(1);
      }

      sName += " (" + getValue(3) + ") - " + sComment;
      sName = DtdMain.strTran("\"", "^", sName);
      sName = DtdMain.strTran("'", "^", sName);
      sName =
         "<a onMouseOver=\"self.status='" + sName + "'\" onMouseOut=\"self.status=''\">" + sName0
               + "</a>";

      if ((getChildCount() > 0)) {
         if (!isMarked()) {
            sName =
               "\n<div onclick=\"expandCollapse('Req" + sId + "', 'div" + sId + "')\" id=div" + sId
                     + " style='cursor:pointer;'><b>- " + sName + "</b></div>\n";
         } else {
            sName = "<b>&nbsp;&nbsp;" + sName + "</b>";
         }
      }

      String sCol = (getChildCount() > 0) ? "" : " class='LINE'";
      String sPre =
         (getChildCount() == 0) ? "" : "<tr><td colspan='4'><font size='-2'>" + sIntPath
               + "</font></td></tr>";

      sPre += "<tr align='left' valign='top'" + sCol + ">" + "<td>" + sName;
      sPre += "</td><td>\n";

      s = sPre + "<!-- START " + sIntPath + " -->\n" + s + "\n<!-- END " + sIntPath + " -->\n";

      if (!isMarked() && (getChildCount() > 0)) {
         s += "<tr><td colspan='4'>";
         s += "<span id=Req" + sId + " style='display:block' width='100%'>\n";

         if (deep % 2 == 0) {
            s += DtdTree.getTableStart(-1) + "\n";
            s += "<tr><td>XML-Tag</td><td>Pnr Elements</td><td>Transform Rules</td>";
            s += "<td>Provider Ref</td><!-- <td>Date (User)</td> --></tr></table>";
         }

         s += DtdTree.getTableStart(deep + 1) + "\n";

         for (int i = 0; i < getChildCount(); i++) {
            TreeNode tn = getChildAt(i);

            if (tn instanceof TreeObject) {
               s += ((TreeObject) tn).getHtml(deep + 1);
            }
         }

         s += "</table></span></td></tr>\n";

      }

      return s;
   }

   /** 
    * Method getSourceInfo 
    * 
    * @return 
    * 
    * @author $author$ 
    */
   public String getSourceInfo()
   {
      id++;

      String sPnrElements = "";
      String sIntPath = getIntPath(true);
      String sDataIntPath = data.get(sIntPath);

      StringTokenizer st = new StringTokenizer(getValue(0), "\n");

      while (st.hasMoreTokens()) {
         String sToken = st.nextToken();

         if (sToken.length() > 0) {
            if (sPnrElements.length() > 0) {
               sPnrElements += ": ";
            }

            sPnrElements += DtdData.getFormatedPnrElement(sToken);
         }
      }

      String s = getIntPath(false);

      if (s.endsWith("*")) {
         s = s.substring(0, s.length() - 1);
      }
      if (sDataIntPath.startsWith("!")) {
         s += "!";
      }

      s += ": " + sPnrElements + "\n";

      if (!isMarked() && (getChildCount() > 0)) {

         for (int i = 0; i < getChildCount(); i++) {
            TreeNode tn = getChildAt(i);

            if (tn instanceof TreeObject) {
               s += ((TreeObject) tn).getSourceInfo();
            }
         }

      }

      return s;
   }

   /** 
    * Method isMarked 
    * 
    * @return 
    * 
    * @author $author$ 
    */
   public boolean isMarked()
   {

      // if (iMarked<0){

      iMarked = (containsText()) ? 0 : 1;

      // }
      if (getChildCount() > 0) {
         for (int i = 0; i < getChildCount(); i++) {
            TreeNode tn = getChildAt(i);

            if (tn instanceof TreeObject) {
               if (!((TreeObject) tn).isMarked() || ((TreeObject) tn).containsText()) {
                  return false;
               }
            }

         }

         return true;
      }

      return (iMarked == 1);
   }

   /** 
    * Method containsText 
    * 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public boolean containsText()
   {
      getValue(0);

      return (getValue(0).length() > 0) || (getValue(1).length() > 0);
   }

   /** 
    * Method hasCustomizedTransformerFunc 
    * 
    * @return 
    * 
    * @author Winfried Wunder 
    */
   public boolean hasCustomizedTransformerFunc()
   {
      String sElement = getValue(0);

      if (sElement.length() > 0) {
         return sElement.endsWith("*");
      }
      return false;
   }

   /** 
    * Method isNotSupported 
    * 
    * @return 
    * 
    * @author Winfried Wunder 
    */
   public boolean isNotSupported()
   {
      getValue(0);

      String sTransformRule = getValue(1);

      return (sTransformRule != null) && sTransformRule.toLowerCase().startsWith("not supported");
   }

   /** 
    * Method containsText 
    * 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public boolean containsPnrElement()
   {

      return getValue(0).length() > 0;
   }

}
