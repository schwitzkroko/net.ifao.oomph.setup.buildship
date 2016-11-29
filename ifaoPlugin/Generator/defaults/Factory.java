package net.ifao.arctic.agents._PACKAGE_.framework.elements;


import net.ifao.arctic.agents._PACKAGE_.framework.transform.*;

import net.ifao.arctic.agents.common.pnr.business.*;
import net.ifao.arctic.agents.common.pnr.elements.*;
import net.ifao.arctic.agents.common.pnr.transform.*;
import net.ifao.arctic.agents.common.elements.*;

import net.ifao.arctic.framework.*;

import net.ifao.arctic.io.log.*;
import net.ifao.arctic.xml.arcticpnrelementinfos.types.*;

import net.ifao.arctic.xml.request.*;
import net.ifao.arctic.xml.response.*;


/**
 * Factory_Provider_ implements the ElementFactory for _Provider_.
 * All elements should be created via the factory! It is not necessary
 * to modify this code in common.
 * <p>
 * See PnrElementFactory2 for detailed information how a PnrElementFactory
 * works.
 * @author _GENERATOR_
 * @version 1.0
 */

public class Factory_Provider_
    extends PnrElementFactory2
{

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( F R A M E W O R K )
// ------------------------------------------------------------------------

    /**
     * Constructor PnrElementFactory_Provider_
     * @author _GENERATOR_
     */
    public Factory_Provider_() {}

    /**
     * Method getProviderType returns _PROVIDER_
     *
     * @return The related PnrEnumProviderType._PROVIDER_
     * @author _GENERATOR_
     */
    @Override
    protected PnrEnumProviderType getProviderType()
    {
        return PnrEnumProviderType._PROVIDER_;
    }


    /**
     * Method getTransformerHandler
     *
     *
     * @param transformActionType
     * @param pLog
     * @return
     * @author _GENERATOR_
     */
    @Override
    protected TransformerHandler getTransformerHandler(
            EnumTransformActionType transformActionType, IArcticLog pLog)
    {

        // ################################## GENERATOR.BEGIN ##########
        // Do not edit this lines if you want your changes to be preserved.
        // The following lines will be overwritten by the DTDInfo-Generator
        //
        // [START:_METHOD_]
        if (transformActionType.equals(EnumTransformActionType._METHOD_)) {

            return new TransformerHandler_Method_(pLog);
        }
        // [END:_METHOD_]
        //
        // Do not edit the lines above.
        // ################################## GENERATOR.END ############

        return super.getTransformerHandler(transformActionType, pLog);
    }

}
