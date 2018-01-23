package org.crysil.communications.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.directory.InitialDirContext;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.logging.Logger;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.credentials.SecretAuthInfo;
import org.crysil.protocol.payload.auth.credentials.SecretAuthType;

public class ActiveDirectoryAuthPlugin extends AuthPlugin {

	private SecretAuthInfo secretAuthInfo;
	private String url;
	private String username;

	public ActiveDirectoryAuthPlugin(String url, String username) throws MalformedURLException {
		// do some validation
		if (0 > url.indexOf(":"))
			throw new MalformedURLException("missing protocol (should be: {ldap, ldaps})");
		if (!url.startsWith("ldap"))
			throw new MalformedURLException(
					"unknown protocol: " + url.substring(0, url.indexOf(":")) + " (should be: {ldap, ldaps})");
		// - RFC2255 is pretty similar to a standard web url
		// - it just starts with "ldap".
		// - so why not reuse the URL parser
		new URL(url.replaceFirst("ldap", "http"));

		this.url = url;
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

		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_PRINCIPAL, username);
		env.put(Context.SECURITY_CREDENTIALS, secretAuthInfo.getSecret());
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

		try {
			// Create initial context and see if it works
			InitialContext ctx = new InitialDirContext(env);

			// note that we only need to close the context when it was
			// successfully created.
			ctx.close();

			// if it worked the credentials have been valid.
			return "granted";

		} catch (Exception e) {
			Logger.debug("authentication failed. AD said so.");
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
