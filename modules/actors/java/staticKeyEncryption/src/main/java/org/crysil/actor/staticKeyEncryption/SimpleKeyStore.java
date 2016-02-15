package org.crysil.actor.staticKeyEncryption;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.protocol.payload.crypto.key.ExternalCertificate;
import org.crysil.protocol.payload.crypto.key.InternalCertificate;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

import com.google.common.io.BaseEncoding;

/**
 * holds exactly one hardcoded key for demonstration purposes.
 */
public class SimpleKeyStore {

	/** the raw key data */
	private static final String rawPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwQSeSuzJ9MndkJeBkymovvDiFjCOYsd8zPgBGw2+euFfZI3aNJ32dZWMfxW1A2qDF39MazLsCAsE6Z2Vo5BQXz2xEiPlmTko8cUrjM9Qsj0WXc0vUmYKbPsrghco9tQ98PMVQn95kUUTpLDOSPFTebL6rolU+WEtdC1LQtrLaNhrowWJ0OnxtGJINc//ZZFNroHWzdmg6rlu+BojePSBzEoR7/2UmeGQK47vE7KsOl4zMW99E6njCp7vanDnK92XlOfHkAH8pYpnBDzbBBwdKgTok6sfP18/HoQGN853qSq2PXLgk6CNrJoKHB5N9Lp1REO6yCZMarm4eRYCd2GIhwIDAQAB";
	private static final String rawPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDBBJ5K7Mn0yd2Ql4GTKai+8OIWMI5ix3zM+AEbDb564V9kjdo0nfZ1lYx/FbUDaoMXf0xrMuwICwTpnZWjkFBfPbESI+WZOSjxxSuMz1CyPRZdzS9SZgps+yuCFyj21D3w8xVCf3mRRROksM5I8VN5svquiVT5YS10LUtC2sto2GujBYnQ6fG0Ykg1z/9lkU2ugdbN2aDquW74GiN49IHMShHv/ZSZ4ZArju8Tsqw6XjMxb30TqeMKnu9qcOcr3ZeU58eQAfylimcEPNsEHB0qBOiTqx8/Xz8ehAY3znepKrY9cuCToI2smgocHk30unVEQ7rIJkxqubh5FgJ3YYiHAgMBAAECggEAA4WwRFzAiLSpeJ0TKXX3ObQzv2l8IxzkGQ5KYJbK1PtbuaKqeFhV4POR5PymxuX3nJVfUWkvsL4rTkfEGu3tUYnlBGFemd1pF4ITO9+AVYzlZYTy8zDpW++dJJE66aNQ3ufR0rYvg4Og08fBU+y3dAhKTO/HDR2p7gkEIaUdeY+AxBdtYB9xEnZ7KNs9Tvcd8gYm7szcq5czNZ5g4kFZ1CM1RjUscG6KwfeTqpmmSDjjQy6PIHra5FCB5WzsuYqKxztXUUE5kdc3334evRJ7GG8SHvXHDllaX/MoDhCIqj9BCuaHyXv7H52KWUcl6gNB2MTuk5ssnHQEVymv9yELmQKBgQDtpGDabkuRdStXLMIpi0V2fJwo61+9tDrj+PNfKaQt/kVPN1L9Aj+3eRx9EBG0dWd8QGXgTiWca9iNKAfDFlbYp+YdUj+CdFBQjx4gaMmQPjS9shp+h8vyKbpe7gNnX5h2KKdt+Ioomeoz9AsU7wr/ddtP1/8bB0UhTBwQrvdnKQKBgQDP7cCclUhrfX6cKjlsH+WZsvRKV4TXa02U1hwQNhDPZWWQpkDMy00RooziPDnND7XrQ6RAD2Ls4K5VXnm+iQ+4sKWQHuknE8cnM2PSGMp25CnFMjnakAJVl2POUnV7fIT+OY0tZnaThMLF8TM20Q0cWrHjcgyC4ykSWAL75fPYLwKBgQDoaOCH/2JMaYjvgsiJFLnkfU3D/x3tS7xkhG6P3QvCJ3DlXjf9VRu3dezUqsiF8mQ48kowKn1CE37/3expcQmSbfHxLyUJknORtcZC7/hg51VxSCP9JxXgScsJWEFf8fALbwr/1BhaSNzx3nSQDpB08nCAD8BgUKXdQLAZ6OPwsQKBgGXt2BEqgUDoWSu260Vc8ZICDw1uj9mGaZa/yywLRPxWaY6aYYPDWbl+ZO/2tCMZQ4XcN+WLZWRX1D5XPPkxeXqBZfgbnxIf+O33nER/EKltuihIMeI53FsXBr863wq1BQEXN2T9KL2yREUCs6d4naO7th6YZxe2wgiTCotvs7TTAoGAJMzf8wYAJPhKGDd08KgXxDTtgntl7kegwnXJFNambg010X83i9yDKvkPZF4Nvy3FT2Bwb/qvWwN8vC9Jb55NotG0JbNVTdf75CS12G/U9YZxAHXAvdDdAUKI1jlL6n0lCU6vcrcCK35mde7lbnicq7TG8F/zEnIt82f7NzKXBDw=";
	private static final String rawCert = "MIICzjCCAbagAwIBAgIGAVLViUrLMA0GCSqGSIb3DQEBCwUAMBExDzANBgNVBAMTBmNyeXNpbDAeFw0xNjAyMTExMjUxMzBaFw0xNjAxMjcyMTA5NDFaMCMxITAfBgNVBAMMGHN0YXRpY0tleUVuY3J5cHRpb25BY3RvcjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMEEnkrsyfTJ3ZCXgZMpqL7w4hYwjmLHfMz4ARsNvnrhX2SN2jSd9nWVjH8VtQNqgxd/TGsy7AgLBOmdlaOQUF89sRIj5Zk5KPHFK4zPULI9Fl3NL1JmCmz7K4IXKPbUPfDzFUJ/eZFFE6SwzkjxU3my+q6JVPlhLXQtS0Lay2jYa6MFidDp8bRiSDXP/2WRTa6B1s3ZoOq5bvgaI3j0gcxKEe/9lJnhkCuO7xOyrDpeMzFvfROp4wqe72pw5yvdl5Tnx5AB/KWKZwQ82wQcHSoE6JOrHz9fPx6EBjfOd6kqtj1y4JOgjayaChweTfS6dURDusgmTGq5uHkWAndhiIcCAwEAAaMaMBgwFgYDVR0lAQH/BAwwCgYIKwYBBQUHAwgwDQYJKoZIhvcNAQELBQADggEBAAh7motLy9RdpvFCEgqMidrgON+n3570OTBjgePsWxHLXzdWRiKevmAI1VAi7K+Qr7KqdZhE7CM5KM5tmhUJ+9SorPmEPbyeaA8SVMDF0whibena3KorBTqIlTkYLwZL9UXkTnOb876VlijxqABKt/rOTP7dZrgErqgcbbTo8KVi2BueiXjLwlV8CJK4s2BWYcLPdMO+Z0jGIjcI4/wuk+60oR8tb5vUwWH62pXw+1IgpnVrklkkM3tNQ0v38A9xKgrK3c1UL7F9KWpZgsCkUR8lfDP0wHAx+Yd5fDp4vTdxSyH/WydLxy2syo1hyoRSE4SXWJBj+N0C+IgGOX3GsNQ=";

	/** java representation of the above raw key data */
	private PublicKey pubKey;
	private PrivateKey privKey;
	private X509Certificate cert;

	/**
	 * make it a singleton to avoid loading the keys everytime we need them
	 */
	private static SimpleKeyStore instance = null;

	/**
	 * get the single instance
	 * 
	 * @return the singleton instance of SimpleKeyStore
	 * @throws KeyStoreUnavailableException
	 */
	public static SimpleKeyStore getInstance() throws KeyStoreUnavailableException {
		if (null == instance)
			instance = new SimpleKeyStore();
		return instance;
	}

	private SimpleKeyStore() throws KeyStoreUnavailableException {
		try {

			// create java representation of the raw key data
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(BaseEncoding.base64().decode(rawPublicKey));
			pubKey = keyFactory.generatePublic(pubKeySpec);

			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(BaseEncoding.base64().decode(rawPrivateKey));
			privKey = keyFactory.generatePrivate(privKeySpec);

			cert = X509Certificate.getInstance(BaseEncoding.base64().decode(rawCert));
		} catch (Exception e) {
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
	public PublicKey getJCEPublicKey(Key current) throws InvalidCertificateException, KeyNotFoundException {
		if (current instanceof KeyHandle || current instanceof InternalCertificate) {
			return pubKey;
		} else if (current instanceof ExternalCertificate) {
			try {
				return ((ExternalCertificate) current).getCertificate().getPublicKey();
			} catch (CertificateException e) {
				throw new InvalidCertificateException();
			}
		} else
			throw new KeyNotFoundException();
	}

	/**
	 * returns the private key of the specified key in JCE-readable form
	 * 
	 * @param current the CrySIL key representation
	 * @return the private key
	 * @throws KeyNotFoundException
	 */
	public PrivateKey getJCEPrivateKey(Key current) throws KeyNotFoundException {
		if (current instanceof KeyHandle || current instanceof InternalCertificate) {
			return privKey;
		} else
			throw new KeyNotFoundException();
	}

	/**
	 * returns the certificate of the specified key in JCE-readable form
	 * 
	 * @param current the CrySIL key representation
	 * @return the certificate
	 * @throws InvalidCertificateException
	 * @throws KeyNotFoundException
	 */
	public X509Certificate getX509Certificate(Key current) throws InvalidCertificateException, KeyNotFoundException {
		if (current instanceof KeyHandle || current instanceof InternalCertificate) {
			return cert;
		} else if (current instanceof ExternalCertificate) {
			try {
				return ((ExternalCertificate) current).getCertificate();
			} catch (CertificateException e) {
				throw new InvalidCertificateException();
			}
		} else
			throw new KeyNotFoundException();
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
