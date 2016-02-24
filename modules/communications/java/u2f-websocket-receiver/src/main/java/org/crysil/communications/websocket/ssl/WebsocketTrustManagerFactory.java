package org.crysil.communications.websocket.ssl;

import java.security.KeyStore;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;

import org.crysil.communications.websocket.interfaces.CertificateCallback;

import io.netty.handler.ssl.util.SimpleTrustManagerFactory;

/**
 * Simply uses {@link WebsocketX509TrustManager} to trust server certificates
 */
public class WebsocketTrustManagerFactory extends SimpleTrustManagerFactory {

	private final CertificateCallback certificateCallback;
	private KeyStore trustStore;

	public WebsocketTrustManagerFactory(CertificateCallback certificateCallback, KeyStore trustStore) {
		this.certificateCallback = certificateCallback;
		this.trustStore = trustStore;
	}

	@Override
	protected void engineInit(KeyStore keyStore) throws Exception {
		this.trustStore = keyStore;
	}

	@Override
	protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {

	}

	@Override
	protected TrustManager[] engineGetTrustManagers() {
		return new TrustManager[] { new WebsocketX509TrustManager(certificateCallback, trustStore) };
	}
}
