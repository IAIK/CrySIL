package org.crysil.communications.u2f;

import org.crysil.commons.Module;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyRequest;
import org.crysil.protocol.payload.crypto.sign.PayloadSignRequest;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.*;

import static org.mockito.Mockito.*;

import static org.hamcrest.MatcherAssert.*;

import static org.hamcrest.Matchers.*;

public class CrySILForwarderTest extends AbstractU2FTest {

	CrySILForwarder handler;

	@BeforeMethod
	public void before() {
		handler = new CrySILForwarder();
	}

	@Test
	public void testExecuteGenerateWrappedKey() {
		U2FReceiverInterface receiver = mock(U2FReceiverInterface.class);
		Module actor = mock(Module.class);

		byte[] clientParam = randomBytes(16);
		byte[] appParam = randomBytes(16);
		byte[] encodedRandom = randomBytes(16);

		handler.executeGenerateWrappedKey(clientParam, appParam, encodedRandom, actor, receiver);

		ArgumentCaptor<Request> argument = ArgumentCaptor.forClass(Request.class);
		verify(receiver).forwardRequest(argument.capture(), eq(actor));
		Request request = argument.getValue();

		assertThat(request.getPayload(), instanceOf(PayloadGenerateU2FKeyRequest.class));
		PayloadGenerateU2FKeyRequest payload = (PayloadGenerateU2FKeyRequest) request.getPayload();
		assertThat(payload.getCertificateSubject(), containsString("CN=CrySIL"));
		assertThat(payload.getAppParam(), equalTo(appParam));
		assertThat(payload.getClientParam(), equalTo(clientParam));
		assertThat(payload.getEncodedRandom(), equalTo(encodedRandom));
	}

	@Test
	public void testExecuteSignatureRequest() {
		U2FReceiverInterface receiver = mock(U2FReceiverInterface.class);
		Module actor = mock(Module.class);

		byte[] keyEncoded = randomBytes(16);
		byte[] hashToBeSigned = randomBytes(16);

		handler.executeSignatureRequest(keyEncoded, hashToBeSigned, actor, receiver);

		ArgumentCaptor<Request> argument = ArgumentCaptor.forClass(Request.class);
		verify(receiver).forwardRequest(argument.capture(), eq(actor));
		Request request = argument.getValue();

		assertThat(request.getPayload(), instanceOf(PayloadSignRequest.class));
		PayloadSignRequest payload = (PayloadSignRequest) request.getPayload();

		assertThat(payload.getHashesToBeSigned(), contains(equalTo(hashToBeSigned)));
		assertThat(payload.getSignatureKey(), hasProperty("encodedWrappedKey", equalTo(keyEncoded)));
		assertThat(payload.getKeys(), contains(hasProperty("encodedWrappedKey", equalTo(keyEncoded))));
	}
}
