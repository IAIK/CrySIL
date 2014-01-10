package at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery;

import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */


@JsonTypeName("discoverKeysRequest")
public class SPayloadDiscoverKeysRequest extends SPayloadRequest {
    protected String representation;

    @JsonIgnore
    @Override
    public String getType() {
        return "discoverKeysRequest";
    }

    public SPayloadDiscoverKeysRequest() {
        this.command = "discoverKeys";
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }
}
