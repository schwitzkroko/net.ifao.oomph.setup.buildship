package schemagenerator.actions.amadeus;


import java.io.*;
import java.util.*;

import schemagenerator.actions.amadeus.gui.ImportAmadeusGuiMain;


/** 
 * This is a helper class to load AmadeusHelp files from the 
 * web to the file system. 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author brod 
 */
public class AmadeusHelpToFile
{
   static final String AMADEUSBASE = "api/Development/Static/devdoc_index.htm";

   private File _amadeusCacheDirectory;
   private ImportAmadeusGuiMain _gui;
   private PrintStream _logOutput;

   /** 
    * Constructor AmadeusHelpToFile 
    * 
    * @param pAmadeusCacheDirectory AmadeusCacheDirectory 
    * @param pImportAmadeusGuiMain ImportAmadeusGuiMain 
    * @param pGui Gui object 
    * 
    * @author brod 
    */
   public AmadeusHelpToFile(File pAmadeusCacheDirectory, PrintStream pImportAmadeusGuiMain,
                            ImportAmadeusGuiMain pGui)
   {
      _amadeusCacheDirectory = pAmadeusCacheDirectory;
      _logOutput = pImportAmadeusGuiMain;
      _gui = pGui;
   }

   /** 
    * main method to read the Main HelpPage 
    * 
    * @param psProxy Amadeus Proxy number (for the help pages) 
    * @return Hashtable of help pages 
    * 
    * @author brod 
    */
   public Hashtable<String, List<String>> readMainHelpPage(String psProxy)
   {
      // get the amadeus StartPage
      String sStartPage = AMADEUSBASE;

      // get the links (for this proxy) from the startpage
      List<String> lstStartpageLinks =
         AmadeusUtils.getTags(new AmadeusFile(_amadeusCacheDirectory, sStartPage, false)
               .read(_logOutput), "src", "/" + psProxy + "/");
      _logOutput.println(" finished read " + sStartPage);
      // get a stringBuilder for the Index page
      StringBuilder sbIndexHtml = new StringBuilder();
      sbIndexHtml.append("<html><HEAD><TITLE>Amadeus Documentation " + psProxy + "</TITLE>"
            + "<link rel=\"stylesheet\" href=\"Stylesheets/API.css\" type=\"text/css\">"
            + "</HEAD>\n<body>\n");
      sbIndexHtml.append("<table cellspacing='0' cellpadding='0'>\n");

      // init variables
      Hashtable<String, List<String>> htInterface = new Hashtable<String, List<String>>();
      HashSet<String> hsAllLoadedPages = new HashSet<String>();
      String sLastInterface = "";
      String sLinkForIndexHtm = "";

      // loop over all links
      for (int i = 0; i < lstStartpageLinks.size(); i++) {
         // get the 'next' path for the javascripts
         String sJSLink = AmadeusUtils.getRelativePath(sStartPage, lstStartpageLinks.get(i));
         if (sJSLink.endsWith(".js")) {
            double dStart = i * 1.0 / lstStartpageLinks.size();
            _gui.readHtml(dStart, sJSLink);

            // get the js page
            String sJsPage =
               new AmadeusFile(_amadeusCacheDirectory, sJSLink, false).read(_logOutput);
            _logOutput.println(" finished read " + sJSLink);

            // get the Callpage links
            List<String> lstCallPageLinks =
               AmadeusUtils.getTags(sJsPage, "callPage(\\\\\\'", "\\\\\\'", "");
            Collections.sort(lstCallPageLinks);

            // loop over all call pages (which are accessable within the js page
            for (int j = 0; j < lstCallPageLinks.size(); j++) {
               // get the link
               String sCallPageLink = lstCallPageLinks.get(j);
               String sHtmLink = psProxy + "/XML/Transaction/" + sCallPageLink + ".htm";

               // get the transaction URL (for this proxy)
               String sTransactionUrl =
                  AmadeusUtils.getRelativePath(sStartPage, "../../" + sHtmLink);

               double dStart1 =
                  dStart + (j * 1.0 / lstCallPageLinks.size() / lstStartpageLinks.size());
               // if there is a page (and related subpages)  
               String sSubPage =
                  readSubHelpPage(sTransactionUrl, hsAllLoadedPages, dStart1, dStart1 + 1.0
                        / lstCallPageLinks.size() / lstStartpageLinks.size());
               if (sSubPage != null && sSubPage.length() > 0) {

                  // get the images
                  List<String> lstImages = AmadeusUtils.getTags(sSubPage, "SRC", ".gif");
                  String sImage = "<img src=\"Images/ftv2folderopen.gif\">";
                  // get the second image
                  if (lstImages.size() > 1) {
                     sImage = lstImages.get(1);
                     while (sImage.startsWith(".") || sImage.startsWith("/")) {
                        sImage = sImage.substring(1);
                     }
                     sImage = " <img src=\"" + sImage + "\" width=\"25\" height=\"25\" />";
                  }

                  // create a linktext for the index html page
                  String sLinkText;

                  // a CallPageLink looks e.g. like: "Air/Air_ModifyAirSegment.htm" 
                  if (sCallPageLink.indexOf("/") > 0) {
                     // get the Interface and the linktext is the name of the htm page
                     String sInterface = sCallPageLink.substring(0, sCallPageLink.indexOf("/"));
                     sLinkText = sCallPageLink.substring(sCallPageLink.indexOf("/") + 1);

                     // if there is a cnage within the Interfacetype
                     if (!sInterface.equals(sLastInterface)) {
                        // add the link entry
                        if (sLinkForIndexHtm.length() > 0) {
                           sbIndexHtml.append(sLinkForIndexHtm);
                        }
                        // set the lastInterface
                        sLastInterface = sInterface;
                        // ... and add an image and directory folder item to the index.htm page
                        sbIndexHtml.append("<tr><td colspan='2'>" + sImage + " <b>" + sInterface
                              + "</b></td></tr>");
                     } else {
                        // else (continuous list)
                        // ... add the add the link entry (but replace the node to a
                        // continues node) 
                        sbIndexHtml.append(sLinkForIndexHtm.replaceAll("ftv2lastnode", "ftv2node"));
                     }

                     // add the htmlLink to the hashtable (for result)
                     List<String> list = htInterface.get(sInterface);
                     if (list == null) {
                        list = new ArrayList<String>();
                        htInterface.put(sInterface, list);
                     }
                     list.add(sTransactionUrl);
                     // set the LinkForIndexHtm
                     sLinkForIndexHtm =
                        "<tr><td style='background-image:url(Images/ftv2lastnode.gif);width:20px'>&nbsp;</td><td>&nbsp;<a href=\""
                              + sHtmLink + "\">" + sLinkText + "</a></td></tr>\n";
                  } else {
                     // invalid link
                  }
               }
            }
         }
      }
      // finalise the Link List
      sbIndexHtml.append(sLinkForIndexHtm);

      // finalise the index.htm page
      sbIndexHtml.append("</table></body></html>");
      // read the icons
      new AmadeusFile(_amadeusCacheDirectory, "api/Images/ftv2folderopen.gif", false)
            .read(_logOutput);
      new AmadeusFile(_amadeusCacheDirectory, "api/Images/ftv2lastnode.gif", false)
            .read(_logOutput);
      new AmadeusFile(_amadeusCacheDirectory, "api/Images/ftv2node.gif", false).read(_logOutput);
      // and write the index.htm page
      new AmadeusFile(_amadeusCacheDirectory, "api/index.htm", false).write(sbIndexHtml.toString()
            .getBytes());
      _gui.readHtml(1, "");

      return htInterface;
   }

   /** 
    * private method to read sub HelpPages 
    * 
    * @param psTransactionUrl TransactionUrl 
    * @param phsAllLoadedPages Hashset of all loaded pages (so far) 
    * @param pdLocation double for Location slider 
    * @param pdMax Max values for Location slider 
    * @return String of the html page 
    * 
    * @author brod 
    */
   private String readSubHelpPage(String psTransactionUrl, HashSet<String> phsAllLoadedPages,
                                  double pdLocation, double pdMax)
   {
      if (phsAllLoadedPages.add(psTransactionUrl) && !_gui.isStopped()) {
         AmadeusFile amadeusFile = new AmadeusFile(_amadeusCacheDirectory, psTransactionUrl, false);
         boolean bAmadeusFileExists = amadeusFile.exists();
         String sHtmlPage = amadeusFile.read(_logOutput);
         _logOutput.println(" finished read " + psTransactionUrl);

         // if file does not exist ... read the sup pages
         if (!bAmadeusFileExists) {
            // get all tags for links ("src","href","background",...)
            List<String> linkTags = AmadeusUtils.getTags(sHtmlPage, "src", "");
            linkTags.addAll(AmadeusUtils.getTags(sHtmlPage, "href", ""));
            linkTags.addAll(AmadeusUtils.getTags(sHtmlPage, "background", ""));
            linkTags.addAll(AmadeusUtils.getTags(sHtmlPage, "SRC", ""));
            linkTags.addAll(AmadeusUtils.getTags(sHtmlPage, "HREF", ""));
            // loop over all 'links'
            for (int j = 0; j < linkTags.size(); j++) {
               String sLink = linkTags.get(j);
               if (!sLink.startsWith("#")) {
                  // if this is an anchor link
                  if (sLink.contains("#")) {
                     // truncate the ancor link
                     sLink = sLink.substring(0, sLink.lastIndexOf("#"));
                  }
                  // calculate the location and update of gui 
                  if (pdLocation >= 0) {
                     double dDelta = 1.0 * j / linkTags.size() * (pdMax - pdLocation);
                     _gui.readHtml(pdLocation + dDelta, sLink);
                  }
                  // read the sub page
                  readSubHelpPage(AmadeusUtils.getRelativePath(psTransactionUrl, sLink),
                        phsAllLoadedPages, -1, -1);
               }
            }
         }
         return sHtmlPage;
      }
      return "";
   }
}
