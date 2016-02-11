package org.crysil.protocol.payload.auth.credentials;

import org.crysil.protocol.payload.auth.AuthType;

/**
 * {@link AuthType} implementation of a username/password tuple.
 */
public class UserPasswordAuthType extends AuthType {

	@Override
	public String getType() {
		return "UserNamePasswordAuthType";
	}

	@Override
	public AuthType getBlankedClone() {
		return new UserPasswordAuthType();
	}
}
