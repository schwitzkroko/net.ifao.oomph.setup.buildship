package net.ifao.tools.schemaconversion.localtoglobalelements;


import java.util.HashSet;


/**
 * <p>This class contains the double schema element mark visitor.
 * <p>&copy; 2010 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public class DoubleElementMarkVisitor
   implements IElementVisitor
{

   // double different schema element name hash set,
   // which contains the elements that appear more than one time, but some or all of them have different definitions
   private HashSet<String> _hsetDifferentElementName;


   //------------------ Methods ---------------------------------------------------
   //------------------------------------------------------------------------------

   /**
    * <p>Constructor.
    * @param hsetDifferentElementName double different schema element name hash set
    */
   public DoubleElementMarkVisitor(HashSet<String> hsetDifferentElementName)
   {
      _hsetDifferentElementName = hsetDifferentElementName;
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

      // Check if the schema element is a double different element and mark it accordingly
      if (_hsetDifferentElementName.contains(ptElement.getName()))
         ptElement.setDoubleDifferentElementFlag();

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
