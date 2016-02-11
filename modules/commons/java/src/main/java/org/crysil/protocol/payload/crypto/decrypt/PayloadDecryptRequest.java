package org.crysil.protocol.payload.crypto.decrypt;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.key.Key;

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
	public List<String> getEncryptedData() {
		return encryptedData;
	}

	/**
	 * Sets the encrypted data.
	 *
	 * @param encryptedData
	 *            the new encrypted data
	 */
	public void setEncryptedData(List<String> encryptedData) {
		this.encryptedData = encryptedData;
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
}
