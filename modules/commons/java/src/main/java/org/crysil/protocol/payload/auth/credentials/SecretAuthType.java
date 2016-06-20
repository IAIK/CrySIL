/*
 * crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.protocol.payload.auth.credentials;

import org.crysil.protocol.payload.auth.AuthType;

/**
 * {@link AuthType} implementation of a single secret authentication string.
 */
public class SecretAuthType extends AuthType {

	@Override
	public String getType() {
		return "SecretAuthType";
	}

	@Override
	public AuthType getBlankedClone() {
		return new SecretAuthType();
	}
}
