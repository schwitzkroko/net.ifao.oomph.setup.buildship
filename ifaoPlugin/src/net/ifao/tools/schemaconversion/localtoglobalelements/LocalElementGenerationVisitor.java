package net.ifao.tools.schemaconversion.localtoglobalelements;


/**
 * <p>This class contains the local element schema generation visitor.
 * <p>&copy; 2010 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public class LocalElementGenerationVisitor
   extends GenerationVisitorBase
   implements IElementVisitor
{


   //------------------ Methods ---------------------------------------------------
   //------------------------------------------------------------------------------

   /**
    * <p>Constructor.
    */
   public LocalElementGenerationVisitor()
   {
      super();
   }

   //------------------------------------------------------------------------------

   /**
     * <p>This method commences the local element schema generation visitor.
     * @author Jochen Pinder
     */
   public void commence()
   {

      _schemaHandler.createSchemaHeader();
   }

   //------------------------------------------------------------------------------

   /**
     * <p>This method terminates the local element schema generation visitor.
     * @author Jochen Pinder
     */
   public void terminate()
   {

      _schemaHandler.createSchemaFooter();
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

      if (ptElement.isRootElement()) {
         _schemaHandler.createSchemaRootComplexTypeLocalElementStart(ptElement, _iHierachyLevel);
      } else if (ptElement.isComplexElement()) {
         _schemaHandler.createSchemaComplexTypeLocalElementStart(ptElement, _iHierachyLevel);
      } else {
         _schemaHandler.createSchemaSimpleTypeLocalElement(ptElement, _iHierachyLevel);
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

      return true;
   }

   //------------------------------------------------------------------------------

   /**
    * <p>This method performs the visit after visiting the child schema elements.
    * @param ptElement schema element
    * @author Jochen Pinder
    */
   @Override
   public void visitAfterChildren(TElement ptElement)
   {

      if (ptElement.isComplexElement())
         _schemaHandler.createSchemaComplexTypeLocalElementEnd(_iHierachyLevel);
   }
}
