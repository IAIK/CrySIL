package org.crysil.errorhandling;

public class InvalidWrappedKeyException extends CrySILException {

	private static final long serialVersionUID = -7809783587692047119L;

	@Override
	public int getErrorCode() {
		return 605;
	}

}
