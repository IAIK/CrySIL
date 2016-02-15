package org.crysil.protocol.payload.crypto.decrypt;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.key.Key;

import com.google.common.io.BaseEncoding;

public class PayloadDecryptRequest extends PayloadRequest {

	/** The decryption key. */
	protected Key decryptionKey;

	/** The algorithm. */
	protected String algorithm;

	/** The encrypted data. */
	protected List<String> encryptedData = new ArrayList<>();

	@Override
	public String getType() {
		return "decryptRequest";
	}

	/**
	 * Gets the algorithm.
	 *
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * Sets the algorithm.
	 *
	 * @param algorithm
	 *            the new algorithm
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Gets the encrypted data.
	 *
	 * @return the encrypted data
	 */
	public List<byte[]> getEncryptedData() {
		List<byte[]> tmp = new ArrayList<>();
		for (String current : encryptedData)
			tmp.add(BaseEncoding.base64().decode(current));

		return tmp;
	}

	/**
	 * clear and set new encrypted data
	 *
	 * @param encryptedData
	 *            the new encrypted data
	 */
	public void setEncryptedData(List<byte[]> encryptedData) {
		clearEncryptedData();

		for (byte[] current : encryptedData)
			addEncryptedData(current);
	}

	/**
	 * clear any encrypted data that has already been added
	 */
	public void clearEncryptedData() {
		this.encryptedData.clear();
	}

	/**
	 * add encrypted data to set
	 * 
	 * @param encryptedData
	 */
	public void addEncryptedData(byte[] encryptedData) {
		this.encryptedData.add(BaseEncoding.base64().encode(encryptedData));
	}

	/**
	 * Gets the decryption key.
	 *
	 * @return the decryption key
	 */
	public Key getDecryptionKey() {
		return decryptionKey;
	}

	/**
	 * Sets the decryption key.
	 *
	 * @param decryptionKey
	 *            the new decryption key
	 */
	public void setDecryptionKey(Key decryptionKey) {
		this.decryptionKey = decryptionKey;
	}

	@Override
	public PayloadRequest getBlankedClone() {
		PayloadDecryptRequest result = new PayloadDecryptRequest();
		List<String> data = new ArrayList<>();
		for (String current : encryptedData)
			data.add(Logger.isDebugEnabled() ? current : "*****");
		result.encryptedData = data;
		result.decryptionKey = decryptionKey.getBlankedClone();
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((decryptionKey == null) ? 0 : decryptionKey.hashCode());
		result = prime * result + ((encryptedData == null) ? 0 : encryptedData.hashCode());
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
		PayloadDecryptRequest other = (PayloadDecryptRequest) obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		if (decryptionKey == null) {
			if (other.decryptionKey != null)
				return false;
		} else if (!decryptionKey.equals(other.decryptionKey))
			return false;
		if (encryptedData == null) {
			if (other.encryptedData != null)
				return false;
		} else if (!encryptedData.equals(other.encryptedData))
			return false;
		return true;
	}
}
