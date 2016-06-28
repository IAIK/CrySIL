package org.crysil.gridh.io.container;

import java.io.IOException;
import java.io.InputStream;

import org.crysil.gridh.data.EncryptedContainer;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

/**
 * Reads an {@link EncryptedContainer} from an {@link InputStream}. The
 * underlying stream must be set to a position
 * where the header of the EncryptedContainer can be read in.
 *
 * @author Bernd Prünster
 *
 */
public class CryptContainerInputStream extends InputStream {

  private final WrappedKey  wrappedKey;
  private final InputStream in;

  /**
   * Created a new CryptContainerInputStream from the specified
   * {@link InputStream}. Checks the header and tries to read
   * out the container's {@link WrappedKey}
   *
   * @param src
   *          the stream to read from
   * @throws IOException
   *           if the header check fails and/or the wrapped key cannot be read
   */
  public CryptContainerInputStream(final InputStream src) throws IOException {
    this.wrappedKey = new WrappedKey();
    wrappedKey.setEncodedWrappedKey(EncryptedContainer.parseHeaderAndReadWrappedKey(src));
    this.in = src;
  }

  @Override
  public int read() throws IOException {
    return in.read();
  }

  @Override
  public int read(final byte[] b) throws IOException {
    return in.read(b);
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    return in.read(b, off, len);
  }

  @Override
  public void close() throws IOException {
    in.close();
  }

  /**
   * returns this container's wrapped key
   *
   * @return the wrapped key read from the underlying input stream
   */
  public WrappedKey getWrappedKey() {
    return wrappedKey;
  }
}