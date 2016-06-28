package org.crysil.protocol.payload.crypto.encryptCMS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

import com.google.common.io.BaseEncoding;

public class PayloadEncryptCMSResponse extends PayloadResponse {

	/** The encrypted data. */
	protected List<String> encryptedCMSData = new ArrayList<>();

	@Override
	public String getType() {
		return "encryptCMSResponse";
	}

	/**
	 * Gets the encrypted data.
	 *
	 * @return the encrypted data
	 */
	public List<byte[]> getEncryptedCMSData() {
		final List<byte[]> tmp = new ArrayList<>();
		for (final String current : encryptedCMSData) {
      tmp.add(BaseEncoding.base64().decode(current));
    }

		return tmp;
	}

	/**
	 * Sets the encrypted data.
	 *
	 * @param encryptedCMSData
	 *            the new encrypted data
	 */
	public void setEncryptedCMSData(final List<byte[]> data) {
		clearEncryptedData();

		for (final byte[] current : data) {
      addEncryptedData(current);
    }
	}

	/**
	 * clear any encrypted data that has already been added
	 */
	public void clearEncryptedData() {
		this.encryptedCMSData.clear();
	}

	/**
	 * add encrypted data to set
	 *
	 * @param encryptedData
	 */
	public void addEncryptedData(final byte[] data) {
		this.encryptedCMSData.add(BaseEncoding.base64().encode(data));
	}

	@Override
	public PayloadResponse getBlankedClone() {
		final PayloadEncryptCMSResponse result = new PayloadEncryptCMSResponse();
		final List<String> data = new ArrayList<>();
		for (final String current : encryptedCMSData) {
      data.add(Logger.isDebugEnabled() ? current : "*****");
    }
		result.encryptedCMSData = data;
		return result;
	}

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[]{getType(),encryptedCMSData});
  }
}
