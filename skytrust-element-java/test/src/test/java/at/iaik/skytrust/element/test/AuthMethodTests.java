package at.iaik.skytrust.element.test;


import java.io.IOException;
import java.util.Calendar;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import at.iaik.skytrust.element.SkytrustElement;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.none.NoAuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.username.UsernamePasswordAuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.AuthenticationInfo;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.TimeLimit;
import at.iaik.skytrust.element.actors.test.AuthMethodTestActor;
import at.iaik.skytrust.element.receiver.test.TestReceiver;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.header.SkyTrustHeader;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword.SUserPasswordAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.status.SPayloadStatus;

/**
 * Test different authentication methods.
 */
@ContextConfiguration(locations = { "classpath:AuthMethodTests.xml" })
public class AuthMethodTests extends AbstractTestNGSpringContextTests {

	/** The ow. */
    protected ObjectWriter ow;

	/** The session id. */
	protected String sessionID;

	/** The interface to the skytrust world. */
	private TestReceiver receiver;

	/**
	 * The actor who allows control of the authentication requirements. Is injected via spring. Due to the singleton nature of the actor, this actor
	 * is the same object as the actor used in the skytrust element.
	 */
	@Autowired
	private AuthMethodTestActor actor;

	/**
	 * Create a fresh {@link SkytrustElement} and retrieve the {@link TestReceiver} before every test.
	 */
	@BeforeMethod
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        ow = mapper.writer().withDefaultPrettyPrinter();

		SkytrustElement element = SkytrustElement.create("AuthMethodTests");
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

	/**
	 * Send a simple request.
	 * 
	 * @return the response
	 */
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

	/**
	 * Test NoAuthPlugin.
	 */
	@Test
	public void testNoAuthPlugin() {
		actor.setAuthenticationInfo(new AuthenticationInfo(new NoAuthPlugin(), null));

		SResponse response = sendRequest();
		assertNotNull(response);
		assertFalse(response.getPayload() instanceof SPayloadAuthResponse);
	}

	/**
	 * Answer auth challenge.
	 * 
	 * @param challenge
	 *            the challenge
	 * @param useridentifier
	 *            the useridentifier
	 * @return the s response
	 */
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
	 * Uid dataprovider.
	 * 
	 * @return the object[][]
	 */
	@DataProvider(name = "UidDataprovider")
	public Object[][] UidDataprovider() {
		return new Object[][] { { "correct", true }, { "wrong", false } };
	}

	/**
	 * Test NoAuthPlugin.
	 */
	@Test(dataProvider = "UidDataprovider")
	public void testUsernamePasswordAuthPlugin(String uid, boolean expectedAuthenticationResult) {
		AuthPlugin tmp = new UsernamePasswordAuthPlugin();
		tmp.setExpectedValue("correct");
		actor.setAuthenticationInfo(new AuthenticationInfo(tmp, null));

		SResponse response = sendRequest();
		assertNotNull(response);
		assertTrue(response.getPayload() instanceof SPayloadAuthResponse);

		response = answerAuthChallenge(response, "doesn't matter"); // with this answer we reply to the user authentication question. The TestActor
																	// used here allows any user regardless of the provided uid.
		response = answerAuthChallenge(response, uid);
		assertFalse(expectedAuthenticationResult == (response.getPayload() instanceof SPayloadStatus));
		assertFalse(response.getPayload() instanceof SPayloadAuthResponse);
	}
}
