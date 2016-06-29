package org.crysil.authentication;

public class AuthException extends Exception {
	private static final long serialVersionUID = 1L;

	
	public AuthException(String message){
		super(message);
	}
	
	public AuthException(Throwable e){
		super(e);
	}
	
	public AuthException(String message, Throwable e){
		super(message, e);
	}
	
}
