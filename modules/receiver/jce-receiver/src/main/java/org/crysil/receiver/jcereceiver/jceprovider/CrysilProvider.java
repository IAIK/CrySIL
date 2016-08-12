/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.jceprovider;

import common.CrySilAlgorithm;

import java.security.Provider;

/**
 * The Class CrysilProvider.
 */
public final class CrysilProvider extends Provider {
    
    /**
     * Instantiates a new crysil provider.
     */
    public CrysilProvider() {
        super("Crysil", 1.0, "Crysil Security Provider");

        // Keystore
        put("KeyStore.Crysil", "org.crysil.communications.jcereceiver.jceprovider.CrysilKeyStore");

        // Ciphers
        put("Cipher.RSA", "org.crysil.communications.jcereceiver.jceprovider.Ciphers$RSAES_RAW");
        put("Cipher.RSA/RAW", "org.crysil.communications.jcereceiver.jceprovider.Ciphers$RSAES_RAW");
        put("Cipher.RSA/ECB/NOPADDING", "org.crysil.communications.jcereceiver.jceprovider.Ciphers$RSAES_RAW");
        put("Alg.Alias.Cipher.RSA//RAW", "RSA");
        put("Alg.Alias.Cipher.RSA//NOPADDING", "RSA");

        put("Cipher.RSA/PKCS1", "org.crysil.communications.jcereceiver.jceprovider.Ciphers$RSAES_PKCS1_V1_5");
        put("Alg.Alias.Cipher.RSA//PKCS1PADDING", "RSA/PKCS1");

        put("Cipher.RSA/OAEP", "org.crysil.communications.jcereceiver.jceprovider.Ciphers$RSA_OAEP");
        put("Alg.Alias.Cipher.RSA//OAEPPADDING", "RSA/OAEP");

        put("Cipher.CMS", "org.crysil.communications.jcereceiver.jceprovider.Ciphers$CMS");

        // Signatures
        put("Signature." + CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_1.getAlgorithmName(),
                "org.crysil.communications.jcereceiver.jceprovider.Signatures$SHA1withRSA");
        put("Signature." + CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_224.getAlgorithmName(),
                "org.crysil.communications.jcereceiver.jceprovider.Signatures$SHA224withRSA");
        put("Signature." + CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_256.getAlgorithmName(),
                "org.crysil.communications.jcereceiver.jceprovider.Signatures$SHA256withRSA");
        put("Signature." + CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_512.getAlgorithmName(),
                "org.crysil.communications.jcereceiver.jceprovider.Signatures$SHA512withRSA");
        put("Signature.NONEwithRSA",
                "org.crysil.communications.jcereceiver.jceprovider.Signatures$NONEwithRSA");

        put("Signature.SHA1withRSA", "org.crysil.communications.jcereceiver.jceprovider.Signatures$SHA1withRSA");
        put("Signature.SHA224withRSA", "org.crysil.communications.jcereceiver.jceprovider.Signatures$SHA224withRSA");
        put("Signature.SHA256withRSA", "org.crysil.communications.jcereceiver.jceprovider.Signatures$SHA256withRSA");
        put("Signature.SHA512withRSA", "org.crysil.communications.jcereceiver.jceprovider.Signatures$SHA512withRSA");
    }
}
