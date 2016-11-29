package net.ifao.tools.schemaconversion.localtoglobalelements;


import java.util.ArrayList;


/**
 * <p>This class contains the schema handler.
 * <p>&copy; 2010 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public class SchemaHandler
{

   // schema header
   private static final String SCHEMA_HEADER_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
   private static final String SCHEMA_HEADER_2 =
      "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">";

   // element binding header
   private static final String ELEMENT_BINDING_HEADER =
      "<binding xmlns=\"http://www.castor.org/SourceGenerator/Binding\" defaultBindingType=\"element\">";


   // generated data
   private ArrayList<String> _lstLine;


   //------------------ Methods ----------------------------------------------------
   //------------------------------------------------------------------------------

   /**
    * <p>Constructor.
    * @param plstLine generated data
    */
   public SchemaHandler(ArrayList<String> plstLine)
   {
      _lstLine = plstLine;
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates the schema header.
    * @author Jochen Pinder 
    */
   public void createSchemaHeader()
   {

      _lstLine.add(SCHEMA_HEADER_1);
      _lstLine.add(SCHEMA_HEADER_2);
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates the schema footer.
    * @author Jochen Pinder 
    */
   public void createSchemaFooter()
   {

      _lstLine.add("</xs:schema>");
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates the start of a schema root complex type local element.
    * @param ptElement schema root element
    * @param piHierachyLevel current hierarchy level
    * @author Jochen Pinder 
    */
   public void createSchemaRootComplexTypeLocalElementStart(TElement ptElement, int piHierachyLevel)
   {

      String sIndent = createIndent(piHierachyLevel);
      StringBuilder sb;

      sb = new StringBuilder();
      sb.append(sIndent);
      sb.append("<xs:element name=\"");
      sb.append(ptElement.getName());
      sb.append("\">");
      _lstLine.add(sb.toString());

      sb = new StringBuilder();
      sb.append(sIndent);
      sb.append(" <xs:complexType>");
      _lstLine.add(sb.toString());

      sb = new StringBuilder();
      sb.append(sIndent);
      sb.append("  <xs:sequence>");
      _lstLine.add(sb.toString());
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates the start of a schema complex type local element.
    * @param ptElement schema complex type element
    * @param piHierachyLevel current hierarchy level
    * @author Jochen Pinder 
    */
   public void createSchemaComplexTypeLocalElementStart(TElement ptElement, int piHierachyLevel)
   {

      String sIndent = createIndent(piHierachyLevel);
      StringBuilder sb;

      sb = new StringBuilder();
      sb.append(sIndent);
      sb.append("<xs:element name=\"");
      sb.append(ptElement.getName());
      sb.append("\" ");
      sb.append("minOccurs=\"");
      sb.append(ptElement.getMininumOccurrence());
      sb.append("\" ");
      sb.append("maxOccurs=\"");
      sb.append(ptElement.getMaximumOccurrence());
      sb.append("\">");
      _lstLine.add(sb.toString());

      sb = new StringBuilder();
      sb.append(sIndent);
      sb.append(" <xs:complexType>");
      _lstLine.add(sb.toString());

      sb = new StringBuilder();
      sb.append(sIndent);
      sb.append("  <xs:sequence>");
      _lstLine.add(sb.toString());
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates the end of a schema complex type local element.
    * @param piHierachyLevel current hierarchy level
    * @author Jochen Pinder 
    */
   public void createSchemaComplexTypeLocalElementEnd(int piHierachyLevel)
   {

      String sIndent = createIndent(piHierachyLevel);
      StringBuilder sb;

      sb = new StringBuilder();
      sb.append(sIndent);
      sb.append("  </xs:sequence>");
      _lstLine.add(sb.toString());

      sb = new StringBuilder();
      sb.append(sIndent);
      sb.append(" </xs:complexType>");
      _lstLine.add(sb.toString());

      sb = new StringBuilder();
      sb.append(sIndent);
      sb.append("</xs:element>");
      _lstLine.add(sb.toString());
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates a schema simple type local element.
    * @param ptElement schema simple type element
    * @param piHierachyLevel current hierarchy level
    * @author Jochen Pinder 
    */
   public void createSchemaSimpleTypeLocalElement(TElement ptElement, int piHierachyLevel)
   {

      String sIndent = createIndent(piHierachyLevel);
      StringBuilder sb;

      sb = new StringBuilder();
      sb.append(sIndent);
      sb.append("<xs:element name=\"");
      sb.append(ptElement.getName());
      sb.append("\" ");
      sb.append("minOccurs=\"");
      sb.append(ptElement.getMininumOccurrence());
      sb.append("\" ");
      sb.append("maxOccurs=\"");
      sb.append(ptElement.getMaximumOccurrence());
      sb.append("\" ");
      sb.append("type=\"");
      sb.append(ptElement.getSimpleType());
      sb.append("\" />");
      _lstLine.add(sb.toString());
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates the start of a schema complex type global element.
    * @param ptElement schema complex type element
    * @author Jochen Pinder 
    */
   public void createSchemaComplexTypeGlobalElementStart(TElement ptElement)
   {

      StringBuilder sb;

      sb = new StringBuilder();
      sb.append("<xs:element name=\"");
      sb.append(ptElement.getName());
      sb.append("\">");
      _lstLine.add(sb.toString());

      sb = new StringBuilder();
      sb.append(" <xs:complexType>");
      _lstLine.add(sb.toString());

      sb = new StringBuilder();
      sb.append("  <xs:sequence>");
      _lstLine.add(sb.toString());
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates a sub-element of a schema complex type global element.
    * @param ptSubElement sub-element of a schema complex type global element
    * @author Jochen Pinder 
    */
   public void createSchemaComplexTypeGlobalSubElement(TElement ptSubElement)
   {

      StringBuilder sb;

      sb = new StringBuilder();
      sb.append("   <xs:element ref=\"");
      sb.append(ptSubElement.getName());
      sb.append("\" ");
      sb.append("minOccurs=\"");
      sb.append(ptSubElement.getMininumOccurrence());
      sb.append("\" ");
      sb.append("maxOccurs=\"");
      sb.append(ptSubElement.getMaximumOccurrence());
      sb.append("\" />");
      _lstLine.add(sb.toString());
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates the end of a schema complex type global element.
    * @author Jochen Pinder 
    */
   public void createSchemaComplexTypeGlobalElementEnd()
   {

      StringBuilder sb;

      sb = new StringBuilder();
      sb.append("  </xs:sequence>");
      _lstLine.add(sb.toString());

      sb = new StringBuilder();
      sb.append(" </xs:complexType>");
      _lstLine.add(sb.toString());

      sb = new StringBuilder();
      sb.append("</xs:element>");
      _lstLine.add(sb.toString());

      _lstLine.add("");
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates a schema simple type global element.
    * @param ptElement schema simple type element
    * @author Jochen Pinder 
    */
   public void createSchemaSimpleTypeGlobalElement(TElement ptElement)
   {

      StringBuilder sb;

      sb = new StringBuilder();
      sb.append("<xs:element name=\"");
      sb.append(ptElement.getName());
      sb.append("\" ");
      sb.append("type=\"");
      sb.append(ptElement.getSimpleType());
      sb.append("\" />");
      _lstLine.add(sb.toString());
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates a schema comment.
    * @param psComment schema comment
    * @author Jochen Pinder 
    */
   public void createSchemaComment(String psComment)
   {

      StringBuilder sb;

      _lstLine.add("");
      _lstLine.add("");
      _lstLine.add("<!-- -->");

      sb = new StringBuilder();
      sb.append("<!-- ");
      sb.append(psComment);
      sb.append(" -->");
      _lstLine.add(sb.toString());

      _lstLine.add("<!-- -->");
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates the castor binding header.
    * @author Jochen Pinder 
    */
   public void createCastorBindingHeader()
   {

      _lstLine.add(ELEMENT_BINDING_HEADER);
      _lstLine.add("");
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates the castor binding footer.
    * @author Jochen Pinder 
    */
   public void createCastorBindingFooter()
   {

      _lstLine.add("</binding>");
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates a castor element binding.
    * @param psElementPathName schema element path name
    * @param psJavaClassName JAVA class name
    * @author Jochen Pinder 
    */
   public void createCastorElementBinding(String psElementPathName, String psJavaClassName)
   {

      StringBuilder sb;

      sb = new StringBuilder();
      sb.append("  <elementBinding name=\"");
      sb.append(psElementPathName);
      sb.append("\">");
      _lstLine.add(sb.toString());

      sb = new StringBuilder();
      sb.append("    <java-class name=\"");
      sb.append(psJavaClassName);
      sb.append("\" />");
      _lstLine.add(sb.toString());

      sb = new StringBuilder();
      sb.append("  </elementBinding>");
      _lstLine.add(sb.toString());

      _lstLine.add("");
   }

   // ------------------------------------------------------------------------------

   /** 
    * <p>This method creates the indent.
    * @param piHierachyLevel current hierarchy level
    * @return indent
    * @author Jochen Pinder 
    */
   private String createIndent(int piHierachyLevel)
   {

      StringBuilder sb = new StringBuilder();

      for (int iIndex = 0; iIndex < piHierachyLevel; iIndex++) {
         sb.append("   ");
      }

      return sb.toString();
   }
}
