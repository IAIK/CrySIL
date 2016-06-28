package org.crysil.gridh.io.storage.local;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.crysil.gridh.io.storage.StorageOutputStream;

public class LocalFileOutputStream extends StorageOutputStream<LocalFileURI> {

  private final FileOutputStream out;
  private final LocalFileURI     uri;
  private long                   transferred;

  public LocalFileOutputStream(String fileName) throws FileNotFoundException {
    out = new FileOutputStream(fileName);
    uri = new LocalFileURI(LocalFileURI.PREFIX_FILE + fileName.replace(File.separatorChar, '/'));
    transferred = 0;
  }

  @Override
  public LocalFileURI getUri() throws IllegalStateException {
    return uri;
  }

  @Override
  public void write(int b) throws IOException {
    transferred--;
    out.write(b);
    fireProgressUpdate(transferred);
  }

  @Override
  public void write(byte[] b) throws IOException {
    transferred -= b.length;
    out.write(b);
    fireProgressUpdate(transferred);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    transferred -= len;
    out.write(b, off, len);
    fireProgressUpdate(transferred);
  }

  @Override
  public void flush() throws IOException {
    out.flush();
  }

  @Override
  public void close() throws IOException {
    fireProgressFinished();
    out.close();
  }

  public void fireProgressUpdate(float f) {
    super.fireProgressUpdate(f);
    if (f == 1)
      fireProgressFinished();
  }

  public static String getFriendlyName() {
    return "Local Storage";
  }
}
