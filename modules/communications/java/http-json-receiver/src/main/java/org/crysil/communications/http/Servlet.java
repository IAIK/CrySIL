package org.crysil.communications.http;

import org.crysil.commons.OneToOneInterlink;
import org.crysil.communications.json.JsonUtils;
import org.crysil.errorhandling.CrySILException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.header.StandardHeader;
import org.crysil.protocol.payload.status.PayloadStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Servlet extends OneToOneInterlink {

	@RequestMapping(value = "/json", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@CrossOrigin
	@ResponseBody
	public ResponseEntity<String> handleCommand(@RequestBody String rawRequest) {

		if (WebAppInitializer.getConfiguration().isValidateSchema() && !JsonUtils.isValidJSONRequest(rawRequest)) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}

		Request request = JsonUtils.fromJson(rawRequest, Request.class);
		if (request == null) {
			return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
		}

		Logger.info("Incoming request: {}", JsonUtils.toJson(request.getBlankedClone()));

		Response response;
		try {
			response = WebAppInitializer.getConfiguration().getAttachedModule().take(request);
		} catch (CrySILException e) {
			response = new Response();
			final Header header = new StandardHeader();
			header.setCommandId(request.getHeader().getCommandId());
			response.setHeader(header);
			final PayloadStatus responsePayload = new PayloadStatus();
			responsePayload.setCode(e.getErrorCode());
			response.setPayload(responsePayload);
		}

		Logger.info("Created response: {}", JsonUtils.toJson(response.getBlankedClone()));

		String rawResponse = JsonUtils.toJson(response);
		if (rawResponse.isEmpty() || (WebAppInitializer.getConfiguration().isValidateSchema()
				&& !JsonUtils.isValidJSONResponse(rawResponse))) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(rawResponse, HttpStatus.OK);
	}
}
