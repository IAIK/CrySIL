package org.crysil.gridh.io.container.dropfile.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.crysil.gridh.data.EncryptedContainer;
import org.crysil.gridh.io.container.CryptContainerInputStream;
import org.crysil.gridh.io.container.CryptContainerOutputStream;
import org.crysil.gridh.test.TestUtils;
import org.crysil.protocol.payload.crypto.key.WrappedKey;
import org.testng.annotations.Test;

public class ContainerIOTest extends EncryptedContainer {

  private static byte[] createContainer(final WrappedKey wk, final byte[] payload) {
    final byte[] reference = new byte[EncryptedContainer.NUM_BYTES_FOR_INT
        + EncryptedContainer.NUM_BYTES_FOR_INT + wk.getEncodedWrappedKey().length + payload.length];
    final byte[] magic_num = EncryptedContainer.intToByteArray(EncryptedContainer.MAGIC_NUMBER);
    System.arraycopy(magic_num, 0, reference, 0, magic_num.length);

    final byte[] wk_len = intToByteArray(wk.getEncodedWrappedKey().length);
    System.arraycopy(wk_len, 0, reference, magic_num.length, EncryptedContainer.NUM_BYTES_FOR_INT);
    System.arraycopy(wk.getEncodedWrappedKey(), 0, reference, NUM_BYTES_FOR_INT + NUM_BYTES_FOR_INT,
        wk.getEncodedWrappedKey().length);
    System.arraycopy(payload, 0, reference, EncryptedContainer.NUM_BYTES_FOR_INT
        + EncryptedContainer.NUM_BYTES_FOR_INT + wk.getEncodedWrappedKey().length, payload.length);
    return reference;
  }

  @Test(invocationCount = 10)
  public void testWriteToStream() throws IOException {
    final WrappedKey wk = new WrappedKey();
    wk.setEncodedWrappedKey(TestUtils.genRandom());
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final CryptContainerOutputStream cryptContainerOutputStream = new CryptContainerOutputStream(wk, out);
    final byte[] payload = TestUtils.genRandom();
    IOUtils.write(payload, cryptContainerOutputStream);
    cryptContainerOutputStream.close();

    final byte[] written = out.toByteArray();
    final byte[] reference = createContainer(wk, payload);
    assertEquals(reference.length, written.length);
    assertEquals(written, reference);
  }

  @Test(invocationCount = 10)
  public void testMagicNumberInvalid() {
    final ByteArrayInputStream bogus = new ByteArrayInputStream(TestUtils.genRandom());
    assertFalse(checkMagicNumber(bogus));
  }

  @Test(invocationCount = 10)
  public void testByteToInt() throws IOException {
    final int foo = new Random().nextInt();
    assertEquals(readInt(new ByteArrayInputStream(intToByteArray(foo))), foo);
  }

  @Test
  public void testCheckMagicNumber() {
    final ByteArrayInputStream correct = new ByteArrayInputStream(intToByteArray(MAGIC_NUMBER));
    assertTrue(checkMagicNumber(correct));
  }

  @Test(invocationCount = 10)
  public void testReadFromStream() throws IOException {
    final byte[] payload = TestUtils.genRandom();
    final WrappedKey wk = new WrappedKey();
    wk.setEncodedWrappedKey(payload);
    final byte[] container = createContainer(wk, payload);
    final ByteArrayInputStream bin = new ByteArrayInputStream(container);
    final CryptContainerInputStream ccIn = new CryptContainerInputStream(bin);
    assertEquals(wk.getEncodedWrappedKey(), ccIn.getWrappedKey().getEncodedWrappedKey());
    final byte[] read = IOUtils.toByteArray(ccIn);
    assertEquals(payload, read);
  }
}
