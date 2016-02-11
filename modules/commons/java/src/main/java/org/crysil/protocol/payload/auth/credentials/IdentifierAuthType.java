package org.crysil.protocol.payload.auth.credentials;

import org.crysil.protocol.payload.auth.AuthType;

/**
 * {@link AuthType} implementation of a simple world readable identifier string.
 */
public class IdentifierAuthType extends AuthType {

	@Override
	public String getType() {
		return "IdentifierAuthType";
	}

	@Override
	public AuthType getBlankedClone() {
		return new IdentifierAuthType();
	}
}
