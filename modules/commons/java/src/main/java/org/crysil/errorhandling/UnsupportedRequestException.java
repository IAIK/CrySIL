package org.crysil.errorhandling;

/**
 * Gets thrown when a module does not know how to handle a request or response.
 */
public class UnsupportedRequestException extends CrySILException {

	private static final long serialVersionUID = 2034533437775740827L;

	@Override
	public int getErrorCode() {
		return 601;
	}

}
