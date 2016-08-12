package org.crysil.actor.staticKeyEncryption;

import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.PayloadResponse;

public interface Command {
	public PayloadResponse perform(PayloadRequest request) throws CrySILException;
}
