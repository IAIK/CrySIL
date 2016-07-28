package org.crysil.communications.http;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import org.crysil.commons.Module;
import org.crysil.communications.json.JsonUtils;
import org.crysil.errorhandling.CrySILException;
import org.crysil.errorhandling.NotAcceptableException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.payload.status.PayloadStatus;

import java.io.IOException;

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

	/** validate the response against the schema? */
	private boolean isValidateSchema = true;

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

	/**
	 * Sets whether to validate the response against the schema or not.
	 *
	 * @param validate
	 */
	public void setValidateSchema(boolean validate) {
		isValidateSchema = validate;
	}

	/* (non-Javadoc)
	 * @see at.iaik.crysil.element.actors.Actor#take(at.iaik.crysil.element.crysilprotocol.SRequest)
	 */
	@Override
	public Response take(Request crysilRequest) throws CrySILException {
		String url = targetURI;

		try {
			byte[] data = JsonUtils.toJson(crysilRequest).getBytes();
			RequestBody body = RequestBody.create(JSON, data);
			com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(url).post(body).build();
			com.squareup.okhttp.Response response = client.newCall(request).execute();

			String responsestring = response.body().string();
			if (isValidateSchema && !JsonUtils.isValidJSONResponse(responsestring))
				throw new NotAcceptableException();

			Response result = JsonUtils.fromJson(responsestring, Response.class);

			if("status".equals(result.getPayload().getType())) {
				throw CrySILException.fromErrorCode(((PayloadStatus) result.getPayload()).getCode());
			}

			return result;

		} catch (IOException e) {
			Logger.error("could not find host {}", e.getMessage());
		} catch (NotAcceptableException e) {
			Logger.error("malformed response", e);
		}
		return null;
	}
}
