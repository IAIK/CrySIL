package org.crysil.instance.datastore;

import java.security.Principal;

import org.crysil.instance.WebSocketSecurityHandshakeHandler;

/**
 * Identifies the CrySIL Android server in the WebSocket session with its client TLS certificate. It may provide <b>no
 * certificate</b> e.g. on registration.
 * 
 * @see WebSocketSecurityHandshakeHandler
 */
public class CertificatePrincipal implements Principal {

	private String name;
	private boolean hasCertificate;

	public CertificatePrincipal(String name, boolean hasCertificate) {
		this.name = name;
		this.hasCertificate = hasCertificate;
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean hasCertificate() {
		return hasCertificate;
	}
}
