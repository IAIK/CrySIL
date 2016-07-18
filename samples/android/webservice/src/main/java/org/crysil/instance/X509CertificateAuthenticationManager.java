package org.crysil.instance;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Handles authentication via client TLS certificates. <br/>
 * 
 * We can safely accept any certificate here, because it has already been verified by Tomcat due to the configuration
 * property "server.ssl.trust-store". The certificate is stored in authentication.getCredentials();
 */
public final class X509CertificateAuthenticationManager implements AuthenticationManager {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		authentication.setAuthenticated(true);
		return authentication;
	}

}