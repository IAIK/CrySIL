package gui;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthType;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.credentials.SUserPasswordAuthInfo;
import gui.Server.ServerInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
		System.out.println("reading info!");
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
				System.out.println("current line: " + wholeline);
				if (wholeline.compareTo("") == 0) {
					// empty line... new server!
					System.out.println("initiating serverclass");
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
					System.out.println("read tokenname: " + tokenname);
					break;
				case "serverurl":
					serverurl = line[1];
					System.out.println("read serverurl: " + serverurl);
					break;
				case "username":
					username = line[1];
					break;
				case "password":
					password = line[1];
					break;
				}
				wholeline = breader.readLine();
			}
			if(serverurl!=null){
					server = new Server(serverurl, tokenname);
					SUserPasswordAuthInfo cre = new SUserPasswordAuthInfo();
					cre.setUserName(username);
					cre.setPassWord(password);
					server.setCredentials(cre);
					servers.add(server);
					tokenname ="default";
					serverurl = null;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ArrayList<ServerInfo> getServerInfoList() {
		ArrayList<Server.ServerInfo> res = new ArrayList<ServerInfo>();
		for (Server s : servers) {
			res.add(s.getInfo());
		}
		return res;
	}

}
