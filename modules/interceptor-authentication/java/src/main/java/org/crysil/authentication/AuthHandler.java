package org.crysil.authentication;

import org.crysil.protocol.Request;

public interface AuthHandler {
  Request authenticate() throws AuthException;

  public String getFriendlyName();

  public boolean authenticatesAuthomatically();
}
