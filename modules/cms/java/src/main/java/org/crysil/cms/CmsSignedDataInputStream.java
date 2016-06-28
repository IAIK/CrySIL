package org.crysil.cms;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.Collection;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedDataParser;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

public class CmsSignedDataInputStream extends InputStream {
  private final InputStream    in;
  private final CMSTypedStream signedContent;
  private boolean              closed;
  private CMSSignedDataParser  sp;
  private boolean              verified;

  public CmsSignedDataInputStream(final InputStream in) throws CMSException {
    try {
      this.sp = new CMSSignedDataParser(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build(),
          in);
    } catch (final OperatorCreationException e) {
      e.printStackTrace();

      throw new CMSException(e.getMessage(), e);
    }

    this.signedContent = sp.getSignedContent();
    this.in = signedContent.getContentStream();
    closed = false;
  }

  public boolean isValid() throws IOException {
    if (!closed) {
      throw new IOException("The input stream is still open! there may still be data left to read. "
          + "Close this stream before trying to verify signatures! After all, "
          + "how should a signature be verified, if the signed data has not been fully read???");
    }
    return verified;
  }

  private boolean verifySignatures() throws CMSException {

    final Store<X509CertificateHolder> certStore = sp.getCertificates();
    final SignerInformationStore signers = sp.getSignerInfos();

    final Collection<SignerInformation> signerInfos = signers.getSigners();

    for (final SignerInformation signer : signerInfos) {
      final Collection<X509CertificateHolder> certCollection = certStore.getMatches(signer.getSID());
      for (final X509CertificateHolder holder : certCollection) {
        try {
          if (!signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(holder))) {
            return false;
          }
        } catch (OperatorCreationException | CertificateException e) {
          throw new CMSException(e.getLocalizedMessage(), e);
        }
      }
    }
    return true;
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
    signedContent.drain();
    try {
      this.verified = verifySignatures();
    } catch (final CMSException e) {
      throw new IOException(e);
    }
    in.close();
    closed = true;
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
