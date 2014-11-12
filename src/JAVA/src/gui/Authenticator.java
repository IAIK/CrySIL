package gui;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;

import java.io.Serializable;

public interface Authenticator extends Serializable{

	public SAuthInfo askUserForSecret();
}


