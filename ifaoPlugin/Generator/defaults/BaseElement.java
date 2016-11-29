package net.ifao.arctic.agents._PACKAGE_.framework.elements;


import net.ifao.arctic.agents.common.pnr.elements.*;
import net.ifao.arctic.agents.common.pnr.communication.*;

import net.ifao.arctic.xml.arcticpnrelementinfos.types.*;

import java.text.*;


/**
 * The class BaseElement_Provider_ is the BaseClass of which all
 * Elements for _Provider_ have to inherit from.
 *
 * <p>
 * Copyright &copy; 2002, i:FAO
 * @author _GENERATOR_
 */
public abstract class BaseElement_Provider_
    extends PnrElementBase
{

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( F R A M E W O R K )
// ------------------------------------------------------------------------

    /**
     * Constructor BaseElement_Provider_
     * @author _GENERATOR_
     */
    public BaseElement_Provider_() {}

    /**
     * Method setAddToProtoRequest
     * defines the method to add this pnr element to the ProtoRequest in order
     * to add the element to the pnr
     * @param pRequest The proto request object.
     * @author _GENERATOR_
     *
     * @throws ParseException
     */
    @Override
    public void setAddToProtoRequest(ProtoRequestBase pRequest)
        throws ParseException
    {
        setAddToProviderRequest(pRequest.getNativeRequest(),
                                pRequest.getRequestStatus()
                                    .getRequestSituation());
    }

    /**
     * Method setRemoveToProtoRequest
     * defines the method to add this pnr element to the ProtoRequest in order
     * to remove the element from the pnr.
     * @param pRequest The proto request object.
     *
     * @throws ParseException
     * @author _GENERATOR_
     */
    @Override
    public void setRemoveToProtoRequest(ProtoRequestBase pRequest)
        throws ParseException
    {
        setRemoveToProviderRequest(pRequest.getNativeRequest(),
                                   pRequest.getRequestStatus()
                                       .getRequestSituation());
    }

    /**
     * Method getFromNativeResponse
     * fills it's data members from the data of the native response element.
     * @param pRequestType The type of the request from which the response
     * results.
     * @param pNativeResponseElement The element of the native response that
     * stores the data for this pnr element.
     * @param pSituation the situation on which the response is read out.
     * @author _GENERATOR_
     * @throws ParseException
     */
    @Override
    public void getFromNativeResponse(
            PnrEnumPnrElementRequestType pRequestType,
            Object pNativeResponseElement, RequestSituation pSituation)
        throws ParseException
    {
        getFromProviderResponse(pNativeResponseElement, pSituation);
    }

// ------------------------------------------------------------------------
//   A B S T R A C T    M E T H O D S
// ------------------------------------------------------------------------

    /**
     * Method setAddToProviderRequest
     * fills the native request object with it's data in order to
     * add this element to a PNR.
     * @param pRequest The native request object.
     * @param pSituation The situation object that provides more detaild
     * information about the context where the current request takes place.
     * @author _GENERATOR_
     * @throws java.text.ParseException
     */
    protected abstract void setAddToProviderRequest(Object pRequest,
            RequestSituation pSituation)
        throws java.text.ParseException;

    /**
     * Method getFromProviderResponse
     * reads it's data from the response element.
     * @param pResponse The response element.
     * @param pSituation the situation on which the response is read out.
     * @throws java.text.ParseException
     * @author _GENERATOR_
     */
    protected abstract void getFromProviderResponse(Object pResponse,
            RequestSituation pSituation)
        throws java.text.ParseException;

// ------------------------------------------------------------------------
//   O V E R W R I T A B L E S
// ------------------------------------------------------------------------

    /**
     * Method setRemoveToProviderRequest
     * fills the native request object with it's data in order to
     * remove this element from a PNR.
     * @param pRequest The native request object.
     * @param pSituation The situation object that provides more detaild
     * information about the context where the current request takes place.
     * @author _GENERATOR_
     * @throws java.text.ParseException
     */
    protected void setRemoveToProviderRequest(Object pRequest,
                                              RequestSituation pSituation)
        throws java.text.ParseException
    {

        /**
         * @todo EXTENDED BaseElement_Provider_.setRemoveToProviderRequest(): Overwrite this method in your specific Element class.
         *
         * This method is called by the r2a framework to cause this
         * Element to fill the request in order to remove itself from an
         * existing PNR.
         */
    }

    /**
     * Method initialise
     * @author _GENERATOR_
     */
    @Override
    protected void initialise()
    {
        /**
         * @todo EXTENDED BaseElement_Provider_.initialise(): Place your specific initialise-code for the _Provider_ Elements here.
         *
         * This method is called by the r2a framework just before finishing
         * the construction of this object. Place code here for additional
         */
    }
}
