package org.crysil.actor.smcc.strategy;

import at.gv.egiz.smcc.SignatureCard;
import at.gv.egiz.smcc.SignatureCard.KeyboxName;
import at.gv.egiz.smcc.pin.gui.PINGUI;

import java.io.ByteArrayInputStream;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * This implements a simple key handle strategy: We only sign the app Param with the RSA key from the card. This has the
 * disadvantage that every keyhandle for the same relying party looks the same.
 */
public class SimpleSignedKeyHandleStrategy extends U2FKeyHandleStrategy {

	private static final String SIG_RSA = "SHA256withRSA";
	private static final String SIG_RSA_XML = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

	@Override
	protected boolean verifyKeyHandle(byte[] keyHandle, byte[] appParam, byte[] clientParam, SignatureCard card,
			PINGUI pinGUI) {
		try {
			final CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(
					card.getCertificate(KeyboxName.CERTIFIED_KEYPAIR, pinGUI)));
			Signature signature = Signature.getInstance(SIG_RSA);
			signature.initVerify(certificate.getPublicKey());
			signature.update(appParam);
			return signature.verify(keyHandle);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Tuple<byte[], byte[]> generateKeyHandleAndRandom(byte[] encodedRandom, byte[] appParam, SignatureCard card,
			PINGUI pinGUI) throws Exception {
		byte[] input = appParam;
		if (encodedRandom != null && encodedRandom.length > 0)
			input = encodedRandom;
		byte[] signature = card.createSignature(new ByteArrayInputStream(input), KeyboxName.CERTIFIED_KEYPAIR, pinGUI,
				SIG_RSA_XML);
		return new Tuple<byte[], byte[]>(signature, input);
	}

}
