package org.crysil.gridh.io.storage.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.crysil.gridh.io.storage.StorageInputStream;

public class LocalFileInputStream extends StorageInputStream<LocalFileURI> {

  private final FileInputStream in;
  private final long            totalSize;
  private long transferred;

  public LocalFileInputStream(final LocalFileURI uri) throws IOException {
    super(uri);

    File f;
    try {
      f = new File(new URI(uri.getSchemeURI()));
    } catch (final URISyntaxException e) {
      throw new IOException(e);
    }
    if (!f.exists()) {
      throw new FileNotFoundException("File " + uri.getSchemeURI() + " does not exists!");
    }
    totalSize = f.length();
    transferred = 0;
    in = new FileInputStream(f);
  }

  @Override
  public void close() throws IOException {
    fireProgressFinished();
    in.close();
  }

  @Override
  public int read() throws IOException {
    transferred++;
    fireProgressUpdate(((float) transferred) / ((float) totalSize));
    return in.read();
  }

  @Override
  public int read(final byte[] b) throws IOException {
    final int read = in.read(b);
    transferred += read;
    fireProgressUpdate(((float) transferred) / ((float) totalSize));
    return read;
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    final int read = in.read(b, off, len);
    transferred += read;
    fireProgressUpdate(((float) transferred) / ((float) totalSize));
    return read;
  }

  @Override
  public void fireProgressUpdate(final float f) {
    super.fireProgressUpdate(f);
    if (f == 1) {
      fireProgressFinished();
    }
  }

}
