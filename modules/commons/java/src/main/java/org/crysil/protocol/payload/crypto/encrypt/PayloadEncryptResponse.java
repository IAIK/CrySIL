package org.crysil.protocol.payload.crypto.encrypt;

import java.util.ArrayList;
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
		List<List<byte[]>> result = new ArrayList<>();
		for (List<String> currentKey : encryptedData) {
			List<byte[]> tmp = new ArrayList<>();
			for (String currentData : currentKey)
				tmp.add(BaseEncoding.base64().decode(currentData));
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
	public void setEncryptedData(List<List<byte[]>> data) {
		clearEncryptedData();

		for (List<byte[]> currentKey : data) {
			List<String> tmp = new ArrayList<>();
			for (byte[] currentData : currentKey)
				tmp.add(BaseEncoding.base64().encode(currentData));
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
	public void addEncryptedDataPerKey(List<byte[]> data) {
		List<String> tmp = new ArrayList<>();

		for (byte[] current : data)
			tmp.add(BaseEncoding.base64().encode(current));

		encryptedData.add(tmp);
	}

	@Override
	public PayloadResponse getBlankedClone() {
		PayloadEncryptResponse result = new PayloadEncryptResponse();

		List<List<String>> data = new ArrayList<>();
		for (List<String> current : encryptedData) {
			List<String> dataa = new ArrayList<>();
			for (String currenter : current)
				dataa.add(Logger.isDebugEnabled() ? currenter : "*****");
			data.add(dataa);
		}
		result.encryptedData = data;
		return result;
	}
}
