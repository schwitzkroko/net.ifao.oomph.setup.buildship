package schemagenerator.correctors;


import net.ifao.xml.*;


/**
 * Class ICorrector
 *
 * <p>
 * Copyright &copy; 2011, i:FAO Group GmbH
 * @author kaufmann
 */
public interface ICorrector
{

   
   /**
    * Use this method to apply corrections to schemas
    *
    * @param pSchema the schema
    *
    * @author kaufmann
    */
   public void correct(XmlObject pSchema);

   /**
    * Overwrite this method and return a description what and why this correction class does
    * @return The correction description.
    */
   public String getCorrectionSummary();

}
