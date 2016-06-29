package org.crysil.protocol.header;

import java.util.Arrays;
import org.crysil.logging.Logger;

/**
 * The most basic header one can use. Contains various fields that popped up during the CrySIL project up until now.
 */
public class StandardHeader extends SessionHeader {


	@Override
	public String getType() {
		return "standardHeader";
	}

	@Override
	public Header getBlankedClone() {
		final StandardHeader result = new StandardHeader();
		result.commandId = Logger.isInfoEnabled() ? commandId : "*****";
		result.sessionId = Logger.isDebugEnabled() ? sessionId : "*****";
		return result;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] { type, commandId, sessionId });
	}
}
