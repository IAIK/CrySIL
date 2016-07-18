package org.crysil.communications.websocket.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

public class WebsocketKeyStore implements KeyStoreInterface {
	
	private final KeyStore keyStore;
	private final char[] password;
	
	public WebsocketKeyStore(KeyStore keyStore, char[] password) {
		this.keyStore = keyStore;
		this.password = password;
	}
	
	@Override
	public X509Certificate getCertificate(String alias) {
		try {
			return (X509Certificate) keyStore.getCertificate(alias);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public PrivateKey getKey(String alias) {
		try {
			return (PrivateKey) keyStore.getKey(alias, password);
		} catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
		}
		return null;
	}
}