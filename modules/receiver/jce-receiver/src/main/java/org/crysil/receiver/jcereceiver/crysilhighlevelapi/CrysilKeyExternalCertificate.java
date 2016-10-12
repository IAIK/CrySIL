package org.crysil.receiver.jcereceiver.crysilhighlevelapi;

import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.crysil.protocol.payload.crypto.key.ExternalCertificate;

import iaik.utils.Util;

/**
 * This high-level API key model represents an external key that is used within a Crysil operation.
 * Such external keys (represented by their {@link java.security.cert.X509Certificate}) only are required for
 * operations that include keys which are not stored within the Crysil instance.
 * Obviously, the use cases for external keys are limited to those
 * that do not require a private key (e.g. encryption, or signature verification).
 */

public class CrysilKeyExternalCertificate extends CrysilKey {
    
	private static final long serialVersionUID = 1593909455040564569L;

	/** The external certificate. */
    protected ExternalCertificate ExternalCertificate;

    /**
     * Instantiates a new crysil key external certificate.
     *
     * @param ExternalCertificate the external certificate
     */
    public CrysilKeyExternalCertificate(ExternalCertificate ExternalCertificate) {
        this.ExternalCertificate = ExternalCertificate;
    }

    /**
     * Instantiates a new crysil key external certificate.
     *
     * @param certificate the certificate
     */
    public CrysilKeyExternalCertificate(X509Certificate certificate) {
        ExternalCertificate = new ExternalCertificate();
        try {
        	ExternalCertificate.setCertificate(certificate);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the x509 certificate.
     *
     * @return the x509 certificate
     */
    public X509Certificate getX509Certificate() {
        try {
        	iaik.x509.X509Certificate iaikCertificate = new iaik.x509.X509Certificate(ExternalCertificate.getCertificate().getEncoded());
            return Util.convertCertificate(iaikCertificate);
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.crysil.communications.jcereceiver.crysilhighlevelapi.CrysilKey#getInternalRepresentation()
     */
    @Override
    public ExternalCertificate getInternalRepresentation() {
        return ExternalCertificate;
    }
}
