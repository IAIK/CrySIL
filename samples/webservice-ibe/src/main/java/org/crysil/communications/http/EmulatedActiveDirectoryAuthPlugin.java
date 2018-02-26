package org.crysil.communications.http;

import java.net.MalformedURLException;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.logging.Logger;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.credentials.SecretAuthInfo;
import org.crysil.protocol.payload.auth.credentials.SecretAuthType;

public class EmulatedActiveDirectoryAuthPlugin extends AuthPlugin {

	private SecretAuthInfo secretAuthInfo;
	private String username;

	public EmulatedActiveDirectoryAuthPlugin(String username) throws MalformedURLException {
		this.username = username;
		setExpectedValue("granted");
	}

	@Override
    public AuthType getAuthType() {
		return new SecretAuthType();
    }

	/**
	 * The expected value is ignored when dealing with active directory. Setting the value does not do nothing. 
	 *
	 * @param value is ignored
	 */
	@Override
	public void setExpectedValue(String value) {
		super.setExpectedValue("granted");
	}

    @Override
    public String getReceivedIdentifier(PayloadAuthRequest authRequest) throws AuthenticationFailedException {
        // check authRequest format
		AuthInfo authInfo = authRequest.getAuthInfo();
		if (!(authInfo instanceof SecretAuthInfo)) {
            throw new AuthenticationFailedException();
        }
		secretAuthInfo = (SecretAuthInfo) authInfo;

		/*
		 * having username:password notation
		 * - username:
		 * - :password
		 * - :
		 * yielded a true in the code below. This behavior is surprising, because
		 * - the doc says it works that way
		 * - an incorrect but non-empty username:password results in an authentication error.
		 * 
		 * Therefore:
		 */
		if (secretAuthInfo.getSecret() == null || secretAuthInfo.getSecret().length() == 0) {
			Logger.debug("authentication failed. Credentials are empty or null.");
			throw new AuthenticationFailedException();
		}
		
		if (username.equals("IAIK\\" + secretAuthInfo.getSecret())) {
			// if it worked the credentials have been valid.
			return "granted";

		} else {
			// if not, the credentials were bad
			return "denied";
		}
    }

    @Override
    public Feature getAuthenticationResult() {
		if (null != secretAuthInfo)
			return new ActiveDirectoryAuthResult(username);
        return null;
    }
}
