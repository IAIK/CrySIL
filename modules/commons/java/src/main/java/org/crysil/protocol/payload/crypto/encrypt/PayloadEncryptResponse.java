package org.crysil.protocol.payload.crypto.encrypt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

import com.google.common.io.BaseEncoding;

public class PayloadEncryptResponse extends PayloadResponse {

	/** The encrypted data. */
	protected List<List<String>> encryptedData = new ArrayList<>();

	@Override
	public String getType() {
		return "encryptResponse";
	}

	/**
	 * Gets the encrypted data.
	 *
	 * @return the encrypted data
	 */
	public List<List<byte[]>> getEncryptedData() {
		final List<List<byte[]>> result = new ArrayList<>();
		for (final List<String> currentKey : encryptedData) {
			final List<byte[]> tmp = new ArrayList<>();
			for (final String currentData : currentKey) {
        tmp.add(BaseEncoding.base64().decode(currentData));
      }
			result.add(tmp);
		}

		return result;
	}

	/**
	 * Sets the encrypted data.
	 *
	 * @param encryptedData
	 *            the new encrypted data
	 */
	public void setEncryptedData(final List<List<byte[]>> data) {
		clearEncryptedData();

		for (final List<byte[]> currentKey : data) {
			final List<String> tmp = new ArrayList<>();
			for (final byte[] currentData : currentKey) {
        tmp.add(BaseEncoding.base64().encode(currentData));
      }
			encryptedData.add(tmp);
		}
	}

	/**
	 * clear the list of plain data
	 */
	public void clearEncryptedData() {
		encryptedData.clear();
	}

	/**
	 * add plain data to the list
	 *
	 * @param plainData
	 */
	public void addEncryptedDataPerKey(final List<byte[]> data) {
		final List<String> tmp = new ArrayList<>();

		for (final byte[] current : data) {
      tmp.add(BaseEncoding.base64().encode(current));
    }

		encryptedData.add(tmp);
	}

	@Override
	public PayloadResponse getBlankedClone() {
		final PayloadEncryptResponse result = new PayloadEncryptResponse();

		final List<List<String>> data = new ArrayList<>();
		for (final List<String> current : encryptedData) {
			final List<String> dataa = new ArrayList<>();
			for (final String currenter : current) {
        dataa.add(Logger.isDebugEnabled() ? currenter : "*****");
      }
			data.add(dataa);
		}
		result.encryptedData = data;
		return result;
	}

	@Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type,encryptedData});
  }
}
