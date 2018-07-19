package schemagenerator.actions;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import javax.swing.*;

import net.ifao.util.CorrectDatabindingXsd;
import net.ifao.xml.WsdlObject;
import net.ifao.xml.XmlObject;


/**
 * Class CreateTravelfusionSchemas
 *
 * <p>
 * Copyright &copy; 2006, i:FAO Group GmbH
 * @author kaufmann
 */
public class ImportTravelfusionSchemas
{

   private static String _sBaseDir; //  @jve:decl-index=0:
   private static String _sURL;
   private static String _sRequest;
   private static String _sResponse;

   private JFrame jFrame = null; //  @jve:decl-index=0:visual-constraint="21,9"
   private JPanel jContentPane = null;
   private JPanel jPanelSouth = null;
   private JTextArea jTextAreaCenter = null;
   private JButton jButtonExit = null;
   private JScrollPane jScrollPaneTextArea = null;
   private JProgressBar jProgressBar = null;

   /**
    * This method initializes jFrame
    *
    * @return javax.swing.JFrame
    *
    */
   private JFrame getJFrame()
   {
      if (jFrame == null) {
         jFrame = new JFrame();
         jFrame.setSize(new java.awt.Dimension(620, 400));
         jFrame.setTitle("Create Travelfusion Schemas");
         jFrame.setContentPane(getJContentPane());
         (new TravelfusionThread()).start();
      }
      return jFrame;
   }

   /**
    * This method initializes jContentPane
    *
    * @return javax.swing.JPanel
    *
    */
   private JPanel getJContentPane()
   {
      if (jContentPane == null) {
         jContentPane = new JPanel();
         jContentPane.setLayout(new BorderLayout());
         jContentPane.add(getJPanelSouth(), java.awt.BorderLayout.SOUTH);
         jContentPane.add(getJScrollPaneTextArea(), java.awt.BorderLayout.CENTER);
      }
      return jContentPane;
   }

   /**
    * This method initializes jPanelSouth
    *
    * @return javax.swing.JPanel
    *
    */
   private JPanel getJPanelSouth()
   {
      if (jPanelSouth == null) {
         FlowLayout flowLayout = new FlowLayout();
         flowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
         jPanelSouth = new JPanel();
         jPanelSouth.setLayout(flowLayout);
         jPanelSouth.add(getJProgressBar(), null);
         jPanelSouth.add(getJButtonExit(), null);
      }
      return jPanelSouth;
   }

   /**
    * This method initializes jTextAreaCenter
    *
    * @return javax.swing.JTextArea
    *
    */
   private JTextArea getJTextAreaCenter()
   {
      if (jTextAreaCenter == null) {
         jTextAreaCenter = new JTextArea();
         jTextAreaCenter.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 11));
         jTextAreaCenter.setTabSize(3);
         jTextAreaCenter.setWrapStyleWord(true);
      }
      return jTextAreaCenter;
   }

   /**
    * This method initializes jButtonExit
    *
    * @return javax.swing.JButton
    *
    */
   private JButton getJButtonExit()
   {
      if (jButtonExit == null) {
         jButtonExit = new JButton();
         jButtonExit.setText("Close");
         jButtonExit.setEnabled(false);
         jButtonExit.addActionListener(new java.awt.event.ActionListener()
         {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
               getJFrame().setVisible(false);
            }
         });
      }
      return jButtonExit;
   }

   /**
    * Method startWindow starts the tool which generates the Travelfusion schemas
    *
    * @param psBaseDir the base directory of the travelfusion provider classes, for example:
    * "net\ifao\providerdata\travelfusion\xml\"
    * @param psURL the base URL, where the schemas can be found, for example:
    * "http://www.travelfusion.com/xmlspec/schema/"
    * @param psRequest the URL of the "enclosing" request schema, for example:
    * "requests/GeneralRequest.xsd"
    * @param psResponse the URL of the "enclosing" response schema, for example:
    * "responses/GeneralResponse.xsd"
    *
    * @author kaufmann
    */
   public static void startBuild(String psBaseDir, String psURL, String psRequest, String psResponse)
   {
      _sBaseDir = psBaseDir.replace('\\', '/');
      _sURL = psURL;
      _sRequest = psRequest;
      _sResponse = psResponse;

      // start the tool and it's GUI
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            ImportTravelfusionSchemas application = new ImportTravelfusionSchemas();

            application.getJFrame().setVisible(true);
         }
      });
   }

   /**
    * This method initializes jScrollPaneTextArea
    *
    * @return javax.swing.JScrollPane
    *
    */
   private JScrollPane getJScrollPaneTextArea()
   {
      if (jScrollPaneTextArea == null) {
         jScrollPaneTextArea = new JScrollPane();
         jScrollPaneTextArea
               .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
         jScrollPaneTextArea
               .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
         jScrollPaneTextArea.setAutoscrolls(true);
         jScrollPaneTextArea.setViewportView(getJTextAreaCenter());
      }
      return jScrollPaneTextArea;
   }

   /**
    * This method initializes jProgressBar
    *
    * @return javax.swing.JProgressBar
    *
    */
   private JProgressBar getJProgressBar()
   {
      if (jProgressBar == null) {
         jProgressBar = new JProgressBar();
         jProgressBar.setString("");
         jProgressBar.setStringPainted(true);
         jProgressBar.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 11));
         jProgressBar.setMaximum(10);
         jProgressBar.setPreferredSize(new java.awt.Dimension(188, 17));
         jProgressBar.setValue(0);
      }
      return jProgressBar;
   }

   /**
    * This thread does all the work to create data.xsd and dataBinding.xml for request and
    * response of Travelfusion
    *
    * <p>
    * Copyright &copy; 2006, i:FAO
    *
    * @author kaufmann
    */
   private class TravelfusionThread
      extends Thread
   {
      /**
       * Method run is the main method of the tool.
       *
       * @author kaufmann
       */
      @Override
      public void run()
      {
         displayText("Starting to generate the data.xsd and dataBinding.xml files for request and response\n\n");
         generateDataFiles(_sBaseDir + "request/", _sURL, _sRequest);
         generateDataFiles(_sBaseDir + "response/", _sURL, _sResponse);
         checkIgnoredSchemas();
         displayText("Files data.xsd and dataBinding.xml have been created for request and"
               + " response\nof Travelfusion.\n\nYou can now build the ProviderData.jar File.");
         getJButtonExit().setEnabled(true);
         getJProgressBar().setString("Finished");
         getJProgressBar().setValue(getJProgressBar().getMaximum());
      }

      /**
       * Outputs a message if unreachable schemas had been ignored 
       *
       * @author kaufmann
       */
      private void checkIgnoredSchemas()
      {
         if (getJTextAreaCenter().getText().contains("Ignored unreachable schema")) {
            displayText("\n\nWARNING:\nSome schemas could not be loaded and were ignored!\n\n");
         }
      }

      /**
       * Method generateDataFiles creates data.xsd and dataBinding.xml for one URL (request OR
       * response). After each step, the progress indicator is "increased"
       *
       * @param psDir directory of the provider classes
       * @param psSchemaURL base URL where the schemas will be found
       * @param psFile the "enclosing" schema
       *
       * @author kaufmann
       */
      private void generateDataFiles(String psDir, String psSchemaURL, String psFile)
      {
         // create a temporary directory
         String sTempDir = createTempDir(psDir);
         nextStep("read schemas");

         // read all schemas from the URL and generate the data.xsd file
         String sData = readUrl(psDir, sTempDir, psSchemaURL, psFile);
         nextStep("write data.xsd");

         // write data.xsd to disk
         writeToFile(psDir + "data.xsd", sData);
         nextStep("create binding");

         // create dataBinding.xml for the schemas found in the temporary directory.
         // the temporary directory will be ereased, too
         String sDataBinding = getBinding(psDir + sTempDir);
         nextStep("write dataBinding.xml");

         // write dataBinding.xml to disk
         writeToFile(psDir + "dataBinding.xml", sDataBinding);
         CorrectDatabindingXsd.correctDataBinding(new File(psDir + "dataBinding.xml"), "");

         nextStep("");
      }

      /**
       * Method nextStep "increases" the progress bar and displays a text on it
       *
       * @param psText text to show on the progress bar
       *
       * @author kaufmann
       */
      private void nextStep(String psText)
      {
         getJProgressBar().setValue(getJProgressBar().getValue() + 1);
         getJProgressBar().setString(psText);
      }

      /**
       * Method getBinding create dataBinding.xml for the schemas found in the directory passed.
       * The directory and its contents will be deleted!
       *
       * @param psDir directory containing the schemas
       * @return content for file dataBinding.xml
       *
       * @author kaufmann
       */
      private String getBinding(String psDir)
      {
         displayText("Generating binding file for directory " + psDir + "\n");
         String sSchema = getAllSchemasAsOne(psDir);
         String sBinding =
            WsdlObject
                  .getDataBinding(new XmlObject(sSchema), new HashSet<String>(), "", true, true);
         displayText(sBinding + "\n...DONE\n\n");
         return sBinding;
      }

      /**
       * Method getAllSchemasAsOne creates a String containing the contents of all schemas
       * within the directory passed. The directory and its contents will be deleted!
       *
       * @param psDir directory containing the schemas
       * @return concatenation of all schemas
       *
       * @author kaufmann
       */
      private String getAllSchemasAsOne(String psDir)
      {
         StringBuilder sbSchema = new StringBuilder();
         // step through all files within the directory
         File dir = new File(psDir);
         for (File file : dir.listFiles()) {
            // only regard schemas
            if (file.getName().endsWith(".xsd")) {
               try {
                  // read the whole file and add its contents to the string to return
                  BufferedReader reader = new BufferedReader(new FileReader(file));
                  String sLine;
                  while ((sLine = reader.readLine()) != null) {
                     sbSchema.append(sLine).append("\n");
                  }
                  reader.close();
               }
               catch (IOException exception) {
                  exception.printStackTrace();
               }
            }
            // remove the file
            displayText("Removing temporary file " + file.getAbsolutePath() + "\n");
            file.delete();
         }
         // remove the directory, after all files have been accessed and deleted
         displayText("Removing temporary directory " + dir.getAbsolutePath() + "\n");
         dir.delete();
         return sbSchema.toString();
      }

      /**
       * Method readUrl reads a schema and all schemas imported by the "main" schema from
       * a website and writes them to disk. The imports of the "main" schema are transformed
       * to absolute URLs and the transformed schema is returned as content for data.xsd
       *
       * @param psDir directory to store the files in
       * @param psTempDir directory (relative to psDir) to store the temporary files in
       * @param psBaseUrl base URL where the schemas will be found
       * @param psUrl path and name of the schema
       *
       * @return content for data.xsd
       * @author kaufmann
       */
      private String readUrl(String psDir, String psTempDir, String psBaseUrl, String psUrl)
      {
         // transform the URL to an absolute URL and separate it from the schema name
         while (psUrl.indexOf("/") >= 0) {
            if (psUrl.substring(0, psUrl.indexOf("/")).equals("..")) {
               psBaseUrl = psBaseUrl.substring(0, psBaseUrl.length() - 1);
               psBaseUrl = psBaseUrl.substring(0, psBaseUrl.lastIndexOf("/") + 1);
               psUrl = psUrl.substring(3);
            } else {
               psBaseUrl += psUrl.substring(0, psUrl.indexOf("/") + 1);
               psUrl = psUrl.substring(psUrl.indexOf("/") + 1);
            }
         }

         displayText("Reading " + psBaseUrl + psUrl + "\n");

         // read the schema from the web
         StringBuilder sbFileContent = new StringBuilder();
         StringBuilder sbData = new StringBuilder();
         String sArchiveDir =
            (psDir + psBaseUrl.substring(psBaseUrl.indexOf("schema"))).replace('\\', '/');
         try {
            URL url = new URL(psBaseUrl + psUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String sLine;
            while ((sLine = reader.readLine()) != null) {
               // replace tabs by 2 spaces
               // Castor does not supports anySimpleType
               sLine = sLine.replaceAll("\t", "  ").replaceAll("xs\\:anySimpleType", "xs:string");

               if (sLine.trim().length() != 0) {
                  sbFileContent.append(fixLineForFileContent(sLine, psBaseUrl)).append("\n");
                  sbData.append(fixLineForDataXsd(sLine, psBaseUrl)).append("\n");
                  displayText(sLine + "\n");
               }
            }
            reader.close();
            displayText("\n");
         }
         catch (MalformedURLException e) {
            e.printStackTrace();
         }
         catch (IOException e) {
            e.printStackTrace();
         }
         XmlObject schema = new XmlObject(sbFileContent.toString()).getFirstObject();
         correctSimpleTypes(schema, schema, "");

         String sSchema = schema.toString();
         // write the schema to disk
         writeToFile(sArchiveDir + psUrl, sSchema);
         // write the schema to the temporary folder, too
         writeToFile(psDir + psTempDir + psUrl, sSchema);

         // process the imports
         int iStart = sSchema.indexOf("schemaLocation=\"");
         while (iStart > 0) {
            // get the URL for the imported schema
            String sLine = sSchema.substring(iStart + 16);
            sLine = sLine.substring(0, sLine.indexOf("\""));
            // read it
            readUrl(psDir, psTempDir, psBaseUrl, sLine);
            // process the next import
            iStart = sSchema.indexOf("schemaLocation=\"", iStart + 1);
         }

         return sbData.toString();
      }

      private void correctSimpleTypes(XmlObject rootSchema, XmlObject element, String psName)
      {
         if (element == null) {
            return;
         }
         XmlObject[] subObjects = element.getObjects("");
         if (subObjects.length == 0) {
            // no sub objects
            return;
         }
         XmlObject xmlObject1 = subObjects[0];
         if (psName.length() > 0 && subObjects.length == 1
               && xmlObject1.getName().contains("simpleType")) {
            // delete all objects
            element.deleteObjects(xmlObject1);
            // move this element to the root
            rootSchema.addObject(xmlObject1);
            xmlObject1.setAttribute("name", "Simple" + psName);
            element.setAttribute("type", "Simple" + psName);
         } else {
            String sName = psName + element.getAttribute("name");
            for (XmlObject subObject : subObjects) {
               correctSimpleTypes(rootSchema, subObject, sName);
            }
         }
      }

      /**
       * Method fixLineForDataXsd transforms the imports to absolute URLs
       *
       * @param psLine the line currently read from the schema
       * @return the transformed line
       *
       * @author kaufmann
       */
      private Object fixLineForDataXsd(String psLine, String psBaseUrl)
      {
         String sBaseDir = psBaseUrl.substring(psBaseUrl.indexOf("schema")).replace('\\', '/');
         String sReturn = psLine;
         // check, if the line contains an import
         int iStart = sReturn.indexOf("schemaLocation=\"");
         // if yes, replace the URL by its absolute value
         if (iStart >= 0) {
            sReturn = psLine.substring(0, iStart + 16);
            String sAbsolutDir =
               getAbsoluteDir(
                     sBaseDir,
                     psLine.substring(iStart + 16, psLine.indexOf("\"", iStart + 17)).replace('\\',
                           '/'));
            sReturn += sAbsolutDir;
            sReturn += psLine.substring(psLine.indexOf("\"", iStart + 17));

            String sSchemaURL =
               psBaseUrl.substring(0, psBaseUrl.indexOf("schema/"))
                     + sAbsolutDir.replace('\\', '/');
            if (!schemaLocationExists(sSchemaURL)) {
               sReturn = "<!-- SchemaGenerator ignored unreachable schema '" + sSchemaURL + "' -->";
            }
         }
         return sReturn.replace('\\', '/');
      }


      /**
       * Method fixLineForFileContent ignores imports of unreachable schemas
       *
       * @param psLine the line currently read from the schema
       * @return the transformed line
       *
       * @author kaufmann
       */
      private Object fixLineForFileContent(String psLine, String psBaseUrl)
      {
         String sBaseDir = psBaseUrl.substring(psBaseUrl.indexOf("schema")).replace('\\', '/');
         String sReturn = psLine;
         // check, if the line contains an import
         int iStart = sReturn.indexOf("schemaLocation=\"");
         // if yes, replace the URL by its absolute value
         if (iStart >= 0) {
            String sAbsolutDir =
               getAbsoluteDir(
                     sBaseDir,
                     psLine.substring(iStart + 16, psLine.indexOf("\"", iStart + 17)).replace('\\',
                           '/'));

            String sSchemaURL =
               psBaseUrl.substring(0, psBaseUrl.indexOf("schema/"))
                     + sAbsolutDir.replace('\\', '/');
            if (!schemaLocationExists(sSchemaURL)) {
               sReturn = "<!-- SchemaGenerator ignored unreachable schema '" + sSchemaURL + "' -->";
               displayText("Ignored unreachable schema '" + sSchemaURL + "'");
            }
         }
         return sReturn;
      }

      /**
       * Checks, if a URL can be accessed
       *
       * @param psSchemaURL URL of a schema
       * @return true, if the URL exists/can be opened
       *
       * @author kaufmann
       */
      private boolean schemaLocationExists(String psSchemaURL)
      {
         boolean bSchemaExists = false;
         URL url;
         try {
            url = new URL(psSchemaURL);
            InputStream openStream = url.openStream();
            openStream.close();
            bSchemaExists = true;
         }
         catch (MalformedURLException pException) {}
         catch (IOException pException) {}
         return bSchemaExists;
      }

      /**
       * Method getAbsoluteUrl transforms an URL and Path to a single absolute URL
       *
       * @param pPsBaseUrl the base URL
       * @param psPath the relative Path to "follow"
       * @return absolute URL
       *
       * @author kaufmann
       */
      private String getAbsoluteDir(String psBaseDir, String psPath)
      {
         while (psPath.indexOf("/") >= 0) {
            // step one directory back
            if (psPath.substring(0, psPath.indexOf("/")).equals("..")) {
               psBaseDir = psBaseDir.substring(0, psBaseDir.length() - 1);
               psBaseDir = psBaseDir.substring(0, psBaseDir.lastIndexOf("/") + 1);
               psPath = psPath.substring(3);
            } else {
               // step one directory forth
               psBaseDir += psPath.substring(0, psPath.indexOf("/") + 1);
               psPath = psPath.substring(psPath.indexOf("/") + 1);
            }
         }
         return psBaseDir + psPath;
      }

      /**
       * Method createTempDir creates a temporary directory below psDir. Default name is
       * "temp", but if this directory already exists, a new name is searched by adding an
       * increasing int value to temp, i.e. "temp1", "temp2", "temp3",...
       *
       * @param psDir base directory
       * @return name of the temporary directory
       *
       * @author kaufmann
       */
      private String createTempDir(String psDir)
      {
         displayText("generating temporary folder...");
         // the default name of the temporary directory
         String sTmp = "temp/";
         int i = 0;
         // as long as the directory exists, add a number to "temp"
         while ((new File(psDir + sTmp)).exists()) {
            sTmp = "temp" + (++i) + "/";
         }
         // create the directory
         File dir = new File(psDir + sTmp);
         if (dir.mkdir()) {
            displayText("OK (" + psDir + sTmp + ")\n\n");
         } else {
            displayText("FAILED (" + psDir + sTmp + ")\n\n");
         }

         return sTmp;
      }

      /**
       * Method writeToFile writes a string to disk using UTF-8 characters. If the file already
       * exists, it will be overwritten.
       *
       * @param psFileName the file name
       * @param psContent the content for the file
       *
       * @author kaufmann
       */
      private void writeToFile(String psFileName, String psContent)
      {
         try {
            displayText("Writing file " + psFileName + " (" + psContent.length() + " bytes)...");

            File file = new File(psFileName);

            // overwrite the file, if necessary
            if (file.exists()) {
               file.delete();
               displayText("(overwritten)...");
            }

            File parent = file.getParentFile();
            if (!parent.exists()) {
               parent.mkdirs();
               displayText("created directory " + parent.getAbsolutePath() + "...");
            }

            FileOutputStream out = new FileOutputStream(psFileName);

            out.write(psContent.getBytes("UTF-8"));
            out.close();
            displayText("OK\n\n");
         }
         catch (IOException ex) {
            ex.printStackTrace();
         }
      }

      /**
       * Method displayText adds a text to the GUI and scrolls down, if necessary
       *
       * @param psText text to add to the GUI
       *
       * @author kaufmann
       */
      private void displayText(String psText)
      {
         getJTextAreaCenter().append(psText);
         getJTextAreaCenter().setCaretPosition(getJTextAreaCenter().getText().length());
      }
   }
}
