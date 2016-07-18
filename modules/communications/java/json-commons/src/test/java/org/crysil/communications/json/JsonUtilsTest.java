package org.crysil.communications.json;

import org.crysil.protocol.Request;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JsonUtilsTest {

	@Test
	public void testU2FGenerateRequestClientParam() {
		String input = "{\"header\":{\"type\":\"standardSkyTrustHeader\",\"commandId\":\"\",\"sessionId\":\"\",\"path\":[\"\"],\"protocolVersion\":\"2.0\"},\"payload\":{\"type\":\"generateU2FKeyRequest\",\"certificateSubject\":\"CN=CrySIL\",\"appParam\":\"VWc7UTjMkNO38yv9rWo4qO3Xs1W3erl5IZbxBtFsoxI=\",\"clientParam\":\"MJPKeVEbF0NgLRq4MWBZ74ziQWbAh5JZtdBFfads7bg=\",\"encodedRandom\":null}}";
		Assert.assertTrue(JsonUtils.isValidJSONRequest(input));
		Request request = JsonUtils.fromJson(input, Request.class);
		Assert.assertNotNull(request);
	}

	@Test
	public void testU2FGenerateRequestEncodedRandom() {
		String input = "{\"header\":{\"type\":\"standardSkyTrustHeader\",\"commandId\":\"\",\"sessionId\":\"\",\"path\":[\"\"],\"protocolVersion\":\"2.0\"},\"payload\":{\"type\":\"generateU2FKeyRequest\",\"certificateSubject\":\"CN=CrySIL\",\"appParam\":\"VWc7UTjMkNO38yv9rWo4qO3Xs1W3erl5IZbxBtFsoxI=\",\"clientParam\":null,\"encodedRandom\":\"MJPKeVEbF0NgLRq4MWBZ74ziQWbAh5JZtdBFfads7bg=\"}}";
		Assert.assertTrue(JsonUtils.isValidJSONRequest(input));
		Request request = JsonUtils.fromJson(input, Request.class);
		Assert.assertNotNull(request);
	}

	@Test
	public void testU2FGenerateRequestStandardHeader() {
		String input = "{\"header\":{\"type\":\"standardHeader\",\"commandId\":\"\",\"sessionId\":\"\",\"path\":[\"\"],\"protocolVersion\":\"2.0\"},\"payload\":{\"type\":\"generateU2FKeyRequest\",\"certificateSubject\":\"CN=CrySIL\",\"appParam\":\"VWc7UTjMkNO38yv9rWo4qO3Xs1W3erl5IZbxBtFsoxI=\",\"clientParam\":\"MJPKeVEbF0NgLRq4MWBZ74ziQWbAh5JZtdBFfads7bg=\",\"encodedRandom\":null}}";
		Assert.assertTrue(JsonUtils.isValidJSONRequest(input));
		Request request = JsonUtils.fromJson(input, Request.class);
		Assert.assertNotNull(request);
	}

	@Test
	public void testU2FSign() {
		String input = "{\"header\":{\"type\":\"standardHeader\",\"commandId\":\"\",\"sessionId\":\"\",\"path\":[],\"protocolVersion\":\"2.0\"},\"payload\":{\"type\":\"signRequest\",\"algorithm\":\"SHA256withECDSA\",\"hashesToBeSigned\":[\"AFVnO1E4zJDTt/Mr/a1qOKjt17NVt3q5eSGW8QbRbKMSJ9zXliDC6V02E7UvgZ9V4+2UNvgp7KJ9T+HS9TwHE6LmdH14lPgGYmYliIqvvYyBN3oeLe+rTwVpJLcX2bXFnbHpuFtzJ/3xu/g3batGGhSMWem0GtDaGdIJp6TKnAiXBKQjZF26iyPtbNnl5IuTKs/fRWTHVzHxz1IHRRBrSbqWD60PCqUJPe4zkIRFqBa4NnzdhVcS80nlZuY3ANQm0J8=\"],\"signatureKey\":{\"type\":\"wrappedKey\",\"encodedWrappedKey\":\"5nR9eJT4BmJmJYiKr72MgTd6Hi3vq08FaSS3F9m1xZ2x6bhbcyf98bv4N22rRhoUjFnptBrQ2hnSCaekypwIlw==\"}}}";
		Assert.assertTrue(JsonUtils.isValidJSONRequest(input));
		Request request = JsonUtils.fromJson(input, Request.class);
		Assert.assertNotNull(request);
	}

	@Test
	public void testU2FHeader() {
		String input = "{\"header\":{\"type\":\"u2fHeader\",\"commandId\":\"\",\"sessionId\":\"\",\"path\":[],\"protocolVersion\":\"2.0\",\"counter\":0},\"payload\":{\"type\":\"signRequest\",\"algorithm\":\"SHA256withECDSA\",\"hashesToBeSigned\":[\"VWc7UTjMkNO38yv9rWo4qO3Xs1W3erl5IZbxBtFsoxIBAAAAAJmlC4WhOJAMl7BTaz7LVJNvv09hM79o+yfLoeXweDwf\"],\"signatureKey\":{\"type\":\"wrappedKey\", \"encodedWrappedKey\": \"WRu83ynIO6EPzJGMGDbqBQFfDFuk47Ls4lazGWqeCbuR1tAUQkVLvckli1QSg/COTVh356qrU1I7z379nmTvzg==\"}}}";
		Assert.assertTrue(JsonUtils.isValidJSONRequest(input));
		Request request = JsonUtils.fromJson(input, Request.class);
		Assert.assertNotNull(request);
	}
}
