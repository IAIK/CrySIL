/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.crysil;

import org.crysil.protocol.payload.crypto.key.Key;

import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * The Class WrappedKey.
 */
public class WrappedKey extends Key {

    /** The wrapped key. */
    protected byte[] wrappedKey;

    /** The wrapped key certificate. */
    protected X509Certificate wrappedKeyCertificate;

    /**
     * Gets the wrapped key.
     *
     * @return the wrapped key
     */
    public byte[] getWrappedKey() {
        return wrappedKey;
    }

    /**
     * Sets the wrapped key.
     *
     * @param wrappedKey the new wrapped key
     */
    public void setWrappedKey(byte[] wrappedKey) {
        this.wrappedKey = ((wrappedKey != null) ? Arrays.copyOf(wrappedKey, wrappedKey.length) : null);
    }

    /**
     * Gets the wrapped key certificate.
     *
     * @return the wrapped key certificate
     */
    public X509Certificate getWrappedKeyCertificate() {
        return wrappedKeyCertificate;
    }

    /**
     * Sets the wrapped key certificate.
     *
     * @param wrappedKeyCertificate the new wrapped key certificate
     */
    public void setWrappedKeyCertificate(X509Certificate wrappedKeyCertificate) {
        this.wrappedKeyCertificate = wrappedKeyCertificate;
    }

    /* (non-Javadoc)
     * @see org.crysil.protocol.payload.crypto.key.Key#getBlankedClone()
     */
    @Override
    public Key getBlankedClone() {
        return this;
    }

    //TODO: ...
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}
}
