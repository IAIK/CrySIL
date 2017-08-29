package org.crysil.gatekeeperwithsessions.authentication.plugins.oauth.handysignatur;

import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.UserBean;
import org.crysil.gatekeeperwithsessions.authentication.plugins.oauth.OAuthPlugin;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class HandySigOAuthPlugin extends OAuthPlugin {
    @Override
    public UserBean getUser(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        response = restTemplate.postForObject(getResource_URI(), getResourceParams(accessToken), HandySigOAuthUserBean.class);
        if (response == null) {
            return null;
        } else {
            return response;
        }
    }

    @Override
    public AuthPlugin newInstance() {
        return new HandySigOAuthPlugin();
    }

}
