package org.crysil.protocol.payload.crypto.modifyWrappedKey;

import javax.security.cert.CertificateEncodingException;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

import com.google.common.io.BaseEncoding;

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
		PayloadModifyWrappedKeyResponse result = new PayloadModifyWrappedKeyResponse();
		result.encodedWrappedKey = Logger.isDebugEnabled() ? encodedWrappedKey : "*****";
		result.encodedX509Certificate = Logger.isDebugEnabled() ? encodedX509Certificate : "*****";

		return result;
	}
}
