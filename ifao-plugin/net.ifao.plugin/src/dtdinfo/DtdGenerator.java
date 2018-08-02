package dtdinfo;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;

import dtdinfo.gui.DtdFrame;
import dtdinfo.gui.DtdSchemaChanged;
import ifaoplugin.Util;
import net.ifao.xml.XmlObject;


/**
 * Class DtdGenerator
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
public class DtdGenerator
{

   static String DONOTEDITTHISMETHOD = "// DO NOT EDIT THIS METHOD, BECAUSE IT WILL BE OVERWRITTEN";

   /**
    * Constructor DtdGenerator
    */
   public DtdGenerator()
   {}

   /**
    * Method main
    *
    * @param psArgs
    *
    * @author Andreas Brod
    */
   public static void main(String[] psArgs)
   {
      String sPath = (new File(".")).getAbsolutePath();
      try {
         sPath = "c:\\arctic\\Workspace\\arcticX";
         if (sPath.indexOf("\\tools\\") > 0) {
            sPath = sPath.substring(0, sPath.indexOf("\\tools\\"));
         }
         analyse(sPath, System.out, new HashSet<File>());
      }
      catch (Throwable th) {
         // just in case
         th.printStackTrace();
      }
      System.exit(0);
   }

   /**
    * Method exec
    *
    * @param psText
    *
    * @author Andreas Brod
    */
   private static void exec(String psText)
   {
      Util.exec(psText, false);
   }

   /**
    * Method validateXml
    * @param pFrame
    * @param psbLog
    * @param psBaseDir
    * @param psPackage
    * @param pArcticPnrElementInfos
    * @param psProvider
    * @param psUser
    * @param psAgent
    * @param phtElements
    * @return
    *
    * @author Andreas Brod
    * @param phsFiles
    */
   private static boolean validateXml(JFrame pFrame, StringBuffer psbLog, String psBaseDir, String psPackage,
                                      XmlObject pArcticPnrElementInfos, String psProvider, String psUser, String psAgent,
                                      Hashtable<String, List<String>> phtElements, Set<File> phsFiles)
   {
      System.out.println("---- validateXml ----");

      String sInfo = Util.loadFromFile("Generator\\ArcticPnrElementInfos.info");

      if (psBaseDir.endsWith("\\src\\")) {
         psBaseDir = psBaseDir.substring(0, psBaseDir.length() - 5);
      }

      psbLog.append("-------------------------------------\n");

      XmlObject xmlProvider =
         pArcticPnrElementInfos.createObject("Arctic").createObject("PnrElementInfos", "provider", psProvider, true);

      // validate if all classnames are set.

      for (String sKey : phtElements.keySet()) {
         XmlObject xmlElement = xmlProvider.createObject("PnrElementInfo", "type", sKey, false);

         if (xmlElement != null) {
            String sNewName = psPackage + ".framework.elements.Element" + getUnFormatedProvider(sKey);
            String sOldName = xmlElement.getAttribute("className");
            if (!sOldName.equalsIgnoreCase(sNewName)) {
               xmlElement.setAttribute("className", sNewName);
            }
         }
      }

      boolean bXsdChanged = false;
      boolean bXmlChanged = false;

      String sArcticPnrElementInfosXml = Util.getConfFile(psBaseDir, "ArcticPnrElementInfos.xml").getAbsolutePath();
      String sArcticPnrElementInfosXsd = Util.getConfFile(psBaseDir, "ArcticPnrElementInfos.xsd").getAbsolutePath();

      String sGeneratorInfos = "Generator\\ArcticPnrElementInfos.";
      Util.writeToFile(sGeneratorInfos + "xml", Util.loadFromFile(sArcticPnrElementInfosXml));

      String sXsd = Util.loadFromFile(sArcticPnrElementInfosXsd);

      Util.writeToFile(sGeneratorInfos + "xsd", sXsd);

      String sNewProvider = xmlProvider.toString();

      psbLog.append("Validate ArcticPnrElementInfos.xml for " + psProvider + "\n");

      if (sInfo.indexOf(sNewProvider) < 0) {
         psbLog.append("... changes found\n");

         String sArcticPnrElementInfos = Util.loadFromFile(sArcticPnrElementInfosXml);

         try {
            int iProviderStartPos = sArcticPnrElementInfos.indexOf("<PnrElementInfos provider=\"" + psProvider + "\"");

            if (iProviderStartPos >= 0) {
               String sOrig = sArcticPnrElementInfos.substring(iProviderStartPos);

               sOrig = sOrig.substring(0, sOrig.indexOf("</PnrElementInfos>") + 18);

               sArcticPnrElementInfos = Util.replaceString(sArcticPnrElementInfos, sOrig, sNewProvider);
            } else {
               StringBuffer sbHelper = new StringBuffer(sArcticPnrElementInfos);
               int iInsertPos = sbHelper.lastIndexOf("</PnrElementInfos>") + 18;

               sbHelper.insert(iInsertPos, "\n" + sNewProvider);

               sArcticPnrElementInfos = sbHelper.toString();
            }

            Util.writeToFile(sGeneratorInfos + "xml", sArcticPnrElementInfos);

            bXmlChanged = true;

            psbLog.append("... replaced " + sArcticPnrElementInfosXml + "\n\n");

            // validate XSD
            psbLog.append("Validate ArcticPnrElementInfos.xsd for invalid/new entries\n");

            // get the Elements
            String sPnrEnumPnrElementType = sXsd.substring(sXsd.indexOf("xs:simpleType name=\"PnrEnumPnrElementType\""));

            sPnrEnumPnrElementType = sPnrEnumPnrElementType.substring(0, sPnrEnumPnrElementType.indexOf("</xs:simpleType>"));

            List<String> lstElement = getTags(sPnrEnumPnrElementType, "xs:enumeration", "value");

            // get the parameters
            String sPnrEnumPnrElementParameterId =
               sXsd.substring(sXsd.indexOf("xs:simpleType name=\"PnrEnumPnrElementParameterId\""));

            sPnrEnumPnrElementParameterId =
               sPnrEnumPnrElementParameterId.substring(0, sPnrEnumPnrElementParameterId.indexOf("</xs:simpleType>"));

            List<String> lstParams = getTags(sPnrEnumPnrElementParameterId, "xs:enumeration", "value");

            // get all known elements from xml
            List<String> lstElementXml = getTags(sArcticPnrElementInfos, "PnrElementInfo", "type");

            List<String> lstParamsXml = getTags(sArcticPnrElementInfos, "PnrElementParamInfo", "id");

            boolean bChanged = false;

            // compare elements

            if (correct(psbLog, "PnrEnumPnrElementType", lstElement, lstElementXml)) {
               bChanged = true;
            }

            if (correct(psbLog, "PnrEnumPnrElementParameterId", lstParams, lstParamsXml)) {
               bChanged = true;
            }

            // if changed modify xsd
            if (bChanged) {
               sXsd = Util.replaceString(sXsd, sPnrEnumPnrElementType, getEnumList("PnrEnumPnrElementType", lstElement));
               sXsd =
                  Util.replaceString(sXsd, sPnrEnumPnrElementParameterId, getEnumList("PnrEnumPnrElementParameterId", lstParams));

               sXsd = addComment(sXsd, psUser + " GeneratorChanges for " + psProvider + "." + psAgent);

               Util.writeToFile(sGeneratorInfos + "xsd", sXsd);

               bXsdChanged = true;
            } else {
               psbLog.append("... no changes found\n");
            }

         }
         catch (Exception ex) {
            psbLog.append("!!! Exception " + ex.getMessage() + " !!!\n");
            ex.printStackTrace();
         }
      } else {
         psbLog.append("... no changes found\n");
      }

      // validate if requested Agent is available in "xsd"
      // get the EnumTransformActionType Block
      int iSimpleType = sXsd.indexOf("<xs:simpleType name=\"EnumTransformActionType");
      if (iSimpleType > 0) {
         String sBlock = sXsd.substring(iSimpleType);

         sBlock = sBlock.substring(0, sBlock.indexOf("</xs:simpleType>"));

         String sUpperAgent = getCamelCaseName(psAgent);

         if (sBlock.indexOf("\"" + sUpperAgent + "\"") < 0) {
            psbLog.append("\nAdded EnumTransformActionType." + sUpperAgent + " in ArcticPnrElementInfos.xsd\n");

            // Agent
            sXsd = addComment(sXsd, psUser + " GeneratorChanges Added EnumTransformActionType." + sUpperAgent);

            String sBlock2 = sBlock.substring(0, sBlock.indexOf("<xs:enum")) + "<xs:enumeration value=\"" + sUpperAgent
                  + "\"/>\n   " + sBlock.substring(sBlock.indexOf("<xs:enum"));

            sXsd = Util.replaceString(sXsd, sBlock, sBlock2);

            Util.writeToFile(sGeneratorInfos + "xsd", sXsd);

            bXsdChanged = true;
         }
      }

      if (bXmlChanged || bXsdChanged) {
         DtdSchemaChanged dialog =
            new DtdSchemaChanged(pFrame, "Validate xml", sArcticPnrElementInfosXml, sArcticPnrElementInfosXsd, sGeneratorInfos);

         dialog.show(bXmlChanged, bXsdChanged);

         if (dialog.hasContinue()) {
            phsFiles.add(Util.writeToFile("Generator\\ArcticPnrElementInfos.info", sNewProvider));
            phsFiles.add(Util.writeToFile(sArcticPnrElementInfosXml, Util.loadFromFile(sGeneratorInfos + "xml")));
            phsFiles.add(Util.writeToFile(sArcticPnrElementInfosXsd, Util.loadFromFile(sGeneratorInfos + "xsd")));

         } else {

            return false;
         }
      }

      return true;
   }

   /**
    * Method addComment
    *
    *
    * @param psXsd
    * @param psComment
    * @return
    *
    * @author Andreas Brod
    */
   private static String addComment(String psXsd, String psComment)
   {

      // find the current version
      int iVersion = psXsd.indexOf("<xs:attribute name=\"version\"");
      String sVersion = psXsd.substring(iVersion, psXsd.indexOf(">", iVersion) + 1);
      String sVerDefault = sVersion.substring(sVersion.indexOf(" default=\"") + 10, sVersion.lastIndexOf("\""));
      String sNextVersion = getNextNumber(sVerDefault);

      psXsd = Util.replaceString(psXsd, sVersion,
            Util.replaceString(sVersion, " default=\"" + sVerDefault + "\"", " default=\"" + sNextVersion + "\""));

      iVersion = psXsd.indexOf("<xs:documentation>", iVersion);

      int iStart = psXsd.lastIndexOf("\n", iVersion) + 1;
      SimpleDateFormat sd = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
      String sDoc = psXsd.substring(iStart, iVersion + 18) + sNextVersion + " " + sd.format(new Date()) + " " + psComment
            + "</xs:documentation>\n";

      psXsd = psXsd.substring(0, iStart) + sDoc + psXsd.substring(iStart);

      return psXsd;
   }

   /**
    * Method getNextNumber
    *
    *
    * @param psNumber
    * @return
    *
    * @author Andreas Brod
    */
   private static String getNextNumber(String psNumber)
   {
      String sNum = "";

      while (psNumber.indexOf(".") >= 0) {
         sNum += psNumber.substring(0, psNumber.indexOf(".") + 1);
         psNumber = psNumber.substring(psNumber.indexOf(".") + 1);
      }

      sNum += (Integer.parseInt(psNumber) + 1);

      return sNum;
   }

   /**
    * Method getEnumList
    *
    *
    * @param psName
    * @param plstValues
    * @return
    *
    * @author Andreas Brod
    */
   private static String getEnumList(String psName, List<String> plstValues)
   {
      String sRet = "xs:simpleType name=\"" + psName + "\">\n";

      sRet += "  <xs:restriction base=\"xs:NMTOKEN\">\n";

      for (int i = 0; i < plstValues.size(); i++) {
         sRet += "   <xs:enumeration value=\"" + plstValues.get(i) + "\"/>\n";
      }

      sRet += "  </xs:restriction>\n ";

      return sRet;
   }

   /**
    * Method correct
    *
    *
    * @param psbLog
    * @param psType
    * @param plstOrg
    * @param plstNew
    * @return
    *
    * @author Andreas Brod
    */
   private static boolean correct(StringBuffer psbLog, String psType, List<String> plstOrg, List<String> plstNew)
   {
      boolean bChanged = false;

      String sDeleted = "";
      String sAdded = "";

      // delete unused element
      for (int i = 0; i < plstOrg.size();) {
         if (plstNew.contains(plstOrg.get(i))) {
            i++;
         } else {
            if (sDeleted.length() > 0) {
               sDeleted += ", ";
            }

            sDeleted += plstOrg.get(i);

            plstOrg.remove(i);

            bChanged = true;
         }
      }

      // add new elements
      for (int i = 0; i < plstNew.size(); i++) {
         if (!plstOrg.contains(plstNew.get(i))) {
            if (sAdded.length() > 0) {
               sAdded += ", ";
            }

            sAdded += plstNew.get(i).toString();

            plstOrg.add(plstNew.get(i));

            bChanged = true;
         }
      }

      if ((sDeleted + sAdded).length() > 0) {
         psbLog.append("" + psType + "\n");

      }

      if (sDeleted.length() > 0) {
         psbLog.append("... delete " + sDeleted + "\n");
      }

      if (sAdded.length() > 0) {
         psbLog.append("... added " + sAdded + "\n");
      }

      return bChanged;
   }

   /**
    * Method getTags
    *
    *
    * @param psList
    * @param psTag
    * @param psAttr
    * @return
    *
    * @author Andreas Brod
    */
   private static List<String> getTags(String psList, String psTag, String psAttr)
   {
      int iPos = psList.indexOf("<" + psTag + " ");

      psAttr = " " + psAttr + "=\"";

      Vector<String> hs = new Vector<>();

      while (iPos > 0) {
         int iEnd = psList.indexOf(">", iPos);
         String sValue = psList.substring(iPos, iEnd);
         int iVal = sValue.indexOf(psAttr);

         if (iVal > 0) {
            iVal += psAttr.length();
            sValue = sValue.substring(iVal, sValue.indexOf("\"", iVal));

            if (!hs.contains(sValue)) {
               hs.add(sValue);
            }
         }

         iPos = psList.indexOf("<" + psTag + " ", iPos + 1);
      }

      return hs;
   }

   /**
    * Method analyse
    *
    * @param psBaseDirectory
    *
    * @author Andreas Brod
    * @param pOut
    * @param phsFiles
    */
   public static void analyse(String psBaseDirectory, PrintStream pOut, Set<File> phsFiles)
   {
      String sAgents = Util.loadFromFile(Util.getConfFile(psBaseDirectory, "Agents.xml"));
      DtdData dtdData = new DtdData(null, false);

      dtdData.setPath(psBaseDirectory);
      Hashtable<String, String> pSourceCodes = new Hashtable<>();
      analyse(dtdData, psBaseDirectory, sAgents, DtdData.SRCPATH, pSourceCodes, pOut, phsFiles);
   }

   /**
    * Method analyse
    *
    * @param pDtdData
    * @param psBaseDirectory
    * @param psAgents
    * @param psPath
    *
    * @author Andreas Brod
    * @param pSourceCodes
    * @param pOut
    * @param phsFiles
    */
   public static void analyse(DtdData pDtdData, String psBaseDirectory, String psAgents, String psPath,
                              Hashtable<String, String> pSourceCodes, PrintStream pOut, Set<File> phsFiles)
   {
      File f = new File(psBaseDirectory + psPath);

      if (f.isDirectory()) {
         System.out.println("Analyse directory " + f.getAbsolutePath());
         if (!f.getName().startsWith(".")) {
            File[] files = f.listFiles();

            for (File file : files) {
               analyse(pDtdData, psBaseDirectory, psAgents, psPath + "\\" + file.getName(), pSourceCodes, pOut, phsFiles);
            }
         }
      }

      if (f.getName().startsWith("TransformerHandler") && f.getName().endsWith(".java")) {
         String sName = f.getName().substring("TransformerHandler".length());

         File fBase = new File(f.getParentFile().getParentFile().getParentFile().toString() + File.separator + sName);

         if (!fBase.exists()) {
            System.out.println("Ignore: " + fBase.getAbsolutePath());
         } else {
            System.out.println("Start: " + fBase.getAbsolutePath());

            String psMethod = sName.substring(0, sName.indexOf("."));

            psPath = Util.replaceString(psPath, "\\", ".");

            if (psPath.indexOf(".framework") > 0) {
               psPath = psPath.substring(0, psPath.indexOf(".framework"));
            } else {
               psPath = psPath.substring(0, psPath.lastIndexOf("."));
            }

            String psProvider = getProvider(psAgents, psPath, psMethod);

            if (psProvider.length() == 0) {
               psProvider = getProvider(psAgents, psPath, "");
            }

            if (psProvider.length() > 0) {
               String psSourceList = DtdFrame.getSourceList(pDtdData, psBaseDirectory,
                     Util.replaceString(psPath, ".", "\\") + "\\" + psMethod, false, new StringBuffer(), null, phsFiles);

               /*
                * if (psBaseDirectory.indexOf("\\src")<0){
                * psBaseDirectory+=(psBaseDirectory.endsWith("\\")?"":"\\")+"src";
                *                }
                */
               String sPath = Util.replaceString(psPath, ".", "\\");

               if (sPath.indexOf("agents") > 0) {

                  // sPath = sPath.substring(sPath.indexOf("agents") + 7);
               }

               analyse(psBaseDirectory, sPath, psMethod, psSourceList, psProvider, pSourceCodes, pOut, phsFiles);
            } else {
               pOut.println("No Provider in Agents.xml for: " + psPath + "-" + psMethod + "-" + fBase.getAbsolutePath());

            }

         }
      }

   }

   /**
    * Method getProvider
    *
    * @param psAgents
    * @param psPath
    * @param psClass
    * @return
    *
    * @author Andreas Brod
    */
   private static String getProvider(String psAgents, String psPath, String psClass)
   {
      if (psPath.indexOf("ifao") > 0) {
         psPath = psPath.substring(psPath.indexOf("ifao"));
      }

      if (psPath.indexOf(".framework.") > 0) {
         psPath = psPath.substring(0, psPath.indexOf(".framework.") + 1);
      }

      if (!psPath.endsWith(".")) {
         psPath += ".";
      }

      int iPos = psAgents.indexOf(psPath + psClass);

      if (iPos > 0) {
         try {
            String sLine = psAgents.substring(psAgents.lastIndexOf("<", iPos), psAgents.indexOf("/>", iPos));
            Hashtable<String, StringBuffer> tags = getTags(sLine);
            StringBuffer sProviderType = tags.get("providerType");
            if ((sProviderType != null) && (sProviderType.length() > 0)) {
               return sProviderType.toString();
            }
            sLine = sLine.substring(sLine.lastIndexOf("=\"") + 2);
            sLine = sLine.substring(0, sLine.indexOf("\""));

            return sLine;
         }
         catch (Exception ex) {}
      }

      psPath = psPath.substring(0, psPath.length() - 1);

      if (psPath.indexOf(".") > 0) {
         return getProvider(psAgents, psPath.substring(0, psPath.lastIndexOf(".")), psClass);
      }

      return "";

   }

   /**
    * Method analyse
    *
    * @param psBaseDirectory
    * @param psPath
    * @param psMethod
    * @param psSourceList
    * @param psProvider
    *
    * @author Andreas Brod
    * @param pSourceCodes
    * @param pOut
    * @param phsFiles
    */
   private static void analyse(String psBaseDirectory, String psPath, String psMethod, String psSourceList, String psProvider,
                               Hashtable<String, String> pSourceCodes, PrintStream pOut, Set<File> phsFiles)
   {
      StringBuffer sbReturn = new StringBuffer();

      if (psBaseDirectory.endsWith("\\")) {
         psBaseDirectory = psBaseDirectory.substring(0, psBaseDirectory.length() - 1);
      }

      // ignore framework at the end
      if (psPath.endsWith("framework")) {
         psPath = psPath.substring(0, psPath.length() - 10);
      }

      if (psPath.startsWith("\\src\\")) {
         psPath = psPath.substring(5);
         psBaseDirectory += "\\src\\";
      }

      if (psPath.indexOf("\\agents\\") > 0) {
         psPath = psPath.substring(psPath.indexOf("\\agents\\") + 8);
      }

      // getBase Package
      String sPackage = Util.replaceString(psPath, "\\", ".");

      pOut.println("analyse " + psBaseDirectory + "..." + psProvider + "." + psMethod + "(" + sPackage + ")");

      writeFile(sbReturn, psBaseDirectory, loadDefaultClass(sbReturn, "TransformerHandler", sPackage, psProvider, psMethod, "",
            psSourceList, psBaseDirectory, pSourceCodes, phsFiles), phsFiles);

   }

   /**
    * Method generate
    *
    *
    * @param pFrame
    * @param psBaseDirectory
    * @param psPath
    * @param psProvider
    * @param psMethod
    * @param phtElements
    * @param pArcticPnrElementInfos
    * @param psUser
    * @param pbGenerateJavaFiles
    * @return
    *
    * @author Andreas Brod
    * @param pSourceCodes
    * @param phsFiles
    */
   public static String generate(DtdFrame pFrame, String psBaseDirectory, String psPath, String psProvider, String psMethod,
                                 Hashtable<String, List<String>> phtElements, XmlObject pArcticPnrElementInfos, String psUser,
                                 boolean pbGenerateJavaFiles, Hashtable<String, String> pSourceCodes, Set<File> phsFiles)
   {

      StringBuffer sbValidate = new StringBuffer();
      StringBuffer sbReturn = new StringBuffer();

      if (psBaseDirectory.endsWith("\\")) {
         psBaseDirectory = psBaseDirectory.substring(0, psBaseDirectory.length() - 1);
      }

      // ignore framework at the end
      if (psPath.endsWith("framework")) {
         psPath = psPath.substring(0, psPath.length() - 10);
      }

      if (psPath.startsWith("\\src\\")) {
         psPath = psPath.substring(5);
         psBaseDirectory += "\\src\\";
      }

      // getBase Package
      char[] cPackage = psPath.toCharArray();

      for (int i = 0; i < cPackage.length; i++) {
         if ((cPackage[i] == '\\') || (cPackage[i] == '/')) {
            cPackage[i] = '.';
         }
      }

      String sPackage = new String(cPackage);

      if (sPackage.startsWith("net.ifao.arctic.agents.")) {
         if (validateXml(pFrame, sbValidate, psBaseDirectory, sPackage, pArcticPnrElementInfos, psProvider, psUser, psMethod,
               phtElements, phsFiles)) {
            StringBuffer sb = new StringBuffer();
            String psSourceList = pFrame.save(true, sb, pFrame.getProviderElementInfo(), phsFiles);
            sbReturn.append(sb.toString() + "\n");
            sbReturn.append("-------------------------------------\n");

            if (pbGenerateJavaFiles) {
               sbReturn.append("PROVIDER: " + psProvider + "\n");
               sbReturn.append("AGENT: " + psMethod + "\n");
               sbReturn.append("PACKAGE: " + sPackage + "\n");
               sbReturn.append("-------------------------------------\n");

               sPackage = sPackage.substring(23);

               // TransformerHandler has to be created first
               String[] list = { "Communication", "AgentFramework", "BusinessRulesController", "Factory", "GdsAdapter",
                     "GdsRulesController", "ResponseReader", "BaseElement", "ErrorChecker", "Exception", "ErrorMapper",
                     "GdsRulesControllerTest", "AllTestsCommunication", "AllTests", "AllTestsRoot", "TransformerHandler" };

               for (String element : list) {
                  String sDirectory = psBaseDirectory;

                  if (element.endsWith("Test") || element.startsWith("AllTests")) {
                     sDirectory = sDirectory.substring(0, sDirectory.lastIndexOf("\\src") + 1) + "jUnitTest";
                  }

                  String sFile = loadDefaultClass(sbReturn, element, sPackage, psProvider, psMethod, "", psSourceList, sDirectory,
                        pSourceCodes, phsFiles);

                  writeFile(sbReturn, sDirectory, sFile, phsFiles);

               }

               // _PACKAGE_
               // _PROVIDER_
               // _Provider_

               for (String sKey : phtElements.keySet()) {
                  writeFile(sbReturn, psBaseDirectory, loadDefaultClass(sbReturn, "Element", sPackage, psProvider, psMethod, sKey,
                        "", psBaseDirectory, pSourceCodes, phsFiles), phsFiles);
               }

               // validate if all classes are generated
               String sInfo = Util.loadFromFile("Generator\\ArcticPnrElementInfos.info");
               int iStart = sInfo.indexOf(" className=\"");

               while (iStart > 0) {
                  iStart += 12;

                  int iEnd = sInfo.indexOf("\"", iStart);
                  String sClassName = sInfo.substring(iStart, iEnd);
                  String sKey = sClassName.substring(sClassName.lastIndexOf(".") + 1);

                  if ((sClassName.indexOf(sPackage) > 0) && sKey.startsWith("Element")) {
                     sKey = getCamelCaseName(sKey.substring(7));

                     // remove the toDos for the default Classes.
                     String sNewFile = removeToDos(loadDefaultClass(sbReturn, "Element", sPackage, psProvider, "", sKey, "",
                           psBaseDirectory, pSourceCodes, phsFiles));

                     // get the Parameters
                     iEnd = sInfo.indexOf("</PnrElementInfo>", iStart);
                     if (iEnd > iStart) {
                        int iStart0 = sInfo.lastIndexOf("<", iStart) + 1;
                        sNewFile = validateGetSetMethods(sNewFile, sInfo.substring(iStart0, iEnd));
                     }
                     writeFile(sbReturn, psBaseDirectory, sNewFile, phsFiles);
                  }

                  iStart = sInfo.indexOf(" className=\"", iStart);
               }

               // validate RuleMap
               validateRuleMap(sbReturn, psProvider, psBaseDirectory);

            }

         } else {

            // cancelled
            sbReturn.append("... Cancelled\n\n");
            sbReturn.append("changes not written !!!");

            sbValidate = new StringBuffer();
         }
      }

      return sbReturn.toString() + sbValidate.toString();
   }

   /**
    * method validateGetSetMethods
    *
    * @param psNewFile
    * @param psPnrElementInfo
    * @return
    *
    * @author brod
    */
   private static String validateGetSetMethods(String psNewFile, String psPnrElementInfo)
   {
      StringBuffer baseType = getTags(psPnrElementInfo.substring(0, psPnrElementInfo.indexOf(">"))).get("type");
      if (baseType == null) {
         baseType = new StringBuffer("___");
      } else {
         baseType = new StringBuffer(getUnFormatedProvider(baseType.toString()));
      }
      psNewFile = removeAutomaticallyGeneratedMethods(psNewFile);
      int iStart = psPnrElementInfo.indexOf("<PnrElementParamInfo ");
      while (iStart > 0) {
         String sPnrElementParamInfo = psPnrElementInfo.substring(iStart + 1, psPnrElementInfo.indexOf(">", iStart) + 1);
         if (!sPnrElementParamInfo.endsWith("/>")) {
            sPnrElementParamInfo =
               psPnrElementInfo.substring(iStart + 1, psPnrElementInfo.indexOf("</PnrElementParamInfo>", iStart));
         }
         Hashtable<String, StringBuffer> ht = getTags(sPnrElementParamInfo);
         String id = ht.get("id").toString();
         String type = ht.get("type").toString();
         String sDefault = ht.get("default") != null ? ht.get("default").toString() : "";
         String hint = ht.get("hint") != null ? ht.get("hint").toString() : "Method " + id;

         String id_low = getUnFormatedProvider(id);
         String type_low = getUnFormatedProvider(type);
         String type_low2 = "";
         String sHas = "";
         // boolean,Date,Duration,double,float,int
         if (type.startsWith("DATE_")) {
            type_low = "java.util.Date";
            type_low2 = "Date";
         } else if (type.startsWith("DURATION") || type.startsWith("TIME_")) {
            type_low = "org.exolab.castor.types.Duration";
            type_low2 = "Duration";
         } else if (type.startsWith("BOOLEAN")) {
            type_low = "boolean";
            type_low2 = "Boolean";
            sHas = "Boolean.parseBoolean";
         } else if (type.equals("NUMBER")) {
            type_low = "int";
            type_low2 = "Int";
            sHas = "Integer.parseInt";
         } else if (type.equals("ENUMERATION")) {
            type_low = "Enum" + id_low;
            type_low2 = "Enum";
         } else if (type.equals("AMOUNT") || type.equals("PERCENTAGE")) {
            type_low = "double";
            type_low2 = "Double";
            sHas = "Double.parseDouble";
         } else if (type.equals("GEO_COORDINATE")) {
            type_low = "double";
            type_low2 = "Double";
            sHas = "Double.parseDouble";
         } else if (type.equals("OBJECT")) {
            type_low = "Object";
            type_low2 = "Object";
         } else {
            type_low = "String";
            type_low2 = "";
         }
         id_low = baseType + id_low;

         // validate enumerations
         String sParams = "";
         int iPnrElementParamEnum = sPnrElementParamInfo.indexOf("<PnrElementParamEnum");
         if (iPnrElementParamEnum > 0) {
            sParams += "    * Possible Enumerations are:<ul>\n";
            while (iPnrElementParamEnum > 0) {
               Hashtable<String, StringBuffer> htEnum = getTags(sPnrElementParamInfo.substring(iPnrElementParamEnum));
               iPnrElementParamEnum = sPnrElementParamInfo.indexOf("<PnrElementParamEnum", iPnrElementParamEnum + 1);
               StringBuffer sName = htEnum.get("name");
               StringBuffer sValue = htEnum.get("value");
               if (sValue == null) {
                  sValue = new StringBuffer("???");
               }
               if (sName == null) {
                  sName = new StringBuffer(getUnFormatedProvider(sValue.toString()));
               }
               sParams += "    *   <li> <b>" + sName + "</b>=" + sValue + "\n";
            }
            sParams += "    * </ul>\n";
         }
         sParams += "    * <p>There are the following parameters:<ul>\n";
         Enumeration<String> keys = ht.keys();
         while (keys.hasMoreElements()) {
            String sKey = keys.nextElement();
            sParams += "    * <li> <b>" + getUnFormatedProvider(sKey) + "</b> = " + ht.get(sKey).toString() + "\n";
         }
         sParams += "    * </ul>\n";
         sParams += "    * <p>\n";

         // try to find get method
         // add get method to the end
         String s = "";
         s += "   /**\n";
         if (hint.length() > 0) {
            hint = hint.substring(0, 1).toUpperCase() + hint.substring(1);
         }
         s += "    * " + hint + "\n";
         s += sParams;
         s += "    * Method <code>set" + id_low + "()</code> was automatically generated.\n";
         int iPos = type_low.lastIndexOf(".") + 1;
         String sValueName = "p" + type_low.substring(iPos, iPos + 1).toLowerCase() + "Value";
         s += "    * @param " + sValueName + " The " + id_low + " value\n";
         s += "    */\n";
         s += "   @SuppressWarnings(\"deprecation\")\n";
         s += "   public void set" + id_low + "(" + type_low + " " + sValueName + ")\n";
         s += "   {\n";
         s += "      " + DONOTEDITTHISMETHOD + "\n";
         s += "      setParamValue(PnrEnumPnrElementParameterId." + id + ", " + sValueName + ");\n";
         // ensure that there is also a float method ... because this could lead to errors
         if (type_low.equals("double")) {
            // s += "   // This method overloads the related double method\n";
            s += "   }\n\n";
            s += "   /**\n";
            s += "    * " + hint + "\n";
            s += sParams;
            s += "    * Method <code>set" + id_low + "()</code> was automatically generated.\n";
            s += "    * @param pfValue The float value\n";
            s += "    */\n";
            s += "   @SuppressWarnings(\"deprecation\")\n";
            s += "   public void set" + id_low + "(float pfValue)\n";
            s += "   {\n";
            s += "      setParamValue(PnrEnumPnrElementParameterId." + id + ", pfValue);\n";
         }
         s += "   } // Generated Method set" + id_low + "(" + type_low + " " + sValueName + ")\n";
         s += "\n";
         psNewFile = addOrReplace(psNewFile, s, "set" + id_low, true);

         // add get method to the end
         s = "";
         s += "   /**\n";
         s += sParams;
         s += "    * Method <code>get" + id_low + "()</code> was automatically generated.\n";
         s += "    * @return the value of the parameter " + id_low + "\n";
         s += "    */\n";
         s += "   @SuppressWarnings(\"deprecation\")\n";
         s += "   public " + type_low + " get" + id_low + "()\n";
         s += "   {\n";
         s += "      " + DONOTEDITTHISMETHOD + "\n";
         if (type_low2.startsWith("Enum")) {
            if (sDefault.length() > 0) {
               s += "      String sEnumValue = getParamValue(PnrEnumPnrElementParameterId." + id + ");\n";
               s += "      if (sEnumValue == null || sEnumValue.length() == 0) {\n";
               s += "          sEnumValue = \"" + sDefault + "\";\n";
               s += "      }\n";
               s += "      return " + type_low + ".parse(sEnumValue);\n";
            } else {
               s += "      return " + type_low + ".parse(getParamValue(PnrEnumPnrElementParameterId." + id + "));\n";

            }
         } else {
            s += "      return getParamValue" + type_low2 + "(PnrEnumPnrElementParameterId." + id + ");\n";
         }
         s += "   } // Generated Method get" + id_low + "()\n";
         s += "\n";
         psNewFile = addOrReplace(psNewFile, s, "get" + id_low + "", true);

         // add Has Method
         if (sHas.length() > 0) {
            s = "";
            s += "   /**\n";
            s += sParams;
            s += "    * Method <code>has" + id_low + "()</code> was automatically generated.\n";
            s += "    * @return true if parameter " + id_low + " exist\n";
            s += "    */\n";
            s += "   @SuppressWarnings(\"deprecation\")\n";
            s += "   public boolean has" + id_low + "()\n";
            s += "   {\n";
            s += "      " + DONOTEDITTHISMETHOD + "\n";
            s += "      try {\n";
            s += "          String s" + id + " = getParamValue(PnrEnumPnrElementParameterId." + id + ");\n";
            s += "          if (s" + id + " == null || s" + id + ".length() == 0) {\n";
            s += "            return false;\n";
            s += "          }\n";
            s += "          // try if " + sHas + " fails (DT 17053)\n";
            s += "          " + sHas + "(s" + id + ");\n";
            s += "      } catch (Exception ex){\n";
            s += "          return false;\n";
            s += "      }\n";
            s += "      return true;\n";
            s += "   } // Generated Method has" + id_low + "()\n";
            s += "\n";
            psNewFile = addOrReplace(psNewFile, s, "has" + id_low + "", true);
         }

         iStart = psPnrElementInfo.indexOf("<PnrElementParamInfo ", iStart + 1);
      }
      return psNewFile;
   }

   /**
    * method getTags
    *
    * @param psText
    * @return The Tags within the text
    *
    * @author brod
    */
   private static Hashtable<String, StringBuffer> getTags(String psText)
   {
      Hashtable<String, StringBuffer> ht = new Hashtable<>();
      boolean bText = false;
      StringBuffer sb = new StringBuffer();
      char[] cElement = psText.toCharArray();
      boolean bEnd = false;
      for (int i = 0; !bEnd && (i < cElement.length); i++) {
         if (bText) {
            if (cElement[i] == '\"') {
               bText = false;
               sb = new StringBuffer();
            } else {
               sb.append(cElement[i]);
            }
         } else if (cElement[i] == ' ') {
            if (sb.length() > 0) {
               sb = new StringBuffer();
            }
         } else if (cElement[i] == '=') {
            //make nothing
         } else if (cElement[i] == '>') {
            bEnd = true;
         } else if (cElement[i] == '\"') {
            String key = sb.toString();
            sb = new StringBuffer();
            ht.put(key, sb);
            bText = true;
         } else {
            sb.append(cElement[i]);
         }
      }
      return ht;
   }

   /**
    * Method removeAutomaticallyGeneratedMethods
    *
    * @param psNewFile
    * @return File without automatically GeneratedMethods
    *
    * @author brod
    */
   private static String removeAutomaticallyGeneratedMethods(String psNewFile)
   {
      int iStart = psNewFile.indexOf(DONOTEDITTHISMETHOD);
      while (iStart > 0) {
         int iEnd = psNewFile.indexOf("// Generated Method", iStart);
         iEnd = psNewFile.indexOf("\n", iEnd) + 1;
         iStart = psNewFile.lastIndexOf("/**", iStart);
         iStart = psNewFile.lastIndexOf("\n", iStart) + 1;
         psNewFile = psNewFile.substring(0, iStart) + psNewFile.substring(iEnd);
         iStart = psNewFile.indexOf(DONOTEDITTHISMETHOD);
      }
      return psNewFile;
   }

   /**
    * Method addOrReplace adds/replaces a specific method
    *
    * @param psJavaFile The JavaFile
    * @param psMethodValue The value of the method
    * @param psMethodName The name of the method
    * @param pbElement
    * @return New new text
    *
    * @author brod
    */
   private static String addOrReplace(String psJavaFile, String psMethodValue, String psMethodName, boolean pbElement)
   {
      if (psMethodName.indexOf("(") > 0) {
         psMethodName = psMethodName.substring(0, psMethodName.indexOf("("));
      }
      int iStart = psJavaFile.indexOf("// Generated Method " + psMethodName + "(");
      if (pbElement) {
         if (psMethodValue.indexOf(DONOTEDITTHISMETHOD) < 0) {
            return psJavaFile;
         }
      } else {
         if (iStart >= 0) {
            return psJavaFile;
         }
      }
      int iEnd = 0;
      if (iStart > 0) {
         iEnd = psJavaFile.indexOf("\n", iStart) + 1;
         iStart = psJavaFile.lastIndexOf("/**", iStart);
         iStart = psJavaFile.lastIndexOf("\n", iStart);
      } else {
         iStart = psJavaFile.lastIndexOf("}");
         iEnd = iStart;
      }
      return psJavaFile.substring(0, iStart) + psMethodValue + "\n" + psJavaFile.substring(iEnd);
   }

   /**
    * Method removeToDos
    *
    *
    * @param psText
    * @return
    *
    * @author $author$
    */
   private static String removeToDos(String psText)
   {
      int iToDo = psText.indexOf("@todo");

      while (iToDo > 0) {
         psText = psText.substring(0, iToDo) + psText.substring(psText.indexOf("*/", iToDo));
         iToDo = psText.indexOf("@todo");
      }

      return psText;
   }

   /**
    * Method validateRuleMap
    *
    * @param psbLog
    * @param psProvider
    * @param psBaseDir
    *
    * @author Andreas Brod
    */
   private static void validateRuleMap(StringBuffer psbLog, String psProvider, String psBaseDir)
   {
      if (psBaseDir.endsWith("\\src\\")) {
         psBaseDir = psBaseDir.substring(0, psBaseDir.length() - 5);
      }

      String sRuleMap = Util.loadFromFile(Util.getConfFile(psBaseDir, "RuleMap.xml"));
      int lastIdx = sRuleMap.lastIndexOf("</Provider>");

      if ((lastIdx > 0) && (sRuleMap.indexOf("<Provider type=\"" + psProvider + "\"") < 0)) {
         String sText = "</Provider>\n";

         sText += "   <Provider type=\"" + psProvider + "\">\n";
         sText += "      <Event name=\"onBeforeProcess\">\n";
         sText += "         <Rule class=\"net.ifao.arctic.agents.common.pnr.business.DisableEmptyElementsRule\"/>\n";
         sText += "         <Rule class=\"net.ifao.arctic.agents.common.pnr.business.SplitParameterRule\"/>\n";
         sText += "         <Rule class=\"net.ifao.arctic.agents.common.pnr.business.PlausiCheckRule\"/>\n";
         sText += "      </Event>\n";
         sText += "      <Event name=\"onAfterProcess\">\n";
         sText += "         <Rule class=\"net.ifao.arctic.agents.common.pnr.business.PostReservationConsistencyRule\"/>\n";
         sText += "      </Event>\n";
         sText += "   ";
         sRuleMap = sRuleMap.substring(0, lastIdx) + sText + sRuleMap.substring(lastIdx);

         Util.writeToFile(Util.getConfFile(psBaseDir, "RuleMap.xml").getAbsolutePath(), sRuleMap);
         psbLog.append("\nCreated BaseRules for " + psProvider + " in RuleMap.xml\n");
      }

   }

   /**
    * Method loadDefaultClass
    *
    *
    *
    * @param psbLog
    * @param psFileName
    * @param psPackage
    * @param psProvider
    * @param psMethod
    * @param psElement
    * @param psSourceList
    * @param psArcticBase
    * @return
    *
    * @author Andreas Brod
    * @param pSourceCodes
    * @param phsFiles
    */
   private static String loadDefaultClass(StringBuffer psbLog, String psFileName, String psPackage, String psProvider,
                                          String psMethod, String psElement, String psSourceList, String psArcticBase,
                                          Hashtable<String, String> pSourceCodes, Set<File> phsFiles)
   {

      String sFile = Util.loadFile("Generator/defaults/" + psFileName + ".java");

      sFile = Util.replaceString(sFile, "_PACKAGE_", psPackage);
      sFile = Util.replaceString(sFile, "_PROVIDER_", psProvider);

      String sPackageRoot = psPackage + ".";

      sFile = Util.replaceString(sFile, "_PACKAGEROOT_", sPackageRoot.substring(0, sPackageRoot.indexOf(".")));
      sFile = Util.replaceString(sFile, "_Provider_", getUnFormatedProvider(psProvider));
      sFile = Util.replaceString(sFile, "_METHOD_", getCamelCaseName(psMethod));
      sFile = Util.replaceString(sFile, "_Method_", psMethod);

      if (psElement.length() > 0) {
         sFile = Util.replaceString(sFile, "_ELEMENT_", psElement);
         sFile = Util.replaceString(sFile, "_Element_", getUnFormatedProvider(psElement));
      }

      if (psFileName.equals("TransformerHandler")) {

         int iStart = sFile.indexOf("THE FOLLOWING LINES WILL BE AUTOMATICALLY GENERATED");

         if (iStart > 0) {
            iStart = sFile.indexOf("\n", iStart) + 1;
         } else {
            return "";
         }

         // DtdTransformerHandler handler = new DtdTransformerHandler(psProvider, psSourceList,
         //               psArcticBase);
         DtdTransformerHandler handler =
            new DtdTransformerHandler(psProvider, psSourceList, psArcticBase, pSourceCodes, sPackageRoot);

         String sSource = handler.scrOfTransformer();

         sFile = sFile.substring(0, iStart);

         // if there is any abstract method ...
         if (sSource.indexOf("protected abstract ") >= 0) {

            // ... define the class as abstract
            sSource =
               "\n    // Class is defined as abstract because DtdInfo contains" + "\n    // User defined entires\n\n" + sSource;

            String sCustomized = loadDefaultClass(psbLog, "CustomizedTransformer", psPackage, psProvider, psMethod, psElement,
                  psSourceList, psArcticBase, pSourceCodes, phsFiles);

            sCustomized = sCustomized.substring(0, sCustomized.lastIndexOf("}"));

            for (Iterator<String> i = handler.getAbstractItems(); i.hasNext();) {
               String sMethod = i.next();

               sCustomized += sMethod + "\n";
            }

            sCustomized += "}";

            writeFile(psbLog, psArcticBase, sCustomized, phsFiles);

         }

         sFile += sSource + "\n}\n";
      }

      return sFile;
   }

   /**
    * Method withoutVersionInfo
    *
    * @param psText
    * @return
    *
    * @author Andreas Brod
    */
   private static String withoutVersionInfo(String psText)
   {
      int i = psText.indexOf(DtdTransformerHandler.GENERATED_VERSION);

      if (i >= 0) {
         psText = psText.substring(0, i) + psText.substring(psText.indexOf("\n", i) + 1);
      }

      return psText;
   }

   /**
    * Method replace
    *
    *
    * @param psBaseDir
    * @param psbLog
    * @param psFileName
    * @param psShortName
    * @param psNewFile
    * @param psOldFile
    * @return
    *
    * @author Andreas Brod
    * @param phsFiles
    */
   private static String updateFile(String psBaseDir, StringBuffer psbLog, String psFileName, String psShortName,
                                    String psNewFile, String psOldFile, Set<File> phsFiles)
   {

      // replace all 'old' generated by
      // get 'old' authors
      Hashtable<String, String> htGeneratedBy = new Hashtable<>();
      int iGeneratedBy = psOldFile.indexOf("@author generated by");
      try {
         while (iGeneratedBy > 0) {
            iGeneratedBy += 20;
            int i0 = psOldFile.indexOf("\n", iGeneratedBy);
            int i1 = psOldFile.indexOf("*/", i0);
            i1 = psOldFile.indexOf("\n", i1) + 1;
            while (psOldFile.charAt(i1) <= ' ') {
               i1++;
            }
            int i2 = psOldFile.indexOf(")", i1);
            htGeneratedBy.put(psOldFile.substring(i1, i2).replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", ""),
                  psOldFile.substring(iGeneratedBy, i0));
            iGeneratedBy = psOldFile.indexOf("@author generated by", iGeneratedBy + 1);
         }
      }
      catch (Exception ex) {
         // catch eventual exceptions
      }

      // set the 'old' authors
      iGeneratedBy = psNewFile.indexOf("@author generated by");
      try {
         while (iGeneratedBy > 0) {
            iGeneratedBy += 20;
            int i0 = psNewFile.indexOf("\n", iGeneratedBy);
            int i1 = psNewFile.indexOf("*/", i0);
            i1 = psNewFile.indexOf("\n", i1) + 1;
            while (psNewFile.charAt(i1) <= ' ') {
               i1++;
            }
            int i2 = psNewFile.indexOf(")", i1);
            String sOldAuthor =
               htGeneratedBy.get(psNewFile.substring(i1, i2).replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", ""));
            if ((sOldAuthor != null) && (sOldAuthor.length() > 0)) {
               // replace the text
               psNewFile = psNewFile.substring(0, iGeneratedBy) + sOldAuthor + psNewFile.substring(i0);
            }
            iGeneratedBy = psNewFile.indexOf("@author generated by", iGeneratedBy + 1);
         }
      }
      catch (Exception ex) {
         // catch eventual exceptions
      }

      String sToDoFile = "";

      // replace EndOfFile
      if (psNewFile.indexOf("THE FOLLOWING LINES WILL BE AUTOMATICALLY GENERATED !!!") > 0) {
         // psNewFile = getJIndent(psBaseDir, psNewFile);

         String sGenOld = psOldFile.substring(psOldFile.indexOf("THE FOLLOWING LINES WILL BE AUTOMATICALLY GENERATED !!!"));
         String sGenNew = psNewFile.substring(psNewFile.indexOf("THE FOLLOWING LINES WILL BE AUTOMATICALLY GENERATED !!!"));

         // correct the abstract entry
         if ((sGenNew.indexOf("protected abstract ") > 0) && (psOldFile.indexOf("public abstract class") < 0)) {
            psOldFile = Util.replaceString(psOldFile, "public class ", "public abstract class ");
            sGenOld = "- changed in each case -";
         } else if ((sGenNew.indexOf("protected abstract ") < 0) && (psOldFile.indexOf("public abstract class") > 0)) {
            psOldFile = Util.replaceString(psOldFile, "public abstract class ", "public class ");
            sGenOld = "- changed in each case -";
         }

         if (!withoutVersionInfo(sGenOld).equals(withoutVersionInfo(sGenNew))) {
            psOldFile =
               psOldFile.substring(0, psOldFile.indexOf("THE FOLLOWING LINES WILL BE AUTOMATICALLY GENERATED !!!")) + sGenNew;
            psOldFile = copyImports(psNewFile, psOldFile);
            phsFiles.add(Util.writeToFile(psFileName, psOldFile));
            psbLog.append("UPDATED - " + psShortName + "\n");
         }
      } else if (psNewFile.indexOf("// Generated Method ") > 0) {

         Hashtable<String, String> getGeneratedMethods = getGeneratedMethods(psNewFile);
         Hashtable<String, String> getGeneratedMethodsOld = getGeneratedMethods(psOldFile);

         // the methods have to match
         boolean bChanged = false;
         Object[] keys = getGeneratedMethodsOld.keySet().toArray();
         for (int i = 0; !bChanged && (i < keys.length); i++) {
            String s1 = getGeneratedMethodsOld.get(keys[i]);
            String s2 = getGeneratedMethods.get(keys[i]);
            if ((s2 == null) || ((s2.indexOf(DONOTEDITTHISMETHOD) > 0) && !methodMatch(s1, s2))) {
               bChanged = true;
            }
         }
         keys = getGeneratedMethods.keySet().toArray();
         Arrays.sort(keys);
         for (int i = 0; !bChanged && (i < keys.length); i++) {
            String s1 = getGeneratedMethods.get(keys[i]);
            String s2 = getGeneratedMethodsOld.get(keys[i]);
            if ((s2 == null) || ((s2.indexOf(DONOTEDITTHISMETHOD) > 0) && !methodMatch(s1, s2))) {
               bChanged = true;
            }
            // validate if all enums exist
            int iReturn = s1.indexOf("return Enum");
            int iParse = s1.indexOf(".parse(sEnumValue)", iReturn + 1);
            if ((iReturn > 0) && (iParse > 0)) {
               String sClassName = s1.substring(iReturn + 7, iParse);

               // remove class from oldFile
               if (psOldFile.indexOf("static class " + sClassName) < 0) {
                  bChanged = true;
               } else if (psOldFile.indexOf("public int hashCode()") < 0) {
                  bChanged = true;
               }
            }
         }

         // if there is a change
         if (bChanged) {
            psOldFile = removeAutomaticallyGeneratedMethods(psOldFile);
            String sEnumClasses = "";
            for (Object key : keys) {
               String sNewCode = getGeneratedMethods.get(key);
               int iReturn = sNewCode.indexOf("return Enum");
               int iParse = sNewCode.indexOf(".parse(getParamValue(", iReturn + 1);
               if (iParse < 0) {
                  iParse = sNewCode.indexOf(".parse(sEnumValue)", iReturn + 1);
               }
               if ((iReturn > 0) && (iParse > 0)) {
                  String sClassName = sNewCode.substring(iReturn + 7, iParse);

                  // remove class from oldFile
                  int iStart = psOldFile.indexOf("static class " + sClassName);
                  if (iStart > 0) {
                     iStart = psOldFile.lastIndexOf("/**", iStart);
                     iStart = psOldFile.lastIndexOf("\n", iStart) + 1;
                     int iEnd = psOldFile.indexOf("// end class " + sClassName, iStart);
                     if (iEnd > 0) {
                        iEnd = psOldFile.indexOf("\n", iEnd) + 1;
                        psOldFile = psOldFile.substring(0, iStart) + psOldFile.substring(iEnd);
                     }
                  }

                  sEnumClasses += "   /**\n";
                  sEnumClasses += "    * This Enumerator is used for " + sClassName + " business elements.\n";
                  sEnumClasses += "    * <p>\n";
                  sEnumClasses += "    * DO NOT EDIT THIS CLASS, BECAUSE IT WILL BE REGENERATED\n";
                  sEnumClasses += "    * @author Generator\n";
                  sEnumClasses += "    */\n";
                  sEnumClasses += "   public static class " + sClassName + "\n";
                  sEnumClasses += "   {\n";
                  HashSet<String> enumItems = new HashSet<>();
                  // get the EnumTypes
                  iStart = sNewCode.indexOf("Possible Enumerations are:");
                  int iEnd = sNewCode.indexOf("</ul>");
                  while (iStart > 0) {
                     iStart = sNewCode.indexOf("<li>", iStart);
                     if ((iStart > 0) && (iStart < iEnd)) {
                        iStart += 4;
                        int iEndLi = sNewCode.indexOf("\n", iStart);
                        String sLi = sNewCode.substring(iStart, iEndLi).trim();
                        if (sLi.indexOf("=") > 0) {
                           String sLi2 = sLi.substring(sLi.indexOf("=") + 1).trim();
                           if (enumItems.add(sLi2)) {
                              sEnumClasses += "      /**\n";
                              sEnumClasses += "       * Enumeration: " + sLi + "\n";
                              sEnumClasses += "       */\n";
                              sEnumClasses += "      public static final " + sClassName + " " + getMemberName(sLi2) + " = new "
                                    + sClassName + "(\"" + sLi2 + "\");\n";
                              sEnumClasses += "\n";
                           }
                        }
                     } else {
                        iStart = -1;
                     }
                  }
                  sEnumClasses += "      private final String _s" + sClassName + ";\n";
                  sEnumClasses += "\n";
                  sEnumClasses += "      /**\n";
                  sEnumClasses += "       * Private Constructor " + sClassName + "\n";
                  sEnumClasses += "       * @param ps" + sClassName + " The String of this Type\n";
                  sEnumClasses += "       * @return created " + sClassName + " object\n";
                  sEnumClasses += "       */\n";
                  sEnumClasses += "      private " + sClassName + "(String ps" + sClassName + ")\n";
                  sEnumClasses += "      {\n";
                  sEnumClasses += "         _s" + sClassName + " = ps" + sClassName + ";\n";
                  sEnumClasses += "      }\n";
                  sEnumClasses += "\n";
                  sEnumClasses += "      /**\n";
                  sEnumClasses += "       * @param ps" + sClassName + " The type to parse.\n";
                  sEnumClasses += "       * @return The related " + sClassName + "-object. If the\n";
                  sEnumClasses += "       * " + sClassName + " is not found the returnValue is null\n";
                  sEnumClasses += "       */\n";
                  sEnumClasses += "      public static " + sClassName + " parse(String ps" + sClassName + ")\n";
                  sEnumClasses += "      {\n";
                  sEnumClasses += "         if(ps" + sClassName + " == null) {\n";
                  sEnumClasses += "            return null;\n";
                  sEnumClasses += "         }\n";
                  Object[] array = enumItems.toArray();
                  for (Object element : array) {
                     sEnumClasses += "         if (ps" + sClassName + ".equalsIgnoreCase(\"" + element + "\")) {\n";
                     sEnumClasses += "            return " + getMemberName((String) element) + ";\n";
                     sEnumClasses += "         }\n";
                  }
                  sEnumClasses += "         return null;\n";
                  sEnumClasses += "      }\n";
                  sEnumClasses += "\n";
                  sEnumClasses += "      /**\n";
                  sEnumClasses += "       * The String representation of this Object\n";
                  sEnumClasses += "       * @return the String representation of this Object\n";
                  sEnumClasses += "       *\n";
                  sEnumClasses += "       * @see java.lang.Object#toString()\n";
                  sEnumClasses += "       */\n";
                  sEnumClasses += "      @Override\n";
                  sEnumClasses += "      public String toString()\n";
                  sEnumClasses += "      {\n";
                  sEnumClasses += "         return _s" + sClassName + ";\n";
                  sEnumClasses += "      }\n";
                  sEnumClasses += "\n";
                  sEnumClasses += "      /**\n";
                  sEnumClasses += "       * @param pOtherObject The object, which has to be compared\n";
                  sEnumClasses += "       * @return true, if the ElementConfirmation equals the other object\n";
                  sEnumClasses += "       */\n";
                  sEnumClasses += "      @Override\n";
                  sEnumClasses += "      public boolean equals(Object pOtherObject)\n";
                  sEnumClasses += "      {\n";
                  sEnumClasses += "         if (pOtherObject == null) {\n";
                  sEnumClasses += "             return false;\n";
                  sEnumClasses += "         }\n";
                  sEnumClasses += "         return _s" + sClassName + ".equals(pOtherObject.toString());\n";
                  sEnumClasses += "      }\n";
                  sEnumClasses += "\n";
                  sEnumClasses += "      /**\n";
                  sEnumClasses += "       * Method hashCode\n";
                  sEnumClasses += "       * overrides @see java.lang.Object#hashCode()\n";
                  sEnumClasses += "       *\n";
                  sEnumClasses += "       * @return hash code of the enum value\n";
                  sEnumClasses += "       */\n";
                  sEnumClasses += "      @Override\n";
                  sEnumClasses += "      public int hashCode()\n";
                  sEnumClasses += "      {\n";
                  sEnumClasses += "         return _s" + sClassName + ".hashCode();\n";
                  sEnumClasses += "      }\n";
                  sEnumClasses += "\n";
                  sEnumClasses += "   } // end class " + sClassName + "\n\n";

               }
               boolean bElement = false;
               psOldFile = addOrReplace(psOldFile, sNewCode, key.toString(), bElement);
            }

            if (sEnumClasses.length() > 0) {
               int iEnd = psOldFile.lastIndexOf("}");
               psOldFile = psOldFile.substring(0, iEnd) + sEnumClasses + psOldFile.substring(iEnd);
            }
            // remove empty lines
            psOldFile = psOldFile.replaceAll("\r", "");
            int iPos = psOldFile.indexOf("\n\n\n\n");
            while (iPos >= 0) {
               psOldFile = psOldFile.substring(0, iPos) + psOldFile.substring(iPos + 1);
               iPos = psOldFile.indexOf("\n\n\n\n");
            }

            // psOldFile = getJIndent(psBaseDir, psOldFile);
            psOldFile = psOldFile.replaceAll("\\n", "\r\n");
            psOldFile = copyImports(psNewFile, psOldFile);

            phsFiles.add(Util.writeToFile(psFileName, psOldFile));
            psbLog.append("UPDATED - " + psShortName + "\n");

            sToDoFile = psOldFile;
         }

      } else if (psNewFile.indexOf("## GENERATOR.BEGIN ##") > 0) {

         // replace Block
         try {
            boolean ok = true;

            String sGenOld = getStartEnd(
                  psOldFile.substring(psOldFile.indexOf("## GENERATOR.BEGIN ##"), psOldFile.indexOf("## GENERATOR.END ##")));

            Hashtable<String, String> elementsOld = getElements(sGenOld);
            Hashtable<String, String> elementsNew = getElements(
                  psNewFile.substring(psNewFile.indexOf("## GENERATOR.BEGIN ##"), psNewFile.indexOf("## GENERATOR.END ##")));

            // validate if all new element exist correctly
            for (String sKey : elementsNew.keySet()) {
               String sNew = elementsNew.get(sKey);

               // validate if TransformerHandler is abstract
               if (sNew.indexOf("new TransformerHandler") > 0) {
                  int iTH = sNew.indexOf("new TransformerHandler") + 4;
                  String sTransHandler = sNew.substring(iTH, sNew.indexOf("(", iTH));

                  // get package
                  int iPack = psOldFile.indexOf(".framework.transform.");
                  String sPack = psBaseDir + "\\" + Util.replaceString(
                        psOldFile.substring(psOldFile.lastIndexOf(" ", iPack) + 1, psOldFile.indexOf(".*", iPack)), ".", "\\");
                  String sTH = Util.loadFromFile(sPack + "\\" + sTransHandler + ".java");

                  if (sTH.indexOf(" abstract class ") > 0) {
                     String sCust = "CustomizedTransformer" + sTransHandler.substring(18);

                     sNew = Util.replaceString(sNew, sTransHandler, sCust);
                  }
               }

               if ((elementsOld.get(sKey) == null) || !comp(elementsOld.get(sKey), sNew)) {
                  elementsOld.put(sKey, sNew);

                  ok = false;
               }
            }

            if (!ok) {

               String sGenNew = "";

               // sort the keys
               Object[] keys = elementsOld.keySet().toArray();

               Arrays.sort(keys);

               for (Object key : keys) {
                  String sKey = (String) key;

                  sGenNew += elementsOld.get(sKey);
               }

               int iStart = psOldFile.indexOf(sGenOld);

               psOldFile = psOldFile.substring(0, iStart) + sGenNew + psOldFile.substring(iStart + sGenOld.length());

               // do not JIndent file !
               // psOldFile = getJIndent(psBaseDir, psOldFile);
               psOldFile = copyImports(psNewFile, psOldFile);

               phsFiles.add(Util.writeToFile(psFileName, psOldFile));
               psbLog.append("UPDATED - " + psShortName + "\n");

            }
         }
         catch (Exception ex1) {}
      } else if (psNewFile.indexOf("//$JUnit-BEGIN$") > 0) {
         boolean bNew = false;
         String sGenNew = psOldFile.substring(psOldFile.indexOf("//$JUnit-BEGIN$"), psOldFile.indexOf("//$JUnit-END$"));
         StringTokenizer tokenizer = new StringTokenizer(
               psNewFile.substring(psNewFile.indexOf("//$JUnit-BEGIN$"), psNewFile.indexOf("//$JUnit-END$")), "\n");

         while (tokenizer.hasMoreTokens()) {
            String element = tokenizer.nextToken().trim();

            if (!element.startsWith("//") && (element.length() > 0)) {
               if (sGenNew.indexOf(element) < 0) {
                  sGenNew = sGenNew + element + sGenNew.substring(sGenNew.lastIndexOf("\n"));
                  bNew = true;
               }
            }
         }

         if (bNew) {

            // update imports
            tokenizer = new StringTokenizer(psNewFile, "\n");

            while (tokenizer.hasMoreTokens()) {
               String element = tokenizer.nextToken().trim();

               if (element.startsWith("import ")) {
                  if (psOldFile.indexOf(element) < 0) {
                     psOldFile = psOldFile.substring(0, psOldFile.indexOf("import ")) + element + "\n"
                           + psOldFile.substring(psOldFile.indexOf("import "));

                  }
               }
            }

            psOldFile = psOldFile.substring(0, psOldFile.indexOf("//$JUnit-BEGIN$")) + sGenNew
                  + psOldFile.substring(psOldFile.indexOf("//$JUnit-END$"));
            psOldFile = copyImports(psNewFile, psOldFile);
            phsFiles.add(Util.writeToFile(psFileName, psOldFile));
            psbLog.append("UPDATED - " + psShortName + "\n");

         }

      }

      return sToDoFile;
   }

   /**
    * The method copyImports copies the imports from one file to another
    *
    * @param psNewFile The text of the new File
    * @param psOldFile The Text of the old File
    * @return The oldFile with the (new) Imports from the psNewFile
    *
    * @author brod
    */
   private static String copyImports(String psNewFile, String psOldFile)
   {
      List<String> importsOld = getImports(psOldFile);
      List<String> importsNew = getImports(psNewFile);
      for (int j = 0; j < importsNew.size(); j++) {
         String sImport = importsNew.get(j);
         if (!importsOld.contains(sImport)) {
            int iImport = psOldFile.indexOf("import ");
            psOldFile = psOldFile.substring(0, iImport) + "import " + sImport + ";\n" + psOldFile.substring(iImport);
         }
      }
      return psOldFile;
   }

   /**
    * The method methodMatch returns true if two methods match. The
    * whiteSpaces are eliminated.
    *
    * @param psText1 The first text
    * @param psText2 The second text
    * @return true if two methods match
    *
    * @author brod
    */
   private static boolean methodMatch(String psText1, String psText2)
   {
      psText1 = withoutBlank(psText1);
      psText2 = withoutBlank(psText2);
      return psText1.equals(psText2);
   }

   /**
    * The method withoutBlank returns the String without any blank characters
    *
    * @param psText The text
    * @return The text (only with characters within the range ]32,127[
    *
    * @author brod
    */
   private static String withoutBlank(String psText)
   {
      if (psText == null) {
         return "";
      }
      StringBuffer sb = new StringBuffer();
      char[] charArray = psText.toCharArray();
      for (char element : charArray) {
         if ((element > ' ') && (element < 127)) {
            sb.append(element);
         }
      }
      return sb.toString();
   }

   /**
    * The method getImports return the import of a Text
    *
    * @param psText The text
    * @return The list of Imports.
    *
    * @author brod
    */
   private static List<String> getImports(String psText)
   {
      List<String> ret = new ArrayList<>();
      psText = psText.substring(0, psText.indexOf("{") + 1);
      StringTokenizer st = new StringTokenizer(psText, ";");
      while (st.hasMoreTokens()) {
         String sLine = withoutBlank(st.nextToken());
         if (sLine.startsWith("import")) {
            ret.add(sLine.substring(6));
         }
      }
      return ret;
   }

   /**
    * method getMemberName
    *
    * @param psClassName The name of the class
    * @return The clas with valid chars.
    *
    * @author brod
    */
   private static String getMemberName(String psClassName)
   {
      char[] charArray = psClassName.toCharArray();
      for (int i = 0; i < charArray.length; i++) {
         if ((charArray[i] >= 'A') && (charArray[i] <= 'Z')) {
            // ok
         } else if ((charArray[i] >= 'a') && (charArray[i] <= 'z')) {
            // ok
         } else if ((charArray[i] >= '0') && (charArray[i] <= '9')) {
            // ok
         } else {
            charArray[i] = '_';
         }
      }
      return new String(charArray);
   }

   /**
    * method getGeneratedMethods
    *
    * @param psFile the file
    * @return The list of GeneratedMethods
    *
    * @author brod
    */
   private static Hashtable<String, String> getGeneratedMethods(String psFile)
   {
      Hashtable<String, String> ht = new Hashtable<>();
      int iStart = psFile.indexOf("// Generated Method ");

      while (iStart > 0) {
         iStart += 19;

         String sGenMethod =
            psFile.substring(iStart, Math.max(psFile.indexOf("(", iStart + 1) + 1, psFile.indexOf(" ", iStart + 1))).trim();
         if (sGenMethod.indexOf(")") > 0) {
            sGenMethod = sGenMethod.substring(0, sGenMethod.indexOf(")"));
         }
         int iEnd = 0;
         iEnd = psFile.indexOf("\n", iStart) + 1;
         iStart = psFile.lastIndexOf(" " + sGenMethod, iStart - 1);
         iStart = psFile.lastIndexOf("/**", iStart);
         iStart = psFile.lastIndexOf("\n", iStart) + 1;

         String sMethod = psFile.substring(iStart, iEnd);
         ht.put(sGenMethod, sMethod);
         iStart = psFile.indexOf("// Generated Method ", iEnd);
      }
      return ht;
   }

   /**
    * The Method comp compares two string, whereas all
    * charaters <= ' ' are ignored.
    *
    * @param s1 the first String
    * @param s2 the second String
    *
    * @return true if they are the same
    * @author Andreas Brod
    */
   private static boolean comp(String s1, String s2)
   {
      char[] cs = s1.toCharArray();

      s1 = "";

      for (char element : cs) {
         if (element > ' ') {
            s1 += element;
         }
      }

      cs = s2.toCharArray();
      s2 = "";

      for (char element : cs) {
         if (element > ' ') {
            s2 += element;
         }
      }

      return s1.equals(s2);
   }

   /**
    * Method writeFile
    *
    *
    * @param psbLog
    * @param psBaseDir
    * @param psFile
    *
    * @author Andreas Brod
    * @param phsFiles
    */
   public static void writeFile(StringBuffer psbLog, String psBaseDir, String psFile, Set<File> phsFiles)
   {
      String sPackage = "";

      if (psFile.indexOf("package ") >= 0) {
         sPackage = psFile.substring(psFile.indexOf("package ") + 8);

         sPackage = sPackage.substring(0, sPackage.indexOf(";"));
      }

      psFile = Util.replaceString(psFile, "_GENERATOR_", "generated by " + System.getProperty("user.name"));

      String sFileName = "";

      if (psBaseDir.endsWith("\\")) {
         psBaseDir = psBaseDir.substring(0, psBaseDir.length() - 1);
      }

      if (psFile.indexOf("public class ") > 0) {
         sFileName = psFile.substring(psFile.indexOf("public class "));
      } else if (psFile.indexOf("public abstract class ") > 0) {
         sFileName = psFile.substring(psFile.indexOf("public abstract class "));
      }

      if (sFileName.length() > 0) {
         sFileName = sFileName.substring(0, sFileName.indexOf("\n")).trim();
         sFileName = sFileName.substring(sFileName.lastIndexOf(" ") + 1);

         if (sPackage.length() > 0) {
            sFileName = psBaseDir + "\\" + Util.replaceString(sPackage, ".", "\\") + "\\" + sFileName + ".java";
         } else {
            sFileName = psBaseDir + "\\" + sFileName + ".java";

         }

         String sOldFile = Util.loadFromFile(sFileName);

         String sShortName = sFileName;

         if (sFileName.indexOf("\\framework\\") > 0) {
            sShortName = sFileName.substring(sFileName.indexOf("\\framework\\") + 1);
         }

         String sToDoFile = "";

         if (sOldFile.length() == 0) {
            // psFile = getJIndent(psBaseDir, psFile);

            psbLog.append("CREATED - " + sShortName + "\n");
            phsFiles.add(Util.writeToFile(sFileName, psFile));

            sToDoFile = updateFile(psBaseDir, psbLog, sFileName, sShortName, psFile, psFile, phsFiles);
         } else {
            sToDoFile = updateFile(psBaseDir, psbLog, sFileName, sShortName, psFile, sOldFile, phsFiles);
         }

         // analyse the toDo's
         if (sToDoFile.length() > 0) {
            psbLog.append(getToDoInfo(sToDoFile));
         }
      }

   }

   //   public static byte btCounter = 1;
   //
   //   /**
   //    * Method getJIndent
   //    *
   //    * @param psBaseDir
   //    * @param psText
   //    * @return
   //    *
   //    * @author Andreas Brod
   //    */
   //   public static synchronized String getJIndent(String psBaseDir, String psText)
   //   {
   //      if (psText.length() > 0) {
   //         btCounter++;
   //
   //         // validate if java exist
   //         if (!(new File("C:\\JBuilder8x\\jdk1.4\\bin\\java.exe")).exists()) {
   //            if (btCounter == 1) {
   //               ConfimDialog.info(null, "C:\\JBuilder8x\\jdk1.4 not available for JIndent");
   //            }
   //
   //            btCounter = 2;
   //
   //            return psText;
   //         }
   //
   //         String sFileName = "temp" + btCounter + ".java";
   //         String sBatchName = "temp" + btCounter + ".bat";
   //
   //         if (psBaseDir.endsWith("\\src")) {
   //            psBaseDir = psBaseDir.substring(0, psBaseDir.lastIndexOf("\\"));
   //         }
   //
   //         Util.writeToFile("temp/" + sFileName, psText);
   //         Util.writeToFile("temp/Author.txt", "author      = Generator\n\r");
   //
   //         File fTemp = new File("temp/temp" + btCounter + ".java");
   //         String sPath = fTemp.getParentFile().getAbsolutePath();
   //         String sBatch = "";
   //
   //         if (sPath.indexOf(":") > 0) {
   //            sBatch += sPath.substring(0, sPath.indexOf(":") + 1) + "\n";
   //         }
   //
   //         sBatch += "cd " + sPath + "\n";
   //         sBatch += "C:\\JBuilder8\\jdk1.4\\bin\\java.exe -jar \"" + psBaseDir
   //               + "\\extFiles\\JIdent\\Jindent.jar\" -auto -r -p \"" + psBaseDir
   //               + "\\extFiles\\JIdent\\ifao-style.jin\" -envf " + sPath
   //               + "\\Author.txt -nobak -time \"" + fTemp.getAbsolutePath() + "\"\n";
   //         sBatch += "exit\n";
   //
   //         Util.writeToFile("temp/" + sBatchName, sBatch);
   //
   //         File fBatch = new File(sPath + "\\" + sBatchName);
   //
   //         Util.exec("start /WAIT cmd.exe /c \"" + fBatch.getAbsolutePath() + "\" ");
   //
   //         psText = UtilBase.loadFromFile("temp/" + sFileName);
   //
   //         fTemp.delete();
   //         fBatch.delete();
   //      }
   //
   //      return psText;
   //   }
   //

   /**
    * Method getToDoInfo
    *
    *
    * @param psText
    * @return
    *
    * @author $author$
    */
   public static String getToDoInfo(String psText)
   {
      int iStart = psText.indexOf("@todo ");
      StringBuffer sb = new StringBuffer();

      while (iStart > 0) {

         int iEnd = psText.indexOf("*/", iStart);

         try {
            String sToDo = Util.replaceString(psText.substring(psText.lastIndexOf("\n", iStart) + 1, iEnd), "\r", "");

            sb.append(sToDo + "\n");
         }
         catch (Exception ex) {}

         iStart = psText.indexOf("@todo ", iStart + 1);
      }

      return sb.toString();
   }

   /**
    * Method getUnFormatedProvider
    *
    * @param psProvider
    * @return
    *
    * @author Andreas Brod
    */
   public static String getUnFormatedProvider(String psProvider)
   {
      boolean bUpper = true;
      String sAgent = "";

      while (psProvider.startsWith("!")) {
         psProvider = psProvider.substring(1);
      }

      while (psProvider.startsWith("*")) {
         psProvider = psProvider.substring(1);
      }

      for (int i = 0; i < psProvider.length(); i++) {
         if (psProvider.charAt(i) == '_') {
            bUpper = true;
         } else {
            sAgent += bUpper ? psProvider.substring(i, i + 1).toUpperCase() : psProvider.substring(i, i + 1).toLowerCase();
            bUpper = false;
         }
      }

      return sAgent;

   }

   /**
    * Method getCamelCaseName
    *
    *
    * @param psName
    * @return
    *
    * @author Andreas Brod
    */
   public static String getCamelCaseName(String psName)
   {
      String sCamelCaseName = "";
      boolean bLast = false;

      for (int i = 0; i < psName.length(); i++) {
         char c = psName.charAt(i);

         if ((c >= 'A') && (c <= 'Z')) {
            if (bLast) {
               sCamelCaseName += '_';
            }

            sCamelCaseName += c;
            bLast = false;
         } else if ((c >= 'a') && (c <= 'z')) {
            sCamelCaseName += (char) (c - 32);
            bLast = true;
         } else {
            sCamelCaseName += c;
            bLast = false;
         }
      }

      if (sCamelCaseName.toUpperCase().endsWith("_TYPE")) {
         sCamelCaseName =
            sCamelCaseName.substring(0, sCamelCaseName.length() - 5) + sCamelCaseName.substring(sCamelCaseName.length() - 4);
      }

      return sCamelCaseName;
   }

   /**
    * Method getElements
    *
    *
    * @param psText
    * @return
    *
    * @author Andreas Brod
    */
   private static Hashtable<String, String> getElements(String psText)
   {
      Hashtable<String, String> ht = new Hashtable<>();
      BufferedReader reader = new BufferedReader(new StringReader(psText));
      String sLine = "";
      String sKey = "";
      String sText = "";

      try {
         while ((sLine = reader.readLine()) != null) {
            String sTrim = sLine.trim();

            if (sTrim.startsWith("// [START:")) {
               sKey = sTrim.substring(sTrim.indexOf(":") + 1, sTrim.indexOf("]"));
               sText = sLine + "\n";
            } else {
               sText += sLine + "\n";
            }

            if (sTrim.startsWith("// [END:") && (sKey.length() > 0)) {
               ht.put(sKey, sText);

               sKey = "";
            }

         }
      }
      catch (IOException ex) {}

      return ht;
   }

   /**
    * Method getStartEnd
    *
    *
    * @param psText
    * @return
    *
    * @author Andreas Brod
    */
   private static String getStartEnd(String psText)
   {
      psText += "  ";

      String sRet = "";

      try {
         int iStart = psText.indexOf("//");

         while ((iStart > 0) && (psText.charAt(iStart + 2) >= ' ') && (psText.charAt(iStart + 3) >= ' ')) {
            iStart = psText.indexOf("//", iStart + 2);
         }

         int iEnd = psText.lastIndexOf("//");

         while ((iEnd > 0) && (psText.charAt(iEnd + 2) >= ' ') && (psText.charAt(iEnd + 3) >= ' ')) {
            iEnd = psText.lastIndexOf("//", iEnd - 2);
         }

         iStart = psText.indexOf("\n", iStart) + 1;
         iEnd = psText.lastIndexOf("\n", iEnd) + 1;

         if ((iStart >= 0) && (iStart < iEnd)) {
            sRet = psText.substring(iStart, iEnd);
         }
      }
      catch (Exception ex) {}

      return sRet;
   }

}
