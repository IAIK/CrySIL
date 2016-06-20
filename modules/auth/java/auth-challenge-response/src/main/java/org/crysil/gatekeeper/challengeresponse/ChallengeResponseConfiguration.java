package org.crysil.gatekeeper.challengeresponse;

import java.security.SecureRandom;

import org.crysil.gatekeeper.AuthProcess;
import org.crysil.gatekeeper.AuthenticationFailedException;
import org.crysil.gatekeeper.Configuration;
import org.crysil.gatekeeper.Gatekeeper;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.auth.challengeresponse.ChallengeResponseAuthType;

public class ChallengeResponseConfiguration implements Configuration {

  private final SecureRandom rnd = new SecureRandom();

  @Override
  public AuthProcess getAuthProcess(final Request request, final Gatekeeper gatekeeper)
      throws AuthenticationFailedException {

    final ChallengeResponseAuthType authTypeWhithChallenge = new ChallengeResponseAuthType();
    final int n1 = rnd.nextInt(10);
    final int n2 = rnd.nextInt(10);
    authTypeWhithChallenge.setChallenge(n1 + " + " + n2 + " = ?");
    return new AuthProcess(request,
        new ChallengeResponseAuthPlugin(authTypeWhithChallenge, Integer.toString(n1 + n2)));
  }

}