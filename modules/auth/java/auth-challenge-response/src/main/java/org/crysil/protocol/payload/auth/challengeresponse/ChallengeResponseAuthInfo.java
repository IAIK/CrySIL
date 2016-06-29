package org.crysil.protocol.payload.auth.challengeresponse;

import java.util.Arrays;

import org.crysil.protocol.payload.auth.AuthInfo;

/**
 * {@link AuthInfo} implementation of a username/password tuple.
 */
public class ChallengeResponseAuthInfo extends AuthInfo {

  private String  responseString;
  private boolean question;
  private long    expiryDate;
  private String  challengeString;

  public String getResponseString() {
    return responseString;
  }

  @Override
  public String getType() {
    return "challengeRepsonse";
  }

  @Override
  public AuthInfo getBlankedClone() {
    final ChallengeResponseAuthInfo result = new ChallengeResponseAuthInfo();

    return result;
  }

  public void setResponseString(final String string) {
    this.responseString = string;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {
        type,
        responseString });
  }

  public void setExpiryDate(final long timeInMillis) {
    this.expiryDate = timeInMillis;
  }

  public long getExpiryDate() {
    return expiryDate;
  }

  public void setChallengeString(final String challenge) {
    this.challengeString = challenge;
  }

  public String getChallengeString() {
    return challengeString;
  }

  public boolean isQuestion() {
    return question;
  }

  public void setQuestion(final boolean question) {
    this.question = question;
  }
}
