package net.ifao.tools.dtdinfo.swt;


import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import net.ifao.tools.dtdinfo.*;
import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;


/** 
 * TODO (brod) add comment for class DtdInfo 
 * 
 * <p> 
 * Copyright &copy; 2008, i:FAO 
 * 
 * @author brod 
 */
public class DtdInfo
   extends DtdInfoFrame
{

   public DtdInfo()
   {
      super();
      createSShell();
      loadSettings();
   }

   /** 
    * Method main 
    * 
    * <p> TODO rename args to pArgs
    * @param args 
    * 
    * @author brod 
    */
   public static void main(String[] args)
   {
      /* Before this is run, be sure to set up the launch configuration (Arguments->VM Arguments)
       * for the correct SWT library path in order to run with the SWT dlls. 
       * The dlls are located in the SWT plugin jar.  
       * For example, on Windows the Eclipse SWT 3.1 plugin jar is:
       *       installation_directory\plugins\org.eclipse.swt.win32_3.1.0.jar
       */
      new DtdInfo().show(Display.getDefault());

   }

   private void show(Display display)
   {
      sShell.open();
      sShell.setActive();
      while (!sShell.isDisposed()) {
         if (!display.readAndDispatch())
            display.sleep();
      }
      display.dispose();
   }

   private boolean bChange;
   private Data data = null; //  @jve:decl-index=0:
   private TreeItem treeItemSelected;

   private XmlObject xmlSelected;

   /** 
    * TODO (brod) add comment for method addSashForm 
    * 
    * <p> TODO rename xml to pXml, string to psString
    * @param xml TODO (brod) add text for param xml 
    * @param string TODO (brod) add text for param string 
    * @param pSashForm TODO (brod) add text for param pSashForm 
    * 
    * @author brod 
    */
   private void addSashForm(XmlObject xml, String string, SashForm pSashForm)
   {
      int[] weights = pSashForm.getWeights();
      String s = "";
      for (int i = 0; i < weights.length; i++) {
         if (i > 0)
            s += ",";
         s += weights[i];
      }
      xml.createObject("Sash", "id", string, true).setAttribute("weights", s);
   }

   /** 
    * TODO (brod) add comment for method addTree 
    * 
    * <p> TODO rename treeItem to pItem, xml to pXml
    * @param treeItem TODO (brod) add text for param treeItem 
    * @param xml TODO (brod) add text for param xml 
    * 
    * @author brod 
    */
   private void addTree(TreeItem treeItem, XmlObject xml)
   {
      TreeItem item = new TreeItem(treeItem, SWT.NULL);
      item.setText(xml.getAttribute("name"));
      item.setData(xml);
      XmlObject[] objects = xml.getObjects("Item");
      for (int i = 0; i < objects.length; i++) {
         addTree(item, objects[i]);
      }
      boolean bExpanded = true;
      if (!Data.hasFilledSubObjects(xml))
         bExpanded = false;
      else
         bExpanded = !Data.isNotSupported(xml);
      item.setExpanded(bExpanded);
      item.setImage(getImage(bExpanded, xml, treeItem.getDisplay()));
   }

   /** 
    * TODO (brod) add comment for method changeImage 
    * 
    * @param pTreeItem TODO (brod) add text for param pTreeItem
    * 
    * @author brod 
    */
   private void changeImage(TreeItem pTreeItem)
   {
      try {
         XmlObject pXmlSelected = (XmlObject) pTreeItem.getData();
         Image imageOld = pTreeItem.getImage();
         Image image = getImage(pTreeItem.getExpanded(), pXmlSelected, pTreeItem.getDisplay());
         if (imageOld != image) {
            pTreeItem.setImage(image);
            changeImage(pTreeItem.getParentItem());
         }
      }
      catch (Exception ex) {

      }

   }

   /** 
    * TODO (brod) add comment for method checkChange 
    * 
    * <p> TODO rename xml to pXml, attribute to psAttribute, checkBoxSelfDefined to pBoxSelfDefined
    * @param xml TODO (brod) add text for param xml 
    * @param attribute TODO (brod) add text for param attribute 
    * @param pCheckBoxSelfDefined1 TODO (brod) add text for param checkBoxSelfDefined 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   private boolean checkChange(XmlObject xml, String attribute, Button pCheckBoxSelfDefined1)
   {
      boolean bOk = xml.getAttribute(attribute).equalsIgnoreCase("true");
      if (pCheckBoxSelfDefined1.getSelection() != bOk) {
         xml.setAttribute(attribute, pCheckBoxSelfDefined1.getSelection() ? "true" : null);
         return true;
      }
      return false;
   }

   /** 
    * TODO (brod) add comment for method exit 
    * 
    * @author brod 
    */
   @Override
   void exit()
   {
      saveData();
      System.exit(0);

   }

   private void saveData()
   {
      // save Properties
      XmlObject xml = null;
      try {
         xml = new XmlObject(new FileInputStream("DtdData.xml")).getFirstObject();
      }
      catch (Exception e) {
         xml = null;
      }

      if (xml == null)
         xml = new XmlObject("<Settings />").getFirstObject();
      Point size = sShell.getSize();
      xml.setAttribute("width", "" + size.x);
      xml.setAttribute("height", "" + size.y);
      Point location = sShell.getLocation();
      xml.setAttribute("x", "" + location.x);
      xml.setAttribute("y", "" + location.y);
      xml.setAttribute("agent", textAgent.getText());
      xml.setAttribute("provider", textProvider.getText());
      addSashForm(xml, "1", sashForm);
      addSashForm(xml, "2", sashForm2);

      DtdUtil.saveFile("DtdData.xml", xml.toString());
   }

   /** 
    * TODO (brod) add comment for method getImage 
    * 
    * <p> TODO rename bOpened to pbOpened, xml to pXml, device to pDevice
    * @param bOpened TODO (brod) add text for param bOpened 
    * @param xml TODO (brod) add text for param xml 
    * @param device TODO (brod) add text for param device 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   private Image getImage(boolean bOpened, XmlObject xml, Display device)
   {
      Image image;
      if (xml != null && xml.getAttribute("type").length() == 0)
         image = DtdImages.getImageFolder(bOpened, xml, device);
      else
         image = DtdImages.getTextFolder(xml, device);

      return image;
   }

   /** 
    * TODO (brod) add comment for method loadData 
    * 
    * @author brod 
    * @param dtdStatus 
    */
   private void loadData(DtdStatusFrame dtdStatus)
   {
      try {
         dtdStatus.setText("load DtdData.xml");

         XmlObject xml = new XmlObject(new FileInputStream("DtdData.xml")).getFirstObject();
         String sAgent = xml.getAttribute("agent");
         String sProvider = xml.getAttribute("provider");
         data.load(dtdStatus, sAgent, sProvider);

         setComboBoxes();
         setTree(treeRequest, data.request);
         setTree(treeResponse, data.response);

         DtdUtil.saveFile("C:\\temp\\file.txt", data.request.toString());
         DtdUtil.saveFile("C:\\temp\\file2.txt", data.response.toString());
      }
      catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   private void setComboBoxes()
   {
      String[] elements = data.getProviderPnrElements();
      comboPnrElement.removeAll();
      for (int i = 0; i < elements.length; i++) {
         comboPnrElement.add(elements[i]);

      }
      if (elements.length > 0) {
         comboPnrElement.select(0);
         updateCombo();
      }
   }


   @Override
   void updateCombo()
   {
      String sText = comboPnrElement.getText();
      String[] elements = data.getProviderPnrAttributes(sText);
      comboPnrAttribute.removeAll();
      for (int i = 0; i < elements.length; i++) {
         comboPnrAttribute.add(elements[i]);
      }
      if (elements.length > 0)
         comboPnrAttribute.select(0);
   }

   /** 
    * TODO (brod) add comment for method loadSash 
    * 
    * <p> TODO rename xml to pXml, string to psString
    * @param xml TODO (brod) add text for param xml 
    * @param string TODO (brod) add text for param string 
    * @param pSashForm TODO (brod) add text for param pSashForm 
    * 
    * @author brod 
    */
   private void loadSash(XmlObject xml, String string, SashForm pSashForm)
   {
      try {
         StringTokenizer st =
            new StringTokenizer(xml.findSubObject("Sash", "id", string).getAttribute("weights"),
                  ",");
         int[] weights = new int[st.countTokens()];
         for (int j = 0; j < weights.length; j++) {
            weights[j] = Integer.parseInt(st.nextToken());
         }
         pSashForm.setWeights(weights);
      }
      catch (Exception ex) {

      }
   }

   /** 
    * TODO (brod) add comment for method loadSettings 
    * 
    * @author brod 
    */
   private void loadSettings()
   {
      try {

         // init the new Elements
         DtdStatusFrame dtdStatus = new DtdStatusFrame();
         dtdStatus.setText("load Settings");
         XmlObject xml = new XmlObject(new FileInputStream("DtdData.xml")).getFirstObject();
         Point size =
            new Point(Integer.parseInt(xml.getAttribute("width")), Integer.parseInt(xml
                  .getAttribute("height")));
         Point location =
            new Point(Integer.parseInt(xml.getAttribute("x")), Integer.parseInt(xml
                  .getAttribute("y")));
         sShell.setLocation(location);
         sShell.setSize(size);

         loadSash(xml, "1", sashForm);
         loadSash(xml, "2", sashForm2);

         if (data == null) {
            // add Tree Adapter for openClose
            org.eclipse.swt.events.TreeAdapter treeAdapter =
               new org.eclipse.swt.events.TreeAdapter()
               {
                  @Override
                  public void treeCollapsed(org.eclipse.swt.events.TreeEvent e)
                  {
                     try {
                        TreeItem source = (TreeItem) e.item;
                        Image imageFolder =
                           getImage(false, (XmlObject) source.getData(), source.getDisplay());
                        source.setImage(imageFolder);
                     }
                     catch (Exception ex) {
                        ex.printStackTrace();
                     }
                     super.treeCollapsed(e);
                  }

                  @Override
                  public void treeExpanded(org.eclipse.swt.events.TreeEvent e)
                  {
                     try {

                        TreeItem source = (TreeItem) e.item;
                        source.setImage(getImage(true, (XmlObject) source.getData(), source
                              .getDisplay()));
                     }
                     catch (Exception ex) {
                        ex.printStackTrace();
                     }
                     super.treeExpanded(e);
                  }
               };

            // TreeRequest         
            treeRequest.addTreeListener(treeAdapter);
            treeRequest.addKeyListener(new org.eclipse.swt.events.KeyAdapter()
            {
               @Override
               public void keyPressed(org.eclipse.swt.events.KeyEvent e)
               {
                  selected(treeRequest, e);
                  super.keyPressed(e);
               }
            });
            treeRequest.addMouseListener(new org.eclipse.swt.events.MouseAdapter()
            {
               @Override
               public void mouseDown(org.eclipse.swt.events.MouseEvent e)
               {
                  selected(treeRequest, null);
                  super.mouseDown(e);
               }
            });

            treeResponse.addTreeListener(treeAdapter);
            treeResponse.addMouseListener(new org.eclipse.swt.events.MouseAdapter()
            {
               @Override
               public void mouseDown(org.eclipse.swt.events.MouseEvent e)
               {
                  selected(treeResponse, null);
                  super.mouseDown(e);
               }
            });
            treeResponse.addKeyListener(new org.eclipse.swt.events.KeyAdapter()
            {
               @Override
               public void keyPressed(org.eclipse.swt.events.KeyEvent e)
               {
                  selected(treeResponse, e);
                  super.keyPressed(e);
               }
            });

         }
         data = new Data(dtdStatus, "c:\\iFAO\\Workspace3.6\\arctic1");

         String sAgent = xml.getAttribute("agent");
         String sProvider = xml.getAttribute("provider");

         textAgent.setText(sAgent);
         textProvider.setText(sProvider);

         loadData(dtdStatus);

         dtdStatus.close();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   /** 
    * TODO (brod) add comment for method noHtml 
    * 
    * <p> TODO rename data2 to ps2
    * @param data2 TODO (brod) add text for param data2
    * @return TODO (brod) add text for returnValue
    * 
    * @author brod 
    */
   private String noHtml(String data2)
   {
      // TODO Auto-generated method stub
      return data2.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
   }


   /** 
    * TODO (brod) add comment for method selected 
    * 
    * <p> TODO rename tree to pTree
    * @param tree TODO (brod) add text for param tree 
    * 
    * @author brod 
    */
   private void selected(Tree tree, KeyEvent e)
   {
      try {
         treeItemSelected = tree.getSelection()[0];
         xmlSelected = (XmlObject) treeItemSelected.getData();
         if (xmlSelected != null) {
            labelItem.setText(xmlSelected.getAttribute("name"));
            labelItemComment.setText(xmlSelected.getAttribute("comment"));

            bChange = false;
            textAreaPnr.setText(noHtml(xmlSelected.createObject("Pnr").getCData()));
            textAreaProviderRef.setText(noHtml(xmlSelected.createObject("ProviderRef").getCData()));
            textAreaTransformRules.setText(noHtml(xmlSelected.createObject("TransformRules")
                  .getCData()));
            textAreaOldCode.setText(noHtml(xmlSelected.createObject("SourceCode").getCData()));
            checkBoxSelfDefined.setSelection(xmlSelected.getAttribute("selfDefined")
                  .equalsIgnoreCase("true"));

            textDateUser.setText(xmlSelected.getAttribute("changed"));
            setCombos();
            bChange = true;
            if (e != null) {
               textAreaTransformRules.setFocus();
            }
         }
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /** 
    * TODO (brod) add comment for method setTree 
    * 
    * <p> TODO rename tree to pTree, xml to pXml
    * @param tree TODO (brod) add text for param tree 
    * @param xml TODO (brod) add text for param xml 
    * 
    * @author brod 
    */
   private void setTree(Tree tree, XmlObject xml)
   {
      tree.clearAll(true);
      TreeItem treeItem = new TreeItem(tree, SWT.NULL);
      treeItem.setText("Arctic");
      addTree(treeItem, xml);
      treeItem.setImage(getImage(treeItem.getExpanded(), xml, tree.getDisplay()));
      treeItem.setExpanded(true);
   }

   /** 
    * TODO (brod) add comment for method textChange 
    * 
    * <p> TODO rename createObject to pObject, text to pText
    * @param createObject TODO (brod) add text for param createObject 
    * @param text TODO (brod) add text for param text 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   private boolean textChange(XmlObject createObject, Text text)
   {
      String scData = createObject.getCData();
      String sText = text.getText().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
      if (!sText.equals(scData)) {
         createObject.setCData(sText);
         return true;
      }
      return false;
   }

   /** 
    * TODO (brod) add comment for method textChanged 
    * 
    * @author brod 
    */
   @Override
   void textChanged()
   {
      if (bChange)
         try {
            boolean bChanged = false;
            if (textChange(xmlSelected.createObject("Pnr"), textAreaPnr)) {
               bChanged = true;
            }
            if (textChange(xmlSelected.createObject("ProviderRef"), textAreaProviderRef)) {
               bChanged = true;
            }
            if (textChange(xmlSelected.createObject("TransformRules"), textAreaTransformRules)) {
               bChanged = true;
            }
            if (checkChange(xmlSelected, "selfDefined", checkBoxSelfDefined)) {
               bChanged = true;
            }

            if (bChanged) {
               String sDate =
                  new SimpleDateFormat("dd.MMM.yyyy").format(new Date()).toString() + " - "
                        + DtdUtil.camelCase(System.getProperty("user.name"));
               if (sDate.endsWith("xp"))
                  sDate = sDate.substring(0, sDate.length() - 2);
               xmlSelected.setAttribute("changed", sDate);
               textDateUser.setText(sDate);
            }
            changeImage(treeItemSelected);
         }
         catch (Exception ex) {
            ex.printStackTrace();
         }
   }

   private void setCombos()
   {
      StringTokenizer st = new StringTokenizer(textAreaPnr.getText(), " .*\n");
      if (st.hasMoreTokens()) {
         if (getSelectedItem(comboPnrElement, st.nextToken()) >= 0) {
            updateCombo();
            if (st.hasMoreTokens()) {
               getSelectedItem(comboPnrAttribute, st.nextToken());
            }
         }
      }
   }

   private int getSelectedItem(Combo combo, String nextToken)
   {
      String nextToken2 = "[ " + nextToken + " ]";
      String[] items = combo.getItems();
      int iSelect = -1;
      for (int i = 0; i < items.length; i++) {
         if (items[i].equals(nextToken)) {
            iSelect = i;
            break;
         }
         if (items[i].equals(nextToken2)) {
            iSelect = i;
            break;
         }
      }
      if (iSelect >= 0) {
         combo.select(iSelect);
      }
      return iSelect;
   }

   @Override
   protected void showDetail()
   {
      String text = textAreaPnr.getText();
      MessageBox messageBox = new MessageBox(sShell, SWT.ICON_INFORMATION | SWT.OK);
      messageBox.setMessage(text);
      messageBox.setText("Information");
      int response = messageBox.open();
      //      if (response == SWT.YES)
      //         System.exit(0);
   }

   @Override
   protected void menuClose()
   {
   // TODO Auto-generated method stub

   }

   @Override
   protected void menuOpen()
   {
   // TODO Auto-generated method stub

   }

   @Override
   protected void menuProperties()
   {

   }


   /** 
    * TODO (brod) add comment for method saveData 
    * 
    * @author brod 
    */
   @Override
   void menuSave()
   {
      DtdStatusFrame dtdStatus = new DtdStatusFrame();
      data.save(dtdStatus);
      dtdStatus.close();
   }
}
