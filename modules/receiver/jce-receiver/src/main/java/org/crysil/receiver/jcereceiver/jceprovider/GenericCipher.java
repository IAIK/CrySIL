/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.jceprovider;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.crysil.errorhandling.CrySILException;
import org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKey;

/**
 * The Class GenericCipher.
 */
public class GenericCipher extends CipherSpi {

	protected CrysilProvider provider;
    
    /** The opmode. */
    protected int opmode;
    
    /** The crysil key. */
    protected CrysilKey crysilKey = null;
    
    /** The byte stream. */
    protected final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    
    /** The algorithm. */
    protected String algorithm;
    
    /** The current command id. */
    protected String currentCommandID;

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineDoFinal(byte[], int, int)
     */
    @Override
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen)
            throws IllegalBlockSizeException, BadPaddingException {
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

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineDoFinal(byte[], int, int, byte[], int)
     */
    @Override
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset)
            throws IllegalBlockSizeException, BadPaddingException {
        byte[] out = engineDoFinal(input, inputOffset, inputLen);

        for (int i = 0; i != out.length; i++) {
            output[outputOffset + i] = out[i];
        }

        return out.length;
    }

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineGetBlockSize()
     */
    @Override
    protected int engineGetBlockSize() {
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineGetIV()
     */
    @Override
    protected byte[] engineGetIV() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineGetOutputSize(int)
     */
    @Override
    protected int engineGetOutputSize(int arg0) {
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineGetParameters()
     */
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineInit(int, java.security.Key, java.security.SecureRandom)
     */
    @Override
    protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        if (!(key instanceof CrysilKey)) {
            throw new InvalidKeyException("Key not suitable for encryption.");
        }

        this.opmode = opmode;
        this.crysilKey = (CrysilKey) key;
    }

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineInit(int, java.security.Key, java.security.spec.AlgorithmParameterSpec, java.security.SecureRandom)
     */
    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec param, SecureRandom random)
            throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (param!=null) {
            if (!(param instanceof CommandIdParameters)) {
                throw new InvalidAlgorithmParameterException(param.getClass() + " parameters not supported");
            } else {
                currentCommandID = ((CommandIdParameters) param).getCommandId();
            }
        }
        engineInit(opmode, key, random);
    }

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineInit(int, java.security.Key, java.security.AlgorithmParameters, java.security.SecureRandom)
     */
    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random)
            throws InvalidKeyException, InvalidAlgorithmParameterException {
        engineInit(opmode, key, random);
    }

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineSetMode(java.lang.String)
     */
    @Override
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
    }

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineSetPadding(java.lang.String)
     */
    @Override
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
    }

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineUpdate(byte[], int, int)
     */
    @Override
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        byteStream.write(input, inputOffset, inputLen);

        return null;
    }

    /* (non-Javadoc)
     * @see javax.crypto.CipherSpi#engineUpdate(byte[], int, int, byte[], int)
     */
    @Override
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) {
        engineUpdate(input, inputOffset, inputLen);

        return 0;
    }
}
