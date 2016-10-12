package org.crysil.receiver.jcereceiver.crysilhighlevelapi;

import org.crysil.protocol.payload.crypto.key.KeyHandle;

/**
 * This high-level API key model stores the minimal information for identifying a key on a given Crysil instance
 * To allow this identification, the following properties are included:
 * <ul>
 * <li>the Crysil key id and
 * <li>the Crysil key subId.
 * </ul>
 */
public class CrysilKeyHandle extends CrysilKey {
    
	private static final long serialVersionUID = -4409904311579757648L;

	/** The key handle. */
    protected KeyHandle KeyHandle;

    /**
     * Instantiates a new crysil key handle.
     */
    protected CrysilKeyHandle() {
    }

    /**
     * Instantiates a new crysil key handle.
     *
     * @param KeyHandle the key handle
     */
    public CrysilKeyHandle(KeyHandle KeyHandle) {
        this.KeyHandle = KeyHandle;
    }

    /**
     * Instantiates a new crysil key handle.
     *
     * @param id the id
     * @param subId the sub id
     */
    public CrysilKeyHandle(String id, String subId) {
        KeyHandle = new KeyHandle();
        KeyHandle.setId(id);
        KeyHandle.setSubId(subId);
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return KeyHandle.getId();
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) {
        KeyHandle.setId(id);
    }

    /**
     * Gets the sub id.
     *
     * @return the sub id
     */
    public String getSubId() {
        return KeyHandle.getSubId();
    }

    /**
     * Sets the sub id.
     *
     * @param subId the new sub id
     */
    public void setSubId(String subId) {
        KeyHandle.setSubId(subId);
    }

    /* (non-Javadoc)
     * @see org.crysil.communications.jcereceiver.crysilhighlevelapi.CrysilKey#getInternalRepresentation()
     */
    @Override
    public KeyHandle getInternalRepresentation() {
        return KeyHandle;
    }
}
