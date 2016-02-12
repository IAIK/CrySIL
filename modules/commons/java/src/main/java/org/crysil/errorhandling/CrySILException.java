package org.crysil.errorhandling;

public abstract class CrySILException extends Exception {

	private static final long serialVersionUID = -1129344166754694958L;

	public abstract int getErrorCode();

}
