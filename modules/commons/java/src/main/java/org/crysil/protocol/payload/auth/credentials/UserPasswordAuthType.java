package org.crysil.protocol.payload.auth.credentials;

import java.util.Arrays;

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

	@Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type});
  }
}
