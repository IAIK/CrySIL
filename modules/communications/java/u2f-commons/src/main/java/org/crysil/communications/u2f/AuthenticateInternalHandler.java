package org.crysil.communications.u2f;

import org.crysil.commons.Module;
import org.crysil.communications.json.JsonUtils;
import org.crysil.communications.u2f.counter.U2FCounterStore;
import org.crysil.communications.u2f.data.AuthInternalRequest;
import org.crysil.communications.u2f.data.AuthInternalResponse;
import org.crysil.logging.Logger;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyResponse;
import org.crysil.protocol.payload.crypto.sign.PayloadSignResponse;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;

public class AuthenticateInternalHandler implements Handler {

	private final U2FCounterStore counterStore;
	private final CrySILForwarder crysilForwarder;

	public AuthenticateInternalHandler(CrySILForwarder crysilForwarder, U2FCounterStore counterStore) {
		this.crysilForwarder = crysilForwarder;
		this.counterStore = counterStore;
	}

	public String handle(String u2fRawRequest, Module actor, U2FReceiverInterface receiver) {
		AuthInternalRequest u2fInternalAuthRequest = JsonUtils.fromJson(u2fRawRequest, AuthInternalRequest.class);
		if (u2fInternalAuthRequest == null) {
			return null;
		}
		Logger.debug("Incoming request: " + JsonUtils.toJson(u2fInternalAuthRequest));

		byte[] encodedRandom = u2fInternalAuthRequest.getKeyHandle();
		Response responseGenKey = crysilForwarder.executeGenerateWrappedKey(null,
				u2fInternalAuthRequest.getAppIdHash(), encodedRandom, actor, receiver);
		byte[] wrappedKey = ((PayloadGenerateU2FKeyResponse) responseGenKey.getPayload()).getEncodedWrappedKey();

		byte[] signature;
		try {
			byte[] challengeParameter = u2fInternalAuthRequest.getChallengeHash();
			byte[] applicationBytes = u2fInternalAuthRequest.getAppIdHash();
			byte[] bytesToSign = buildSignatureBytes(applicationBytes, challengeParameter,
					counterStore.incrementCounter());

			Response responseSign = crysilForwarder.executeSignatureRequest(wrappedKey, bytesToSign, actor, receiver);
			if (responseSign == null || responseSign.getPayload() == null) {
				Logger.error("No response for generate wrapped key");
				return null;
			}

			Logger.debug("Generated signature: " + JsonUtils.toJson(responseSign));
			PayloadSignResponse payloadSign = (PayloadSignResponse) responseSign.getPayload();

			signature = payloadSign.getSignedHashes().iterator().next();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		boolean fromU2FActor = signature.length > 72;
		AuthInternalResponse response;
		if (!fromU2FActor) {
			response = new AuthInternalResponse(buildResponseBytes(signature, counterStore.getCounter()));
		} else {
			response = new AuthInternalResponse(signature);
		}
		Logger.debug("Answering with: " + JsonUtils.toJson(response));

		return JsonUtils.toJson(response);
	}

	private static byte[] buildSignatureBytes(byte[] appId, byte[] challengeParam, int counter) {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.write(appId);
		builder.write((byte) 0x01);
		builder.write((counter >> 24) & 0xFF);
		builder.write((counter >> 16) & 0xFF);
		builder.write((counter >> 8) & 0xFF);
		builder.write(counter & 0xFF);
		builder.write(challengeParam);
		byte[] result = builder.toByteArray();
		builder.close();
		return result;
	}

	private static byte[] buildResponseBytes(byte[] signature, int counter) {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.write((byte) 0x01);
		builder.write((counter >> 24) & 0xFF);
		builder.write((counter >> 16) & 0xFF);
		builder.write((counter >> 8) & 0xFF);
		builder.write(counter & 0xFF);
		builder.write(signature);
		byte[] result = builder.toByteArray();
		builder.close();
		return result;
	}

}
