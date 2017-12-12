package org.crysil.communications.websocket;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public interface KeyStoreInterface {

	X509Certificate getCertificate(String alias);
	
	PrivateKey getKey(String alias);
	
}
