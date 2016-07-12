package org.crysil.authentication.authplugins;

import org.crysil.authentication.AuthException;
import org.crysil.authentication.AuthHandler;
import org.crysil.authentication.AuthHandlerFactory;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthInfo;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthType;

public class AuthConstantIdentifier implements AuthHandler {
  public static String identifier = "";

  public AuthConstantIdentifier() {
  }

  @SuppressWarnings("rawtypes")
  public static class Factory implements AuthHandlerFactory {
    @Override
    public AuthHandler createInstance(final Response crysilResponse, final AuthType authType,
        final Class ignoreMe) throws AuthException {
      if (!canTake(crysilResponse, authType)) {
        throw new AuthException("Invalid authType");
      }

      return new AuthConstantIdentifier();
    }

    @Override
    public boolean canTake(final Response crysilResponse, final AuthType authType) throws AuthException {
      return (authType instanceof IdentifierAuthType);
    }

    @Override
    public Class getDialogType() {
      return null;
    }
  }

  @Override
  public AuthInfo authenticate() throws AuthException {
    final IdentifierAuthInfo authInfo = new IdentifierAuthInfo();
    authInfo.setIdentifier(identifier);
    return authInfo;
  }

  @Override
  public String getFriendlyName() {
    return "Constant identifier";
  }

  @Override
  public boolean authenticatesAuthomatically() {
    return true;
  }
}
