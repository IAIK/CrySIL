package org.crysil.gatekeeperwithsessions.authentication.plugins.credentials;

import org.crysil.gatekeeperwithsessions.authentication.plugins.AuthenticationResult;

/**
 * The Class UsernamePasswordAuthResult.
 */
public class UsernamePasswordAuthResult extends AuthenticationResult {

    /** The username. */
    private final String username;

    /** The password. */
    private final String password;

    /**
     * Instantiates a new username password auth result.
     *
     * @param userName
     *            the user name
     * @param passWord
     *            the pass word
     */
    public UsernamePasswordAuthResult(String userName, String passWord) {
        username = userName;
        password = passWord;
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
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }
}
