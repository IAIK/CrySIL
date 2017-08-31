package org.crysil.actor.softwarecrypto;

import java.math.BigInteger;

public class U2FUtil {

	/**
	 * Converts the byte array containing an ECDSA-SHA-256 signature (without any headers, just two 32-byte values) into
	 * the appropriate ASN.1 structure needed for U2F
	 *
	 * The result looks like this, where 0x44, 0x20, 0x20 depend on the actual lenght of the signature values. One
	 * signature value can be 33 bytes long, because values >= 0x7f need to be prepended with 0x00, because they would
	 * be considered a negative value otherwise
	 *
	 * <code>
	 * 0x30 0x44 0x02 0x20 (... 32 bytes ...) 0x02 0x20 (... 32 bytes ...)
	 * </code>
	 */
	public static byte[] ensureASN1(byte[] response) {
		if (response.length == 64) {
			byte[] r = new byte[32];
			byte[] s = new byte[32];
			System.arraycopy(response, 0, r, 0, 32);
			System.arraycopy(response, 32, s, 0, 32);

			byte[] rEnc = new BigInteger(1, r).toByteArray();
			byte[] sEnc = new BigInteger(1, s).toByteArray();

			byte[] realResponse = new byte[6 + rEnc.length + sEnc.length];
			realResponse[0] = (byte) 0x30; // SEQUENCE
			realResponse[1] = (byte) (4 + rEnc.length + sEnc.length);

			realResponse[2] = (byte) 0x02; // INTEGER
			realResponse[3] = (byte) rEnc.length;
			System.arraycopy(rEnc, 0, realResponse, 4, rEnc.length);

			realResponse[4 + rEnc.length] = (byte) 0x02; // INTEGER
			realResponse[5 + rEnc.length] = (byte) sEnc.length;
			System.arraycopy(sEnc, 0, realResponse, 6 + rEnc.length, sEnc.length);

			return realResponse;
		} else {
			return response;
		}
	}

	/**
	 * Replaces the counter value in the input data. This happens because counter needed for U2F is not known to the
	 * client (and most probably should not be), and thus inserted by the U2F receiver. So the actor gets the counter
	 * and needs to place it into the data that will be signed.
	 */
	public static void replaceCounter(byte[] inputData, int counter) {
		if (inputData.length == 69 && inputData[33] == 0 && inputData[34] == 0 && inputData[35] == 0
				&& inputData[36] == 0) {
			inputData[33] = (byte) ((counter >> 24) & 0xFF);
			inputData[34] = (byte) ((counter >> 16) & 0xFF);
			inputData[35] = (byte) ((counter >> 8) & 0xFF);
			inputData[36] = (byte) (counter & 0xFF);
		}
	}
}
