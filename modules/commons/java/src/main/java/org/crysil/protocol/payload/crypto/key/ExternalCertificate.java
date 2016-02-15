package org.crysil.protocol.payload.crypto.key;

import org.crysil.logging.Logger;

import com.google.common.io.BaseEncoding;

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
	public byte[] getEncodedCertificate() {
		return BaseEncoding.base64().decode(encodedCertificate);
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
	public void setEncodedCertificate(byte[] encodedCertificate) {
		this.encodedCertificate = BaseEncoding.base64().encode(encodedCertificate);
	}

	@Override
	public Key getBlankedClone() {
		ExternalCertificate result = new ExternalCertificate();
		result.encodedCertificate = Logger.isDebugEnabled() ? encodedCertificate : "*****";

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((encodedCertificate == null) ? 0 : encodedCertificate.hashCode());
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
		ExternalCertificate other = (ExternalCertificate) obj;
		if (encodedCertificate == null) {
			if (other.encodedCertificate != null)
				return false;
		} else if (!encodedCertificate.equals(other.encodedCertificate))
			return false;
		return true;
	}
}
