package org.crysil.protocol.payload.auth.challengeresponse;

import java.util.Arrays;

import org.crysil.protocol.payload.auth.AuthType;

public class ChallengeResponseAuthType extends AuthType {

  public static final String TYPE_ID = "challenge-response";

  private String             challenge;
  private boolean            question;

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

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {
        type,
        challenge });
  }

  public boolean isQuestion() {
    return question;
  }

  public void setQuestion(final boolean question) {
    this.question = question;
  }

}
