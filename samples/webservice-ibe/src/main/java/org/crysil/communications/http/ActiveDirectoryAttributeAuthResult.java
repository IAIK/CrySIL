package org.crysil.communications.http;

import org.crysil.gatekeeperwithsessions.authentication.plugins.AuthenticationResult;

/**
 * ActiveDirectoryAuthResult contains the username of a successful authentication
 */
public class ActiveDirectoryAttributeAuthResult extends AuthenticationResult {

	/** The username. */
	private String username;
	private String mail;

	/**
	 * Instantiates a new active directory auth result.
	 *
	 * @param username the username
	 * @param mail 
	 */
	public ActiveDirectoryAttributeAuthResult(String username, String mail) {
		this.username = username;
		this.mail = mail;
	}

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets the email address.
	 *
	 * @return the email address
	 */
	public String geteMailAddress() {
		return mail;
	}

}
