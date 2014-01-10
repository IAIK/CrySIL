package at.iaik.skytrust.element.skytrustprotocol.payload;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadDiscoverKeysRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.keydiscovery.SPayloadGetKeyRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.operation.SPayloadCryptoOperationRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SPayloadCryptoOperationRequest.class, name = "cryptoOperationRequest"),
        @JsonSubTypes.Type(value = SPayloadAuthRequest.class, name = "authChallengeReply"),
        @JsonSubTypes.Type(value = SPayloadDiscoverKeysRequest.class, name = "discoverKeysRequest"),
        @JsonSubTypes.Type(value = SPayloadGetKeyRequest.class, name = "getKeyRequest")
})

public class SPayloadRequest {

    protected String command="";
    protected String type;

    @JsonIgnore
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
