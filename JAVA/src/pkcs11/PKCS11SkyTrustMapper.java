package pkcs11;

import iaik.security.rsa.RSAPublicKey;
import iaik.utils.Base64Exception;
import iaik.utils.Util;
import iaik.x509.X509Certificate;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.x500.X500Principal;

import obj.CK_ATTRIBUTE;
import obj.CK_ATTRIBUTE_TYPE;
import obj.CK_CERTIFICATE_TYPE;
import obj.CK_KEY_TYPE;
import obj.CK_MECHANISM;
import obj.CK_MECHANISM_TYPE;
import obj.CK_OBJECT_TYPE;
import obj.CK_RETURN_TYPE;
import objects.ObjectBuilder;
import objects.PKCS11Object;
import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKeyCertificate;

public class PKCS11SkyTrustMapper {

	private static HashMap<Long, SkyTrustAlgorithm> mechanism_map = new HashMap<>();
	private static ArrayList<CK_ATTRIBUTE> skytrust_template;
	static {
		skytrust_template = new ArrayList<>();
		skytrust_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_TOKEN,
				true, 1));
		skytrust_template.add(new CK_ATTRIBUTE(
				CK_ATTRIBUTE_TYPE.CKA_MODIFIABLE, false, 1));
		skytrust_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_KEY_TYPE,
				CK_KEY_TYPE.CKK_RSA, 8));
		skytrust_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_LOCAL,
				false, 1));
		skytrust_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_DERIVE,
				false, 1));
	}
	static {
		mechanism_map.put(CK_MECHANISM_TYPE.CKM_RSA_PKCS,
				SkyTrustAlgorithm.RSAES_PKCS1_V1_5);
		mechanism_map.put(CK_MECHANISM_TYPE.CKM_RSA_PKCS_PSS,
				SkyTrustAlgorithm.RSA_PSS);
		// mechanism_map.put(CK_MECHANISM_TYPE.CKM_RSA_PKCS_OAEP,SkyTrustAlgorithm.RSA_OAEP);
		mechanism_map.put(CK_MECHANISM_TYPE.CKM_SHA1_RSA_PKCS,
				SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_1);
		mechanism_map.put(CK_MECHANISM_TYPE.CKM_SHA256_RSA_PKCS,
				SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_256);
		mechanism_map.put(CK_MECHANISM_TYPE.CKM_SHA512_RSA_PKCS,
				SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_512);
		mechanism_map.put(CK_MECHANISM_TYPE.CKM_SHA224_RSA_PKCS,
				SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_224);
	}

	public static SkyTrustAlgorithm mapMechanism(CK_MECHANISM mech)
			throws PKCS11Error {
		SkyTrustAlgorithm algo = mechanism_map.get(mech.getMechanism());
		if (algo == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_MECHANISM_INVALID);
		}
		return algo;
	}

	public static SKey mapKey(PKCS11Object key) throws PKCS11Error {
		if (key == null) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_KEY_HANDLE_INVALID);
		}
		// key is not a keyObj
		CK_ATTRIBUTE attributeClass = key
				.getAttribute(CK_ATTRIBUTE_TYPE.CKA_CLASS);
		if (!attributeClass.getpValue().equals(CK_OBJECT_TYPE.CKO_PRIVATE_KEY)) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_KEY_HANDLE_INVALID);
		}

		// key is not a SkytrustKey
		Object skykey = key.getTag();
		if (skykey == null || !(skykey instanceof SKey)) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_KEY_HANDLE_INVALID);
		}
		return (SKey) skykey;
	}

	public static PKCS11Object mapToCert(SKey key) throws PKCS11Error {
		if (key == null) {
			return null;
		}

		if (!key.getRepresentation().equals("certificate")) {
			return null;
		}
		ArrayList<CK_ATTRIBUTE> cert_template = new ArrayList<>(
				skytrust_template);
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ID, key
				.getId().getBytes(), key.getId().getBytes().length));
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_LABEL,
				"skytrust" + key.getId(), 10)); // TODO: fix length
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_CLASS,
				CK_OBJECT_TYPE.CKO_CERTIFICATE, 8));
		cert_template.add(new CK_ATTRIBUTE(
				CK_ATTRIBUTE_TYPE.CKA_CERTIFICATE_TYPE,
				CK_CERTIFICATE_TYPE.CKC_X_509, 8));
		cert_template.add(new CK_ATTRIBUTE(
				CK_ATTRIBUTE_TYPE.CKA_CERTIFICATE_CATEGORY, 1L, 8));
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_TRUSTED, true,
				1));

		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_PRIVATE,
				false, 1));
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_EXTRACTABLE,
				true, 1));
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SENSITIVE,
				false, 1));
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_VERIFY, true,
				1));
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ENCRYPT, true,
				1));
		cert_template
				.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_WRAP, false, 1));

		// byte[] data = new byte[10];
		// cert_template.add(new
		// CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_MODULUS,data, data.length));
		// cert_template.add(new
		// CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_MODULUS_BITS,10,10));
		// cert_template.add(new
		// CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_PUBLIC_EXPONENT,data,
		// data.length));

		String certb64 = ((SKeyCertificate) key).getEncodedCertificate();
		certb64 = "MIIEoDCCBAmgAwIBAgIHAoma5vyQ4jANBgkqhkiG9w0BAQUFADBAMSIwIAYDVQQDExlJQUlLIFRlc3QgSW50ZXJtZWRpYXRlIENBMQ0wCwYDVQQKEwRJQUlLMQswCQYDVQQGEwJBVDAeFw0xMzEwMDMxMDAxMThaFw0yMzEwMDMxMDAxMThaMIHSMQswCQYDVQQGEwJBVDENMAsGA1UEBxMER3JhejEmMCQGA1UEChMdR3JheiBVbml2ZXJzaXR5IG9mIFRlY2hub2xvZ3kxSDBGBgNVBAsTP0luc3RpdHV0ZSBmb3IgQXBwbGllZCBJbmZvcm1hdGlvbiBQcm9jZXNzaW5nIGFuZCBDb21tdW5pY2F0aW9uczETMBEGA1UEBBMKUmVpdGVyIEVuYzEQMA4GA1UEKhMHQW5kcmVhczEbMBkGA1UEAxMSQW5kcmVhcyBSZWl0ZXIgRW5jMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyLHXdCy/n9WjofFDG9X11vyBQESaDyNsY+XiRwxFMslJRYlG12yCN5ESle64HsfTMugOKxut1zlDVKefi5HqkZxf0pTeUd9lEZkLvXx5oUX95jsIZVHepumuw+aI1Fu4+O9kK/lTBHtSmR8vgPKXvnvdkgvFhKzlEUfpCth90JMN8pFzkzQaQWwQokZk3j325QYK0FYWua/HTZdDnzcboew4NPn0TJkSYbJQZb4QxIIBb72ZbyHK3v5TSmcG+QSbe/ZC5w++VPR60eMFfIB0IQUv/mwTN5t5y/o5uJMy42pd4sscBW6tC5Mw/NLxk59AFN7MX9mXyUor9ZsyQD/8CwIDAQABo4IBizCCAYcwDgYDVR0PAQH/BAQDAgQwMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFHtef3qaiBxZxmwx1iDNDtrMjUPRMFAGA1UdHwRJMEcwRaBDoEGGP2h0dHA6Ly9jYS5pYWlrLnR1Z3Jhei5hdC9jYXBzby9jcmxzL0lBSUtUZXN0X0ludGVybWVkaWF0ZUNBLmNybDCBqgYIKwYBBQUHAQEEgZ0wgZowSgYIKwYBBQUHMAGGPmh0dHA6Ly9jYS5pYWlrLnR1Z3Jhei5hdC9jYXBzby9PQ1NQP2NhPUlBSUtUZXN0X0ludGVybWVkaWF0ZUNBMEwGCCsGAQUFBzAChkBodHRwOi8vY2EuaWFpay50dWdyYXouYXQvY2Fwc28vY2VydHMvSUFJS1Rlc3RfSW50ZXJtZWRpYXRlQ0EuY2VyMCgGA1UdEQQhMB+BHWFuZHJlYXMucmVpdGVyQGlhaWsudHVncmF6LmF0MB8GA1UdIwQYMBaAFGiiXhHa3i+Aa0REv436ZTaBJKdvMA0GCSqGSIb3DQEBBQUAA4GBAGCfzauvTNTAVBPi/ziOtw1PZ5TRhxpabJljt3HmFoK+dtXarv6NcK2w4tLSdtHp+5/6TTB5KlY5tlfEs/LOlb3AGctVe1qinvGBdv+unD5mWiJ7Z+ASCzuzPGOaDtl4KfB+3UpqFYA+S8ncsmquEtetcxl+5Cqq5hX5T0bVDghd";
		try {
			byte[] cert = Util.fromBase64String(certb64);
			cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_VALUE,
					cert, certb64.length()));

			X509Certificate iaikcert = new X509Certificate(cert);
			X500Principal subject = iaikcert.getSubjectX500Principal();
			X500Principal issuer = iaikcert.getIssuerX500Principal();
			cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ISSUER,
					issuer.getEncoded(), issuer.getEncoded().length));
			cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SUBJECT,
					subject.getEncoded(), subject.getEncoded().length));
			cert_template.add(new CK_ATTRIBUTE(
					CK_ATTRIBUTE_TYPE.CKA_SERIAL_NUMBER, iaikcert
							.getSerialNumber().toByteArray(), iaikcert
							.getSerialNumber().toByteArray().length));

		} catch (CertificateException | Base64Exception e) {
			cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ISSUER,
					"ISSUER".getBytes(), "ISSUER".getBytes().length));
			cert_template.add(new CK_ATTRIBUTE(
					CK_ATTRIBUTE_TYPE.CKA_SERIAL_NUMBER, "SERIAL_NUMBER"
							.getBytes(), "SERIAL_NUMBER".getBytes().length));
			cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SUBJECT,
					"SUBJECT".getBytes(), "SUBJECT".getBytes().length));
			e.printStackTrace();
		}

		PKCS11Object obj = ObjectBuilder
				.createFromTemplate((CK_ATTRIBUTE[]) cert_template
						.toArray(new CK_ATTRIBUTE[cert_template.size()]));
		obj.setTag(key);
		return obj;
	}

	public static PKCS11Object mapToPub(SKey key) throws PKCS11Error {
		if (key == null) {
			return null;
		}

		if (!key.getRepresentation().equals("certificate")) {
			return null;
		}
		ArrayList<CK_ATTRIBUTE> pub_template = new ArrayList<>(
				skytrust_template);
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ID, key.getId()
				.getBytes(), key.getId().getBytes().length));
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_LABEL,
				"skytrust" + key.getId(), 10));
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_CLASS,
				CK_OBJECT_TYPE.CKO_PUBLIC_KEY, 8));

		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_PRIVATE, false,
				1));
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_EXTRACTABLE,
				true, 1));
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SENSITIVE,
				false, 1));
		pub_template
				.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_VERIFY, true, 1));
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ENCRYPT, true,
				1));
		pub_template
				.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_WRAP, false, 1));

		String certb64 = ((SKeyCertificate) key).getEncodedCertificate();
		certb64 = "MIIEoDCCBAmgAwIBAgIHAoma5vyQ4jANBgkqhkiG9w0BAQUFADBAMSIwIAYDVQQDExlJQUlLIFRlc3QgSW50ZXJtZWRpYXRlIENBMQ0wCwYDVQQKEwRJQUlLMQswCQYDVQQGEwJBVDAeFw0xMzEwMDMxMDAxMThaFw0yMzEwMDMxMDAxMThaMIHSMQswCQYDVQQGEwJBVDENMAsGA1UEBxMER3JhejEmMCQGA1UEChMdR3JheiBVbml2ZXJzaXR5IG9mIFRlY2hub2xvZ3kxSDBGBgNVBAsTP0luc3RpdHV0ZSBmb3IgQXBwbGllZCBJbmZvcm1hdGlvbiBQcm9jZXNzaW5nIGFuZCBDb21tdW5pY2F0aW9uczETMBEGA1UEBBMKUmVpdGVyIEVuYzEQMA4GA1UEKhMHQW5kcmVhczEbMBkGA1UEAxMSQW5kcmVhcyBSZWl0ZXIgRW5jMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyLHXdCy/n9WjofFDG9X11vyBQESaDyNsY+XiRwxFMslJRYlG12yCN5ESle64HsfTMugOKxut1zlDVKefi5HqkZxf0pTeUd9lEZkLvXx5oUX95jsIZVHepumuw+aI1Fu4+O9kK/lTBHtSmR8vgPKXvnvdkgvFhKzlEUfpCth90JMN8pFzkzQaQWwQokZk3j325QYK0FYWua/HTZdDnzcboew4NPn0TJkSYbJQZb4QxIIBb72ZbyHK3v5TSmcG+QSbe/ZC5w++VPR60eMFfIB0IQUv/mwTN5t5y/o5uJMy42pd4sscBW6tC5Mw/NLxk59AFN7MX9mXyUor9ZsyQD/8CwIDAQABo4IBizCCAYcwDgYDVR0PAQH/BAQDAgQwMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFHtef3qaiBxZxmwx1iDNDtrMjUPRMFAGA1UdHwRJMEcwRaBDoEGGP2h0dHA6Ly9jYS5pYWlrLnR1Z3Jhei5hdC9jYXBzby9jcmxzL0lBSUtUZXN0X0ludGVybWVkaWF0ZUNBLmNybDCBqgYIKwYBBQUHAQEEgZ0wgZowSgYIKwYBBQUHMAGGPmh0dHA6Ly9jYS5pYWlrLnR1Z3Jhei5hdC9jYXBzby9PQ1NQP2NhPUlBSUtUZXN0X0ludGVybWVkaWF0ZUNBMEwGCCsGAQUFBzAChkBodHRwOi8vY2EuaWFpay50dWdyYXouYXQvY2Fwc28vY2VydHMvSUFJS1Rlc3RfSW50ZXJtZWRpYXRlQ0EuY2VyMCgGA1UdEQQhMB+BHWFuZHJlYXMucmVpdGVyQGlhaWsudHVncmF6LmF0MB8GA1UdIwQYMBaAFGiiXhHa3i+Aa0REv436ZTaBJKdvMA0GCSqGSIb3DQEBBQUAA4GBAGCfzauvTNTAVBPi/ziOtw1PZ5TRhxpabJljt3HmFoK+dtXarv6NcK2w4tLSdtHp+5/6TTB5KlY5tlfEs/LOlb3AGctVe1qinvGBdv+unD5mWiJ7Z+ASCzuzPGOaDtl4KfB+3UpqFYA+S8ncsmquEtetcxl+5Cqq5hX5T0bVDghd";

		try {
			byte[] cert = Util.fromBase64String(certb64);
			X509Certificate iaikcert = new X509Certificate(cert);

			PublicKey k = iaikcert.getPublicKey();
			RSAPublicKey rsakey = new RSAPublicKey(k.getEncoded()); // TODO geht
																	// das ned
																	// irgendwie
																	// schöner?
			pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_VALUE, k
					.getEncoded(), k.getEncoded().length));

			BigInteger exp = rsakey.getPublicExponent();
			BigInteger mod = rsakey.getModulus();
			if (mod == null || exp == null) {
				return null;
			}
			pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_MODULUS,
					mod.toByteArray(), mod.toByteArray().length));
			pub_template.add(new CK_ATTRIBUTE(
					CK_ATTRIBUTE_TYPE.CKA_MODULUS_BITS, (long) mod.bitLength(),
					8));
			pub_template.add(new CK_ATTRIBUTE(
					CK_ATTRIBUTE_TYPE.CKA_PUBLIC_EXPONENT, exp.toByteArray(),
					exp.toByteArray().length));

			X500Principal subject = iaikcert.getSubjectX500Principal();
			pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SUBJECT,
					subject.getEncoded(), subject.getEncoded().length));

		} catch (CertificateException | Base64Exception | InvalidKeyException e) {
			e.printStackTrace();
			return null;
		}

		PKCS11Object obj = ObjectBuilder
				.createFromTemplate((CK_ATTRIBUTE[]) pub_template
						.toArray(new CK_ATTRIBUTE[pub_template.size()]));
		obj.setTag(key);
		return obj;
	}

	public static PKCS11Object mapToPrivate(SKey key) throws PKCS11Error {
		ArrayList<CK_ATTRIBUTE> private_template = new ArrayList<>(
				skytrust_template);
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_LABEL,
				"skytrust" + key.getId(), 10));

		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_CLASS,
				CK_OBJECT_TYPE.CKO_PRIVATE_KEY, 8));
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_PRIVATE,
				true, 1));
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SENSITIVE,
				true, 1));
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ID, key
				.getId().getBytes(), key.getId().getBytes().length));
		private_template.add(new CK_ATTRIBUTE(
				CK_ATTRIBUTE_TYPE.CKA_ALWAYS_AUTHENTICATE, false, 1));

		private_template.add(new CK_ATTRIBUTE(
				CK_ATTRIBUTE_TYPE.CKA_NEVER_EXTRACTABLE, true, 1));
		private_template.add(new CK_ATTRIBUTE(
				CK_ATTRIBUTE_TYPE.CKA_EXTRACTABLE, false, 1));
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SIGN, true,
				1));
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_DECRYPT,
				true, 1));
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_UNWRAP,
				false, 1));

		String certb64 = ((SKeyCertificate) key).getEncodedCertificate();
		certb64 = "MIIEoDCCBAmgAwIBAgIHAoma5vyQ4jANBgkqhkiG9w0BAQUFADBAMSIwIAYDVQQDExlJQUlLIFRlc3QgSW50ZXJtZWRpYXRlIENBMQ0wCwYDVQQKEwRJQUlLMQswCQYDVQQGEwJBVDAeFw0xMzEwMDMxMDAxMThaFw0yMzEwMDMxMDAxMThaMIHSMQswCQYDVQQGEwJBVDENMAsGA1UEBxMER3JhejEmMCQGA1UEChMdR3JheiBVbml2ZXJzaXR5IG9mIFRlY2hub2xvZ3kxSDBGBgNVBAsTP0luc3RpdHV0ZSBmb3IgQXBwbGllZCBJbmZvcm1hdGlvbiBQcm9jZXNzaW5nIGFuZCBDb21tdW5pY2F0aW9uczETMBEGA1UEBBMKUmVpdGVyIEVuYzEQMA4GA1UEKhMHQW5kcmVhczEbMBkGA1UEAxMSQW5kcmVhcyBSZWl0ZXIgRW5jMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyLHXdCy/n9WjofFDG9X11vyBQESaDyNsY+XiRwxFMslJRYlG12yCN5ESle64HsfTMugOKxut1zlDVKefi5HqkZxf0pTeUd9lEZkLvXx5oUX95jsIZVHepumuw+aI1Fu4+O9kK/lTBHtSmR8vgPKXvnvdkgvFhKzlEUfpCth90JMN8pFzkzQaQWwQokZk3j325QYK0FYWua/HTZdDnzcboew4NPn0TJkSYbJQZb4QxIIBb72ZbyHK3v5TSmcG+QSbe/ZC5w++VPR60eMFfIB0IQUv/mwTN5t5y/o5uJMy42pd4sscBW6tC5Mw/NLxk59AFN7MX9mXyUor9ZsyQD/8CwIDAQABo4IBizCCAYcwDgYDVR0PAQH/BAQDAgQwMAwGA1UdEwEB/wQCMAAwHQYDVR0OBBYEFHtef3qaiBxZxmwx1iDNDtrMjUPRMFAGA1UdHwRJMEcwRaBDoEGGP2h0dHA6Ly9jYS5pYWlrLnR1Z3Jhei5hdC9jYXBzby9jcmxzL0lBSUtUZXN0X0ludGVybWVkaWF0ZUNBLmNybDCBqgYIKwYBBQUHAQEEgZ0wgZowSgYIKwYBBQUHMAGGPmh0dHA6Ly9jYS5pYWlrLnR1Z3Jhei5hdC9jYXBzby9PQ1NQP2NhPUlBSUtUZXN0X0ludGVybWVkaWF0ZUNBMEwGCCsGAQUFBzAChkBodHRwOi8vY2EuaWFpay50dWdyYXouYXQvY2Fwc28vY2VydHMvSUFJS1Rlc3RfSW50ZXJtZWRpYXRlQ0EuY2VyMCgGA1UdEQQhMB+BHWFuZHJlYXMucmVpdGVyQGlhaWsudHVncmF6LmF0MB8GA1UdIwQYMBaAFGiiXhHa3i+Aa0REv436ZTaBJKdvMA0GCSqGSIb3DQEBBQUAA4GBAGCfzauvTNTAVBPi/ziOtw1PZ5TRhxpabJljt3HmFoK+dtXarv6NcK2w4tLSdtHp+5/6TTB5KlY5tlfEs/LOlb3AGctVe1qinvGBdv+unD5mWiJ7Z+ASCzuzPGOaDtl4KfB+3UpqFYA+S8ncsmquEtetcxl+5Cqq5hX5T0bVDghd";
		try {
			byte[] cert = Util.fromBase64String(certb64);
			X509Certificate iaikcert = new X509Certificate(cert);

			PublicKey k = iaikcert.getPublicKey();
			RSAPublicKey rsakey = new RSAPublicKey(k.getEncoded()); // TODO geht
																	// das ned
																	// irgendwie
																	// schöner?
			BigInteger mod = rsakey.getModulus();
			private_template.add(new CK_ATTRIBUTE(
					CK_ATTRIBUTE_TYPE.CKA_MODULUS, mod.toByteArray(), mod
							.toByteArray().length));
		} catch (CertificateException | Base64Exception | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// private_template.add(new
		// ATTRIBUTE(ATTRIBUTE_TYPE.PRIVATE_EXPONENT,data));

		PKCS11Object obj = ObjectBuilder
				.createFromTemplate((CK_ATTRIBUTE[]) private_template
						.toArray(new CK_ATTRIBUTE[private_template.size()]));
		obj.setTag(key);
		return obj;
	}
}
