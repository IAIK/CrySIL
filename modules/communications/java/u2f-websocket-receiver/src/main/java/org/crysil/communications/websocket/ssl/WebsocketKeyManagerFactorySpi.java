package org.crysil.communications.websocket.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;

/**
 * Simply return a {@link WebsocketX509ExtendedKeyManager} instance
 */
public class WebsocketKeyManagerFactorySpi extends KeyManagerFactorySpi {

	private String certAlias;
	private KeyStoreInterface keyStore;

	public WebsocketKeyManagerFactorySpi(KeyStoreInterface keyStore, String certAlias) {
		this.keyStore = keyStore;
		this.certAlias = certAlias;
	}

	@Override
	protected void engineInit(KeyStore ks, char[] password) throws KeyStoreException, NoSuchAlgorithmException,
			UnrecoverableKeyException {

	}

	@Override
	protected void engineInit(ManagerFactoryParameters spec) throws InvalidAlgorithmParameterException {

	}

	@Override
	protected KeyManager[] engineGetKeyManagers() {
		return new KeyManager[] { new WebsocketX509ExtendedKeyManager(keyStore, certAlias) };
	}
}
