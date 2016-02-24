package org.crysil.communications.u2f;

import org.crysil.commons.Module;

public interface Handler {

	String handle(String request, Module actor, U2FReceiverInterface receiver);

}
