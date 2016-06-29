package org.crysil.gatekeeper;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;

public abstract class AuthPlugin<T extends AuthType, R extends AuthResult> {

  public void authenticate(final PayloadAuthRequest responseToChallenge)
      throws AuthenticationFailedException {
    final R result = getResponsetoChallenge(responseToChallenge);
    if (!isValid(result)) {
      throw new AuthenticationFailedException();
    }
  }

  protected abstract boolean isValid(final R result);

  public abstract Response generateAuthChallenge(final Request request);

  public abstract T getAuthType();

  protected abstract R getResponsetoChallenge(PayloadAuthRequest responseToChallenge);

}
