package schemagenerator.actions;


import ifaoplugin.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import net.ifao.xml.*;


/**
 * Class ImportNvs creates the classes for the elements defined in the service catalog
 *
 * <p>
 * Copyright &copy; 2008, i:FAO Group GmbH
 * @author kaufmann
 */
public class ImportNvs
{
   private String[] _sActionServices = null;
   private String _sServiceCatalog = null;
   private String _sBaseDir = null;
   private StringBuilder _sbResult = null;

   /**
    * Constructor ImportNvs
    * @param psBaseDir arctic base dir
    * @param psServiceCatalog file name of the service catalog
    * @param psActionServices list of action/service combinations, e.g. {ANG10000, DBU1000}
    *
    * @author kaufmann
    * @throws Exception 
    */
   public ImportNvs(String psBaseDir, String psServiceCatalog, String[] psActionServices)
      throws Exception
   {
      // cut off a backslash at the end
      if (psBaseDir.endsWith("\\")) {
         psBaseDir = psBaseDir.substring(0, psBaseDir.length() - 1);
      }

      _sBaseDir = psBaseDir;

      _sServiceCatalog = psServiceCatalog;
      if (!(new File(_sServiceCatalog).exists())) {
         throw new Exception("File " + _sServiceCatalog + " not found!");
      }

      _sActionServices = psActionServices;
      if (_sActionServices.length == 0) {
         throw new Exception("No action/service combinations selected");
      }

      _sbResult = new StringBuilder();

   }


   /**
    * Method getResult returns the string for the result ("log")
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
    * Method startGeneration is the main entry for the generation of the NVS classes of the 
    * service catalog
    *
    * @author kaufmann
    * @throws Exception 
    */
   public void startGeneration()
      throws Exception
   {

      // read the service catalog and transform it to XmlObjects 
      String sCatalogXML = Util.loadFromFile(_sServiceCatalog);
      XmlObject catalog = new XmlObject(sCatalogXML);

      Pattern actionServicePattern = Pattern.compile("([A-Z]{3})(\\d+)");
      for (String sActionService : _sActionServices) {
         Matcher actionServiceMatcher = actionServicePattern.matcher(sActionService);
         if (actionServiceMatcher.matches()) {
            String sAction = actionServiceMatcher.group(1);
            String sService = actionServiceMatcher.group(2);
            // get the parameter-list for the acton/service combination
            String sParameters = getParameters(catalog, sAction, sService);
            if (sParameters != null && sParameters.length() > 0) {
               // if the parameters have been found, save the data.xml file
               String sFileName =
                  new StringBuilder()
                        .append(
                              Util.getProviderDataPath(_sBaseDir,
                                    "net/ifao/providerdata/bahn/nvs/leistkatalog")).append("/")
                        .append(sAction.toLowerCase()).append(sService).append("/data.xml")
                        .toString();
               if (Util.updateFile(sFileName, sParameters, _sbResult)) {
                  // if the data.xml file has been changed, create the schema (data.xsd)
                  generateSchema(sFileName.replaceFirst("data.xml$", "data.xsd"), sParameters,
                        sAction, sService);
               }
            }
            _sbResult.append("\n");
         }
      }

      // add a final comment to the result "log"
      if (_sbResult.indexOf("* ") >= 0) {
         _sbResult.append("_______________________________________\n\n");
         _sbResult.append("\nFiles/directories marked with * have to be submitted (in CVS) !\n");
      }

   }

   /**
    * Class SchemaData is a data structure used to create the schemas
    *
    * <p>
    * Copyright &copy; 2008, i:FAO Group GmbH
    * @author kaufmann
    */
   private class SchemaData
   {
      String _sName = null;
      boolean _bLeaf = false;
      XmlObject _param = null;
      boolean _brequired = false;
      List<SchemaData> _lstSubEle = null;
   }

   /**
    * Method generateSchema is the java implementation of the perl script lk2xsd.pl
    *
    * @param psSchemaFile file name of the resulting schema file
    * @param psParameters xml containing the definition of the schema (parameter-list within service
    * catalog)
    * @param psAction action code
    * @param psService service ID within the service catalog
    *
    * @author kaufmann
    */
   private void generateSchema(String psSchemaFile, String psParameters, String psAction,
                               String psService)
   {
      // set up the data structures by scanning the parameters
      Set<String> fullTagsHandled = new HashSet<String>();
      Map<String, SchemaData> elements = new HashMap<String, SchemaData>();
      List<SchemaData> listElements = new ArrayList<SchemaData>();

      String sPrevTag = null;
      XmlObject parameters = new XmlObject(psParameters).getFirstObject();
      XmlObject[] args = parameters.getObjects("parameter-arg");
      for (XmlObject arg : args) {
         String sFullTag = arg.getObject("full-tag").getCData();
         if (fullTagsHandled.add(sFullTag)) {
            String[] sTags = sFullTag.split("/");
            for (int i = sTags.length - 1; i >= 0; i--) {
               String sTag = sTags[i];
               if (i == sTags.length - 1) {
                  // leaf-tag
                  if (elements.containsKey(sTag)) {
                     // Leaf tag already exists; do nothing
                  } else {
                     // new leaf tag
                     SchemaData data = new SchemaData();
                     data._sName = sTag;
                     data._bLeaf = true;
                     data._param = arg;

                     listElements.add(data);
                     elements.put(sTag, data);
                  }
               } else {
                  // non-leaf tag
                  SchemaData schemaData = null;
                  if (elements.containsKey(sTag)) {
                     // Non-Leaf tag already exists
                     schemaData = elements.get(sTag);
                     // Overwrite optional/mandatory setting
                     if (arg.getObject("required").getCData().equalsIgnoreCase("true")) {
                        schemaData._brequired = true;
                     }
                  } else {
                     // Non-Leaf tag is new
                     schemaData = new SchemaData();
                     schemaData._sName = sTag;
                     schemaData._bLeaf = false;
                     schemaData._lstSubEle = new ArrayList<SchemaData>();
                     schemaData._brequired =
                        Boolean.parseBoolean(arg.getObject("required").getCData());

                     listElements.add(schemaData);
                     elements.put(sTag, schemaData);
                  }
                  // Check if child-tag is already in the sub element list
                  boolean bChildIsInList = false;
                  for (SchemaData subEle : schemaData._lstSubEle) {
                     if (subEle._sName.equals(sPrevTag)) {
                        bChildIsInList = true;
                        break;
                     }
                  }
                  if (!bChildIsInList) {
                     schemaData._lstSubEle.add(elements.get(sPrevTag));
                  }
               }
               sPrevTag = sTag;
            }
         }
      }

      // sort elements
      // sort W3C Schema elements
      Collections.sort(listElements, new Comparator<SchemaData>()
      {
         @Override
         public int compare(SchemaData pO1, SchemaData pO2)
         {
            return pO1._sName.compareTo(pO2._sName);
         }
      });

      // sort sub-element list for each W3C Schema element
      for (SchemaData element : listElements) {
         if (!element._bLeaf) {
            Collections.sort(element._lstSubEle, new Comparator<SchemaData>()
            {
               @Override
               public int compare(SchemaData pO1, SchemaData pO2)
               {
                  return pO1._sName.compareTo(pO2._sName);
               }
            });
         }
      }

      // create the schema
      StringBuilder sbSchema = new StringBuilder();
      // add header
      sbSchema.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      sbSchema.append("<!-- W3C Schema generated by Schema Generator -->\n");
      sbSchema
            .append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">\n");

      for (SchemaData element : listElements) {
         XmlObject param = element._param;
         if (element._bLeaf) {
            String sFullTag = param.getObject("full-tag").getCData();
            String sBezeichnung = param.getObject("bezeichnung").getCData();
            String sRequired = param.getObject("required").getCData();
            String sDefaultWert = param.getObject("default-wert").getCData();
            String sWertebereich = param.getObject("wertebereich").getCData();
            String sType = param.getObject("typ").getCData();
            String sMaxLaenge = param.getObject("max-laenge").getCData();
            String sCodestable = param.getObject("codestable").getCData();
            String sMessageId = param.getObject("message-id").getCData();

            sbSchema.append("<!--\n");
            sbSchema.append("name:         ").append(element._sName).append("\n");
            sbSchema.append("leaf:         yes\n");
            sbSchema.append("full-tag:     ").append(sFullTag).append("\n");
            sbSchema.append("bezeichnung:  ").append(sBezeichnung).append("\n");
            sbSchema.append("required:     ").append(sRequired).append("\n");
            sbSchema.append("default-wert: ").append(sDefaultWert).append("\n");
            sbSchema.append("wertebereich: ").append(sWertebereich).append("\n");
            sbSchema.append("typ:          ").append(sType).append("\n");
            sbSchema.append("max-laenge:   ").append(sMaxLaenge).append("\n");
            sbSchema.append("codestable:   ").append(sCodestable).append("\n");
            sbSchema.append("message-id:   ").append(sMessageId).append("\n");
            sbSchema.append("-->\n");

            sbSchema.append("<xs:element name=\"").append(element._sName).append("\" type=\"xs:")
                  .append(sType).append("\"/>\n");

         } else {
            sbSchema.append("<!--\n");
            sbSchema.append("name:         ").append(element._sName).append("\n");
            sbSchema.append("leaf:         no\n");
            sbSchema.append("required:     ").append(element._brequired).append("\n");
            sbSchema.append("-->\n");

            sbSchema.append("<xs:element name=\"").append(element._sName).append("\">\n");
            sbSchema.append("<xs:complexType>\n");
            sbSchema.append("  <xs:sequence>\n");

            for (SchemaData subEle : element._lstSubEle) {
               String sSubName = subEle._sName;
               boolean bSubLeaf = subEle._bLeaf;

               if ((bSubLeaf && subEle._param.getObject("required").getCData().equals("false"))
                     || (!bSubLeaf && !subEle._brequired)) {
                  if (element._sName.endsWith("List")) {
                     sbSchema.append("         <xs:element ref=\"").append(sSubName).append(
                           "\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n");
                  } else {
                     sbSchema.append("         <xs:element ref=\"").append(sSubName).append(
                           "\" minOccurs=\"0\"/>\n");
                  }
               } else {
                  if (element._sName.endsWith("List")) {
                     sbSchema.append("          <xs:element ref=\"").append(sSubName).append(
                           "\" maxOccurs=\"unbounded\"/>\n");
                  } else {
                     sbSchema.append("          <xs:element ref=\"").append(sSubName).append(
                           "\"/>\n");
                  }
               }
            }
            sbSchema.append("            </xs:sequence>\n");
            sbSchema.append("            </xs:complexType>\n");
            sbSchema.append("          </xs:element>\n");
         }
      }
      sbSchema.append("</xs:schema>\n\n");

      // update the schema on disk and enhance the batch file, if the schema has been changed
      Util.updateFile(psSchemaFile, sbSchema.toString(), _sbResult);
   }


   /**
    * Method getParameters scans the service catalog for the parameters of the action/service
    * passed
    *
    * @param pCatalog the service catalog
    * @param psAction the action to be searched within the service passed
    * @param psService the service ID to be searched
    *
    * @author kaufmann
    * @throws Exception 
    */
   private String getParameters(XmlObject pCatalog, String psAction, String psService)
      throws Exception
   {
      String sParameters = null;
      XmlObject catalog = pCatalog.getObject("leistungskatalog");
      XmlObject services = catalog.getObject("leistungen");
      XmlObject[] servicesArray = services.getObjects("leistung");

      boolean bFoundService = false;
      boolean bFoundAction = false;

      for (int i = 0; i < servicesArray.length; i++) {
         XmlObject service = servicesArray[i];
         XmlObject serviceId = service.getObject("leistungs-id");

         if (psService.equals(serviceId.getCData().trim())) {
            bFoundService = true;

            XmlObject msgDefinitions = service.getObject("nachricht-definition-liste");
            XmlObject[] msgDefinitionsArray = msgDefinitions.getObjects("nachricht-definition");

            for (int j = 0; j < msgDefinitionsArray.length; j++) {
               XmlObject msgDefinition = msgDefinitionsArray[j];
               XmlObject action = msgDefinition.getObject("aktion");

               if (psAction.equals(action.getCData().trim())) {
                  XmlObject parameterListe = msgDefinition.getObject("parameter-arg-liste");

                  if (parameterListe != null) {
                     _sbResult.append("Found parameter list for ").append(psAction).append(
                           psService).append("\n");
                     sParameters =
                        parameterListe.toString().replaceAll("\\&#228;", "ä").replaceAll("\\&#62;",
                              "&gt;").replaceAll("\\&#246;", "ö").replaceAll("\\&#223;", "ß")
                              .replaceAll("\\&#252;", "ü").replaceAll("\\&#220;", "Ü");
                  } else {
                     _sbResult.append("Found service ").append(psService).append(" action ")
                           .append(psAction).append(", but the parameter list is empty!\n\n");
                     sParameters = "";
                  }

                  bFoundAction = true;
                  break;
               }
            }
            if (!bFoundAction) {
               _sbResult.append("Service ").append(psService).append(
                     " found, but it does not have action ").append(psAction).append("\n");
            }
            break;
         }
      }

      if (!bFoundService) {
         _sbResult.append("Service ").append(psService).append(" not found\n");
      }

      return sParameters;
   }

}
