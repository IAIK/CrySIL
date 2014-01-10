package at.iaik.skytrust.element.receiver.test;

import at.iaik.skytrust.element.receiver.Receiver;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;

/**
 * The TestReceiver takes skytrust commands and returns the responses.
 */
public class TestReceiver extends Receiver {

	/**
	 * Take any given request.
	 * 
	 * @param skyTrustRequest
	 *            of type {@link SRequest}
	 * @return the response of type {@link SResponse}
	 */
	public SResponse take(SRequest skyTrustRequest) {
		return router.take(skyTrustRequest);
    }
}
