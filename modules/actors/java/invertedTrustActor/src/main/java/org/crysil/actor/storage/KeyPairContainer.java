package org.crysil.actor.storage;

import java.security.KeyPair;

import org.crysil.protocol.payload.auth.AuthInfo;

public class KeyPairContainer extends CryptoContainer {

  private static final long serialVersionUID = 4001778387666525627L;
  private final KeyPair     keyPair;

  public KeyPairContainer(final KeyPair keyPair) {
    this(keyPair, null);
  }

  public KeyPairContainer(final KeyPair keyPair, final AuthInfo stickyPolicy) {
    super(stickyPolicy);
    this.keyPair = keyPair;

  }

  public KeyPair getKeyPair() {
    return keyPair;
  }
}
