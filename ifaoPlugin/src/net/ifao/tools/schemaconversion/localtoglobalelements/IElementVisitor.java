package net.ifao.tools.schemaconversion.localtoglobalelements;


/**
 * <p>This is the interface for a schema element visitor.
 * <p>&copy; 2008 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public interface IElementVisitor
{

   //------------------ Methods ---------------------------------------------------
   //------------------------------------------------------------------------------

   /**
     * <p>This method initialises the visit.
     * @param ptElement schema element
     * @author Jochen Pinder
     */
   public void init(TElement ptElement);

   //------------------------------------------------------------------------------  

   /**
     * <p>This method performs the visit before visiting the child schema elements.
     * @param ptElement schema element
     * @author Jochen Pinder
     */
   public void visitBeforeChildren(TElement ptElement);

   //------------------------------------------------------------------------------  

   /**
     * <p>This method determines if the child schema elements are visited.
     * @param ptElement schema element
     * @return true if the child schema elements are visited, false otherwise
     * @author Jochen Pinder
     */
   public boolean visitChildren(TElement ptElement);

   //------------------------------------------------------------------------------  

   /**
     * <p>This method performs the visit after visiting the child schema elements.
     * @param ptElement schema element
     * @author Jochen Pinder
     */
   public void visitAfterChildren(TElement ptElement);

   //------------------------------------------------------------------------------

   /**
     * <p>This method finishes the visit.
     * @param ptElement schema element
     * @author Jochen Pinder
     */
   public void finish(TElement ptElement);

}
