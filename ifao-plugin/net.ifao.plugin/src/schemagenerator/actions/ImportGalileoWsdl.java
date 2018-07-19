package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;
import java.util.*;

import net.ifao.xml.XmlObject;
import schemagenerator.Generator;


/** 
 * TODO (brod) add comment for class ImportGalileoWsdl 
 * 
 * <p> 
 * Copyright &copy; 2009, i:FAO 
 * 
 * @author brod 
 */
public class ImportGalileoWsdl
{

   private static UrlConnection urlConnection = null;

   /** 
    * TODO (brod) add comment for method getLastVersions 
    * 
    * @param psUrl TODO (brod) add text for param psUrl 
    * @param pbShowVersionsOnly TODO (brod) add text for param pbShowVersionsOnly 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   public static Hashtable<String, String[]> getLastVersions(String psUrl,
                                                             boolean pbShowVersionsOnly)
   {

      Hashtable<String, String> htInstalled = getInstalledVersions();

      Hashtable<String, String[]> ht = new Hashtable<String, String[]>();

      String[] installedKeys = htInstalled.keySet().toArray(new String[0]);
      for (String installedKey : installedKeys) {
         String sValue = htInstalled.get(installedKey);
         ht.put(installedKey, new String[]{ sValue, "---" });
      }
      Hashtable<String, Integer> htArchived = new Hashtable<String, Integer>();
      try {
         // List<List<String>> archivedTransactions = getArchivedTransactions(psUrl);

         // get all links
         String loadUrl = loadUrl(psUrl);
         int iStart = loadUrl.indexOf("<a ");
         while (iStart > 0) {
            int iEnd = loadUrl.indexOf(">", iStart);
            if (iEnd > iStart) {
               String sLine = loadUrl.substring(iStart, iEnd);
               int iHref = sLine.toLowerCase().indexOf(" href=\"");
               if (iHref > 0) {
                  iHref += 7;
                  String sHref = sLine.substring(iHref, sLine.toLowerCase().indexOf("\"", iHref));

                  int iVersion = sHref.indexOf("_");
                  int iTab = sHref.indexOf("/");
                  if ((iTab > 0) && (iVersion > 0)) {
                     String sRequest = sHref.substring(0, iVersion);
                     String sVersion = sHref.substring(iVersion + 1, iTab);
                     // if (isMaxVersion(sRequest, sVersion, archivedTransactions)) {
                     String sInstalledVersion = htInstalled.get(sRequest);
                     int iVersionNew = version(sRequest + "_" + sVersion);
                     if (sInstalledVersion == null) {
                        sInstalledVersion = "---";
                     } else {
                        Integer iArchived = htArchived.get(sRequest);
                        if ((iArchived != null) && (iArchived >= iVersionNew)) {
                           sInstalledVersion = "+" + sInstalledVersion;

                        }
                     }
                     String[] sVersions = { sInstalledVersion, sVersion };
                     if (pbShowVersionsOnly) {
                        String[] oldVersion = ht.get(sRequest);
                        if ((oldVersion == null)
                              || (iVersionNew >= version(sRequest + "_" + oldVersion[1]))) {
                           ht.put(sRequest, sVersions);
                        }
                     } else {
                        ht.put(sRequest, new String[]{ sHref });
                     }
                     //}
                  } else if (sHref.indexOf("Archived_Transactions.htm") >= 0) {
                     getArchivedTransactions(htArchived,
                           psUrl.substring(0, psUrl.lastIndexOf("/") + 1) + sHref);
                  }
               }
            }
            iStart = loadUrl.indexOf("<a ", iStart + 3);
         }

      }
      catch (IOException e) {
         e.printStackTrace();
      }

      return ht;
   }

   //   /** 
   //    * @author brod 
   //    */
   //   private static boolean isMaxVersion(String request, String version,
   //                                       List<List<String>> archivedTransactions)
   //   {
   //      request += "_";
   //      // try to find version within used archivedTransactions
   //      for (int i = 0; i < archivedTransactions.size(); i++) {
   //         List<String> list = archivedTransactions.get(i);
   //         if (list.size() > 1) {
   //            String sUsedVersion = list.get(1);
   //            if (sUsedVersion.startsWith(request)) {
   //               return (version(request + version) <= version(sUsedVersion));
   //            }
   //         }
   //      }
   //      return true;
   //   }

   /** 
    * TODO (brod) add comment for method version 
    * 
    * <p> TODO rename string to psString
    * @param string TODO (brod) add text for param string 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   private static int version(String string)
   {
      int iVersion = 0;
      StringTokenizer st = new StringTokenizer(string, "._");
      st.nextToken();
      for (int i = 0; i < 3; i++) {
         iVersion = iVersion * 100;
         try {
            iVersion += Integer.parseInt(st.nextToken());
         }
         catch (Exception ex) {
            // invalid number
         }
      }
      return iVersion;
   }

   /** 
    * TODO (brod) add comment for method getArchivedTransactions 
    * 
    * <p> TODO rename htArchived to pArchived
    * @param htArchived TODO (brod) add text for param htArchived
    * @param psUrl TODO (brod) add text for param psUrl 
    * 
    * @author brod 
    */
   public static void getArchivedTransactions(Hashtable<String, Integer> htArchived, String psUrl)
   {
      try {
         // String sUrl = psUrl.substring(0, psUrl.lastIndexOf("/") + 1);
         String s = loadUrl(psUrl);
         s = s.substring(s.indexOf("<table "), s.indexOf("</table>") + 8);

         int iTr = s.indexOf("<tr");
         while (iTr > 0) {
            int iTrEnd = s.indexOf("</tr", iTr);
            int iTd = s.indexOf("<td", iTr);
            if ((iTd > 0) && (iTd < iTrEnd)) {
               int iTdEnd = s.indexOf("</td", iTd);

               StringTokenizer stNoTags =
                  new StringTokenizer(noTags(s.substring(iTd, iTdEnd)), " \n\r");
               while (stNoTags.hasMoreTokens()) {
                  String noTags = stNoTags.nextToken();
                  int iStart = noTags.indexOf("_");
                  if (iStart > 0) {
                     int version = version(noTags);
                     if (version > 0) {
                        String sElement = noTags.substring(0, iStart);
                        Integer iOldValue = htArchived.get(sElement);
                        if (iOldValue == null) {
                           htArchived.put(sElement, version);
                        } else {
                           htArchived.put(sElement, Math.max(version, iOldValue));
                        }
                     }
                  }
               }
               iTd = s.indexOf("<td", iTd + 1);
            }
            iTr = s.indexOf("<tr", iTr + 3);
         }
      }
      catch (IOException e) {
         e.printStackTrace();
      }

   }

   /** 
    * TODO (brod) add comment for method noTags 
    * 
    * @param psText TODO (brod) add text for param psText
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   private static String noTags(String psText)
   {
      String s = psText;
      while (s.indexOf("<") >= 0) {
         s = s.substring(0, s.indexOf("<")) + " " + s.substring(s.indexOf(">") + 1);
      }
      s = s.replaceAll("\\n", " ");
      s = s.replaceAll("\\r", " ");
      while (s.indexOf("  ") > 0) {
         s = s.replaceAll("  ", " ");
      }
      return s.trim();
   }

   /** 
    * TODO (brod) add comment for method getInstalledVersions 
    * 
    * @return TODO (brod) add text for returnValue 
    * 
    * @author brod 
    */
   private static Hashtable<String, String> getInstalledVersions()
   {
      Hashtable<String, String> ht = new Hashtable<String, String>();
      try {
         XmlObject data =
            new XmlObject(Util.getProviderDataFile(Generator.getSettings().getAttribute("baseDir"),
                  "net/ifao/providerdata/galileo/data.xsd")).getFirstObject();
         XmlObject[] elements =
            data.findSubObject("element", "name", "GalileoRequest").findSubObject("complexType")
                  .findSubObject("choice").getObjects("element");
         for (XmlObject element : elements) {
            String sName = element.getAttribute("name");
            int iPos = sName.indexOf("_");
            if (iPos > 0) {
               ht.put(sName.substring(0, iPos), sName.substring(iPos + 1));
            }
         }

      }
      catch (Exception e) {
         // make nothing
      }
      return ht;
   }

   /** 
    * TODO (brod) add comment for method startToImport 
    * 
    * <p> TODO rename hsSelected to pSelected
    * @param hsSelected TODO (brod) add text for param hsSelected 
    * @param psUrl TODO (brod) add text for param psUrl 
    * @param psTempDir TODO (brod) add text for param psTempDir
    * 
    * @author brod 
    */
   public static void startToImport(HashSet<String> hsSelected, String psUrl, String psTempDir)
   {
      XmlObject data;
      XmlObject choiceRequest, choiceResponse;
      File dataXsd =
         Util.getProviderDataFile(Generator.getSettings().getAttribute("baseDir"),
               "net/ifao/providerdata/galileo/data.xsd");
      try {
         data = new XmlObject(dataXsd).getFirstObject();
         choiceRequest =
            data.findSubObject("element", "name", "GalileoRequest").findSubObject("complexType")
                  .findSubObject("choice");
         choiceResponse =
            data.findSubObject("element", "name", "GalileoResponse").findSubObject("complexType")
                  .findSubObject("choice");
      }
      catch (Exception e) {
         // make nothing
         throw new RuntimeException(e);
      }

      // get the last versions
      // Hashtable<String, String[]> lastVersions = getLastVersions(psUrl, false);
      // load the related versions to temp directory

      File fTransactionBase = new File(psTempDir + "/TransactionHelp");

      if (!fTransactionBase.exists()) {
         fTransactionBase.mkdirs();
      }
      String sBaseUrl = psUrl.substring(0, psUrl.lastIndexOf("/") + 1);

      String[] keys = hsSelected.toArray(new String[0]);
      String[] reqRes = { "_request.xml", "_response.xml" };
      for (String sKey : keys) {
         String sUrl = sKey + "/" + sKey + ".htm";
         String sTestUrl = sKey + "/Test_" + sKey + ".htm";
         try {
            if (sUrl.endsWith(".htm")) {
               sUrl = sUrl.substring(0, sUrl.lastIndexOf("."));
               for (String reqRe : reqRes) {
                  File file = new File(fTransactionBase, sUrl + reqRe);
                  if (!file.exists()) {
                     String loadUrl = loadUrl(sBaseUrl + sUrl + reqRe);
                     Utils.writeFile(file, loadUrl);
                  }
               }
               String sType = sUrl.substring(sUrl.lastIndexOf("/") + 1);

               addToChoice(choiceRequest, sType, "_req");
               addToChoice(choiceResponse, sType, "_res");
               // copy samples
               copySamples(sTestUrl, sBaseUrl, fTransactionBase);
            }
         }
         catch (IOException e) {
            // Make nothing     
            throw new RuntimeException(e.getLocalizedMessage()
                  + "\nThe following request is not avaiable " + sUrl
                  + ".\nTry to deactivate the request and use the old version");
         }
      }
      try {
         Utils.writeFile(dataXsd, data.toString());
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }

      ImportGalileo.start2Import(fTransactionBase.getAbsolutePath(), dataXsd.getParent());

   }

   /** 
    * This method load a specific url 
    * @param psUrl The related URL (to load) 
    * @return the content of the URL 
    * @throws IOException 
    * 
    */
   private static String loadUrl(String psUrl)
      throws IOException
   {
      if (urlConnection == null) {
         urlConnection = new UrlConnection(psUrl);
      } else {
         urlConnection.setUrl(psUrl);
      }

      String sResponse = urlConnection.getContent();
      // get the forms
      List<HtmlForm> forms = HtmlForm.getForms(sResponse);
      if (forms.size() > 0) {
         // get the first form
         HtmlForm htmlForm = forms.get(0);
         if (htmlForm.getName().equals("login")) {
            // set user and password
            htmlForm.putInput("tbUserName", "ifao");
            htmlForm.putInput("tbPassword", "ifao");
            sResponse = urlConnection.getContent(htmlForm);
         }
      }
      return sResponse;
   }

   /** 
    * TODO (brod) add comment for method copySamples 
    * 
    * <p> TODO rename testUrl to psUrl, fTransactionBase to pTransactionBase
    * @param testUrl TODO (brod) add text for param testUrl
    * @param psBaseUrl TODO (brod) add text for param psBaseUrl
    * @param fTransactionBase TODO (brod) add text for param fTransactionBase
    * 
    * @author brod 
    */
   private static void copySamples(String testUrl, String psBaseUrl, File fTransactionBase)
   {
      File file = new File(fTransactionBase, testUrl);
      if (!file.exists()) {
         String loadUrl;
         try {
            loadUrl = loadUrl(psBaseUrl + testUrl);
            Utils.writeFile(file, loadUrl);
            System.out.println("url2file:" + file.getAbsolutePath());
            // get the links
            if (testUrl.endsWith(".htm")) {
               String sBaseUrl = psBaseUrl.substring(0, psBaseUrl.lastIndexOf("/"));
               sBaseUrl = sBaseUrl.substring(0, sBaseUrl.lastIndexOf("/") + 1);
               int iStart = loadUrl.indexOf("<a ");
               while (iStart > 0) {
                  int iEnd = loadUrl.indexOf(">", iStart);
                  if (iEnd > iStart) {
                     String sLine = loadUrl.substring(iStart, iEnd);
                     int iHref = sLine.toLowerCase().indexOf(" href=\"");
                     if (iHref > 0) {
                        iHref += 7;
                        String sHref =
                           sLine.substring(iHref, sLine.toLowerCase().indexOf("\"", iHref));
                        int iXmlSamples = sHref.indexOf("../../XML_Samples/");
                        if ((iXmlSamples >= 0) && sHref.endsWith(".xml")) {
                           iXmlSamples += 6;
                           copySamples(sHref.substring(iXmlSamples), sBaseUrl,
                                 fTransactionBase.getParentFile());
                        }
                     }
                  }
                  iStart = loadUrl.indexOf("<a ", iStart + 1);
               }
            }
         }
         catch (IOException e) {
            // make nothing
         }
      }
   }

   /** 
    * TODO (brod) add comment for method addToChoice 
    * 
    * <p> TODO rename choice to pChoice
    * @param choice TODO (brod) add text for param choice
    * @param psType TODO (brod) add text for param psType
    * @param psReqRes TODO (brod) add text for param psReqRes
    * 
    * @author brod 
    */
   private static void addToChoice(XmlObject choice, String psType, String psReqRes)
   {
      String sType0 = psType.substring(0, psType.indexOf("_") + 1);
      // try for find such a choice
      XmlObject found = null;
      XmlObject[] objects = choice.getObjects("");
      for (XmlObject object : objects) {
         if (object.getAttribute("name").startsWith(sType0)) {
            found = object;
         }
      }
      if (found == null) {
         found = choice.createObject("xs:element", "name", psType, true);
      } else {
         found.setAttribute("name", psType);
      }
      // create a choice object
      found.setAttribute("type", psType + psReqRes);

   }
}
