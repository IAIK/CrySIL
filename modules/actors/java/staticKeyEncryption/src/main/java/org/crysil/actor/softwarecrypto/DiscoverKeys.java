package org.crysil.actor.softwarecrypto;

import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.KeyHandle;
import org.crysil.protocol.payload.crypto.key.KeyRepresentation;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysResponse;

/**
 * can handle discover key requests.
 */
public class DiscoverKeys implements Command {

	@Override
	public PayloadResponse perform(Request input, SoftwareCryptoKeyStore keystore) throws CrySILException {
		PayloadDiscoverKeysRequest request = (PayloadDiscoverKeysRequest) input.getPayload();

		final List<Key> keys = new ArrayList<>();
		if (null == request.getRepresentation()) {
      request.setRepresentation(KeyRepresentation.UNKNOWN);
    }

		switch (request.getRepresentation()) {
		case HANDLE:
			final KeyHandle keyhandle = new KeyHandle();
			keyhandle.setId("testkey");
			keyhandle.setSubId("1");
			keys.add(keyhandle);
			break;
		case CERTIFICATE:
			final InternalCertificate internalcertificate = new InternalCertificate();
			internalcertificate.setId("testkey");
			internalcertificate.setSubId("1");
			try {
				internalcertificate.setCertificate(keystore.getX509Certificate(new KeyHandle()));
				keys.add(internalcertificate);
			} catch (CertificateEncodingException e) {

				throw new KeyStoreUnavailableException();
			}
			break;
		default:
			throw new UnsupportedRequestException();
		}

		final PayloadDiscoverKeysResponse response = new PayloadDiscoverKeysResponse();
		response.setKey(keys);

		return response;
	}

}
