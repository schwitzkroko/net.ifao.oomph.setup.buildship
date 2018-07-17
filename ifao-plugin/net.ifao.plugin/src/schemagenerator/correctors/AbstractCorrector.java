package schemagenerator.correctors;

import net.ifao.xml.XmlObject;

/** 
 * Base class for schema corrector classes. 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author wunder 
 */
public abstract class AbstractCorrector implements ICorrector {

	/** 
	 * Iterates recursively through the xml and calls the abstract
	 * correctXmlObject method to allow subclasses applying their corrections.  
	 * 
	 * @param pXml The currently visited xml object.
	 * 
	 * @author wunder 
	 */
	public final void correct(XmlObject pXml) {
		String[] names = pXml.getObjectNames();
		for (int i = 0; i < names.length; i++) {
			XmlObject[] xmlObjects = pXml.getObjects(names[i]);
			for (int j = 0; j < xmlObjects.length; j++) {
				correct(xmlObjects[j]);
				correctXmlObject(xmlObjects[j]);
			}
		}
	}

	/**
	 * Overwrite this method and apply corrections if applicable.
	 * @param pXml The xml object to corret.
	 */
	protected abstract void correctXmlObject(XmlObject pXml);

}
