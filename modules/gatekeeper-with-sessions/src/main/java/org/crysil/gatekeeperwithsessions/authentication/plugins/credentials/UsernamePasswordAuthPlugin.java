package org.crysil.gatekeeperwithsessions.authentication.plugins.credentials;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.credentials.UserPasswordAuthInfo;
import org.crysil.protocol.payload.auth.credentials.UserPasswordAuthType;

public class UsernamePasswordAuthPlugin extends AuthPlugin {
	private UserPasswordAuthInfo userPasswordAuthInfo;

    @Override
    public AuthType getAuthType() {
		return new UserPasswordAuthType();
    }

    @Override
    public String getReceivedIdentifier(PayloadAuthRequest authRequest) throws AuthenticationFailedException {
        // check authRequest format
		AuthInfo authInfo = authRequest.getAuthInfo();
		if (!(authInfo instanceof UserPasswordAuthInfo)) {
            throw new AuthenticationFailedException();
        }
		userPasswordAuthInfo = (UserPasswordAuthInfo) authInfo;

        return userPasswordAuthInfo.getUserName() + userPasswordAuthInfo.getPassWord();
    }

    @Override
    public Feature getAuthenticationResult() {
        if (null != userPasswordAuthInfo)
            return new UsernamePasswordAuthResult(userPasswordAuthInfo.getUserName(), userPasswordAuthInfo.getPassWord());
        return null;
    }
}
