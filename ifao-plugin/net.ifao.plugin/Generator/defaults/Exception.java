package net.ifao.arctic.agents._PACKAGE_.framework.communication;

import net.ifao.arctic.agents.common.AgentErrors;
import net.ifao.arctic.framework.AgentException;
import net.ifao.arctic.io.log.IArcticLog;
import net.ifao.arctic.xml.response.types.ResEnumErrorCategory;
import net.ifao.arctic.xml.response.types.ResEnumErrorComponent;


/**
 * The class _Provider_Exception can be used instead of an AgentException.
 * It encapsulates an specific _Provider_Exception which causes to an error.
 *
 * @author _GENERATOR_
 *
 */
public class _Provider_Exception
    extends AgentException
{

    private static final long serialVersionUID = 1L;

    private Object _exceptionObject;

   /**
     * Default Constructor for _Provider_Exception
     *
     * @param pExceptionObject
     * @param pLog
     * @param pbSendEmail
     * @author _GENERATOR_
     */
    public _Provider_Exception(Object pExceptionObject, IArcticLog pLog,
                        boolean pbSendEmail)
    {
        // call super constructor without sending an email. 
        super(AgentErrors.HTTP_SOAP_FAULT,
              ResEnumErrorCategory.ARCTIC_COMPONENT,
              ResEnumErrorComponent.ARCTIC_HTTP_SOAP,
              "_Provider_ Specific Exception "
              + pExceptionObject.getClass().getName(), pLog, false);

        _exceptionObject = pExceptionObject;

        // finally send EMail if neccessary.
        if (pbSendEmail) {
            sendErrorEmail(getErrorNumber(), pLog);
        }

    }

    /**
     * The method getExceptionObject returns the current ExceptionObject
     *
     * @return the Provider ExceptionObject
     * @author _GENERATOR_
     */
    public Object get_Provider_ExceptionObject()
    {
        return _exceptionObject;
    }

}
