package ifaoplugin;


import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import net.ifao.xml.XmlObject;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;


/**
 * Util class
 * 
 * <p>
 * Copyright &copy; 2010, i:FAO
 * 
 * @author brod
 */
public class Util
{
   private static final String ARCTIC_CONFIGURATION_CLASS =
      "net.ifao.arctic.io.config.Configuration";

   /**
    * return a specific arctic configuration files, which is within an
    * arctic root directory
    * 
    * @param psRootDirectory the arctic root directory
    * @param psFileName the name of the configuration file
    * 
    * @return the file handle for this configuration file
    */
   public static File getConfFile(String psRootDirectory, String psFileName)
   {
      // dtd wins
      File confFile = new File(psRootDirectory, "dtd" + File.separator + psFileName);
      if (confFile.exists()) {
         return confFile;
      }
      // use the confCirectory
      if (psFileName.endsWith("xml")) {
         return new File(psRootDirectory, "conf" + File.separator + psFileName);
      }
      return new File(psRootDirectory,
            "conf" + File.separator + "definitions" + File.separator + psFileName);
   }

   /**
    * This method returns the current plugin VERSION
    * 
    * @return the current plugin VERSION
    * 
    * @author brod
    */
   public static String getVERSION()
   {
      String sVersion = "0.0.0";
      File f = new File("META-INF/MANIFEST.MF");
      InputStream in = null;
      try {
         if (f.exists()) {
            in = new FileInputStream(f);
         }
      }
      catch (Exception e) {}
      if (in == null) {
         in = Util.class.getResourceAsStream("/META-INF/MANIFEST.MF");
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      String sLine;
      try {
         while ((sLine = reader.readLine()) != null) {
            if (sLine.startsWith("Bundle-Version:")) {
               StringTokenizer st =
                  new StringTokenizer(sLine.substring(sLine.indexOf(":") + 1), " .:");
               sVersion = "" + (Integer.parseInt(st.nextToken()) + 1);
               while (st.hasMoreTokens()) {
                  sVersion += "." + st.nextToken();
               }
            }
         }
      }
      catch (Exception e) {}
      finally {
         try {
            in.close();
         }
         catch (Exception e) {}
      }
      return sVersion;
   }

   /**
    * Method loadFromFile
    * 
    * @param psFileName
    * @return
    * 
    * @author Andreas Brod
    */
   public static String loadFromFile(String psFileName)
   {
      return loadFromFile(new File(psFileName));
   }

   /** 
    * loads a text from file. 
    * 
    * @param pFile f object File
    * @return loaded text from file
    * 
    * @author Brod 
    */
   public static String loadFromFile(File pFile)
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      if (!pFile.isFile()) {
         return "";
      }

      try {
         _systemOut.println("Load from file " + pFile.getAbsolutePath());

         FileInputStream bf = new FileInputStream(pFile);

         byte[] chars = new byte[8000];
         int anz = 0;

         while ((anz = bf.read(chars)) > 0) {
            out.write(chars, 0, anz);
         }

         bf.close();
      }
      catch (FileNotFoundException ex) {
         System.err.println("Problems with file " + pFile.getName());
         System.err.println(ex.getMessage());

         // ex.printStackTrace();
         return "";
      }
      catch (IOException ex1) {
         System.err.println("Problems with file " + pFile.getName());
         ex1.printStackTrace();

         return "";
      }

      try {
         if (isXmlFile(pFile.getName())) {
            return new String(out.toByteArray(), "UTF-8");
         }
         return new String(out.toByteArray());
      }
      catch (UnsupportedEncodingException ex2) {}
      String sRet = out.toString();
      return sRet;

   }

   /**
    * method isXmlFile return true if file ends with *.x*
    * 
    * @param psFileName The Mane of the File
    * @return true if file ends with *.x*
    * 
    * @author brod
    */
   protected static boolean isXmlFile(String psFileName)
   {
      String sEnd = psFileName.substring(psFileName.lastIndexOf(".") + 1);
      if (sEnd.startsWith("x")) {
         return true;
      }
      return false;
   }

   /**
    * Method replaceString
    * 
    * @param psText
    * @param psStartString
    * @param psReplaceWith
    * @return
    * 
    * @author Andreas Brod
    */
   public static String replaceString(String psText, String psStartString, String psReplaceWith)
   {
      int iStart = psText.lastIndexOf(psStartString);

      if (psReplaceWith.startsWith(psStartString)) {
         return psText;
      }

      while (iStart >= 0) {
         int iEnd = iStart + psStartString.length();
         String s1 = (iStart > 0) ? psText.substring(0, iStart) : "";
         String s2 = (iEnd < psText.length()) ? psText.substring(iEnd) : "";

         psText = s1 + psReplaceWith + s2;
         iStart = psText.lastIndexOf(psStartString, iStart + 1);
      }

      return psText;
   }

   /**
    * Method writeFile
    * 
    * 
    * @param psFileName
    * @param psAddiText additional text String
    * @return
    * 
    */
   public static File writeToFile(String psFileName, String psAddiText)
   {
      int iCount = 0;

      try {
         while (hsFiles.contains(psFileName) && (iCount < 100)) {
            Thread.sleep(50);

            iCount++;
         }
      }
      catch (InterruptedException e) {
         e.printStackTrace();
      }

      hsFiles.add(psFileName);

      File f = new File(psFileName);
      if ((f.getParentFile() != null) && !f.getParentFile().exists()) {
         f.getParentFile().mkdirs();
      }

      try {
         _systemOut.println("Write to file " + psFileName + " (" + psAddiText.length() + " bytes)");

         FileOutputStream out = new FileOutputStream(f, false);
         String sAddiText = psAddiText;
         if (isXmlFile(psFileName)) {
            if (!psAddiText.startsWith("<?xml")) {
               sAddiText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + psAddiText;
            }

            out.write(sAddiText.getBytes("UTF-8"));
         } else {
            if (psFileName.endsWith(".java")) {
               try {
                  Class<?>[] params = { f.getClass(), psAddiText.getClass() };
                  Object[] values = { f, psAddiText };
                  Method method = Class.forName("net.ifao.plugins.tools.CodeFormatterApplication")
                        .getDeclaredMethod("formatSourceCode", params);
                  // start the codeFormatter to format java code
                  String formatFile = (String) method.invoke(null, values);
                  // String formatFile = CodeFormatterApplication.formatSourceCode(f, psAddiText);
                  if (formatFile != null) {
                     _systemOut.println("... formatted file");
                     sAddiText = formatFile;
                  }
               }
               catch (Exception ex) {
                  // could not format file
               }
            }
            out.write(sAddiText.getBytes());
         }
         out.close();


      }
      catch (Exception ex) {
         ex.printStackTrace();
      }

      hsFiles.remove(psFileName);
      return f;
   }

   private static HashSet<String> hsFiles = new HashSet<String>();
   private static PrintStream _systemOut = System.out;
   private static Stack<PrintStream> _stackPrintStream = new Stack<PrintStream>();
   private static Hashtable<String, File> _htPackageDirectories;
   private static String _sBaseArctic;

   /**
    * Method camelCase
    * 
    * @param psText
    * @return
    * 
    * @author Andreas Brod
    */
   public static String camelCase(String psText)
   {
      return camelCase(psText, true);
   }

   /**
    * Method camelCase
    * 
    * @param psText
    * @param pbReplaceFirst
    * @return
    * 
    * @author Andreas Brod
    */
   public static String camelCase(String psText, boolean pbReplaceFirst)
   {
      char[] charArray = psText.toCharArray();
      StringBuilder sbText = new StringBuilder();
      boolean bUpper = pbReplaceFirst;
      for (char c : charArray) {
         if ((c == '_') || (c == '-')) {
            bUpper = true;
         } else if ((c >= 'a') && (c <= 'z') && bUpper) {
            sbText.append((char) (c - 32));
            bUpper = false;
         } else {
            sbText.append(c);
            bUpper = false;
         }
      }
      return sbText.toString();
   }

   /**
    * This method can be used to initSwing
    * 
    * @author brod
    */
   public static void initSwing()
   {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Method exec
    * 
    * @param psArgs
    * 
    * @author Andreas Brod
    */
   public static void exec(String psArgs)
   {
      exec(psArgs, true);
   }

   /**
    * Method exec
    * 
    * @param psArgs
    * @param pbPrint
    * 
    * @author Andreas Brod
    */
   public static void exec(String psArgs, boolean pbPrint)
   {
      if (psArgs.endsWith(".bat") && !psArgs.startsWith("call ") && !psArgs.startsWith("start ")) {
         psArgs = "call " + psArgs;
      }

      try {
         if (pbPrint) {
            _systemOut.println("Execing " + psArgs);
         }

         psArgs = "cmd /c " + psArgs;

         Runtime rt = Runtime.getRuntime();

         Process proc = rt.exec(psArgs);

         // any error???
         int exitVal = proc.waitFor();

         if (pbPrint) {
            _systemOut.println("ExitValue: " + exitVal);
         }
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }
      catch (Throwable t) {
         t.printStackTrace();
      }

   }

   /**
    * This method loads a File
    * 
    * @param psFileName name of the file
    * @return content of the file
    * 
    * @author brod
    */
   public static String loadFile(String psFileName)
   {
      byte[] out = readBytes(psFileName);

      try {
         if (isXmlFile(psFileName)) {
            return new String(out, "UTF-8");
         }
      }
      catch (UnsupportedEncodingException ex2) {}
      return new String(out);
   }

   /**
    * This method reads Bytes from a file
    * 
    * @param psFileName FileName
    * @return content of the file
    * 
    * @author brod
    */
   protected static byte[] readBytes(String psFileName)
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      try {
         InputStream inputStream = getInputStream(psFileName);

         byte[] chars = new byte[8000];
         int anz = 0;

         while ((anz = inputStream.read(chars)) > 0) {
            out.write(chars, 0, anz);
         }

         inputStream.close();
      }
      catch (Exception ex1) {}
      return out.toByteArray();
   }

   /**
    * This method returns an InputStream (from a file or the content
    * of the compiled package.
    * 
    * @param psFileName FileName
    * @return content of the file
    * @throws IOException
    * 
    * @author brod
    */
   private static InputStream getInputStream(String psFileName)
      throws IOException
   {
      File f = new File(psFileName);
      if (!f.exists()) {
         return Util.class.getResourceAsStream("/" + psFileName.replaceAll("\\\\", "/"));
      }

      return new FileInputStream(f);
   }

   /**
    * This method shows an Exception with a JOptionPane
    * 
    * @param pException exception
    * 
    * @author brod
    */
   public static void showException(Exception pException)
   {
      OutputStream out = new ByteArrayOutputStream();
      pException.printStackTrace(new PrintStream(out));
      showException(out.toString());
   }

   /**
    * This method shows an Exception with a JOptionPane
    * 
    * @param psException exception String
    * 
    * @author brod
    */
   public static void showException(String psException)
   {
      JOptionPane.showMessageDialog(null, psException);
   }

   /**
    * this method returns an ImageIcon
    * 
    * @param psIconName IconName
    * @return ImageIcon
    * 
    * @author brod
    */
   public static ImageIcon getImageIcon(String psIconName)
   {
      return new ImageIcon(Util.readBytes("icons/" + psIconName));
   }

   /**
    * This method sets the system Output
    * 
    * @param pOutputStream OutputStream
    * 
    * @author brod
    */
   public static void setOutput(PrintStream pOutputStream)
   {
      synchronized (_stackPrintStream) {
         _stackPrintStream.add(_systemOut);
         _systemOut = pOutputStream;
      }
   }

   /** 
    * removes the output. 
    * 
    * @author Brod 
    */
   public static void removeOutput()
   {
      synchronized (_stackPrintStream) {
         if (_stackPrintStream.size() > 0) {
            _systemOut = _stackPrintStream.pop();
         } else {
            _systemOut = System.out;
         }
      }
   }

   /**
    * Method getClipboard
    * 
    * @return
    * 
    * @author Andreas Brod
    */
   public static String getClipboard()
   {
      Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

      try {
         if ((t != null) && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String text = (String) t.getTransferData(DataFlavor.stringFlavor);

            return text;
         }
      }
      catch (UnsupportedFlavorException e) {}
      catch (IOException e) {}

      return null;
   }

   /**
    * Method validDir
    * 
    * 
    * @param psDirToValidate
    * @return
    * 
    * @author Andreas Brod
    */
   public static boolean validDir(String psDirToValidate)
   {
      if (psDirToValidate.endsWith("\\") || psDirToValidate.endsWith("/")) {
         psDirToValidate = psDirToValidate.substring(0, psDirToValidate.length() - 1);
      }

      if (psDirToValidate.length() == 0) {
         return false;
      }

      if (psDirToValidate.indexOf("\\") > 0) {
         validDir(psDirToValidate.substring(0, psDirToValidate.lastIndexOf("\\")));
      } else if (psDirToValidate.indexOf("/") > 0) {
         validDir(psDirToValidate.substring(0, psDirToValidate.lastIndexOf("/")));
      }

      File f = new File(psDirToValidate);

      if (!f.exists()) {
         return f.mkdir();
      }

      return true;
   }

   /**
    * Method deleteAllInDirectory
    * 
    * @param psDir
    * 
    * @author Andreas Brod
    */
   public static void deleteAllInDirectory(String psDir)
   {
      File f = new File(psDir);

      if (f.isDirectory()) {
         File[] files = f.listFiles();

         for (File file : files) {
            file.delete();
         }
      }

   }

   /**
    * Method getUntil
    * 
    * @param pTokens
    * @param psStartText
    * @return
    * 
    * @author Andreas Brod
    */
   public static String getUntil(StringTokenizer pTokens, String psStartText)
   {
      String sReturn = "";

      while (pTokens.hasMoreTokens()) {
         String s = pTokens.nextToken();

         if (s.startsWith(psStartText)) {
            return sReturn;
         }
         sReturn += s + "\n";
      }

      return sReturn;
   }

   /**
    * Method getUntil
    * 
    * @param pTokens
    * @param psEnd
    * @param psUp
    * @param psDown
    * @return
    * 
    * @author Andreas Brod
    */
   public static String getUntil(StringTokenizer pTokens, String psEnd, String psUp, String psDown)
   {
      String sReturn = "";
      int id = 0;

      while (pTokens.hasMoreTokens()) {
         String s = pTokens.nextToken();

         if (s.startsWith(psEnd) && (id == 0)) {
            return sReturn;
         }
         if (s.startsWith(psUp)) {
            id++;
         }

         if (s.startsWith(psDown)) {
            id--;
         }

         sReturn += s + "\n";
      }

      return sReturn;
   }

   /**
    * Method surroundHtml
    * 
    * @param psText
    * @return
    * 
    * @author Andreas Brod
    */
   public static String surroundHtml(String psText)
   {
      return "<html><body><font face='Arial'>" + psText + "</font></body></html>";
   }

   // -----------------------------------------------------------------------------

   /**
    * Method fillXml
    * 
    * @param pXmlObject
    * @param pjTree1
    * @param pTop
    * @return
    * 
    * @author Andreas Brod
    */
   public static String fillXml(XmlObject pXmlObject, JTree pjTree1, DefaultMutableTreeNode pTop)
   {
      String sText = "";

      if (pXmlObject.getFirstObject() != null) {
         sText = pXmlObject.getFirstObject().getHtmlString();
      }

      createNodes(pTop, pXmlObject);

      for (int i = 0; i < pjTree1.getRowCount(); i++) {
         pjTree1.expandRow(i);
      }

      return sText;
   }

   /**
    * Method fillXmlPlain
    * 
    * @param pXmlObject
    * @param pjTree1
    * @param pTop
    * @return
    * 
    * @author Andreas Brod
    */
   public static String fillXmlPlain(XmlObject pXmlObject, JTree pjTree1,
                                     DefaultMutableTreeNode pTop)
   {
      String sText = "";

      if (pXmlObject.getFirstObject() != null) {
         sText = pXmlObject.getFirstObject().toString();
      }

      createNodes(pTop, pXmlObject);

      for (int i = 0; i < pjTree1.getRowCount(); i++) {
         pjTree1.expandRow(i);
      }

      return sText;
   }

   /**
    * Method createNodes
    * 
    * 
    * @param pNode
    * @param pXmlObj
    * 
    * @author Andreas Brod
    */
   private static void createNodes(DefaultMutableTreeNode pNode, XmlObject pXmlObj)
   {
      pNode.removeAllChildren();

      if (pXmlObj == null) {
         return;
      }

      String[] attrs = pXmlObj.getAttributeNames();

      for (String attr : attrs) {
         pNode.add(new DefaultMutableTreeNode(attr));
      }

      String[] names = pXmlObj.getObjectNames();

      for (String name : names) {
         String s = pXmlObj.getCData(name);

         if (s.trim().length() > 0) {
            s = " = [" + s + "]";
         }

         XmlObject[] os = pXmlObj.getObjects(name);

         for (XmlObject element : os) {
            DefaultMutableTreeNode category = new DefaultMutableTreeNode(name + s);

            pNode.add(category);
            createNodes(category, element);
         }
      }
   }

   // FILE Communication

   /**
    * Method updateFile checks, if the file has been changed and saves the new content. Unchanged
    * files will not be saved (again).
    * 
    * @param psFileName file name
    * @param psNewContent content to be compared to the content of the file
    * @param psbResult for "logging" the result, might be <code>null</code>
    * @return true, if the file has been changed (and saved)
    * 
    * @author kaufmann
    */
   public static boolean updateFile(String psFileName, String psNewContent, StringBuilder psbResult)
   {
      boolean bChanged = false;
      String sOldContent = loadFromFile(psFileName);
      if (psNewContent.equals(sOldContent)) {
         if (psbResult != null) {
            psbResult.append(psFileName).append(" has not been changed\n");
         }
      } else {
         if (psbResult != null) {
            psbResult.append("* ").append(psFileName).append("\n");
         }
         writeToFile(psFileName, psNewContent);
         bChanged = true;
      }
      return bChanged;
   }

   /**
    * Method deleteFile
    * 
    * @param psFileName
    * 
    * @author Andreas Brod
    */
   public static void deleteFile(String psFileName)
   {
      File f = new File(psFileName);

      if (f.exists()) {
         writeToFile(psFileName + ".bak", loadFromFile(psFileName));
         f.delete();
      }
   }

   /**
    * Method replaceString
    * 
    * @param psText
    * @param psStartString
    * @param psEndString
    * @param psReplaceWith
    * @return
    * 
    * @author Andreas Brod
    */
   public static String replaceString(String psText, String psStartString, String psEndString,
                                      String psReplaceWith)
   {
      int iStart = psText.indexOf(psStartString);

      if ((iStart < 0) || psStartString.startsWith(psReplaceWith)) {
         return psText;
      }

      int iEnd = psText.indexOf(psEndString, iStart);

      while ((iStart >= 0) && (iEnd >= 0)) {
         iEnd += psEndString.length();

         String s1 = (iStart > 0) ? psText.substring(0, iStart) : "";
         String s2 = (iEnd < psText.length()) ? psText.substring(iEnd) : "";

         psText = s1 + psReplaceWith + s2;
         iStart = psText.indexOf(psStartString, iEnd);
         iEnd = psText.indexOf(psEndString, iStart);
      }

      return psText;
   }

   /**
    * Method replaceProvider
    * 
    * @param psText
    * @param psProvider
    * @param pGdsObject
    * @return
    * 
    * @author Andreas Brod
    */
   public static String replaceProvider(String psText, String psProvider, XmlObject pGdsObject)
   {
      boolean ok = false;

      String sOldProviders = "";
      try {
         XmlObject xmlProviders = new XmlObject(
               psText.substring(psText.indexOf("<Providers>"), psText.indexOf("</Providers>")))
                     .getFirstObject();
         sOldProviders = xmlProviders.getObject("Provider").toString();
      }
      catch (Exception e) {
         sOldProviders = "<Provider />";
      }

      String sProvider = "<Providers>\n";

      XmlObject queues = null;

      String sProviderReference = loadFromFile("data/ProviderReference.xml");

      if (sProviderReference.length() == 0) {
         sProviderReference = "<ProviderReference />";
      }

      XmlObject xmlProviderReference =
         (new XmlObject(sProviderReference)).createObject("ProviderReference");

      if (pGdsObject.getName().equals("root")) {
         pGdsObject = pGdsObject.getFirstObject();
      }

      String sProviderType = "";

      XmlObject[] gds = pGdsObject.getObjects("");
      StringTokenizer st = new StringTokenizer(psProvider, " ");

      while (st.hasMoreTokens()) {
         String sToken = st.nextToken();

         for (XmlObject gd : gds) {
            XmlObject[] provider = gd.getObjects("");

            for (int j = 0; (provider != null) && (j < provider.length); j++) {
               if (provider[j].getName().equals(sToken)) {

                  XmlObject xmlProfile1 = xmlProviderReference.getObject(sToken);

                  XmlObject xmlProvider = new XmlObject(sOldProviders).getFirstObject();

                  xmlProvider.setAttribute("id", gd.getName());
                  xmlProvider.setAttribute("type", gd.getName());
                  xmlProvider.setAttribute("profile", provider[j].getName());

                  if (xmlProfile1 != null) {
                     queues = xmlProfile1.getObject("Queues");

                     XmlObject providerProfiles = xmlProfile1.getObject("ProviderProfiles");
                     if (providerProfiles != null) {
                        providerProfiles.getObjects("");
                        // get attributes
                        String[] attributeNames = providerProfiles.getAttributeNames(true);
                        for (String attributeName : attributeNames) {
                           if (xmlProvider.getAttribute(attributeName).length() == 0) {
                              xmlProvider.setAttribute(attributeName,
                                    providerProfiles.getAttribute(attributeName));
                           }
                        }

                        // ignore additional Objects for Provider
                        //                        for (int k = 0; k < objects.length; k++) {
                        //                           if (!objects[k].getName().equals("!--")) {
                        //                              if (xmlProvider.getObject(objects[k].getName()) == null)
                        //                                 xmlProvider.addObject(objects[k]);
                        //                           }
                        //                        }
                     }
                  }

                  sProvider += xmlProvider.toString() + "\n";
                  sProviderType = gd.getName();
                  ok = true;
               }
            }
         }
      }

      sProvider += "  </Providers> ";

      if (ok) {
         if ((psText.indexOf("<Queues>") > 0) && (psText.indexOf("</Queues>") > 0)) {
            if (queues != null) {
               queues.deleteObjects("!--");
               queues.addElementObject(
                     new XmlObject("<!-- Queues where taken from data/ProviderReference.xml ("
                           + psProvider.trim() + ") -->"),
                     0);
            } else {

               // get queues
               queues = (new XmlObject(
                     psText.substring(psText.indexOf("<Queues>"), psText.indexOf("</Queues>") + 9)))
                           .getFirstObject();

               queues.deleteObjects("!--");
               queues.addElementObject(
                     new XmlObject("<!-- Enter Queues for " + psProvider.trim() + " -->"), 0);
            }

            String sQueues = queues.toString();

            psText = replaceString(psText, "<Queues>", "</Queues>", sQueues);
            psText = replaceString(psText, "<Queues />", sQueues);
         }

         psText = replaceString(psText, "<Providers>", "</Providers>", sProvider);
         psText = replaceString(psText, "<Providers/>", "<Providers />");
         psText = replaceString(psText, "<Providers />", sProvider);
      }

      // replace also providerId
      int iProviderId = psText.indexOf(" providerId=\"");

      while ((sProviderType.length() > 0) && (iProviderId > 0)) {
         iProviderId = psText.indexOf("\"", iProviderId) + 1;
         psText = psText.substring(0, iProviderId) + sProviderType
               + psText.substring(psText.indexOf("\"", iProviderId));

         iProviderId = psText.indexOf(" providerId=\"", iProviderId + 10);
      }

      return psText;
   }

   /**
    * Method getHtmlNoSpecial
    * 
    * @param psHtmlString
    * @return
    * 
    * @author Andreas Brod
    */
   public static String getHtmlNoSpecial(String psHtmlString)
   {
      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < psHtmlString.length(); i++) {
         char c = psHtmlString.charAt(i);

         switch (c) {

            case '<':
               sb.append("&lt;");
               break;

            case '>':
               sb.append("&gt;");
               break;

            default:
               sb.append(c);
         }
      }

      return sb.toString();
   }

   /**
    * Method getNoHtml
    * 
    * @param psHtmlString
    * @return
    * 
    * @author $author$
    */
   public static String getNoHtml(String psHtmlString)
   {
      StringBuffer sb = new StringBuffer();
      int iReturn = 0;
      int count0 = 0;

      if (psHtmlString.startsWith("<") || psHtmlString.startsWith("&lt;")) {
         count0 = -1;
      }

      int count = count0;
      boolean bText = false;

      for (int i = 0; i < psHtmlString.length(); i++) {
         char c = psHtmlString.charAt(i);

         switch (c) {

            case (char) 8240:
               sb.append(c + "\n");

               count = count0;

               iReturn = 0;
               break;

            case '\"':
               sb.append(c);

               bText = !bText;
               break;

            case '\n':
               if (iReturn != 2) {
                  sb.append("\n");

                  count = count0;

                  iReturn = 1;
               }

               count = count0;
               break;

            case '\r':
               if (iReturn != 1) {
                  sb.append("\n");

                  iReturn = 2;
               }

               count = count0;
               break;

            case '&':
               iReturn = 0;

               String s = "";
               int iEnd = i + 6;

               while ((c != ';') && (i + 1 < psHtmlString.length()) && (i < iEnd)) {
                  i++;

                  c = psHtmlString.charAt(i);
                  s += c;
               }

               if (bText) {
                  sb.append('&' + s);
               } else if (s.equals("lt;")) {
                  sb.append('<');
               } else if (s.equals("gt;")) {
                  sb.append('>');
               } else if (s.equals("amp;")) {
                  sb.append('&');
               } else if (s.equals("gt;")) {
                  sb.append('>');
               } else if (s.equals("quot;")) {
                  sb.append('\"');
               } else if (s.startsWith("#") && s.endsWith(";")) {
                  try {
                     sb.append((char) Integer.parseInt(s.substring(1, s.length() - 1)));
                  }
                  catch (NumberFormatException ex) {
                     sb.append('&' + s);
                  }
               } else {
                  sb.append('&' + s);
               }
               break;

            default:
               iReturn = 0;

               sb.append(c);

               if (count >= 0) {
                  count++;

                  if (count >= 64) {
                     sb.append("\n");

                     count = count0;
                  }
               }
         }
      }

      return formatXmlString(sb.toString(), false);
   }

   /**
    * The functionallity of method formatXmlString is described in the
    * previous method. The only difference is, that there is an additional
    * parameters if the Returns should be kept. If this flag is set to false
    * the Return (preformated) characters are ignored (if there is an empty
    * line between the tags).
    * 
    * @param psXmlString unformatted XML string
    * @param pbKeepReturn Keep return indicator
    * @return formatted XML string
    * 
    * @author Jochen Pinder, Andreas Brod
    */
   public static String formatXmlString(String psXmlString, boolean pbKeepReturn)
   {

      // int sXmlLength = psXmlString.length();
      // long lStart = System.currentTimeMillis();

      if (psXmlString == null) {
         return "";
      }

      // Initialize formatted XML string buffer
      StringBuffer sbFormatXml = new StringBuffer();

      // Initialize other variables
      // sbTag       = intermediate buffer to store a tag
      // sbIndent    = intermediate buffer to store the carriage return and the indent
      // bInTag      = true if the input XML string is inside a tag during parsing
      // bIsStartTag = true for <Hallo attr="2"> and <Hallo attr="2" />
      // bIsEndTag   = true for <Hallo attr="2" /> and </Hallo>
      StringBuffer sbTag = new StringBuffer();

      boolean bCharsBetweenTags = false;
      StringBuffer sbIndent;
      boolean bInTag = false;
      boolean bIsStartTag = true;
      boolean bIsEndTag = false;
      int iIndent = -2;

      char cLast = ' ';

      // Loop over all characters in the given XML string
      for (int iIndex = 0; iIndex < psXmlString.length(); iIndex++) {

         // Get next character from the given XML string
         char c = psXmlString.charAt(iIndex);

         switch (c) {

            case '<':
               bInTag = true;
               bIsStartTag = true;
               bIsEndTag = false;

               sbTag.append(c);
               break;

            case '>':
               bInTag = false;
               bCharsBetweenTags = false;

               sbTag.append(c);

               if (bIsStartTag && (cLast != '-')) {
                  iIndent += 2;
                  sbIndent = new StringBuffer();

                  sbIndent.append('\n');

                  for (int iInd = 0; iInd < iIndent; iInd++) {
                     sbIndent.append(' ');
                  }

                  sbFormatXml.append(sbIndent);
               }

               sbFormatXml.append(sbTag);

               if (bIsEndTag) {
                  iIndent -= 2;
               }

               sbTag = new StringBuffer();
               break;

            case '/':
               if (bInTag) {
                  if (sbTag.length() == 1) {
                     bIsStartTag = false;
                  }

                  bIsEndTag = true;

                  sbTag.append(c);
               } else {
                  sbFormatXml.append(c);
               }
               break;

            case '\n':
            case '\r':

               // make nothing
               if (!pbKeepReturn && !bInTag && !bCharsBetweenTags) {
                  break;
               }
            default:
               if (bInTag) {
                  sbTag.append(c);
               } else {
                  if (c > ' ') {
                     bCharsBetweenTags = true;
                  }

                  sbFormatXml.append(c);
               }
         }

         cLast = c;

      }


      return sbFormatXml.toString();
   }

   /**
    * Method isNumber
    * 
    * @param pcChar
    * @return
    * 
    * @author Andreas Brod
    */
   public static boolean isNumber(char pcChar)
   {
      return ((pcChar >= '0') && (pcChar <= '9'));
   }

   // -----------------------------------------------------------------------------
   //
   // -----------------------------------------------------------------------------

   /**
    * Method getDateTime
    * 
    * @param psFormat
    * @return
    * 
    * @author Andreas Brod
    */
   public static String getDateTime(String psFormat)
   {
      SimpleDateFormat sd = new SimpleDateFormat(psFormat);

      return sd.format(new Date());
   }

   /**
    * Method getValue
    * 
    * @param psText
    * @param psTag
    * @param psField
    * @return
    * 
    * @author Andreas Brod
    */
   public static String getValue(String psText, String psTag, String psField)
   {
      int iStart = psText.indexOf("<" + psTag + " ");

      if (iStart >= 0) {
         int iEnd = psText.indexOf(">", iStart);
         int iField = psText.indexOf(" " + psField + "=\"", iStart);

         if ((iField > 0) && (iField < iEnd)) {
            psText = psText.substring(iField, iEnd);
            psText = psText.substring(psText.indexOf("\"") + 1);
            psText = psText.substring(0, psText.indexOf("\""));

            return psText;
         }
      }

      return "";
   }

   /**
    * Method personialze
    * 
    * @param psRequest
    * @param pFile
    * @return
    * 
    * @author Andreas Brod
    */
   public static String personialze(String psRequest, File pFile)
   {
      try {
         XmlObject oPersonalize = new XmlObject(pFile);

         if (oPersonalize.isRoot()) {
            oPersonalize = oPersonalize.getFirstObject();
         }

         if (oPersonalize != null) {
            XmlObject oCreditCard = oPersonalize.getObject("CreditCard");

            if (oCreditCard != null) {
               psRequest = findCreditCard(psRequest,
                     oCreditCard.getAttribute("company") + oCreditCard.getAttribute("number"));
            }

            XmlObject oEmail = oPersonalize.getObject("Email");

            if (oEmail != null) {
               psRequest = findEMail(psRequest, oEmail.getAttribute("mailto"));
            }
         }

         return personialze(psRequest, oPersonalize);
      }
      catch (FileNotFoundException ex) {
         return psRequest;
      }
   }

   /**
    * Method personialze
    * 
    * @param psRequest
    * @param pXmlObject
    * @return
    * 
    * @author Andreas Brod
    */
   public static String personialze(String psRequest, XmlObject pXmlObject)
   {
      String tagName = pXmlObject.getName();
      String[] names = pXmlObject.getAttributeNames(true);

      for (String name : names) {
         psRequest = personialze(psRequest, tagName, name, pXmlObject.getAttribute(name), false);
      }

      XmlObject[] objs = pXmlObject.getObjects("");

      for (XmlObject obj : objs) {
         psRequest = personialze(psRequest, obj);
      }

      return psRequest;
   }

   /**
    * Method personialze
    * 
    * @param psText
    * @param psTag
    * @param psName
    * @param psValue
    * @param pbReplaceAll
    * @return
    * 
    * @author Andreas Brod
    */
   private static String personialze(String psText, String psTag, String psName, String psValue,
                                     boolean pbReplaceAll)
   {
      if (pbReplaceAll) {
         String sValueAll = getValue(psText, psTag, psName).toUpperCase();

         if (sValueAll.length() == 0) {
            return psText;
         }

         if (psValue.startsWith("%D")) {
            psValue = getDateTime(psValue.substring(2));
         }

         String sUpper = psText.toUpperCase();
         int i = sUpper.indexOf(sValueAll);

         while (i >= 0) {
            int j = i;

            while ((j >= 0) && (sUpper.charAt(j) != '\"')) {
               j--;
            }

            if ((j > 0) && (sUpper.charAt(j - 1) == '=')) {
               psText = psText.substring(0, i) + psValue + psText.substring(i + sValueAll.length());
               sUpper = psText.toUpperCase();
            }

            i = sUpper.indexOf(sValueAll, i + 1);
         }

         // s = replaceString(s,sValueAll,sValue);
      }

      return personialze(psText, psTag, psName, psValue);
   }

   /**
    * Method personialze
    * 
    * @param psText
    * @param psTag
    * @param psName
    * @param psValue
    * @return
    * 
    * @author Andreas Brod
    */
   private static String personialze(String psText, String psTag, String psName, String psValue)
   {
      int iPos = 0;

      psTag = "<" + psTag;

      while (psText.indexOf(psTag, iPos) >= 0) {
         iPos = psText.indexOf(psTag, iPos) + 1;

         int iEnd = psText.indexOf(">", iPos);

         if (iEnd > 0) {

            String sRep = psText.substring(iPos, iEnd);

            if (sRep.indexOf(psName) >= 0) {
               psText = psText.substring(0, iPos) + personialze(sRep, psName, psValue)
                     + psText.substring(iEnd);
            }

            iPos = iEnd;
         }
      }

      return psText;
   }

   /**
    * Method personialze
    * 
    * @param psText
    * @param psName
    * @param psValue
    * @return
    * 
    * @author Andreas Brod
    */
   private static String personialze(String psText, String psName, String psValue)
   {
      int iPos = 0;

      psName = psName += "=\"";

      if (psValue.startsWith("%D")) {
         psValue = getDateTime(psValue.substring(2));
      }

      while (psText.indexOf(psName, iPos) >= 0) {
         iPos = psText.indexOf(psName, iPos) + psName.length();

         int iEnd = psText.indexOf("\"", iPos);

         if (iEnd > 0) {
            psText = psText.substring(0, iPos) + psValue + psText.substring(iEnd);
            iPos = iEnd;
         }
      }

      return psText;
   }

   /**
    * Method findCreditCard
    * 
    * @param psText
    * @param psValue
    * @return
    * 
    * @author Andreas Brod
    */
   public static String findCreditCard(String psText, String psValue)
   {
      int iPos = psText.indexOf("CC");

      while (iPos >= 0) {
         if (iPos + 2 + psValue.length() < psText.length()) {
            String sCC = psText.substring(iPos + 2, iPos + 2 + psValue.length());
            boolean ok = (sCC.length() > 2);

            for (int i = 2; ok && (i < sCC.length()); i++) {
               ok = ((sCC.charAt(i) >= '0') && (sCC.charAt(i) <= '9'));
            }

            if (ok) {
               psText = replaceString(psText, sCC, psValue);
            }
         }

         iPos = psText.indexOf("CC", iPos + 1);
      }

      return psText;
   }

   /**
    * Method findEMail
    * 
    * @param psText
    * @param psValue
    * @return
    * 
    * @author Andreas Brod
    */
   public static String findEMail(String psText, String psValue)
   {
      int iPos = psText.indexOf("@");

      while (iPos >= 0) {
         int end = iPos + 1;

         while ((end < psText.length()) && isEMailChar(psText.charAt(end))) {
            end++;
         }

         int start = iPos - 1;

         while ((start >= 0) && isEMailChar(psText.charAt(start))) {
            start--;
         }

         String sEMail = psText.substring(start + 1, end);

         psText = replaceString(psText, sEMail, psValue);

         iPos = psText.indexOf("@", iPos + 1);
      }

      return psText;
   }

   /**
    * Method isEMailChar
    * 
    * @param pcChar
    * @return
    * 
    * @author Andreas Brod
    */
   private static boolean isEMailChar(char pcChar)
   {
      if ((pcChar >= 'A') && (pcChar <= 'Z')) {
         return true;
      }

      if ((pcChar >= 'a') && (pcChar <= 'z')) {
         return true;
      }

      if ((pcChar >= '0') && (pcChar <= '9')) {
         return true;
      }

      return ("._-".indexOf("" + pcChar) >= 0);
   }

   private static final String hexVals = "0123456789ABCDEF";

   /**
    * Method hexToInt
    * 
    * @param psText
    * @return
    * 
    * @author Andreas Brod
    */
   public static int hexToInt(String psText)
   {
      int val = 0;

      psText = psText.toUpperCase().trim();

      for (int i = 0; i < psText.length(); i++) {
         val = val * 16 + hexVals.indexOf(psText.charAt(i));
      }

      return val;
   }

   /**
    * Method getPath
    * 
    * @param psPath
    * @return
    * 
    * @author Andreas Brod
    */
   public static String getPath(String psPath)
   {
      if (psPath.startsWith("\"") && psPath.endsWith("\"")) {
         psPath = psPath.substring(1, psPath.length() - 1);
      }

      if ((psPath.indexOf("\\") > 0) || (psPath.indexOf("/") > 0)) {
         psPath =
            psPath.substring(0, Math.max(psPath.lastIndexOf("\\"), psPath.lastIndexOf("/")) + 1);
      }

      return psPath;
   }

   /**
    * Method removeDir
    * 
    * @param psDir
    * 
    * @author Andreas Brod
    */
   public static void removeDir(String psDir)
   {
      File f = new File(psDir);

      if (f.exists()) {
         if (f.isDirectory()) {
            File[] files = f.listFiles();

            for (int i = 0; i < files.length; i++) {
               if (!files[i].getName().startsWith(".")) {
                  removeDir(files[i].getAbsolutePath());
               }
            }
         }

         f.delete();
      }
   }

   /**
    * Method getAttribute
    * 
    * @param psText
    * @param psTag
    * @param psAttribute
    * @return
    * 
    * @author $author$
    */
   public static String getAttribute(String psText, String psTag, String psAttribute)
   {
      psText = getTag(psText, psTag);

      int i = psText.indexOf(" " + psAttribute + "=\"");

      if (i < 0) {
         return "";
      }

      psText = psText.substring(i) + "\"\"";
      psText = psText.substring(psText.indexOf("\"") + 1);

      return psText.substring(0, psText.indexOf("\""));
   }

   /**
    * Method getTag
    * 
    * @param psText
    * @param psTag
    * @return
    * 
    * @author $author$
    */
   public static String getTag(String psText, String psTag)
   {
      if (psTag.length() == 0) {
         return psText;
      }

      int i = psText.indexOf("<" + psTag + " ");

      if (i < 0) {
         i = psText.indexOf("<" + psTag + "/");
      }

      if (i < 0) {
         return "";
      }

      psText = psText.substring(i);
      i = psText.indexOf("</" + psTag + ">");

      if (i > 0) {
         psText = psText.substring(0, i + psTag.length() + 3);
      } else {
         psText = psText.substring(0, psTag.indexOf(">") + 1);
      }

      return psText;
   }

   /**
    * Method fillInt
    * 
    * @param piIntValue
    * @param piLength
    * @return
    * 
    * @author Andreas Brod
    */
   private static String fillInt(int piIntValue, int piLength)
   {
      String s = "" + piIntValue;

      while (s.length() < piLength) {
         s = "0" + s;
      }

      return s;
   }

   /**
    * This method returns a text as CamelCase
    * 
    * @param psText Text, which has to be transformed
    * @return CamelCase text
    * 
    * @author brod
    */
   public static String getCamelCase(String psText)
   {
      if (psText.length() > 1) {
         psText = psText.toLowerCase();
         int indexOf = psText.indexOf("_");
         if (indexOf > 0) {
            psText = psText.substring(0, indexOf) + getCamelCase(psText.substring(indexOf + 1));
         }
         psText = psText.substring(0, 1).toUpperCase() + psText.substring(1);
      } else {
         psText = psText.toUpperCase();
      }
      return psText;
   }

   /**
    * Method getDateTime
    * 
    * @param piDaysToAdd
    * @param pbDateTime
    * @param pBaseDate
    * @return
    * 
    * @author Andreas Brod
    */
   public static String getDateTime(int piDaysToAdd, boolean pbDateTime, Date pBaseDate)
   {
      GregorianCalendar gc = new GregorianCalendar();

      if (pBaseDate != null) {
         gc.setTime(pBaseDate);
      }

      gc.add(Calendar.DAY_OF_MONTH, piDaysToAdd);

      // 2004-04-03T09:00:00
      String sRet = fillInt(gc.get(Calendar.YEAR), 4);

      sRet += '-' + fillInt(gc.get(Calendar.MONTH) + 1, 2);
      sRet += '-' + fillInt(gc.get(Calendar.DAY_OF_MONTH), 2);

      if (!pbDateTime) {
         return sRet;
      }

      sRet += 'T' + fillInt(gc.get(Calendar.HOUR_OF_DAY), 2);
      sRet += ':' + fillInt(gc.get(Calendar.MINUTE), 2);
      sRet += ':' + fillInt(gc.get(Calendar.SECOND), 2);

      return sRet;

   }

   /**
    * Method getDateTime2
    * 
    * @param piDaysToAdd
    * @param pbDateTime
    * @param pBaseDate
    * @return
    * 
    * @author Andreas Brod
    */
   public static String getDateTimeZone(int piDaysToAdd, boolean pbDateTime, Date pBaseDate)
   {
      GregorianCalendar gc = new GregorianCalendar();
      // gc.setTimeZone(TimeZone.getTimeZone("GMT+04"));
      _systemOut.println(gc.getTimeZone());
      _systemOut.println(gc.getTimeZone().getRawOffset());

      if (pBaseDate != null) {
         gc.setTime(pBaseDate);
      }
      gc.add(Calendar.DAY_OF_MONTH, piDaysToAdd);

      String sDateOffset = (new SimpleDateFormat("Z")).format(gc.getTime());

      try {
         if (sDateOffset.length() == 5) {
            int pre = sDateOffset.charAt(0) == '-' ? 1 : -1;
            int hours = pre * Integer.parseInt(sDateOffset.substring(1, 3));
            int mins = pre * Integer.parseInt(sDateOffset.substring(3, 5));
            gc.add(Calendar.HOUR_OF_DAY, hours);
            gc.add(Calendar.MINUTE, mins);
         }
      }
      catch (NumberFormatException e) {}

      SimpleDateFormat sd = pbDateTime ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            : new SimpleDateFormat("yyyy-MM-dd");

      return sd.format(gc.getTime());
   }

   /**
    * This method returns a CheckSum using the Adler32
    * algorithm.
    * 
    * @param pyarrBytes bytes 
    * @return checksum for the bytes
    * 
    * @author brod
    */
   public static long getCheckSum(byte[] pyarrBytes)
   {
      Checksum checksumEngine = new Adler32();
      checksumEngine.update(pyarrBytes, 0, pyarrBytes.length);
      return checksumEngine.getValue();
   }

   /**
    * This method returns the compiled class
    * 
    * @param psPath Path
    * @param psName Name
    * @return CompiledClass
    * @throws ClassNotFoundException
    * 
    * @author brod
    */
   public static Class<?> getCompiledClass(String psPath, String psName)
      throws ClassNotFoundException
   {
      return new ArcticClassLoader(Thread.currentThread().getContextClassLoader(), psPath)
            .loadClass(psName);

   }


   /** 
    * writes a to file. 
    * <p> 
    *  ... add detailed information for method writeToFile 
    * 
    * <p> TODO rename file to pFile, content to pyarrContent
    * @param pFile file object
    * @param pyarrContent pyarr byte array of contents
    * @throws IOException
    * 
    * @author Brod 
    */
   public static void writeToFile(File pFile, byte[] pyarrContent)
      throws IOException
   {
      _systemOut.println(
            "write file " + pFile.getAbsolutePath() + " (" + pyarrContent.length + " bytes)");
      if (!pFile.getParentFile().exists()) {
         pFile.getParentFile().mkdirs();
      }
      FileOutputStream fileOut = new FileOutputStream(pFile);
      BufferedOutputStream out = new BufferedOutputStream(fileOut);
      out.write(pyarrContent);
      out.close();
   }

   /** 
    * loads the byte array of bytes. 
    * 
    * @param pFile file object
    * @return load byte array of bytes
    * @throws IOException
    * 
    * @author Brod 
    */
   public static byte[] loadBytes(File pFile)
      throws IOException
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(pFile));
      copy(in, out);
      return out.toByteArray();
   }

   /** 
    * copies this Util. 
    * <p> 
    *  ... add detailed information for method copy 
    * 
    * <p> TODO rename fis to pFis, fos to pFos
    * @param pFis fis object Input Stream
    * @param pFos fos object Output Stream
    * @throws IOException
    * 
    * @author Brod 
    */
   static void copy(InputStream pFis, OutputStream pFos)
      throws IOException
   {
      try {
         byte[] buffer = new byte[0xFFFF];
         for (int len; (len = pFis.read(buffer)) > 0;) {
            pFos.write(buffer, 0, len);
         }
      }
      catch (IOException e) {
         throw e;
      }
      finally {
         if (pFis != null) {
            try {
               pFis.close();
            }
            catch (IOException e) {
               e.printStackTrace();
            }
         }
         if (pFos != null) {
            try {
               pFos.close();
            }
            catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   /**
    * Deletes the specified file, of all files in the specified directory and its subdirectories.
    * CVS-directories will be ignored
    *
    * @param pFile file or directory to clear
    *
    * @author kaufmann
    */
   public static void clearDirectoryExceptCVS(File pFile)
   {
      if (pFile.exists()) {
         if (pFile.isDirectory()) {
            File[] listFiles = pFile.listFiles();
            for (File listFile : listFiles) {
               if (listFile.getName().equalsIgnoreCase("CVS")) {
                  // ignore cvs directories
               } else {
                  clearDirectoryExceptCVS(listFile);
               }
            }
         } else {
            pFile.delete();
         }
      }
   }

   /**
    * Creates a full package name for a namespace below the "base" package, e.g.<pre>
    * base package = "net.railgds.shopping"
    * namespace    = "http://railgds.net/ws/shopping"
    * ==> net.railgds.shopping.ws.shopping
    * </pre>
    *
    * @param psPackage "base" package
    * @param psNamespace namespace to create the package for
    * @return full package name
    *
    * @author kaufmann
    */
   public static String getPackage(String psPackage, String psNamespace)
   {
      String sPackage = psPackage;
      StringTokenizer st = new StringTokenizer(psNamespace, "\\/");
      if (st.countTokens() > 0) {
         String sFirstToken = st.nextToken();
         if (sFirstToken.endsWith(":")) {
            if (st.hasMoreTokens()) {
               // consume the next token
               st.nextToken();
            }
         }
         while (st.hasMoreTokens()) {
            sPackage += ".";
            String sNextToken = st.nextToken();
            if ((sNextToken.charAt(0) >= '0') && (sNextToken.charAt(0) <= '9')) {
               sPackage += "p";
            }
            sPackage += sNextToken;
         }
      }
      return sPackage.toLowerCase();
   }

   /**
    * Creates a relative path from the path of psPackage to psPackage2, which can be used for
    * imports in schemas, attribute import/@schemaLocation, e.g.:<pre>
    * psPackage  = net.railgds.shopping.ws.shopping
    * psPackage2 = net.railgds.shopping.ws.commontypes
    * ==> ../commontypes/
    * </pre>
    *
    * @param psPackage "base" package
    * @param psPackage2 package relative to the "base" package
    * @return relative path for psPackage2
    *
    * @author kaufmann
    */
   public static String getRelDirectory(String psPackage, String psPackage2)
   {
      StringBuilder sb = new StringBuilder();
      StringTokenizer st1 = new StringTokenizer(psPackage, ".");
      StringTokenizer st2 = new StringTokenizer(psPackage2, ".");
      while (st1.hasMoreTokens() && st2.hasMoreTokens()) {
         String s1 = st1.nextToken();
         String s2 = st2.nextToken();
         if (!s1.equals(s2)) {
            sb.append("../");
            sb.append(s2);
            sb.append("/");
            break;
         }
      }
      while (st1.hasMoreTokens()) {
         st1.nextToken();
         sb.insert(0, "../");
      }
      while (st2.hasMoreTokens()) {
         sb.append(st2.nextToken());
         sb.append("/");
      }
      return sb.toString();
   }

   /**
    * This methods loads the content from an URL
    * 
    * @param psUrl url
    * @return url content
    * 
    */
   public static String loadFromUrl(String psUrl)
   {
      try {
         URL url = new URL(psUrl);
         URLConnection yc = url.openConnection();
         BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
         String inputLine;
         StringBuilder sb = new StringBuilder();
         while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine + "\n");
         }
         in.close();
         return sb.toString();
      }
      catch (Exception e) {
         // could not find
      }
      return "";
   }

   /** 
    * selects this Util. 
    * 
    * @param psMessage message final
    * @param parrValues lst final
    * @return select
    * 
    * @author Brod 
    */
   public static String select(final String psMessage, final String[] parrValues)
   {
      final Display defa = Display.getDefault();
      final StringBuilder sb = new StringBuilder();
      defa.syncExec(new Runnable()
      {
         @Override
         public void run()
         {
            ElementListSelectionDialog dialog =
               new ElementListSelectionDialog(defa.getActiveShell(), new LabelProvider());
            dialog.setElements(parrValues);
            dialog.setTitle(psMessage);
            // user pressed cancel
            if (dialog.open() == Window.OK) {
               Object[] result = dialog.getResult();
               if (result != null && result.length > 0) {
                  sb.append(result[0].toString());
               }
            }
         }
      });

      return sb.toString();
   }


   /**
    * Returns the output path of an arctic project in the current eclipse workspace. If more than 
    * one arctic project is found in the workspace, the developer has to select the project.
    * If no arctic project exists in the workspace, <code>null</code> will be returned.
    *
    * @return output path of an arctic project, or null if no arctic project is available or none 
    * has been selected
    *
    * @author kaufmann
    */
   public static String getProjectOutputPath()
   {
      Map<String, String> outputPaths = new HashMap<>();

      IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      for (IProject project : projects) {
         // indicates an arctic project ;-)
         IFile file = project.getFile("/conf/Arctic.xml");
         if (file != null && file.exists()) {

            // check, that it is a Java project
            IProjectNature nature = null;
            try {
               nature = project.getNature("org.eclipse.jdt.core.javanature");
            }
            catch (CoreException pException1) {
               // ignore exception
            }

            if (nature != null) {
               // get the output path
               String sProjectOutputPath = null;
               try {
                  sProjectOutputPath = project.getLocation()
                        .append(JavaCore.create(project).getOutputLocation().removeFirstSegments(1))
                        .toPortableString();
               }
               catch (JavaModelException pException) {
                  // ignored
               }

               // if an output path has been found, remind it for the project name
               if (sProjectOutputPath != null) {
                  outputPaths.put(project.getName(), sProjectOutputPath);
               }
            }
         }
      }

      String sOutputPath;
      switch (outputPaths.size()) {
         case 0: // no arctic project found 
            sOutputPath = null;
            break;
         case 1: // return the one and only arctic project's output path 
            sOutputPath = outputPaths.values().iterator().next();
            break;
         default: // let the developer select the project
            sOutputPath = null;
            String[] projectNames = outputPaths.keySet().toArray(new String[0]);
            Arrays.sort(projectNames);
            String selectedProject = select("Please select the arctic project :", projectNames);
            if (selectedProject != null) {
               sOutputPath = outputPaths.get(selectedProject);
            }
            break;
      }

      return sOutputPath;
   }

   //   public static String getFormattedJSonOutput(String psJsonText, ClassLoader pLoader)
   //      throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException,
   //      InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException
   //   {
   //      Class<?> parserClass = pLoader.loadClass("net.ifao.util.json.parse.Parser");
   //      Object jsonObject = parserClass.getMethod("parse", String.class).invoke(null, psJsonText);
   //      Class<?> standardFormatterClass =
   //         pLoader.loadClass("net.ifao.util.json.formatter.StandardFormatter");
   //      Constructor<?> constructor = standardFormatterClass.getConstructor(jsonObject.getClass());
   //      Object standardFormatter = constructor.newInstance(jsonObject);
   //      return standardFormatter.toString();
   //   }


   public interface IDialogContentBuilder
   {

      /**
       * Creates the content of the dialog
       *
       * @param pContainer parent element for the dialog content
       *
       * @author kaufmann
       */
      void createContent(Composite pContainer);

   }

   /**
    * Creates a dialog with an OK button and the specified text. The text may be multilined. The 
    * dialog will show scrollbars, if necessary
    *
    * @param psTitle title of the dialog
    * @param psText text to display
    *
    * @author kaufmann
    */
   public static void displayDialog(final String psTitle,
                                    final IDialogContentBuilder pDialogContentBuilder)
   {
      Shell shell = Display.getDefault().getActiveShell();

      Dialog dialog = new Dialog(shell)
      {
         /**
          * Builds the dialog with customized parameters: SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX
          *
          * @author kaufmann
          */
         @Override
         public void create()
         {
            setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX);
            super.create();
         }

         /**
          * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
          *
          * @author kaufmann
          */
         @Override
         protected void configureShell(Shell pNewShell)
         {
            super.configureShell(pNewShell);
            pNewShell.setText(psTitle == null ? "" : psTitle);
         }

         /**
          * Creates the button; has been overwritten to prevent the creation of the cancel button
          *
          * @author kaufmann
          */
         @Override
         protected Button createButton(Composite pParent, int pId, String pLabel,
                                       boolean pbDefaultButton)
         {
            // prevent the cancel button to be created
            if (pId == IDialogConstants.CANCEL_ID) {
               return null;
            }
            return super.createButton(pParent, pId, pLabel, pbDefaultButton);
         }

         /**
          * Creates the dialog content
          *
          * @author kaufmann
          */
         @Override
         protected Control createDialogArea(Composite parent)
         {
            Composite container = (Composite) super.createDialogArea(parent);
            pDialogContentBuilder.createContent(container);
            return container;
         }
      };
      dialog.open();
   }

   /**
    * Creates a dialog with an OK button and the specified text. The text may be multilined. The 
    * dialog will show scrollbars, if necessary
    *
    * @param psTitle title of the dialog
    * @param psText text to display
    *
    * @author kaufmann
    */
   public static void displayTextInDialog(final String psTitle, final String psText)
   {
      displayDialog(psTitle, new IDialogContentBuilder()
      {

         @Override
         public void createContent(Composite pContainer)
         {
            Text text = new Text(pContainer, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
            text.setFont(new Font(Display.getDefault(), "Courier", 10, SWT.NONE));
            text.setText(psText);
            GridData layoutData = new GridData();
            layoutData.minimumHeight = 100;
            layoutData.minimumWidth = 400;
            layoutData.grabExcessHorizontalSpace = true;
            layoutData.horizontalAlignment = GridData.FILL;
            layoutData.grabExcessVerticalSpace = true;
            layoutData.verticalAlignment = GridData.FILL;
            text.setLayoutData(layoutData);
         }
      });
   }

   /**
    * Creates a dialog with an OK button and the stacktrace of an exception. The stacktrace may be 
    * preceeded by an additional text
    *
    * @param psTitle title of the dialog
    * @param pException Exception to display
    * @param psAdditionalText Additional text; is shown before the stacktrace
    *
    * @author kaufmann
    */
   public static void displayExceptionInDialog(String psTitle, Exception pException,
                                               String psAdditionalText)
   {
      try (OutputStream out = new ByteArrayOutputStream()) {
         pException.printStackTrace(new PrintStream(out));
         displayTextInDialog(psTitle, psAdditionalText + out.toString());
      }
      catch (IOException pException1) {
         // ignored
      }
   }

   /**
    * Sets the path to the arctic configuration. This method might be used, if arctic classes
    * are called via reflection.
    *
    * @param psPath path to the arctic configuration
    * @param pLoader class loader
    * @throws ClassNotFoundException
    * @throws IllegalAccessException
    * @throws IllegalArgumentException
    * @throws InvocationTargetException
    * @throws NoSuchMethodException
    * @throws SecurityException
    *
    * @author kaufmann
    */
   public static void setArcticConfiguration(String psPath, ClassLoader pLoader)
      throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException
   {
      Class<?> configuration = pLoader.loadClass(ARCTIC_CONFIGURATION_CLASS);
      configuration.getMethod("setConfigDirectory", new Class[]{ String.class }).invoke(null,
            new Object[]{ psPath });

   }

   /** 
    * returns a provider data file. 
    * 
    * @param psBaseArctic base arctic String
    * @param psPath path String
    * @return the provider data file
    * 
    * @author Brod 
    */
   public static File getProviderDataFile(String psBaseArctic, String psPath)
   {
      String sBaseDir = psBaseArctic.replace('\\', '/');
      while (sBaseDir.endsWith("/")) {
         sBaseDir = sBaseDir.substring(0, sBaseDir.length() - 1);
      }
      // truncate slashes
      // remove Type
      Matcher m = Pattern.compile("(?i)(\\.x.{2}|\\.wsdl|\\.properties)$").matcher(psPath);
      String sSuffix = "";
      if (m.find()) {
         sSuffix = m.group(1);
         psPath = psPath.substring(0, psPath.length() - sSuffix.length());
      }
      String sRequestedPath = psPath.replaceAll("[.\\\\]", "/") + sSuffix;
      while (sRequestedPath.startsWith("/")) {
         sRequestedPath = sRequestedPath.substring(1);
      }
      while (sRequestedPath.endsWith("/")) {
         sRequestedPath = sRequestedPath.substring(0, sRequestedPath.length() - 1);
      }
      // get the directories (for the different providers)
      Hashtable<String, File> packageDirectories = getPackageDirectories(psBaseArctic);
      // loop over the directories
      for (String sProviderFolder : packageDirectories.keySet()) {
         File file = packageDirectories.get(sProviderFolder);
         if (sProviderFolder.length() == 0) {
            // ignore
         } else if (sProviderFolder.equals(sRequestedPath)) {
            return getFile(file);
         } else {
            if (sRequestedPath.startsWith(sProviderFolder)) {
               // get relative File
               return getFile(
                     new File(file, sRequestedPath.substring(sProviderFolder.length() + 1)));
            } else if (sProviderFolder.startsWith(sRequestedPath)) {
               // get the absulte path
               String absolutePath = file.getAbsolutePath().replace('\\', '/');
               // ... and find the relative path 
               int indexOf = absolutePath.indexOf(sRequestedPath);
               if (indexOf > 0) {
                  return getFile(
                        new File(absolutePath.substring(0, indexOf + sRequestedPath.length())));
               }
            }
         }
      }
      // file not found: return 'old' path 
      return getFile(new File(sBaseDir, "lib/providerdata/" + sRequestedPath));
   }

   /** 
    * returns a CanonicalFile file (if possible) 
    * 
    * @param pFile file object
    * @return the file
    * 
    * @author Brod 
    */
   private static File getFile(File pFile)
   {
      try {
         return pFile.getCanonicalFile();
      }
      catch (IOException e) {
         return pFile.getAbsoluteFile();
      }
   }

   /** 
    * returns the package directories. 
    * 
    * @param psBaseArctic base arctic String
    * @return the package directories
    * 
    * @author Brod 
    */
   private static synchronized Hashtable<String, File> getPackageDirectories(String psBaseArctic)
   {
      if (_htPackageDirectories == null || !psBaseArctic.equalsIgnoreCase(_sBaseArctic)) {
         _htPackageDirectories = new Hashtable<String, File>();
         _sBaseArctic = psBaseArctic;
         File file = new File(_sBaseArctic, "lib/provider");
         if (file.isDirectory()) {
            for (File fileProvider : file.listFiles()) {
               File dataFile = new File(fileProvider, "data");
               if (dataFile.exists()) {
                  addPackageDirectories(_htPackageDirectories, dataFile, "");
               }
            }
         }
      }
      return _htPackageDirectories;
   }

   /** 
    * adds the package directories. 
    * 
    * @param phtPackageDirectories pht package directories Hashtable of strings and files
    * @param pDataFile data file object
    * @param psDirectory directory String
    * 
    * @author Brod 
    */
   private static void addPackageDirectories(Hashtable<String, File> phtPackageDirectories,
                                             File pDataFile, String psDirectory)
   {
      File subDirectory = null;
      for (File subFile : pDataFile.listFiles()) {
         if (subFile.getName().equalsIgnoreCase("CVS")) {
            // ignore
         } else if (subFile.isDirectory()) {
            if (subDirectory == null) {
               subDirectory = subFile;
            } else {
               subDirectory = null;
               break;
            }
         } else {
            subDirectory = null;
            break;
         }
      }
      if (subDirectory == null) {
         phtPackageDirectories.put(psDirectory, pDataFile);
      } else {
         if (psDirectory.length() > 0)
            psDirectory += "/";
         psDirectory += subDirectory.getName();
         addPackageDirectories(phtPackageDirectories, subDirectory, psDirectory);
      }
   }

   /** 
    * returns a provider data file. 
    * 
    * @param psBaseArctic base arctic File
    * @param psPath path String
    * @return the provider data file
    * 
    * @author Brod 
    */
   public static File getProviderDataFile(File psBaseArctic, String psPath)
   {
      return getProviderDataFile(psBaseArctic.getAbsolutePath(), psPath);
   }

   /** 
    * returns a provider data path. 
    * 
    * @param psBaseArctic base arctic String
    * @param psPath from namespace String
    * @return the provider data path
    * 
    * @author Brod 
    */
   public static String getProviderDataPath(String psBaseArctic, String psPath)
   {
      String sBaseDir = psBaseArctic;
      if (sBaseDir.endsWith("\\") || sBaseDir.endsWith("/")) {
         sBaseDir = sBaseDir.substring(0, sBaseDir.length() - 1);
      }
      File pdFile = getProviderDataFile(sBaseDir, psPath);
      try {
         String providerDataFile = pdFile.getCanonicalPath();
         return providerDataFile;
      }
      catch (IOException e) {
         String providerDataFile = pdFile.getAbsolutePath();
         return providerDataFile;
      }
   }

   /** 
    * returns a provider data package path. For this the path will be scanned for the 
    * related lib directory and the related package path.
    * <p>
    * This works with directories (delimited with '/' or '\') and packages (delimited
    * with '.').
    * 
    * @param psAbsolutePath path String
    * @param psPath path String
    * @return the provider data package path
    * 
    * @author Brod 
    */
   public static String getProviderDataPackagePath(String psAbsolutePath, String psPath)
   {
      Matcher matcher = Pattern.compile(".*[./\\\\]lib[./\\\\]provider.*?data[./\\\\](.+)")
            .matcher(psAbsolutePath);
      if (matcher.find()) {
         return matcher.group(1);
      }
      return psPath;
   }

   /** 
    * returns a provider data root directory. Below this directory the Path can be found.
    * This may be be e.g. <code>arcticRoot/lib/Provider/AmadeusWsdl/data</code> 
    * 
    * @param psBaseArctic base arctic String
    * @param psPath path String
    * @return the provider data root directory
    * 
    * @author Brod 
    */
   public static String getProviderDataRootDirectory(String psBaseArctic, String psPath)
   {
      String relativeProviderDataFile = getProviderDataPath(psBaseArctic, psPath);
      return relativeProviderDataFile.substring(0,
            relativeProviderDataFile.length() - psPath.length() - 1);
   }


   /**
    * Method getPathFromNamespace creates the path for the namespace
    *
    * @param psURI namespace URI
    * @return path for the namespace
    *
    * @author kaufmann
    */
   public static String getPathFromNamespace(String psURI, String psDefault)
   {
      URI uri = null;
      try {
         uri = new URI(psURI);
      }
      catch (URISyntaxException pException) {
         pException.printStackTrace();
         throw new RuntimeException("namespace URI is invalid");
      }
      StringBuilder sbPath = new StringBuilder(psURI.length());
      boolean bIsJavaScheme = uri.getScheme().equalsIgnoreCase("java");
      String sHost = uri.getHost();
      if (sHost == null) {
         sHost = psDefault;
      }
      String[] sHostParts = sHost.split("[.]");
      for (int i = sHostParts.length - 1; i >= 0; i--) {
         sbPath.append(sHostParts[i]).append("/");
      }

      String sPath = bIsJavaScheme ? uri.getSchemeSpecificPart() : uri.getPath();
      String[] sPathParts = sPath.split("[\\\\/.]");
      for (String sPathPart : sPathParts) {
         if (sPathPart.length() > 0 && !sPathPart.equalsIgnoreCase("package")) {
            sbPath.append(sPathPart).append("/");
         }
      }
      return sbPath.toString();
   }
}
