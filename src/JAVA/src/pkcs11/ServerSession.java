package pkcs11;

import gui.Server;
import iaik.asn1.structures.AlgorithmID;
import iaik.pkcs.pkcs1.RSASSAPkcs1v15ParameterSpec;
import iaik.utils.Base64Exception;
import iaik.utils.Util;
import iaik.x509.X509Certificate;
import objects.MKey;

import org.springframework.web.client.RestTemplate;

import at.iaik.skytrust.SkyTrustAPIFactory;
import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.common.SkyTrustException;
import at.iaik.skytrust.element.SkytrustElement;
import at.iaik.skytrust.element.receiver.skytrust.SkyTrustAPI;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;

import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;

/*
 * Stellt verbindung zum Server dar ist für kommunikation zuständig
 * ist für authentifizierung über Authenticator Plugins zuständig
 * */
public class ServerSession implements IServerSession {

	private String sessionID;
	private Server.ServerInfo server;
	protected RestTemplate restTemplate = new RestTemplate();

	private SkyTrustAPI api = null;

	public ServerSession(Server.ServerInfo s) {
		server = s;
		System.out.println("url:"+s.getUrl());
		System.out.println("http://skytrust-dev.iaik.tugraz.at/skytrust-server-no-auth-2.0/rest/json ");
		SkyTrustAPIFactory.initialize("http://skytrust-dev.iaik.tugraz.at/skytrust-server-no-auth-2.0/rest/json");
		api = SkyTrustAPI.getInstance();
	}


	public Server.ServerInfo getInfo() {
		return server;
	}

	@Override
	public List<MKey> getKeyList() {
		
		try {
			List<MKey> mKeys = new ArrayList<>();
			List<SKey> keys =api.discoverKeys("certificate");

			for(SKey k : keys){
				mKeys.add(MKey.fromSKey(k));
			}
		
			MKey mkey = MKey.fromBase64String("keyfile", "2QGIYSID96JCYO4SnPruw5KFR/or8bAJyw6KFa+yikLeNcC9AXhnpswU08R/xe1qIjY9mPFqnoHiBU4WVQLd9g==");
			mKeys.add(mkey);
			
			
			return mKeys;
		} catch (SkyTrustException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public byte[] sign(byte[] pData, MKey key, SkyTrustAlgorithm mech)
			throws PKCS11Error {

		ArrayList<byte[]> list = new ArrayList<>();
		list.add(pData);
		List<byte[]> signedData;
		try {
			signedData = api.signHashRequest(mech.getAlgorithmName(), list, key.getSKey());
            return signedData.get(0);
		} catch (SkyTrustException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String mapSkytrustToJCE(SkyTrustAlgorithm m) {
		switch (m) {
		case RSAES_RAW:
			return "";
		case RSAES_PKCS1_V1_5:
			return "";
		case RSA_OAEP:
			return "";
		case RSASSA_PKCS1_V1_5_SHA_1:
			return "SHA1withRSA";
		case RSASSA_PKCS1_V1_5_SHA_224:
			return "SHA224withRSA";
		case RSASSA_PKCS1_V1_5_SHA_256:
			return "SHA256withRSA";
		case RSASSA_PKCS1_V1_5_SHA_512:
			return "SHA512withRSA";
		case RSA_PSS:
			return "";
		}
		return "";
	}

	public AlgorithmParameterSpec mapSkytrustToJCEPara(SkyTrustAlgorithm m) {
		switch (m) {
		case RSAES_RAW:
			return null;
		case RSAES_PKCS1_V1_5:
		case RSA_OAEP:
			return null;
		case RSASSA_PKCS1_V1_5_SHA_1:
			return new RSASSAPkcs1v15ParameterSpec(AlgorithmID.sha1);
		case RSASSA_PKCS1_V1_5_SHA_224:
			return new RSASSAPkcs1v15ParameterSpec(AlgorithmID.sha224);
		case RSASSA_PKCS1_V1_5_SHA_256:
			return new RSASSAPkcs1v15ParameterSpec(AlgorithmID.sha256);
		case RSASSA_PKCS1_V1_5_SHA_512:
			return new RSASSAPkcs1v15ParameterSpec(AlgorithmID.sha512);
		case RSA_PSS:
			return null; // new RSAPssParameterSpec();
		}
		return null;
	}

	@Override
	public boolean verify(byte[] data, byte[] signature, MKey key,
			SkyTrustAlgorithm mech) {
//		SKeyCertificate cert = (SKeyCertificate) key;
//		String certb64 = cert.getEncodedCertificate();
//		try {
//			byte[] enc_cert = Util.fromBase64String(certb64);
//			X509Certificate iaikcert = new X509Certificate(enc_cert);
//			PublicKey pubkey = iaikcert.getPublicKey();
//			// http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Signature
//			// TODO set algo and params
//			Signature rsaSignatureEngine = Signature
//					.getInstance(mapSkytrustToJCE(mech));
//			rsaSignatureEngine.initVerify(pubkey);
//			rsaSignatureEngine.update(data);
//			return rsaSignatureEngine.verify(signature);
//
//		} catch (Base64Exception | CertificateException e) {
//			System.err.println("error in certificate decoding");
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
//			System.err
//					.println("error no such algo or key doesn't fit (mapping error?)");
//			e.printStackTrace();
//		} catch (SignatureException e) {
//			System.err.println("error in performing verify locally");
//			e.printStackTrace();
//		}
		return false;
	}

	@Override
	public byte[] encrypt(byte[] plaindata, MKey key, SkyTrustAlgorithm mech)
			throws PKCS11Error {

		//String data = new String(Base64.encodeBase64(plaindata));
		//String encdata = api.doCryptoCommand("encrypt", mech.getAlgorithmName(), data, key.getId(), key.getSubId());
		ArrayList<byte[]> list = new ArrayList<>();
		list.add(plaindata);
		ArrayList<SKey> keyList = new ArrayList<>();
		keyList.add(key.getSKey());
		List<List<byte[]>> cipher =null;
		try {
			cipher = api.encryptDataRequest(mech.getAlgorithmName(), list,  keyList);
		} catch (SkyTrustException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cipher.get(0).get(0);
	}

	@Override
	public byte[] decrypt(byte[] encdata, MKey key, SkyTrustAlgorithm mech)
			throws PKCS11Error {
		
		ArrayList<byte[]> list = new ArrayList<>();
		list.add(encdata);
		List<byte[]> plain =null;
		try {
			plain = api.decryptDataRequest(mech.getAlgorithmName(), list, key.getSKey());
		} catch (SkyTrustException e) {
			e.printStackTrace();
		}
		
		return plain.get(0);
	}

	@Override
	public boolean isAutheticated() {
		// TODO Auto-generated method stub
		return false;
	}
}
