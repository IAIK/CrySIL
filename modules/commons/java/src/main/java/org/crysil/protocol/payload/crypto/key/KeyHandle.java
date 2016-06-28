package org.crysil.protocol.payload.crypto.key;

import java.util.Arrays;

import org.crysil.logging.Logger;

/**
 * {@link Key} implementation holding a simple two-part identifier of a key that
 * is available in the CrySIL infrastructure.
 */
public class KeyHandle extends Key {

  /** The id (key slot). */
  protected String id    = "";

  /** The sub id (key identifier). */
  protected String subId = "";

  /**
   * Gets the id (key slot).
   *
   * @return the id (key slot)
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id (key slot).
   *
   * @param id
   *          the new id (key slot)
   */
  public void setId(final String id) {
    this.id = id;
  }

  /**
   * Gets the sub id.
   *
   * @return the sub id
   */
  public String getSubId() {
    return subId;
  }

  /**
   * Sets the sub id (key identifier).
   *
   * @param subId
   *          the new sub id (key identifier)
   */
  public void setSubId(final String subId) {
    this.subId = subId;
  }

  @Override
  public String getType() {
    return "handle";
  }

  @Override
  public Key getBlankedClone() {
    final KeyHandle result = new KeyHandle();
    result.id = Logger.isInfoEnabled() ? id : "*****";
    result.subId = Logger.isInfoEnabled() ? subId : "*****";

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
    final KeyHandle other = (KeyHandle) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (subId == null) {
      if (other.subId != null) {
        return false;
      }
    } else if (!subId.equals(other.subId)) {
      return false;
    }
    return true;
  }
  @Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type,id,subId});
  }
}
