package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;
import java.util.*;

import net.ifao.xml.*;
import schemagenerator.actions.jaxb.*;


/** 
 * This class handles the creation of schemas, bindings and interface classes for the WSDL files of 
 * Silverrail 
 * 
 * <p> 
 * Copyright &copy; 2012, i:FAO Group GmbH 
 * @author kaufmann 
 */
public class ImportSilverrail
{
   // output stream for info messages
   private PrintStream _out;

   private String _sBasePackage;

   /** 
    * Constructor ImportSilverrail 
    * 
    * @param pOut output stream for info messages 
    * @param psPackage "base" package for Silverrail's WSDL (e.g. "net.railgds.shopping") 
    * 
    * @author kaufmann 
    */
   public ImportSilverrail(PrintStream pOut, String psBasePackage)
   {
      _out = pOut;
      _sBasePackage = psBasePackage;
   }

   /** 
    * Here the conversion starts 
    * 
    * @param psUrl URL of the WSDL to extract the schemas from 
    * @param psService name of the service
    * @param psDirProviderData base directory for all providerdatas  
    * @param psBaseArctic base directory of the arctic project
    * @throws Exception
    * 
    * @author kaufmann 
    */
   public void importWsdl(String psUrl, String psService, String psDirProviderData,
                          String psBaseArctic)
      throws Exception
   {
      try {
         Util.setOutput(_out);

         // base directory for the package
         String sPackageDirectory = _sBasePackage.replace('.', '/');

         // read the wsdl from the Silverrail server and write it to disk
         XmlObject wsdl =
            getWsdl(psUrl, psService, psDirProviderData, sPackageDirectory, psBaseArctic);

         // create the schemas from the wsdl
         createSchemas(wsdl, psDirProviderData);

         // create the Interface for this wsdl
         createInterface(wsdl, psDirProviderData);

      }
      finally {
         Util.removeOutput();
      }
   }

   /** 
    * Extracts the schemas from the WSDL, corrects the imports and creates the bindings. The schemas 
    * and binding will be written to disk 
    * 
    * @param pWsdl Silverrail WSDL 
    * @param psDirProviderData base directory for all providerdatas 
    * @throws IOException 
    * 
    * @author kaufmann 
    */
   private void createSchemas(XmlObject pWsdl, String psDirProviderData)
      throws IOException
   {
      // get the different schemas from the WSDL
      XmlObject types = pWsdl.getObject("types");
      XmlObject[] schemaObjects = types.getObjects("schema");

      // get the namespaces of imported schemas; they will get name "schema.xsd", non-imported
      // schemas will get name "data.xsd"
      Set<String> importedSchemas = new HashSet<String>();
      for (XmlObject schema : schemaObjects) {
         XmlObject[] imports = schema.getObjects("import");
         for (XmlObject importObject : imports) {
            importedSchemas.add(importObject.getAttribute("namespace"));
         }
      }

      // correct the imports and create the binding for the schemas
      Map<String, SchemaData> schemaDatas = new HashMap<String, SchemaData>();
      for (XmlObject schema : schemaObjects) {
         SchemaData schemaData =
            new SchemaData(schema, _sBasePackage, psDirProviderData, importedSchemas);

         schemaDatas.put(schemaData._sTargetNamespace, schemaData);
         // correct the import locations
         schemaData.correctImports(importedSchemas);

         // create the binding for this schema
         schemaData.createBinding();
      }

      // now write the adapted schemas and binding to disk
      for (SchemaData schemaData : schemaDatas.values()) {
         // write the schema to disk
         schemaData.writeSchema();

         // write the binding to disk; bindings will only be written for "base" schemas (data.xsd).
         // bindings for the imported schemas (schema.xsd) will be part of the "base" schema's binding
         schemaData.writeBinding(schemaDatas);
      }
   }

   /** 
    * Creates a java interface for the WSDL. This can (should) be implemented by the 
    * communication class. 
    * 
    * @param pWsdl Silverrail WSDL 
    * @param psDirProviderData base directory for all providerdatas 
    * @throws IOException 
    * 
    * @author kaufmann 
    */
   private void createInterface(XmlObject pWsdl, String psDirProviderData)
      throws IOException
   {
      InterfaceData interfaceData = new InterfaceData(pWsdl, _sBasePackage, psDirProviderData);

      interfaceData.createInterface();

      interfaceData.writeInterface();

   }

   /** 
    * Reads the WSDL from the URL and writes it to disk 
    * 
    * @param psUrl URL of the WSDL 
    * @param psService name of the service
    * @param psRootDir base directory for all providerdatas 
    * @param psPackageDirectory directory for the WSDL's package (net/railgds/shopping) 
    * @param psBaseArctic base directory of the arctic project
    * @return WSDL object 
    * @throws Exception 
    * 
    * @author kaufmann 
    */
   private XmlObject getWsdl(String psUrl, String psService, String psRootDir,
                             String psPackageDirectory, String psBaseArctic)
      throws Exception
   {

      // get the WSDL from the URL
      _out.println("Read WSDL from url " + psUrl);
      UrlConnection connect = new UrlConnection(psUrl, psBaseArctic);
      XmlObject wsdl = new XmlObject(connect.getContent()).getFirstObject();

      // write the WSDL to disk
      File wsdlFile = new File(psRootDir, psPackageDirectory + "/data" + psService + ".wsdl");
      Util.writeToFile(wsdlFile, wsdl.toString().getBytes());
      return wsdl;
   }

   /** 
    * Class SchemaData handles data of a schema 
    * 
    * <p> 
    * Copyright &copy; 2012, i:FAO Group GmbH 
    * @author kaufmann 
    */
   private class SchemaData
   {
      private XmlObject _schema;
      private String _sTargetNamespace;
      private String _sPackage;
      private String _sName;
      private File _file;
      private boolean _bIsDataXsd;
      private String _sPackageBase;
      private XmlObject _binding;

      /**
       * Initializes the SchemaData object
       *
       * @param pSchema schema object 
       * @param psPackage "base" package for Silverrail's WSDL (e.g. "net.railgds.shopping")
       * @param psDirProviderData base directory for all providerdatas
       * @param pImportedSchemas
       *
       * @author kaufmann
       */
      SchemaData(XmlObject pSchema, String psPackage, String psDirProviderData,
                 Set<String> pImportedSchemas)
      {
         _schema = pSchema;
         _sTargetNamespace = _schema.getAttribute("targetNamespace");
         _sPackageBase = psPackage;
         _sPackage = Util.getPackage(_sPackageBase, _sTargetNamespace);

         _bIsDataXsd = isDataXsd(_sTargetNamespace, pImportedSchemas);

         _sName = getSchemaFileName(_sTargetNamespace, pImportedSchemas);

         _file = new File(psDirProviderData, _sPackage.replace('.', '/') + "/" + _sName);
      }

      /**
       * Creates the binding for the schema. It contains a binding for the schema to its package and
       * bindings for enum attributes/elements (JaxB does not automatically create classes for 
       * anonymous simpleType enums, so a binding is necessary (jaxb:typesafeEnumClass))
       *
       * @author kaufmann
       */
      void createBinding()
      {
         _binding = new XmlObject("<jxb:bindings />").getFirstObject();

         _binding.createObject("jxb:schemaBindings").createObject("jxb:package")
               .setAttribute("name", _sPackage);
         _binding.setAttribute("schemaLocation", _sName);

         new EnumBindingCorrector(_schema, _binding).addBindingsForSimpleTypeEnums();
      }

      /**
       * Returns the file name for the schema. If the schema is imported by an other schema, the
       * file name is "schema.xsd", if not the file name is "data.xsd"
       *
       * @param psNameSpace namespace of the schema
       * @param pImportedSchemas set of namespaces of imported schemas
       * @return "data.xsd" or "schema.xsd"
       *
       * @author kaufmann
       */
      private String getSchemaFileName(String psNameSpace, Set<String> pImportedSchemas)
      {
         return (isDataXsd(psNameSpace, pImportedSchemas) ? "data.xsd" : "schema.xsd");
      }

      /**
       * Returns true for schemas, which are not imported by other schemas
       *
       * @param psNameSpace namespace of the schema
       * @param pImportedSchemas set of namespaces of imported schemas
       * @return true for schemas, which are not imported by other schemas
       *
       * @author kaufmann
       */
      private boolean isDataXsd(String psNameSpace, Set<String> pImportedSchemas)
      {
         return !pImportedSchemas.contains(psNameSpace);
      }

      /**
       * Corrects the import definitions in the schema by changing attribute schemaLocation to the path 
       * to the downloaded schema on disk
       *
       * @param pImportedSchemas set of namespaces of imported schemas
       *
       * @author kaufmann
       */
      void correctImports(Set<String> pImportedSchemas)
      {
         XmlObject[] imports = _schema.getObjects("import");
         for (XmlObject importObject : imports) {
            String sImportNameSpace = importObject.getAttribute("namespace");
            String sImportedPackage = Util.getPackage(_sPackageBase, sImportNameSpace);
            String sRelativeDirectory = Util.getRelDirectory(_sPackage, sImportedPackage);
            String sNewLocation =
               sRelativeDirectory + getSchemaFileName(sImportNameSpace, pImportedSchemas);
            importObject.setAttribute("schemaLocation", sNewLocation);
         }

      }

      /**
       * Writes the schema to disk
       *
       * @throws IOException
       *
       * @author kaufmann
       */
      void writeSchema()
         throws IOException
      {
         Util.writeToFile(_file, _schema.toString().getBytes());
      }

      /**
       * Writes the binding to disk
       *
       * @param pSchemaDatas
       * @throws IOException
       *
       * @author kaufmann
       */
      void writeBinding(Map<String, SchemaData> pSchemaDatas)
         throws IOException
      {
         if (_bIsDataXsd) {
            // create the xmlObject for the binding
            XmlObject jxb = new XmlObject("<jxb:bindings />").getFirstObject();

            jxb.setAttribute("xmlns:jxb", "http://java.sun.com/xml/ns/jaxb");
            jxb.setAttribute("xmlns", "http://java.sun.com/xml/ns/jaxb");
            jxb.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
            jxb.setAttribute("xmlns:jaxb", "http://java.sun.com/xml/ns/jaxb");
            jxb.setAttribute("xmlns:xjc", "http://java.sun.com/xml/ns/jaxb/xjc");
            jxb.setAttribute("jaxb:version", "1.0");
            jxb.setAttribute("jaxb:extensionBindingPrefixes", "xjc");

            // add the binding for the schema
            jxb.addElementObject(_binding);

            // add the binding for the imported schemas
            XmlObject[] imports = _schema.getObjects("import");
            for (XmlObject importObject : imports) {
               SchemaData importedSchemaData =
                  pSchemaDatas.get(importObject.getAttribute("namespace"));
               // create a copy of the binding
               XmlObject importedBindingCopy = importedSchemaData._binding.copy();
               // and replace the schemaLocation by the relative path
               importedBindingCopy.setAttribute("schemaLocation",
                     importObject.getAttribute("schemaLocation"));
               // add the binding
               jxb.addElementObject(importedBindingCopy);
            }

            // write the whole binding to disk
            Util.writeToFile(new File(_file.getParent(), "bindings.xjb"), jxb.toString().getBytes());
         }
      }
   }

   /** 
    * Class InterfaceData handles data of a java interface 
    * 
    * <p> 
    * Copyright &copy; 2012, i:FAO Group GmbH 
    * @author kaufmann 
    */
   private class InterfaceData
   {
      private XmlObject _wsdl;
      private String _sPackage;
      private String _sDirProviderData;
      private String _sServiceName;
      private String _sInterfaceName;
      private ArrayList<Operation> _operations;

      /**
       * Initializes the data for the java interface
       *
       * @param pWsdl wsdl object
       * @param psPackage "base" package for Silverrail's WSDL (e.g. "net.railgds.shopping")
       * @param psDirProviderData base directory for all providerdatas
       *
       * @author kaufmann
       */
      InterfaceData(XmlObject pWsdl, String psPackage, String psDirProviderData)
      {
         _wsdl = pWsdl;
         _sPackage = psPackage; //getPackage(psPackage, _wsdl.getAttribute("targetNamespace"));
         _sDirProviderData = psDirProviderData;
      }

      /**
       * Creates the data for the java Interface by parsing the SOAP definitions
       *
       * @author kaufmann
       */
      void createInterface()
      {
         // get service/port/ for the name of the interface
         XmlObject service = _wsdl.getObject("service");
         _sServiceName = service.getAttribute("name");

         XmlObject servicePort = service.getObject("port");
         _sInterfaceName = servicePort.getAttribute("name");

         // get the binding info for the port
         XmlObject binding = findXmlObjectByName(servicePort, "binding", "binding");

         // get the portType
         XmlObject portType = findXmlObjectByName(binding, "type", "portType");

         // collect the operations of the port
         collectOperations(portType);
      }

      /**
       * Creates the souce code for the Java interface and writes it to disk
       *
       * @author kaufmann
       * @throws IOException 
       */
      void writeInterface()
         throws IOException
      {
         StringBuilder sbInterface = new StringBuilder();
         // package
         sbInterface.append("package ").append(_sPackage).append(";\n\n\n");

         // comment
         sbInterface.append("// Interface for port ").append(_sInterfaceName)
               .append(" of service ").append(_sServiceName).append("\n");
         sbInterface.append("// generated by the schema generator tab Rail/Silverrail\n\n");

         // import
         sbInterface.append("import ")
               .append(Util.getPackage(_sPackage, _wsdl.getAttribute("targetNamespace")))
               .append(".*;\n\n");

         // interface
         sbInterface.append("public interface I").append(_sInterfaceName).append("\n{\n");

         // methods
         for (Operation operation : _operations) {
            sbInterface.append("   ");
            sbInterface.append(operation.toMethod());
         }

         // end of interface
         sbInterface.append("}\n");

         // create the file name of the java interface class and write it to disk
         File file =
            new File(_sDirProviderData, _sPackage.replace('.', '/') + "/I" + _sInterfaceName
                  + ".java");
         Util.writeToFile(file, sbInterface.toString().getBytes());
      }

      /**
       * Collects the Operations of a portType
       *
       * @param pWsdl WSDL object
       * @param pPortType portType object
       *
       * @author kaufmann
       */
      private void collectOperations(XmlObject pPortType)
      {
         _operations = new ArrayList<Operation>();
         XmlObject[] operations = pPortType.getObjects("operation");
         for (XmlObject xmlOperation : operations) {
            Operation operation = new Operation(xmlOperation);

            // find the type of the output message
            XmlObject outMessage =
               findXmlObjectByName(xmlOperation.getObject("output"), "message", "message");
            operation.setOutType(findMessageType(outMessage.getObject("part")));

            // find the types of the input message
            XmlObject inMessage =
               findXmlObjectByName(xmlOperation.getObject("input"), "message", "message");
            XmlObject[] inParts = inMessage.getObjects("part");
            for (XmlObject inPart : inParts) {
               operation.addInType(findMessageType(inPart));
            }

            _operations.add(operation);
         }
      }

      /**
       * Finds a XML object, which is part of the WSDL and has the name set to the same value as the
       * pSource object's attribute psSourceAttribute (without namespace). Example:<pre>
       * pSource: &lt;wsdl:binding name="ShoppingServicesEndpointImplServiceSoapBinding" type="tns:Shopping"&gt;
       * psSourceAttribute: "type"
       * psObjectType: "portType"
       * 
       * Element found: &lt;wsdl:portType name="Shopping"&gt;...&lt;/wsdl:portType&gt; 
       * </pre>
       *
       * @param pSource Source element
       * @param psSourceAttribute Attribute of the source element, which defines the name of the object to find
       * @param psObjectType type of the object to find
       * @return <code>null</code>, or the object fitting to the constraints
       *
       * @author kaufmann
       */
      private XmlObject findXmlObjectByName(XmlObject pSource, String psSourceAttribute,
                                            String psObjectType)
      {
         XmlObject xmlObject = null;
         String sSearchValue = removeNamespace(pSource.getAttribute(psSourceAttribute));
         if (sSearchValue != null) {
            XmlObject[] objectsToSearch = _wsdl.getObjects(psObjectType);
            for (XmlObject object : objectsToSearch) {
               if (sSearchValue.equals(object.getAttribute("name"))) {
                  xmlObject = object;
                  break;
               }
            }
         }
         return xmlObject;
      }

      /**
       * Finds the (Java) type of the message part
       *
       * @param pPart message part
       * @return (Java) type for the message part
       *
       * @author kaufmann
       */
      private String findMessageType(XmlObject pPart)
      {
         String sType = null;

         // extract the element's name to find
         String sElementNameToFind = removeNamespace(pPart.getAttribute("element"));

         // all types/schemas will be checked. The namespaces will not be regarded! Maybe this must
         // be changed in a later version, if 2 elements with the same name exist in different schemas!
         XmlObject types = _wsdl.getObject("types");
         XmlObject[] schemaObjects = types.getObjects("schema");

         // find the element by scanning the schemas
         for (XmlObject schema : schemaObjects) {
            XmlObject[] elements = schema.getObjects("element");
            for (XmlObject element : elements) {
               if (sElementNameToFind.equals(element.getAttribute("name"))) {
                  sType = removeNamespace(element.getAttribute("type"));
                  break;
               }
            }
            if (sType != null) {
               break;
            }
         }

         return sType;
      }

      /**
       * Removes the namespace information from a XML value, e.g.<pre>
       * "shop:FareType"  ==>  "FareType"
       *
       * @param psValue value to remove the namespace info from
       * @return value without namespace
       *
       * @author kaufmann
       */
      private String removeNamespace(String psValue)
      {
         if (psValue != null && psValue.contains(":")) {
            return psValue.substring(psValue.indexOf(':') + 1);
         }
         return psValue;
      }

      /**
       * Class Operation handles data of a SOAP operation
       *
       * <p>
       * Copyright &copy; 2012, i:FAO Group GmbH
       * @author kaufmann
       */
      private class Operation
      {
         private XmlObject _operation;
         private String _sName;
         private List<String> _inTypes;
         private String _sOutType;

         /**
          * Initializes the data of a SOAP operation
          *
          * @param pOperation operation xmlObject
          *
          * @author kaufmann
          */
         Operation(XmlObject pOperation)
         {
            _operation = pOperation;
            _inTypes = new ArrayList<String>();
            _sName = pOperation.getAttribute("name");
         }

         /**
          * Adds a type of the input message
          *
          * @param psInType type of the input message part
          *
          * @author kaufmann
          */
         void addInType(String psInType)
         {
            _inTypes.add(psInType);
         }

         /**
          * Sets the type of the output message
          *
          * @param psOutType type of the output message
          *
          * @author kaufmann
          */
         void setOutType(String psOutType)
         {
            _sOutType = psOutType;
         }

         /**
          * Creates the interface method source code for this SOAP operation
          *
          * @return java code for the method
          *
          * @author kaufmann
          */
         String toMethod()
         {
            StringBuilder sbMethod = new StringBuilder();
            sbMethod.append("public ");
            sbMethod.append(_sOutType == null ? "void" : firstCharToUpper(_sOutType)).append(" ");
            sbMethod.append(_sName.substring(0, 1).toLowerCase()).append(_sName.substring(1))
                  .append("(");
            int iCount = 0;
            for (String sInType : _inTypes) {
               if (iCount++ > 0) {
                  sbMethod.append(", ");
               }
               sbMethod.append(firstCharToUpper(sInType)).append(" ");
               sbMethod.append("p").append(firstCharToUpper(sInType));
            }
            sbMethod.append(") throws net.ifao.arctic.framework.AgentException;\n");

            return sbMethod.toString();
         }

         /**
          * Converts the first char of the class name passed to uppercase. This is necessary for
          * the generated interface, because JAXB always creates classes starting with an uppercase
          * letter.
          *
          * @param psClassName original class name, e.g. dataRequestType
          * @return class name with first char in upper case, e.g. DataRequestType
          *
          * @author kaufmann
          */
         private String firstCharToUpper(String psClassName)
         {
            // no class name passed, nothing can be done
            if (psClassName == null) {
               return null;
            }

            // class is only one character, return in upper case
            if (psClassName.length() == 1) {
               return psClassName.toUpperCase();
            }

            // return the first char in upper case, the rest as is
            return psClassName.substring(0, 1).toUpperCase() + psClassName.substring(1);
         }
      }
   }

}
