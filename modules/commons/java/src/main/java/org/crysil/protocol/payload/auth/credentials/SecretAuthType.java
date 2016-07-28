package org.crysil.protocol.payload.auth.credentials;

import java.util.Arrays;

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

	@Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type});
  }
}
