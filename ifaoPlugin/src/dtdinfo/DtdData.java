package dtdinfo;


import ifaoplugin.Util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import net.ifao.xml.*;
import dtdinfo.gui.DtdFrame;


/** 
 * Class DtdData 
 * 
 * <p> 
 * Copyright &copy; 2002, i:FAO, AG. 
 * @author Andreas Brod 
 */
public class DtdData
{

   public static final String SRCPATH = "\\src\\net\\ifao\\arctic\\agents";
   public static final String MANDATORY = "<!-- Mandatory -->";

   private DtdObject request, response;
   private Vector<String> vRequests = new Vector<String>();
   private Vector<String> classes = new Vector<String>();

   Hashtable<String, String> htList = new Hashtable<String, String>();
   XmlObject settings = null;

   /** 
    * Constructor DtdData 
    */
   public DtdData(boolean pbReloadPath)
   {
      this("data\\DtdInfo.xml", pbReloadPath);
   }

   /** 
    * Constructor DtdData 
    * @param pbReloadPath 
    */
   public DtdData(String psFile, boolean pbReloadPath)
   {
      if (psFile != null) {
         try {
            settings = new XmlObject(new File(psFile));

            if (settings.getObject("Settings") != null) {
               settings = settings.getObject("Settings");
            } else {
               settings = null;
            }
         }
         catch (FileNotFoundException ex) {}
      }

      if (settings == null) {
         settings =
            (new XmlObject("<Settings user=\"\" left=\"0\" top=\"0\" "
                  + "width=\"640\" height=\"500\" r1=\"300\" " + "r2=\"300\" r3=\"100\"/>"))
                  .getObject("Settings");
      }

      if (pbReloadPath) {
         setPath("");
      }

   }

   /** 
    * Method setSettings 
    * 
    * <p> TODO rename sUser to psUser, iLeft to piLeft, iTop to piTop, iWitdh to piWitdh, iHeight to piHeight, iRuler1 to piRuler1, iRuler2 to piRuler2, iRuler3 to piRuler3, sAgent to psAgent, sProvider to psProvider, sDefaultPath to psDefaultPath
    * @param sUser 
    * @param iLeft 
    * @param iTop 
    * @param iWitdh 
    * @param iHeight 
    * @param iRuler1 
    * @param iRuler2 
    * @param iRuler3 
    * @param sAgent 
    * @param sProvider 
    * @param sDefaultPath 
    * 
    * @author $author$ 
    */
   public void setSettings(String sUser, int iLeft, int iTop, int iWitdh, int iHeight, int iRuler1,
                           int iRuler2, int iRuler3, String sAgent, String sProvider,
                           String sDefaultPath)
   {
      settings.setAttribute("user", sUser);
      settings.setAttribute("left", "" + iLeft);
      settings.setAttribute("top", "" + iTop);
      settings.setAttribute("width", "" + iWitdh);
      settings.setAttribute("height", "" + iHeight);
      settings.setAttribute("r1", "" + iRuler1);
      settings.setAttribute("r2", "" + iRuler2);
      settings.setAttribute("agent", sAgent);
      settings.setAttribute("provider", sProvider);
      settings.setAttribute("defaultPath", sDefaultPath);
      Util.writeToFile("data\\DtdInfo.xml", settings.toString());
   }

   /** 
    * Method getUser 
    * 
    * @return 
    * 
    * @author $author$ 
    */
   public String getUser()
   {
      return settings.getAttribute("user");
   }

   /** 
    * Method getSetting 
    * 
    * <p> TODO rename sAttribute to psAttribute
    * @param sAttribute 
    * @return 
    * 
    * @author $author$ 
    */
   public int getSetting(String sAttribute)
   {
      try {
         return Integer.parseInt(settings.getAttribute(sAttribute));
      }
      catch (NumberFormatException ex) {
         return 0;
      }
   }

   /** 
    * Method getStringSetting 
    * 
    * <p> TODO rename sAttribute to psAttribute
    * @param sAttribute 
    * @return 
    * 
    * @author $author$ 
    */
   public String getStringSetting(String sAttribute)
   {
      return settings.getAttribute(sAttribute);
   }

   /** 
    * Method getPath 
    * 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public String getPath()
   {
      return settings.getAttribute("defaultPath");
   }

   /** 
    * Method setPath 
    * 
    * <p> TODO rename sPath to psPath
    * @param sPath 
    * 
    * @author Andreas Brod 
    */
   public void setPath(String sPath)
   {
      DtdMain.startWaitThread("Load DTD Data");

      if (sPath.length() == 0) {

         // get the default path
         sPath = getPath();
      }

      if (sPath.length() == 0) {
         File f = new File("..\\..\\..");

         try {
            sPath = f.getCanonicalPath();
         }
         catch (IOException ex) {
            sPath = "C:\\";
         }
      }

      System.out.println("Load Path " + sPath);

      settings.setAttribute("defaultPath", sPath);


      request = new DtdObject(Util.getConfFile(sPath, "ArcticRequest.dtd"));
      response = new DtdObject(Util.getConfFile(sPath, "ArcticResponse.dtd"));

      vRequests.clear();

      XmlObject[] requests =
         request.getXmlObject("Arctic").createObject("Arctic").createObject("Request")
               .getObjects("");

      classes.clear();
      addClasses(new File(sPath + SRCPATH), "\\");

      String sClasses = classes.toString();

      for (XmlObject request2 : requests) {
         if (sClasses.toLowerCase().indexOf("\\" + request2.getName().toLowerCase() + ".java") > 0) {
            vRequests.add(request2.getName());
         }
      }

      DtdMain.stopWaitThread();

      // System.out.println(classes);
   }

   /** 
    * Method getFormatedPnrElement 
    * 
    * <p> TODO rename sPnr to psPnr
    * @param sPnr 
    * @return 
    * 
    * @author $author$ 
    */
   public static String getFormatedPnrElement(String sPnr)
   {
      String sAttr = "";

      sPnr = sPnr.trim();

      if (sPnr.indexOf("<") > 0) {
         sPnr = sPnr.substring(0, sPnr.indexOf("<"));
      }

      if (sPnr.indexOf(".") > 0) {
         sAttr = sPnr.substring(sPnr.indexOf(".") + 1).trim();
         sPnr = sPnr.substring(0, sPnr.indexOf(".")).trim();
      } else if (sPnr.indexOf("/") > 0) {
         sAttr = sPnr.substring(sPnr.lastIndexOf("/") + 1).trim();
         sPnr = sPnr.substring(0, sPnr.lastIndexOf("/")).trim();

         if (sPnr.indexOf("/") > 0) {
            sPnr = sPnr.substring(sPnr.indexOf("/") + 1).trim();
         }
      } else {
         sAttr = "";
      }

      if (sPnr.startsWith("!")) {
         sPnr = sPnr.substring(1);
      }

      return sPnr + "." + sAttr;
   }

   /** 
    * Method getPnrElements 
    * 
    * @return 
    * 
    * @author $author$ 
    */
   public Hashtable<String, List<String>> getPnrElements()
   {
      Hashtable<String, List<String>> lst = new Hashtable<String, List<String>>();

      for (String sKey : htList.keySet()) {
         StringValues sValue = new StringValues(get(sKey));

         if (sValue.hasNext()) {
            String sLine = sValue.getNext();
            String sPnr = getFormatedPnrElement(sLine);
            if (sPnr.indexOf(")") > 0) {
               sPnr = sPnr.substring(sPnr.indexOf(")") + 1);
            }

            String sAttr = sPnr.substring(sPnr.indexOf(".") + 1).trim();

            sPnr = sPnr.substring(0, sPnr.indexOf(".")).trim();

            if ((sPnr.length() > 0) && (sAttr.length() > 0)) {
               List<String> elements = lst.get(sPnr);

               if (elements == null) {
                  elements = new Vector<String>();

                  lst.put(sPnr, elements);
               }

               while (sValue.hasNext()) {
                  String sLine2 =
                     sValue.getNext().replaceAll("<br>", "\n   ").replaceAll("&nbsp;", " ").trim();

                  if ((sLine2.length() > 0) && sValue.hasNext()) {
                     sKey += "\n   " + sLine2;
                  }
               }

               elements.add(sAttr + "=" + sKey);
            }
         }
      }


      return lst;
   }

   /** 
    * Method addClasses 
    * 
    * <p> TODO rename f to another name, sAdd to psAdd
    * @param f 
    * @param sAdd 
    * 
    * @author Andreas Brod 
    */
   private void addClasses(File f, String sAdd)
   {
      if (!f.exists()) {
         return;
      }

      if (f.isDirectory()) {
         if (!f.getName().toUpperCase().equals("CVS")) {
            File[] list = f.listFiles();

            for (File element : list) {
               addClasses(element, sAdd + "\\" + element.getName());
            }
         }
      } else {
         if (sAdd.endsWith(".java")) {
            classes.add(sAdd);
         }
      }
   }

   /** 
    * Method getRequests 
    * 
    * 
    * <p> TODO rename sProvider to psProvider
    * @param sProvider 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public Vector<String> getRequests(String sProvider)
   {
      Object[] list = vRequests.toArray();

      if (sProvider.startsWith("-")) {
         sProvider = "";
      } else if (sProvider.length() > 0) {
         sProvider += "\\";
      }

      Arrays.sort(list);

      Vector<String> vList = new Vector<String>();

      for (Object element : list) {
         String sPro = "\\\\" + sProvider + element + ".java";

         if ((sProvider.length() == 0) || classes.contains(sPro)) {
            vList.add((String) element);
         }
      }

      return vList;
   }

   /** 
    * Method getProvider 
    * 
    * <p> TODO rename sRequest to psRequest, bChangeToSmartAgent to pbChangeToSmartAgent
    * @param sRequest 
    * @param bChangeToSmartAgent 
    * @param psProvider 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public Vector<String> getProvider(String sRequest, boolean bChangeToSmartAgent, String psProvider)
   {
      Vector<String> vList = new Vector<String>();

      sRequest += ".java";

      for (int i = 0; i < classes.size(); i++) {
         String s = classes.get(i);

         if (s.toLowerCase().endsWith("\\" + sRequest.toLowerCase())) {
            s = s.substring(0, s.lastIndexOf("\\"));

            while (s.startsWith("\\")) {
               s = s.substring(1);
            }

            if (bChangeToSmartAgent && (s.trim().length() == 0)) {
               s = "- SmartAgent -";
            }

            vList.add(s);
         }
      }

      if (psProvider.length() > 0) {
         if (vList.contains(psProvider)) {
            vList = new Vector<String>();

            vList.add(psProvider);

            return vList;
         }
      }

      // sort List
      Object[] list = vList.toArray();

      Arrays.sort(list);

      vList = new Vector<String>();

      for (Object element : list) {
         vList.add((String) element);
      }

      return vList;
   }

   /** 
    * Method getRequest 
    * 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public DtdObject getRequest()
   {
      return request;
   }

   /** 
    * Method getResponse 
    * 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public DtdObject getResponse()
   {
      return response;
   }

   /** 
    * Method getRequest 
    * 
    * <p> TODO rename sName to psName
    * @param sName 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public XmlObject getRequest(String sName)
   {
      return request.getXmlObject(sName);
   }

   /** 
    * Method getResponse 
    * 
    * <p> TODO rename sName to psName
    * @param sName 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public XmlObject getResponse(String sName)
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

   String sLastLoad = "";

   /** 
    * Method loadList 
    * 
    * <p> TODO rename sFileName to psFileName
    * @param sFileName 
    * 
    * @author $author$ 
    */
   public void loadList(String sFileName)
   {
      htList.clear();
      loadList(sFileName + "_req.html", true);
      loadList(sFileName + "_res.html", false);

      sLastLoad = sFileName;
   }

   /** 
    * TODO (brod) add comment for method getHtmlHeader 
    * 
    * @param psTitle TODO (brod) add text for param psTitle
    * @param psRootPath TODO (brod) add text for param psRootPath
    * @param pbRevisionHistory TODO (brod) add text for param pbRevisionHistory
    * @return TODO (brod) add text for returnValue
    * 
    * @author brod 
    */
   private String getHtmlHeader(String psTitle, String psRootPath, boolean pbRevisionHistory)
   {
      StringBuffer sb = new StringBuffer();
      sb.append("<html>\n");
      sb.append("<head>\n");
      sb.append(" <title>" + psTitle + "</title>\n");
      sb.append(" <style type='text/css'>\n");
      sb.append("   table {border:1px solid #000080;border-spacing:1px;background-color:#000080;}\n");
      sb.append("   th {background-color:#000080;color:white;border:1px solid white;}\n");
      sb.append("   td {vertical-align:top;background-color:white;color:#000080;}\n");
      sb.append(" </style>\n");
      sb.append("</head>\n");
      sb.append("<body style='font-family:Arial;'><center>\n");
      // sb.append("<b>new arctic<br><small>Documentation</small></b><br>\n");
      sb.append("<hr width='50%' noshade='noshade' size='1'>\n");
      sb.append("<b><i>Additional document</i><br>\n");
      int iUnderscoreTitle = psTitle.indexOf("_");
      if (iUnderscoreTitle > 0) {
         sb.append(psTitle.substring(iUnderscoreTitle + 1).replaceAll("_", " ") + "<br>\n");
         psTitle = psTitle.substring(0, iUnderscoreTitle);
      }
      String sSearchAgents = "\\net\\ifao\\arctic\\agents\\";
      int iAgents = psRootPath.indexOf(sSearchAgents);
      if (iAgents >= 0) {
         iAgents += sSearchAgents.length();
         String sAgent = psRootPath.substring(iAgents, psRootPath.indexOf("\\", iAgents));
         sb.append(Util.camelCase(sAgent) + "\n");
      }
      sb.append(psTitle + "<br>\n");
      sb.append("</b>\n");
      if (pbRevisionHistory) {
         sb.append("<hr width='50%' noshade='noshade' size='1'>\n");
         sb.append("<i><b>Revision History</b></i><br><br>\n\n");

         sb.append("<table width='75%'>\n");
         sb.append("  <tr>\n");
         sb.append("    <th width='15%'>Author</td>\n");
         sb.append("    <th width='10%'>Version</td>\n");
         sb.append("    <th width='15%'>Date</td>\n");
         sb.append("    <th width='60%'>Comment</td>\n");
         sb.append("  </tr>\n");
         sb.append("  <tr>\n");
         sb.append("    <td>[enter your name]</td>\n");
         sb.append("    <td>1.0</td>\n");
         sb.append("    <td>" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "</td>\n");
         sb.append("    <td>Initial version<br>\n");
         sb.append("    </td>\n");
         sb.append("  </tr>\n");
         sb.append("</table>\n");
      }
      sb.append("</center><hr>\n\n");
      sb.append("<p>\n");
      return sb.toString();
   }

   /** 
    * TODO (brod) add comment for method getHtmlFooter 
    * 
    * @return TODO (brod) add text for returnValue
    * 
    * @author brod 
    */
   private String getHtmlFooter()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("\n</p><hr>\n");
      sb.append("<p style='text-align:right;font-size:10px'><b style='color:red'>i:</b><b style='color:#000080'>FAO</b> &copy; "
            + (new GregorianCalendar()).get(Calendar.YEAR) + "</b>\n");
      sb.append("</p></body>\n");
      sb.append("</html>\n");
      return sb.toString();

   }

   /** 
    * TODO (brod) add comment for method createAditionalDocuments 
    * 
    * <p> TODO rename sbFiles to pFiles
    * @param psRootPath TODO (brod) add text for param psRootPath
    * @param psText TODO (brod) add text for param psText
    * @param pArcticPnrElementInfos TODO (brod) add text for param pArcticPnrElementInfos
    * @param sbFiles TODO (brod) add text for param sbFiles
    * 
    * @author brod 
    */
   private void createAditionalDocuments(String psRootPath, String psText,
                                         XmlObject pArcticPnrElementInfos, StringBuffer sbFiles,
                                         Set<File> phsFiles)
   {
      int iPos = psText.indexOf(DtdFrame.DOC_REF);
      String sLast = DtdFrame.DOC_REF.substring(DtdFrame.DOC_REF.length() - 1);
      while (iPos >= 0) {
         int iStart = psText.indexOf("\"", iPos) + 1;
         int iEnd = psText.indexOf("\"", iStart);
         String sName = psRootPath + psText.substring(iStart, iEnd);
         if (!(new File(sName)).exists()) {
            StringBuffer sb = new StringBuffer();
            String sTitle = sName.substring(sName.lastIndexOf(sLast) + 1, sName.lastIndexOf("."));
            if (sTitle.endsWith("_PnrElements") || sTitle.endsWith("_PnrElement")) {
               sb.append(getHtmlHeader(sTitle, psRootPath, false));
               sb.append(getPnrElements4Documentation(pArcticPnrElementInfos, sName, psRootPath,
                     sbFiles, phsFiles));

            } else {
               sb.append(getHtmlHeader(sTitle, psRootPath, true));
               sb.append("[Start your document here]");
            }
            sb.append(getHtmlFooter());
            phsFiles.add(Util.writeToFile(sName, sb.toString()));
            sbFiles.append("Write: " + sName + "\n");
         }

         iPos = psText.indexOf(DtdFrame.DOC_REF, iPos + 1);
      }
   }

   /** 
    * TODO (brod) add comment for method getPnrElements4Documentation 
    * 
    * <p> TODO rename sRootFile to psRootFile, sbFiles to pFiles
    * @param pArcticPnrElementInfos TODO (brod) add text for param pArcticPnrElementInfos
    * @param sRootFile TODO (brod) add text for param sRootFile
    * @param psRootPath TODO (brod) add text for param psRootPath
    * @param sbFiles TODO (brod) add text for param sbFiles
    * @return TODO (brod) add text for returnValue
    * 
    * @author brod 
    * @param phsFiles 
    */
   private String getPnrElements4Documentation(XmlObject pArcticPnrElementInfos, String sRootFile,
                                               String psRootPath, StringBuffer sbFiles,
                                               Set<File> phsFiles)
   {

      StringBuffer sb = new StringBuffer();
      sb.append("List of public BusinessElements:<br>\n");
      sb.append("<ul>\n");

      XmlObject[] pnrElementInfo = pArcticPnrElementInfos.getObjects("PnrElementInfo");
      String sProvider = pArcticPnrElementInfos.getAttribute("provider");
      String sLast = DtdFrame.DOC_REF.substring(DtdFrame.DOC_REF.length() - 1);
      int iRootEnd = sRootFile.lastIndexOf(sLast) + 1;
      String sName = sRootFile.substring(iRootEnd);
      sRootFile = sRootFile.substring(0, iRootEnd);
      for (XmlObject element : pnrElementInfo) {
         if (element.getAttribute("scope").equals("PUBLIC")) {
            int iNameEnd = sName.lastIndexOf(".");
            String sDetail =
               sName.substring(0, iNameEnd) + "_" + element.getAttribute("type")
                     + sName.substring(iNameEnd);
            sb.append("<li><a href='" + sDetail + "'>" + element.getAttribute("name") + "("
                  + element.getAttribute("type") + ")" + "</a>\n");

            // create Additional Document
            if (Util.loadFromFile(sRootFile + sDetail).length() == 0) {
               StringBuffer sb2 = new StringBuffer();
               sb2.append(getHtmlHeader(sDetail.substring(0, sDetail.lastIndexOf(".")), psRootPath,
                     true));
               sb2.append("<b>Parameters for BusinessElement:</b><br>"
                     + element.getAttribute("name")
                     + " <a href='"
                     + "/arctic/repository/info?Method=Component&typeComponents=BusinessElement&Component="
                     + sProvider + "." + element.getAttribute("type") + "'>("
                     + element.getAttribute("type") + ")</a><br><br>\n");

               XmlObject[] pnrElementParamInfo = element.getObjects("PnrElementParamInfo");
               sb2.append("<table width='75%'>\n");
               sb2.append("  <tr>\n");
               sb2.append("    <th width='20%'>Parameter</th>\n");
               sb2.append("    <th width='40%'>Transform Rules</th>\n");
               sb2.append("    <th width='40%'>Provider Ref</th>\n");
               sb2.append("  </tr>\n");
               for (XmlObject element2 : pnrElementParamInfo) {
                  sb2.append("  <tr>\n");
                  sb2.append("    <td><b>" + element2.getAttribute("name") + " ("
                        + element2.getAttribute("id") + ")</b></td>\n");
                  sb2.append("    <td>... to be added</td>\n");
                  sb2.append("    <td>... to be added</td>\n");
                  sb2.append("  </tr>\n");

               }
               sb2.append("</table><br>\n");
               sb2.append("<a href='" + sName + "'>... back to overview</a>\n");

               sb2.append(getHtmlFooter());
               phsFiles.add(Util.writeToFile(sRootFile + sDetail, sb2.toString()));
               sbFiles.append("Write: " + sRootFile + sDetail + "\n");
            }
         }

      }
      sb.append("</ul>\n");

      return sb.toString();
   }

   /** 
    * Method writeList 
    * 
    * <p> TODO rename sRequest to psRequest, sResponse to psResponse
    * @param sRequest 
    * @param sResponse 
    * @param pArcticPnrElementInfos TODO (brod) add text for param pArcticPnrElementInfos
    * @return TODO (brod) add text for returnValue
    * 
    * @author $author$ 
    * @param xmlData 
    * @param phsFiles 
    */
   public String writeList(String sRequest, String sResponse, XmlObject pArcticPnrElementInfos,
                           XmlObject xmlData, Set<File> phsFiles)
   {
      StringBuffer sbFiles = new StringBuffer();
      if (sLastLoad.length() > 0) {
         sbFiles.append("Write: " + sLastLoad + "_req.html\n");
         phsFiles.add(Util.writeToFile(sLastLoad + "_req.html", sRequest));

         sbFiles.append("Write: " + sLastLoad + "_res.html\n");
         phsFiles.add(Util.writeToFile(sLastLoad + "_res.html", sResponse));

         sbFiles.append("Write: " + sLastLoad + "_reqres.xml\n");
         phsFiles.add(Util.writeToFile(sLastLoad + "_reqres.xml", xmlData.toString()));

         String sPath = sLastLoad.substring(0, sLastLoad.lastIndexOf("\\") + 1);

         createAditionalDocuments(sPath, sRequest, pArcticPnrElementInfos, sbFiles, phsFiles);
         createAditionalDocuments(sPath, sResponse, pArcticPnrElementInfos, sbFiles, phsFiles);

         //         if (sLastLoad.indexOf("src\\net\\ifao\\") >= 0) {
         //            String sPath2 = DtdMain.strTran("src\\net\\ifao\\", "doc\\net\\ifao\\", sLastLoad);
         //
         //            Util.writeToFile(sPath2 + "_req.html", sRequest);
         //            Util.writeToFile(sPath2 + "_res.html", sResponse);
         //         }

         String sJava = Util.loadFromFile(sLastLoad + ".java");

         String sReq = sLastLoad.substring(sLastLoad.lastIndexOf("\\") + 1);
         String sRef1 =
            "<p>Request for <a href='" + sReq + "_req.html'><var>" + sReq + "</var></a></p>";
         String sRef2 =
            "<p>Response for <a href='" + sReq + "_res.html'><var>" + sReq + "</var></a></p>";
         String sAdd = "<!-- STARTREQUEST -->\n";

         sAdd += " * " + sRef1 + "\n";
         sAdd += " * <!-- ENDREQUEST -->\n";
         sAdd += " * <!-- STARTRESPONSE -->\n";
         sAdd += " * " + sRef2 + "\n";
         sAdd += " * <!-- ENDRESPONSE -->\n * ";

         if ((sJava.indexOf("/**") > 0)
               && ((sJava.indexOf(sRef1) < 0) || (sJava.indexOf(sRef2) < 0))) {

            sJava = eliminateLines(sJava, "REQUEST");
            sJava = eliminateLines(sJava, "RESPONSE");

            int start = sJava.indexOf("/**");

            start = sJava.indexOf(" *", start) + 2;

            int end1 = sJava.indexOf("*/", start);
            int end2 = sJava.indexOf("<p>", start);

            if ((end2 > 0) && (end2 < end1)) {
               start = end2;
            }

            sJava = sJava.substring(0, start) + sAdd + sJava.substring(start);

            phsFiles.add(Util.writeToFile(sLastLoad + ".java", sJava));
            sbFiles.append("Modified: " + sLastLoad + ".java\n");
         }
      }
      return sbFiles.toString();
   }

   /** 
    * Method eliminateLines 
    * 
    * <p> TODO rename sText to psText, sType to psType
    * @param sText 
    * @param sType 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private String eliminateLines(String sText, String sType)
   {
      String sStart = "<!-- START" + sType + " -->";
      String sEnd = "<!-- END" + sType + " -->";
      int start = sText.indexOf(sStart);
      int end = sText.indexOf(sEnd);

      if ((start > 0) && (end > 0)) {
         start = sText.lastIndexOf("\n", start);
         end = sText.indexOf("\n", end);
         sText = sText.substring(0, start) + sText.substring(end);
      }

      return sText;
   }

   /** 
    * Method loadList 
    * 
    * <p> TODO rename sFileName to psFileName, bRequest to pbRequest
    * @param sFileName 
    * @param bRequest 
    * 
    * @author $author$ 
    */
   private void loadList(String sFileName, boolean bRequest)
   {
      StringTokenizer st;

      st = new StringTokenizer(Util.loadFromFile(sFileName), "\n");

      String sAdd = "";

      while (st.hasMoreTokens()) {
         String sLine = st.nextToken();

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

            put(bRequest + " " + sLine, sAdd);

            sAdd = "";
         } else if (sLine.startsWith("<!-- START ")) {
            sAdd = "";
         } else {
            sAdd += sLine + "\n";
         }
      }
   }

   /** 
    * Method put 
    * 
    * <p> TODO rename sTitle to psTitles to another name
    * @param sTitle 
    * @param s 
    * 
    * @author $author$ 
    */
   public void put(String sTitle, String s)
   {
      if (sTitle.endsWith(".FixSettings")) {
         sTitle = sTitle.substring(0, sTitle.indexOf(" ")) + " FixSettings";
      }

      if (s.startsWith(MANDATORY)) {
         s = "!" + s.substring(MANDATORY.length());
      }

      htList.put(sTitle, s);

      // System.out.println("PUT "+sTitle);
   }

   /** 
    * Method get 
    * 
    * <p> TODO rename sTitle to psTitle
    * @param sTitle 
    * @return 
    * 
    * @author $author$ 
    */
   public String get(String sTitle)
   {
      if (sTitle.endsWith(".FixSettings")) {
         sTitle = sTitle.substring(0, sTitle.indexOf(" ")) + " FixSettings";
      }

      // System.out.println("GET "+sTitle);
      sTitle = htList.get(sTitle);

      if (sTitle == null) {
         sTitle = "";
      }

      return sTitle;
   }

}
