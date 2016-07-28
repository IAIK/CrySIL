package org.crysil.actor.smcc;

import org.crysil.actor.smcc.strategy.U2FKeyHandleStrategy;
import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.payload.PayloadResponse;

import at.gv.egiz.smcc.pin.gui.PINGUI;

public interface Command {

	public PayloadResponse perform(Request request, U2FKeyHandleStrategy strategy, PINGUI pinGUI)
			throws CrySILException;

}
