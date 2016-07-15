package org.crysil.protocol.payload.crypto.keydiscovery;

import java.util.Arrays;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.key.KeyRepresentation;

/**
 * This request lets a client ask for a list of keys available at a CrySIL key
 * service.
 */
public class PayloadDiscoverKeysRequest extends PayloadRequest {

  /** The representation. */
  protected String representation;

  @Override
  public String getType() {
    return "discoverKeysRequest";
  }

  /**
   * Gets the representation.
   *
   * @return the representation
   */
  public KeyRepresentation getRepresentation() {
    return KeyRepresentation.value(representation);
  }

  /**
   * Sets the representation.
   *
   * @param representation
   *          the new representation
   */
  public void setRepresentation(final KeyRepresentation representation) {
    this.representation = representation == null ? KeyRepresentation.UNKNOWN.getProtocolCompliantString() : representation.getProtocolCompliantString();
  }

  @Override
  public PayloadRequest getBlankedClone() {
    final PayloadDiscoverKeysRequest result = new PayloadDiscoverKeysRequest();
    result.representation = Logger.isDebugEnabled() ? representation : "*****";
    return result;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {
        type,
        representation });
  }
}
