/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package receiver;

import proxy.Router;

/**
 * The receiving part of the Crysil Element. The {@link Receiver} acts as an
 * interface to the outer world of deamon's and machine codes.
 * <p>
 * Each {@link Receiver} has to use the static available {@link Router} to forward their quests. Therefore, each {@link Receiver} has to convert its
 * quest to the unified command format.
 */
public abstract class Receiver {

	/** The interface to the Crysil infrastructure. */
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
