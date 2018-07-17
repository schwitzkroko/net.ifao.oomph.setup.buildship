package schemagenerator.actions;


import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import net.ifao.util.CorrectDatabindingXsd;
import net.ifao.xml.XmlObject;


/** 
 * Class ImportGalileoHelp to import galileo help files into 
 * castor generateable schemas 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author brod 
 */
public class ImportGalileo
{

   private static final String _COMPLEX = "complex";
   private static final String _TYPE = "_TYPE";
   private static final Hashtable<String, String> htAdditionalFields =
      new Hashtable<String, String>();

   static {

      // add special characters
      htAdditionalFields.put("SeatMap", "ErrorCode");
      htAdditionalFields.put("SeatSell", "ErrorCode");
      htAdditionalFields.put("PNRBFPrimaryBldChg", "ErrorCode");
      htAdditionalFields.put("PNRBFSecondaryBldChg", "ErrorCode");
      htAdditionalFields.put("SegCancel", "ErrorCode");

      htAdditionalFields.put("PassiveSegmentSellResponse", "ErrorCode,TextMsg-Txt");

      htAdditionalFields.put("TextMsg", "Txt");

   }

   /** 
    * The method start2Import is the main method for this class and requires 
    * 
    * @param psTransactionHelp The transaction help directory
    * @param psOutputDir The output directory (where e.g. the data.xsd is written)
    * 
    * @author brod 
    */
   public static void start2Import(String psTransactionHelp, String psOutputDir)
   {
      ImportGalileo importGalileo = new ImportGalileo(psTransactionHelp);
      importGalileo.generateSchemaFiles(psOutputDir);

   }

   /** 
    * This method writes a File 
    * 
    * @param psFileContent The file content (text)
    * @param pFile the file which has to be created / updated
    * 
    * @author brod 
    */
   private static void writeFile(String psFileContent, File pFile)
   {
      try {
         FileWriter fileWriter = new FileWriter(pFile);
         fileWriter.write(psFileContent);
         fileWriter.close();
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   private String _sTransactionHelp;

   /** 
    * Constructor ImportGalileo requires 
    * 
    * @param psTransactionHelp The transaction help directory
    * 
    * @author brod 
    */
   private ImportGalileo(String psTransactionHelp)
   {
      _sTransactionHelp = psTransactionHelp;
   }

   /** 
    * This method adds additional fields within the response 
    * 
    * @param pXmlHelpObject xmlHelpObject
    * 
    * @author brod 
    */
   private void addAdditionalFieldsWithinResponse(XmlObject pXmlHelpObject)
   {
      addSpecialFieldsWithinResponse(pXmlHelpObject);
      // add common objects
      pXmlHelpObject.addObject(new XmlObject("<TransactionErrorCode>"
            + "<Domain><!-- DataType: Alphanumeric --></Domain>"
            + "<Code><!-- DataType: Alphanumeric --></Code>" + "</TransactionErrorCode>"));

      pXmlHelpObject.addObject(new XmlObject("<HostApplicationError>"
            + "<ErrorCode><!-- DataType: Alphanumeric --></ErrorCode>"
            + "<Text><!-- DataType: Alphanumeric --></Text>" + "</HostApplicationError>"));

      pXmlHelpObject.addObject(new XmlObject("<ErrorFaultList>" + "<ErrorFault>"
            + "<IsValid><!-- DataType: Alphanumeric --></IsValid>"
            + "<Text><!-- DataType: Alphanumeric --></Text>"
            + "<Id><!-- DataType: Alphanumeric --></Id>" + "</ErrorFault></ErrorFaultList>"));
   }

   /** 
    * This method adds special fields within the response (for a specific help
    * object) 
    * 
    * @param pXmlHelpObject Galileo xmlHelpObject
    * 
    * @author brod 
    */
   private void addSpecialFieldsWithinResponse(XmlObject pXmlHelpObject)
   {
      XmlObject[] subObjects = pXmlHelpObject.getObjects("");
      for (XmlObject subObject : subObjects) {
         addSpecialFieldsWithinResponse(subObject);
      }

      String sMainName = pXmlHelpObject.getName();

      String sAddFields = htAdditionalFields.get(sMainName);
      if (sAddFields != null) {
         StringTokenizer stAll = new StringTokenizer(sAddFields, ",");
         while (stAll.hasMoreTokens()) {
            StringTokenizer st = new StringTokenizer(stAll.nextToken(), "-");
            String sPost = "";
            String sPre = "";
            String sType = "";
            while (st.hasMoreTokens()) {
               String nextToken = st.nextToken();
               if (st.hasMoreTokens()) {
                  sPost += "<" + nextToken + ">";
                  sPre = "</" + nextToken + ">" + sPre;
               } else {
                  sType = "<" + nextToken + "><!-- DataType: Alphanumeric --></" + nextToken + ">";
               }
            }
            XmlObject newObject = new XmlObject(sPost + sType + sPre).getFirstObject();
            // if object does not exist
            if (pXmlHelpObject.getObjects(newObject.getName()).length == 0) {
               // add the object
               pXmlHelpObject.addObject(newObject);
            }
         }
      }

   }

   /** 
    * TODO (brod) add comment for method analyseHelpObject 
    * 
    * <p> TODO rename xmlHelpObject to pHelpObject
    * @param xmlHelpObject TODO (brod) add text for param xmlHelpObject
    * 
    * @author brod 
    */
   private void analyseHelpObject(XmlObject xmlHelpObject)
   {
      // get all comments objects
      XmlObject[] subObjects = xmlHelpObject.deleteObjects("!--");
      String sComment = "";
      for (int i = 0; i < subObjects.length; i++) {
         String sSubObject = subObjects[i].toString();
         if (i > 0) {
            sComment += "\n";
         }
         sComment +=
            sSubObject.substring(sSubObject.indexOf("--") + 2, sSubObject.lastIndexOf("--")).trim();
      }

      // get all other objects
      subObjects = xmlHelpObject.deleteObjects("");

      // create a comment object
      if (sComment.length() > 0) {
         xmlHelpObject.createObject("_comment").setCData(sComment);
      }
      xmlHelpObject.setAttribute("type", getType(sComment));

      boolean bType = true;
      if (subObjects.length > 0) {
         xmlHelpObject.setAttribute("count", "" + subObjects.length);
      } else {
         bType = false;
      }
      // get the amount of SubObjects
      for (XmlObject subObject : subObjects) {
         analyseHelpObject(subObject);
         if (subObject.getAttribute("count").length() == 0) {
            bType = false;
         }
         // re'add' the subobjects
         xmlHelpObject.addObject(subObject);
      }
      if (bType) {
         xmlHelpObject.setAttribute(_COMPLEX, "true");
      }
      if (xmlHelpObject.getAttribute("count").length() == 0
            && xmlHelpObject.getAttribute("type").length() == 0) {
         xmlHelpObject.setAttribute("type", "set");
      }
   }

   /** 
    * TODO (brod) add comment for method changeComplexTypesToElements 
    * 
    * <p> TODO rename commonXsd to pXsd, dataBindingXml to pBindingXml
    * @param commonXsd TODO (brod) add text for param commonXsd
    * @param dataBindingXml TODO (brod) add text for param dataBindingXml
    * 
    * @author brod 
    */
   private void changeComplexTypesToElements(XmlObject commonXsd, XmlObject dataBindingXml)
   {
      HashSet<String> hsCorrectTypes = new HashSet<String>();
      // add all 'basic' elements as correct types
      XmlObject[] elements = commonXsd.getObjects("element");
      for (XmlObject element : elements) {
         hsCorrectTypes.add(element.getAttribute("name"));
      }
      // get all complexTypes
      XmlObject[] complexTypes = commonXsd.deleteObjects("complexType");
      for (XmlObject complexType : complexTypes) {
         String sName = complexType.getAttribute("name");
         if (sName.endsWith(_TYPE)) {
            // search if related element exists
            String sElementName = sName.substring(0, sName.length() - _TYPE.length());
            // if there are no such elements
            XmlObject[] subElements = commonXsd.findSubObjects("element", "name", sElementName);
            //            boolean bCopyElements = false;
            //            // validate if elements can be merged to the subElements
            //            if (subElements.length == 1) {
            //               XmlObject sequence = subElements[0].getObject("complexType").getObject("sequence");
            //               XmlObject[] subElementElements = sequence.getObjects("element");
            //               XmlObject[] complexTypeElements =
            //                  complexTypes[i].getObject("sequence").getObjects("element");
            //               // the elements MUST not be available
            //               bCopyElements = true;
            //               for (int j = 0; bCopyElements && j < complexTypeElements.length; j++) {
            //                  String sCTName =
            //                     complexTypeElements[j].getAttribute("ref")
            //                           + complexTypeElements[j].getAttribute("name");
            //                  for (int k = 0; bCopyElements && k < subElementElements.length; k++) {
            //                     String sSEName =
            //                        subElementElements[k].getAttribute("ref")
            //                              + subElementElements[k].getAttribute("name");
            //                     if (sSEName.equals(sCTName)) {
            //                        bCopyElements = false;
            //                     }
            //                  }
            //               }
            //            }
            //            if (bCopyElements) {
            //               XmlObject sequence = subElements[0].getObject("complexType").getObject("sequence");
            //               sequence.addObjects(complexTypes[i].getObject("sequence").getObjects("element"));
            //            } else 
            if (subElements.length == 0) {
               hsCorrectTypes.add(sName);
               // create such a element
               XmlObject newElement =
                  commonXsd.createObject("xs:element", "name", sElementName, true);
               // shift the annotation
               XmlObject[] annotation = complexType.deleteObjects("annotation");
               for (XmlObject element : annotation) {
                  newElement.addObject(element);
               }
               complexType.setAttribute("name", null);
               newElement.addObject(complexType);
            } else {
               commonXsd.addObject(complexType);
            }
         } else {
            commonXsd.addObject(complexType);
         }
      }
      correctComplextypeReferences(commonXsd, commonXsd, dataBindingXml, hsCorrectTypes, "");
   }

   /** 
    * TODO (brod) add comment for method correctComplextypeReferences 
    * 
    * <p> TODO rename commonXsd to pXsd, commonElement to pElement, dataBindingXml to pBindingXml, hsCorrectTypes to pCorrectTypes
    * @param commonXsd TODO (brod) add text for param commonXsd
    * @param commonElement TODO (brod) add text for param commonElement
    * @param dataBindingXml TODO (brod) add text for param dataBindingXml
    * @param hsCorrectTypes TODO (brod) add text for param hsCorrectTypes
    * @param psPath TODO (brod) add text for param psPath
    * 
    * @author brod 
    */
   private void correctComplextypeReferences(XmlObject commonXsd, XmlObject commonElement,
                                             XmlObject dataBindingXml,
                                             HashSet<String> hsCorrectTypes, String psPath)
   {
      String sPath = psPath;
      String sName = commonElement.getAttribute("name");
      if (sName.length() > 0) {
         sPath += "/" + sName;
      }

      String sType = commonElement.getAttribute("type");
      if (sType.length() > 0 && !sType.startsWith("xs")) {
         if (hsCorrectTypes.contains(sType)) {
            String sElementName = sType.substring(0, sType.length() - _TYPE.length());
            commonElement.setAttribute("ref", sElementName);
            commonElement.setAttribute("type", null);
            commonElement.setAttribute("name", null);
         } else {
            String sId = commonElement.getAttribute("id");
            if (sId.length() > 0) {
               String sPathName = sPath;
               // add specific binding
               XmlObject elementBinding;
               elementBinding = new XmlObject("<elementBinding />").getFirstObject();
               StringTokenizer st = new StringTokenizer(sPath, "/");
               sId = st.nextToken();
               // if there is a complex item
               if (commonXsd.findSubObjects("complexType", "name", sId).length > 0) {
                  // change the pathName
                  while (sPathName.startsWith("/")) {
                     sPathName = sPathName.substring(1);
                  }
                  sPathName = "complexType:" + sPathName;
               }
               // truncate the id (for creation of java classes)
               if (sId.contains("_")) {
                  sId = sId.substring(0, sId.indexOf("_"));
               }
               while (st.hasMoreTokens()) {
                  String nextToken = st.nextToken();
                  sId += nextToken;
               }
               elementBinding.setAttribute("name", sPathName);
               elementBinding.createObject("java-class").setAttribute("name", sId);
               dataBindingXml.addObject(elementBinding);
            }
         }
      }

      XmlObject[] subObjects = commonElement.getObjects("");
      for (XmlObject subObject : subObjects) {
         correctComplextypeReferences(commonXsd, subObject, dataBindingXml, hsCorrectTypes, sPath);
      }
   }

   /** 
    * TODO (brod) add comment for method generateSchemaFiles 
    * 
    * @param psOutputDir TODO (brod) add text for param psOutputDir
    * 
    * @author brod 
    */
   private void generateSchemaFiles(String psOutputDir)
   {
      XmlObject commonXsd =
         new XmlObject(
               "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\" />")
               .getFirstObject();

      XmlObject dataXsd =
         new XmlObject(
               "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\" />")
               .getFirstObject();
      dataXsd.createObject("xs:include", "schemaLocation", "common.xsd", true);

      XmlObject dataBindingXml =
         new XmlObject("<binding xmlns=\"http://www.castor.org/SourceGenerator/Binding\" "
               + "defaultBindingType=\"element\" />").getFirstObject();
      // first step read the HelpFiles
      File helpDirectory = new File(_sTransactionHelp);

      readHelpFiles(dataXsd, dataBindingXml, commonXsd, helpDirectory, psOutputDir);
      changeComplexTypesToElements(commonXsd, dataBindingXml);
      readSampleFiles(commonXsd, new File(helpDirectory.getParentFile(), "XML_Samples"));
      // writeFile(commonXsd.toString(), new File(psOutputDir, "common1.xsd"));

      sortSchemaFile(commonXsd);

      writeFile(commonXsd.toString(), new File(psOutputDir, "common.xsd"));
      writeFile(dataXsd.toString(), new File(psOutputDir, "data.xsd"));
      writeFile(dataBindingXml.toString(), new File(psOutputDir, "dataBinding.xml"));

      CorrectDatabindingXsd.correctDataBinding(new File(psOutputDir, "dataBinding.xml"), "");
   }

   /** 
    * TODO (brod) add comment for method getType 
    * 
    * @param psHelp TODO (brod) add text for param psHelp
    * @return TODO (brod) add text for returnValue
    * 
    * @author brod 
    */
   private String getType(String psHelp)
   {
      char[] charArray = psHelp.toCharArray();
      String sHelp = "";
      for (char element : charArray) {
         if (element > ' ') {
            sHelp += element;
         }
      }
      if (sHelp.contains("DataType:Alpha")) {
         return "xs:string";
      }
      if (sHelp.contains("DataType:Numeric")) {
         return "xs:string";
      }
      if (sHelp.contains("DataType:Char")) {
         return "xs:string";
      }
      if (sHelp.contains("DataType:Array")) {
         return "array";
      }
      if (sHelp.contains("DataType:FieldSet")) {
         return "set";
      }
      return "";
   }

   /** 
    * TODO (brod) add comment for method readHelpFiles 
    * 
    * <p> TODO rename dataXsd to pXsd, dataBinding to pBinding, commonXsd to pXsd, file to pFile
    * @param dataXsd TODO (brod) add text for param dataXsd
    * @param dataBinding TODO (brod) add text for param dataBinding
    * @param commonXsd TODO (brod) add text for param commonXsd
    * @param file TODO (brod) add text for param file
    * @param psOutputDir TODO (brod) add text for param psOutputDir
    * 
    * @author brod 
    */
   private void readHelpFiles(XmlObject dataXsd, XmlObject dataBinding, XmlObject commonXsd,
                              File file, String psOutputDir)
   {
      if (file.isDirectory()) {
         File[] listFiles = file.listFiles();
         for (File listFile : listFiles) {
            readHelpFiles(dataXsd, dataBinding, commonXsd, listFile, psOutputDir);
         }
      } else if (file.getName().endsWith(".xml")) {
         try {
            XmlObject xmlHelpObject = new XmlObject(file).getFirstObject();
            System.out.println(file.getAbsolutePath());

            if (file.getName().endsWith("_response.xml")) {
               // writeFile(xmlHelpObject.toString(), new File(psOutputDir, "response.xml"));
               addAdditionalFieldsWithinResponse(xmlHelpObject);
            } else {
               // writeFile(xmlHelpObject.toString(), new File(psOutputDir, "request.xml"));
            }

            analyseHelpObject(xmlHelpObject);

            readHelpObjects(dataXsd, dataBinding, commonXsd, xmlHelpObject, file.getName());
         }
         catch (Exception e) {}
      }
   }

   /** 
    * TODO (brod) add comment for method readHelpObjects 
    * 
    * <p> TODO rename dataXsd to pXsd, dataBinding to pBinding, commonXsd to pXsd, xmlHelpObject to pHelpObject
    * @param dataXsd TODO (brod) add text for param dataXsd
    * @param dataBinding TODO (brod) add text for param dataBinding
    * @param commonXsd TODO (brod) add text for param commonXsd
    * @param xmlHelpObject TODO (brod) add text for param xmlHelpObject
    * @param psFileName TODO (brod) add text for param psFileName
    * @return TODO (brod) add text for returnValue
    * 
    * @author brod 
    */
   private boolean readHelpObjects(XmlObject dataXsd, XmlObject dataBinding, XmlObject commonXsd,
                                   XmlObject xmlHelpObject, String psFileName)
   {
      String sMainName = xmlHelpObject.getName();

      if (sMainName.startsWith("!")) {
         return false;
      }
      String sMainType = xmlHelpObject.getAttribute("type");

      String sComplexType = "";
      String sElementName;
      if (dataXsd != null) {
         String sShortName;
         if (sMainName.contains("_")) {
            sShortName = sMainName.substring(0, sMainName.indexOf("_"));
         } else {
            sShortName = sMainName;
         }
         String sType = "";
         if (psFileName.endsWith("_request.xml")) {
            sType = "Request";
         } else if (psFileName.endsWith("_response.xml")) {
            sType = "Response";
         }
         sElementName = sShortName + sType;
         sComplexType = sElementName;

         // enhance data.xsd
         XmlObject dataXsdElement =
            dataXsd.createObject("xs:element", "name", "Galileo" + sType, true)
                  .createObject("xs:complexType").createObject("xs:choice")
                  .createObject("xs:element", "name", sMainName, true);
         dataXsdElement.setAttribute("minOccurs", "0");
         dataXsdElement.setAttribute("type", sComplexType);

         // enhance dataBinding.xml
         dataBinding.createObject("elementBinding", "name", "/Galileo" + sType + "/" + sMainName,
               true).createObject("java-class", "name", sType + sShortName, true);
      } else if (xmlHelpObject.getAttribute(_COMPLEX).equals("true")) {
         sElementName = sMainName + _TYPE;
         sComplexType = sElementName;
      } else {
         sElementName = sMainName;
      }
      XmlObject complexType;
      XmlObject annotation;
      if (sComplexType.length() > 0) {
         complexType = commonXsd.createObject("xs:complexType", "name", sComplexType, true);
         annotation = complexType.createObject("xs:annotation");
      } else {
         XmlObject element = commonXsd.createObject("xs:element", "name", sElementName, true);
         annotation = element.createObject("xs:annotation");
         complexType = element.createObject("xs:complexType");
      }

      XmlObject appinfo = new XmlObject("<xs:appinfo />").getFirstObject();
      appinfo.setAttribute("source", psFileName);
      annotation.addObject(appinfo);

      // create the sequence
      XmlObject sequence = complexType.createObject("xs:sequence");
      // remove the comment
      XmlObject[] comment = xmlHelpObject.deleteObjects("_comment");
      if (comment.length > 0) {
         annotation.createObject("xs:documentation").setCData(comment[0].getCData());
      }
      // get all other objects
      XmlObject[] subObjects = xmlHelpObject.getObjects("");
      for (XmlObject subObject : subObjects) {
         XmlObject subComment = subObject.getObject("_comment");
         String sSubName = subObject.getName();
         XmlObject element =
            sequence.createObject("xs:element", "id", sElementName + sSubName, true);
         String sCData = "";
         if (subComment != null) {
            sCData = subComment.getCData();
            if (sCData.length() > 0) {
               if (!sCData.startsWith("<")) {
                  sCData = "<pre>" + sCData.replaceAll(Pattern.quote("*/"), "* /") + "</pre>";
               }
               element.createObject("xs:annotation").createObject("xs:documentation")
                     .setCData(sCData);
            }
         }
         element.setAttribute("minOccurs", "0");
         String sType = subObject.getAttribute("type");
         element.setAttribute("maxOccurs", null);
         if (sCData.length() == 0 || sType.equals("set") || sMainType.equals("array")) {
            element.setAttribute("maxOccurs", "unbounded");
         }

         if (sType.startsWith("xs:")) {
            element.setAttribute("name", sSubName);
            element.setAttribute("type", sType);
            element.setAttribute("ref", null);
         } else {
            boolean bComplex = subObject.getAttribute(_COMPLEX).equals("true");
            if (bComplex) {
               element.setAttribute("name", sSubName);
               element.setAttribute("type", subObject.getName() + "_TYPE");
               element.setAttribute("ref", null);
            } else {
               element.setAttribute("name", null);
               element.setAttribute("type", null);
               element.setAttribute("ref", sSubName);
            }
            readHelpObjects(null, dataBinding, commonXsd, subObject, psFileName);
         }
      }

      return true;
   }

   //   private boolean readHelpObjects2(XmlObject dataXsd, XmlObject dataBinding, XmlObject commonXsd,
   //                                    XmlObject xmlHelpObject, String psFileName,
   //                                    StringBuilder sbOtherType)
   //   {
   //      String sMainName = xmlHelpObject.getName();
   //      XmlObject element;
   //      XmlObject complexType;
   //      if (dataXsd != null) {
   //         if (sMainName.indexOf("_") > 0)
   //            sMainName = sMainName.substring(0, sMainName.indexOf("_"));
   //         String sType = "";
   //         if (psFileName.endsWith("_request.xml")) {
   //            sType = "Request";
   //         } else if (psFileName.endsWith("_response.xml")) {
   //            sType = "Response";
   //            // in case of the response, enhance the response
   //            addAdditionalFieldsWithinResponse(sMainName, xmlHelpObject);
   //         }
   //
   //         // enhance data.xsd
   //         XmlObject dataXsdElement =
   //            dataXsd.createObject("xs:element", "name", "Galielo" + sType, true).createObject(
   //                  "xs:complexType").createObject("xs:choice").createObject("xs:element", "name",
   //                  xmlHelpObject.getName(), true);
   //         dataXsdElement.setAttribute("minOccurs", "0");
   //         dataXsdElement.setAttribute("type", sMainName + sType);
   //
   //         // enhance dataBinding.xml
   //         dataBinding.createObject("elementBinding", "name",
   //               "/Galielo" + sType + "/" + xmlHelpObject.getName(), true).createObject("java-class",
   //               "name", sType + sMainName, true);
   //
   //         // enhance the mainName
   //         sMainName += sType;
   //         element = commonXsd.createObject("xs:complexType", "name", sMainName, true);
   //         complexType = element;
   //      } else {
   //         element = commonXsd.createObject("xs:element", "name", sMainName, true);
   //         element.createObject("xs:annotation");
   //         complexType = element.createObject("xs:complexType");
   //      }
   //
   //
   //      XmlObject appinfo = new XmlObject("<xs:appinfo />").getFirstObject();
   //      appinfo.setAttribute("source", psFileName);
   //      element.createObject("xs:annotation").addObject(appinfo);
   //
   //      XmlObject[] objects = xmlHelpObject.getObjects("");
   //      String sMainHelp = getHelp(xmlHelpObject, element);
   //      boolean bArray = getType(sMainHelp).equalsIgnoreCase("array");
   //      boolean bSubObjects = false;
   //      for (int i = 0; i < objects.length; i++) {
   //         XmlObject object = objects[i];
   //         String name = object.getName();
   //         if (name.startsWith("!--")) {
   //            // ignore this entry
   //         } else {
   //            bSubObjects = true;
   //            appinfo.createObject("xs:attribute", "name", name, true);
   //            XmlObject sequence = complexType.createObject("xs:sequence");
   //            XmlObject subElement =
   //               sequence.createObject("xs:element", "id", sMainName + "_" + name, true);
   //            sequence.setAttribute("minOccurs", "0");
   //            subElement.setAttribute("minOccurs", "0");
   //            if (bArray)
   //               subElement.setAttribute("maxOccurs", "unbounded");
   //            String subObjectHelp = getHelp(object, subElement);
   //            String sType = getType(subObjectHelp);
   //            if (subObjectHelp.length() == 0 || sType.equals("set"))
   //               subElement.setAttribute("maxOccurs", "unbounded");
   //            // if the type if a 'normal' element
   //            if (sType.startsWith("xs:")) {
   //               if (bArray) {
   //                  XmlObject complexType2 =
   //                     commonXsd.createObject("xs:complexType", "name", sMainName + "_ARRAY", true);
   //                  XmlObject element2 =
   //                     complexType2.createObject("xs:sequence").createObject("xs:element", "name",
   //                           name, true);
   //                  element2.setAttribute("type", sType);
   //                  element2.setAttribute("minOccurs", "0");
   //                  element2.setAttribute("maxOccurs", "unbounded");
   //                  if (sbOtherType.length() == 0)
   //                     sbOtherType.append(sMainName + "_ARRAY");
   //               } else {
   //                  subElement.setAttribute("name", name);
   //                  subElement.setAttribute("type", sType);
   //                  if (subElement.getAttribute("ref").length() > 0) {
   //                     System.err.println("Error " + sMainName + "_" + name);
   //                  }
   //                  subElement.setAttribute("ref", null);
   //               }
   //            } else {
   //               if (subElement.getAttribute("name").length() > 0) {
   //                  System.err.println("Error " + sMainName + "_" + name);
   //               } else {
   //                  StringBuilder sb = new StringBuilder();
   //                  if (!readHelpObjects2(null, dataBinding, commonXsd, object, psFileName, sb)) {
   //                     // there are no subObjects
   //                     subElement.setAttribute("name", name);
   //                     subElement.setAttribute("type", "xs:string");
   //                     subElement.setAttribute("ref", null);
   //                  } else {
   //                     if (sb.length() > 0) {
   //                        subElement.setAttribute("name", name);
   //                        subElement.setAttribute("type", sb.toString());
   //                        subElement.setAttribute("ref", null);
   //                     } else {
   //                        subElement.setAttribute("name", null);
   //                        subElement.setAttribute("type", null);
   //                        subElement.setAttribute("ref", name);
   //                     }
   //                  }
   //               }
   //            }
   //         }
   //      }
   //      if (!bSubObjects && element != null) {
   //         commonXsd.deleteObjects(element);
   //      }
   //      return bSubObjects;
   //   }

   /** 
    * TODO (brod) add comment for method readSampleFiles 
    * 
    * <p> TODO rename commonXsd to pXsd
    * @param commonXsd TODO (brod) add text for param commonXsd
    * @param pSampleFile TODO (brod) add text for param pSampleFile
    * 
    * @author brod 
    */
   private void readSampleFiles(XmlObject commonXsd, File pSampleFile)
   {
      if (pSampleFile.exists()) {
         if (pSampleFile.isDirectory()) {
            File[] listFiles = pSampleFile.listFiles();
            for (File listFile : listFiles) {
               readSampleFiles(commonXsd, listFile);
            }
         } else if (pSampleFile.getName().endsWith(".xml")) {
            try {
               System.out.println(pSampleFile.getAbsolutePath());
               XmlObject xmlObject = new XmlObject(pSampleFile).getFirstObject();
               readSampleFiles(commonXsd, xmlObject, pSampleFile.getName() + ".smp");
            }
            catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      }
   }

   /** 
    * TODO (brod) add comment for method readSampleFiles 
    * 
    * <p> TODO rename commonXsd to pXsd, xmlObject to pObject
    * @param commonXsd TODO (brod) add text for param commonXsd
    * @param xmlObject TODO (brod) add text for param xmlObject
    * @param psFileName TODO (brod) add text for param psFileName
    * 
    * @author brod 
    */
   private void readSampleFiles(XmlObject commonXsd, XmlObject xmlObject, String psFileName)
   {
      String[] wrongSampleFiles = { "FareQuoteMultiDisplay_14_s29.xml" };
      // ignore the following files
      for (String wrongSampleFile : wrongSampleFiles) {
         if (psFileName.contains(wrongSampleFile)) {
            return;
         }
      }

      XmlObject[] objects = xmlObject.getObjects("");
      String sMainName = xmlObject.getName();
      if (sMainName.startsWith("!")) {
         return;
      }
      if (sMainName.contains("_")) {
         sMainName = sMainName.substring(0, sMainName.indexOf("_"));
      }
      List<XmlObject> lstSequences = new ArrayList<XmlObject>();
      List<XmlObject> lstAnnotation = new ArrayList<XmlObject>();
      List<XmlObject> lstAppInfo = new ArrayList<XmlObject>();

      XmlObject[] arrHelpObjects = commonXsd.findSubObjects("element", "name", sMainName);
      for (XmlObject arrHelpObject : arrHelpObjects) {
         lstSequences.add(arrHelpObject);
      }

      arrHelpObjects = commonXsd.findSubObjects("complexType", "name", sMainName);
      for (XmlObject arrHelpObject : arrHelpObjects) {
         lstSequences.add(arrHelpObject);
      }
      arrHelpObjects = commonXsd.findSubObjects("complexType", "name", sMainName + _TYPE);
      for (XmlObject arrHelpObject : arrHelpObjects) {
         lstSequences.add(arrHelpObject);
      }
      arrHelpObjects = commonXsd.findSubObjects("complexType", "name", sMainName + "Request");
      for (XmlObject arrHelpObject : arrHelpObjects) {
         lstSequences.add(arrHelpObject);
      }
      for (int i = 0; i < lstSequences.size(); i++) {
         XmlObject element = lstSequences.get(i);
         lstAnnotation.add(element.createObject("xs:annotation"));
         lstAppInfo.add(new XmlObject("<xs:appinfo source=\"" + psFileName + "\" />")
               .getFirstObject());
         XmlObject complexType = element.getObject("complexType");
         if (complexType == null) {
            complexType = element;
         }
         XmlObject sequence = complexType.getObject("sequence");
         if (sequence == null) {
            System.err.println("Error within common.xsd/" + sMainName);
            return;
         }
         lstSequences.set(i, sequence);
      }

      for (XmlObject object : objects) {
         String sName = object.getName();
         if (!sName.startsWith("!")) {
            XmlObject subObject = null;
            XmlObject appinfo = null;
            for (int i = 0; subObject == null && i < lstSequences.size(); i++) {
               XmlObject sequence = lstSequences.get(i);
               appinfo = lstAppInfo.get(i);
               subObject = sequence.findSubObject("element", "name", sName);
               if (subObject == null) {
                  subObject = sequence.findSubObject("element", "ref", sName);
               }
            }
            if (subObject == null) {
               // Ignore this sample file
               System.err.println("Error within " + psFileName + " > common.xsd/" + sMainName + "/"
                     + sName + " does not exist");
               return;
            }

            if (appinfo != null) {
               appinfo.addObject(new XmlObject("<xs:attribute name=\"" + sName + "\"/>")
                     .getFirstObject());
            }
         }
      }

      // add the appInfos
      for (int i = 0; i < lstAnnotation.size(); i++) {
         XmlObject appInfo = lstAppInfo.get(i);
         if (appInfo.countObjects() > 0) {
            lstAnnotation.get(i).addObject(appInfo);
         }
      }
      //helpObjects[i].createObject("xs:annotation").addObject(appinfo);

      for (XmlObject object : objects) {
         readSampleFiles(commonXsd, object, psFileName);
      }
   }

   class CompareElement
      implements Comparable<CompareElement>
   {
      String sMin;
      String sMax;
      int iCount = 1;

      @Override
      public String toString()
      {
         return "[" + iCount + "x " + sMin + "<" + sMax + "]";
      }

      @Override
      public int compareTo(CompareElement o)
      {
         int compareTo = sMin.compareTo(o.sMin);
         if (compareTo == 0) {
            compareTo = sMax.compareTo(o.sMax);
         }
         return compareTo;
      }

      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + (sMax == null ? 0 : sMax.hashCode());
         result = prime * result + (sMin == null ? 0 : sMin.hashCode());
         return result;
      }

      public boolean invert(CompareElement other)
      {
         if (sMin.equals(other.sMax) && sMax.equals(other.sMin)) {
            return true;
         }
         return false;
      }

      @Override
      public boolean equals(Object obj)
      {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         CompareElement other = (CompareElement) obj;

         if (sMax == null) {
            if (other.sMax != null) {
               return false;
            }
         } else if (!sMax.equals(other.sMax)) {
            return false;
         }
         if (sMin == null) {
            if (other.sMin != null) {
               return false;
            }
         } else if (!sMin.equals(other.sMin)) {
            return false;
         }
         return true;
      }

      public int indexOf(String sMinValue)
      {
         if (sMinValue.equals(sMin)) {
            return 0;
         }
         if (sMinValue.equals(sMax)) {
            return 1;
         }
         return -1;
      }

      public String getMinValue(HashSet<String> hsAllMinValues)
      {
         if (!hsAllMinValues.contains(sMin)) {
            return sMin;
         }
         if (!hsAllMinValues.contains(sMax)) {
            return sMax;
         }
         return "";
      }

      public void inc()
      {
         iCount++;
      }

   }

   /** 
    * TODO (brod) add comment for method sortSchemaElement 
    * 
    * <p> TODO rename xmlObject to pObject
    * @param xmlObject TODO (brod) add text for param xmlObject
    * 
    * @author brod 
    */
   private void sortSchemaElement(XmlObject xmlObject)
   {
      String xmlName = xmlObject.getAttribute("name");
      System.out.println("Sort " + xmlName);
      XmlObject annotation = xmlObject.getObject("annotation");

      XmlObject complexType =
         xmlObject.getName().equals("complexType") ? xmlObject : xmlObject.getObject("complexType");
      XmlObject sequence = complexType.getObject("sequence");
      XmlObject[] elements = sequence.deleteObjects("element");

      //      // add appInfo with 'original' order ... so that each object will be compared
      //      XmlObject originalOrder =
      //         annotation.createObject("xs:appinfo", "source", "OriginalOrder", true);
      //      // add the missing attributes to the last object
      //      for (int i = 0; i < elements.length; i++) {
      //         XmlObject element = elements[i];
      //         originalOrder.createObject("xs:attribute", "name", element.getAttribute("name")
      //               + element.getAttribute("ref"), true);
      //      }

      // get all appInfos (including the last one)
      XmlObject[] appInfos = annotation.deleteObjects("appinfo");

      // get all elements to a list
      List<CompareElement> lstAll = new ArrayList<CompareElement>();
      for (XmlObject appInfo : appInfos) {
         XmlObject[] attributes = appInfo.getObjects("attribute");
         for (int j = 0; j < attributes.length; j++) {
            String sMin = attributes[j].getAttribute("name");
            for (int k = j + 1; k < attributes.length; k++) {
               String sMax = attributes[k].getAttribute("name");
               if (!sMin.equals(sMax)) {
                  CompareElement compareElement = new CompareElement();
                  compareElement.sMin = sMin;
                  compareElement.sMax = sMax;
                  CompareElement bFound = null;
                  for (int l = 0; l < lstAll.size(); l++) {
                     if (lstAll.get(l).equals(compareElement)) {
                        bFound = lstAll.get(l);
                        break;
                     }
                  }
                  if (bFound == null) {
                     lstAll.add(compareElement);
                  } else {
                     bFound.inc();
                  }
               }
            }
         }
      }

      // try to find invert elements
      for (int i = 0; i < lstAll.size();) {
         CompareElement compareElement = lstAll.get(i);
         CompareElement invert = null;
         for (int j = i + 1; j < lstAll.size(); j++) {
            if (compareElement.invert(lstAll.get(j))) {
               invert = lstAll.get(j);
            }
         }
         if (invert != null) {
            if (invert.iCount > compareElement.iCount) {
               System.err.println("Keep:" + invert.toString() + ", Ignore:"
                     + compareElement.toString());
               lstAll.remove(compareElement);
            } else {
               System.err.println("Keep:" + compareElement.toString() + ", Ignore:"
                     + invert.toString());
               lstAll.remove(invert);
            }
         } else {
            i++;
         }
      }

      HashSet<String> hsAllMinValues = new HashSet<String>();
      int iPosition = 0;
      // get the minimum element over all elements
      while (lstAll.size() > 0 && iPosition < elements.length) {
         // get the min Value
         String sMinValue =
            elements[iPosition].getAttribute("name") + elements[iPosition].getAttribute("ref");
         HashSet<String> hsMinValues = new HashSet<String>();
         hsMinValues.add(sMinValue);
         for (int i = 0; i < lstAll.size();) {
            CompareElement compareElement = lstAll.get(i);
            if (compareElement != null) {
               // if the min value is not set or the minValue is at a 
               // later position                  
               String sAppMinValue = compareElement.getMinValue(hsAllMinValues);
               if (sAppMinValue.length() == 0 || sMinValue.equals(sAppMinValue)) {
                  // ignore ths entry
               } else if (sMinValue.length() == 0) {
                  sMinValue = sAppMinValue;
               } else if (compareElement.indexOf(sMinValue) > 0) {
                  if (hsMinValues.add(sAppMinValue)) {
                     System.out.print(sMinValue + " > ");
                     sMinValue = sAppMinValue;
                     // start from the begining
                     if (i > 0) {
                        i = -1;
                     }
                  } else {
                     // incorrect order
                     System.out.print(sMinValue + " < ");
                     // reset the min value
                     sMinValue =
                        elements[iPosition].getAttribute("name")
                              + elements[iPosition].getAttribute("ref");
                  }
               }
            }
            i++;
         }
         System.out.println(sMinValue);
         if (sMinValue.length() > 0) {
            hsAllMinValues.add(sMinValue);
            // get the element, which matches this minValue
            for (int i = iPosition; i < elements.length; i++) {
               String sElementName =
                  elements[i].getAttribute("name") + elements[i].getAttribute("ref");
               if (sElementName.equals(sMinValue)) {
                  if (i > iPosition) {
                     // move this element to the top
                     XmlObject e = elements[i];
                     for (int k = i - 1; k >= iPosition; k--) {
                        elements[k + 1] = elements[k];
                     }
                     elements[iPosition] = e;
                  }
                  iPosition++;
                  break;
               }
            }
            // remove all, which contains all min values

         } else {
            lstAll.clear();
         }
      }

      int iCountMaxOccurs = 0;

      for (XmlObject element : elements) {
         // add the "sorted" element
         sequence.addObject(element);
         // correct the maxoccur flag only if elements contain a maxOccurs flag 
         if (element.getAttribute("maxOccurs").length() > 0) {
            iCountMaxOccurs++;
         }
      }
      boolean bCorrectMaxOccurs = false;
      if (iCountMaxOccurs > 1) {
         bCorrectMaxOccurs = iCountMaxOccurs == elements.length;
      }

      Hashtable<String, String> htRequestResponse = new Hashtable<String, String>();
      // clean the appInfos
      String sResponse = "Response";
      String sRequest = "Request";
      for (XmlObject appInfo : appInfos) {
         String sSource = appInfo.getAttribute("source");
         String sType =
            sSource.endsWith("_request.xml") ? sRequest : sSource.endsWith("_response.xml")
                  ? sResponse : "";
         if (sType.length() > 0) {
            sSource = sSource.substring(0, sSource.lastIndexOf("_"));
            String sTypes = htRequestResponse.get(sType);
            if (sTypes == null) {
               sTypes = sSource;
            } else if (!sTypes.contains(sSource)) {
               sTypes += "," + sSource;
            }
            htRequestResponse.put(sType, sTypes);
         }
      }
      XmlObject documentation = annotation.createObject("xs:documentation");
      String cData = documentation.getCData();
      if (cData.length() > 0 && !cData.startsWith("<")) {
         cData = "<pre>" + cData.replaceAll(Pattern.quote("*/"), "* /") + "</pre>\n";
      }
      cData += "Used within " + htRequestResponse;
      documentation.setCData(cData);

      // if there are no requests (only responses) don't correct the maxOccur flags 
      if (htRequestResponse.get(sRequest) != null) {
         bCorrectMaxOccurs = false;
      }
      // validate, if there is a duplicate element within the response
      for (XmlObject appInfo : appInfos) {
         HashSet<String> hsAllNames = new HashSet<String>();
         XmlObject[] attributes = appInfo.getObjects("attribute");
         String sLast = "";
         for (XmlObject attribute : attributes) {
            String sName = attribute.getAttribute("name");
            if (sLast.equals(sName)) {
               // this element has to occur multiple times
               for (XmlObject element : elements) {
                  String sNameElement = element.getAttribute("name") + element.getAttribute("ref");
                  if (sNameElement.equals(sName)) {
                     element.setAttribute("maxOccurs", "unbounded");
                  }
               }
            } else {
               if (!hsAllNames.add(sName)) {
                  // duplicate element in different order
                  if (!bCorrectMaxOccurs) {
                     System.err.println("duplicate element in different order for element "
                           + xmlName + "/" + sName + " within " + appInfo.getAttribute("source")
                           + "");

                     // deactivated, because requests require correct order
                     // bCorrectMaxOccurs = true;
                  }
               }
            }
            sLast = sName;
         }
      }

      if (bCorrectMaxOccurs) {
         sequence.setName("xs:choice");
         sequence.setAttribute("minOccurs", "0");
         sequence.setAttribute("maxOccurs", "unbounded");
         for (XmlObject element : elements) {
            element.setAttribute("maxOccurs", null);
         }
      }

      //      // if there are no elements, remove the sequence
      //      if (elements.length == 0) {
      //         xmlObject.deleteObjects("complexType");
      //         xmlObject.deleteObjects("sequence");
      //         xmlObject.setAttribute("type", "xs:string");
      //      }
   }

   /** 
    * TODO (brod) add comment for method sortSchemaFile 
    * 
    * <p> TODO rename schemaFile to pFile
    * @param schemaFile TODO (brod) add text for param schemaFile
    * 
    * @author brod 
    */
   private void sortSchemaFile(XmlObject schemaFile)
   {
      XmlObject[] elements = schemaFile.getObjects("");
      for (XmlObject element : elements) {
         try {
            sortSchemaElement(element);
         }
         catch (Exception ex) {
            ex.printStackTrace();
            // ignor this entry
            System.err.println(element);
         }
      }
   }
}
