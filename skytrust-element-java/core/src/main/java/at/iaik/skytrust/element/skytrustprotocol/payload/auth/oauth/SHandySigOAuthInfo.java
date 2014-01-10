package at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/12/13
 * Time: 7:57 AM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("HandySignaturAuthInfo")
public class SHandySigOAuthInfo extends SAuthInfo {
    protected String accessToken="";

    @JsonIgnore
    @Override
    public String getType() {
        return "HandySignaturAuthInfo";
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
