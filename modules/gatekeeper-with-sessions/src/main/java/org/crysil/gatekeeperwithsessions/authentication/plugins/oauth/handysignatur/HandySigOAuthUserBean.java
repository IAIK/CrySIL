package org.crysil.gatekeeperwithsessions.authentication.plugins.oauth.handysignatur;

import org.crysil.gatekeeperwithsessions.authentication.plugins.UserBean;

public class HandySigOAuthUserBean extends UserBean {
    protected String id;
    private String wbpk;
    private String lastname;
    private String birth_date;
    private String firstname;

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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastName) {
        this.lastname = lastName;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birthDate) {
        this.birth_date = birthDate;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstName) {
        this.firstname = firstName;
    }
}




