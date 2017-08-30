package org.crysil.gatekeeperwithsessions.authentication.plugins.credentials;

import org.crysil.gatekeeperwithsessions.authentication.plugins.AuthenticationResult;

/**
 * The Class IdentifierAuthResult.
 */
public class IdentifierAuthResult extends AuthenticationResult {

    /** The identifier. */
    private final String identifier;

    /**
     * Instantiates a new identifier auth result.
     *
     * @param identifier
     *            the identifier
     */
    public IdentifierAuthResult(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the identifier.
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

}
