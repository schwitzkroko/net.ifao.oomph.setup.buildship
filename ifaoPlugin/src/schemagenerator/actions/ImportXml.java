package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;
import java.net.*;
import java.util.*;

import net.ifao.util.*;
import net.ifao.xml.*;
import schemagenerator.correctors.*;


/** 
 * Class ImportXml will be used to start the xml files 
 * 
 * <p> 
 * Copyright &copy; 2002, i:FAO, AG. 
 * @author Andreas Brod 
 */
public class ImportXml
{


   /** 
    * TODO (wunder) add comment for method startBuild 
    * 
    * @param psDir TODO (wunder) add text for param psDir
    * @param pbIgnoreSimpleTypes TODO (wunder) add text for param pbIgnoreSimpleTypes
    * @param pbDeleteDataNewXsd TODO (wunder) add text for param pbDeleteDataNewXsd
    * 
    * @author wunder 
    * @param psPrefix 
    */
   public static void startBuild(String psDir, boolean pbIgnoreSimpleTypes,
                                 boolean pbDeleteDataNewXsd, String psPrefix,
                                 boolean pbSplitAtUnderscore, boolean pbDateTimeHandler)
   {
      startBuild(psDir, pbIgnoreSimpleTypes, pbDeleteDataNewXsd, null, psPrefix,
            pbSplitAtUnderscore, pbDateTimeHandler);
   }

   /** 
    * mainm Method startBuild 
    * 
    * @param psDir Import directory 
    * @param pbIgnoreSimpleTypes ignore simple types 
    * @param pbDeleteDataNewXsd 
    * @param psNameCorrector TODO (wunder) add text for param psNameCorrector
    * 
    * @author Andreas Brod 
    * @param psPrefix 
    */
   public static void startBuild(String psDir, boolean pbIgnoreSimpleTypes,
                                 boolean pbDeleteDataNewXsd, ICorrector pCorrector,
                                 String psPrefix, boolean pbSplitAtUnderscore,
                                 boolean pbDateTimeHandler)
   {

      if (psDir.endsWith("\\") || psDir.endsWith("/")) {
         psDir = psDir.substring(0, psDir.length() - 1);
      }

      File[] files = (new File(psDir)).listFiles();
      XmlObject schema = (new XmlObject("<schema />")).getFirstObject();
      XmlObject data =
         (new XmlObject("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" />"))
               .getFirstObject();

      for (int i = 0; i < files.length; i++) {
         String sName = files[i].getName();

         if (sName.endsWith(".xsd") && !sName.startsWith("data")) {
            System.out.println("Analyse " + sName);

            try {
               XmlObject xml = (new XmlObject(files[i])).createObject("schema");

               // reset the includes
               XmlObject[] includes = xml.getObjects("include");
               for (int j = 0; j < includes.length; j++) {
                  String schemaLocation = includes[j].getAttribute("schemaLocation");
                  if (schemaLocation.length() > 0) {
                     includes[j].setAttribute("schemaLocation", "../"
                           + getDataXsdName(schemaLocation));
                  }
               }

               String sTargetNameSpace = xml.getAttribute("targetNamespace");
               schema.addObjects(xml.getObjects(""));

               XmlObject include = (new XmlObject("<xs:include />")).getFirstObject();
               include.setAttribute("schemaLocation", getDataXsdName(sName));

               if (sTargetNameSpace.length() > 0) {
                  include.setAttribute("id", "ns" + (i + 1));
                  //include.setAttribute("namespace", sTargetNameSpace);

                  data.addObject(include);
                  if (data.getAttribute("targetNamespace").length() == 0) {
                     data.setAttribute("targetNamespace", sTargetNameSpace);
                  }
                  data.setAttribute("xmlns:ns" + (i + 1), sTargetNameSpace);
               } else {
                  data.addObject(include);
               }
               File newDataXsdName = new File(files[i].getParentFile(), getDataXsdName(sName));

               if (pCorrector != null) {
                  pCorrector.correct(xml);
               }

               correctChoiceSequence(xml, xml);
               Util.writeToFile(newDataXsdName.getAbsolutePath(), xml.toString());
            }
            catch (FileNotFoundException ex) {}
         }

      }

      Util.writeToFile(psDir + "/data.xsd", data.toString());

      startDataBinding(psDir, pbIgnoreSimpleTypes, true, psPrefix, pbSplitAtUnderscore,
            pbDateTimeHandler);

      // delete the data_new.xsd
      if (pbDeleteDataNewXsd) {
         // reload the data.xsd
         XmlObject[] imports =
            new XmlObject(Util.loadFile(psDir + "/data.xsd")).getFirstObject()
                  .getObjects("include");
         // if there are no include any more
         if (imports.length == 0 && data.getObjects("include").length > 0) {
            // delete all 'foreign' data.xsds
            deleteForeignDataXsds(psDir);
         }
         File file = new File(psDir + "/data_new.xsd");
         if (file.exists())
            file.delete();
      }
   }

   private static boolean deleteForeignDataXsds(String psDir)
   {
      boolean bDeleted = false;
      File file = new File(psDir);
      if (file.isDirectory()) {
         File[] listFiles = file.listFiles();
         for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].getName().endsWith("data.xsd")
                  && !listFiles[i].getName().equals("data.xsd")) {
               listFiles[i].delete();
               bDeleted = true;
            }
         }

         // delete within sub-directories
         for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
               if (deleteForeignDataXsds(listFiles[i].getAbsolutePath())) {
                  bDeleted = true;
               }
            }
         }
         // remove directory if deleted 
         if (bDeleted) {
            file = new File(psDir);
            listFiles = file.listFiles();
            if (listFiles.length == 0) {
               file.delete();
            }
         }
      }
      return bDeleted;
   }

   /** 
    * private method correctChoiceSequence 
    * 
    * @param pXmlRoot schema Root object 
    * @param pXmlElement xml element 
    * 
    * @author brod 
    */
   private static void correctChoiceSequence(XmlObject pXmlRoot, XmlObject pXmlElement)
   {
      String sSchemaName = pXmlElement.getName();
      if (sSchemaName.equals("choice")) {
         Hashtable<String, List<XmlObject>> htAllElements =
            new Hashtable<String, List<XmlObject>>();
         // get sequence
         XmlObject[] sequences = pXmlElement.getObjects("sequence");
         for (int i = 0; i < sequences.length; i++) {
            XmlObject[] elements = sequences[i].getObjects("element");
            for (int j = 0; j < elements.length; j++) {
               String sName = elements[j].getAttribute("name");
               if (sName.length() > 0) {
                  List<XmlObject> list = htAllElements.get(sName);
                  if (list == null) {
                     list = new ArrayList<XmlObject>();
                     htAllElements.put(sName, list);
                  } else {
                     // if this is the first duplicate
                     if (list.size() == 1) {
                        XmlObject copy = elements[j].copy();
                        copy.setAttribute("minOccurs", null);
                        copy.setAttribute("maxOccurs", null);
                        pXmlRoot.addObject(copy);
                     }
                  }
                  list.add(elements[j]);
                  if (list.size() > 1)
                     for (int k = list.size() - 2; k < list.size(); k++) {
                        XmlObject xmlObject = list.get(k);
                        xmlObject.setAttribute("name", null);
                        xmlObject.setAttribute("type", null);
                        xmlObject.setAttribute("ref", sName);
                     }
               }
            }

         }

      }
      XmlObject[] subObjects = pXmlElement.getObjects("");
      for (int i = 0; i < subObjects.length; i++) {
         correctChoiceSequence(pXmlRoot, subObjects[i]);
      }
   }

   /** 
    * TODO (wunder) add comment for method getDataXsdName 
    * 
    * @param psFileName TODO (wunder) add text for param psFileName
    * @return TODO (wunder) add text for returnValue
    * 
    * @author wunder 
    */
   private static String getDataXsdName(String psFileName)
   {
      String sName = "data/data.";
      StringTokenizer st =
         new StringTokenizer("" + psFileName.substring(0, psFileName.lastIndexOf(".") + 1), "._");
      while (st.hasMoreTokens()) {
         sName += st.nextToken().toLowerCase();
         if (st.hasMoreTokens())
            sName += "_";
      }
      return sName + ".xsd";
   }

   /** 
    * public Method startDataBinding 
    * 
    * @param psDir import directory 
    * @param pbIgnoreSimpleTypes ignore simple types 
    * 
    * @author Andreas Brod 
    * @param psPrefix 
    */
   public static void startDataBinding(String psDir, boolean pbIgnoreSimpleTypes, String psPrefix,
                                       boolean pbSplitAtUnderscore, boolean pbDateTimeHandler)
   {
      startDataBinding(psDir, pbIgnoreSimpleTypes, false, psPrefix, pbSplitAtUnderscore,
            pbDateTimeHandler);

   }

   /** 
    * Method startDataBinding 
    * 
    * @param psDir 
    * @param pbIgnoreSimpleTypes 
    * @param pbSub 
    * @return 
    * 
    * @author Andreas Brod 
    * @param psPrefix 
    */
   public static void startDataBinding(String psDir, boolean pbIgnoreSimpleTypes, boolean pbSub,
                                       String psPrefix, boolean pbSplitAtUnderscore,
                                       boolean pbDateTimeHandler)
   {

      try {
         if (psDir.endsWith("\\") || psDir.endsWith("/")) {
            psDir = psDir.substring(0, psDir.length() - 1);
         }

         File dataXsd = new File(psDir ,"data.xsd");

         if (!dataXsd.exists()) {
            if (dataXsd.getParentFile().exists()) {
               File[] files = dataXsd.getParentFile().listFiles();

               if (files != null) {
                  for (int i = 0; i < files.length; i++) {
                     if (files[i].isDirectory()) {
                        startDataBinding(files[i].getAbsolutePath(), pbIgnoreSimpleTypes, true,
                              psPrefix, pbSplitAtUnderscore, pbDateTimeHandler);
                     }
                  }
               }
            }
            return;
         }

         System.out.println("Create DataBinding for " + dataXsd.getAbsolutePath());

         XmlObject schema = (new XmlObject(dataXsd)).getObject("schema");

         importSchema(schema, psDir, new HashSet<String>(), pbIgnoreSimpleTypes, psPrefix,
               pbSplitAtUnderscore, pbDateTimeHandler);

         ImportToPackages.correctSchema(schema);

         HashSet<String> simpleTypes = pbIgnoreSimpleTypes ? null : new HashSet<String>();

         Util.writeToFile(psDir + "/dataBinding.xml", WsdlObject.getDataBinding(schema,
               simpleTypes, psPrefix, pbSplitAtUnderscore, pbDateTimeHandler));

         CorrectDatabindingXsd.correctDataBinding(new File(psDir + "/dataBinding.xml"), "");

         if (!pbIgnoreSimpleTypes) {
            Util.writeToFile(psDir + "/data.xsd", schema.toString());
         } else {
            Util.writeToFile(psDir + "/data_new.xsd", schema.toString());
         }

      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   /** 
    * Method importSchema 
    * 
    * @param pXmlSchema 
    * @param psDir 
    * @param phsImports 
    * @param pbIgnoreSimpleTypes 
    * 
    * @author brod 
    * @param psPrefix 
    */
   private static void importSchema(XmlObject pXmlSchema, String psDir, HashSet<String> phsImports,
                                    boolean pbIgnoreSimpleTypes, String psPrefix,
                                    boolean pbSplitAtUnderscore, boolean pbDateTimeHandler)
   {
      // get Imports
      XmlObject[] imports = pXmlSchema.getObjects("include");
      List<String> lstDuplicate = new ArrayList<String>();
      String parentDataXsd = "../data.xsd";

      for (int i = 0; i < imports.length; i++) {
         String sName = getName(psDir, imports[i]);

         if (sName.length() > 0 && !sName.endsWith(parentDataXsd)) {
            if (phsImports.add(sName)) {
               XmlObject subSchema = getSchemaFromUrl(sName);
               if (subSchema != null) {
                  // 'copy' basic schema attibutes
                  String[] attributeNames = subSchema.getAttributeNames(false);
                  for (int j = 0; j < attributeNames.length; j++) {
                     pXmlSchema.setAttribute(attributeNames[j]);
                  }
                  if (sName.lastIndexOf("/") > 0) {
                     sName = sName.substring(0, sName.lastIndexOf("/"));
                     importSchema(subSchema, sName, phsImports, pbIgnoreSimpleTypes, psPrefix,
                           pbSplitAtUnderscore, pbDateTimeHandler);
                  }
                  XmlObject[] objects = subSchema.getObjects("");
                  // add objects which are non existent
                  for (int j = 0; j < objects.length; j++) {
                     XmlObject findSubObject = null;
                     String sSubName = objects[j].getAttribute("name");
                     if (sSubName.length() > 0) {
                        String sKey = objects[j].getName() + "~" + sSubName;
                        findSubObject =
                           pXmlSchema.findSubObject(objects[j].getName(), "name", sSubName);
                        if (findSubObject != null
                              && !findSubObject.toString().equals(objects[j].toString())) {
                           // different SubObject
                           if (!lstDuplicate.contains(sKey))
                              lstDuplicate.add(sKey);
                        }
                     }
                     if (findSubObject == null)
                        pXmlSchema.addObject(objects[j]);
                  }
               }
            }
         }
      }
      if (lstDuplicate.size() > 0) {
         // delete 'duplicate' objects
         XmlObject[] objects = pXmlSchema.getObjects("");
         for (int i = 0; i < objects.length; i++) {
            String sKey = objects[i].getName() + "~" + objects[i].getAttribute("name");
            if (lstDuplicate.contains(sKey))
               pXmlSchema.deleteObjects(objects[i]);
         }

         // create different schemas
         for (int i = 0; i < imports.length; i++) {
            String sName = getName(psDir, imports[i]);

            if (sName.length() > 0) {
               XmlObject subSchema = getSchemaFromUrl(sName);
               String schemaName = imports[i].getAttribute("schemaLocation");
               schemaName = schemaName.substring(schemaName.lastIndexOf("/") + 1);
               if (schemaName.indexOf(".") > 0)
                  schemaName = schemaName.substring(0, schemaName.indexOf("."));
               // remove common Elements
               XmlObject[] subObjects = subSchema.getObjects("");
               for (int j = 0; j < subObjects.length; j++) {
                  String sKey = subObjects[j].getName() + "~" + subObjects[j].getAttribute("name");
                  if (!lstDuplicate.contains(sKey))
                     subSchema.deleteObjects(subObjects[j]);
               }
               // if there are any objects left
               if (subSchema.getObjects("").length > 0) {
                  XmlObject includeObject =
                     new XmlObject("<xs:import schemaLocation=\"" + parentDataXsd
                           + "\" namespace=\"" + subSchema.getAttribute("targetNamespace") + "\"/>")
                           .getFirstObject();
                  subSchema.setAttribute("targetNamespace",
                        subSchema.getAttribute("targetNamespace") + "/" + schemaName);
                  subSchema.addElementObject(includeObject, 0);
                  Util.writeToFile(psDir + "/" + schemaName + "/data.xsd", subSchema.toString());
                  startDataBinding((new File(psDir + "/" + schemaName)).getAbsolutePath(),
                        pbIgnoreSimpleTypes, false, psPrefix, pbSplitAtUnderscore,
                        pbDateTimeHandler);
               }
            }
         }
      }

      // delete includes
      pXmlSchema.deleteObjects("include");
   }

   /** 
    * method getSchemaFromUrl 
    * 
    * @param psSchemaName SchemaName 
    * @return schema from url 
    * 
    * @author brod 
    */
   private static XmlObject getSchemaFromUrl(String psSchemaName)
   {
      System.out.println("open " + psSchemaName);

      StringBuffer sText = new StringBuffer();
      try {
         URL url = new URL(psSchemaName);
         String sLine;
         BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
         while ((sLine = reader.readLine()) != null) {
            sText.append(sLine);
            sText.append("\n");
         }
         reader.close();
      }
      catch (IOException e) {
         e.printStackTrace();
      }

      System.out.println("open " + psSchemaName + " (" + sText.length() + " bytes)");
      XmlObject subSchema = (new XmlObject(sText.toString())).getObject("schema");
      return subSchema;
   }

   /** 
    * method getName 
    * 
    * @param psDir Dir 
    * @param pXmlObject xmlObject 
    * @return Name 
    * 
    * @author brod 
    */
   private static String getName(String psDir, XmlObject pXmlObject)
   {
      String sName = pXmlObject.getAttribute("schemaLocation");

      if (sName.length() > 0) {
         if (sName.startsWith("http") || sName.startsWith("file:")) {
            // make nothing
         } else if (sName.indexOf(":") > 0) {
            sName = "file:/" + sName;
         } else if (psDir.indexOf(":") > 0) {
            sName = (psDir.startsWith("file:") ? "" : "file:/") + psDir + "/" + sName;
         } else {
            sName = "file:/" + psDir + "/" + sName;

         }
         sName = sName.replaceAll("\\\\", "/");


      }
      return sName;
   }

   public static String getCorrectedName(String psName)
   {
      // get absolute File Name
      String[] split = psName.split("[\\\\/]");
      String sNewName = "";
      String sDelimiter = "";
      for (int i = 0; i < split.length; i++) {
         sNewName += sDelimiter;
         if (i + 1 < split.length && split[i + 1].equals("..")) {
            // ignore this directory
            i++;
            sDelimiter = "";
         } else {
            sNewName += split[i];
            if (i == 0 && sNewName.endsWith(":") && sNewName.endsWith("http")) {
               // e.g. starts with "http:"
               sDelimiter = "//";
            } else {
               sDelimiter = "/";
            }
         }
      }
      return sNewName;
   }

   /** 
    * method clearImportDir 
    * 
    * @param psImportDir import Directory 
    * 
    * @author brod 
    */
   public static void clearImportDir(String psImportDir)
   {
      File f = new File(psImportDir);
      if (f.exists() && f.isDirectory()) {
         File[] listFiles = f.listFiles();
         for (int i = 0; i < listFiles.length; i++) {
            String sName = listFiles[i].getName();
            // delete all directories except CVS
            if (listFiles[i].isDirectory()) {
               if (!sName.equalsIgnoreCase("CVS"))
                  deleteDir(listFiles[i]);
            } else if (sName.endsWith(".java") || sName.startsWith("data")) {
               listFiles[i].delete();
            }
         }
      }
   }

   /** 
    * private method deleteDir 
    * 
    * @param pFile2Delete filename 
    * 
    * @author brod 
    */
   private static void deleteDir(File pFile2Delete)
   {
      File[] listFiles = pFile2Delete.listFiles();
      for (int i = 0; i < listFiles.length; i++) {
         if (listFiles[i].isDirectory())
            deleteDir(listFiles[i]);
         else
            listFiles[i].delete();
      }
      pFile2Delete.delete();
   }
}
