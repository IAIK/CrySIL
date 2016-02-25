package org.crysil.protocol.header;

public class U2FHeader extends StandardHeader {

	protected int counter;

	@Override
	public String getType() {
		return "u2fHeader";
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}
