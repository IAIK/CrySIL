package org.crysil.commons;

import org.crysil.protocol.Request;
import org.crysil.protocol.Response;

/**
 * The {@link Module} interface enables basic communication between implementing
 * blocks at runtime.
 *
 */
public interface Module {

	/**
	 * Takes a CrySIL protocol package.
	 * 
	 * @param object
	 */
	public Response take(Request request);
}
