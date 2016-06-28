package org.crysil.protocol.payload.crypto.exportWrappedKey;

import java.util.Arrays;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.key.Key;

/**
 * A request to decrypt a wrapped key and return the plain key.
 */
public class PayloadExportWrappedKeyRequest extends PayloadRequest {

	/** The encoded wrapped key. */
	protected String encodedWrappedKey;

	/** The decryption key. */
	protected Key decryptionKey;

	/**
	 * Gets the encoded wrapped key.
	 *
	 * @return the encoded wrapped key
	 */
	public String getEncodedWrappedKey() {
		return encodedWrappedKey;
	}

	/**
	 * Sets the encoded wrapped key.
	 *
	 * @param encodedWrappedKey
	 *            the new encoded wrapped key
	 */
	public void setEncodedWrappedKey(String encodedWrappedKey) {
		this.encodedWrappedKey = encodedWrappedKey;
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
	public String getType() {
		return "exportWrappedKeyRequest";
	}

	@Override
	public PayloadRequest getBlankedClone() {
		PayloadExportWrappedKeyRequest result = new PayloadExportWrappedKeyRequest();
		result.encodedWrappedKey = Logger.isInfoEnabled() ? encodedWrappedKey : "*****";
		if (null != decryptionKey)
			result.decryptionKey = decryptionKey.getBlankedClone();
		else
			result.decryptionKey = null;

		return result;
	}

  @Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{getType(),decryptionKey,encodedWrappedKey});
  }
}
