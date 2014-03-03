package pkcs11;
import gui.DataVaultSingleton;
import gui.Server;

import iaik.asn1.structures.AlgorithmID;
import iaik.pkcs.pkcs1.RSASSAPkcs1v15ParameterSpec;
import iaik.utils.Base64Exception;
import iaik.utils.Util;
import iaik.x509.X509Certificate;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;


import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.header.SkyTrustHeader;
import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthType;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKeyCertificate;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKeyIdentifier;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SCryptoParams;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SPayloadCryptoOperationRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SPayloadWithLoadResponse;


import proxys.RETURN_TYPE;

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


	public ServerSession(Server.ServerInfo s) {
		server = s;
	}

	public Server.ServerInfo getInfo() {
		return server;
	}
	@Override
	public List<SKey> getKeyList() throws PKCS11Error{
		return discoverKeys("certificate");
	}
	@Override
	public byte[] sign(byte[] pData, SKey key, SkyTrustAlgorithm mech) throws PKCS11Error {
		pData = doCryptoCommand("sign", mech.getAlgorithmName(),
				pData, key.getId(), key.getSubId());
		return pData;
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
		byte[] encdata = doCryptoCommand("encrypt", mech.getAlgorithmName(),
				plaindata, key.getId(), key.getSubId());
		return encdata;
	}

	@Override
	public byte[] decrypt(byte[] encdata, SKey key, SkyTrustAlgorithm mech)
			throws PKCS11Error {
		byte[] plaindata = doCryptoCommand("encrypt", mech.getAlgorithmName(),
				encdata, key.getId(), key.getSubId());
		return plaindata;
	}
	public boolean isAutheticated() {
		// TODO Auto-generated method stub
		return true;
	}
	private SRequest createBasicRequest() {
		SRequest request = new SRequest();
		SkyTrustHeader header = new SkyTrustHeader();
		header.setSessionId(sessionID);
		header.setProtocolVersion("0.1");
		request.setHeader(header);
		return request;
	}
	private SResponse handleAuth(SResponse skyTrustResponse) throws PKCS11Error{
		SPayloadAuthResponse authResponse = (SPayloadAuthResponse)skyTrustResponse.getPayload();
        SAuthType authType = authResponse.getAuthTypes().get(0);
        //ask User for Credentials
        if(credentials == null){
        	credentials = DataVaultSingleton.getInstance().askForAuthInfo(authType,server);
        }
        //build authRequest
        SRequest authRequest = createBasicRequest();
        SPayloadAuthRequest authRequestPayload = new SPayloadAuthRequest();
        authRequestPayload.setAuthInfo(credentials);
        authRequestPayload.setCommand("authenticate");
        authRequest.setPayload(authRequestPayload);
        authRequest.getHeader().setCommandId(skyTrustResponse.getHeader().getCommandId());
        //send authRequest and wait for Response
        try{
        	skyTrustResponse = restTemplate.postForObject(server.getUrl(),authRequest,SResponse.class);
        }catch(ResourceAccessException e){
        	throw new PKCS11Error(RETURN_TYPE.DEVICE_ERROR);
        }catch(RestClientException e){
        	throw new PKCS11Error(RETURN_TYPE.DEVICE_REMOVED);
        }
        //TODO wie erkennen wir ob Auth erfolgreich war
        if(!rememberCredentialsForSession){
        	credentials = null;
        }
        //save new (authenticated) SessionID
        sessionID=skyTrustResponse.getHeader().getSessionId();
        return skyTrustResponse;
	}


	private List<SKey> discoverKeys(String representation) throws PKCS11Error {
        SPayloadDiscoverKeysRequest payload = new SPayloadDiscoverKeysRequest();
        payload.setRepresentation(representation);

        SRequest request = createBasicRequest();
        request.setPayload(payload);

        SResponse skyTrustResponse = restTemplate.postForObject(server.getUrl(),request,SResponse.class);
        SPayloadResponse payloadResponse = skyTrustResponse.getPayload();

        if (payloadResponse instanceof SPayloadAuthResponse) {
            skyTrustResponse = handleAuth(skyTrustResponse);
            payloadResponse = skyTrustResponse.getPayload();
        }
        if (payloadResponse instanceof SPayloadDiscoverKeysResponse) {
            SPayloadDiscoverKeysResponse discoverKeysResponse = (SPayloadDiscoverKeysResponse)payloadResponse;
            List<SKey> keys = discoverKeysResponse.getKey();
            return keys;
        }
        throw new PKCS11Error(RETURN_TYPE.DEVICE_ERROR);
    }
	private byte[] doCryptoCommand(String command, String algorithm,
			byte[] data, String keyId, String keySubId) throws PKCS11Error {
		
		String load = Util.toBase64String(data);
		
		SRequest request = createBasicRequest();
		SPayloadCryptoOperationRequest payload = new SPayloadCryptoOperationRequest();

		SCryptoParams params = new SCryptoParams();

		SKey keyHandle = new SKeyIdentifier();
		keyHandle.setId(keyId);
		keyHandle.setSubId(keySubId);
		params.setKey(keyHandle);
		params.setAlgorithm(algorithm);
		payload.setCryptoParams(params);
		payload.setCommand(command);
		payload.setLoad(load);
		request.setPayload(payload);

		SResponse skyTrustResponse = restTemplate.postForObject(server.getUrl(),
				request, SResponse.class);

		SPayloadResponse payloadResponse = skyTrustResponse.getPayload();
		if (payloadResponse instanceof SPayloadAuthResponse) {
			skyTrustResponse = handleAuth(skyTrustResponse);
			payloadResponse = skyTrustResponse.getPayload();
		}

		if (payloadResponse instanceof SPayloadWithLoadResponse) {
			SPayloadWithLoadResponse payLoadWithLoadResponse = (SPayloadWithLoadResponse) payloadResponse;
			String resp_b64Data = payLoadWithLoadResponse.getLoad();
			
			try {
				return Util.fromBase64String(resp_b64Data);
			} catch (IOException e) {
				throw new PKCS11Error(RETURN_TYPE.DEVICE_ERROR);
			}
		}
		throw new PKCS11Error(RETURN_TYPE.DEVICE_ERROR);
	}
}
