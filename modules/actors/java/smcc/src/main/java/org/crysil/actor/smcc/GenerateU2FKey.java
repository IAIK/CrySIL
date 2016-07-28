package org.crysil.actor.smcc;

import java.util.Locale;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.crysil.actor.smcc.strategy.Tuple;
import org.crysil.actor.smcc.strategy.U2FKeyHandleStrategy;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.UnknownErrorException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyResponse;

import at.gv.egiz.smcc.SignatureCard;
import at.gv.egiz.smcc.SignatureCard.KeyboxName;
import at.gv.egiz.smcc.SignatureCardException;
import at.gv.egiz.smcc.pin.gui.PINGUI;
import at.gv.egiz.smcc.util.SMCCHelper;

/**
 * Can generate U2F Keys
 */
public class GenerateU2FKey implements Command {

	@Override
	public PayloadResponse perform(Request request, U2FKeyHandleStrategy strategy, PINGUI pinGUI)
			throws CrySILException {

		PayloadGenerateU2FKeyRequest payload = (PayloadGenerateU2FKeyRequest) request.getPayload();
		SMCCHelper smccHelper = new SMCCHelper();
		SignatureCard card = smccHelper.getSignatureCard(Locale.getDefault());

		javax.security.cert.X509Certificate signingCertificate;
		try {
			signingCertificate = X509Certificate.getInstance(card.getCertificate(KeyboxName.SECURE_SIGNATURE_KEYPAIR,
					pinGUI));
		} catch (SignatureCardException | InterruptedException | CertificateException e) {
			throw new InvalidCertificateException();
		}

		try {
			Tuple<byte[], byte[]> keyHandle = strategy.generateKeyHandleAndRandom(payload.getEncodedRandom(),
					payload.getAppParam(), card, pinGUI);

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
