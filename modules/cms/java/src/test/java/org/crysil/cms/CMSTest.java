package org.crysil.cms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@SuppressWarnings("deprecation")
public class CMSTest {

  private X509Certificate cert;
  private PrivateKey      privKey;

  @Test(dataProvider = "genRandomTestData")
  public void testEncryptManualDecrypt(final byte[] input) throws CMSException, IOException {

    final ByteArrayOutputStream encrypt = new ByteArrayOutputStream();
    final CmsEnvelopedOutputStream cmsEnvelopedOutputStream = new CmsEnvelopedOutputStream(encrypt,
        CMSAlgorithm.AES256_GCM, cert);
    final byte[] ref = new byte[input.length];
    System.arraycopy(input, 0, ref, 0, input.length);
    cmsEnvelopedOutputStream.write(input);
    cmsEnvelopedOutputStream.close();
    final String base64Encrypted = Base64.toBase64String(encrypt.toByteArray());
    final CmsEnvelopedInputStream cmsEnvelopedInputStream = new CmsEnvelopedInputStream(
        new ByteArrayInputStream(Base64.decode(base64Encrypted)), new ProvidedKeyManualKeyDecryptor(privKey));
    final ByteArrayOutputStream decrypt = new ByteArrayOutputStream();
    IOUtils.copy(cmsEnvelopedInputStream, decrypt);
    cmsEnvelopedInputStream.close();
    Assert.assertEquals(decrypt.toByteArray(), ref);

  }

  @Test(dataProvider = "genRandomTestData")
  public void testEncryptAutomagicDecrypt(final byte[] input) throws CMSException, IOException {

    final ByteArrayOutputStream encrypt = new ByteArrayOutputStream();
    final CmsEnvelopedOutputStream cmsEnvelopedOutputStream = new CmsEnvelopedOutputStream(encrypt,
        CMSAlgorithm.AES256_GCM, cert);
    final byte[] ref = new byte[input.length];
    System.arraycopy(input, 0, ref, 0, input.length);
    cmsEnvelopedOutputStream.write(input);
    cmsEnvelopedOutputStream.close();
    final String base64Encrypted = Base64.toBase64String(encrypt.toByteArray());
    final CmsEnvelopedInputStream cmsEnvelopedInputStream = new CmsEnvelopedInputStream(
        new ByteArrayInputStream(Base64.decode(base64Encrypted)), privKey);
    final ByteArrayOutputStream decrypt = new ByteArrayOutputStream();
    IOUtils.copy(cmsEnvelopedInputStream, decrypt);
    cmsEnvelopedInputStream.close();
    Assert.assertEquals(decrypt.toByteArray(), ref);

  }

  @Test(dataProvider = "genRandomTestData")
  public void testSignVerify(final byte[] input) throws CMSException, IOException {

    final ByteArrayOutputStream sign = new ByteArrayOutputStream();
    final CmsSignedDataOutputStream cmsSignedOutputStream = new CmsSignedDataOutputStream(sign, cert, privKey,
        "SHA256withRSA");
    final byte[] ref = new byte[input.length];
    System.arraycopy(input, 0, ref, 0, input.length);
    cmsSignedOutputStream.write(input);
    cmsSignedOutputStream.close();
    final String base64Signed = Base64.toBase64String(sign.toByteArray());
    final CmsSignedDataInputStream cmsSignedInputStream = new CmsSignedDataInputStream(
        new ByteArrayInputStream(Base64.decode(base64Signed)));
    final ByteArrayOutputStream verify = new ByteArrayOutputStream();
    IOUtils.copy(cmsSignedInputStream, verify);
    cmsSignedInputStream.close();
    Assert.assertEquals(verify.toByteArray(), ref);
    Assert.assertTrue(cmsSignedInputStream.isValid());

  }

  @DataProvider
  public Object[][] genRandomTestData() {
    final Random rnd = new Random(System.currentTimeMillis());
    final Object[][] data = new Object[5][1];
    for (int i = 0; i < data.length; ++i) {
      final byte[] bytes = new byte[rnd.nextInt(512 * 7031)];
      rnd.nextBytes(bytes);
      data[i][0] = bytes;
    }
    return data;
  }

  @BeforeTest
  public void setup() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException,
      SecurityException, SignatureException {
    Security.addProvider(new BouncyCastleProvider());
    final KeyPairGenerator kgen = KeyPairGenerator.getInstance("RSA");
    kgen.initialize(512); // make it superfast for testing
    final KeyPair keypair = kgen.generateKeyPair();

    privKey = keypair.getPrivate();

    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

    final X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

    certGen.setSerialNumber(BigInteger.valueOf(1337));
    certGen.setIssuerDN(new X500Principal("CN=CrySIL Test Certificate"));
    certGen.setNotBefore(new Date(System.currentTimeMillis() - 1000000));
    certGen.setNotAfter(new Date(System.currentTimeMillis() + 1000000));
    certGen.setSubjectDN(new X500Principal("CN=CrySIL Test Certificate"));
    certGen.setPublicKey(keypair.getPublic());
    certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

    certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
    certGen.addExtension(X509Extensions.KeyUsage, true,
        new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
    cert = certGen.generateX509Certificate(keypair.getPrivate(), "BC");
  }

}
