package org.crysil.communications.websocket;

import io.netty.util.internal.EmptyArrays;

import java.security.cert.CertificateException;

import javax.net.ssl.X509TrustManager;

/**
 * Check whether to accept the certificate of the WebVPN webservice when connecting over a WebSocket
 */
public class WebsocketX509TrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
			throws CertificateException {
		System.out.println("Accepting a client certificate: " + chain[0].getSubjectDN());
	}

	@Override
	public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
			throws CertificateException {
		System.out.println("Accepting a server certificate: " + chain[0].getSubjectDN());
	}

	@Override
	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return EmptyArrays.EMPTY_X509_CERTIFICATES;
	}
}
