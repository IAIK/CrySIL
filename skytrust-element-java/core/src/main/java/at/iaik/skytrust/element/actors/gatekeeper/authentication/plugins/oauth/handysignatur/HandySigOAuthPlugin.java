package at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.oauth.handysignatur;

import at.iaik.skytrust.element.actors.gatekeeper.AuthenticationFailedException;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.oauth.OAuthPlugin;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth.SHandySigOAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth.SHandySigOAuthType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;


/**
 * Authentication plugin for oAuth Handysignatur.
 */
public class HandySigOAuthPlugin extends OAuthPlugin {

	private static String clientID; // = "61b642f1d1699303c53aa4ad4481c0";
	private static String clientSecret; // = "41899de393eaba59dc13f7aec9b73d";
	private static String identityLink_URI; // = "https://sso-service.appspot.com/resources/identity_link/?";

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

	public String getIdentityLink_URI() {
		return identityLink_URI;
	}

	public void setIdentityLink_URI(String identityLink_URI) {
		this.identityLink_URI = identityLink_URI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.iaik.skytrust.element.actors.gatekeeper.authentication.IAuthPlugin#generateAuthChallenge(at.iaik.skytrust.element.skytrustprotocol.SResponse
	 * )
	 */
    public SResponse generateAuthChallenge(SResponse response) {
        SHandySigOAuthType handySigOAuthType = new SHandySigOAuthType();

        SPayloadAuthResponse authResponse = new SPayloadAuthResponse();
        authResponse.setAuthType(handySigOAuthType);
        response.setPayload(authResponse);
        return response;
    }
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.iaik.skytrust.element.actors.gatekeeper.authentication.IAuthPlugin#getReceivedIdentifier(at.iaik.skytrust.element.skytrustprotocol.payload
	 * .auth.SPayloadAuthRequest)
	 */
	public String getReceivedIdentifier(SPayloadAuthRequest authRequest) throws AuthenticationFailedException {
		try {
			// check authRequest format
			SHandySigOAuthInfo handySigOAuthType = (SHandySigOAuthInfo) authRequest.getAuthInfo();
			String token = handySigOAuthType.getAccessToken();

			String userInfoParams = "bearer_token=" + token + "&client_id=" + clientID + "&client_secret=" + clientSecret;
			String url = identityLink_URI + "?" + userInfoParams;

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

			HandySigOAuthUserBean userBean = restTemplate.getForObject(url, HandySigOAuthUserBean.class);

			return userBean.getUserId();
		} catch (Exception e) {
			throw new AuthenticationFailedException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin#newInstance()
	 */
	@Override
	public AuthPlugin newInstance() {
		return new HandySigOAuthPlugin();
	}
}
