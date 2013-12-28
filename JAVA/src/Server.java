import gui.Credential;

import java.util.ArrayList;


public class Server {
	private String url;
	private ArrayList<?> supported_authTypes;
	private boolean authenticated;
	private String sessionID;
	
	public boolean authRequest(){
		return false;
	}
	public boolean handleautheresponse(){
		return false;
	}
	// is the server reachable
	public boolean isConnected(){
		return false;
	}
	// is the user authenticated
	public boolean isAutheticated(){
		return authenticated;
	}
	public String toString(){
		return "url: "+url;
	}
}
