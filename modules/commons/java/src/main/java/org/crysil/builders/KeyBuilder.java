package org.crysil.builders;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.crysil.protocol.payload.crypto.key.ExternalCertificate;
import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

import com.google.common.io.BaseEncoding;

public class KeyBuilder {

  public static KeyHandle buildKeyHandle(final String id, final String subId) {
    final KeyHandle tmp = new KeyHandle();
    tmp.setId(id);
    tmp.setSubId(subId);
    return tmp;
  }

  public static InternalCertificate buildInternalCertificate(final String id, final String subId,
      final String encodedCertificate) throws CertificateEncodingException, CertificateException {
    final InternalCertificate tmp = new InternalCertificate();
    tmp.setId(id);
    tmp.setSubId(subId);
    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
    tmp.setCertificate((X509Certificate) cf
        .generateCertificate(new ByteArrayInputStream(BaseEncoding.base64().decode(encodedCertificate))));
    return tmp;
  }

  public static Key buildExternalCertificate(final String base64X509Certificate)
      throws CertificateEncodingException, CertificateException {
    final ExternalCertificate tmp = new ExternalCertificate();
    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
    tmp.setCertificate((X509Certificate) cf
        .generateCertificate(new ByteArrayInputStream(BaseEncoding.base64().decode(base64X509Certificate))));
    return tmp;
  }
}
