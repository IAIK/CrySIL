package gui;

import java.io.Serializable;
import java.util.ArrayList;

import at.iaik.skytrust.element.skytrustprotocol.payload.auth.SAuthInfo;

public interface Authenticator extends Serializable{

	public SAuthInfo askUserForSecret();
}


