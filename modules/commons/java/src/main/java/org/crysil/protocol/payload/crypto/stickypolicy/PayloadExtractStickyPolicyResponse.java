package org.crysil.protocol.payload.crypto.stickypolicy;

import java.util.Arrays;

import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.auth.AuthInfo;

public class PayloadExtractStickyPolicyResponse extends PayloadResponse {

  private AuthInfo authInfo;

  @Override
  public PayloadResponse getBlankedClone() {
    return new PayloadExtractStickyPolicyResponse();
  }

  @Override
  public String getType() {
    return "extractStickyPolicyResponse";
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {
        getType(),
        getAuthInfo() });
  }

  public AuthInfo getAuthInfo() {
    return authInfo;
  }

  public void setAuthInfo(final AuthInfo auhtInfo) {
    this.authInfo = auhtInfo;
  }

}
