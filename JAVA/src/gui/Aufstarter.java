package gui;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Aufstarter {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Credentials> list = DataVaultSingleton.getInstance().getCredentials();
		for(Credentials tmp: list){
			System.out.println(tmp);
		}


	}
	


}
