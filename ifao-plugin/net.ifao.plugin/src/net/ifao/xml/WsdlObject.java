package net.ifao.xml;


import ifaoplugin.*;

import java.io.*;
import java.net.URL;
import java.util.*;

import net.ifao.util.CorrectDatabindingXsd;


/** 
 * Class WsdlObject 
 * 
 * <p> 
 * Copyright &copy; 2002, i:FAO 
 * @author Andreas Brod 
 */
public class WsdlObject
{
   String sPath;
   public static final String REPLACEXMLTAG = "_replaceXmlTag";

   /** 
    * Constructor WsdlObject 
    * 
    * @param psPath 
    * 
    */
   public WsdlObject(String psPath)
   {
      int i = Math.max(psPath.lastIndexOf("\\"), psPath.lastIndexOf("/"));

      if (i > 0) {
         sPath = psPath.substring(0, i + 1);
      } else {
         sPath = psPath;
      }

   }

   /** 
    * Constructor WsdlObject 
    * 
    * @param pFile Java object of the wsdl file 
    * 
    * @author brod 
    */
   public WsdlObject(File pFile)
   {
      this(pFile.getAbsolutePath());

   }

   static Hashtable<String, XmlObject> htWsdl = new Hashtable<String, XmlObject>();

   /** 
    * Method getWSDLObject 
    * 
    * 
    * 
    * @param psIp 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public static XmlObject getWSDLObjectIp(String psIp)
   {
      String sWsdlFileName = "data\\Ip\\" + psIp + "\\data.wsdl";

      if (htWsdl.get(sWsdlFileName) != null) {
         return htWsdl.get(sWsdlFileName);
      }

      try {
         XmlObject wsdl = new XmlObject(new File(sWsdlFileName));

         XmlObject fileName = wsdl.getObject("!--FILE");

         wsdl = wsdl.createObject("definitions");

         if (fileName != null) {
            wsdl.setAttribute("fileName", fileName.getAttribute("name"));
         }

         htWsdl.put(sWsdlFileName, wsdl);

         return wsdl;
      }
      catch (FileNotFoundException ex) {
         return new XmlObject("<definitions />");
      }
   }

   /** 
    * Method clearHashtable 
    * @author Andreas Brod 
    */
   public static void clearHashtable()
   {
      htWsdl.clear();
   }

   /** 
    * Method writeWsdl 
    * 
    * @param psIp 
    * @param psWsdl 
    * @param pbIgnoreSimpleTypes 
    * @param psPrefix 
    * @param pbSplitAtUnderscore 
    * 
    * @author Andreas Brod 
    */
   public static void writeWsdl(String psIp, String psWsdl, boolean pbIgnoreSimpleTypes,
                                String psPrefix, boolean pbSplitAtUnderscore,
                                boolean pbDateTimeHandler)
   {
      if (psWsdl.length() > 0) {
         Util.writeToFile("data\\Ip\\" + psIp + "\\data.wsdl", psWsdl);
         writeDataXsd(psIp, "xs:", getSchema(psWsdl), pbIgnoreSimpleTypes, psPrefix,
               pbSplitAtUnderscore, pbDateTimeHandler);
      }

   }

   /** 
    * Method getSchema 
    * 
    * @param psWsdl 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public static XmlObject getSchema(String psWsdl)
   {
      XmlObject wsdl = new XmlObject(psWsdl).getFirstObject();

      String sTargetNamespace = wsdl.getAttribute("targetNamespace");

      XmlObject schema = new XmlObject("<xs:schema />").getFirstObject();

      schema.setAttribute("elementFormDefault", "qualified");
      schema.setAttribute("version", "1.0");

      schema.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");

      // loop over schemas and collect elements
      XmlObject[] schemas = wsdl.createObject("types").getObjects("schema");

      // get the namespace form the schema if there is only one.
      if (schemas.length == 1) {
         if (schemas[0].getAttribute("targetNamespace").length() > 0) {
            sTargetNamespace = schemas[0].getAttribute("targetNamespace");
         }
      }

      schema.setAttribute("targetNamespace", sTargetNamespace);
      schema.setAttribute("xmlns", sTargetNamespace);

      for (int i = 0; i < schemas.length; i++) {
         XmlObject[] elements = schemas[i].getObjects("");

         for (int j = 0; j < elements.length; j++) {

            // ignore imports
            if (!elements[j].getName().equals("import")) {
               schema.addObject(elements[j], true);
            } else {
               String schemaLocation = elements[j].getAttribute("schemaLocation");
               if (schemaLocation.length() > 0 && !schemaLocation.contains("/")) {
                  schema.addObject(elements[j], true);
               }
            }
         }
      }

      // add messages
      HashSet<String> hs = new HashSet<String>();
      XmlObject[] messages = wsdl.getObjects("message");

      for (int i = 0; i < messages.length; i++) {
         String sType = messages[i].createObject("part").getAttribute("type");
         if (sType.length() == 0) {
            sType = messages[i].createObject("part").getAttribute("element");
         }

         if (sType.indexOf(":") > 0) {
            hs.add(sType.substring(sType.indexOf(":") + 1));
         }

      }

      // add elements for complex types
      XmlObject[] complexType = schema.getObjects("complexType");

      for (int i = 0; i < complexType.length; i++) {
         String sType = complexType[i].getAttribute("name");

         if (sType.length() > 0) {
            if (schema.findSubObject("element", "name", sType) == null) {
               if (hs.contains(sType)) {
                  String sElement = "<xs:element name=\"" + sType + "\" type=\"" + sType + "\"/>";

                  schema.addObject(new XmlObject(sElement));
               }
            }
         }
      }

      schema.setNameSpace("xs");

      // validate Arrays
      Hashtable<String, String> htComplexTypes = new Hashtable<String, String>();

      XmlObject[] objs = schema.getObjects("");

      // ... collect Arrays
      for (int i = 0; i < objs.length; i++) {
         if (objs[i].getName().equals("complexType")) {
            try {
               htComplexTypes.put(objs[i].getAttribute("name"), "");

               XmlObject attribute =
                  objs[i].getObject("complexContent").getObject("restriction")
                        .getObject("attribute");
               String sArray = attribute.getAttribute("arrayType");

               if (sArray.length() > 0) {

                  if (sArray.indexOf("[") > 0) {
                     sArray = sArray.substring(0, sArray.indexOf("["));

                     htComplexTypes.put(objs[i].getAttribute("name"), sArray);
                     schema.deleteObjects(objs[i]);
                  }
               }
            }
            catch (Exception ex) {
               // ignore this
            }
         }
      }

      // ... replace Arrays
      replaceArrays(htComplexTypes, "", schema);

      correctNameSpaces(schema, schema);

      // root elements must not contain maxOccurs
      XmlObject[] objects = schema.getObjects("element");
      for (int i = 0; i < objects.length; i++) {
         objects[i].setAttribute("maxOccurs", null);
      }

      return schema;
   }

   /** 
    * Method correctNameSpaceAttribute 
    * 
    * @param psType 
    * @param pXmlBase 
    * @param pXmlObject 
    * 
    * @author Andreas Brod 
    */
   private static void correctNameSpaceAttribute(String psType, XmlObject pXmlBase,
                                                 XmlObject pXmlObject)
   {

      // correct types
      String sType = pXmlObject.getAttribute(psType);

      if (sType.length() > 0) {
         sType = ":" + sType;
         sType = sType.substring(sType.lastIndexOf(":") + 1);

         // search for complex/simple type
         XmlObject xmlType = pXmlBase.findSubObject("complexType", "name", sType);

         if (xmlType == null) {
            xmlType = pXmlBase.findSubObject("simpleType", "name", sType);
         }

         if (xmlType == null) {
            sType = "xs:" + sType;
         }

         pXmlObject.setAttribute(psType, sType);
      }

   }

   /** 
    * Method correctNameSpaces 
    * 
    * @param pXmlBase 
    * @param pXml 
    * 
    * @author Andreas Brod 
    */
   private static void correctNameSpaces(XmlObject pXmlBase, XmlObject pXml)
   {

      correctNameSpaceAttribute("type", pXmlBase, pXml);
      correctNameSpaceAttribute("base", pXmlBase, pXml);

      // validate nilable
      if (pXml.getAttribute("nillable").equals("true")
            && (pXml.getAttribute("minOccurs").length() == 0)) {
         pXml.setAttribute("minOccurs", "0");
      }

      XmlObject[] subs = pXml.getObjects("");

      for (int i = 0; i < subs.length; i++) {
         correctNameSpaces(pXmlBase, subs[i]);
      }
   }

   /** 
    * Method writeDataXsd 
    * 
    * @param psIp 
    * @param psNameSpace 
    * @param pXsd 
    * @param pbIgnoreSimpleTypes 
    * @param psPrefix 
    * @param pbSplitAtUnderscore 
    * 
    * @author Andreas Brod 
    */
   private static void writeDataXsd(String psIp, String psNameSpace, XmlObject pXsd,
                                    boolean pbIgnoreSimpleTypes, String psPrefix,
                                    boolean pbSplitAtUnderscore, boolean pbDateTimeHandler)
   {

      // delete 'duplicate' elements
      HashSet<String> hs = new HashSet<String>();

      XmlObject[] objs = pXsd.getObjects("");

      for (int i = 0; i < objs.length; i++) {
         if (!hs.add(objs[i].toString())) {
            pXsd.deleteObjects(objs[i]);
         }
      }

      HashSet<String> simpleTypes = null;

      // write to files
      Util.writeToFile("data\\Ip\\" + psIp + "\\dataBinding.xml",
            getDataBinding(pXsd, simpleTypes, psPrefix, pbSplitAtUnderscore, pbDateTimeHandler));

      Util.writeToFile("data\\Ip\\" + psIp + "\\data.xsd", pXsd.toString());

      CorrectDatabindingXsd
            .correctDataBinding(new File("data\\Ip\\" + psIp, "dataBinding.xml"), "");
   }

   /** 
    * Method main 
    * 
    * @param psArgs 
    * 
    * @author Andreas Brod 
    */
   public static void main(String[] psArgs)
   {
      WsdlObject wsdl = new WsdlObject("C:/temp/vanguard/");

      XmlObject schema =
         (new XmlObject(Util.loadFromFile("C:/temp/vanguard/data.xsd"))).createObject("schema");

      XmlObject types =
         (new XmlObject("<types>" + schema.toString() + "</types>")).getFirstObject();

      XmlObject[] element = schema.getObjects("element");

      for (int i = 0; i < element.length; i++) {
         String sXmlns = schema.getAttribute("xmlns");

         if (sXmlns.length() == 0) {
            sXmlns = schema.getAttribute("targetNamespace");
         }

         element[i].setAttribute("xs:xmlns", sXmlns);
         wsdl.searchTypes(element[i], schema, types, "");

      }

      // xml = wsdl.getWsdlType("",xml.createObject("schema"),"");

      /*
       * XmlObject xml = wsdl.getWsdlElement("start",wsdl.loadSchema("data.xsd"),
       *              true);
       */
      WsdlObject.writeDataXsd("TEST", "", schema, true, "", true, true);

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
   private static void replaceArrays(Hashtable<String, String> phtComplexTypes, String psNameSpace,
                                     XmlObject pXmlObject)
   {
      String sType = pXmlObject.getAttribute("type");

      if (sType.indexOf(":") > 0) {
         sType = sType.substring(sType.indexOf(":") + 1);
      }

      if ((sType.length() > 0) && (phtComplexTypes.get(sType) != null)) {
         String sType2 = phtComplexTypes.get(sType);

         if (sType2.length() == 0) {
            sType2 = pXmlObject.getAttribute("type");
         } else {
            pXmlObject.setAttribute("maxOccurs", "unbounded");
         }

         if (sType2.length() > 0) {
            if (sType2.indexOf(":") > 0) {
               sType2 = sType2.substring(sType2.indexOf(":") + 1);
            }

            sType2 = psNameSpace + sType2;

            pXmlObject.setAttribute("type", sType2);
         }
      }

      // loop over subElements
      XmlObject[] subs = pXmlObject.getObjects("");

      for (int i = 0; i < subs.length; i++) {
         replaceArrays(phtComplexTypes, psNameSpace, subs[i]);
      }
   }

   /** 
    * Method getSoapAction 
    * 
    * @param pXmlWsdl 
    * @param psBindingName 
    * @param psOperationName 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private static String getSoapAction(XmlObject pXmlWsdl, String psBindingName,
                                       String psOperationName)
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
    * Method getInputNamespace 
    * 
    * @param pXmlWsdl 
    * @param psBindingName 
    * @param psOprationName 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private static String getInputNamespace(XmlObject pXmlWsdl, String psBindingName,
                                           String psOprationName)
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
    * Method getInputPackage 
    * 
    * @param pXmlWsdl 
    * @param psBindingName 
    * @param psOperationName 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private static String getInputPackage(XmlObject pXmlWsdl, String psBindingName,
                                         String psOperationName)
   {
      String sNew = getInputNamespace(pXmlWsdl, psBindingName, psOperationName);

      if (sNew.length() == 0) {
         sNew = getSoapAction(pXmlWsdl, psBindingName, psOperationName);
      }

      return url2ImportPath(sNew);
   }

   /** 
    * Method url2ImportPath 
    * 
    * @param psUrlPath 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public static String url2ImportPath(String psUrlPath)
   {
      String sAccept = "\\./";

      String sUrlPath = psUrlPath;
      if (sUrlPath.indexOf(":") > 0) {
         sUrlPath = sUrlPath.substring(sUrlPath.indexOf(":") + 1);
      }

      while ((sUrlPath.length() > 0) && (sAccept.indexOf(sUrlPath.charAt(0)) >= 0)) {
         sUrlPath = sUrlPath.substring(1);
      }

      String s = "";

      // eliminate special chars
      for (int i = 0; i < sUrlPath.length(); i++) {
         char c = sUrlPath.charAt(i);

         if ((c >= 'A') && (c <= 'Z')) {
            c += 32;
         }

         if (c == ':') {
            c = '.';
         }

         if (((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9'))
               || (sAccept.indexOf("" + c) >= 0)) {
            if ((c >= '0') && (c <= '9')
                  && ((s.length() == 0) || (sAccept.indexOf(s.substring(s.length() - 1)) >= 0))) {
               s += "p";
            }

            s += c;
         }
      }

      if (s.indexOf("//") > 0) {
         s = s.substring(s.lastIndexOf("//") + 2);
      }

      if (s.indexOf("\\\\") > 0) {
         s = s.substring(s.lastIndexOf("\\\\") + 2);
      }

      if (s.indexOf("/") > 0) {

         // sort other order
         StringTokenizer st = new StringTokenizer(s.substring(0, s.indexOf("/")), ".:");

         s = s.substring(s.indexOf("/"));

         while (st.hasMoreTokens()) {
            if (!s.startsWith("/")) {
               s = "." + s;
            }

            s = st.nextToken() + s;
         }
      }

      while (s.indexOf("/") > 0) {
         s = s.substring(0, s.indexOf("/")) + "." + s.substring(s.indexOf("/") + 1);
      }

      while (s.indexOf("\\") > 0) {
         s = s.substring(0, s.indexOf("\\")) + "." + s.substring(s.indexOf("\\") + 1);
      }

      // remove trailing .
      if (s.endsWith(".")) {
         s = s.substring(0, s.length() - 1);
      }

      return s;
   }

   /** 
    * Method getOperation 
    * 
    * @param pXmlWsdl 
    * @param psBindingName 
    * @param psOprationName 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private static XmlObject getOperation(XmlObject pXmlWsdl, String psBindingName,
                                         String psOprationName)
   {
      String sBindingName = psBindingName;
      XmlObject port = pXmlWsdl.createObject("service").findSubObject("port", "name", sBindingName);

      if (port == null) {
         XmlObject[] binding = pXmlWsdl.getObjects("binding");
         boolean ok = false;

         for (int i = 0; i < binding.length; i++) {
            String sType = binding[i].getAttribute("type");

            if (sType.equals(sBindingName) || sType.endsWith(":" + sBindingName)) {
               sBindingName = binding[i].getAttribute("name");
               ok = true;
            }
         }

         if (!ok) {
            return null;
         }
      } else {
         sBindingName = port.getAttribute("binding");
      }

      if (sBindingName.indexOf(":") > 0) {
         sBindingName = sBindingName.substring(sBindingName.indexOf(":") + 1);
      }

      XmlObject binding = pXmlWsdl.findSubObject("binding", "name", sBindingName);

      if (binding != null) {
         XmlObject operation = binding.findSubObject("operation", "name", psOprationName);

         return operation;
      }

      return null;
   }

   /** 
    * Method getInputWSDLObject 
    * 
    * 
    * 
    * @param psIp 
    * @param psMethod 
    * @param pbReturnJava 
    * @param phsExceptions 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public static String loadWSDLObject(String psIp, String psMethod, boolean pbReturnJava,
                                       HashSet<String> phsExceptions)
   {
      String sRet = "";
      String sParams = "";

      if (!pbReturnJava) {
         sRet = Util.loadFromFile("data\\Ip\\" + psIp + "\\" + psMethod + ".xsd");

         if (sRet.length() > 0) {
            return sRet;
         }
      }

      XmlObject wsdl = getWSDLObjectIp(psIp);

      // load the wsdlObject
      WsdlObject wsdlObject = new WsdlObject(wsdl.getAttribute("fileName"));

      // get portType and additional types (with schema)
      XmlObject[] portType = wsdl.getObjects("portType");
      XmlObject types = wsdl.createObject("types");

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
            String soapAction = getSoapAction(wsdl, portType[i].getAttribute("name"), psMethod);

            // get the inputNameSpace
            String inputNameSpace =
               getInputNamespace(wsdl, portType[i].getAttribute("name"), psMethod);

            String inputPackage = getInputPackage(wsdl, portType[i].getAttribute("name"), psMethod);
            boolean bAddImport = false;

            String sOutputType = "";

            // if the is a message

            if (sMessageOutput.length() > 1) {

               // search a the related message-object
               XmlObject messageOutput =
                  wsdl.findSubObject("message", "name",
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
                  wsdl.findSubObject("message", "name",
                        sMessageInput.substring(sMessageInput.lastIndexOf(":") + 1));

               // get the faultException
               XmlObject messageFault =
                  wsdl.findSubObject("message", "name",
                        sMessageFault.substring(sMessageFault.lastIndexOf(":") + 1));

               XmlObject binding =
                  wsdl.createObject("binding", "type",
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
                           sRet += wsdlObject.getWsdlElement(sElement, types, false);
                        } else {
                           if (j == 0) {
                              sRet += "<m:" + psMethod;

                              if (inputNameSpace.length() > 0) {
                                 sRet += " xmlns:m=\"" + inputNameSpace + "\"";
                              }

                              sRet += ">";
                           }

                           wsdlObject.searchTypes(partInput[j], null, types, "");

                           // sRet += part[j];
                           sRet += wsdlObject.getElement(partInput[j], "", "");

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
                           "            String " + sObj + " = Common.marshalProviderRequest("
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
                                 + ") Common.unmarshalProviderResponse(\n               " + sInner
                                 + ",\n               " + sClass
                                 + ".class, log, _bValidateResponse);\n";
                     }

                  }
                  sHeader += "    * @throws AgentException\n";
                  sHeader += "    * @author _GENERATOR_\n";

                  sHeader += "    */\n";

                  sJava = sHeader + sJava + "    }\n";

                  Util.writeToFile("data\\Ip\\" + psIp + "\\" + psMethod + ".xsd", sRet);

                  if ((inputPackage.length() > 0) && (sJava.indexOf("import " + inputPackage) < 0)
                        && bAddImport) {
                     sJava = "import " + inputPackage + ".*;\n" + sJava + "";
                  }

                  if (pbReturnJava) {
                     return sJava;
                  }

                  return sRet;
               }
            }
         } // endif for this method
      }

      if (pbReturnJava) {
         return sParams;
      }

      return "<" + psMethod + " />";
   }

   /** 
    * Method getWsdlElement 
    * 
    * @param psName 
    * @param pXmlTypes 
    * @param pbConvert 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public XmlObject getWsdlElement(String psName, XmlObject pXmlTypes, boolean pbConvert)
   {
      String sName = psName;
      if (sName.indexOf(":") > 0) {
         sName = sName.substring(sName.lastIndexOf(":") + 1);
      }

      if (sName.indexOf("[") > 0) {
         sName = sName.substring(0, sName.lastIndexOf("["));
      }

      XmlObject[] schema = pXmlTypes.getObjects("schema");

      for (int i = 0; i < schema.length; i++) {

         XmlObject element = schema[i].findSubObject("element", "name", sName);

         if (element != null) {
            String sXmlns = schema[i].getAttribute("xmlns");

            if (sXmlns.length() == 0) {
               sXmlns = schema[i].getAttribute("targetNamespace");
            }

            element.setAttribute("xs:xmlns", sXmlns);
            searchTypes(element, schema[i], pXmlTypes, "");

            if (pbConvert) {
               return element;
            }

            return getElement(element, sXmlns, "");
         }
      }

      return null;

   }

   /** 
    * Method getDocumentation 
    * 
    * @param pXmlElement 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private XmlObject getDocumentation(XmlObject pXmlElement)
   {
      XmlObject doc = pXmlElement.getObject("annotation");

      if (doc != null) {
         doc = doc.getObject("documentation");

         if (doc != null) {
            return (new XmlObject("<!-- " + doc.getCData() + " -->")).getObject("!--");
         }
      }

      XmlObject complexType = pXmlElement.getObject("complexType");

      if (complexType != null) {
         return getDocumentation(complexType);
      }

      return null;
   }

   /** 
    * Method getElement 
    * 
    * @param pXmlElement 
    * @param psXmlns 
    * @param psPath 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public XmlObject getElement(XmlObject pXmlElement, String psXmlns, String psPath)
   {
      String sName = pXmlElement.getAttribute("name");

      if (sName.length() == 0) {
         XmlObject complexType = pXmlElement.getObject("complexType");

         if (complexType != null) {
            sName = complexType.getAttribute("name");
         }
      }

      if (sName.length() > 0) {
         XmlObject obj = (new XmlObject("<" + sName + " />")).getFirstObject();

         if (psXmlns.length() > 0) {
            obj.setAttribute("xmlns", psXmlns);
         }

         if (psPath.indexOf(sName) < 0) {
            analyse(pXmlElement, obj, ".");
         }

         if ((obj.getAttributeNames().length == 0) && (obj.getObjects("").length == 0)) {
            String sType = pXmlElement.getAttribute("type");

            if (sType.indexOf(":") > 0) {
               sType = sType.substring(sType.indexOf(":") + 1);
            }

            if (sType.length() > 0) {
               obj.setCData(sType);
            }
         }

         if ((obj.getAttributeNames().length == 1) && (obj.getObjects("").length == 0)) {
            if (obj.getAttribute("").length() > 0) {
               obj.setCData(obj.getAttribute(""));
               obj.setAttribute("", null);
            }
         }

         return obj;
      }

      return new XmlObject("");
   }

   /** 
    * Method analyse 
    * 
    * @param pXmlElement 
    * @param pXmlObject 
    * @param psDeep 
    * 
    * @author Andreas Brod 
    */
   private void analyse(XmlObject pXmlElement, XmlObject pXmlObject, String psDeep)
   {
      String sElementName = pXmlElement.getAttribute("name");

      String sDeep = psDeep;
      if ((sElementName.length() > 0) && (sDeep.indexOf("." + sElementName + ".") > 0)) {
         return;
      }

      sDeep += sElementName + ".";

      XmlObject[] objs = pXmlElement.getObjects("");

      for (int i = 0; i < objs.length; i++) {
         String sName = objs[i].getAttribute("name");

         if (objs[i].getName().equals("element")) {
            XmlObject doc = getDocumentation(objs[i]);

            if (doc != null) {
               pXmlObject.addObject(doc);
            }

            pXmlObject.addObject(getElement(objs[i], "", sDeep));
         } else if (objs[i].getName().equals("simpleType")) {
            pXmlObject.setCData(getAttribute(pXmlElement));
         } else if (objs[i].getName().equals("attribute")) {

            if (sName.length() > 0) {
               pXmlObject.setAttribute(sName, getAttribute(objs[i]));
            } else {
               pXmlObject.addObject(getElement(objs[i], "", sDeep));
            }
         } else if (!objs[i].getName().startsWith("!--")) {
            analyse(objs[i], pXmlObject, sDeep);
         }
      }
   }

   /** 
    * Method getAttribute 
    * 
    * @param pXmlAttr 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private String getAttribute(XmlObject pXmlAttr)
   {
      String sRet = "";
      String sType = pXmlAttr.getAttribute("type");

      if (sType.length() == 0) {
         sType = pXmlAttr.getAttribute("arrayType");
      }

      if (pXmlAttr.getAttribute("use").startsWith("req")) {
         sRet += "! ";
      }

      if (sType.length() > 0) {
         return sRet + sType;
      }

      XmlObject simpleType = pXmlAttr.getObject("simpleType");

      if (simpleType != null) {
         XmlObject restriction = simpleType.getObject("restriction");

         if (restriction != null) {
            XmlObject pattern = restriction.getObject("pattern");

            if (pattern != null) {
               return sRet + pattern.getAttribute("value");
            }

            XmlObject[] enumeration = restriction.getObjects("enumeration");

            if (enumeration.length > 0) {
               for (int i = 0; i < enumeration.length; i++) {
                  if (i > 0) {
                     sRet += "|";
                  }

                  sRet += enumeration[i].getAttribute("value");
               }

               return sRet;
            }

            if (restriction.getAttribute("base").length() > 0) {
               String sLen = restriction.createObject("maxLength").getAttribute("value");

               if (sLen.length() > 0) {
                  sLen = "-" + sLen;
               }

               sLen = restriction.createObject("minLength").getAttribute("value") + sLen;

               if (sLen.length() > 0) {
                  sLen = "(" + sLen + ")";
               }

               return sRet + restriction.getAttribute("base") + sLen;
            }
         }

         XmlObject union = simpleType.getObject("union");

         if (union != null) {
            return sRet + union.getAttribute("memberTypes");
         }
      }

      return sRet;
   }

   /** 
    * Method searchTypes 
    * 
    * @param pXmlElement 
    * @param pSchema 
    * @param pTypes 
    * @param psPath 
    * 
    * @author Andreas Brod 
    */
   public void searchTypes(XmlObject pXmlElement, XmlObject pSchema, XmlObject pTypes, String psPath)
   {
      if (pXmlElement != null) {
         if (pXmlElement.getAttribute("type").length() > 0) {
            XmlObject add = getWsdlType(pXmlElement.getAttribute("type"), pSchema, pTypes, false);

            if (add != null) {

               // element.setAttribute("type", "");
               pXmlElement.addObject(new XmlObject(add.toString()).getFirstObject());
            }

         } else if (pXmlElement.getAttribute("arrayType").length() > 0) {
            XmlObject add =
               getWsdlType(pXmlElement.getAttribute("arrayType"), pSchema, pTypes, false);

            if (add != null) {
               pXmlElement.setAttribute("arrayType", "");
               pXmlElement.addObject(add);
            }

         } else if (pXmlElement.getAttribute("base").length() > 0) {
            XmlObject add = getWsdlType(pXmlElement.getAttribute("base"), pSchema, pTypes, false);

            if (add != null) {
               pXmlElement.setAttribute("base", "");
               pXmlElement.addObject(add);
            }

         } else if (pXmlElement.getAttribute("ref").length() > 0) {
            XmlObject add = getWsdlType(pXmlElement.getAttribute("ref"), pSchema, pTypes, true);

            if (add != null) {
               pXmlElement.setAttribute("ref", null);

               if (add.getName().equals("element")) {

                  // add parameters
                  pXmlElement.copyFrom(add);
               } else {
                  pXmlElement.addObject(add);
               }
            }

         }

         XmlObject[] objects = pXmlElement.getObjects("");

         for (int i = 0; i < objects.length; i++) {
            String sName = objects[i].getAttribute("name");

            if (sName.length() == 0) {
               sName += "" + objects[i].hashCode();
            }

            if (psPath.indexOf("." + sName + ".") < 0) {
               searchTypes(objects[i], pSchema, pTypes, psPath + sName + ".");
            }
         }
      }

   }

   /**
    * Method getWsdlType
    *
    * @param sType
    * @param schema
    * @param types
    * @param bRefElement
    *
    * @return a complexType with name = sType
    * @author Andreas Brod
    */
   private XmlObject getWsdlType(String sType, XmlObject schema, XmlObject types,
                                 boolean bRefElement)
   {
      XmlObject type = getWsdlType(sType, schema, "", bRefElement);

      if (type == null) {
         XmlObject[] schemas = types.getObjects("schema");

         for (int i = 0; (type == null) && (i < schemas.length); i++) {
            if (schemas[i] != schema) {
               type = getWsdlType(sType, schemas[i], "", bRefElement);
            }
         }
      }

      return type;
   }

   /**
    * Method getWsdlType
    *
    * @param psType
    * @param schema
    * @param sDeep
    * @param bRefElement
    *
    * @return return a complexType with name = sType
    * @author Andreas Brod
    */
   private XmlObject getWsdlType(String psType, XmlObject schema, String sDeep, boolean bRefElement)
   {
      if (schema == null) {
         return null;
      }

      String sType = psType;
      if (sType.indexOf(":") > 0) {
         sType = sType.substring(sType.indexOf(":") + 1);
      }

      if (sType.indexOf("[") > 0) {
         sType = sType.substring(0, sType.indexOf("["));
      }

      XmlObject complexType = schema.findSubObject("complexType", "name", sType);

      if (complexType != null) {
         return complexType;
      }

      XmlObject simpleType = schema.findSubObject("simpleType", "name", sType);

      if (simpleType != null) {
         return simpleType;
      }

      XmlObject attributeGroup = schema.findSubObject("attributeGroup", "name", sType);

      if (attributeGroup != null) {
         return attributeGroup;
      }

      XmlObject complexContent = schema.findSubObject("complexContent", "name", sType);

      if (complexContent != null) {
         return complexContent;
      }

      if (bRefElement) {
         XmlObject refElement = schema.findSubObject("element", "name", sType);

         if (refElement != null) {
            return refElement;
         }
      }

      // try to find it in imports
      XmlObject[] importSchema = schema.getObjects("import");

      for (int i = 0; i < importSchema.length; i++) {
         String schemaLocation = importSchema[i].getAttribute("schemaLocation");

         if (sDeep.indexOf("<" + schemaLocation + ">") < 0) {
            XmlObject schema2 =
               getWsdlType(sType, loadSchema(schemaLocation), sDeep + "<" + schemaLocation + ">",
                     bRefElement);

            if (schema2 != null) {
               return schema2;
            }
         }
      }

      return null;
   }

   Hashtable<String, XmlObject> imports = new Hashtable<String, XmlObject>();

   /** 
    * Method loadSchema 
    * 
    * @param psName 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private XmlObject loadSchema(String psName)
   {
      if ((sPath.length() > 0) && (psName.length() > 0)) {
         if ((imports.get(psName) != null) || (psName.length() < 3)) {
            return imports.get(psName);
         }

         try {
            String fName = psName;

            if (fName.indexOf(":") < 0) {
               fName = sPath + fName;
            }

            if (psName.indexOf(":") < 2) {
               fName = "file:///" + fName;
            } else {
               if (fName.startsWith("http://schemas.xmlsoap.org")) {
                  return null;
               }
            }

            URL url = new URL(fName);
            XmlObject schema = (new XmlObject(url.openStream())).createObject("schema");

            imports.put(psName, schema);

            return schema;
         }
         catch (Exception ex) {
            XmlObject schema = new XmlObject("");

            imports.put(psName, schema);
         }
      }

      return null;
   }

   // -------------

   /** 
    * Method getDataBinding 
    * 
    * @param pXsd 
    * @param phsSimpleTypes 
    * @param psPrefix Prefix 
    * @param pbSplitAtUnderscore 
    * @return 
    * 
    * @author Andreas Brod 
    */
   public static String getDataBinding(XmlObject pXsd, HashSet<String> phsSimpleTypes,
                                       String psPrefix, boolean pbSplitAtUnderscore,
                                       boolean pbDateTimeHandler)
   {
      String sPrefix = psPrefix != null ? psPrefix : "";
      StringBuilder sRet =
         new StringBuilder(
               "<binding xmlns='http://www.castor.org/SourceGenerator/Binding' defaultBindingType='element'>\n");

      sRet.append("<namingXML>\n");
      sRet.append("   <elementName>\n");
      sRet.append("       <prefix>" + sPrefix + "</prefix>\n");
      sRet.append("   </elementName>\n");
      sRet.append("   <complexTypeName>\n");
      sRet.append("       <prefix>" + sPrefix + "</prefix>\n");
      sRet.append("   </complexTypeName>\n");
      sRet.append("</namingXML>\n");

      List<String> listTypes = new ArrayList<String>();
      listTypes.add("xs:language");
      XmlObject[] simpleTypes = pXsd.getObjects("simpleType");
      for (int i = 0; i < simpleTypes.length; i++) {
         if (simpleTypes[i].getObject("list") != null) {
            String sName = simpleTypes[i].getAttribute("name");
            listTypes.add(sName);
         }
      }
      XmlObject xml =
         new XmlObject("<xml>"
               + getXmlInfo(listTypes, pXsd, "/", phsSimpleTypes, pbDateTimeHandler) + "</xml>")
               .getFirstObject();

      // sort the objects
      String[] order = { "elementBinding", "attributeBinding", "complexTypeBinding" };
      for (int i = order.length - 1; i >= 0; i--) {
         XmlObject[] elements = xml.deleteObjects(order[i]);
         if (elements.length > 0)
            xml.addObjects(elements);
      }

      XmlObject[] xmlInfo = xml.getObjects("");

      // sort complex before elements
      // Arrays.sort(xmlInfo);

      // get duplicate names
      HashSet<String> baseElements = new HashSet<String>();
      Hashtable<String, Integer> elementCount = new Hashtable<String, Integer>();
      HashSet<String> duplicateElements = new HashSet<String>();

      // add attribute fields 
      addAttributeFields(pXsd, duplicateElements, "");

      // loop over the info fields
      for (int i = xmlInfo.length - 1; i >= 0; i--) {
         XmlObject javaClass = xmlInfo[i].getObject("java-class");
         XmlObject member = xmlInfo[i].getObject("member");
         String sName0 = xmlInfo[i].getAttribute("name");

         if (!baseElements.add(sName0)) {

            // make nothing
         } else if (javaClass != null) {
            String sName = javaClass.getAttribute("name");

            if (sName.length() > 0) {

               Stack<String> stackLast = new Stack<String>();

               pushStack(xmlInfo[i].getAttribute("name"), stackLast, true, pbSplitAtUnderscore);

               String sLast = "";

               while (stackLast.size() > 0) {
                  sLast = stackLast.pop() + sLast;

                  if (elementCount.get(sLast) == null) {
                     elementCount.put(sLast, new Integer(1));
                  } else {
                     elementCount.put(sLast, new Integer(elementCount.get(sLast).intValue() + 1));
                  }
               }
            }
         } else if (member != null) {
            sRet.append(xmlInfo[i].toString() + "\n");
         }
      }

      // prefill elements
      for (Iterator<String> iter = elementCount.keySet().iterator(); iter.hasNext();) {
         String element = iter.next();

         if (elementCount.get(element).intValue() > 1) {
            duplicateElements.add(element);
         }
      }

      HashSet<String> elements = new HashSet<String>();

      baseElements = new HashSet<String>();

      // validate  Objects
      for (int i = xmlInfo.length - 1; i >= 0; i--) {
         XmlObject javaClass = xmlInfo[i].getObject("java-class");
         String sName0 = xmlInfo[i].getAttribute("name");

         if (!baseElements.add(sName0)) {
            xmlInfo[i].deleteObjects("");
         } else if (javaClass != null) {
            String sElementName = "";
            Stack<String> stackLast = new Stack<String>();

            pushStack(sName0, stackLast, false, pbSplitAtUnderscore);

            if (stackLast.size() > 0) {
               sElementName = stackLast.pop();
            }

            String sName = javaClass.getAttribute("name");

            if (sName.length() > 0) {
               String sName2 = Util.camelCase(sName);

               if (duplicateElements.contains(sName0.toUpperCase())) {
                  String sLast = "";

                  int counter = 1;

                  do {
                     if (stackLast.size() > 0) {
                        sLast = stackLast.pop() + Util.camelCase(sLast);
                        sName2 = sLast + sElementName;
                     } else {
                        sName2 = Util.camelCase(sName) + counter;
                        counter++;
                     }
                  }
                  while (!elements.add(sName2.toUpperCase()));

               } else if (duplicateElements.contains(sName2.toUpperCase())) {

                  String sLast = "";

                  int counter = 0;

                  do {
                     if (stackLast.size() > 0) {
                        sLast = stackLast.pop() + Util.camelCase(sLast);
                        sName2 = sLast + sElementName;
                     } else {
                        if (counter == 0) {
                           sName2 = sLast + sElementName;
                        } else {
                           sName2 = Util.camelCase(sName) + counter;
                        }

                        counter++;
                     }
                  }
                  while (!elements.add(sName2.toUpperCase()));

               }

               if (!sName.equals(sName2)) {
                  javaClass.setAttribute("name", sPrefix + sName2);

                  sRet.append(xmlInfo[i].toString() + "\n");
               } else {
                  javaClass.setAttribute("name", sPrefix + javaClass.getAttribute("name"));
                  sRet.append(xmlInfo[i].toString() + "\n");

               }
            }
         }
      }

      sRet.append("</binding>\n");

      // correct Descriptors
      XmlObject binding = new XmlObject(sRet.toString()).getFirstObject();
      // get the names
      XmlObject[] allElements = binding.getObjects("");
      HashSet<String> hsComplexTypeBinding = new HashSet<String>();
      for (XmlObject xmlBind : allElements) {
         hsComplexTypeBinding.add(xmlBind.getAttribute("name"));
      }
      // validate the Descriptors
      for (XmlObject xmlObject : allElements) {
         XmlObject javaClass = xmlObject.getObject("java-class");
         if (javaClass != null) {
            String sName = javaClass.getAttribute("name");
            if (sName.endsWith("Descriptor")) {
               String sPreName = sName.substring(0, sName.length() - 10);
               if (!sPreName.endsWith("_") && hsComplexTypeBinding.contains(sPreName)) {
                  javaClass.setAttribute("name", sPreName + "_Descriptor");
               }
            }
         }
      }
      return binding.toString();

   }

   /** 
    * private method addAttributeFields 
    * 
    * @param pXsd 
    * @param pDuplicateElements 
    * @param psParent 
    * 
    * @author brod 
    */
   private static void addAttributeFields(XmlObject pXsd, HashSet<String> pDuplicateElements,
                                          String psParent)
   {
      String sParent = psParent;
      String sName = pXsd.getAttribute("name").toUpperCase();
      sName = sName.substring(sName.indexOf(":") + 1);
      String sBase = pXsd.getAttribute("base").toUpperCase();
      if (sBase.length() > 0 && !sBase.startsWith("XS:")) {
         sBase = sBase.substring(sBase.indexOf(":") + 1);
         sParent = "COMPLEXTYPE:" + sBase;
      } else if (sName.length() > 0) {
         sParent += "/" + sName;
         if (pXsd.getName().equalsIgnoreCase("attribute")) {
            pDuplicateElements.add(sParent);
         }
      }
      XmlObject[] objects = pXsd.getObjects("");
      for (int i = 0; i < objects.length; i++) {
         addAttributeFields(objects[i], pDuplicateElements, sParent);
      }
   }

   // xmlInfo[i].getAttribute("name")

   /** 
    * Method pushStack 
    * 
    * @param psText 
    * @param pStackLast 
    * @param pbToUpper 
    * @param pbSplitAtUnderscore 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private static String pushStack(String psText, Stack<String> pStackLast, boolean pbToUpper,
                                   boolean pbSplitAtUnderscore)
   {
      String sText = psText;
      StringTokenizer st = new StringTokenizer(sText, ":\\/");

      String sElementName = "";

      while (st.hasMoreTokens()) {
         sText = st.nextToken();

         boolean bHasNumberInMiddle = false;
         char[] cs = sText.toCharArray();

         for (int i = 1; !bHasNumberInMiddle && (i < cs.length); i++) {
            bHasNumberInMiddle = ("123456780".indexOf(cs[i]) >= 0);
         }

         StringTokenizer st2 =
            new StringTokenizer(sText, bHasNumberInMiddle && pbSplitAtUnderscore ? "_" : "/");

         int iIndex = pStackLast.size();
         String sTotal = "";
         boolean bFirst = true;

         while (st2.hasMoreTokens()) {
            sText = st2.nextToken();

            // Name may not start with a number.
            if ((sText.length() > 0) && ("1234567890".indexOf(sText.charAt(0)) >= 0)) {
               sTotal += "_" + sText;
            } else {
               sElementName = Util.camelCase(sText);

               if (bFirst) {
                  bFirst = false;

                  if (pbToUpper) {
                     pStackLast.add(iIndex, sElementName.toUpperCase());
                  } else {
                     pStackLast.add(iIndex, sElementName);
                  }
               } else {
                  sTotal = sElementName + sTotal;
               }
            }

         }

         if (sTotal.length() > 0) {
            if (sTotal.startsWith("_")) {
               sTotal = "X" + sTotal.substring(1);
            }

            pStackLast.add(iIndex, sTotal);

         }
      }

      return sElementName;

   }

   /** 
    * Method getXmlInfo 
    * 
    * @param plstTypes list of Types 
    * @param pXmlObject 
    * @param psPath 
    * @param phsSimpleTypes 
    * @return 
    * 
    * @author Andreas Brod 
    */
   private static String getXmlInfo(List<String> plstTypes, XmlObject pXmlObject, String psPath,
                                    HashSet<String> phsSimpleTypes, boolean pbDateTimeHandler)
   {
      String sRet = "";
      XmlObject[] objs = pXmlObject.getObjects("");

      for (int i = 0; i < objs.length; i++) {
         XmlObject xmlObject = objs[i];
         String sName = xmlObject.getAttribute("name");
         String sCamelCaseName = Util.camelCase(sName);
         String sPre = xmlObject.getNameSpace() + ":";

         String sType = xmlObject.getAttribute("type");
         String sObjName = xmlObject.getName();
         if ((sName.length() > 0)
               && ((sPre.length() == 0 && sType.indexOf(":") > 0) || !sType.startsWith(sPre))) {
            if (sObjName.endsWith("element")) {

               if ((phsSimpleTypes == null) || (xmlObject.getObject("simpleType") == null)
                     || (psPath.indexOf(":") < 0)) {
                  sRet += "  <elementBinding name=\"" + psPath + sName + "\">\n";
                  sRet += "    <java-class name=\"" + sCamelCaseName + "\" />\n";
                  sRet += "  </elementBinding>\n";
               } else {
                  String stName = sCamelCaseName + "_TYPE";

                  if (phsSimpleTypes.contains(stName)) {
                     int iCount = 2;

                     while (phsSimpleTypes.contains(stName + iCount)) {
                        iCount++;
                     }

                     stName += "" + iCount;
                  }

                  phsSimpleTypes.add(stName);

                  xmlObject.getObject("simpleType").setAttribute("name", stName);

                  //                        sRet += "  <complexTypeBinding name=\"" + sPath.substring(sPath.indexOf(":")+1) + sName
                  //                        + "\">\n";
                  //                              sRet += "    <java-class name=\"" + sCamelCaseName
                  //                                      + "\" />\n";
                  //                              sRet += "  </complexTypeBinding>\n";

               }

               sRet +=
                  getXmlInfo(plstTypes, xmlObject, psPath + sName + "/", phsSimpleTypes,
                        pbDateTimeHandler);
            } else if (sObjName.endsWith("complexType")) {
               sRet += "  <complexTypeBinding name=\"" + sName + "\">\n";
               sRet += "    <java-class name=\"" + sCamelCaseName + "\" />\n";
               sRet += "  </complexTypeBinding>\n";

               String sComplex = "complexType:";

               sRet +=
                  getXmlInfo(plstTypes, xmlObject, sComplex + sName + "/", phsSimpleTypes,
                        pbDateTimeHandler);
            } else if (sObjName.endsWith("attribute") && plstTypes.contains(sType)) {
               sRet += "  <attributeBinding name=\"" + psPath + "@" + sName + "\">\n";
               sRet += "    <member java-type=\"java.lang.String\" />\n";
               sRet += "  </attributeBinding>\n";
            } else if (sName.endsWith("Count") && sName.length() > 5) {

               String sNewName = sName.substring(0, sName.length() - 5);
               // validate if there is an object with the same Name ... so that
               // a count would be created twice.
               boolean bElementFound = false;
               for (int j = 0; j < objs.length; j++) {
                  if (objs[j].toString().indexOf("\"" + sNewName + "\"") >= 0) {
                     bElementFound = true;
                  }
               }
               if (bElementFound) {
                  sRet += "  <attributeBinding name=\"" + psPath + "@" + sName + "\">\n";
                  sRet += "    <member name=\"" + sNewName + "_Count" + "\" />\n";
                  sRet += "  </attributeBinding>\n";
               }
            }
         } else if (sObjName.endsWith("attribute")) {
            if (sType.endsWith(sPre + "unsignedShort")) {
               sRet += "  <attributeBinding name=\"" + psPath + "@" + sName + "\">\n";
               sRet += "    <member java-type=\"int\" />\n";
               sRet += "  </attributeBinding>\n";
            } else if (plstTypes.contains(sType)) {
               sRet += "  <attributeBinding name=\"" + psPath + "@" + sName + "\">\n";
               sRet += "    <member java-type=\"java.lang.String\" />\n";
               sRet += "  </attributeBinding>\n";
            } else if (sName.endsWith("Count") && sName.length() > 5) {

               String sNewName = sName.substring(0, sName.length() - 5);
               // validate if there is an object with the same Name ... so that
               // a count would be created twice.
               boolean bElementFound = false;
               for (int j = 0; j < objs.length; j++) {
                  if (objs[j].toString().indexOf("\"" + sNewName + "\"") >= 0) {
                     bElementFound = true;
                  }
               }
               if (bElementFound) {
                  sRet += "  <attributeBinding name=\"" + psPath + "@" + sName + "\">\n";
                  sRet += "    <member name=\"" + sNewName + "_Count" + "\" />\n";
                  sRet += "  </attributeBinding>\n";
               }
            }
         } else {
            if (sName.length() > 0 && sType.startsWith(sPre)) {
               if (sType.endsWith(":dateTime") && pbDateTimeHandler) {
                  if (sObjName.endsWith("element")) {
                     sRet += "  <elementBinding name=\"" + psPath + sName + "\">\n";
                     sRet +=
                        "    <member handler='net.ifao.util.castor.fieldhandler.DateTimeHandlerIgnoreMillisAndTimezone'"
                              + "/>\n";
                     sRet += "  </elementBinding>\n";
                  } else {
                     sRet += "  <attributeBinding name=\"" + psPath + "@" + sName + "\">\n";
                     sRet +=
                        "    <member handler='net.ifao.util.castor.fieldhandler.DateTimeHandlerIgnoreMillisAndTimezone'"
                              + "/>\n";
                     sRet += "  </attributeBinding>\n";
                  }
               }
               sRet += getXmlInfo(plstTypes, xmlObject, psPath, phsSimpleTypes, pbDateTimeHandler);
            } else {
               sRet += getXmlInfo(plstTypes, xmlObject, psPath, phsSimpleTypes, pbDateTimeHandler);
            }
         }
      }

      if ((sRet.length() > 0) && !sRet.endsWith("\n")) {
         sRet += "\n";
      }

      return sRet;
   }

}
