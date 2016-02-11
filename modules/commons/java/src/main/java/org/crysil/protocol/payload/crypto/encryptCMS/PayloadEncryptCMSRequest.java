package org.crysil.protocol.payload.crypto.encryptCMS;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.PayloadWithKey;
import org.crysil.protocol.payload.crypto.key.Key;

public class PayloadEncryptCMSRequest extends PayloadRequest implements PayloadWithKey {

	/** The encryption keys. */
	protected List<Key> encryptionKeys;

	/** The algorithm. */
	protected String algorithm;

	/** The plain data. */
	protected List<String> plainData = new ArrayList<>();

	@Override
	public String getType() {
		return "encryptCMSRequest";
	}

	/**
	 * Gets the encryption keys.
	 *
	 * @return the encryption keys
	 */
	public List<Key> getEncryptionKeys() {
		return encryptionKeys;
	}

	/**
	 * Sets the encryption keys.
	 *
	 * @param encryptionKeys
	 *            the new encryption keys
	 */
	public void setEncryptionKeys(List<Key> encryptionKeys) {
		this.encryptionKeys = encryptionKeys;
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
	public List<Key> getKeys() {
		return getEncryptionKeys();
	}

	@Override
	public PayloadRequest getBlankedClone() {
		PayloadEncryptCMSRequest result = new PayloadEncryptCMSRequest();
		List<Key> keys = new ArrayList<>();
		for (Key current : encryptionKeys)
			keys.add(current.getBlankedClone());
		result.encryptionKeys = keys;
		result.algorithm = Logger.isDebugEnabled() ? algorithm : "*****";
		List<String> data = new ArrayList<>();
		for (String current : plainData)
			data.add(Logger.isDebugEnabled() ? current : "*****");
		result.plainData = data;

		return result;
	}
}
