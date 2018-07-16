package net.ifao.tools.schemaconversion.localtoglobalelements;


/**
 * <p>This class contains the statistic schema element visitor.
 * <p>&copy; 2010 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public class StatisticElementVisitor
   implements IElementVisitor
{

   // simple and complex element counts
   private int _bSimpleElementCount;
   private int _bComplexElementCount;


   //------------------ Methods ---------------------------------------------------
   //------------------------------------------------------------------------------

   /**
    * <p>Constructor.
    */
   public StatisticElementVisitor()
   {
      _bSimpleElementCount = 0;
      _bComplexElementCount = 0;
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the simple element count.
    * @return simple element count
    * @author Jochen Pinder
    */
   public int getSimpleElementCount()
   {

      return _bSimpleElementCount;
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the complex element count.
    * @return complex element count
    * @author Jochen Pinder
    */
   public int getComplexElementCount()
   {

      return _bComplexElementCount;
   }

   //------------------------------------------------------------------------------

   /**
    * <p>This method initialises the visit.
    * @param ptElement schema element
    * @author Jochen Pinder
    */
   @Override
   public void init(TElement ptElement)
   {}

   //------------------------------------------------------------------------------

   /**
    * <p>This method performs the visit before visiting the child schema elements.
    * @param ptElement schema element
    * @author Jochen Pinder
    */
   @Override
   public void visitBeforeChildren(TElement ptElement)
   {

      // Increment the simple or the complex element count
      if (ptElement.isComplexElement()) {
         _bComplexElementCount++;
      } else {
         _bSimpleElementCount++;
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
   {}

   //------------------------------------------------------------------------------

   /**
     * <p>This method finishes the visit.
     * @param ptElement schema element
     * @author Jochen Pinder
     */
   @Override
   public void finish(TElement ptElement)
   {}
}
