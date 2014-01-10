package at.iaik.skytrust.element.actors.test;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import at.iaik.skytrust.element.actors.Actor;
import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.actors.gatekeeper.AuthenticationFailedException;
import at.iaik.skytrust.element.actors.gatekeeper.AuthenticationRequiredException;
import at.iaik.skytrust.element.actors.gatekeeper.Configuration;
import at.iaik.skytrust.element.actors.gatekeeper.IGateKeeper;
import at.iaik.skytrust.element.actors.gatekeeper.SimpleGateKeeper;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.none.NoAuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.username.UsernamePasswordAuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.AuthenticationInfo;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.FeatureSet;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.TimeLimit;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.header.SkyTrustHeader;

public class GatekeeperTestActor implements Actor, Configuration {
	private final String username = "correct";
	// private final String passphrase = "correct";
	private IGateKeeper gateKeeper = new SimpleGateKeeper(this);

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.iaik.skytrust.element.actors.Actor#take(at.iaik.skytrust.element.skytrustprotocol.SRequest)
	 */
	public SResponse take(SRequest skyTrustRequest) {
		try {
			SRequest sRequest = gateKeeper.process(skyTrustRequest);

			// create response cmd packet
			SResponse skyTrustResponse = new SResponse();
			skyTrustResponse.setHeader(new SkyTrustHeader());
			skyTrustResponse.getHeader().setProtocolVersion("0.1");
			skyTrustResponse.getHeader().setCommandId(sRequest.getHeader().getCommandId());
			skyTrustResponse.getHeader().setSessionId(sRequest.getHeader().getSessionId());

			// return result
			return skyTrustResponse;
		} catch (AuthenticationRequiredException e) {
			return e.getResponse();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.iaik.skytrust.element.actors.Actor#getProvidedCommands()
	 */
	public Set<CmdType> getProvidedCommands() {
		Set<CmdType> result = new HashSet<>();
		result.add(CmdType.discoverKeys);
		result.add(CmdType.authenticate);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.iaik.skytrust.element.actors.gatekeeper.Configuration#getAuthenticationInfo(java.util.List)
	 */
	@Override
	public AuthenticationInfo getAuthenticationInfo(FeatureSet set) {
		if (set.containsKey("user")) {
			AuthPlugin tmp = new UsernamePasswordAuthPlugin();
			tmp.setExpectedValue(username);
			return new AuthenticationInfo(tmp, new TimeLimit(Calendar.MILLISECOND, 300));
		} else if (set.containsKey("operation")) {
			AuthPlugin tmp = new UsernamePasswordAuthPlugin();
			tmp.setExpectedValue(username);
			return new AuthenticationInfo(tmp, new TimeLimit(Calendar.MILLISECOND, 200));
		} else {
			return new AuthenticationInfo(new NoAuthPlugin(), null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.iaik.skytrust.element.actors.gatekeeper.Configuration#isUserValid(java.lang.String)
	 */
	@Override
	public void isUserValid(String identifier) throws AuthenticationFailedException {
		try {
			if (!username.equals(identifier))
				throw new Exception();
		} catch (Exception e) {
			throw new AuthenticationFailedException();
		}
	}
}
