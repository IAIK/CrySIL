package org.crysil.commons;

/**
 * The {@link Module} interface enables basic communication between implementing
 * blocks at runtime.
 *
 */
public interface Module {

	/**
	 * Takes a CrySIL protocol package.
	 * 
	 * @param object
	 */
	public void take(Object object);
}
