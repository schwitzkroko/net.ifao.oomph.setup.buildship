package schemagenerator.actions.amadeus;


import ifaoplugin.*;

import java.io.*;
import java.util.*;

import schemagenerator.actions.amadeus.gui.ImportAmadeusGuiMain;

import net.ifao.util.CorrectDatabindingXsd;
import net.ifao.xml.XmlObject;


/**
 * Additional helper class AmadeusHelpToXml to convert the
 * Amadeus help files to xml schema files
 *
 * <p>
 * Copyright &copy; 2010, i:FAO
 *
 * @author brod
 */
public class AmadeusHelpToXml
{
   private File _amadeusCacheDirectory;
   private ImportAmadeusGuiMain _gui;
   PrintStream _logOutput;

   /**
    * Constructor AmadeusHelpToXml
    *
    * @param pGui Gui
    * @param pLogOutput LogOutput stream
    * @param pAmadeusCacheDirectory AmadeusCache Directory
    *
    * @author brod
    */
   public AmadeusHelpToXml(ImportAmadeusGuiMain pGui, PrintStream pLogOutput,
                           File pAmadeusCacheDirectory)
   {
      _gui = pGui;
      _logOutput = pLogOutput;
      _amadeusCacheDirectory = pAmadeusCacheDirectory;
   }

   /**
    * This method adds objects ToDataBinding
    *
    * @param phtDatabinding Hashtable for Databinding
    * @param psElementName ElementName
    * @param psParent Parent name
    * @param psPath Path
    *
    * @author brod
    */
   private void addObjectToDataBinding(Hashtable<String, Hashtable<String, String>> phtDatabinding,
                                       String psElementName, String psParent, String psPath)
   {
      String sLowerCase = Util.camelCase(psElementName).toLowerCase();
      // add this element to the list of paths
      Hashtable<String, String> htOfThisElement = phtDatabinding.get(sLowerCase);
      if (htOfThisElement == null) {
         htOfThisElement = new Hashtable<String, String>();
         phtDatabinding.put(sLowerCase, htOfThisElement);
      }
      String sKey = Util.camelCase(psParent) + Util.camelCase(psElementName);
      String sUniqueKey;
      int iCounter = 1;
      while (htOfThisElement.get(sUniqueKey = sKey + (iCounter > 1 ? iCounter : "")) != null) {
         iCounter++;
      }

      htOfThisElement.put(sUniqueKey, psPath);
   }

   /**
    * this method converts to databinding.xml
    *
    * @param pXmlDataBinding xmlDataBinding
    * @param phtDatabinding Hashtable for Databinding
    *
    * @author brod
    */
   private void convertToDataBinding(XmlObject pXmlDataBinding,
                                     Hashtable<String, Hashtable<String, String>> phtDatabinding)
   {
      String[] arrKeys = phtDatabinding.keySet().toArray(new String[0]);
      for (String sKey : arrKeys) {
         Hashtable<String, String> htOfElements = phtDatabinding.get(sKey);
         if (htOfElements.size() > 1) {
            pXmlDataBinding.addComment(sKey);
            String[] arrClasses = htOfElements.keySet().toArray(new String[0]);
            Arrays.sort(arrClasses);
            for (String arrClasse : arrClasses) {
               XmlObject elementBinding =
                  pXmlDataBinding.createObject("elementBinding", "name",
                        htOfElements.get(arrClasse), true);
               elementBinding.createObject("java-class").setAttribute("name", arrClasse);
            }
         }
      }
   }

   /**
    * Private method convertToSchema
    *
    * @param pXmlTree xmlTree
    * @param pbSetAttributes if true set the Attributes
    * @param psPath Path
    * @param psParent Parent element
    * @param phtDatabinding Hashtable for Databinding
    * @return new XmlObject (xs:element)
    *
    * @author brod
    */
   private XmlObject convertToSchema(XmlObject pXmlTree, boolean pbSetAttributes, String psPath,
                                     String psParent,
                                     Hashtable<String, Hashtable<String, String>> phtDatabinding)
   {
      XmlObject element = new XmlObject("<xs:element />").getFirstObject();
      String sElementName = pXmlTree.getName();

      element.setAttribute("name", sElementName);
      // XmlObject documentation = element.createObject("xs:annotation").createObject("xs:documentation");
      String sPath = psPath + sElementName;

      XmlObject sequence = null;
      XmlObject[] objects = pXmlTree.getObjects("");
      for (XmlObject object : objects) {
         String sName = object.getName();
         if (sName.equals("_doc")) {
            // ignore this entry because this is documentation
            //   if (documentation != null) {
            //      documentation.setCData(objects[i].getCData().replaceAll("[\n\r]+", " "));
            //   }
         } else {
            if (sequence == null) {
               // create a sequence
               sequence = element.createObject("xs:complexType").createObject("xs:sequence");

               addObjectToDataBinding(phtDatabinding, sElementName, psParent, sPath);

            }
            sequence.addObject(convertToSchema(object, true, sPath + "/", sElementName,
                  phtDatabinding));
         }
      }

      if (pbSetAttributes) {
         // set minOccurs
         String sMinOccurs;
         if (pXmlTree.getAttribute("statusrequirementdesignator").equalsIgnoreCase("Mandatory")) {
            sMinOccurs = "1";
         } else {
            sMinOccurs = pXmlTree.getAttribute("minOccurs");
            if (sMinOccurs.length() == 0) {
               sMinOccurs = "0";
            }
         }
         element.setAttribute("minOccurs", sMinOccurs);

         // set maxOccurs
         String sMaxOccurs = pXmlTree.getAttribute("repetition");
         if (sMaxOccurs.length() == 0) {
            // if there is no specific entry
            if (pXmlTree.getAttribute("href").length() == 0) {
               // make a list, because we don't know how often this occurs
               sMaxOccurs = "999";
            } else if (sequence != null) {
               // because this is a node, this may occur multiple times
               sMaxOccurs = "99";
            } else {
               // set the default to 1
               sMaxOccurs = "999";
            }
         }
         element.setAttribute("maxOccurs", sMaxOccurs);

         // set the type
         if (sequence == null) {
            // there are no subobjects
            element.setAttribute("type", "xs:string");
         }
      }
      return element;
   }

   /**
    * private method to correct links within html pPage
    *
    * @param sHtmlPage HtmlPage
    * @param psHtmlFileName HtmlFileName
    * @return corrected html page
    *
    * @author brod
    */
   private String correctLinksWithinHtmlPage(String psHtmlPage, String psHtmlFileName)
   {
      boolean bCorrected = false;
      HashSet<String> hsNames = new HashSet<String>();
      HashSet<String> hsMultiple = new HashSet<String>();
      String sLower = psHtmlPage.toLowerCase();
      String sHtmlPage = psHtmlPage;
      int iPos = sLower.indexOf("<a name=");
      while (iPos >= 0) {
         iPos += 8;
         int iEnd = sLower.indexOf(">", iPos);
         if (iEnd > iPos) {
            String sName = sHtmlPage.substring(iPos, iEnd).trim();
            if (sName.startsWith("\"") || sName.startsWith("\'") && sName.length() > 2) {
               sName = sName.substring(1, sName.length() - 1);
               iEnd--;
            }
            String sAdd = "";
            int iAdd = 1;
            while (!hsNames.add(sName + sAdd)) {
               // already existing
               iAdd++;
               sAdd = Integer.toString(iAdd);
            }
            if (iAdd > 1) {
               hsMultiple.add(sName);
               sLower = sLower.substring(0, iEnd) + iAdd + sLower.substring(iEnd);
               sHtmlPage = sHtmlPage.substring(0, iEnd) + iAdd + sHtmlPage.substring(iEnd);
            }
         }
         iPos = sLower.indexOf("<a name=", iPos);
      }
      Hashtable<String, Integer> htCounter = new Hashtable<String, Integer>();

      if (hsMultiple.size() > 0) {
         // correct the multiples
         iPos = sLower.indexOf("<a href=");
         while (iPos >= 0) {
            iPos += 8;
            int iEnd = sLower.indexOf(">", iPos);
            if (iEnd > iPos) {
               String sName = sHtmlPage.substring(iPos, iEnd).trim();
               if (sName.startsWith("\"") || sName.startsWith("\'") && sName.length() > 2) {
                  sName = sName.substring(1, sName.length() - 1);
                  iEnd--;
               }
               if (sName.startsWith("#")) {
                  sName = sName.substring(1);
                  if (hsMultiple.contains(sName)) {
                     Integer iAdd = htCounter.get(sName);
                     if (iAdd == null) {
                        iAdd = 1;
                     } else {
                        iAdd = iAdd + 1;
                        if (hsNames.contains(sName + iAdd)) {
                           // correct the link
                           sLower = sLower.substring(0, iEnd) + iAdd + sLower.substring(iEnd);
                           sHtmlPage =
                              sHtmlPage.substring(0, iEnd) + iAdd + sHtmlPage.substring(iEnd);
                           bCorrected = true;
                        }
                     }
                     htCounter.put(sName, iAdd);
                  }
               }
            }
            iPos = sLower.indexOf("<a href=", iPos);
         }
      }
      if (bCorrected) {
         _logOutput.println("Corrected " + psHtmlFileName);
      }
      return sHtmlPage;
   }

   /**
    * private method to get a Directory
    *
    * @param psTransactionUrl TransactionUrl
    * @return directory name (which matched to the TransactionUrl)
    *
    * @author brod
    */
   private String getDirectory(String psTransactionUrl)
   {
      String sDirectory = psTransactionUrl.substring(psTransactionUrl.lastIndexOf("/") + 1);
      // truncate the htm suffix
      sDirectory = sDirectory.substring(0, sDirectory.lastIndexOf("."));
      // create a subdirectory
      sDirectory = sDirectory.replaceFirst("_", "/");
      // set the final directory
      sDirectory = "XML/" + sDirectory.toLowerCase();
      return sDirectory;
   }

   /**
    * private method loadInterface
    *
    * @param psInterfaceUrl InterfaceUrl
    *
    * @author brod
    */
   private void loadInterface(String psInterfaceUrl)
   {

      Hashtable<String, String> htFileBuffer = new Hashtable<String, String>();
      String sPage =
         new AmadeusFile(_amadeusCacheDirectory, psInterfaceUrl, false).read(htFileBuffer,
               _logOutput);
      // get the links
      List<String> lstHrefs = AmadeusUtils.getTags(sPage, "HREF", ".htm");
      for (int i = 0; i < lstHrefs.size(); i++) {
         String sRelativePath = AmadeusUtils.getRelativePath(psInterfaceUrl, lstHrefs.get(i));

         // if data.xsd already exists return
         File dataXsd =
            new AmadeusFile(_amadeusCacheDirectory, getDirectory(sRelativePath), true)
                  .getFile("data.xsd");
         if (dataXsd.exists()) {
            continue;
         }

         loadTransactionToXml(psInterfaceUrl, sRelativePath, htFileBuffer);
         _logOutput.println("\nfinished read " + sRelativePath);
      }
   }

   /**
    * This is the main method to load the Interfaces
    *
    * @param phtInterfacePages Hastable of interfacePages (which have
    * to be loaded)
    *
    * @author brod
    */
   void loadInterfaces(Hashtable<String, List<String>> phtInterfacePages)
   {
      String[] arrInterfaces = phtInterfacePages.keySet().toArray(new String[0]);
      for (int i = 0; i < arrInterfaces.length; i++) {
         _gui.readInterface(i, arrInterfaces.length, arrInterfaces[i]);
         if (_gui.isStopped()) {
            _gui.readTransaction(0, 0, "");
            continue;
         }

         List<String> lstInterfaceUrl = phtInterfacePages.get(arrInterfaces[i]);
         // lstInterfaceUrl = interfacePages.get("PoweredFare");
         for (int j = 0; j < lstInterfaceUrl.size(); j++) {
            String sInterfaceUrl = lstInterfaceUrl.get(j);
            _gui.readTransaction(j, lstInterfaceUrl.size(), sInterfaceUrl);
            if (_gui.isStopped()) {
               continue;
            }
            loadInterface(sInterfaceUrl);
         }
         _gui.readTransaction(lstInterfaceUrl.size(), lstInterfaceUrl.size(), "");
      }
      _gui.readInterface(arrInterfaces.length, arrInterfaces.length, "");

   }

   /**
    * private method loadTransactionToXml
    *
    * @param psInterfaceUrl InterfaceUrl
    * @param psTransactionUrl TransactionUrl
    * @param pHashTable Hashtable
    *
    * @author brod
    */
   private void loadTransactionToXml(String psInterfaceUrl, String psTransactionUrl,
                                     Hashtable<String, String> pHashTable)
   {

      // read the tree and the model
      String sHtmlPage = readCorrectedHtml(psTransactionUrl, pHashTable);

      Hashtable<String, StringBuilder> htStringBuilder = new Hashtable<String, StringBuilder>();
      loadTreeAndModel(sHtmlPage, htStringBuilder);

      // get the xml tree
      StringBuilder sbXmlTree = htStringBuilder.get("tree");
      if (sbXmlTree != null && sbXmlTree.length() > 0) {
         Hashtable<String, Hashtable<String, String>> htDatabinding =
            new Hashtable<String, Hashtable<String, String>>();

         XmlObject xmlTree = new XmlObject(sbXmlTree.toString()).getFirstObject();
         loadTree(psTransactionUrl, sHtmlPage, xmlTree, pHashTable);
         String sDirectory = getDirectory(psTransactionUrl);
         writeFile(sDirectory + "/XmlTree.xml", xmlTree.toString());

         // write the data.xsd file
         XmlObject xmlSchema =
            new XmlObject("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
                  + "elementFormDefault=\"qualified\"/>").getFirstObject();
         XmlObject firstSchemaElement = convertToSchema(xmlTree, false, "/", "", htDatabinding);
         xmlSchema.addObject(firstSchemaElement);
         String sDataXsd =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                  + "<!-- Refer: http://api.dev.amadeus.net/" + psInterfaceUrl + "\n"
                  + "http://api.dev.amadeus.net/" + psTransactionUrl + " -->" + "\n"
                  + xmlSchema.toString() + "\n";

         writeFile(sDirectory + "/data.xsd", sDataXsd);

         // create the dataBinding
         XmlObject xmlDataBinding =
            new XmlObject("<binding xmlns=\"http://www.castor.org/SourceGenerator/Binding\" "
                  + "defaultBindingType=\"element\"/>").getFirstObject();
         convertToDataBinding(xmlDataBinding, htDatabinding);

         // if there are subobjects
         if (xmlDataBinding.getObjects("").length > 0) {
            // write the databinding file
            writeFile(sDirectory + "/databinding.xml", xmlDataBinding.toString());

            // correct the DataBinding
            CorrectDatabindingXsd.correctDataBinding(new AmadeusFile(_amadeusCacheDirectory,
                  sDirectory, true).getFile("dataBinding.xml"), "", _logOutput);
         }

         // get the xml model
         StringBuilder sbXmlModel = htStringBuilder.get("model");
         if (sbXmlModel != null && sbXmlModel.length() > 0) {
            try {
               writeFile(sDirectory + "/XmlModel.xml", new XmlObject(sbXmlModel.toString())
                     .getFirstObject().toString());
            }
            catch (Exception ex) {
               // invalid xml model
            }
         }
      }
   }

   /**
    * private  method loadTree
    *
    * @param psTransactionUrl TransactionUrl
    * @param psHtmlPage HtmlPage
    * @param pXmlTree xmlTree
    * @param pHashtable Hashtable (with collected results)
    *
    * @author brod
    */
   private void loadTree(String psTransactionUrl, String psHtmlPage, XmlObject pXmlTree,
                         Hashtable<String, String> pHashtable)
   {
      if (pXmlTree == null) {
         return;
      }
      // read the subobjects
      XmlObject[] objects = pXmlTree.getObjects("");
      for (XmlObject object : objects) {
         loadTree(psTransactionUrl, psHtmlPage, object, pHashtable);
      }

      String sHref = pXmlTree.getAttribute("href");
      if (sHref.length() > 0) {

         if (!sHref.contains("#")) {

            String relativePath = AmadeusUtils.getRelativePath(psTransactionUrl, sHref);
            String sHtmlPage = readCorrectedHtml(relativePath, pHashtable);
            Hashtable<String, StringBuilder> htStringBuilder =
               new Hashtable<String, StringBuilder>();
            // read the tree and the model
            loadTreeAndModel(sHtmlPage, htStringBuilder);

            setDocForXmlObject(pXmlTree, htStringBuilder.get("desc").toString());

            // if the xmlTree is filled
            StringBuilder sbTree = htStringBuilder.get("tree");
            if (sbTree != null && sbTree.length() > 0) {
               XmlObject[] xmlObjects =
                  new XmlObject(sbTree.toString()).getFirstObject().getObjects("");
               for (XmlObject xmlObject : xmlObjects) {
                  loadTree(relativePath, sHtmlPage, xmlObject, pHashtable);
                  pXmlTree.addObject(xmlObject);
               }
            } else {
               System.err.println("!!! Invalid tree found at " + relativePath);
            }
         } else {
            String sHtmlPage;
            if (sHref.startsWith("#")) {
               sHtmlPage = psHtmlPage;
            } else {
               String relativePath =
                  AmadeusUtils.getRelativePath(psTransactionUrl,
                        sHref.substring(0, sHref.lastIndexOf("#")));
               sHtmlPage = readCorrectedHtml(relativePath, pHashtable);
            }
            String sAnc = sHref.substring(sHref.lastIndexOf("#") + 1);
            // get the content form the html entry
            String sHtmlLowerPage = sHtmlPage.toLowerCase();
            int iPos = sHtmlLowerPage.indexOf("<a name=" + sAnc.toLowerCase() + ">");
            if (iPos < 0) {
               iPos = sHtmlLowerPage.indexOf("<a name=\"" + sAnc.toLowerCase() + "\"");
            }
            if (iPos > 0) {
               int iEnd = sHtmlLowerPage.indexOf("</tr", iPos);
               if (iEnd > 0) {
                  String sSubstring = sHtmlPage.substring(iPos, iEnd);
                  setDocForXmlObject(pXmlTree, sSubstring);
               }
            }

         }
      }

   }

   /**
    * private method loadTreeAndModel
    *
    * @param psHtmlPage HtmlPage
    * @param phtStringBuilder Hashtable with StringBuilders (of this page)
    *
    * @author brod
    */
   private void loadTreeAndModel(String psHtmlPage,
                                 Hashtable<String, StringBuilder> phtStringBuilder)
   {
      boolean bOn = false;
      boolean bXml = true;
      StringBuilder sb = null;
      phtStringBuilder.put("tree", new StringBuilder());
      phtStringBuilder.put("model", new StringBuilder());
      phtStringBuilder.put("desc", new StringBuilder());

      List<String> lstLines = AmadeusUtils.getLines(psHtmlPage);
      for (int i = 0; i < lstLines.size(); i++) {
         String sLine = lstLines.get(i);
         String sLineLower = sLine.toLowerCase();
         if (sb == null) {
            if (sLineLower.contains("xml model")) {
               sb = phtStringBuilder.get("model");
               bOn = false;
               bXml = true;
            } else if (sLineLower.startsWith("<span") && sLine.contains("Query : XML Tree")) {
               sb = phtStringBuilder.get("tree");
               bOn = false;
               bXml = true;
            } else if (!sLine.startsWith("<!--")) {
               // ignore non comment lines
            } else if (sLineLower.contains("xml tree")) {
               sb = phtStringBuilder.get("tree");
               bOn = false;
               bXml = true;
            } else if (sLineLower.contains("descrip")) {
               sb = phtStringBuilder.get("desc");
               bOn = false;
               bXml = false;
            }
         } else if (bXml) {
            if (sLine.contains("&lt;")) {
               bOn = true;
               sb.append(AmadeusUtils.extractXml(sLine));
            }
            if (bOn) {
               // if there is a new table
               if (sLineLower.contains("<table") || sLineLower.contains("</table")) {
                  // terminate the xmlTree
                  sb = null;
               }
            }
         } else {
            // non xml
            if (bOn) {
               if (sLine.startsWith("<!")) {
                  sb = null;
               } else {
                  sb.append(sLine + "\n");
               }
            } else {
               if (sLine.startsWith("<!")) {
                  bOn = true;
               }
            }
         }
      }
   }

   /**
    * private method to read Html files (which will be automatically corrected
    * to avoid inkorrect link attributes)
    *
    * @param psTransactionUrl TransactionUrl
    * @param pHashTable pHashTable
    * @return corrected html page
    *
    * @author brod
    */
   private String readCorrectedHtml(String psTransactionUrl, Hashtable<String, String> pHashTable)
   {
      AmadeusFile amadeusFile = new AmadeusFile(_amadeusCacheDirectory, psTransactionUrl, false);

      String sHtmlPage = amadeusFile.read(pHashTable, _logOutput);

      // correct links within html Page
      sHtmlPage = correctLinksWithinHtmlPage(sHtmlPage, psTransactionUrl);

      return sHtmlPage;
   }

   /**
    * this method sets the Doc objects for XmlObject
    *
    * @param pXmlTree xmlTree
    * @param psHtmlCode HtmlCode
    *
    * @author brod
    */
   private void setDocForXmlObject(XmlObject pXmlTree, String psHtmlCode)
   {
      String sExtractedHtml = AmadeusUtils.extractHtml(psHtmlCode);
      if (sExtractedHtml.startsWith("Description")) {
         sExtractedHtml = sExtractedHtml.substring(11).trim();
      }
      pXmlTree.addObject(new XmlObject("<_doc>" + sExtractedHtml.replaceAll("[\n\r]+", " ").trim()
            + "</_doc>").getFirstObject());
      // validate icons
      if (psHtmlCode.contains("/Images/tick.gif")) {
         pXmlTree.setAttribute("minOccurs", "1");
      } else {
         pXmlTree.setAttribute("minOccurs", "0");
      }
      // analyse the parameters
      StringTokenizer st = new StringTokenizer(sExtractedHtml, ";,.\n");
      while (st.hasMoreTokens()) {
         String sToken = st.nextToken().replace(" ", "");
         if (sToken.equals("AlphaNumeric") || sToken.equals("Numeric")
               || sToken.equals("Alphabetic")) {
            sToken = "type=" + sToken;
         }
         if (sToken.contains("=")) {
            pXmlTree.setAttribute(sToken.substring(0, sToken.indexOf("=")).replaceAll("[()]+", "")
                  .toLowerCase(), sToken.substring(sToken.indexOf("=") + 1));
         }
      }

   }

   /**
    * This method writes a File
    *
    * @param psFileName FileName
    * @param psText Text to write
    *
    * @author brod
    */
   private void writeFile(String psFileName, String psText)
   {
      AmadeusFile amadeusFile = new AmadeusFile(_amadeusCacheDirectory, psFileName, true);
      amadeusFile.write(psText.getBytes());
      _logOutput.println(" finished write " + amadeusFile.getFile().getAbsolutePath() + " ("
            + psText.length() + " bytes)");

   }

}
