package org.crysil.instance.u2f.utils;

import android.util.Log;

import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.X500NameBuilder;
import org.spongycastle.asn1.x500.style.BCStyle;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.X509v3CertificateBuilder;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.util.PrivateKeyFactory;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.spongycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.bc.BcRSAContentSignerBuilder;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;
import org.spongycastle.pkcs.PKCS10CertificationRequest;
import org.spongycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import static java.util.AbstractMap.*;

/**
 * Helper class for creating keys and certificates
 */
public class CertificateUtils {

    public static final String TAG = CertificateUtils.class.getSimpleName();

    private static final String CERT_CA_COMMON_NAME = "crysil.org";
    private static final String CERT_CA_EMAIL = "crysil@example.com";

    private static final int CERT_KEYSIZE_DEFAULT = 2048;
    private static final String CERT_ALGORITHM_ID_DEFAULT = "SHA256withRSAEncryption";
    private static final String CERT_ALGORITHM_DEFAULT = "RSA";
    private static final String CERT_PROVIDER_DEFAULT = "SC";
    private static final String CERT_SIGN_PROVIDER = "AndroidOpenSSL";

    public static void storeWebserviceCert(String alias, Certificate cert) {
        try {
            Key key = KeyStoreHandler.getInstance().getKey(alias);
            Certificate[] certChain = new Certificate[1];
            certChain[0] = cert;
            if (!KeyStoreHandler.getInstance().addKey(alias, key, certChain, true)) {
                throw new KeyStoreException("Error while adding key to key store");
            }
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static PKCS10CertificationRequest createWebserviceCsr(String alias, String crysilId) {
        SimpleImmutableEntry<KeyPair, X509Certificate> webVpnCertificate = createKeyAndCert(CERT_PROVIDER_DEFAULT,
                CERT_ALGORITHM_ID_DEFAULT, CERT_ALGORITHM_DEFAULT, CERT_KEYSIZE_DEFAULT, alias, CERT_CA_COMMON_NAME,
                CERT_CA_EMAIL, null, true);
        if (webVpnCertificate == null) {
            Log.e(TAG, String.format("Error at creation of CSR for alias %s", alias));
            return null;
        }
        try {
            X500Name subject = new X500NameBuilder().addRDN(BCStyle.CN, crysilId).addRDN(BCStyle.EmailAddress,
                    CERT_CA_EMAIL).build();
            JcaPKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject,
                    webVpnCertificate.getKey().getPublic());
            ContentSigner cs = new JcaContentSignerBuilder(CERT_ALGORITHM_ID_DEFAULT).build(
                    webVpnCertificate.getKey().getPrivate());
            PKCS10CertificationRequest certRequest = builder.build(cs);
            Log.d(TAG, String.format("Created a new CSR for alias %s", alias));
            return certRequest;
        } catch (OperatorCreationException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static boolean createKeyAndCert(String alias, String commonName, String emailAddress) {
        SimpleImmutableEntry<KeyPair, X509Certificate> pair = createKeyAndCert(CERT_PROVIDER_DEFAULT,
                CERT_ALGORITHM_ID_DEFAULT, CERT_ALGORITHM_DEFAULT, CERT_KEYSIZE_DEFAULT, alias, commonName,
                emailAddress, null, false);
        return pair != null;
    }

    private static SimpleImmutableEntry<KeyPair, X509Certificate> createKeyAndCert(String provider,
                                                                                   String algorithmID,
                                                                                   String algorithm,
                                                                                   int keySize,
                                                                                   String alias,
                                                                                   String commonName,
                                                                                   String emailAddress,
                                                                                   SimpleImmutableEntry<PrivateKey, Certificate> issuer,
                                                                                   boolean overwrite) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm, provider);
            keyPairGenerator.initialize(keySize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Cerate the subject of the new certificate
            X500Name certSubject = new X500NameBuilder().addRDN(BCStyle.CN, commonName).addRDN(BCStyle.EmailAddress,
                    emailAddress).build();

            // Create the new certificate
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            Date notBefore = gregorianCalendar.getTime();
            gregorianCalendar.add(Calendar.MONTH, 6);
            Date notAfter = gregorianCalendar.getTime();

            BigInteger serialNumber = BigInteger.valueOf(Math.abs((new Random()).nextLong() + 1));

            X500Name certIssuer = certSubject;
            if (issuer != null) {
                certIssuer = (X500Name) ((X509Certificate) issuer.getValue()).getSubjectDN();
            }
            AsymmetricKeyParameter certPrivateKey = PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());
            if (issuer != null) {
                PrivateKeyFactory.createKey(issuer.getKey().getEncoded());
            }

            X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(certIssuer, serialNumber, notBefore,
                    notAfter, certSubject, SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));
            AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(
                    "SHA256withRSAEncryption");
            AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
            ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(certPrivateKey);
            X509CertificateHolder holder = certGen.build(sigGen);
            CertificateFactory factory = CertificateFactory.getInstance("X.509", "SC");
            X509Certificate certificate = (X509Certificate) factory.generateCertificate(
                    new ByteArrayInputStream(holder.getEncoded()));

            Certificate[] certChain;
            if (issuer == null) {
                // Self Signed Certificate
                certChain = new Certificate[1];
                certChain[0] = certificate;
            } else {
                // Sign with given CA certificate
                certChain = new Certificate[2];
                certChain[0] = certificate;
                certChain[1] = issuer.getValue();
            }

            if (!KeyStoreHandler.getInstance().addKey(alias, keyPair.getPrivate(), certChain, overwrite)) {
                throw new KeyStoreException("Error while adding key to key-store");
            }
            return new SimpleImmutableEntry<KeyPair, X509Certificate>(keyPair, certificate);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (CertificateException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (NoSuchProviderException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (OperatorCreationException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static byte[] calculateFingerprint(Certificate cert) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            return md.digest(cert.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (CertificateEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return new byte[]{};
    }

    public static String printCertificate(java.security.cert.X509Certificate cert) {
        StringBuilder sb = new StringBuilder();
        if (cert.getSubjectDN() != null)
            sb.append(String.format("Subject: %s\n", cert.getSubjectDN().toString()));
        if (cert.getIssuerDN() != null)
            sb.append(String.format("Issuer: %s\n", cert.getIssuerDN().toString()));
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(
                ApplicationContextProvider.getAppContext());
        if (cert.getNotBefore() != null) {
            sb.append(String.format("Valid from: %s\n", dateFormat.format(cert.getNotBefore())));
        }
        if (cert.getNotAfter() != null) {
            sb.append(String.format("Valid to: %s\n", dateFormat.format(cert.getNotAfter())));
        }
        sb.append(String.format("Fingerprint (SHA1): %s\n", toHexString(calculateFingerprint(cert), ":")));
        return sb.toString();
    }

    public static String toHexString(byte[] bytes, String pad) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02x%s", bytes[i], pad));
        }
        return sb.toString();
    }
}
