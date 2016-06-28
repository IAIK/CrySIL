package org.crysil.protocol.payload.crypto.generatekey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.crysil.commons.KeyType;
import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.crypto.key.KeyRepresentation;

/**
 * Request for generating a key following some specifications but return a
 * wrapped (i.e. encrypted) representation of the key.
 * Only the service that has the wrapping key (encrypting key) can use the key
 * itself.
 */
public class PayloadGenerateKeyRequest extends PayloadRequest {

  /** The key type. */
  protected String              keyType;

  protected String              representation;

  protected Map<String, Object> params;

  protected AuthInfo            stickyPolicy;

  public PayloadGenerateKeyRequest(final KeyType keyType, final Map<String, Object> params,
      final KeyRepresentation representation, final AuthInfo stickyPolicy) {
    this.keyType = keyType == null ? null : keyType.name();
    this.params = new HashMap<>(params);
    this.stickyPolicy = stickyPolicy;
    this.representation = representation.name();
  }

  public KeyRepresentation getRepresentation() {
    return KeyRepresentation.valueOf(representation);
  }

  public void setRepresentation(final KeyRepresentation representation) {
    this.representation = representation.name();
  }

  public AuthInfo getStickyPolicy() {
    return stickyPolicy;
  }

  public void setStickyPolicy(final AuthInfo stickyPolicy) {
    this.stickyPolicy = stickyPolicy;
  }

  public Map<String, Object> getParams() {
    return params;
  }

  public void setParams(final Map<String, Object> params) {
    this.params = params;
  }

  /**
   * Gets the key type.
   *
   * @return the key type
   */
  public KeyType getKeyType() {
    return keyType == null ? null : KeyType.valueOf(keyType);
  }

  /**
   * Sets the key type.
   *
   * @param keyType
   *          the new key type
   */
  public void setKeyType(final KeyType keyType) {
    this.keyType = keyType.name();
  }

  @Override
  public String getType() {
    return "generateKeyRequest";
  }

  @Override
  public PayloadRequest getBlankedClone() {
    return new PayloadGenerateKeyRequest((Logger.isDebugEnabled() ? getKeyType() : null),
        (Logger.isDebugEnabled() ? params : new HashMap<String, Object>()), getRepresentation(),
        stickyPolicy);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PayloadGenerateKeyRequest other = (PayloadGenerateKeyRequest) obj;

    if (keyType == null) {
      if (other.keyType != null) {
        return false;
      }
    } else if (!keyType.equals(other.keyType)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type,keyType,representation,params,stickyPolicy});
  }
}
