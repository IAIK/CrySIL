package configuration;

import org.crysil.protocol.payload.auth.AuthInfo;

public class Server{

	private ServerInfo info;
	private AuthInfo credentials = null;
	
	public class ServerInfo{
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
	public Server(String url,String name){
		info = new ServerInfo(url,name);
	}

	public void setCredentials(AuthInfo cre) {
		credentials = cre;
	}

	public AuthInfo getCredentials() {
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
	@Override
	public String toString(){
		return "url: "+info.url;
	}
}
