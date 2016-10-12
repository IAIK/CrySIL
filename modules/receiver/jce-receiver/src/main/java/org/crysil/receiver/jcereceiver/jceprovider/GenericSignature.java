/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.jceprovider;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;

import org.crysil.errorhandling.CrySILException;
import org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKey;

import common.CrySilAlgorithm;
import iaik.asn1.CodingException;
import iaik.pkcs.pkcs7.DigestInfo;

/**
 * The Class GenericSignature.
 */
public class GenericSignature extends SignatureSpi {

	protected CrysilProvider provider;

    /** The crysil key. */
    private CrysilKey crysilKey = null;

    /** The algorithm. */
    protected String algorithm;

    /** The digest name. */
    protected String digestName;

    /** The digest. */
    protected MessageDigest digest = null;

    /** The hash. */
    protected byte[] hash;

    /** The current command id. */
    protected String currentCommandId;


    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineSetParameter(java.security.spec.AlgorithmParameterSpec)
     */
    @Override
    protected void engineSetParameter(AlgorithmParameterSpec param) throws InvalidAlgorithmParameterException {
        if (param!=null) {
            if (!(param instanceof CommandIdParameters)) {
                throw new InvalidAlgorithmParameterException(param.getClass() + " parameters not supported");
            } else {
                currentCommandId = ((CommandIdParameters) param).getCommandId();
            }
        }
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineInitVerify(java.security.PublicKey)
     */
    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        initDataSink();
        this.crysilKey = (CrysilKey) publicKey;
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineInitSign(java.security.PrivateKey)
     */
    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        initDataSink();
        this.crysilKey = (CrysilKey) privateKey;
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineUpdate(byte)
     */
    @Override
    protected void engineUpdate(byte b) throws SignatureException {
        if (digest == null) {
            throw new SignatureException("Not implemented");
        } else {
            digest.update(b);
        }
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineUpdate(byte[], int, int)
     */
    @Override
    protected void engineUpdate(byte[] b, int offset, int length) throws SignatureException {
        if (digest == null) {
            hash = b;
        } else {
            digest.update(b, offset, length);
        }
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineSign()
     */
    @Override
    protected byte[] engineSign() throws SignatureException {
        try {
            if (digest == null) {
                DigestInfo digestInfo = new DigestInfo(hash);
                String algorithm = digestInfo.getDigestAlgorithm().getName();
                String crysilAlgorithm = "";
                if ("SHA256".equals(algorithm)) {
                    crysilAlgorithm = CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_256.getAlgorithmName();
                } else if ("SHA512".equals(algorithm)) {
                    crysilAlgorithm = CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_512.getAlgorithmName();
                } else if ("SHA224".equals(algorithm)) {
                    crysilAlgorithm = CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_224.getAlgorithmName();
                } else if ("SHA1".equals(algorithm)) {
                    crysilAlgorithm = CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_1.getAlgorithmName();
                } else {
                    throw new SignatureException("Algorithm " + algorithm + "not implemented");
                }
				provider.getApi().setCurrentCommandID(currentCommandId);
				return provider.getApi().signHashRequest(crysilAlgorithm, digestInfo.getDigest(), crysilKey);
            } else {
				provider.getApi().setCurrentCommandID(currentCommandId);
				return provider.getApi().signHashRequest(algorithm, digest.digest(), crysilKey);
            }
        } catch (CrySILException e) {
            throw new SignatureException(e);
        } catch (CodingException e) {
            throw new SignatureException(e);
        }
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineVerify(byte[])
     */
    @Override
    protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
        throw new SignatureException("Not implemented.");
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineSetParameter(java.lang.String, java.lang.Object)
     */
    @Override
    protected void engineSetParameter(String param, Object value) throws InvalidParameterException {
        throw new InvalidParameterException("Not implemented.");
    }

    /* (non-Javadoc)
     * @see java.security.SignatureSpi#engineGetParameter(java.lang.String)
     */
    @Override
    protected Object engineGetParameter(String param) throws InvalidParameterException {
        return null;
    }

    /**
     * Inits the data sink.
     */
    private void initDataSink() {
        if ("NONEwithRSA".equals(digestName)) {
            digest = null;
        } else {
            if (digest == null) {
                try {
                    digest = MessageDigest.getInstance(digestName);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return;
                }
            }

            digest.reset();
        }
    }
}
