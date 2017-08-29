package org.crysil.gatekeeperwithsessions.authentication.plugins;


public class UserBean extends AuthenticationResult {
    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }


    public boolean equals(Object otherUser) {
        boolean response = false;

        if (otherUser == null) {
            response = false;
        } else if (!(otherUser instanceof UserBean)) {
            response = false;
        } else {
            if (((UserBean) otherUser).getUserId().equals(this.getUserId())) {
                response = true;
            }
        }

        return response;
    }


}
