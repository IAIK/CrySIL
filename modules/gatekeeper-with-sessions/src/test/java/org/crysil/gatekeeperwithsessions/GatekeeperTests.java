package org.crysil.gatekeeperwithsessions;


import java.util.LinkedList;
import java.util.List;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.AuthenticationRequiredException;
import org.crysil.gatekeeperwithsessions.AuthorizationProcess;
import org.crysil.gatekeeperwithsessions.Configuration;
import org.crysil.gatekeeperwithsessions.Gatekeeper;
import org.crysil.gatekeeperwithsessions.Result;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.IdentifierAuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.IdentifierAuthResult;
import org.crysil.gatekeeperwithsessions.configuration.AuthenticationPeriod;
import org.crysil.gatekeeperwithsessions.configuration.CountLimit;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
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

	/**
	 * Create a fresh {@link crysilElement} and retrieve the {@link TestReceiver} before every test.
	 */
	@BeforeMethod
    public void init() {

        DUT = new Gatekeeper();

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
        public AuthorizationProcess getAuthorizationProcess(FeatureSet features) throws AuthenticationFailedException {
            return new AuthorizationProcess(period, authSteps);
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////// basic tests ///////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 1-step authentication, wrong credential.
     *
     * @throws AuthenticationFailedException
     *             the authentication failed exception
     */
    @Test(groups = "basicTests")
    public void testFailedUserAuthentication() throws AuthenticationFailedException {
        Config config = new Config();

        AuthPlugin authPlugin = new IdentifierAuthPlugin();
        authPlugin.setExpectedValue("correct");

        config.authSteps.add(authPlugin);
        config.period = new CountLimit(1);

        DUT.setConfiguration(config);

        Request request = generateRequest();

        try {
            DUT.process(request);
        } catch (AuthenticationRequiredException e) {
            if (e.getResponse().getPayload() instanceof PayloadAuthResponse) {
                try {
                    DUT.process(answerAuthChallenge(e.getResponse(), "wrong"));
                    Assert.fail("authentication was wrong but we got a reponse other than a statuscode");
                } catch (AuthenticationRequiredException e1) {
                    Assert.assertTrue(e1.getResponse().getPayload() instanceof PayloadStatus, "got anything but PayloadStatus");
					Assert.assertEquals(((PayloadStatus) e1.getResponse().getPayload()).getCode(),
							new AuthenticationFailedException().getErrorCode(),
                            "unexpected status code");
                }
            } else
                Assert.fail("we got data instead of an auth challenge", e);
        }
    }

    /**
     * 2-step authentication
     *
     * @throws AuthenticationFailedException
     *             the authentication failed exception
     */
    @Test(groups = "basicTests")
    public void testSuccessfulUserAuthentication() throws AuthenticationFailedException {
        Config config = new Config();

        AuthPlugin authPlugin = new IdentifierAuthPlugin();
        authPlugin.setExpectedValue("correct");

        config.authSteps.add(authPlugin);
        config.authSteps.add(authPlugin);
        config.period = new CountLimit(1);

        DUT.setConfiguration(config);

        Request request = generateRequest();

        try {
            DUT.process(request);
        } catch (AuthenticationRequiredException e) {
            if (e.getResponse().getPayload() instanceof PayloadAuthResponse) {
                try {
                    DUT.process(answerAuthChallenge(e.getResponse(), "correct"));
                } catch (AuthenticationRequiredException e1) {
                    if (e1.getResponse().getPayload() instanceof PayloadAuthResponse) {
                        try {
                            Result result = DUT.process(answerAuthChallenge(e1.getResponse(), "correct"));
                            Assert.assertNotNull(result.getSessionFeatures(), "session feature list is null");
                            Assert.assertFalse(result.getSessionFeatures().isEmpty(), "no session features present");

                            int hitCount = 0;
                            for (Feature current : result.getSessionFeatures())
                                if (current instanceof IdentifierAuthResult)
                                    hitCount++;

                            Assert.assertEquals(hitCount, 2, "wrong number of authentication results in session");
                        } catch (AuthenticationRequiredException e2) {
                            Assert.fail("we got data instead of an auth challenge", e2);
                        }
                    } else {
                        Assert.fail("we got data instead of an auth challenge", e1);
                    }
                }
            } else
                Assert.fail("we got data instead of an auth challenge", e);

        }
	}

    /**
     * 2-step authentication where the second set of credentials is wrong
     *
     * @throws AuthenticationFailedException
     *             the authentication failed exception
     */
    @Test(groups = "basicTests")
    public void testFailedUserAuthenticationInSecondStep() throws AuthenticationFailedException {
        Config config = new Config();

        AuthPlugin authPlugin = new IdentifierAuthPlugin();
        authPlugin.setExpectedValue("correct");

        config.authSteps.add(authPlugin);
        config.authSteps.add(authPlugin);
        config.period = new CountLimit(1);

        DUT.setConfiguration(config);

        Request request = generateRequest();

        try {
            DUT.process(request);
        } catch (AuthenticationRequiredException e) {
            if (e.getResponse().getPayload() instanceof PayloadAuthResponse) {
                try {
                    DUT.process(answerAuthChallenge(e.getResponse(), "correct"));
                } catch (AuthenticationRequiredException e1) {
                    if (e1.getResponse().getPayload() instanceof PayloadAuthResponse) {
                        try {
                            DUT.process(answerAuthChallenge(e1.getResponse(), "wrong"));
                        } catch (AuthenticationRequiredException e2) {
                            Assert.assertTrue(e2.getResponse().getPayload() instanceof PayloadStatus, "got anything but PayloadStatus");
                            Assert.assertEquals(((PayloadStatus) e2.getResponse().getPayload()).getCode(),
									new AuthenticationFailedException().getErrorCode(),
                                    "unexpected status code");
                        }
                    } else {
                        Assert.fail("we got data instead of an auth challenge", e1);
                    }
                }
            } else
                Assert.fail("we got data instead of an auth challenge", e);

        }
    }

    /**
     * 1-step authentication without expected value.
     *
     * @throws AuthenticationFailedException
     *             the authentication failed exception
     */
    @Test(groups = "basicTests")
    public void testSuccessfulUserAuthenticationWithoutExpectedValue() throws AuthenticationFailedException {
        Config config = new Config();

        AuthPlugin authPlugin = new IdentifierAuthPlugin();

        config.authSteps.add(authPlugin);
        config.period = new CountLimit(1);

        DUT.setConfiguration(config);

        Request request = generateRequest();

        try {
            DUT.process(request);
        } catch (AuthenticationRequiredException e1) {
            if (e1.getResponse().getPayload() instanceof PayloadAuthResponse) {
                try {
                    Result result = DUT.process(answerAuthChallenge(e1.getResponse(), "correct"));
                    Assert.assertNotNull(result.getSessionFeatures(), "session feature list is null");
                    Assert.assertFalse(result.getSessionFeatures().isEmpty(), "no session features present");
                    Assert.assertTrue(result.getSessionFeatures().get(0) instanceof IdentifierAuthResult
                            || result.getSessionFeatures().get(1) instanceof IdentifierAuthResult,
                            "wrong authentication result info in session");
                } catch (AuthenticationRequiredException e2) {
                    Assert.fail("we got data instead of an auth challenge", e2);
                }
            } else {
                Assert.fail("we got data instead of an auth challenge", e1);
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////// session tests //////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 1-step authentication
     *
     * @throws AuthenticationFailedException
     *             the authentication failed exception
     * @throws AuthenticationRequiredException
     */
    @Test(dependsOnGroups = { "basicTests" })
    public void testSessionKeptOpen() throws AuthenticationFailedException, AuthenticationRequiredException {
        Config config = new Config();

        AuthPlugin authPlugin = new IdentifierAuthPlugin();
        authPlugin.setExpectedValue("correct");

        config.authSteps.add(authPlugin);
        config.period = new CountLimit(2);

        DUT.setConfiguration(config);

        Request request = generateRequest();

        try {
            DUT.process(request);
        } catch (AuthenticationRequiredException e) {
            if (e.getResponse().getPayload() instanceof PayloadAuthResponse) {
                try {
                    Result result = DUT.process(answerAuthChallenge(e.getResponse(), "correct"));
					sessionID = ((SessionHeader) result.getOriginalRequest().getHeader()).getSessionId();
                } catch (AuthenticationRequiredException e1) {
                    Assert.fail("we got data instead of an auth challenge", e);
                }
            } else
                Assert.fail("we got data instead of an auth challenge", e);
        }

        DUT.process(generateRequest());
    }

    /**
     * 1-step authentication
     *
     * @throws AuthenticationFailedException
     *             the authentication failed exception
     * @throws AuthenticationRequiredException
     */
    @Test(dependsOnGroups = { "basicTests" })
    public void testSessionFeatureSetExpired() throws AuthenticationFailedException {
        Config config = new Config();

        AuthPlugin authPlugin = new IdentifierAuthPlugin();
        authPlugin.setExpectedValue("correct");

        config.authSteps.add(authPlugin);
        config.period = new CountLimit(1);

        DUT.setConfiguration(config);

        Request request = generateRequest();

        try {
            DUT.process(request);
        } catch (AuthenticationRequiredException e) {
            if (e.getResponse().getPayload() instanceof PayloadAuthResponse) {
                try {
                    Result result = DUT.process(answerAuthChallenge(e.getResponse(), "correct"));
					sessionID = ((SessionHeader) result.getOriginalRequest().getHeader()).getSessionId();
                } catch (AuthenticationRequiredException e1) {
                    Assert.fail("we got data instead of an auth challenge", e);
                }
            } else
                Assert.fail("we got data instead of an auth challenge", e);
        }

        try {
            DUT.process(generateRequest());
            Assert.fail("we got data although we should get asked for authentication");
        } catch (AuthenticationRequiredException e) {
            Assert.assertTrue(e.getResponse().getPayload() instanceof PayloadAuthResponse, "got anything but asked for authentication");

        }
    }
}
