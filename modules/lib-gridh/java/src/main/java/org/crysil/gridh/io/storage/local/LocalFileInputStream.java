package org.crysil.gridh.io.storage.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.crysil.gridh.io.storage.StorageInputStream;

public class LocalFileInputStream extends StorageInputStream<LocalFileURI> {

  private FileInputStream in;
  private long            totalSize, transferred;

  public LocalFileInputStream(LocalFileURI uri) throws IOException {
    super(uri);

    File f;
    try {
      f = new File(new URI(uri.getSchemeURI()));
    } catch (URISyntaxException e) {
      throw new IOException(e);
    }
    if (!f.exists())
      throw new FileNotFoundException("File " + uri.getSchemeURI() + " does not exists!");
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
  public int read(byte[] b) throws IOException {
    final int read = in.read(b);
    transferred += read;
    fireProgressUpdate(((float) transferred) / ((float) totalSize));
    return read;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    final int read = in.read(b, off, len);
    transferred += read;
    fireProgressUpdate(((float) transferred) / ((float) totalSize));
    return read;
  }

  public void fireProgressUpdate(float f) {
    super.fireProgressUpdate(f);
    if (f == 1)
      fireProgressFinished();
  }

}
