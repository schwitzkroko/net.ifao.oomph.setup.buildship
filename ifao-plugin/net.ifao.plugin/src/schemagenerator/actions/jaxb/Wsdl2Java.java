package schemagenerator.actions.jaxb;


import ifaoplugin.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

import net.ifao.xml.XmlObject;


class Wsdl2Java
{

   protected static void createJavaClasses(XmlObject wsdl, String psRoot, String psPackage,
                                           String psUrl, String psService)
   {

      String sUser = System.getProperty("user.name");

      if ((sUser == null) || (sUser.length() == 0)) {
         try {
            sUser = InetAddress.getLocalHost().getHostName();
         }
         catch (UnknownHostException e) {
            sUser = "unkown";
         }
      }

      sUser = "@author generator, " + sUser;
      Hashtable<String, XmlObject> htSchemaFile = new Hashtable<String, XmlObject>();

      Hashtable<String, String[]> htMapping = new Hashtable<String, String[]>();

      // 1.) get the wsdlClassname
      String wsdlClassName = wsdl.getAttribute("name");

      // 2.) get the service
      XmlObject service = wsdl.getObject("service");

      // 2.1) get the portBinding
      XmlObject port = service.getObject("port");
      String sBinding = getAttribute(port, "binding");
      XmlObject address = port.getObject("address");

      // 2.2) get the portType
      XmlObject portType = wsdl.getObject("portType");

      // 3.) get the related binding
      XmlObject binding = wsdl.findSubObject("binding", "name", sBinding);

      // 4.) get the oprations
      XmlObject[] operations = binding.getObjects("operation");

      // START TO BUILD THE JAVA-CLASS
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteArrayOutputStream outMethods = new ByteArrayOutputStream();
      PrintStream stream = new PrintStream(out);
      PrintStream streamMethods = new PrintStream(outMethods);

      StringBuilder sDefaultPath = new StringBuilder();
      StringTokenizer stLocation = new StringTokenizer(address.getAttribute("location"), "/");
      if (stLocation.hasMoreTokens() && stLocation.nextToken().endsWith(":")) {
         // consume IP and port
         if (stLocation.hasMoreTokens()) {
            stLocation.nextToken();
         }
         // get the path
         while (stLocation.hasMoreTokens()) {
            sDefaultPath.append("/");
            sDefaultPath.append(stLocation.nextToken());
         }
      }
      String sPackage = psPackage + psService;
      stream.println("package " + sPackage + ";");
      stream.println("");
      stream.println("import java.io.*;");
      stream.println("import java.lang.reflect.Method;");
      stream.println("import net.ifao.common.*;");
      if (psService.length() > 0) {
         stream.println("import " + psPackage + ".*;");
      }
      // stream.println("import net.ifao.common.log.ILog;");
      stream.println("import javax.xml.bind.*;");
      stream.println("// specific imports");
      stream.println("");
      stream.println("/**");
      stream.println(" * The class " + wsdlClassName + " is automactially generated with");
      stream.println(" * the class <code>Wsdl2Java</code>.<p>");
      stream.println(" * The original wsdl file was taken from the following url:<br>");
      stream.println(" * <a href='" + psUrl + "'>" + psUrl + "</a><br>");
      stream.println(" * This url file was copied to <code>data.wsdl</code>");
      stream.println(" * within the package <code>" + psPackage + "</code>");
      stream.println(" * <p>");
      stream.println(" * The default service is:");
      printXmlComment(stream, service, " ");
      stream.println(" * <p>");
      stream.println(" * Copyright &copy; " + new GregorianCalendar().get(Calendar.YEAR)
            + ", i:FAO");
      stream.println(" * " + sUser);
      stream.println(" */");
      stream.println("public class " + wsdlClassName + "\n{");
      stream.println("");
      List<String> lstInitParameters = new ArrayList<String>();
      HashSet<String> hsJaxBClasses = new HashSet<String>();
      Hashtable<String, String> hsSoapActions = new Hashtable<String, String>();
      boolean bXmlDateString = false;

      for (XmlObject operation : operations) {

         StringBuilder sbMethod = new StringBuilder();

         streamMethods.println("");
         XmlObject oInput = operation.getObject("input");
         XmlObject oOutput = operation.getObject("output");
         XmlObject oOperation = operation.getObject("operation");
         XmlObject[] inputObjects = oInput.getObjects("");
         XmlObject oOutputBody = null;
         String sOutputClass = "void";
         if (oOutput != null) {
            oOutputBody = oOutput.getObject("body");
            List<String[]> lstClassName =
               getClassName(wsdl, portType, operation, oOutput, oOutputBody, psPackage, psRoot,
                     htSchemaFile);
            if ((lstClassName != null) && (lstClassName.size() > 0)) {
               sOutputClass = lstClassName.get(0)[0];
            } else {
               sOutputClass = "String";
            }
         }

         String soapAction;
         if (oOperation != null) {
            soapAction = oOperation.getAttribute("soapAction");
            sbMethod.append("      // the service requires a SoapAction\n");
            sbMethod.append("      setSoapAction(\"" + soapAction + "\");\n");
         } else {
            soapAction = "";
         }
         sbMethod.append("      // set the path for the communication\n");
         sbMethod.append("      _pHttpCommunication.setPath(_sHttpCommunicationPath);\n");
         int iCount = 0;
         boolean bJaxbClass = false;
         if (sOutputClass.startsWith(psPackage)) {
            bJaxbClass = true;
            if (!sOutputClass.contains("soap.enc")) {
               hsJaxBClasses
                     .add(sOutputClass.substring(0, sOutputClass.lastIndexOf(".") + 1) + "*");
            }
            sOutputClass = sOutputClass.substring(sOutputClass.lastIndexOf(".") + 1);
         }
         String sMethodName = getMethodName(operation.getAttribute("name"));
         streamMethods.println("   /**");
         streamMethods.println("    * The method " + sMethodName
               + " sends a request (via soap)<br>");
         streamMethods.println("    * The related wsdl element is:");
         printXmlComment(streamMethods, operation, "    ");
         streamMethods.println("    * " + sUser);
         streamMethods.println("    */");
         streamMethods.print("   public " + sOutputClass + " " + sMethodName + "(");
         boolean bException = false;

         for (XmlObject inpObject : inputObjects) {
            if (inpObject.getName().equals("header")) {
               List<String[]> lstClassName =
                  getClassName(wsdl, portType, operation, oInput, inpObject, psPackage, psRoot,
                        htSchemaFile);
               for (String[] sClassName : lstClassName) {
                  String sPart = inpObject.getAttribute("part");
                  boolean bSpecificClass = sClassName[0].startsWith(psPackage);
                  if (bSpecificClass) {
                     hsJaxBClasses.add(sClassName[0].substring(0,
                           sClassName[0].lastIndexOf(".") + 1) + "*");
                     sClassName[0] = sClassName[0].substring(sClassName[0].lastIndexOf(".") + 1);
                  }
                  String sParams = sClassName[0] + " p" + sPart + " " + sClassName[1];
                  if (!lstInitParameters.contains(sParams)) {
                     lstInitParameters.add(sParams);
                  }
                  sbMethod
                        .append("      // set the " + sPart + " element within the soap header\n");
                  sbMethod
                        .append("      _pHttpCommunication.init(\"addSoapHeaderElement\", new String(_pHttpCommunication.marshal(");
                  if (!bSpecificClass) {
                     sbMethod
                           .append(sClassName[1].substring(0, sClassName[1].lastIndexOf(".") + 1));
                     sbMethod.append("ObjectFactory.class, ");
                  }
                  sbMethod.append("_p" + sPart + ")));\n");

               }
            } else if (inpObject.getName().equals("body")) {
               List<String[]> lstClassName =
                  getClassName(wsdl, portType, operation, oInput, inpObject, psPackage, psRoot,
                        htSchemaFile);
               String sNameOperation;
               if (inpObject.getAttribute("use").equalsIgnoreCase("encoded")) {
                  sNameOperation = operation.getAttribute("name");
               } else {
                  sNameOperation = null;
               }
               if ((lstClassName != null) && (lstClassName.size() > 0)) {
                  sbMethod.append("\n      // Create the request object\n");
                  sbMethod.append("      StringBuilder sb = new StringBuilder();\n");

                  if (sNameOperation != null) {
                     sbMethod.append("      sb.append((\"<ns:" + sNameOperation + " xmlns:ns=\\\""
                           + inpObject.getAttribute("namespace") + "\\\">\"));\n");
                  }
                  String sClassName0 = null;
                  for (String[] sClassName : lstClassName) {
                     sClassName0 = sClassName[0];
                     if (sClassName0.startsWith(psPackage)) {
                        hsJaxBClasses
                              .add(sClassName0.substring(0, sClassName0.lastIndexOf(".") + 1) + "*");
                        sClassName[0] = sClassName0.substring(sClassName0.lastIndexOf(".") + 1);
                     }
                     if (iCount > 0) {
                        streamMethods.print(", ");
                     }
                     String sName = sClassName[2];
                     if (sName.length() == 0) {
                        sName = oInput.getAttribute("name");
                     }
                     String sNameUpper =
                        ("p" + sClassName0.charAt(sClassName0.lastIndexOf(".") + 1)).toLowerCase()
                              + sName.substring(0, 1).toUpperCase() + sName.substring(1);
                     streamMethods.print(sClassName0 + " " + sNameUpper);

                     iCount++;
                     if (sClassName0.startsWith(psPackage)) {
                        sbMethod.append("      sb.append(_pHttpCommunication.marshal(" + sNameUpper
                              + "));\n");
                     } else {
                        sbMethod.append("      if (" + sNameUpper + " != null) {\n");
                        if (sClassName0.equalsIgnoreCase("java.util.Date")) {
                           sNameUpper = "getXmlDateString(" + sNameUpper + ")";
                           bXmlDateString = true;
                        }
                        sbMethod.append("         sb.append((\"<" + sName + ">\" + "
                              + sNameUpper + " + \"</" + sName + ">\"));\n");
                        sbMethod.append("      }\n");
                     }
                  }
                  if (sNameOperation != null) {
                     if (soapAction.length() > 0) {
                        hsSoapActions.put(sNameOperation, soapAction);
                     }
                     sbMethod.append("      sb.append((\"</ns:" + sNameOperation + ">\"));\n");
                  } else {
                     if ((soapAction.length() > 0) && (sClassName0 != null)) {
                        hsSoapActions.put(sClassName0, soapAction);
                     }
                  }

                  sbMethod
                        .append("\n      // Send the request (via HttpCommunication) and get the response bytes\n");
                  sbMethod
                        .append("      String sReponse = _pHttpCommunication.sendRequest(sb.toString());\n");
                  bException = true;
               } else {
                  if (sNameOperation != null) {
                     sbMethod.append("      // Send an empty " + sNameOperation
                           + " request (via HttpCommunication) and get the response bytes\n");

                     sbMethod
                           .append("      String sReponse = _pHttpCommunication.sendRequest((\"<ns:"
                                 + sNameOperation
                                 + " xmlns:ns=\\\""
                                 + inpObject.getAttribute("namespace") + "\\\"/>\"));\n");
                  } else {
                     sbMethod
                           .append("      // Send an empty request (via HttpCommunication) and get the response bytes\n");
                     sbMethod
                           .append("      String sReponse = _pHttpCommunication.sendRequest(\"\");\n");
                  }
               }
            }
         }
         streamMethods.println(")");
         streamMethods.println("      throws Exception");
         streamMethods.println("   {");
         streamMethods.println(sbMethod.toString());
         if (!sOutputClass.equals("void")) {

            if (sOutputClass.equals("String")) {
               streamMethods.println("      // convert the response and return the object");
               streamMethods.println("      return sReponse;");
            } else if (sOutputClass.equals("boolean")) {
               streamMethods.println("      // convert the response and return the object");
               streamMethods.println("      return sReponse.equalsIgnoreCase(\"true\");");
            } else if (bJaxbClass) {
               streamMethods.println("      // convert the response and return the object");
               streamMethods.println("      " + sOutputClass + " response =");
               streamMethods.println("         (" + sOutputClass
                     + ") _pHttpCommunication.unmarshal(");
               streamMethods.println("            " + sOutputClass + ".class, sReponse);");
               streamMethods.println("      return response;");
            } else {
               streamMethods.println("      return null;");
            }
         }
         streamMethods.println("   }");
      }
      if (hsSoapActions.size() > 0) {
         stream.println("");
         stream.println("   /** ");
         stream.println("    * return the soap action which is assigned to the requested");
         stream.println("    * 'request type'.<p>");
         String[] keySet = hsSoapActions.keySet().toArray(new String[0]);
         Arrays.sort(keySet);
         if (keySet.length > 0) {
            stream.println("    * E.g. <code>" + wsdlClassName + ".getSoapAction(\""
                  + keySet[0].substring(keySet[0].lastIndexOf(".") + 1) + "\")</code> will ");
            stream.println("    * return <code>\"" + hsSoapActions.get(keySet[0]) + "\"</code>");
         }
         stream.println("    * ");
         stream.println("    * @param psRequestType The request type String");
         stream.println("    * " + sUser);
         stream.println("    */");
         stream.println("   public static String getSoapAction(String psRequestType)");
         stream.println("   {");
         stream.println("      if (psRequestType == null) {");
         stream.println("        return null;");
         for (String sKey : keySet) {
            stream.println("      } else if (psRequestType.equals(\""
                  + sKey.substring(sKey.lastIndexOf(".") + 1) + "\")) {");
            stream.println("        return \"" + hsSoapActions.get(sKey) + "\";");
         }
         stream.println("      }");
         stream.println("      return null;");
         stream.println("   }");
      }
      // add the header members
      stream.println("");
      stream.println("   // private members for this class");
      stream.println("   private ICommunication _pHttpCommunication;");
      // get the default path
      stream.println("   private String _sHttpCommunicationPath = \"" + sDefaultPath + "\";");
      for (int i = 0; i < lstInitParameters.size(); i++) {
         StringTokenizer st = new StringTokenizer(lstInitParameters.get(i), " ");
         if (st.countTokens() == 3) {
            String sType = st.nextToken();
            if (sType.startsWith(psPackage)) {
               stream.print("   private ");
               stream.print(sType);
               stream.print(" _");
            } else {
               stream.print("   private JAXBElement<");
               stream.print(sType);
               stream.print("> _");
            }
            stream.print(st.nextToken());
            stream.println(" = null;");
         }
      }
      // add the constructor
      stream.println("");
      stream.println("   /** ");
      stream.println("    * This is the constructor for the class " + wsdlClassName);
      stream.println("    * <p>");
      stream.println("    * It encapsulates the soap methods of the wsdl file<br>");
      stream.println("    * <a href='" + psUrl + "'>" + psUrl + "</a>");
      stream.println("    * ");
      stream.println("    * @param pHttpCommunication http communication object");
      stream.println("    * " + sUser);
      stream.println("    */");
      stream.println("   public " + wsdlClassName + "(ICommunication pHttpCommunication)");
      stream.println("   {");
      stream.println("      _pHttpCommunication = pHttpCommunication;");
      stream.println("   }");

      // add the constructor
      stream.println("");
      stream.println("   /** ");
      stream.println("    * change the http communication path, which will be used for the communication");
      stream.println("    * <p>");
      stream.println("    * This overwrites the default communication-path '" + sDefaultPath + "'");
      stream.println("    * ");
      stream.println("    * @param psHttpCommunicationPath the http communication path which has to be used");
      stream.println("    * instead of the default path.");
      stream.println("    * " + sUser);
      stream.println("    */");
      stream.println("   public void changeHttpCommunicationPath(String  psHttpCommunicationPath)");
      stream.println("   {");
      stream.println("      _sHttpCommunicationPath = psHttpCommunicationPath;");
      stream.println("   }");
      // add the initParameters
      for (int i = 0; i < lstInitParameters.size(); i++) {
         StringTokenizer st = new StringTokenizer(lstInitParameters.get(i), " ");
         if (st.countTokens() == 3) {
            String sType = st.nextToken();
            String sName = st.nextToken();
            String sOriginalClass = st.nextToken();
            stream.println("");
            String sName1 = sName.substring(1);
            stream.println("   /** ");
            stream.println("    * init the " + sName1 + ", which is used within the soap header");
            stream.println("    * for specific requests ");
            stream.println("    * ");
            stream.println("    * @param " + sName + " " + sType + " value of the " + sName1);
            stream.println("    * " + sUser);
            stream.println("    */");
            stream.println("   public void init" + sName1 + "(" + sType + " " + sName + ")");
            stream.println("   {");
            stream.println("      // set the " + sName + " member of this class");
            stream.print("      _");
            stream.print(sName);
            if (sType.startsWith(psPackage)) {
               stream.print(" = ");
               stream.print(sName);
               stream.println(";");
            } else {
               stream.println(" =");
               stream.print("         new ");
               stream.print(sOriginalClass.substring(0, sOriginalClass.lastIndexOf(".") + 1));
               stream.println("ObjectFactory()");
               stream.print("            .create");
               stream.print(sName1);
               stream.print("(");
               stream.print(sName);
               stream.println(");");
            }
            stream.println("   }");
         }
      }

      // print private methods
      stream.println("");
      stream.println("    /** ");
      stream.println("     * set a soap action within the current communication");
      stream.println("     * ");
      stream.println("     * @param psSoapAction soap action String");
      stream.println("     * " + sUser);
      stream.println("     */");
      stream.println("    private void setSoapAction(String psSoapAction)");
      stream.println("    {");
      stream.println("       try {");
      stream.println("          Object initCommunication = _pHttpCommunication.getInitCommunication();");
      stream.println("          Method method = initCommunication.getClass().getMethod(\"setSoapAction\", psSoapAction.getClass());");
      stream.println("          method.invoke(initCommunication, psSoapAction);");
      stream.println("       }");
      stream.println("       catch (Exception e) {");
      stream.println("          // could not set the soap Action");
      stream.println("       }");
      stream.println("    }");
      if (bXmlDateString) {
         hsJaxBClasses.add("javax.xml.datatype.*");
         hsJaxBClasses.add("java.util.GregorianCalendar");
         stream.println("");
         stream.println("    /** ");
         stream.println("     * get the dateString");
         stream.println("     * ");
         stream.println("     * @param pDate java.util.Date object");
         stream.println("     * " + sUser);
         stream.println("     * @return xml date string");
         stream.println("     * @throws DatatypeConfigurationException");
         stream.println("     */");
         stream.println("    private String getXmlDateString(java.util.Date pDate)");
         stream.println("       throws DatatypeConfigurationException");
         stream.println("    {");
         stream.println("       GregorianCalendar cal = new GregorianCalendar();");
         stream.println("       cal.setTime(pDate);");
         stream.println("       XMLGregorianCalendar newXMLGregorianCalendar =");
         stream.println("          DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);");
         stream.println("       return newXMLGregorianCalendar.toXMLFormat();");
         stream.println("    }");
      }

      stream.println("");
      stream.print(outMethods.toString());
      stream.println("}");

      // write the JavaClass
      try {
         String sOut = out.toString();
         // replace the imports
         StringBuilder sb = new StringBuilder();
         for (String sJaxbClass : hsJaxBClasses) {
            sb.append("import " + sJaxbClass + ";\n");
         }
         sOut = sOut.replaceFirst(Pattern.quote("// specific imports"), sb.toString());

         Util.writeToFile(new File(psRoot, sPackage.replaceAll("\\.", "/") + "/" + wsdlClassName
               + ".java"), sOut.toString().getBytes());
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private static void printXmlComment(PrintStream stream, XmlObject pXmlObject, String psFiller)
   {
      StringBuffer sb = new StringBuffer();
      pXmlObject.toHtmlStringBuffer(sb);
      StringTokenizer st = new StringTokenizer(sb.toString(), "\n\r");
      stream.println(psFiller + "* <pre>");
      while (st.hasMoreTokens()) {
         stream.print(psFiller);
         stream.print("* ");
         stream.println(st.nextToken());
      }
      stream.println(psFiller + "* </pre>");
   }

   private static String getMethodName(String psMethodName)
   {
      return psMethodName.substring(0, 1).toLowerCase() + psMethodName.substring(1);
   }

   private static List<String[]> getClassName(XmlObject wsdl, XmlObject portType,
                                              XmlObject operation, XmlObject oInput,
                                              XmlObject inpObject, String psPackage, String psRoot,
                                              Hashtable<String, XmlObject> htSchemaFile)
   {
      List<String> lstInputParts = new ArrayList<String>();

      StringTokenizer stInput =
         new StringTokenizer(
               inpObject.getAttribute("part") + " " + inpObject.getAttribute("parts"), " ");
      while (stInput.hasMoreTokens()) {
         lstInputParts.add(stInput.nextToken());
      }
      if (lstInputParts.size() == 0 && oInput.getName().equals("input")) {
         XmlObject message = wsdl.findSubObject("message", "name", oInput.getAttribute("name"));
         if (message != null) {
            for (XmlObject part : message.getObjects("part")) {
               String name = part.getAttribute("name");
               if (name.length() > 0) {
                  lstInputParts.add(name);
               }
            }
         }
      }

      List<String[]> lstParameters = new ArrayList<String[]>();
      String sMessage = getAttribute(inpObject, "message");
      XmlObject inputMessage;
      if (sMessage.length() > 0) {
         inputMessage = wsdl.findSubObject("message", "name", sMessage);
      } else {
         XmlObject ptOpration =
            portType.findSubObject("operation", "name", operation.getAttribute("name"));
         XmlObject ptInput = ptOpration.getObject(oInput.getName());
         sMessage = getAttribute(ptInput, "message");
         if (lstInputParts.size() == 0) {
            lstInputParts.add("parameters");
         }
      }
      inputMessage = wsdl.findSubObject("message", "name", sMessage);
      if (inputMessage == null) {
         return null;
      }

      List<XmlObject> lstInputPartsXml = new ArrayList<XmlObject>();
      for (String sPart : lstInputParts) {
         XmlObject findSubObject = inputMessage.findSubObject("part", "name", sPart);
         if (findSubObject != null) {
            lstInputPartsXml.add(findSubObject);
         }
      }
      if (lstInputPartsXml.size() == 0) {
         return lstParameters;
      }
      for (XmlObject part : lstInputPartsXml) {
         String element = part.getAttribute("element");
         String sName = part.getAttribute("name");
         if (element.length() == 0) {
            element = part.getAttribute("type");

            if (oInput.getName().equalsIgnoreCase("output")) {
               element = inputMessage.getAttribute("name");
               element = "tns:" + element.substring(0, 1).toUpperCase() + element.substring(1);
            } else {
               element = part.getAttribute("type");
            }

         }
         if (element.contains(":")) {
            String sNs = element.substring(0, element.indexOf(":"));
            String xmlns = part.getAttribute("xmlns:" + sNs);
            if (xmlns.length() == 0) {
               xmlns = wsdl.getAttribute("xmlns:" + sNs);
            }
            String sPackage = getPackage(psPackage, xmlns);
            String sElementName = element.substring(element.indexOf(":") + 1);
            String sClassName = sPackage + "." + sElementName;
            String[] retValue = new String[]{ sClassName, sClassName, sName };
            try {

               XmlObject xmlElement;
               XmlObject xsd;
               if (sPackage.endsWith(".xmlschema")) {
                  xmlElement = new XmlObject("<element />").getFirstObject();
                  xmlElement.setAttribute("type", "xs:" + sElementName);
                  xsd = null;
               } else {
                  // try to find the element within the related schema file
                  xsd = htSchemaFile.get(sPackage);
                  if (xsd == null) {
                     File xsdFile =
                        new File(psRoot, sPackage.replaceAll("\\.", "/") + "/Schema.xsd");
                     if (!xsdFile.exists()) {
                        xsdFile = new File(psRoot, sPackage.replaceAll("\\.", "/") + "/data.xsd");
                     }
                     xsd = new XmlObject(xsdFile).getFirstObject();
                     htSchemaFile.put(sPackage, xsd);
                  }

                  xmlElement = xsd.findSubObject("element", "name", sElementName);
                  if (xmlElement == null) {
                     xmlElement = xsd.findSubObject("simpleType", "name", sElementName);
                  }
               }
               if (xmlElement != null) {
                  String type = xmlElement.getAttribute("type");
                  if (type.length() == 0) {
                     XmlObject restriction = xmlElement.getObject("restriction");
                     if (restriction != null) {
                        type = restriction.getAttribute("base");
                        if (!type.contains(":")) {
                           type = "xs:" + type;
                        }
                     } else {
                        if (xmlElement.getObject("union") != null) {
                           type = "xs:string";
                        }
                     }
                  }
                  if (type.startsWith("xs:") || type.startsWith("xsd:")) {
                     if (type.contains(":int")) {
                        retValue = (new String[]{ "Integer", sClassName, sName });
                     } else if (type.contains(":long")) {
                        retValue = (new String[]{ "Long", sClassName, sName });
                     } else if (type.contains(":date")) {
                        retValue = (new String[]{ "java.util.Date", sClassName, sName });
                     } else if (type.contains(":bool")) {
                        retValue = (new String[]{ "Boolean", sClassName, sName });
                     } else if (type.contains(":string")) {
                        retValue = (new String[]{ "String", sClassName, sName });
                     }
                  } else if (type.length() > 0) {
                     if (xsd != null) {
                        String sTypeName = type.substring(type.indexOf(":") + 1);
                        XmlObject complexType = xsd.findSubObject("complexType", "name", sTypeName);
                        if (complexType != null) {
                           sClassName = sPackage + "." + sTypeName;
                           retValue = new String[]{ sClassName, sClassName, sName };
                        }
                        System.out.println(type);
                     }
                  }
               }
            }
            catch (FileNotFoundException e) {
               // should never happen
            }

            lstParameters.add(retValue);

         } else if (element.length() > 0) {
            lstParameters.add(new String[]{ element, "", sName });
         }
      }
      return lstParameters;
   }

   private static String getPackage(String psPackage, String targetNamespace)
   {
      String sPackage = psPackage;
      StringTokenizer st = new StringTokenizer(targetNamespace, "\\/");
      if (st.countTokens() > 0) {
         String sFirstToken = st.nextToken();
         if (sFirstToken.endsWith(":")) {
            if (st.hasMoreTokens()) {
               // consume the next token
               st.nextToken();
            }
         }
         while (st.hasMoreTokens()) {
            sPackage += ".";
            String sNextToken = st.nextToken();
            if ((sNextToken.charAt(0) >= '0') && (sNextToken.charAt(0) <= '9')) {
               sPackage += "p";
            }
            sPackage += sNextToken;
         }
      }
      return sPackage.toLowerCase();
   }

   private static String getAttribute(XmlObject xmlObject, String psAttributeName)
   {
      String attribute = xmlObject.getAttribute(psAttributeName);
      if (attribute.contains(":")) {
         return attribute.substring(attribute.indexOf(":") + 1);
      }
      return attribute;
   }

}
