package org.crysil.u2f;

public interface U2FCounterStore {

	/**
	 * @return value of the counter
	 */
	int getCounter();

	/**
	 * @return counter after increment
	 */
	int incrementCounter();

}
