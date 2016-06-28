package org.crysil.commons;

import java.util.Map;

public enum KeyType {

    RSA("keySize"),
    ECDSA("keySize"),
    AES("keySize"),

  ;

  private String[] mandatoryParams;

  private KeyType(final String... mandatoryParams) {
    this.mandatoryParams = mandatoryParams;
  }

  public boolean hasAllRequiredParams(final Map<String, Object> params) {
    for (final String param : mandatoryParams) {
      if (!params.containsKey(param)) {
        return false;
      }
    }
    return true;
  }
}
