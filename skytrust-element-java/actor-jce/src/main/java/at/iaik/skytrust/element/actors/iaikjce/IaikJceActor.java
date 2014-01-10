package at.iaik.skytrust.element.actors.iaikjce;

import at.iaik.skytrust.element.actors.Actor;
import at.iaik.skytrust.element.actors.common.BasicCommand;
import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.actors.gatekeeper.AuthenticationFailedException;
import at.iaik.skytrust.element.actors.gatekeeper.AuthenticationRequiredException;
import at.iaik.skytrust.element.actors.gatekeeper.Configuration;
import at.iaik.skytrust.element.actors.gatekeeper.IGateKeeper;
import at.iaik.skytrust.element.actors.gatekeeper.SimpleGateKeeper;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.none.NoAuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.oauth.handysignatur.HandySigOAuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.username.UsernamePasswordAuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.AuthenticationInfo;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.FeatureSet;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.TimeLimit;
import at.iaik.skytrust.element.actors.iaikjce.commands.*;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.keystorage.rest.client.RestKeyStorage;
import at.iaik.skytrust.keystorage.rest.client.moveaway.JCEKeyAndCertificate;
import at.iaik.skytrust.keystorage.rest.database.model.SkyTrustUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A very simple {@link Actor} implementation of iaikjce which handles Commands
 * 
 * @author Hubert Gasparitz, Peter Teufl, Christof Stromberger, Florian Reimair
 */
public class IaikJceActor implements Actor, Configuration {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    protected Map<CmdType, BasicCommand> providedCommands;
	protected IGateKeeper gateKeeper = new SimpleGateKeeper(this);
    protected JCE cryptoProvider;
	protected RestKeyStorage restKeyStorage;

	/** The auth plugin injected through spring configuration. */
	private static AuthPlugin authPlugin;

	public static AuthPlugin getAuthPlugin() {
		return authPlugin;
	}

	public static void setAuthPlugin(AuthPlugin authPlugin) {
		IaikJceActor.authPlugin = authPlugin;
	}

	public RestKeyStorage getRestKeyStorage() {
		return restKeyStorage;
	}

	private String user;

    /**
     * Initialize IAIK JCE Actor
     *
     * @param restKeyStorageUrl pointing to the Rest Key Storage Server that contains the private keys
     */
    public IaikJceActor(String restKeyStorageUrl) {
		restKeyStorage = RestKeyStorage.getInstance(restKeyStorageUrl);
        restKeyStorage.baseUrl = restKeyStorageUrl;
		cryptoProvider = new SimpleJCEImpl(restKeyStorageUrl);
        createCommandEntries();
    }

    public JCE getCryptoProvider() {
        return cryptoProvider;
    }

    @Override
    public SResponse take(SRequest skyTrustRequest) {
		try {
			SRequest sRequest = gateKeeper.process(skyTrustRequest);
			return takeAuthenticatedCommand(sRequest, gateKeeper.getUserIdentifier(sRequest));
		} catch (AuthenticationRequiredException e) {
			return e.getResponse();
		}
    }

    @Override
    public Set<CmdType> getProvidedCommands() {
        return providedCommands.keySet();
    }

    private void createCommandEntries() {
        logger.info("Registering IAIK JCE Actor commands");
        providedCommands = new HashMap<CmdType, BasicCommand>();

        IaikJceCommandGetKey cmdGetCertificate = new IaikJceCommandGetKey(this);
        providedCommands.put(cmdGetCertificate.getCommandType(), cmdGetCertificate);

        IaikJceCommandDecrypt cmdDecrypt = new IaikJceCommandDecrypt(this);
        providedCommands.put(cmdDecrypt.getCommandType(), cmdDecrypt);

        IaikJceCommandEncrypt cmdEncrypt = new IaikJceCommandEncrypt(this);
        providedCommands.put(cmdEncrypt.getCommandType(), cmdEncrypt);

        IaikJceCommandDiscoverKeys cmdGetKeys = new IaikJceCommandDiscoverKeys(this);
        providedCommands.put(cmdGetKeys.getCommandType(), cmdGetKeys);

        IaikJceCommandSign cmdsign = new IaikJceCommandSign(this);
        providedCommands.put(cmdsign.getCommandType(), cmdsign);

        IaikJceCommandAuthenticate cmdAuth = new IaikJceCommandAuthenticate();
        providedCommands.put(cmdAuth.getCommandType(),cmdAuth);

        for (BasicCommand command : providedCommands.values()) {
            logger.info("Command: " + command.getCommandType().getName());
        }
    }

    /**
	 * treats the incoming request as already authorized
	 * 
	 * @param sRequest
	 *            to be header
	 * @return response for caller
	 */
	public SResponse takeAuthenticatedCommand(SRequest sRequest, String userIdentifier) {
        SResponse sResponse = providedCommands.get(CmdType.valueOf(sRequest.getPayload().getCommand())).handle(sRequest);
        sResponse.getHeader().setSessionId(sRequest.getHeader().getSessionId());
        return sResponse;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.iaik.skytrust.element.actors.gatekeeper.Configuration#getAuthenticationInfo(java.util.List)
	 */
	@Override
	public AuthenticationInfo getAuthenticationInfo(FeatureSet set) {
		if (set.containsKey("operation") && set.get("operation").getValue().equals("discoverKeys")) {
			return new AuthenticationInfo(new NoAuthPlugin(), null);
		}
		AuthPlugin tmp = authPlugin.newInstance();
		tmp.setExpectedValue(user);
		return new AuthenticationInfo(tmp, new TimeLimit(Calendar.MINUTE, 10));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.iaik.skytrust.element.actors.gatekeeper.Configuration#isUserValid(java.lang.String)
	 */
	@Override
	public void isUserValid(String identifier) throws AuthenticationFailedException {
		try {
			SkyTrustUser tmp = restKeyStorage.getUser(identifier);
			user = tmp.getUserIdentifier();
			if ("inactive".equalsIgnoreCase(tmp.getUserRole()))
				throw new AuthenticationFailedException();
		} catch (Exception e) {
			throw new AuthenticationFailedException();
		}
	}
}
