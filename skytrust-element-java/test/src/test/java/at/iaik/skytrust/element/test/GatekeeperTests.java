package at.iaik.skytrust.element.test;


import at.iaik.skytrust.element.SkytrustElement;
import at.iaik.skytrust.element.receiver.test.TestReceiver;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.header.SkyTrustHeader;
import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthType;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword.SUserPasswordAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword.SUserPasswordAuthType;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.status.SPayloadStatus;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.testng.annotations.*;

import sun.awt.RepaintArea;
import static org.testng.Assert.*;
import java.io.IOException;
import java.util.List;

/**
 * High level tests of skytrust element.
 */
public class GatekeeperTests {

	/** The ow. */
    protected ObjectWriter ow;

	/** The session id. */
	protected String sessionID;

	/** The interface to the skytrust world. */
	private TestReceiver receiver;

	/**
	 * Create a fresh {@link SkytrustElement} and retrieve the {@link TestReceiver} before every test.
	 */
	@BeforeMethod
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        ow = mapper.writer().withDefaultPrettyPrinter();

		SkytrustElement element = SkytrustElement.create("GatekeeperTests");
		receiver = (TestReceiver) element.getReceiver(TestReceiver.class.getSimpleName());

		sessionID = "";
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

	@Test
	public void testSuccessfulUserAuthentication() {
		SResponse skyTrustResponse = sendRequest();

		SPayloadResponse payloadResponse = skyTrustResponse.getPayload();
		assertTrue(payloadResponse instanceof SPayloadAuthResponse);

		skyTrustResponse = answerAuthChallenge(skyTrustResponse, "correct");

		assertNotNull(skyTrustResponse);
		assertFalse(skyTrustResponse.getPayload() instanceof SPayloadStatus);
	}

	@Test
	public void testBadUserAuthentication() {
		SResponse skyTrustResponse = sendRequest();

		SPayloadResponse payloadResponse = skyTrustResponse.getPayload();
		assertTrue(payloadResponse instanceof SPayloadAuthResponse);

		skyTrustResponse = answerAuthChallenge(skyTrustResponse, "wrong");

		assertNotNull(skyTrustResponse);
		assertTrue(skyTrustResponse.getPayload() instanceof SPayloadStatus);
		assertEquals(((SPayloadStatus) skyTrustResponse.getPayload()).getCode(), 400);
	}

	public SResponse sendRequest() {
		SPayloadDiscoverKeysRequest payload = new SPayloadDiscoverKeysRequest();
		payload.setRepresentation("handle");

		SRequest request = createBasicRequest();
		request.setPayload(payload);

		logJSON(request);
		SResponse skyTrustResponse = receiver.take(request);
		logJSON(skyTrustResponse);
		return skyTrustResponse;
	}

	public SResponse answerAuthChallenge(SResponse challenge, String useridentifier) {
		SRequest authRequest = createBasicRequest();
		SUserPasswordAuthInfo authInfo = new SUserPasswordAuthInfo();
		authInfo.setUserName(useridentifier);
		SPayloadAuthRequest authRequestPayload = new SPayloadAuthRequest();
		authRequestPayload.setAuthInfo(authInfo);
		authRequestPayload.setCommand("authenticate");
		authRequest.setPayload(authRequestPayload);
		authRequest.getHeader().setCommandId(challenge.getHeader().getCommandId());

		logJSON(authRequest);
		SResponse response = receiver.take(authRequest);
		logJSON(response);

		sessionID = response.getHeader().getSessionId();

		return response;
	}

	/**
	 * Perform operations within timeouts.
	 */
	@Test(dependsOnMethods = { "testSuccessfulUserAuthentication", "testBadUserAuthentication" })
	public void testAuthTimeout() {
		SResponse response = sendRequest();
		// answer user auth challenge
		response = answerAuthChallenge(response, "correct");
		// answer operation auth challenge
		response = answerAuthChallenge(response, "correct");

		// send next request right after
		response = sendRequest();
		assertFalse(response.getPayload() instanceof SPayloadAuthResponse);
	}

	/**
	 * Exceed operation timeout.
	 * 
	 * @throws InterruptedException
	 */
	@Test(dependsOnMethods = { "testAuthTimeout" })
	public void testAuthExceedOpTimeout() throws InterruptedException {
		SResponse response = sendRequest();
		// answer user auth challenge
		response = answerAuthChallenge(response, "correct");
		// answer operation auth challenge
		response = answerAuthChallenge(response, "correct");

		// wait until the operation session times out
		Thread.sleep(230);

		// send another request
		response = sendRequest();
		assertTrue(response.getPayload() instanceof SPayloadAuthResponse);
		// answer operation auth challenge
		response = answerAuthChallenge(response, "correct");
		assertFalse(response.getPayload() instanceof SPayloadAuthResponse);
	}

	/**
	 * Exceed user timeout.
	 * 
	 * @throws InterruptedException
	 */
	@Test(dependsOnMethods = { "testAuthExceedOpTimeout" })
	public void testAuthExceedUserTimeout() throws InterruptedException {
		SResponse response = sendRequest();
		// answer user auth challenge
		response = answerAuthChallenge(response, "correct");
		// answer operation auth challenge
		response = answerAuthChallenge(response, "correct");

		// wait until the operation session times out
		Thread.sleep(230);

		// send another request
		response = sendRequest();
		// answer operation auth challenge
		response = answerAuthChallenge(response, "correct");

		// wait until the user session times out
		Thread.sleep(100);

		// send another request
		response = sendRequest();
		assertTrue(response.getPayload() instanceof SPayloadAuthResponse);
		// answer user auth challenge
		response = answerAuthChallenge(response, "correct");
		assertFalse(response.getPayload() instanceof SPayloadAuthResponse);
	}
}
