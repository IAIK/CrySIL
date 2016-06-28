package org.crysil.gridh.io.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.crysil.gridh.io.util.ProgressListener;

public abstract class StorageInputStream<T extends StorageURI> extends InputStream {

  protected final T                          uri;

  private final Set<ProgressListener<Float>> listeners;

  public StorageInputStream(T uri) throws IOException {
    this.uri = uri;
    this.listeners = new HashSet<ProgressListener<Float>>();
  }

  @Override
  public abstract void close() throws IOException;

  @Override
  public abstract int read(byte[] b) throws IOException;

  @Override
  public abstract int read(byte[] b, int off, int len) throws IOException;

  public void addProgressListener(ProgressListener<Float> listener) {
    synchronized (listeners) {
      listeners.add(listener);
    }
  }

  public void removeProgressListener(ProgressListener<Float> listener) {
    synchronized (listeners) {
      listeners.remove(listener);
    }
  }

  protected void fireProgressUpdate(float f) {
    synchronized (listeners) {
      for (ProgressListener<Float> listener : listeners) {
        listener.updateProgress(f);
      }
    }
  }

  protected void fireProgressFinished() {
    synchronized (listeners) {
      for (ProgressListener<Float> listener : listeners) {
        listener.finished();
        ;
      }
    }
  }
}
