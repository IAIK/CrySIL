package org.crysil.actor.u2f;

import java.util.Map;

import org.crysil.actor.u2f.strategy.U2FKeyHandleStrategy;
import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;

public interface Command {

	// Note: Do not use byte[] as key in a HashMap, it won't give expected results
	public PayloadResponse perform(Request request, U2FKeyHandleStrategy strategy, Map<String, byte[]> cachedResponses,
			U2FActivityHandler activityHandler) throws CrySILException;

}
