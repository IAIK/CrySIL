package org.crysil.protocol.payload.crypto.key;

import org.crysil.logging.Logger;

/**
 * {@link Key} implementation representing a simple X509 certificate that is not known to the CrySIL infrastructure.
 */
public class ExternalCertificate extends Key {

	/** The encoded certificate. */
	protected String encodedCertificate = "";

	/**
	 * Gets the encoded certificate.
	 *
	 * @return the encoded certificate
	 */
	public String getEncodedCertificate() {
		return encodedCertificate;
	}

	@Override
	public String getType() {
		return "externalCertificate";
	}

	/**
	 * Sets the encoded certificate.
	 *
	 * @param encodedCertificate
	 *            the new encoded certificate
	 */
	public void setEncodedCertificate(String encodedCertificate) {
		this.encodedCertificate = encodedCertificate;
	}

	@Override
	public Key getBlankedClone() {
		ExternalCertificate result = new ExternalCertificate();
		result.encodedCertificate = Logger.isDebugEnabled() ? encodedCertificate : "*****";

		return result;
	}
}
