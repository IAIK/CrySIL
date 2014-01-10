package at.iaik.skytrust.element;

import at.iaik.skytrust.element.actors.Actor;
import at.iaik.skytrust.element.receiver.Receiver;
import at.iaik.skytrust.element.proxy.Router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Basic SkyTrust Element which contains a router and receivers
 *
 * @author Hubert Gasparitz
 */
public class SkytrustElement {
    private Router _router;
	private final static SkytrustElement _skytrustelement = new SkytrustElement();
    private String _elementname;
    private Map<String, Receiver> _receiverList;
    private boolean isInitialised = false;

    private SkytrustElement() {
        _router = new Router();
        _receiverList = new HashMap<String, Receiver>();
        //Set Standard Name
        _elementname = "skytrust-element";
    }

    /**
     * Returns a basic SkyTrust Element with a HTTP Receiver a name have to be set
     * afterwards
     *
     * @return SkyTrustElement
     */
    public final static SkytrustElement create(String name) {
		if (!_skytrustelement.isInitialized()) {
			_skytrustelement._elementname = name;
			_skytrustelement.build();
        }
        return _skytrustelement;
    }

	/**
	 * Gets the instance.
	 * 
	 * @return the skytrust element
	 */
	public final static SkytrustElement get() {
		return _skytrustelement;
	}

	/**
	 * Builds the element according to the configuration. The configuration location is derived from the element name.
	 */
	private void build() {
		// load beans from configuration file
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { _elementname + ".xml" });

		// register actors
		for (Actor current : appContext.getBeansOfType(Actor.class).values())
			_router.registerActor(current.getClass().getSimpleName(), current);

		// register receivers
		for (Receiver current : appContext.getBeansOfType(Receiver.class).values()) {
			current.setRouter(_router);
			registerReceiver(current.getClass().getSimpleName(), current);
		}
	}

	/**
	 * Gets the receiver specified by the type.
	 * 
	 * @param type
	 *            the type
	 * @return the receiver
	 */
    public Receiver getReceiver(String type) {
        if (_receiverList.containsKey(type)) {
            return _receiverList.get(type);
        }
        return null;
    }

    /**
     * Register new Receiver type. If receiver name exists the receiver will be
     * replaced.
     *
     * @param type     of the receiver
     * @param receiver the receiver
     */
	private void registerReceiver(String type, Receiver receiver) {
        _receiverList.put(type, receiver);
    }

    /**
     * Deregister a existing receiver. If the receiver is not available nothing
     * will be header
     *
     * @param type of the receiver
     */
	private void deRegisterReceiver(String type) {
        _receiverList.remove(type);
    }

	/**
	 * Returns a list of receiver types. Types can be used in {@link SkytrustElement#getReceiver(String)}.
	 * 
	 * @return the receivers
	 */
	public List<String> getReceivers() {
		return new ArrayList<>(_receiverList.keySet());
	}

    /**
     * Returns the Name of the SkyTrust Element
     *
     * @return
     */
    public static String getName() {
        return _skytrustelement._elementname;
    }

	private boolean isInitialized() {
        if (_router.getAvailableActors().size() > 0) {
            isInitialised = true;
        }
        return isInitialised;
    }
}
