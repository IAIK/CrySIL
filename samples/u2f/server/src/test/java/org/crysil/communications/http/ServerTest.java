package org.crysil.communications.http;

import org.crysil.builders.PayloadBuilder;
import org.crysil.protocol.Request;
import org.crysil.protocol.header.StandardHeader;
import org.testng.annotations.Test;

public class ServerTest {

	@Test
	public void simpleTest() {
		HttpJsonTransmitter DUT = new HttpJsonTransmitter();
		DUT.setTargetURI("http://localhost:8080/server/json");

		DUT.take(new Request(new StandardHeader(), PayloadBuilder.buildGenerateU2FKeyRequest("appParam".getBytes(), "CN=Test", "clientParam".getBytes(), "encodedRandom".getBytes())));
	}
}
