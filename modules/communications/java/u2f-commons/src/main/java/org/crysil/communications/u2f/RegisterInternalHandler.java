package org.crysil.communications.u2f;

import org.crysil.commons.Module;
import org.crysil.communications.json.JsonUtils;
import org.crysil.communications.u2f.data.RegisterInternalRequest;
import org.crysil.communications.u2f.data.RegisterInternalResponse;
import org.crysil.logging.Logger;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyResponse;
import org.crysil.protocol.payload.crypto.sign.PayloadSignResponse;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.google.common.primitives.Bytes;

import static org.crysil.communications.u2f.U2FUtil.*;

public class RegisterInternalHandler implements Handler {

	private final CrySILForwarder skytrustHandler;

	public RegisterInternalHandler(CrySILForwarder skytrustHandler) {
		this.skytrustHandler = skytrustHandler;
	}

	@Override
	public String handle(String u2fRawRequest, Module actor, U2FReceiverInterface receiver) {
		RegisterInternalRequest u2fInternalRegisterRequest = JsonUtils.fromJson(u2fRawRequest,
				RegisterInternalRequest.class);
		if (u2fInternalRegisterRequest == null) {
			return null;
		}
		Logger.debug("Incoming request: " + JsonUtils.toJson(u2fInternalRegisterRequest));

		byte[] signature;
		byte[] keyEncoded;
		byte[] certEncoded;
		byte[] keyHandleBytes;
		try {
			byte[] challengeParam = u2fInternalRegisterRequest.getChallengeHash();
			byte[] appParam = u2fInternalRegisterRequest.getAppIdHash();

			Response responseGenKey = skytrustHandler.executeGenerateWrappedKey(
					u2fInternalRegisterRequest.getChallengeHash(), u2fInternalRegisterRequest.getAppIdHash(), null,
					actor, receiver);
			if (responseGenKey == null || responseGenKey.getPayload() == null) {
				Logger.error("No response for generate wrapped key");
				return null;
			}
			Logger.debug("Generated wrapped key: " + JsonUtils.toJson(responseGenKey));
			if (responseGenKey.getPayload().getType().equals("authChallengeRequest"))
				return JsonUtils.toJson(responseGenKey);

			PayloadGenerateU2FKeyResponse payloadGenKey = (PayloadGenerateU2FKeyResponse) responseGenKey.getPayload();

			javax.security.cert.X509Certificate cert = payloadGenKey.getCertificate();
			keyEncoded = cert.getPublicKey().getEncoded();
			if (keyEncoded.length > 65) {
				keyEncoded = stripMetaData(keyEncoded);
			}
			certEncoded = cert.getEncoded();
			keyHandleBytes = payloadGenKey.getEncodedRandom();

			byte[] signatureData = buildSignatureBytes(appParam, challengeParam, keyHandleBytes, keyEncoded);
			Response responseSign = skytrustHandler.executeSignatureRequest(payloadGenKey.getEncodedWrappedKey(),
					signatureData, actor, receiver);
			if (responseSign == null || responseSign.getPayload() == null) {
				Logger.error("No response for sign");
				return null;
			}
			Logger.debug("Generated signature: " + JsonUtils.toJson(responseSign));
			PayloadSignResponse payloadSign = (PayloadSignResponse) responseSign.getPayload();
			signature = payloadSign.getSignedHashes().iterator().next();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		RegisterInternalResponse response;
		boolean fromU2FActor = Bytes.indexOf(signature, keyHandleBytes) > -1
				&& Bytes.indexOf(signature, certEncoded) > -1;
		if (!fromU2FActor) {
			byte[] signatureData = buildResponseBytes(keyEncoded, keyHandleBytes, certEncoded, signature);
			response = new RegisterInternalResponse(signatureData);
		} else {
			response = new RegisterInternalResponse(signature);
		}
		Logger.debug("Answering with: " + JsonUtils.toJson(response));

		return JsonUtils.toJson(response);
	}

	private static byte[] buildSignatureBytes(byte[] appParam, byte[] challengeParam, byte[] keyHandleBytes,
			byte[] keyEncoded) {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.write((byte) 0x00);
		builder.write(appParam);
		builder.write(challengeParam);
		builder.write(keyHandleBytes);
		builder.write(keyEncoded);
		byte[] result = builder.toByteArray();
		builder.close();
		return result;
	}

	private static byte[] buildResponseBytes(byte[] keyEncoded, byte[] keyHandleBytes, byte[] certEncoded,
			byte[] signature) {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.write(0x05);
		builder.write(keyEncoded);
		builder.write(keyHandleBytes.length);
		builder.write(keyHandleBytes);
		builder.write(certEncoded);
		builder.write(signature);
		byte[] result = builder.toByteArray();
		builder.close();
		return result;
	}

}
