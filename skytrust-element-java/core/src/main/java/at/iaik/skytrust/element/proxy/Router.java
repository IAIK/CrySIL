package at.iaik.skytrust.element.proxy;

import at.iaik.skytrust.element.SkytrustElement;
import at.iaik.skytrust.element.actors.Actor;
import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;


/**
 * The router takes commands and forwards them to an appropriate actor.
 * <p/>
 * The class design is aimed at easy use with minimal coding overhead. Making
 * the {@link Router} a static class is a first step towards clean code.
 * <p/>
 * The router may implement some fancy routing mechanisms in the future. For now
 * we just take the very first actor we find.
 */
public class Router {

    /**
     * the list of available actors for this very instance of the Skytrust
     * Element
     */
    private Map<String, Actor> actors;
    private Logger logger;

	/**
	 * Instantiates a new router.
	 */
    public Router() {
        actors = new HashMap<String, Actor>();
        logger = Logger.getLogger("Router");
    }

	/**
	 * Gets the available actors.
	 * 
	 * @return the available actors
	 */
    public Set<String> getAvailableActors() {
        return actors.keySet();
    }

	/**
	 * Registers the given actor.
	 * 
	 * @param name
	 *            the name of the actor
	 * @param actor
	 *            the actor himself
	 */
    public void registerActor(String name, Actor actor) {
        actors.put(name, actor);
    }

	/**
	 * Unregister the specified actor.
	 * 
	 * @param name
	 *            the name of the actor to be removed
	 */
    public void deregisterActor(String name) {
        if (actors.containsKey(name)) {
            actors.remove(name);
        }
    }

    /**
	 * Take a command and forward it to an appropriate actor.
	 * <p>
	 * Some fancy routing will happen here as the time comes...
	 * 
	 * @param skyTrustRequest
	 *            the SkyTrustRequest
	 */
    public SResponse take(SRequest skyTrustRequest) {
        String command = skyTrustRequest.getPayload().getCommand();
        List<String> path = skyTrustRequest.getHeader().getPath();
        if (path == null) {
            path = new ArrayList<String>();
            skyTrustRequest.getHeader().setPath(path);
        }
        path.add(SkytrustElement.getName());

		// TODO make some fancy routing happen here
		// TODO - forward if we have a forwarder available
		// - if we can handle the requested command, we do it
        for (Entry<String, Actor> entry : actors.entrySet()) {
            Actor actor = entry.getValue();
            if (actor.getProvidedCommands().contains(CmdType.valueOf(command))) {
                 SResponse skyTrustResponse = actor.take(skyTrustRequest);
                return skyTrustResponse;
            }
        }
		// TODO - if we cannot handle the request, do we tell?
        return null;
    }
}
