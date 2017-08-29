package org.crysil.gatekeeperwithsessions;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.configuration.FeatureSet;

/**
 * The Configuration interface provides a GateKeeper implementation the information about required authentication methods and timely constraints.
 */
public interface Configuration {

    /**
     * Gets the authorization process.
     *
     * @param features
     *            a {@link FeatureSet}
     * @return the authorization process
     * @throws AuthenticationFailedException
     *             the authentication failed exception
     */
    public AuthorizationProcess getAuthorizationProcess(FeatureSet features) throws AuthenticationFailedException;
}
