package dtdinfo;


import ifaoplugin.Util;

import java.io.*;
import java.util.*;

import net.ifao.xml.XmlObject;


/** 
 * The class CompareTransformers compares all r2a Transformers 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author brod 
 */
public class CompareTransformers
{

   private static PrintStream pSysOut;

   /** 
    * @param psArgs arguments 
    * 
    */
   public static void main(String[] psArgs)
   {
      try {
         StringBuilder sbLog = new StringBuilder();
         long start = System.currentTimeMillis();
         String sBaseDir = "c:\\arctic\\Workspace\\arctic";
         if (psArgs.length > 0) {
            sBaseDir = psArgs[0];
         }
         String sCorrectFile = "c:\\BuildServer\\conf\\CompareTransformers.xml";
         if (psArgs.length > 1) {
            sCorrectFile = psArgs[1];
         }
         File fPath = new File(sBaseDir);

         String sTransformerVersion = Util.getVERSION();
         String arcticVersion = getArcticVersion(fPath);

         String sVersion2 = "arcticCT." + sTransformerVersion;
         File fPath2 = new File(fPath.getParentFile(), sVersion2);
         if (!fPath2.exists()) {
            fPath2.mkdirs();
         }

         sbLog.append("##################################################\n");
         sbLog.append("# CompareTransformers Log File\n#\n");
         sbLog.append("# Created on " + new Date().toString() + "\n");
         sbLog.append("# Base directory " + fPath.getAbsolutePath() + "\n");
         sbLog.append("# Arctic Version " + arcticVersion + "\n");
         sbLog.append("# Transformer Version " + sTransformerVersion + "\n");
         sbLog.append("##################################################\n");

         // change the outputStream
         pSysOut =
            new PrintStream(new FileOutputStream(new File(fPath2, "CompareTransformers.log")));
         Util.setOutput(pSysOut);

         Hashtable<File, File> copySources = copySources(fPath, fPath2);
         // start to analyse
         DtdGenerator.analyse(fPath2.getAbsolutePath(), pSysOut, new HashSet<File>());

         Hashtable<String, String> htResults = new Hashtable<String, String>();
         HashSet<String> hsProviders = new HashSet<String>();
         HashSet<String> hsAgents = new HashSet<String>();
         XmlObject result = new XmlObject("<Result />").getFirstObject();
         result.setAttribute("arcticVersion", arcticVersion);
         result.setAttribute("transformerVersion", sTransformerVersion);
         compareTransformers(copySources, htResults, hsProviders, hsAgents, result, sbLog,
               sCorrectFile);

         sbLog.append("\nTime: " + (System.currentTimeMillis() - start) / 1000.0D + "\n");
         String sLog = sbLog.toString();

         // remove invalid providers (common Transformers)
         hsProviders.remove("???");

         // create html
         StringBuilder sbHtml = getHtml(sbLog);

         // write the result Files
         writeFile(new File(fPath2, "CompareTransformers.html").getAbsolutePath(),
               sbHtml.toString());
         writeFile(new File(fPath2, "CompareTransformers.xml").getAbsolutePath(), result.toString());
         writeFile(new File(fPath2, "CompareTransformers.txt").getAbsolutePath(), sLog);

         // write the result to the console
         System.out.println(sLog);
         pSysOut.close();
      }
      catch (Throwable th) {
         // just in case
         th.printStackTrace();
      }
      // reset the output stream
      Util.removeOutput();
      System.exit(0);
   }

   /** 
    * The method returns the Html 
    * 
    * @param psbLog StringBuilder Log object
    * @return html (as StringBuilder object)
    * 
    * @author brod 
    */
   private static StringBuilder getHtml(StringBuilder psbLog)
   {
      StringBuilder sbHtml = new StringBuilder();
      sbHtml.append("<html>\n");
      sbHtml.append("<body>\n");
      sbHtml.append("<h1>Integritytest of the arctic transformers</h1><hr>\n");
      String sCode = "<code style='white-space:nowrap;'>";
      StringTokenizer st = new StringTokenizer(psbLog.toString(), "\n");
      while (st.hasMoreTokens()) {
         String sLine =
            st.nextToken().replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");

         if (sLine.startsWith("Time: ")) {
            sLine = "<b>" + sLine + "</b><br>";
         } else if (sLine.startsWith("# Created on")) {
            sLine = "<b>StartDate: " + sLine.substring(12).trim() + "</b><br>";
         } else if (sLine.startsWith("OK (")) {
            sLine = "<b style='color:green'>" + sLine + "</b><br>";
         } else if (sLine.startsWith("Test") && (sLine.indexOf("run:") > 0)) {
            StringTokenizer st1 = new StringTokenizer(sLine, ",");

            if (st1.countTokens() == 3) {
               sLine =
                  "<b style='color:green'>" + st1.nextToken() + "</b>, "
                        + "<b style='color:C0A000'>" + st1.nextToken() + "</b>, "
                        + "<b style='color:red'>" + st1.nextToken() + "</b>";
            }

            sLine += "<br>";

         } else if (sLine.startsWith("There ")) {
            String sCol = "#C0A000";

            if (sLine.indexOf("error") > 0) {
               sCol = "red";
            }

            sLine = "<b style='color:" + sCol + ";'>" + sLine + "</b><br>";
         } else {
            sLine = sCode + sLine + "</code><br>";

         }

         if (!sLine.startsWith(sCode + "#")) {
            sbHtml.append(sLine.replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;") + "\n");
         }
      }
      sbHtml.append("</body>\n");
      sbHtml.append("</html>");
      return sbHtml;
   }

   /** 
    * @param pfPath File Path
    * @return the ArcticVersion
    * 
    * @author brod 
    */
   private static String getArcticVersion(File pfPath)
   {
      try {
         File file = new File(pfPath, "src\\net\\ifao\\util\\Version.java");
         String loadFile = Util.loadFile(file.getAbsolutePath());
         int iPos = loadFile.indexOf("final String ARCTIC_RELEASE =");
         iPos = loadFile.indexOf("\"", iPos) + 1;
         return loadFile.substring(iPos, loadFile.indexOf("\"", iPos));
      }
      catch (Exception ex) {
         return "???";
      }
   }

   /** 
    * This method writes a File 
    * 
    * @param psFileName FileName 
    * @param psText Text 
    * 
    * @author brod 
    */
   private static void writeFile(String psFileName, String psText)
   {
      try {
         FileWriter fileWriter = new FileWriter(new String(psFileName));
         fileWriter.write(psText);
         fileWriter.close();
      }
      catch (IOException e) {
         // should never happen
         e.printStackTrace();
      }
   }

   /** 
    * This method compares the Transformers 
    * 
    * @param phtCopySources copySources Hashtable 
    * @param phtResults Results Hashtable
    * @param phsProviders set of Providers 
    * @param phsAgentsseto of  hsAgents 
    * @param pXmlResult Xml result Object 
    * @param psbResult Result StringBuilder 
    * @param psCorrectFile CorrectFile
    * 
    * @author brod 
    */
   private static void compareTransformers(Hashtable<File, File> phtCopySources,
                                           Hashtable<String, String> phtResults,
                                           HashSet<String> phsProviders, HashSet<String> phsAgents,
                                           XmlObject pXmlResult, StringBuilder psbResult,
                                           String psCorrectFile)
   {
      XmlObject correctFile = null;
      if (psCorrectFile != null) {
         pSysOut.append(">>> Use CorrectFile '" + psCorrectFile + "'\n");
         try {
            correctFile = new XmlObject(new File(psCorrectFile)).getFirstObject();
         }
         catch (Exception ex) {
            pSysOut.append(ex.getLocalizedMessage() + "\n");
            correctFile = null;
         }
      }
      if (correctFile == null) {
         correctFile = new XmlObject("<Correct />").getFirstObject();
      }
      int iCounter = 0;
      int iTotal = 0;
      int iError = 0;
      int iFailure = 0;

      String sAddText = "";
      // get all transformers
      File[] files = phtCopySources.keySet().toArray(new File[0]);
      Arrays.sort(files);
      for (File file : files) {
         File bakFile = phtCopySources.get(file);
         // get the requestType
         String sAgent = file.getName();
         if (sAgent.startsWith("?")) {
            continue;
         }
         String sProvider;
         try {
            sAgent = sAgent.substring("TransformerHandler".length(), sAgent.lastIndexOf("."));
            sProvider = file.getAbsolutePath().replaceAll("\\\\", "/");
            sProvider =
               sProvider.substring(sProvider.lastIndexOf("/agents/") + 8,
                     sProvider.lastIndexOf("/framework/")).replaceAll("[/]", ".");
         }
         catch (Exception ex) {
            // Exception
            sAgent = file.getName();
            // ingore this entry 
            continue;
         }

         XmlObject xmlAgent =
            pXmlResult.createObject("Provider", "name", sProvider, true).createObject("Agent",
                  "name", sAgent, true);

         XmlObject correctAgent =
            correctFile.createObject("Provider", "name", sProvider, true).createObject("Agent",
                  "name", sAgent, true);

         StringBuilder sbVersion = new StringBuilder();
         long lCheckSum = compareTransformers(file, bakFile, sbVersion);
         String sVersion = sbVersion.toString();
         String sResult;
         xmlAgent.setAttribute("version", sVersion);
         xmlAgent.setAttribute("status", ".");
         iTotal++;

         if (lCheckSum == 0) {
            sResult = "#CFC";
            xmlAgent.setAttribute("text", "Rebuild is the same.");
         } else {
            xmlAgent.setAttribute("checksum", "" + lCheckSum);
            xmlAgent.setAttribute("status", "~");

            StringTokenizer st = new StringTokenizer(sVersion, ".");
            if (st.countTokens() == 3) {
               String sVer = "";
               while (st.hasMoreTokens()) {
                  String sNextToken = "00" + st.nextToken();
                  sVer += sNextToken.substring(sNextToken.length() - 3);
               }
               XmlObject checksum =
                  correctAgent.createObject("Version", "checksum", "" + lCheckSum, true);

               if (!checksum.getAttribute("id").equals(sVersion)) {
                  sResult = "#FCC";
                  xmlAgent.setAttribute("status", "E");
                  xmlAgent.setAttribute("text", "Created version does not match");
                  iCounter++;
                  sAddText +=
                     "\n" + iCounter + ") " + sProvider + " " + sAgent + " has version " + sVersion;
                  iError++;
               } else {
                  sResult = "#FFC";
                  xmlAgent.setAttribute("text", "Old version accepted");
               }
            } else {
               sResult = "#FFF";
               xmlAgent.setAttribute("status", "F");
               xmlAgent.setAttribute("text", "Unkown version " + sVersion);
               iCounter++;
               sAddText +=
                  "\n" + iCounter + ") " + sProvider + " " + sAgent + " has invalid version";
               iFailure++;
            }
         }
         if (iTotal % 40 == 1) {
            psbResult.append('\n');
         }
         psbResult.append(xmlAgent.getAttribute("status"));

         phtResults.put(sAgent + "/" + sProvider, sResult + "-" + sVersion);

         phsAgents.add(sAgent);
         phsProviders.add(sProvider);
      }
      psbResult.append("\n\nTests run: " + iTotal + ",  Failures: " + iFailure + ",  Errors: "
            + iError);
      if (sAddText.length() > 0) {
         psbResult.append("\n\nThe following Transformers contain deprecated code:");
         psbResult.append(sAddText);
      }
   }

   /** 
    * private  method compareTransformers 
    * 
    * @param pJavaFile JavaFile
    * @param pBakFile bakFile 
    * @param sbVersion Stringbuilder for Version  
    * @return long value (calculated after Adler-32 checksum)
    * 
    * @author brod 
    */
   private static long compareTransformers(File pJavaFile, File pBakFile, StringBuilder sbVersion)
   {
      String sTransformer = readTransformer(pJavaFile, new StringBuilder());
      String sTransformerBak = readTransformer(pBakFile, sbVersion);
      if (sTransformer.equals(sTransformerBak)) {
         return 0;
      }

      // Compute Adler-32 checksum
      long lCheckSum = Util.getCheckSum(sTransformer.getBytes());
      return lCheckSum != 0 ? lCheckSum : 1;
   }

   /** 
    * private method readTransformer 
    * 
    * @param pJavaFile java file 
    * @param psbVersion Version stringbuilder  
    * @return read transformer string 
    * 
    * @author brod 
    */
   private static String readTransformer(File pJavaFile, StringBuilder psbVersion)
   {
      String sVersion = "???";

      StringBuilder sb = new StringBuilder();
      try {
         BufferedReader reader = new BufferedReader(new FileReader(pJavaFile));
         String sLine;
         while ((sLine = reader.readLine()) != null) {
            String sLINE = "";
            char[] charArray = sLine.toUpperCase().toCharArray();
            for (char element : charArray) {
               if (element > ' ') {
                  sLINE += element;
               }
            }
            if (sLINE.contains("//")) {
               if (sLINE.startsWith("//CLASSGENERATEDWITH") && sLINE.contains("VERSION")) {
                  sVersion = sLine.substring(sLine.lastIndexOf(" ") + 1);
               }
               sLINE = sLINE.substring(0, sLINE.indexOf("//"));
            }
            if ((sLine.length() == 0) || sLine.trim().startsWith("import ")
                  || sLINE.startsWith("/*") || sLINE.startsWith("*")) {
               // ignore this line
            } else {
               sb.append(sLINE);
            }
         }
         reader.close();
      }
      catch (Exception e) {
         // just for this case, which should never happen
         e.printStackTrace();
      }
      psbVersion.append(sVersion);

      return sb.toString();
   }

   /** 
    * private method copySources 
    * 
    * @param psPathFrom Path from 
    * @param psPathTo Path to 
    * @return hashtable of copied sources 
    * 
    * @author brod 
    */
   private static Hashtable<File, File> copySources(File psPathFrom, File psPathTo)
   {
      // copy dtd files
      String[] dtdFiles =
         { "Agents.xml", "RuleMap.xml", "ArcticRequest.dtd", "ArcticResponse.dtd",
               "ArcticPnrElementInfos.xml", "ArcticPnrElementInfos.xsd",
               "ArcticPnrElementInfos.bind.xml" };
      for (String dtdFile : dtdFiles) {
         copyFile(Util.getConfFile(psPathFrom.getAbsolutePath(), dtdFile),
               Util.getConfFile(psPathTo.getAbsolutePath(), dtdFile), true);
      }

      // copy requestResponse generated classes
      copyFile(new File(psPathFrom, "src-gen\\net\\ifao\\arctic\\xml\\response"), new File(psPathTo,
            "src-gen\\net\\ifao\\arctic\\xml\\response"), false);
      copyFile(new File(psPathFrom, "src-gen\\net\\ifao\\arctic\\xml\\request"), new File(psPathTo,
            "src-gen\\net\\ifao\\arctic\\xml\\request"), false);

      Hashtable<File, File> htTransformers = new Hashtable<File, File>();
      // copy transformers
      copyTransformers(htTransformers, new File(psPathFrom, "src\\net\\ifao\\arctic\\agents"),
            new File(psPathTo, "\\src\\net\\ifao\\arctic\\agents"));
      return htTransformers;
   }

   /** 
    * private  method copyTransformers 
    * 
    * @param phtTransformers Hashtable of Transformers 
    * @param pFile1 File1 
    * @param pFile2 File2 
    * 
    * @author brod 
    */
   private static void copyTransformers(Hashtable<File, File> phtTransformers, File pFile1,
                                        File pFile2)
   {
      if (pFile1.isDirectory()) {
         File[] listFiles = pFile1.listFiles();
         int iMax = listFiles.length;
         if (pFile1.getName().equalsIgnoreCase("agents")) {
            // iMax = 3;
         }
         for (File file : listFiles) {
            File file2 = new File(pFile2, file.getName());
            if (file.isDirectory()) {
               if (iMax >= 0) {
                  copyTransformers(phtTransformers, file, file2);
                  iMax--;
               }
            } else {
               String sFileName = file.getName();
               if (sFileName.startsWith("TransformerHandler") && sFileName.endsWith(".java")) {
                  String sName = file2.getName();
                  String sAgent =
                     sFileName.substring("TransformerHandler".length(), sFileName.lastIndexOf("."));
                  copyFile(file, file2, true);
                  // add to list of transformers
                  File file2Bak =
                     new File(file2.getParentFile(), sName.substring(0, sName.lastIndexOf("."))
                           + ".bak");
                  copyFile(file, file2Bak, true);
                  phtTransformers.put(file2, file2Bak);
                  // copy the 'main' java class
                  File fBaseDir = file.getParentFile().getParentFile().getParentFile();
                  File fBaseDir2 = file2.getParentFile().getParentFile().getParentFile();

                  String[] sAgentFiles =
                     { sAgent + ".java", sAgent + "_req.html", sAgent + "_res.html",
                           sAgent + "_reqres.xml" };
                  for (String sAgentFile : sAgentFiles) {
                     copyFile(new File(fBaseDir, sAgentFile), new File(fBaseDir2, sAgentFile), true);
                  }
               }
            }
         }
      } else {

      }
   }

   /** 
    * private method copyFile 
    * 
    * @param pFile1 file1 
    * @param pFile2 file2 
    * @param pbOverwrite if true, Overwrite the files 
    * 
    * @author brod 
    */
   private static void copyFile(File pFile1, File pFile2, boolean pbOverwrite)
   {
      if (pFile1.exists()) {
         if (pbOverwrite && pFile2.exists()) {
            return;
         }
         if (pFile1.isDirectory()) {
            // copy subFiles
            File[] listFiles = pFile1.listFiles();
            for (File listFile : listFiles) {
               copyFile(listFile, new File(pFile2, listFile.getName()), pbOverwrite);
            }
         } else {

            pSysOut.println("... copy to " + pFile2.getAbsolutePath());
            if (!pFile2.getParentFile().exists()) {
               pFile2.getParentFile().mkdirs();
            }

            try {
               BufferedInputStream in = new BufferedInputStream(new FileInputStream(pFile1));
               BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(pFile2));
               byte[] bytes = new byte[4096];
               int count;
               while ((count = in.read(bytes)) > 0) {
                  out.write(bytes, 0, count);
               }
               out.close();
               in.close();
            }
            catch (IOException e) {
               // should never happen
               e.printStackTrace();
            }
         }
      }
   }

}
