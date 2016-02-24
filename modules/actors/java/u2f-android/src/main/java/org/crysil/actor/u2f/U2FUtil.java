package org.crysil.actor.u2f;

import com.google.common.io.BaseEncoding;

public class U2FUtil {

	/**
	 * Wrapper for decoding a base64 string, with handling of newlines and potential url-safe encoding.<br/>
	 * (We can't use the commons-codecs library on Android, that would have taken care of this)
	 */
	public static byte[] decodeBase64(String base64String) {
		return BaseEncoding.base64Url().decode(urlSafe(base64String));
	}

	/**
	 * Wrapper for base64 encoding
	 */
	public static String encodeBase64(byte[] bytes) {
		return BaseEncoding.base64().encode(bytes);
	}

	/**
	 * Wrapper for URL-safe base64 encoding
	 */
	public static String encodeBase64Url(byte[] bytes) {
		return BaseEncoding.base64Url().encode(bytes);
	}

	/**
	 * Converts from the usual Base64 format into a websafe variant: Replaces <code>-</code> with <code>+</code>,
	 * <code>_</code> with <code>/</code>, strips new lines, and fills with padding <code>=<code> if necessary.
	 */
	public static String deUrlSafe(String base64String) {
		base64String = base64String.replace('-', '+').replace('_', '/').replace("\r", "").replace("\n", "");
		while (base64String.length() % 4 != 0) {
			base64String += "=";
		}
		return base64String;
	}

	/**
	 * Converts from the usual Base64 format into a websafe variant: Replaces <code>+</code> with <code>-</code>,
	 * <code>/</code> with <code>_</code>, strips new lines, and strips the padding <code>=<code>
	 */
	public static String urlSafe(String base64String) {
		return base64String.replace('+', '-').replace('/', '_').replace("=", "").replace("\r", "").replace("\n", "");
	}

	final protected static char[] hexArray = "0123456789abcdef".toCharArray();

	/**
	 * Converts bytes into a hex string, for debugging
	 */
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

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
