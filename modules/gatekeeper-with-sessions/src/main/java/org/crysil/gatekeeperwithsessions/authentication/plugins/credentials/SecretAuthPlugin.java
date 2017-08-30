package org.crysil.gatekeeperwithsessions.authentication.plugins.credentials;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.credentials.SecretAuthInfo;
import org.crysil.protocol.payload.auth.credentials.SecretAuthType;

public class SecretAuthPlugin extends AuthPlugin {
	private SecretAuthInfo userPasswordAuthInfo;

    @Override
    public AuthType getAuthType() {
		return new SecretAuthType();
    }

    @Override
	public String getReceivedIdentifier(PayloadAuthRequest authRequest) throws AuthenticationFailedException {
        // check authRequest format
		AuthInfo authInfo = authRequest.getAuthInfo();
		if (!(authInfo instanceof SecretAuthInfo)) {
            throw new AuthenticationFailedException();
        }
		userPasswordAuthInfo = (SecretAuthInfo) authInfo;

        return userPasswordAuthInfo.getSecret();
    }

    @Override
    public AuthPlugin newInstance() {
        return new SecretAuthPlugin();
    }

    @Override
    public Feature getAuthenticationResult() {
        if (null != userPasswordAuthInfo)
            return new SecretAuthResult(userPasswordAuthInfo.getSecret());
        return null;
    }
}
