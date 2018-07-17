package schemagenerator.actions.amadeus;


import java.io.*;
import java.util.Hashtable;

import schemagenerator.actions.amadeus.gui.ImportAmadeusGuiMain;


/** 
 * helper class to copy Amadeus Xml files to the amadeus Lib 
 * directories 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author brod 
 */
class AmadeusXmlToLib
{
   private File _amadeusLibDirectory;
   private File _helpDirectory;

   /** 
    * Constructor AmadeusXmlToLib 
    * 
    * @param pHelpDirectory xml help directory 
    * @param pAmadeusLibDirectory AmadeusLib Directory 
    * 
    * @author brod 
    */
   public AmadeusXmlToLib(File pHelpDirectory, File pAmadeusLibDirectory)
   {
      _helpDirectory = pHelpDirectory;
      _amadeusLibDirectory = pAmadeusLibDirectory;
   }

   /** 
    * This method copies DataXsds files 
    * 
    * @param pSourceDirectory SourceDirectory 
    * @param pTargetDirectory TargetDirectory 
    * 
    * @author brod 
    */
   public void copyDataXsds(File pSourceDirectory, File pTargetDirectory)
   {
      File[] listFiles = pSourceDirectory.listFiles();
      // 1. get data.xsd and databinding.xml
      File dataXsd = null;
      File dataBindingXml = null;
      for (int i = 0; i < listFiles.length; i++) {
         if (listFiles[i].getName().equalsIgnoreCase("data.xsd")) {
            dataXsd = listFiles[i];
         } else if (listFiles[i].getName().equalsIgnoreCase("databinding.xml")) {
            dataBindingXml = listFiles[i];
         }
      }
      if (dataXsd != null) {
         // create empty directory (if not existent)
         if (!pTargetDirectory.exists()) {
            pTargetDirectory.mkdirs();
         }
         // 2. clean up the target directory
         File[] lstOldFile = pTargetDirectory.listFiles();
         for (int i = 0; i < lstOldFile.length; i++) {
            String sOldName = lstOldFile[i].getName();
            if (sOldName.equalsIgnoreCase("databinding.xsd")
                  || sOldName.equalsIgnoreCase("databinding.xml")
                  || sOldName.equalsIgnoreCase("XmlModel.xml")
                  || (sOldName.startsWith("Structure") && sOldName.endsWith("xml"))) {
               lstOldFile[i].delete();
            }
         }
         // 3. copy data.xsd
         AmadeusUtils.copyFile(dataXsd, new File(pTargetDirectory, dataXsd.getName()));

         // 4. copy databinding.xml
         if (dataBindingXml != null) {
            AmadeusUtils.copyFile(dataBindingXml, new File(pTargetDirectory, dataBindingXml
                  .getName()));
         }

      }
   }

   /** 
    * This method returns the copy Directories 
    * 
    * @return table of directories with data.xsd files (old and new) 
    * 
    * @author brod 
    * @param gui 
    */
   public Hashtable<File, File> getCopyDirectories(ImportAmadeusGuiMain pGui)
   {
      Hashtable<File, File> htCopyDirectories =
         getDirectories(_helpDirectory, _amadeusLibDirectory);

      // validate if additional version exist
      Hashtable<String, File> htNewFiles = getVersionFiles(_helpDirectory, pGui, 0);
      Hashtable<String, File> htOldFiles = getVersionFiles(_amadeusLibDirectory, pGui, 50);
      for (String sKey : htOldFiles.keySet().toArray(new String[0])) {
         // if this also exists within new environment
         File file = htNewFiles.get(sKey);
         if (file != null && file.exists() && htCopyDirectories.get(file) == null) {
            htCopyDirectories.put(file, htOldFiles.get(sKey));
         }
      }
      // create new 'empty' directories
      for (String sKey : htNewFiles.keySet().toArray(new String[0])) {
         File file = htNewFiles.get(sKey);
         if (htCopyDirectories.get(file) == null) {
            String sPath = file.getAbsolutePath().replaceAll("\\\\", "/");
            if (sPath.contains("/XML/")) {
               htCopyDirectories.put(file, new File(_amadeusLibDirectory, sPath.substring(sPath
                     .lastIndexOf("/XML/") + 5)));
            }
         }
      }
      return htCopyDirectories;
   }

   /** 
    * This method return the Directories (where the data.xsd files are 
    * located) 
    * 
    * @param pProxyDirectory ProxyDirectory 
    * @param pAmadeusLibDirectory AmadeusLibDirectory 
    * @return hashtable of file (xml to amadeus Lib Directory) 
    * 
    * @author brod 
    */
   private Hashtable<File, File> getDirectories(File pProxyDirectory, File pAmadeusLibDirectory)
   {
      Hashtable<File, File> ht = new Hashtable<File, File>();
      if (!pAmadeusLibDirectory.exists()) {
         return ht;
      }
      File[] arrProxyFiles = pProxyDirectory.listFiles();
      for (int i = 0; i < arrProxyFiles.length; i++) {
         // validate if files exist
         File fProxyFile = arrProxyFiles[i];

         String sProxyFileName = fProxyFile.getName();
         if (fProxyFile.isDirectory()) {
            File fAmadLibFile = new File(pAmadeusLibDirectory, sProxyFileName);
            if (fAmadLibFile.exists() && fAmadLibFile.isDirectory()) {
               ht.putAll(getDirectories(fProxyFile, fAmadLibFile));
            }
         } else if (sProxyFileName.equals("data.xsd")) {
            ht.put(pProxyDirectory, pAmadeusLibDirectory);
         }
      }
      return ht;
   }

   /** 
    * This method return additional VersionFiles 
    * 
    * @param pFile File 
    * @return hastable of files 
    * 
    * @author brod 
    * @param j 
    * @param pGui 
    */
   private Hashtable<String, File> getVersionFiles(File pFile, ImportAmadeusGuiMain pGui,
                                                   int piSliderValue)
   {
      Hashtable<String, File> htFiles = new Hashtable<String, File>();
      if (pFile.isDirectory()) {
         File[] listFiles = pFile.listFiles();
         for (int i = 0; i < listFiles.length; i++) {
            if (pGui != null) {
               pGui.scanLibDirectories(
                     (int) (Math.round(i * 50.0 / listFiles.length) + piSliderValue), 100,
                     listFiles[i].getName());
            }
            htFiles.putAll(getVersionFiles(listFiles[i], null, piSliderValue));
         }
         if (pGui != null) {
            pGui.scanLibDirectories(50 + piSliderValue, 100, "");
         }
      } else if (pFile.getName().equals("data.xsd")) {
         // read the file
         String psDataXsd = AmadeusFile.readFile(pFile);
         int iStart = psDataXsd.indexOf("<!--");
         if (iStart > 0) {
            int iEnd = psDataXsd.indexOf("-->");
            if (iEnd > iStart + 10) {
               String sContent = psDataXsd.substring(iStart + 5, iEnd - 3).trim();
               if (sContent.contains("http://api.dev.amadeus.net/")) {
                  htFiles.put(sContent.substring(sContent.lastIndexOf("/") + 1), pFile
                        .getParentFile());
               }
            }
         }
         // get the version
      }
      return htFiles;
   }
}
