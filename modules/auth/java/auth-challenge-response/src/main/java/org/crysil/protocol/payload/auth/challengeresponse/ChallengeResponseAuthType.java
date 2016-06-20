package org.crysil.protocol.payload.auth.challengeresponse;

import org.crysil.protocol.payload.auth.AuthType;

public class ChallengeResponseAuthType extends AuthType {

  public static final String TYPE_ID = "challenge-response";

  private String             challenge;

  @Override
  public AuthType getBlankedClone() {
    return this;
  }

  @Override
  public String getType() {
    return TYPE_ID;
  }

  public String getChallenge() {
    return challenge;
  }

  public void setChallenge(final String challenge) {
    this.challenge = challenge;
  }

}
