package org.crysil.actor.invertedtrust;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.crysil.actor.storage.CryptoContainer;
import org.crysil.cms.CmsEnvelopedInputStream;
import org.crysil.cms.CmsSignedDataInputStream;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.InvalidWrappedKeyException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.KeyHandle;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

public class SingleKeyStore {

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  private static final Map<UUID, CryptoContainer> storage    = new HashMap<>();

  final static String                             SIGALG     = "SHA256WithRSA";

  private static final String                     ALIAS_CERT = "cert";

  private static final String                     ALIAS_KEY  = "key";

  /** java representation of the above raw key data */
  private PublicKey                               pubKey;
  private PrivateKey                              privKey;
  private X509Certificate                         cert;

  public SingleKeyStore(final File keyStoreFile, final char[] password) throws KeyStoreUnavailableException {
    try {

      if (!keyStoreFile.exists()) {
        genKS(keyStoreFile, password);
      }
      loadKS(keyStoreFile, password);
      Arrays.fill(password, (char) 0);
    } catch (final Exception e) {
      throw new KeyStoreUnavailableException();
    }
  }

  private void genKS(final File keyStoreFile, final char[] password) throws KeyStoreException {
    try {
      final KeyPair kp = GenerateKey.genRSAKey(4096);

      final PrivateKey privKey = kp.getPrivate();
      final X509Certificate cert = genSelfSignedCertFromKeyPair(kp, UUID.randomUUID());

      final KeyStore ks = KeyStore.getInstance("UBER", "BC");
      ks.load(null, password);
      ks.setCertificateEntry(ALIAS_CERT, cert);
      ks.setKeyEntry(ALIAS_KEY, privKey, password, new X509Certificate[] {
          cert });
      final FileOutputStream stream = new FileOutputStream(keyStoreFile);
      ks.store(stream, password);
      stream.close();
    } catch (final CrySILException | CryptoException | NoSuchProviderException | NoSuchAlgorithmException
        | CertificateException | IOException e) {
      throw new KeyStoreException(e);
    }
  }

  private void loadKS(final File keyStoreFile, final char[] password) throws KeyStoreException {
    KeyStore ks;
    try {
      ks = KeyStore.getInstance("UBER", "BC");
      final FileInputStream stream = new FileInputStream(keyStoreFile);
      ks.load(stream, password);
      cert = (X509Certificate) ks.getCertificate(ALIAS_CERT);
      pubKey = cert.getPublicKey();
      privKey = (PrivateKey) ks.getKey(ALIAS_KEY, password);
      stream.close();
    } catch (NoSuchProviderException | NoSuchAlgorithmException | CertificateException | IOException
        | UnrecoverableKeyException e) {
      throw new KeyStoreException(e);
    }
  }

  /**
   * returns the public key of the specified key in JCE-readable form
   *
   * @param current
   *          the CrySIL key representation
   * @return the public key
   * @throws InvalidCertificateException
   * @throws KeyNotFoundException
   */
  public PublicKey getJCEPublicKey() {
    return pubKey;

  }

  /**
   * returns the private key of the specified key in JCE-readable form
   *
   * @param current
   *          the CrySIL key representation
   * @return the private key
   * @throws KeyNotFoundException
   */
  public PrivateKey getPrivateKey() {
    return privKey;

  }

  /**
   * returns the certificate of the specified key in JCE-readable form
   *
   * @param current
   *          the CrySIL key representation
   * @return the certificate
   * @throws InvalidCertificateException
   * @throws KeyNotFoundException
   */
  public X509Certificate getX509Certificate() {
    return cert;

  }

  public void addCryptographicMaterial(final CryptoContainer container) {
    storage.put(container.getIdentifier(), container);

  }

  public CryptoContainer getCryptographicMaterial(final UUID id) {
    return storage.get(id);
  }

  CryptoContainer extractKey(final Key decryptionKey) throws IOException, ClassNotFoundException,
      CMSException, InvalidWrappedKeyException, UnsupportedRequestException, KeyStoreUnavailableException {
    CryptoContainer container;
    if (decryptionKey instanceof WrappedKey) {
      final ByteArrayInputStream bIn = new ByteArrayInputStream(
          ((WrappedKey) decryptionKey).getEncodedWrappedKey());
      final PrivateKey key = getPrivateKey();
      // try{

      final CmsEnvelopedInputStream envIn = new CmsEnvelopedInputStream(bIn, key);
      final CmsSignedDataInputStream signedIn = new CmsSignedDataInputStream(envIn);
      final ObjectInputStream objIn = new ObjectInputStream(signedIn);
      container = (CryptoContainer) objIn.readObject();
      objIn.close();
      if (!signedIn.isValid()) {
        throw new InvalidWrappedKeyException();
      }
      return container;
    } else if (decryptionKey instanceof KeyHandle) {
      container = getCryptographicMaterial(UUID.fromString(((KeyHandle) decryptionKey).getId()));
      return container;
    } else {
      throw new UnsupportedRequestException();
    }

  }

  static X509Certificate genSelfSignedCertFromKeyPair(final KeyPair keyPair, final UUID keyID)
      throws CryptoException {

    // build a certificate generator

    final X509v3CertificateBuilder bld = new JcaX509v3CertificateBuilder(new X500Name("cn=crysil"),
        new BigInteger(keyID.toString().getBytes()), new Date(0), new Date(Long.MAX_VALUE),
        new X500Name("cn=InvertedTrustActor"), keyPair.getPublic());
    X509CertificateHolder certHldr;
    try {
      certHldr = bld.build(
          new JcaContentSignerBuilder(SIGALG).setProvider("BC").build(keyPair.getPrivate()));
      return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHldr);
    } catch (OperatorCreationException | CertificateException e) {
      throw new CryptoException(e.getLocalizedMessage(), e);
    }

  }
}
