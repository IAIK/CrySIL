package org.crysil.builders;

import javax.security.cert.CertificateEncodingException;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.crysil.protocol.payload.crypto.key.ExternalCertificate;
import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

import com.google.common.io.BaseEncoding;

public class KeyBuilder {

	public static KeyHandle buildKeyHandle(String id, String subId) {
		KeyHandle tmp = new KeyHandle();
		tmp.setId(id);
		tmp.setSubId(subId);
		return tmp;
	}

	public static InternalCertificate buildInternalCertificate(String id, String subId, String encodedCertificate)
			throws CertificateEncodingException, CertificateException {
		InternalCertificate tmp = new InternalCertificate();
		tmp.setId(id);
		tmp.setSubId(subId);
		tmp.setCertificate(X509Certificate.getInstance(BaseEncoding.base64().decode(encodedCertificate)));
		return tmp;
	}

	public static Key buildExternalCertificate(String base64X509Certificate)
			throws CertificateEncodingException, CertificateException {
		ExternalCertificate tmp = new ExternalCertificate();
		tmp.setCertificate(X509Certificate.getInstance(BaseEncoding.base64().decode(base64X509Certificate)));
		return tmp;
	}
}
