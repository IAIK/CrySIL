package org.crysil.cms;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CryptoException;

public class ProvidedKeyManualKeyDecryptor implements ContentEncryptionKeyManualDecryptor {

  private final PrivateKey wrappingKey;

  public ProvidedKeyManualKeyDecryptor(final PrivateKey wrappingKey) {
    this.wrappingKey = wrappingKey;
  }

  @Override
  public SecretKey decrypt(final KeyTransRecipientInfo kInfo,
      final AlgorithmIdentifier contentEncryptionAlgorithm) throws CryptoException {
    final byte[] encryptedKey = kInfo.getEncryptedKey().getOctets();
    final AlgorithmIdentifier wrappingAlg = kInfo.getKeyEncryptionAlgorithm();
    final ASN1ObjectIdentifier algorithm = wrappingAlg.getAlgorithm();

    AlgorithmParameters params = null;
    try {

      params = AlgorithmParameters.getInstance(algorithm.getId());
    } catch (final NoSuchAlgorithmException e) {
      // this algorithm does not take any parameters
    }
    if (params != null) {
      try {
        params.init(wrappingAlg.getParameters().toASN1Primitive().getEncoded());
      } catch (final IOException e) {
        throw new CryptoException(e.getLocalizedMessage(), e);
      }

    }

    Cipher cipher;
    try {
      cipher = Cipher.getInstance(algorithm.getId());
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw new CryptoException(e.getLocalizedMessage(), e);
    }

    try {
      if (params != null) {
        cipher.init(Cipher.UNWRAP_MODE, wrappingKey, params);
      } else {
        cipher.init(Cipher.UNWRAP_MODE, wrappingKey);
      }
    } catch (final InvalidKeyException | InvalidAlgorithmParameterException e) {
      throw new CryptoException(e.getLocalizedMessage(), e);
    }

    try {
      return (SecretKey) cipher.unwrap(encryptedKey, contentEncryptionAlgorithm.getAlgorithm().getId(),
          Cipher.SECRET_KEY);
    } catch (InvalidKeyException | NoSuchAlgorithmException e) {
      throw new CryptoException(e.getLocalizedMessage(), e);
    }
  }

}
