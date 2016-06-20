package org.crysil.gatekeeper.debugnoauth;

import org.crysil.gatekeeper.AuthPlugin;
import org.crysil.gatekeeper.AuthResult;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;
import org.crysil.protocol.payload.auth.debugnoauth.DebugNoAuthType;

public class DebugNoAuthPlugin extends AuthPlugin {

  @Override
  public AuthType getAuthType() {
    return new DebugNoAuthType();
  }

  @Override
  public Response generateAuthChallenge(final Request request) {
    final Response noAuthResp = new Response();
    noAuthResp.setHeader(request.getHeader());
    final PayloadAuthResponse payloadAuthResponse = new PayloadAuthResponse();
    payloadAuthResponse.addAuthType(new DebugNoAuthType());
    noAuthResp.setPayload(payloadAuthResponse);
    return noAuthResp;
  }

  @Override
  protected AuthResult getResponsetoChallenge(final PayloadAuthRequest responseToChallenge) {
    return null;
  }

  @Override
  protected boolean isValid(final AuthResult result) {
    return true;
  }

}
