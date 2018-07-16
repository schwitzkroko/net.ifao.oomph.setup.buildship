package net.ifao.arctic.agents._PACKAGE_.framework.elements;


import net.ifao.arctic.agents.common.pnr.communication.*;

import java.text.*;

import net.ifao.arctic.xml.arcticpnrelementinfos.types.*;


/**
 * Class Element_Element_
 * This class implements the element _Element_.
 * Copyright &copy; 2002, i:FAO
 * @author _GENERATOR_
 */
public class Element_Element_
    extends BaseElement_Provider_
{

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( F R A M E W O R K )
// ------------------------------------------------------------------------

    /**
     * Constructor BaseElement_Provider_
     * @author _GENERATOR_
     */
    public Element_Element_() {}

    /**
     * The method getType returns the type of the Pnr-Element this class
     * handles.
     *
     * @return EnumPnrElementType._ELEMENT_
     * @author _GENERATOR_
     */
    @Override
    public PnrEnumPnrElementType getType()
    {
        return PnrEnumPnrElementType._ELEMENT_;
    }

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( A P P L I C A T I O N )
// ------------------------------------------------------------------------

    /**
     * Method setAddToProviderRequest
     * fills the native request object with it's data
     * @param pRequest The native request object.
     * @param pSituation The situation object that provides more detailed
     * information about the context where the current request takes place.
     * @author _GENERATOR_
     *
     * @throws java.text.ParseException
     */
    @Override
    protected void setAddToProviderRequest(Object pRequest,
                                           RequestSituation pSituation)
        throws java.text.ParseException
    {

        /**
         * @todo STANDARD Element_Element_.setAddToProviderRequest(): Add your code to fill the pRequest-Object with the request data.
         *
         * This method is called by the framework to cause this element
         * to add it's content to the native request. Place your code
         * for adding this element to the request, here.
         */
    }

    /**
     * Method getFromProviderResponse
     * reads it's data from the response element.
     * @param pNativeResponseElement The response element.
     * @param pSituation The situation object that provides more detailed
     * information about the context where the current request takes place.
     * @author _GENERATOR_
     *
     * @throws java.text.ParseException
     */
    @Override
    protected void getFromProviderResponse(Object pNativeResponseElement,
                                           RequestSituation pSituation)
        throws java.text.ParseException
    {

        /**
         * @todo STANDARD Element_Element_.getFromProviderResponse(): Add your code to populate this-data with the data of the pNativeResponseElement object
         *
         * This method is called by the ResponseReader to cause this
         * element to read it from the native response. Place your code
         * for reading out this element, here.
         */
    }
}
