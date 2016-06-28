package org.crysil.protocol.payload.status;

import java.util.Arrays;

import org.crysil.protocol.payload.PayloadResponse;

public class PayloadStatus extends PayloadResponse {

  /** The code. */
  protected int code;

  @Override
  public String getType() {
    return "status";
  }

  /**
   * Gets the code.
   *
   * @return the code
   */
  public int getCode() {
    return code;
  }

  /**
   * Sets the code.
   *
   * @param code
   *          the new code
   * @return
   */
  public PayloadStatus setCode(final int code) {
    this.code = code;
    return this;
  }

  @Override
  public PayloadResponse getBlankedClone() {
    final PayloadStatus result = new PayloadStatus();
    result.code = code;

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
    final PayloadStatus other = (PayloadStatus) obj;
    if (code != other.code) {
      return false;
    }
    return true;
  }
  @Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type,code});
  }
}
