package pkcs11;

import iaik.asn1.structures.AlgorithmID;
import iaik.pkcs.pkcs1.RSASSAPkcs1v15ParameterSpec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;

import objects.MKey;

import org.springframework.web.client.RestTemplate;

import at.iaik.skytrust.SkyTrustAPIFactory;
import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.common.SkyTrustException;
import at.iaik.skytrust.element.receiver.skytrust.SkyTrustAPI;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import configuration.L;
import configuration.Server;

/**
 * 
 * is the connection to the Skytrust server, does the communication and should
 * be the starting point for authentication.
 * 
 * 
 * */
public class ServerSession implements IServerSession {

	// private String sessionID;
	private Server.ServerInfo server;
	protected RestTemplate restTemplate = new RestTemplate();

	private SkyTrustAPI api = null;

	public ServerSession(Server.ServerInfo s) {
		server = s;
		L.log("ServerSession.java: using server: " + s.getUrl(), 1);
		SkyTrustAPIFactory.initialize(s.getUrl());
		api = SkyTrustAPI.getInstance();
	}

	public Server.ServerInfo getInfo() {
		return server;
	}

	@Override
	public List<MKey> getKeyList() {

		try {
			List<MKey> mKeys = new ArrayList<>();
			List<SKey> keys = api.discoverKeys("certificate");

			for (SKey k : keys) {
				mKeys.add(MKey.fromSKey(k));
			}

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
			signedData = api.signHashRequest(mech.getAlgorithmName(), list,
					key.getSKey());
			return signedData.get(0);
		} catch (SkyTrustException e) {
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
		case CMS_AES_128_CBC:
			break;
		case CMS_AES_128_CCM:
			break;
		case CMS_AES_128_GCM:
			break;
		case CMS_AES_192_CBC:
			break;
		case CMS_AES_192_CCM:
			break;
		case CMS_AES_192_GCM:
			break;
		case CMS_AES_256_CBC:
			break;
		case CMS_AES_256_CCM:
			break;
		case CMS_AES_256_GCM:
			break;
		case SMIME_AES_128:
			break;
		case SMIME_AES_192:
			break;
		case SMIME_AES_256:
			break;
		case SMIME_DECRYPT:
			break;
		default:
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
		case CMS_AES_128_CBC:
			break;
		case CMS_AES_128_CCM:
			break;
		case CMS_AES_128_GCM:
			break;
		case CMS_AES_192_CBC:
			break;
		case CMS_AES_192_CCM:
			break;
		case CMS_AES_192_GCM:
			break;
		case CMS_AES_256_CBC:
			break;
		case CMS_AES_256_CCM:
			break;
		case CMS_AES_256_GCM:
			break;
		case SMIME_AES_128:
			break;
		case SMIME_AES_192:
			break;
		case SMIME_AES_256:
			break;
		case SMIME_DECRYPT:
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public boolean verify(byte[] data, byte[] signature, MKey key,
			SkyTrustAlgorithm mech) {
		// TODO: implement this!
		return false;
	}

	@Override
	public byte[] encrypt(byte[] plaindata, MKey key, SkyTrustAlgorithm mech)
			throws PKCS11Error {

		ArrayList<byte[]> list = new ArrayList<>();
		list.add(plaindata);
		ArrayList<SKey> keyList = new ArrayList<>();
		keyList.add(key.getSKey());
		List<List<byte[]>> cipher = null;
		try {
			cipher = api.encryptDataRequest(mech.getAlgorithmName(), list,
					keyList);
		} catch (SkyTrustException e) {
			e.printStackTrace();
		}

		return cipher.get(0).get(0);
	}

	@Override
	public byte[] decrypt(byte[] encdata, MKey key, SkyTrustAlgorithm mech)
			throws PKCS11Error {

		ArrayList<byte[]> list = new ArrayList<>();
		list.add(encdata);
		List<byte[]> plain = null;
		try {
			plain = api.decryptDataRequest(mech.getAlgorithmName(), list,
					key.getSKey());
		} catch (SkyTrustException e) {
			e.printStackTrace();
		}

		return plain.get(0);
	}

	@Override
	public boolean isAutheticated() {
		return false;
	}
}
