package org.crysil.communications.websocket.ssl;

import javax.net.ssl.KeyManagerFactory;

/**
 * Simply uses {@link WebsocketKeyManagerFactorySpi}
 */
public class WebsocketKeyManagerFactory extends KeyManagerFactory {

	public WebsocketKeyManagerFactory(KeyStoreInterface keyStore, String certAlias) {
		super(new WebsocketKeyManagerFactorySpi(keyStore, certAlias), null, null);
	}
}
