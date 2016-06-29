package org.crysil.gatekeeper.challengeresponse;

import java.util.LinkedList;
import java.util.List;

import org.crysil.gatekeeper.AuthPlugin;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;
import org.crysil.protocol.payload.auth.challengeresponse.ChallengeResponseAuthInfo;
import org.crysil.protocol.payload.auth.challengeresponse.ChallengeResponseAuthResult;
import org.crysil.protocol.payload.auth.challengeresponse.ChallengeResponseAuthType;

public class ChallengeResponseAuthPlugin
    extends AuthPlugin<ChallengeResponseAuthType, ChallengeResponseAuthResult> {
  private final ChallengeResponseAuthType authTypeWithChallenge;
  private final ChallengeResponseAuthInfo expectedResult;

  public ChallengeResponseAuthPlugin(final ChallengeResponseAuthType authTypeWhithChallenge,
      final ChallengeResponseAuthInfo expectedResult) {
    this.authTypeWithChallenge = authTypeWhithChallenge;
    this.expectedResult = expectedResult;
  }

  @Override
  public ChallengeResponseAuthType getAuthType() {
    return authTypeWithChallenge;
  }

  @Override
  protected boolean isValid(final ChallengeResponseAuthResult result) {
    return expectedResult.getResponseString().equals(result.getResult());
  }

  @Override
  public Response generateAuthChallenge(final Request request) {
    final Response resp = new Response();

    resp.setHeader(request.getHeader());
    final PayloadAuthResponse payload = new PayloadAuthResponse();
    final List<AuthType> auth = new LinkedList<>();
    auth.add(authTypeWithChallenge);
    payload.setAuthTypes(auth);
    resp.setPayload(payload);
    return resp;
  }

  @Override
  protected ChallengeResponseAuthResult getResponsetoChallenge(final PayloadAuthRequest responseToChallenge) {
    final AuthInfo authInfo = responseToChallenge.getAuthInfo();
    if (!(authInfo instanceof ChallengeResponseAuthInfo)) {
      return null;
    }
    return new ChallengeResponseAuthResult(((ChallengeResponseAuthInfo) authInfo).getResponseString());

  }
}
