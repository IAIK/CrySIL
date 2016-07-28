package org.crysil.actor.smcc;

import com.google.common.primitives.Bytes;
import org.crysil.actor.smcc.strategy.YubicoRandomKeyHandleStrategy;
import org.crysil.commons.Module;
import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyRequest;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyResponse;
import org.crysil.protocol.payload.crypto.key.WrappedKey;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;

/**
 * Tests require a smcc-compliant card connected to this machine and the correct PIN in {@link SmccPinConfiguration}
 */
@Test(enabled = false)
public class SmccActorYubicoTest {

	private Module module;
	private PayloadGenerateU2FKeyResponse generateKeyResponsePayload;
	private byte[] appId;

	@BeforeMethod
	public void before() {
		module = new SmccActor(new YubicoRandomKeyHandleStrategy());
		appId = calculateDigest("https://localhost");
	}

	@Test(enabled = false)
	public void testGenerateU2FKey() throws CrySILException, CertificateException {
		PayloadGenerateU2FKeyRequest generatePayload = new PayloadGenerateU2FKeyRequest();
		generatePayload.setAppParam(appId);
		generatePayload.setCertificateSubject("CN=Test");
		generatePayload.setClientParam("foo".getBytes());
		StandardHeader header = new StandardHeader();
		Request request = new Request(header, generatePayload);

		Response response = module.take(request);
		Assert.assertTrue(response.getPayload() instanceof PayloadGenerateU2FKeyResponse);
		generateKeyResponsePayload = (PayloadGenerateU2FKeyResponse) response.getPayload();
		generateKeyResponsePayload.getCertificate().checkValidity();
	}

	private static byte[] buildSignatureBytes(byte[] appId, byte[] challengeParam, int counter) {
		return Bytes.concat(appId, new byte[] { 0x01 }, buildCounterArray(counter), challengeParam);
	}

	private static byte[] buildCounterArray(int counter) {
		byte[] result = new byte[4];
		result[0] = (byte) ((counter >> 24) & 0xFF);
		result[1] = (byte) ((counter >> 16) & 0xFF);
		result[2] = (byte) ((counter >> 8) & 0xFF);
		result[3] = (byte) (counter & 0xFF);
		return result;
	}

	public static byte[] calculateDigest(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(text.getBytes("UTF-8"));
			return md.digest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text.getBytes();
	}

	@Test(dependsOnMethods = "testGenerateU2FKey", enabled = false)
	public void testSign() throws CrySILException, CertificateException, GeneralSecurityException {
		PayloadSignRequest signPayload = new PayloadSignRequest();
		signPayload.setAlgorithm("SHA256withECDSA");
		byte[] hashToBeSigned = buildSignatureBytes(appId, calculateDigest("bar"), 1);
		signPayload.addHashToBeSigned(hashToBeSigned);
		WrappedKey key = new WrappedKey();
		key.setEncodedWrappedKey(generateKeyResponsePayload.getEncodedWrappedKey());
		signPayload.setSignatureKey(key);

		StandardHeader header = new StandardHeader();
		Request request = new Request(header, signPayload);

		Response response = module.take(request);
		Assert.assertTrue(response.getPayload() instanceof PayloadSignResponse);
		PayloadSignResponse signResponsePayload = (PayloadSignResponse) response.getPayload();
		Assert.assertFalse(signResponsePayload.getSignedHashes().isEmpty());
		byte[] signedHash = signResponsePayload.getSignedHashes().iterator().next();

		PublicKey publicKey = generateKeyResponsePayload.getCertificate().getPublicKey();
		Signature signature = Signature.getInstance("SHA256withECDSA");
		signature.initVerify(publicKey);
		signature.update(hashToBeSigned);
		Assert.assertTrue(signature.verify(signedHash));
	}

}
