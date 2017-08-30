package org.crysil.gatekeeperwithsessions.authentication.plugins.oauth.google;

import org.crysil.gatekeeperwithsessions.authentication.plugins.UserBean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public String getGive_name() {
        return givenName;
    }

    public void setGiven_name(String givenName) {
        this.givenName = givenName;
    }

    public String getFamily_name() {
        return familyName;
    }

    public void setFamily_name(String familyName) {
        this.familyName = familyName;
    }

    public String getVerified_email() {
        return verifiedEmail;
    }

    public void setVerified_email(String verifiedEmail) {
        this.verifiedEmail = verifiedEmail;
    }
}
