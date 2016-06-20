package org.crysil.authentication;

public class AuthenticationPluginException extends Exception {
	private static final long serialVersionUID = 1L;

	
	public AuthenticationPluginException(String message){
		super(message);
	}
	
	public AuthenticationPluginException(Throwable e){
		super(e);
	}
	
	public AuthenticationPluginException(String message, Throwable e){
		super(message, e);
	}
	
}
