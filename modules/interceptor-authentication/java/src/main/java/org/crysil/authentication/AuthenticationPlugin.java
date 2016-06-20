package org.crysil.authentication;

import org.crysil.protocol.Request;

public interface AuthenticationPlugin {
  Request authenticate() throws AuthenticationPluginException;

  public String getFriendlyName();

  public boolean authenticatesAuthomatically();
}
