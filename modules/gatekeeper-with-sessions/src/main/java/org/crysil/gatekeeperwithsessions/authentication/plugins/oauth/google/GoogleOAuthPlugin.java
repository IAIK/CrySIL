package org.crysil.gatekeeperwithsessions.authentication.plugins.oauth.google;

import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.UserBean;
import org.crysil.gatekeeperwithsessions.authentication.plugins.oauth.OAuthPlugin;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Authentication plugin for oAuth Google.
 */
public class GoogleOAuthPlugin extends OAuthPlugin {
    @Override
    public UserBean getUser(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        response = restTemplate.getForObject(getResource_URI() + "?bearer_token=" + accessToken, GoogleOAuthUserBean.class);
        if (response == null) {
            return null;
        } else {
            return response;
        }
    }

    @Override
    public AuthPlugin newInstance() {
        return new GoogleOAuthPlugin();
    }
}
