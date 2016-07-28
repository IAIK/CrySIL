package org.crysil.communications.u2f;

import org.crysil.commons.Module;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

public interface U2FReceiverInterface {

	Response forwardRequest(Request request, Module actor);

}
