package org.crysil.instance;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.crysil.instance.datastore.CertificatePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/**
 * Creates a {@link CertificatePrincipal} for the WebSocket session to check, whether the CrySIL Android server has
 * provided a client TLS certificate
 * 
 * @see X509CertificateAuthenticationManager
 */
public class WebSocketSecurityHandshakeHandler extends DefaultHandshakeHandler {

	private static Logger logger = LoggerFactory.getLogger(WebSocketSecurityHandshakeHandler.class);

	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();
		if (credentials instanceof X509Certificate) {
			X509Certificate certificate = (X509Certificate) credentials;
			logger.debug("Got certificate for '{}' from client", certificate.getSubjectDN().getName());
			return new CertificatePrincipal(certificate.getSubjectDN().getName(), true);
		} else {
			logger.debug("Got no certificate from client");
			return new CertificatePrincipal("[UNKNOWN]", false);
		}
	}

}
