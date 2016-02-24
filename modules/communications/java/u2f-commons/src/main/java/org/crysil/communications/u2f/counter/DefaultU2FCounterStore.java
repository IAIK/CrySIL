package org.crysil.communications.u2f.counter;

public class DefaultU2FCounterStore implements U2FCounterStore {

	private int counter = 1;

	@Override
	public int getCounter() {
		return counter;
	}

	@Override
	public int incrementCounter() {
		return ++counter;
	}

}
