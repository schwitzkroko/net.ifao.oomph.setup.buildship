package net.ifao.plugins.editor.dtdinfo;


import ifaoplugin.Util;

import java.io.*;
import java.util.*;

import net.ifao.xml.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.*;


/** 
 * The class DtdInfo was automatically genereated with Xml2Swt.
 * It uses the related DtdInfoAdapter, which will be recreated
 * whith Xml2Swt. Change should only be done within DtdInfo.java
 * <p> 
 * Copyright &copy; 2006, i:FAO
 * 
 * @author generator
 */
public class DtdInfo
   extends DtdInfoAdapter
{
   private Color color_orange = new Color(null, 255, 200, 0);
   private Color color_blue = new Color(null, 0, 200, 255);
   private Color color_yellow = new Color(null, 255, 255, 230);
   private IFile iFile;
   private File file;
   private String sPath;
   private String sRoot = "";
   private String sAgent = "";
   private String sPackage = "";
   private String sProvider = "";
   private Dtd arcticRequest = null;
   private Dtd arcticResponse = null;
   private Xml requestXml;
   private Xml responseXml;
   private Xml xmlSelect;
   private TreeItem itemSelect;
   private Hashtable<Xml, String> htBusiness = new Hashtable<Xml, String>();

   /**
    * Constructor for DtdInfo, which can be enhanced.
    * 
    * @param pAbstractUIPlugin InterfaceClass for AbstractUIPlugin
    **/
   public DtdInfo(Class pAbstractUIPlugin, IFile pIFile)
   {
      this(pAbstractUIPlugin, pIFile.getLocation().toFile());
      iFile = pIFile;
   }

   public DtdInfo(Class pAbstractUIPlugin, File pFile)
   {
      super(pAbstractUIPlugin);
      file = pFile;


      try {
         sPath = file.getCanonicalPath();
      }
      catch (IOException e) {
         sPath = file.getAbsolutePath();
      }

      String sAgents = "\\src\\net\\ifao\\arctic\\agents\\";
      int iStart = sPath.indexOf(sAgents);
      // get the RootDirectory
      if (iStart > 0) {
         sRoot = sPath.substring(0, iStart);
         sPackage =
            sPath.substring(iStart + 5, sPath.lastIndexOf("\\") + 1).replaceAll("\\\\", ".");
         sAgent = sPath.substring(sPath.lastIndexOf("\\") + 1, sPath.lastIndexOf("."));

         // analyse the Agents.xml for Provider
         try {
            Xml xml = new Xml(Util.getConfFile(sRoot, "Agents.xml"));
            Xml group =
               xml.getFirstObject().createObject("Agents")
                     .findSubObject("AgentGroup", "requestType", sAgent);
            if (group != null) {
               Xml[] objects = group.getObjects("");
               // 1. try to find exact
               for (Xml object : objects) {
                  if (object.getAttribute("className").equals(sPackage + sAgent)) {
                     sProvider = object.getAttribute("providerType");
                  }
               }

               if (sProvider.length() == 0) {
                  Xml[] agentGroup =
                     xml.getFirstObject().createObject("Agents").getObjects("AgentGroup");
                  for (int i = 0; (sProvider.length() == 0) && (i < agentGroup.length); i++) {
                     Xml[] agent = agentGroup[i].getObjects("Agent");
                     for (int j = 0; (sProvider.length() == 0) && (j < agent.length); j++) {
                        if (objects[i].getAttribute("className").startsWith(sPackage)) {
                           sProvider = objects[i].getAttribute("providerType");
                        }
                     }
                  }
               }
            }
         }
         catch (FileNotFoundException e) {}

         // get the ArcticRequest.dtd ArcticResponse.dtd 
         arcticRequest = new Dtd(Util.getConfFile(sRoot, "ArcticRequest.dtd"));
         requestXml = getFormatedXml(arcticRequest.getXmlObject(sAgent).getFirstObject());
         arcticResponse = new Dtd(Util.getConfFile(sRoot, "ArcticResponse.dtd"));

         responseXml =
            getFormatedXml(getResponse(sAgent, arcticRequest, arcticResponse).getFirstObject());
      }

      // Call the initAdapter-method, which creates the components
      initAdapter();

      // set the LabelColors
      getCLabelDetailName().setBackground(color_blue);
      getCLabelDetailDescription().setBackground(color_orange);

      setGroupTypePage(0);

      // set the titleName
      getCLabelTitle().setText(sAgent + " - " + Util.getCamelCase(sProvider.toLowerCase()));

      // init the trees
      addToTree(getTreeReq(), requestXml);
      addToTree(getTreeRes(), responseXml);

   }

   public Xml getFormatedXml(Xml xmlIn)
   {
      String sEmpty = "comment=\"???\"";
      Xml xmlNew =
         (new Xml("<dir name=\"" + xmlIn.getName() + "\" " + sEmpty + "/>")).getFirstObject();
      String[] attributeNames = xmlIn.getAttributeNames();
      for (String attributeName : attributeNames) {
         Xml xmlAttrib =
            (new Xml("<attrib name=\"" + attributeName + "\"" + " value=\""
                  + xmlIn.getAttribute(attributeName) + "\" " + sEmpty + "/>")).getFirstObject();
         xmlNew.addObject(xmlAttrib);
      }
      // add the subobjects
      Xml[] subs = xmlIn.getObjects("");
      for (Xml sub : subs) {
         Xml xmlSub = getFormatedXml(sub);
         xmlNew.addObject(xmlSub);
      }
      return xmlNew;
   }

   public Xml getResponse(String sName, Dtd request, Dtd response)
   {
      StringTokenizer st = new StringTokenizer(request.getRemark("Request"), "\n");
      String sNewName = sName;

      while (st.hasMoreTokens()) {
         String s1 = st.nextToken().trim();

         if (s1.startsWith(sName + " ")) {
            if (s1.indexOf(" response will be ") > 0) {
               sNewName = s1.substring(s1.lastIndexOf(" ") + 1);
            }
         }
      }

      return response.getXmlObject(sNewName);
   }

   private void addToTree(Tree pTree, Xml xml)
   {
      TreeItem item = new TreeItem(pTree, SWT.NONE);
      addToTree(item, xml);
   }

   private void addToTree(TreeItem pParent, Xml xml)
   {
      pParent.setText(xml.getAttribute("name"));

      // add the attributes
      Xml[] subs = xml.getObjects("attrib");
      for (Xml sub : subs) {
         TreeItem subItem = new TreeItem(pParent, SWT.NONE);
         String sValue = sub.getAttribute("value");
         if (sValue.startsWith("!")) {
            subItem.setBackground(color_yellow);
         }
         updateImage(subItem, sub);
         subItem.setText(sub.getAttribute("name"));
         subItem.setData(sub);
      }

      // add the subobjects
      subs = xml.getObjects("dir");
      for (Xml sub : subs) {
         TreeItem subItem = new TreeItem(pParent, SWT.NONE);
         addToTree(subItem, sub);
         subItem.setData(sub);
      }
      pParent.setExpanded(true);
      updateImage(pParent, xml);
   }

   private void updateImage(TreeItem item, Xml data)
   {
      updateImage(item, data, ' ');
   }

   private void updateImage(TreeItem item, Xml data, char cOpen)
   {
      if (data == null) {
         return;
      }
      String sCData = data.createObject("business").getCData();
      String sTransform = data.createObject("transform").getCData();

      if (data.getName().equalsIgnoreCase("dir")) {
         if (cOpen == ' ') {
            cOpen = item.getExpanded() ? 'O' : 'C';
         }
         item.setImage(getIcon("dtd_Folder" + cOpen + ".gif"));
      } else {
         if (sCData.length() > 0) {
            item.setImage(getIcon("dtd_Text.gif"));
         } else if (sTransform.equalsIgnoreCase("not supported")) {
            item.setImage(getIcon("dtd_Empty_NotSupported.gif"));
         } else if (sTransform.length() > 0) {
            item.setImage(getIcon("dtd_Empty.gif"));
         } else {
            item.setImage(getIcon("dtd_Middle.gif"));
         }
      }

   }

   private void updateButtons(Xml data)
   {
      String sTransform = data.createObject("transform").getCData();
      getButtonNotSupported1().setEnabled(sTransform.length() == 0);
      getButtonNotSupported2().setEnabled(sTransform.length() == 0);
   }

   @Override
   public void close()
   {
      color_orange.dispose();
      color_blue.dispose();
      color_yellow.dispose();
      super.close();
   }

   @Override
   protected void clickComboPnrName(Combo pComboPnrName)
   {
      // add click functionallity
   }

   @Override
   protected void clickComboPnrAttr(Combo pComboPnrAttr)
   {
      // add click functionallity
   }

   @Override
   protected void clickButtonUsePnrElement(Button pButtonUsePnrElement)
   {
      // add click functionallity
   }

   @Override
   protected void clickButtonSelfDefined(Button pButtonSelfDefined)
   {
      // add click functionallity
   }

   @Override
   protected void modifyTextDetailPnr(Text pTextDetailPnr)
   {
      // add modify functionallity
      if (xmlSelect == null) {
         return;
      }

      String sText = pTextDetailPnr.getText();
      xmlSelect.createObject("business").setCData(sText);

      if (sText.length() > 0) {
         htBusiness.put(xmlSelect, sText);
      } else {
         htBusiness.remove(xmlSelect);
      }
      getButtonHidePnrElements().setEnabled(htBusiness.size() == 0);
      updateImage(itemSelect, xmlSelect);
      updateButtons(xmlSelect);
   }

   @Override
   protected void clickButtonDisplayPnrElements(Button pButtonDisplayPnrElements)
   {
      setValues(itemSelect);
      setGroupTypePage(2);
   }

   @Override
   protected void clickButtonAdditionalDocument1(Button pButtonAdditionalDocument1)
   {
      // TODO Auto-generated method stub

   }

   @Override
   protected void clickButtonAdditionalDocument2(Button pButtonAdditionalDocument2)
   {
      // TODO Auto-generated method stub

   }

   @Override
   protected void clickButtonDetailPnr2(Button pButtonDetailPnr2)
   {
      // TODO Auto-generated method stub

   }

   @Override
   protected void clickButtonHidePnrElements(Button pButtonHidePnrElements)
   {
      setValues(itemSelect);
      setGroupTypePage(1);
   }

   @Override
   protected void clickButtonNotSupported1(Button pButtonNotSupported1)
   {
      getTextTransformRules1().setText("not supported");
      getTextTransformRules2().setText("not supported");
      modifyTextTransformRules1(getTextTransformRules1());
   }

   @Override
   protected void clickButtonNotSupported2(Button pButtonNotSupported2)
   {
      clickButtonNotSupported1(pButtonNotSupported2);

   }

   @Override
   protected void modifyTextDateUser1(Text pTextDateUser1)
   {
      // TODO Auto-generated method stub

   }

   @Override
   protected void modifyTextDateUser2(Text pTextDateUser2)
   {
      // TODO Auto-generated method stub

   }

   @Override
   protected void modifyTextProviderRef1(Text pTextProviderRef1)
   {
      if (xmlSelect == null) {
         return;
      }

      xmlSelect.createObject("provider").setCData(pTextProviderRef1.getText());
      updateImage(itemSelect, xmlSelect);
      updateButtons(xmlSelect);
   }

   @Override
   protected void modifyTextProviderRef2(Text pTextProviderRef2)
   {
      modifyTextProviderRef1(pTextProviderRef2);

   }

   @Override
   protected void modifyTextTransformRules1(Text pTextTransformRules1)
   {
      if (xmlSelect == null) {
         return;
      }

      xmlSelect.createObject("transform").setCData(pTextTransformRules1.getText());
      updateImage(itemSelect, xmlSelect);
      updateButtons(xmlSelect);

   }

   @Override
   protected void modifyTextTransformRules2(Text pTextTransformRules2)
   {
      modifyTextTransformRules1(pTextTransformRules2);

   }

   @Override
   protected void clickTreeReq(Tree pTreeReq)
   {
      TreeItem[] selection = pTreeReq.getSelection();
      if (selection.length > 0) {
         try {
            setValues(selection[0]);

         }
         catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   protected void treeCollapsedTreeReq(org.eclipse.swt.events.TreeEvent e)
   {
      try {
         TreeItem item = (TreeItem) e.item;
         itemSelect = item;
         xmlSelect = (Xml) item.getData();
         if (xmlSelect == null) {
            return;
         }
         updateImage(itemSelect, xmlSelect, 'C');
      }
      catch (Exception e1) {}
   }

   @Override
   protected void treeExpandedTreeReq(org.eclipse.swt.events.TreeEvent e)
   {
      try {
         TreeItem item = (TreeItem) e.item;
         itemSelect = item;
         xmlSelect = (Xml) item.getData();
         if (xmlSelect == null) {
            return;
         }
         updateImage(itemSelect, xmlSelect, 'O');
      }
      catch (Exception e1) {}
   }

   private void setValues(TreeItem item)
   {
      itemSelect = item;
      xmlSelect = (Xml) item.getData();
      if (xmlSelect == null) {
         return;
      }
      Xml b = xmlSelect.createObject("business");
      Xml t = xmlSelect.createObject("transform");
      Xml p = xmlSelect.createObject("provider");

      String sBusiness = b.getCData();

      getTextDetailPnr().setText(sBusiness);
      getTextTransformRules1().setText(t.getCData());
      getTextProviderRef1().setText(p.getCData());
      getTextTransformRules2().setText(t.getCData());
      getTextProviderRef2().setText(p.getCData());

      if (htBusiness.size() > 0) {
         setGroupTypePage(2);
         getButtonHidePnrElements().setEnabled(false);
      } else {
         setGroupTypePage(1);
         getButtonHidePnrElements().setEnabled(true);
      }
      updateImage(itemSelect, xmlSelect);
      updateButtons(xmlSelect);

   }

   @Override
   protected void clickTreeRes(Tree pTreeRes)
   {
      TreeItem[] selection = pTreeRes.getSelection();
      if (selection.length > 0) {
         try {
            setValues(selection[0]);

         }
         catch (Exception e) {}
      }

   }

   @Override
   protected void clickTabFolderReqRes(TabFolder pTabFolderReqRes)
   {

   }

}
