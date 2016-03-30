package org.crysil.actor.pkcs11.strategy;

/**
 * Class that represents a tuple of two values with different types
 */
public class Tuple<T, U> {

	public final T first;

	public final U second;

	public Tuple(T first, U second) {
		this.first = first;
		this.second = second;
	}
}