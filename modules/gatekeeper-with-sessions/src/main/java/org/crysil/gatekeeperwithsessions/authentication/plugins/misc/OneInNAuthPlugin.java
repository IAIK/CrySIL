package org.crysil.gatekeeperwithsessions.authentication.plugins.misc;

import java.util.ArrayList;
import java.util.List;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.IdentifierAuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.SecretAuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.UsernamePasswordAuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;

public class OneInNAuthPlugin extends AuthPlugin {
    /**
     * The authentication methods.
     */
    private List<AuthPlugin> methods;

    public List<AuthPlugin> getMethods() {
        return methods;
    }

    public void setMethods(List<AuthPlugin> methods) {
        this.methods = methods;
    }

    /**
     * Instantiates a new one in n auth plugin.
     */
    public OneInNAuthPlugin() {
        if (methods == null) {
            methods = new ArrayList<>();
            methods.add(new IdentifierAuthPlugin());
            methods.add(new SecretAuthPlugin());
            methods.add(new UsernamePasswordAuthPlugin());
        }
    }

    /**
     * not used since {@link OneInNAuthPlugin#generateAuthChallenge(Response)} implements its own method
     */
    @Override
    public AuthType getAuthType() {
        return null;
    }

    /**
     * pack all given authentication methods into the response.
     */
    @Override
    public Response generateAuthChallenge(Response response) {
        PayloadAuthResponse authResponse = new PayloadAuthResponse();
        for (AuthPlugin current : methods) {
            authResponse.addAuthType(current.getAuthType());
        }
        response.setPayload(authResponse);
        return response;
    }

    /**
     * asks all configured AuthPlugins for the identifier
     */
    @Override
    public String getReceivedIdentifier(PayloadAuthRequest authRequest) throws AuthenticationFailedException {
        for (AuthPlugin current : methods) {
            try {
                return current.getReceivedIdentifier(authRequest);
            } catch (AuthenticationFailedException e) {
                // this very AuthPlugin cannot handle the response
            }
        }

        // none of the AuthPlugins could handle the response
        throw new AuthenticationFailedException();
    }

    @Override
    public AuthPlugin newInstance() {
        return new OneInNAuthPlugin();
    }

    @Override
    public Feature getAuthenticationResult() {
        for (AuthPlugin current : methods) {
            if (null != current.getAuthenticationResult())
                return current.getAuthenticationResult();
        }
        return null;
    }
}
