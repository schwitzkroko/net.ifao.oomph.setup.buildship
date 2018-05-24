package dtdinfo;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import ifaoplugin.Util;
import net.ifao.xml.XmlObject;


/**
 * Class DtdTransformerHandler
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
public class DtdTransformerHandler
{

   private String _sArcticDir = "";
   protected static String ATTRIBUTEMASK = "!:, \n\r\t";
   protected static String GENERATED_METHOD = "// Do not remove the following line !\n    // Generated Method";
   protected static String GENERATED_VERSION = "// Class generated with ";

   private List<String> _abstractList = new ArrayList<>();

   // ----------------------------------------------------------------------------
   //
   // ----------------------------------------------------------------------------

   private Hashtable<String, String> _sourceCodes;
   private Hashtable<String, String> _abstractMethods = new Hashtable<>();

   private TransformerMethod _defaultTransformerMethod;
   private XmlObject _xmlArcticPrnElementInfo;
   private Hashtable<String, String> _htElementNames;

   /**
    * Method getSourceCode
    *
    * @param psType
    * @param psName
    * @return SourceCode
    *
    * @author Andreas Brod
    */
   public String getSourceCode(String psType, String psName)
   {
      String sCode = "";

      psType += psName;
      //test

      if (_sourceCodes.get(psType) != null) {
         return _sourceCodes.get(psType);
      }

      String reqResType;
      if (psType.startsWith("Req")) {
         reqResType = "\\request\\";
      } else {
         reqResType = "\\response\\";
      }

      String srcGenFolder = _sArcticDir + "..\\src-gen\\";
      String configuration4ArcticFolder =
         _sArcticDir + "..\\..\\..\\arctic_configurations\\Configuration4Arctic\\src\\main\\generated-sources\\castor\\";
      String basePath = "net\\ifao\\arctic\\xml";
      String path = basePath + reqResType + psType + ".java";
      sCode = Util.loadFromFile(srcGenFolder + path, configuration4ArcticFolder + path);

      _sourceCodes.put(psType, sCode);

      return sCode;
   }

   /**
    * Method getAbstractItems
    *
    * @return AbstractItems
    *
    * @author Andreas Brod
    */
   public Iterator<String> getAbstractItems()
   {
      return _abstractList.iterator();
   }

   /**
    * Method setAttribute
    *
    * @param psType
    * @param psNAElement
    * @param psPNRElements
    *
    * @author Andreas Brod
    */
   private void setAttribute(String psType, String psNAElement, String psPNRElements)
   {

      // tokenize the newarctic element (the path of the
      // element is separated by "." e.g.
      // "RailReservation.Travellers.Person.Address.type")
      StringTokenizer stNAElement = new StringTokenizer(psNAElement, ".");
      String sSourceCode = null;
      TransformerMethod tm = _defaultTransformerMethod;
      String sLastToken = "";

      // loop over the "path" of newarctic element
      while (stNAElement.hasMoreTokens()) {
         String sToken = stNAElement.nextToken();
         String sTokenLow = sToken.substring(0, 1).toLowerCase() + sToken.substring(1);
         int iOk = 0;

         if (sSourceCode == null) {

            // for the first time
            iOk = 1;
         } else if (sSourceCode.indexOf(" " + psType + sToken + " ") > 0) {

            // e.g. "ReqTravellers" is found is sourceCode
            iOk = 2;
         } else if (sSourceCode.indexOf("." + psType + sToken + " ") > 0) {

            // e.g. "ReqTravellers" is found is sourceCode
            iOk = 2;
         } else if (sSourceCode.indexOf("List _" + sToken + "List ") > 0) {

            // e.g. listelement is found is sourceCode
            iOk = 3;
         } else if (sSourceCode.indexOf(sToken + "> _" + sTokenLow + "List;") > 0) {

            // e.g. listelement is found is sourceCode
            iOk = 3;
         } else if (sSourceCode.indexOf("atStart(\"" + sToken + "\"") > 0) {

            // e.g. explicit reference is found is sourceCode
            // which is also an indicator for a listelement
            iOk = 3;
         }

         if (iOk > 0) {

            // ... new Method can be called
            sSourceCode = getSourceCode(psType, sToken);

            if (sSourceCode.length() > 0) {

               // increase the transformer method is sourcecode
               // is found ...
               tm = tm.getSubMethod(psType, sToken, sSourceCode);
               sLastToken = "*";
            } else {
               sLastToken = sToken;
            }
         } else {

            // finally is is an attribute
            sLastToken = sToken;
         }
      }

      // set the attribute (of the last TransformerMethod)
      if (sLastToken.length() > 0) {
         tm.addAttribute(sLastToken, psPNRElements);
      }
   }

   /**
    * Constructor DtdTransformerHandler2
    *
    * @param psProvider TODO (brod) add text for param psProvider
    * @param psSourceList
    * @param psArcticDir
    * @param pSourceCodes
    * @param sPackageRoot
    *
    */
   public DtdTransformerHandler(String psProvider, String psSourceList, String psArcticDir,
                                Hashtable<String, String> pSourceCodes, String psPackageRoot)
   {
      _sourceCodes = pSourceCodes;
      _sArcticDir = psArcticDir;

      _htElementNames = new Hashtable<>();
      File fElements =
         new File(_sArcticDir + ("net.ifao.arctic.agents." + psPackageRoot + "framework.elements").replaceAll("\\.", "/"));
      if (fElements.exists()) {
         String[] listFiles = fElements.list();
         for (String listFile : listFiles) {
            if (listFile.endsWith(".java")) {
               String sName = listFile;
               sName = sName.substring(0, sName.lastIndexOf("."));
               _htElementNames.put(sName.toUpperCase(), sName);
            }
         }
      }

      _defaultTransformerMethod = new TransformerMethod(this, null, "", "", "", "", _abstractMethods);

      Util.writeToFile("Generator\\DtdTransformerHandler.lst", psSourceList);

      try {
         _xmlArcticPrnElementInfo = new XmlObject(Util.getConfFile(_sArcticDir + "..", "ArcticPnrElementInfos.xml"))
               .getFirstObject().createObject("PnrElementInfos", "provider", psProvider, true);
      }
      catch (FileNotFoundException e1) {
         _xmlArcticPrnElementInfo = null;
      }

      // read all lines
      BufferedReader reader = new BufferedReader(new StringReader(psSourceList));
      String sLine;
      String sType = "";

      try {
         while ((sLine = reader.readLine()) != null) {
            if (sLine.indexOf(".Pnr:") > 0) {
               sLine = sLine + "";
            }

            if (sLine.startsWith("<RES")) {
               sType = "Res";
            } else if (sLine.startsWith("<REQ")) {
               sType = "Req";
            } else if (sLine.indexOf(":") > 0) {

               // get new arctic element
               String sNAElement = sLine.substring(0, sLine.indexOf(":")).trim();

               // get Pnr Element(s)
               String sPnrElement = sLine.substring(sLine.indexOf(":") + 1).trim();

               // if there is a valid PNR Element
               if (sPnrElement.length() > 0) {
                  setAttribute(sType, sNAElement, sPnrElement);

                  // ... additionally the TransformerMethod is
                  // build for this attribute
               } else if (sNAElement.indexOf("_choice") > 0) {
                  setAttribute(sType, sNAElement, "_CHOICE");
               }
            }

         }

      }
      catch (IOException e) {

         e.printStackTrace();
      }

   }

   /**
    * TODO (brod) add comment for method getAttributeType
    *
    * @param psType TODO (brod) add text for param psType
    * @param psId TODO (brod) add text for param psId
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   public String getAttributeType(String psType, String psId)
   {
      XmlObject info = _xmlArcticPrnElementInfo.createObject("PnrElementInfo", "type", psType, true);
      XmlObject param = info.createObject("PnrElementParamInfo", "id", psId, true);
      String type = param.getAttribute("type");
      if (type.startsWith("DATE_")) {
         type = "Date";
      } else if (type.startsWith("TIME_")) {
         type = "Duration";
      } else if (type.startsWith("BOOLEAN")) {
         type = "boolean";
      } else if (type.equals("NUMBER")) {
         type = "int";
      } else if (type.equals("LONG")) {
         type = "long";
      } else if (type.equals("ENUMERATION")) {
         type = "Enum" + type;

      } else if (type.equals("AMOUNT") || type.equals("PERCENTAGE") || type.equals("GEO_COORDINATE")) {
         type = "double";
      } else {
         type = "String";
      }
      return type;
   }

   /**
    * Method getProtected
    *
    * @return
    *
    * @author Andreas Brod
    */
   private String getPrivateProtectedMembers()
   {
      StringBuffer sCode =
         new StringBuffer("\n// ----------------------------------------------------------------------------\n");

      sCode.append("//  D E F I N E   P R I V A T E / P R O T E C T E D   M E M B E R S\n");
      sCode.append("// ----------------------------------------------------------------------------\n");

      sCode.append("    protected Traveller _traveller;\n");
      sCode.append("    protected PnrElementFactory2 _elementFactory;\n");

      return sCode.toString();
   }

   /**
    * Method scrRequest returns the sourcecode accoring to _lstSource (which
    * was initialized in constructor)
    *
    * @return The complete courceCode for the transformer
    *
    * @author Andreas Brod
    */
   public String scrOfTransformer()
   {
      StringBuffer sbCode = new StringBuffer();

      TransformerMethod request = _defaultTransformerMethod.getFirstSubMethod("Req");
      TransformerMethod response = _defaultTransformerMethod.getFirstSubMethod("Res");

      // get the name for the request (which is the first element in the List)
      String sAttr = request.getMethodName().substring(3);

      // because sAttr contains now REQUEST.RequestName get only the requestName
      // sAttr = sAttr.substring(sAttr.lastIndexOf(".") + 1);
      sbCode.append(GENERATED_VERSION + "DtdInfo version " + Util.getVERSION() + "\n");

      sbCode.append(getPrivateProtectedMembers());

      sbCode.append("\n// ----------------------------------------------------------------------------\n");
      sbCode.append("//  C O N V E R T   T H E   R E Q U E S T - O B J E C T\n");
      sbCode.append("// ----------------------------------------------------------------------------\n\n");
      sbCode.append("\n    /**\n");
      sbCode.append("     * Method getBase\n");
      sbCode.append("     *\n");
      sbCode.append("     * @param pReqRequest The newarctic Request object\n");
      sbCode.append("     * @param pPTD The PnrTransportData to which the Elements have to be written\n");
      sbCode.append("     * @param pTraveller The Traveller Object\n");
      sbCode.append("     * @author _GENERATOR_\n");
      sbCode.append("     */\n");
      sbCode.append("    @Override\n");

      sbCode.append("    protected void getBase(ReqRequest pReqRequest, PnrTransportData pPTD, Traveller pTraveller)\n");
      sbCode.append("    {\n");
      sbCode.append("        _traveller = pTraveller;\n");
      sbCode.append("        _PTD = pPTD;\n");
      sbCode.append("        _elementFactory = ThreadDataManager.getInstance().getFactory();\n");

      if (sAttr.length() == 0) {
         sbCode.append("        // Response not defined\n");

      } else {
         //         sbCode.append("        try {\n");
         //         sbCode.append("            add" + sAttr + "(pReqRequest.getRequestChoice().get" + sAttr
         //               + "());\n");
         //         sbCode.append("        } catch (Exception ex) {\n");
         //         sbCode.append("          // avoid nullpointer Exceptions\n");
         //         sbCode.append("        }\n");
         sbCode.append("        add" + sAttr + "(pReqRequest.getRequestChoice().get" + sAttr + "());\n");
      }

      sbCode.append("    }\n\n");
      sbCode.append("\n");

      // get now the sourcecode for Request
      sbCode.append(request.toSourceCode(""));

      // load now the Responseobjects into the SubList
      sbCode.append("\n// ----------------------------------------------------------------------------\n");
      sbCode.append("//  C O N V E R T   T H E   R E S P O N S E - O B J E C T\n");
      sbCode.append("// ----------------------------------------------------------------------------\n");

      // ... and get the ResponseType
      sAttr = response.getMethodName().substring(3);

      // sAttr = sAttr.substring(sAttr.lastIndexOf(".") + 1);

      sbCode.append("\n    /**\n");
      sbCode.append("     * Method getBase\n");
      sbCode.append("     *\n");
      sbCode.append("     * @param pResponse The newarctic Response object which has to be filled\n");
      sbCode.append("     * @param pPTD The PnrTransportData with the Elements\n");
      sbCode.append("     * @param pTraveller The Traveller Object\n");
      sbCode.append("     * @return true if success\n");
      sbCode.append("     * @author _GENERATOR_\n");
      sbCode.append("     */\n");
      sbCode.append("    @Override\n");
      sbCode.append("    protected boolean setResponse(ResResponse pResponse, PnrTransportData pPTD, Traveller pTraveller)\n");
      sbCode.append("    {\n");
      sbCode.append("        _traveller = pTraveller;\n");
      sbCode.append("        _PTD = pPTD;\n");
      sbCode.append("        _elementFactory = ThreadDataManager." + "getInstance().getFactory();\n");
      sbCode.append("        \n");

      sbCode.append("        ResponseChoice choice = new ResponseChoice();\n");
      sbCode.append("        pResponse.setResponseChoice(choice);\n");
      if (sAttr.length() == 0) {
         sbCode.append("        // no PnrElements defined\n");
      } else {
         sbCode.append("        choice.set" + sAttr + "(get" + sAttr + "(new PnrElementBase[0]));\n");
      }

      sbCode.append("        \n");
      sbCode.append("        return true;\n");
      sbCode.append("    }\n");
      sbCode.append("\n");

      // ... now get the sourcode for the Response
      sbCode.append(response.toSourceCode(""));

      if (sbCode.indexOf("// set the Tag Object by default") > 0) {
         StringBuilder sText =
            new StringBuilder("\n    private Hashtable<String, Integer> _htTagList = new Hashtable<String, Integer>();\n");

         sText.append("\n");
         sText.append("    /**\n");
         sText.append("     * Method getTag\n");
         sText.append("     *\n");
         sText.append("     * @param sValue\n");
         sText.append("     *\n");
         sText.append("     * @return\n");
         sText.append("     * @author Andreas Brod\n");
         sText.append("     */\n");
         sText.append("    private ResTag getTag(String sValue)\n");
         sText.append("    {\n");
         sText.append("        ResTag resTag = new ResTag();\n");
         sText.append("        Integer itemId = _htTagList.get(sValue);\n");
         sText.append("\n");
         sText.append("        if (itemId == null) {\n");
         sText.append("            itemId = Integer.valueOf(1);\n");
         sText.append("        } else {\n");
         sText.append("            itemId = Integer.valueOf(itemId.intValue() + 1);\n");
         sText.append("        }\n");
         sText.append("\n");
         sText.append("        _htTagList.put(sValue, itemId);\n");
         sText.append("        resTag.setProviderId(_PTD.getAdditionalPnrInfo().getProviderId());\n");
         sText.append("        resTag.setItemId(itemId.toString());\n");
         sText.append("\n");
         sText.append("        return resTag;\n");
         sText.append("    }\n");
         sText.append("\n");

         sbCode.append(sText.toString());
      }

      if (_abstractMethods.size() > 0) {
         sbCode.append("\n// ----------------------------------------------------------------------------\n");
         sbCode.append("//  A B S T R A C T   M E T H O D S\n");
         sbCode.append("// ----------------------------------------------------------------------------\n");

         Enumeration<String> keys = _abstractMethods.keys();

         while (keys.hasMoreElements()) {
            String sKey = keys.nextElement();

            System.out.println(sKey);

            String sAbstractMethod = _abstractMethods.get(sKey);

            String sAbstractMethod2 = sAbstractMethod.substring(0, sAbstractMethod.indexOf("{")) + ";\n";

            _abstractList.add(Util.replaceString(sAbstractMethod, "protected abstract ", "protected "));

            sAbstractMethod2 = sAbstractMethod2.replaceAll(TransformerMethod.ANNOTATION_OVERRIDE, "");
            sbCode.append(sAbstractMethod2 + "\n");
         }
      }

      String sCode = sbCode.toString();
      if (sCode.indexOf("assignResponseTraveller") > 0) {
         int iBooleanSetResponse = sCode.indexOf("boolean setResponse");
         // correct the setResponse
         if (iBooleanSetResponse > 0) {
            iBooleanSetResponse = sCode.indexOf("return true", iBooleanSetResponse);
            if (iBooleanSetResponse > 0) {
               // add the validateMethod
               sCode = sCode.substring(0, iBooleanSetResponse) + "validateResponseTraveller();\n        "
                     + sCode.substring(iBooleanSetResponse);
               // and add the methods
               sCode += appendResponseTraveller();
            }
         }
      }
      if (sCode.indexOf("assignResponseSegment") > 0) {
         String sValidateResponseSegment = "";
         List<String> lst = new ArrayList<>();

         if (sCode.indexOf("assignResponseSegmentAir") > 0) {
            lst.add("Air");
            sCode += appendResponseSegment("Air", null);
            sValidateResponseSegment += "validateResponseSegment(htResSegmentAir, htResObjSegmentsAir);\n        ";
         }
         if (sCode.indexOf("assignResponseSegmentCar") > 0) {
            lst.add("Car");
            sCode += appendResponseSegment("Car", null);
            sValidateResponseSegment += "validateResponseSegment(htResSegmentCar, htResObjSegmentsCar);\n        ";
         }
         if (sCode.indexOf("assignResponseSegmentHotel") > 0) {
            lst.add("Hotel");
            sCode += appendResponseSegment("Hotel", null);
            sValidateResponseSegment += "validateResponseSegment(htResSegmentHotel, htResObjSegmentsHotel);\n        ";
         }
         if (sCode.indexOf("assignResponseSegmentCtwItem") > 0) {
            lst.add("CtwItem");
            sCode += appendResponseSegment("CtwItem", null);
            sValidateResponseSegment += "validateResponseSegment(htResSegmentCtwItem, htResObjSegmentsCtwItem);\n        ";
         }
         if (sCode.indexOf("assignResponseSegmentRail") > 0) {
            lst.add("Rail");
            sCode += appendResponseSegment("Rail", null);
            sValidateResponseSegment += "validateResponseSegment(htResSegmentRail, htResObjSegmentsRail);\n        ";
         }
         if (sCode.indexOf("assignResponseSegmentOther") > 0) {
            sCode += appendResponseSegment("Other", lst);
            sValidateResponseSegment += "validateResponseSegment(htResSegmentOther, htResObjSegmentsOther);\n        ";
         }


         int iBooleanSetResponse = sCode.indexOf("boolean setResponse");
         // correct the setResponse
         if (iBooleanSetResponse > 0) {
            iBooleanSetResponse = sCode.indexOf("return true", iBooleanSetResponse);
            if (iBooleanSetResponse > 0) {
               // add the validateMethod
               sCode = sCode.substring(0, iBooleanSetResponse) + sValidateResponseSegment + sCode.substring(iBooleanSetResponse);
               // and add the methods
               sCode += appendResponseSegment("", null);
            }
         }

      }
      return sCode;
   }

   /**
    * method appendResponseTraveller
    * creates code to correct the for attributes of the arctic response
    *
    * @return code to correct the for attributes of the arctic response
    *
    * @author brod
    */
   private String appendResponseTraveller()
   {
      StringBuffer sb = new StringBuffer();
      sb.append("   // private members, to handle Traveller assignments within response  \n");
      sb.append("\n");
      sb.append(
            "   private TreeMap<Integer, HashSet<PnrElementBase>> htResTraveller = new TreeMap<Integer, HashSet<PnrElementBase>>();\n");
      sb.append("   private List<ResponseTraveller> htResObjects = new ArrayList<ResponseTraveller>();\n");
      sb.append("\n");
      sb.append("   /** \n");
      sb.append("    * The method assignResponseTraveller assigns a pnrElement to an Id\n");
      sb.append("    * \n");
      sb.append("    * @param piPerson The person\n");
      sb.append("    * @param pnrBase The PnrElement\n");
      sb.append("    * \n");
      sb.append("    * @author _GENERATOR_ \n");
      sb.append("    */\n");
      sb.append("   protected void assignResponseTraveller(int piPerson, PnrElementBase pnrBase)\n");
      sb.append("   {\n");
      sb.append("      Integer person = Integer.valueOf(piPerson);\n");
      sb.append("      HashSet<PnrElementBase> lst = htResTraveller.get(person);\n");
      sb.append("      if (lst == null) {\n");
      sb.append("         lst = new HashSet<PnrElementBase>();\n");
      sb.append("         htResTraveller.put(person, lst);\n");
      sb.append("      }\n");
      sb.append("      lst.add(pnrBase);\n");
      sb.append("   }\n");
      sb.append("\n");
      sb.append("   /** \n");
      sb.append("    * The method assignResponseTraveller assigns a response object\n");
      sb.append("    * to a pnr Element\n");
      sb.append("    * \n");
      sb.append("    * @param pObject The ResponseObject with the method getFor\n");
      sb.append("    * @param pPnrBase The related PnrElement\n");
      sb.append("    * \n");
      sb.append("    * @author _GENERATOR_\n");
      sb.append("    */\n");
      sb.append("   protected void assignResponseTraveller(Object pObject, PnrElementBase pPnrBase)\n");
      sb.append("   {\n");
      sb.append("      if ((pPnrBase != null) && (pObject != null)) {\n");
      sb.append("         htResObjects.add(new ResponseTraveller(pObject, pPnrBase));\n");
      sb.append("      }\n");
      sb.append("   }\n");
      sb.append("\n");
      sb.append("   /** \n");
      sb.append("    * The class ResponseTraveller is a DataContainer which contains\n");
      sb.append("    * the object with the setFor method and the related pnrElement\n");
      sb.append("    * \n");
      sb.append("    * <p>\n");
      sb.append("    * Copyright &copy; 2007, i:FAO \n");
      sb.append("    * \n");
      sb.append("    * @author _GENERATOR_\n");
      sb.append("    */\n");
      sb.append("   static class ResponseTraveller\n");
      sb.append("   {\n");
      sb.append("      Object objWith4Method;\n");
      sb.append("      PnrElementBase pnrBase;\n");
      sb.append("\n");
      sb.append("      /**\n");
      sb.append("       * Constructor for ResponseTraveller\n");
      sb.append("       *\n");
      sb.append("       * @param pObject generic object\n");
      sb.append("       * @param pPnrBase PnrElementBase object\n");
      sb.append("       * @return ResponseTraveller object\n");
      sb.append("       */\n");
      sb.append("      ResponseTraveller(Object pObject, PnrElementBase pPnrBase)\n");
      sb.append("      {\n");
      sb.append("         objWith4Method = pObject;\n");
      sb.append("         pnrBase = pPnrBase;\n");
      sb.append("      }\n");
      sb.append("\n");
      sb.append("   }\n");
      sb.append("\n");
      sb.append("   /** \n");
      sb.append("    * The method validateResponseTraveller \'merges\' the associations \n");
      sb.append("    * \n");
      sb.append("    * @author _GENERATOR_\n");
      sb.append("    */\n");
      sb.append("   private void validateResponseTraveller()\n");
      sb.append("   {\n");
      sb.append("      StringBuffer sbAssociations = new StringBuffer(\">>> Correct for-attributes within response\\n\");\n");
      sb.append("      // loop over all Travellers\n");
      sb.append("      for (Entry<Integer, HashSet<PnrElementBase>> entry : htResTraveller.entrySet()) {\n");
      sb.append("         Integer id = entry.getKey();\n");
      sb.append("         for (PnrElementBase base4Id : entry.getValue()) {\n");
      sb.append("\n");
      sb.append("            sbAssociations.append(\"Traveller: \" + id + \" (\" + base4Id.getShortDump() + \")\\n\");\n");
      sb.append("            // validate if this associated to any object\n");
      sb.append("            for (Object element : htResObjects) {\n");
      sb.append("               ResponseTraveller item = (ResponseTraveller) element;\n");
      sb.append("\n");
      sb.append("               PnrElementList associatedElements = base4Id.getAssociatedElements(item.pnrBase\n");
      sb.append("                     .getType());\n");
      sb.append("               for (Enumeration<PnrElementBase> lstOfElements = associatedElements.elements(); lstOfElements\n");
      sb.append("                     .hasMoreElements();) {\n");
      sb.append("\n");
      sb.append("                  PnrElementBase nextElement = lstOfElements.nextElement();\n");
      sb.append("                  if (nextElement.getId() == item.pnrBase.getId()) {\n");
      sb.append("                     Object[] ids = { id };\n");
      sb.append("                     Class<?>[] cls = { int.class };\n");
      sb.append("                     try {\n");
      sb.append("                        item.objWith4Method.getClass().getMethod(\"setFor\", cls).invoke(\n");
      sb.append("                              item.objWith4Method, ids);\n");
      sb.append("\n");
      sb.append("                        sbAssociations.append(\"associated to \" + nextElement.getShortDump() + \"\\n\");\n");
      sb.append("                     }\n");
      sb.append("                     catch (Exception e) {\n");
      sb.append("                        // try to use setFor(String); in case there is already something in the for\n");
      sb.append("                        // attribute, concatenate the values (NMTOKEN)\n");
      sb.append("                        String sNewFor = id.toString();\n");
      sb.append("                        try {\n");
      sb.append("                           String sOldFor = (String) item.objWith4Method.getClass().getMethod(\n");
      sb.append("                                 \"getFor\", (Class[]) null).invoke(item.objWith4Method,\n");
      sb.append("                                 (Object[]) null);\n");
      sb.append("                           if (sOldFor != null) {\n");
      sb.append("                              if ((\" \" + sOldFor + \" \").indexOf(\" \" + sNewFor + \" \") > -1) {\n");
      sb.append("                                 sbAssociations.append(\"was already \");\n");
      sb.append("                              } else {\n");
      sb.append("                                 sNewFor = sOldFor + \" \" + sNewFor;\n");
      sb.append("                              }\n");
      sb.append("                           }\n");
      sb.append("                           item.objWith4Method.getClass().getMethod(\"setFor\",\n");
      sb.append("                                 new Class[]{ String.class }).invoke(item.objWith4Method,\n");
      sb.append("                                 new Object[]{ sNewFor });\n");
      sb.append("                           sbAssociations.append(\"associated to \" + nextElement.getShortDump()\n");
      sb.append("                                 + \"\\n\");\n");
      sb.append("                        }\n");
      sb.append("                        catch (Exception ex) {\n");
      sb.append("                           sbAssociations\n");
      sb.append("                                 .append(\"Caught the following exception while associating Traveller \"\n");
      sb.append("                                       + id + \" (\" + base4Id.getShortDump() + \") and \"\n");
      sb.append("                                       + nextElement.getShortDump() + \":\\n\" + ex.getMessage()\n");
      sb.append("                                       + \"\\n\");\n");
      sb.append("                        }\n");
      sb.append("                     }\n");
      sb.append("                  }\n");
      sb.append("               }\n");
      sb.append("            }\n");
      sb.append("         }\n");
      sb.append("      }\n");
      sb.append("      getLog().finest(sbAssociations.toString());\n");
      sb.append("   }\n");
      sb.append("\n");
      return sb.toString();
   }

   /**
    * method appendResponseSegment
    * creates code to correct the segment attributes of the arctic response
    *
    * <p> TODO rename sType to psType, lstTypes to pTypes
    * @param sType TODO (brod) add text for param sType
    * @param lstTypes TODO (brod) add text for param lstTypes
    * @return code to correct the segment attributes of the arctic response
    *
    * @author brod
    */
   private String appendResponseSegment(String sType, List<String> lstTypes)
   {
      StringBuffer sb = new StringBuffer();
      if (sType.length() > 0) {
         sb.append("   // private members, to handle Segment assignments within response  \n");
         sb.append("\n");
         sb.append("   private TreeMap<Integer, HashSet<PnrElementBase>> htResSegment" + sType
               + " = new TreeMap<Integer, HashSet<PnrElementBase>>();\n");
         sb.append("   private List<ResponseSegment> htResObjSegments" + sType + " = new ArrayList<ResponseSegment>();\n");
         sb.append("\n");
         sb.append("   private int _iSegmentCounter" + sType + "=0;\n");
         sb.append("   /** \n");
         sb.append("    * The method assignResponseSegment" + sType + " assigns a pnrElement to an " + sType + "-Id \n");
         sb.append("    * \n");
         sb.append("    * @param pnrBase The PnrElement\n");
         sb.append("    * \n");
         sb.append("    * @author _GENERATOR_\n");
         sb.append("    */\n");
         sb.append("   protected void assignResponseSegment" + sType + "(PnrElementBase pnrBase)\n");
         sb.append("   {\n");
         sb.append("      // pnrBase may be assigned only once\n");
         sb.append("      for (int i = 1; i <= _iSegmentCounter" + sType + "; i++) {\n");
         sb.append("         HashSet<PnrElementBase> lst = htResSegment" + sType + ".get(Integer.valueOf(i));\n");
         sb.append("         if ((lst != null) && lst.contains(pnrBase)) {\n");
         sb.append("            // ignore, because PnrElement is already assigned \n");
         sb.append("            return;\n");
         sb.append("         }\n");
         sb.append("      }\n");

         sb.append("      Integer iCustomReferenceNumber" + sType + " = pnrBase.getCustomReferenceNumber();\n");
         sb.append("      if (iCustomReferenceNumber" + sType + " != null) {\n");
         sb.append("         _iSegmentCounter" + sType + " = iCustomReferenceNumber" + sType + ";\n");
         sb.append("      } else {\n");
         sb.append("         _iSegmentCounter" + sType + "++;\n");
         sb.append("      }\n");

         sb.append("      Integer person = Integer.valueOf(_iSegmentCounter" + sType + ");\n");
         sb.append("      HashSet<PnrElementBase> lst = htResSegment" + sType + ".get(person);\n");
         sb.append("      if (lst == null) {\n");
         sb.append("         lst = new HashSet<PnrElementBase>();\n");
         sb.append("         htResSegment" + sType + ".put(person, lst);\n");
         sb.append("      }\n");
         sb.append("      lst.add(pnrBase);\n");
         sb.append("   }\n");
         sb.append("\n");
         sb.append("   /** \n");
         sb.append("    * The method assignResponseSegment" + sType + " assigns a response object \n");
         sb.append("    * to a pnr Element \n");
         sb.append("    * \n");
         sb.append("    * @param pObject The ResponseObject with the method getSegment \n");
         sb.append("    * @param pPnrBase The related PnrElement \n");
         sb.append("    * \n");
         sb.append("    * @author _GENERATOR_\n");
         sb.append("    */\n");
         sb.append("   protected void assignResponseSegment" + sType + "(Object pObject, PnrElementBase pPnrBase)\n");
         sb.append("   {\n");
         if (lstTypes == null) {
            sb.append("      if ((pPnrBase != null) && (pObject != null)) {\n");
            sb.append("         htResObjSegments" + sType + ".add(new ResponseSegment(pObject, pPnrBase));\n");
         } else {
            sb.append("      if ((pPnrBase != null) && (pObject != null)) {\n");
            if (!lstTypes.contains(sType)) {
               lstTypes.add(sType);
            }
            for (int i = 0; i < lstTypes.size(); i++) {
               sb.append("         htResObjSegments" + lstTypes.get(i) + ".add(new ResponseSegment(pObject, pPnrBase));\n");
            }
         }
         sb.append("      }\n");
         sb.append("   }\n");
         sb.append("\n");
      } else {
         sb.append("   /** \n");
         sb.append("    * The method validateResponseSegment \'merges\' the associations \n");
         sb.append("    * \n");
         sb.append("    * @param phtResSegment Tree Map of integers and hashsets of PnrElementBases\n");
         sb.append("    * @param phtResObjSegments List of responseSegments\n");
         sb.append("    * @author _GENERATOR_\n");
         sb.append("    */\n");
         sb.append("   private void validateResponseSegment(TreeMap<Integer, " + "HashSet<PnrElementBase>> phtResSegment,\n"
               + "        List<ResponseSegment> phtResObjSegments)\n");
         sb.append("   {\n");
         sb.append("      StringBuffer sbAssociations = new StringBuffer(\n");
         sb.append("            \">>> Correct segment-attributes within response\\n\");\n");
         sb.append("      // loop over all Segments\n");
         sb.append("      for (Entry<Integer, HashSet<PnrElementBase>> entry : phtResSegment.entrySet()) {\n");
         sb.append("         Integer segmentId = entry.getKey();\n");
         sb.append("         for (PnrElementBase base4SegmentId : entry.getValue()) {\n");
         sb.append("\n");
         sb.append("            sbAssociations.append(\"Segment: \" + segmentId + \" (\" + base4SegmentId.getShortDump()\n");
         sb.append("                  + \")\\n\");\n");
         sb.append("            // validate if this associated to any object\n");
         sb.append("            for (ResponseSegment responseItem : phtResObjSegments) {\n");
         sb.append("               PnrElementList assocElements4SegmentId = base4SegmentId\n");
         sb.append("                     .getAssociatedElements(responseItem.pnrBase.getType());\n");
         sb.append("               // Add also the item, to validate also the same element\n");
         sb.append("               if ((assocElements4SegmentId.size() == 0)\n");
         sb.append("                     && responseItem.pnrBase.getType().equals(base4SegmentId.getType())) {\n");
         sb.append("                  assocElements4SegmentId.addPnrElement(base4SegmentId);\n");
         sb.append("               }\n");
         sb.append(
               "               for (Enumeration<PnrElementBase> lstOfElements = assocElements4SegmentId.elements(); lstOfElements\n");
         sb.append("                     .hasMoreElements();) {\n");
         sb.append("\n");
         sb.append("                  PnrElementBase assocElement = lstOfElements.nextElement();\n");
         sb.append("                  if (assocElement.getId() == responseItem.pnrBase.getId()) {\n");
         sb.append("                     Object[] ids = { segmentId };\n");
         sb.append("                     Class<?>[] cls = { int.class };\n");
         sb.append("                     try {\n");
         sb.append("                        responseItem.objWithSegmentMethod.getClass().getMethod(\"setSegment\", cls)\n");
         sb.append("                              .invoke(responseItem.objWithSegmentMethod, ids);\n");
         sb.append("\n");
         sb.append("                        sbAssociations\n");
         sb.append("                              .append(\"associated to \" + assocElement.getShortDump() + \"\\n\");\n");
         sb.append("                     }\n");
         sb.append("                     catch (Exception e) {\n");
         sb.append(
               "                        // try to use setSegment(String); in case there is already something in the segment\n");
         sb.append("                        // attribute, concatenate the values (NMTOKEN)\n");
         sb.append("                        String sNewSegment = segmentId.toString();\n");
         sb.append("                        try {\n");
         sb.append("                           String sOldSegment = (String) responseItem.objWithSegmentMethod\n");
         sb.append("                                 .getClass().getMethod(\"getSegment\", (Class[]) null).invoke(\n");
         sb.append("                                       responseItem.objWithSegmentMethod, (Object[]) null);\n");
         sb.append("                           if (sOldSegment != null) {\n");
         sb.append(
               "                              if ((\" \" + sOldSegment + \" \").indexOf(\" \" + sNewSegment + \" \") > -1) {\n");
         sb.append("                                 sbAssociations.append(\"was already \");\n");
         sb.append("                              } else {\n");
         sb.append("                                 sNewSegment = sOldSegment + \" \" + sNewSegment;\n");
         sb.append("                              }\n");
         sb.append("                           }\n");
         sb.append("                           responseItem.objWithSegmentMethod.getClass().getMethod(\"setSegment\",\n");
         sb.append("                                 new Class[]{ String.class }).invoke(\n");
         sb.append("                                 responseItem.objWithSegmentMethod, new Object[]{ sNewSegment });\n");
         sb.append("                           sbAssociations.append(\"associated to \" + assocElement.getShortDump()\n");
         sb.append("                                 + \"\\n\");\n");
         sb.append("                        }\n");
         sb.append("                        catch (Exception ex) {\n");
         sb.append("                           sbAssociations\n");
         sb.append("                                 .append(\"Caught the following exception while associating Segment \"\n");
         sb.append("                                       + segmentId\n");
         sb.append("                                       + \" (\"\n");
         sb.append("                                       + base4SegmentId.getShortDump()\n");
         sb.append("                                       + \") and \"\n");
         sb.append("                                       + assocElement.getShortDump()\n");
         sb.append("                                       + \":\\n\"\n");
         sb.append("                                       + ex.getMessage() + \"\\n\");\n");
         sb.append("                        }\n");
         sb.append("                     }\n");
         sb.append("                  }\n");
         sb.append("               }\n");
         sb.append("            }\n");
         sb.append("         }\n");
         sb.append("      }\n");
         sb.append("      getLog().finest(sbAssociations.toString());\n");
         sb.append("   }\n");
         sb.append("\n");
         sb.append("   /** \n");
         sb.append("    * The class ResponseSegment is a DataContainer which contains\n");
         sb.append("    * the object with the setSegment method and the related pnrElement\n");
         sb.append("    * \n");
         sb.append("    * <p> \n");
         sb.append("    * Copyright &copy; 2007, i:FAO \n");
         sb.append("    * \n");
         sb.append("    * @author _GENERATOR_\n");
         sb.append("    */\n");
         sb.append("   static class ResponseSegment\n");
         sb.append("   {\n");
         sb.append("      Object objWithSegmentMethod;\n");
         sb.append("      PnrElementBase pnrBase;\n");
         sb.append("\n");
         sb.append("      /**\n");
         sb.append("       * Constructor for ResponseSegment\n");
         sb.append("       *\n");
         sb.append("       * @param pObject generic object\n");
         sb.append("       * @param pPnrBase PnrElementBase object\n");
         sb.append("       * @return ResponseSegment object\n");
         sb.append("       */\n");
         sb.append("      ResponseSegment(Object pObject, PnrElementBase pPnrBase)\n");
         sb.append("      {\n");
         sb.append("         objWithSegmentMethod = pObject;\n");
         sb.append("         pnrBase = pPnrBase;\n");
         sb.append("      }\n");
         sb.append("\n");
         sb.append("   }\n");
         sb.append("\n");
      }
      return sb.toString();
   }

   private Hashtable<String, List<String>> _lstNames = new Hashtable<>();

   /**
    * TODO (brod) add comment for method getClassName
    *
    * @param psType TODO (brod) add text for param psType
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   public String getClassName(String psType)
   {
      XmlObject info = _xmlArcticPrnElementInfo.createObject("PnrElementInfo", "type", psType, true);
      String sClassName = info.getAttribute("className");
      if (sClassName.length() > 0) {
         String sDir = _sArcticDir + sClassName.substring(0, sClassName.lastIndexOf(".")).replaceAll("\\.", "/");
         List<String> listFiles = _lstNames.get(sDir);
         if (listFiles == null) {
            listFiles = new ArrayList<>();
            _lstNames.put(sDir, listFiles);

            File f = new File(sDir);
            if (f.exists()) {
               String[] files = f.list();
               for (String file : files) {
                  if (file.endsWith(".java")) {
                     listFiles.add(file.substring(0, file.lastIndexOf(".")));
                  }
               }
            }
         }
         sClassName = sClassName.substring(sClassName.lastIndexOf(".") + 1);
         // correct camelCase
         for (int i = 0; i < listFiles.size(); i++) {
            if (listFiles.get(i).toLowerCase().equals(sClassName.toLowerCase())) {
               sClassName = listFiles.get(i);
            }
         }

         if (sClassName.startsWith("Element")) {
            sClassName = sClassName.substring(7);
         }
      } else {
         String sType = psType.equals(psType.toUpperCase()) ? psType.toLowerCase() : psType;
         sClassName = Util.camelCase(sType);
      }
      // TODO Auto-generated method stub
      return sClassName;
   }

   protected String getRealClassName(String psClassName)
   {
      String sName = _htElementNames.get(psClassName.toUpperCase());
      if (sName != null) {
         return sName;
      }

      return psClassName;
   }

}


//----------------------------------------------------------------------------
//TransformerMethod
//----------------------------------------------------------------------------

/**
 * Class TransformerMethod
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class TransformerMethod
{

   private String _sType, _sName, _sDeep, _sSourceCode;
   private boolean _bRebookingType = false;
   private List<String> _pnrElements = new Vector<>();
   private TransformerMethod _tmParent;
   private Hashtable<String, String> _abstractMethods;
   private String _sLastSegmentId = "";

   private static final String SUPPRESS_WARNINGS_UNCHECKED = "    @SuppressWarnings(\"unchecked\")\n";
   protected static final String ANNOTATION_OVERRIDE = "    @Override\n";

   /**
    * Constructor TransformerMethod
    *
    * @param pDtdTransformerHandler
    * @param pParent
    * @param psType
    * @param psName
    * @param psDeep
    * @param psSourceCode
    * @param pAbstractMethods
    *
    */
   protected TransformerMethod(DtdTransformerHandler pDtdTransformerHandler, TransformerMethod pParent, String psType,
                               String psName, String psDeep, String psSourceCode, Hashtable<String, String> pAbstractMethods)
   {
      _tmParent = pParent;
      _sType = psType;
      _sName = psName;
      _sDeep = psDeep;
      _sSourceCode = psSourceCode;
      _abstractMethods = pAbstractMethods;
      _dtdTransformerHandler = pDtdTransformerHandler;
   }

   //--------------------------------------------------------------------------
   //Attributes
   //--------------------------------------------------------------------------

   private Hashtable<String, String> _htAttributes = new Hashtable<>();
   private List<String> _lstAttributeKeys = new Vector<>();

   /**
    * Method getAttributeKeys
    *
    * @return
    *
    * @author Andreas Brod
    */
   private Iterator<String> getAttributeKeys()
   {
      return _lstAttributeKeys.iterator();
   }

   /**
    * Method getAttribute
    *
    * @param psKey
    * @return
    *
    * @author Andreas Brod
    */
   private String getAttribute(String psKey)
   {
      String string = _htAttributes.get(psKey);
      if (string == null) {
         string = _htAttributes.get(psKey + "!");
      }
      return string;
   }

   /**
    * Method setAttribute
    *
    * @param psKey
    * @param psValue
    *
    * @author Andreas Brod
    */
   private void setAttribute(String psKey, String psValue)
   {
      if (!_lstAttributeKeys.contains(psKey)) {
         _lstAttributeKeys.add(psKey);
      }

      _htAttributes.put(psKey, psValue);
   }

   //--------------------------------------------------------------------------
   //Sub Methods
   //--------------------------------------------------------------------------
   private Hashtable<String, TransformerMethod> _subMethods = new Hashtable<>();
   private List<String> _lstSubMethod = new Vector<>();
   private DtdTransformerHandler _dtdTransformerHandler;
   private String _sLastCtwItemId = "";

   /**
    * Method getSubMethodKeys
    *
    * @return
    *
    * @author Andreas Brod
    */
   private Iterator<String> getSubMethodKeys()
   {
      return _lstSubMethod.iterator();
   }

   /**
    * Method getSubMethod
    *
    * @param psKey
    * @return
    *
    * @author Andreas Brod
    */
   private TransformerMethod getSubMethod(String psKey)
   {
      return _subMethods.get(psKey);
   }

   /**
    * Method getFirstSubMethod
    *
    * <p> TODO rename sKey to psKey
    * @param sKey
    * @return
    *
    * @author Andreas Brod
    */
   protected TransformerMethod getFirstSubMethod(String sKey)
   {
      for (String element : _subMethods.keySet()) {
         if (element.startsWith(sKey)) {
            return _subMethods.get(element);
         }
      }

      return new TransformerMethod(_dtdTransformerHandler, _tmParent, "", "", "", "", _abstractMethods);
   }

   /**
    * Method setSubMethod
    *
    * @param psKey
    * @param pValue
    *
    * @author Andreas Brod
    */
   private void setSubMethod(String psKey, TransformerMethod pValue)
   {
      if (psKey.length() > 0) {
         if (!_lstSubMethod.contains(psKey)) {
            _lstSubMethod.add(psKey);
         }

         _subMethods.put(psKey, pValue);
      }
   }

   /**
    * Method isResponse
    *
    * @return
    *
    * @author Andreas Brod
    */
   private boolean isResponse()
   {
      return _sType.equals("Res");
   }

   /**
    * Method getParameters
    *
    * @return
    *
    * @author Andreas Brod
    */
   private HashSet<String> getParameters()
   {
      HashSet<String> hsSubElements = isResponse() ? getSubPnrElements() : getParentPnrElements();

      HashSet<String> hsParameters = new HashSet<>();

      for (String sBase : hsSubElements) {
         if (parentContainsPnrElement(sBase)) {
            hsParameters.add(sBase);
         }
      }

      return hsParameters;
   }

   /**
    * Method getMethodHeader
    *
    * @return
    *
    * @author Andreas Brod
    */
   private String getMethodHeader(String psFor)
   {
      StringBuilder sText = new StringBuilder();
      String sMethodName = getMethodName();

      HashSet<String> hsParameters = getParameters();

      sText.append("    /**\n");
      sText.append("     * Method " + sMethodName + "\n");
      sText.append("     *\n");

      if (isResponse()) {

         if (sMethodName.endsWith("sPerson")) {
            sText.append("     * @param piPersonId related PersonId.\n");
         }
         sText.append("     * @param parrPnrElements Array of PnrElements\n");
         // PnrElements which are already created (in previous methods)
         // are submitted as parameters
         for (String sBase : hsParameters) {
            sText.append("     * @param pnr" + sBase + " related element which is already created.\n");
         }

         // The returnvalue of a method is the related responseobject
         String sRetType = (!_sName.equals("String") ? "Res" : "") + _sName;

         sText.append("     * @return " + sRetType + " object (if available)\n");
         sText.append("     * @author _GENERATOR_\n");
         sText.append("     */\n");

         sText.append(SUPPRESS_WARNINGS_UNCHECKED);

         // Each method is defined as private
         String sLine = "    private " + sRetType + " " + sMethodName + "(";

         sText.append(sLine);

         char[] c = new char[sLine.length()];

         Arrays.fill(c, ' ');

         if (sMethodName.endsWith("sPerson")) {
            sText.append("int piPersonId, PnrElementBase[] parrPnrElements");
         } else {
            sText.append("PnrElementBase[] parrPnrElements");
         }

         StringBuilder sParams = new StringBuilder();

         for (String sBase : hsParameters) {
            if (sParams.length() > 0) {
               sParams.append(", ");
            }

            sParams.append("pnr" + sBase);

            sText.append(",\n" + new String(c) + "Element" + _dtdTransformerHandler.getClassName(sBase) + " pnr" + sBase);
         }

         sText.append(")\n    {\n");
         sText.append("        PnrElementBase[] pnrElements = parrPnrElements;\n");
         sText.append("        logStartResponse(\"" + _sName + "\");\n");
         sText.append("        // Create new Res" + _sName + "-object (which will be\n");
         sText.append("        // returned in case of success.\n");

         sText.append("        " + sRetType + " res" + _sName + " = new " + sRetType + "();\n");
         sText.append("        boolean bFound = false;\n");
         sText.append("    \n");

         // If there is the response element ResTag, it will be
         // created automatically (if it is not defined explicitly).
         if (!_lstSubMethod.contains("ResTag")) {
            if (_sSourceCode.indexOf("ResTag getTag(") > 0) {
               sText.append("        // set the Tag Object by default\n");
               sText.append("        res" + _sName + ".setTag(getTag(\"" + getMethodName() + "\"));\n");
            }
         }

      } else {
         String sReqName = "pReq" + _sName;

         sText.append("     * @param " + sReqName + " The " + _sName + " Request object\n");

         if (_sName.equals("Person")) {
            sText.append("     * @param piPerson The id of the person (starting with 0)\n");

         }

         for (String sBase : hsParameters) {
            sText.append("     * @param pnr" + sBase + " related element which is already created.\n");
         }
         if (psFor.length() > 0) {
            sText.append("     * @param psFor this element is responsible for\n");
         }
         sText.append("     * @author _GENERATOR_\n");
         sText.append("     */\n");

         // Each method is defined as private
         if (!_sName.equals("Person")) {
            sText.append("    private void " + sMethodName + "(Req" + _sName + " " + sReqName + "");
         } else {
            sText.append("    private void " + sMethodName + "(int piPerson, Req" + _sName + " " + sReqName + "");

         }

         for (String sBase : hsParameters) {
            sText.append(", Element" + _dtdTransformerHandler.getClassName(sBase) + " pnr" + sBase);
         }
         if (psFor.length() > 0) {
            sText.append(", String psFor");
         }

         sText.append(")\n    {\n");

         sText.append("        if (" + sReqName + " == null) {\n");
         sText.append("            return;\n");
         sText.append("        }\n");

         if (_sName.equals("Travellers")) {

            sText.append("        // Add travellers\n");
            sText.append("        _traveller.setTraveller(" + sReqName + ");\n");
         }

         // validate the PnrElements .. and transform the automatically
         // into self defined Pnr-Elements
         if (_sSourceCode.indexOf("ReqPnrElements getPnrElements(") > 0) {
            sText.append("        // validate if there are userdefined PnrElements\n");
            sText.append("        if (" + sReqName + ".getPnrElements() != null) {\n");
            sText.append("            // loop over all PnrElements\n");
            sText.append("            ReqPnrElement[] reqPnrElements = " + sReqName + ".getPnrElements().getPnrElement();\n");
            sText.append(
                  "            for (int iReqPnrElement = 0; iReqPnrElement < reqPnrElements.length; iReqPnrElement++) {\n");
            sText.append("               _PTD.addPNRElementToPnrElementList(_traveller.userDefinedPnrElement(\n");
            sText.append("                     reqPnrElements[iReqPnrElement], false));\n");
            sText.append("            }\n");
            //            sText.append("            for (Iterator i =\n");
            //            sText.append("                    " + sReqName + ".getPnrElements().getPnrElementList()\n");
            //            sText.append("                        .iterator(); i.hasNext(); ) {\n");
            //            sText.append("                _PTD.addPNRElementToPnrElementList(_traveller\n");
            //            sText.append("                    .userDefinedPnrElement((ReqPnrElement) i.next(),false));\n");
            //            sText.append("            }\n");
            sText.append("        }\n");
            sText.append("        \n");
         }

         if (_sSourceCode.indexOf("ReqRemovePnrElements getRemovePnrElements(") > 0) {
            sText.append("        // validate if there are removeable PnrElements\n");
            sText.append("        if (" + sReqName + ".getRemovePnrElements() != null) {\n");
            sText.append("            // loop over all PnrElements\n");
            sText.append("            ReqPnrElement[] reqPnrElements = " + sReqName + ".getRemovePnrElements()\n");
            sText.append("                    .getPnrElement();\n");
            sText.append(
                  "            for (int iReqPnrElement = 0; iReqPnrElement < reqPnrElements.length; iReqPnrElement++) {\n");
            sText.append("                _PTD.addPNRElementToPnrElementList(_traveller.userDefinedPnrElement(\n");
            sText.append("                    reqPnrElements[iReqPnrElement], true));\n");
            sText.append("            }\n");
            //            sText.append("            for (Iterator i =\n");
            //            sText.append("                    " + sReqName
            //                  + ".getRemovePnrElements().getPnrElementList()\n");
            //            sText.append("                        .iterator(); i.hasNext(); ) {\n");
            //            sText.append("                _PTD.addPNRElementToPnrElementList(_traveller\n");
            //            sText.append("                    .userDefinedPnrElement((ReqPnrElement) i.next(),true));\n");
            //            sText.append("            }\n");
            sText.append("        }\n");
            sText.append("        \n");
         }

         if (_sSourceCode.indexOf("ReqEnumRebookingType getRebookingType(") > 0) {
            sText.append("        boolean bKeepSegment = ((" + sReqName + ".getRebookingType() != null)\n            && ("
                  + sReqName + ".getRebookingType().equals(ReqEnumRebookingType.OLD_SEGMENT)));\n");
            _bRebookingType = true;
         }

      }

      return sText.toString();
   }

   /**
    * Method getMethodFooter
    *
    * @return
    *
    * @author Andreas Brod
    */
   private String getMethodFooter()
   {
      StringBuilder sText = new StringBuilder();

      if (isResponse()) {
         sText.append("        logEndResponse(\"" + _sName + "\", bFound);\n");
         sText.append("        return bFound ? res" + _sName + " : null;\n");
      } else {
         String sReqName = "pReq" + _sName;

         // correct ACTIVE/PASSIVE for reservation request
         if (_sSourceCode.indexOf("ReqEnumPnrType getType(") > 0) {
            sText.append("\n        // correct PnrElements according ACTIVE/PASSIVE !\n");
            sText.append("        PnrElementControlAttributes pnrElementControlAttributes =\n");
            sText.append("            new PnrElementControlAttributes();\n");
            sText.append("        pnrElementControlAttributes\n");
            sText.append("            .setIsActiveElement(" + sReqName + ".getType()==null || " + sReqName + ".getType()\n");
            sText.append("                .equals(ReqEnumPnrType.ACTIVE));\n");
            sText.append("        _PTD.getPnrElementList()\n");
            sText.append("            .changeElementControlAttributes(pnrElementControlAttributes);\n");

         }

      }

      sText.append("    } // END " + getMethodName() + " for " + _sName + "\n");

      return sText.toString();
   }

   /**
    * Method assignParameterElements
    *
    * @return
    *
    * @author Andreas Brod
    * @param sbFor
    * @param psFor
    */
   private String assignParameterElements(StringBuilder sbFor, String psFor)
   {
      StringBuilder sText = new StringBuilder();

      HashSet<String> elements = getParameters();

      for (String sBase : elements) {
         sText.append(assignElements(sBase, sbFor, psFor, true));
      }

      return sText.toString();
   }

   /**
    * Method assignElements
    *
    * @param psBase
    * @return
    *
    * @author Andreas Brod
    */
   private String assignElements(String psBase, StringBuilder sbFor, String psFor, boolean pbParameters)
   {
      StringBuilder sText = new StringBuilder();

      if (isResponse()) {
         return "";
      }

      String psPath = getMethodName();
      String sMainName = "pReq" + _sName;
      HashSet<String> hsParameters = getParameters();

      // Set Person
      // sText+="// sName = "+sName+" ("+psPath+")\n";

      if (_sName.equals("Person") && psPath.endsWith("TravellersPerson")) {
         if (!hsParameters.contains(psBase)) {

            // add the Person (if it is created within this method)
            sText.append("        // Set Person\n");
            sText.append("        _traveller.setPerson(piPerson, pnr" + psBase + ");\n");
         }
      }

      // sText+="//"+_lstAttributeKeys.toString()+"\n";
      // FOR and SEGMENT is NOT DEFINED !!!
      if (_sSourceCode.indexOf(" getFor(") > 0) {
         sText.append("        // Assign pnr" + psBase + " to the correct traveller\n");
         String sFor = sMainName + "For";
         boolean bHasFor = _sSourceCode.indexOf("hasFor(") >= 0;
         if (sbFor.toString().equals(sFor)) {
            // ignore this
         } else {
            sbFor.setLength(0);
            sbFor.append(sFor);
            sText.append("        String " + sFor + " = null;\n");

            if (bHasFor) {
               sText.append("        if (" + sMainName + ".hasFor()) {\n");
               sText.append("            " + sFor + " = String.valueOf(" + sMainName + "." + "getFor());\n");
            } else {
               sText.append("        if (" + sMainName + ".getFor() != null) {\n");
               sText.append("            " + sFor + " = " + sMainName + "." + "getFor();\n");
            }
            sText.append("        }\n");
         }
         sText.append("        if (" + sFor + " != null) {\n");
         sText.append("            _traveller.addTravellerAssociation(" + sFor + ", pnr" + psBase + ");\n");

         // sText.append("        } else {\n");
         // sText.append("            _traveller.addTravellerAssociation(\"0\", pnr" + psBase + ");\n");
         sText.append("        }\n\n");
      } else if (psFor.length() > 0 && !pbParameters) {
         sText.append("        // Assign pnr" + psBase + " to the correct traveller\n");
         sText.append("        if (psFor != null) {\n");
         sText.append("            _traveller.addTravellerAssociation(psFor , pnr" + psBase + ");\n");
         sText.append("        }\n\n");

      }

      if (_sSourceCode.indexOf(" getSegment(") > 0) {

         // add the Segment (if it is created within this method)
         sText.append("        // Assign pnr" + psBase + " to the correct segment\n");
         sText.append("        if (" + sMainName + ".hasSegment()) {\n");
         sText.append("            _traveller.addSegmentAssociation(" + getReqEnumSegmentType() + "," + sMainName
               + ".getSegment(), pnr" + psBase + ");\n        } else {\n");
         sText.append("            _traveller.addSegmentAssociation(" + getReqEnumSegmentType() + ",0, pnr" + psBase + ");\n");
         sText.append("        }\n");
      } else if (_sName.endsWith("Segment") && !_sName.endsWith("SubSegment")) {
         if (!_sName.startsWith("Cancel")) {
            // add the Segment (if it is created within this method)
            if (!hsParameters.contains(psBase)) {
               if (_sLastSegmentId.length() == 0) {
                  _sLastSegmentId = "int iSegmentId = _traveller.getNextSegmentId(" + getReqEnumSegmentType() + ");";
                  sText.append("        // get the new SegmentId\n");
                  sText.append("        " + _sLastSegmentId + "\n");
               }

               sText.append("        // Assign pnr" + psBase + " to the correct segment\n");
               sText.append("        _traveller.setSegment(" + getReqEnumSegmentType() + ",iSegmentId , pnr" + psBase + ");\n\n");
            }
         } else {
            // add the Cancel Segment (if it is created within this method)
            if (!hsParameters.contains(psBase)) {
               if (_sLastSegmentId.length() == 0) {
                  _sLastSegmentId = "int iSegmentId = _traveller.getNextSegmentId(" + getReqEnumSegmentType() + ", \"Cancel\");";
                  sText.append("        // get the new SegmentId\n");
                  sText.append("        " + _sLastSegmentId + "\n");
               }

               sText.append("        // Assign pnr" + psBase + " to the correct segment\n");
               sText.append("        _traveller.addSegmentAssociation(" + getReqEnumSegmentType()
                     + ", \"Cancel\", iSegmentId , pnr" + psBase + ");\n");
               sText.append("        pnr" + psBase + ".setCancelReferenceNumber(iSegmentId);\n\n");
            }

         }
      } else if (_sName.endsWith("CtwItem") && !_sName.endsWith("SubCtwItem") && !_sName.startsWith("Cancel")) {

         // add the CtwItem (if it is created within this method)
         if (!hsParameters.contains(psBase)) {
            if (_sLastCtwItemId.length() == 0) {
               _sLastCtwItemId = "int iCtwItemId = _traveller.getNextSegmentId(" + getReqEnumSegmentType() + ");";
               sText.append("        // get the new CtwItemId\n");
               sText.append("        " + _sLastCtwItemId + "\n");
            }

            sText.append("        // Assign pnr" + psBase + " to the correct CtwItem\n");
            sText.append("        _traveller.setSegment(" + getReqEnumSegmentType() + ",iCtwItemId , pnr" + psBase + ");\n\n");
         }
      }

      return sText.toString();

   }

   /**
    * Method getReqEnumSegmentType
    *
    * @return
    *
    * @author Andreas Brod
    */
   private String getReqEnumSegmentType()
   {
      String sUpper = _sName.toUpperCase();

      if (sUpper.indexOf("AIR") >= 0) {
         return "ReqEnumSegmentType.AIR";
      }

      if (sUpper.indexOf("CAR") >= 0) {
         return "ReqEnumSegmentType.CAR";
      }

      if (sUpper.indexOf("HOTEL") >= 0) {
         return "ReqEnumSegmentType.HOTEL";
      }

      if (sUpper.indexOf("RAIL") >= 0) {
         return "ReqEnumSegmentType.RAIL";
      }

      if (sUpper.indexOf("HTL") >= 0) {
         return "ReqEnumSegmentType.HOTEL";
      }

      if (sUpper.indexOf("CTW") >= 0) {
         return "ReqEnumSegmentType.CTW_ITEM";
      }

      return "null";
   }

   /**
    * Method createElement
    *
    * <p> TODO rename createdElements to pElements
    * @param psBase
    * @param pbCancel
    * @param pHsLocalElements
    * @param createdElements
    * @return
    *
    * @author Andreas Brod
    * @param sbFor
    * @param psFor
    */
   private String createElement(String psBase, boolean pbCancel, HashSet<String> pHsLocalElements,
                                HashSet<String> createdElements, StringBuilder sbFor, String psFor)
   {
      StringBuilder sText = new StringBuilder();

      HashSet<String> phsSubElements = getParameters();

      String sBase = psBase;
      String sParent = "";
      if (sBase.indexOf("->") > 0) {
         sParent = sBase.substring(0, sBase.indexOf("->"));
         sBase = sBase.substring(sBase.indexOf(">") + 1);
      }
      String sClassName = _dtdTransformerHandler.getClassName(sBase);
      if (isResponse()) {
         sText.append("\n        // Create PnrElement " + sBase + "\n");

         StringBuilder sList = new StringBuilder();

         for (Object name : phsSubElements) {
            String sBaseNext = name.toString();

            if (!sBaseNext.equals(sBase)) {
               if (sList.length() > 0) {
                  sList.append(", ");
               }

               sList.append("pnr" + sBaseNext);
            }
         }

         sText.append("        Element" + sClassName + " pnr" + sBase + " = (Element" + sClassName + ")\n");

         if (createdElements.size() > 0) {
            sText.append("            (");

            for (Iterator<String> iter = createdElements.iterator(); iter.hasNext();) {
               String element = iter.next();

               sText.append("pnr" + element + " == null " + (iter.hasNext() ? " || " : "") + "");
            }

            sText.append("? null : ");
         } else {
            sText.append("                ");
         }

         sText.append("getNextElementBase(PnrEnumPnrElementType." + sBase + ", pnrElements");
         if (sParent.length() > 0) {
            sText.append(", PnrEnumPnrElementType." + sParent + "");
         }
         sText.append(")");
         if (createdElements.size() > 0) {
            sText.append(")");
         }
         sText.append(";\n");

         sText.append("        pnrElements = addPnrElementBase(pnr" + sBase + ", pnrElements);\n");
         sText.append("        \n");
         createdElements.add(sBase);

      } else if (sBase.length() > 0) {

         String sElementName = sClassName;
         sText.append("\n        Element" + sElementName + " pnr" + sBase + " = (Element" + sElementName + ") _elementFactory\n");
         sText.append("            .createPnrElement(PnrEnumPnrElementType." + sBase + ");\n");
         sText.append("        pnr" + sBase + ".getControlAttributes().setSource(" + "EnumPnrElementSource.FIX_CODED);\n");

         if (pbCancel) {
            sText.append("\n        // set to deleted because this or parent type\n");
            sText.append("        // starts with 'Cancel'.\n");
            sText.append("        pnr" + sBase + ".getControlAttributes().setAction(" + "EnumPnrElementAction.DELETE);\n\n");
         }

         // If the request element contains an attribute of the
         // type ReqEnumRebookingType which has the value
         // OLD_SEGMENT, all PnrElements which are created within
         // the method get as actionType EnumPnrElementAction.KEEP.
         if (_bRebookingType) {
            sText.append("\n        if (bKeepSegment) {\n");
            sText.append("            pnr" + sBase + ".getControlAttributes().setAction(" + "EnumPnrElementAction.KEEP);\n");
            sText.append("        }\n");

         }

         sText.append("        _PTD.addPNRElementToPnrElementList(pnr" + sBase + ");\n");

         // set associations
         for (String sBaseNext : pHsLocalElements) {
            if (!sBaseNext.equals(sBase)) {
               sText.append("        pnr" + sBase + ".setAssociations(pnr" + sBaseNext + ");\n");

            }
         }

         sText.append(assignElements(sBase, sbFor, psFor, false));

      }

      pHsLocalElements.add(sBase);

      return sText.toString();
   }

   /**
    * Method callSub
    *
    * <p> TODO rename bChoice to pbChoice
    * @param psSubElement
    * @param pTransformerMethod
    * @param pbIsResponse
    * @param bChoice
    * @return
    *
    * @author Andreas Brod
    */
   private String callSub(String psSubElement, TransformerMethod pTransformerMethod, boolean pbIsResponse, boolean bChoice,
                          String psFor)
   {
      StringBuffer sCode = new StringBuffer();

      // get SubElements

      // get MethodCall from JaxB Source
      String sMethod = getMethod(_sSourceCode, psSubElement, pbIsResponse).trim();

      if (_sSourceCode.indexOf("FormOfId") > 0) {
         _sSourceCode = _sSourceCode + "";
      }

      if (getMethod(_sSourceCode, psSubElement + "List", false).trim().length() > 0) {
         sMethod = "";
      }

      // validate the method
      if (sMethod.length() == 0) {

         // get the "get" Method (independant of Request/Response).
         sMethod = getMethod(_sSourceCode, psSubElement + "List", false).trim();

         if (isResponse() && sMethod.length() == 0 && _sSourceCode.indexOf("getContent(") > 0) {
            sMethod = getMethod(_sSourceCode, "Content", false).trim();

         }

         if (sMethod.length() > 0) {
            String sAdd = "";

            sCode.append("        // loop over " + psSubElement + "-Elements\n");

            if (psSubElement.equals("Person")) {
               sCode.append("        int iPersonCounter = " + (pbIsResponse ? "1" : "0") + ";\n");

               sAdd += "iPersonCounter++, ";
            }

            sCode.append(callSubListElement(psSubElement, pTransformerMethod, sAdd, bChoice, psFor));

         }
      } else {
         sCode.append(callSubDirect(_sSourceCode, _sName, pTransformerMethod, pbIsResponse, bChoice, psFor));

      }

      return sCode.toString();
   }

   /**
    * Method callSub
    *
    * <p> TODO rename bChoice to pbChoice
    * @param psSubElement
    * @param pTransformerMethod
    * @param psAdd
    * @param bChoice
    * @return
    *
    * @author Andreas Brod
    */
   private String callSubListElement(String psSubElement, TransformerMethod pTransformerMethod, String psAdd, boolean bChoice,
                                     String psFor)
   {
      StringBuilder sText = new StringBuilder();
      String psTransform2 = pTransformerMethod.getMethodName();

      HashSet<String> hsUsedPnrElements = getParameters();

      hsUsedPnrElements.addAll(_pnrElements);

      boolean pbAvoidEndlessLoop = false;
      StringBuilder psNextSub = new StringBuilder();

      for (String sToken : pTransformerMethod.getParameters()) {
         psNextSub.append(", ");

         // if this pnrElement is used (in submethod)
         if (hsUsedPnrElements.contains(sToken)) {
            pbAvoidEndlessLoop = true;
            sToken = "pnr" + sToken;
         } else {
            sToken = "null";
         }

         psNextSub.append(sToken);

      }

      if (psFor.length() > 0) {
         psNextSub.append(", " + psFor);
      }

      if (_sType.startsWith("Res")) {
         String psReqName = "res" + _sName;

         hsUsedPnrElements.addAll(_pnrElements);

         if (psTransform2.endsWith("String")) {
            sText.append("        String");

         } else {
            sText.append("        Res" + psSubElement);

         }

         sText.append(" res" + psSubElement + ";\n");
         sText.append("\n");

         String sGetSubElementList = "" + psSubElement + "";

         if (bChoice) {
            sText.append("        // Validate only once, because " + _sName + " is defined as CHOICE (within dtd)\n");
            sText.append("        if (");
         } else if (!pbAvoidEndlessLoop) {
            sText.append(
                  "        while ((" + psReqName + ".get" + sGetSubElementList + "Count() < 20000)\n" + "                && ");
         } else {
            sText.append("        // Validate only once (to avoid enlessLoop)\n");
            sText.append("        // because submitted pnrElement is used in " + psTransform2 + "().\n");
            sText.append("        if (");
         }
         psNextSub = new StringBuilder("pnrElements" + psNextSub.toString());

         sText.append("(res" + psSubElement + " = " + psTransform2 + "(" + psAdd + psNextSub + ")) != null) {\n");
         sText.append("            bFound = true;\n");

         sText.append("            " + psReqName + ".add" + sGetSubElementList + "(res" + psSubElement + ");\n");
         if (bChoice) {
            sText.append("\n            // return " + psReqName + " as CHOICE element\n");
            sText.append("            logEndResponse(\"" + _sName + "\", bFound);\n");
            sText.append("            return " + psReqName + ";\n");
         }
         sText.append("        }\n");

      } else {
         String psReqName = "pReq" + _sName;

         sText.append("        for (int i = 0; i < " + psReqName + ".get" + psSubElement + "Count();i++) {\n");

         sText.append(
               "            " + psTransform2 + "(" + psAdd + psReqName + ".get" + psSubElement + "(i)" + psNextSub + ");\n");
         sText.append("        }\n");
      }

      return sText.toString();
   }

   /**
    * Method callSub2
    *
    * @param psCode
    * @param psReqName
    * @param pTransformerMethod
    * @param pbResponse
    * @return
    *
    * @author Andreas Brod
    * @param pbChoice
    */
   private String callSubDirect(String psCode, String psReqName, TransformerMethod pTransformerMethod, boolean pbResponse,
                                boolean pbChoice, String psFor)
   {
      StringBuilder sText = new StringBuilder();

      String psNextSub = new String();

      for (String string : pTransformerMethod.getParameters()) {
         psNextSub += ", pnr" + string;
      }
      if (psFor.length() > 0) {
         psNextSub += ", " + psFor;
      }
      String psSubElement = pTransformerMethod._sName;
      String psTransform2 = pTransformerMethod.getMethodName();

      if (pbResponse) {
         if (psNextSub.startsWith(",")) {
            psNextSub = psNextSub.substring(1).trim();
         }

         String sPnrElements = "pnrElements";

         sText.append("        Res" + psSubElement + " res" + psSubElement + " = ");

         sText.append(psTransform2 + "(\n            " + sPnrElements);

         if (psNextSub.trim().length() > 0) {
            sText.append(", " + psNextSub);

         }

         sText.append(");\n\n");

         sText.append("        if (res" + psSubElement + " != null) {\n");
         sText.append("            res" + psReqName + ".set" + psSubElement + "(res" + psSubElement + ");\n");
         if (pbChoice) {
            sText.append("            logEndResponse(\"" + psReqName + "\", true);\n");
            sText.append("            // return because choice object found\n");
            sText.append("            return res" + psReqName + ";\n");
         } else {
            sText.append("            bFound = true;\n");
         }
         sText.append("        }\n");

      } else {
         if (psCode.indexOf(" has" + psSubElement + "()") > 0) {
            sText.append("        if (pReq" + psReqName + ".has" + psSubElement + "())\n    ");
         }

         if (psSubElement.equals("Person")) {
            sText.append("        " + psTransform2 + "(-1, pReq" + psReqName + ".get" + psSubElement + "()" + psNextSub + ");\n");
         } else {
            sText.append("        " + psTransform2 + "(pReq" + psReqName + ".get" + psSubElement + "()" + psNextSub + ");\n");

         }
      }

      return sText.toString();
   }

   /**
    * Method getPnrAttribute
    *
    * @param psText
    * @return
    *
    * @author Andreas Brod
    */
   private String getPnrAttribute(String psText)
   {
      psText = "." + psText;

      return psText.substring(psText.lastIndexOf(".") + 1);
   }

   /**
    * Method getReqHasMethod
    *
    * @param psCode
    * @param psName
    * @return
    *
    * @author Andreas Brod
    */
   private boolean getReqHasMethod(String psCode, String psName)
   {
      String sFind = " HAS" + psName.toUpperCase() + "(";
      int iStart = psCode.toUpperCase().indexOf(sFind);

      return iStart > 0;
   }

   /**
    * Method getMethod
    *
    * @param psCode
    * @param psName
    * @param pbResponse
    * @return
    *
    * @author Andreas Brod
    */
   private String getMethod(String psCode, String psName, boolean pbResponse)
   {
      String sFind = pbResponse ? " SET" + psName.toUpperCase() + "(" : " GET" + psName.toUpperCase() + "(";
      int iStart = psCode.toUpperCase().indexOf(sFind);
      String sType = "";

      if (iStart < 0 && psName.equals("Content")) {
         iStart = psCode.toUpperCase().indexOf("PUBLIC JAVA.LANG.STRING[] GET");

         if (iStart > 0) {
            iStart += 25;
            sType = "java.lang.String[]";
         } else {
            iStart = psCode.toUpperCase().indexOf("PUBLIC JAVA.LANG.STRING GET");
            if (iStart > 0) {
               iStart += 23;
               sType = "java.lang.String";
            }
         }
      }

      if (iStart > 0) {
         try {
            if (pbResponse) {
               int iNext = psCode.indexOf("(", iStart);
               String sMethod = psCode.substring(iStart + 1, iNext);
               if (sType.length() == 0) {
                  sType = psCode.substring(iNext + 1, psCode.indexOf(")", iNext)).trim();
                  while (sType.length() > 0 && sType.charAt(0) <= ' ') {
                     sType = sType.substring(1);
                  }
                  if (sType.startsWith("final ")) {
                     sType = sType.substring(6);
                  }
                  if (sType.contains(" ")) {
                     sType = sType.substring(0, sType.indexOf(" "));
                  }
               }
               // there is a setContent(""); within the request ... so
               if (sType.startsWith("\"")) {
                  sType = "java.lang.String";
               }
               return sType + " " + sMethod;
            }
            return psCode.substring(psCode.lastIndexOf(" ", iStart - 1) + 1, psCode.indexOf("(", iStart));
         }
         catch (Exception ex) {}
      }

      return "";
   }

   /**
    * Method getPnrAttr
    *
    * @param psText
    * @param pbHasHasMethod
    * @param psBase
    * @param psType TODO (brod) add text for param psType
    * @param psPath
    * @param psName
    * @param psReqName
    * @param psMethod
    * @param psPnrAttr
    * @param pbResponse
    * @param pbSelfDefinedTransformer
    * @return
    *
    * @author Andreas Brod
    */
   private String getPnrAttr(String psText, boolean pbHasHasMethod, String psBase, String psType, String psPath, String psName,
                             String psReqName, String psMethod, String psPnrAttr, boolean pbResponse,
                             boolean pbSelfDefinedTransformer)
   {
      StringBuilder sText = new StringBuilder("\n");

      if (pbResponse) {
         if (psType.indexOf(".valueOf(") > 0) {
            psType = psType.substring(0, psType.indexOf(".valueOf("));
         }
         String sType = psType.substring(psType.lastIndexOf(".") + 1);

         String sList = psMethod.substring(3);
         String sException = "            }\n            catch (Exception ex) {\n                logException(\"" + psName + "."
               + sList + "\", ex);\n" + "            }\n";
         String sParamValue = psPnrAttr.indexOf("(") > 0 ? psPnrAttr : getParamValue(psBase, psPnrAttr);
         String sType2 = _dtdTransformerHandler.getAttributeType(psBase, psPnrAttr);

         String sHas = "";
         if (sParamValue.startsWith("get")) {
            if (" int float double boolean ".indexOf(sType2) > 0) {
               sHas = "            if (pnr" + psBase + ".has" + sParamValue.substring(3)
                     + ") {\n                // add Param only if exist\n";
            }
         }

         if (pbSelfDefinedTransformer) {
            if (psPnrAttr.endsWith("*")) {
               psPnrAttr = psPnrAttr.substring(0, psPnrAttr.length() - 1);
            }

            StringBuilder sAdd = new StringBuilder();
            String sRemark = "";

            if (sType.equals("void")) {
               sAdd.append("            if (" + getResAbstractMethod(psPath, "boolean", psBase, psPnrAttr, psName, psMethod, true)
                     + ") {\n");
               sAdd.append("               bFound = true;\n");
               sAdd.append("           }\n");

               sRemark = "// ... " + psReqName + "." + psMethod + "(" + psBase + "." + psPnrAttr + ")\n";

            } else {
               sAdd.append("            res" + psName + ".set" + sList + "("
                     + getResAbstractMethod(psPath, psType, psBase, psPnrAttr, psName, psMethod, false) + ");\n");

               sRemark = "// ... " + psReqName + "." + psMethod + "(" + psBase + "." + psPnrAttr + ")\n";
            }

            if (psText.indexOf(sAdd.toString()) < 0) {
               sText.append(sAdd.toString());
            }

            sText.append("            " + sRemark);
         } else if (psPnrAttr.length() == 0) {

            // make nothing
            sText.setLength(0);
            sText.append("\n");

         } else if (sType.equals(")") && sList.endsWith("List")) {
            sText.append(sHas);
            // this is in case of Content (%Text;)
            sText.append("            res" + psName + ".get" + sList);
            sText.append("().add(pnr" + psBase + "\n                ." + sParamValue + ");\n");
            if (sHas.length() > 0) {
               sText.append("            }\n");
            }
         } else if (sType.equals("String[]")) {
            sText.append("            while (pnr" + psBase + " != null  && res" + psName + ".get" + sList + "Count()<2000) {\n");
            sText.append("               res" + psName + ".add" + sList);
            sText.append("(pnr" + psBase + "\n                   ." + sParamValue + ");\n");
            String sCamelCaseBase = Util.camelCase(psBase.toLowerCase());
            sText.append("               pnr" + psBase + " = (Element" + sCamelCaseBase
                  + ") getNextElementBase(PnrEnumPnrElementType." + psBase + ", pnrElements);\n");
            sText.append("            }\n");

         } else if (sType.equals("String")) {
            sText.append(sHas);
            if (psName.equals("String")) {
               sText.append("            res" + psName + " = ");
            } else {
               sText.append("            res" + psName + ".set" + sList);
            }

            sText.append("(" + (!sType2.equals("String") ? "\"\" + " : "") + "pnr" + psBase + "\n                ." + sParamValue
                  + ");\n");
            if (sHas.length() > 0) {
               sText.append("            }\n");
            }

         } else if (sType.startsWith("ResEnum")) {
            sText.append("            try {\n");
            sText.append("              if (pnr" + psBase + "." + sParamValue + " != null) {\n");
            sText.append("                res" + psName + ".set" + sList + "(" + sType + ".valueOf(pnr" + psBase + "\n");
            sText.append("                    ." + sParamValue + (sType2.equals("String") ? "" : ".toString()") + "));\n");
            sText.append("              }\n");
            sText.append(sException);
         } else if (sType.equals("int")) {
            sText.append(sHas);
            sText.append("            try {\n");
            if (sType2.equals("String")) {
               sText.append("              if (pnr" + psBase + "." + sParamValue + " != null) {\n");
               sText.append("                res" + psName + ".set" + sList + "(Integer.parseInt(pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + "));\n");
               sText.append("              }\n");
            } else if (sType2.equals("int")) {
               sText.append("                res" + psName + ".set" + sList + "(pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + ");\n");
            } else {
               sText.append("                res" + psName + ".set" + sList + "((int) pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + ");\n");
            }
            sText.append(sException);
            if (sHas.length() > 0) {
               sText.append("            }\n");
            }
         } else if (sType.equals("long")) {
            sText.append(sHas);
            sText.append("            try {\n");
            if (sType2.equals("String")) {
               sText.append("              if (pnr" + psBase + "." + sParamValue + " != null) {\n");
               sText.append("                res" + psName + ".set" + sList + "(Long.parseLong(pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + "));\n");
               sText.append("              }\n");
            } else if (sType2.equals("long")) {
               sText.append("                res" + psName + ".set" + sList + "(pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + ");\n");
            } else {
               sText.append("                res" + psName + ".set" + sList + "((long) pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + ");\n");
            }
            sText.append(sException);
            if (sHas.length() > 0) {
               sText.append("            }\n");
            }

         } else if (sType.equals("float")) {
            sText.append(sHas);
            sText.append("            try {\n");
            if (sType2.equals("String")) {
               sText.append("              if (pnr" + psBase + "." + sParamValue + " != null) {\n");
               sText.append("                res" + psName + ".set" + sList + "(Float.parseFloat(pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + "));\n");
               sText.append("              }\n");
            } else {
               sText.append("                res" + psName + ".set" + sList + "((float)pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + ");\n");

            }
            sText.append(sException);
            if (sHas.length() > 0) {
               sText.append("            }\n");
            }

         } else if (sType.equals("double")) {
            sText.append(sHas);
            sText.append("            try {\n");
            if (sType2.equals("String")) {
               sText.append("              if (pnr" + psBase + "." + sParamValue + " != null) {\n");
               sText.append("                res" + psName + ".set" + sList + "(Double.parseDouble(pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + "));\n");
               sText.append("              }\n");
            } else {
               sText.append("                res" + psName + ".set" + sList + "((double)pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + ");\n");
            }
            sText.append(sException);
            if (sHas.length() > 0) {
               sText.append("            }\n");
            }

         } else if (sType.equals("short")) {
            sText.append(sHas);
            sText.append("            try {\n");
            if (sType2.equals("String")) {
               sText.append("              if (pnr" + psBase + "." + sParamValue + " != null) {\n");
               sText.append("                res" + psName + ".set" + sList + "(Short.parseShort(pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + "));\n");
               sText.append("              }\n");
            } else {

               sText.append("                res" + psName + ".set" + sList + "((short)pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + ");\n");
            }
            sText.append(sException);
            if (sHas.length() > 0) {
               sText.append("            }\n");
            }

         } else if (psType.equalsIgnoreCase("java.util.Date")) {
            if (sType2.equals("Date")) {
               sText.append("                res" + psName + ".set" + sList + "(pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + ");\n");
            } else {
               sText.append("            res" + psName + ".set" + sList + "(pnr" + psBase + "\n");
               sText.append("                    .getParamValueDate(PnrEnumPnrElementParameterId." + psPnrAttr + "));\n");
            }
         } else if (sType.equals("Date")) {
            if (sType2.equals("Date")) {
               sText.append("            try {\n" + "                res" + psName + ".set" + sList
                     + "(new org.exolab.castor.types.Date(pnr" + psBase + "\n");
               sText.append("                    ." + sParamValue + "));\n" + "            } catch (Exception e) {\n"
                     + "               // catch Nullpointer and ParseExceptions\n" + "            }\n");
            } else {
               sText.append("            try {\n" + "                res" + psName + ".set" + sList
                     + "(new org.exolab.castor.types.Date(net.ifao.util.CommonDate\n" + "                     .getDate(pnr"
                     + psBase + "." + getParamValue(psBase, psPnrAttr) + ")));\n" + "            } catch (Exception e) {\n"
                     + "               // catch Nullpointer and ParseExceptions\n" + "            }\n");
            }
         } else if (sType.equals("Duration")) {
            sText.append("            try {\n");
            if (sType2.equals("Duration")) {
               sText.append("               if (pnr" + psBase + "." + getParamValue(psBase, psPnrAttr) + " != null) {\n");
               sText.append("                   res" + psName + ".set" + sList + "(pnr" + psBase + "."
                     + getParamValue(psBase, psPnrAttr) + ");\n");
               sText.append("               }\n");
            } else {
               sText.append("               if (pnr" + psBase + "." + getParamValue(psBase, psPnrAttr) + " != null) {\n");
               sText.append("                   res" + psName + ".set" + sList + "(new org.exolab.castor.types.Duration(pnr"
                     + psBase + "\n");
               sText.append("                           ." + getParamValue(psBase, psPnrAttr) + "));\n");
               sText.append("               }\n");

            }
            sText.append("            } catch (Exception e) {\n");
            sText.append("              // make nothing\n");
            sText.append("            }\n");
         } else if (sType.equalsIgnoreCase("boolean")) {
            sText.append(sHas);
            sText.append("            try {\n");
            if (sType2.equals("boolean")) {
               sText.append("                res" + psName + ".set" + sList + "(\n");
               sText.append("                    pnr" + psBase + "." + sParamValue + ");\n");
            } else {
               sText.append("              if (pnr" + psBase + "." + sParamValue + " != null) {\n");
               sText.append("                res" + psName + ".set" + sList + "(\n");
               sText.append("                    pnr" + psBase + "." + sParamValue + ".equalsIgnoreCase(\"yes\") ||\n");
               sText.append("                    pnr" + psBase + "." + sParamValue + ".equalsIgnoreCase(\"true\"));\n");
               sText.append("               }\n");
            }
            sText.append(sException);
            if (sHas.length() > 0) {
               sText.append("            }\n");
            }

         }

      } else {
         String sClose = "";
         if (pbHasHasMethod && !pbSelfDefinedTransformer) {
            sText.append("        if (" + psReqName + ".has" + psMethod.substring(3)
                  + ") {\n            // add Param only if exist\n    ");
            sClose += "        }\n";
         }

         if (psType.indexOf("ReqEnum") >= 0) {
            sText.append(
                  "        if (" + psReqName + "." + psMethod + " != null) {\n            // add Param only if exist\n    ");
            psMethod += ".toString()";
            sClose += "        }\n";

         }

         if (pbSelfDefinedTransformer) {
            if (psPnrAttr.endsWith("*")) {
               psPnrAttr = psPnrAttr.substring(0, psPnrAttr.length() - 1);
            }

            String sAdd = "        " + getReqAbstractMethod(psPath, psBase, psPnrAttr, psName, psMethod) + "(" + psReqName
                  + (psBase.length() > 0 ? ", pnr" + psBase : "") + ");\n";

            String sRemark = "// ... set " + psBase + "." + psPnrAttr + " with " + psReqName + "." + psMethod + "\n";

            if (psText.indexOf(sAdd) < 0) {
               sText.append(sAdd + "        " + sRemark);
            } else {
               sText.setLength(0);
               sText.append("        " + sRemark);
            }

         } else if (psPnrAttr.length() > 0) {
            StringBuilder sAdd = new StringBuilder();
            if (psMethod.startsWith("\"")) {

               // If there is an assignment of an element (not attribute)
               // to a parameter, the name of the element is written
               // into this parameter.
               sAdd.append("        pnr" + psBase + "."
                     + setParamValue(psType, psBase, psPnrAttr, psMethod.substring(0, psMethod.lastIndexOf("\"") + 1)) + ");\n");
            } else {
               boolean bOk = psType.indexOf("Date") > 0 || psType.indexOf("Duration") > 0;
               if (bOk) {
                  sAdd.append(
                        "        if (" + psReqName + "." + psMethod + " != null) {\n            // add Param only if exist\n");
                  sAdd.append("          try {  \n");
               }

               sAdd.append("          pnr" + psBase + "." + setParamValue(psType, psBase, psPnrAttr, psReqName + "." + psMethod)
                     + ");\n");
               if (bOk) {
                  sAdd.append("          }\n");
                  sAdd.append("          catch (Exception ex) {\n");
                  sAdd.append("            // make nothing\n");
                  sAdd.append("          }\n");
                  sAdd.append("       }\n");
               }
            }
            if (sAdd.indexOf(".parse") > 0) {
               sAdd = new StringBuilder("        try {\n  " + sAdd.toString().replaceAll("\\n", "\n  ")
                     + "      }\n        catch (Exception ex) {\n" + "          // make nothing\n" + "        }\n");
            }
            sText.append(sAdd);
         }
         sText.append(sClose);
      }

      return sText.toString();
   }

   /**
    * TODO (brod) add comment for method getParamValue
    *
    * @param psBase TODO (brod) add text for param psBase
    * @param psPnrAttr TODO (brod) add text for param psPnrAttr
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   private String getParamValue(String psBase, String psPnrAttr)
   {
      String sBase = Util.camelCase(psBase.toLowerCase());
      // String sClass = _dtdTransformerHandler.getClassName(psBase);
      String sAttr = Util.camelCase(psPnrAttr.toLowerCase());
      String sRet = "getParamValue(PnrEnumPnrElementParameterId." + psPnrAttr + ")";
      sRet = "get" + sBase + sAttr + "()";
      return sRet;
   }

   /**
    * TODO (brod) add comment for method setParamValue
    *
    * <p> TODO rename substring to psSubstring
    * @param psType TODO (brod) add text for param psType
    * @param psBase TODO (brod) add text for param psBase
    * @param psPnrAttr TODO (brod) add text for param psPnrAttr
    * @param substring TODO (brod) add text for param substring
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   private String setParamValue(String psType, String psBase, String psPnrAttr, String substring)
   {
      String sType2 = _dtdTransformerHandler.getAttributeType(psBase, psPnrAttr);
      String sRet = "";
      // String sBase = _dtdTransformerHandler.getClassName(psBase);
      String sBase = Util.camelCase(psBase.toLowerCase());
      String sClass = _dtdTransformerHandler.getClassName(sBase);
      String sAttr = Util.camelCase(psPnrAttr.toLowerCase());

      if (psType.equalsIgnoreCase("java.lang.String")) {
         psType = "String";
      }
      if (psType.equalsIgnoreCase(sType2)) {
         sRet = "set" + sBase + sAttr + "(" + substring;
      } else {
         if (sType2.equalsIgnoreCase("int")) {
            if (psType.equals("String")) {
               sRet = "set" + sBase + sAttr + "(Integer.parseInt(" + substring + ")";
            } else if (psType.equalsIgnoreCase("int")) {
               sRet = "set" + sBase + sAttr + "(" + substring;
            } else {
               sRet = "set" + sBase + sAttr + "((int) " + substring;
            }
         } else if (sType2.equalsIgnoreCase("long")) {
            if (psType.equals("String")) {
               sRet = "set" + sBase + sAttr + "(Long.parseLong(" + substring + ")";
            } else {
               sRet = "set" + sBase + sAttr + "(" + substring;
            }
         } else if (sType2.equalsIgnoreCase("double")) {
            if (psType.equals("String")) {
               sRet = "set" + sBase + sAttr + "(Double.parseDouble(" + substring + ")";
            } else {
               sRet = "set" + sBase + sAttr + "(" + substring;
            }
         } else if (sType2.startsWith("Enum")) {
            sRet = "set" + sBase + sAttr + "(" + _dtdTransformerHandler.getRealClassName("Element" + sClass) + "." + "Enum"
                  + sAttr + ".parse(" + substring + ")";
         } else if (sType2.equalsIgnoreCase("Date")) {
            if (psType.equalsIgnoreCase("java.util.Date")) {
               sRet = "set" + sBase + sAttr + "(" + substring;
            } else if (psType.indexOf("exolab") >= 0) {
               sRet = "set" + sBase + sAttr + "(" + substring + ".toDate()";
            } else {
               sRet = "set" + sBase + sAttr + "(" + substring;
            }
         } else if (sType2.equalsIgnoreCase("Duration")) {
            if (psType.endsWith(".Duration")) {
               sRet = "set" + sBase + sAttr + "(" + substring;
            } else if (psType.equalsIgnoreCase("java.util.Date")) {

               sRet = "set" + sBase + sAttr + "(new org.exolab.castor.types.Duration(\n";
               sRet += "              new java.text.SimpleDateFormat(\"'P'yyyy'Y'MM'M'dd'DT'HH'H'mm'M'ss.SSS'S'\").format(\n";
               sRet += "                " + substring + "))";

               //               sRet = "set" + sBase + sAttr + "(new org.exolab.castor.types.Duration(\n"
               //                     + "            " + substring + ".getTime())";
            } else {
               sRet = "set" + sBase + sAttr + "(\"\" + " + substring;
            }

         } else {
            if (psType.endsWith(".Date")) {
               sRet = "set" + sBase + sAttr + "(net.ifao.util.Common.getDateString(" + substring + ")";
            } else {
               sRet = "set" + sBase + sAttr + "(\"\" + " + substring;
            }
         }
      }
      return sRet;
   }

   /**
    * Method getReqAbstractMethod
    *
    * @param psPath
    * @param psBase
    * @param psAttr
    * @param psRequest
    * @param psReqMethod
    * @return
    *
    * @author Andreas Brod
    */
   private String getReqAbstractMethod(String psPath, String psBase, String psAttr, String psRequest, String psReqMethod)
   {
      if (psAttr.endsWith("()")) {
         psAttr = psAttr.substring(0, psAttr.length() - 2);
      }

      String sMethod = DtdGenerator.getUnFormatedProvider(psBase) + DtdGenerator.getUnFormatedProvider(psAttr);

      if (sMethod.length() == 0) {
         sMethod = psPath + psRequest;
      } else {
         sMethod = psPath + psRequest + "_" + sMethod;
      }

      String sText = _abstractMethods.get(sMethod + "." + psRequest);
      String sReqName = "pReq" + psRequest;

      if (sText == null) {
         StringBuilder sbText = new StringBuilder();
         sbText.append("    /**\n");
         sbText.append("     * Method set" + sMethod + "\n");
         sbText.append("     *\n");
         sbText.append("     * @param " + sReqName + " Request object of type Req" + psRequest + "\n");

         if (psBase.length() > 0) {
            sbText.append("     * @param pBase PnrElementBase of type PnrEnumPnrElementType." + psBase + "\n");
         }

         sbText.append("     * @author _GENERATOR_\n");
         sbText.append("     */\n");
         sbText.append(ANNOTATION_OVERRIDE);
         sbText.append("    protected abstract void set" + sMethod + "(Req" + psRequest + " " + sReqName);

         if (psBase.length() > 0) {
            sbText.append(", PnrElementBase pBase");
         }

         sbText.append(")\n");
         sbText.append("    {\n");
         sbText.append("        /**\n");
         sbText.append("         * @todo The method set" + sMethod + " has to be modified, because it is defined\n");
         sbText.append("         * as a user defined method within DtdInfo.\n");
         sbText.append("         */\n");

         if (psBase.length() > 0) {
            sbText.append("    } " + DtdTransformerHandler.GENERATED_METHOD + " set" + sMethod + "(Req" + psRequest
                  + " , PnrElementBase)\n");
         } else {
            sbText.append("    } " + DtdTransformerHandler.GENERATED_METHOD + " set" + sMethod + "(Req" + psRequest + " )\n");
         }
         sText = sbText.toString();
      }

      if (psReqMethod.length() > 0) {
         psReqMethod = "." + psReqMethod;
      } else {
         sReqName = "\"" + psRequest + "\"";
      }

      if (psAttr.length() > 0) {
         int lastIndexOf = sText.lastIndexOf("}");
         sText = sText.substring(0, lastIndexOf) + "    pBase.setParamValue(PnrEnumPnrElementParameterId." + psAttr + ", "
               + sReqName + psReqMethod + ");\n    " + "    getLog().finer(toString() + \" is setting " + psBase + "." + psAttr
               + " to \\\"\"\n            + pBase." + getParamValue(psBase, psAttr) + "\n            + \"\\\"\");\n    "
               + sText.substring(lastIndexOf);
      }

      _abstractMethods.put(sMethod + "." + psRequest, sText);

      return "set" + sMethod;

   }

   /**
    * Method getResAbstractMethod
    *
    * @param psPath
    * @param psType
    * @param psBase
    * @param psAttr
    * @param psRequest
    * @param psReqMethod
    * @param pbUpdate
    * @return
    *
    * @author Andreas Brod
    */
   private String getResAbstractMethod(String psPath, String psType, String psBase, String psAttr, String psRequest,
                                       String psReqMethod, boolean pbUpdate)
   {
      String sType = psType;
      if (sType.indexOf(".") > 0) {
         sType = sType.substring(sType.lastIndexOf(".") + 1);
      }
      String sMethod = psPath + psRequest + psReqMethod.substring(3);

      String sText = _abstractMethods.get(sMethod + "." + psRequest);

      String sReqName = "p" + psBase;
      String sMethodName = (pbUpdate ? "update" : "get") + "Res" + sMethod;

      if (sText == null) {
         StringBuilder sbText = new StringBuilder();
         sbText.append("    /**\n");
         sbText.append("     * Method " + sMethodName + "\n");
         sbText.append("     *\n");

         if (pbUpdate) {
            sbText.append(
                  "     * @param p" + psReqMethod + " " + psReqMethod + "-Element which has to be modified." + psBase + "\n");
         }

         if (psBase.length() > 0) {
            sbText.append("     * @param " + sReqName + " PnrElementBase of type PnrEnumPnrElementType." + psBase + "\n");
         }
         sbText.append("     * @return " + psType + " object\n");
         sbText.append("     * @author _GENERATOR_\n");
         sbText.append("     */\n");
         sbText.append(ANNOTATION_OVERRIDE);
         sbText.append("    protected abstract " + psType + " " + sMethodName + "(");

         String sParameters = "";

         if (pbUpdate) {
            sParameters += psReqMethod + " p" + psReqMethod;
         }

         if (psBase.length() > 0) {
            if (sParameters.length() > 0) {
               sParameters += ", ";
            }

            sParameters += "PnrElementBase " + sReqName;
         }

         sbText.append(sParameters + ")\n");
         sbText.append("    {\n");
         sbText.append("        /**\n");
         sbText.append("         * @todo The method  " + sMethodName + " has to be modified, because it is defined\n");
         sbText.append("         * as a user defined method within DtdInfo.\n");
         sbText.append("         */\n");
         sbText.append("        throw new UnsupportedOperationException(\n");
         sbText.append("            \"Method " + sMethodName + "() not implemented\");\n");
         sbText.append("    } " + DtdTransformerHandler.GENERATED_METHOD + " " + sMethodName + "(" + sParameters + " )\n");
         sText = sbText.toString();
      }

      String sExample = "";

      if (psAttr.length() > 0) {
         sExample = sReqName + "." + psReqMethod + "(getParamValue(PnrEnumPnrElementParameterId." + psAttr + "));";
      } else if (!pbUpdate) {
         sExample = "return new " + psType + "();";
      }

      int lastIndexOf = sText.lastIndexOf("}");
      sText = sText.substring(0, lastIndexOf) + "    // " + sExample + "\n    " + sText.substring(lastIndexOf);

      _abstractMethods.put(sMethod + "." + psRequest, sText);

      // build MethodRequest
      sMethodName += "(";

      if (pbUpdate) {
         sMethodName += "res" + psRequest;
      }

      if (psBase.length() > 0) {
         if (!sMethodName.endsWith("(")) {
            sMethodName += ", ";
         }

         sMethodName += "pnr" + psBase;

      }

      sMethodName += ")";

      if (psType.equalsIgnoreCase("java.util.Date")) {
         // make nothing
      } else if (psType.equalsIgnoreCase("org.exolab.castor.types.Date")) {
         // make also nothing
      } else if (sType.equalsIgnoreCase("Date")) {
         sMethodName = "new org.exolab.castor.types.Date(" + sMethodName + ")";
      }
      return sMethodName;

   }

   /**
    * Method getContent
    *
    * @return
    *
    * @author Andreas Brod
    */
   private String getContent()
   {
      return "";
      //      String sCode = "";
      //
      //      if (true || isResponse()) {
      //         return sCode;
      //      }
      //
      //      // this is only within the response
      //
      //      String sMainName = "pReq" + _sName;
      //      HashSet<String> hsContent = new HashSet<String>();
      //
      //      // validate the contents of the JaxBSource
      //      if (_sSourceCode.indexOf("public void set") > 0) {
      //
      //         // search Elements
      //         try {
      //            int iStart = _sSourceCode.indexOf("public void set");
      //            while (iStart > 0) {
      //               iStart = _sSourceCode.indexOf("set", iStart) + 3;
      //               String sName = _sSourceCode.substring(iStart, _sSourceCode.indexOf("\n", iStart));
      //
      //               if (sName.indexOf("net.ifao.arctic") > 0) {
      //                  hsContent.add(sName.substring(0, sName.indexOf("(")));
      //               }
      //               iStart = _sSourceCode.indexOf("public void set", iStart);
      //            }
      //
      //         }
      //         catch (Exception ex) {}
      //      }
      //
      //      if (hsContent.size() == 0) {
      //
      //         // there is no content
      //         return "";
      //      }
      //
      //      HashSet<String> hsContents = new HashSet<String>();
      //
      //      // loop over all Sub Elements
      //      for (Iterator<String> iter = getSubMethodKeys(); iter.hasNext();) {
      //
      //         String element = iter.next();
      //         TransformerMethod value = getSubMethod(element);
      //
      //         // get SubElements
      //         String sSubElement = value._sName;
      //
      //         // validate the method
      //         if (hsContent.contains(sSubElement)) {
      //            String sNextSub = "";
      //
      //            for (String pnrElement : value.getParameters()) {
      //               sNextSub.append(", pnr" + pnrElement);
      //            }
      //
      //            hsContents.add("Req" + sSubElement + " " + value.getMethodName() + "((Req"
      //                  + sSubElement + ")oContent" + sNextSub + ")");
      //         }
      //
      //      }
      //
      //      // set the contents if there are any
      //
      //      boolean bFirst = true;
      //
      //      sCode.append("          ");
      //      for (String sNext : hsContents) {
      //
      //         if (bFirst) {
      //            bFirst = false;
      //         } else {
      //            sCode.append(" else ");
      //         }
      //
      //         sCode +=
      //            "if (" + sMainName + ".get" + sNext.substring(3, sNext.indexOf(" "))
      //                  + "() != null) {\n";
      //         sCode.append("             " + sNext.substring(sNext.indexOf(" ") + 1) + ";\n");
      //         sCode.append("          }");
      //      }
      //
      //      sCode.append("\n");
      //
      //      return sCode;
   }

   /**
    * Method getAttributes
    *
    * <p> TODO rename htAttr to pAttr
    * @param sCode
    * @param htAttr
    * @return
    *
    * @author Andreas Brod
    */
   private String getAttributes(String psCode, Hashtable<String, String> htAttr)
   {
      String psPath = _tmParent != null ? _tmParent.getName() : "";
      boolean pbResponse = _sType.equals("Res");
      String sMainName = pbResponse ? "res" + _sName : "pReq" + _sName;
      Iterator<String> enumeration = getAttributeKeys();
      StringBuilder sbCode = new StringBuilder(psCode);
      while (enumeration.hasNext()) {

         String sArcticElement = enumeration.next();

         String sPnrAttribute;

         if (sArcticElement.endsWith("!")) {
            sArcticElement = sArcticElement.substring(0, sArcticElement.length() - 1);
            sPnrAttribute = getAttribute(sArcticElement);
            if (sPnrAttribute.indexOf(")") > 0) {
               String sException = sPnrAttribute.substring(sPnrAttribute.indexOf("(") + 1, sPnrAttribute.indexOf(")"));
               sPnrAttribute = sPnrAttribute.substring(sPnrAttribute.indexOf(")") + 1);
               sbCode.append("        // " + sArcticElement + " is defined as mandatory\n");
               String sMethod = getMethod(_sSourceCode, getPnrAttribute(sArcticElement), pbResponse).trim();
               if (sMethod.indexOf(" get") >= 0) {
                  sMethod = sMethod.substring(sMethod.indexOf(" ") + 4);
                  if (getReqHasMethod(_sSourceCode, getPnrAttribute(sArcticElement))) {
                     sbCode.append("        if (!" + sMainName + ".has" + sMethod + "()) {\n");
                  } else {
                     sbCode.append("        if (" + sMainName + ".get" + sMethod + "() == null) {\n");
                  }
                  sbCode.append("            throw new net.ifao.arctic.agents.common.elements.MandatoryException(\n"
                        + "                 \"" + _sName + "." + sMethod + " is defined as mandatory (within DTDInfo)\", "
                        + sException + ");\n");
                  sbCode.append("        }\n");
               }
            }
         } else {
            sPnrAttribute = getAttribute(sArcticElement);
         }

         String sAttribute = getPnrAttribute(sArcticElement);

         if (sPnrAttribute != null && sPnrAttribute.length() > 0) {

            if (sAttribute.equals("%Text;")) {
               sAttribute = "Content";
            }

            boolean hasHasMethod = getReqHasMethod(_sSourceCode, sAttribute);

            String sMethod = getMethod(_sSourceCode, sAttribute, pbResponse).trim();

            // sAttribute.equals("*") means request/response-element
            if (sAttribute.equals("*") && !isResponse()) {
               sMethod = "String \"" + _sName + "\"";
            } else if (sAttribute.equals("*")) {
               sMethod = sMethod + "void Res" + _sName + "";
            }

            if (sMethod.indexOf(" ") > 0) {
               String sType = sMethod.substring(0, sMethod.indexOf(" "));

               sMethod = sMethod.substring(sMethod.indexOf(" ") + 1);

               if (!pbResponse) {
                  sMethod += "()";
               }

               boolean bSelfDefinedTransformer = sPnrAttribute.endsWith("*") || sPnrAttribute.endsWith("*.");

               StringTokenizer st = new StringTokenizer(sPnrAttribute, DtdTransformerHandler.ATTRIBUTEMASK);

               while (st.hasMoreTokens()) {
                  String sToken = st.nextToken();

                  if (sToken.indexOf(".") > 0) {
                     String sBase = sToken.substring(0, sToken.indexOf("."));

                     if (sBase.endsWith("*")) {
                        sBase = sBase.substring(0, sBase.length() - 1);
                     }
                     if (sBase.endsWith("!")) {
                        sBase = sBase.substring(0, sBase.length() - 1);
                     }

                     // hsUsedPnrElements.add("pnr" + sBase);

                     String sPnrAttr = sToken.substring(sToken.indexOf(".") + 1);
                     String sAttr = htAttr.get(sBase);

                     if (sPnrAttr.endsWith("!")) {
                        sPnrAttr = sPnrAttr.substring(0, sPnrAttr.length() - 1);
                     }
                     if (sAttr == null) {
                        sAttr = "";
                     }

                     if (sPnrAttr.length() > 0) {
                        sAttr += getPnrAttr(sAttr, hasHasMethod, sBase, sType, psPath, _sName, sMainName, sMethod, sPnrAttr,
                              pbResponse, bSelfDefinedTransformer);

                        htAttr.put(sBase, sAttr);

                     } else {
                        if (sAttribute.equals("*")) {

                           sAttr += getPnrAttr(sAttr, hasHasMethod, sBase, "void", psPath, _sName, sMainName, sMethod, sPnrAttr,
                                 pbResponse, bSelfDefinedTransformer);

                           htAttr.put(sBase, sAttr);

                        } else if (pbResponse && sAttribute.equals("for")) {

                           if (bSelfDefinedTransformer) {
                              sAttr += "            try {\n  ";
                              sAttr += getPnrAttr(sAttr, hasHasMethod, sBase, sType, psPath, _sName, sMainName, sMethod,
                                    "getForFromCorrespondingTraveller()", pbResponse, bSelfDefinedTransformer);

                              sAttr += "            }\n            catch (Exception ex) {\n";
                              sAttr += "                res" + _sName + ".setFor(1);\n";
                              sAttr += "            }\n";
                           } else {

                              sAttr += "\n            assignResponseTraveller(res" + _sName + "" + ",pnr" + sBase + ");\n";
                           }
                           htAttr.put(sBase, sAttr);

                        } else if (pbResponse && sAttribute.equals("segment")) {

                           sAttr += "\n            assignResponseSegment" + getType(_sName + psPath) + "(res" + _sName + ""
                                 + ",pnr" + sBase + ");\n";
                           htAttr.put(sBase, sAttr);

                        }

                     }

                  }
               }
            } else if (!sAttribute.equalsIgnoreCase("_choice")) {
               sbCode.append("        // " + sPnrAttribute + " = " + sAttribute + "\n");
            }
         }
      }

      if (htAttr.size() > 0) {
         sbCode.append("\n        // Set Attributes\n");

         if (pbResponse) {

            // replace all bFound = true;

            String sMethodCode = sbCode.substring(sbCode.lastIndexOf("*/"));
            sbCode = new StringBuilder(sbCode.substring(0, sbCode.lastIndexOf("*/"))
                  + sMethodCode.replaceAll("bFound = true", "// bFound will be set within attributes"));

            for (String sPnrElement : htAttr.keySet()) {

               String sPnrElementName = sPnrElement.substring(sPnrElement.indexOf(">") + 1);
               if (sPnrElementName.length() == 0) {
                  sbCode.append("        if (true) {\n");
               } else {
                  sbCode.append("        if (pnr" + sPnrElementName + " != null) {\n");
               }

               if (sMethodCode.indexOf("(int piPersonId,") > 0) {
                  sbCode.append("            assignResponseTraveller(piPersonId, pnr" + sPnrElementName + ");\n\n");
               }
               if (sMethodCode.indexOf("Segment(") > 0 && sMethodCode.indexOf("SubSegment(") < 0) {
                  sbCode.append(
                        "            assignResponseSegment" + getType(_sName + psPath) + "(pnr" + sPnrElementName + ");\n\n");
               }

               String sAttr = htAttr.get(sPnrElement);
               if (sAttr.indexOf("bFound = true;") < 0) {
                  sbCode.append("            bFound = true; // Element was found\n");
               } else {
                  sbCode.append("            // Set bFound within ...\n");
               }
               sbCode.append(sAttr);
               sbCode.append("        }\n");
            }

         } else {
            for (String sPnrElement : htAttr.keySet()) {
               sbCode.append(htAttr.get(sPnrElement));
            }

         }
      }

      return sbCode.toString();
   }

   /**
    * TODO (brod) add comment for method getType
    *
    * @param psPath TODO (brod) add text for param psPath
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   private String getType(String psPath)
   {
      int iAir = psPath.lastIndexOf("Air");
      int iCar = psPath.lastIndexOf("Car");
      int iHotel = psPath.lastIndexOf("Hotel");
      int iCtwItem = psPath.lastIndexOf("CtwItem");
      int iRail = psPath.lastIndexOf("Rail");
      int iMax = Math.max(Math.max(Math.max(Math.max(iAir, iCar), iHotel), iRail), iCtwItem);
      if (iMax >= 0) {
         if (iAir == iMax) {
            return "Air";
         }
         if (iCar == iMax) {
            return "Car";
         }
         if (iHotel == iMax) {
            return "Hotel";
         }
         if (iRail == iMax) {
            return "Rail";
         }
         if (iCtwItem == iMax) {
            return "CtwItem";
         }
      }
      return "Other";
   }

   /**
    * Method getMethodName
    *
    * @return
    *
    * @author Andreas Brod
    */
   protected String getMethodName()
   {
      return (_sType.startsWith("Res") ? "get" : "add") + getName();
   }

   /**
    * Method getSubMethod
    *
    * @param psType
    * @param psName
    * @param psSourceCode
    * @return
    *
    * @author Andreas Brod
    */
   protected TransformerMethod getSubMethod(String psType, String psName, String psSourceCode)
   {
      TransformerMethod subMethod = getSubMethod(psType + psName);

      if (subMethod == null) {
         subMethod =
            new TransformerMethod(_dtdTransformerHandler, this, psType, psName, _sDeep + "  ", psSourceCode, _abstractMethods);

         setSubMethod(psType + psName, subMethod);
      }

      return subMethod;
   }

   /**
    * Method addAttribute
    *
    * <p> TODO rename sName to psName, sValue to psValue
    * @param sName
    * @param sValue
    *
    * @author Andreas Brod
    */
   protected void addAttribute(String sName, String sValue)
   {
      setAttribute(sName, sValue);

      StringTokenizer st = new StringTokenizer(sValue, "*" + DtdTransformerHandler.ATTRIBUTEMASK);

      while (st.hasMoreElements()) {
         String sItem = st.nextToken() + ".";

         sItem = sItem.substring(0, sItem.indexOf("."));

         if (sItem.trim().length() > 0) {
            if (sItem.startsWith("Mandatory") && sItem.indexOf(")") > 0) {
               sItem = sItem.substring(sItem.indexOf(")") + 1);
            }
            if (!contains(_pnrElements, sItem)) {
               _pnrElements.add(sItem);
            }
         }
      }
   }

   /**
    * TODO (brod) add comment for method contains
    *
    * @param pLstElements TODO (brod) add text for param pLstElements
    * @param psItem TODO (brod) add text for param psItem
    * @return TODO (brod) add text for returnValue
    *
    * @author brod
    */
   private boolean contains(List<String> pLstElements, String psItem)
   {
      if (psItem.startsWith("Mandatory") && psItem.indexOf(")") > 0) {
         // case of MandatoryException
         psItem = psItem.substring(psItem.indexOf(")") + 1);
      }

      String sItem = "->" + psItem;

      for (int i = 0; i < pLstElements.size(); i++) {
         String element = pLstElements.get(i);
         if (element.equals(psItem)) {
            return true;
         }
         if (element.endsWith(sItem)) {
            return true;
         }
      }
      return false;
   }

   /**
    * Method getName
    *
    * @return
    *
    * @author Andreas Brod
    */
   private String getName()
   {
      return (_tmParent != null ? _tmParent.getName() : "") + _sName;
   }

   /**
    * Method parentContainsPnrElement
    *
    * @param psElement
    * @return
    *
    * @author Andreas Brod
    */
   private boolean parentContainsPnrElement(String psElement)
   {
      if (_tmParent != null) {
         if (_tmParent._pnrElements.contains(psElement)) {
            return true;
         }

         return _tmParent.parentContainsPnrElement(psElement);
      }

      return false;
   }

   /**
    * Method parentContainsPnrElement
    *
    *
    * @return
    *
    * @author Andreas Brod
    */
   private HashSet<String> getSubPnrElements()
   {
      HashSet<String> hsElements = new HashSet<>();

      for (String element1 : _pnrElements) {
         if (element1.trim().length() > 0) {
            hsElements.add(element1);
         }
      }

      for (Iterator<String> iter = getSubMethodKeys(); iter.hasNext();) {
         String element = iter.next();
         TransformerMethod value = getSubMethod(element);

         hsElements.addAll(value.getSubPnrElements());
      }
      hsElements.remove("_CHOICE");

      return hsElements;
   }

   /**
    * Method getParentPnrElements
    *
    * @return
    *
    * @author Andreas Brod
    */
   private HashSet<String> getParentPnrElements()
   {
      HashSet<String> hsElements = new HashSet<>();

      if (_tmParent != null) {

         for (String element1 : _tmParent._pnrElements) {
            if (element1.trim().length() > 0) {
               hsElements.add(element1);
            }
         }

         hsElements.addAll(_tmParent.getParentPnrElements());
      }
      hsElements.remove("_CHOICE");

      return hsElements;
   }

   /**
    * Method toSourceCode
    *
    * @return SourceCode
    *
    * @author Andreas Brod
    */
   public String toSourceCode(String psFor)
   {

      if (_sName.trim().length() == 0) {
         return "";
      }


      StringBuilder sbCurrentMethod = new StringBuilder();
      // Display the title
      sbCurrentMethod.append(getMethodHeader(psFor));

      StringBuilder sbFor = new StringBuilder();
      if (psFor.length() > 0) {
         sbFor.append("psFor");
      }
      // Assign the parameters
      sbCurrentMethod.append(assignParameterElements(sbFor, psFor));

      boolean firstElement = true;

      HashSet<String> localElements = getParameters();
      HashSet<String> createdElements = new HashSet<>();
      String sChoice = "";

      // create new Pnr Elements
      for (String element : _pnrElements) {
         if (element.equalsIgnoreCase("_CHOICE")) {
            sChoice = element;
         } else if (!parentContainsPnrElement(element)) {
            if (firstElement) {
               sbCurrentMethod.append("        // Create Elements");
               firstElement = false;
            }

            sbCurrentMethod.append(createElement(element, _sName.startsWith("Cancel") && !_sName.startsWith("Cancellation"),
                  localElements, createdElements, sbFor, psFor));
         }
      }

      if (sChoice.length() > 0) {
         _pnrElements.remove(_pnrElements);
      }
      firstElement = true;

      Hashtable<String, String> htAttr = new Hashtable<>();

      // Display the attributes
      sbCurrentMethod = new StringBuilder(getAttributes(sbCurrentMethod.toString(), htAttr));

      // List all subMethods
      for (Iterator<String> iter = getSubMethodKeys(); iter.hasNext();) {

         String element = iter.next();
         TransformerMethod value = getSubMethod(element);

         if (firstElement) {

            // Rule (Validation of responseObjects)
            if (isResponse() && htAttr.size() > 0) {
               sbCurrentMethod.append("        // if Element was not found so far ...\n");
               sbCurrentMethod.append("        if (!bFound) {\n");
               sbCurrentMethod.append("            logEndResponse(\"" + _sName + "\", bFound);\n");
               sbCurrentMethod.append("            return null;\n");
               sbCurrentMethod.append("        }\n");
            }

            sbCurrentMethod.append("        // Transform Sub Elements\n");

            firstElement = false;
         }

         sbCurrentMethod.append(callSub(element.substring(3), value, isResponse(), sChoice.length() > 0, sbFor.toString()));
      }

      sbCurrentMethod.append(getContent());

      sbCurrentMethod.append(getMethodFooter());

      removeUnnecessarySuppressWarningsAnnotation(sbCurrentMethod);


      StringBuffer sbRet = new StringBuffer();

      // Add sourceCodes of all subMethods
      for (Iterator<String> iter = getSubMethodKeys(); iter.hasNext();) {
         String element = iter.next();
         TransformerMethod value = getSubMethod(element);

         sbRet.append(value.toSourceCode(sbFor.toString()));
      }

      sbRet.append(sbCurrentMethod);

      String sRet = sbRet.toString();

      // avoid findBugs errors
      int iPos = sRet.lastIndexOf("pnrElements = addPnrElementBase(");
      if (iPos > 0) {
         int iStart = sRet.indexOf(")", iPos);
         if (iStart > 0) {
            if (sRet.indexOf("pnrElements", iStart) < 0) {
               sRet = sRet.substring(0, iPos) + sRet.substring(iPos + 14);
            }
         }
      }
      if (!sRet.contains(" pnrElements)")) {
         int iRemark = sRet.indexOf("PnrElementBase[] pnrElements = parrPnrElements");
         if (iRemark > 0) {
            sRet = sRet.substring(0, iRemark) + "// " + sRet.substring(iRemark);
         }
      }

      iPos = sRet.lastIndexOf("boolean bKeepSegment =");
      if (iPos > 0) {
         int iStart = sRet.indexOf("bKeepSegment", iPos + 20);
         if (iStart < 0) {
            sRet = sRet.substring(0, iPos) + "// Rebook Segment" + sRet.substring(sRet.indexOf(";", iPos) + 1);
         }
      }
      return sRet;
   }

   /**
    * Method removeUnnecessarySuppressWarningsAnnotation
    *
    * @param psbRet TODO (brod) add text for param psbRet
    *
    * @author kaufmann
    */
   private void removeUnnecessarySuppressWarningsAnnotation(StringBuilder psbRet)
   {
      if (psbRet.indexOf(SUPPRESS_WARNINGS_UNCHECKED) >= 0 && !psbRet.toString().matches("(?s).*res.*\\.add\\(res.*?\\).*")) {
         String sNew =
            psbRet.toString().replaceFirst(SUPPRESS_WARNINGS_UNCHECKED.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)"), "");
         psbRet.setLength(0);
         psbRet.append(sNew);
      }
   }
}
