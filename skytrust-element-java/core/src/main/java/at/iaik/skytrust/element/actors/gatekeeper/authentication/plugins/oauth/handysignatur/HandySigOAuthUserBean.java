package at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.oauth.handysignatur;

import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.UserBean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/20/13
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class HandySigOAuthUserBean extends UserBean {

    protected String id;
    private String wbpk;
    private String lastName;
    private String birthDate;
    private String firstName;


    @Override
    public String getUserId() {
		return getWbpk();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWbpk() {
        return wbpk;
    }

    public void setWbpk(String wbpk) {
        this.wbpk = wbpk;
    }

    @JsonProperty("lastname")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("lastname")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("birth_date")
    public String getBirthDate() {
        return birthDate;
    }

    @JsonProperty("birth_date")
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @JsonProperty("firstname")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("firstname")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}




