package org.crysil.builders;

import org.crysil.protocol.Response;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.payload.PayloadResponse;

public class ResponseBuilder {

	public static Response build(Header header, PayloadResponse payload) {
		Response tmp = new Response();
		tmp.setHeader(header);
		tmp.setPayload(payload);

		return tmp;
	}
}
