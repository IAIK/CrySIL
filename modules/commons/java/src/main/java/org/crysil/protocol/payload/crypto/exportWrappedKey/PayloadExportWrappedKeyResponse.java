package org.crysil.protocol.payload.crypto.exportWrappedKey;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

/**
 * Holds a world readable copy of a formerly wrapped key.
 */
public class PayloadExportWrappedKeyResponse extends PayloadResponse {

	/** The encoded private key. */
	protected String encodedPrivateKey;

	/** The encoded x509 certificate. */
	protected String encodedX509Certificate;

	@Override
	public String getType() {
		return "exportWrappedKeyResponse";
	}

	/**
	 * Gets the encoded private key.
	 *
	 * @return the encoded private key
	 */
	public String getEncodedPrivateKey() {
		return encodedPrivateKey;
	}

	/**
	 * Sets the encoded private key.
	 *
	 * @param encodedPrivateKey
	 *            the new encoded private key
	 */
	public void setEncodedPrivateKey(String encodedPrivateKey) {
		this.encodedPrivateKey = encodedPrivateKey;
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
		PayloadExportWrappedKeyResponse result = new PayloadExportWrappedKeyResponse();
		result.encodedPrivateKey = Logger.isTraceEnabled() ? encodedPrivateKey : "*****";
		result.encodedX509Certificate = Logger.isDebugEnabled() ? encodedX509Certificate : "*****";

		return result;
	}
}
