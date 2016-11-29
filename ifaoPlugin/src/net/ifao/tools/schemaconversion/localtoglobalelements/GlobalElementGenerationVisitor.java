package net.ifao.tools.schemaconversion.localtoglobalelements;


import java.util.ArrayList;
import java.util.HashSet;

import static net.ifao.tools.schemaconversion.localtoglobalelements.Util.traverseElementTree;


/**
 * <p>This class contains the global element schema generation visitor.
 * <p>&copy; 2010 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public class GlobalElementGenerationVisitor
   extends GenerationVisitorBase
   implements IElementVisitor
{

   /**
    * <p>This class contains the local sub-element schema generation visitor.
    * @author Jochen Pinder
    */
   static class LocalSubElementGenerationVisitor
      extends LocalElementGenerationVisitor
   {
      /**
       * <p>Constructor.
       * @param lstLine generated data
       * @param schemaHandler schema handler
       */
      public LocalSubElementGenerationVisitor(ArrayList<String> lstLine, SchemaHandler schemaHandler)
      {
         super();

         _lstLine = lstLine;
         _schemaHandler = schemaHandler;
      }
   }


   // simple and complex schema element list
   private ArrayList<TElement> _lstSimpleElement;
   private ArrayList<TElement> _lstComplexElement;

   // element name hash set
   private HashSet<String> _hsetElementName;


   //------------------ Methods ---------------------------------------------------
   //------------------------------------------------------------------------------

   /**
    * <p>Constructor.
    */
   public GlobalElementGenerationVisitor()
   {
      super();

      _lstSimpleElement = new ArrayList<TElement>();
      _lstComplexElement = new ArrayList<TElement>();

      _hsetElementName = new HashSet<String>();
   }

   //------------------------------------------------------------------------------

   /**
     * <p>This method commences the global element schema generation visitor.
     * @author Jochen Pinder
     */
   public void commence()
   {

      _schemaHandler.createSchemaHeader();
   }

   //------------------------------------------------------------------------------

   /**
     * <p>This method terminates the global element schema generation visitor.
     * @author Jochen Pinder
     */
   public void terminate()
   {

      // Create the simple schema elements
      _schemaHandler.createSchemaComment("Simple Elements");
      for (TElement tElement : _lstSimpleElement) {
         _schemaHandler.createSchemaSimpleTypeGlobalElement(tElement);
      }

      // Create the complex schema elements
      _schemaHandler.createSchemaComment("Complex Elements");
      for (TElement tElement : _lstComplexElement) {
         _schemaHandler.createSchemaComplexTypeGlobalElementStart(tElement);

         for (TElement tSubElement : tElement.getChildElementList()) {
            if (tSubElement.isDoubleDifferentElement()) {
               LocalSubElementGenerationVisitor localSubElementGenerationVisitor =
                  new LocalSubElementGenerationVisitor(_lstLine, _schemaHandler);
               traverseElementTree(tSubElement, localSubElementGenerationVisitor);
            } else {
               _schemaHandler.createSchemaComplexTypeGlobalSubElement(tSubElement);
            }
         }

         _schemaHandler.createSchemaComplexTypeGlobalElementEnd();
      }

      _schemaHandler.createSchemaFooter();
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the simple element count.
    * @return simple element count
    * @author Jochen Pinder
    */
   public int getSimpleElementCount()
   {

      return _lstSimpleElement.size();
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the complex element count.
    * @return complex element count
    * @author Jochen Pinder
    */
   public int getComplexElementCount()
   {

      return _lstComplexElement.size();
   }

   //------------------------------------------------------------------------------

   /**
    * <p>This method performs the visit before visiting the child schema elements.
    * @param ptElement schema element
    * @author Jochen Pinder
    */
   @Override
   public void visitBeforeChildren(TElement ptElement)
   {

      // Check if the schema element is a double different element or has already been considered
      if (ptElement.isDoubleDifferentElement() || !_hsetElementName.add(ptElement.getName()))
         return;

      // Add the schema element to the simple or to the complex schema element list
      if (ptElement.isComplexElement()) {
         _lstComplexElement.add(ptElement);
      } else {
         _lstSimpleElement.add(ptElement);
      }

   } // end visitBeforeChilds

   //------------------------------------------------------------------------------

   /**
    * <p>This method determines if the child schema elements are visited.
    * @param ptElement schema element
    * @return true if the child schema elements are visited, false otherwise
    * @author Jochen Pinder
    */
   @Override
   public boolean visitChildren(TElement ptElement)
   {

      return (!ptElement.isDoubleDifferentElement());
   }

   //------------------------------------------------------------------------------

   /**
    * <p>This method performs the visit after visiting the child schema elements.
    * @param ptElement schema element
    * @author Jochen Pinder
    */
   @Override
   public void visitAfterChildren(TElement ptElement)
   {}
}
