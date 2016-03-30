package org.crysil.actor.pkcs11.model;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.security.cert.X509Certificate;

public class KeyAndCertificate {

	protected final PrivateKey privateKey;
	protected final X509Certificate certificate;
	protected final String alias;

	public KeyAndCertificate(PrivateKey privateKey, X509Certificate certificate, String alias) {
		this.privateKey = privateKey;
		this.certificate = certificate;
		this.alias = alias;
	}

	public X509Certificate getCertificate() {
		return certificate;
	}

	public PublicKey getPublicKey() {
		return certificate.getPublicKey();
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public String getAlias() {
		return alias;
	}
}
