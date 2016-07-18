package org.crysil.actor.pkcs11;

public class U2FUtil {

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
