package org.crysil.protocol.payload.crypto.decryptCMS;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

import com.google.common.io.BaseEncoding;

public class PayloadDecryptCMSResponse extends PayloadResponse {

	/** The plain data. */
	protected List<String> plainData = new ArrayList<>();

	@Override
	public String getType() {
		return "decryptCMSResponse";
	}

	/**
	 * Gets the plain data.
	 *
	 * @return the plain data
	 */
	public List<byte[]> getPlainData() {
		List<byte[]> tmp = new ArrayList<>();
		for (String current : plainData)
			tmp.add(BaseEncoding.base64().decode(current));
		return tmp;
	}

	/**
	 * clear and set new data
	 *
	 * @param data
	 *            the new data
	 */
	public void setPlainData(List<byte[]> plainData) {
		clearPlainData();

		for (byte[] current : plainData)
			addPlainData(current);
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
	public void addPlainData(byte[] plainData) {
		this.plainData.add(BaseEncoding.base64().encode(plainData));
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((plainData == null) ? 0 : plainData.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PayloadDecryptCMSResponse other = (PayloadDecryptCMSResponse) obj;
		if (plainData == null) {
			if (other.plainData != null)
				return false;
		} else if (!plainData.equals(other.plainData))
			return false;
		return true;
	}
}
