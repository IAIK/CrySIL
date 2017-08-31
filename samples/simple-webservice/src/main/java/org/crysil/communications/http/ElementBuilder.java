package org.crysil.communications.http;

import org.crysil.actor.softwarecrypto.SimpleKeyStore;
import org.crysil.actor.softwarecrypto.SoftwareCrypto;
import org.crysil.commons.Module;
import org.crysil.errorhandling.KeyStoreUnavailableException;

/**
 * exemplary element builder without the need for specifying the whole CrySIL node with xml
 */
public class ElementBuilder {

	/**
	 * creates the rest of the CrySIL node
	 *
	 * @return the entry module to the CrySIL node
	 * @throws KeyStoreUnavailableException 
	 */
	public static Module build() throws KeyStoreUnavailableException {
		return new SoftwareCrypto(new SimpleKeyStore());
	}

}
