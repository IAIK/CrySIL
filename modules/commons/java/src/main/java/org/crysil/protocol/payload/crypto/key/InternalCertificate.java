package org.crysil.protocol.payload.crypto.key;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.crysil.logging.Logger;

import com.google.common.io.BaseEncoding;

/**
 * {@link Key} implementation representing a simple X509 certificate that is
 * already known to the
 * CrySIL infrastructure as a key handle but also supply the certificate.
 */
public class InternalCertificate extends KeyHandle {

  /** The encoded certificate. */
  protected String encodedCertificate = "";

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
   * @throws java.security.cert.CertificateEncodingException
   */
  public void setCertificate(final X509Certificate cert) throws CertificateEncodingException {
    this.encodedCertificate = BaseEncoding.base64().encode(cert.getEncoded());
  }

  @Override
  public String getType() {
    return "internalCertificate";
  }

  @Override
  public Key getBlankedClone() {
    final InternalCertificate result = new InternalCertificate();
    result.id = Logger.isInfoEnabled() ? id : "*****";
    result.subId = Logger.isInfoEnabled() ? subId : "*****";
    result.encodedCertificate = Logger.isDebugEnabled() ? encodedCertificate : "*****";

    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final InternalCertificate other = (InternalCertificate) obj;
    if (encodedCertificate == null) {
      if (other.encodedCertificate != null) {
        return false;
      }
    } else if (!encodedCertificate.equals(other.encodedCertificate)) {
      return false;
    }
    return true;
  }

}
