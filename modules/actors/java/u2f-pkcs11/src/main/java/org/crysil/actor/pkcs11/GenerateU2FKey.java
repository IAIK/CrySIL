package org.crysil.actor.pkcs11;

import javax.security.cert.X509Certificate;

import org.crysil.actor.pkcs11.strategy.Tuple;
import org.crysil.actor.pkcs11.strategy.U2FKeyHandleStrategy;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyResponse;

/**
 * Can generate U2F Keys
 */
public class GenerateU2FKey extends Command {

	@Override
	public PayloadResponse perform(Request request, U2FKeyHandleStrategy strategy, Pkcs11KeyStore keyStore)
			throws CrySILException {

		PayloadGenerateU2FKeyRequest payload = (PayloadGenerateU2FKeyRequest) request.getPayload();
		X509Certificate signingCertificate = keyStore.getKey(U2F_KEY_ID).getCertificate();

		try {
			Tuple<byte[], byte[]> keyHandle = strategy.generateKeyHandleAndRandom(payload.getEncodedRandom(),
					payload.getAppParam(), keyStore);

			PayloadGenerateU2FKeyResponse generateWrappedKeyResponse = new PayloadGenerateU2FKeyResponse();
			generateWrappedKeyResponse.setEncodedWrappedKey(keyHandle.first);
			generateWrappedKeyResponse.setCertificate(signingCertificate);
			generateWrappedKeyResponse.setEncodedRandom(keyHandle.second);

			return generateWrappedKeyResponse;
		} catch (Exception e) {
			throw new UnknownErrorException();
		}

	}
}
