package gui;

import gui.Server;
import gui.Server.ServerInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthType;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.credentials.SUserPasswordAuthInfo;

public class DataVaultSingleton {

	private static DataVaultSingleton _instance;

	private ArrayList<Server> servers;
	private ArrayList<Client> clients;

	public static DataVaultSingleton getInstance() {
		if (_instance == null) {
			_instance = new DataVaultSingleton();
		} else {

		}
		return _instance;
	}

	private DataVaultSingleton() {
		servers = new ArrayList<Server>();
		clients = new ArrayList<Client>();
		Server ser = new Server("http://skytrust-dev.iaik.tugraz.at/skytrust-server/rest/json");
		SUserPasswordAuthInfo cre = new SUserPasswordAuthInfo();
		cre.setUserName("testuser");
		cre.setPassWord("");
		ser.setCredentials(cre);
		servers.add(ser);
	}

	public ArrayList<ServerInfo> getServerInfoList() {
		ArrayList<Server.ServerInfo> res = new ArrayList<ServerInfo>();
		for (Server s : servers) {
			res.add(s.getInfo());
		}
		return res;
	}

	public SAuthInfo askForAuthInfo(SAuthType authType,
			Server.ServerInfo server_info) {
		for (Server s : servers) {
			if (s.getInfo().equals(server_info)) {
				if (s.isAutheticated()) {
					// GUI.askUserIf App:ID isAllowed to connect to server_info
					SAuthInfo cre = s.getCredentials();
					String info_type = cre.getType();
					info_type = info_type.replace("Info", "Type");
					String req_type = authType.getType();
					if (info_type.compareTo(req_type) == 0){
						return cre;
					}
				} else {
					// GUI.askUserFor Credentials for App:ID,server_info
				}
			}
		}
		return null;
	}

	private void doStuff(){
//		Properties configFile = new Properties();
//	    try {
	    	
	    	
//			configFile.load(new FileInputStream("my_config.properties"));
//			SUserPasswordAuthInfo cre = new SUserPasswordAuthInfo();
//			Server ser = new Server(configFile.getProperty("url"));
//			cre.setUserName(configFile.getProperty("username"));
//			cre.setPassWord(configFile.getProperty("password"));
	    	
	    	
			SUserPasswordAuthInfo cre = new SUserPasswordAuthInfo();
			Server ser = new Server("url");
			cre.setUserName("test");
			cre.setPassWord("test");
			ser.setCredentials(cre);
			servers.add(ser);
			
			
			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public void registerClient(Client c) {
		clients.add(c);
	}

}
