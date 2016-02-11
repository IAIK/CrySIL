package org.crysil.protocol.payload.auth.credentials;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.auth.AuthInfo;

/**
 * {@link AuthInfo} implementation of a single secret authentication string.
 */
public class SecretAuthInfo extends AuthInfo {

	/** The secret. */
	protected String secret = "";

	@Override
	public String getType() {
		return "SecretAuthInfo";
	}

	/**
	 * Gets the secret.
	 *
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * Sets the secret.
	 *
	 * @param secret
	 *            the new secret
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Override
	public AuthInfo getBlankedClone() {
		SecretAuthInfo result = new SecretAuthInfo();
		result.secret = Logger.isTraceEnabled() ? secret : "*****";

		return result;
	}
}
