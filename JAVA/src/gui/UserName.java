package gui;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;
import at.iaik.skytrust.element.skytrustprotocol.payload.auth.userpassword.SUserPasswordAuthInfo;

public class UserName implements Authenticator{
	
	@Override
	public SAuthInfo askUserForSecret() {
		// TODO Auto-generated method stub
		return new SUserPasswordAuthInfo();
	}
}
