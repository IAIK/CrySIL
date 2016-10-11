package org.crysil.receiver.jcereceiver.jceprovider;

import common.CrySilAlgorithm;


/**
 * The Class Signatures.
 */
public class Signatures {
    
    /**
     * The Class SHA1withRSA.
     */
    public static class SHA1withRSA extends GenericSignature {
        
        /**
         * Instantiates a new SH a1with rsa.
         */
        public SHA1withRSA() {
            this.algorithm = CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_1.getAlgorithmName();
            this.digestName = "SHA-1";
        }
    }

    /**
     * The Class SHA224withRSA.
     */
    public static class SHA224withRSA extends GenericSignature {
        
        /**
         * Instantiates a new SH a224with rsa.
         */
        public SHA224withRSA() {
            this.algorithm = CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_224.getAlgorithmName();
            this.digestName = "SHA-224";
        }
    }

    /**
     * The Class SHA256withRSA.
     */
    public static class SHA256withRSA extends GenericSignature {
        
        /**
         * Instantiates a new SH a256with rsa.
         */
        public SHA256withRSA() {
            this.algorithm = CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_256.getAlgorithmName();
            this.digestName = "SHA-256";
        }
    }

    /**
     * The Class SHA512withRSA.
     */
    public static class SHA512withRSA extends GenericSignature {
        
        /**
         * Instantiates a new SH a512with rsa.
         */
        public SHA512withRSA() {
            this.algorithm = CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_512.getAlgorithmName();
            this.digestName = "SHA-512";
        }
    }

    /**
     * The Class NONEwithRSA.
     */
    public static class NONEwithRSA extends GenericSignature {
        
        /**
         * Instantiates a new NON ewith rsa.
         */
        public NONEwithRSA() {
            this.algorithm = "NONEwithRSA";
            this.digestName = "NONEwithRSA";
        }
    }
}
