package org.crysil.communications.http;

import org.crysil.builders.PayloadBuilder;
import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.header.StandardHeader;
import org.testng.annotations.Test;

public class TestTest {

	@Test
	public void simpleTest() throws CrySILException {
		HttpJsonTransmitter DUT = new HttpJsonTransmitter();
		DUT.setTargetURI("http://localhost:8080/http-json-receiver/json");

		DUT.take(new Request(new StandardHeader(), PayloadBuilder.buildDiscoverKeysRequest("handle")));
	}
}
