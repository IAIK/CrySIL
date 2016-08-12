package org.crysil.actor.staticKeyEncryption;

import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.payload.PayloadRequest;
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
	public PayloadResponse perform(final PayloadRequest input) throws CrySILException {
		final PayloadDiscoverKeysRequest request = (PayloadDiscoverKeysRequest) input;

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
				final SimpleKeyStore keystore = SimpleKeyStore.getInstance();
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

		final PayloadDiscoverKeysResponse response = new PayloadDiscoverKeysResponse();
		response.setKey(keys);

		return response;
	}

}
