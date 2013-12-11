package gui;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class DataVaultSingleton {
	
	private static DataVaultSingleton _instance;
	
	private ArrayList<Credentials> credentials;
	
	
	
	public static DataVaultSingleton getInstance(){
		if(_instance==null){
			_instance=new DataVaultSingleton();
		}else{
			
		}
		return _instance;
		
		
		
	}
	
	private DataVaultSingleton(){
		
	}
	
	
	public ArrayList<Credentials> getCredentials(){
		
		if(credentials==null){
			credentials=new ArrayList<Credentials>();
			doStuff();
		}
		return credentials;
	}
	
	private void doStuff(){
		
		Properties configFile = new Properties();
	    try {
			configFile.load(new FileInputStream("/home/faxxe/pkcs11/pkcs11_private/my_config.properties"));
			Credentials cre = new Credentials();
			cre.url= configFile.getProperty("url");
			cre.username= configFile.getProperty("username");
			cre.password= configFile.getProperty("password");
			credentials.add(cre);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
