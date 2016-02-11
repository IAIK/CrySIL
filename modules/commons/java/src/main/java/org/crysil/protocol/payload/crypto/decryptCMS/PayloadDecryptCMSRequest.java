package org.crysil.protocol.payload.crypto.decryptCMS;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.PayloadWithKey;
import org.crysil.protocol.payload.crypto.key.Key;

public class PayloadDecryptCMSRequest extends PayloadRequest implements PayloadWithKey {

	/** The decryption key. */
	protected Key decryptionKey;

	/** The encrypted data. */
	protected List<String> encryptedCMSData = new ArrayList<>();

	@Override
	public String getType() {
		return "decryptCMSRequest";
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
	public List<Key> getKeys() {
		List<Key> result = new ArrayList<>();
		result.add(getDecryptionKey());
		return result;
	}

	@Override
	public PayloadRequest getBlankedClone() {
		PayloadDecryptCMSRequest result = new PayloadDecryptCMSRequest();
		List<String> data = new ArrayList<>();
		for (String current : encryptedCMSData)
			data.add(Logger.isDebugEnabled() ? current : "*****");
		result.encryptedCMSData = data;
		result.decryptionKey = decryptionKey.getBlankedClone();
		return result;
	}

}
