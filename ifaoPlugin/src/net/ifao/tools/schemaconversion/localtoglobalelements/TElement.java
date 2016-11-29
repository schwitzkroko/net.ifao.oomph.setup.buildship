package net.ifao.tools.schemaconversion.localtoglobalelements;


import java.util.ArrayList;


/**
 * <p>This transport class contains one schema element.
 * <p>&copy; 2010 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public class TElement
{

   // name
   private String _sName;

   // minimum and maximum occurrence
   private int _iMinOccurs;
   private int _iMaxOccurs;

   // simple type, e.g. "xs:string".
   // Is NULL for a complex type.
   private String _sSimpleType;

   // double different element flag
   //  - true:  belongs to the double different schema elements that appear more than one time,
   //           but some or all of them have different definitions
   //  - false: otherwise
   private boolean _bIsDoubleDifferentElement;

   // hierarchy
   //  - schema parent element
   //  - schema child element list
   private TElement _parentElement;
   private ArrayList<TElement> _lstChildElement;


   // ------------------ Methods ---------------------------------------------------
   // ------------------------------------------------------------------------------

   /**
    * <p>This method initialises a simple type schema element.
    * @param sName name
    * @param iMinOccurs minimum occurrence
    * @param iMaxOccurs maximum occurrence
    * @param sSimpleType type
    * @author Jochen Pinder
    */
   public TElement(String sName, int iMinOccurs, int iMaxOccurs, String sSimpleType)
   {
      _sName = sName;

      _iMinOccurs = iMinOccurs;
      _iMaxOccurs = iMaxOccurs;

      _sSimpleType = sSimpleType;

      _bIsDoubleDifferentElement = false;

      _parentElement = null;
      _lstChildElement = new ArrayList<TElement>();
   }

   /**
    * <p>This method initialises a complex type non-root schema element.
    * @param sName name
    * @param iMinOccurs minimum occurrence
    * @param iMaxOccurs maximum occurrence
    * @author Jochen Pinder
    */
   public TElement(String sName, int iMinOccurs, int iMaxOccurs)
   {
      this(sName, iMinOccurs, iMaxOccurs, null);
   }

   /**
    * <p>This method initialises the complex type root schema element.
    * @param sName name
    * @author Jochen Pinder
   */
   public TElement(String sName)
   {
      this(sName, 1, 1, null);
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the name.
    * @return name
    * @author Jochen Pinder
    */
   public String getName()
   {

      return _sName;
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the minimum occurrence.
    * @return minimum occurrence
    * @author Jochen Pinder
    */
   public int getMininumOccurrence()
   {

      return _iMinOccurs;
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the maximum occurrence.
    * @return maximum occurrence
    * @author Jochen Pinder
    */
   public int getMaximumOccurrence()
   {

      return _iMaxOccurs;
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the simple type.
    * @return simple type
    * @author Jochen Pinder
    */
   public String getSimpleType()
   {

      return _sSimpleType;
   }

   /**
    * <p>This method indicates if the schema element is a complex type element or a simple type element.
    * @return true for a complex type element, false for a simple type element
    * @author Jochen Pinder
    */
   public boolean isComplexElement()
   {

      return (_sSimpleType == null);
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method sets the double different element flag.
    * @author Jochen Pinder
    */
   public void setDoubleDifferentElementFlag()
   {

      _bIsDoubleDifferentElement = true;
   }

   /**
    * <p>This method gets the double different element flag.
    * @return double different element flag
    * @author Jochen Pinder
    */
   public boolean isDoubleDifferentElement()
   {

      return _bIsDoubleDifferentElement;
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method sets the schema parent element.
    * @param pparentElement schema parent element
    * @author Jochen Pinder
    */
   public void setParentElement(TElement pparentElement)
   {

      _parentElement = pparentElement;
   }

   /**
    * <p>This method gets the schema parent element.
    * @return schema parent element
    * @author Jochen Pinder
    */
   public TElement getParentElement()
   {

      return _parentElement;
   }

   /**
    * <p>This method indicates if the schema element is the root element.
    * @return true if the schema element is the root element, false otherwise
    * @author Jochen Pinder
    */
   public boolean isRootElement()
   {

      return (_parentElement == null);
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method adds a schema child element.
    * @param pchildElement schema child element
    * @author Jochen Pinder
    */
   public void addChildElement(TElement pchildElement)
   {

      _lstChildElement.add(pchildElement);
   }

   /**
    * <p>This method gets the schema child element list.
    * @return schema child element list
    * @author Jochen Pinder
    */
   public ArrayList<TElement> getChildElementList()
   {

      return _lstChildElement;
   }

   //------------------------------------------------------------------------------

   /**
    * <p>This method checks if this schema element equals to a second schema element.
    * <p>Both schema elements are equal if they
    * <ul>
    *   <li>have the same name
    *   <li>are both non-root elements
    *   <li>are both complex type elements with equal child elements or are both simple type
    *       elements with the same type
    * </ul>
    * @param pobj second schema element
    * @return true if this schema element equals to the second schema element, otherwise false.
    * @author Jochen Pinder
    */
   @Override
   public boolean equals(Object pobj)
   {
      if (pobj == null) {
         return false;
      }
      // Get the second schema element
      TElement tSecondElement = (TElement) pobj;

      // Compare the name
      if (!getName().equals(tSecondElement.getName())) {
         return false;
      }

      // Check for non-root elements
      if (isRootElement() || tSecondElement.isRootElement()) {
         return false;
      }

      // Both elements are complex type elements:
      if (isComplexElement() && tSecondElement.isComplexElement()) {

         // Compare the child elements
         if (!_lstChildElement.equals(tSecondElement.getChildElementList())) {
            return false;
         }
      }

      // One element is a simple type element, the other one is a complex type element:
      else if (isComplexElement() || tSecondElement.isComplexElement()) {
         return false;
      }

      // Both elements are simple type elements:
      else {

         // Compare the type
         if (!getSimpleType().equals(tSecondElement.getSimpleType())) {
            return false;
         }
      }

      // From here: Both schema element are equal
      return true;
   }

   //------------------------------------------------------------------------------

   /**
    * <p>This method calculates the hash code of the schema element.
    * @return hash code
    * @author Jochen Pinder
    */
   @Override
   public int hashCode()
   {

      return (_sName.charAt(0));
   }
}
