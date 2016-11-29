package schemagenerator.actions.jaxb;


import net.ifao.xml.*;


/** 
 * @author ernst 
 */
public class EnumBindingCorrector
{
   XmlObject _schema;
   XmlObject _binding;

   /** 
    * TODO (ernst) add comment for Constructor EnumBindingCorrector 
    * 
    * @param pSchema TODO (ernst) add text for param pSchema
    * @param pBinding TODO (ernst) add text for param pBinding
    * 
    * @author ernst 
    */
   public EnumBindingCorrector(XmlObject pSchema, XmlObject pBinding)
   {
      _schema = pSchema;
      _binding = pBinding;
   }

   /** 
    * Searches the schema for anonymous simpleType enums and creates the referring bindings. 
    * It serves as entry point for the recursion 
    * 
    * @author kaufmann, ernst 
    */
   public void addBindingsForSimpleTypeEnums()
   {
      XmlObject[] xmlObjects = _schema.getObjects("");
      for (XmlObject xmlObject : xmlObjects) {
         addBindingsForSimpleTypeEnums(xmlObject, "/" + getPathPart(xmlObject),
               xmlObject.getAttribute("name"));
      }
   }

   /**
    * Creates the xpath (part) for the xmlObject passed, e.g. <pre>
    * /xs:complexType[@name='FareType']
    * </pre> 
    *
    * @param pXmlObject xmlObject for which the xpath (part) should be created
    * @return part for a xpath
    *
    * @author kaufmann, ernst
    */
   private String getPathPart(XmlObject pXmlObject)
   {
      StringBuilder sbPart = new StringBuilder();
      sbPart.append("/");
      sbPart.append(pXmlObject.getFullName());
      String sName = pXmlObject.getAttribute("name");
      if (sName != null && sName.length() > 0) {
         sbPart.append("[@name='");
         sbPart.append(sName);
         sbPart.append("']");
      }
      return sbPart.toString();
   }

   /** 
    * Recursive method which checks, if the xmlObject passed is an anonymous simpleType enum (attributes and elements). 
    * If yes, the binding for this attribute/element is added to the binding for the schema (field _binding) 
    * 
    * <p> TODO rename sLastName to psLastName
    * @param pXmlObject xmlObject to check 
    * @param psPath TODO (ernst) add text for param psPath
    * @param sLastName TODO (ernst) add text for param sLastName
    * 
    * @author kaufmann, ernst 
    */
   private void addBindingsForSimpleTypeEnums(XmlObject pXmlObject, String psPath, String sLastName)
   {
      if (pXmlObject.getName().equalsIgnoreCase("attributeGroup")) {
         return;
      }

      if ((pXmlObject.getName().equalsIgnoreCase("attribute") || pXmlObject.getName()
            .equalsIgnoreCase("element")) && pXmlObject.getObjects("simpleType").length > 0) {
         if (isEnum(pXmlObject)) {
            // <jaxb:bindings node="//xs:complexType[@name='PointToPointShoppingQuery']/xs:attribute[@name='fareFilter']/xs:simpleType">
            //   <jaxb:typesafeEnumClass name="EnumFareFilter" />
            // </jaxb:bindings>
            XmlObject bindingsForEnum =
               _binding.createObject("jaxb:bindings", "node", psPath + "/xs:simpleType", true);
            bindingsForEnum.createObject("jaxb:typesafeEnumClass", "name", "Enum" + sLastName + "_"
                  + pXmlObject.getAttribute("name"), true);

            createBindingsForEnumValues(pXmlObject, bindingsForEnum);
         }
      } else {
         String sName = pXmlObject.getAttribute("name");
         if (sName.length() == 0) {
            sName = sLastName;
         }
         for (XmlObject xmlObject : pXmlObject.getObjects("")) {
            addBindingsForSimpleTypeEnums(xmlObject, psPath + getPathPart(xmlObject), sName);
         }
      }
   }

   /** 
    * Creates bindings for enumeration values.
    * 
    * Numbers are mapped to "VALUE_(number)" (e.g.: 1 -> VALUE_1)
    * Negative numbers are mapped to "VALUE_MINUS_(number)" (e.g.: -1 -> VALUE_MINUS_1)
    * 
    * This is necessary, because JaxB compiler can not handle enumeration values which are numbers (e.g. 1, 2, ...)
    * or negative numbers (e.g. -1, -2, ...).
    * 
    * The minus sign "-" is replaced by an underscore "_", as minus sign is not allowed in variable names.
    * 
    * @param pXmlObject The xml object containing the number(s) as enumeration value(s)
    * @param pBindingsForEnum The binding which is being adapted
    * 
    * @author ernst 
    */
   private void createBindingsForEnumValues(XmlObject pXmlObject, XmlObject pBindingsForEnum)
   {
      XmlObject simpleType = pXmlObject.getObject("simpleType");
      if (simpleType != null) {
         XmlObject restriction = simpleType.getObject("restriction");
         if (restriction != null) {
            XmlObject[] enumerations = restriction.getObjects("enumeration");
            for (XmlObject enumeration : enumerations) {
               String sValue = enumeration.getAttribute("value");

               if (sValue != null && sValue.length() > 0) {
                  String sEnumMember = sValue;

                  if (sEnumMember.matches("-.*")) {
                     sEnumMember = "VALUE_MINUS" + sEnumMember;
                  }

                  if (sEnumMember.contains("-")) {
                     sEnumMember = sEnumMember.replace("-", "_");
                  }

                  if (sEnumMember.matches("[0-9].*")) {
                     sEnumMember = "VALUE_" + sEnumMember;
                  }

                  if (!sEnumMember.equals(sValue)) {
                     pBindingsForEnum.createObject("jxb:bindings", "node",
                           "xs:restriction/xs:enumeration[@value='" + sValue + "']", true)
                           .createObject("jxb:typesafeEnumMember", "name", sEnumMember, true);
                  }
               }
            }
         }
      }
   }

   /** 
    * Checks recursivly, if the current xmlObject contains an enumeration 
    * 
    * @param pXmlObject xmlObject to check 
    * @return true, if subelement "enumeration" has been found 
    * 
    * @author kaufmann, ernst 
    */
   private boolean isEnum(XmlObject pXmlObject)
   {
      boolean bIsEnum = false;

      // Regard enumerations which are within a union not as enumeration
      if (pXmlObject.getObjects("union").length > 0) {
         return false;
      }

      if (pXmlObject.getObjects("enumeration").length > 0) {
         return true;
      }
      for (XmlObject xmlObject : pXmlObject.getObjects("")) {
         bIsEnum = isEnum(xmlObject);
         if (bIsEnum == true) {
            return true;
         }
      }
      return bIsEnum;
   }

   /** 
    * TODO (ernst) add comment for method main 
    * 
    * <p> TODO rename args to pArgs
    * @param args TODO (ernst) add text for param args
    * 
    * @author ernst 
    */
   public static void main(String[] args)
   {
      EnumBindingCorrector corr = new EnumBindingCorrector(null, null);

      String sXmlObject = "<xs:attribute name=\"ReroutingType\" use=\"optional\">";
      sXmlObject += "<xs:annotation>";
      sXmlObject +=
         "<xs:documentation xml:lang=\"en\">Indicates if the rerouting, which made the exchange necessary was voluntary or involuntary.</xs:documentation>";
      sXmlObject += "   </xs:annotation>";
      sXmlObject += "   <xs:simpleType>";
      sXmlObject += "     <xs:restriction base=\"xs:NMTOKEN\">";
      sXmlObject += "        <xs:enumeration value=\"voluntary\" />";
      sXmlObject += "        <xs:enumeration value=\"involuntary\" />";
      sXmlObject += "      </xs:restriction>";
      sXmlObject += "   </xs:simpleType>";
      sXmlObject += "</xs:attribute>";

      String sPath =
         "//xs:complexType[@name='PaymentFormType']/xs:choice/xs:element[@name='Ticket']/xs:complexType/xs:attribute[@name='ReroutingType']";
      String sLastName = "Ticket";

      XmlObject xmlo = new XmlObject(sXmlObject);

      corr.addBindingsForSimpleTypeEnums(xmlo, sPath, sLastName);
      System.out.println();
   }
}
