package org.crysil.actor.spongycastle;

import org.crysil.actor.spongycastle.model.KeyAndCertificate;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.logging.Logger;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidKeyStore {

	public static final String KEYSTORE_TYPE = "AndroidKeyStore";
	public static final String PROVIDER = "AndroidOpenSSL";

	private KeyStore keystore = null;
	private List<KeyAndCertificate> loadedKeys = new ArrayList<>();
	private final String defaultSigningKey;

	public AndroidKeyStore(String defaultSigningKey) {
		this.defaultSigningKey = defaultSigningKey;
	}

	public synchronized List<KeyAndCertificate> getKeys() throws CrySILException {
		if (keystore != null) {
			return loadedKeys;
		}
		loadedKeys = new ArrayList<>();
		try {
			final CertificateFactory cf = CertificateFactory.getInstance("X.509");
			keystore = KeyStore.getInstance(KEYSTORE_TYPE);
			keystore.load(null, null);

			for (String keyAlias : Collections.list(keystore.aliases())) {
				Key privateKey = keystore.getKey(keyAlias, null);
				if (!(privateKey instanceof PrivateKey))
					continue;
				Certificate certificate = keystore.getCertificate(keyAlias);
				if (null == certificate)
					continue;

				String alias = keyAlias.contains("/") ? keyAlias.substring(0, keyAlias.indexOf("/")) : keyAlias;
				KeyAndCertificate newKey = new KeyAndCertificate((PrivateKey) privateKey,
						(X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificate.getEncoded())), alias);
				loadedKeys.add(newKey);
			}
		} catch (Exception e) {
			Logger.error("Could not read keystore", e);
			throw new KeyStoreUnavailableException();
		}

		return loadedKeys;
	}

	public KeyAndCertificate getKey(String keyId) throws CrySILException {
		for (KeyAndCertificate current : getKeys()) {
			if (current.getAlias().equals(keyId)) {
				return current;
			}
		}

		throw new KeyNotFoundException();
	}

	public KeyAndCertificate getDefaultSigningKey() throws CrySILException {
		return getKey(defaultSigningKey);
	}
}
