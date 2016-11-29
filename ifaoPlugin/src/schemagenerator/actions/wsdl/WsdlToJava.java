package schemagenerator.actions.wsdl;


import ifaoplugin.*;

import java.io.*;
import java.net.*;
import java.util.*;

import net.ifao.xml.XmlObject;


/**
 * This class implements a WsdlToJava.
 *
 * Copyright &copy; 2013, i:FAO
 *
 * @author brod
 */
public class WsdlToJava
{

   /**
    * creates the java classes.
    *
    * @param pfWsdlFile wsdl file
    * @param psRootDirectory root directory String
    * @param psPackage package String
    * @param psUrl url String
    * @param phsObjectElements phs object elements Hash Set of strings
    * @param phtSpecificNamspacePackages pht specific namspace packages Hashtable of strings and lists of strings
    * @param phsJavaFiles phs java files Hash Set of strings
    * @throws IOException
    *
    * @author brod
    */
   public static void createJavaClasses(File pfWsdlFile,
                                        String psRootDirectory,
                                        String psPackage,
                                        String psUrl,
                                        HashSet<String> phsObjectElements,
                                        Hashtable<String, List<String>> phtSpecificNamspacePackages,
                                        HashSet<String> phsJavaFiles)
      throws IOException
   {

      String sFileName = pfWsdlFile.getName();
      sFileName = sFileName.substring(0, sFileName.lastIndexOf("."));
      String sUser = System.getProperty("user.name");

      if (sUser == null || sUser.length() == 0) {
         try {
            sUser = InetAddress.getLocalHost().getHostName();
         }
         catch (UnknownHostException e) {
            sUser = "unkown";
         }
      } else {
         sUser = sUser.substring(0, 1).toUpperCase() + sUser.substring(1).toLowerCase();
      }

      sUser = "@author generator, " + sUser;

      XmlObject wsdl = new XmlObject(pfWsdlFile).getFirstObject();

      Hashtable<String, String> hsNameSpaces = new Hashtable<String, String>();
      Hashtable<String, String> hsSoapActions = new Hashtable<>();

      addNameSpaces(hsNameSpaces, psPackage, wsdl);
      // search for Import
      for (XmlObject imp : wsdl.getObjects("import")) {
         File impFile = new File(pfWsdlFile.getParentFile(), imp.getAttribute("location"));
         if (impFile.exists()) {
            // import the content
            XmlObject firstObject = new XmlObject(impFile).getFirstObject();
            addNameSpaces(hsNameSpaces, psPackage, firstObject);
            for (XmlObject xmlObject : firstObject.getObjects("")) {
               wsdl.addObject(xmlObject);
            }
         }
      }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintStream javaCode = new PrintStream(out);

      createJavaHeader(javaCode, sFileName, psPackage + ".service", hsNameSpaces, sUser,
            phtSpecificNamspacePackages);
      // get the services
      XmlObject[] services = wsdl.getObjects("service");
      if (services.length == 0) {
         // there are no services
         return;
      }
      for (XmlObject service : services) {
         for (XmlObject port : service.getObjects("port")) {
            // get the binding
            String portBinding = getName(port.getAttribute("binding"));

            XmlObject binding = wsdl.findSubObject("binding", "name", portBinding);
            String bindingType = getName(binding.getAttribute("type"));

            XmlObject bindingOperation = binding.getObject("operation");

            XmlObject portType = wsdl.findSubObject("portType", "name", bindingType);

            // search the port operation
            XmlObject portTypeOperation =
               portType.findSubObject("operation", "name", bindingOperation.getAttribute("name"));

            createJavaMethod(javaCode, port, bindingOperation, portTypeOperation, wsdl,
                  hsSoapActions, phsObjectElements);

         }
      }
      createJavaFooter(javaCode, hsSoapActions, sUser);

      // write the file
      String child = psPackage.replaceAll("[\\\\.]", "/") + "/service/" + sFileName + ".java";
      File file = new File(psRootDirectory, child);
      Util.writeToFile(file, out.toByteArray());
      phsJavaFiles.add(sFileName);
   }

   /**
    * creates a java footer.
    *
    * @param pJavaCodeStream java code stream object Print Stream
    * @param phsSoapActions phs soap actions Hashtable of strings and strings
    * @param psUser user String
    *
    * @author brod
    */
   private static void createJavaFooter(PrintStream pJavaCodeStream,
                                        Hashtable<String, String> phsSoapActions, String psUser)
   {
      String[] keySet = phsSoapActions.keySet().toArray(new String[0]);
      if (keySet.length > 0) {
         Arrays.sort(keySet);
         pJavaCodeStream.println("");
         pJavaCodeStream.println("   /** ");
         pJavaCodeStream.println("    * return the soap action which is assigned to the requested");
         pJavaCodeStream.println("    * 'request type'.");
         pJavaCodeStream.println("    * ");
         pJavaCodeStream.println("    * @param psRequestType The request type String");
         pJavaCodeStream.println("    * " + psUser);
         pJavaCodeStream.println("    */");
         pJavaCodeStream.println("   public static String getSoapAction(String psRequestType)");
         pJavaCodeStream.println("   {");
         pJavaCodeStream.println("      if (psRequestType == null) {");
         pJavaCodeStream.println("        return null;");
         for (String sKey : keySet) {
            String[] sRequest = phsSoapActions.get(sKey).split("\\n");
            if (sRequest.length > 0) {
               pJavaCodeStream.println("      } else if (psRequestType.equals(\""
                     + sKey.substring(sKey.lastIndexOf(".") + 1) + "\")) {");
               pJavaCodeStream.println("        return \"" + sRequest[0] + "\";");
            }
         }
         pJavaCodeStream.println("      }");
         pJavaCodeStream.println("      return null;");
         pJavaCodeStream.println("   }");

         // get the systemPath
         pJavaCodeStream.println("");
         pJavaCodeStream.println("   /** ");
         pJavaCodeStream.println("    * return the port path which is assigned to the requested");
         pJavaCodeStream.println("    * 'request type'.");
         pJavaCodeStream.println("    * ");
         pJavaCodeStream.println("    * @param psRequestType The request type String");
         pJavaCodeStream.println("    * " + psUser);
         pJavaCodeStream.println("    */");
         pJavaCodeStream.println("   public static String getPortPath(String psRequestType)");
         pJavaCodeStream.println("   {");
         pJavaCodeStream.println("      if (psRequestType == null) {");
         pJavaCodeStream.println("        return null;");
         for (String sKey : keySet) {
            String[] sRequest = phsSoapActions.get(sKey).split("\\n");
            if (sRequest.length > 1) {
               pJavaCodeStream.println("      } else if (psRequestType.equals(\""
                     + sKey.substring(sKey.lastIndexOf(".") + 1) + "\")) {");
               pJavaCodeStream.println("        return \"" + sRequest[1] + "\";");
            }
         }
         pJavaCodeStream.println("      }");
         pJavaCodeStream.println("      return null;");
         pJavaCodeStream.println("   }");
      }
      pJavaCodeStream.println("}");
   }

   /**
    * creates a java header.
    *
    * @param pJavaCodeStream java code stream object Print Stream
    * @param psFileName file name String
    * @param psPackage package String
    * @param phsNameSpaces phs name spaces Hashtable of strings and strings
    * @param psUser user String
    * @param phtSpecificNamspacePackages pht specific namspace packages Hashtable of strings and lists of strings
    *
    * @author brod
    */
   private static void createJavaHeader(PrintStream pJavaCodeStream, String psFileName,
                                        String psPackage, Hashtable<String, String> phsNameSpaces,
                                        String psUser,
                                        Hashtable<String, List<String>> phtSpecificNamspacePackages)
   {
      pJavaCodeStream.println("package " + psPackage + ";");
      pJavaCodeStream.println("");
      pJavaCodeStream.println("import java.io.*;");
      pJavaCodeStream.println("import java.lang.reflect.Method;");
      pJavaCodeStream.println("import net.ifao.common.*;");
      pJavaCodeStream.println("");

      HashSet<String> hashPackages = new HashSet<String>();
      // add imports
      for (String sNameSpace : phsNameSpaces.keySet()) {
         String sPackage = phsNameSpaces.get(sNameSpace);
         if (hashPackages.add(sPackage)) {
            List<String> list = phtSpecificNamspacePackages.get(sPackage);
            if (list != null && list.size() > 0) {
               for (String string : list) {
                  pJavaCodeStream.println("import " + string + ".*;");
               }

            } else {
               pJavaCodeStream.println("import " + sPackage + ".*;");
            }
         }
      }
      pJavaCodeStream.println("");
      pJavaCodeStream.println("public class " + psFileName + " {");

      // add the header members
      pJavaCodeStream.println("");
      pJavaCodeStream.println("   // private members for this class");
      pJavaCodeStream.println("   private ICommunication _pHttpCommunication;");
      // add the constructor
      pJavaCodeStream.println("");
      pJavaCodeStream.println("   /** ");
      pJavaCodeStream.println("    * This is the constructor for the class " + psFileName);
      pJavaCodeStream.println("    * <p>");
      pJavaCodeStream.println("    * It encapsulates the soap methods of the wsdl file");
      pJavaCodeStream.println("    * ");
      pJavaCodeStream.println("    * @param pHttpCommunication http communication object");
      pJavaCodeStream.println("    * " + psUser);
      pJavaCodeStream.println("    */");
      pJavaCodeStream.println("   public " + psFileName + "(ICommunication pHttpCommunication)");
      pJavaCodeStream.println("   {");
      pJavaCodeStream.println("      _pHttpCommunication = pHttpCommunication;");
      pJavaCodeStream.println("   }");

      // print private methods
      pJavaCodeStream.println("");
      pJavaCodeStream.println("    /** ");
      pJavaCodeStream.println("     * set a soap action within the current communication");
      pJavaCodeStream.println("     * ");
      pJavaCodeStream.println("     * @param psSoapAction soap action String");
      pJavaCodeStream.println("     * " + psUser);
      pJavaCodeStream.println("     */");
      pJavaCodeStream.println("    private void setSoapAction(String psSoapAction)");
      pJavaCodeStream.println("    {");
      pJavaCodeStream.println("       try {");
      pJavaCodeStream
            .println("          Object initCommunication = _pHttpCommunication.getInitCommunication();");
      pJavaCodeStream
            .println("          Method method = initCommunication.getClass().getMethod(\"setSoapAction\", psSoapAction.getClass());");
      pJavaCodeStream.println("          method.invoke(initCommunication, psSoapAction);");
      pJavaCodeStream.println("       }");
      pJavaCodeStream.println("       catch (Exception e) {");
      pJavaCodeStream.println("          // could not set the soap Action");
      pJavaCodeStream.println("       }");
      pJavaCodeStream.println("    }");

   }

   /**
    * adds the name spaces.
    *
    * @param phsNameSpaces phs name spaces Hashtable of strings and strings
    * @param psPackage package String
    * @param pXsdWsdlObject xsd wsdl object object Xml Object
    *
    * @author brod
    */
   private static void addNameSpaces(Hashtable<String, String> phsNameSpaces, String psPackage,
                                     XmlObject pXsdWsdlObject)
   {
      String sTargetNamespace = pXsdWsdlObject.getAttribute("targetNamespace");
      String targetNamespace = sTargetNamespace;
      int iPos = targetNamespace.indexOf("/");
      for (int i = 0; i < 2; i++) {
         iPos = targetNamespace.indexOf("/", iPos + 1);
      }
      targetNamespace = targetNamespace.substring(0, iPos + 1);

      for (String sAttributeName : pXsdWsdlObject.getAttributeNames(true)) {
         if (sAttributeName.startsWith("xmlns:")) {
            String sUrl = pXsdWsdlObject.getAttribute(sAttributeName);
            if (!sUrl.startsWith(targetNamespace) || sUrl.equals(sTargetNamespace)) {
               continue;
            }
            for (int i = 0; i < 3; i++) {
               sUrl = sUrl.substring(sUrl.indexOf("/") + 1);
            }
            sUrl = psPackage + "." + sUrl.replaceAll("[\\\\/]", ".").toLowerCase();
            phsNameSpaces.put(sAttributeName.substring(sAttributeName.indexOf(":") + 1), sUrl);
         }
      }
   }

   /**
    * returns a name.
    *
    * @param psAttribute attribute String
    * @return the name
    *
    * @author brod
    */
   private static String getName(String psAttribute)
   {
      return psAttribute.substring(psAttribute.indexOf(":") + 1);

   }

   /**
    * creates a java method.
    *
    * @param pJavaCodeStream java code stream object Print Stream
    * @param pXmlPort xml port object Xml Object
    * @param pXmlbindingOperation xmlbinding operation object Xml Object
    * @param pXmlPortTypeOperation xml port type operation object Xml Object
    * @param pXmlWsdlObject xml wsdl object object Xml Object
    * @param phsSoapActions phs soap actions Hashtable of strings and strings
    * @param phsObjectElements phs object elements Hash Set of strings
    *
    * @author brod
    */
   private static void createJavaMethod(PrintStream pJavaCodeStream, XmlObject pXmlPort,
                                        XmlObject pXmlbindingOperation,
                                        XmlObject pXmlPortTypeOperation, XmlObject pXmlWsdlObject,
                                        Hashtable<String, String> phsSoapActions,
                                        HashSet<String> phsObjectElements)
   {
      // get the methodName
      String methodName = pXmlPort.getAttribute("name");
      if (methodName.endsWith("Port")) {
         methodName = methodName.substring(0, methodName.length() - 4);
      }
      if (methodName.length() > 1) {
         methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
      }

      // get the input and output values
      String in = getName(pXmlPortTypeOperation.createObject("input").getAttribute("message"));
      String out = getName(pXmlPortTypeOperation.createObject("output").getAttribute("message"));
      String soapAction = pXmlbindingOperation.createObject("operation").getAttribute("soapAction");
      String sPath = pXmlPort.createObject("address").getAttribute("location");
      for (int i = 0; i < 3; i++) {
         sPath = sPath.substring(sPath.indexOf("/") + 1);
      }

      in = getMessageType(in, pXmlWsdlObject);
      out = getMessageType(out, pXmlWsdlObject);

      if (in.length() > 0) {
         phsSoapActions.put(in, soapAction + "\n/" + sPath);
         in = in + " pParameter";
      }
      if (phsObjectElements.contains(out)) {
         out = "Object";
      }
      if (out.length() == 0) {
         out = "void";
      }
      pJavaCodeStream.println("");
      pJavaCodeStream.println("   public " + out + " " + methodName + "(" + in + ")");
      pJavaCodeStream.println("      throws Exception");
      pJavaCodeStream.println("   {");

      // set the soapAction
      pJavaCodeStream.println("      // the service requires a SoapAction");
      pJavaCodeStream.println("      setSoapAction(\"" + soapAction + "\");");
      pJavaCodeStream.println("      // set the path for the communication");
      pJavaCodeStream.println("      _pHttpCommunication.setPath(\"/" + sPath + "\");");
      pJavaCodeStream.println("      ");
      pJavaCodeStream.println("      // Create the request object");
      pJavaCodeStream
            .println("      String sRequest = JAXBUtil.marshal(_pHttpCommunication.getLog(), pParameter);");
      pJavaCodeStream.println("      ");
      pJavaCodeStream
            .println("      // Send the request (via HttpCommunication) and get the response");
      pJavaCodeStream
            .println("      String sReponse = _pHttpCommunication.sendRequest(sRequest);");
      pJavaCodeStream.println("      ");
      if (!out.equals("void")) {
         if (out.equals("Object")) {
            pJavaCodeStream.println("      return sReponse;");
         } else {
            pJavaCodeStream.println("      // convert the response and return the object");
            pJavaCodeStream.println("      " + out + " response =");
            pJavaCodeStream.println("         (" + out
                  + ") JAXBUtil.unmarshal(_pHttpCommunication.getLog(),");
            pJavaCodeStream.println("            " + out
                  + ".class, sReponse);");
            pJavaCodeStream.println("      return response;");
         }
      }

      pJavaCodeStream.println("   }");

   }

   /**
    * returns a message type.
    *
    * @param psMessageName message name String
    * @param pXmlWsdlObject xml wsdl object object Xml Object
    * @return the message type
    *
    * @author brod
    */
   private static String getMessageType(String psMessageName, XmlObject pXmlWsdlObject)
   {
      XmlObject findSubObject = pXmlWsdlObject.findSubObject("message", "name", psMessageName);
      if (findSubObject != null) {
         String element = findSubObject.createObject("part").getAttribute("element");
         if (element.indexOf(":") > 0) {
            element = element.substring(element.indexOf(":") + 1);
            return element;
         }
      }
      return psMessageName;
   }

}
