package org.crysil.actor.staticKeyEncryption;

import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;

public interface Command {
	public PayloadResponse perform(Request request) throws CrySILException;
}
