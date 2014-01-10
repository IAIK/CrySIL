package at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonTypeName("UserNamePasswordAuthInfo")
public class SUserPasswordAuthInfo extends SAuthInfo {

    protected String userName="";
    protected String passWord="";

    @JsonIgnore
    @Override
    public String getType() {
        return "UserNamePasswordAuthInfo";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
