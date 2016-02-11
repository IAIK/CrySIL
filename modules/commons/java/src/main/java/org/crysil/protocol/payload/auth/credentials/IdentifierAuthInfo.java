package org.crysil.protocol.payload.auth.credentials;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.auth.AuthInfo;

/**
 * {@link AuthInfo} implementation for a simple world readable identifier string.
 */
public class IdentifierAuthInfo extends AuthInfo {

	/** The identifier. */
	protected String identifier = "";

	@Override
	public String getType() {
		return "IdentifierAuthInfo";
	}

	/**
	 * Gets the identifier.
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Sets the identifier.
	 *
	 * @param identifier
	 *            the new identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public AuthInfo getBlankedClone() {
		IdentifierAuthInfo result = new IdentifierAuthInfo();
		result.identifier = Logger.isTraceEnabled() ? identifier : "*****";

		return result;
	}
}
