package at.iaik.skytrust.element.actors.test;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import com.sun.xml.internal.ws.api.PropertySet.Property;

import at.iaik.skytrust.element.actors.Actor;
import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.actors.gatekeeper.AuthenticationFailedException;
import at.iaik.skytrust.element.actors.gatekeeper.AuthenticationRequiredException;
import at.iaik.skytrust.element.actors.gatekeeper.Configuration;
import at.iaik.skytrust.element.actors.gatekeeper.IGateKeeper;
import at.iaik.skytrust.element.actors.gatekeeper.SimpleGateKeeper;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.AuthenticationInfo;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.FeatureSet;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.header.SkyTrustHeader;

/**
 * Offers control of authentication requirement to the outside.
 */
public class AuthMethodTestActor implements Actor, Configuration {

	/** The instance. */
	private static AuthMethodTestActor instance = null;

	/**
	 * Gets the single instance of {@link AuthMethodTestActor}.
	 * 
	 * @return single instance of {@link AuthMethodTestActor}
	 */
	public static AuthMethodTestActor getInstance() {
		if (null == instance)
			instance = new AuthMethodTestActor();
		return instance;
	}

	/** The gate keeper. */
	private IGateKeeper gateKeeper = new SimpleGateKeeper(this);

	/** The authentication info. */
	private AuthenticationInfo authenticationInfo;

	/**
	 * Instantiates a new auth method test actor.
	 */
	private AuthMethodTestActor() {
		// make standard constructor private
	}

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

	/**
	 * Sets the authentication info.
	 * 
	 * @param info
	 *            the new authentication info
	 */
	public void setAuthenticationInfo(AuthenticationInfo info) {
		authenticationInfo = info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.iaik.skytrust.element.actors.gatekeeper.Configuration#getAuthenticationInfo(at.iaik.skytrust.element.actors.gatekeeper.configuration.FeatureSet
	 * )
	 */
	@Override
	public AuthenticationInfo getAuthenticationInfo(FeatureSet features) throws AuthenticationFailedException {
		return authenticationInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.iaik.skytrust.element.actors.gatekeeper.Configuration#isUserValid(java.lang.String)
	 */
	@Override
	public void isUserValid(String identifier) throws AuthenticationFailedException {
		// every user is valid
	}
}
