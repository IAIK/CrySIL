package org.crysil.actor.staticKeyEncryption;

import org.crysil.actor.staticKeyEncryption.strategy.Tuple;
import org.crysil.actor.staticKeyEncryption.strategy.YubicoRandomKeyHandleStrategy;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyResponse;

import java.security.cert.X509Certificate;

/**
 * Can generate U2F Keys
 */
public class GenerateU2FKey implements Command {

	@Override
	public PayloadResponse perform(Request request) throws CrySILException {

		PayloadGenerateU2FKeyRequest payload = (PayloadGenerateU2FKeyRequest) request.getPayload();

		X509Certificate signingCertificate = SimpleKeyStore.getInstance().getX509CertificateEC();

		try {
			Tuple<byte[], byte[]> keyHandle = new YubicoRandomKeyHandleStrategy()
					.generateKeyHandleAndRandom(payload.getEncodedRandom(), payload.getAppParam());

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
