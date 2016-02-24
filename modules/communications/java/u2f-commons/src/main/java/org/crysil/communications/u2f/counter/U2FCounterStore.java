package org.crysil.communications.u2f.counter;

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
