package gui;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthType;

public class AuthTypeFactory {
	public static Authenticator getAuthObj(String type){
		return new UserName();
	}
	public static Authenticator getAuthObj(SAuthType type){
		return new UserName();
	}
}
