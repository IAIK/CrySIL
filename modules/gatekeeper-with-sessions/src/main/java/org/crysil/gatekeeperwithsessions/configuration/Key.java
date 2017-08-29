package org.crysil.gatekeeperwithsessions.configuration;

/**
 * The Class Key.
 */
public class Key extends Feature {

    /** The key. */
	private final org.crysil.protocol.payload.crypto.key.Key key;

    /**
     * Instantiates a new key.
     *
     * @param key
     *            the key
     */
	public Key(org.crysil.protocol.payload.crypto.key.Key key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Key other = (Key) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

}
