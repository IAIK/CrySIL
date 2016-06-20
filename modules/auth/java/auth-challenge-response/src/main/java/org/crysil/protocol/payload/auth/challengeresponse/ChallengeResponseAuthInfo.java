package org.crysil.protocol.payload.auth.challengeresponse;

import org.crysil.protocol.payload.auth.AuthInfo;

/**
 * {@link AuthInfo} implementation of a username/password tuple.
 */
public class ChallengeResponseAuthInfo extends AuthInfo {

  private String responseString;

  public String getResponseString() {
    return responseString;
  }

  @Override
  public String getType() {
    return "ChallengeRepsonse";
  }

  @Override
  public AuthInfo getBlankedClone() {
    final ChallengeResponseAuthInfo result = new ChallengeResponseAuthInfo();

    return result;
  }

  public void setResponseString(final String string) {
    this.responseString = string;
  }
}
