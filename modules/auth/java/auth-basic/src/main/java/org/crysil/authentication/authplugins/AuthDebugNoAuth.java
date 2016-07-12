package org.crysil.authentication.authplugins;

import org.crysil.authentication.AuthException;
import org.crysil.authentication.AuthHandler;
import org.crysil.authentication.AuthHandlerFactory;
import org.crysil.authentication.ui.IAuthUI;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.debugnoauth.DebugNoAuthInfo;
import org.crysil.protocol.payload.auth.debugnoauth.DebugNoAuthType;

public class AuthDebugNoAuth<T extends IAuthUI<Void, Void>> implements AuthHandler {

  public static class Factory<T extends IAuthUI<Void, Void>> implements AuthHandlerFactory<Void, Void, T> {

    private final Class<T> dialogType;

    public Factory(final Class<T> dialogType) {
      this.dialogType = dialogType;
    }

    @Override
    public AuthHandler createInstance(final Response crysilResponse, final AuthType authType,
        final Class<T> dialogType) throws AuthException {
      if (!canTake(crysilResponse, authType)) {
        throw new AuthException("Invalid authType");
      }

      return new AuthDebugNoAuth<>();
    }

    @Override
    public boolean canTake(final Response crysilResponse, final AuthType authType) throws AuthException {
      return (authType instanceof DebugNoAuthType);
    }

    @Override
    public Class<T> getDialogType() {
      return dialogType;
    }
  }

  public AuthDebugNoAuth() {
  }

  @Override
  public AuthInfo authenticate() throws AuthException {
    return new DebugNoAuthInfo();
  }

  @Override
  public String getFriendlyName() {
    return "Debug No-Auth";
  }

  @Override
  public boolean authenticatesAuthomatically() {
    return false;
  }
}
