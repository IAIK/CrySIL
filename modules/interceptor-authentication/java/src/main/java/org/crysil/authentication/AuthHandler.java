package org.crysil.authentication;

import org.crysil.protocol.payload.auth.AuthInfo;

public interface AuthHandler {
  AuthInfo authenticate() throws AuthException;

  public String getFriendlyName();

  public boolean authenticatesAuthomatically();
}
