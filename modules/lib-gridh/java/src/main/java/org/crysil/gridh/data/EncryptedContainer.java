package org.crysil.gridh.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import org.apache.commons.compress.utils.IOUtils;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

public abstract class EncryptedContainer implements Serializable {

  private static final long serialVersionUID  = -3485683833032527843L;
  public static final int   MAGIC_NUMBER      = 0xD06EF00D;
  public static final int   NUM_BYTES_FOR_INT = 4;

  public static byte[] parseHeaderAndReadWrappedKey(final InputStream src) throws IOException {
    if (!checkMagicNumber(src)) {
      throw new IOException("Invalid Magic Number!");
    }
    return readWrappedKey(src);
  }

  public static void writeHeaderAndWrappedKey(final OutputStream dst, final WrappedKey wrappedKey)
      throws IOException {
    dst.write(intToByteArray(MAGIC_NUMBER));
    dst.write(intToByteArray(wrappedKey.getEncodedWrappedKey().length));
    dst.write(wrappedKey.getEncodedWrappedKey());
  }

  protected static byte[] readWrappedKey(final InputStream src) throws IOException {
    final byte[] wrappedKey = new byte[readInt(src)];
    IOUtils.readFully(src, wrappedKey);
    return wrappedKey;
  }

  protected static int readInt(final InputStream src) throws IOException {
    final byte[] keyLen = new byte[NUM_BYTES_FOR_INT];
    IOUtils.readFully(src, keyLen);
    return ByteBuffer.wrap(keyLen).getInt();
  }

  protected static byte[] intToByteArray(final int num) {
    final byte[] bytes = new byte[NUM_BYTES_FOR_INT];
    final ByteBuffer buf = ByteBuffer.wrap(bytes);
    buf.putInt(num);
    return bytes;
  }

  public static boolean checkMagicNumber(final InputStream src) {
    try {
      final int magic_int = readInt(src);
      if (magic_int != MAGIC_NUMBER) {
        return false;
      }
    } catch (final IOException e) {
      return false;
    }
    return true;
  }
}
