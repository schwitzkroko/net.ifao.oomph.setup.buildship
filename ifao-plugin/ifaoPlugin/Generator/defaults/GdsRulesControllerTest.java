package net.ifao.arctic.agents._PACKAGE_.framework.communication;


import net.ifao.arctic.agents.common.framework.business.PnrTransportContainer;
import net.ifao.arctic.agents.common.pnr.communication.GdsRulesControllerBase;
import net.ifao.arctic.agents.common.pnr.communication
    .GdsRulesControllerTestBase;


/**
 * Class GdsRulesController_Provider_Test
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author _GENERATOR_
 */
public class GdsRulesController_Provider_Test
    extends GdsRulesControllerTestBase
{

    /**
     * Method createGdsRulesController
     *
     * @param pTransport
     *
     * @return
     * @author _GENERATOR_
     */
    @Override
    protected GdsRulesControllerBase createGdsRulesController(
            PnrTransportContainer pTransport)
    {
        return new GdsRulesController_Provider_(null, null, pTransport);
    }

}
