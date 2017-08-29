package org.crysil.gatekeeperwithsessions.authentication.plugins.oauth;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.UserBean;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.protocol.payload.auth.AuthType;
import org.crysil.protocol.payload.auth.PayloadAuthRequest;
import org.crysil.protocol.payload.auth.oauth.OAuthAuthInfo;
import org.crysil.protocol.payload.auth.oauth.OAuthAuthType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Authentication plugin for oAuth.
 */
public abstract class OAuthPlugin extends AuthPlugin {

    private String clientID;
    private String clientSecret;
    private String resource_URI;
    private String accessToken_URI;
    private String authorization_URI;
    private String scope;
    private String redirect_URI;
    protected UserBean response; // user bean

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getResource_URI() {
        return resource_URI;
    }

    public void setResource_URI(String resource_URI) {
        this.resource_URI = resource_URI;
    }

    public String getAccessToken_URI() {
        return accessToken_URI;
    }

    public void setAccessToken_URI(String accessToken_URI) {
        this.accessToken_URI = accessToken_URI;
    }

    public String getAuthorization_URI() {
        return authorization_URI;
    }

    public void setAuthorization_URI(String authorization_URI) {
        this.authorization_URI = authorization_URI;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRedirect_URI() {
        return redirect_URI;
    }

    public void setRedirect_URI(String redirect_URI) {
        this.redirect_URI = redirect_URI;
    }

    @Override
    public AuthType getAuthType() {
		OAuthAuthType authType = new OAuthAuthType();
        authType.setUrl(getAuthorization_URI() + "?response_type=code&client_id=" + getClientID() + "&redirect_uri=" + getRedirect_URI() + "&scope="
                + getScope());
        return authType;
    }

    @Override
	public String getReceivedIdentifier(PayloadAuthRequest authRequest) throws AuthenticationFailedException {
        try {
            // check authRequest format
			OAuthAuthInfo authInfo = (OAuthAuthInfo) authRequest.getAuthInfo();
            String code = authInfo.getAuthorizationCode();

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
            AccessTokenBean response = restTemplate.postForObject(getAccessToken_URI(), getTokenParams(code, getRedirect_URI()),
                    AccessTokenBean.class);

            return getUser(response.getAccess_token()).getUserId();
        } catch (Exception e) {
            throw new AuthenticationFailedException();
        }
    }

    private MultiValueMap<String, String> getTokenParams(String code, String redirect) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", getClientID());
        params.add("client_secret", getClientSecret());
        params.add("redirect_uri", redirect);
        params.add("grant_type", "authorization_code");
        return params;
    }

    protected MultiValueMap<String, String> getResourceParams(String accessToken) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("bearer_token", accessToken);
        return params;
    }

    public abstract UserBean getUser(String accessToken);

    @Override
    public Feature getAuthenticationResult() {
        if (null != response)
            return response;
        return null;
    }
}
