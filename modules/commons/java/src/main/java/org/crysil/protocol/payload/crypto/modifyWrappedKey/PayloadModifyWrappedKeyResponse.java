package org.crysil.protocol.payload.crypto.modifyWrappedKey;

import com.google.common.io.BaseEncoding;
import org.crysil.logging.Logger;
import org.crysil.protocol.payload.PayloadResponse;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

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
	public void setEncodedWrappedKey(final String encodedWrappedKey) {
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
	public void setCertificate(final X509Certificate cert) throws CertificateEncodingException {
		this.encodedX509Certificate = BaseEncoding.base64().encode(cert.getEncoded());
	}

	@Override
	public PayloadResponse getBlankedClone() {
		final PayloadModifyWrappedKeyResponse result = new PayloadModifyWrappedKeyResponse();
		result.encodedWrappedKey = Logger.isDebugEnabled() ? encodedWrappedKey : "*****";
		result.encodedX509Certificate = Logger.isDebugEnabled() ? encodedX509Certificate : "*****";

		return result;
	}

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[]{getType(),encodedWrappedKey,encodedX509Certificate});
  }
}
