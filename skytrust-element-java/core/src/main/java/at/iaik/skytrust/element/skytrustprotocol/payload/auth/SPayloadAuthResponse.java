package at.iaik.skytrust.element.skytrustprotocol.payload.auth;

import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("authChallenge")
public class SPayloadAuthResponse extends SPayloadResponse {

    protected SAuthType authType;

    @JsonIgnore
    @Override
    public String getType() {
        return "authChallenge";
    }

    public SAuthType getAuthType() {
        return authType;
    }

    public void setAuthType(SAuthType authType) {
        this.authType = authType;
    }

}
