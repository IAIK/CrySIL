package pkcs11;

import at.iaik.skytrust.common.SkyTrustAlgorithm;
import iaik.security.rsa.RSAPublicKey;
import iaik.utils.Base64Exception;
import iaik.utils.Util;
import iaik.x509.X509Certificate;
import obj.*;
import objects.MKey;
import objects.ObjectBuilder;
import objects.PKCS11Object;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;

public class PKCS11SkyTrustMapper {

	private static HashMap<Long, SkyTrustAlgorithm> mechanism_map = new HashMap<>();
	private static ArrayList<CK_ATTRIBUTE> skytrust_template;
	private static Long architekturkorrekturmanufaktur = 1L;

	static {
		if(System.getProperty("os.arch").compareTo("amd64")==0){
			architekturkorrekturmanufaktur=2L;
		}

		skytrust_template = new ArrayList<>();
		skytrust_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_TOKEN, true, 1));
		skytrust_template.add(new CK_ATTRIBUTE(
				CK_ATTRIBUTE_TYPE.CKA_MODIFIABLE, false, 1));
		skytrust_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_KEY_TYPE,
				CK_KEY_TYPE.CKK_RSA, 4*architekturkorrekturmanufaktur));
		skytrust_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_LOCAL,
				false, 1));
		skytrust_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_DERIVE,
				false, 1));
	}
	static {
		if(System.getProperty("os.arch").compareTo("amd64")==0){
			architekturkorrekturmanufaktur=2L;
			System.out.println("korrectur erforderlich!");
		}
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

	public static MKey mapKey(PKCS11Object key) throws PKCS11Error {
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
		if (skykey == null || !(skykey instanceof MKey)) {
			throw new PKCS11Error(CK_RETURN_TYPE.CKR_KEY_HANDLE_INVALID);
		}
		return (MKey) skykey;
	}

	public static PKCS11Object mapToCert(MKey key) throws PKCS11Error {
		if (key == null) {
			return null;
		}

		if (!key.getType().equals("certificate")) {
			return null;
		}
		ArrayList<CK_ATTRIBUTE> cert_template = new ArrayList<>( skytrust_template);
		byte[] id = key.getId().getBytes();
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ID, id, id.length));
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_LABEL,id, id.length)); // TODO: fix length
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_CLASS, CK_OBJECT_TYPE.CKO_CERTIFICATE, 4*architekturkorrekturmanufaktur));
		cert_template.add(new CK_ATTRIBUTE( CK_ATTRIBUTE_TYPE.CKA_CERTIFICATE_TYPE, CK_CERTIFICATE_TYPE.CKC_X_509, 4*architekturkorrekturmanufaktur));
		cert_template.add(new CK_ATTRIBUTE( CK_ATTRIBUTE_TYPE.CKA_CERTIFICATE_CATEGORY, 1L, 4*architekturkorrekturmanufaktur));
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_TRUSTED, true, 1));

		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_PRIVATE, false, 1));
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_EXTRACTABLE, true, 1));
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SENSITIVE, false, 1));
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_VERIFY, true, 1));
		cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ENCRYPT, true, 1));
		cert_template .add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_WRAP, false, 1));

		// byte[] data = new byte[10];
		// cert_template.add(new
		// CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_MODULUS,data, data.length));
		// cert_template.add(new
		// CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_MODULUS_BITS,10,10));
		// cert_template.add(new
		// CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_PUBLIC_EXPONENT,data,
		// data.length));

		String certb64 = key.getEncodedCertificate();
		try {
			byte[] cert = Util.fromBase64String(certb64);
			cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_VALUE, cert, cert.length));

			X509Certificate iaikcert = new X509Certificate(cert);
			byte[] subject = iaikcert.getSubjectX500Principal().getEncoded();
			byte[] issuer = iaikcert.getIssuerX500Principal().getEncoded();
			byte[] serialNr = iaikcert.getSerialNumber().toByteArray();

			cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ISSUER, issuer, issuer.length));
			cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SUBJECT, subject, subject.length));
			cert_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SERIAL_NUMBER, serialNr, serialNr.length));

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

	public static PKCS11Object mapToPub(MKey key) throws PKCS11Error {
		if (key == null) {
			return null;
		}

		if (!key.getType().equals("certificate")) {
			return null;
		}
		ArrayList<CK_ATTRIBUTE> pub_template = new ArrayList<>(
				skytrust_template);
		byte[] id = key.getId().getBytes();
		
		
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ID, id, id.length));
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_LABEL,id, id.length));
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_CLASS, CK_OBJECT_TYPE.CKO_PUBLIC_KEY, 4*architekturkorrekturmanufaktur));
		
		
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_PRIVATE, false, 1));
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_EXTRACTABLE, true, 1));
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SENSITIVE, false, 1));
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_VERIFY, true, 1));
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ENCRYPT, true, 1));
		pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_WRAP, false, 1));

		String certb64 = key.getEncodedCertificate();
		try {
			byte[] cert = Util.fromBase64String(certb64);
			X509Certificate iaikcert = new X509Certificate(cert);

			PublicKey k = iaikcert.getPublicKey();
			RSAPublicKey rsakey = new RSAPublicKey(k.getEncoded()); // TODO geht
																	// das ned
																	// irgendwie
																	// schöner?
			byte[] kenc = k.getEncoded();
			pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_VALUE, kenc, kenc.length));

			BigInteger exp = rsakey.getPublicExponent();
			byte[] expb =exp.toByteArray();
			BigInteger mod = rsakey.getModulus();
			byte[] modb = mod.toByteArray();
			if (mod == null || exp == null) {
				return null;
			}
			pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_MODULUS, modb, modb.length));
			pub_template.add(new CK_ATTRIBUTE( CK_ATTRIBUTE_TYPE.CKA_MODULUS_BITS, (long) mod.bitLength(), 4*architekturkorrekturmanufaktur));
			pub_template.add(new CK_ATTRIBUTE( CK_ATTRIBUTE_TYPE.CKA_PUBLIC_EXPONENT, expb, expb.length));

			byte[] subject = iaikcert.getSubjectX500Principal().getEncoded();
			pub_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SUBJECT, subject, subject.length));

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

	public static PKCS11Object mapToPrivate(MKey key) throws PKCS11Error {
		ArrayList<CK_ATTRIBUTE> private_template = new ArrayList<>(
				skytrust_template);
		byte[] id = key.getId().getBytes();
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_LABEL,id, id.length));

		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_CLASS,
				CK_OBJECT_TYPE.CKO_PRIVATE_KEY, 4*architekturkorrekturmanufaktur));
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_PRIVATE,
				true, 1));
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SENSITIVE,
				true, 1));
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_ID, id, id.length));
		private_template.add(new CK_ATTRIBUTE( CK_ATTRIBUTE_TYPE.CKA_ALWAYS_AUTHENTICATE, false, 1));

		private_template.add(new CK_ATTRIBUTE( CK_ATTRIBUTE_TYPE.CKA_NEVER_EXTRACTABLE, true, 1));
		private_template.add(new CK_ATTRIBUTE( CK_ATTRIBUTE_TYPE.CKA_EXTRACTABLE, false, 1));
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_SIGN, true, 1));
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_DECRYPT, true, 1));
		private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_UNWRAP, false, 1));

		String certb64 =key.getEncodedCertificate();
		try {
			byte[] cert = Util.fromBase64String(certb64);
			X509Certificate iaikcert = new X509Certificate(cert);

			PublicKey k = iaikcert.getPublicKey();
			RSAPublicKey rsakey = new RSAPublicKey(k.getEncoded()); // TODO geht
																	// das ned
																	// irgendwie
																	// schöner?
			BigInteger mod = rsakey.getModulus();
			byte[] modb = mod.toByteArray();
			private_template.add(new CK_ATTRIBUTE(CK_ATTRIBUTE_TYPE.CKA_MODULUS, modb, modb.length));
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
