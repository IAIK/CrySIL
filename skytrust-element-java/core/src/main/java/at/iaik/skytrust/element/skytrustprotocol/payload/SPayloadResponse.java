package at.iaik.skytrust.element.skytrustprotocol.payload;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadGetKeyResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SPayloadWithLoadResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.status.SPayloadStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SPayloadWithLoadResponse.class, name = "responseWithLoad"),
        @JsonSubTypes.Type(value = SPayloadAuthResponse.class, name = "authChallenge"),
        @JsonSubTypes.Type(value = SPayloadStatus.class, name = "status"),
        @JsonSubTypes.Type(value = SPayloadGetKeyResponse.class, name = "getKeyResponse"),
        @JsonSubTypes.Type(value = SPayloadDiscoverKeysResponse.class, name = "discoverKeysResponse")
})
public class SPayloadResponse {
    protected String type;

    @JsonIgnore
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
