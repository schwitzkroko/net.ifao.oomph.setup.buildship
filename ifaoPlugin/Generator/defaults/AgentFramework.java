package net.ifao.arctic.agents._PACKAGE_.framework;


import net.ifao.arctic.agents._PACKAGE_.framework.communication.*;
import net.ifao.arctic.agents._PACKAGE_.framework.elements.*;
import net.ifao.arctic.agents._PACKAGE_.framework.business.*;

import net.ifao.arctic.agents.common.pnr.business.*;
import net.ifao.arctic.agents.common.framework.communication.IDynamicSocketTimeout;
import net.ifao.arctic.agents.common.framework.business.*;

import net.ifao.arctic.xml.request.*;
import net.ifao.arctic.xml.response.*;

import net.ifao.arctic.framework.*;

import net.ifao.arctic.xml.arcticpnrelementinfos.types.*;
import net.ifao.arctic.xml.providerprofiles.*;

import java.util.*;

/**
 * Class _Provider_AgentFramework
 * Base class for all agents that support the r2a framework.
 * <p>
 * Copyright &copy; 2002, i:FAO
 * @author _GENERATOR_
 */
public abstract class _Provider_AgentFramework
    extends Agent
    implements IFrameworkAgent
{

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( F R A M E W O R K )
// ------------------------------------------------------------------------

    private List<_Provider_Communication> _communication = null;

    /**
     * Main method for agent class. This method isn't called by the arctic
     * framework for r2a agents (that implements IFrameworkAgent). But
     * it must be defined because it is abstract.
     *
     * @param pRequest
     * The request which is defined in the 'new arctic' interface for this agent.
     * @param pResponse
     * The response in the 'new arctic' interface for this agent
     *
     * @author _GENERATOR_
     */
    @Override
    public void process(ReqRequest pRequest, ResResponse pResponse) {}


    /**
     * Method createPnrElementFactory
     * this method creates the pnrElement factory that is responsible for this
     * GDS.
     * @return The factory
     * @author _GENERATOR_
     */
    @Override
    public PnrElementFactory2 createPnrElementFactory()
    {

        return new Factory_Provider_();
    }

    /**
     * Method createPnrElementFactory
     * This method returns the name of the pnrElement factory as a string.
     * @return The factory
     * @author _GENERATOR_
     */
    @Override
    public String getFactoryName()
    {

        return "Factory_Provider_";
    }
    
    /**
     * Method createBusinessRulesController
     * this method creates the BusinessRulesController for the Agent.
     * @return The BusinessRulesControllerBase
     * @author _GENERATOR_
     */
    @Override
    public BusinessRulesControllerBase createBusinessRulesController()
    {

        try {
            return new BusinessRulesController_Provider_(getLog(),
                    getCommunication());
        } catch (AgentException ex) {
            return null;
        }

    }

    /**
     * The method getNewCommunication creates a new _Provider_Communication-object.
     * Use this method if you must have more than one communication. If there
     * is only one Communication use getCommunication() instead.
     *
     * @return a new _Provider_Communication-object
     *
     * @throws AgentException
     * @author _GENERATOR_
     */
    protected _Provider_Communication getNewCommunication()
        throws AgentException
    {
        if (_communication == null) {
            _communication = new Vector<_Provider_Communication>();
        }

        _Provider_Communication communication =
            new _Provider_Communication(getConfiguration(),
                                   getLog());

        _communication.add(communication);

        // Create a Communication with the Configuration (from ProviderProfiles)
        return communication;
    }

    /**
     * Method getCommunication returns a _Provider_Communication object, which is
     * unique for this agent.
     *
     * @return a _Provider_Communication-object
     *
     * @throws AgentException
     * @author _GENERATOR_
     */
    protected _Provider_Communication getCommunication()
        throws AgentException
    {
        if (_communication == null) {

            return getNewCommunication();
        }

        return _communication.get(0);
    }

    /**
     * The method getProviderType returns the default ProviderType for all
     * _Provider_ Agents.
     *
     * @return PnrEnumProviderType._PROVIDER_
     * @author _GENERATOR_
     */
    @Override
    public PnrEnumProviderType getProviderType()
    {
        return PnrEnumProviderType._PROVIDER_;
    }

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( A P P L I C A T I O N )
// ------------------------------------------------------------------------

    /**
     * Method onBeforeTransformToRequest
     * is called by the framework just before the factory starts the trans-
     * formation of the request data into the internal pnr element transport
     * structure.
     * @author _GENERATOR_
     */
    @Override
    public void onBeforeTransformToRequest() throws AgentException
    {
        /**
         * @todo EXTENDED _Provider_AgentFramework.onBeforeTransformToRequest()
         * Implement this net.ifao.arctic.framework.IFrameworkAgent method
         *
         * Place code here to be executed before request transformation.
         */
    }

    /**
     * Method onInitialiseProcess
     * is called by the r2a framework just before the process method of the
     * BusinessRulesController is startet. This method is a good place to
     * activate rules.
     * NOTE: Since 20 JAN 2005, rules should be deployed by using the
     * RuleMap.xml file.
     * @param pData The data to process.
     * @param pRulesManager The agent's rule manager.
     * @author _GENERATOR_
     */
    @Override
    public void onInitialiseProcess(PnrTransportContainer pData,
                                    BusinessRulesManager pRulesManager)
    {
        /**
         * @todo EXTENDED _Provider_AgentFramework.onInitialiseProcess()
         * Implement this net.ifao.arctic.framework.IFrameworkAgent method
         *
         * Pleace code here to be executed after request tranformation and
         * before element processing
         */
    }

    /**
     * Method onFinal
     * is called by the framework just before finishing the process. This is
     * a good place to close connections to the provider.
     * @author _GENERATOR_
     */
    @Override
    @SuppressWarnings("cast")
    public void onFinal(PnrTransportContainer pData)
    {
        /**
         * @todo EXTENDED _Provider_AgentFramework.onFinal()
         * Implement this net.ifao.arctic.framework.IFrameworkAgent method
         *
         * Place code here to be executed just before the Agent finishs.
         */

        // Finally close all Communications
        if (_communication != null) {
            for (int i = 0; i < _communication.size(); i++) {
                try {
                	// Set the timeout limit to the initial value
                	// NA#16877 (MA) Return Partial Results in Case of Agent 
                	// TimeOut instead of Returning an Error (r2a)
                	if(_communication.get(i) instanceof IDynamicSocketTimeout) {
                		IDynamicSocketTimeout dst = (IDynamicSocketTimeout)_communication.get(i);
                		dst.setTimeout(dst.getCommunicationTimeout());
                	}
                	
                   _communication.get(i).closeConnection();
                }
                catch (AgentException ex) {}
            }

            _communication = null;
        }
    }

}
