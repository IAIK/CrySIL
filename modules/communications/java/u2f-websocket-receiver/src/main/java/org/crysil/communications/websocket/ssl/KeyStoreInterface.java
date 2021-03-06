package org.crysil.communications.websocket.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public interface KeyStoreInterface {

	X509Certificate getCertificate(String alias);
	
	PrivateKey getKey(String alias);
	
}
