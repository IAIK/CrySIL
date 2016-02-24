package org.crysil.protocol.payload.crypto.generatekey;

import javax.security.cert.CertificateEncodingException;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

import com.google.common.io.BaseEncoding;

public class PayloadGenerateU2FKeyResponse extends PayloadResponse {

	/** The encoded random. */
	protected String encodedRandom;

	/** The encoded wrapped key. */
	protected String encodedWrappedKey;

	/** The encoded x509 certificate. */
	protected String encodedX509Certificate;

	@Override
	public String getType() {
		return "generateU2FKeyResponse";
	}

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

	/**
	 * Gets the encoded random.
	 *
	 * @return the encoded random
	 */
	public byte[] getEncodedRandom() {
		return BaseEncoding.base64().decode(encodedRandom);
	}

	/**
	 * Sets the encoded random.
	 *
	 * @param encodedRandom
	 *            the new encoded random
	 */
	public void setEncodedRandom(byte[] encodedRandom) {
		this.encodedRandom = BaseEncoding.base64().encode(encodedRandom);
	}

	/**
	 * get the certificate
	 * 
	 * @return
	 * @throws CertificateException
	 */
	public X509Certificate getCertificate() throws CertificateException {
		return X509Certificate.getInstance(BaseEncoding.base64().decode(encodedX509Certificate));
	}

	/**
	 * Sets the encoded certificate.
	 *
	 * @param encodedCertificate
	 *            the new encoded certificate
	 * @throws CertificateEncodingException
	 * @throws javax.security.cert.CertificateEncodingException
	 */
	public void setCertificate(X509Certificate cert) throws CertificateEncodingException {
		this.encodedX509Certificate = BaseEncoding.base64().encode(cert.getEncoded());
	}

	@Override
	public PayloadResponse getBlankedClone() {
		PayloadGenerateU2FKeyResponse result = new PayloadGenerateU2FKeyResponse();
		result.encodedWrappedKey = Logger.isDebugEnabled() ? encodedWrappedKey : "*****";
		result.encodedRandom = Logger.isDebugEnabled() ? encodedRandom : "*****";
		result.encodedX509Certificate = Logger.isDebugEnabled() ? encodedX509Certificate : "*****";
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((encodedWrappedKey == null) ? 0 : encodedWrappedKey.hashCode());
		result = prime * result + ((encodedRandom == null) ? 0 : encodedRandom.hashCode());
		result = prime * result + ((encodedX509Certificate == null) ? 0 : encodedX509Certificate.hashCode());
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
		PayloadGenerateU2FKeyResponse other = (PayloadGenerateU2FKeyResponse) obj;
		if (encodedWrappedKey == null) {
			if (other.encodedWrappedKey != null)
				return false;
		} else if (!encodedWrappedKey.equals(other.encodedWrappedKey))
			return false;
		if (encodedRandom == null) {
			if (other.encodedRandom != null)
				return false;
		} else if (!encodedRandom.equals(other.encodedRandom))
			return false;
		if (encodedX509Certificate == null) {
			if (other.encodedX509Certificate != null)
				return false;
		} else if (!encodedX509Certificate.equals(other.encodedX509Certificate))
			return false;
		return true;
	}
}
