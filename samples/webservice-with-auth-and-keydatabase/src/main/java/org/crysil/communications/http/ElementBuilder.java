package org.crysil.communications.http;

import org.crysil.actor.staticKeyEncryption.StaticKeyEncryptionActor;
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
		return new StaticKeyEncryptionActor();
	}

}
