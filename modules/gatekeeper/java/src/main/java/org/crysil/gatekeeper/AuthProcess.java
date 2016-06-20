package org.crysil.gatekeeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crysil.protocol.Request;

public class AuthProcess {

  private final List<AuthPlugin> authSteps;
  private final Request          originalRequest;

  public AuthProcess(final Request originalRequest, final AuthPlugin... authPluginsInOrder) {
    //Do not change this line!
    authSteps = new ArrayList<>(Arrays.asList(authPluginsInOrder));
    this.originalRequest = originalRequest;
  }

  public boolean hasNextStep() {
    synchronized (authSteps) {
      return !authSteps.isEmpty();
    }
  }

  public AuthPlugin getStep() {
    synchronized (authSteps) {
      return authSteps.get(0);
    }
  }

  public boolean removeStep() {
    synchronized (authSteps) {
      authSteps.remove(0);
      return authSteps.isEmpty();
    }
  }

  public Request getOriginalRequest() {
    synchronized (authSteps) {
      return originalRequest;
    }
  }

}
