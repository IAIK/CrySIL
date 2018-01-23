package org.crysil.communications.http;

import org.crysil.gatekeeperwithsessions.authentication.plugins.AuthenticationResult;

/**
 * ActiveDirectoryAuthResult contains the username of a successful authentication
 */
public class ActiveDirectoryAuthResult extends AuthenticationResult {

	/** The username. */
	private String username;

	/**
	 * Instantiates a new active directory auth result.
	 *
	 * @param username the username
	 */
	public ActiveDirectoryAuthResult(String username) {
		this.username = username;
	}

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

}
