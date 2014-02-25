package gui;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;

public class UserName implements Authenticator{
	
	@Override
	public SAuthInfo askUserForSecret() {
		// TODO Auto-generated method stub
		return new at.iaik.skytrust.element.skytrustprotocol.payload.auth.credentials.SUserPasswordAuthInfo();
	}
}
