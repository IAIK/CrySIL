/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.crysilhighlevelapi;

import java.security.cert.X509Certificate;

import org.crysil.receiver.jcereceiver.crysil.WrappedKey;

/**
 * This high-level key model represents a wrapped key, including
 * the encoded wrapped key itself and
 * the X509 Certificate of the wrapped key. The model does not enforce the binding between those two entities. This
 * must be enforced with other methods.
 */
public class CrysilWrappedKey extends CrysilKey {
    
    /** The wrapped key. */
    protected WrappedKey wrappedKey;

    /**
     * Instantiates a new crysil wrapped key.
     *
     * @param wrappedKey the wrapped key
     */
    public CrysilWrappedKey(WrappedKey wrappedKey) {
        this.wrappedKey = wrappedKey;
    }

    /**
     * Instantiates a new crysil wrapped key.
     *
     * @param encodedWrappedKey the encoded wrapped key
     */
    public CrysilWrappedKey(byte[] encodedWrappedKey) {
        wrappedKey = new WrappedKey();
        wrappedKey.setWrappedKey(encodedWrappedKey);
    }

    /**
     * Instantiates a new crysil wrapped key.
     *
     * @param encodedWrappedKey the encoded wrapped key
     * @param certificate the certificate
     */
    public CrysilWrappedKey(byte[] encodedWrappedKey,X509Certificate certificate) {
        wrappedKey = new WrappedKey();
        wrappedKey.setWrappedKey(encodedWrappedKey);
        wrappedKey.setWrappedKeyCertificate(certificate);
    }

    /**
     * Gets the x509 certificate.
     *
     * @return the x509 certificate
     */
    public X509Certificate getX509Certificate() {
        return wrappedKey.getWrappedKeyCertificate();
    }

    /**
     * Sets the x509 certificate.
     *
     * @param x509Certificate the new x509 certificate
     */
    public void setX509Certificate(X509Certificate x509Certificate) {
        wrappedKey.setWrappedKeyCertificate(x509Certificate);
    }

    /**
     * Gets the encoded wrapped key.
     *
     * @return the encoded wrapped key
     */
    public byte[] getEncodedWrappedKey() {
        return wrappedKey.getWrappedKey();
    }

    /**
     * Sets the encoded wrapped key.
     *
     * @param encodedWrappedKey the new encoded wrapped key
     */
    public void setEncodedWrappedKey(byte[] encodedWrappedKey) {
        wrappedKey.setWrappedKey(encodedWrappedKey);
    }

    /* (non-Javadoc)
     * @see org.crysil.communications.jcereceiver.crysilhighlevelapi.CrysilKey#getInternalRepresentation()
     */
    @Override
    public WrappedKey getInternalRepresentation() {
        return wrappedKey;
    }
}
