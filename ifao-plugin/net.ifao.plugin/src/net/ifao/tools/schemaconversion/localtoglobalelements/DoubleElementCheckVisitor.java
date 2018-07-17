package net.ifao.tools.schemaconversion.localtoglobalelements;


import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;


/**
 * <p>This class contains the double schema element check visitor.
 * <p>&copy; 2010 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public class DoubleElementCheckVisitor
   implements IElementVisitor
{

   // double schema element name hash set
   //  - identical: elements that appear more than one time, but all of them have the same definition
   //  - different: elements that appear more than one time, but some or all of them have different definitions
   private HashSet<String> _hsetIdenticalElementName;
   private HashSet<String> _hsetDifferentElementName;

   // overall schema element hash map, which maps element names to elements
   private HashMap<String, TElement> _hmapNameToElement;


   //------------------ Methods ---------------------------------------------------
   //------------------------------------------------------------------------------

   /**
    * <p>Constructor.
    */
   public DoubleElementCheckVisitor()
   {
      _hsetIdenticalElementName = new HashSet<String>();
      _hsetDifferentElementName = new HashSet<String>();

      _hmapNameToElement = new HashMap<String, TElement>();
   }

   //------------------------------------------------------------------------------

   /**
     * <p>This method terminates the double schema element check visitor.
     * @author Jochen Pinder
     */
   public void terminate()
   {

      // Remove all double identical schema element names that are also double different schema element names
      Iterator<String> it = _hsetIdenticalElementName.iterator();
      while (it.hasNext()) {
         if (_hsetDifferentElementName.contains(it.next()))
            it.remove();
      }
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the double identical schema element name hash set.
    * @return double identical schema element name hash set
    * @author Jochen Pinder
    */
   public HashSet<String> getIdenticalElementNameSet()
   {

      return _hsetIdenticalElementName;
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the double different schema element name hash set.
    * @return double different schema element name hash set
    * @author Jochen Pinder
    */
   public HashSet<String> getDifferentElementNameSet()
   {

      return _hsetDifferentElementName;
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

      // Get the schema element name
      String sElementName = ptElement.getName();

      // Check if the schema element already exists
      if (_hmapNameToElement.containsKey(sElementName)) {
         TElement tAlreadyExistingElement = _hmapNameToElement.get(sElementName);

         if (ptElement.equals(tAlreadyExistingElement)) {
            _hsetIdenticalElementName.add(sElementName);
         } else {
            _hsetDifferentElementName.add(sElementName);
         }
      } else {
         _hmapNameToElement.put(sElementName, ptElement);
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
