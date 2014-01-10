package at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery;

import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("getKeyResponse")
public class SPayloadGetKeyResponse extends SPayloadResponse {
    protected SKey key;

    @JsonIgnore
    @Override
    public String getType() {
        return "getKeyResponse";
    }

    public SKey getKey() {
        return key;
    }

    public void setKey(SKey key) {
        this.key = key;
    }


}
