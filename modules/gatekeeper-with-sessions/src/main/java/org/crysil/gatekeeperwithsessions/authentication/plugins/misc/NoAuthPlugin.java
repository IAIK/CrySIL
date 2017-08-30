package org.crysil.gatekeeperwithsessions.authentication.plugins.misc;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;

/**
 * Implements an authentication not required method.
 */
public class NoAuthPlugin extends AuthPlugin {

    @Override
    public Response generateAuthChallenge(Response response) {
        // return nothing because we do not need anything from the user
        return null;
    }

    @Override
    public String getReceivedIdentifier(PayloadAuthRequest authRequest) throws AuthenticationFailedException {
        return "";
    }

    @Override
    public void authenticate(PayloadAuthRequest authRequest) throws AuthenticationFailedException {
        // everything is valid
    }

    @Override
    public AuthPlugin newInstance() {
        return new NoAuthPlugin();
    }

    /**
     * Unused because {@link NoAuthPlugin#generateAuthChallenge(Response)} does not use this method.
     */
    @Override
    public AuthType getAuthType() {
        return null;
    }

    @Override
    public Feature getAuthenticationResult() {
        return null;
    }
}
