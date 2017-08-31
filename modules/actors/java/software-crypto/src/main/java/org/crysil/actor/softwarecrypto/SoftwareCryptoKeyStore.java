package org.crysil.actor.softwarecrypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

public interface SoftwareCryptoKeyStore {

	public PrivateKey getJCEPrivateKey(Key decryptionKey) throws KeyNotFoundException;

	public X509Certificate getX509Certificate(KeyHandle keyHandle);

	public PublicKey getJCEPublicKey(Key currentKey) throws InvalidCertificateException, KeyNotFoundException;

}