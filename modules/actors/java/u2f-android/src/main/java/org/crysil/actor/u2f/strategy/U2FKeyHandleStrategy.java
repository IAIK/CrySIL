package org.crysil.actor.u2f.strategy;

import org.crysil.actor.u2f.U2FDeviceHandler;
import org.crysil.actor.u2f.nfc.NfcU2FDeviceStrategy;
import org.crysil.actor.u2f.nfc.YubikeyNfcU2FDeviceStrategy;

/**
 * Strategy design pattern. Handles generation and verification of a U2F key handle for smart cards.
 * 
 * Contrary to U2F tokens from Yubico, we can not generate a new private key (and then export it in a key handle) to
 * calculate signatures for every registration request, since we can only use the one ECC key on the smartcard.
 * 
 * We do not use such when talking to actual Yubico tokens, as in {@link YubikeyNfcU2FDeviceStrategy}
 */
public abstract class U2FKeyHandleStrategy {

	public abstract boolean verifyKeyHandle(byte[] keyHandle, byte[] appParam, byte[] clientParam,
			U2FDeviceHandler device, NfcU2FDeviceStrategy strategy);

	public byte[] calculateKeyHandle(byte[] appParam, U2FDeviceHandler device, NfcU2FDeviceStrategy strategy)
			throws Exception {
		return calculateKeyHandle(null, appParam, device, strategy);
	}

	protected abstract byte[] calculateKeyHandle(byte[] encodedRandom, byte[] appParam, U2FDeviceHandler device,
			NfcU2FDeviceStrategy strategy) throws Exception;

}
