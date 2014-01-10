package at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("UserNamePasswordAuthType")
public class SUserPasswordAuthType extends SAuthType {
    @JsonIgnore
    @Override
    public String getType() {
        return "UserNamePasswordAuthType";
    }
}
