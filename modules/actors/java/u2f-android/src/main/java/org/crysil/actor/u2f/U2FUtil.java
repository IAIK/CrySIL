package org.crysil.actor.u2f;

public class U2FUtil {

	/**
	 * Strips meta-data of a public EC key to conform to U2F standard (from 91 bytes down to 65 bytes)
	 */
	public static byte[] stripMetaData(byte[] pubKey) {
		if (pubKey.length < 4)
			return pubKey;
		int headerLen = pubKey[3];
		if (pubKey.length < headerLen + 6)
			return pubKey;
		int keyLen = pubKey[headerLen + 5];
		if (pubKey.length < headerLen + 7 + keyLen - 1)
			return pubKey;
		byte[] key = new byte[keyLen - 1]; // strip first byte of key too
		System.arraycopy(pubKey, headerLen + 7, key, 0, keyLen - 1);
		return key;
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
