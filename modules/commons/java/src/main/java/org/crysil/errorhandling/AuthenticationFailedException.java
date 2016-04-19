package org.crysil.errorhandling;

public class AuthenticationFailedException extends CrySILException {

	private static final long serialVersionUID = 2367809819693717009L;

	@Override
	public int getErrorCode() {
		return 609;
	}

}
