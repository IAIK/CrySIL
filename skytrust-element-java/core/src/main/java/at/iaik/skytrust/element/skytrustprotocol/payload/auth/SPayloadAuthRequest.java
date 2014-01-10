package at.iaik.skytrust.element.skytrustprotocol.payload.auth;

import at.iaik.skytrust.element.skytrustprotocol.payload.SPayloadRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("authChallengeReply")
public class SPayloadAuthRequest extends SPayloadRequest {
    protected SAuthInfo authInfo;

    public SPayloadAuthRequest() {
        command = "authenticate";
    }

    @JsonIgnore
    @Override
    public String getType() {
        return "authChallengeReply";
    }

    public SAuthInfo getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(SAuthInfo authInfo) {
        this.authInfo = authInfo;
    }

}
