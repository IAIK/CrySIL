package org.crysil.errorhandling;

public class NotAcceptableException extends CrySILException {

	private static final long serialVersionUID = 1145982602675124269L;

	@Override
	public int getErrorCode() {
		return 406;
	}

}
