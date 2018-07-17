package net.ifao.arctic.agents._PACKAGE_.framework.business;


import net.ifao.arctic.agents._PACKAGE_.framework.communication.*;

import net.ifao.arctic.io.log.*;
import net.ifao.arctic.agents.common.pnr.business.*;
import net.ifao.arctic.agents.common.framework.business.*;
import net.ifao.arctic.agents.common.pnr.communication.*;


/**
 * Class BusinessRulesController_Provider_
 * This class implements the BusinessRulesController for _Provider_.
 * This code must not be modified in common.
 * <p>
 * Copyright &copy; 2002, i:FAO
 * @author _GENERATOR_
 */
public class BusinessRulesController_Provider_
    extends BusinessRulesControllerBase
{

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( F R A M E W O R K )
// ------------------------------------------------------------------------

    _Provider_Communication _communication;

    /**
     * Constructor BusinessRulesController_Provider_
     *
     * @param pLog the log object
     * @param pCommunication object used to communicate with _Provider_
     * @author _GENERATOR_
     */
    public BusinessRulesController_Provider_(
            IArcticLog pLog, _Provider_Communication pCommunication)
    {
        super(pLog);

        _communication = pCommunication;
    }

    /**
     * Method createGdsRulesController creates the gds rules controller for
     * this provider
     *
     * @param pPnrTransportContainer
     * @return a GdsRulesController_Provider_-object
     * @author _GENERATOR_
     */
    @Override
    protected GdsRulesControllerBase createGdsRulesController(
            PnrTransportContainer pPnrTransportContainer)
    {
        return new GdsRulesController_Provider_(_communication, _log,
                                           pPnrTransportContainer);
    }

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( A P P L I C A T I O N )
// ------------------------------------------------------------------------

}
