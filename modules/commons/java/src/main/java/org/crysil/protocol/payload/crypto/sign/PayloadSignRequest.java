package org.crysil.protocol.payload.crypto.sign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.PayloadWithKey;
import org.crysil.protocol.payload.crypto.key.Key;

import com.google.common.io.BaseEncoding;

public class PayloadSignRequest extends PayloadRequest implements PayloadWithKey {

  /** The signature key. */
  protected Key          signatureKey;

  /** The algorithm. */
  protected String       algorithm;

  /** The hashes to be signed. */
  protected List<String> hashesToBeSigned = new ArrayList<>();

  @Override
  public String getType() {
    return "signRequest";
  }

  /**
   * Gets the algorithm.
   *
   * @return the algorithm
   */
  public String getAlgorithm() {
    return algorithm;
  }

  /**
   * Sets the algorithm.
   *
   * @param algorithm
   *          the new algorithm
   */
  public void setAlgorithm(final String algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * Gets the hashes to be signed.
   *
   * @return the hashes to be signed
   */
  public List<byte[]> getHashesToBeSigned() {
    final List<byte[]> tmp = new ArrayList<>();

    for (final String current : hashesToBeSigned) {
      tmp.add(BaseEncoding.base64().decode(current));
    }

    return tmp;
  }

  /**
   * Sets the hashes to be signed.
   *
   * @param hashesToBeSigned
   *          the new hashes to be signed
   */
  public void setHashesToBeSigned(final List<byte[]> hashesToBeSigned) {
    clearHashesToBeSigned();

    for (final byte[] current : hashesToBeSigned) {
      addHashToBeSigned(current);
    }
  }

  /**
   * clear list of hashes
   */
  public void clearHashesToBeSigned() {
    this.hashesToBeSigned.clear();
  }

  /**
   * add another hash
   *
   * @param hash
   */
  public void addHashToBeSigned(final byte[] hash) {
    hashesToBeSigned.add(BaseEncoding.base64().encode(hash));
  }

  /**
   * Gets the signature key.
   *
   * @return the signature key
   */
  public Key getSignatureKey() {
    return signatureKey;
  }

  /**
   * Sets the signature key.
   *
   * @param signatureKey
   *          the new signature key
   */
  public void setSignatureKey(final Key signatureKey) {
    this.signatureKey = signatureKey;
  }

  @Override
  public List<Key> getKeys() {
    final List<Key> result = new ArrayList<>();
    result.add(getSignatureKey());
    return result;
  }

  @Override
  public PayloadRequest getBlankedClone() {
    final PayloadSignRequest result = new PayloadSignRequest();
    result.algorithm = algorithm;
    result.hashesToBeSigned = Logger.isInfoEnabled() ? hashesToBeSigned : Arrays.asList(new String[] {
        hashesToBeSigned.size() + " blinded hashes" });
    result.signatureKey = signatureKey.getBlankedClone();

    return result;
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
    final PayloadSignRequest other = (PayloadSignRequest) obj;
    if (algorithm == null) {
      if (other.algorithm != null) {
        return false;
      }
    } else if (!algorithm.equals(other.algorithm)) {
      return false;
    }
    if (hashesToBeSigned == null) {
      if (other.hashesToBeSigned != null) {
        return false;
      }
    } else if (!hashesToBeSigned.equals(other.hashesToBeSigned)) {
      return false;
    }
    if (signatureKey == null) {
      if (other.signatureKey != null) {
        return false;
      }
    } else if (!signatureKey.equals(other.signatureKey)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type,algorithm,hashesToBeSigned,signatureKey});
  }
}
