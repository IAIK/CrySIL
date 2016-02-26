package org.crysil.actor.smcc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.crysil.actor.smcc.strategy.U2FKeyHandleStrategy;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.header.U2FHeader;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignResponse;

import at.gv.egiz.smcc.SignatureCard;
import at.gv.egiz.smcc.SignatureCard.KeyboxName;
import at.gv.egiz.smcc.SignatureCardException;
import at.gv.egiz.smcc.pin.gui.PINGUI;
import at.gv.egiz.smcc.util.SMCCHelper;

/**
 * Signs data with a U2F Key
 */
public class Sign implements Command {

	protected static final String SIG_ECDSA = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";

	@Override
	public PayloadResponse perform(Request request, U2FKeyHandleStrategy strategy, PINGUI pinGUI)
			throws CrySILException {
		PayloadSignRequest payload = (PayloadSignRequest) request.getPayload();

		SMCCHelper smccHelper = new SMCCHelper();
		SignatureCard card = smccHelper.getSignatureCard(Locale.getDefault());

		List<byte[]> hashesToBeSigned = payload.getHashesToBeSigned();
		List<byte[]> signedHashes = new ArrayList<>();
		for (byte[] inputData : hashesToBeSigned) {
			if (request.getHeader() instanceof U2FHeader) {
				int counter = ((U2FHeader) request.getHeader()).getCounter();
				U2FUtil.replaceCounter(inputData, counter);
			}

			if (!strategy.verifyKeyHandle(inputData, payload.getSignatureKey(), card, pinGUI))
				throw new KeyNotFoundException();

			byte[] signature = null;
			try {
				signature = card.createSignature(new ByteArrayInputStream(inputData),
						KeyboxName.SECURE_SIGNATURE_KEYPAIR, pinGUI, SIG_ECDSA);
			} catch (SignatureCardException | InterruptedException | IOException e) {
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
