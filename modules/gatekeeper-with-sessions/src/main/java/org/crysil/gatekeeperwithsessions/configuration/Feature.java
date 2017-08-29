package org.crysil.gatekeeperwithsessions.configuration;

public abstract class Feature {

    /**
     * Gets the key.
     *
     * @return the key
     */
	public String getKey() {
        return getClass().getSimpleName();
    }
}
