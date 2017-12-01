package org.crysil.actor.softwarecrypto;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

import com.google.common.io.BaseEncoding;

/**
 * holds exactly one hardcoded key for demonstration purposes.
 */
public class SimpleKeyStore implements SoftwareCryptoKeyStore {

	/** the raw key data */
	private static final String rawPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkOWz5tbm7+q1Ap+y9nv864F7vqtKNED5/Gt3FTUfjwcSL7hKtZBRKjLKNyZDlaeJruojHU6sfZA6W1oQN5DOAU+SdfEeattYIwvtIHsBNeDSN2kxUph+NmQ6hW57Iyd3qeiq+obNTDZXgtfVSc7tFEYWd3hmFx3GlDJN1tvlBAXiXDxKKQuCnbh+PKiDW8yrrzaCnaC0bJl8qfos0FSE7DgHC/tesfTMtPtLyq+tYed4xXocAQWnk04B6KE2XiYKme2Ar6ZdxPq86jj6Jndc5j8LLbf8LrI0Zk3Xlu6ZDH//uqNwSk+0fBv+3DGg8/dHYBihhUfVGTyTjqF9Rr0KeQIDAQAB";
	private static final String rawPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCQ5bPm1ubv6rUCn7L2e/zrgXu+q0o0QPn8a3cVNR+PBxIvuEq1kFEqMso3JkOVp4mu6iMdTqx9kDpbWhA3kM4BT5J18R5q21gjC+0gewE14NI3aTFSmH42ZDqFbnsjJ3ep6Kr6hs1MNleC19VJzu0URhZ3eGYXHcaUMk3W2+UEBeJcPEopC4KduH48qINbzKuvNoKdoLRsmXyp+izQVITsOAcL+16x9My0+0vKr61h53jFehwBBaeTTgHooTZeJgqZ7YCvpl3E+rzqOPomd1zmPwstt/wusjRmTdeW7pkMf/+6o3BKT7R8G/7cMaDz90dgGKGFR9UZPJOOoX1GvQp5AgMBAAECggEAD8Xjgi3TZJtIAk28Y2rOltvo6rH2gU7v75eKtmUqpu0SAJ4XCuH3heiSvPNmQMakvSTZdvWjHwWJ5rscgBH8LOYat0pMwXzYYLNKCEK/mFv9gkDqGLfHKHhbav5CewqPMxCj+SLeuwzA1LpWSmMzSWVSSYXSljJcdTIvKUQVb20jEt//BQO0lFGeHp1p7eCm3Z41LJO7vTaOa3RuZRdNmm+ovsJ/0G0ET9/YYDSzL8NdBqbbcssnlwkUlGpa8dmvTvja4s2Ut53vkN2L+44DteHQkqGozB55842BVDDY2zwRbv3qRdP2TwQXbX6OIb8mofGudGcYKDsXKxFm3AyI0QKBgQDKB5vaLawjQrlNup/0k1s01Z2cdglfelcVxeC5JFA1el88Kx06C2DG3f+kVkZ7UJkaWEkLN6bDUPaJvpKbEYq8lTLXe4IGCpAPjpEfrTAFMS+qw0/aP9j3ptVKzYnvn9Nzr/vsJe/pKnqvvVbKUC/z2dWfmD/vSJh97/9cLEwaEQKBgQC3muzY0PaUSavbHATVTWGN8xOf9cdXkVlQQJvgjUC1mPW2fqmrWRRhPWmQ/6IUcTsbViv/dCpLZ61d43Lrp17JECgiX5N6kZ2hVKLnBBh0jp+AVhaSMdV9SLXW4BeoZ0NDIMA0KWO2z+CsS11qUWWlTzIWYZRz4rDMyH5rIitB6QKBgHMVKn56Ddh4Jb5VVNCpMAdEt+dshaJRulG4ym4sdVRBRQatOJTb7mDaDMm8K6ILI2uSZulw5hnUgNuuJy0WLQWHp9J/lG2EjtONzrVQnk4W2qRXZaFZRWqtKkrWEoDhdj4sPUEjyyny/LgnDs1oKCisKbgjrifMHrF+iOQkh6VBAoGBAJ4tpbEhTd3xHlLVb8CIOux9J0IfD478lNKT5vbtGnuY/M8tjmUliG5B50nZQjyUVJS3h447+RgfiPejNWWaOwnDlqAOcVMeUowkC3g1ShBYs0dwY1+t+TxHQQmO5DLcS9FwTMZjhhmAPBKTlzY6S/TAJumrRacU/LZgYLFRrazJAoGADDTb5rz8adzXao4okOOMGW5JWgSRt3osOMpmMCy3AfWNO59fJFoVFUVjsVjeknhxleUk8iceZ2xcSMOzWH1u3zxW0Ap0jChuvzyONWW/Ba1Td9Y7/t+pCHazve0tJuf6riLKwHelDD8vTLTDU5tm5Udw7VYHm+dBPseFc7Dv6x4=";
	private static final String rawCert = "MIICzjCCAbagAwIBAgIGAV/ed2FxMA0GCSqGSIb3DQEBCwUAMBExDzANBgNVBAMTBmNyeXNpbDAeFw0xNzExMjAxMjA0NDZaFw0yMTExMjAxMjA0NDZaMCMxITAfBgNVBAMMGHN0YXRpY0tleUVuY3J5cHRpb25BY3RvcjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAJDls+bW5u/qtQKfsvZ7/OuBe76rSjRA+fxrdxU1H48HEi+4SrWQUSoyyjcmQ5Wnia7qIx1OrH2QOltaEDeQzgFPknXxHmrbWCML7SB7ATXg0jdpMVKYfjZkOoVueyMnd6noqvqGzUw2V4LX1UnO7RRGFnd4ZhcdxpQyTdbb5QQF4lw8SikLgp24fjyog1vMq682gp2gtGyZfKn6LNBUhOw4Bwv7XrH0zLT7S8qvrWHneMV6HAEFp5NOAeihNl4mCpntgK+mXcT6vOo4+iZ3XOY/Cy23/C6yNGZN15bumQx//7qjcEpPtHwb/twxoPP3R2AYoYVH1Rk8k46hfUa9CnkCAwEAAaMaMBgwFgYDVR0lAQH/BAwwCgYIKwYBBQUHAwgwDQYJKoZIhvcNAQELBQADggEBAHDgGSV1mK/4wgUnx6BxSDoIavMtlTY/OptR1Jk/G8R58bfxCSD2guJZ5smUbLHslpVqfOMkYumX5UxkhD+w+L8SyM/jA0qQ561kGX7skC8as2/NRf9jH1GXpUemDSwihVFO1gKvBR5u9Nhh8ojDw9kdGXUAh5ciRTTlAfogvVFZzhvjUiDWHvQD6R+M6HTYnsSZB5ryQfKohcNjBKOVhNxlST81F1XYT+3uiFJj5rqCoaCiuaWMkKffOJsj9D32E+VY3EMAouvkDIXIXSIvmktFf+P/UudU0dhsRdJVnW28PZXvTHrf48gtR+hg1i183ht+dEBXkHJDUm1yM4s1GJg=";

	private static final String rawPublicKeyECDSA = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEsrzMLb8Dr6fz3dWmgvOdK3dZGkVAyGAks5rvbPmjaNK1tTJlYHsv8sQgSLauOSos/R7qLNY2NcLlBH3LqDg99g==";
	private static final String rawPrivateKeyECDSA = "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQg9JrLQtrn6RwH+7V34AiW8jNtuF9lgUluLYplxEf4qHuhRANCAASyvMwtvwOvp/Pd1aaC850rd1kaRUDIYCSzmu9s+aNo0rW1MmVgey/yxCBItq45Kiz9Huos1jY1wuUEfcuoOD32";
	private static final String rawCertEC = "MIIBlzCCAT2gAwIBAgIJAOTuZwR5nph7MAoGCCqGSM49BAMCMCgxJjAkBgNVBAMMHXN0YXRpY0tleUVuY3J5cHRpb25BY3RvckVDRFNBMB4XDTE2MDcyNjA5MTkwMFoXDTM2MDcyMTA5MTkwMFowKDEmMCQGA1UEAwwdc3RhdGljS2V5RW5jcnlwdGlvbkFjdG9yRUNEU0EwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAASyvMwtvwOvp/Pd1aaC850rd1kaRUDIYCSzmu9s+aNo0rW1MmVgey/yxCBItq45Kiz9Huos1jY1wuUEfcuoOD32o1AwTjAdBgNVHQ4EFgQUS0OmriRQ1Pp1ZWyB2i6HBUQKOrswHwYDVR0jBBgwFoAUS0OmriRQ1Pp1ZWyB2i6HBUQKOrswDAYDVR0TBAUwAwEB/zAKBggqhkjOPQQDAgNIADBFAiEApzYobPBo+EKteXBaiKYR+XOtbGIGQBX3+Q0NduXavEgCIB12fofx1gNfpHI7r3NwdK7+3RwnKw2tSkHHTGpgHijB";

	/** java representation of the above raw key data */
	private PublicKey pubKey, pubKeyECDSA;
	private PrivateKey privKey, privKeyECDSA;
	private X509Certificate cert, certEC;

	public SimpleKeyStore() throws KeyStoreUnavailableException {
		try {
			// create java representation of the raw key data
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(BaseEncoding.base64().decode(rawPublicKey));
			pubKey = keyFactory.generatePublic(pubKeySpec);

			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(BaseEncoding.base64().decode(rawPrivateKey));
			privKey = keyFactory.generatePrivate(privKeySpec);

      final CertificateFactory cf = CertificateFactory.getInstance("X.509");
      cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(BaseEncoding.base64().decode(rawCert)));

			keyFactory = KeyFactory.getInstance("EC");
			pubKeySpec = new X509EncodedKeySpec(BaseEncoding.base64().decode(rawPublicKeyECDSA));
			pubKeyECDSA = keyFactory.generatePublic(pubKeySpec);

			privKeySpec = new PKCS8EncodedKeySpec(BaseEncoding.base64().decode(rawPrivateKeyECDSA));
			privKeyECDSA = keyFactory.generatePrivate(privKeySpec);

			certEC = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(BaseEncoding.base64().decode(rawCertEC)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new KeyStoreUnavailableException();
		}
	}

	/**
	 * returns the public key of the specified key in JCE-readable form
	 *
	 * @param current the CrySIL key representation
	 * @return the public key
	 * @throws InvalidCertificateException
	 * @throws KeyNotFoundException
	 */
	@Override
	public PublicKey getPublicKey(final KeyHandle current) throws InvalidCertificateException, KeyNotFoundException {
		return pubKey;
	}

	/**
	 * returns the private key of the specified key in JCE-readable form
	 *
	 * @param current the CrySIL key representation
	 * @return the private key
	 * @throws KeyNotFoundException
	 */
	@Override
	public PrivateKey getPrivateKey(final KeyHandle current) throws KeyNotFoundException {
		return privKey;
	}

	public PrivateKey getJCEPrivateKey() {
		return privKey;
	}

	/**
	 * returns the certificate of the specified key in JCE-readable form
	 *
	 * @param current the CrySIL key representation
	 * @return the certificate
	 * @throws InvalidCertificateException
	 * @throws KeyNotFoundException
	 */
	@Override
	public X509Certificate getX509Certificate(final KeyHandle current)
			throws InvalidCertificateException, KeyNotFoundException {
		return cert;
	}

	public PublicKey getJCEPublicKeyECDSA() {
		return pubKeyECDSA;
	}

	public PrivateKey getJCEPrivateKeyECDSA() {
		return privKeyECDSA;
	}

	public X509Certificate getX509CertificateEC() {
		return certEC;
	}

	@Override
	public List<KeyHandle> getKeyList() {
		List<KeyHandle> result = new ArrayList<>();

		KeyHandle tmp = new KeyHandle();
		tmp.setId("test");
		tmp.setSubId("key");
		result.add(tmp);
		return result;
	}

	/**
	 * helper for creating the raw key data
	 * @throws KeyStoreUnavailableException 
	 */
	public static void main(String[] args) throws CertificateEncodingException, InvalidKeyException,
			IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException,
			KeyStoreUnavailableException {

		// uncomment next line to test encodings
		// new SimpleKeyStore();
		Security.addProvider(new BouncyCastleProvider());
		// generate a key pair
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
		keyPairGenerator.initialize(2048, new SecureRandom());
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		System.out.println("private static final String rawPublicKey = \""
				+ Base64.toBase64String(keyPair.getPublic().getEncoded()) + "\";");
		System.out.println("private static final String rawPrivateKey = \""
				+ Base64.toBase64String(keyPair.getPrivate().getEncoded()) + "\";");

		// build a certificate generator
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
		X500Principal dnName = new X500Principal("cn=crysil");

		// add some options
		certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		certGen.setSubjectDN(new X509Name("cn=staticKeyEncryptionActor"));
		certGen.setIssuerDN(dnName); // use the same
		// yesterday
		certGen.setNotBefore(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
		// in 2 years
		certGen.setNotAfter(new Date(System.currentTimeMillis() + 4L * 365 * 24 * 60 * 60 * 1000));
		certGen.setPublicKey(keyPair.getPublic());
		certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
		certGen.addExtension(X509Extensions.ExtendedKeyUsage, true,
				new ExtendedKeyUsage(KeyPurposeId.id_kp_timeStamping));

		// finally, sign the certificate with the private key of the same
		// KeyPair
		java.security.cert.X509Certificate cert = certGen.generate(keyPair.getPrivate(), "BC");
		System.out
				.println("private static final String rawCert = \"" + Base64.toBase64String(cert.getEncoded()) + "\";");
	}
}
