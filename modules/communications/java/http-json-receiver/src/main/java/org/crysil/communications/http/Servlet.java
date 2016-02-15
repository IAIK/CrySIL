package org.crysil.communications.http;

import org.crysil.builders.PayloadBuilder;
import org.crysil.builders.ResponseBuilder;
import org.crysil.commons.OneToOneInterlink;
import org.crysil.errorhandling.CrySILException;
import org.crysil.logging.Logger;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Servlet extends OneToOneInterlink {

	@RequestMapping(value = "/json", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public ResponseEntity<String> handleCommand(@RequestBody String rawRequest) {

		if (WebAppInitializer.getConfiguration().isValidateSchema()
				&& !JsonUtils.isValidJSON(rawRequest, WebAppInitializer.requestSchema)) {
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
			response = ResponseBuilder.build(request.getHeader(), PayloadBuilder.buildStatusResponse(e.getErrorCode()));
		}

		Logger.info("Created response: {}", JsonUtils.toJson(response.getBlankedClone()));

		String rawResponse = JsonUtils.toJson(response);
		if (rawResponse.isEmpty() || (WebAppInitializer.getConfiguration().isValidateSchema()
				&& !JsonUtils.isValidJSON(rawResponse, WebAppInitializer.responseSchema))) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(rawResponse, HttpStatus.OK);
	}
}
