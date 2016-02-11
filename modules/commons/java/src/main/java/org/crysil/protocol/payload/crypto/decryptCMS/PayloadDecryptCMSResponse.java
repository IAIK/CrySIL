package org.crysil.protocol.payload.crypto.decryptCMS;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

public class PayloadDecryptCMSResponse extends PayloadResponse {

	/** The plain data. */
	protected List<String> plainData;

	@Override
	public String getType() {
		return "decryptCMSResponse";
	}

	/**
	 * Gets the plain data.
	 *
	 * @return the plain data
	 */
	public List<String> getPlainData() {
		return plainData;
	}

	/**
	 * Sets the plain data.
	 *
	 * @param plainData
	 *            the new plain data
	 */
	public void setPlainData(List<String> plainData) {
		this.plainData = plainData;
	}

	@Override
	public PayloadResponse getBlankedClone() {
		PayloadDecryptCMSResponse result = new PayloadDecryptCMSResponse();
		List<String> data = new ArrayList<>();
		for (String current : plainData)
			data.add(Logger.isDebugEnabled() ? current : "*****");
		result.plainData = data;
		return result;
	}
}
