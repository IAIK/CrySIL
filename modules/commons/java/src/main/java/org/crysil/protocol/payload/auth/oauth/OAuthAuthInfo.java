package org.crysil.protocol.payload.auth.oauth;

import java.util.Arrays;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.auth.AuthInfo;

/**
 * {@link AuthInfo} implementation of an oAuth Authentication Code token.
 */
public class OAuthAuthInfo extends AuthInfo {

	/** The authorization code. */
	protected String authorizationCode = "";

	@Override
	public String getType() {
		return "OauthAuthInfo";
	}

	/**
	 * Gets the authorization code.
	 *
	 * @return the authorization code
	 */
	public String getAuthorizationCode() {
		return authorizationCode;
	}

	/**
	 * Sets the authorization code.
	 *
	 * @param accessToken
	 *            the new authorization code
	 */
	public void setAuthorizationCode(final String accessToken) {
		this.authorizationCode = accessToken;
	}

	@Override
	public AuthInfo getBlankedClone() {
		final OAuthAuthInfo result = new OAuthAuthInfo();
		result.authorizationCode = Logger.isTraceEnabled() ? authorizationCode : "*****";

		return result;
	}
	@Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type,authorizationCode});
  }
}
