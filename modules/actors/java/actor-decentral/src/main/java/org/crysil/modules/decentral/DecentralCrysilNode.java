package org.crysil.modules.decentral;

import java.util.List;

import org.crysil.commons.Module;
import org.crysil.decentral.DecentralNode;
import org.crysil.decentral.comm.CommunicationBehavior;
import org.crysil.decentral.exceptions.irrecoverable.IrrecoverableDecentralException;
import org.crysil.decentral.exceptions.recoverable.RecoverableDecentralException;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

public class DecentralCrysilNode implements Module {
  private final DecentralNode<?, String, Response, Request, ?>      node;
  private final CommunicationBehavior<?, String, Response, Request> comm;
  private final Module                                              localActor;

  public DecentralCrysilNode(final DecentralNode<?, String, Response, Request, ?> node, final Module actor) {
    this.node = node;
    this.comm = node.setupDefaultCommunicationBehavior(new CrysilConnectionModule(actor));
    this.localActor = actor;
  }

  public DecentralNode<?, String, Response, Request, ?> getNode() {
    return node;
  }

  @Override
  public Response take(final Request crysilRequest) throws CrySILException {
    final List<String> destination = crysilRequest.getHeader().getRequestPath();
    if (destination.isEmpty()) {
      return localActor.take(crysilRequest);
    }

    final String rcpt = destination.get(0);
    destination.remove(0);

    crysilRequest.getHeader().addToResponsePath(rcpt);
    Response response;
    try {
      response = comm.sendBlocking(rcpt, crysilRequest);
      return response;
    } catch (RecoverableDecentralException | IrrecoverableDecentralException e) {
      Throwable t = e;
      do {
        if (t instanceof CrySILException) {
          throw (CrySILException) t;
        }
      } while ((t = t.getCause()) != null);
    }
    throw new UnknownErrorException();

  }
}
