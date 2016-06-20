package org.crysil.protocol.payload.auth.challengeresponse;

import org.crysil.gatekeeper.AuthResult;
import org.crysil.gatekeeper.Descriptor;

@Descriptor(identifier = "challenge-response")
public class ChallengeResponseAuthResult extends AuthResult {

  private final String result;

  public ChallengeResponseAuthResult(final String result) {
    this.result = result;
  }

  public String getResult() {
    return result;
  }

}
