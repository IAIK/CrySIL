package org.crysil.actor.pkcs11.strategy;

import java.security.Signature;

import org.crysil.actor.pkcs11.Pkcs11KeyStore;

/**
 * This implements a simple key handle strategy: We only sign the app Param with the RSA key from the card. This has the
 * disadvantage that every keyhandle for the same relying party looks the same.
 */
public class SimpleSignedKeyHandleStrategy extends U2FKeyHandleStrategy {

	@Override
	protected boolean verifyKeyHandle(byte[] keyHandle, byte[] appParam, byte[] clientParam, Pkcs11KeyStore keyStore) {
		try {
			Signature signature = Signature.getInstance(SIG_RSA);
			signature.initVerify(keyStore.getKey(RSA_KEY_ID).getPublicKey());
			signature.update(appParam);
			return signature.verify(keyHandle);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Tuple<byte[], byte[]> generateKeyHandleAndRandom(byte[] encodedRandom, byte[] appParam,
			Pkcs11KeyStore keyStore) throws Exception {
		byte[] input = appParam;
		if (encodedRandom != null && encodedRandom.length > 0)
			input = encodedRandom;

		Signature signature = Signature.getInstance(SIG_RSA, keyStore.getProviderName());
		signature.initSign(keyStore.getKey(RSA_KEY_ID).getPrivateKey());
		signature.update(input);
		byte[] sig = signature.sign();
		return new Tuple<byte[], byte[]>(sig, input);
	}

}
