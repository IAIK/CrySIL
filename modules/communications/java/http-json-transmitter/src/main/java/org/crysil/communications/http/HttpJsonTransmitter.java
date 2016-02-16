package org.crysil.communications.http;

import java.io.IOException;

import org.crysil.builders.PayloadBuilder;
import org.crysil.commons.Module;
import org.crysil.communications.json.JsonUtils;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.StandardHeader;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

/**
 * The Class HttpForwarder.
 */
public class HttpJsonTransmitter implements Module {

	/** The Constant JSON. */
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	/** The client. */
	private final OkHttpClient client = new OkHttpClient();

	/** The target uri. */
	private String targetURI;

	/**
	 * Instantiates a new http forwarder.
	 */
	public HttpJsonTransmitter() {
	}

	/**
	 * Sets the target uri.
	 *
	 * @param targetURI the new target uri
	 */
	public void setTargetURI(String targetURI) {
		this.targetURI = targetURI;
	}

	/* (non-Javadoc)
	 * @see at.iaik.skytrust.element.actors.Actor#take(at.iaik.skytrust.element.skytrustprotocol.SRequest)
	 */
	@Override
	public Response take(Request crysilRequest) {
		String url = targetURI;

		try {
			byte[] data = JsonUtils.toJson(crysilRequest).getBytes();
			RequestBody body = RequestBody.create(JSON, data);
			com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(url).post(body).build();
			com.squareup.okhttp.Response response = client.newCall(request).execute();

			return JsonUtils.fromJson(response.body().string(), Response.class);
		} catch (IOException e) {
			Logger.error("could not find host {}", e.getMessage());
		}
		return null;
	}

	public static void main(String[] args) {
		HttpJsonTransmitter DUT = new HttpJsonTransmitter();
		DUT.setTargetURI("http://localhost:8080/http-json-receiver/json");

		DUT.take(new Request(new StandardHeader(), PayloadBuilder.buildDiscoverKeysRequest("handle")));
		System.out.println("done");
	}
}
