package org.crysil.protocol.payload.crypto.exportWrappedKey;

import com.google.common.io.BaseEncoding;
import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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
		PayloadExportWrappedKeyResponse result = new PayloadExportWrappedKeyResponse();
		result.encodedPrivateKey = Logger.isTraceEnabled() ? encodedPrivateKey : "*****";
		result.encodedX509Certificate = Logger.isDebugEnabled() ? encodedX509Certificate : "*****";

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((encodedPrivateKey == null) ? 0 : encodedPrivateKey.hashCode());
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
		PayloadExportWrappedKeyResponse other = (PayloadExportWrappedKeyResponse) obj;
		if (encodedPrivateKey == null) {
			if (other.encodedPrivateKey != null)
				return false;
		} else if (!encodedPrivateKey.equals(other.encodedPrivateKey))
			return false;
		if (encodedX509Certificate == null) {
			if (other.encodedX509Certificate != null)
				return false;
		} else if (!encodedX509Certificate.equals(other.encodedX509Certificate))
			return false;
		return true;
	}
}
