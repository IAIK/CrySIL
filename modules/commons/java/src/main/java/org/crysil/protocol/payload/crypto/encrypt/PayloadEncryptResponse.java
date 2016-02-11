package org.crysil.protocol.payload.crypto.encrypt;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

public class PayloadEncryptResponse extends PayloadResponse {

	/** The encrypted data. */
	protected List<List<String>> encryptedData;

	@Override
	public String getType() {
		return "encryptResponse";
	}

	/**
	 * Gets the encrypted data.
	 *
	 * @return the encrypted data
	 */
	public List<List<String>> getEncryptedData() {
		return encryptedData;
	}

	/**
	 * Sets the encrypted data.
	 *
	 * @param encryptedData
	 *            the new encrypted data
	 */
	public void setEncryptedData(List<List<String>> encryptedData) {
		this.encryptedData = encryptedData;
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
