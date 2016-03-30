package org.crysil.actor.spongycastle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.Signature;

import org.crysil.logging.Logger;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;

/**
 * Signs data with a new key pair from Android
 */
public class KeypairContentSigner implements ContentSigner {

	private ByteArrayOutputStream dataStream;
	private KeyPair keyPair;
	private AlgorithmIdentifier sigAlgId;
	private String algorithmText;

	public KeypairContentSigner(String algorithmText, KeyPair keyPair) {
		this.sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(algorithmText);
		this.dataStream = new ByteArrayOutputStream();
		this.algorithmText = algorithmText;
		this.keyPair = keyPair;
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
			byte[] data = dataStream.toByteArray();
			dataStream.flush();
			Signature s = Signature.getInstance(algorithmText, AndroidKeyStore.PROVIDER);
			s.initSign(keyPair.getPrivate());
			s.update(data);
			return s.sign();
		} catch (Exception e) {
			Logger.error("Signature calculation failed", e);
		}
		return null;
	}

}
