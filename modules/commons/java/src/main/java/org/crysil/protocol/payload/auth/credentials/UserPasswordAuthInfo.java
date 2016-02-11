package org.crysil.protocol.payload.auth.credentials;

import org.crysil.logging.Logger;
import org.crysil.protocol.payload.auth.AuthInfo;

/**
 * {@link AuthInfo} implementation of a username/password tuple.
 */
public class UserPasswordAuthInfo extends AuthInfo {

	/** The user name. */
	protected String userName = "";

	/** The pass word. */
	protected String passWord = "";

	@Override
	public String getType() {
		return "UserNamePasswordAuthInfo";
	}

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the username.
	 *
	 * @param userName
	 *            the username
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassWord() {
		return passWord;
	}

	/**
	 * Sets the password.
	 *
	 * @param passWord
	 *            the password
	 */
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	@Override
	public AuthInfo getBlankedClone() {
		UserPasswordAuthInfo result = new UserPasswordAuthInfo();
		result.userName = Logger.isTraceEnabled() ? userName : "*****";
		result.passWord = Logger.isTraceEnabled() ? passWord : "*****";

		return result;
	}
}
