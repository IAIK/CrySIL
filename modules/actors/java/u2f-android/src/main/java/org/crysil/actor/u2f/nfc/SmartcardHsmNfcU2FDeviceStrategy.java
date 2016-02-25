package org.crysil.actor.u2f.nfc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.spec.SecretKeySpec;

import org.crysil.actor.u2f.U2FDeviceHandler;
import org.crysil.actor.u2f.U2FUtil;
import org.crysil.actor.u2f.strategy.U2FKeyHandleStrategy;
import org.crysil.actor.u2f.strategy.YubicoRandomKeyHandleStrategy;

import com.google.common.primitives.Bytes;

/**
 * Implements U2F commands for "Smartcard-HSM" Dual Interface cards from <a
 * href="http://www.smartcard-hsm.com/">CardContact</a>.
 * 
 * Note: You'll need one RSA key ({@link #RSA_KEY_ID}), one ECC key for the attestation signature ({@link #ATT_KEY_ID} )
 * with a matching certificate ({@link #ATT_CERT_ID}), one ECC key for the signature itself ( {@link #ECC_KEY_ID}) and
 * one certificate for that key ({@link #ATT_CERT_ID}) signed with the attestation key (needed in registration to create
 * a signature)
 * 
 * @see YubicoRandomKeyHandleStrategy for key handle generation and verification
 */
public class SmartcardHsmNfcU2FDeviceStrategy implements NfcU2FDeviceStrategy {

	public static byte[] SELECT = { 0x00, (byte) 0xA4, 0x04, 0x00, 0x0B, (byte) 0xE8, 0x2B, 0x06, 0x01, 0x04, 0x01,
			(byte) 0x81, (byte) 0xC3, 0x1F, 0x02, 0x01, 0x00 };

	// TODO Configure those IDs somehow?
	private static byte[] ATT_CERT_ID = { (byte) 0xCE, 0x08 };
	private static byte[] ATT_KEY_ID = { (byte) 0xC4, 0x08 };
	private static byte[] ECC_CERT_ID = { (byte) 0xCE, 0x09 };
	private static byte[] ECC_KEY_ID = { (byte) 0xC4, 0x09 };
	private static byte[] RSA_KEY_ID = { (byte) 0xC4, 0x0A };
	private static byte[] PIN = { 0x31, 0x32, 0x33, 0x34, 0x35, 0x36 };
	private U2FKeyHandleStrategy strategy = new YubicoRandomKeyHandleStrategy();

	@Override
	public String getVersion(U2FDeviceHandler device) throws IOException, APDUError {
		return "U2F_V2";
	}

	@Override
	public byte[] registerPlain(byte[] clientParam, byte[] appParam, U2FDeviceHandler device) throws Exception {
		byte[] eccCertBytes = readCertificate(device, ECC_CERT_ID);
		CertificateFactory cf = CertificateFactory.getInstance("X.509", "IAIK");
		X509Certificate eccCert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(eccCertBytes));

		byte[] keyEncoded = eccCert.getPublicKey().getEncoded();
		if (keyEncoded.length > 65) {
			keyEncoded = U2FUtil.stripMetaData(keyEncoded);
		}
		byte[] keyHandleBytes = strategy.calculateKeyHandle(appParam, device, this);

		byte[] signatureBytes = Bytes.concat(new byte[] { 0x00 }, appParam, clientParam, keyHandleBytes, keyEncoded);
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(signatureBytes);
		byte[] digest = md.digest();
		byte[] attSignature = executeSign(digest, ATT_KEY_ID, device);
		byte[] attCertBytes = readCertificate(device, ATT_CERT_ID);
		byte[] resp = Bytes.concat(new byte[] { 0x05 }, keyEncoded, new byte[] { (byte) keyHandleBytes.length },
				keyHandleBytes, attCertBytes, attSignature);

		return resp;
	}

	private byte[] readCertificate(U2FDeviceHandler device, byte[] certId) throws IOException, APDUError {
		byte[] apduSelect = { 0x00, (byte) 0xA4, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00 };
		System.arraycopy(certId, 0, apduSelect, 5, 2);
		byte[] response = device.send(apduSelect);
		byte[] apduRead = new byte[] { 0x00, (byte) 0xB1, 0x00, 0x00, 0x00, 0x00, 0x04, 0x54, 0x02, 0x00, 0x00, 0x01,
				response[5] };
		return device.send(apduRead);
	}

	private byte[] executeSign(byte[] signatureBytes, byte[] keyId, U2FDeviceHandler device) throws IOException,
			APDUError {
		byte[] apduSelect = { 0x00, (byte) 0xA4, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00 };
		System.arraycopy(keyId, 0, apduSelect, 5, 2);
		device.send(apduSelect);

		boolean logoutRequired = false;
		byte[] response = null;
		try {
			response = device.send(buildApduSign(signatureBytes, keyId));
		} catch (APDUError ex) {
			if (ex.getCode() == 0x6982) { // login required
				device.send(buildApduLogin());
				logoutRequired = true;
				response = device.send(buildApduSign(signatureBytes, keyId));
			} else {
				throw ex;
			}
		}
		if (logoutRequired) {
			byte[] apduLogout = new byte[] { 0x00, (byte) 0xA4, 0x04, 0x00, 0x0B, (byte) 0xE8, 0x2B, 0x06, 0x01, 0x04,
					0x01, (byte) 0x81, (byte) 0xC3, 0x1F, 0x02, 0x01, 0x00 };
			device.send(apduLogout);
		}
		return response;
	}

	private byte[] buildApduLogin() {
		byte[] apduLogin = new byte[5 + PIN.length];
		apduLogin[0] = 0x00;
		apduLogin[1] = 0x20;
		apduLogin[2] = 0x00;
		apduLogin[3] = (byte) 0x81;
		apduLogin[4] = (byte) PIN.length;
		System.arraycopy(PIN, 0, apduLogin, 5, PIN.length);
		return apduLogin;
	}

	private byte[] buildApduSign(byte[] signatureBytes, byte[] keyId) {
		byte[] apduSign = new byte[5 + signatureBytes.length + 1];
		apduSign[0] = (byte) 0x80;
		apduSign[1] = 0x68;
		apduSign[2] = keyId[1]; // TODO: Not sure where to put first byte tough
		apduSign[3] = 0x70;
		apduSign[4] = (byte) signatureBytes.length;
		apduSign[5 + signatureBytes.length] = (byte) 0x00;
		System.arraycopy(signatureBytes, 0, apduSign, 5, signatureBytes.length);
		return apduSign;
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

	@Override
	public byte[] signPlain(byte[] keyHandle, byte[] clientParam, byte[] appParam, byte[] counterB,
			U2FDeviceHandler device) throws Exception {
		if (!strategy.verifyKeyHandle(keyHandle, appParam, clientParam, device, this)) {
			throw new IOException("Invalid KeyHandle given");
		}

		int counter = new BigInteger(counterB).intValue();
		byte[] signatureBytes = buildSignatureBytes(appParam, clientParam, counter);
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(signatureBytes);
		byte[] digest = md.digest();
		byte[] resp = executeSign(digest, ECC_KEY_ID, device);
		return resp;
	}

	/**
	 * Deterministically derive a secret from a signing key to be used as a HMAC key <br />
	 * We can't use the key directly, because a private key (asymmetric) is not a secret key (symmetric)
	 */
	public Key getSecret(U2FDeviceHandler device) throws Exception {
		// byte[] textBytes = "super secret u2f".getBytes("UTF-8");
		// TODO find out how the heck to pad "textBytes" that this string is the result
		byte[] toSign = { (byte) 0x80, (byte) 0x68, (byte) 0x01, (byte) 0x20, (byte) 0x00, (byte) 0x01, (byte) 0x00,
				(byte) 0x00, (byte) 0x01, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x00,
				(byte) 0x73, (byte) 0x75, (byte) 0x70, (byte) 0x65, (byte) 0x72, (byte) 0x20, (byte) 0x73, (byte) 0x65,
				(byte) 0x63, (byte) 0x72, (byte) 0x65, (byte) 0x74, (byte) 0x20, (byte) 0x75, (byte) 0x32, (byte) 0x66,
				(byte) 0x01, (byte) 0x00 };

		byte[] apduSelect = { 0x00, (byte) 0xA4, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00 };
		System.arraycopy(RSA_KEY_ID, 0, apduSelect, 5, 2);
		device.send(apduSelect);

		boolean logoutRequired = false;
		byte[] response = null;
		try {
			response = device.send(toSign);
		} catch (APDUError ex) {
			if (ex.getCode() == 0x6982) { // login required
				device.send(buildApduLogin());
				logoutRequired = true;
				response = device.send(toSign);
			} else {
				throw ex;
			}
		}
		if (logoutRequired) {
			byte[] apduLogout = new byte[] { 0x00, (byte) 0xA4, 0x04, 0x00, 0x0B, (byte) 0xE8, 0x2B, 0x06, 0x01, 0x04,
					0x01, (byte) 0x81, (byte) 0xC3, 0x1F, 0x02, 0x01, 0x00 };
			device.send(apduLogout);
		}
		return new SecretKeySpec(response, "HmacSHA256");
	}

}
