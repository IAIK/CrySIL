package pkcs11;
import gui.Server;
import org.apache.commons.codec.binary.Base64;
import iaik.asn1.structures.AlgorithmID;
import iaik.pkcs.pkcs1.RSASSAPkcs1v15ParameterSpec;
import iaik.utils.Base64Exception;
import iaik.utils.Util;
import iaik.x509.X509Certificate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;

import org.springframework.web.client.RestTemplate;

import at.iaik.skytrust.SkyTrustAPIFactory;
import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.element.receiver.skytrust.SkyTrustAPI;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKeyCertificate;

/*
 * Stellt verbindung zum Server dar ist für kommunikation zuständig
 * ist für authentifizierung über Authenticator Plugins zuständig
 * */
public class ServerSession implements IServerSession {

	private String sessionID;
	private Server.ServerInfo server;
	protected RestTemplate restTemplate = new RestTemplate();

	private SAuthInfo credentials = null;
	private boolean rememberCredentialsForSession = true;
	private SkyTrustAPI api = null;

	public ServerSession(Server.ServerInfo s) {
		server = s;
		SkyTrustAPIFactory.initialize(s.getUrl());

        this.api = SkyTrustAPIFactory.getSkyTrustAPI();
	}

	public Server.ServerInfo getInfo() {
		return server;
	}
	@Override
	public List<SKey> getKeyList() throws PKCS11Error{
		return api.discoverKeys("certificate");
	}

	@Override
	public byte[] sign(byte[] pData, SKey key, SkyTrustAlgorithm mech) throws PKCS11Error {
		key.setId(key.getId().substring(0, key.getId().length()-1));
		String data = new String(Base64.encodeBase64(pData));
		String signedData = api.doCryptoCommand("sign", mech.getAlgorithmName(), data, key.getId(), key.getSubId());
		return Base64.decodeBase64(signedData);
	}
	
	public String mapSkytrustToJCE(SkyTrustAlgorithm m){
	    switch(m){
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
	public AlgorithmParameterSpec mapSkytrustToJCEPara(SkyTrustAlgorithm m){
	    switch(m){
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
	    	return null; //new RSAPssParameterSpec();
	    }
		return null;
	}
	@Override
	public boolean verify(byte[] data,byte[] signature, SKey key,SkyTrustAlgorithm mech) {
		if(!key.getRepresentation().equals("certificate")){
			return false;
		}
		SKeyCertificate cert = (SKeyCertificate) key;
		String certb64 = cert.getEncodedCertificate();
		try {
			byte[] enc_cert = Util.fromBase64String(certb64);
			X509Certificate iaikcert = new X509Certificate(enc_cert);
			PublicKey pubkey = iaikcert.getPublicKey();
			//http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Signature
            //TODO set algo and params			
			Signature rsaSignatureEngine = Signature.getInstance(mapSkytrustToJCE(mech));
            rsaSignatureEngine.initVerify(pubkey);
            rsaSignatureEngine.update(data);
            return rsaSignatureEngine.verify(signature);

		} catch (Base64Exception | CertificateException e) {
			System.err.println("error in certificate decoding");
			e.printStackTrace();
		}catch(NoSuchAlgorithmException | InvalidKeyException e){
			System.err.println("error no such algo or key doesn't fit (mapping error?)");
			e.printStackTrace();
		}catch(SignatureException e){
			System.err.println("error in performing verify locally");
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public byte[] encrypt(byte[] plaindata, SKey key, SkyTrustAlgorithm mech) throws PKCS11Error {

		key.setId(key.getId().substring(0, key.getId().length()-1));
		String data = new String(Base64.encodeBase64(plaindata));
		String encdata = api.doCryptoCommand("encrypt", mech.getAlgorithmName(), data, key.getId(), key.getSubId());
		return Base64.decodeBase64(encdata);
	}

	@Override
	public byte[] decrypt(byte[] encdata, SKey key, SkyTrustAlgorithm mech)
			throws PKCS11Error {

		String data = new String(Base64.encodeBase64(encdata));
		System.out.println("  encdata: "+ data);
		String plaindata = api.doCryptoCommand("decrypt", mech.getAlgorithmName(), data, key.getId().substring(0, key.getId().length()-1), key.getSubId());
		System.out.println("plaindata: "+ plaindata);
		return Base64.decodeBase64(plaindata);
	}

	@Override
	public boolean isAutheticated() {
		// TODO Auto-generated method stub
		return false;
	}
}
