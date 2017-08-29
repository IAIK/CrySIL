package org.crysil.gatekeeperwithsessions;


import java.util.LinkedList;
import java.util.List;

import org.crysil.commons.Module;
import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.errorhandling.CrySILException;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.IdentifierAuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.AuthenticationPeriod;
import org.crysil.gatekeeperwithsessions.configuration.CountLimit;
import org.crysil.gatekeeperwithsessions.configuration.FeatureSet;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.SessionHeader;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;
import org.crysil.protocol.payload.auth.credentials.IdentifierAuthInfo;
import org.crysil.protocol.payload.crypto.key.KeyRepresentation;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysResponse;
import org.crysil.protocol.payload.status.PayloadStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * High level tests of crysil element.
 */
public class GatekeeperTests {
	protected String sessionID;
    private Gatekeeper DUT;

	class TestActor implements Module {

		@Override
		public Response take(Request request) throws CrySILException {
			return new Response(request.getHeader(), new PayloadDiscoverKeysResponse());
		}
    }

	/**
	 * Create a fresh {@link crysilElement} and retrieve the {@link TestReceiver} before every test.
	 */
	@BeforeMethod
    public void init() {

        DUT = new Gatekeeper();
		DUT.attach(new TestActor());

		sessionID = "";
    }

    /**
     * Creates the basic request.
     *
     * @return the s request
     */
    protected Request createBasicRequest() {
        Request request = new Request();

		SessionHeader header = new StandardHeader();
        header.setSessionId(sessionID);
        request.setHeader(header);

        return request;
    }

    /**
     * Generate request.
     *
     * @return the s request
     */
    protected Request generateRequest() {
		PayloadDiscoverKeysRequest payload = new PayloadDiscoverKeysRequest();
		payload.setRepresentation(KeyRepresentation.HANDLE);

        Request request = createBasicRequest();
        request.setPayload(payload);

        return request;
    }

    /**
     * creates an answer to an Identifier-auth challenge.
     *
     * @param challenge
     *            the challenge
     * @param useridentifier
     *            the useridentifier
     * @return the s request
     */
    public Request answerAuthChallenge(Response challenge, String useridentifier) {
        Request authRequest = createBasicRequest();
		IdentifierAuthInfo authInfo = new IdentifierAuthInfo();
        authInfo.setIdentifier(useridentifier);
        PayloadAuthRequest authRequestPayload = new PayloadAuthRequest();
        authRequestPayload.setAuthInfo(authInfo);
        authRequest.setPayload(authRequestPayload);
        authRequest.getHeader().setCommandId(challenge.getHeader().getCommandId());

        return authRequest;
    }

    /**
     * Testconfig
     */
    class Config implements Configuration {

        public List<AuthPlugin> authSteps = new LinkedList<>();
        public AuthenticationPeriod period;

        @Override
		public AuthorizationProcess getAuthorizationProcess(FeatureSet features) {
			return new AuthorizationProcess(period, authSteps);
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////// basic tests ///////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
	 * 1-step authentication, wrong credential.
	 * @throws CrySILException 
	 */
    @Test(groups = "basicTests")
	public void testFailedUserAuthentication() throws CrySILException {
        Config config = new Config();

        AuthPlugin authPlugin = new IdentifierAuthPlugin();
        authPlugin.setExpectedValue("correct");

        config.authSteps.add(authPlugin);
        config.period = new CountLimit(1);

        DUT.setConfiguration(config);

        Request request = generateRequest();

		Response response0 = DUT.take(request);
		if (response0.getPayload() instanceof PayloadAuthResponse) {
			Response response1 = DUT.take(answerAuthChallenge(response0, "wrong"));
			Assert.assertTrue(response1.getPayload() instanceof PayloadStatus, "got anything but PayloadStatus");
			Assert.assertEquals(((PayloadStatus) response1.getPayload()).getCode(),
							new AuthenticationFailedException().getErrorCode(),
                            "unexpected status code");
		} else
			Assert.fail("we got data instead of an auth challenge");
    }

    /**
	 * 2-step authentication
	 * @throws CrySILException 
	 */
    @Test(groups = "basicTests")
	public void testSuccessfulUserAuthentication() throws CrySILException {
        Config config = new Config();

        AuthPlugin authPlugin = new IdentifierAuthPlugin();
        authPlugin.setExpectedValue("correct");

        config.authSteps.add(authPlugin);
        config.authSteps.add(authPlugin);
        config.period = new CountLimit(1);

        DUT.setConfiguration(config);

        Request request = generateRequest();

		Response response0 = DUT.take(request);
		if (response0.getPayload() instanceof PayloadAuthResponse) {
			Response response1 = DUT.take(answerAuthChallenge(response0, "correct"));
			if (response1.getPayload() instanceof PayloadAuthResponse) {
				Response response2 = DUT.take(answerAuthChallenge(response1, "correct"));
				Assert.assertTrue(response2.getPayload() instanceof PayloadDiscoverKeysResponse);
				// Assert.assertNotNull(result.getSessionFeatures(), "session
				// feature list is null");
				// Assert.assertFalse(result.getSessionFeatures().isEmpty(), "no
				// session features present");
				//
				// int hitCount = 0;
				// for (Feature current : result.getSessionFeatures())
				// if (current instanceof IdentifierAuthResult)
				// hitCount++;
				//
				// Assert.assertEquals(hitCount, 2, "wrong number of
				// authentication results in session");
			} else
				Assert.fail("we got data instead of an auth challenge");
		} else
			Assert.fail("we got data instead of an auth challenge");
	}

    /**
	 * 2-step authentication where the second set of credentials is wrong
	 * @throws CrySILException 
	 */
    @Test(groups = "basicTests")
	public void testFailedUserAuthenticationInSecondStep() throws CrySILException {
        Config config = new Config();

        AuthPlugin authPlugin = new IdentifierAuthPlugin();
        authPlugin.setExpectedValue("correct");

        config.authSteps.add(authPlugin);
        config.authSteps.add(authPlugin);
        config.period = new CountLimit(1);

        DUT.setConfiguration(config);

        Request request = generateRequest();

		Response response0 = DUT.take(request);
		if (response0.getPayload() instanceof PayloadAuthResponse) {
			Response response1 = DUT.take(answerAuthChallenge(response0, "correct"));
			if (response1.getPayload() instanceof PayloadAuthResponse) {
				Response response2 = DUT.take(answerAuthChallenge(response1, "wrong"));
				Assert.assertTrue(response2.getPayload() instanceof PayloadStatus, "got anything but PayloadStatus");
				Assert.assertEquals(((PayloadStatus) response2.getPayload()).getCode(),
						new AuthenticationFailedException().getErrorCode(), "unexpected status code");
			} else
				Assert.fail("we got data instead of an auth challenge");
		} else
			Assert.fail("we got data instead of an auth challenge");
    }

    /**
	 * 1-step authentication without expected value.
	 * @throws CrySILException 
	 */
	@Test(groups = "basicTests", enabled = false)
	public void testSuccessfulUserAuthenticationWithoutExpectedValue() throws CrySILException {
        Config config = new Config();

        AuthPlugin authPlugin = new IdentifierAuthPlugin();

        config.authSteps.add(authPlugin);
        config.period = new CountLimit(1);

        DUT.setConfiguration(config);

        Request request = generateRequest();

			Response response0 = DUT.take(request);
		if (response0.getPayload() instanceof PayloadAuthResponse) {
					Response response1 = DUT.take(answerAuthChallenge(response0, "correct"));
//                    Assert.assertNotNull(result.getSessionFeatures(), "session feature list is null");
//                    Assert.assertFalse(result.getSessionFeatures().isEmpty(), "no session features present");
//                    Assert.assertTrue(result.getSessionFeatures().get(0) instanceof IdentifierAuthResult
//                            || result.getSessionFeatures().get(1) instanceof IdentifierAuthResult,
//                            "wrong authentication result info in session");
            } else
			Assert.fail("we got data instead of an auth challenge");
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////// session tests //////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
	 * 1-step authentication
	 * @throws CrySILException 
	 *
	 * @throws AuthenticationFailedException
	 *             the authentication failed exception
	 * @throws AuthenticationRequiredException
	 */
    @Test(dependsOnGroups = { "basicTests" })
	public void testSessionKeptOpen() throws CrySILException {
        Config config = new Config();

        AuthPlugin authPlugin = new IdentifierAuthPlugin();
        authPlugin.setExpectedValue("correct");

        config.authSteps.add(authPlugin);
        config.period = new CountLimit(2);

        DUT.setConfiguration(config);

        Request request = generateRequest();

		Response response0 = DUT.take(request);
		if (response0.getPayload() instanceof PayloadAuthResponse) {
			DUT.take(answerAuthChallenge(response0, "correct"));
			sessionID = ((SessionHeader) response0.getHeader()).getSessionId();
		} else
			Assert.fail("we got data instead of an auth challenge");

		DUT.take(generateRequest());
    }

    /**
     * 1-step authentication
     * @throws CrySILException 
     *
     * @throws AuthenticationRequiredException
     */
    @Test(dependsOnGroups = { "basicTests" })
    public void testSessionFeatureSetExpired() throws CrySILException {
        Config config = new Config();

        AuthPlugin authPlugin = new IdentifierAuthPlugin();
        authPlugin.setExpectedValue("correct");

        config.authSteps.add(authPlugin);
        config.period = new CountLimit(1);

        DUT.setConfiguration(config);

        Request request = generateRequest();

		Response response0 = DUT.take(request);
		if (response0.getPayload() instanceof PayloadAuthResponse) {
			Response response1 = DUT.take(answerAuthChallenge(response0, "correct"));
			sessionID = ((SessionHeader) response1.getHeader()).getSessionId();
		} else
			Assert.fail("we got data instead of an auth challenge");

		Response response2 = DUT.take(generateRequest());
		Assert.assertTrue(response2.getPayload() instanceof PayloadAuthResponse,
				"got anything but asked for authentication");
    }
}
