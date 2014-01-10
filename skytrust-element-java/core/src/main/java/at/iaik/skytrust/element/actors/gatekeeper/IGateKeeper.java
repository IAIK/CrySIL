package at.iaik.skytrust.element.actors.gatekeeper;

import at.iaik.skytrust.element.skytrustprotocol.SRequest;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/12/13
 * Time: 7:17 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IGateKeeper {
	/**
	 * 
	 * takes an arbitrary request and checks for its authorization status. If some actions are needed, the method will raise exceptions supplied with
	 * necessary responses to perform authentication and authorization of user, keys, operations, ...
	 * 
	 * @param request
	 *            any type of request
	 * @return the authorized request
	 * @throws AuthenticationRequiredException
	 *             whenever some action is needed to perform authorization. The exception contains an appropriate response.
	 */
    SRequest process(SRequest request) throws AuthenticationRequiredException;
    
    /**
     * Gets the user.
     *
     * @param authenticatedRequest the authenticated request
     * @return the user
     */
	String getUserIdentifier(SRequest authenticatedRequest);
}
