package org.crysil.gridh.io.container;

import java.io.IOException;
import java.io.OutputStream;

import org.crysil.gridh.data.EncryptedContainer;
import org.crysil.logging.Logger;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

public class CryptContainerOutputStream extends OutputStream {
  private final OutputStream out;

  public CryptContainerOutputStream(final WrappedKey wrappedKey, final OutputStream out) throws IOException {
    this.out = out;
    EncryptedContainer.writeHeaderAndWrappedKey(out, wrappedKey);
  }

  @Override
  public void write(final int b) throws IOException {
    out.write(b);
  }

  @Override
  public void write(final byte[] b) throws IOException {
    out.write(b);
  }

  @Override
  public void write(final byte[] b, final int off, final int len) throws IOException {
    out.write(b, off, len);
  }

  @Override
  public void flush() throws IOException {
    out.flush();
  }

  @Override
  public void close() throws IOException {
    Logger.debug("closing");
    out.close();
  }
}