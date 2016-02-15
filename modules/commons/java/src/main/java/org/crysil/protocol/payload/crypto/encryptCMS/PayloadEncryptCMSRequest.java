package org.crysil.protocol.payload.crypto.encryptCMS;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.PayloadWithKey;
import org.crysil.protocol.payload.crypto.key.Key;

import com.google.common.io.BaseEncoding;

public class PayloadEncryptCMSRequest extends PayloadRequest implements PayloadWithKey {

	/** The encryption keys. */
	protected List<Key> encryptionKeys = new ArrayList<>();

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
		clearEncryptionKeys();

		for (Key current : encryptionKeys)
			addEncryptionKey(current);
	}

	/**
	 * clear the list of encryption keys
	 */
	public void clearEncryptionKeys() {
		this.encryptionKeys.clear();
	}

	/**
	 * add a new key to the list
	 * 
	 * @param encryptionKey
	 */
	public void addEncryptionKey(Key encryptionKey) {
		this.encryptionKeys.add(encryptionKey);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((encryptionKeys == null) ? 0 : encryptionKeys.hashCode());
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
		PayloadEncryptCMSRequest other = (PayloadEncryptCMSRequest) obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		if (encryptionKeys == null) {
			if (other.encryptionKeys != null)
				return false;
		} else if (!encryptionKeys.equals(other.encryptionKeys))
			return false;
		if (plainData == null) {
			if (other.plainData != null)
				return false;
		} else if (!plainData.equals(other.plainData))
			return false;
		return true;
	}
}
