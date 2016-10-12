/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.crysilhighlevelapi;

/**
 * Crysil key representation
 * Currently, only two representations are used
 * handle: this corresponds to the {@link org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilKeyHandle} key type.
 * certificate: this corresponds to the {@link org.crysil.communications.jcereceiver.crysilhighlevelapi.CrysilInternalCertificate} key type.
 */
public enum KeyRepresentation {
    
    /** The handle. */
    HANDLE("handle"),
    
    /** The certificate. */
    CERTIFICATE("certificate");

    /** The handle. */
    protected String handle;

    /**
     * Instantiates a new key representation.
     *
     * @param handle the handle
     */
    KeyRepresentation(String handle) {
        this.handle = handle;
    }

    /**
     * Gets the handle as string.
     *
     * @return the handle as string
     */
    public String getHandleAsString() {
        return handle;
    }
}
