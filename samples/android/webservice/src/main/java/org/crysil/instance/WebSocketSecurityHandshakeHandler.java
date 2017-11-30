package org.crysil.instance;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.crysil.instance.datastore.CertificatePrincipal;
import org.crysil.instance.util.CertificateUtil;
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

		/*
		 *  whenever the server is run in a non-/ environment, we cannot terminate the TLS connection. Likely, a reverse proxy
		 *  will handle and therefore terminate TLS. However, in order to get the client certificate one can set the client certificate
		 *  as a header field within the reverse proxy. The code below parses the field whenever the direct method fails.
		 */
		if (!(credentials instanceof X509Certificate)) {
			try {
				credentials = CertificateUtil.parseCertificate(request.getHeaders().get("ssl_client_cert").get(0));
			} catch (Exception e) {
				// did not work. we do not care.
				e.printStackTrace();
			}
		}
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
