package org.crysil.communications.u2f;

import org.crysil.commons.Module;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyRequest;
import org.crysil.protocol.payload.crypto.key.WrappedKey;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;

public class CrySILForwarder {

	private static final String CERT_SUBJECT = "CN=CrySIL";
	private static final String SIGNATURE_ALG = "SHA256withECDSA";

	public Response executeGenerateWrappedKey(byte[] clientParam, byte[] appParam, byte[] encodedRandom, Module actor,
			U2FReceiverInterface receiver) {
		Request request = new Request();
		StandardHeader header = new StandardHeader();
		request.setHeader(header);

		PayloadGenerateU2FKeyRequest payload = new PayloadGenerateU2FKeyRequest();
		payload.setCertificateSubject(CERT_SUBJECT);
		payload.setAppParam(appParam);
		payload.setClientParam(clientParam);
		payload.setEncodedRandom(encodedRandom);
		request.setPayload(payload);

		return receiver.forwardRequest(request, actor);
	}

	public Response executeSignatureRequest(byte[] keyEncoded, byte[] hashToBeSigned, Module actor,
			U2FReceiverInterface receiver) {
		Request request = new Request();
		StandardHeader header = new StandardHeader();
		request.setHeader(header);

		WrappedKey key = new WrappedKey();
		key.setEncodedWrappedKey(keyEncoded);

		PayloadSignRequest payload = new PayloadSignRequest();
		payload.setAlgorithm(SIGNATURE_ALG);
		payload.addHashToBeSigned(hashToBeSigned);
		payload.setSignatureKey(key);
		request.setPayload(payload);

		return receiver.forwardRequest(request, actor);
	}

}
