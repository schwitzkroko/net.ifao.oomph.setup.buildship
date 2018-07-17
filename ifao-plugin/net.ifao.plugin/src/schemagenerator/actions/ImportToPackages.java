package schemagenerator.actions;


import java.io.*;
import java.util.*;

import net.ifao.xml.XmlObject;


/** 
 * This class imports complete directory 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author brod 
 */
public class ImportToPackages
{

   /** 
    * public method to import a complete Directory into packages 
    * 
    * @param psDirectory Directory
    * @param pbIgnoreSimpleTypes 
    * 
    * @author brod 
    */
   public static void importDirectory(String psDirectory, boolean pbIgnoreSimpleTypes, boolean pbDateTimeHandler)
   {
      Hashtable<String, String> htNSIds = new Hashtable<String, String>();
      File f = new File(psDirectory);
      File[] listFiles = f.listFiles();
      // create dataXsds
      for (int i = 0; i < listFiles.length; i++) {
         createDataXsd(listFiles[i], htNSIds, pbIgnoreSimpleTypes, pbDateTimeHandler);
      }
   }

   /** 
    * method createDataXsd 
    * 
    * @param pFile file
    * @param phtNSIds hashtable of NSIds
    * @param pbIgnoreSimpleTypes 
    * 
    * @author brod 
    */
   private static void createDataXsd(File pFile, Hashtable<String, String> phtNSIds,
                                     boolean pbIgnoreSimpleTypes, boolean pbDateTimeHandler)
   {
      String sName = pFile.getName();
      if (sName.endsWith(".xsd") && !sName.startsWith("data")) {
         try {
            XmlObject schema = new XmlObject(pFile).getFirstObject();
            if (schema.getName().equals("schema")) {
               // create a subdirectory
               sName = getName(sName);
               // get the parent path
               StringTokenizer st = new StringTokenizer(sName, "/");
               String sPath = "";
               while (st.hasMoreTokens()) {
                  st.nextToken();
                  if (st.hasMoreTokens())
                     sPath += "../";
               }
               schema.setAttribute("xmlns", sName);
               schema.setAttribute("targetNamespace", sName);

               Hashtable<String, String> htTypes = new Hashtable<String, String>();
               // add the local types
               addTypes(schema, htTypes, "");

               XmlObject[] allObjects = schema.deleteObjects("");

               // addImports
               addImports(pFile, schema, htTypes, allObjects, phtNSIds, sPath);

               // get all includes
               for (int i = 0; i < allObjects.length; i++) {
                  if (allObjects[i].getName().equals("include")) {
                     // ignore this entry
                  } else {
                     schema.addObject(allObjects[i]);
                  }
               }
               correctTypes(schema, htTypes);

               correctChoiceSequence(schema, schema);

               correctPatterns(schema);

               // correct the includes
               File newDataXsd = new File(pFile.getParentFile(), sName);
               Utils.writeFile(newDataXsd, schema.toString());

               schemagenerator.actions.ImportXml.startDataBinding(newDataXsd.getParentFile()
                     .getAbsolutePath(), pbIgnoreSimpleTypes, true, "", true, pbDateTimeHandler);

            }
         }
         catch (IOException e) {}

      }
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
    * private method to add the Imports 
    * 
    * @param pFile (import) file
    * @param pXmlSchema root schema
    * @param phtTypes hashtable of Types
    * @param parrAllObjects array of allObjects
    * @param phtNSIds table N(ame)S(pace) Ids
    * @param psPath Path
    * @throws FileNotFoundException
    * 
    * @author brod 
    */
   private static void addImports(File pFile, XmlObject pXmlSchema,
                                  Hashtable<String, String> phtTypes, XmlObject[] parrAllObjects,
                                  Hashtable<String, String> phtNSIds, String psPath)
      throws FileNotFoundException
   {
      for (int i = 0; i < parrAllObjects.length; i++) {
         if (parrAllObjects[i].getName().equals("include")) {
            String sSchemaLocation = parrAllObjects[i].getAttribute("schemaLocation");
            if (sSchemaLocation.length() > 0) {
               File schemaLocationFile = new File(pFile.getParentFile(), sSchemaLocation);
               XmlObject importXml = new XmlObject(schemaLocationFile).getFirstObject();
               String name = getName(sSchemaLocation);
               XmlObject[] xmlImports = pXmlSchema.findSubObjects("import", "namespace", name);
               if (xmlImports.length == 0) {
                  String sNsId = phtNSIds.get(name);
                  if (sNsId == null) {
                     sNsId = "ns" + (phtNSIds.keySet().size() + 1);
                     phtNSIds.put(name, sNsId);
                  }
                  addTypes(importXml, phtTypes, sNsId);
                  XmlObject xmlImport =
                     pXmlSchema.createObject("xs:import", "namespace", name, true);
                  xmlImport.setAttribute("id", sNsId);
                  xmlImport.setAttribute("namespace", name);
                  xmlImport.setAttribute("schemaLocation", psPath + name);
                  pXmlSchema.setAttribute("xmlns:" + sNsId, name);
                  addImports(schemaLocationFile, pXmlSchema, phtTypes, importXml
                        .getObjects("include"), phtNSIds, psPath);
               }
            }
         }
      }
   }

   /** 
    * private method correctTypes 
    * 
    * @param pXmlSchema schema files
    * @param phtTypes Hashtable of tTypes
    * 
    * @author brod 
    */
   private static void correctTypes(XmlObject pXmlSchema, Hashtable<String, String> phtTypes)
   {
      String sSchemaName = pXmlSchema.getName();
      if (sSchemaName.equals("attribute")) {
         correctTypes("type", "_TYPE", pXmlSchema, phtTypes);
      } else if (sSchemaName.equals("extension") || sSchemaName.equals("restriction")) {
         correctTypes("base", "_TYPE", pXmlSchema, phtTypes);
      } else if (sSchemaName.equals("union")) {
         correctTypes("memberTypes", "_TYPE", pXmlSchema, phtTypes);
      } else if (sSchemaName.equals("attributeGroup")) {
         correctTypes("ref", "_AG", pXmlSchema, phtTypes);
      } else if (sSchemaName.equals("element")) {
         correctTypes("ref", "_EL", pXmlSchema, phtTypes);
         correctTypes("type", "_TYPE", pXmlSchema, phtTypes);
      }

      XmlObject[] subObjects = pXmlSchema.getObjects("");
      for (int i = 0; i < subObjects.length; i++) {
         correctTypes(subObjects[i], phtTypes);
      }
   }

   /** 
    * private method to add Types 
    * 
    * @param pImportXml xml object
    * @param phtTypes Hashtable of types
    * @param psNsId name space
    * 
    * @author brod 
    */
   private static void addTypes(XmlObject pImportXml, Hashtable<String, String> phtTypes,
                                String psNsId)
   {
      // get the simpleTypes
      addTypes(pImportXml, phtTypes, psNsId, "simpleType", "name", "_TYPE");
      addTypes(pImportXml, phtTypes, psNsId, "element", "name", "_EL");
      addTypes(pImportXml, phtTypes, psNsId, "complexType", "name", "_TYPE");
      addTypes(pImportXml, phtTypes, psNsId, "attributeGroup", "name", "_AG");
   }

   /** 
    * private method correctTypes 
    * 
    * @param psArrTypes String of arr types
    * @param psSfx String of suffix
    * @param pXmlSchema xml schema object
    * @param phtTypes tashtable of Types
    * 
    * @author brod 
    */
   private static void correctTypes(String psArrTypes, String psSfx, XmlObject pXmlSchema,
                                    Hashtable<String, String> phtTypes)
   {
      String sTypes = pXmlSchema.getAttribute(psArrTypes);
      if (sTypes.length() > 0 && !sTypes.contains(":")) {
         String sNewType = "";
         StringTokenizer st = new StringTokenizer(sTypes, " ");
         while (st.hasMoreTokens()) {
            if (sNewType.length() > 0)
               sNewType += " ";
            String sType = st.nextToken();
            String sNameSpace = phtTypes.get(sType + psSfx);
            if (sNameSpace != null && sNameSpace.length() > 0) {
               sNewType += sNameSpace + ":" + sType;
            } else {
               sNewType += sType;
            }
         }
         pXmlSchema.setAttribute(psArrTypes, sNewType);
      }

   }

   /** 
    * private method to add Types 
    * 
    * @param pImportXml import XmlObject
    * @param phtTypes Hashtable of Types
    * @param psNsId namespace
    * @param psObjectNames Object Names
    * @param psAttributeNames Attribute Names
    * @param psNameToAdd Name To Add
    * 
    * @author brod 
    */
   private static void addTypes(XmlObject pImportXml, Hashtable<String, String> phtTypes,
                                String psNsId, String psObjectNames, String psAttributeNames,
                                String psNameToAdd)
   {
      XmlObject[] xmlTypes = pImportXml.getObjects(psObjectNames);
      for (int i = 0; i < xmlTypes.length; i++) {
         String sName = xmlTypes[i].getAttribute(psAttributeNames);
         if (sName.length() == 0)
            continue;
         sName += psNameToAdd;
         String sTypeName = phtTypes.get(sName);
         if (sTypeName == null) {
            phtTypes.put(sName, psNsId);
         }
      }

   }

   /** 
    * private method returns the Name 
    * 
    * @param psName 'original'name
    * @return the name of the data.xsd file
    * 
    * @author brod 
    */
   private static String getName(String psName)
   {
      return psName.substring(0, psName.lastIndexOf(".")).replaceAll("\\_", "/").toLowerCase()
            + "/data.xsd";
   }

   public static void correctSchema(XmlObject schema)
   {
      Hashtable<String, String> htTypes = new Hashtable<String, String>();
      addTypes(schema, htTypes, "");
      correctTypes(schema, htTypes);
      correctChoiceSequence(schema, schema);
      correctPatterns(schema);
   }

   /**
    * Consolidates all patterns of a restriction to one single pattern. (see DT 21265)
    * Schemas might contain multiple restriction base="string"/pattern elements for one attribute. 
    * This can not be handled by Castor 0.9. So these patterns are concatenated using delimiter "|". 
    * This method is called first with the schema object as parameter, and then it is called 
    * recursivly with its sub elements.
    *
    * @param pXmlObject schema element to check.
    *
    * @author kaufmann
    */
   private static void correctPatterns(XmlObject pXmlObject)
   {
      XmlObject[] restrictions = pXmlObject.getObjects("restriction");
      if (restrictions.length != 0) {
         for (XmlObject restriction : restrictions) {
            if (restriction.getAttribute("base").toLowerCase().contains("string")) {
               XmlObject[] patterns = restriction.getObjects("pattern");
               if (patterns.length > 1) {
                  StringBuilder sbPattern = new StringBuilder();
                  for (int i = 0; i < patterns.length; i++) {
                     if (i > 0) {
                        sbPattern.append("|");
                     }
                     sbPattern.append(patterns[i].getAttribute("value"));
                  }
                  XmlObject newPattern = patterns[0].copy();
                  newPattern.setAttribute("value", sbPattern.toString());
                  restriction.deleteObjects("pattern");
                  restriction.addObject(newPattern);
               }
            }
         }
      } else {
         XmlObject[] subObjects = pXmlObject.getObjects("");
         for (XmlObject subObject : subObjects) {
            correctPatterns(subObject);
         }
      }
   }

}
