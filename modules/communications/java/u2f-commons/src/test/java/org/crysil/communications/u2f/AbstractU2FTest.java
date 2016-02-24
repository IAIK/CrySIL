package org.crysil.communications.u2f;

import java.util.Random;

public class AbstractU2FTest {

	protected static String randomString(int length) {
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		Random generator = new Random();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(chars[generator.nextInt(chars.length)]);
		}
		return sb.toString();
	}

	protected static byte[] randomBytes(int length) {
		byte[] bytes = new byte[length];
		new Random().nextBytes(bytes);
		return bytes;
	}

}