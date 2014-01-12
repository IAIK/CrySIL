import gui.DataVaultSingleton;
import gui.Server;

import java.util.List;

import at.iaik.skytrust.element.skytrustprotocol.SResponse;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/*
 * Stellt verbindung zum Server dar ist für kommunikation zuständig
 * ist für authentifizierung über Authenticator Plugins zuständig
 * könnte caching übernehmen
 * */
public class ServerSession {
	
	private String sessionID;
	private Server.ServerInfo server;
    protected RestTemplate restTemplate;
    
	private SAuthInfo credentials;
	private boolean rememberCredentialsForSession = true;
	
	public ServerSession(Server.ServerInfo s){
		server = s;
	}
	public List<SKey> getKeyList(){
		return null;
	}
	public void sign(SKey key){
		
	}
	public void verify(){
		
	}
	public void encrypt(){
		
	}
	public SResponse handleAuth(SResponse skyTrustResponse){
		//auth response
		// send possible AuthTypes to GUI
		// get SAuthInfo back
		//build and send authrequest
        SPayloadAuthResponse authResponse = (SPayloadAuthResponse)skyTrustResponse.getPayload();
        SAuthType authType = authResponse.getAuthType();
        
        SAuthInfo credentials = DataVaultSingleton.getInstance().askForAuthInfo(authType,server);
        
        SRequest authRequest = createBasicRequest();
        SPayloadAuthRequest authRequestPayload = new SPayloadAuthRequest();
        authRequestPayload.setAuthInfo(credentials);
        authRequestPayload.setCommand("authenticate");
        authRequest.setPayload(authRequestPayload);
        authRequest.getHeader().setCommandId(skyTrustResponse.getHeader().getCommandId());

        skyTrustResponse = restTemplate.postForObject(server,authRequest,SResponse.class);
        sessionID=skyTrustResponse.getHeader().getSessionId();
        return skyTrustResponse;
	}
}
