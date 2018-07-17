package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import net.ifao.util.CorrectDatabindingXsd;
import net.ifao.xml.*;


/**
 * Class ImportNvsWsdl
 *
 * <p>
 * Copyright &copy; 2009, i:FAO Group GmbH
 * @author kaufmann
 */
public class ImportNvsWsdl
{

   private String _sBaseDir = null;
   private String _sWsdlFile = null;
   private String _sJavaCommunication = null;
   private StringBuilder _sbResult = null;
   private String _sLastError = null;
   private TreeSet<String> _namespacesCreated = null;
   private HashMap<String, String> _namespacePackage = null;
   private static final String REPLACEXMLTAG = "_replaceXmlTag";

   /**
    * Constructor ImportNvsWsdl
    *
    *
    * @author kaufmann
    */
   public ImportNvsWsdl(String psBaseDir, String psWsdlFile, String psJavaCommunication)
      throws Exception
   {
      // cut off a backslash at the end
      if (psBaseDir.endsWith("\\") || psBaseDir.endsWith("/")) {
         psBaseDir = psBaseDir.substring(0, psBaseDir.length() - 1);
      }

      _sBaseDir = psBaseDir.replace('\\', '/');

      _sWsdlFile = psWsdlFile;
      if (!(new File(_sWsdlFile).exists())) {
         throw new Exception("File " + _sWsdlFile + " not found!");
      }

      _sJavaCommunication = psJavaCommunication;
      if (!(new File(_sJavaCommunication).exists())) {
         throw new Exception("File " + _sJavaCommunication + " not found!");
      }

      _sbResult = new StringBuilder();
   }

   /**
    * Method startGeneration
    *
    *
    * @author kaufmann
    */
   public void startGeneration()
   {
      // get the WSDL and save it
      String sWsdl = updateWsdl();
      if (sWsdl != null) {
         updateSchemasAndDataBinding(sWsdl);
         updateBatchAndCastorBuilderProperties();
         updateJavaCommunication(sWsdl);
      }
      _sbResult.append("_______________________________________\n\n");

      if (_sbResult.indexOf("* ") >= 0) {
         _sbResult.append("_______________________________________\n\n");
         _sbResult.append("\nFiles/directories marked with * have to be submitted (in CVS) !\n");
      }

   }

   /**
    * Method getResult
    *
    * @return result of the operation
    *
    * @author kaufmann
    */
   public String getResult()
   {
      return _sbResult.toString();
   }

   /**
    * Method getLastError
    *
    * @return last error stored in _sLastError
    *
    * @author kaufmann
    */
   public String getLastError()
   {
      return _sLastError;
   }

   /**
    * Method updateWsdl loads the WSDL from the file and checks, if it has changed
    *
    * @return new WSDL file or <code>null</code>, if it has not been changed or could not be loaded
    *
    * @author kaufmann
    */
   private String updateWsdl()
   {
      String sOldWsdlFileName = Util.getProviderDataPath(_sBaseDir, "de/dbsystems/www/data.wsdl");
      String sNewWsdl = Util.loadFromFile(_sWsdlFile);
      if (sNewWsdl == null || sNewWsdl.trim().length() == 0) {
         _sLastError = "Error reading " + _sWsdlFile;
         return null;
      }
      if (!Util.updateFile(sOldWsdlFileName, sNewWsdl, _sbResult)) {
         _sbResult.append("\nWSDL has not been changed.\n");
      }
      return sNewWsdl;
   }

   /**
    * Method updateSchemasAndDataBinding creates the dataBinding for the WebService passed and 
    * calls method updateSchemasOfWebservice to update the data.xsd and dataBinding.xml files 
    * related to this WebService 
    *
    * @param psWsdl WSDL of the WebService
    * @return true, if one of the files has been changed
    *
    * @author kaufmann
    */
   private boolean updateSchemasAndDataBinding(String psWsdl)
   {
      Hashtable<String, List<String>> htComplexTypes = new Hashtable<String, List<String>>();
      XmlObject wsdl = new XmlObject(psWsdl);

      // handle the WSDL-Arrays
      collectComplexTypes(htComplexTypes, wsdl);
      replaceArraysOfWsdl(htComplexTypes, wsdl);

      // make nillable elements optional
      makeNillableElementsOptionalAndRemoveEmptySequences(wsdl);

      String sWsdl = wsdl.toString();

      XmlObject schema = WsdlObject.getSchema(sWsdl);

      // delete 'duplicate' elements
      HashSet<String> hs = new HashSet<String>();

      XmlObject[] objs = schema.getObjects("");

      for (int i = 0; i < objs.length; i++) {
         if (!hs.add(objs[i].toString())) {
            schema.deleteObjects(objs[i]);
         }
      }

      // create data binding
      HashSet<String> simpleTypes = null;
      String sBinding = WsdlObject.getDataBinding(schema, simpleTypes, "", true, true);

      return updateSchemasOfWebservice(sWsdl, sBinding);
   }

   /**
    * Method makeNillableElementsOptional adds minOccurs="0" to all elements of the wsdl having
    * nillable="true". Additionally empty sequences (&lt;sequence /&gt;) will be removed
    *
    * @param pWsdl the wsdl to change
    *
    * @author kaufmann
    */
   private void makeNillableElementsOptionalAndRemoveEmptySequences(XmlObject pWsdl)
   {
      XmlObject[] definitions = pWsdl.getObjects("definitions");
      for (XmlObject definition : definitions) {
         XmlObject[] types = definition.getObjects("types");
         for (XmlObject type : types) {
            XmlObject[] schemas = type.getObjects("schema");
            for (XmlObject schema : schemas) {
               XmlObject[] objs = schema.getObjects("");
               for (XmlObject xmlObject : objs) {
                  makeNillablesOptionalAndRemoveEmptySequences(xmlObject);
               }
            }
         }
      }

   }

   /**
    * Method makeNillablesOptional adds minOccurs="0" to the element passed, if it has attribute
    * nillable="true". Additionally empty sequences (&lt;sequence /&gt;) will be removed. This 
    * method calls itself for every subobject of the element passed
    *
    * @param pXmlObject the XMl object to check
    *
    * @author kaufmann
    */
   private void makeNillablesOptionalAndRemoveEmptySequences(XmlObject pXmlObject)
   {
      if (pXmlObject.getName().equals("element")) {
         if ("true".equals(pXmlObject.getAttribute("nillable"))) {
            pXmlObject.setAttribute("minOccurs", "0");
         }
      }

      // get the sub objects
      XmlObject[] subObjects = pXmlObject.getObjects("");

      if (subObjects.length == 1 && subObjects[0].getName().equals("sequence")
            && subObjects[0].getObjects("").length == 0) {
         // remove the emptzy sequence
         pXmlObject.deleteObjects(subObjects[0]);
      } else {
         // loop over subElements
         for (XmlObject subObject : subObjects) {
            makeNillablesOptionalAndRemoveEmptySequences(subObject);
         }
      }
   }

   /**
    * Method collectComplexTypes
    *
    * @param phtComplexTypes
    * @param pWsdl
    *
    * @author kaufmann
    */
   private void collectComplexTypes(Hashtable<String, List<String>> phtComplexTypes, XmlObject pWsdl)
   {
      XmlObject[] definitions = pWsdl.getObjects("definitions");
      for (XmlObject definition : definitions) {
         XmlObject[] types = definition.getObjects("types");
         for (XmlObject type : types) {
            XmlObject[] schemas = type.getObjects("schema");
            for (XmlObject schema : schemas) {
               XmlObject[] objs = schema.getObjects("");

               // ... collect wsdl-Arrays
               for (int i = 0; i < objs.length; i++) {
                  if (objs[i].getName().equals("complexType")) {
                     try {
                        String sComplexTypeName = objs[i].getAttribute("name");
                        List<String> list = phtComplexTypes.get(sComplexTypeName);

                        XmlObject attribute =
                           objs[i].getObject("complexContent").getObject("restriction")
                                 .getObject("attribute");
                        String sArray = attribute.getAttribute("arrayType");

                        if (sArray.length() > 0 && sArray.indexOf("[") > 0) {
                           if (list == null) {
                              list = new ArrayList<String>();
                              phtComplexTypes.put(sComplexTypeName, list);
                           }
                           sArray = sArray.substring(0, sArray.indexOf("["));

                           list.add(sArray);
                           schema.deleteObjects(objs[i]);
                        }
                     }
                     catch (Exception ex) {}
                  }
               }
            }
         }
      }
   }

   private void replaceArraysOfWsdl(Hashtable<String, List<String>> phtComplexTypes, XmlObject pWsdl)
   {
      XmlObject[] definitions = pWsdl.getObjects("definitions");
      for (XmlObject definition : definitions) {
         XmlObject[] types = definition.getObjects("types");
         for (XmlObject type : types) {
            XmlObject[] schemas = type.getObjects("schema");
            for (XmlObject schema : schemas) {
               replaceArrays(phtComplexTypes, schema);
            }
         }
      }
   }

   /** 
    * Method replaceArrays 
    * 
    * @param phtComplexTypes 
    * @param psNameSpace 
    * @param pXmlObject 
    * 
    * @author Andreas Brod 
    */
   private void replaceArrays(Hashtable<String, List<String>> phtComplexTypes, XmlObject pXmlObject)
   {
      String sType = pXmlObject.getAttribute("type");
      String sNameSpace = "";

      if (sType.indexOf(":") > 0) {
         sNameSpace = sType.substring(0, sType.indexOf(":"));
         sType = sType.substring(sType.indexOf(":") + 1);
      }

      if ((sType.length() > 0) && (phtComplexTypes.get(sType) != null)) {
         //         String sType2 = phtComplexTypes.get(sType);

         //         if (sType2.length() == 0) {
         //            sType2 = pXmlObject.getAttribute("type");
         //         } else {

         List<String> list = phtComplexTypes.get(sType);
         String sType2 = list.get(0);
         if (list.size() > 1) {
            for (String sCurrentType : list) {
               if (sCurrentType.startsWith(sNameSpace)) {
                  sType2 = sCurrentType;
                  System.out.println("Found type " + sType2);
                  break;
               }
            }
         }
         XmlObject arrayItem = pXmlObject.copy();
         pXmlObject.setAttribute("type", null);
         XmlObject complexType = new XmlObject("<complexType/>").getFirstObject();
         pXmlObject.addElementObject(complexType);
         XmlObject sequence = new XmlObject("<sequence/>").getFirstObject();
         complexType.addElementObject(sequence);
         arrayItem.setAttribute("maxOccurs", "unbounded");
         arrayItem.setAttribute("minOccurs", "0");
         sequence.addElementObject(arrayItem);
         pXmlObject = arrayItem;
         pXmlObject.setAttribute("type", sType2);

      }

      // loop over subElements
      XmlObject[] subs = pXmlObject.getObjects("");

      for (int i = 0; i < subs.length; i++) {
         replaceArrays(phtComplexTypes, subs[i]);
      }
   }


   /**
    * Method updateSchemasOfWebservice updates all data.xsd and dataBinding.xml files related to
    * the WebService passed
    *
    * @param psWsdl WSDL of the WebService
    * @param psBinding content for dataBinding.xml
    * 
    * @return true, if one of the files has been changed
    *
    * @author kaufmann
    */
   private boolean updateSchemasOfWebservice(String psWsdl, String psBinding)
   {
      boolean bChanged = false;
      _namespacesCreated = new TreeSet<String>();
      _namespacePackage = new HashMap<String, String>();


      XmlObject wsdl = new XmlObject(psWsdl);

      Hashtable<String, String> htMessages = getWsdlMessages(wsdl);

      XmlObject[] definitions = wsdl.getObjects("definitions");
      for (XmlObject definition : definitions) {
         // get namespace definitions
         HashMap<String, String> globalNamespaces = getNamespaces(definition);

         XmlObject[] types = definition.getObjects("types");
         for (XmlObject type : types) {
            XmlObject[] schemas = type.getObjects("schema");
            for (XmlObject schema : schemas) {
               addElementsForWsdlMessages(schema, htMessages);

               schema.deleteObjects("import");
               HashMap<String, String> schemaNamespaces = getNamespaces(schema);
               String sSchemaTargetNamespace = schemaNamespaces.get("targetNamespace");
               String sPathForDataXsd = getPathFromNamespace(sSchemaTargetNamespace);

               // get the namespaces used in this schema
               Set<String> namespacesUsed = new TreeSet<String>();
               String sSchema = schema.toString();
               Matcher m = Pattern.compile(" (?:base|type|ref)=\"(\\w+):\\w+\"").matcher(sSchema);
               while (m.find()) {
                  namespacesUsed.add(m.group(1));
               }
               m = Pattern.compile(" (\\w+):arrayType=\"").matcher(sSchema);
               while (m.find()) {
                  namespacesUsed.add(m.group(1));
               }
               int iInsertPos = 0;
               for (String sNamespace : namespacesUsed) {
                  String sNamespaceURI = globalNamespaces.get(sNamespace);
                  schema.setAttribute("xmlns:" + sNamespace, sNamespaceURI);
                  if (!sNamespaceURI.equals(sSchemaTargetNamespace)
                        && !sNamespaceURI.startsWith("http://www.w3.org/")
                        && !sNamespaceURI.startsWith("http://schemas.xmlsoap.org/")) {
                     XmlObject importObject = new XmlObject("<import/>").getFirstObject();
                     importObject.setAttribute("id", sNamespace);
                     importObject.setAttribute("namespace", sNamespaceURI);
                     importObject
                           .setAttribute(
                                 "schemaLocation",
                                 (getRelativePathFromNamespace(sSchemaTargetNamespace,
                                       sNamespaceURI) + "data.xsd").replace('\\', '/'));
                     schema.addElementObject(importObject, iInsertPos++);
                  }
               }

               _namespacesCreated.add(sSchemaTargetNamespace);
               _namespacePackage.put(sSchemaTargetNamespace, sPathForDataXsd.replace('/', '.'));


               // write the schema, if necessary
               bChanged |=
                  Util.updateFile(
                        Util.getProviderDataPath(_sBaseDir, sPathForDataXsd + "data.xsd"),
                        schema.toString(), _sbResult);

               // write the binding file, if necessary
               if (psBinding != null && psBinding.length() > 0) {
                  bChanged |=
                     Util.updateFile(
                           Util.getProviderDataPath(_sBaseDir, sPathForDataXsd + "dataBinding.xml"),
                           psBinding, _sbResult);
                  if (CorrectDatabindingXsd.correctDataBinding(
                        new File(Util.getProviderDataPath(_sBaseDir, sPathForDataXsd
                              + "dataBinding.xml")), "")) {
                     bChanged = true;
                  }

               }
            }
         }
      }

      return bChanged;
   }

   /**
    * Method addElementsForWsdlMessages
    *
    * @param pSchema
    * @param phtMessages
    *
    * @author kaufmann
    */
   private void addElementsForWsdlMessages(XmlObject pSchema, Hashtable<String, String> phtMessages)
   {
      // add elements for complex types
      XmlObject[] complexTypes = pSchema.getObjects("complexType");

      for (XmlObject complexType : complexTypes) {
         String sType = complexType.getAttribute("name");

         if (sType.length() > 0) {
            if (pSchema.findSubObject("element", "name", sType) == null) {
               if (phtMessages.containsKey(sType)) {
                  String sElement =
                     "<element name=\"" + sType + "\" type=\"" + phtMessages.get(sType) + "\"/>";

                  pSchema.addObject(new XmlObject(sElement));
               }
            }
         }
      }
   }

   /**
    * Method getWsdlMessages
    *
    * @param pWsdl
    * @return
    *
    * @author kaufmann
    */
   private Hashtable<String, String> getWsdlMessages(XmlObject pWsdl)
   {
      Hashtable<String, String> htMessages = new Hashtable<String, String>();
      XmlObject[] messages = pWsdl.getFirstObject().getObjects("message");
      for (XmlObject message : messages) {
         String sType = message.createObject("part").getAttribute("type");

         if (sType.indexOf(":") > 0) {
            htMessages.put(sType.substring(sType.indexOf(":") + 1), sType);
         }

      }
      return htMessages;
   }

   /**
    * Method getNamespaces collects all name spaces used by the XML passed
    *
    * @param pXml XML to check
    * @return HashMap containing the namespaces used
    *
    * @author kaufmann
    */
   private HashMap<String, String> getNamespaces(XmlObject pXml)
   {
      HashMap<String, String> namespaces = new HashMap<String, String>();
      String[] attributeNames = pXml.getAttributeNames();
      for (String sAttribute : attributeNames) {
         Matcher m =
            Pattern.compile("^(?:xmlns:)?(targetNamespace|(?<=xmlns:)\\w+)=\"([^\"]+)\"$").matcher(
                  sAttribute);
         if (m.matches()) {
            namespaces.put(m.group(1), m.group(2));
         }
      }
      return namespaces;
   }

   /**
    * Method getPathFromNamespace creates the path for the namespace
    *
    * @param psURI namespace URI
    * @return path for the namespace
    *
    * @author kaufmann
    */
   private String getPathFromNamespace(String psURI)
   {
      URI uri = null;
      try {
         uri = new URI(psURI);
      }
      catch (URISyntaxException pException) {
         pException.printStackTrace();
         throw new RuntimeException("namespace URI is invalid");
      }
      StringBuilder sbPath = new StringBuilder(psURI.length());
      String[] sHostParts = uri.getHost().split("[.]");
      for (int i = sHostParts.length - 1; i >= 0; i--) {
         sbPath.append(sHostParts[i]).append("/");
      }
      String[] sPathParts = uri.getPath().split("[\\\\/.]");
      for (String sPathPart : sPathParts) {
         if (sPathPart.length() > 0 && !sPathPart.equalsIgnoreCase("package")) {
            sbPath.append(sPathPart).append("/");
         }
      }
      return sbPath.toString();
   }

   /**
    * Method getRelativePathFromNamespace creates the path to the namespace relative to the 
    * base namespace passed
    *
    * @param psNamespaceURIBase base of the namespaces
    * @param psNamespaceURI2 namespace for which the relative path will be created
    * @return relative path for the namespace
    *
    * @author kaufmann
    */
   private String getRelativePathFromNamespace(String psNamespaceURIBase, String psNamespaceURI2)
   {
      String sBasePath = getPathFromNamespace(psNamespaceURIBase);
      String[] basePathParts = sBasePath.split("[\\\\/]");
      String sPath = getPathFromNamespace(psNamespaceURI2);
      String[] pathParts = sPath.split("[\\\\/]");

      String sNewPath = "";

      int i = 0;
      while (i < basePathParts.length && i < pathParts.length
            && basePathParts[i].equals(pathParts[i])) {
         i++;
      }

      for (int j = 0; j < basePathParts.length - i; j++) {
         sNewPath += "../";
      }
      for (int j = i; j < pathParts.length; j++) {
         sNewPath += pathParts[j] + "/";
      }

      return sNewPath;
   }

   /**
    * Method updateBatch adds the code for the batch file for the WebService passed. Field 
    * _namespacesCreated must contain all namespaces (=sub-packages) used by the webservice
    * @author kaufmann
    */
   private void updateBatchAndCastorBuilderProperties()
   {
      StringBuilder sbCastorBuilderProperties = new StringBuilder();
      sbCastorBuilderProperties.append("org.exolab.castor.builder.nspackages=\\\n");
      int i = _namespacesCreated.size();
      for (String sNamespace : _namespacesCreated) {
         String sPackage = getPathFromNamespace(sNamespace).replaceAll("[\\\\/]", ".");
         sPackage = _namespacePackage.get(sNamespace).replace("lib.providerdata.", "");
         sbCastorBuilderProperties.append(sNamespace).append("=")
               .append(sPackage.substring(0, sPackage.length() - 1));
         if (--i > 0) {
            sbCastorBuilderProperties.append(",\\");
         }
         sbCastorBuilderProperties.append("\n");
      }

      // save the castorbuilder.properties file
      for (String sNamespace : _namespacesCreated) {
         String sPathForDataXsd = getPathFromNamespace(sNamespace);
         Util.updateFile(
               Util.getProviderDataPath(_sBaseDir, sPathForDataXsd + "castorbuilder.properties"),
               sbCastorBuilderProperties.toString(), _sbResult);
      }
   }

   /**
    * Method updateJavaCommunication creates the java communication class for the webservice passed
    *
    * @param psService WebService code, e.g. EvRailApi, EvReferenceData
    * @param psWsdl WSDL of the WebService
    *
    * @author kaufmann
    */
   private void updateJavaCommunication(String psWsdl)
   {
      String sJavaCommunication = createJavaCommunication(psWsdl);
      writeBahnNvsCommunication("NVSGUS", sJavaCommunication);
   }

   /**
    * Method createJavaCommunication has been copied from old arctic requester
    *
    * @param psWsdl WSDL of the WebService
    * @return Java code for the communication class
    *
    * @author kaufmann
    */
   private String createJavaCommunication(String psWsdl)
   {
      WsdlObject wsdl = new WsdlObject(psWsdl);
      XmlObject definitions = new XmlObject(psWsdl).createObject("definitions");
      XmlObject service = definitions.createObject("service");
      XmlObject binding = definitions.createObject("binding");
      XmlObject[] operation = binding.getObjects("operation");

      XmlObject schema = definitions.createObject("types").createObject("schema");

      StringBuffer sbJava = new StringBuffer();
      HashSet<String> hsExceptions = new HashSet<String>();

      for (int i = 0; i < operation.length; i++) {
         String sOperation = operation[i].getAttribute("name");

         String sJavaOperation = loadWSDLObject(wsdl, definitions, sOperation, hsExceptions);

         while (sJavaOperation.startsWith("import ")) {

            // trunc Import from response
            sJavaOperation = sJavaOperation.substring(sJavaOperation.indexOf("\n") + 1);
         }

         // add Java operation
         sbJava.append(sJavaOperation + "\n");
      }

      if (sbJava.indexOf(" _getParamType(") > 0) {
         sbJava.append(getParamTypeMethod());
      }

      sbJava.append(getInnerMethod(hsExceptions, "BahnNvs"));

      return sbJava.toString();
   }


   /** 
    * The method getParamTypeMethod returns the complete sourcecode for 
    * the method _getParamType 
    * 
    * @return The SourceCode for the  ParamTypeMethod 
    * 
    * @author brod 
    */
   private String getParamTypeMethod()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("  /**\n");
      sb.append("    * The method _getParamType returns the 'correct' type according\n");
      sb.append("    * to the defined namespaces.\n");
      sb.append("    *\n");
      sb.append("    * @param psType The 'raw' type\n");
      sb.append("    * @return The formated type e.g. xsi:type='xsd:string'\n");
      sb.append("    *\n");
      sb.append("    * @author _GENERATOR_\n");
      sb.append("    */\n");
      sb.append("   private String _getParamType(String psType)\n");
      sb.append("    {\n");
      sb.append("       InitHttpSoapCommunication initCommunication = (InitHttpSoapCommunication) _httpCommunication\n");
      sb.append("             .getInitCommunication();\n");
      sb.append("\n");
      sb.append("       String schemaInstanceNamespace = initCommunication.getSchemaInstanceNamespace();\n");
      sb.append("       String schemaNamespace = initCommunication.getSchemaNamespace();\n");
      sb.append("       if (schemaInstanceNamespace.length() == 0 || schemaNamespace.length() == 0) {\n");
      sb.append("          return \"\";\n");
      sb.append("       }\n");
      sb.append("       return \" \" + schemaInstanceNamespace + \":type=\\\"\" + \"\" + schemaNamespace + \":\" + psType\n");
      sb.append("             + \"\\\"\";\n");
      sb.append("    }\n");

      return sb.toString();
   }

   /** 
    * Method getInnerMethod has been copied from old arctic requester
    * 
    * @param phsExceptions has been copied from old arctic requester 
    * @param psProvider has been copied from old arctic requester
    * @return has been copied from old arctic requester
    * 
    * @author Andreas Brod 
    */
   private String getInnerMethod(HashSet<String> phsExceptions, String psProvider)
   {
      StringBuffer sb = new StringBuffer();

      sb.append("\n");
      sb.append("   /**\n");
      sb.append("    * The method ").append(REPLACEXMLTAG).append(" extracts the 'inner' area\n");
      sb.append("    * from <code>psText</code> and surrounds it with\n");
      sb.append("    * a <code>psToTag</code> tag.\n");
      sb.append("    *\n");
      sb.append("    * @param psFromTag This element has to be extracted\n");
      sb.append("    * @param psToTag The element has to 'frame' the\n");
      sb.append("    * result\n");
      sb.append("    * @param psText The response object which has\n");
      sb.append("    * to be analysed\n");
      sb.append("    * @return The modified result.\n");
      sb.append("    * @author _GENERATOR_\n");
      sb.append("    * @throws AgentException\n");
      sb.append("    */\n");
      sb.append("   private String ").append(REPLACEXMLTAG)
            .append("(String psFromTag, String psToTag, String psText)\n");
      sb.append("      throws AgentException\n");
      sb.append("   {\n");
      sb.append("      return ").append(REPLACEXMLTAG)
            .append("(psFromTag, psToTag, psText, true);\n");
      sb.append("   }\n");
      sb.append("\n");
      sb.append("   /**\n");
      sb.append("    * The private Method getStartTag gets the location of a Tag \n");
      sb.append("    * named in psType, which is within the response (psResponse)\n");
      sb.append("    *\n");
      sb.append("    * @param psResponse The responseString with the Elements\n");
      sb.append("    * @param psType The searchText of the tag\n");
      sb.append("    *\n");
      sb.append("    * @return\n");
      sb.append("    * @author _GENERATOR_\n");
      sb.append("    */\n");
      sb.append("   private int getStartTag(String psResponse, String psType)\n");
      sb.append("   {\n");
      sb.append("\n");
      sb.append("      // get the StartTag\n");
      sb.append("      int iStart = -1;\n");
      sb.append("\n");
      sb.append("      char[] cEndItems = \" />\".toCharArray();\n");
      sb.append("\n");
      sb.append("      for (int i = 0; (iStart < 0) && (i < cEndItems.length); i++) {\n");
      sb.append("         iStart = psResponse.indexOf(\"<\" + psType + cEndItems[i]);\n");
      sb.append("\n");
      sb.append("         if (iStart < 0) {\n");
      sb.append("            iStart = psResponse.indexOf(\":\" + psType + cEndItems[i]);\n");
      sb.append("         }\n");
      sb.append("      }\n");
      sb.append("\n");
      sb.append("      if (iStart >= 0) {\n");
      sb.append("         return psResponse.indexOf(\">\", iStart) + 1;\n");
      sb.append("      }\n");
      sb.append("\n");
      sb.append("      return iStart;\n");
      sb.append("   }\n");
      sb.append("\n");
      sb.append("   /**\n");
      sb.append("    * Method ").append(REPLACEXMLTAG).append("\n");
      sb.append("    * @param psType\n");
      sb.append("    * @param psFrame\n");
      sb.append("    * @param psResponse\n");
      sb.append("    * @param pbRecurse\n");
      sb.append("    * @return\n");
      sb.append("    * @throws AgentException\n");
      sb.append("    * @author _GENERATOR_\n");
      sb.append("    */\n");
      sb.append("   private String ").append(REPLACEXMLTAG)
            .append("(String psType, String psFrame, String psResponse,\n");
      sb.append("                             boolean pbRecurse)\n");
      sb.append("      throws AgentException\n");
      sb.append("   {\n");
      sb.append("\n");
      sb.append("      // get the StartTag\n");
      sb.append("      int iStart = getStartTag(psResponse, psType);\n");
      sb.append("\n");
      sb.append("      if (iStart < 0 && !Common.empty(psFrame)) {\n");
      sb.append("         // search if there is the Frame available, because this might also\n");
      sb.append("         // be returned.\n");
      sb.append("         psType = psFrame;\n");
      sb.append("         iStart = getStartTag(psResponse, psType);\n");
      sb.append("      }\n");
      sb.append("\n");
      sb.append("      // if the StartTag is found\n");
      sb.append("      if (iStart >= 0) {\n");
      sb.append("\n");
      sb.append("         // get the EndTag\n");
      sb.append("         int iEnd = psResponse.lastIndexOf(\"/\" + psType + \">\");\n");
      sb.append("\n");
      sb.append("         if (iEnd < 0) {\n");
      sb.append("            iEnd = psResponse.lastIndexOf(\":\" + psType + \">\");\n");
      sb.append("         }\n");
      sb.append("\n");
      sb.append("         if (iEnd > 0) {\n");
      sb.append("            iEnd = psResponse.lastIndexOf(\"<\", iEnd);\n");
      sb.append("         }\n");
      sb.append("\n");
      sb.append("         // ... there is a valid response\n");
      sb.append("         if (iEnd > iStart) {\n");
      sb.append("            psResponse = psResponse.substring(iStart, iEnd);\n");
      sb.append("         } else {\n");
      sb.append("            psResponse = \"\";\n");
      sb.append("         }\n");
      sb.append("\n");
      sb.append("         // \"frame\" the response\n");
      sb.append("         if (psFrame.length() > 0) {\n");
      sb.append("            return \"<\" + psFrame + \">\" + psResponse + \"</\" + psFrame + \">\";\n");
      sb.append("         }\n");
      sb.append("\n");
      sb.append("         return psResponse;\n");
      sb.append("      }\n");
      sb.append("\n");

      sb.append("      if (pbRecurse) {\n");
      sb.append("\n");

      for (Iterator<String> i = phsExceptions.iterator(); i.hasNext();) {
         String sNext = i.next();

         sb.append("         if (getStartTag(psResponse, \"").append(sNext).append("\") > 0) {\n");
         sb.append("            Object exceptionObject = CastorSerializer.unmarshalProviderResponse(\n");
         sb.append("               ").append(REPLACEXMLTAG).append("(\n");
         sb.append("               \"").append(sNext).append("\", \"").append(sNext)
               .append("\", psResponse, false),\n");
         sb.append("               ").append(sNext).append(".class, log, false);\n");
         sb.append("\n");
         sb.append("            throw new ").append(psProvider)
               .append("Exception(exceptionObject, log, false);\n");
         sb.append("\n");
         sb.append("         }\n");
      }

      sb.append("         if (getStartTag(psResponse, \"Fault\") > 0) {\n");
      sb.append("            String exceptionObject = ").append(REPLACEXMLTAG)
            .append("(\"Fault\", \"\", psResponse,\n");
      sb.append("                                              false);\n");
      sb.append("\n");
      sb.append("            throw new ").append(psProvider)
            .append("Exception(exceptionObject, log, true);\n");
      sb.append("         }\n");
      sb.append("         throw new ").append(psProvider)
            .append("Exception(psResponse, log, true);\n");

      sb.append("      }\n");
      sb.append("\n");
      sb.append("      throw new AgentException(AgentErrors.INVALID_RESPONSE,\n");
      sb.append("                               ResEnumErrorCategory.AGENT,\n");
      sb.append("                               ResEnumErrorComponent.ARCTIC_HTTP_SOAP, \"\",\n");
      sb.append("                               new Exception(\"Invalid Provider Response\"),\n");
      sb.append("                               log);\n");
      sb.append("\n");
      sb.append("\n");
      sb.append("   }\n");
      sb.append("\n");

      return sb.toString();
   }

   /** 
    * Method getInputWSDLObject has been copied from old arctic requester
    * 
    * @param pWsdl has been copied from old arctic requester
    * @param pDefinitions has been copied from old arctic requester
    * @param psMethod has been copied from old arctic requester
    * @param phsExceptions has been copied from old arctic requester
    * @return has been copied from old arctic requester
    * 
    * @author Andreas Brod 
    */
   private String loadWSDLObject(WsdlObject pWsdl, XmlObject pDefinitions, String psMethod,
                                 HashSet<String> phsExceptions)
   {
      String sRet = "";
      String sParams = "";

      // get portType and additional types (with schema)
      XmlObject[] portType = pDefinitions.getObjects("portType");
      XmlObject types = pDefinitions.createObject("types");

      // search through the protTypes
      for (int i = 0; i < portType.length; i++) {
         XmlObject operation = portType[i].findSubObject("operation", "name", psMethod);

         // if an operation is found ...
         if (operation != null) {

            String sMessageTns = operation.createObject("input").getAttribute("message");
            if (sMessageTns.indexOf(":") > 0) {
               sMessageTns = sMessageTns.substring(0, sMessageTns.indexOf(":") + 1);
            } else {
               sMessageTns = "";
            }

            // get the message for this operation
            String sMessageInput = ":" + operation.createObject("input").getAttribute("message");

            String sMessageOutput = ":" + operation.createObject("output").getAttribute("message");

            String sMessageFault = ":" + operation.createObject("fault").getAttribute("message");

            // get the soapAction (if neccessary)
            String soapAction =
               getSoapAction(pDefinitions, portType[i].getAttribute("name"), psMethod);

            // get the inputNameSpace
            String inputNameSpace =
               getInputNamespace(pDefinitions, portType[i].getAttribute("name"), psMethod);

            String inputPackage =
               getInputPackage(pDefinitions, portType[i].getAttribute("name"), psMethod);
            boolean bAddImport = false;

            String sOutputType = "";

            // if the is a message

            if (sMessageOutput.length() > 1) {

               // search a the related message-object
               XmlObject messageOutput =
                  pDefinitions.findSubObject("message", "name",
                        sMessageOutput.substring(sMessageOutput.lastIndexOf(":") + 1));

               String sOutputTypeOrg = sOutputType;

               if (messageOutput != null) {

                  // get the first response
                  XmlObject part = messageOutput.getObject("part");

                  if (part != null) {
                     sOutputType = part.getAttribute("type");

                     if (sOutputType.length() == 0) {
                        sOutputType = part.getAttribute("element");
                     }

                     if (sOutputType.indexOf(":") > 0) {
                        sOutputTypeOrg = sOutputType.substring(sOutputType.lastIndexOf(":") + 1);
                        sOutputType = Util.camelCase(sOutputTypeOrg);

                     } else {
                        sOutputType = Util.camelCase(sOutputType);
                     }

                     sOutputType = sOutputType + " " + part.getAttribute("name");
                  }
               }

               // search a the related message-object
               XmlObject messageInput =
                  pDefinitions.findSubObject("message", "name",
                        sMessageInput.substring(sMessageInput.lastIndexOf(":") + 1));

               // get the faultException
               XmlObject messageFault =
                  pDefinitions.findSubObject("message", "name",
                        sMessageFault.substring(sMessageFault.lastIndexOf(":") + 1));

               XmlObject binding =
                  pDefinitions.createObject("binding", "type",
                        sMessageTns + portType[i].getAttribute("name"), true);

               String sException = "";

               if (messageFault != null) {
                  XmlObject partFault = messageFault.getObject("part");

                  if (partFault.getAttribute("element").length() > 0) {
                     sException = partFault.getAttribute("element");
                  } else if (partFault.getAttribute("type").length() > 0) {
                     sException = partFault.getAttribute("type");
                  }

                  if (sException.indexOf(":") > 0) {
                     sException = sException.substring(sException.indexOf(":") + 1);
                  }

                  if (sException.length() > 0) {
                     phsExceptions.add(sException);
                  }
               }

               if (messageInput != null) {

                  // ... now we have a message object with input/output
                  XmlObject[] partInput = messageInput.getObjects("part");

                  sRet = "";

                  sParams = "";

                  boolean bIsSoapAction =
                     soapAction.length() > 0
                           && !binding.createObject("binding").getAttribute("style")
                                 .equalsIgnoreCase("rpc");

                  if (bIsSoapAction) {
                     sRet += "<!-- SOAPAction: " + soapAction + " -->";
                  }

                  // if there are no parts, there is a void method
                  if (partInput.length == 0) {

                     if (inputNameSpace.length() > 0) {
                        sRet += "<m:" + psMethod + " xmlns:m=\"" + inputNameSpace + "\"" + " />";
                     } else {
                        sRet += "<" + psMethod + " />";
                     }
                  }

                  List<String> listNames = new Vector<String>();
                  List<String> listTypes = new Vector<String>();
                  List<String> listClasses = new Vector<String>();

                  for (int j = 0; j < partInput.length; j++) {
                     String sName = partInput[j].getAttribute("name");
                     String sType = partInput[j].getAttribute("type");
                     String sElement = partInput[j].getAttribute("element");

                     if (sParams.length() > 0) {
                        sParams += ", ";
                     }

                     if (sType.length() == 0) {
                        sType = sElement;
                     }

                     if (sType.trim().length() > 0) {

                        // add the type to the java Source
                        if (sType.indexOf(":") > 0) {
                           sType = sType.substring(sType.lastIndexOf(":") + 1);
                        }

                        listTypes.add(sType);

                        sType = Util.camelCase(sType);

                        // correct types
                        if (sType.equalsIgnoreCase("int") || sType.equalsIgnoreCase("long")
                              || sType.equalsIgnoreCase("float")
                              || sType.equalsIgnoreCase("boolean")) {
                           sType = sType.toLowerCase();
                        }

                        sParams += sType + "1 ";

                        listClasses.add(sType);

                        // add the Name to the java Source
                        if (sName.indexOf(":") > 0) {
                           listNames.add(sName.substring(sName.lastIndexOf(":") + 1));
                        } else {
                           listNames.add(sName);
                        }

                        sParams += listNames.get(listNames.size() - 1);

                        if (sElement.length() > 0) {
                           sRet += pWsdl.getWsdlElement(sElement, types, false);
                        } else {
                           if (j == 0) {
                              sRet += "<m:" + psMethod;

                              if (inputNameSpace.length() > 0) {
                                 sRet += " xmlns:m=\"" + inputNameSpace + "\"";
                              }

                              sRet += ">";
                           }

                           pWsdl.searchTypes(partInput[j], null, types, "");

                           // sRet += part[j];
                           sRet += pWsdl.getElement(partInput[j], "", "");

                           if (j + 1 == partInput.length) {
                              sRet += "</m:" + psMethod + ">";
                           }
                        }
                     }
                  }

                  StringBuilder sbHeader = new StringBuilder();
                  sbHeader.append("   /**\n");

                  sbHeader.append("    * The method ").append(psMethod)
                        .append(" is automatically generated\n");
                  sbHeader.append("    * \n");

                  String psMethodJava = Util.camelCase(psMethod, false);

                  StringBuilder sbJava = new StringBuilder();

                  // create the JavaSourceCode
                  if (sOutputType.length() > 0) {
                     sbJava.append("   public ")
                           .append(sOutputType.substring(0, sOutputType.indexOf(" "))).append("1 ")
                           .append(psMethodJava).append("(").append(sParams)
                           .append(")\n      throws AgentException\n   {\n");

                  } else {
                     sbJava.append("   public void ").append(psMethodJava).append("(")
                           .append(sParams).append(")\n      throws AgentException\n   {\n");
                  }

                  sbJava.append("      log.fine(\"+++ Start ").append(psMethod)
                        .append(" +++\");\n");
                  sbJava.append(
                        "      LogTimer transactionTimer = log.getLogTimer(EnumLogTimer.Transaction.addText(\"")
                        .append(psMethod).append("\"));\n\n");

                  sbJava.append("      StringBuffer sbRequest = new StringBuffer();\n\n");

                  if (bIsSoapAction) {
                     sbJava.append("      setSOAPAction(\"").append(soapAction).append("\");\n\n");
                  } else {

                     // if there is no soapAction ... add the method
                     if (inputNameSpace.length() > 0) {
                        sbJava.append("      sbRequest.append(\"<m:").append(psMethod)
                              .append(" xmlns:m=\\\"").append(inputNameSpace).append("\\\">\");\n");
                     } else {
                        sbJava.append("      sbRequest.append(\"<m:").append(psMethod)
                              .append(">\");\n");
                     }
                  }

                  // add parameters
                  if (listNames.size() > 0) {
                     sbJava.append("\n      // add parameters\n\n");
                  } else {
                     sbJava.append("\n      // no parameters defined\n\n");
                  }

                  for (int iLst = 0; iLst < listNames.size(); iLst++) {
                     sbHeader.append("    * @param ").append(listNames.get(iLst))
                           .append(" parameter ").append(iLst + 1).append("\n");

                     String sClass = listClasses.get(iLst);
                     String sElement = listNames.get(iLst);

                     if (sClass.equalsIgnoreCase("String") || sClass.equalsIgnoreCase("int")
                           || sClass.equalsIgnoreCase("long") || sClass.equalsIgnoreCase("float")
                           || sClass.equalsIgnoreCase("boolean")) {
                        sbJava.append("      sbRequest.append(\"<").append(sElement)
                              .append("\" + _getParamType(\"").append(sClass.toLowerCase())
                              .append("\")+ \">\"+").append(sElement).append("+\"</")
                              .append(sElement).append(">\");\n");
                     } else {
                        bAddImport = true;
                        sbHeader.insert(0, "   private static Object _" + psMethodJava
                              + "Monitor = new Object();\n");

                        String sObj = "s" + Util.camelCase(listTypes.get(iLst));
                        sbJava.append("      // Convert Castor Object to String\n");
                        sbJava.append("      String ").append(sObj).append(" = \"\";\n");
                        sbJava.append("      synchronized (_").append(psMethodJava)
                              .append("Monitor) {\n");


                        sbJava.append("         ").append(sObj).append(" = marshal(")
                              .append(listNames.get(iLst)).append(");\n");

                        sbJava.append("      }\n");
                        if (!bIsSoapAction && !listTypes.get(iLst).equals(listNames.get(iLst))) {
                           sbJava.append("      // ").append(listTypes.get(iLst))
                                 .append(" has to be ").append(listNames.get(iLst))
                                 .append(" for the request\n");
                           sbJava.append("      ").append(sObj).append(" = ").append(REPLACEXMLTAG)
                                 .append("(\"").append(listTypes.get(iLst)).append("\", \"")
                                 .append(listNames.get(iLst)).append("\", ").append(sObj)
                                 .append(");\n");
                        }

                        sbJava.append("      sbRequest.append(").append(sObj).append(");\n");

                     }
                  }

                  // if there is no soapAction ... add the method
                  if (!bIsSoapAction) {
                     sbJava.append("\n      // close the method\n");
                     sbJava.append("      sbRequest.append(\"</m:").append(psMethod)
                           .append(">\");\n\n");
                  }

                  sbJava.append("\n      // get the Provider Response\n");

                  sbJava.append("      String sResponse = sendReceive(sbRequest.toString());\n\n");

                  if (sOutputType.length() > 0) {
                     sbHeader.append("    * @return response object\n");

                     // get the return value

                     String sClass = sOutputType.substring(0, sOutputType.indexOf(" "));

                     String sInner =
                        new StringBuilder().append(REPLACEXMLTAG).append("(\"")
                              .append(sOutputType.substring(sOutputType.indexOf(" ") + 1))
                              .append("\", \"\", sResponse)").toString();

                     if (sClass.equalsIgnoreCase("String")) {
                        sbJava.append("      String response = ").append(sInner).append(";\n");
                     } else if (sClass.equalsIgnoreCase("int")) {
                        sbJava.append("      int response = Integer.parseInt(").append(sInner)
                              .append(");\n");
                     } else if (sClass.equalsIgnoreCase("long")) {
                        sbJava.append("      long response = Long.parseLong(").append(sInner)
                              .append(");\n");
                     } else if (sClass.equalsIgnoreCase("float")) {
                        sbJava.append("      float response = Float.parseFloat(").append(sInner)
                              .append(");\n");
                     } else if (sClass.equalsIgnoreCase("boolean")) {
                        sbJava.append("      boolean response = Boolean.parseBoolean(")
                              .append(sInner).append(");\n");
                     } else {
                        sbJava.append("      sResponse = ").append(REPLACEXMLTAG).append("(\"")
                              .append(sOutputType.substring(sOutputType.indexOf(" ") + 1))
                              .append("\", \"").append(sOutputTypeOrg).append("\", sResponse);\n");

                        sbJava.append("      ").append(sClass).append("1 response = (")
                              .append(sClass).append("1) unmarshal(sResponse, ").append(sClass)
                              .append(".class);\n");
                     }

                     sbJava.append("      transactionTimer.flush();\n");
                     sbJava.append("      log.fine(\"+++ ").append(psMethod)
                           .append(" done +++\");\n");

                     sbJava.append("      return response;\n");
                  }
                  sbHeader.append("    * @throws AgentException\n");
                  sbHeader.append("    * @author _GENERATOR_\n");

                  sbHeader.append("    */\n");

                  sbJava.insert(0, sbHeader.toString());
                  sbJava.append("   }\n");

                  if ((inputPackage.length() > 0) && (sbJava.indexOf("import " + inputPackage) < 0)
                        && bAddImport) {
                     sbJava.insert(0, "import " + inputPackage + ".*;\n");
                  }

                  return sbJava.toString();
               }
            }
         } // endif for this method
      }

      return sParams;
   }

   /** 
    * has been copied from old arctic requester 
    * 
    * @param pXmlWsdl has been copied from old arctic requester
    * @param psBindingName has been copied from old arctic requester
    * @param psOperationName has been copied from old arctic requester
    * @return has been copied from old arctic requester
    * 
    * @author brod
    */
   private String getSoapAction(XmlObject pXmlWsdl, String psBindingName, String psOperationName)
   {
      XmlObject operation = getOperation(pXmlWsdl, psBindingName, psOperationName);

      if (operation != null) {
         String s = operation.createObject("operation").getAttribute("soapAction");

         if (s.endsWith("/") || s.endsWith("\\")) {
            s = s.substring(0, s.length() - 1);
         }

         return s;
      }

      return "";
   }


   /** 
    * Method getOperation  has been copied from old arctic requester 
    * 
    * @param pXmlWsdl 
    * @param psBindingName 
    * @param psOprationName 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private XmlObject getOperation(XmlObject pXmlWsdl, String psBindingName, String psOprationName)
   {
      XmlObject port =
         pXmlWsdl.createObject("service").findSubObject("port", "name", psBindingName);

      if (port == null) {
         XmlObject[] binding = pXmlWsdl.getObjects("binding");
         boolean ok = false;

         for (int i = 0; i < binding.length; i++) {
            String sType = binding[i].getAttribute("type");

            if (sType.equals(psBindingName) || sType.endsWith(":" + psBindingName)) {
               psBindingName = binding[i].getAttribute("name");
               ok = true;
            }
         }

         if (!ok) {
            return null;
         }
      } else {
         psBindingName = port.getAttribute("binding");
      }

      if (psBindingName.indexOf(":") > 0) {
         psBindingName = psBindingName.substring(psBindingName.indexOf(":") + 1);
      }

      XmlObject binding = pXmlWsdl.findSubObject("binding", "name", psBindingName);

      if (binding != null) {
         XmlObject operation = binding.findSubObject("operation", "name", psOprationName);

         return operation;
      }

      return null;
   }


   /** 
    * Method getInputNamespace  has been copied from old arctic requester
    * 
    * @param pXmlWsdl 
    * @param psBindingName 
    * @param psOprationName 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private String getInputNamespace(XmlObject pXmlWsdl, String psBindingName, String psOprationName)
   {
      XmlObject operation = getOperation(pXmlWsdl, psBindingName, psOprationName);

      if (operation != null) {
         String s = "";

         // try to get the NameSpace from the message
         XmlObject[] message = pXmlWsdl.getObjects("message");

         for (int i = 0; i < message.length; i++) {
            if (((s.length() == 0) && message[i].getAttribute("name").equals(psOprationName))
                  || message[i].getAttribute("name").equals(":" + psOprationName)) {
               s = message[i].createObject("part").getAttribute("partns");
            }
         }

         if (s.length() == 0) {

            // get nameSpace from Body
            s = operation.createObject("input").createObject("body").getAttribute("namespace");

            // if stiil empty get the default
            if (s.length() == 0) {
               s = pXmlWsdl.getAttribute("targetNamespace");
            }
         }

         if (s.endsWith("/") || s.endsWith("\\")) {
            s = s.substring(0, s.length() - 1);
         }

         return s;
      }

      return "";
   }

   /** 
    * Method getInputPackage has been copied from old arctic requester
    * 
    * @param pXmlWsdl 
    * @param psBindingName 
    * @param psOperationName 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private String getInputPackage(XmlObject pXmlWsdl, String psBindingName, String psOperationName)
   {
      String sNew = getInputNamespace(pXmlWsdl, psBindingName, psOperationName);

      if (sNew.length() == 0) {
         sNew = getSoapAction(pXmlWsdl, psBindingName, psOperationName);
      }

      return WsdlObject.url2ImportPath(sNew);
   }

   /**
    * Method writeBahnNvsCommunication
    *
    * @param psService
    * @param psJavaCommunication
    *
    * @author kaufmann
    */
   private void writeBahnNvsCommunication(String psService, String psJavaCommunication)
   {
      // get the imports
      String sImport = "";

      psJavaCommunication = encapsulateCommunication(psJavaCommunication, psService);

      String sFileNameJavaCommunication = _sJavaCommunication;
      String sSource = Util.loadFromFile(sFileNameJavaCommunication);

      if ((sSource.length() > 0) && (psJavaCommunication.length() > 0)) {

         String sPackage = "";

         while (psJavaCommunication.startsWith("import ")) {
            sImport = psJavaCommunication.substring(0, psJavaCommunication.indexOf("\n"));

            sPackage =
               new String(sImport.substring(sImport.indexOf(" ") + 1, sImport.lastIndexOf(".*")));

            psJavaCommunication =
               psJavaCommunication.substring(psJavaCommunication.indexOf("\n") + 1);

            if (sSource.indexOf(sImport) < 0) {
               sSource =
                  sSource.substring(0, sSource.indexOf("import")) + sImport + "\n"
                        + sSource.substring(sSource.indexOf("import"));
            }
         }

         sSource =
            Util.replaceString(sSource, "public Object sendReceive", "private Object sendReceive");

         sSource =
            Util.replaceString(sSource, "public String sendReceive", "private String sendReceive");

         psJavaCommunication =
            Util.replaceString(psJavaCommunication, "_GENERATOR_",
                  "generated by " + System.getProperty("user.name"));

         int iStart = -1;
         int iEnd = -1;

         Matcher matcherStart =
            Pattern.compile("(?m)^(.*// ---- START \\[" + psService + "\\] -+)$").matcher(sSource);
         if (matcherStart.find()) {
            iStart = matcherStart.start(1);
         }
         Matcher matcherEnd =
            Pattern.compile("(?m)^(.*// ---- END \\[" + psService + "\\] -+)$").matcher(sSource);
         if (matcherEnd.find()) {
            iEnd = matcherEnd.end(1);
         }

         boolean bAddLineBreak = false;
         if (iStart < 0) {
            iStart = sSource.lastIndexOf("}");
         }

         if (iEnd < 0) {
            iEnd = iStart;
            bAddLineBreak = true;
         }

         sSource =
            sSource.substring(0, iStart) + "   " + psJavaCommunication.trim()
                  + (bAddLineBreak ? "\n" : "") + sSource.substring(iEnd);

         Util.updateFile(sFileNameJavaCommunication, sSource, _sbResult);

      }
   }

   /**
    * Method getWsdlSource

    * @param sSource
    *
    * @return
    * @author Andreas Brod
    */
   private String encapsulateCommunication(String sSource, String psName)
   {

      // get the imports
      StringBuilder sbImport = new StringBuilder();

      while (sSource.startsWith("import ")) {
         sbImport.append(sSource.substring(0, sSource.indexOf("\n") + 1));

         sSource = sSource.substring(sSource.indexOf("\n") + 1);
      }

      // extract the monitors
      StringTokenizer st = new StringTokenizer(sSource, "\n\r");
      StringBuilder sbMonitors = new StringBuilder();
      StringBuilder sbJava = new StringBuilder();
      String sClassName = Util.camelCase(psName);
      sbJava.append("   public ").append(sClassName).append("Class ").append(psName)
            .append(" = new ").append(sClassName).append("Class();\n\n");
      sbJava.append("   public class ").append(sClassName).append("Class\n   {\n");

      addMarshalUnmarshalMethods(sbJava);

      while (st.hasMoreTokens()) {
         String sLine = st.nextToken();
         if (sLine.trim().startsWith("private static Object _")) {
            sbMonitors.append(sLine.replace("Monitor = new ", psName + "Monitor = new ")).append(
                  "\n");
         } else if (sLine.trim().matches("synchronized \\(_.*?Monitor\\) \\{")) {
            sbJava.append("   ").append(sLine.replaceFirst("Monitor\\)", psName + "Monitor)"))
                  .append("\n");
         } else {
            sbJava.append("   ").append(sLine).append("\n");
         }
      }
      sbJava.append("   }\n");

      StringBuilder sbHeader = new StringBuilder();
      sbHeader.append("   // The following lines are automatically generated with the\n");
      sbHeader.append("   // SchemaGenerator. Please do not edit any\n");
      sbHeader.append("   // lines below, because these are deleted. If you want to add\n");
      sbHeader.append("   // some code, please use the lines above.\n");
      sbHeader.append("   // ------------------------------------------------------------\n");
      sbHeader.append("   // URL: ").append(_sWsdlFile.replaceAll("\\\\", "/"))
            .append("\n   // DATE: ");
      sbHeader.append((new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()))).append("\n");

      StringBuilder sbInterfaceName = new StringBuilder(50);
      sbInterfaceName.append(" [").append(psName).append("] -");
      while (sbInterfaceName.length() < 50) {
         sbInterfaceName.append("-");
      }
      StringBuilder sbSource = new StringBuilder();
      sbSource.append("// ---- START").append(sbInterfaceName).append("\n").append(sbHeader)
            .append(sbMonitors).append(sbJava);
      sbSource.append("   // ---- END").append(sbInterfaceName).append("--\n");

      return sbImport.append(sbSource).toString();
   }

   /**
    * Method addMarshalUnmarshalMethods adds methods to the communication object, which encapsulate
    * the Common methods for marshalling and unmarshalling castor objects
    *
    * @param psbJava the java code to enhance
    *
    * @author kaufmann
    */
   private void addMarshalUnmarshalMethods(StringBuilder psbJava)
   {
      psbJava.append("\n");
      psbJava.append("      /**\n");
      psbJava
            .append("       * Method marshal encapsulates the marshalling by the Common method.\n");
      psbJava
            .append("       * This method has been generated by the schema generator, do not change!\n");
      psbJava.append("       *\n");
      psbJava.append("       * @param pObjectToMarshal object which should be marshalled\n");
      psbJava.append("       * @return marshalled object (XML-String)\n");
      psbJava.append("       * @throws AgentException\n");
      psbJava.append("       *\n");
      psbJava.append("       * @author kaufmann\n");
      psbJava.append("       */\n");
      psbJava.append("      private String marshal(Object pObjectToMarshal)\n");
      psbJava.append("          throws AgentException\n");
      psbJava.append("       {\n");
      psbJava
            .append("          return CastorSerializer.marshalProviderRequest(pObjectToMarshal, false, null, null,\n");
      psbJava.append("                _bValidateRequest, log, true);\n");
      psbJava.append("       }\n");
      psbJava.append("\n");
      psbJava.append("      /**\n");
      psbJava
            .append("       * Method unmarshal encapsulates the unmarshalling by the Common method.\n");
      psbJava
            .append("       * This method has been generated by the schema generator, do not change!\n");
      psbJava.append("       *\n");
      psbJava.append("       * @param psXml the xml which should be converted to an object\n");
      psbJava.append("       * @param pClass the new object's class\n");
      psbJava.append("       * @return unmarshalled object of class pClass\n");
      psbJava.append("       * @throws AgentException\n");
      psbJava.append("       *\n");
      psbJava.append("       * @author kaufmann\n");
      psbJava.append("       */\n");
      psbJava.append("      private Object unmarshal(String psXml, Class<?> pClass)\n");
      psbJava.append("         throws AgentException\n");
      psbJava.append("      {\n");
      psbJava
            .append("         return CastorSerializer.unmarshalProviderResponse(psXml, pClass, log,\n");
      psbJava.append("               _bValidateResponse, true, true, true);\n");
      psbJava.append("      }\n\n\n");
   }


}
