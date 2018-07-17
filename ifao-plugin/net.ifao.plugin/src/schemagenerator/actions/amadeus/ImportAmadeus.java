package schemagenerator.actions.amadeus;


import ifaoplugin.*;

import java.io.*;
import java.util.*;

import schemagenerator.actions.amadeus.gui.*;


/**
 * Main class for Import Amadeus help files (this is implemented as a
 * Thread, so that the execution is not disturbed).
 *
 * <p>
 * Copyright &copy; 2010, i:FAO
 *
 * @author brod
 */
public class ImportAmadeus
   extends Thread
{

   private static ImportAmadeus _importAmadeus;

   /**
    * This static method returns the link url to the BaseHtmlPage
    *
    * @return link url to the BaseHtmlPage
    *
    * @author brod
    */
   public static String getBaseHtmlPage()
   {
      return AmadeusFile.AMADEUSBASEURL + AmadeusHelpToFile.AMADEUSBASE;
   }

   /**
    * This is the main method to start the ImportAmadeus Window
    *
    * @param psBaseArctic BaseArctic directory
    * @param piProxyVersion Proxy Version
    * @param pOutputStream outputStream
    *
    * @author brod
    */
   public static void startWindow(String psBaseArctic, int piProxyVersion, PrintStream pOutputStream)
   {
      // don't start if already running
      if (_importAmadeus == null || !_importAmadeus.isAlive()) {
         // validate the Cache
         File amadeusCacheDirectory;
         // amadeusCacheDirectory = new File(psBaseArctic, "data/Cache" + piProxyVersion);
         amadeusCacheDirectory =
            new File("h:/cytric_development/new-arctic/projects/AmadeusHelp/Cache"
                  + piProxyVersion);

         // get the amadeusLib Directory
         File amadeusLibDirectory =
            Util.getProviderDataFile(psBaseArctic, "net/ifao/providerdata/amadeus");
         // create a new thread
         _importAmadeus =
            new ImportAmadeus("Proxy" + piProxyVersion, pOutputStream, amadeusCacheDirectory,
                  amadeusLibDirectory);
         // ... here we go
         _importAmadeus.start();
      }
   }

   private File _amadeusCacheDirectory;
   private File _amadeusLibDirectory;
   private ImportAmadeusGuiMain _gui;
   private PrintStream _logOutput;
   private String _sProxy;

   /**
    * Constructor ImportAmadeus
    *
    * @param psProxy Proxy String
    * @param pLogOutputs LogOutputs stream
    * @param pAmadeusCacheDirectory AmadeusCacheDirectory
    * @param pAmadeusLibDirectory AmadeusLibDirectory
    *
    * @author brod
    */
   public ImportAmadeus(String psProxy, PrintStream pLogOutputs, File pAmadeusCacheDirectory,
                        File pAmadeusLibDirectory)
   {
      _amadeusCacheDirectory = pAmadeusCacheDirectory;
      _amadeusLibDirectory = pAmadeusLibDirectory;
      _sProxy = psProxy;
      _gui = new ImportAmadeusGuiMain(_amadeusCacheDirectory);
      _logOutput = pLogOutputs;
   }

   /**
    * Method run (for the thread)
    *
    * @author brod
    */
   @Override
   public void run()
   {
      // init the file System
      AmadeusFile.initFileSystem();

      Hashtable<String, List<String>> proxyPages = null;
      if (!_gui.isStopped()) {
         // copy the AmadeusHelp files to File system
         AmadeusHelpToFile amadeusHelpToFile =
            new AmadeusHelpToFile(_amadeusCacheDirectory, _logOutput, _gui);
         proxyPages = amadeusHelpToFile.readMainHelpPage(_sProxy);
      }

      // activate the help link
      _gui.activateHelp();

      //      if (!_gui.isStopped()) {
      //         // clear all XML directory
      //         File xmlDirectory = new File(_amadeusCacheDirectory, "XML");
      //         if (xmlDirectory.exists()) {
      //            _logOutput.println("Clear directory " + xmlDirectory.getAbsolutePath());
      //            AmadeusUtils.deleteFile(xmlDirectory);
      //         }
      //      }


      if (!_gui.isStopped() && proxyPages != null) {
         // create xsd and xml files
         AmadeusHelpToXml amadeusHelpToXml =
            new AmadeusHelpToXml(_gui, _logOutput, _amadeusCacheDirectory);
         amadeusHelpToXml.loadInterfaces(proxyPages);
      }

      if (!_gui.isStopped()) {
         // update all data xsd file
         updateDataXsdFiles();
      }

      // reset the file System
      AmadeusFile.initFileSystem();

      // close the gui
      _gui.close();

   }

   /**
    * This method copies the data.xsd Files from the
    * amadeusCache Directory to the amadeusLib Directory
    *
    * @author brod
    */
   private void updateDataXsdFiles()
   {
      if (_amadeusLibDirectory == null || !_amadeusLibDirectory.exists()) {
         _logOutput.println("!!! Lib directory " + _amadeusLibDirectory + " does not exist !!!");
      } else {
         File newDirectory = new File(_amadeusCacheDirectory, "XML");
         if (newDirectory.exists()) {
            // create AmadeusXmlToLib object
            AmadeusXmlToLib amadeusXmlToLib =
               new AmadeusXmlToLib(newDirectory, _amadeusLibDirectory);
            // get the copy directories
            Hashtable<File, File> htCopyDirectories = amadeusXmlToLib.getCopyDirectories(_gui);

            // select specific files
            ImportAmadeusSelectMain.select(htCopyDirectories);

            // scan the directories to copy
            File[] keys = htCopyDirectories.keySet().toArray(new File[0]);
            for (File key : keys) {
               _logOutput.println("Copy data files from " + key + " to "
                     + htCopyDirectories.get(key));
               amadeusXmlToLib.copyDataXsds(key, htCopyDirectories.get(key));
            }
            // start to 'merge' the Powered fares
            PoweredFares.updatePoweredFares(_amadeusLibDirectory.getAbsolutePath(), _logOutput);

            // start to correct
            File correctTxt = new File(_amadeusLibDirectory, "Correct.txt");
            if (correctTxt.exists()) {
               AmadeusUtils.startCorrect(correctTxt.getAbsolutePath(), _logOutput);
            }

         }
      }
   }

}
