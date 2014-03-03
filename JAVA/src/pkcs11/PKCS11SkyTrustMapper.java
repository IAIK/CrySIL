package pkcs11;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.x500.X500Principal;


import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKeyCertificate;
import objects.ATTRIBUTE;
import objects.MECHANISM;
import objects.ObjectBuilder;
import objects.PKCS11Object;
import proxys.ATTRIBUTE_TYPE;
import proxys.CERT_TYPE;
import proxys.KEY_TYP;
import proxys.MECHANISM_TYPES;
import proxys.OBJECT_CLASS;
import proxys.RETURN_TYPE;
import iaik.x509.X509Certificate;
import iaik.security.rsa.RSAPublicKey;
import iaik.utils.Base64Exception;
import iaik.utils.Util;

public class PKCS11SkyTrustMapper {
	
	private static HashMap<MECHANISM_TYPES,SkyTrustAlgorithm> mechanism_map = new HashMap<>();
	private static ArrayList<ATTRIBUTE> skytrust_template;
	static{
		skytrust_template = new ArrayList<>();
		try {
			skytrust_template.add( new ATTRIBUTE(ATTRIBUTE_TYPE.TOKEN,true));
			skytrust_template.add( new ATTRIBUTE(ATTRIBUTE_TYPE.MODIFIABLE,false));
			skytrust_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.KEY_TYPE,KEY_TYP.RSA_KEY));
			skytrust_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.LOCAL,false));
			skytrust_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.DERIVE,false));
		} catch (PKCS11Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static{	
		mechanism_map.put(MECHANISM_TYPES.RSA_PKCS,SkyTrustAlgorithm.RSAES_PKCS1_V1_5);
		mechanism_map.put(MECHANISM_TYPES.RSA_PKCS_PSS,SkyTrustAlgorithm.RSA_PSS);
		mechanism_map.put(MECHANISM_TYPES.RSA_PKCS_OAEP,SkyTrustAlgorithm.RSA_OAEP);
		mechanism_map.put(MECHANISM_TYPES.SHA1_RSA_PKCS,SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_1);
		mechanism_map.put(MECHANISM_TYPES.SHA256_RSA_PKCS,SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_256);
		mechanism_map.put(MECHANISM_TYPES.SHA512_RSA_PKCS,SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_512);
		mechanism_map.put(MECHANISM_TYPES.SHA224_RSA_PKCS,SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_224);
	}
	
	public static SkyTrustAlgorithm mapMechanism(MECHANISM mech) throws PKCS11Error{
		SkyTrustAlgorithm algo = mechanism_map.get(mech.getType());
		if(algo == null){
			throw new PKCS11Error(RETURN_TYPE.MECHANISM_INVALID);
		}
		return algo;
	}
	public static SKey mapKey(PKCS11Object key) throws PKCS11Error{
		if(key == null ){
			throw new PKCS11Error(RETURN_TYPE.KEY_HANDLE_INVALID);
		}
	    //key is not a keyObj 
		OBJECT_CLASS objtype = key.getAttribute(ATTRIBUTE_TYPE.CLASS).copyToSwigEnum(OBJECT_CLASS.class);
		if(objtype != OBJECT_CLASS.CERTIFICATE && objtype != OBJECT_CLASS.PRIVATE_KEY){
			throw new PKCS11Error(RETURN_TYPE.KEY_HANDLE_INVALID);
		}
		//key is not a SkytrustKey
		Object skykey = key.getTag();
		if(skykey == null || !(skykey instanceof SKey)) {
			throw new PKCS11Error(RETURN_TYPE.KEY_HANDLE_INVALID);
		}
		return (SKey) skykey;
	}

	public static PKCS11Object mapToCert(SKey key) throws PKCS11Error{
		if(key == null){
			return null;
		}
		
		if(!key.getRepresentation().equals("certificate")){
			return null;
		}
		ArrayList<ATTRIBUTE> cert_template = new ArrayList<>(skytrust_template);
		cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.ID,key.getId().getBytes()));
		cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.LABEL,"skytrust"));
		cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.CLASS,OBJECT_CLASS.CERTIFICATE));
		cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.CERTIFICATE_TYPE,CERT_TYPE.X_509));

		cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.PRIVATE,false));
		cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.EXTRACTABLE,true));
		cert_template.add( new ATTRIBUTE(ATTRIBUTE_TYPE.SENSITIVE,false));
		cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.VERIFY,true));
		cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.ENCRYPT,true));
		cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.WRAP,false));
		
		byte[] data = new byte[10];
		cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.MODULUS,data));		
		cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.MODULUS_BITS,10));
		cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.PUBLIC_EXPONENT,data));

		
		String certb64 = ((SKeyCertificate) key).getEncodedCertificate();
		
		try {
			byte[] cert = Util.fromBase64String(certb64);
			cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.VALUE,cert));
		
			X509Certificate iaikcert = new X509Certificate(cert);
			X500Principal subject = iaikcert.getSubjectX500Principal();
			X500Principal issuer = iaikcert.getIssuerX500Principal();			
			cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.ISSUER,issuer.getEncoded()));
			cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.SUBJECT,subject.getEncoded()));
			cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.SERIAL_NUMBER,iaikcert.getSerialNumber().toByteArray()));
		
		} catch (CertificateException | Base64Exception e ) {
			cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.ISSUER,"ISSUER".getBytes()));
			cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.SERIAL_NUMBER,"SERIAL_NUMBER".getBytes()));
			cert_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.SUBJECT,"SUBJECT".getBytes()));
			e.printStackTrace();
		}
		
		
		
		PKCS11Object obj = ObjectBuilder.createFromTemplate(cert_template);
		obj.setTag(key);
		return obj;
	}
	public static PKCS11Object mapToPub(SKey key) throws PKCS11Error{
		if(key == null){
			return null;
		}
		
		if(!key.getRepresentation().equals("certificate")){
			return null;
		}
		ArrayList<ATTRIBUTE> pub_template = new ArrayList<>(skytrust_template);
		pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.ID,key.getId().getBytes()));
		pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.LABEL,"skytrust"));
		pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.CLASS,OBJECT_CLASS.PUBLIC_KEY));		
		
		pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.PRIVATE,false));
		pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.EXTRACTABLE,true));
		pub_template.add( new ATTRIBUTE(ATTRIBUTE_TYPE.SENSITIVE,false));
		pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.VERIFY,true));
		pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.ENCRYPT,true));
		pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.WRAP,false));
				
		String certb64 = ((SKeyCertificate) key).getEncodedCertificate();
		
		try {
			byte[] cert = Util.fromBase64String(certb64);		
			X509Certificate iaikcert = new X509Certificate(cert);

			PublicKey k = iaikcert.getPublicKey();
			RSAPublicKey rsakey = new RSAPublicKey(k.getEncoded());  //TODO geht das ned irgendwie schöner?
			pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.VALUE,k.getEncoded()));

			BigInteger exp = rsakey.getPublicExponent();
			BigInteger mod = rsakey.getModulus();
			if(mod == null || exp == null){
				return null;
			}
			pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.MODULUS,mod.toByteArray()));
			pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.MODULUS_BITS,mod.bitLength()));
			pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.PUBLIC_EXPONENT,exp.toByteArray()));

			X500Principal subject = iaikcert.getSubjectX500Principal();
			pub_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.SUBJECT,subject.getEncoded()));
			
		} catch (CertificateException | Base64Exception | InvalidKeyException e ) {
			e.printStackTrace();
			return null;
		}
		
		PKCS11Object obj = ObjectBuilder.createFromTemplate(pub_template);
		obj.setTag(key);
		return obj;
	}
	public static PKCS11Object mapToPrivate(SKey key) throws PKCS11Error{
		ArrayList<ATTRIBUTE> private_template = new ArrayList<>(skytrust_template);
		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.LABEL,"skytrust"));

		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.CLASS,OBJECT_CLASS.PRIVATE_KEY));
		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.PRIVATE,true));
		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.SENSITIVE,true));
		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.ID,key.getId().getBytes()));
		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.ALWAYS_AUTHENTICATE,false));
		
		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.NEVER_EXTRACTABLE,true));
		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.EXTRACTABLE,false));
		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.SIGN,true));
		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.DECRYPT,true));
		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.UNWRAP,false));
		byte[] data = new byte[10];
		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.MODULUS,data));
		private_template.add(new ATTRIBUTE(ATTRIBUTE_TYPE.PRIVATE_EXPONENT,data));

		PKCS11Object obj = ObjectBuilder.createFromTemplate(private_template);
		obj.setTag(key);
		return obj;
	}
}
