package at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery;

import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */


@JsonTypeName("getKeyRequest")
public class SPayloadGetKeyRequest extends SPayloadRequest {
    protected SKey key;
    protected String representation="";

    @JsonIgnore
    @Override
    public String getType() {
        return "getKeyRequest";
    }

    public SPayloadGetKeyRequest() {
         this.command = "getKey";
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    public SKey getKey() {
        return key;
    }

    public void setKey(SKey key) {
        this.key = key;
    }

}
