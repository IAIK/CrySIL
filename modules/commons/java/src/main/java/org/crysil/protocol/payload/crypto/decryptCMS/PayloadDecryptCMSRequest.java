package org.crysil.protocol.payload.crypto.decryptCMS;

import java.util.ArrayList;
import java.util.List;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.PayloadWithKey;
import org.crysil.protocol.payload.crypto.key.Key;

import com.google.common.io.BaseEncoding;

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
	public List<byte[]> getEncryptedCMSData() {
		List<byte[]> tmp = new ArrayList<>();
		for (String current : encryptedCMSData)
			tmp.add(BaseEncoding.base64().decode(current));

		return tmp;
	}

	/**
	 * Sets the encrypted data.
	 *
	 * @param encryptedCMSData
	 *            the new encrypted data
	 */
	public void setEncryptedCMSData(List<byte[]> data) {
		clearEncryptedData();

		for (byte[] current : data)
			addEncryptedData(current);
	}

	/**
	 * clear any encrypted data that has already been added
	 */
	public void clearEncryptedData() {
		this.encryptedCMSData.clear();
	}

	/**
	 * add encrypted data to set
	 * 
	 * @param encryptedData
	 */
	public void addEncryptedData(byte[] data) {
		this.encryptedCMSData.add(BaseEncoding.base64().encode(data));
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((decryptionKey == null) ? 0 : decryptionKey.hashCode());
		result = prime * result + ((encryptedCMSData == null) ? 0 : encryptedCMSData.hashCode());
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
		PayloadDecryptCMSRequest other = (PayloadDecryptCMSRequest) obj;
		if (decryptionKey == null) {
			if (other.decryptionKey != null)
				return false;
		} else if (!decryptionKey.equals(other.decryptionKey))
			return false;
		if (encryptedCMSData == null) {
			if (other.encryptedCMSData != null)
				return false;
		} else if (!encryptedCMSData.equals(other.encryptedCMSData))
			return false;
		return true;
	}
}
