package at.iaik.skytrust.element.actors.iaikjce;

import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.keystorage.rest.client.RestKeyStorage;
import at.iaik.skytrust.keystorage.rest.client.moveaway.JCEKeyAndCertificate;
import iaik.asn1.structures.AlgorithmID;
import iaik.pkcs.pkcs1.RSASSAPkcs1v15ParameterSpec;
import iaik.security.provider.IAIK;
import iaik.x509.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/16/13
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleJCEImpl implements JCE {

    protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    protected RestKeyStorage restKeyStorage;

    protected String mapW3CAlgorithmToJCE(String w3cAlgorithm) {
        if (SkyTrustAlgorithm.RSA_OAEP.getAlgorithmName().equals(w3cAlgorithm)) {
            return "RSAES/ECB/OAEP";    //TODO
        } else if (SkyTrustAlgorithm.RSAES_RAW.getAlgorithmName().equals(w3cAlgorithm)) {
            return "RSA/ECB/NOPADDING";
        } else if (SkyTrustAlgorithm.RSAES_PKCS1_V1_5.getAlgorithmName().equals(w3cAlgorithm)) {
            return "RSA/ECB/PKCS1Padding";
        } else if (SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_1.getAlgorithmName().equals(w3cAlgorithm)) {
            return "RawRSASSA-PKCS1-v1_5/SHA-1";
        } else if (SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_224.getAlgorithmName().equals(w3cAlgorithm)) {
            return "RawRSASSA-PKCS1-v1_5/SHA-224";
        } else if (SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_256.getAlgorithmName().equals(w3cAlgorithm)) {
            return "RawRSASSA-PKCS1-v1_5/SHA-256";
        } else if (SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_512.getAlgorithmName().equals(w3cAlgorithm)) {
            return "RawRSASSA-PKCS1-v1_5/SHA-512";
        } else {
            return null;
        }


    }

    protected AlgorithmID getHashAlgorithm(String hash) {
        if ("SHA-1".equals(hash)) {
            return AlgorithmID.sha1;
        } else if ("SHA-224".equals(hash)) {
            return AlgorithmID.sha224;
        } else if ("SHA-256".equals(hash)) {
            return AlgorithmID.sha256;
        } else if ("SHA-512".equals(hash)) {
            return AlgorithmID.sha512;
        } else {
            return null;
        }

    }


	public SimpleJCEImpl(String keystorage_baseurl) {
        logger.info("Initializing IAIK Provider");
        Security.addProvider(new IAIK());
        logger.info("Initializing REST Key Storage Provider");
		restKeyStorage = RestKeyStorage.getInstance(keystorage_baseurl);
    }

    @Override
    public X509Certificate getCertificate(String keyId, String subKeyId, String userId) {
        JCEKeyAndCertificate jceKeyAndCertificate = restKeyStorage.getKey(userId, keyId,subKeyId);
        return jceKeyAndCertificate.getX509Certificate();
    }

    @Override
    public byte[] decrypt(byte[] data, String keyId, String subKeyId, String userId, String algorithm) {
        String jceAlgorithm = mapW3CAlgorithmToJCE(algorithm);
        if (jceAlgorithm==null) {
            return null;
        }

        logger.info("Get key with userid" + userId + ", keyId" + keyId + " from Rest Server");
        //TODO handle communication problems
        JCEKeyAndCertificate jceKeyAndCertificate = restKeyStorage.getKey(userId, keyId,subKeyId);
        try {
            logger.info("Setting up ciper to decrypt data");
            Cipher rsacipher = Cipher.getInstance(jceAlgorithm, "IAIK");
            rsacipher.init(Cipher.DECRYPT_MODE, jceKeyAndCertificate.getPrivateKey(), getSecureRandom());
            logger.info("Decrypting data");
            byte[] dec = rsacipher.doFinal(data);
            logger.info("Data decrypted successfully");
            return dec;
        } catch (NoSuchAlgorithmException e) {
            logger.error("Algorithm identifier does not exist", e);
        } catch (NoSuchProviderException e) {
            logger.error("NoSuchProviderException Exception");
        } catch (NoSuchPaddingException e) {
            logger.error("NoSuchPaddingException Exception");
        } catch (IllegalBlockSizeException e) {
            logger.error("IllegalBlockSizeException Exception");
        } catch (BadPaddingException e) {
            logger.error("BadPaddingException Exception");
        } catch (InvalidKeyException e) {
            logger.error("InvalidKeyException Exception");
        }
        return null;
    }

    @Override
    public byte[] sign(byte[] data, String keyId, String subKeyId, String userId, String algorithm) {
        if ("RSAES-RAW".equals(algorithm)) {
            algorithm="RSASSA-PKCS1-v1_5-SHA-1";
        }
        String jceAlgorithm = mapW3CAlgorithmToJCE(algorithm);
        if (jceAlgorithm==null) {
            return null;
        }

        logger.info("Get key with userid" + userId + ", keyId" + keyId + " from Rest Server");
        JCEKeyAndCertificate jceKeyAndCertificate = restKeyStorage.getKey(userId, keyId, subKeyId);

        //do encryption with private key
        if ("RSA/ECB/NOPADDING".equals(jceAlgorithm) || ("RSA/ECB/PKCS1Padding".equals(jceAlgorithm))) {
            Cipher rsacipher = null;
            try {
                rsacipher = Cipher.getInstance(jceAlgorithm, "IAIK");
                rsacipher.init(Cipher.ENCRYPT_MODE, jceKeyAndCertificate.getPrivateKey(), getSecureRandom());
                logger.info("Signing data");
                byte[] sig = rsacipher.doFinal(data);
                return sig;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (NoSuchProviderException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (BadPaddingException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvalidKeyException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            String rsa = jceAlgorithm.split("/")[0];
            String hash = jceAlgorithm.split("/")[1];
            AlgorithmID hashAlgorithm = getHashAlgorithm(hash);

            try {
                logger.info("Setting up signature algorithms");
                byte[] signature = null;
                Key key = jceKeyAndCertificate.getPrivateKey();

                // raw rsa signature without hashing
                // test raw signature (DigestInfo calculated inside)
                Signature rawRsaSignatureEngine = Signature.getInstance(rsa, "IAIK");

                // init Signature object with private keys
                rawRsaSignatureEngine.initSign((PrivateKey) key);

                // supply hash algorithm id as parameter
                RSASSAPkcs1v15ParameterSpec params = new RSASSAPkcs1v15ParameterSpec(hashAlgorithm);
                // set parameters
                rawRsaSignatureEngine.setParameter(params);

                // create the signature
                logger.info("Creating signature");
                rawRsaSignatureEngine.update(data);
                signature = rawRsaSignatureEngine.sign();
                logger.info("Signature creation header");
                return signature;
            } catch (NoSuchProviderException e) {
                logger.error("Provider not known", e);
            } catch (NoSuchAlgorithmException e) {
                logger.error("Algorithm not known", e);
            } catch (InvalidKeyException e) {
                logger.error("Invalid Key", e);
            } catch (InvalidAlgorithmParameterException e) {
                logger.error("Invalid algorithm parameter", e);
            } catch (SignatureException e) {
                logger.error("Singature failed", e);
            }
        }

        return null;
    }

    @Override
    public byte[] encrypt(byte[] data, String keyId, String subKeyId, String userId, String algorithm) {
        // TODO Define algorithm Identifiers!!
        String jceAlgorithm = mapW3CAlgorithmToJCE(algorithm);
        if (jceAlgorithm==null) {
            return null;
        }

        logger.info("Get key with userid" + userId + ", keyId" + keyId + " from Rest Server");
        JCEKeyAndCertificate jceKeyAndCertificate = restKeyStorage.getKey(userId, keyId,subKeyId);
        try {
            logger.info("Setting up ciper to encrypt data");
            Cipher rsacipher = Cipher.getInstance(jceAlgorithm, "IAIK");
            rsacipher.init(Cipher.ENCRYPT_MODE, jceKeyAndCertificate.getX509Certificate().getPublicKey(), getSecureRandom());
            logger.info("Encrypting data");
            byte[] enc = rsacipher.doFinal(data);
            logger.info("Encrypting data header");
            return enc;
        } catch (NoSuchAlgorithmException e) {
            logger.error("No such algorithm", e);
        } catch (NoSuchProviderException e) {
            logger.error("NoSuchProviderException Exception", e);
        } catch (NoSuchPaddingException e) {
            logger.error("NoSuchPaddingException Exception", e);
        } catch (IllegalBlockSizeException e) {
            logger.error("IllegalBlockSizeException Exception", e);
        } catch (BadPaddingException e) {
            logger.error("BadPaddingException Exception", e);
        } catch (InvalidKeyException e) {
            logger.error("InvalidKeyException Exception", e);
        }
        return null;
    }

    private SecureRandom getSecureRandom() throws NoSuchAlgorithmException, NoSuchProviderException {
        return SecureRandom.getInstance("SHA1PRNG", "IAIK");

    }

}
