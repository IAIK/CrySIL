package org.crysil.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

//TODO performance, buffering???
abstract class CMS {

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  static OutputStream wrapOutputStreamforEncryption(final OutputStream out,
      final ASN1ObjectIdentifier encryptionAlgorithm, final X509Certificate... recipientCertificates)
      throws CMSException {
    final CMSEnvelopedDataStreamGenerator edGen = new CMSEnvelopedDataStreamGenerator();

    for (final X509Certificate crt : recipientCertificates) {
      try {
        edGen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(crt).setProvider("BC"));
      } catch (final CertificateEncodingException e) {
        throw new CMSException(e.getMessage(), e);
      }
    }

    try {

      return edGen.open(out,
          new JceCMSContentEncryptorBuilder(encryptionAlgorithm).setProvider("BC").build());
    } catch (final IOException e) {
      throw new CMSException(e.getMessage(), e);
    }
  }

  static InputStream wrapInputStreamforDecryption(final InputStream in, final PrivateKey decryptionKey)
      throws CMSException {
    CMSEnvelopedDataParser ep;
    try {
      ep = new CMSEnvelopedDataParser(in);
    } catch (final IOException e) {
      throw new CMSException(e.getMessage(), e);
    }

    final RecipientInformationStore recipients = ep.getRecipientInfos();

    final Collection<RecipientInformation> rcpts = recipients.getRecipients();

    for (final RecipientInformation recipient : rcpts) {

      CMSTypedStream recData;
      try {
        recData = recipient
            .getContentStream(new JceKeyTransEnvelopedRecipient(decryptionKey).setProvider("BC"));
        return (recData.getContentStream());
      } catch (final IOException e) {
        e.printStackTrace();
      }

    }
    throw new CMSException("No data enveloped for the given private key");
  }

  static InputStream wrapInputStreamforManualDecryption(final InputStream in,
      final ContentEncryptionKeyManualDecryptor decryptor) throws CMSException {
    CMSEnvelopedDataParser ep;

    try {
      ep = new CMSEnvelopedDataParser(in);
    } catch (final IOException e) {
      throw new CMSException(e.getMessage(), e);
    }

    final RecipientInformationStore recipients = ep.getRecipientInfos();

    final Collection<RecipientInformation> rcpts = recipients.getRecipients();

    for (final RecipientInformation recipient : rcpts) {
      if (!(recipient instanceof KeyTransRecipientInformation)) {
        continue; // non-keytrans → no can do
      }

      Field info;
      try {
        info = recipient.getClass().getDeclaredField("info");
        info.setAccessible(true);
        final KeyTransRecipientInfo kInfo = (KeyTransRecipientInfo) info.get(recipient);

        try {

          final AlgorithmIdentifier contentEncryptionAlgorithm = ep.getContentEncryptionAlgorithm();
          final SecretKey unwrap = decryptor.decrypt(kInfo, contentEncryptionAlgorithm);

          // setup cipher for content decryption
          final Cipher decryptionCipher = Cipher
              .getInstance(contentEncryptionAlgorithm.getAlgorithm().getId());
          final AlgorithmParameters cipherParams = AlgorithmParameters
              .getInstance(contentEncryptionAlgorithm.getAlgorithm().getId());
          cipherParams.init(contentEncryptionAlgorithm.getParameters().toASN1Primitive().getEncoded());
          decryptionCipher.init(Cipher.DECRYPT_MODE, unwrap, cipherParams);

          // extract underlying stream containing the encrypted data
          final Field stream = RecipientInformation.class.getDeclaredField("secureReadable");
          stream.setAccessible(true);
          final Method method = stream.get(recipient).getClass().getMethod("getInputStream");
          method.setAccessible(true);
          final InputStream str = (InputStream) method.invoke(stream.get(recipient));

          return new CipherInputStream(str, decryptionCipher);
          // final byte[] plainKey = cipher.doFinal(encryptedKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException
            | InvalidAlgorithmParameterException | NoSuchMethodException | InvocationTargetException
            | CryptoException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
          | IllegalAccessException e) {
        throw new CMSException(e.getMessage(), e);
      }

    }
    throw new CMSException("No data enveloped for the given private key");
  }

  @SuppressWarnings("unchecked")
  static OutputStream wrapOutputStreamForSigning(final OutputStream out, final X509Certificate signingCert,
      final PrivateKey signingKey, final String signAlg) throws CMSException {
    final List<X509Certificate> certList = new LinkedList<>();
    certList.add(signingCert);

    Store<X509Certificate> certs;
    try {
      certs = new JcaCertStore(certList);
    } catch (final CertificateEncodingException e) {
      throw new CMSException(e.getMessage(), e);
    }
    ContentSigner signer;
    try {
      signer = new JcaContentSignerBuilder(signAlg).setProvider("BC").build(signingKey);
    } catch (final OperatorCreationException e) {
      throw new CMSException(e.getMessage(), e);
    }

    final CMSSignedDataStreamGenerator gen = new CMSSignedDataStreamGenerator();

    try {
      gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
          new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(signer, signingCert));
    } catch (CertificateEncodingException | OperatorCreationException e) {
      throw new CMSException(e.getMessage(), e);
    }

    gen.addCertificates(certs);

    try {
      return gen.open(out, true);
    } catch (final IOException e) {
      throw new CMSException(e.getMessage(), e);
    }
  }
}
