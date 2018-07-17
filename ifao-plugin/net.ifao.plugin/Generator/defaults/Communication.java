package net.ifao.arctic.agents._PACKAGE_.framework.communication;

import java.io.*;

import java.lang.reflect.*;

import net.ifao.arctic.agents.common.*;
import net.ifao.arctic.communication.*;
import net.ifao.arctic.communication.http.*;
import net.ifao.arctic.framework.*;
import net.ifao.arctic.xml.providerprofiles.*;
import net.ifao.arctic.io.log.*;
import net.ifao.arctic.xml.response.*;
import net.ifao.arctic.xml.response.types.*;
import net.ifao.util.*;

/**
 * <p>
 * This is the Class for the communication with _Provider_.</p>
 * <p>
 * <b>HTTP-connection:</b> A connection to _Provider_ has to be solved by a
 * HTTP-connection.
 * </p>
 * <p>
 * Copyright &copy; 2002, i:FAO
 * @author _GENERATOR_
 */
public class _Provider_Communication
{

// ------------------------------------------------------------------------
//   I M P L E M E N T A T I O N   ( F R A M E W O R K )
// ------------------------------------------------------------------------

    protected IArcticLog log = null;

    // HttpCommunicationObject
    private Communication _httpCommunication = null;

// -------------------------------------------------------------
//  Set validationFlags (for request/response)
// -------------------------------------------------------------

    private boolean _bValidateResponse;

    private boolean _bValidateRequest;


    /**
     * This function initializes the _Provider_Communication object. A
     * HttpCommunicationObject will be initialized.
     *
     * @param pAgentConfiguration The Pro_Provider_Profile with related settings from
     * the ProviderProfiles.xml.
     * @param pLog The log-object. To this object the log-entries are written.
     *
     * @throws AgentException
     * If there was an AgentException, this it thrown.
     * @author _GENERATOR_
     */
    public _Provider_Communication(
            java.io.Serializable pAgentConfiguration, IArcticLog pLog)
        throws AgentException
    {

        /**
         * @todo STANDARD Set the correct values for the validations for request and response elements.
         */

        setValidateRequest(true);
        setValidateResponse(true);

        // Set parameters.
        this.log = pLog;

        // Get a communication form the Communication.object
        try {
            Class[] classes = {};
            Object[] params = {};
            Method getProfileReference =
                pAgentConfiguration.getClass()
                    .getMethod("getProfileReference", classes);

            _httpCommunication = Communication.getCommunication(
                pLog,
                (ProProfileReference) getProfileReference.invoke(
                pAgentConfiguration, params), pAgentConfiguration);
        } catch (Exception ex) {

            throw new AgentException(AgentErrors.PROVIDERPROFILES_ERROR,
                                     ResEnumErrorCategory.ARCTIC_COMPONENT,
                                     ResEnumErrorComponent.ARCTIC_FRAMEWORK,
                                     "Validate _Provider_Communication class",
                                     ex, log);
        }

    }

    /**
     * This function is the 'main' function of this class. It can be used
     * to send a request object to _Provider_. As a result the object
     * of the type <code>pResponseClass</code> is returned.
     * <p>
     * Internally the objects are transformed (via Castor) into String
     * objects and the method <code>sendReceive(String)</code> is called.
     * </p>
     *
     * @param pRequest The RequestObject
     * @param pResponseClass The Reponse class (which is used to instantiate
     * the response Object
     *
     * @return The response object of the type pResponseClass
     *
     * @throws AgentException
     * @author _GENERATOR_
     */
    public Object sendReceive(Object pRequest, Class pResponseClass)
        throws AgentException
    {

        String sResponse = sendReceive(Common.marshalCastorObject(pRequest,
                               false, null, null, _bValidateRequest, log));

        return Common.unmarshalCastorObject(sResponse, pResponseClass, log,
                                            _bValidateResponse);

    }

    /**
     * This function is the 'main' function of this class. It can be used
     * to send a XML formated request string to _Provider_. As a result also
     * a XML-string is returned.
     *
     * @param psRequest
     * The XML-request-string.
     *
     * @return XML-response String
     *
     * @throws AgentException
     * If there was an AgentException, this it thrown.
     * @author _GENERATOR_
     */
    public String sendReceive(String psRequest)
        throws AgentException
    {

        // If there _httpCommunication is not initialized
        if (_httpCommunication == null) {

            // ... throw an AgentException
            throw new AgentException(AgentErrors.HTTP_CONNECTION_FAILURE,
                                     ResEnumErrorCategory.ARCTIC_COMPONENT,
                                     ResEnumErrorComponent.JAVA_IO,
                                     "HttpErrorException", log);
        }

        // a xml-formated string has to start with '<?xml'
        if (!psRequest.startsWith("<?xml")) {
            psRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + psRequest;
        }

        // Set the requestString
        try {

            // Try to start a 'normal' sendReceive Procedure.
            _httpCommunication.sendRequest(psRequest);

            // return the response
            return _httpCommunication.getResponse();
        } catch (HttpErrorException ex1) {

            // ... throw an AgentException if the was an IO-error
            throw new AgentException(AgentErrors.HTTP_CONNECTION_FAILURE,
                                     ResEnumErrorCategory.ARCTIC_COMPONENT,
                                     ResEnumErrorComponent.JAVA_IO,
                                     "HttpErrorException", ex1, log);

        } catch (IOException ex) {

            // ... throw an AgentException if the was an IO-error
            throw new AgentException(AgentErrors.IO_ERROR,
                                     ResEnumErrorCategory.ARCTIC_COMPONENT,
                                     ResEnumErrorComponent.JAVA_IO,
                                     "HttpErrorException", ex, log);
        }
    }

    /**
     * This method closes the connection. It should be called at the end of
     * each communication.
     * @throws AgentException
     *
     * @author _GENERATOR_
     *
     */
    public void closeConnection()
        throws AgentException
    {

        // If the _httpCommunication is not initialized
        if (_httpCommunication == null) {

            // .. make nothing
            return;
        }

        // Close the connection.
        _httpCommunication.closeConnection();

        _httpCommunication = null;
    }

    /**
     * The method setSOAPAction can be used, to set a SOAPAction within a
     * Soap communication. If there is no SOAP communication, this method
     * has no effect.
     *
     * @param psSoapAction The SoapAction-value
     * @author _GENERATOR_
     */
    public void setSOAPAction(String psSoapAction)
    {
        if (_httpCommunication.getInitCommunication()
                instanceof InitHttpSoapCommunication) {
            ((InitHttpSoapCommunication) _httpCommunication
                .getInitCommunication()).setSoapAction(psSoapAction);
        }
    }

    /**
     * Method setValidateResponse
     *
     * @param pbValue true if response should be validated (default=false)
     * @author _GENERATOR_
     */
    public void setValidateResponse(boolean pbValue)
    {
        _bValidateResponse = pbValue;
    }

    /**
     * Method setValidateRequest
     *
     * @param pbValue true if request should be validated (default=true)
     * @author _GENERATOR_
     */
    public void setValidateRequest(boolean pbValue)
    {
        _bValidateRequest = pbValue;
    }


}
