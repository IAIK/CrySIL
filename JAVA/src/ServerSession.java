import gui.Server;

import java.util.List;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;

/*
 * Stellt verbindung zum Server dar ist für kommunikation zuständig
 * ist für authentifizierung über Authenticator Plugins zuständig
 * könnte caching übernehmen
 * */
public class ServerSession {
	
	private String sessionID;
	private Server.ServerInfo server;
	
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
	public void handleAuth(){
		//auth response
		// send possible AuthTypes to GUI
		// get SAuthInfo back
		//build and send authrequest
	}
}
