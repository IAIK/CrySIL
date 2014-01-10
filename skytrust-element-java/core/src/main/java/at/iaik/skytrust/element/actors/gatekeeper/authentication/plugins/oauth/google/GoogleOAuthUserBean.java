package at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.oauth.google;

import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.UserBean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/20/13
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoogleOAuthUserBean extends UserBean {

    protected String id;
    protected String email;
    protected String givenName;
    protected String familyName;
    protected String verifiedEmail;


    @Override
    public String getUserId() {
        return email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("given_name")
    public String getGivenName() {
        return givenName;
    }

    @JsonProperty("given_name")
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @JsonProperty("family_name")
    public String getFamilyName() {
        return familyName;
    }

    @JsonProperty("family_name")
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @JsonProperty("verified_email")
    public String getVerifiedEmail() {
        return verifiedEmail;
    }

    @JsonProperty("verified_email")
    public void setVerifiedEmail(String verifiedEmail) {
        this.verifiedEmail = verifiedEmail;
    }
}
