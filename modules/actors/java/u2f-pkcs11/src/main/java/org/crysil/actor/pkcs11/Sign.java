package org.crysil.actor.pkcs11;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

import org.crysil.actor.pkcs11.strategy.U2FKeyHandleStrategy;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.header.U2FHeader;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignResponse;

/**
 * Signs data with an U2F Key
 */
public class Sign extends Command {

	@Override
	public PayloadResponse perform(Request request, U2FKeyHandleStrategy strategy, Pkcs11KeyStore keyStore)
			throws CrySILException {
		PayloadSignRequest payload = (PayloadSignRequest) request.getPayload();

		List<byte[]> signedHashes = new ArrayList<>();
		for (byte[] inputData : payload.getHashesToBeSigned()) {

			if (request.getHeader() instanceof U2FHeader) {
				int counter = ((U2FHeader) request.getHeader()).getCounter();
				U2FUtil.replaceCounter(inputData, counter);
			}

			if (!strategy.verifyKeyHandle(inputData, payload.getSignatureKey(), keyStore))
				throw new KeyNotFoundException();

			byte[] sig = null;
			try {
				Signature signature = Signature.getInstance(ECDSA_ALG, keyStore.getProviderName());
				signature.initSign(keyStore.getKey(U2F_KEY_ID).getPrivateKey());
				signature.update(inputData);
				sig = signature.sign();
			} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
				throw new UnknownErrorException();
			}

			if (sig != null) {
				signedHashes.add(sig);
			}
		}

		PayloadSignResponse response = new PayloadSignResponse();
		response.setSignedHashes(signedHashes);
		return response;
	}
}
