package org.crysil.protocol.payload.crypto.key;

import org.crysil.logging.Logger;

import com.google.common.io.BaseEncoding;

/**
 * {@link Key} implementation representing an encrypted key that is only readable by a service
 * that has the appropriate information on how to decrypt the key prior to using it.
 */
public class WrappedKey extends Key {

	/** The encoded wrapped key. */
	protected String encodedWrappedKey = "";

	/**
	 * Gets the encoded wrapped key.
	 *
	 * @return the encoded wrapped key
	 */
	public byte[] getEncodedWrappedKey() {
		return BaseEncoding.base64().decode(encodedWrappedKey);
	}

	/**
	 * Sets the encoded wrapped key.
	 *
	 * @param encodedWrappedKey
	 *            the new encoded wrapped key
	 */
	public void setEncodedWrappedKey(byte[] encodedWrappedKey) {
		this.encodedWrappedKey = BaseEncoding.base64().encode(encodedWrappedKey);
	}

	@Override
	public String getType() {
		return "wrappedKey";
	}

	@Override
	public Key getBlankedClone() {
		WrappedKey result = new WrappedKey();
		result.encodedWrappedKey = Logger.isInfoEnabled() ? encodedWrappedKey : "*****";

		return result;
	}
}
