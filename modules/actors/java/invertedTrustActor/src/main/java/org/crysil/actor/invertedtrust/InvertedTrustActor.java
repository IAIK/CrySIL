package org.crysil.actor.invertedtrust;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.CryptoException;
import org.crysil.actor.storage.KeyPairContainer;
import org.crysil.builders.PayloadBuilder;
import org.crysil.cms.CmsEnvelopedInputStream;
import org.crysil.cms.CmsEnvelopedOutputStream;
import org.crysil.cms.ContentEncryptionKeyManualDecryptor;
import org.crysil.commons.KeyType;
import org.crysil.commons.Module;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.InvalidWrappedKeyException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.auth.AuthInfo;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptResponse;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateKeyRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateKeyResponse;
import org.crysil.protocol.payload.crypto.key.KeyRepresentation;
import org.crysil.protocol.payload.crypto.key.WrappedKey;
import org.crysil.protocol.payload.crypto.stickypolicy.PayloadExtractStickyPolicyRequest;
import org.crysil.protocol.payload.status.PayloadStatus;

/**
 * Has one static key available and can use this very key to encrypt and decrypt
 * data.
 */
public class InvertedTrustActor implements Module {
  private final Map<Class<? extends PayloadRequest>, Command> commands = new HashMap<>();
  private final SingleKeyStore                                keyStore;

  public InvertedTrustActor(final File keyStoreLocation, final char[] password)
      throws KeyStoreUnavailableException {
    keyStore = new SingleKeyStore(keyStoreLocation, password);
    commands.put(PayloadDecryptRequest.class, new Decrypt(keyStore));
    commands.put(PayloadGenerateKeyRequest.class, new GenerateKey(keyStore));
    commands.put(PayloadExtractStickyPolicyRequest.class, new ExtractStickyPolicy(keyStore));
  }

  @Override
  public Response take(final Request request) throws UnsupportedRequestException {
    // see if we have someone capable of handling the request
    final Command command = commands.get(request.getPayload().getClass());

    // if not, do tell
    if (null == command) {
      throw new UnsupportedRequestException();
    }

    // prepare the response
    final Response response = new Response();
    response.setHeader(request.getHeader());

    // let someone else do the actual work
    try {
      response.setPayload(command.perform(request.getPayload()));
    } catch (final CrySILException e) {
      e.printStackTrace();
      response.setPayload(PayloadBuilder.buildStatusResponse(e.getErrorCode()));
    }

    return response;
  }

  public WrappedKey genWrappedKey(final AuthInfo authInfo) throws UnsupportedRequestException {
    final Request request = new Request();
    final Map<String, Object> params = new HashMap<>();
    params.put("keySize", 4096);
    final PayloadGenerateKeyRequest payload = new PayloadGenerateKeyRequest(KeyType.RSA, params,
        KeyRepresentation.WRAPPED, authInfo);
    request.setPayload(payload);
    final Response resp = take(request);
    return (WrappedKey) ((PayloadGenerateKeyResponse) resp.getPayload()).getKey();

  }

  public WrappedKey genWrappedKey() throws UnsupportedRequestException {
    return genWrappedKey(null);
  }

  public CmsEnvelopedOutputStream genCmsOutputStream(final OutputStream out, final WrappedKey key)
      throws CMSException {
    try {
      final KeyPairContainer keyPair = (KeyPairContainer) keyStore.extractKey(key);
      final X509Certificate cert = SingleKeyStore.genSelfSignedCertFromKeyPair(keyPair.getKeyPair(),
          keyPair.getIdentifier());
      return new CmsEnvelopedOutputStream(out, CMSAlgorithm.AES128_GCM, cert);
    } catch (final IOException | CryptoException | ClassNotFoundException | InvalidWrappedKeyException
        | UnsupportedRequestException | KeyStoreUnavailableException e) {
      throw new CMSException(e.getLocalizedMessage(), e);
    }
  }

  public static CmsEnvelopedInputStream genCMSInputStream(final InputStream in, final Module remote,
      final WrappedKey decryptionKey, final String destination) throws CMSException {

    return new CmsEnvelopedInputStream(in, new ContentEncryptionKeyManualDecryptor() {

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

        try {
          final Request unwrapRequest = new Request();
          unwrapRequest.setHeader(new StandardHeader());
          final ArrayList<String> path = new ArrayList<>(1);
          path.add(destination);
          unwrapRequest.getHeader().setPath(path);
          final PayloadDecryptRequest decrypt = new PayloadDecryptRequest();
          decrypt.setDecryptionKey(decryptionKey);
          decrypt.addEncryptedData(
              params == null ? new byte[] {} : wrappingAlg.getParameters().toASN1Primitive().getEncoded());
          decrypt.addEncryptedData(new byte[] {
              Cipher.UNWRAP_MODE });
          decrypt.addEncryptedData(encryptedKey);
          decrypt.setAlgorithm(algorithm.getId());
          unwrapRequest.setPayload(decrypt);
          final Response unwrapResponse = remote.take(unwrapRequest);
          if (unwrapResponse.getPayload().getType().equals("status")) {
            final CrySILException crysilException = CrySILException
                .fromErrorCode(((PayloadStatus) unwrapResponse.getPayload()).getCode());
            throw new CryptoException(crysilException.getLocalizedMessage(), crysilException);
          }
          if (!unwrapResponse.getPayload().getType().equals("decryptResponse")) {
            throw new CryptoException(unwrapResponse.getPayload().getType() + " is not supported!");
          }
          final PayloadDecryptResponse unwrapped = (PayloadDecryptResponse) unwrapResponse.getPayload();
          final byte[] secretKey = unwrapped.getPlainData().get(0);
          return new SecretKeySpec(secretKey, contentEncryptionAlgorithm.getAlgorithm().getId());
        } catch (IOException | CrySILException e) {
          throw new CryptoException(e.getLocalizedMessage(), e);
        }
      }
    });
  }

}
