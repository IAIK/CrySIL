package at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/12/13
 * Time: 7:57 AM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("GoogleOauthAuthType")
public class SGoogleOAuthType extends SAuthType {
    protected String url="";

    @JsonIgnore
    @Override
    public String getType() {
        return "GoogleOauthAuthType";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
