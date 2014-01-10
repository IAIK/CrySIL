package at.iaik.skytrust.element.receiver;

import at.iaik.skytrust.element.proxy.Router;

/**
 * The receiving part of the Skytrust Element. The {@link Receiver} acts as an
 * interface to the outer world of deamon's and machine codes.
 * <p/>
 * Each {@link Receiver} has to use the static available {@link Router} to
 * forward their quests. Therefore, each {@link Receiver} has to convert its
 * quest to the unified command format.
 */
public abstract class Receiver {

	/** The interface to the Skytrust infrastructure. */
	protected Router router;

	/**
	 * Sets the router.
	 * 
	 * @param router
	 *            the new router
	 */
	public final void setRouter(Router router) {
		this.router = router;
	}
}
