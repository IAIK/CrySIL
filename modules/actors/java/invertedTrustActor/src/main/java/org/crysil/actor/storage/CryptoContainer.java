package org.crysil.actor.storage;

import java.io.Serializable;
import java.util.UUID;

import org.crysil.protocol.payload.auth.AuthInfo;

public abstract class CryptoContainer implements Serializable {
  private static final long serialVersionUID = -3820136446494442180L;
  private final AuthInfo    stickyPolicy;
  private final UUID        identifier;

  public CryptoContainer() {
    this(null);
  }

  public CryptoContainer(final AuthInfo stickyPolicy) {
    this.stickyPolicy = stickyPolicy;
    identifier = UUID.randomUUID();
  }

  public AuthInfo getStickyPolicy() {
    return stickyPolicy;
  }

  public UUID getIdentifier() {
    return identifier;
  }
}
