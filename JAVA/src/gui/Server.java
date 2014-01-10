package gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;


public class Server implements Serializable{
	public class ServerInfo{
		public String url;		
	}
	private ServerInfo info;
	private SAuthInfo credentials = null;
	
	// is the server reachable
	public boolean isConnected(){
		return false;
	}
	// is the user authenticated
	public boolean isAutheticated(){
		return credentials != null;
	}
	public String toString(){
		return "url: "+info.url;
	}
}
