package org.crysil.authentication;

import org.crysil.authentication.ui.IAuthUI;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthType;

public interface AuthenticationPluginFactory<A, V, T extends IAuthUI<A, V>> {
  AuthenticationPlugin createInstance(Response crysilResponse, AuthType authType, Class<T> dialogType)
      throws AuthenticationPluginException;

  /**
   * Returns if this {@link ResponseInterceptor} can handle the specified response
   *
   * @param crysilResponse
   * @return
   */
  boolean canTake(Response crysilResponse, AuthType authType) throws AuthenticationPluginException;

  public Class<T> getDialogType();
}
