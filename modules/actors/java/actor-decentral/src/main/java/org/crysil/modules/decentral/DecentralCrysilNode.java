package org.crysil.modules.decentral;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.crysil.commons.Module;
import org.crysil.decentral.DecentralNode;
import org.crysil.decentral.comm.CommunicationBehavior;
import org.crysil.decentral.exceptions.irrecoverable.IrrecoverableDecentralException;
import org.crysil.decentral.exceptions.recoverable.RecoverableDecentralException;
import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

public class DecentralCrysilNode implements Module {
  public static final String                                        DST_LOCAL = "****LOCAL****";

  private final DecentralNode<?, String, Response, Request, ?>      node;
  private final CommunicationBehavior<?, String, Response, Request> comm;
  private String                                                    destinationNode;
  private final Module                                              localActor;
  private final Lock                                                lock;

  public DecentralCrysilNode(final DecentralNode<?, String, Response, Request, ?> node, final Module actor) {
    this.node = node;
    this.comm = node.setupDefaultCommunicationBehavior(new CrysilConnectionModule(actor));
    this.destinationNode = node.getName();
    this.localActor = actor;
    lock = new ReentrantLock();
  }

  public DecentralNode<?, String, Response, Request, ?> getNode() {
    return node;
  }

  public void setDestinationNode(final String destinationNode) {
    lock.lock();
    this.destinationNode = destinationNode;
  }

  public void releaseDestinationNode() {
    destinationNode = null;
    lock.unlock();
  }

  @Override
	public Response take(final Request crysilRequest) throws CrySILException {
    if (destinationNode == null) {
      System.err.println("No Destination set!");
    }
    if (DST_LOCAL.equals(destinationNode)) {
			return localActor.take(crysilRequest);
    }

    try {
      final Response response = comm.sendBlocking(destinationNode, crysilRequest);
      return response;
    } catch (final RecoverableDecentralException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (final IrrecoverableDecentralException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
}
