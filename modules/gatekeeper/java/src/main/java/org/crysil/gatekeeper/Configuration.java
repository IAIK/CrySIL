package org.crysil.gatekeeper;

import org.crysil.protocol.Request;

public interface Configuration {



  public AuthProcess getAuthProcess(Request request, Gatekeeper gatekeeper) throws AuthenticationFailedException;
}
