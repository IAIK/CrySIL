package gui;
import gui.Server;
import gui.Server.ServerInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthType;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword.SUserPasswordAuthInfo;

public class DataVaultSingleton {
	
	private static DataVaultSingleton _instance;
	
	private ArrayList<Server> servers;
	private ArrayList<Client> clients;
	
	public static DataVaultSingleton getInstance(){
		if(_instance==null){
			_instance=new DataVaultSingleton();
		}else{
			
		}
		return _instance;
	}
	
	private DataVaultSingleton(){
		servers = new ArrayList<Server>();
		clients = new ArrayList<Client>();
		Server ser = new Server("http://test.com");
		servers.add(ser);
	}
	
	public ArrayList<ServerInfo> getServerInfoList(){
		ArrayList<Server.ServerInfo> res = new ArrayList<ServerInfo>();
		for(Server s:servers){
			res.add(s.getInfo());
		} 
		return res;
	}
	public SAuthInfo askForAuthInfo(SAuthType authType,Server.ServerInfo server_info){
		for(Server s:servers){
			if(s.getInfo().equals(server_info)){
				if(s.isAutheticated()){
					//GUI.askUserIf App:ID isAllowed to connect to server_info
					SAuthInfo cre = s.getCredentials();
					if(cre.getType().equals(authType.getType())){
						return cre;
					}
				}else{
					//GUI.askUserFor Credentials for App:ID,server_info
				}
			}
		}
		return null;
	}


	public void registerClient(Client c) {
		clients.add(c);
	}

}
