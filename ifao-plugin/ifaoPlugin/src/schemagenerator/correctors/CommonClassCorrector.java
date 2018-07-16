package schemagenerator.correctors;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import ifaoplugin.Util;
import net.ifao.xml.XmlObject;
import schemagenerator.actions.Utils;


public class CommonClassCorrector
{

   class SchemaFiles
   {
      public SchemaFiles(File file2, XmlObject firstObject)
      {
         file = file2;
         _schemaXsd = firstObject;
         changed = true;
      }

      public SchemaFiles(File file2, String psNameSpace)
      {
         file = file2;
         _schemaXsd = new XmlObject("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"" + psNameSpace
               + "\" targetNamespace=\"" + psNameSpace + "\" elementFormDefault=\"qualified\" />").getFirstObject();
         changed = true;
      }

      File file;
      XmlObject _schemaXsd;
      private boolean changed;

      public void write()
      {
         try {
            Util.writeToFile(file, _schemaXsd.toString().getBytes());
         }
         catch (IOException e) {
            // could not write
         }
         changed = false;
      }

      public String addTargetNameSpace(String psTargetNameSpace, String psRelativePath)
      {
         // check if ns exists
         int iCounter = 1;
         while (_schemaXsd.getAttribute("xmlns:ns" + iCounter).length() > 0
               && !_schemaXsd.getAttribute("xmlns:ns" + iCounter).equals(psTargetNameSpace)) {
            iCounter++;
         }
         _schemaXsd.setAttribute("xmlns:ns" + iCounter, psTargetNameSpace);
         XmlObject xmlImport = _schemaXsd.createObject("xs:import", "namespace", psTargetNameSpace, true);
         xmlImport.setAttribute("schemaLocation", psRelativePath);
         _schemaXsd.deleteObjects(xmlImport);
         _schemaXsd.addObject(0, xmlImport);
         changed = true;
         return "ns" + iCounter;
      }

      public void replaceTypes(String psName, String psNewName)
      {
         replaceTypes(_schemaXsd, psName, psNewName);
      }

      private void replaceTypes(XmlObject pXmlObject, String psName, String psNewName)
      {
         if (pXmlObject.getAttribute("type").equals(psName)) {
            pXmlObject.setAttribute("type", psNewName);
            changed = true;
         } else {
            for (XmlObject subObject : pXmlObject.getObjects("")) {
               replaceTypes(subObject, psName, psNewName);
            }
         }
      }

      public String getRelativePath(SchemaFiles pOtherSchema)
      {
         return Utils.getRelativePath(pOtherSchema.file, file);
      }

      public boolean isChanged()
      {
         return changed;
      }

      public void setChanged(boolean changed)
      {
         this.changed = changed;
      }

      public boolean checkPath(String sPath)
      {
         try {
            return file.getCanonicalPath().contains(sPath);
         }
         catch (IOException e) {
            return file.getAbsolutePath().contains(sPath);
         }
      }

      public XmlObject findObjectWithContent(XmlObject newXmlObject)
      {
         XmlObject xml1 = withoutAnnotation(newXmlObject.copy());
         for (XmlObject subElement : _schemaXsd.getObjects("")) {
            XmlObject xml2 = withoutAnnotation(subElement.copy());
            removeTypeReferences(xml2, null, null);
            xml1.setAttribute("name", "");
            xml2.setAttribute("name", "");
            if (xml1.toString().equals(xml2.toString())) {
               return subElement;
            }
         }
         return null;
      }

      public String getParent()
      {
         return file.getParentFile().getName();
      }

      public boolean containsName(String sReplaceName)
      {
         for (XmlObject xmlObject : _schemaXsd.getObjects("")) {
            if (xmlObject.getAttribute("name").equals(sReplaceName)) {
               return true;
            }
         }
         return false;
      }

      public String getXmlns(String sNs)
      {
         return _schemaXsd.getAttribute("xmlns:" + sNs);
      }

      public String createNs(String sXmlns, File pSchemaFile)
      {
         if (_schemaXsd.getAttribute("targetNamespace").equals(sXmlns)) {
            return "";
         }
         int iCount = 0;
         while (true) {
            iCount++;
            String attribute = _schemaXsd.getAttribute("xmlns:ns" + iCount);
            if (attribute.length() == 0) {
               _schemaXsd.setAttribute("xmlns:ns" + iCount, sXmlns);
               XmlObject createObject = _schemaXsd.findSubObject("import", "namespace", sXmlns);
               if (createObject == null) {
                  createObject = _schemaXsd.createObject(_schemaXsd.getNameSpace() + ":import", "namespace", sXmlns, true);
                  createObject.setAttribute("schemaLocation", Utils.getRelativePath(file, pSchemaFile));
                  _schemaXsd.deleteObjects(createObject);
                  _schemaXsd.addObject(0, createObject);
                  file.getParentFile();
               }
               break;
            } else if (attribute.equals(sXmlns)) {
               break;
            }
         }
         return "ns" + iCount;
      }

      public File getSchemalocation(String sXmlns)
      {
         XmlObject findSubObject = _schemaXsd.findSubObject("import", "namespace", sXmlns);
         if (findSubObject != null) {
            File f = new File(file.getParentFile(), findSubObject.getAttribute("schemaLocation"));
            try {
               return f.getCanonicalFile();
            }
            catch (IOException e) {
               // should be possible
            }
            return f;
         }

         return null;
      }
   }

   private PrintStream _out;

   public CommonClassCorrector(PrintStream pOutputStream)
   {
      _out = pOutputStream;
   }

   public void changeElementsToComplexTypes(XmlObject pXmlObject)
   {
      ElementsToComplexTypes.start(_out, pXmlObject);
   }

   class CommonType
   {
      private boolean different;
      private XmlObject copy;
      private String withoutAnnotation;
      private List<SchemaFiles> lstFiles = new ArrayList<>();
      private SchemaFiles _schema;

      public CommonType(SchemaFiles pSchema, XmlObject copy, String withoutAnnotation, SchemaFiles file)
      {
         _schema = pSchema;
         this.copy = copy;
         this.withoutAnnotation = withoutAnnotation;
         if (file != null) {
            lstFiles.add(file);
         }
      }

      public boolean matches(String psWithoutAnnotation)
      {
         return psWithoutAnnotation.equals(this.withoutAnnotation);
      }

      public boolean isDifferent()
      {
         return different;
      }

      public void setDifferent(boolean different)
      {
         this.different = different;
      }


      public XmlObject getXmlObject()
      {
         return copy;
      }

      public void increaseCounter(SchemaFiles schemaFiles)
      {
         if (schemaFiles != null) {
            lstFiles.add(schemaFiles);
         }
      }


      public void moveTo(SchemaFiles schemaFiles, String psParentName)
      {
         XmlObject newXmlObject = copy.copy();
         String sTargetNamespace = schemaFiles._schemaXsd.getAttribute("targetNamespace");
         String sName = newXmlObject.getAttribute("name");
         String sReplaceName = Util.camelCase(sName);

         XmlObject alreadyExisting = newXmlObject.copy();
         removeTypeReferences(alreadyExisting, null, null);
         alreadyExisting = schemaFiles.findObjectWithContent(alreadyExisting);
         if (alreadyExisting != null) {
            String sReplaceName2 = Util.camelCase(alreadyExisting.getAttribute("name"));
            println("- Move use name " + sReplaceName2 + " instead of " + sReplaceName);
            sReplaceName = sReplaceName2;
         } else {
            if (schemaFiles.containsName(sReplaceName)) {
               sReplaceName += psParentName;
               char cType = 'A';
               while (schemaFiles.containsName(sReplaceName)) {
                  sReplaceName = Util.camelCase(sName) + psParentName + cType;
                  cType++;
               }
            }
            schemaFiles._schemaXsd.addObject(newXmlObject);
         }
         String sFiles = new String();
         for (SchemaFiles file : lstFiles) {
            XmlObject subObject = file._schemaXsd.findSubObject(newXmlObject.getName(), "name", sName);
            if (subObject != null) {
               file._schemaXsd.deleteObjects(subObject);
               String sNs = file.addTargetNameSpace(sTargetNamespace, schemaFiles.getRelativePath(file));
               file.replaceTypes(sName, sNs + ":" + sReplaceName);
               if (sFiles.length() > 0) {
                  sFiles += ",";
               }
               sFiles += file.getParent();
            }
         }
         newXmlObject.setAttribute("name", sReplaceName);
         removeTypeReferences(newXmlObject, _schema, schemaFiles);
         println("- Move (" + sFiles + ") " + sName + " to (" + sTargetNamespace + ") " + sReplaceName);
      }

   }

   public void checkForCommonTypes(File rootDir, String psNameSpace)
   {
      if (!rootDir.isDirectory()) {
         return;
      }
      List<SchemaFiles> lstSchemaFiles = new ArrayList<>();
      addSchemaFiles(rootDir, lstSchemaFiles, 2);
      SchemaFiles schemaFiles = new SchemaFiles(new File(rootDir, "schema.xsd"), psNameSpace);

      // get all common simpleTypes
      List<CommonType> lstSimpleTypes = getSimpleTypes(lstSchemaFiles);

      // add simple types for common schema
      for (CommonType commonType : lstSimpleTypes) {
         commonType.moveTo(schemaFiles, "");
      }

      // get the group Files
      for (File subDirectory : rootDir.listFiles()) {
         File schemaGroupFile = new File(subDirectory, "schema.group");
         if (schemaGroupFile.exists()) {
            // create a schema file
            String sNameSpace = psNameSpace;
            if (!sNameSpace.endsWith("/")) {
               sNameSpace += "/";
            }
            SchemaFiles groupSchemaFiles =
               new SchemaFiles(new File(subDirectory, "schema.xsd"), sNameSpace + subDirectory.getName());
            String loadFromFile = Util.loadFromFile(schemaGroupFile);
            StringTokenizer st = new StringTokenizer(loadFromFile, "\r\n");
            while (st.hasMoreTokens()) {
               String sLine = st.nextToken();
               if (sLine.contains("=")) {
                  String sName = sLine.substring(0, sLine.indexOf("="));
                  StringTokenizer stItems = new StringTokenizer(sLine.substring(sLine.indexOf("=") + 1), " ,;");
                  List<SchemaFiles> lstSubSchemas = new ArrayList<>();
                  while (stItems.hasMoreTokens()) {
                     String sPath = subDirectory.getName() + File.separator + stItems.nextToken() + File.separator;
                     for (SchemaFiles schemas : lstSchemaFiles) {
                        if (schemas.checkPath(sPath)) {
                           lstSubSchemas.add(schemas);
                        }
                     }
                  }
                  if (lstSubSchemas.size() > 0) {
                     // get all common simpleTypes

                     while (true) {
                        List<CommonType> lstComplexTypes = getComplexTypes(lstSubSchemas);

                        // add simple types for common schema
                        if (lstComplexTypes.size() == 0) {
                           break;
                        } else {
                           for (CommonType commonType : lstComplexTypes) {
                              commonType.moveTo(groupSchemaFiles, sName);
                           }
                        }
                     }
                  }
               }
            }
            groupSchemaFiles.write();
         }
      }
      for (SchemaFiles schemaFile : lstSchemaFiles) {
         if (schemaFile.isChanged()) {
            schemaFile.write();
         }
      }

      schemaFiles.write();

   }

   public void println(String string)
   {
      _out.println(string);
   }

   private List<CommonType> getComplexTypes(List<SchemaFiles> lstSubSchemas)
   {

      // get only duplicate CommonTypes
      List<CommonType> lstCommonTypes = new ArrayList<>();
      for (SchemaFiles schemaFiles : lstSubSchemas) {
         for (XmlObject complexType : schemaFiles._schemaXsd.getObjects("complexType")) {
            if (!containsInnerReferences(complexType)) {
               lstCommonTypes.add(
                     new CommonType(schemaFiles, complexType, withoutAnnotation(complexType.copy()).toString(), schemaFiles));
            }
         }
      }
      return lstCommonTypes;
   }

   private boolean containsInnerReferences(XmlObject complexType)
   {
      String sType = complexType.getAttribute("type");
      if (sType.length() > 0) {
         if (!sType.contains(":")) {
            return true;
         }
      } else {
         for (XmlObject subElement : complexType.getObjects("")) {
            if (containsInnerReferences(subElement)) {
               return true;
            }
         }
      }
      return false;
   }

   private List<CommonType> getSimpleTypes(List<SchemaFiles> lstSchemaFiles)
   {
      Hashtable<String, CommonType> htNames = new Hashtable<>();
      for (SchemaFiles xmlObject : lstSchemaFiles) {
         for (XmlObject simpleType : xmlObject._schemaXsd.getObjects("simpleType")) {
            String sName = simpleType.getAttribute("name");
            if (sName.length() > 0) {
               String withoutAnnotation = withoutAnnotation(simpleType.copy()).toString();
               CommonType commonType = htNames.get(sName);
               if (commonType == null) {
                  htNames.put(sName, new CommonType(xmlObject, simpleType, withoutAnnotation, xmlObject));
               } else {
                  commonType.increaseCounter(xmlObject);
                  if (!commonType.matches(withoutAnnotation)) {
                     // there are changes for this type
                     commonType.setDifferent(true);
                  }
               }
            }
         }
      }
      // get only duplicate CommonTypes
      List<CommonType> lstCommonTypes = new ArrayList<>();
      for (CommonType commonType : htNames.values()) {
         if (!commonType.isDifferent()) {
            lstCommonTypes.add(commonType);
         }
      }
      return lstCommonTypes;
   }

   static XmlObject withoutAnnotation(XmlObject copy)
   {
      copy.deleteObjects("annotation");
      for (XmlObject subObject : copy.getObjects("")) {
         withoutAnnotation(subObject);
      }
      return copy;
   }

   private void addSchemaFiles(File pDirectory, List<SchemaFiles> lstSchemaFiles, int piCount)
   {
      for (File file : pDirectory.listFiles()) {
         if (file.isDirectory() && piCount > 0) {
            addSchemaFiles(file, lstSchemaFiles, piCount - 1);
         } else if (file.getName().equals("schema.xsd")) {
            try {
               SchemaFiles schemaFiles = new SchemaFiles(file, new XmlObject(file).getFirstObject());
               lstSchemaFiles.add(schemaFiles);
            }
            catch (FileNotFoundException e) {
               // file exists
            }
         }
      }
   }


   static void removeTypeReferences(XmlObject newXmlObject, SchemaFiles pSchemaFrom, SchemaFiles pSchemaTo)
   {
      String sType = newXmlObject.getAttribute("type");
      if (sType.contains(":")) {
         String sNs = "";
         if (pSchemaFrom != null && pSchemaTo != null) {
            sNs = sType.substring(0, sType.indexOf(":"));
            String sXmlns = pSchemaFrom.getXmlns(sNs);
            if (sXmlns.length() > 0) {
               File pFile = pSchemaFrom.getSchemalocation(sXmlns);
               sNs = pSchemaTo.createNs(sXmlns, pFile);
            } else {
               sNs = "";
            }
            if (sNs.length() > 0) {
               sNs += ":";
            }
         }
         newXmlObject.setAttribute("type", sNs + sType.substring(sType.indexOf(":") + 1));
      }
      for (XmlObject subElement : newXmlObject.getObjects("")) {
         removeTypeReferences(subElement, pSchemaFrom, pSchemaTo);
      }
   }
}
