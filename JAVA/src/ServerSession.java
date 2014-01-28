import gui.DataVaultSingleton;
import gui.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKeyIdentifier;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SCryptoParams;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SPayloadCryptoOperationRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SPayloadWithLoadResponse;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import proxys.ATTRIBUTE_TYPE;
import proxys.CERT_TYPE;
import proxys.CK_ATTRIBUTE;
import proxys.CK_MECHANISM;
import proxys.CK_ULONG_ARRAY;
import proxys.CK_ULONG_JPTR;
import proxys.OBJECT_CLASS;
import proxys.RETURN_TYPE;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/*
 * Stellt verbindung zum Server dar ist für kommunikation zuständig
 * ist für authentifizierung über Authenticator Plugins zuständig
 * könnte caching übernehmen
 * */
public class ServerSession {

	private String sessionID;
	private Server.ServerInfo server;
	protected RestTemplate restTemplate;

	private SAuthInfo credentials;
	private boolean rememberCredentialsForSession = true;

	private ObjectStorage keyStorage = new ObjectStorage();

	public ServerSession(Server.ServerInfo s) {
		server = s;
	}

	public Server.ServerInfo getInfo() {
		return server;
	}

	public List<SKey> getKeyList() {
		return null;
	}

	public byte[] sign(byte[] pData, long hKey) throws IOException, PKCS11Error {
		byte[] cData = null;
		String b64Data = null;

		SKey key = (SKey) keyStorage.getObjectById(hKey);

		if (key == null) {
			throw new PKCS11Error(RETURN_TYPE.KEY_HANDLE_INVALID);
		}

		b64Data = new BASE64Encoder().encode(pData);
		b64Data = doCryptoCommand("sign",
				SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_1.getAlgorithmName(),
				b64Data, key.getId(), key.getSubId()); // FIXME: right
														// algorithm?
		cData = new BASE64Decoder().decodeBuffer(b64Data);

		return cData;
	}

	public void verify() {

	}

	public void encrypt() {

	}

	/**
	 * wraps (i.e., encrypts) a private or secret
	 * 
	 * @param pMechanism
	 *            wrapping mechanism
	 * @param hWrappingKey
	 *            handle of the wrapping-key
	 * @param hKey
	 *            handle of the key to be wrapped
	 * @return returns a byte-Array containing the wraped key or null in case of
	 *         fire ;)
	 */

	public byte[] wrapKey(CK_MECHANISM pMechanism, long wrappingKey, long hKey)
			throws IOException {
		// TODO: do something with the mechanism

		SKey keyToWrap = (SKey) keyStorage.getObjectById(hKey);
		SKey wrapKey = (SKey) keyStorage.getObjectById(wrappingKey);

		String data = new BASE64Encoder().encode(keyToWrap.getRepresentation()
				.getBytes());

		data = doCryptoCommand("encrypt",
				SkyTrustAlgorithm.RSAES_RAW.getAlgorithmName(), data,
				wrapKey.getId(), wrapKey.getSubId());

		return new BASE64Decoder().decodeBuffer(data);

	}

	public long unwrapKey(CK_MECHANISM pMechanism, long hUnwrappingKey,
			byte[] pWrappedKey, long ulWrappedKeyLen, CK_ATTRIBUTE[] pTemplate,
			long ulAttributeCount, CK_ULONG_JPTR phKey) {

		SKey unWrappingKey = (SKey) keyStorage.getObjectById(hUnwrappingKey);

		String data = new BASE64Encoder().encode(pWrappedKey);

		data = doCryptoCommand("decrypt",
				SkyTrustAlgorithm.RSAES_RAW.getAlgorithmName(), data,
				unWrappingKey.getId(), unWrappingKey.getSubId());

		SKey key = new SKey();

		return 0L;
	}
	
	
	
	public SResponse handleAuth(SResponse skyTrustResponse){
		//get possible authType(s) 
		SPayloadAuthResponse authResponse = (SPayloadAuthResponse)skyTrustResponse.getPayload();
        SAuthType authType = authResponse.getAuthType();
        //ask User for Credentials
        SAuthInfo credentials = DataVaultSingleton.getInstance().askForAuthInfo(authType,server);
        //build authRequest
        SRequest authRequest = createBasicRequest();
        SPayloadAuthRequest authRequestPayload = new SPayloadAuthRequest();
        authRequestPayload.setAuthInfo(credentials);
        authRequestPayload.setCommand("authenticate");
        authRequest.setPayload(authRequestPayload);
        authRequest.getHeader().setCommandId(skyTrustResponse.getHeader().getCommandId());
        //send authRequest and wait for Response
        try{
        	skyTrustResponse = restTemplate.postForObject(server.url,authRequest,SResponse.class);
        }catch(RestClientException e){
        	return null;
        }
        //save new (authenticated) SessionID
        sessionID=skyTrustResponse.getHeader().getSessionId();
        return skyTrustResponse;
	}

	private SRequest createBasicRequest() {
		SRequest request = new SRequest();
		SkyTrustHeader header = new SkyTrustHeader();
		header.setSessionId(sessionID);
		header.setProtocolVersion("0.1");
		request.setHeader(header);
		return request;
	}

	protected String doCryptoCommand(String command, String algorithm,
			String load, String keyId, String keySubId) {
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

		SResponse skyTrustResponse = restTemplate.postForObject(server.url,
				request, SResponse.class);

		SPayloadResponse payloadResponse = skyTrustResponse.getPayload();
		if (payloadResponse instanceof SPayloadAuthResponse) {
			skyTrustResponse = handleAuth(skyTrustResponse);
		}

		if (payloadResponse instanceof SPayloadWithLoadResponse) {
			SPayloadWithLoadResponse payLoadWithLoadResponse = (SPayloadWithLoadResponse) payloadResponse;
			return ((SPayloadWithLoadResponse) payloadResponse).getLoad();
		}
		return null;

	}
	
	protected List<SKey> discoverKeys(String representation) {
        SPayloadDiscoverKeysRequest payload = new SPayloadDiscoverKeysRequest();
        payload.setRepresentation(representation);

        SRequest request = createBasicRequest();
        request.setPayload(payload);

        SResponse skyTrustResponse = restTemplate.postForObject(server.url,request,SResponse.class);
        SPayloadResponse payloadResponse = skyTrustResponse.getPayload();
        if (payloadResponse instanceof SPayloadAuthResponse) {
            skyTrustResponse = handleAuth(skyTrustResponse);
        }
        payloadResponse = skyTrustResponse. getPayload();

        if (payloadResponse instanceof SPayloadDiscoverKeysResponse) {
            SPayloadDiscoverKeysResponse discoverKeysResponse = (SPayloadDiscoverKeysResponse)payloadResponse;
            List<SKey> keys = discoverKeysResponse.getKey();
            return keys;
        }
        return null;
    }

	public boolean isAutheticated() {
		// TODO Auto-generated method stub
		return true;
	}

	public void findObjects(FindObjectsHelper findObjectsHelper, CK_ULONG_JPTR phObject, long ulMaxObjectCount, CK_ULONG_JPTR pulObjectCount) throws PKCS11Error {
		if (pulObjectCount.getCPtr() == 0L || phObject.getCPtr() == 0L) {
			throw new PKCS11Error(RETURN_TYPE.DEVICE_MEMORY);
		}

		if (ulMaxObjectCount == 0L) {
			pulObjectCount.assign(0L);
			return;
		}

		List<SKey> list = new ArrayList<SKey>();
		CK_ATTRIBUTE[] template = findObjectsHelper.pTemplate;
		for (CK_ATTRIBUTE tmp : template) {

			if(tmp.getType()==ATTRIBUTE_TYPE.CLASS.swigValue()){
				
				short[] array = JAVApkcs11Interface.getByteArrayAsShort(tmp);
				
				if(OBJECT_CLASS.SECRET_KEY.swigValue() == array[array.length-1] ){
					list = discoverKeys("SECRET_KEY");
				}
				if(OBJECT_CLASS.PUBLIC_KEY.swigValue() == array[array.length-1] ){
					list = discoverKeys("PUBLIC_KEY");
				}
				if(OBJECT_CLASS.PRIVATE_KEY.swigValue() == array[array.length-1] ){
					list = discoverKeys("PRIVATE_KEY");
				}
				if(OBJECT_CLASS.CERTIFICATE.swigValue() == array[array.length-1] ){
					list = discoverKeys("certificate");
				}
			}else if(tmp.getType()==ATTRIBUTE_TYPE.KEY_TYPE.swigValue()){
			}else if(tmp.getType()==ATTRIBUTE_TYPE.TOKEN.swigValue()){
			}else if(tmp.getType()==ATTRIBUTE_TYPE.ID.swigValue()){
			}else if(tmp.getType()==ATTRIBUTE_TYPE.VALUE.swigValue()){
			}
		}
			if(list==null){
					pulObjectCount.assign(0L);
					return;
				}
			CK_ULONG_ARRAY ar = new CK_ULONG_ARRAY(phObject.getCPtr(), false);
			pulObjectCount.assign(list.size() > ulMaxObjectCount ? ulMaxObjectCount : list.size());
			for(long i=findObjectsHelper.actualCount; i<pulObjectCount.value(); i++){
				long handle = keyStorage.addNewObject(list.get((int) i));
				ar.setitem((int) i, handle);
			}
			findObjectsHelper.actualCount=pulObjectCount.value();
			

	}

}
