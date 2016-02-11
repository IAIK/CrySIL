package org.crysil.protocol.payload.crypto.keydiscovery;

import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.key.Key;

public class PayloadGetKeyResponse extends PayloadResponse {

	/** The key. */
	protected Key key;

	@Override
	public String getType() {
		return "getKeyResponse";
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 *
	 * @param key
	 *            the new key
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	@Override
	public PayloadResponse getBlankedClone() {
		PayloadGetKeyResponse result = new PayloadGetKeyResponse();
		result.key = key.getBlankedClone();

		return result;
	}
}
