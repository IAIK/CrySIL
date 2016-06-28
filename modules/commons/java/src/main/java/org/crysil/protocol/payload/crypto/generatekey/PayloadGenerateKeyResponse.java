package org.crysil.protocol.payload.crypto.generatekey;

import java.util.Arrays;

import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.key.Key;

/**
 * Holds an encrypted (wrapped) key. Only the service which can decrypt the key
 * container can use the key.
 */
public class PayloadGenerateKeyResponse extends PayloadResponse {

  /** The encoded wrapped key. */
  protected Key key;

  public PayloadGenerateKeyResponse(final Key key) {
    this.key = key;
  }

  public PayloadGenerateKeyResponse() {
  }

  @Override
  public String getType() {
    return "generateKeyResponse";
  }

  /**
   * Gets the encoded wrapped key.
   *
   * @return the encoded wrapped key
   */
  public Key getKey() {
    return key;
  }

  /**
   * Sets the encoded wrapped key.
   *
   * @param encodedWrappedKey
   *          the new encoded wrapped key
   */
  public void setKey(final Key key) {
    this.key = key;
  }

  @Override
  public PayloadResponse getBlankedClone() {
    return new PayloadGenerateKeyResponse(getKey());
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
    final PayloadGenerateKeyResponse other = (PayloadGenerateKeyResponse) obj;
    if (key == null) {
      if (other.key != null) {
        return false;
      }
    } else if (!key.equals(other.key)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type,key});
  }
}
