package org.crysil.authentication.auth_android.gatekeeperplugin;

import java.util.LinkedList;
import java.util.List;

import org.crysil.gatekeeper.AuthPlugin;
import org.crysil.gatekeeper.AuthResult;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthInfo;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthType;

/**
 * misuse IdentifierAuth-stuff of protocol.
 */
public class OneClickAuthPlugin extends AuthPlugin<IdentifierAuthType, OneClickAuthResult> {
  private final IdentifierAuthType authTypeWithChallenge;
  private final IdentifierAuthInfo expectedResult;

  public OneClickAuthPlugin(final IdentifierAuthType authTypeWhithChallenge,
      final IdentifierAuthInfo expectedResult) {
    this.authTypeWithChallenge = authTypeWhithChallenge;
    this.expectedResult = expectedResult;
  }

  @Override
  public IdentifierAuthType getAuthType() {
    return authTypeWithChallenge;
  }

  @Override
  protected boolean isValid(final OneClickAuthResult result) {
    return expectedResult.getIdentifier().equals(result.getResult());
  }

  @Override
  public Response generateAuthChallenge(final Request request) {
    final PayloadAuthResponse payload = new PayloadAuthResponse();
    final List<AuthType> auth = new LinkedList<>();
    auth.add(authTypeWithChallenge);
    payload.setAuthTypes(auth);
    return new Response(request.getHeader().clone(), payload);
  }

  @Override
  protected OneClickAuthResult getResponsetoChallenge(final PayloadAuthRequest responseToChallenge) {
    final AuthInfo authInfo = responseToChallenge.getAuthInfo();
    if (!(authInfo instanceof IdentifierAuthInfo)) {
      return null;
    }
    return new OneClickAuthResult(((IdentifierAuthInfo) authInfo).getIdentifier());

  }
}
