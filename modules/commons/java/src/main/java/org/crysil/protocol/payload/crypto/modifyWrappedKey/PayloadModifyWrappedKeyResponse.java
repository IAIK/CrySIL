package org.crysil.protocol.payload.crypto.modifyWrappedKey;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

/**
 * Holds the modified version of the initial wrapped key.
 */
public class PayloadModifyWrappedKeyResponse extends PayloadResponse {

	/** The encoded wrapped key. */
	protected String encodedWrappedKey;

	/** The encoded x509 certificate. */
	protected String encodedX509Certificate;

	@Override
	public String getType() {
		return "modifyWrappedKeyResponse";
	}

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
	 * Gets the encoded x509 certificate.
	 *
	 * @return the encoded x509 certificate
	 */
	public String getEncodedX509Certificate() {
		return encodedX509Certificate;
	}

	/**
	 * Sets the encoded x509 certificate.
	 *
	 * @param encodedX509Certificate
	 *            the new encoded x509 certificate
	 */
	public void setEncodedX509Certificate(String encodedX509Certificate) {
		this.encodedX509Certificate = encodedX509Certificate;
	}

	@Override
	public PayloadResponse getBlankedClone() {
		PayloadModifyWrappedKeyResponse result = new PayloadModifyWrappedKeyResponse();
		result.encodedWrappedKey = Logger.isDebugEnabled() ? encodedWrappedKey : "*****";
		result.encodedX509Certificate = Logger.isDebugEnabled() ? encodedX509Certificate : "*****";

		return result;
	}
}
