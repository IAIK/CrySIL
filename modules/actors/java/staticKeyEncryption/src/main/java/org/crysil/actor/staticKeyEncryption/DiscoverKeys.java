package org.crysil.actor.staticKeyEncryption;

import org.crysil.errorhandling.*;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.KeyHandle;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysResponse;

import javax.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * can handle discover key requests.
 */
public class DiscoverKeys implements Command {

	@Override
	public PayloadResponse perform(Request input) throws CrySILException {
		PayloadDiscoverKeysRequest request = (PayloadDiscoverKeysRequest) input.getPayload();

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
			try {
				SimpleKeyStore keystore = SimpleKeyStore.getInstance();
				internalcertificate.setCertificate(keystore.getX509Certificate(new KeyHandle()));
				keys.add(internalcertificate);
			} catch (KeyStoreUnavailableException | CertificateEncodingException | InvalidCertificateException
					| KeyNotFoundException e) {

				throw new KeyStoreUnavailableException();
			}
			break;
		default:
			throw new UnsupportedRequestException();
		}

		PayloadDiscoverKeysResponse response = new PayloadDiscoverKeysResponse();
		response.setKey(keys);

		return response;
	}

}
