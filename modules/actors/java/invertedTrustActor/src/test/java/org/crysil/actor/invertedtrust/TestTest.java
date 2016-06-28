package org.crysil.actor.invertedtrust;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.cms.CMSException;
import org.crysil.cms.CmsEnvelopedInputStream;
import org.crysil.cms.CmsEnvelopedOutputStream;
import org.crysil.commons.KeyType;
import org.crysil.commons.Module;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateKeyRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateKeyResponse;
import org.crysil.protocol.payload.crypto.key.KeyRepresentation;
import org.crysil.protocol.payload.crypto.key.WrappedKey;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestTest {

  @Test
  public void testGenerate() throws UnsupportedRequestException, KeyStoreUnavailableException {

    final Module DUT = new InvertedTrustActor(new File("keyStore.uber"),"foo".toCharArray());
    final Request request = new Request();
    final Map<String, Object> params = new HashMap<>();
    params.put("keySize", 128);
    PayloadGenerateKeyRequest payload = new PayloadGenerateKeyRequest(KeyType.AES, params,
        KeyRepresentation.WRAPPED, null);
    request.setPayload(payload);
    Response resp = DUT.take(request);

    Assert.assertEquals(resp.getPayload().getType(), "generateKeyResponse");
    PayloadGenerateKeyResponse response = (PayloadGenerateKeyResponse) resp.getPayload();
    Assert.assertEquals(response.getKey().getType(), "wrappedKey");

    payload = new PayloadGenerateKeyRequest(KeyType.AES, params, KeyRepresentation.HANDLE, null);
    request.setPayload(payload);
    resp = DUT.take(request);

    Assert.assertEquals(resp.getPayload().getType(), "generateKeyResponse");
    response = (PayloadGenerateKeyResponse) resp.getPayload();
    Assert.assertEquals(response.getKey().getType(), "handle");
  }

  @Test()
  public void testEncryptDecrypt() throws UnsupportedRequestException, CMSException, IOException, KeyStoreUnavailableException {
    final InvertedTrustActor actor = new InvertedTrustActor(new File("keyStore.uber"),"foo".toCharArray());
    final WrappedKey encryptionKey = actor.genWrappedKey();
    final ByteArrayOutputStream encrypted = new ByteArrayOutputStream();
    final CmsEnvelopedOutputStream cmsOut = actor.genCmsOutputStream(encrypted, encryptionKey);
    cmsOut.write(encryptionKey.getEncodedWrappedKey());
    cmsOut.close();
    System.out.println("SZ_WK:  " + encryptionKey.getEncodedWrappedKey().length);
    System.out.println("SZ_CMS: " + encrypted.toByteArray().length);
    System.out.println("WK:\n" + ASN1Dump
        .dumpAsString(new ASN1InputStream(encryptionKey.getEncodedWrappedKey()).readObject(), true));
    System.out.println(
        "BULK:\n" + ASN1Dump.dumpAsString(new ASN1InputStream(encrypted.toByteArray()).readObject(), true));
    final ByteArrayInputStream cmsIn = new ByteArrayInputStream(encrypted.toByteArray());
    final CmsEnvelopedInputStream genCMSInputStream = InvertedTrustActor.genCMSInputStream(cmsIn, actor,
        encryptionKey);
    final ByteArrayOutputStream decrypted = new ByteArrayOutputStream();
    IOUtils.copy(genCMSInputStream, decrypted);
    genCMSInputStream.close();
    Assert.assertEquals(encryptionKey.getEncodedWrappedKey(), decrypted.toByteArray());
  }



  @DataProvider
  public Object[][] genRandomTestData() {
    final Random rnd = new Random(System.currentTimeMillis());
    final Object[][] data = new Object[1][1];
    for (int i = 0; i < data.length; ++i) {
      final byte[] bytes = new byte[rnd.nextInt(512 /** 1024 */
      )];
      rnd.nextBytes(bytes);
      data[i][0] = bytes;
    }
    return data;
  }

}
