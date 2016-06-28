package org.crysil.actor.storage;

import javax.crypto.SecretKey;

import org.crysil.protocol.payload.auth.AuthInfo;

public class SecretKeyContainer extends CryptoContainer {

  private static final long serialVersionUID = -8958160723881691516L;
  private final SecretKey key;

  public SecretKeyContainer(final SecretKey key) {
    this(key, null);
  }

  public SecretKeyContainer(final SecretKey key, final AuthInfo stickyPolicy) {
    super(stickyPolicy);
    this.key = key;
  }

  public SecretKey getKey() {
    return key;
  }

}
