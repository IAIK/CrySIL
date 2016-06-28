package org.crysil.decentral;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.crysil.decentral.comm.CommunicationBehavior;

public abstract class DecentralNode<SENDER, RCPT, R extends Serializable, S extends Serializable, COMM extends CommunicationBehavior<SENDER, RCPT, R, S>> {

  protected final String               name;
  private final Set<NodeStateListener> changeListeners;

  private NodeState                    state;

  protected COMM                       communicationBehavior;

  public DecentralNode(final String name, final Set<NodeStateListener> initialListeners) {
    this.name = name;
    this.changeListeners = (initialListeners == null ? new HashSet<NodeStateListener>()
        : new HashSet<NodeStateListener>(initialListeners));
    this.state = NodeState.OFFLINE;
  }

  public abstract COMM setupDefaultCommunicationBehavior(DecentralNodeActor<R, S> actor);

  public String getName() {
    return name;
  }

  public NodeState getState() {
    return state;
  }

  public void addChangeListener(final NodeStateListener l) {
    synchronized (changeListeners) {
      changeListeners.add(l);
    }
  }

  public void removeChangeListener(final NodeStateListener l) {
    synchronized (changeListeners) {
      changeListeners.remove(l);
    }
  }

  protected void fireChangeEvent(final NodeState info) {
    state = info;
    synchronized (changeListeners) {
      for (final NodeStateListener l : changeListeners) {
        l.stateChanged(info);
      }
    }
  }

  public COMM getCommunicationBehavior() {
    return communicationBehavior;
  }

  public abstract void shutdown();

}
