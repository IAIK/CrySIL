package org.crysil.protocol.payload.crypto.decrypt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

import com.google.common.io.BaseEncoding;

public class PayloadDecryptResponse extends PayloadResponse {

  /** The plain data. */
  protected List<String> plainData = new ArrayList<>();

  @Override
  public String getType() {
    return "decryptResponse";
  }

  /**
   * Gets the plain data.
   *
   * @return the plain data
   */
  public List<byte[]> getPlainData() {
    final List<byte[]> tmp = new ArrayList<>();
    for (final String current : plainData) {
      tmp.add(BaseEncoding.base64().decode(current));
    }
    return tmp;
  }

  /**
   * clear and set new data
   *
   * @param data
   *          the new data
   */
  public void setPlainData(final List<byte[]> plainData) {
    clearPlainData();

    for (final byte[] current : plainData) {
      addPlainData(current);
    }
  }

  /**
   * clear any encrypted data that has already been added
   */
  public void clearPlainData() {
    this.plainData.clear();
  }

  /**
   * add data to set
   *
   * @param data
   */
  public void addPlainData(final byte[] plainData) {
    this.plainData.add(BaseEncoding.base64().encode(plainData));
  }

  @Override
  public PayloadResponse getBlankedClone() {
    final PayloadDecryptResponse result = new PayloadDecryptResponse();
    final List<String> data = new ArrayList<>();
    for (final String current : plainData) {
      data.add(Logger.isDebugEnabled() ? current : "*****");
    }
    result.plainData = data;
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
    final PayloadDecryptResponse other = (PayloadDecryptResponse) obj;
    if (plainData == null) {
      if (other.plainData != null) {
        return false;
      }
    } else if (!plainData.equals(other.plainData)) {
      return false;
    }
    return true;
  }
  @Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type,plainData});
  }
}
