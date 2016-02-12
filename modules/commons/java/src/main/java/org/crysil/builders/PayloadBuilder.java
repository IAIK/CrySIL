package org.crysil.builders;

import java.util.List;

import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptRequest;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;
import org.crysil.protocol.payload.status.PayloadStatus;

public class PayloadBuilder {

	public static PayloadDiscoverKeysRequest buildDiscoverKeysRequest(String representation) {
		PayloadDiscoverKeysRequest tmp = new PayloadDiscoverKeysRequest();
		tmp.setRepresentation(representation);

		return tmp;
	}

	public static PayloadEncryptRequest buildEncryptRequest(String algorithm, List<String> plainData,
			List<Key> encryptionKeys) {
		PayloadEncryptRequest tmp = new PayloadEncryptRequest();
		tmp.setAlgorithm(algorithm);
		tmp.setPlainData(plainData);
		tmp.setEncryptionKeys(encryptionKeys);

		return tmp;
	}

	public static PayloadResponse buildStatusResponse(int errorCode) {
		PayloadStatus tmp = new PayloadStatus();
		tmp.setCode(errorCode);

		return tmp;
	}

}
