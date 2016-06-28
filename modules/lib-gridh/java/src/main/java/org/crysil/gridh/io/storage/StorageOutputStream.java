package org.crysil.gridh.io.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.crysil.gridh.io.util.ProgressListener;

public abstract class StorageOutputStream<T extends StorageURI> extends OutputStream {

  private final Set<ProgressListener<Float>> listeners;

  public StorageOutputStream() {
    this.listeners = new HashSet<ProgressListener<Float>>();
  }

  public abstract T getUri() throws IllegalStateException;

  @Override
  public abstract void write(byte[] b) throws IOException;

  @Override
  public abstract void write(byte[] b, int off, int len) throws IOException;

  @Override
  public abstract void flush() throws IOException;

  @Override
  public abstract void close() throws IOException;

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

  public static String getFriendlyName() {
    return "Abstract Storage";
  }
}
