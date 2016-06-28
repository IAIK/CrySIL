package org.crysil.gridh.io.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.crysil.gridh.GridhAPI;
import org.crysil.gridh.exceptions.irrecoverable.IrrecoverableGridhException;
import org.crysil.gridh.io.util.ProgressListener;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

public class CMSDecryptionInputStream extends InputStream {

  private InputStream                        envStream;

  private final Set<ProgressListener<Float>> listeners;
  private long                               transferred;

  public CMSDecryptionInputStream(final InputStream in, final WrappedKey wrappedKey, final GridhAPI gridh,
      final String destination) throws IOException {
    try {
      this.envStream = gridh.setupDecryptionStream(in, destination, wrappedKey);
    } catch (final IrrecoverableGridhException e) {
      throw new IOException(e);
    }
    this.listeners = new HashSet<>();
    transferred = 0;
  }

  @Override
  public int read() throws IOException {
    transferred--;
    final int read = envStream.read();
    fireProgressUpdate(transferred);
    return read;
  }

  @Override
  public int read(final byte[] b) throws IOException {
    final int read = envStream.read(b);
    transferred -= read;
    fireProgressUpdate(transferred);
    return read;
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    final int read = envStream.read(b, off, len);
    transferred -= read;
    fireProgressUpdate(transferred);
    return read;
  }

  @Override
  public void close() throws IOException {
    fireProgressFinished();
    envStream.close();
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

  protected void fireProgressUpdate(final float f) {
    synchronized (listeners) {
      for (final ProgressListener<Float> listener : listeners) {
        listener.updateProgress(f);
      }
    }
  }

  protected void fireProgressFinished() {
    synchronized (listeners) {
      for (final ProgressListener<Float> listener : listeners) {
        listener.finished();
        ;
      }
    }
  }
}
