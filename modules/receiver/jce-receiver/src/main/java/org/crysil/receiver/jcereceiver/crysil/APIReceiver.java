/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.crysil;

import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

import receiver.Receiver;

/**
 * The Class APIReceiver.
 */
public class APIReceiver extends Receiver {
    
    /**
     * Take.
     *
     * @param crysilRequest the crysil request
     * @return the crysil response
     */
    public Response take(Request crysilRequest) {
		return router.take(crysilRequest);
    }
}
