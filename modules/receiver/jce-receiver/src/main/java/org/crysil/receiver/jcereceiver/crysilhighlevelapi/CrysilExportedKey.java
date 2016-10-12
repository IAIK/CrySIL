package org.crysil.receiver.jcereceiver.crysilhighlevelapi;

import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;

import org.crysil.receiver.jcereceiver.crysil.ExportedKey;

/**
 * High level API model for an exported wrapped key. The model contains the following entities:
 * the {@link java.security.spec.KeySpec}
 * the corresponding X509Certificate {@link java.security.cert.X509Certificate}
 */
public class CrysilExportedKey extends CrysilKey {
    
	private static final long serialVersionUID = -6197510932723060341L;

	/** The exported key. */
    protected ExportedKey exportedKey;

    /**
     * Instantiates a new crysil exported key.
     *
     * @param exportedKey the exported key
     */
    public CrysilExportedKey(ExportedKey exportedKey) {
        this.exportedKey = exportedKey;
    }

    /**
     * Instantiates a new crysil exported key.
     *
     * @param privateKey the private key
     * @param x509Certificate the x509 certificate
     */
    public CrysilExportedKey(KeySpec privateKey, X509Certificate x509Certificate) {
        exportedKey = new ExportedKey();
        exportedKey.setPrivateKey(privateKey);
        exportedKey.setX509Certificate(x509Certificate);
    }

    /**
     * Gets the x509 certificate.
     *
     * @return the x509 certificate
     */
    public X509Certificate getX509Certificate() {
        return exportedKey.getX509Certificate();
    }

    /**
     * Sets the x509 certificate.
     *
     * @param x509Certificate the new x509 certificate
     */
    public void setX509Certificate(X509Certificate x509Certificate) {
        exportedKey.setX509Certificate(x509Certificate);
    }

    /**
     * Gets the private key.
     *
     * @return the private key
     */
    public KeySpec getPrivateKey() {
        return exportedKey.getPrivateKey();
    }

    /**
     * Sets the private key.
     *
     * @param privateKey the new private key
     */
    public void setPrivateKey(KeySpec privateKey) {
        exportedKey.setPrivateKey(privateKey);
    }

    /* (non-Javadoc)
     * @see org.crysil.communications.jcereceiver.crysilhighlevelapi.CrysilKey#getInternalRepresentation()
     */
    @Override
    public ExportedKey getInternalRepresentation() {
        return exportedKey;
    }
}
