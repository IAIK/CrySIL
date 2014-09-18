package gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;


public class Server implements Serializable{

	private ServerInfo info;
	private SAuthInfo credentials = null;
	
	public class ServerInfo{
		ServerInfo(String url){
			this.url = url;
			this.name = "http://skytrust-dev.iaik.tugraz.at/skytrust-server-no-auth/rest/json";
		}
		ServerInfo(String url,String name){
			this.url = url;
			this.name = name;
		}
		private String url;
		private String name;
		public String getName(){
			return name;
		}
		public String getUrl(){
			return url;
		}
	}
	public Server(String url){
		info = new ServerInfo(url);
	}
	public Server(String url,String name){
		info = new ServerInfo(url,name);
	}
	public void setCredentials(SAuthInfo cre){
		credentials = cre;
	}
	public SAuthInfo getCredentials(){
		return credentials;
	}
	public ServerInfo getInfo(){
		return info;
	}
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
