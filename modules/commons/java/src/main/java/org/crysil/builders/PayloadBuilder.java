package org.crysil.builders;

import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest;
import org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptResponse;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptRequest;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;
import org.crysil.protocol.payload.status.PayloadStatus;

import com.google.common.io.BaseEncoding;

public class PayloadBuilder {

	public static PayloadDiscoverKeysRequest buildDiscoverKeysRequest(String representation) {
		PayloadDiscoverKeysRequest tmp = new PayloadDiscoverKeysRequest();
		tmp.setRepresentation(representation);

		return tmp;
	}

	public static PayloadEncryptRequest buildEncryptRequest(String algorithm, String string, Key key) {
		PayloadEncryptRequest tmp = new PayloadEncryptRequest();
		tmp.setAlgorithm(algorithm);
		tmp.addPlainData(string.getBytes());
		tmp.addEncryptionKey(key);

		return tmp;
	}

	public static PayloadResponse buildStatusResponse(int errorCode) {
		PayloadStatus tmp = new PayloadStatus();
		tmp.setCode(errorCode);

		return tmp;
	}

	public static PayloadDecryptRequest buildDecryptRequest(Key decryptionKey, String plaintext) {
		PayloadDecryptRequest tmp = new PayloadDecryptRequest();
		tmp.setDecryptionKey(decryptionKey);
		tmp.addEncryptedData(BaseEncoding.base64().decode(plaintext));

		return tmp;
	}

	public static PayloadDecryptResponse buildDecryptResponse(String plaintext) {
		PayloadDecryptResponse tmp = new PayloadDecryptResponse();
		tmp.addPlainData(plaintext.getBytes());

		return tmp;
	}
}
