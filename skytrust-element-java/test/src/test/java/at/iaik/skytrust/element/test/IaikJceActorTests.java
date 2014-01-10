package at.iaik.skytrust.element.test;


import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.element.SkytrustElement;
import at.iaik.skytrust.element.receiver.test.TestReceiver;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.header.SkyTrustHeader;
import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthType;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth.SGoogleOAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth.SGoogleOAuthType;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth.SHandySigOAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth.SHandySigOAuthType;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword.SUserPasswordAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword.SUserPasswordAuthType;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKeyIdentifier;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadGetKeyRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadGetKeyResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SCryptoParams;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SPayloadCryptoOperationRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SPayloadWithLoadResponse;
import iaik.utils.Base64Exception;
import iaik.utils.Util;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.testng.Assert;
import org.testng.annotations.*;
import static org.testng.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * High level tests of skytrust element.
 */
public class IaikJceActorTests {

	/** The ow. */
    protected ObjectWriter ow;

	/** The session id. */
    protected String sessionID="";

	/** The interface to the skytrust world. */
	private TestReceiver receiver;

	/**
	 * Create a fresh {@link SkytrustElement} and retrieve the {@link TestReceiver} before every test.
	 */
	@BeforeTest
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        ow = mapper.writer().withDefaultPrettyPrinter();

		SkytrustElement element = SkytrustElement.create("IaikJceActorTests");
		receiver = (TestReceiver) element.getReceiver(TestReceiver.class.getSimpleName());
    }

	/**
	 * Creates the basic request.
	 * 
	 * @return the s request
	 */
    protected SRequest createBasicRequest() {
        SRequest request = new SRequest();

        SkyTrustHeader header = new SkyTrustHeader();
        header.setSessionId(sessionID);
        header.setProtocolVersion("0.1");
        request.setHeader(header);

        return request;
    }

	/**
	 * Log json.
	 * 
	 * @param request
	 *            the request
	 */
    protected void logJSON(SRequest request) {
        try {
            String requestJson = ow.writeValueAsString(request);
            System.out.println(requestJson);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

	/**
	 * Log json.
	 * 
	 * @param response
	 *            the response
	 */
    protected void logJSON(SResponse response) {
        try {
            String requestJson = ow.writeValueAsString(response);
            System.out.println(requestJson);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

	/**
	 * Encrypt decrypt dataprovider.
	 * 
	 * @return the object[][]
	 */
	@DataProvider(name = "EncryptDecryptDataprovider")
	public Object[][] encryptDecryptDataprovider() {
		return new Object[][] { { SkyTrustAlgorithm.RSAES_RAW }, { SkyTrustAlgorithm.RSAES_PKCS1_V1_5 }, { SkyTrustAlgorithm.RSA_OAEP } };
	}

	/**
	 * Test encrypt decrypt.
	 * 
	 * <p>
	 * depends on a successful {@link Tests#discoverKeys*}!
	 * 
	 * @param algo
	 *            the algo
	 */
	@Test(dataProvider = "EncryptDecryptDataprovider", dependsOnMethods = "testDiscoverKeys")
	public void testEncryptDecrypt(SkyTrustAlgorithm algo) {
		try {
			List<SKey> keys = discoverKeys("handle");
			String plainOrig = "encrypt me";
			String base64Encrypted = doCryptoCommand("encrypt", algo.getAlgorithmName(),
					Util.toBase64String(plainOrig.getBytes()), keys.get(0).getId(), keys.get(0).getSubId());

			assertNotNull(base64Encrypted, "encrypt");
			assertNotEquals(base64Encrypted.length(), 0, "encrypt");

			String base64PlainDecrypt = doCryptoCommand("decrypt", algo.getAlgorithmName(), base64Encrypted, keys.get(0)
					.getId(), keys.get(0).getSubId());

			assertNotNull(base64Encrypted, "decrypt");
			assertNotEquals(base64Encrypted.length(), 0, "decrypt");

			String plainDecrypt = new String(Util.fromBase64String(base64PlainDecrypt));
			assertTrue(plainDecrypt.contains(plainOrig), "decrypt"); // TODO handle block size of RSA...
		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * Do crypto command.
	 * 
	 * @param command
	 *            the command
	 * @param algorithm
	 *            the algorithm
	 * @param load
	 *            the load
	 * @param keyId
	 *            the key id
	 * @param keySubId
	 *            the key sub id
	 * @return the string
	 */
    protected String doCryptoCommand(String command, String algorithm, String load,String keyId,String keySubId) {
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

        logJSON(request);
		SResponse skyTrustResponse = receiver.take(request);
        logJSON(skyTrustResponse);

        SPayloadResponse payloadResponse = skyTrustResponse.getPayload();
        if (payloadResponse instanceof SPayloadAuthResponse) {
            skyTrustResponse = handleAuthentication(skyTrustResponse);
        }

		payloadResponse = skyTrustResponse.getPayload();
        if (payloadResponse instanceof SPayloadWithLoadResponse) {
            return ((SPayloadWithLoadResponse) payloadResponse).getLoad();
        }
        return null;


    }

	/**
	 * Discover keys dataprovider.
	 * 
	 * @return the object[][]
	 */
	@DataProvider(name = "discoverKeysDataprovider")
	public Object[][] discoverKeysDataprovider() {
		return new Object[][] { { "handle" }, { "certificate" } };
	}

	/**
	 * Test discover keys.
	 * 
	 * @param representation
	 *            the representation
	 */
	@Test(dataProvider = "discoverKeysDataprovider")
	public void testDiscoverKeys(String representation) {
		List<SKey> keys = discoverKeys(representation);

		assertNotNull(keys);
		assertFalse(keys.isEmpty());
	}

	/**
	 * Discover keys.
	 * 
	 * @param representation
	 *            the representation
	 * @return the list
	 */
    protected List<SKey> discoverKeys(String representation) {
        SPayloadDiscoverKeysRequest payload = new SPayloadDiscoverKeysRequest();
        payload.setRepresentation(representation);

        SRequest request = createBasicRequest();
        request.setPayload(payload);

        logJSON(request);
		SResponse skyTrustResponse = receiver.take(request);
        logJSON(skyTrustResponse);

        SPayloadResponse payloadResponse = skyTrustResponse.getPayload();
        if (payloadResponse instanceof SPayloadAuthResponse) {
            skyTrustResponse = handleAuthentication(skyTrustResponse);
        }
        payloadResponse = skyTrustResponse. getPayload();

		payloadResponse = skyTrustResponse.getPayload();
		if (payloadResponse instanceof SPayloadAuthResponse) {
			skyTrustResponse = handleAuthentication(skyTrustResponse);
		}
		payloadResponse = skyTrustResponse.getPayload();

        if (payloadResponse instanceof SPayloadDiscoverKeysResponse) {
            SPayloadDiscoverKeysResponse discoverKeysResponse = (SPayloadDiscoverKeysResponse)payloadResponse;
            List<SKey> keys = discoverKeysResponse.getKey();
            return keys;
        }
        return null;
    }

	/**
	 * Test get key.
	 */
	@Test(dependsOnMethods = "testDiscoverKeys")
	public void testGetKey() {
		List<SKey> keys = discoverKeys("handle");

		SKey key = getKey("certificate", keys.get(0).getId(), keys.get(0).getSubId());

		assertNotNull(key);
		assertEquals(key.getId(), keys.get(0).getId());
		assertEquals(key.getSubId(), keys.get(0).getSubId());
		assertEquals(key.getRepresentation(), "certificate");
	}

	/**
	 * Gets the key.
	 * 
	 * @param representation
	 *            the representation
	 * @param keyId
	 *            the key id
	 * @param subKeyId
	 *            the sub key id
	 * @return the key
	 */
    protected SKey getKey(String representation,String keyId,String subKeyId) {
        SPayloadGetKeyRequest payload = new SPayloadGetKeyRequest();
        payload.setRepresentation(representation);
        SKey key = new SKeyIdentifier();
        key.setSubId(subKeyId);
        key.setId(keyId);
        payload.setKey(key);

        SRequest request = createBasicRequest();
        request.setPayload(payload);

        logJSON(request);
		SResponse skyTrustResponse = receiver.take(request);
        logJSON(skyTrustResponse);

        SPayloadResponse payloadResponse = skyTrustResponse.getPayload();
        if (payloadResponse instanceof SPayloadAuthResponse) {
            skyTrustResponse = handleAuthentication(skyTrustResponse);
        }
        payloadResponse = skyTrustResponse. getPayload();

        if (payloadResponse instanceof SPayloadAuthResponse) {
            skyTrustResponse = handleAuthentication(skyTrustResponse);
        }

        payloadResponse = skyTrustResponse. getPayload();

        if (payloadResponse instanceof SPayloadGetKeyResponse) {
            SPayloadGetKeyResponse getKeyResponse = (SPayloadGetKeyResponse)payloadResponse;
            SKey returnedKey = getKeyResponse.getKey();
            return returnedKey;
        }
        return null;
    }

	/**
	 * Handle authentication.
	 * 
	 * @param skyTrustResponse
	 *            the sky trust response
	 * @return the s response
	 */
    protected SResponse handleAuthentication(SResponse skyTrustResponse) {
        SPayloadAuthResponse authResponse = (SPayloadAuthResponse)skyTrustResponse.getPayload();
        SAuthType authType = authResponse.getAuthType();
        if (authType instanceof SUserPasswordAuthType) {
            SRequest authRequest = createBasicRequest();
            SUserPasswordAuthInfo authInfo = new SUserPasswordAuthInfo();
			// authInfo.setPassWord("password");
			authInfo.setUserName("Peter-Franz-Teufl");
			// authInfo.setUserName("falsch");
            SPayloadAuthRequest authRequestPayload = new SPayloadAuthRequest();
            authRequestPayload.setAuthInfo(authInfo);
            authRequestPayload.setCommand("authenticate");
            authRequest.setPayload(authRequestPayload);
            authRequest.getHeader().setCommandId(skyTrustResponse.getHeader().getCommandId());

            logJSON(authRequest);
			skyTrustResponse = receiver.take(authRequest);
            logJSON(skyTrustResponse);
            sessionID=skyTrustResponse.getHeader().getSessionId();
            return  skyTrustResponse;

        } else if (authType instanceof SGoogleOAuthType) {
            SRequest authRequest = createBasicRequest();
            SGoogleOAuthInfo authInfo = new SGoogleOAuthInfo();
            authInfo.setAccessToken("ya29.AHES6ZTm2ywtmsiTyjZgtnfbRfsmjxNRCGotBzxy_Reh-HVYRuu2");
            SPayloadAuthRequest authRequestPayload = new SPayloadAuthRequest();
            authRequestPayload.setAuthInfo(authInfo);
            authRequestPayload.setCommand("authenticate");
            authRequest.setPayload(authRequestPayload);
            authRequest.getHeader().setCommandId(skyTrustResponse.getHeader().getCommandId());

            logJSON(authRequest);
			skyTrustResponse = receiver.take(authRequest);
            logJSON(skyTrustResponse);
            sessionID=skyTrustResponse.getHeader().getSessionId();
            return  skyTrustResponse;
        } else if (authType instanceof SHandySigOAuthType) {
            SRequest authRequest = createBasicRequest();
            SHandySigOAuthInfo authInfo = new SHandySigOAuthInfo();
            authInfo.setAccessToken("433b67a5d1");
            SPayloadAuthRequest authRequestPayload = new SPayloadAuthRequest();
            authRequestPayload.setAuthInfo(authInfo);
            authRequestPayload.setCommand("authenticate");
            authRequest.setPayload(authRequestPayload);
            authRequest.getHeader().setCommandId(skyTrustResponse.getHeader().getCommandId());

            logJSON(authRequest);
			skyTrustResponse = receiver.take(authRequest);
            logJSON(skyTrustResponse);
            sessionID=skyTrustResponse.getHeader().getSessionId();
            return  skyTrustResponse;


        }
        return null;
    }


	/**
	 * Test discover keys.
	 */
	@Test(dependsOnMethods = { "testDiscoverKeys", "testEncryptDecrypt" })
    public void testBigTest() {
        List<SKey> keys = discoverKeys("certificate");
		keys = discoverKeys("handle");

		String plainOrig = "encrypt me";
		String base64Encrypted = doCryptoCommand("encrypt", SkyTrustAlgorithm.RSAES_RAW.getAlgorithmName(),
				Util.toBase64String(plainOrig.getBytes()), keys.get(0).getId(), keys.get(0).getSubId());
		String base64PlainDecrypt = doCryptoCommand("decrypt", SkyTrustAlgorithm.RSAES_RAW.getAlgorithmName(), base64Encrypted, keys.get(0).getId(),
				keys.get(0).getSubId());
		try {
			String plainDecrypt = new String(Util.fromBase64String(base64PlainDecrypt));
			assert (plainDecrypt.contains(plainOrig)); // TODO handle block size of RSA...
		} catch (Base64Exception e) {
			e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
		}
		//
		//
		//
//        plainOrig = "encrypt me";
		// base64Encrypted = doCryptoCommand("encrypt", SkyTrustAlgorithm.RSAES_PKCS1_V1_5.getAlgorithmName(),
		// Util.toBase64String(plainOrig.getBytes()),keys.get(0).getId(),keys.get(0).getSubId());
		// base64PlainDecrypt = doCryptoCommand("decrypt", SkyTrustAlgorithm.RSAES_PKCS1_V1_5.getAlgorithmName(),
		// base64Encrypted,keys.get(0).getId(),keys.get(0).getSubId());
		// try {
		// String plainDecrypt = new String(Util.fromBase64String(base64PlainDecrypt));
		// assert(plainOrig.equals(plainDecrypt));
		// } catch (Base64Exception e) {
		// e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
		// }
		//
		//
		//
		//
		//
		// //assert(plainOrig.equals(plainDecrypt));
		//
		// // plainOrig = "encrypt me";
		// // base64Encrypted = doCryptoCommand("encrypt", SkyTrustAlgorithm.RSA_OAEP.getAlgorithmName(),
		// Util.toBase64String("encrypt me".getBytes()),keys.get(0).getId(),keys.get(0).getSubId());
		// // plainDecrypt = doCryptoCommand("decrypt", SkyTrustAlgorithm.RSA_OAEP.getAlgorithmName(),
		// base64Encrypted,keys.get(0).getId(),keys.get(0).getSubId());
		// // assert(plainOrig.equals(plainDecrypt));
		//
		//
		// doCryptoCommand("sign", SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_1.getAlgorithmName(),
		// Util.toBase64String("sign me".getBytes()),keys.get(0).getId(),keys.get(0).getSubId());
		// doCryptoCommand("sign", SkyTrustAlgorithm.RSASSA_PKCS1_V1_5_SHA_256.getAlgorithmName(),
		// Util.toBase64String("sign me".getBytes()),keys.get(0).getId(),keys.get(0).getSubId());
		// doCryptoCommand("sign", SkyTrustAlgorithm.RSAES_PKCS1_V1_5.getAlgorithmName(),
		// Util.toBase64String("sign me".getBytes()),keys.get(0).getId(),keys.get(0).getSubId());
		// doCryptoCommand("sign", SkyTrustAlgorithm.RSAES_RAW.getAlgorithmName(),
		// Util.toBase64String("sign me".getBytes()),keys.get(0).getId(),keys.get(0).getSubId());
		//
		// //getKey("certificate",keys.get(0).getId(),keys.get(0).getSubId());

    }

//    @Test
//    public void testEncrypt() {
//        doCryptoCommand("encrypt", "RSAES-PKCS1-v1_5", Util.toBase64String("encrypt me".getBytes()));
//    }
//
//    @Test
//    public void testSign() {
//        doCryptoCommand("sign", "RSA-PKCS1-SHA256", Util.toBase64String("encrypt me".getBytes()));
//    }




}
