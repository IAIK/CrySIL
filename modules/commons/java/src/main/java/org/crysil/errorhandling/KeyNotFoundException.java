package org.crysil.errorhandling;

public class KeyNotFoundException extends CrySILException {

	private static final long serialVersionUID = -634157662420631543L;

	@Override
	public int getErrorCode() {
		return 607;
	}

}
