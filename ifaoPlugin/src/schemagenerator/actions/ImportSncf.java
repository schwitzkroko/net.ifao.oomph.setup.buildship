package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;
import java.net.*;
import java.util.*;

import schemagenerator.gui.*;

import net.ifao.xml.*;


/**
 * Class ImportSncf
 *
 * <p>
 * Copyright &copy; 2008, i:FAO Group GmbH
 * @author kaufmann
 */
public class ImportSncf
{
   private String _sLastError = null;
   private final String _sURL_SNCF;
   private final String _sJavaCommunication;
   private final String _sBaseDir;
   private static final String REPLACEXMLTAG = "_replaceXmlTag";
   private StringBuilder _sbResult = null;

   private String _sProviderdataBaseDir;

   /**
    * Constructor ImportSncf
    *
    * @param psURL_SNCF URL of the server on which the WSDLs for the services can be found
    * @param psJavaCommunication base name of the java communication file
    * @param psBaseDir arctic base dir
    *
    * @author kaufmann
    */
   public ImportSncf(String psURL_SNCF, String psJavaCommunication, String psBaseDir)
   {
      if (!(new File(psJavaCommunication)).exists()) {
         _sLastError = "ERROR JavaFile not existent\n" + psJavaCommunication;
      }
      _sURL_SNCF = psURL_SNCF;
      _sJavaCommunication = psJavaCommunication;
      // cut off a backslash at the end
      if (psBaseDir.endsWith("\\") || psBaseDir.endsWith("/")) {
         psBaseDir = psBaseDir.substring(0, psBaseDir.length() - 1);
      }
      _sBaseDir = psBaseDir.replace('\\', '/');
      _sProviderdataBaseDir = Util.getProviderDataRootDirectory(_sBaseDir, "com/vsct/wdi");
      if (!_sProviderdataBaseDir.endsWith("\\") && !_sProviderdataBaseDir.endsWith("/")) {
         _sProviderdataBaseDir += "/";
      }
      _sbResult = new StringBuilder();
      _sbResult.append("Generating castorbuilder.properties files besides the schemas.\n");
      _sbResult.append("Java files will not be generated\n\n");
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
    * Method startGeneration generates the classes for all webservices available from SNCF
    * These are defined in SwtSncf.RIVA_SERVICES
    *
    * @author kaufmann
    */
   public void startGeneration()
   {
      Map<String, SchemaData> schemaDatas = new HashMap<>();

      for (String sService : SwtSncf.RIVA_SERVICES) {
         generateService(sService, schemaDatas);
      }

      if (_sbResult.indexOf("* ") >= 0) {
         _sbResult.append("_______________________________________\n\n");
         _sbResult.append("\nFiles/directories marked with * have to be submitted (in CVS) !\n");
      }

   }


   /**
    * Method generateService generates the classes for a specific web service
    *
    * @param psService service to create the classes for, e.g. AQ, BA, ...
    * @param pSchemaDatas map containing the schema data (e.g. path, schema, package,...) for a namespace
    *
    * @author kaufmann
    */
   private void generateService(String psService, Map<String, SchemaData> pSchemaDatas)
   {
      _sbResult.append("Working on service ").append(psService).append("\n");

      XmlObject wsdl = downloadWsdl(psService);
      String sWsdl = wsdl.toString();
      String sWsdlNamespace = wsdl.getAttribute("targetNamespace");

      XmlObject[] schemas = wsdl.createObject("types").getObjects("schema");

      for (XmlObject schema : schemas) {
         downloadImports(schema, pSchemaDatas);
      }

      wsdl.getObject("types").deleteObjects("schema");
      for (SchemaData schemaData : pSchemaDatas.values()) {
         wsdl.getObject("types").addObject(schemaData._schema);
      }
      String sDataBinding = WsdlObject.getDataBinding(wsdl, null, "", false, false);
      sDataBinding = addPackageInfos(sDataBinding, pSchemaDatas.values());

      for (SchemaData schemaData : pSchemaDatas.values()) {
         Util.updateFile(schemaData._sSchemaFile, schemaData._schema.toString(), _sbResult);
         Util.updateFile(schemaData._sSchemaFile.replaceFirst("data.xsd", "dataBinding.xml"),
               sDataBinding, _sbResult);
      }

      updateJavaCommunication(psService, sWsdl);
   }

   /**
    * Method updateJavaCommunication creates the java communication class for the webservice passed
    *
    * @param psService WebService code, e.g. AQ, BA, ...
    * @param psWsdl WSDL of the WebService
    *
    * @author kaufmann
    */
   private void updateJavaCommunication(String psService, String psWsdl)
   {
      String sJavaCommunication = createJavaCommunication(psWsdl);
      writeSncfCommunication(psService, sJavaCommunication);
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

      String sImport = WsdlObject.url2ImportPath(definitions.getAttribute("targetNamespace"));

      XmlObject schema = definitions.createObject("types").createObject("schema");

      if (sImport.length() == 0) {
         String nameSpace = schema.getNameSpace();

         sImport = WsdlObject.url2ImportPath(schema.getAttribute(nameSpace));
      }

      if (schema.getObjects("").length == 0) {
         sImport = "";
      }

      if (sImport.length() > 0) {
         sImport = "import " + sImport + ".*;\n";
      }

      StringBuffer sbJava = new StringBuffer();
      HashSet<String> hsExceptions = new HashSet<String>();

      for (int i = 0; i < operation.length; i++) {
         String sOperation = operation[i].getAttribute("name");

         String sJavaOperation = loadWSDLObject(wsdl, definitions, sOperation, hsExceptions);

         while (sJavaOperation.startsWith("import ")) {

            // validate import
            if (sImport.length() == 0) {
               sImport = sJavaOperation.substring(0, sJavaOperation.indexOf("\n") + 1);
            }

            // trunc Import from response
            sJavaOperation = sJavaOperation.substring(sJavaOperation.indexOf("\n") + 1);
         }

         // add Java operation
         sbJava.append(sJavaOperation + "\n");
      }

      if ((sImport.length() > 0) && (sbJava.indexOf(sImport) < 0)) {
         sbJava.insert(0, sImport);
      }

      if (sbJava.indexOf(" _getParamType(") > 0) {
         sbJava.append(getParamTypeMethod());
      }

      sbJava.append(getInnerMethod(hsExceptions, "Sncf"));

      return sbJava.toString();
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

                        sParams += sType + " ";

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

                  String sHeader = "    /**\n";

                  sHeader += "    * The method " + psMethod + " is automatically generated\n";
                  sHeader += "    * \n";

                  String psMethodJava = Util.camelCase(psMethod, false);

                  String sJava = "";

                  // create the JavaSourceCode
                  if (sOutputType.length() > 0) {
                     sJava =
                        "    public " + sOutputType.substring(0, sOutputType.indexOf(" ")) + " "
                              + psMethodJava + "(" + sParams
                              + ")\n        throws AgentException {\n";

                  } else {
                     sJava =
                        "    public void " + psMethodJava + "(" + sParams
                              + ")\n        throws AgentException {\n";
                  }

                  sJava += "        StringBuffer sbRequest = new StringBuffer();\n\n";

                  if (bIsSoapAction) {
                     sJava += "        setSOAPAction(\"" + soapAction + "\");\n\n";
                  } else {

                     // if there is no soapAction ... add the method
                     if (inputNameSpace.length() > 0) {
                        sJava +=
                           "        sbRequest.append(\"<m:" + psMethod + " xmlns:m=\\\""
                                 + inputNameSpace + "\\\">\");\n";
                     } else {
                        sJava += "        sbRequest.append(\"<m:" + psMethod + ">\");\n";
                     }
                  }

                  // add parameters
                  if (listNames.size() > 0) {
                     sJava += "\n        // add parameters\n\n";
                  } else {
                     sJava += "\n        // no parameters defined\n\n";
                  }

                  for (int iLst = 0; iLst < listNames.size(); iLst++) {
                     sHeader +=
                        "    * @param " + listNames.get(iLst) + " parameter " + (iLst + 1) + "\n";

                     String sClass = listClasses.get(iLst);
                     String sElement = listNames.get(iLst);

                     if (sClass.equalsIgnoreCase("String") || sClass.equalsIgnoreCase("int")
                           || sClass.equalsIgnoreCase("long") || sClass.equalsIgnoreCase("float")
                           || sClass.equalsIgnoreCase("boolean")) {
                        sJava +=
                           "        sbRequest.append(\"<" + sElement + "\" + _getParamType(\""
                                 + sClass.toLowerCase() + "\")+ \">\"+" + sElement + "+\"</"
                                 + sElement + ">\");\n";
                     } else {
                        bAddImport = true;
                        sHeader =
                           "    private static Object _" + psMethodJava
                                 + "Monitor = new Object();\n" + sHeader;
                        sJava += "        synchronized(_" + psMethodJava + "Monitor) {\n";

                        String sObj = "s" + Util.camelCase(listTypes.get(iLst));

                        sJava += "            // Convert Castor Object to String\n";
                        sJava +=
                           "            String " + sObj
                                 + " = CastorSerializer.marshalProviderRequest("
                                 + listNames.get(iLst) + ", false,\n";
                        sJava +=
                           "                                               "
                                 + "null, null, _bValidateRequest, log);\n";

                        if (!bIsSoapAction && !listTypes.get(iLst).equals(listNames.get(iLst))) {
                           sJava +=
                              "            // " + listTypes.get(iLst) + " has to be "
                                    + listNames.get(iLst) + " for the request\n";
                           sJava +=
                              "            " + sObj + " = " + REPLACEXMLTAG + "(\""
                                    + listTypes.get(iLst) + "\", \"" + listNames.get(iLst) + "\", "
                                    + sObj + ");\n";
                        }

                        sJava += "            sbRequest.append(" + sObj + ");\n";

                        sJava += "        }\n";
                     }
                  }

                  // if there is no soapAction ... add the method
                  if (!bIsSoapAction) {

                     sJava += "\n        // close the method\n";
                     sJava += "        sbRequest.append(\"</m:" + psMethod + ">\");\n\n";
                  }

                  sJava += "\n        // get the Provider Response\n";

                  sJava += "        String sResponse = sendReceive(sbRequest.toString());\n\n";

                  if (sOutputType.length() > 0) {
                     sHeader += "    * @return response object\n";

                     // get the return value

                     String sClass = sOutputType.substring(0, sOutputType.indexOf(" "));

                     String sInner =
                        "" + REPLACEXMLTAG + "(\""
                              + sOutputType.substring(sOutputType.indexOf(" ") + 1)
                              + "\", \"\", sResponse)";

                     if (sClass.equalsIgnoreCase("String") || sClass.equals("string")) {
                        sJava += "        return " + sInner + ";\n";
                     } else if (sClass.equalsIgnoreCase("int")) {
                        sJava += "        return Integer.parseInt(" + sInner + ");\n";
                     } else if (sClass.equalsIgnoreCase("long")) {
                        sJava += "        return Long.parseLong(" + sInner + ");\n";
                     } else if (sClass.equalsIgnoreCase("float")) {
                        sJava += "        return Float.parseFloat(" + sInner + ");\n";
                     } else if (sClass.equalsIgnoreCase("boolean")) {
                        sJava += "        return Boolean.parseBoolean(" + sInner + ");\n";
                     } else {
                        sInner =
                           "" + REPLACEXMLTAG + "(\""
                                 + sOutputType.substring(sOutputType.indexOf(" ") + 1) + "\", \""
                                 + sOutputTypeOrg + "\", sResponse)";

                        sJava +=
                           "        return (" + sClass
                                 + ") CastorSerializer.unmarshalProviderResponse(\n               "
                                 + sInner + ",\n               " + sClass
                                 + ".class, log, _bValidateResponse);\n";
                     }

                  }
                  sHeader += "    * @throws AgentException\n";
                  sHeader += "    * @author _GENERATOR_\n";

                  sHeader += "    */\n";

                  sJava = sHeader + sJava + "    }\n";

                  if ((inputPackage.length() > 0) && (sJava.indexOf("import " + inputPackage) < 0)
                        && bAddImport) {
                     sJava = "import " + inputPackage + ".*;\n" + sJava + "";
                  }

                  return sJava;
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

      sb.append("    /**\n");
      sb.append("     * The method " + WsdlObject.REPLACEXMLTAG + " extracts the 'inner' area\n");
      sb.append("     * from <code>psText</code> and surrounds it with\n");
      sb.append("     * a <code>psToTag</code> tag.\n");
      sb.append("     *\n");
      sb.append("     * @param psFromTag This element has to be extracted\n");
      sb.append("     * @param psToTag The element has to 'frame' the\n");
      sb.append("     * result\n");
      sb.append("     * @param psText The response object which has\n");
      sb.append("     * to be analysed\n");
      sb.append("     * @return The modified result.\n");
      sb.append("     * @author _GENERATOR_\n");
      sb.append("     * @throws AgentException\n");
      sb.append("     */\n");
      sb.append("    private String " + WsdlObject.REPLACEXMLTAG
            + "(String psFromTag, String psToTag, String psText)\n");
      sb.append("        throws AgentException\n");
      sb.append("    {\n");
      sb.append("        return " + WsdlObject.REPLACEXMLTAG
            + "(psFromTag, psToTag, psText, true);\n");
      sb.append("    }\n");
      sb.append("\n");
      sb.append("    /**\n");
      sb.append("     * The private Method getStartTag gets the location of a Tag \n");
      sb.append("     * named in psType, which is within the response (psResponse)\n");
      sb.append("     *\n");
      sb.append("     * @param psResponse The responseString with the Elements\n");
      sb.append("     * @param psType The searchText of the tag\n");
      sb.append("     *\n");
      sb.append("     * @return\n");
      sb.append("     * @author _GENERATOR_\n");
      sb.append("     */\n");
      sb.append("    private int getStartTag(String psResponse, String psType)\n");
      sb.append("    {\n");
      sb.append("\n");
      sb.append("        // get the StartTag\n");
      sb.append("        int iStart = -1;\n");
      sb.append("\n");
      sb.append("        char[] cEndItems = \" />\".toCharArray();\n");
      sb.append("\n");
      sb.append("        for (int i = 0; (iStart < 0) && (i < cEndItems.length); i++) {\n");
      sb.append("            iStart = psResponse.indexOf(\"<\" + psType + cEndItems[i]);\n");
      sb.append("\n");
      sb.append("            if (iStart < 0) {\n");
      sb.append("                iStart = psResponse.indexOf(\":\" + psType + cEndItems[i]);\n");
      sb.append("            }\n");
      sb.append("        }\n");
      sb.append("\n");
      sb.append("        if (iStart >= 0) {\n");
      sb.append("            return psResponse.indexOf(\">\", iStart) + 1;\n");
      sb.append("        }\n");
      sb.append("\n");
      sb.append("        return iStart;\n");
      sb.append("    }\n");
      sb.append("\n");
      sb.append("    /**\n");
      sb.append("     * Method " + WsdlObject.REPLACEXMLTAG + "\n");
      sb.append("     * @param psType\n");
      sb.append("     * @param psFrame\n");
      sb.append("     * @param psResponse\n");
      sb.append("     * @param pbRecurse\n");
      sb.append("     * @return\n");
      sb.append("     * @throws AgentException\n");
      sb.append("     * @author _GENERATOR_\n");
      sb.append("     */\n");
      sb.append("    private String " + WsdlObject.REPLACEXMLTAG
            + "(String psType, String psFrame, String psResponse,\n");
      sb.append("                             boolean pbRecurse)\n");
      sb.append("        throws AgentException\n");
      sb.append("    {\n");
      sb.append("\n");
      sb.append("        // get the StartTag\n");
      sb.append("        int iStart = getStartTag(psResponse, psType);\n");
      sb.append("\n");
      sb.append("        if (iStart < 0 && !Common.empty(psFrame)) {\n");
      sb.append("            // search if there is the Frame available, because this might also\n");
      sb.append("            // be returned.\n");
      sb.append("            psType = psFrame;\n");
      sb.append("            iStart = getStartTag(psResponse, psType);\n");
      sb.append("        }\n");
      sb.append("\n");
      sb.append("        // if the StartTag is found\n");
      sb.append("        if (iStart >= 0) {\n");
      sb.append("\n");
      sb.append("            // get the EndTag\n");
      sb.append("            int iEnd = psResponse.lastIndexOf(\"/\" + psType + \">\");\n");
      sb.append("\n");
      sb.append("            if (iEnd < 0) {\n");
      sb.append("                iEnd = psResponse.lastIndexOf(\":\" + psType + \">\");\n");
      sb.append("            }\n");
      sb.append("\n");
      sb.append("            if (iEnd > 0) {\n");
      sb.append("                iEnd = psResponse.lastIndexOf(\"<\", iEnd);\n");
      sb.append("            }\n");
      sb.append("\n");
      sb.append("            // ... there is a valid response\n");
      sb.append("            if (iEnd > iStart) {\n");
      sb.append("                psResponse = psResponse.substring(iStart, iEnd);\n");
      sb.append("            } else {\n");
      sb.append("                psResponse = \"\";\n");
      sb.append("            }\n");
      sb.append("\n");
      sb.append("            // \"frame\" the response\n");
      sb.append("            if (psFrame.length() > 0) {\n");
      sb.append("                return \"<\" + psFrame + \">\" + psResponse + \"</\" + psFrame + \">\";\n");
      sb.append("            }\n");
      sb.append("\n");
      sb.append("            return psResponse;\n");
      sb.append("        }\n");
      sb.append("\n");

      sb.append("        if (pbRecurse) {\n");
      sb.append("\n");

      for (Iterator<String> i = phsExceptions.iterator(); i.hasNext();) {
         String sNext = i.next();

         sb.append("            if (getStartTag(psResponse, \"" + sNext + "\") > 0) {\n");
         sb.append("                Object exceptionObject = CastorSerializer.unmarshalProviderResponse(\n");
         sb.append("                    " + WsdlObject.REPLACEXMLTAG + "(\n");
         sb.append("                    \"" + sNext + "\", \"" + sNext
               + "\", psResponse, false),\n");
         sb.append("                    " + sNext + ".class, log, false);\n");
         sb.append("\n");
         sb.append("                throw new " + psProvider
               + "Exception(exceptionObject, log, false);\n");
         sb.append("\n");
         sb.append("            }\n");
      }

      sb.append("            if (getStartTag(psResponse, \"Fault\") > 0) {\n");
      sb.append("                String exceptionObject = " + WsdlObject.REPLACEXMLTAG
            + "(\"Fault\", \"\", psResponse,\n");
      sb.append("                                                   false);\n");
      sb.append("\n");
      sb.append("                throw new " + psProvider
            + "Exception(exceptionObject, log, true);\n");
      sb.append("            }\n");
      sb.append("            throw new " + psProvider + "Exception(psResponse, log, true);\n");

      sb.append("        }\n");
      sb.append("\n");
      sb.append("        throw new AgentException(AgentErrors.INVALID_RESPONSE,\n");
      sb.append("                                 ResEnumErrorCategory.AGENT,\n");
      sb.append("                                 ResEnumErrorComponent.ARCTIC_HTTP_SOAP, \"\",\n");
      sb.append("                                 new Exception(\"Invalid Provider Response\"),\n");
      sb.append("                                 log);\n");
      sb.append("\n");
      sb.append("\n");
      sb.append("    }\n");
      sb.append("\n");

      return sb.toString();
   }

   /**
    * Method writeSncfCommunication
    *
    * @param psService
    * @param psJavaCommunication
    *
    * @author kaufmann
    */
   private void writeSncfCommunication(String psService, String psJavaCommunication)
   {
      // get the imports
      String sImport = "";

      while (psJavaCommunication.startsWith("import ")) {
         sImport += psJavaCommunication.substring(0, psJavaCommunication.indexOf("\n") + 1);

         psJavaCommunication = psJavaCommunication.substring(psJavaCommunication.indexOf("\n") + 1);
      }

      psJavaCommunication =
         "// -----------------------------------------------------------\n" + psJavaCommunication;
      psJavaCommunication = "// some code, please use the lines above.\n" + psJavaCommunication;
      psJavaCommunication =
         "// lines below, because these are deleted. If you want to add\n" + psJavaCommunication;
      psJavaCommunication =
         "// ArcticRequester generation menu. Please do not edit any\n" + psJavaCommunication;
      psJavaCommunication =
         "// The following lines are automatically generated with the\n" + psJavaCommunication;
      psJavaCommunication =
         "// ---- START ------------------------------------------------\n" + psJavaCommunication;
      psJavaCommunication += "// ---- END --------------------------------------------------\n";
      psJavaCommunication += "\n}\n";

      String sFileNameJavaCommunication = getJavaCommunicationName(_sJavaCommunication, psService);
      String sSource = Util.loadFromFile(sFileNameJavaCommunication);

      if ((sSource.length() > 0) && (psJavaCommunication.length() > 0)) {

         String sLastSource = sSource;

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

         int iStart = sSource.lastIndexOf("// ---- START -");

         if (iStart < 0) {
            iStart = sSource.lastIndexOf("}");
         }

         sSource = sSource.substring(0, iStart) + psJavaCommunication;

         Util.updateFile(sFileNameJavaCommunication, sSource, _sbResult);

      }
   }

   /**
    * Method getJavaCommunicationName creates the name for the specific communication class of
    * a WebService. 
    * The code of the WebService will be added to the base filename, e.g.:<br>
    * base filename: c:\JavaComm.java<br>
    * WebService: BA<br>
    * Resulting filename: c:\JavaCommBA.java 
    *
    * @param psCommunicationBase base filename of the java communication class
    * @param psService WebService code, e.g. AQ, BA, ...
    * @return name for the specific communication class
    *
    * @author kaufmann
    */
   public static String getJavaCommunicationName(String psCommunicationBase, String psService)
   {
      String sFileName = psCommunicationBase;
      String sExtension = "";
      int iDotPos = sFileName.lastIndexOf('.');
      if (iDotPos >= 0) {
         sExtension = sFileName.substring(iDotPos);
         sFileName = sFileName.substring(0, iDotPos);
      }

      return new StringBuilder().append(sFileName).append(psService).append(sExtension).toString();
   }


   /**
    * Adds the package informations to the binding file
    *
    * @param psDataBinding original binding file
    * @param pSchemaDatas collection of SchemaData elements
    *
    * @author kaufmann
    * @return enhanced dataBinding xml
    */
   private String addPackageInfos(String psDataBinding, Collection<SchemaData> pSchemaDatas)
   {
      XmlObject dataBinding = new XmlObject(psDataBinding).getFirstObject();
      int iPos = 0;

      for (SchemaData schemaData : pSchemaDatas) {
         XmlObject name = new XmlObject("<name/>").getFirstObject();
         name.setCData(schemaData._sPackage);
         XmlObject namespace = new XmlObject("<namespace/>").getFirstObject();
         namespace.setCData(schemaData._sNamespace);

         XmlObject packageBinding = new XmlObject("<package/>").getFirstObject();
         packageBinding.addElementObject(name);
         packageBinding.addElementObject(namespace);

         dataBinding.addElementObject(packageBinding, iPos++);
      }
      return dataBinding.toString();
   }

   /**
    * Recursively downloads the imported schemas and adds appropriate SchemaData elements to the
    * map.
    *
    * @param pSchema schema containing the imports
    * @param pSchemas Map containing the already downloaded schema namespaces
    *
    * @author kaufmann
    */
   private void downloadImports(XmlObject pSchema, Map<String, SchemaData> pSchemas)
   {
      XmlObject[] imports = pSchema.getObjects("import");
      for (XmlObject importObject : imports) {
         String sNamespace = importObject.getAttribute("namespace");
         String sSchemaLocation = importObject.getAttribute("schemaLocation");
         if (sSchemaLocation != null && sSchemaLocation.matches("https?://.*")
               && !pSchemas.containsKey(sNamespace)) {
            // download the new schema
            SchemaData schemaData = new SchemaData(sNamespace, sSchemaLocation);
            pSchemas.put(sNamespace, schemaData);

            downloadImports(schemaData._schema, pSchemas);

            fixImports(schemaData._schema);
         }
      }
   }


   /**
    * Corrects the schema locations of the imports
    *
    * @param pSchema schema containing the imports to fix
    *
    * @author kaufmann
    */
   private void fixImports(XmlObject pSchema)
   {
      String sSchemaNamespace = pSchema.getAttribute("targetNamespace");
      XmlObject[] imports = pSchema.getObjects("import");
      for (XmlObject importObject : imports) {
         String sNamespace = importObject.getAttribute("namespace");
         String sSchemaLocation = importObject.getAttribute("schemaLocation");
         if (sSchemaLocation != null && sSchemaLocation.matches("https?://.*")) {
            importObject.setAttribute("schemaLocation",
                  getRelativePathFromNamespace(sSchemaNamespace, sNamespace)
                        .replaceAll("\\\\", "/") + "data.xsd");
         }
      }
   }

   /**
    * Downloads the WSDL of the specified service
    *
    * @param psService service, e.g. AQ, BA,...
    * @return wsdl object of the service
    *
    * @author kaufmann
    */
   private XmlObject downloadWsdl(String psService)
   {
      String sWsdlUrl = _sURL_SNCF + "/" + psService + "?wsdl";
      String sWsdl = UtilSwt.loadFromURL(sWsdlUrl);
      if (sWsdl == null || sWsdl.trim().length() == 0) {
         _sLastError = "Error retrieving " + sWsdlUrl;
         return null;
      }

      XmlObject wsdl = new XmlObject(sWsdl).getFirstObject();
      String sWsdlNamespace = wsdl.getAttribute("targetNamespace");
      String sWsdlPath = _sProviderdataBaseDir + getPathFromNamespace(sWsdlNamespace);


      sWsdl += "\n<!-- Taken from \"" + sWsdlUrl + "\" -->";
      if (!Util.updateFile(sWsdlPath + "data.wsdl", sWsdl, _sbResult)) {
         _sbResult.append("\nWSDL has not been changed!\n");
      }
      return wsdl;

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
      String[] sHostParts = uri.getHost().split("[.-]");
      for (int i = sHostParts.length - 1; i >= 0; i--) {
         sbPath.append(sHostParts[i]).append("/");
      }
      String[] sPathParts = uri.getPath().split("[\\\\/]");
      for (String sPathPart : sPathParts) {
         if (sPathPart.length() > 0) {
            sbPath.append(sPathPart).append("/");
         }
      }
      return sbPath.toString().toLowerCase();
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
    * Class SchemaData holds different data of a schema, like Namespace, schema xml, package,...
    *
    * <p>
    * Copyright &copy; 2013, i:FAO Group GmbH
    * @author kaufmann
    */
   private class SchemaData
   {
      String _sNamespace;
      String _sSchemaLocation;
      XmlObject _schema;
      String _sSchemaFile;
      String _sPackage;

      private SchemaData(String psNamespace, String psSchemaLocation)
      {
         _sNamespace = psNamespace;
         _sSchemaLocation = psSchemaLocation;
         _schema = new XmlObject(UtilSwt.loadFromURL(psSchemaLocation)).getFirstObject();
         _sSchemaFile = _sProviderdataBaseDir + getPathFromNamespace(psNamespace) + "data.xsd";
         _sPackage = getPackageFromNamespace(psNamespace);
      }

      private String getPackageFromNamespace(String psNamespace)
      {
         String sPackage = getPathFromNamespace(psNamespace).replaceAll("[\\\\/]", ".");
         if (sPackage.endsWith(".")) {
            sPackage = sPackage.substring(0, sPackage.length() - 1);
         }
         return sPackage;
      }

   }

}
