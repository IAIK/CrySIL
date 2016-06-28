package org.crysil.comm.tor.thali;

import java.util.Set;

import org.crysil.comm.tor.TorNode;
import org.crysil.decentral.DecentralNodeActor;
import org.crysil.decentral.NodeStateListener;
import org.crysil.decentral.exceptions.irrecoverable.IrrecoverableDecentralException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

import io.nucleo.net.HiddenServiceDescriptor;

public class ThaliNode extends TorNode<ThaliCommunicationBehavior> {

  private ThaliCommunicationBehavior    comm;
  @SuppressWarnings("rawtypes")
  private final io.nucleo.net.TorNode   torNode;
  private final HiddenServiceDescriptor hiddenService;

  public ThaliNode(final HiddenServiceDescriptor hiddenService,
      @SuppressWarnings("rawtypes") final io.nucleo.net.TorNode torNode,
      final Set<NodeStateListener> initialListeners) {
    super(hiddenService.getHostname(), initialListeners, hiddenService.getLocalPort());
    this.torNode = torNode;
    this.hiddenService = hiddenService;
  }

  @Override
  public void shutdown() {
    comm.shutdown();
  }

  @Override
  protected ThaliCommunicationBehavior setupComm(final DecentralNodeActor<Response, Request> actor) {
    try {
      this.comm = new ThaliCommunicationBehavior(actor, hiddenService, torNode);
    } catch (final IrrecoverableDecentralException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return this.comm;
  }

}
