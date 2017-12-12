package org.crysil.communications.websocket;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;

/**
 * Uses a specified alias from the {@link KeyStoreHandler} as the key in TLS connections for the client authentication
 */
public class WebsocketX509ExtendedKeyManager extends X509ExtendedKeyManager {

	private final String certAlias;
	private final KeyStoreInterface keyStore;

	public WebsocketX509ExtendedKeyManager(KeyStoreInterface keyStore, String certAlias) {
		this.keyStore = keyStore;
		this.certAlias = certAlias;
	}

	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		return certAlias;
	}

	@Override
	public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
		return certAlias;
	}

	@Override
	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		return null;
	}

	@Override
	public X509Certificate[] getCertificateChain(String alias) {
		return new X509Certificate[] { keyStore.getCertificate(alias) };
	}

	@Override
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		return new String[] { certAlias };
	}

	@Override
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		return new String[0];
	}

	@Override
	public PrivateKey getPrivateKey(String alias) {
		return keyStore.getKey(alias);
	}
}
