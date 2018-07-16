package net.ifao.arctic.agents._PACKAGE_.framework.communication;


import net.ifao.arctic.xml.arcticpnrelementinfos.types.*;
import net.ifao.arctic.framework.*;

import java.text.*;

import java.io.*;

import java.util.*;

import net.ifao.arctic.agents.common.elements.*;
import net.ifao.arctic.agents.common.pnr.elements.*;
import net.ifao.arctic.agents.common.pnr.transform.*;
import net.ifao.arctic.agents.common.pnr.communication.*;
import net.ifao.arctic.agents.common.pnr.utils.*;


/**
 * Class ResponseReader_Provider_
 * This class implements the main controller for reading
 * responses of _Provider_
 * <p>
 * Copyright &copy; 2002, i:FAO
 * @author _GENERATOR_
 */
public class ResponseReader_Provider_
    extends ResponseReader
{

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( F R A M E W O R K )
// ------------------------------------------------------------------------

    /**
     * Method getPnrElementFactory
     * returns the PnrElementFactory neccessary for creating the PnrElements
     *
     * @return PnrElementFactory The current PnrElementFactory
     * @author _GENERATOR_
     */
   @Override
    protected PnrElementFactory2 getPnrElementFactory()
    {

        return ThreadDataManager.getInstance().getFactory();
    }

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( A P P L I C A T I O N )
// ------------------------------------------------------------------------

    /**
     * The method getRequestType defines the return of the proper request type
     * for this response reader.
     *
     * @return The proper RequestType (PnrEnumPnrElementRequestType.HTTPPOST)
     * @author _GENERATOR_
     */
   @Override
    protected PnrEnumPnrElementRequestType getRequestType()
    {
        /**
         * @todo STANDARD ResponseReader_Provider_.getRequestType(): Change requestType if neccesary
         */
        return PnrEnumPnrElementRequestType.HTTPPOST;
    }

    /**
     * Method process
     * defines the main method for the reader to read the native request. Do
     * not call this method direct. Call the start() method which subsequently
     * invokes the process method.
     * @author _GENERATOR_
     *
     * @throws AgentException
     * @throws ParseException
     */
   @Override
    protected void process()
        throws AgentException, ParseException
    {
        Object reply = _protoRequest.getNativeResponse();

        /**
         * @todo STANDARD ResponseReader_Provider_.process(): Dependant of reply, create PNR Elements.
         *
         * The r2a framework calls this method after it receives a response
         * of _Provider_. Place your code for analysing the response,
         * creating the proper pnr elements and causing them to read itself
         * from the response by calling the readPnrElement() method.
         * ResponseReader_Provider_ is also responsible to anchoring relations
         * between pnr elements.
         */
    }
}

