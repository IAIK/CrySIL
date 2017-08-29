package org.crysil.gatekeeperwithsessions;

import org.crysil.protocol.Response;

public class AuthenticationRequiredException extends Exception {
    private static final long serialVersionUID = -3870991674993300867L;
    private Response response;

    public AuthenticationRequiredException(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }
}
