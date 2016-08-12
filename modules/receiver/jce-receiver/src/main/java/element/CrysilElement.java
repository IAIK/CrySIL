/*
 * CrySil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package element;

import proxy.Router;
import receiver.Receiver;

import java.util.HashMap;
import java.util.Map;

/**
 * The core component of the Crysil infrastructure. The {@code CrysilElement} is where anything is connected together.
 * TODO: Docu
 */
public class CrysilElement {
    
    /** The Constant INSTANCE. */
    private static final CrysilElement INSTANCE = new CrysilElement();
    
    /** The element name. */
    private String elementName = "crysil-element";
    
    /** The receiver map. */
    private Map<String, Receiver> receiverMap = new HashMap<>();
    
    /** The router. */
    private Router router = new Router();

    /**
     * Instantiates a new crysil element.
     */
    private CrysilElement() {
    }

    /**
     * Gets the basic element.
     *
     * @param elementName the element name
     * @return the basic element
     */
    public static CrysilElement getBasicElement(String elementName) {
        INSTANCE.elementName = elementName;

        return INSTANCE;
    }

    /**
     * Gets the.
     *
     * @return the crysil element
     */
    public static CrysilElement get() {
        return INSTANCE;
    }

    /**
     * Sets the receiver map.
     *
     * @param receiverMap the receiver map
     */
    public void setReceiverMap(Map<String, Receiver> receiverMap) {
        for (Map.Entry<String, Receiver> receiver : receiverMap.entrySet()) {
            registerReceiver(receiver.getKey(), receiver.getValue());
        }
    }

    /**
     * Register receiver.
     *
     * @param type the type
     * @param receiver the receiver
     */
    public void registerReceiver(String type, Receiver receiver) {
        receiver.setRouter(router);
        receiverMap.put(type, receiver);
    }

    /**
     * Gets the receiver.
     *
     * @param type the type
     * @return the receiver
     */
    public Receiver getReceiver(String type) {
        return receiverMap.get(type);
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public static String getName() {
        return INSTANCE.elementName;
    }

    /**
     * Gets the router.
     *
     * @return the router
     */
    public Router getRouter() {
        return router;
    }
}
