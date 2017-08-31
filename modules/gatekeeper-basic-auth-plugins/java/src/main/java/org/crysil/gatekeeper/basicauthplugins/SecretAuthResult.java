package org.crysil.gatekeeper.basicauthplugins;

import org.crysil.gatekeeper.AuthResult;
import org.crysil.gatekeeper.Descriptor;

@Descriptor(identifier = "challenge-response")
public class SecretAuthResult extends AuthResult {

  private final String result;

  public SecretAuthResult(final String result) {
    this.result = result;
  }

  public String getResult() {
    return result;
  }

}
