package org.crysil.actor.u2f.nfc;

import java.security.Key;

import org.crysil.actor.u2f.U2FDeviceHandler;

/**
 * Strategy design pattern: Support different NFC devices to carry out the actual U2F operations
 */
public interface NfcU2FDeviceStrategy {

	byte[] registerPlain(byte[] clientParam, byte[] appParam, U2FDeviceHandler device) throws Exception;

	byte[] signPlain(byte[] keyHandle, byte[] clientParam, byte[] appParam, byte[] counter, U2FDeviceHandler device)
			throws Exception;

	String getVersion(U2FDeviceHandler device) throws Exception;

	Key getSecret(U2FDeviceHandler device) throws Exception;

}
