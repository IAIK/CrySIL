package org.crysil.actor.softwarecrypto;

import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.header.U2FHeader;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignResponse;

import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

/**
 * Signs data with a U2F Key
 */
public class Sign implements Command {

	@Override
	public PayloadResponse perform(Request request) throws CrySILException {
		PayloadSignRequest payload = (PayloadSignRequest) request.getPayload();

		List<byte[]> hashesToBeSigned = payload.getHashesToBeSigned();
		List<byte[]> signedHashes = new ArrayList<>();
		for (byte[] inputData : hashesToBeSigned) {
			if (request.getHeader() instanceof U2FHeader) {
				int counter = ((U2FHeader) request.getHeader()).getCounter();
				U2FUtil.replaceCounter(inputData, counter);
			}

			byte[] signature = null;
			try {
				Signature sig = Signature.getInstance("SHA256withECDSA");
				sig.initSign(SimpleKeyStore.getInstance().getJCEPrivateKeyECDSA());
				sig.update(inputData);
				signature = sig.sign();
			} catch (Exception e) {
				throw new UnknownErrorException();
			}

			if (signature != null) {
				signedHashes.add(U2FUtil.ensureASN1(signature));
			}
		}

		PayloadSignResponse response = new PayloadSignResponse();
		response.setSignedHashes(signedHashes);
		return response;
	}
}
