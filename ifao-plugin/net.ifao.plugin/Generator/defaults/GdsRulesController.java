package net.ifao.arctic.agents._PACKAGE_.framework.communication;

import net.ifao.arctic.xml.providerprofiles.Pro_Provider_Profile;
import net.ifao.arctic.agents.common.framework.business.PnrTransportContainer;

import net.ifao.arctic.agents.common.pnr.communication.*;
import net.ifao.arctic.agents.common.pnr.transform.*;
import net.ifao.arctic.agents.common.pnr.utils.*;
import net.ifao.arctic.agents.common.pnr.elements.*;

import net.ifao.arctic.xml.arcticpnrelementinfos.types.*;
import net.ifao.arctic.io.log.*;

import net.ifao.arctic.framework.*;

import net.ifao.arctic.xml.response.*;


/**
 * Class GdsRulesController_Provider_
 * <p>Description: It's purpose is to handle _Provider_ specific rules.
 * It controls the communication via the GdsAdapter_Provider_.</p>
 * <p>
 * Copyright &copy; 2002, i:FAO
 * @author _GENERATOR_
 */
public class GdsRulesController_Provider_
    extends GdsRulesControllerBase
{
// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( F R A M E W O R K )
// ------------------------------------------------------------------------

    /**
     * This is the default Constructor for GdsRulesController_Provider_
     *
     * @param pCommunication The _Provider_Communication
     * @param pLog The Log object
     * @author _GENERATOR_
     */
    public GdsRulesController_Provider_(
            _Provider_Communication pCommunication, IArcticLog pLog,
            PnrTransportContainer pPnrTransportContainer)
    {
        super(pPnrTransportContainer);

        _log = pLog;

        _gdsAdapter =
            new GdsAdapter_Provider_(pLog, pCommunication,
                                _pnrTransportContainer
                                    .getRequestData().getAdditionalPnrInfo()
                                        .getConfiguration());
    }

    /**
     * Method process
     * This is the main method where communication flow control takes
     * place. Commonly, you should delegate action specific flow into
     * own methods.
     * @author _GENERATOR_
     *
     * @throws AgentException
     */
    @Override
    public void process()
        throws AgentException
    {
        runProcess();
    }

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( A P P L I C A T I O N )
// ------------------------------------------------------------------------

}

