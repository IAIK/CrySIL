package org.crysil.protocol.payload.crypto.generatekey;

import com.google.common.io.BaseEncoding;
import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Holds an encrypted (wrapped) key. Only the service which can decrypt the key container can use the key.
 */
public class PayloadGenerateWrappedKeyResponse extends PayloadResponse {

	/** The encoded wrapped key. */
	protected String encodedWrappedKey;

	/** The encoded x509 certificate. */
	protected String encodedX509Certificate;

	@Override
	public String getType() {
		return "generateWrappedKeyResponse";
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
	 * get the certificate
	 *
	 * @return
	 * @throws CertificateException
	 */
	public X509Certificate getCertificate() throws CertificateException {
		final CertificateFactory cf = CertificateFactory.getInstance("X.509");
		return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(BaseEncoding.base64().decode(encodedX509Certificate)));
	}

	/**
	 * Sets the encoded certificate.
	 *
	 * @param encodedCertificate
	 *            the new encoded certificate
	 * @throws CertificateEncodingException
	 * @throws java.security.cert.CertificateEncodingException
	 */
	public void setCertificate(X509Certificate cert) throws CertificateEncodingException {
		this.encodedX509Certificate = BaseEncoding.base64().encode(cert.getEncoded());
	}

	@Override
	public PayloadResponse getBlankedClone() {
		PayloadGenerateWrappedKeyResponse result = new PayloadGenerateWrappedKeyResponse();
		result.encodedWrappedKey = Logger.isDebugEnabled() ? encodedWrappedKey : "*****";
		result.encodedX509Certificate = Logger.isDebugEnabled() ? encodedX509Certificate : "*****";

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((encodedWrappedKey == null) ? 0 : encodedWrappedKey.hashCode());
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
		PayloadGenerateWrappedKeyResponse other = (PayloadGenerateWrappedKeyResponse) obj;
		if (encodedWrappedKey == null) {
			if (other.encodedWrappedKey != null)
				return false;
		} else if (!encodedWrappedKey.equals(other.encodedWrappedKey))
			return false;
		if (encodedX509Certificate == null) {
			if (other.encodedX509Certificate != null)
				return false;
		} else if (!encodedX509Certificate.equals(other.encodedX509Certificate))
			return false;
		return true;
	}
}
