package org.crysil.gridh.io.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import org.crysil.decentral.concurrent.ExecutorService;

public class FileBuffer extends InputStream {

  private final InputStream     in;
  private final File            buffer;
  private final FileOutputStream      fileOut;
  private final FileInputStream fileIn;

  private long                  written, read;
  private boolean               finished;

  public FileBuffer(final InputStream in, final File buffer) throws IOException {
    written = 0;
    read = 0;
    finished = false;
    this.in = in;
    this.buffer = buffer;
    if (!buffer.createNewFile()) {
      throw new IOException("File " + buffer.getName() + " already exists!");
    }
    fileOut = new FileOutputStream(buffer);
    fileIn = new FileInputStream(buffer);
    startReading();
  }

  private void startReading() {
    ExecutorService.submitLongRunning(new Callable<Void>() {

      @Override
      public Void call() throws Exception {
        final byte[] buf = new byte[2048];
        int len = in.read(buf);
        while ((len != -1) && (!finished)) {
          fileOut.write(buf, 0, len);
          fileOut.flush();
          written += len;
          len = in.read(buf);
        }
        fileOut.close();
        finished = true;
        return null;
      }
    });
  }

  @Override
  public int read() throws IOException {
    boolean wasFinished = finished;
    while ((!wasFinished) && (written <= read)) {
      wasFinished = finished;
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
        throw new IOException("FileBuffer interruped", e);
      }
    }
    if (!wasFinished) {
      final int n = fileIn.read();
      read++;
      return n;
    } else {
      if (written > read) {
        final int n = fileIn.read();
        read++;
        return n;
      }
    }
    return -1;
  }

  @Override
  public int read(final byte b[]) throws IOException {
    if (!finished) {
      final int numBytes = b.length;
      if (written >= (read + numBytes)) {
        final int n = fileIn.read(b);
        read += n;
        return n;
      } else {
        return super.read(b);
      }
    } else {
      return super.read(b);
    }
  }

  @Override
  public void close() throws IOException {
    in.close();
    finished = true;
    fileOut.close();
    fileIn.close();
    buffer.delete();
  }

}
