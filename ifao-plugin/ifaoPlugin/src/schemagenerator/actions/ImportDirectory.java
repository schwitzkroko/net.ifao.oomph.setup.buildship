package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;
import java.util.Arrays;

import net.ifao.xml.XmlObject;


public class ImportDirectory
{


   private String _sBaseDir;
   private int _iRule = 0;
   private String _sMethodName = "";
   private PrintStream out;

   /** 
    * Constructor ImportEuropcar 
    * 
    * @param psBaseDir arctic directory set in the schema generator 
    * 
    * @author Andreas Brod 
    * @param pOut 
    */
   public ImportDirectory(String psBaseDir)
   {
      _sBaseDir = psBaseDir;
   }

   /** 
   * private method to clear a subDirectory (all "data*.*" and 
   * "dtd.*" files will be deleted) 
   * 
   * @param pFileToClear File(directory) to clear
   * 
   * @author brod 
   */
   private void clearSubDirectory(File pFileToClear)
   {
      if (pFileToClear.isDirectory()) {
         File[] listFiles = pFileToClear.listFiles();
         for (int i = 0; i < listFiles.length; i++) {
            clearSubDirectory(listFiles[i]);
         }
         //         if (file.listFiles().length == 0)
         //            file.delete();
      } else if (pFileToClear.getName().startsWith("data")
            || pFileToClear.getName().startsWith("dtd.")) {
         pFileToClear.delete();
      }
   }

   private void copyXsdFile(File pXsdFile, String psMethodName, String psDirectory,
                            XmlObject correctData)
   {
      try {
         println("copy File " + pXsdFile);
         XmlObject xsdObject = new XmlObject(pXsdFile).getFirstObject();
         _sMethodName = psMethodName;
         String sDirectory = psMethodName.toLowerCase();
         if (psDirectory.length() > 0) {
            sDirectory += "/" + psDirectory;
         }

         File file = new File(_sBaseDir + "/" + sDirectory, "data.xsd");

         correctDataXsd(xsdObject, correctData, pXsdFile.getName());

         Util.writeToFile(file.getAbsolutePath(), xsdObject.toString());

         ImportXml.startDataBinding(file.getParent(), true, false, Util.camelCase(psDirectory),
               true, true);
         File file2 = new File(_sBaseDir + "/" + sDirectory, "data_new.xsd");
         if (file2.exists()) {
            file2.delete();
         }
      }
      catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }

   private void correctDataXsd(XmlObject xsdObject, XmlObject correctData, String psPath)
   {
      XmlObject[] subObjects = xsdObject.getObjects("");
      String sPath = psPath;
      String name = xsdObject.getAttribute("name");
      if (name.length() > 0) {
         sPath += "/" + name;
      }
      for (int i = 0; i < subObjects.length; i++) {
         correctDataXsd(subObjects[i], correctData, sPath);
      }
      XmlObject[] ifs = correctData.getObjects("If");
      for (int i = 0; i < ifs.length; i++) {
         _iRule = i + 1;
         doIf(xsdObject, ifs[i], sPath);
      }
      _iRule = 0;
   }

   private void doAction(XmlObject xsdObject, XmlObject actions, String psPath)
   {
      if (actions.getName().equalsIgnoreCase("If")) {
         doIf(xsdObject, actions, psPath);
      } else if (actions.getName().equalsIgnoreCase("Set")) {
         doSet(xsdObject, actions, psPath);
      } else if (actions.getName().equalsIgnoreCase("DeleteObjects")) {
         doDeleteObjects(xsdObject, actions, psPath);
      } else if (actions.getName().equalsIgnoreCase("CreateObject")) {
         doCreateObject(xsdObject, actions, psPath);
      } else if (actions.getName().equalsIgnoreCase("FindObject")) {
         doFindObject(xsdObject, actions, psPath);
      }

   }

   private void doCreateObject(XmlObject pXmlObject, XmlObject pCreateObject, String psPath)
   {
      String element = pCreateObject.getAttribute("element");
      String name = pCreateObject.getAttribute("name");
      String value = pCreateObject.getAttribute("value");
      if (value.equalsIgnoreCase("%type%")) {
         value = _sMethodName;
      }
      XmlObject[] findSubObjects = pXmlObject.findSubObjects(element, name, value);
      if (findSubObjects.length == 0) {
         println("Create " + element + " with " + psPath + "/"
               + (element.equalsIgnoreCase("attribute") ? "@" : "") + name + "=" + value);
         XmlObject subObject =
            pXmlObject.createObject(pXmlObject.getNameSpace() + ":" + element, name, value, true);
         XmlObject[] actions = pCreateObject.getObjects("");
         for (int i = 0; i < actions.length; i++) {
            doAction(subObject, actions[i], psPath + "/" + value);
         }
      }

   }

   private void doDeleteObjects(XmlObject pXmlObject, XmlObject pDeleteObjects, String psPath)
   {
      String name = pDeleteObjects.getAttribute("name");
      String except = pDeleteObjects.getAttribute("except");
      // remove all restrictions (except enumerations)
      XmlObject[] arrExcept;
      if (except.length() > 0) {
         arrExcept = pXmlObject.deleteObjects(except);
      } else {
         arrExcept = new XmlObject[0];
      }
      // delete all other elements
      String sDeleted = "";
      XmlObject[] deletedObjects = pXmlObject.deleteObjects(name);
      for (int i = 0; i < deletedObjects.length; i++) {
         String sName = deletedObjects[i].getName();
         if (!sDeleted.contains(sName)) {
            if (sDeleted.length() > 0) {
               sDeleted += ",";
            }
            sDeleted += sName + "s";
         }
      }
      // 'readd' the excepts
      for (int i = 0; i < arrExcept.length; i++) {
         pXmlObject.addObject(arrExcept[i]);
      }
      if (sDeleted.length() > 0) {
         println("Delete elements " + sDeleted + " of " + psPath
               + (except.length() > 0 ? " (except " + except + ")" : ""));
      }

   }

   private void doFindObject(XmlObject pXmlObject, XmlObject pFindObject, String psPath)
   {
      String element = pFindObject.getAttribute("element");
      String name = pFindObject.getAttribute("name");
      String value = pFindObject.getAttribute("value");
      if (value.equalsIgnoreCase("%type%")) {
         value = _sMethodName;
      }

      XmlObject[] findSubObjects = pXmlObject.findSubObjects(element, name, value);
      XmlObject[] actions = pFindObject.getObjects("");
      for (int i = 0; i < findSubObjects.length; i++) {
         for (int j = 0; j < actions.length; j++) {
            doAction(findSubObjects[i], actions[j], psPath);
         }
      }
   }

   private void doIf(XmlObject xsdObject, XmlObject pIf, String psPath)
   {
      boolean bOk = true;
      // validate the attribute
      String attribute = pIf.getAttribute("attribute");
      String value = pIf.getAttribute("value");
      if (value.equalsIgnoreCase("%type%")) {
         value = _sMethodName;
      }

      if (attribute.length() > 0) {
         if (!xsdObject.getAttribute(attribute).equals(value)) {
            bOk = false;
         }
      }
      // validate the element
      String element = pIf.getAttribute("element");
      if (bOk && element.length() > 0) {
         if (!xsdObject.getName().equals(element)) {
            bOk = false;
         }
      }
      String name = pIf.getAttribute("name");
      if (bOk && name.length() > 0) {
         if (!xsdObject.getAttribute("name").equals(name)) {
            bOk = false;
         }
      }
      if (bOk) {
         XmlObject[] actions = pIf.getObjects("");
         for (int i = 0; i < actions.length; i++) {
            doAction(xsdObject, actions[i], psPath);
         }
      }
   }

   private void doSet(XmlObject xsdObject, XmlObject pSet, String psPath)
   {
      String attribute = pSet.getAttribute("attribute");
      String value = pSet.getAttribute("value");
      if (attribute.length() > 0) {
         String nameSpace = xsdObject.getNameSpace();
         if (value.equalsIgnoreCase("%type%")) {
            value = _sMethodName;
         } else if (value.startsWith("xs:") && nameSpace.length() > 0) {
            value = nameSpace + value.substring(2);
         }
         if (xsdObject.setAttribute(attribute, value)) {
            println("Set " + psPath + "/@" + attribute + "=" + value);
         }
      }
   }

   private void println(String psText)
   {
      if (_iRule > 0) {
         out.println("Rule" + _iRule + ":" + psText);
      } else {
         char[] charArray = psText.toCharArray();
         Arrays.fill(charArray, '-');
         out.println("+-" + new String(charArray) + "-+");
         out.println("| " + psText + " |");
         out.println("+-" + new String(charArray) + "-+");
      }
   }

   /** 
    * method start 
    * 
    * @author brod 
    * @return 
    */
   public String start()
   {
      if (!_sBaseDir.contains("europcar")) {
         return "Directory " + _sBaseDir + " does not contain europcar data.";
      }
      ByteArrayOutputStream out1 = new ByteArrayOutputStream();
      out = new PrintStream(out1);

      // validate the correction file
      XmlObject correctData;
      try {
         correctData = new XmlObject(new File(_sBaseDir, "CorrectData.xml")).getFirstObject();
      }
      catch (Exception e) {
         correctData = new XmlObject("<Correct />").getFirstObject();
      }

      // get all schema Files
      File fDir = new File(_sBaseDir);
      File[] listFiles = fDir.listFiles();
      // clear all subdirectories
      for (int i = 0; i < listFiles.length; i++) {
         clearSubDirectory(listFiles[i]);
      }
      // create all data.xsd files
      for (int i = 0; i < listFiles.length; i++) {
         String sName = listFiles[i].getName().replaceAll("\\.", "/");
         if (sName.toLowerCase().endsWith("req/xsd")) {
            copyXsdFile(listFiles[i], sName.substring(0, sName.length() - 7), "req", correctData);
         } else if (sName.toLowerCase().endsWith("res/xsd")) {
            copyXsdFile(listFiles[i], sName.substring(0, sName.length() - 7), "res", correctData);
         } else if (sName.endsWith("/xsd")) {
            copyXsdFile(listFiles[i], sName.substring(0, sName.length() - 4), "", correctData);
         }

      }
      return out1.toString();
   }

}
