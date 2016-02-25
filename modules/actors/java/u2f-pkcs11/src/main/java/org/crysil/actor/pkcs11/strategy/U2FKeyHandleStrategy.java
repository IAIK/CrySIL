package org.crysil.actor.pkcs11.strategy;

import org.crysil.actor.pkcs11.Pkcs11KeyStore;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

/**
 * Strategy design pattern. Handles generation and verification of an U2F key handle for signature cards.
 * 
 * Contrary to U2F tokens from Yubico, we can not generate a new private key (and then export it in a key handle) to
 * calculate signatures for every registration request, since we can only use the one ECC key on the signature card.
 */
public abstract class U2FKeyHandleStrategy {

	protected static final String SIG_RSA = "SHA256withRSA";
	protected static final String RSA_KEY_ID = "rsakey3";

	public boolean verifyKeyHandle(byte[] inputData, Key signatureKey, Pkcs11KeyStore keyStore) {
		byte[] appParam = new byte[32];
		byte[] clientParam = new byte[32];
		if (!(signatureKey instanceof WrappedKey))
			return false;

		if (inputData.length == 32 + 1 + 4 + 32) { // authentication
			System.arraycopy(inputData, 0, appParam, 0, 32);
			System.arraycopy(inputData, 5 + 32, clientParam, 0, 32);
		} else { // registration
			System.arraycopy(inputData, 1, appParam, 0, 32);
			System.arraycopy(inputData, 1 + 32, clientParam, 0, 32);
		}
		WrappedKey sWrappedKey = (WrappedKey) signatureKey;
		byte[] keyHandle = sWrappedKey.getEncodedWrappedKey();
		return verifyKeyHandle(keyHandle, appParam, clientParam, keyStore);
	}

	protected abstract boolean verifyKeyHandle(byte[] keyHandle, byte[] appParam, byte[] clientParam,
			Pkcs11KeyStore keyStore);

	public abstract Tuple<byte[], byte[]> generateKeyHandleAndRandom(byte[] encodedRandom, byte[] appParam,
			Pkcs11KeyStore keyStore) throws Exception;

}
