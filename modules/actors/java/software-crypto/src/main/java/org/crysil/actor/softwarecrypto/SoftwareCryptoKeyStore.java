package org.crysil.actor.softwarecrypto;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

public interface SoftwareCryptoKeyStore {

	public java.security.Key getPrivateKey(KeyHandle decryptionKey)
			throws KeyNotFoundException, KeyStoreUnavailableException;

	public X509Certificate getX509Certificate(KeyHandle keyHandle)
			throws InvalidCertificateException, KeyNotFoundException, KeyStoreUnavailableException;

	public PublicKey getPublicKey(KeyHandle currentKey)
			throws InvalidCertificateException, KeyNotFoundException, KeyStoreUnavailableException;

	public List<KeyHandle> getKeyList() throws KeyStoreUnavailableException;

	public void addFilter(Header header);

}