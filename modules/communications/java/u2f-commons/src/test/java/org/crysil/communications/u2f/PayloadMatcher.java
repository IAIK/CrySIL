package org.crysil.communications.u2f;

import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadRequest;
import org.mockito.ArgumentMatcher;

public class PayloadMatcher extends ArgumentMatcher<Request> {

	private Class<? extends PayloadRequest> clazz;

	public PayloadMatcher(Class<? extends PayloadRequest> clazz) {
		this.clazz = clazz;
	}

	@Override
	public boolean matches(Object argument) {
		return argument != null && ((Request) argument).getPayload().getClass().equals(clazz);
	}

}