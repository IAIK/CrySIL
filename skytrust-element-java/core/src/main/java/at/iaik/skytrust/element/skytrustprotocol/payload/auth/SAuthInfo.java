package at.iaik.skytrust.element.skytrustprotocol.payload.auth;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth.SGoogleOAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth.SHandySigOAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword.SUserPasswordAuthInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 8:42 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SGoogleOAuthInfo.class, name = "GoogleOauthAuthInfo"),
        @JsonSubTypes.Type(value = SHandySigOAuthInfo.class, name = "HandySignaturAuthInfo"),
        @JsonSubTypes.Type(value = SUserPasswordAuthInfo.class, name = "UserNamePasswordAuthInfo")
})

public class SAuthInfo {
    protected String type;

    @JsonIgnore
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
