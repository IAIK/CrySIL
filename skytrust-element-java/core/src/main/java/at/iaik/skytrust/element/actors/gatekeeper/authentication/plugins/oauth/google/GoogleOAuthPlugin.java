package at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.oauth.google;

import at.iaik.skytrust.element.actors.gatekeeper.AuthenticationFailedException;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin;
import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.oauth.OAuthPlugin;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthRequest;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SPayloadAuthResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth.SGoogleOAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.oauth.SGoogleOAuthType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Authentication plugin for oAuth Google.
 */
public class GoogleOAuthPlugin extends OAuthPlugin {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin#generateAuthChallenge(at.iaik.skytrust.element.skytrustprotocol.SResponse)
	 */
    @Override
	public SResponse generateAuthChallenge(SResponse response) {
		SGoogleOAuthType googleOAuthType = new SGoogleOAuthType();

		SPayloadAuthResponse authResponse = new SPayloadAuthResponse();
		authResponse.setAuthType(googleOAuthType);
		response.setPayload(authResponse);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin#getReceivedIdentifier(at.iaik.skytrust.element.skytrustprotocol.payload
	 * .auth.SPayloadAuthRequest)
	 */
	@Override
	public String getReceivedIdentifier(SPayloadAuthRequest authRequest) throws AuthenticationFailedException {
		try {
			SAuthInfo authInfo = authRequest.getAuthInfo();
            SGoogleOAuthInfo googleOAuthInfo = (SGoogleOAuthInfo) authInfo;
            String token = googleOAuthInfo.getAccessToken();

            String url = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + token;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());


            GoogleOAuthUserBean userBean = restTemplate.getForObject(url, GoogleOAuthUserBean.class);
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
		return new GoogleOAuthPlugin();
	}
}
