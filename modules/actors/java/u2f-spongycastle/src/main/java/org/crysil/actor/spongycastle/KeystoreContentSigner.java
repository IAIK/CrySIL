package org.crysil.actor.spongycastle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;

import org.crysil.logging.Logger;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;

/**
 * Signs data with the Android Keystore
 */
public class KeystoreContentSigner implements ContentSigner {

	private ByteArrayOutputStream dataStream;
	private String keyAlias;
	private AlgorithmIdentifier sigAlgId;
	private String algorithmText;

	public KeystoreContentSigner(String algorithmText, String keyAlias) {
		this.sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(algorithmText);
		this.dataStream = new ByteArrayOutputStream();
		this.keyAlias = keyAlias;
		this.algorithmText = algorithmText;
	}

	@Override
	public OutputStream getOutputStream() {
		return dataStream;
	}

	@Override
	public AlgorithmIdentifier getAlgorithmIdentifier() {
		return sigAlgId;
	}

	@Override
	public byte[] getSignature() {
		try {
			KeyStore ks = KeyStore.getInstance(AndroidKeyStore.KEYSTORE_TYPE);
			ks.load(null);
			byte[] data = dataStream.toByteArray();
			dataStream.flush();
			Signature s = Signature.getInstance(algorithmText, AndroidKeyStore.PROVIDER);
			Key entry = ks.getKey(keyAlias, null);
			s.initSign((PrivateKey) entry);
			s.update(data);
			return s.sign();
		} catch (Exception e) {
			Logger.error("Signature calculation failed", e);
		}
		return null;
	}

}
