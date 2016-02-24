package org.crysil.actor.u2f.nfc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Key;

import org.crysil.actor.u2f.U2FDeviceHandler;
import org.crysil.actor.u2f.U2FUtil;

/**
 * Talks to a YubiKey NEO from Yubico to execute U2F commands
 * 
 * Code from <a href="https://github.com/Yubico/android-u2f-demo">U2F demo app from Yubico</a>
 */
public class YubikeyNfcU2FDeviceStrategy implements NfcU2FDeviceStrategy {

	public static final byte[] SELECT_U2F = { 0x00, (byte) 0xa4, 0x04, 0x00, 0x08, (byte) 0xa0, 0x00, 0x00, 0x06, 0x47,
			0x2f, 0x00, 0x01 };
	public static final byte[] SELECT_YUBICO = { 0x00, (byte) 0xa4, 0x04, 0x00, 0x07, (byte) 0xa0, 0x00, 0x00, 0x05,
			0x27, 0x10, 0x02 };

	private static final byte[] GET_VERSION_COMMAND = { 0x00, (byte) 0x03, 0x00, 0x00, (byte) 0xff };

	public String getVersion(U2FDeviceHandler device) throws IOException, APDUError {
		return new String(device.send(GET_VERSION_COMMAND), Charset.forName("ASCII"));
	}

	public String registerPlain(byte[] clientParam, byte[] appParam, U2FDeviceHandler device) throws Exception {
		byte[] apdu = new byte[5 + 32 + 32 + 1];
		apdu[1] = 0x01; // ins = ENROLL
		apdu[2] = 0x03; // p1
		apdu[4] = 64; // length
		apdu[69] = (byte) 0xff; // 256 byte response
		System.arraycopy(clientParam, 0, apdu, 5, 32);
		System.arraycopy(appParam, 0, apdu, 5 + 32, 32);

		byte[] resp = device.send(apdu);
		return U2FUtil.encodeBase64Url(resp);
	}

	public String signPlain(byte[] keyHandle, byte[] clientParam, byte[] appParam, byte[] counter,
			U2FDeviceHandler device) throws Exception {
		byte[] apdu = new byte[5 + 32 + 32 + 1 + keyHandle.length + 1];
		apdu[1] = 0x02; // ins = SIGN
		apdu[2] = 0x03; // p1
		apdu[4] = (byte) (64 + 1 + keyHandle.length); // length
		apdu[apdu.length - 1] = (byte) 0xff;
		System.arraycopy(clientParam, 0, apdu, 5, 32);
		System.arraycopy(appParam, 0, apdu, 5 + 32, 32);
		apdu[5 + 64] = (byte) keyHandle.length;
		System.arraycopy(keyHandle, 0, apdu, 5 + 64 + 1, keyHandle.length);
		byte[] resp = device.send(apdu);
		return U2FUtil.encodeBase64Url(resp);
	}

	@Override
	public Key getSecret(U2FDeviceHandler device) throws Exception {
		throw new UnsupportedOperationException();
	}

}
