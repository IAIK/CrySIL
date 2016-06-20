package org.crysil.gatekeeper.debugnoauth;

import org.crysil.gatekeeper.AuthProcess;
import org.crysil.gatekeeper.AuthenticationFailedException;
import org.crysil.gatekeeper.Configuration;
import org.crysil.gatekeeper.Gatekeeper;
import org.crysil.protocol.Request;

public class DebugNoAuthConfiguration implements Configuration {

  @Override
  public AuthProcess getAuthProcess(final Request request, final Gatekeeper gatekeeper)
      throws AuthenticationFailedException {
    return new AuthProcess(request, new DebugNoAuthPlugin());
  }

}