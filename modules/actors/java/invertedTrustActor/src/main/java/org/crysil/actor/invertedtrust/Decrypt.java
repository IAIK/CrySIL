package org.crysil.actor.invertedtrust;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.cms.CMSException;
import org.crysil.actor.storage.CryptoContainer;
import org.crysil.actor.storage.KeyPairContainer;
import org.crysil.actor.storage.SecretKeyContainer;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptResponse;
import org.crysil.protocol.payload.crypto.key.Key;

public class Decrypt implements Command {

  private final SingleKeyStore keyStore;

  public Decrypt(final SingleKeyStore keyStore) {
    this.keyStore = keyStore;
  }

  @Override
  public PayloadResponse perform(final PayloadRequest input) throws CrySILException {
    final PayloadDecryptRequest request = (PayloadDecryptRequest) input;

    try {
      if (request.getEncryptedData().size() < 3) {
        throw new UnsupportedRequestException();
      }
      // prepare stuff

      final Key decryptionKey = request.getDecryptionKey();
      final CryptoContainer container = keyStore.extractKey(decryptionKey);
      final int mode = request.getEncryptedData().get(1)[0];

      final Cipher cipher = Cipher.getInstance(request.getAlgorithm());
      final byte[] algorithmParams = request.getEncryptedData().get(0);
      AlgorithmParameters algPArams = null;
      if (algorithmParams.length != 0) {
        algPArams = AlgorithmParameters.getInstance(request.getAlgorithm());
        algPArams.init(algorithmParams);
      }
      if (container instanceof SecretKeyContainer) {
        final SecretKeyContainer sContainer = (SecretKeyContainer) container;
        final SecretKey bulkDecryptionKey = sContainer.getKey();
        if (algPArams != null) {
          cipher.init(mode, bulkDecryptionKey, algPArams);
        } else {
          cipher.init(mode, bulkDecryptionKey);
        }
      } else if (container instanceof KeyPairContainer) {
        final KeyPairContainer kpContainer = (KeyPairContainer) container;
        final KeyPair keyPair = kpContainer.getKeyPair();
        if (algPArams != null) {
          cipher.init(mode, keyPair.getPrivate(), algPArams);
        } else {
          cipher.init(mode, keyPair.getPrivate());
        }
      }
      if (mode == Cipher.DECRYPT_MODE) {
        try (CipherInputStream decryptionStream = new CipherInputStream(
            new ByteArrayInputStream(request.getEncryptedData().get(2)), cipher)) {

          final ByteArrayOutputStream plain = new ByteArrayOutputStream();
          IOUtils.copy(decryptionStream, plain);
          decryptionStream.close();
          final PayloadDecryptResponse result = new PayloadDecryptResponse();
          result.addPlainData(plain.toByteArray());
          return result;
        }
      }
      // unwrap
      final PayloadDecryptResponse result = new PayloadDecryptResponse();
      result.addPlainData(cipher
          .unwrap(request.getEncryptedData().get(2), request.getAlgorithm(), Cipher.SECRET_KEY).getEncoded());
      return result;

    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | CMSException
        | InvalidAlgorithmParameterException | IOException | ClassNotFoundException e) {
      e.printStackTrace();
      throw new UnknownErrorException();
    }
  }

}
