package org.crysil.actor.u2f;

import java.io.IOException;

import org.crysil.actor.u2f.nfc.APDUError;
import org.crysil.actor.u2f.nfc.NfcU2FDeviceStrategy;

/**
 * Interface for communicating with an U2F compatible NFC device.
 * 
 * Gets implemented by the Android app, and uses {@link NfcU2FDeviceStrategy} to select which commands to send to the
 * NFC token
 */
public interface U2FDeviceHandler {

	byte[] registerPlain(byte[] clientParam, byte[] appParam) throws Exception;

	byte[] signPlain(byte[] keyHandle, byte[] clientParam, byte[] appParam, byte[] counter) throws Exception;

	byte[] send(byte[] apdu) throws IOException, APDUError;

}