package net.ifao.plugins.tools;


import ifaoplugin.*;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.*;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.TextEdit;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import net.ifao.plugin.preferences.PreferenceConstants;
import net.ifao.xml.XmlObject;


public abstract class CodeFormatterApplication
{

   protected String sAuthor;

   public CodeFormatterApplication()
   {
      sAuthor = getAuthor();
   }


   public static String formatSourceCode(File pFile, String psSourceCode)
   {
      String sPath = pFile.getAbsolutePath();
      if (sPath.contains("\\src\\net")) {
         String sRoot = sPath.substring(0, sPath.indexOf("\\src\\net"));
         return formatCode(psSourceCode, sRoot, false);
      }
      return null;
   }

   static final String sChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";

   public static String formatCode(InputStream pisStream, String psEclipseRoot, String psFileName,
                                   boolean b)
   {
      // call the CodeFormatterApplication.formatCode
      String sReadText;
      CodeFormatterApplication codeFormatterApplication = getFormatter();
      sReadText = codeFormatterApplication.format(pisStream, psEclipseRoot, psFileName, b);
      // return the formated String
      return sReadText;
   }

   private static CodeFormatterApplication getFormatter()
   {
      boolean bNewFormatter;
      try {
         bNewFormatter =
            Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_FORMATTER);
      }
      catch (Exception ex) {
         // formatter not found
         bNewFormatter = false;
      }

      CodeFormatterApplication codeFormatterApplication;
      if (bNewFormatter) {
         codeFormatterApplication = new CodeFormatterApplicationV2();
      } else {
         codeFormatterApplication = new CodeFormatterApplicationV1();
      }
      return codeFormatterApplication;
   }

   /**
    * The static method readText reads the text from an inputStream and returns
    * the formated String
    * 
    * @param pFile TODO (brod) file object
    * @param psEclipseRoot The name of the root directory (for the formatFile)
    * @param psClassName the name of the java-class
    * @param pbShowErrorDialog Indicator if ErrorDialog has to be displayed
    * 
    * @author brod
    */
   public static void formatCode(File pFile, String psEclipseRoot, String psClassName,
                                 boolean pbShowErrorDialog)
   {
      if (pFile.exists()) {
         if (pFile.isFile()) {
            try {
               String sText =
                  formatCode(new FileInputStream(pFile), psEclipseRoot, psClassName,
                        pbShowErrorDialog);

               BufferedWriter bw = new BufferedWriter(new FileWriter(pFile));
               bw.write(sText);
               bw.close();
            }
            catch (IOException e) {
               // could not write the file
            }
         } else if (pFile.isDirectory()) {
            File[] files = pFile.listFiles();
            for (File file : files) {
               formatCode(file, psEclipseRoot, psClassName, pbShowErrorDialog);
            }
         }
      }
   }

   private static String _author = "";
   private static Hashtable<String, String> _htUsers;

   public static void main(String[] args)
   {
      System.out.println(getAuthor());

   }

   /**
    * the static method getAuthor returns the current System user
    * 
    * @return the current name of the system use
    * 
    * @author brod
    */
   static String getAuthor()
   {
      if (_htUsers == null || _htUsers.size() == 0) {
         _author = "UNKNOWN";
         try {
            _author = System.getenv("USERNAME");
            if (_author == null || _author.length() == 0) {
               _author = InetAddress.getLocalHost().getHostName();
               if (_author.toLowerCase().endsWith("xp")) {
                  _author = _author.substring(0, _author.length() - 2);
               }
               if (_author.toLowerCase().startsWith("pc")) {
                  _author = _author.substring(_author.length() + 2);
               }
            }
         }
         catch (Exception e1) {
            _author = "UNKNOWN";
         }

         // check against all users
         _htUsers = new Hashtable<String, String>();
         try {
            String s =
               Util.loadFromUrl("http://arctic_build/TaskScheduler/editFile?DIR=file://D:/BuildServer/conf&FILE=DetailUsers.xml&TAB=View");
            s = s.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
            XmlObject settings =
               new XmlObject(s.substring(s.indexOf("<Settings"), s.indexOf("</Settings>") + 11))
                     .getFirstObject();
            XmlObject[] users = settings.createObject("Users").getObjects("User");
            for (XmlObject user : users) {
               String sFirstName = user.getAttribute("firstName");
               String sName = user.getAttribute("name");
               if (sName.length() > 1 && sFirstName.length() > 1) {
                  _htUsers.put(sName.toUpperCase(), sFirstName + " " + sName);
               }
            }
         }
         catch (Exception ex) {
            // invalid format ... users not found
         }

         String sName = _htUsers.get(_author.toUpperCase());
         if (sName != null) {
            _author = sName;
         } else {
            _author = _author.substring(0, 1).toUpperCase() + _author.substring(1).toLowerCase();
         }
      }
      return _author;
   }

   /**
    * The static method readText reads the text from an inputStream and returns
    * the formated String
    * 
    * @param pisPsReader The reader with the containing sourceCode
    * @param psEclipseRoot The name of the root directory (for the formatFile)
    * @param psClassName the name of the java-class
    * @param pbShowErrorDialog Indicator if ErrorDialog has to be displayed
    * @return the formated Text.
    * 
    * @author brod
    */
   private String format(InputStream pisPsReader, String psEclipseRoot, String psClassName,
                         boolean pbShowErrorDialog)
   {
      String sClassName = psClassName;
      // convert the fileName
      if (sClassName.endsWith(".java")) {
         sClassName = sClassName.substring(0, sClassName.lastIndexOf("."));
      }
      if (sClassName.indexOf("/") >= 0) {
         sClassName = sClassName.substring(sClassName.lastIndexOf("/") + 1);
      }
      if (sClassName.indexOf("\\") >= 0) {
         sClassName = sClassName.substring(sClassName.lastIndexOf("\\") + 1);
      }
      String sText = readBlock(sClassName, 0, pisPsReader, "" + (char) 0, 0, 2, false);

      return formatCode(sText, psEclipseRoot, pbShowErrorDialog);
   }

   /**
    * The method formatCode formats a java sourceCode according to
    * the settings within extFiles\\EclipseFormatter\\Formatter.xml
    * 
    * @param psSourceCode The JavaSourceCode text
    * @param psProjectRoot The root project (for the formatter)
    * @param pbShowErrorDialog Indicator if ErrorDialog has to be displayed
    * @return The formated Text
    * 
    * @author brod
    */
   private static String formatCode(String psSourceCode, String psProjectRoot,
                                    boolean pbShowErrorDialog)
   {
      File sConfigFile = new File(psProjectRoot + "\\extFiles\\EclipseFormatter\\Formatter.xml");
      Map<String, String> map = readConfig(sConfigFile, pbShowErrorDialog);

      CodeFormatter codeFormatter = org.eclipse.jdt.core.ToolFactory.createCodeFormatter(map);
      String sSourceCode = psSourceCode;
      TextEdit edit =
         codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, sSourceCode, 0,
               sSourceCode.length(), 0, null);

      try {
         if (edit != null) {
            IDocument doc = new Document(sSourceCode);
            edit.apply(doc);
            sSourceCode = doc.get();

         }
      }
      catch (Exception exception) {
         // make nothing
         exception.printStackTrace();
      }
      return sSourceCode;
   }

   /**
    * Read the xml config file and return a Map representing the options that
    * are in the specified config file.
    * 
    * @param pFilename The related xml File
    * @param pbShowErrorDialog Indicator if ErrorDialog has to be displayed
    * @return The Map-element (with the settings)
    * 
    */
   private static Map<String, String> readConfig(File pFilename, boolean pbShowErrorDialog)
   {

      // create default
      @SuppressWarnings("unchecked")
      Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
      // add options
      options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
      options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
      options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);

      if (!pFilename.exists()) {
         StringTokenizer st = new StringTokenizer(pFilename.getAbsolutePath(), "\\/");
         String sAbsolutePath = "\n";
         String sLastPath = st.nextToken();
         while (st.hasMoreTokens()) {
            sLastPath += "\\" + st.nextToken();
            if (sLastPath.length() > 60) {
               sAbsolutePath += sLastPath;
               sLastPath = "\n";
            }
         }
         sAbsolutePath += sLastPath;
         if (pbShowErrorDialog) {
            MessageDialog.openError(new Shell(), "JavaDocHeader Builder", "File " + sAbsolutePath
                  + "\n not found");
         }
         return options;
      }
      try {
         final FileInputStream reader = new FileInputStream(pFilename);
         final ConfigHandler handler = new ConfigHandler();

         try {
            InputSource inputSource = new InputSource(reader);
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser parser = factory.newSAXParser();
            parser.parse(inputSource, handler);
            // Object configName = handler.getName();

            // addAll HandlerSettings
            options.putAll(handler.getSettings());

         }
         finally {
            try {
               reader.close();
            }
            catch (IOException e) { /* ignore */
            }
         }

      }
      catch (Exception e) {
         MessageDialog.openError(new Shell(), "JavaDocHeader Builder", e.getMessage());
      }
      return options;
   }

   abstract String readBlock(String psClassName, int piDeep, InputStream reader, String psEndBlock,
                             int piStatus, int piMaxDeep, boolean pbInterface);
}


/**
 * The class CodeFormatterApplication handles the import of the
 * CodeFormatter Map
 * 
 * <p>
 * Copyright &copy; 2006, i:FAO
 * 
 * @author brod
 */
class ConfigHandler
   extends DefaultHandler
{

   /**
    * Identifiers for the XML file.
    */
   private final String XML_NODE_ROOT = "profiles"; //$NON-NLS-1$

   private final String XML_NODE_PROFILE = "profile"; //$NON-NLS-1$

   private final String XML_NODE_SETTING = "setting"; //$NON-NLS-1$

   private final String XML_ATTRIBUTE_VERSION = "version"; //$NON-NLS-1$

   private final String XML_ATTRIBUTE_ID = "id"; //$NON-NLS-1$

   private final String XML_ATTRIBUTE_NAME = "name"; //$NON-NLS-1$

   private final String XML_ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$

   private int fVersion;

   private String fName;

   private Map<String, String> fSettings;

   /**
    * The method startElement has to be implemented for DefaultHandler
    * 
    * @param psUri The URI
    * @param psLocalName The local Name
    * @param psQName The QName
    * @param pAttributes The attributes (in format Attribute)
    * @throws SAXException if there was any
    * 
    * @author brod
    */
   @Override
   public void startElement(String psUri, String psLocalName, String psQName, Attributes pAttributes)
      throws SAXException
   {

      if (psQName.equals(XML_NODE_SETTING)) {

         final String key = pAttributes.getValue(XML_ATTRIBUTE_ID);
         final String value = pAttributes.getValue(XML_ATTRIBUTE_VALUE);
         fSettings.put(key, value);

      } else if (psQName.equals(XML_NODE_PROFILE)) {

         fName = pAttributes.getValue(XML_ATTRIBUTE_NAME);
         fSettings = new HashMap<String, String>(200);

      } else if (psQName.equals(XML_NODE_ROOT)) {

         try {
            fVersion = Integer.parseInt(pAttributes.getValue(XML_ATTRIBUTE_VERSION));
         }
         catch (NumberFormatException ex) {
            throw new SAXException(ex);
         }

      }
   }

   /**
    * This method has to be implemented for DefaultHandler
    * 
    * @return the settingsMap
    * 
    * @author brod
    */
   public Map<String, String> getSettings()
   {
      return fSettings;
   }

   /**
    * This method has to be implemented for DefaultHandler
    * 
    * @return the int version
    * 
    * @author brod
    */
   public int getVersion()
   {
      return fVersion;
   }

   /**
    * This method has to be implemented for DefaultHandler
    * 
    * @return The name
    * 
    * @author brod
    */
   public String getName()
   {
      return fName;
   }

}
