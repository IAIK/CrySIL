package at.tugraz.iaik.skytrust.utils;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import at.tugraz.iaik.skytrust.database.DatabaseHandler;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.AlgorithmID;
import iaik.asn1.structures.Name;
import iaik.pkcs.pkcs10.CertificateRequest;
import iaik.x509.X509Certificate;

import static java.util.AbstractMap.*;

/**
 * Helper class for creating keys and certificates
 */
public class CertificateUtils {

    public static final String TAG = CertificateUtils.class.getSimpleName();

    private static final String FILE_PROVIDER = "at.tugraz.iaik.skytrust.fileprovider";
    private static final String CERT_CA_FOLDER = "certs";
    private static final String CERT_CA_FILENAME = "skytrust_ca.cert";

    private static final String CERT_CA_ALIAS = "skytrust-ca";
    private static final String CERT_CA_COUNTRY = "Austria";
    private static final String CERT_CA_ORGANIZATION = "TU Graz";
    private static final String CERT_CA_ORGANIZATIONAL_UNIT = "IAIK";
    private static final String CERT_CA_COMMON_NAME = "tugraz-iaik-skytrust";
    private static final String CERT_CA_EMAIL = "skytrust@iaik.tugraz.at";

    private static final int CERT_KEYSIZE_DEFAULT = 2048;
    private static final AlgorithmID CERT_ALGORITHM_ID_DEFAULT = AlgorithmID.sha256WithRSAEncryption;
    private static final String CERT_ALGORITHM_DEFAULT = "RSA";
    private static final String CERT_PROVIDER_DEFAULT = "IAIK";
    private static final String CERT_SIGN_ALG = "SHA256withRSA";
    private static final String CERT_SIGN_PROVIDER = "AndroidOpenSSL";

    private static final String CERT_MAIL_ALIAS = "android-mail";

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

    public static CertificateRequest createWebserviceCsr(String alias, String skytrustId) {
        SimpleImmutableEntry<KeyPair, X509Certificate> webVpnCertificate = createKeyAndCert(CERT_PROVIDER_DEFAULT,
                CERT_ALGORITHM_ID_DEFAULT, CERT_ALGORITHM_DEFAULT, CERT_KEYSIZE_DEFAULT, alias, CERT_CA_COMMON_NAME,
                CERT_CA_EMAIL, CERT_CA_COUNTRY, CERT_CA_ORGANIZATION, CERT_CA_ORGANIZATIONAL_UNIT, null, true);
        if (webVpnCertificate == null) {
            Log.e(TAG, String.format("Error at creation of CSR for alias %s", alias));
            return null;
        }
        CertificateRequest certRequest = null;
        try {
            Name certSubject = new Name();
            certSubject.addRDN(ObjectID.commonName, skytrustId);
            certSubject.addRDN(ObjectID.emailAddress, CERT_CA_EMAIL);
            certRequest = new CertificateRequest(webVpnCertificate.getKey().getPublic(), certSubject);
            certRequest.sign(AlgorithmID.sha256WithRSAEncryption, webVpnCertificate.getKey().getPrivate());
            Log.d(TAG, String.format("Created a new CSR for alias %s", alias));
        } catch (InvalidKeyException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (SignatureException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return certRequest;
    }

    private static SimpleImmutableEntry<PrivateKey, Certificate> getOrCreateCaKeyAndCert() {
        PrivateKey issuerKey = KeyStoreHandler.getInstance().getKey(CERT_CA_ALIAS);
        Certificate issuerCertificate = KeyStoreHandler.getInstance().getCertificate(CERT_CA_ALIAS);
        if (issuerCertificate == null || issuerKey == null) {
            SimpleImmutableEntry<KeyPair, X509Certificate> caCertificate = createKeyAndCert(CERT_PROVIDER_DEFAULT,
                    CERT_ALGORITHM_ID_DEFAULT, CERT_ALGORITHM_DEFAULT, CERT_KEYSIZE_DEFAULT, CERT_CA_ALIAS,
                    CERT_CA_COMMON_NAME, CERT_CA_EMAIL, CERT_CA_COUNTRY, CERT_CA_ORGANIZATION,
                    CERT_CA_ORGANIZATIONAL_UNIT, null, true);
            if (caCertificate == null) {
                Log.e(TAG, "Error at creation of CA certificate");
                return null;
            }
            boolean caFileWritten = CertificateUtils.createCaCertFile(caCertificate.getValue());
            if (!caFileWritten) {
                Log.e(TAG, "Error at exporting the CA certificate");
                KeyStoreHandler.getInstance().deleteKey(CERT_CA_ALIAS);
                return null;
            }
            return new SimpleImmutableEntry<PrivateKey, Certificate>(caCertificate.getKey().getPrivate(),
                    caCertificate.getValue());
        }
        return new SimpleImmutableEntry<PrivateKey, Certificate>(issuerKey, issuerCertificate);
    }

    public static boolean createInitialKeyAndCert(Activity activity, String commonName, String emailAddress) {
        SimpleImmutableEntry<PrivateKey, Certificate> issuer = getOrCreateCaKeyAndCert();
        if (issuer == null) {
            return false;
        }
        SimpleImmutableEntry<KeyPair, X509Certificate> certificate = createKeyAndCert(CERT_PROVIDER_DEFAULT,
                CERT_ALGORITHM_ID_DEFAULT, CERT_ALGORITHM_DEFAULT, CERT_KEYSIZE_DEFAULT, CERT_MAIL_ALIAS, commonName,
                emailAddress, null, null, null, issuer, true);
        if (certificate == null) {
            Log.e(TAG, "Error at creation of issued certificate");
            KeyStoreHandler.getInstance().deleteKey(CERT_CA_ALIAS);
            return false;
        }
        DatabaseHandler handler = new DatabaseHandler(activity);
        handler.insertKey(CERT_MAIL_ALIAS);
        handler.close();
        return true;
    }

    public static boolean createKeyAndCert(String alias,
                                           String country,
                                           String organization,
                                           String organizationalUnit,
                                           String commonName,
                                           String emailAddress) {
        SimpleImmutableEntry<PrivateKey, Certificate> issuer = getOrCreateCaKeyAndCert();
        if (issuer == null) {
            return false;
        }
        SimpleImmutableEntry<KeyPair, X509Certificate> pair = createKeyAndCert(CERT_PROVIDER_DEFAULT,
                CERT_ALGORITHM_ID_DEFAULT, CERT_ALGORITHM_DEFAULT, CERT_KEYSIZE_DEFAULT, alias, commonName,
                emailAddress, country, organization, organizationalUnit, issuer, false);
        return pair != null;
    }

    private static SimpleImmutableEntry<KeyPair, X509Certificate> createKeyAndCert(String provider,
                                                                                   AlgorithmID algorithmID,
                                                                                   String algorithm,
                                                                                   int keySize,
                                                                                   String alias,
                                                                                   String commonName,
                                                                                   String emailAddress,
                                                                                   String country,
                                                                                   String organization,
                                                                                   String organizationalUnit,
                                                                                   SimpleImmutableEntry<PrivateKey, Certificate> issuer,
                                                                                   boolean overwrite) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm, provider);
            keyPairGenerator.initialize(keySize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Cerate the subject of the new certificate
            Name certSubject = new Name();
            if (country != null) {
                certSubject.addRDN(ObjectID.country, country);
            }
            if (organization != null) {
                certSubject.addRDN(ObjectID.organization, organization);
            }
            if (organizationalUnit != null) {
                certSubject.addRDN(ObjectID.organizationalUnit, organizationalUnit);
            }
            certSubject.addRDN(ObjectID.commonName, commonName);
            certSubject.addRDN(ObjectID.emailAddress, emailAddress);

            // Create the new certificate
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            X509Certificate certificate = new X509Certificate();
            certificate.setSubjectDN(certSubject);
            certificate.setSerialNumber(BigInteger.valueOf(Math.abs((new Random()).nextLong() + 1)));
            certificate.setPublicKey(keyPair.getPublic());
            certificate.setValidNotBefore(gregorianCalendar.getTime());
            gregorianCalendar.add(Calendar.MONTH, 6);
            certificate.setValidNotAfter(gregorianCalendar.getTime());

            Certificate[] certChain;
            if (issuer == null) {
                // Self Signed Certificate
                certificate.setIssuerDN(certSubject);
                certificate.sign(algorithmID, keyPair.getPrivate());
                certChain = new Certificate[1];
                certChain[0] = certificate;
            } else {
                // Sign with given CA certificate
                PrivateKey issuerKey = issuer.getKey();
                Certificate issuerCertificate = issuer.getValue();
                if (issuerCertificate instanceof X509Certificate) {
                    certificate.setIssuerDN(((X509Certificate) issuerCertificate).getSubjectDN());
                }
                certificate.setSignatureAlgorithm(algorithmID);
                Signature s = Signature.getInstance(CERT_SIGN_ALG, CERT_SIGN_PROVIDER);
                s.initSign(issuerKey);
                s.update(certificate.getTBSCertificate());
                certificate.setSignature(s.sign());
                certChain = new Certificate[2];
                certChain[0] = certificate;
                certChain[1] = issuerCertificate;
            }

            if (!KeyStoreHandler.getInstance().addKey(alias, keyPair.getPrivate(), certChain, overwrite)) {
                throw new KeyStoreException("Error while adding key to key-store");
            }
            return new SimpleImmutableEntry<KeyPair, X509Certificate>(keyPair, certificate);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (CertificateException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (InvalidKeyException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (NoSuchProviderException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (KeyStoreException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (SignatureException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    private static boolean createCaCertFile(X509Certificate certificate) {
        try {
            File folder = new File(getCaCertFileFolderName());
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    return false;
                }
            }
            File file = new File(getCaCertFileName());
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(certificate.getEncoded());
            fos.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (CertificateEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    private static String getCaCertFileFolderName() {
        return ApplicationContextProvider.getAppContext().getFilesDir() + File.separator +
                CERT_CA_FOLDER;
    }

    private static String getCaCertFileName() {
        return getCaCertFileFolderName() + File.separator + CERT_CA_FILENAME;
    }

    public static Uri getCaCertFile() {
        File file = new File(getCaCertFileName());
        if (!file.exists()) {
            return null;
        }
        return FileProvider.getUriForFile(ApplicationContextProvider.getAppContext(), FILE_PROVIDER, file);
    }

    public static X509Certificate getCaCertIfValid() {
        return KeyStoreHandler.getInstance().getCertificate(CERT_CA_ALIAS);
    }
}
