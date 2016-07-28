package org.crysil.communications.u2f;

import java.io.IOException;
import java.security.MessageDigest;

public class U2FUtil {

	/**
	 * Strips meta-data of a public EC key to conform to U2F standard (from 91 bytes down to 65 bytes)
	 */
	public static byte[] stripMetaData(byte[] pubKey) throws IOException {
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
	 * Calculates the SHA-256 digest of a given text (converted to UTF-8 bytes)
	 */
	public static byte[] calculateDigest(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(text.getBytes("UTF-8"));
			return md.digest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text.getBytes();
	}

}
