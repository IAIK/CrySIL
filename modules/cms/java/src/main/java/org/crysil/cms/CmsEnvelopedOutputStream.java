package org.crysil.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.cms.CMSException;

public class CmsEnvelopedOutputStream extends OutputStream {
  private final OutputStream out;

  public CmsEnvelopedOutputStream(final OutputStream out, final ASN1ObjectIdentifier encryptionAlgorithm,
      final X509Certificate... recipientCertificates) throws CMSException {
    this.out = CMS.wrapOutputStreamforEncryption(out, encryptionAlgorithm, recipientCertificates);
  }

  @Override
  public void write(final int b) throws IOException {
    out.write(b);
  }

  @Override
  public int hashCode() {
    return out.hashCode();
  }

  @Override
  public void write(final byte[] b) throws IOException {
    out.write(b);
  }

  @Override
  public void write(final byte[] b, final int off, final int len) throws IOException {
    out.write(b, off, len);
  }

  @Override
  public boolean equals(final Object obj) {
    return out.equals(obj);
  }

  @Override
  public void flush() throws IOException {
    out.flush();
  }

  @Override
  public void close() throws IOException {
//    flush();
    out.close();
  }

  @Override
  public String toString() {
    return out.toString();
  }
}
