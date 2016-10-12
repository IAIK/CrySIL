package org.crysil.receiver.jcereceiver.jceprovider;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import org.crysil.errorhandling.CrySILException;

import common.CrySilAlgorithm;

/**
 * The Class Ciphers.
 */
public class Ciphers {
    
    /**
     * The Class RSAES_RAW.
     */
    public static class RSAES_RAW extends GenericCipher {
        
        /**
         * Instantiates a new rsaes raw.
         */
        public RSAES_RAW() {
            this.algorithm = CrySilAlgorithm.RSAES_RAW.getAlgorithmName();
        }
    }

    /**
     * The Class RSAES_PKCS1_V1_5.
     */
    public static class RSAES_PKCS1_V1_5 extends GenericCipher {
        
        /**
         * Instantiates a new RSAE s_ pkc s1_ v1_5.
         */
        public RSAES_PKCS1_V1_5() {
            this.algorithm = CrySilAlgorithm.RSAES_PKCS1_V1_5.getAlgorithmName();
        }
    }

    /**
     * The Class RSA_OAEP.
     */
    public static class RSA_OAEP extends GenericCipher {
        
        /**
         * Instantiates a new rsa oaep.
         */
        public RSA_OAEP() {
            this.algorithm = CrySilAlgorithm.RSA_OAEP.getAlgorithmName();
        }
    }

    /**
     * The Class CMS.
     */
    public static class CMS extends GenericCipher {
        
        /* (non-Javadoc)
         * @see org.crysil.communications.jcereceiver.jceprovider.GenericCipher#engineInit(int, java.security.Key, java.security.SecureRandom)
         */
        @Override
        protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
            this.algorithm = CrySilAlgorithm.CMS_AES_256_CBC.getAlgorithmName();

            super.engineInit(opmode, key, random);
        }

        /* (non-Javadoc)
         * @see org.crysil.communications.jcereceiver.jceprovider.GenericCipher#engineDoFinal(byte[], int, int)
         */
        @Override
        protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
            if (crysilKey == null || input == null) {
                throw new IllegalBlockSizeException("Unable to process the provided input data.");
            }

            byteStream.write(input, inputOffset, inputLen);
            byte[] bytes = byteStream.toByteArray();
            byteStream.reset();

            try {
                byte[] load;
                if (opmode == Cipher.DECRYPT_MODE) {
					provider.getApi().setCurrentCommandID(currentCommandID);
					load = provider.getApi().decryptDataRequest(algorithm, bytes, crysilKey);
                } else {
					provider.getApi().setCurrentCommandID(currentCommandID);
					load = provider.getApi().encryptDataRequest(algorithm, bytes, crysilKey);
                }
                return load;
            } catch (CrySILException e) {
                throw new IllegalBlockSizeException(e.getMessage());
            }
        }
    }
}
