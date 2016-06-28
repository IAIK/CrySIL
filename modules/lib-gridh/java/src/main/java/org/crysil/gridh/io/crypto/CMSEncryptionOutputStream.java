package org.crysil.gridh.io.crypto;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.bouncycastle.cms.CMSException;
import org.crysil.actor.invertedtrust.InvertedTrustActor;
import org.crysil.cms.CmsEnvelopedOutputStream;
import org.crysil.gridh.io.util.ProgressListener;
import org.crysil.logging.Logger;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

public class CMSEncryptionOutputStream extends OutputStream {

  private final CmsEnvelopedOutputStream     enveleopedStream;
  private final Set<ProgressListener<Float>> listeners;
  private long                               transferred;

  public CMSEncryptionOutputStream(final OutputStream out, final WrappedKey key, final InvertedTrustActor actor)
      throws IOException {
    try {
      this.enveleopedStream = actor.genCmsOutputStream(out, key);
    } catch (final CMSException e) {
      throw new IOException(e);
    }
    this.listeners = new HashSet<ProgressListener<Float>>();
    transferred = 0;
  }

  @Override
  public void write(final int b) throws IOException {
    transferred--;
    enveleopedStream.write(b);
    fireProgressUpdate(transferred);
  }

  @Override
  public void write(final byte[] b) throws IOException {
    transferred -= b.length;
    enveleopedStream.write(b);
    fireProgressUpdate(transferred);
  }

  @Override
  public void write(final byte[] b, final int off, final int len) throws IOException {
    transferred -= len;
    enveleopedStream.write(b, off, len);
    fireProgressUpdate(transferred);
  }

  @Override
  public void flush() throws IOException {
    enveleopedStream.flush();
  }

  @Override
  public void close() throws IOException {
    Logger.debug("closing");
    fireProgressFinished();
    enveleopedStream.close();
  }

  public void addProgressListener(final ProgressListener<Float> listener) {
    synchronized (listeners) {
      listeners.add(listener);
    }
  }

  public void removeProgressListener(final ProgressListener<Float> listener) {
    synchronized (listeners) {
      listeners.remove(listener);
    }
  }

  protected void fireProgressFinished() {
    synchronized (listeners) {
      for (final ProgressListener<Float> listener : listeners) {
        listener.finished();
      }
    }
  }

  protected void fireProgressUpdate(final float f) {
    synchronized (listeners) {
      for (final ProgressListener<Float> listener : listeners) {
        listener.updateProgress(f);
      }
    }
  }
}
