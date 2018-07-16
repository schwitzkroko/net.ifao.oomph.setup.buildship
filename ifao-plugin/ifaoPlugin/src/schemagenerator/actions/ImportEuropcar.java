package schemagenerator.actions;


import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import net.ifao.xml.*;


/** 
 * Class ImportEuropcar 
 * 
 * <p> 
 * Copyright &copy; 2008, i:FAO Group GmbH 
 * @author Andreas Brod 
 */
public class ImportEuropcar
{
   private String _sBaseDir;
   private Hashtable<String, Dtd2Xsd> _importDtds;

   /** 
    * Constructor ImportEuropcar 
    * 
    * @param psBaseDir arctic directory set in the schema generator 
    * 
    * @author Andreas Brod 
    */
   public ImportEuropcar(String psBaseDir)
   {
      _sBaseDir = psBaseDir;
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
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream log = new PrintStream(out);

      // get all schema Files
      File fDir = new File(_sBaseDir);
      File[] listFiles = fDir.listFiles();
      // clear all subdirectories
      for (int i = 0; i < listFiles.length; i++) {
         clearSubDirectory(listFiles[i]);
      }

      // import the dtd's file
      _importDtds = importDtds();

      // create data.xsd's
      for (int i = 0; i < listFiles.length; i++) {
         String sName = listFiles[i].getName();
         if (!sName.startsWith("data") && sName.endsWith(".xsd")) {
            importFile(listFiles[i], log);
         }
      }

      // create imports
      listFiles = fDir.listFiles();
      for (int i = 0; i < listFiles.length; i++) {
         if (listFiles[i].isDirectory()) {
            createImports(listFiles[i]);
         }
      }

      return writeCorrectionLog(out);
   }

   /** 
    * private method to write a correction Log 
    * 
    * @param pOutputStream OutputStream (to write the correction log to) 
    * 
    * @author brod 
    * @return 
    */
   private String writeCorrectionLog(OutputStream pOutputStream)
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      BufferedReader reader = new BufferedReader(new StringReader(pOutputStream.toString()));
      Hashtable<String, List<String>> ht = new Hashtable<String, List<String>>();
      String sLine;
      try {
         while ((sLine = reader.readLine()) != null) {
            // get the schema
            if (sLine.contains("[")) {
               String sElement = sLine.substring(sLine.indexOf("[") + 1, sLine.indexOf("]"));
               sLine = sLine.replaceAll(Pattern.quote("[" + sElement + "]"), "");
               List<String> list = ht.get(sElement);
               if (list == null) {
                  list = new ArrayList<String>();
                  ht.put(sElement, list);
               }
               list.add(sLine);
            }
         }
         // print the result
         PrintStream psOut = new PrintStream(out);
         String[] keys = ht.keySet().toArray(new String[0]);
         Arrays.sort(keys);
         for (int i = 0; i < keys.length; i++) {
            String sKey = keys[i];
            String sAdd = "+-";
            for (int j = 0; j < sKey.length(); j++) {
               sAdd += "-";
            }
            sAdd += "-+";
            psOut.println(sAdd);
            psOut.print("| ");
            psOut.print(sKey);
            psOut.println(" |");
            psOut.println(sAdd);
            List<String> list = ht.get(sKey);
            Collections.sort(list);
            for (int j = 0; j < list.size(); j++) {
               psOut.println(list.get(j));
            }
         }
         psOut.close();

         FileOutputStream fileOutputStream =
            new FileOutputStream(new File(_sBaseDir, "Correct.log"));
         fileOutputStream.write(out.toByteArray());
         fileOutputStream.close();
      }
      catch (Exception e) {
         // should never happen
         e.printStackTrace(new PrintStream(out));
      }
      return out.toString();
   }

   /** 
    * private method to create the imports 
    * 
    * @param pFile file to import 
    * 
    * @author brod 
    */
   private void createImports(File pFile)
   {
      File[] listFiles = pFile.listFiles();
      Arrays.sort(listFiles);
      Hashtable<File, XmlObject> ht = new Hashtable<File, XmlObject>();
      XmlObject common =
         new XmlObject(
               "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" />")
               .getFirstObject();

      for (int i = 0; i < listFiles.length; i++) {
         // if there is already a data.xsd
         if (listFiles[i].getName().equalsIgnoreCase("data.xsd"))
            // stop the execution
            return;
         if (listFiles[i].getName().startsWith("data.") && listFiles[i].getName().endsWith(".xsd")) {
            try {
               ht.put(listFiles[i], new XmlObject(listFiles[i]).getObject("schema"));
            }
            catch (Exception e) {}
         }
      }
      Object[] keys = ht.keySet().toArray();
      if (keys.length > 1) {
         // there are more than 2 subdirectories to merge
         for (int i = 0; i < keys.length; i++) {
            XmlObject xmlObjects = ht.get(keys[i]);
            boolean bSecondTry =
               xmlObjects.findSubObjects("complexType", "name", "XRSserviceResponse").length > 0;
            XmlObject[] objects = xmlObjects.getObjects("");
            for (int j = 0; j < objects.length; j++) {

               XmlObject xmlObject = objects[j];
               mergeObject(xmlObject.copy(), common, bSecondTry, 1);
            }
         }
         try {
            Utils.writeFile(new File(pFile, "data.xsd"), common.toString());
            ImportXml.startDataBinding(pFile.getAbsolutePath(), true, false, "", true, true);
            File dataNewXsd = new File(pFile, "data_new.xsd");
            if (dataNewXsd.exists()) {
               dataNewXsd.delete();
            }

         }
         catch (Exception e) {}
      } else {
         // scan subdirectories
         for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
               createImports(listFiles[i]);
            }
         }
      }
   }

   /** 
    * private method to merge an object 
    * 
    * @param pObjectToAdd objectToAdd 
    * @param pMainObject mainObject (to add this object) 
    * @param pbSecondTry SecondTry is set for the second try 
    * @param piLevel level (will be increased each time, recursively) 
    * 
    * @author brod 
    */
   private void mergeObject(XmlObject pObjectToAdd, XmlObject pMainObject, boolean pbSecondTry,
                            int piLevel)
   {
      XmlObject[] oldObject =
         pMainObject.findSubObjects(pObjectToAdd.getName(), "name", pObjectToAdd
               .getAttribute("name"));
      if (oldObject.length == 0) {
         pMainObject.addObject(pObjectToAdd);
         if (pbSecondTry && piLevel > 1) {
            setMinOccurs(pObjectToAdd);
         }
      } else {
         XmlObject firstObject = oldObject[0];
         if (firstObject.toString().equals(pObjectToAdd.toString())) {
            // everything is OK
         } else {
            if (pbSecondTry && piLevel > 1) {
               setMinOccurs(firstObject);
            }
            boolean bOk = false;
            // validate ComplexTypes with one element
            if (pObjectToAdd.getName().equals("complexType")) {
               XmlObject sequenceToAdd = pObjectToAdd.getObject("sequence");
               if (sequenceToAdd != null) {
                  XmlObject[] elementsToAdd = sequenceToAdd.getObjects("element");
                  if (elementsToAdd.length == 1) {
                     XmlObject oldSequence = firstObject.getObject("sequence");
                     if (oldSequence == null) {
                        oldSequence = firstObject.getObject("choice");
                     } else {
                        // if sequence already contain element
                        String name = elementsToAdd[0].getName();
                        String attributeName = elementsToAdd[0].getAttribute("name");
                        if (oldSequence.findSubObjects(name, "name", attributeName).length == 0
                              && oldSequence.getObjects("element").length == 1)
                           oldSequence.setName(oldSequence.getNameSpace() + ":choice");
                     }
                     if (oldSequence != null) {
                        if (oldSequence.getName().equals("choice")) {
                           oldSequence.addObject(elementsToAdd[0]);
                        }
                        bOk = true;
                     }
                  }
               }
            }
            // merge 'single' elements
            XmlObject[] objects = pObjectToAdd.getObjects("");
            for (int i = 0; i < objects.length; i++) {
               if (!bOk
                     || !(objects[i].getName().equals("sequence") || objects[i].getName().equals(
                           "choice"))) {
                  mergeObject(objects[i], firstObject, pbSecondTry, piLevel + 1);
               }
            }
         }
         if (firstObject.getName().equals("attribute")
               && firstObject.getAttribute("type").contains(":string"))
            try {
               // remove the simple types
               XmlObject restriction = firstObject.getObject("simpleType").getObject("restriction");
               if (restriction.getAttribute("base").equals(firstObject.getAttribute("type"))) {
                  firstObject.setAttribute("type", null);
                  XmlObject[] deleteObjects = restriction.deleteObjects("");
                  // delete all objects except enumerations
                  for (int i = 0; i < deleteObjects.length; i++) {
                     if (deleteObjects[i].getName().equals("enumeration"))
                        restriction.addObject(deleteObjects[i]);
                  }
               }
            }
            catch (Exception ex) {

            }
      }
      // if mainObject contains any
      if (pMainObject.getObject("all") != null) {
         // 'all' must appear before any attribute
         XmlObject[] deleteObjects = pMainObject.deleteObjects("");
         for (int i = 0; i < deleteObjects.length; i++) {
            if (deleteObjects[i].getName().equals("all")) {
               pMainObject.addObject(deleteObjects[i]);
               deleteObjects[i] = null;
            }
         }
         for (int i = 0; i < deleteObjects.length; i++) {
            if (deleteObjects[i] != null) {
               pMainObject.addObject(deleteObjects[i]);
            }
         }
      }

   }

   /** 
    * private method to set the minOccurs parameters 
    * 
    * @param pXmlObject xml object to set the minOccurs value 
    * 
    * @author brod 
    */
   private void setMinOccurs(XmlObject pXmlObject)
   {
      if (pXmlObject.getName().equals("attribute")) {
         pXmlObject.setAttribute("use", "optional");
      } else if (pXmlObject.getName().equals("element")) {
         pXmlObject.setAttribute("minOccurs", "0");
      }
      XmlObject[] objects = pXmlObject.getObjects("");
      for (int i = 0; i < objects.length; i++) {
         setMinOccurs(objects[i]);
      }
   }

   /** 
    * private method to import a File 
    * 
    * @param pFileToImport FileToImport 
    * @param pLogStream LogStream 
    * 
    * @author brod 
    */
   private void importFile(File pFileToImport, PrintStream pLogStream)
   {
      try {

         String sDirectoryName = pFileToImport.getName().toLowerCase();
         sDirectoryName =
            sDirectoryName.substring(0, sDirectoryName.lastIndexOf(".")).replaceAll("\\.", "/");
         String sMethodName;
         if (sDirectoryName.endsWith("req") || sDirectoryName.endsWith("res")) {
            sMethodName = sDirectoryName.substring(0, sDirectoryName.length() - 3);
            sDirectoryName =
               sMethodName + "/data" + "." + sDirectoryName.substring(sDirectoryName.length() - 3)
                     + ".xsd";
            sMethodName = pFileToImport.getName().substring(0, sMethodName.length());
         } else {
            sMethodName = pFileToImport.getName().substring(0, sDirectoryName.length());
            sDirectoryName = sDirectoryName + "/data.xsd";
         }
         File parentFile = pFileToImport.getParentFile();
         File dataXsd = new File(parentFile, sDirectoryName);
         XmlObject schema = new XmlObject(pFileToImport).getObject("schema");
         String sPath = "[" + pFileToImport.getName() + "]";
         correctxsdFile(schema, sPath, pLogStream, sMethodName);
         String sType = dataXsd.getParentFile().getName().toUpperCase();
         Dtd2Xsd dtdXsd = _importDtds.get(sType);
         if (dtdXsd != null) {
            Utils.writeFile(new File(dataXsd.getParentFile(), "data_dtd.dtd"), dtdXsd.getDtd());
            XmlObject xsd = dtdXsd.getXsd();
            Utils.writeFile(new File(dataXsd.getParentFile(), "data_dtd.xsd"), xsd.toString());
            XmlUtil.mergeSchemas(schema, xsd, "message", sPath, pLogStream);
         }
         Utils.writeFile(dataXsd, schema.toString());
      }
      catch (Exception e) {
         e.printStackTrace();
      }

   }

   /** 
    * private method to correct the (eruopcar) xsdFile.
    * Normally there should be nothing to correct  
    * 
    * @param pXmlObject XmlObject (which has to be corrected)
    * @param psPath Name of the path (so far)
    * @param pLogStream log Stream
    * @param psType Type 
    * 
    * @author brod 
    */
   private void correctxsdFile(XmlObject pXmlObject, String psPath, PrintStream pLogStream,
                               String psType)
   {

      String sFixed = pXmlObject.getAttribute("fixed");

      if (sFixed.equalsIgnoreCase("yes")) {
         pXmlObject.setAttribute("fixed", "true");
         pLogStream.println("corrected " + psPath + ".fixed=\"yes\" to \"true\"");
      } else if (sFixed.equalsIgnoreCase("no")) {
         pXmlObject.setAttribute("fixed", "false");
         pLogStream.println("corrected " + psPath + ".fixed=\"no\" to \"false\"");
      }

      String sName = pXmlObject.getAttribute("name");
      String sElementName = pXmlObject.getName();

      // the restrictions are not valid
      if (sElementName.equals("restriction")) {
         // remove all restrictions (except enumerations)
         XmlObject[] enumerations = pXmlObject.deleteObjects("enumeration");
         // delete all other elements
         pXmlObject.deleteObjects("");
         // 'readd' the enumerations
         for (int i = 0; i < enumerations.length; i++) {
            pXmlObject.addObject(enumerations[i]);
         }
      }

      if (sElementName.equals("attribute") && sName.equals("errorCode")) {
         if (pXmlObject.getAttribute("use").equals("required")) {
            pXmlObject.setAttribute("use", "optional");
            pLogStream.println("corrected " + psPath
                  + "/@errorCode.use=\"required\" to \"optional\"");
         }
      }

      if (sName.equals("XRSserviceResponse")) {
         // there has to be a errorCode
         XmlObject[] findSubObjects = pXmlObject.findSubObjects("attribute", "name", "errorCode");
         if (findSubObjects.length == 0) {
            XmlObject errorCode =
               pXmlObject.createObject(pXmlObject.getNameSpace() + ":attribute", "name",
                     "errorCode", true);
            errorCode.setAttribute("use", "optional");
            errorCode.setAttribute("type", pXmlObject.getNameSpace() + ":string");
            pLogStream.println("added missing attribute " + psPath
                  + "/XRSserviceResponse/@errorCode");
         }
      }

      if (sName.equals("XRSserviceCode")) {
         // there has to be the requested element (as type)
         try {
            XmlObject restriction = pXmlObject.getObject("restriction");
            if (restriction != null) {
               if (restriction.findSubObject(pXmlObject.getNameSpace() + ":enumeration", "value",
                     psType) == null) {
                  restriction.createObject(pXmlObject.getNameSpace() + ":enumeration", "value",
                        psType, true);
                  pLogStream.println("added missing enumeration " + psPath
                        + "/XRSserviceCode/restriction/enumeration/@value=\"" + psType + "\"");

               }
            }
         }
         catch (Exception ex) {

         }
      }

      if (sName.equals("reservationRate")) {
         try {
            XmlObject complexType = pXmlObject.getObject("complexType");
            if (complexType != null) {
               String[] includeTypes = { "includedKm", "includedKmScr", "includedKmType" };
               for (int i = 0; i < includeTypes.length; i++) {
                  if (complexType.findSubObject("attribute", "name", includeTypes[i]) == null) {
                     XmlObject xmlObject =
                        complexType.createObject(pXmlObject.getNameSpace() + ":attribute", "name",
                              includeTypes[i], true);
                     xmlObject.setAttribute("use", "optional");
                     xmlObject.setAttribute("type", pXmlObject.getNameSpace() + ":string");
                     pLogStream.println("added missing attribute " + psPath + "/reservationRate/@"
                           + includeTypes[i]);
                  }
               }
            }
         }
         catch (Exception ex) {

         }

      }

      if (sName.equals("XRSreservation")) {
         // there has to be a errorCode
         XmlObject[] findSubObjects =
            pXmlObject.findSubObjects("attribute", "name", "specialInstructions");
         if (findSubObjects.length == 0) {
            XmlObject errorCode =
               pXmlObject.createObject(pXmlObject.getNameSpace() + ":attribute", "name",
                     "specialInstructions", true);
            errorCode.setAttribute("use", "optional");
            errorCode.setAttribute("type", pXmlObject.getNameSpace() + ":string");
            pLogStream.println("added missing attribute " + psPath
                  + "/XRSreservation/@specialInstructions");
         }
      }

      if (sName.equals("cardIssuer"))
         try {
            XmlObject restriction = pXmlObject.getObject("simpleType").getObject("restriction");
            XmlObject[] findSubObjects = restriction.findSubObjects("enumeration", "value", "PCTP");
            if (findSubObjects.length == 0) {
               restriction.createObject(pXmlObject.getNameSpace() + ":enumeration", "value",
                     "PCTP", true);
               pLogStream.println("added enumeration " + psPath
                     + "/@cardIssuer {enumeration/@value=\"PCTP\"}");
            }
         }
         catch (Exception ex) {}

      if (sName.equals("typeCode"))
         try {
            XmlObject restriction = pXmlObject.getObject("simpleType").getObject("restriction");
            XmlObject[] findSubObjects = restriction.findSubObjects("enumeration", "value", "PC");
            if (findSubObjects.length == 0) {
               restriction.createObject(pXmlObject.getNameSpace() + ":enumeration", "value", "PC",
                     true);
               pLogStream.println("added enumeration " + psPath
                     + "/@typeCode {enumeration/@value=\"PC\"}");
            }
         }
         catch (Exception ex) {}

      if (sName.equals("callerName"))
         try {
            XmlObject pattern =
               pXmlObject.getObject("simpleType").getObject("restriction").getObject("pattern");

            if (pattern.getAttribute("value").equals("[A-Z\\s]*")) {
               pLogStream.println("changed " + psPath + "/@callerName {pattern=\""
                     + pattern.getAttribute("value") + "\" to \"[A-Za-z\\s,-]*\"}");
               pattern.setAttribute("value", "[A-Za-z\\s,-]*");
            }
         }
         catch (Exception ex) {}

      if (sElementName.equals("pattern"))
         try {
            if (pXmlObject.getAttribute("value").equals("[MECISFPLX][BCDWVSTFX][AM][RN]")) {
               String sPatternNew =
                  "[MNEHCDIJSRFGPULWOX][BCDWVLSTFJXPQZEMRHYNGK][MNCABD][RNDQHIECLSABMFVZUX]";
               pLogStream.println("changed " + psPath + " {pattern=\""
                     + pXmlObject.getAttribute("value") + "\" to \"" + sPatternNew + "\"}");
               pXmlObject.setAttribute("value", sPatternNew);
            }
         }
         catch (Exception ex) {}

      // enhance the name
      StringTokenizer st = new StringTokenizer(psPath + "/" + sName, "/");
      sName = "";
      while (st.hasMoreTokens()) {
         sName += "/" + st.nextToken();
      }
      sName = sName.substring(1);
      XmlObject[] objects = pXmlObject.getObjects("");
      for (int i = 0; i < objects.length; i++) {
         correctxsdFile(objects[i], sName, pLogStream, psType);
      }

      if (sName.endsWith("List"))
         try {
            // there has to be one element
            XmlObject complexType = pXmlObject.getObject("complexType");
            XmlObject sequence = complexType.getObject("sequence");
            XmlObject[] elements = sequence.getObjects("element");
            if (elements.length == 1) {
               // element or sequence has maxOccurs
               if (sequence.getAttribute("maxOccurs").length() > 0
                     || elements[0].getAttribute("maxOccurs").length() > 0) {
                  // eveything is OK
               } else {
                  elements[0].setAttribute("maxOccurs", "unbounded");
                  pLogStream.println("corrected " + psPath + "/@" + sName
                        + ".maxOccurs=\"unbounded\" (List without maxOccurs)");
               }
            }
         }
         catch (Exception ex) {}

      // common (normal) corrections
      if (sElementName.equals("sequence") && pXmlObject.getAttribute("minOccurs").equals("0")) {
         // set the minOccurs also to the sub'elements
         XmlObject[] elements = pXmlObject.getObjects("element");
         for (int i = 0; i < elements.length; i++) {
            elements[i].setAttribute("minOccurs", "0");
         }
      }

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

   /** 
    * This method imports the Dtds (which are ALL! stored within 
    * the file "dtds.txt") 
    * 
    * @return imported dtd objects 
    * 
    * @author brod 
    */
   private Hashtable<String, Dtd2Xsd> importDtds()
   {
      Hashtable<String, Dtd2Xsd> ht = new Hashtable<String, Dtd2Xsd>();

      // read
      try {
         BufferedReader reader =
            new BufferedReader(new FileReader(new File(_sBaseDir, "dtds.txt")));
         String sLine;
         String sLines = "";
         boolean bOk = true;
         while ((sLine = reader.readLine()) != null) {
            if (sLine.startsWith("<!-- DTD ")) {
               bOk = true;
               sLines = "";
            } else if (sLine.startsWith("<!-- E")) {
               bOk = false;
               StringTokenizer st = new StringTokenizer(sLine, "><!- ");
               String last = "";
               while (st.hasMoreTokens()) {
                  last = st.nextToken();
               }
               if (last.contains("."))
                  last = last.substring(last.lastIndexOf(".") + 1);

               Dtd2Xsd dtd2Xsd = new Dtd2Xsd(new ByteArrayInputStream(sLines.getBytes()));

               ht.put(last.toUpperCase(), dtd2Xsd);

            } else if (bOk) {
               sLines += sLine + "\n";
            }
         }
         reader.close();
      }
      catch (Exception e) {
         // should never happen
         e.printStackTrace();
      }
      return ht;
   }
}
