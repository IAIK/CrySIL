/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.jceprovider;

import org.crysil.logging.Logger;
import org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilHighLevelAPI;
import org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKey;
import org.crysil.errorhandling.CrySILException;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;

/**
 * The Class WrappedKeyGenerator.
 */
public class WrappedKeyGenerator extends KeyPairGenerator {
    
    /** The key type. */
    private String keyType;
    
    /** The encryptionkeys. */
    private List<CrysilKey> encryptionkeys;
    
    /** The subject. */
    private String subject;
    
    /** The current command id. */
    private String currentCommandId;

    /**
     * Instantiates a new wrapped key generator.
     *
     * @param keyType the key type
     * @param encryptionKeys the encryption keys
     * @param subject the subject
     */
    public WrappedKeyGenerator(String keyType,List<CrysilKey> encryptionKeys,String subject) {
        super("");
        this.keyType = keyType;
        this.encryptionkeys = encryptionKeys;
        this.subject = subject;
    }

    /* (non-Javadoc)
     * @see java.security.KeyPairGenerator#initialize(java.security.spec.AlgorithmParameterSpec)
     */
    @Override
    public void initialize(AlgorithmParameterSpec param) throws InvalidAlgorithmParameterException {
        if (param!=null) {
            if (!(param instanceof CommandIdParameters)) {
                throw new InvalidAlgorithmParameterException(param.getClass() + " parameters not supported");
            } else {
                currentCommandId = ((CommandIdParameters) param).getCommandId();
            }
        }
    }

    /* (non-Javadoc)
     * @see java.security.KeyPairGenerator#initialize(int, java.security.SecureRandom)
     */
    @Override
    public void initialize(int keysize, SecureRandom random) {
    }

    /* (non-Javadoc)
     * @see java.security.KeyPairGenerator#generateKeyPair()
     */
    @Override
    public KeyPair generateKeyPair() {
        try {
            CrysilHighLevelAPI.getInstance().setCurrentCommandID(currentCommandId);
            CrysilKey crysilKey = CrysilHighLevelAPI.getInstance().generateWrappedKey(keyType,encryptionkeys,subject);
            return new KeyPair((PublicKey)crysilKey,(PrivateKey)crysilKey);
        } catch (CrySILException e) {
            Logger.error("error while generating wrapped key", e);
        }
        return null;

    }
}
