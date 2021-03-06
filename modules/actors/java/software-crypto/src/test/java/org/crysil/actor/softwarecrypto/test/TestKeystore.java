package org.crysil.actor.softwarecrypto.test;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.crysil.actor.softwarecrypto.SoftwareCryptoKeyStore;
import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.protocol.payload.crypto.key.ExternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

import com.google.common.io.BaseEncoding;

/**
 * holds exactly one hardcoded key for demonstration purposes.
 */
public class TestKeystore implements SoftwareCryptoKeyStore {

	/** the raw key data */
	private static final String rawPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwQSeSuzJ9MndkJeBkymovvDiFjCOYsd8zPgBGw2+euFfZI3aNJ32dZWMfxW1A2qDF39MazLsCAsE6Z2Vo5BQXz2xEiPlmTko8cUrjM9Qsj0WXc0vUmYKbPsrghco9tQ98PMVQn95kUUTpLDOSPFTebL6rolU+WEtdC1LQtrLaNhrowWJ0OnxtGJINc//ZZFNroHWzdmg6rlu+BojePSBzEoR7/2UmeGQK47vE7KsOl4zMW99E6njCp7vanDnK92XlOfHkAH8pYpnBDzbBBwdKgTok6sfP18/HoQGN853qSq2PXLgk6CNrJoKHB5N9Lp1REO6yCZMarm4eRYCd2GIhwIDAQAB";
	private static final String rawPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDBBJ5K7Mn0yd2Ql4GTKai+8OIWMI5ix3zM+AEbDb564V9kjdo0nfZ1lYx/FbUDaoMXf0xrMuwICwTpnZWjkFBfPbESI+WZOSjxxSuMz1CyPRZdzS9SZgps+yuCFyj21D3w8xVCf3mRRROksM5I8VN5svquiVT5YS10LUtC2sto2GujBYnQ6fG0Ykg1z/9lkU2ugdbN2aDquW74GiN49IHMShHv/ZSZ4ZArju8Tsqw6XjMxb30TqeMKnu9qcOcr3ZeU58eQAfylimcEPNsEHB0qBOiTqx8/Xz8ehAY3znepKrY9cuCToI2smgocHk30unVEQ7rIJkxqubh5FgJ3YYiHAgMBAAECggEAA4WwRFzAiLSpeJ0TKXX3ObQzv2l8IxzkGQ5KYJbK1PtbuaKqeFhV4POR5PymxuX3nJVfUWkvsL4rTkfEGu3tUYnlBGFemd1pF4ITO9+AVYzlZYTy8zDpW++dJJE66aNQ3ufR0rYvg4Og08fBU+y3dAhKTO/HDR2p7gkEIaUdeY+AxBdtYB9xEnZ7KNs9Tvcd8gYm7szcq5czNZ5g4kFZ1CM1RjUscG6KwfeTqpmmSDjjQy6PIHra5FCB5WzsuYqKxztXUUE5kdc3334evRJ7GG8SHvXHDllaX/MoDhCIqj9BCuaHyXv7H52KWUcl6gNB2MTuk5ssnHQEVymv9yELmQKBgQDtpGDabkuRdStXLMIpi0V2fJwo61+9tDrj+PNfKaQt/kVPN1L9Aj+3eRx9EBG0dWd8QGXgTiWca9iNKAfDFlbYp+YdUj+CdFBQjx4gaMmQPjS9shp+h8vyKbpe7gNnX5h2KKdt+Ioomeoz9AsU7wr/ddtP1/8bB0UhTBwQrvdnKQKBgQDP7cCclUhrfX6cKjlsH+WZsvRKV4TXa02U1hwQNhDPZWWQpkDMy00RooziPDnND7XrQ6RAD2Ls4K5VXnm+iQ+4sKWQHuknE8cnM2PSGMp25CnFMjnakAJVl2POUnV7fIT+OY0tZnaThMLF8TM20Q0cWrHjcgyC4ykSWAL75fPYLwKBgQDoaOCH/2JMaYjvgsiJFLnkfU3D/x3tS7xkhG6P3QvCJ3DlXjf9VRu3dezUqsiF8mQ48kowKn1CE37/3expcQmSbfHxLyUJknORtcZC7/hg51VxSCP9JxXgScsJWEFf8fALbwr/1BhaSNzx3nSQDpB08nCAD8BgUKXdQLAZ6OPwsQKBgGXt2BEqgUDoWSu260Vc8ZICDw1uj9mGaZa/yywLRPxWaY6aYYPDWbl+ZO/2tCMZQ4XcN+WLZWRX1D5XPPkxeXqBZfgbnxIf+O33nER/EKltuihIMeI53FsXBr863wq1BQEXN2T9KL2yREUCs6d4naO7th6YZxe2wgiTCotvs7TTAoGAJMzf8wYAJPhKGDd08KgXxDTtgntl7kegwnXJFNambg010X83i9yDKvkPZF4Nvy3FT2Bwb/qvWwN8vC9Jb55NotG0JbNVTdf75CS12G/U9YZxAHXAvdDdAUKI1jlL6n0lCU6vcrcCK35mde7lbnicq7TG8F/zEnIt82f7NzKXBDw=";
	private static final String rawCert = "MIICzjCCAbagAwIBAgIGAVLViUrLMA0GCSqGSIb3DQEBCwUAMBExDzANBgNVBAMTBmNyeXNpbDAeFw0xNjAyMTExMjUxMzBaFw0xNjAxMjcyMTA5NDFaMCMxITAfBgNVBAMMGHN0YXRpY0tleUVuY3J5cHRpb25BY3RvcjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMEEnkrsyfTJ3ZCXgZMpqL7w4hYwjmLHfMz4ARsNvnrhX2SN2jSd9nWVjH8VtQNqgxd/TGsy7AgLBOmdlaOQUF89sRIj5Zk5KPHFK4zPULI9Fl3NL1JmCmz7K4IXKPbUPfDzFUJ/eZFFE6SwzkjxU3my+q6JVPlhLXQtS0Lay2jYa6MFidDp8bRiSDXP/2WRTa6B1s3ZoOq5bvgaI3j0gcxKEe/9lJnhkCuO7xOyrDpeMzFvfROp4wqe72pw5yvdl5Tnx5AB/KWKZwQ82wQcHSoE6JOrHz9fPx6EBjfOd6kqtj1y4JOgjayaChweTfS6dURDusgmTGq5uHkWAndhiIcCAwEAAaMaMBgwFgYDVR0lAQH/BAwwCgYIKwYBBQUHAwgwDQYJKoZIhvcNAQELBQADggEBAAh7motLy9RdpvFCEgqMidrgON+n3570OTBjgePsWxHLXzdWRiKevmAI1VAi7K+Qr7KqdZhE7CM5KM5tmhUJ+9SorPmEPbyeaA8SVMDF0whibena3KorBTqIlTkYLwZL9UXkTnOb876VlijxqABKt/rOTP7dZrgErqgcbbTo8KVi2BueiXjLwlV8CJK4s2BWYcLPdMO+Z0jGIjcI4/wuk+60oR8tb5vUwWH62pXw+1IgpnVrklkkM3tNQ0v38A9xKgrK3c1UL7F9KWpZgsCkUR8lfDP0wHAx+Yd5fDp4vTdxSyH/WydLxy2syo1hyoRSE4SXWJBj+N0C+IgGOX3GsNQ=";
	private static final String rawPublicKeyECDSA = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEsrzMLb8Dr6fz3dWmgvOdK3dZGkVAyGAks5rvbPmjaNK1tTJlYHsv8sQgSLauOSos/R7qLNY2NcLlBH3LqDg99g==";
	private static final String rawPrivateKeyECDSA = "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQg9JrLQtrn6RwH+7V34AiW8jNtuF9lgUluLYplxEf4qHuhRANCAASyvMwtvwOvp/Pd1aaC850rd1kaRUDIYCSzmu9s+aNo0rW1MmVgey/yxCBItq45Kiz9Huos1jY1wuUEfcuoOD32";
	private static final String rawCertEC = "MIIBlzCCAT2gAwIBAgIJAOTuZwR5nph7MAoGCCqGSM49BAMCMCgxJjAkBgNVBAMMHXN0YXRpY0tleUVuY3J5cHRpb25BY3RvckVDRFNBMB4XDTE2MDcyNjA5MTkwMFoXDTM2MDcyMTA5MTkwMFowKDEmMCQGA1UEAwwdc3RhdGljS2V5RW5jcnlwdGlvbkFjdG9yRUNEU0EwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAASyvMwtvwOvp/Pd1aaC850rd1kaRUDIYCSzmu9s+aNo0rW1MmVgey/yxCBItq45Kiz9Huos1jY1wuUEfcuoOD32o1AwTjAdBgNVHQ4EFgQUS0OmriRQ1Pp1ZWyB2i6HBUQKOrswHwYDVR0jBBgwFoAUS0OmriRQ1Pp1ZWyB2i6HBUQKOrswDAYDVR0TBAUwAwEB/zAKBggqhkjOPQQDAgNIADBFAiEApzYobPBo+EKteXBaiKYR+XOtbGIGQBX3+Q0NduXavEgCIB12fofx1gNfpHI7r3NwdK7+3RwnKw2tSkHHTGpgHijB";

	/** java representation of the above raw key data */
	private PublicKey pubKey, pubKeyECDSA;
	private PrivateKey privKey, privKeyECDSA;
	private X509Certificate cert, certEC;

	public TestKeystore() throws KeyStoreUnavailableException {
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
	public X509Certificate getX509Certificate(final Key current) throws InvalidCertificateException, KeyNotFoundException {
		if (current instanceof KeyHandle) {
			return cert;
		} else if (current instanceof ExternalCertificate) {
			try {
				return ((ExternalCertificate) current).getCertificate();
			} catch (final CertificateException e) {
				throw new InvalidCertificateException();
			}
		} else {
      throw new KeyNotFoundException();
    }
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
	public X509Certificate getX509Certificate(KeyHandle keyHandle) {
		return cert;
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

  // /**
	// * helper for creating the raw key data
	// */
	// private void createKeyAndCertificate() throws
	// CertificateEncodingException, InvalidKeyException,
	// IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException,
	// SignatureException {
	// Security.addProvider(new BouncyCastleProvider());
	// // generate a key pair
	// KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA",
	// "BC");
	// keyPairGenerator.initialize(2048, new SecureRandom());
	// KeyPair keyPair = keyPairGenerator.generateKeyPair();
	//
	// System.out.println(Base64.toBase64String(keyPair.getPublic().getEncoded()));
	// System.out.println(Base64.toBase64String(keyPair.getPrivate().getEncoded()));
	//
	// // build a certificate generator
	// X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
	// X500Principal dnName = new X500Principal("cn=crysil");
	//
	// // add some options
	// certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
	// certGen.setSubjectDN(new X509Name("cn=staticKeyEncryptionActor"));
	// certGen.setIssuerDN(dnName); // use the same
	// // yesterday
	// certGen.setNotBefore(new Date(System.currentTimeMillis() - 24 * 60 * 60 *
	// 1000));
	// // in 2 years
	// certGen.setNotAfter(new Date(System.currentTimeMillis() + 2 * 365 * 24 *
	// 60 * 60 * 1000));
	// certGen.setPublicKey(keyPair.getPublic());
	// certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
	// certGen.addExtension(X509Extensions.ExtendedKeyUsage, true,
	// new ExtendedKeyUsage(KeyPurposeId.id_kp_timeStamping));
	//
	// // finally, sign the certificate with the private key of the same
	// // KeyPair
	// java.security.cert.X509Certificate cert =
	// certGen.generate(keyPair.getPrivate(), "BC");
	// System.out.println(Base64.toBase64String(cert.getEncoded()));
	// }
}
