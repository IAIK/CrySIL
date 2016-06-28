package org.crysil.comm.tor;

import java.util.Set;

import org.crysil.decentral.DecentralNode;
import org.crysil.decentral.DecentralNodeActor;
import org.crysil.decentral.NodeState;
import org.crysil.decentral.NodeStateListener;
import org.crysil.decentral.exceptions.irrecoverable.IrrecoverableDecentralException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

public abstract class TorNode<COMM extends TorCommunicationBehavior>
    extends DecentralNode<String, String, Response, Request, COMM> {

  protected final int serverPort;
  protected COMM      comm;

  public TorNode(final String name, final Set<NodeStateListener> initialListeners, final int serverPort) {
    super(name, initialListeners);
    this.serverPort = serverPort;
  }

  @Override
  public COMM setupDefaultCommunicationBehavior(final DecentralNodeActor<Response,Request> actor) {
    try {
      this.comm = setupComm(actor);
      new Thread(comm).start();
    } catch (final IrrecoverableDecentralException e) {
      fireChangeEvent(NodeState.TOR_FAIL);
      e.printStackTrace();
    }
    fireChangeEvent(NodeState.TOR_CONNECTED);
    return this.comm;
  }

  protected abstract COMM setupComm(DecentralNodeActor<Response,Request> actor) throws IrrecoverableDecentralException;

  @Override
  public String getName() {
    return name + ":" + serverPort;
  }

}
