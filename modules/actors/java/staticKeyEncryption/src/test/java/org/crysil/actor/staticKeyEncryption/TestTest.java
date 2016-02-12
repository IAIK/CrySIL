package org.crysil.actor.staticKeyEncryption;

import org.crysil.builders.KeyBuilder;
import org.crysil.builders.PayloadBuilder;
import org.crysil.commons.Module;
import org.crysil.errorhandling.UnsupportedRequestException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest;
import org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysResponse;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestTest {

	@DataProvider
	Object[][] fixtures() {
		return new Object[][] { { "handle", false, KeyBuilder.buildKeyHandle("testkey", "1") },
				{ "certificate", false, KeyBuilder.buildInternalCertificate("testkey", "1", "TODO") },
				{ "wrong", true, PayloadBuilder.buildStatusResponse(601) } };
	}

	@Test(dataProvider = "fixtures")
	public void discoverKeyTest(String keytype, boolean expectError, Object expected) {
		Module DUT = new StaticKeyEncryptionActor();

		Request request = new Request();
		PayloadDiscoverKeysRequest payload = new PayloadDiscoverKeysRequest();
		payload.setRepresentation(keytype);
		request.setPayload(payload);

		Response response;
		try {
			response = DUT.take(request);
			if (expectError)
				Assert.assertEquals(response.getPayload(), expected);
			else {
				Assert.assertEquals(((PayloadDiscoverKeysResponse) response.getPayload()).getKey().size(), 1);
				Assert.assertEquals(((PayloadDiscoverKeysResponse) response.getPayload()).getKey().get(0), expected);
			}
		} catch (UnsupportedRequestException e) {
			Assert.fail("An exception occured where it should not have");
		}
	}
}
