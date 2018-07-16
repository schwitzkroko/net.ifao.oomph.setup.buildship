package net.ifao.tools.dtdinfo;


import ifaoplugin.Util;

import java.io.*;
import java.util.*;

//import net.ifao.tools.dtdinfo.gui.DtdStatus;
import net.ifao.xml.XmlObject;


/** 
 * TODO (brod) add comment for class Data 
 * 
 * <p> 
 * Copyright &copy; 2008, i:FAO 
 * 
 * @author brod 
 */
public class Data
{


   /** 
    * TODO (brod) add comment for method isNotSupported 
    * 
    * <p> TODO rename xml to pXml
    * @param xml TODO (brod) add text for param xml 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   public static boolean isNotSupported(XmlObject xml)
   {
      return xml.createObject("TransformRules").getCData().toLowerCase()
            .startsWith("not supported");
   }


   /** 
    * TODO (brod) add comment for method hasFilledSubObjects 
    * 
    * <p> TODO rename xml to pXml
    * @param xml TODO (brod) add text for param xml 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   public static boolean hasFilledSubObjects(XmlObject xml)
   {
      String sTransformRules =
         xml.createObject("TransformRules").getCData() + xml.createObject("Pnr").getCData();
      if (sTransformRules.length() > 0) {
         return true;
      }
      XmlObject[] items = xml.getObjects("Item");
      for (XmlObject item : items) {
         if (hasFilledSubObjects(item)) {
            return true;
         }
      }
      return false;
   }

   private XmlObject agents;
   private XmlObject arctic;
   private XmlObject arcticReqest;
   private XmlObject arcticResponse;
   private XmlObject providerPnrElementInfos;

   public XmlObject request;
   public XmlObject response;

   private String sAgent;
   private String sClassName;
   private String sPath;
   private Hashtable<String, ArrayList<XmlObject>> htPnrElements;

   /** 
    * TODO (brod) add comment for Constructor Data 
    * 
    * <p> TODO rename dtdStatus to pStatus
    * @param dtdStatus 
    * @param psPath TODO (brod) add text for param psPath 
    * 
    * @author brod 
    */
   public Data(DtdStatus dtdStatus, String psPath)
   {
      load(dtdStatus, psPath);
   }

   /** 
    * TODO (brod) add comment for method addElements 
    * 
    * <p> TODO rename xml to pXml, ret to pRet, object to pObject
    * @param xml TODO (brod) add text for param xml 
    * @param ret TODO (brod) add text for param ret 
    * @param object TODO (brod) add text for param object 
    * 
    * @author brod 
    * @param psPath 
    */
   private void addElements(XmlObject xml, XmlObject ret, XmlObject object, String psPath)
   {
      if (object == null) {
         return;
      }
      XmlObject[] objects = object.getObjects("");
      for (XmlObject object2 : objects) {
         if (object2.getName().endsWith("element")) {
            String sName = object2.getAttribute("ref");
            sName = sName.substring(sName.indexOf(":") + 1);
            if (!psPath.contains("/" + sName + "/")) {
               XmlObject element = getObject(xml, sName, psPath + sName + "/");
               ret.addObject(element);
            } else {
               System.out.println(sName + " already exists within " + psPath);
            }
         } else {
            addElements(xml, ret, object2, psPath);
         }
      }
   }

   /** 
    * TODO (brod) add comment for method extractTds 
    * 
    * <p> TODO rename add to psAdd
    * @param add TODO (brod) add text for param add 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   private String[] extractTds(String add)
   {
      ArrayList<String> lst = new ArrayList<String>();
      int iStart = 0;
      int iEnd = add.indexOf("</td>");
      while (iEnd > 0) {
         String s = add.substring(iStart, iEnd);
         int iStartTd = s.indexOf("<td>");
         if (iStartTd >= 0) {
            s = s.substring(iStartTd + 4);
         }
         // correct html
         s = s.replaceAll("<br>", "\n").replaceAll("&nbsp;", " ");
         lst.add(s.trim());
         iStart = iEnd + 4;
         iEnd = add.indexOf("</td>", iEnd + 1);
      }
      return lst.toArray(new String[0]);
   }

   /** 
    * TODO (brod) add comment for method getComment 
    * 
    * <p> TODO rename xmlObject to pObject
    * @param xmlObject TODO (brod) add text for param xmlObject 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   private String getComment(XmlObject xmlObject)
   {
      try {
         return xmlObject.getObject("annotation").getObject("documentation").getCData()
               .replaceAll("\"", "&quot;");
      }
      catch (Exception ex) {

      }
      return "";
   }

   /** 
    * TODO (brod) add comment for method getObject 
    * 
    * <p> TODO rename xml to pXml, agent to psAgent
    * @param xml TODO (brod) add text for param xml 
    * @param agent TODO (brod) add text for param agent 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   private XmlObject getObject(XmlObject xml, String agent, String psPath)
   {
      XmlObject ret = new XmlObject("<Item name=\"" + agent + "\" />").getFirstObject();
      try {
         XmlObject findSubObject = xml.findSubObject("element", "name", agent);
         ret.setAttribute("comment", getComment(findSubObject));
         XmlObject complexType = findSubObject.getObject("complexType");
         XmlObject[] attributes = complexType.getObjects("attribute");
         for (XmlObject attribute2 : attributes) {
            String attribute = attribute2.getAttribute("name");
            XmlObject xmlAtt =
               new XmlObject("<Item name=\"" + attribute + "\" />").getFirstObject();
            xmlAtt.setAttribute("type", attribute2.getAttribute("type"));
            if (attribute2.getAttribute("use").equalsIgnoreCase("required")) {
               xmlAtt.setAttribute("required", "true");
            }
            xmlAtt.setAttribute("comment", getComment(attribute2));
            ret.addObject(xmlAtt);
         }
         XmlObject sequence = complexType.getObject("sequence");
         if (sequence != null) {
            addElements(xml, ret, sequence, psPath);
         }
         XmlObject choice = complexType.getObject("choice");
         if ((choice != null) && (choice.getObjects("element").length > 0)) {
            addElements(xml, ret, choice, psPath);
         }
      }
      catch (Exception ex) {}
      return ret;
   }

   /** 
    * TODO (brod) add comment for method load 
    * 
    * <p> TODO rename dtdStatus to pStatus
    * @param dtdStatus TODO (brod) add text for param dtdStatus
    * @param psPath TODO (brod) add text for param psPath 
    * 
    * @author brod 
    */
   public void load(DtdStatus dtdStatus, String psPath)
   {
      sPath = psPath;
      try {
         dtdStatus.setText("Load Agents.xml");
         agents =
            new XmlObject(new FileInputStream(Util.getConfFile(sPath, "Agents.xml")))
                  .getFirstObject();

         dtdStatus.setText("Load ArcticRequest.xsd");
         arcticReqest =
            new XmlObject(new FileInputStream(Util.getConfFile(sPath, "ArcticRequest.xsd")))
                  .getFirstObject();
         dtdStatus.setText("Load ArcticResponse.xsd");
         arcticResponse =
            new XmlObject(new FileInputStream(Util.getConfFile(sPath, "ArcticResponse.xsd")))
                  .getFirstObject();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   /** 
    * TODO (brod) add comment for method load 
    * 
    * <p> TODO rename dtdStatus to pStatus
    * @param dtdStatus 
    * @param psAgent TODO (brod) add text for param psAgent 
    * @param psProvider TODO (brod) add text for param psProvider 
    * 
    * @author brod 
    */
   public void load(DtdStatus dtdStatus, String psAgent, String psProvider)
   {
      sAgent = psAgent;
      request = getObject(arcticReqest, sAgent, "/");

      String comment = getComment(arcticReqest.findSubObject("element", "name", sAgent)).trim();
      if (comment.indexOf("response will be") > 0) {
         comment = comment.substring(comment.lastIndexOf(" ") + 1);
         response = getObject(arcticResponse, comment, "/");
      } else {
         response = getObject(arcticResponse, sAgent, "/");
      }

      arctic = new XmlObject("<Arctic />").getFirstObject();
      htPnrElements = new Hashtable<String, ArrayList<XmlObject>>();
      if (psProvider.length() > 0) {
         try {
            XmlObject agent =
               agents.getObject("Agents").findSubObject("AgentGroup", "requestType", sAgent)
                     .findSubObject("Agent", "providerType", psProvider);
            sClassName = agent.getAttribute("className");
            dtdStatus.setText("Load " + sClassName);
            sClassName = "src/" + sClassName.replaceAll("\\.", "/");
            if (!loadXml(request, response, sClassName + ".xml")) {
               loadHtml(request, sClassName + "_req.html", "Request");
               loadHtml(response, sClassName + "_res.html", "Response");
            }

            dtdStatus.setText("Load ArcticPnrElementInfos.xml");
            XmlObject arcticPnrElementInfos =
               new XmlObject(new FileInputStream(Util.getConfFile(sPath,
                     "ArcticPnrElementInfos.xml"))).getFirstObject();
            XmlObject[] provider = arcticPnrElementInfos.getObjects("PnrElementInfos");
            for (XmlObject element : provider) {
               XmlObject[] pnrElement = element.getObjects("PnrElementInfo");
               for (XmlObject element2 : pnrElement) {
                  String type = element2.getAttribute("type");
                  ArrayList<XmlObject> list = htPnrElements.get(type);
                  if (list == null) {
                     list = new ArrayList<XmlObject>();
                     htPnrElements.put(type, list);
                  }
                  XmlObject[] paramInfo = element2.getObjects("PnrElementParamInfo");
                  for (XmlObject element3 : paramInfo) {
                     String id = element3.getAttribute("id");
                     boolean bOk = false;
                     for (int l = 0; !bOk && (l < list.size()); l++) {
                        bOk = list.get(l).getAttribute("id").equals(id);
                     }
                     if (!bOk) {
                        list.add(element3.copy());
                     }
                  }
               }
            }
            providerPnrElementInfos =
               arcticPnrElementInfos.createObject("PnrElementInfos", "provider", psProvider, true);

         }
         catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      arctic.addObject(request);
      arctic.addObject(response);

   }

   /** 
    * TODO (brod) add comment for method loadXml 
    * 
    * <p> TODO rename request2 to p2, response2 to p2, string to psString
    * @param request2 TODO (brod) add text for param request2
    * @param response2 TODO (brod) add text for param response2
    * @param string TODO (brod) add text for param string
    * @return TODO (brod) add text for returnValue
    * 
    * @author brod 
    */
   private boolean loadXml(XmlObject request2, XmlObject response2, String string)
   {
      try {
         File f = new File(sPath, string);
         XmlObject arctic = new XmlObject(new FileInputStream(f)).getFirstObject();
         XmlObject[] items = arctic.getObjects("Item");
         if (items.length == 2) {
            loadXml(items[0], request2);
            loadXml(items[1], response2);
            return true;
         }
      }
      catch (Exception ex) {

      }

      return false;
   }

   /** 
    * TODO (brod) add comment for method loadXml 
    * 
    * <p> TODO rename oldValue to pValue, newValue to pValue
    * @param oldValue TODO (brod) add text for param oldValue
    * @param newValue TODO (brod) add text for param newValue
    * 
    * @author brod 
    */
   private void loadXml(XmlObject oldValue, XmlObject newValue)
   {
      if (oldValue == null) {
         return;
      }
      // name has to match
      if (oldValue.getAttribute("name").equals(newValue.getAttribute("name"))) {
         newValue.addObject(oldValue.getObject("Pnr"));
         newValue.addObject(oldValue.getObject("TransformRules"));
         newValue.addObject(oldValue.getObject("ProviderRef"));
         newValue.setAttribute("changed", oldValue.getAttribute("changed"));
         if (oldValue.getAttribute("selfDefined").length() > 0) {
            newValue.setAttribute("selfDefined", oldValue.getAttribute("selfDefined"));
         }

         XmlObject[] items = newValue.getObjects("Item");
         for (XmlObject item : items) {
            XmlObject subObject = oldValue.findSubObject("Item", "name", item.getAttribute("name"));
            loadXml(subObject, item);
         }
      }

   }

   /** 
    * Method loadList 
    * 
    * <p> TODO rename arcticReqest2 to pReqest2, string to psString
    * @param arcticReqest2 TODO (brod) add text for param arcticReqest2 
    * @param string TODO (brod) add text for param string 
    * @param psBase TODO (brod) add text for param psBase 
    * 
    * @author $author$ 
    */
   private void loadHtml(XmlObject arcticReqest2, String string, String psBase)
   {
      String sAdd = "";
      try {
         File f = new File(sPath, string);
         BufferedReader br = new BufferedReader(new FileReader(f));
         String sLine;
         while ((sLine = br.readLine()) != null) {

            if (sLine.startsWith("<!-- END ")) {
               sLine = sLine.substring(9, sLine.indexOf("-->")).trim();

               if (sLine.endsWith("&gt;")) {
                  sLine = sLine.substring(0, sLine.length() - 4).trim();
               }

               if (sLine.endsWith("*") || sLine.endsWith("+") || sLine.endsWith("?")) {
                  sLine = sLine.substring(0, sLine.length() - 1);
               }

               if (sLine.startsWith("&lt;")) {
                  sLine = sLine.substring(4);
               }
               StringTokenizer st = new StringTokenizer(sLine, " .");
               XmlObject subObject = arcticReqest2;
               st.nextToken();
               while (st.hasMoreTokens() && (subObject != null)) {
                  String nextToken = st.nextToken();
                  subObject = subObject.findSubObject("Item", "name", nextToken);
               }
               if (subObject != null) {
                  String[] tds = extractTds(sAdd);
                  for (int i = 0; i < tds.length; i++) {
                     String sData = tds[i].replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                     switch (i) {
                        case 0:
                           XmlObject pnr = subObject.createObject("Pnr");
                           if (sData.indexOf("*") > 0) {
                              sData = sData.replaceAll("\\*", "");
                              subObject.setAttribute("selfDefined", "true");
                           } else {
                              subObject.setAttribute("selfDefined", null);
                           }
                           pnr.setCData(sData);
                           break;
                        case 1:
                           subObject.createObject("TransformRules").setCData(sData);
                           break;
                        case 2:
                           subObject.createObject("ProviderRef").setCData(sData);
                           break;
                        case 3:
                           subObject.setAttribute("changed", sData);
                           break;
                     }
                  }

               }

               sAdd = "";
            } else if (sLine.startsWith("<!-- START ")) {
               sAdd = "";
            } else {
               sAdd += sLine + "\n";
            }
         }
      }
      catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /** 
    * TODO (brod) add comment for method save 
    * 
    * <p> TODO rename dtdStatus to pStatus
    * @param dtdStatus 
    * 
    * @author brod 
    */
   public void save(DtdStatus dtdStatus)
   {
      dtdStatus.setText("Save file " + sClassName + ".xml");
      DtdUtil.saveFile(sPath + "/" + sClassName + ".xml", arctic.toString());
   }

   public String[] getProviderPnrElements()
   {
      ArrayList<String> lst = new ArrayList<String>();
      XmlObject[] objects =
         providerPnrElementInfos != null ? providerPnrElementInfos.getObjects("PnrElementInfo")
               : new XmlObject[0];
      for (XmlObject object : objects) {
         lst.add(object.getAttribute("type"));
      }
      String[] keys = htPnrElements.keySet().toArray(new String[0]);
      for (int i = 0; i < keys.length; i++) {
         if (!lst.contains(keys[i])) {
            lst.add("[ " + keys[i] + " ]");
         }
      }

      keys = lst.toArray(new String[0]);
      Arrays.sort(keys);
      return keys;
   }

   public String[] getProviderPnrAttributes(String psValue)
   {
      if (psValue.startsWith("[")) {
         psValue = psValue.substring(1, psValue.length() - 1).trim();
      }
      ArrayList<String> lst = new ArrayList<String>();
      try {
         XmlObject[] objects =
            providerPnrElementInfos.findSubObject("PnrElementInfo", "type", psValue).getObjects(
                  "PnrElementParamInfo");
         for (XmlObject object : objects) {
            lst.add(object.getAttribute("id"));
         }
      }
      catch (Exception ex) {

      }
      try {
         ArrayList<XmlObject> arrayList = htPnrElements.get(psValue);
         XmlObject[] keys = arrayList.toArray(new XmlObject[0]);
         for (XmlObject key : keys) {
            String sId = key.getAttribute("id");
            if (!lst.contains(sId)) {
               lst.add("[ " + sId + " ]");
            }
         }
      }
      catch (Exception ex) {

      }

      String[] keys;
      keys = lst.toArray(new String[0]);
      Arrays.sort(keys);
      return keys;
   }


   public String[] getProvider()
   {
      HashSet<String> hsAgents = new HashSet<String>();
      XmlObject[] agentGroup = agents.getObject("Agents").getObjects("AgentGroup");
      for (XmlObject element : agentGroup) {
         XmlObject[] agent = element.getObjects("Agent");
         for (XmlObject element2 : agent) {
            String sProviderType = element2.getAttribute("providerType");
            if (sProviderType.length() > 0) {
               hsAgents.add(sProviderType);
            }
         }
      }
      String[] array = hsAgents.toArray(new String[0]);
      Arrays.sort(array);
      return array;
   }


   public String[] getAgents(String sProvider)
   {
      HashSet<String> hsAgents = new HashSet<String>();
      XmlObject[] agentGroup = agents.getObject("Agents").getObjects("AgentGroup");
      for (XmlObject element : agentGroup) {
         XmlObject[] agent = element.getObjects("Agent");
         for (XmlObject element2 : agent) {
            String sProviderType = element2.getAttribute("providerType");
            if (sProviderType.equals(sProvider)) {
               hsAgents.add(element.getAttribute("requestType"));
            }
         }
      }
      String[] array = hsAgents.toArray(new String[0]);
      Arrays.sort(array);

      return array;
   }
}
