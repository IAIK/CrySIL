package org.crysil.communications.http;

import java.net.MalformedURLException;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.logging.Logger;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthInfo;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthType;

public class EmulatedActiveDirectoryAttributeAuthPlugin extends AuthPlugin {

	private IdentifierAuthInfo credentials;
	private String mail;

	public EmulatedActiveDirectoryAttributeAuthPlugin(String url, String string, String adminpassword, String searchRoot,
			String searchFilter, String targetAttribute)
			throws MalformedURLException {
		setExpectedValue("granted");
	}

	@Override
    public AuthType getAuthType() {
		return new IdentifierAuthType();
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
		if (!(authInfo instanceof IdentifierAuthInfo)) {
            throw new AuthenticationFailedException();
        }
		credentials = (IdentifierAuthInfo) authInfo;

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
		if (credentials.getIdentifier() == null || credentials.getIdentifier().length() == 0) {
			Logger.debug("authentication failed. Credentials are empty or null.");
			throw new AuthenticationFailedException();
		}

		mail = credentials.getIdentifier() + "@crysil.iaik.tugraz.at";

		return "granted";
    }

    @Override
    public Feature getAuthenticationResult() {
		return new ActiveDirectoryAttributeAuthResult(credentials.getIdentifier(), mail);
    }
}
