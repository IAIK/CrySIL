package org.crysil.communications.http;

import org.crysil.builders.PayloadBuilder;
import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateU2FKeyResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ServerTest {

	@Test
	public void simpleTest() throws CrySILException {
		HttpJsonTransmitter DUT = new HttpJsonTransmitter();
		DUT.setTargetURI("http://localhost:8080/json");
		Response response = DUT.take(new Request(new StandardHeader(), PayloadBuilder
				.buildGenerateU2FKeyRequest("appParam".getBytes(), "CN=Test", "clientParam".getBytes(), null)));
		Assert.assertTrue(response.getPayload() instanceof PayloadGenerateU2FKeyResponse);
	}
}
