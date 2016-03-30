package org.crysil.instance.util;

public final class StringUtils {

	/**
	 * Removes white space and cuts string down to length (if longer), used for logging
	 */
	public static String cutDown(String text, int length) {
		if (text == null)
			return null;
		String stripped = text.replace(" ", "").replace("\n", "");
		return stripped.length() > length ? stripped.substring(0, length) + "..." : stripped;
	}

}
