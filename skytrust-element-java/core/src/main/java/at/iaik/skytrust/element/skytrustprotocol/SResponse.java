package at.iaik.skytrust.element.skytrustprotocol;

import at.iaik.skytrust.element.skytrustprotocol.header.SkyTrustHeader;
import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadResponse;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 8:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class SResponse {
    protected SkyTrustHeader header;
    protected SPayloadResponse payload;

    public SkyTrustHeader getHeader() {
        return header;
    }

    public void setHeader(SkyTrustHeader header) {
        this.header = header;
    }

    public SPayloadResponse getPayload() {
        return payload;
    }

    public void setPayload(SPayloadResponse payload) {
        this.payload = payload;
    }
}
