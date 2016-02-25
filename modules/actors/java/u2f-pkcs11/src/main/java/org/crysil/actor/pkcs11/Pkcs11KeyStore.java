package org.crysil.actor.pkcs11;

import iaik.pkcs.pkcs11.provider.Constants;
import iaik.pkcs.pkcs11.provider.IAIKPkcs11;
import iaik.security.ec.provider.ECCelerate;
import iaik.security.provider.IAIK;

import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.crysil.actor.pkcs11.model.KeyAndCertificate;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.logging.Logger;

public class Pkcs11KeyStore {

	private static final String KEYSTORE_TYPE = "PKCS11KeyStore";
	private final IAIKPkcs11 provider;
	private final String password;
	private KeyStore keystore = null;
	private List<KeyAndCertificate> loadedKeys = new ArrayList<>();

	public Pkcs11KeyStore(String hsmLibFile, String password) {
		this.password = password;
		
		IAIK.addAsProvider();
		ECCelerate.addAsProvider();

		Properties properties = new Properties();
		properties.setProperty(Constants.PKCS11_NATIVE_MODULE, hsmLibFile);
		properties.setProperty(Constants.USE_UTF8_ENCODING, "true");
		provider = IAIKPkcs11.getNewProviderInstance(properties);

		if (null != password) // this password has obviously no effect on readers with a pin pad
			provider.setUserPIN(password.toCharArray());
		Security.insertProviderAt(provider, 1);
	}

	public synchronized List<KeyAndCertificate> getKeys() throws CrySILException {
		if (keystore != null) {
			return loadedKeys;
		}

		loadedKeys = new ArrayList<>();
		try {
			keystore = KeyStore.getInstance(KEYSTORE_TYPE);
			keystore.load(null, null);

			for (String keyAlias : Collections.list(keystore.aliases())) {
				Key privateKey = keystore.getKey(keyAlias, null != password ? password.toCharArray() : null);
				if (!(privateKey instanceof PrivateKey))
					continue;
				Certificate certificate = keystore.getCertificate(keyAlias);
				if (null == certificate)
					continue;

				String alias = keyAlias.contains("/") ? keyAlias.substring(0, keyAlias.indexOf("/")) : keyAlias;
				KeyAndCertificate newKey = new KeyAndCertificate((PrivateKey) privateKey,
						javax.security.cert.X509Certificate.getInstance(certificate.getEncoded()), alias);
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

	public String getProviderName() {
		return provider.getName();
	}
}
