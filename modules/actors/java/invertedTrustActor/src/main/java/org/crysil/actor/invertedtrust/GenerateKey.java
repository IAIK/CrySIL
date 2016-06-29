package org.crysil.actor.invertedtrust;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.crysil.actor.storage.CryptoContainer;
import org.crysil.actor.storage.KeyPairContainer;
import org.crysil.actor.storage.SecretKeyContainer;
import org.crysil.cms.CmsEnvelopedOutputStream;
import org.crysil.cms.CmsSignedDataOutputStream;
import org.crysil.commons.KeyType;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateKeyRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateKeyResponse;
import org.crysil.protocol.payload.crypto.key.KeyHandle;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

/**
 * can handle discover key requests.
 */
public class GenerateKey implements Command {

  private final SingleKeyStore keyStore;

  public GenerateKey(final SingleKeyStore keyStore) {
    this.keyStore = keyStore;
  }

  @Override
  public PayloadResponse perform(final PayloadRequest input) throws CrySILException {
    final PayloadGenerateKeyRequest request = (PayloadGenerateKeyRequest) input;

    final KeyType type = request.getKeyType();
    if (!type.hasAllRequiredParams(request.getParams())) {
      throw new UnsupportedRequestException();
    }
    CryptoContainer container;
    switch (type) {
    case AES:
      final SecretKey k = genAESKey((Integer) request.getParams().get("keySize"));
      container = new SecretKeyContainer(k, request.getStickyPolicy());
      break;
    case RSA:
      final KeyPair kp = genRSAKey((Integer) request.getParams().get("keySize"));
      container = new KeyPairContainer(kp, request.getStickyPolicy());
      break;
    case ECDSA:
      final KeyPair ecPair = genECKey((Integer) request.getParams().get("keySize"));
      container = new KeyPairContainer(ecPair, request.getStickyPolicy());
      break;
    default:
      throw new UnsupportedRequestException();
    }
    final ByteArrayOutputStream cms = new ByteArrayOutputStream();
    final X509Certificate cert = keyStore.getX509Certificate();
    try (CmsEnvelopedOutputStream envOut = new CmsEnvelopedOutputStream(cms, CMSAlgorithm.AES128_GCM, cert)) {
      try (CmsSignedDataOutputStream signOut = new CmsSignedDataOutputStream(envOut, cert,
          keyStore.getPrivateKey(), SingleKeyStore.SIGALG)) {
        try (ObjectOutputStream containerOut = new ObjectOutputStream(signOut)) {
          containerOut.writeObject(container);
          if (request.getStickyPolicy() != null) {
            containerOut.writeObject(request.getStickyPolicy());
          }

          containerOut.close();

        }
      }
    } catch (IOException | CMSException e) {
      e.printStackTrace();
      throw new UnknownErrorException();
    }

    final PayloadGenerateKeyResponse response = new PayloadGenerateKeyResponse();
    switch (request.getRepresentation()) {
    case HANDLE:
      final KeyHandle handle = new KeyHandle();
      handle.setId(container.getIdentifier().toString());
      response.setKey(handle);
      break;
    case WRAPPED:
      final WrappedKey wk = new WrappedKey();
      wk.setEncodedWrappedKey(cms.toByteArray());
      response.setKey(wk);
      break;

    default:
      throw new UnsupportedRequestException();
    }

    return response;
  }

  static SecretKey genAESKey(final int keySize) throws CrySILException {
    KeyGenerator gen;
    try {
      gen = KeyGenerator.getInstance("AES");
    } catch (final NoSuchAlgorithmException e) {
      throw new UnknownErrorException();
    }
    gen.init(keySize);
    return gen.generateKey();
  }

  static KeyPair genRSAKey(final int keySize) throws CrySILException {
    KeyPairGenerator gen;
    try {
      gen = KeyPairGenerator.getInstance("RSA");
    } catch (final NoSuchAlgorithmException e) {
      throw new UnknownErrorException();
    }
    gen.initialize(keySize);
    return gen.generateKeyPair();
  }

  static KeyPair genECKey(final int keySize) throws CrySILException {
    final X9ECParameters ecP = CustomNamedCurves.getByName("curve25519");
    final ECParameterSpec ecSpec = new ECParameterSpec(ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(),
        ecP.getSeed());

    KeyPairGenerator g;
    try {
      g = KeyPairGenerator.getInstance("ECDSA");
      g.initialize(ecSpec, new SecureRandom());
      return g.generateKeyPair();
    } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
      throw new UnknownErrorException();
    }

  }

}
