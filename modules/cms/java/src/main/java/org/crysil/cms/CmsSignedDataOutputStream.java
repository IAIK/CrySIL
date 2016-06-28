package org.crysil.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.bouncycastle.cms.CMSException;

public class CmsSignedDataOutputStream extends OutputStream {

  private final OutputStream out;
  private final OutputStream       originalOutputStream;

  public CmsSignedDataOutputStream(final OutputStream out, final X509Certificate signingCert,
      final PrivateKey signingKey, final String signAlg) throws CMSException {
    this.out = CMS.wrapOutputStreamForSigning(out, signingCert, signingKey, signAlg);
    this.originalOutputStream = out;
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
    out.close();
    originalOutputStream.close();
  }

  @Override
  public String toString() {
    return out.toString();
  }

}
