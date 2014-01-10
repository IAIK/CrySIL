package at.iaik.skytrust.element.receiver.http;

import at.iaik.skytrust.element.receiver.Receiver;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;

public class HTTPReceiver extends Receiver {

    public SResponse forwardRequest(SRequest skyTrustRequest) {
		return router.take(skyTrustRequest);
    }

    public boolean supportElementRequest(SRequest skyTrustRequest) {
        // TODO Auto-generated method stub
        return false;
    }

}
