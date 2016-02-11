package org.crysil.protocol.payload.auth.oauth;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.auth.AuthType;

/**
 * {@link AuthType} implementation of an oAuth authentication request. Provides the URL where the oAuth procedure is to be started.
 */
public class OAuthAuthType extends AuthType {

	/** The url. */
	protected String url = "";

	@Override
	public String getType() {
		return "OauthAuthType";
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url
	 *            the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public AuthType getBlankedClone() {
		OAuthAuthType result = new OAuthAuthType();
		result.url = Logger.isDebugEnabled() ? url : "*****";

		return result;
	}
}
