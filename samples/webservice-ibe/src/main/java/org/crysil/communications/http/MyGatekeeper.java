package org.crysil.communications.http;

import org.crysil.builders.ResponseBuilder;
import org.crysil.errorhandling.CrySILException;
import org.crysil.gatekeeperwithsessions.Configuration;
import org.crysil.gatekeeperwithsessions.Gatekeeper;
import org.crysil.gatekeeperwithsessions.session.SessionTokenBean;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.header.SessionHeader;

public class MyGatekeeper extends Gatekeeper {
	private Header originalHeader;

	public MyGatekeeper(Configuration config) {
		super(config);
	}

	@Override
	protected Response preprocess(Request request) throws CrySILException {
		originalHeader = request.getHeader();

		SessionTokenBean currentSession = sessionManager.getSession(((SessionHeader) originalHeader).getSessionId());

		FeatureSetHeader newHeader = new FeatureSetHeader();
		newHeader.setFeatureSet(currentSession.getFeatures());

		Response result = super.preprocess(new Request(newHeader, request.getPayload()));

		return ResponseBuilder.build(originalHeader, result.getPayload());
	}
}
