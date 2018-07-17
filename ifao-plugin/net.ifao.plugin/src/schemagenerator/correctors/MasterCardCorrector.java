package schemagenerator.correctors;


import java.util.*;

import net.ifao.xml.*;


/**
 * Corrects incompatibilities between the mastercard schema and the castor capabilities.
 *
 * <p>
 * Copyright &copy; 2011, i:FAO Group GmbH
 * @author kaufmann
 */
public class MasterCardCorrector
   implements ICorrector
{

   private static final Map<String, String> BASE_TYPES = new HashMap<String, String>()
   {
      { // initialize the map with the ("invalid" restriction) base types and their replacements 
         put("xs:unsignedLong", "xs:long");
         put("xs:unsignedInt", "xs:integer");
         put("xs:unsignedShort", "xs:short");
         put("xs:unsignedByte", "xs:byte");
         put("xs:normalizedString", "xs:string");
         put("xs:nonNegativeInteger", "xs:long");
      }
   };


   /**
    * @see schemagenerator.correctors.AbstractCorrector#correctXmlObject(net.ifao.xml.XmlObject)
    *
    * @author kaufmann
    */
   @Override
   public void correct(XmlObject pSchema)
   {
      // remove the empty namespace
      pSchema.setAttribute("xmlns", null);

      // collect the simpletypes
      Map<String, String> simpleTypes = collectAndRemoveSimpleTypes(pSchema);

      // change xs:token to xs:string (without removal ;-))
      simpleTypes.put("xs:token", "xs:string");

      // perform the changes
      replaceSimpleTypes(pSchema, simpleTypes);

      // merge unions to a single type
      mergeUnions(pSchema);

      // change default values of StateProvince from "--" to "UNK"
      fixStateProvinceDefaults(pSchema);
   }

   /**
    * Changes default values of elements of type StateProvinceType from "--" to "UNK"; the schema 
    * defines "--" as default, but "--" is not part of the enumerator, so it has been decided to
    * use "UNK" as default.
    *
    * @param pSchema the MasterCard schema
    *
    * @author kaufmann
    */
   private void fixStateProvinceDefaults(XmlObject pXmlObject)
   {
      // get the sub elements
      XmlObject[] objects = pXmlObject.getObjects("");
      // do it recursively for all sub elements
      for (XmlObject object : objects) {
         fixStateProvinceDefaults(object);
      }

      // check the "type" attribute
      String sType = pXmlObject.getAttribute("type");
      if ("StateProvinceType".equals(sType)) {
         // check the default value
         String sDefault = pXmlObject.getAttribute("default");
         if ("--".equals(sDefault)) {
            pXmlObject.setAttribute("default", "UNK");
         }
      }
   }

   /**
    * Removes the unions from the simple types and keeps the enumerations of all "unioned" types
    *
    * @param pSchema schema object
    *
    * @author kaufmann
    */
   private void mergeUnions(XmlObject pSchema)
   {
      Map<String, XmlObject> simpleTypes = new HashMap<String, XmlObject>();
      Set<String> unionTypes = new HashSet<String>();

      // collect the simpleTypes and the types with unions
      XmlObject[] xmlObjects = pSchema.getObjects("simpleType");
      for (XmlObject type : xmlObjects) {
         String sName = type.getAttribute("name");
         simpleTypes.put(sName, type);
         XmlObject union = type.getObject("union");
         if (union != null) {
            unionTypes.add(sName);
         }
      }

      // merge the unions by keeping the enumerations
      for (String sUnion : unionTypes) {
         XmlObject unionType = simpleTypes.get(sUnion);
         XmlObject union = unionType.getObject("union");
         XmlObject unionTypeRestriction = getRestrictionWithEnums(union);
         String sMembers = union.getAttribute("memberTypes");
         // get the members
         if (sMembers != null && sMembers.length() > 0) {
            XmlObject memberType = simpleTypes.get(sMembers);
            XmlObject restriction = memberType.getObject("restriction");
            if (restriction != null) {
               if (unionTypeRestriction == null) {
                  unionTypeRestriction = restriction;
               } else {
                  XmlObject[] enumerationsMember = restriction.getObjects("enumeration");
                  for (XmlObject enumMember : enumerationsMember) {
                     unionTypeRestriction.addObject(enumMember);
                  }
               }
            }
         }
         unionType.deleteObjects("union");
         unionType.addObject(unionTypeRestriction);
      }
   }

   /**
    * Finds a simpleType definition containing enumeration values; Unions might have more than one
    * simpleType, so the first one containing enumeration values will be taken. If none has been 
    * found, <code>null</code> will be returned
    *
    * @param pUnion union element
    * @return restriction element containing enumeration values
    *
    * @author kaufmann
    */
   private XmlObject getRestrictionWithEnums(XmlObject pUnion)
   {
      XmlObject[] simpleTypes = pUnion.getObjects("simpleType");
      for (XmlObject simpleType : simpleTypes) {
         XmlObject restriction = simpleType.getObject("restriction");
         if (restriction != null) {
            XmlObject[] enums = restriction.getObjects("enumeration");
            if (enums != null && enums.length > 0) {
               return restriction;
            }
         }
      }
      return null;
   }

   /**
    * Replaces all simpleTypes, which had been removed by method , with their replacement types.
    * This will be done for all attributes "type" and "base" and recursively for all sub elements.
    *
    * @param pXmlObject current object
    * @param pSimpleTypes list of types to replace
    *
    * @author kaufmann
    */
   private void replaceSimpleTypes(XmlObject pXmlObject, Map<String, String> pSimpleTypes)
   {
      // get the sub elements
      XmlObject[] objects = pXmlObject.getObjects("");
      // do it recursively for all sub elements
      for (XmlObject object : objects) {
         replaceSimpleTypes(object, pSimpleTypes);
      }

      // check the "type" attribute
      String sType = pXmlObject.getAttribute("type");
      if (sType != null && sType.length() > 0) {
         String sSimpleType = pSimpleTypes.get(sType);
         if (sSimpleType != null) {
            pXmlObject.setAttribute("type", sSimpleType);
         }
      } else {
         // check the "base" attribute
         String sBase = pXmlObject.getAttribute("base");
         if (sBase != null && sBase.length() > 0) {
            String sSimpleType = pSimpleTypes.get(sBase);
            if (sSimpleType != null) {
               pXmlObject.setAttribute("base", sSimpleType);
            }
         }
      }
   }

   /**
    * Collects all definitions of simpleTypes which use as base type one of the types mentioned in
    * BASE_TYPES. The names of the defined simpleTypes will be stored together with the replacement
    * type in a map. The type definitions will be removed from the schema. Later the elements using
    * these types will use the replacement type stored in the map (see method replaceSimpleTypes)
    *
    * @param pSchema schema object
    * @return map containing the simpleType names, which have been removed, together with their 
    * replacement types
    *
    * @author kaufmann
    */
   private Map<String, String> collectAndRemoveSimpleTypes(XmlObject pSchema)
   {
      Map<String, String> simpleTypes = new HashMap<String, String>();

      XmlObject[] xmlObjects = pSchema.getObjects("simpleType");
      for (XmlObject type : xmlObjects) {
         String sName = type.getAttribute("name");
         XmlObject restriction = type.getObject("restriction");
         if (restriction != null) {
            String sBase = restriction.getAttribute("base");
            if (BASE_TYPES.containsKey(sBase)) {
               simpleTypes.put(sName, BASE_TYPES.get(sBase));
               pSchema.deleteObjects(type);
            }
         }
      }

      return simpleTypes;
   }

   /**
    * @see schemagenerator.correctors.AbstractCorrector#getCorrectionSummary()
    *
    * @author kaufmann
    */
   @Override
   public String getCorrectionSummary()
   {
      StringBuilder sbSummary = new StringBuilder();
      for (Map.Entry<String, String> entry : BASE_TYPES.entrySet()) {
         sbSummary.append("- removes simpleTypes with restriction base=\"").append(entry.getKey())
               .append("\" and replaces the element's type with \"").append(entry.getValue())
               .append("\"\n");
      }
      sbSummary.append("- replaces \"xs:token\" by \"xs:string\"\n");
      sbSummary
            .append("- merges the unions to a single restriction by keeping only all enumeration values of the merged types\n");
      sbSummary
            .append("- changes the default of elements with type=\"StateProvinceType\" from \"--\" to \"UNK\", because \"--\" is not part of the enumerator \n");
      return sbSummary.toString();
   }

}
