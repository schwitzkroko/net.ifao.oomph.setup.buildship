package net.ifao.arctic.agents._PACKAGE_.framework.transform;


import net.ifao.arctic.agents.common.pnr.transform.*;
import net.ifao.arctic.agents.common.pnr.business.*;
import net.ifao.arctic.agents.common.framework.business.*;
import net.ifao.arctic.agents.common.pnr.elements.*;
import net.ifao.arctic.agents._PACKAGE_.framework.elements.*;
import net.ifao.arctic.agents.common.elements.*;
import net.ifao.arctic.framework.*;
import net.ifao.arctic.io.log.*;
import net.ifao.arctic.xml.arcticpnrelementinfos.types.*;
import net.ifao.arctic.xml.response.*;
import net.ifao.arctic.xml.request.*;
import java.util.*;
import java.util.Map.*;

/**
 * Class TransformerHandler_Method_ which is automatically generated
 *
 * <p>
 * Copyright &copy; 2002, i:FAO
 * @author _GENERATOR_
 */
public class TransformerHandler_Method_
    extends TransformerHandlerArcticBase
{
// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( F R A M E W O R K )
// ------------------------------------------------------------------------

    /**
     * Constructor TransformerHandler_Method_
     *
     * @param pLog The Log-object
     * @author _GENERATOR_
     */
    public TransformerHandler_Method_(IArcticLog pLog)
    {
        super(pLog);
    }


// --- THE FOLLOWING LINES WILL BE AUTOMATICALLY GENERATED !!! ----------------


    /**
     * Method setResponse
     *
     * @param pResponse
     * @param pPTD
     * @param pTraveller
     *
     * @return
     * @author _GENERATOR_
     */
    @Override
    protected boolean setResponse(ResResponse pResponse, PnrTransportData pPTD,
                                  Traveller pTraveller)
    {
        return false;
    }

    /**
     * Method getBase
     *
     * @param pRequest
     * @param pPTD
     * @param pTraveller
     *
     * @return
     * @author _GENERATOR_
     */
    @Override
    protected PnrElementBase getBase(ReqRequest pRequest, PnrTransportData pPTD,
                                     Traveller pTraveller)
    {
        return null;
    }

}
