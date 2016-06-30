package org.crysil.gatekeeper.debugnoauth;

import org.crysil.gatekeeper.AuthPlugin;
import org.crysil.gatekeeper.AuthResult;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;
import org.crysil.protocol.payload.auth.debugnoauth.DebugNoAuthType;

public class DebugNoAuthPlugin extends AuthPlugin<DebugNoAuthType, AuthResult> {

  @Override
  public DebugNoAuthType getAuthType() {
    return new DebugNoAuthType();
  }

  @Override
  public Response generateAuthChallenge(final Request request) {
    final PayloadAuthResponse payloadAuthResponse = new PayloadAuthResponse();
    payloadAuthResponse.addAuthType(new DebugNoAuthType());
    return new Response(request.getHeader().clone(), payloadAuthResponse);
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
