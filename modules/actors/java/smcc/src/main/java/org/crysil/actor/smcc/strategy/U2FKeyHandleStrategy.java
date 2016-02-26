package org.crysil.actor.smcc.strategy;

import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

import at.gv.egiz.smcc.SignatureCard;
import at.gv.egiz.smcc.pin.gui.PINGUI;

/**
 * Strategy design pattern. Handles generation and verification of a U2F key handle for signature cards.
 * 
 * Contrary to U2F tokens from Yubico, we can not generate a new private key (and then export it in a key handle) to
 * calculate signatures for every registration request, since we can only use the one ECC key on the signature card.
 */
public abstract class U2FKeyHandleStrategy {

	public boolean verifyKeyHandle(byte[] inputData, Key signatureKey, SignatureCard card, PINGUI pinGUI) {
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
		return verifyKeyHandle(keyHandle, appParam, clientParam, card, pinGUI);
	}

	protected abstract boolean verifyKeyHandle(byte[] keyHandle, byte[] appParam, byte[] clientParam,
			SignatureCard card, PINGUI pinGUI);

	public abstract Tuple<byte[], byte[]> generateKeyHandleAndRandom(byte[] encodedRandom, byte[] appParam,
			SignatureCard card, PINGUI pinGUI) throws Exception;

}
