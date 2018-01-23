package org.crysil.communications.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.logging.Logger;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthInfo;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthType;

public class ActiveDirectoryAttributeAuthPlugin extends AuthPlugin {

	private String url;
	private String adminuser;
	private String adminpassword;
	private final String searchRoot;
	private final String searchFilter;
	private Object targetAttribute;
	private IdentifierAuthInfo credentials;
	private String mail;

	public ActiveDirectoryAttributeAuthPlugin(String url, String adminuser, String adminpassword, String searchRoot,
			String searchFilter, String targetAttribute)
			throws MalformedURLException {
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
		this.adminpassword = adminpassword;
		this.adminuser = adminuser;
		this.searchRoot = searchRoot;
		this.searchFilter = searchFilter;
		this.targetAttribute = targetAttribute;
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

		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_PRINCIPAL, adminuser);
		env.put(Context.SECURITY_CREDENTIALS, adminpassword);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

		DirContext ctx = null;
		try {
			// Create initial context and see if it works
			ctx = new InitialDirContext(env);

			// Attributes anothertmp = ctx.getAttributes("mail=" +
			// credentials.getIdentifier());

			NamingEnumeration<SearchResult> tmp = ctx.search(searchRoot, searchFilter,
					new Object[] { targetAttribute, credentials.getIdentifier() }, null);

			// note that we only need to close the context when it was
			// successfully created.
			if (tmp.hasMore()) {

				mail = (String) tmp.next().getAttributes().get("mail").get(0);
				return "granted";
			}
			else
				return "denied";
		} catch (Exception e) {
			Logger.debug("admin authentication failed. AD said so.");
			// if not, the credentials were bad
			return "denied";
		} finally {
			if (null != ctx)
				try {
					ctx.close();
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
    }

    @Override
    public Feature getAuthenticationResult() {
		return new ActiveDirectoryAttributeAuthResult(credentials.getIdentifier(), mail);
    }
}
