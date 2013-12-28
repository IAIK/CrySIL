import gui.Credential;

import java.io.Serializable;
import java.util.ArrayList;


public class Server implements Serializable{
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
		return false;
	}
	public String toString(){
		return "url: "+url;
	}
}
