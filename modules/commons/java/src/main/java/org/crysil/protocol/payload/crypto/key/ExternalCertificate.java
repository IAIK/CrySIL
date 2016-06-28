package org.crysil.protocol.payload.crypto.key;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.crysil.logging.Logger;

import com.google.common.io.BaseEncoding;

/**
 * {@link Key} implementation representing a simple X509 certificate that is not
 * known to the CrySIL infrastructure.
 */
public class ExternalCertificate extends Key {

  /** The encoded certificate. */
  protected String encodedCertificate = "";

  @Override
  public String getType() {
    return "externalCertificate";
  }

  /**
   * get the certificate
   *
   * @return
   * @throws CertificateException
   */
  public X509Certificate getCertificate() throws CertificateException {
    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
   return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(BaseEncoding.base64().decode(encodedCertificate)));
  }

  /**
   * Sets the encoded certificate.
   *
   * @param encodedCertificate
   *          the new encoded certificate
   * @throws CertificateEncodingException
   * @throws javax.security.cert.CertificateEncodingException
   */
  public void setCertificate(final X509Certificate cert) throws CertificateEncodingException {
    this.encodedCertificate = BaseEncoding.base64().encode(cert.getEncoded());
  }

  @Override
  public Key getBlankedClone() {
    final ExternalCertificate result = new ExternalCertificate();
    result.encodedCertificate = Logger.isDebugEnabled() ? encodedCertificate : "*****";

    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ExternalCertificate other = (ExternalCertificate) obj;
    if (encodedCertificate == null) {
      if (other.encodedCertificate != null) {
        return false;
      }
    } else if (!encodedCertificate.equals(other.encodedCertificate)) {
      return false;
    }
    return true;
  }
  @Override
  public int hashCode() {
   return Arrays.hashCode(new Object[]{type,encodedCertificate});
  }
}
