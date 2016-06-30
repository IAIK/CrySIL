package org.crysil.gatekeeper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.crysil.commons.Module;
import org.crysil.commons.OneToOneInterlink;
import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.errorhandling.CrySILException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.status.PayloadStatus;

public class Gatekeeper extends OneToOneInterlink implements Module {

  private final Map<String, AuthProcess> pendingAuthProcesses;
  private final Configuration            conf;

  public Gatekeeper(final Configuration conf) {
    pendingAuthProcesses = new HashMap<>();
    this.conf = conf;
  }

  private Response fail(final Request request, final String reason) {
    Logger.error(reason);
    final PayloadStatus payloadStatus = new PayloadStatus();
    payloadStatus.setCode(new AuthenticationFailedException().getErrorCode());
    return new Response(request.getHeader().clone(), payloadStatus);
  }

  @Override
	public Response take(final Request request) throws CrySILException {
    String commandId = request.getHeader().getCommandId();
    if (commandId == null) {
      commandId = UUID.randomUUID().toString();
      request.getHeader().setCommandId(commandId);
    }
    synchronized (pendingAuthProcesses) {
      if (isPending(request)) {
        Logger.debug("Auth pending pending for {}", request.getBlankedClone());
        if (request.getPayload() instanceof PayloadAuthRequest) {
          Logger.debug("It is an auth reply!");
          try {
            final AuthProcess authProcess = pendingAuthProcesses.get(commandId);
            final AuthPlugin authStep = authProcess.getStep();
            Logger.debug("Got auth step {}", authStep.getClass().getCanonicalName());
            Logger.debug("Authenticating with palyoad {}", request.getPayload().getBlankedClone());
            authStep.authenticate((PayloadAuthRequest) request.getPayload());
            final boolean done = authProcess.removeStep();
            if (done) {
              pendingAuthProcesses.remove(commandId);
              return getAttachedModule().take(authProcess.getOriginalRequest());
            }
            final AuthPlugin nextStep = authProcess.getStep();
            Logger.debug("Generating auth challenge");
            return nextStep.generateAuthChallenge(authProcess.getOriginalRequest());
          } catch (final AuthenticationFailedException e) {
            e.printStackTrace();
            return fail(request, "AUTH failed");
          }
        }
        // this is bad!
        return fail(request, "An already pending non-authreply request was received");
      }

      // not pending, no auth request
      if (request.getPayload() instanceof PayloadAuthRequest) {
        return fail(request, "non-pending authreply");
      }

      try {
        Logger.debug("creating new auth process for request {}", request);
        final AuthProcess authProcess = conf.getAuthProcess(request, this);
        if (!authProcess.hasNextStep()) {
          Logger.debug("No auth is needed!");
          // no auth needed
          return getAttachedModule().take(request);
        }
        // at least one step!

        Logger.debug("Rememb'ring request {}", request);
        pendingAuthProcesses.put(commandId, authProcess);
        final AuthPlugin step = authProcess.getStep();
        Logger.debug("Generating auth challenge");
        return step.generateAuthChallenge(request);
      } catch (final AuthenticationFailedException e) {
        e.printStackTrace();
        return fail(request, "failed to generate auth challenge");
      }
    }
  }

  private boolean isPending(final Request request) {
    return pendingAuthProcesses.containsKey(request.getHeader().getCommandId());
  }

}
