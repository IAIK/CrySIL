package org.crysil.cms;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;

import org.bouncycastle.cms.CMSException;

public class CmsEnvelopedInputStream extends InputStream {
  private final InputStream in;

  public CmsEnvelopedInputStream(final InputStream in, final PrivateKey decryptionKey) throws CMSException {
    this.in = CMS.wrapInputStreamforDecryption(in, decryptionKey);
  }

  public CmsEnvelopedInputStream(final InputStream in, final ContentEncryptionKeyManualDecryptor decryptor)
      throws CMSException {
    this.in = CMS.wrapInputStreamforManualDecryption(in, decryptor);
  }

  @Override
  public int read() throws IOException {
    return in.read();
  }

  @Override
  public int hashCode() {
    return in.hashCode();
  }

  @Override
  public int read(final byte[] b) throws IOException {
    return in.read(b);
  }

  @Override
  public boolean equals(final Object obj) {
    return in.equals(obj);
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    return in.read(b, off, len);
  }

  @Override
  public long skip(final long n) throws IOException {
    return in.skip(n);
  }

  @Override
  public String toString() {
    return in.toString();
  }

  @Override
  public int available() throws IOException {
    return in.available();
  }

  @Override
  public void close() throws IOException {
    in.close();
  }

  @Override
  public void mark(final int readlimit) {
    in.mark(readlimit);
  }

  @Override
  public void reset() throws IOException {
    in.reset();
  }

  @Override
  public boolean markSupported() {
    return in.markSupported();
  }

}
