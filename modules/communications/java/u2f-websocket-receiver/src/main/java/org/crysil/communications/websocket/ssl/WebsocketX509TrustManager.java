package org.crysil.communications.websocket.ssl;

import io.netty.util.internal.EmptyArrays;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.X509TrustManager;

import org.crysil.communications.websocket.interfaces.CertificateCallback;
import org.crysil.logging.Logger;

/**
 * Check whether to accept the certificate of the WebVPN webservice when connecting over a WebSocket
 */
public class WebsocketX509TrustManager implements X509TrustManager {

	private final CertificateCallback certificateCallback;
	private final KeyStore trustStore;

	public static Set<X509Certificate> getTrustedCerts(KeyStore ks) {
		Set<X509Certificate> set = new HashSet<X509Certificate>();
		try {
			for (Enumeration<String> e = ks.aliases(); e.hasMoreElements();) {
				String alias = e.nextElement();
				if (ks.isCertificateEntry(alias)) {
					Certificate cert = ks.getCertificate(alias);
					if (cert instanceof X509Certificate) {
						set.add((X509Certificate) cert);
					}
				} else if (ks.isKeyEntry(alias)) {
					Certificate[] certs = ks.getCertificateChain(alias);
					if ((certs != null) && (certs.length > 0) && (certs[0] instanceof X509Certificate)) {
						set.add((X509Certificate) certs[0]);
					}
				}
			}
		} catch (KeyStoreException e) {
			// ignore
		}
		return set;
	}

	public WebsocketX509TrustManager(CertificateCallback certificateCallback, KeyStore trustStore) {
		this.certificateCallback = certificateCallback;
		this.trustStore = trustStore;
	}

	@Override
	public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
			throws CertificateException {
		if (chain.length == 0) {
			throw new CertificateException("No certificates provided");
		}
		System.out.println("Accepting a client certificate: " + chain[0].getSubjectDN());

	}

	@Override
	public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
			throws CertificateException {
		if (chain.length == 0) {
			throw new CertificateException("No certificates provided");
		}
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = chain[i];
			Set<X509Certificate> trustedCerts = getTrustedCerts(trustStore);
			boolean trusted = trustedCerts.contains(cert);
			if (!trusted) {
				trusted = certificateCallback.checkServerTrusted(cert, authType);
			}
			if (!trusted) {
				throw new CertificateException("Certificate not trusted");
			}
			Logger.debug("Accepting a server certificate: " + cert.getSubjectDN());
		}
	}

	@Override
	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return EmptyArrays.EMPTY_X509_CERTIFICATES;
	}
}
