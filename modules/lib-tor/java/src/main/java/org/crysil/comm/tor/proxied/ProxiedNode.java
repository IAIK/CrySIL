package org.crysil.comm.tor.proxied;

import java.util.Set;

import org.crysil.comm.tor.TorNode;
import org.crysil.decentral.DecentralNodeActor;
import org.crysil.decentral.NodeStateListener;
import org.crysil.decentral.exceptions.irrecoverable.IrrecoverableDecentralException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

public class ProxiedNode extends TorNode<ProxiedCommunicationBehavior> {

  private final int                    proxyPort;
  private ProxiedCommunicationBehavior comm;

  public ProxiedNode(final String name, final Set<NodeStateListener> initialListeners, final int serverPort,
      final int proxyPort) {
    super(name, initialListeners, serverPort);
    this.proxyPort = proxyPort;
  }

  @Override
  public void shutdown() {
    // TODO Auto-generated method stub

  }

  @Override
  protected ProxiedCommunicationBehavior setupComm(final DecentralNodeActor<Response,Request> actor) {
    try {
      this.comm = new ProxiedCommunicationBehavior(actor, proxyPort, serverPort);
    } catch (final IrrecoverableDecentralException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return this.comm;
  }

}
