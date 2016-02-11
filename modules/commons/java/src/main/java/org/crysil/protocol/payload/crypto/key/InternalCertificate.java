package org.crysil.protocol.payload.crypto.key;

import org.crysil.logging.Logger;

/**
 * {@link Key} implementation representing a simple X509 certificate that is already known to the
 * CrySIL infrastructure as a key handle but also supply the certificate.
 */
public class InternalCertificate extends KeyHandle {

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
	public String getType() {
		return "internalCertificate";
	}

	@Override
	public Key getBlankedClone() {
		InternalCertificate result = new InternalCertificate();
		result.id = Logger.isInfoEnabled() ? id : "*****";
		result.subId = Logger.isInfoEnabled() ? subId : "*****";
		result.encodedCertificate = Logger.isDebugEnabled() ? encodedCertificate : "*****";

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((encodedCertificate == null) ? 0 : encodedCertificate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		InternalCertificate other = (InternalCertificate) obj;
		if (encodedCertificate == null) {
			if (other.encodedCertificate != null)
				return false;
		} else if (!encodedCertificate.equals(other.encodedCertificate))
			return false;
		return true;
	}

}
