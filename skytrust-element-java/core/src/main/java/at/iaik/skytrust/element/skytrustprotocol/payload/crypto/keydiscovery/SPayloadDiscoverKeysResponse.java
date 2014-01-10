package at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery;

import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */

@JsonTypeName("discoverKeysResponse")
public class SPayloadDiscoverKeysResponse extends SPayloadResponse {
    protected List<SKey> key=new ArrayList<SKey>();

    @JsonIgnore
    @Override
        public String getType() {
            return "discoverKeysResponse";
    }

    public List<SKey> getKey() {
        return key;
    }

    public void setKey(List<SKey> key) {
        this.key = key;
    }
}
