package org.crysil.actor.u2f;

import com.google.common.io.BaseEncoding;
import org.crysil.actor.u2f.strategy.U2FKeyHandleStrategy;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyResponse;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Can generate U2F Keys
 */
public class GenerateU2FKey implements Command {

	@Override
	public PayloadResponse perform(Request request, U2FKeyHandleStrategy strategy, Map<String, byte[]> cachedResponses,
			U2FActivityHandler activityHandler) throws CrySILException {
		PayloadGenerateU2FKeyRequest payload = (PayloadGenerateU2FKeyRequest) request.getPayload();

		try {
			if (payload.getClientParam() == null || payload.getClientParam().length == 0) {
				Logger.debug(String.format("GenerateU2FKey returning fake keyHandle='%s'", BaseEncoding.base64()
						.encode(payload.getEncodedRandom())));
				return buildGenerateWrappedKeyResponsePayload(payload.getEncodedRandom(), null);
			} else {
				byte[] appParam = payload.getAppParam();
				byte[] clientParam = payload.getClientParam();

				Logger.debug(String.format("GenerateU2FKey waiting for NFC"));
				U2FDeviceHandler u2fHandler = activityHandler.activateNFC();
				if (u2fHandler == null) {
					Logger.error("No handler from Android");
					throw new UnknownErrorException();
				}

				byte[] u2fResponseBytes = u2fHandler.registerPlain(clientParam, appParam);
				int keyLen = u2fResponseBytes[1 + 65];
				int keyOff = 1 + 65 + 1;
				byte[] keyHandle = new byte[keyLen];
				System.arraycopy(u2fResponseBytes, keyOff, keyHandle, 0, keyLen);
				int certLen = u2fResponseBytes.length - keyOff - keyLen - 32;
				int certOff = keyOff + keyLen;
				byte[] certificate = new byte[certLen];
				System.arraycopy(u2fResponseBytes, certOff, certificate, 0, certLen);
				cachedResponses.put(BaseEncoding.base16().encode(keyHandle), u2fResponseBytes);
				Logger.debug(String.format("GenerateU2FKey returning keyHandle='%s', cert='%s'", BaseEncoding.base64()
						.encode(keyHandle), BaseEncoding.base64().encode(certificate)));
				final CertificateFactory cf = CertificateFactory.getInstance("X.509");
				return buildGenerateWrappedKeyResponsePayload(keyHandle, (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificate)));
			}
		} catch (Exception e) {
			Logger.error("Exception caught", e);
			throw new UnknownErrorException();
		}
	}

	private PayloadGenerateU2FKeyResponse buildGenerateWrappedKeyResponsePayload(byte[] keyHandle,
			X509Certificate certificate) throws CertificateEncodingException {
		PayloadGenerateU2FKeyResponse response = new PayloadGenerateU2FKeyResponse();
		response.setEncodedWrappedKey(keyHandle);
		response.setCertificate(certificate);
		response.setEncodedRandom(keyHandle);
		return response;
	}
}
