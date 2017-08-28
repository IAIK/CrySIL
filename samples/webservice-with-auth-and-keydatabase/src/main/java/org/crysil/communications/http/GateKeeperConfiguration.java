package org.crysil.communications.http;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeper.AuthPlugin;
import org.crysil.gatekeeper.AuthProcess;
import org.crysil.gatekeeper.Configuration;
import org.crysil.gatekeeper.Gatekeeper;
import org.crysil.protocol.Request;

public class GateKeeperConfiguration implements Configuration {

	@Override
	public AuthProcess getAuthProcess(Request request, Gatekeeper gatekeeper) throws AuthenticationFailedException {
		// create database connection
		// find appropriate auth information
		// assemble plugins
		// order plugins
		// return the complete auth process
		return new AuthProcess(request, new AuthPlugin[] {});
	}

}
