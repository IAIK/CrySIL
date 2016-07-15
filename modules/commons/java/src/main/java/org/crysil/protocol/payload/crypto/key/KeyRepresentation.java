package org.crysil.protocol.payload.crypto.key;

public enum KeyRepresentation {

    WRAPPED,
    HANDLE,
    UNKNOWN,
    CERTIFICATE,

  ;

  public static KeyRepresentation value(final String representation) {
    final KeyRepresentation valueOf = valueOf(representation.toUpperCase());
    if (valueOf == null) {
      return UNKNOWN;
    }
    return valueOf;
  }

  public String getProtocolCompliantString() {
    return name().toLowerCase();
  }

}
