package at.iaik.skytrust.element.skytrustprotocol.payload.auth;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth.SGoogleOAuthType;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth.SHandySigOAuthType;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword.SUserPasswordAuthType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 9:02 PM
 * To change this template use File | Settings | File Templates.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SGoogleOAuthType.class, name = "GoogleOauthAuthType"),
        @JsonSubTypes.Type(value = SHandySigOAuthType.class, name = "HandySignaturAuthType"),
        @JsonSubTypes.Type(value = SUserPasswordAuthType.class, name = "UserNamePasswordAuthType")
})

public class SAuthType {
    protected String type;

    @JsonIgnore
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
