package configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.credentials.SUserPasswordAuthInfo;
import configuration.Server.ServerInfo;

public class DataVaultSingleton {

	private static DataVaultSingleton _instance;

	private ArrayList<Server> servers = new ArrayList<>();

	public static DataVaultSingleton getInstance() {
		if (_instance == null) {
			_instance = new DataVaultSingleton();
		} else {

		}
		return _instance;
	}

	private DataVaultSingleton() {
		readConfig();
	}

	private void readConfig() {
		L.log("reading info!", 1);
		String tokenname = "default";
		String serverurl = "";
		String username = "";
		String password = "";
		Server server;

		InputStream input = getClass().getResourceAsStream("/config");
		BufferedReader breader = new BufferedReader(
				new InputStreamReader(input));
		try {
			String wholeline = breader.readLine().trim();
			while (wholeline != null) {
				if (wholeline.compareTo("") == 0) {
					// empty line... new server!
					L.log("DataVAultSingleton.java: found server in configfile", 0);
					server = new Server(serverurl, tokenname);
					SUserPasswordAuthInfo cre = new SUserPasswordAuthInfo();
					cre.setUserName(username);
					cre.setPassWord(password);
					server.setCredentials(cre);
					servers.add(server);
					tokenname ="default";
					serverurl = null;

				}
				String[] line = wholeline.split("=");
				if (line.length == 1) {
					wholeline = breader.readLine();
					continue;
				}

				switch (line[0]) {
				case "tokenname":
					tokenname = line[1];
					L.log("DataVaultSingleton.java: tokenname: " + tokenname, 1);
					break;
				case "serverurl":
					serverurl = line[1];
					L.log("DataVAultSingleton.java: serverurl: " + serverurl,1);
					break;
				case "username":
					username = line[1];
					L.log("DataVAultSingleton.java: username: " + username,1);
					break;
				case "password":
					password = line[1];
					L.log("DataVAultSingleton.java: password: " + password,1);
					break;
				}
				wholeline = breader.readLine();
			}
			if(serverurl!=null){
					server = new Server(serverurl, tokenname);
					L.log("DataVAultSingleton.java: found server in configfile", 0);
					SUserPasswordAuthInfo cre = new SUserPasswordAuthInfo();
					cre.setUserName(username);
					cre.setPassWord(password);
					server.setCredentials(cre);
					servers.add(server);
					tokenname ="default";
					serverurl = null;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<ServerInfo> getServerInfoList() {
		L.log("DataVaultSingleton.java: getServerInfoList", 3);
		ArrayList<Server.ServerInfo> res = new ArrayList<ServerInfo>();
		for (Server s : servers) {
			res.add(s.getInfo());
		}
		return res;
	}

}
