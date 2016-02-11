package org.crysil.commons;

/**
 * An interlink building block can forward CrySIL protocol packages to other
 * building blocks.
 *
 */
public interface Interlink {

	/**
	 * The implementing building block can use the {@link Module} interface of
	 * an attached module to forward commands.
	 * 
	 * @param module
	 */
	void attach(Module module);

	/**
	 * Detaches already attached {@link Module}-building blocks.
	 * 
	 * @param module
	 */
	void detach(Module module);
}