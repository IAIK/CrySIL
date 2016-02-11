package org.crysil.actor.staticKeyEncryption;

import java.util.ArrayList;
import java.util.List;

import org.crysil.UnsupportedRequestException;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.KeyHandle;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysResponse;

/**
 * can handle discover key requests.
 */
public class DiscoverKeys implements Command {

	@Override
	public PayloadResponse perform(PayloadRequest input) throws UnsupportedRequestException {
		PayloadDiscoverKeysRequest request = (PayloadDiscoverKeysRequest) input;

		List<Key> keys = new ArrayList<>();

		switch (request.getRepresentation()) {
		case "handle":
			KeyHandle keyhandle = new KeyHandle();
			keyhandle.setId("testkey");
			keyhandle.setSubId("1");
			keys.add(keyhandle);
			break;
		case "certificate":
			InternalCertificate internalcertificate = new InternalCertificate();
			internalcertificate.setId("testkey");
			internalcertificate.setSubId("1");
			internalcertificate.setEncodedCertificate("TODO");
			keys.add(internalcertificate);
			break;
		default:
			throw new UnsupportedRequestException();
		}

		PayloadDiscoverKeysResponse response = new PayloadDiscoverKeysResponse();
		response.setKey(keys);

		return response;
	}

}
