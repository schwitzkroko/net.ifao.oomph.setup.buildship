package net.ifao.tools.schemaconversion.localtoglobalelements;


import java.util.ArrayList;


/**
 * <p>This is the base class for the schema generation visitor classes.
 * <p>&copy; 2008 i:FAO Aktiengesellschaft
 * @author Jochen Pinder
 */
public abstract class GenerationVisitorBase
{

   // generated data
   protected ArrayList<String> _lstLine;

   // current hierarchy level
   protected int _iHierachyLevel;

   // schema handler
   protected SchemaHandler _schemaHandler;


   //------------------ Methods ---------------------------------------------------
   //------------------------------------------------------------------------------

   /**
    * <p>Constructor.
    */
   protected GenerationVisitorBase()
   {
      _lstLine = new ArrayList<String>();

      _iHierachyLevel = 0;

      _schemaHandler = new SchemaHandler(_lstLine);
   }

   // ------------------------------------------------------------------------------

   /**
    * <p>This method gets the generated data.
    * @return generated data
    * @author Jochen Pinder
    */
   public ArrayList<String> getGeneratedData()
   {

      return _lstLine;
   }

   //------------------------------------------------------------------------------

   /**
     * <p>This method initialises the visit.
     * @param ptElement schema element
     * @author Jochen Pinder
     */
   public void init(@SuppressWarnings("unused")
   TElement ptElement)
   {

      _iHierachyLevel++;
   }

   //------------------------------------------------------------------------------

   /**
     * <p>This method finishes the visit.
     * @param ptElement schema element
     * @author Jochen Pinder
     */
   public void finish(@SuppressWarnings("unused")
   TElement ptElement)
   {

      _iHierachyLevel--;
   }
}
