package org.crysil.authentication;

import org.crysil.authentication.ui.IAuthUI;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthType;

public interface AuthHandlerFactory<A, V, T extends IAuthUI<A, V>> {
  AuthHandler createInstance(Response crysilResponse, AuthType authType, Class<T> dialogType)
      throws AuthException;

  /**
   * Returns if this {@link ResponseInterceptor} can handle the specified response
   *
   * @param crysilResponse
   * @return
   */
  boolean canTake(Response crysilResponse, AuthType authType) throws AuthException;

  public Class<T> getDialogType();
}
