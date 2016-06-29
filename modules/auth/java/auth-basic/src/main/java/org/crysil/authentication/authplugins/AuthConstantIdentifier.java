package org.crysil.authentication.authplugins;

import org.crysil.authentication.AuthHandler;
import org.crysil.authentication.AuthException;
import org.crysil.authentication.AuthHandlerFactory;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthInfo;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthType;

public class AuthConstantIdentifier implements AuthHandler {
  private final Response crysilResponse;
  public static String   identifier = "";

  public AuthConstantIdentifier(final Response crysilResponse, final AuthType authType) {
    this.crysilResponse = crysilResponse;
  }

  @SuppressWarnings("rawtypes")
  public static class Factory implements AuthHandlerFactory {
    @Override
    public AuthHandler createInstance(final Response crysilResponse, final AuthType authType,
        final Class ignoreMe) throws AuthException {
      if (!canTake(crysilResponse, authType)) {
        throw new AuthException("Invalid authType");
      }

      return new AuthConstantIdentifier(crysilResponse, authType);
    }

    @Override
    public boolean canTake(final Response crysilResponse, final AuthType authType)
        throws AuthException {
      return (authType instanceof IdentifierAuthType);
    }

    @Override
    public Class getDialogType() {
      return null;
    }
  }

  @Override
  public Request authenticate() throws AuthException {
    final Request authRequest = new Request();

    final Header header = new StandardHeader();
    header.setCommandId(crysilResponse.getHeader().getCommandId());
    authRequest.setHeader(header);

    final IdentifierAuthInfo authInfo = new IdentifierAuthInfo();
    authInfo.setIdentifier(identifier);
    final PayloadAuthRequest authRequestPayload = new PayloadAuthRequest();
    authRequestPayload.setAuthInfo(authInfo);
    authRequest.setPayload(authRequestPayload);

    return (authRequest);
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
