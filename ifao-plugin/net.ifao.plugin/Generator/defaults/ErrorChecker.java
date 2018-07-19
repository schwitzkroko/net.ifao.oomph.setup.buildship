package net.ifao.arctic.agents._PACKAGE_.framework.communication;


import net.ifao.arctic.agents.common.pnr.communication.ErrorCheckerBase;
import net.ifao.arctic.agents.common.pnr.communication.ProtoRequestBase;
import net.ifao.arctic.agents.common.pnr.communication.ErrorInformation;
import net.ifao.arctic.io.log.IArcticLog;


/**
 * Class ErrorChecker_Provider_
 * This class implements the ErrorChecker for _Provider_.
 * <p>
 * Copyright &copy; 2002, i:FAO
 * @author _GENERATOR_
 */
public class ErrorChecker_Provider_
    extends ErrorCheckerBase
{

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( A P P L I C A T I O N )
// ------------------------------------------------------------------------

    /**
     * Constructor ErrorChecker_Provider_
     *
     * @param pLog The Log object
     * @author _GENERATOR_
     */
    public ErrorChecker_Provider_(IArcticLog pLog)
    {
        _log = pLog;
        _errorMapper = new ErrorMapper_Provider_();
    }

    /**
     * Method getErrorInformation
     * This method has to be implemented to search for error information within
     * the native response.
     * @param pProtoRequest The protorequest containing the native response.
     *
     * @return An ErrorInformation object if errors were detected. null if
     * no error was found.
     * @author _GENERATOR_
     */
    @Override
    protected ErrorInformation getErrorInformation(
            ProtoRequestBase pProtoRequest)
    {
        /**
         * @todo STANDARD ErrorChecker_Provider_.getErrorInformation(): Search for errors in respObject.
         *
         * This method is called by the framework after it receives the
         * response of the provider. Place code to search for error messages, here.
         * And return an ErrorInformation if you found an error or null
         * if there is no error. You can get an ErrorInformation object
         * by using the createErrorInformation method.
         */

        ErrorInformation errorInformation = null;
        Object respObject = pProtoRequest.getNativeResponse();

		// Example:
        //   errorInformation = createErrorInformation(respObject.getError().getRecordID(),
        //                                         respObject.getError().getShortText());

        return errorInformation;
    }


}

