package org.crysil.gatekeeperwithsessions.authentication.plugins.credentials;

import org.crysil.gatekeeperwithsessions.authentication.plugins.AuthenticationResult;

/**
 * The Class SecretAuthResult.
 */
public class SecretAuthResult extends AuthenticationResult {

    /** The secret. */
    private final String secret;

    /**
     * Instantiates a new secret auth result.
     *
     * @param secret
     *            the secret
     */
    public SecretAuthResult(String secret) {
        this.secret = secret;
    }

    /**
     * Gets the secret.
     *
     * @return the secret
     */
    public String getSecret() {
        return secret;
    }

}
