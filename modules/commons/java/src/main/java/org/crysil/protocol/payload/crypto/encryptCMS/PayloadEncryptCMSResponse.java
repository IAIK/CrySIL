package org.crysil.protocol.payload.crypto.encryptCMS;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

public class PayloadEncryptCMSResponse extends PayloadResponse {

	/** The encrypted data. */
	protected List<String> encryptedCMSData;

	@Override
	public String getType() {
		return "encryptCMSResponse";
	}

	/**
	 * Gets the encrypted data.
	 *
	 * @return the encrypted data
	 */
	public List<String> getEncryptedCMSData() {
		return encryptedCMSData;
	}

	/**
	 * Sets the encrypted data.
	 *
	 * @param encryptedCMSData
	 *            the new encrypted data
	 */
	public void setEncryptedCMSData(List<String> encryptedCMSData) {
		this.encryptedCMSData = encryptedCMSData;
	}

	@Override
	public PayloadResponse getBlankedClone() {
		PayloadEncryptCMSResponse result = new PayloadEncryptCMSResponse();
		List<String> data = new ArrayList<>();
		for (String current : encryptedCMSData)
			data.add(Logger.isDebugEnabled() ? current : "*****");
		result.encryptedCMSData = data;
		return result;
	}
}
