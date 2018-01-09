package org.crysil.gatekeeperwithsessions.authentication.plugins.credentials;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthInfo;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthType;

/**
 * Implements a simple authentication method by identifier.
 */
public class IdentifierAuthPlugin extends AuthPlugin {
	private IdentifierAuthInfo userPasswordAuthInfo;

    @Override
    public AuthType getAuthType() {
		return new IdentifierAuthType();
    }

    @Override
	public String getReceivedIdentifier(PayloadAuthRequest authRequest) throws AuthenticationFailedException {
        // check authRequest format
		AuthInfo authInfo = authRequest.getAuthInfo();
		if (!(authInfo instanceof IdentifierAuthInfo)) {
            throw new AuthenticationFailedException();
        }
		userPasswordAuthInfo = (IdentifierAuthInfo) authInfo;

        return userPasswordAuthInfo.getIdentifier();
    }

    @Override
    public Feature getAuthenticationResult() {
        if (null != userPasswordAuthInfo)
            return new IdentifierAuthResult(userPasswordAuthInfo.getIdentifier());
        return null;
    }

}
