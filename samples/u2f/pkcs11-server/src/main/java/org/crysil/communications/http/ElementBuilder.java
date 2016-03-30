package org.crysil.communications.http;

import org.crysil.actor.pkcs11.Pkcs11Actor;
import org.crysil.actor.pkcs11.strategy.YubicoRandomKeyHandleStrategy;
import org.crysil.commons.Module;

/**
 * exemplary element builder without the need for specifying the whole CrySIL node with xml
 */
public class ElementBuilder {

	/**
	 * creates the rest of the CrySIL node
	 * 
	 * @return the entry module to the CrySIL node
	 */
	public static Module build() {
		return new Pkcs11Actor(new YubicoRandomKeyHandleStrategy());
	}

}
