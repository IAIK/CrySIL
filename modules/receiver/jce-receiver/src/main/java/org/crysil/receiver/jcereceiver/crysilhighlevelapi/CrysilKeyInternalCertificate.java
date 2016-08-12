/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.crysilhighlevelapi;

import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import iaik.x509.X509Certificate;

import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

/**
 * This high-level key model extends the CrysilKeyHandle and - in addition - includes the {@link iaik.x509.X509Certificate}
 * associated with the Crysil key.
 */
public class CrysilKeyInternalCertificate extends CrysilKeyHandle {
    
    /**
     * Instantiates a new crysil key internal certificate.
     *
     * @param InternalCertificate the internal certificate
     */
    public CrysilKeyInternalCertificate(InternalCertificate InternalCertificate) {
        KeyHandle = InternalCertificate;
    }

    /**
     * Instantiates a new crysil key internal certificate.
     *
     * @param id the id
     * @param subId the sub id
     * @param certificate the certificate
     * @throws CertificateEncodingException the certificate encoding exception
     */
    public CrysilKeyInternalCertificate(String id, String subId, X509Certificate certificate) throws CertificateEncodingException {
        InternalCertificate InternalCertificate = new InternalCertificate();
        InternalCertificate.setId(id);
        InternalCertificate.setSubId(subId);
        InternalCertificate.setCertificate(certificate);

        KeyHandle = InternalCertificate;
    }

    /**
     * Gets the x509 certificate.
     *
     * @return the x509 certificate
     */
    public X509Certificate getX509Certificate() {
        InternalCertificate InternalCertificate = (InternalCertificate)KeyHandle;

        try {
            //return new X509Certificate(sInternalCertificate.getEncodedCertificate().getBytes());
        	return new X509Certificate(InternalCertificate.getCertificate().getEncoded());
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.crysil.communications.jcereceiver.crysilhighlevelapi.CrysilKeyHandle#getInternalRepresentation()
     */
    @Override
    public InternalCertificate getInternalRepresentation() {
        return (InternalCertificate)KeyHandle;
    }
}
