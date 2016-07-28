package org.crysil.communications.websocket.interfaces;

import java.security.cert.X509Certificate;

public interface CertificateCallback {

	boolean checkServerTrusted(X509Certificate cert, String authType);

}
