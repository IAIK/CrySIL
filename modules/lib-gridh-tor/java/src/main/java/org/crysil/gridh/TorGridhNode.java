package org.crysil.gridh;

import java.util.Set;

import org.crysil.actor.invertedtrust.InvertedTrustActor;
import org.crysil.authentication.interceptor.InterceptorAuth;
import org.crysil.comm.tor.proxied.ProxiedNode;
import org.crysil.comm.tor.thali.ThaliNode;
import org.crysil.decentral.DecentralNode;
import org.crysil.decentral.NodeStateListener;
import org.crysil.gridh.exceptions.irrecoverable.NodeSetupException;
import org.crysil.gridh.ipc.GridhResponseListener;

import io.nucleo.net.HiddenServiceDescriptor;
import io.nucleo.net.TorNode;

@SuppressWarnings("rawtypes")
public class TorGridhNode extends GridhNode {

  public TorGridhNode(final int port, final int proxyPort, final String name,
      final Set<NodeStateListener> initialListeners, final InvertedTrustActor localActor,
      final GridhResponseListener responseListener, final InterceptorAuth interceptor)
      throws NodeSetupException {
    super(setupCrysilNode(setupNode(port, proxyPort, name, initialListeners), localActor, interceptor),
        responseListener, localActor);
  }

  public TorGridhNode(final HiddenServiceDescriptor hiddenService, final TorNode torNode,
      final Set<NodeStateListener> initialListeners, final InvertedTrustActor localActor,
      final GridhResponseListener responseListener, final InterceptorAuth interceptor)
      throws NodeSetupException {
    super(setupCrysilNode(setupNode(hiddenService, torNode, initialListeners), localActor, interceptor),
        responseListener, localActor);
  }

  private static DecentralNode setupNode(final int port, final int proxyPort, final String name,
      final Set<NodeStateListener> initialListeners) {
    return new ProxiedNode(name, initialListeners, port, proxyPort);
  }

  private static DecentralNode setupNode(final HiddenServiceDescriptor hiddenService, final TorNode torNode,
      final Set<NodeStateListener> initialListeners) {
    return new ThaliNode(hiddenService, torNode, initialListeners);
  }

}
