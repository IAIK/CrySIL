import org.crysil.commons.Module;
import org.crysil.communications.http.GateKeeperConfiguration;
import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.errorhandling.CrySILException;
import org.crysil.gatekeeperwithsessions.Gatekeeper;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.SessionHeader;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.PayloadAuthResponse;
import org.crysil.protocol.payload.auth.credentials.SecretAuthInfo;
import org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptRequest;
import org.crysil.protocol.payload.crypto.key.KeyHandle;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysResponse;
import org.crysil.protocol.payload.status.PayloadStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestTest {

	private String sessionID;
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
		DUT = new Gatekeeper(new GateKeeperConfiguration());
		DUT.attach(new TestActor());
	}

	@Test(enabled = false)
	public void gettingStarted() throws AuthenticationFailedException {
		GateKeeperConfiguration DUT = new GateKeeperConfiguration();
		DUT.getAuthorizationProcess(null);
	}

	@Test(enabled = false)
	public void testDiscoverKeysRequest() throws CrySILException {

		Request fixture = new Request(new StandardHeader(), new PayloadDiscoverKeysRequest());
		DUT.take(fixture);
	}

	@Test
	public void testEncryptRequestWrongAuth() throws CrySILException {
		Request fixture = createFixture();

		Response response0 = DUT.take(fixture);
		Assert.assertTrue(response0.getPayload() instanceof PayloadAuthResponse);
		Response response1 = DUT.take(answerAuthChallenge(response0, "wrong"));
		Assert.assertTrue(response1.getPayload() instanceof PayloadStatus);
	}

	@Test
	public void testEncryptRequestCorrectAuth() throws CrySILException {
		Request fixture = createFixture();

		Response response0 = DUT.take(fixture);
		Assert.assertTrue(response0.getPayload() instanceof PayloadAuthResponse);
		Response response1 = DUT.take(answerAuthChallenge(response0, "correct"));
		Assert.assertTrue(response1.getPayload() instanceof PayloadDiscoverKeysResponse);
	}

	private Request createFixture() {
		PayloadEncryptRequest payload = new PayloadEncryptRequest();
		KeyHandle key = new KeyHandle();
		key.setId("a");
		key.setSubId("b");
		payload.addEncryptionKey(key);
		payload.addPlainData("data".getBytes());
		Request fixture = new Request(new StandardHeader(), payload);
		return fixture;
	}

	protected Request createBasicRequest() {
		Request request = new Request();

		SessionHeader header = new StandardHeader();
		header.setSessionId(sessionID);
		request.setHeader(header);

		return request;
	}

	public Request answerAuthChallenge(Response challenge, String secret) {
		Request authRequest = createBasicRequest();
		SecretAuthInfo authInfo = new SecretAuthInfo();
		authInfo.setSecret(secret);
		PayloadAuthRequest authRequestPayload = new PayloadAuthRequest();
		authRequestPayload.setAuthInfo(authInfo);
		authRequest.setPayload(authRequestPayload);
		authRequest.getHeader().setCommandId(challenge.getHeader().getCommandId());

		return authRequest;
	}
}
