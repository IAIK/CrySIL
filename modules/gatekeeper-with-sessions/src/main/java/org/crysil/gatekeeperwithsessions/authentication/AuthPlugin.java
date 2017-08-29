package org.crysil.gatekeeperwithsessions.authentication;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;

/**
 * The abstract authentication plugin. May be implemented to fit various methods.
 */
public abstract class AuthPlugin {
    /**
     * The expected identifier.
     */
    private String expected = null;

    /**
     * Authenticate.
     *
     * @param authRequest the auth request
     * @return the user bean
     * @throws AuthenticationFailedException the authentication failed exception
     */
    public void authenticate(PayloadAuthRequest authRequest) throws AuthenticationFailedException {
        String identifier = getReceivedIdentifier(authRequest);

        if (null != expected)
            if (!identifier.equals(expected)) {
                throw new AuthenticationFailedException();
            }
    }

    /**
     * Generate appropriate auth challenge.
     *
     * @param response the response
     * @return the response
     */
    public Response generateAuthChallenge(Response response) {
        PayloadAuthResponse authResponse = new PayloadAuthResponse();
        authResponse.addAuthType(getAuthType());
        response.setPayload(authResponse);
        return response;
    }

    /**
     * Gets the auth type.
     *
     * @return the auth type
     */
    abstract public AuthType getAuthType();

    /**
     * Sets the expected value.
     *
     * @param value the new expected value
     */
    public void setExpectedValue(String value) {
        expected = value;
    }

    /**
     * Gets the received identifier.
     *
     * @param authRequest the auth request
     * @return the received identifier
     * @throws AuthenticationFailedException the authentication failed exception
     */
    abstract public String getReceivedIdentifier(PayloadAuthRequest authRequest) throws AuthenticationFailedException;

    /**
     * New instance.
     *
     * @return the auth plugin
     */
    public abstract AuthPlugin newInstance();

    public abstract Feature getAuthenticationResult();
}
