package org.crysil.communications.http;

import java.sql.SQLException;

import org.crysil.actor.softwarecrypto.CloudKSKeyStore;
import org.crysil.actor.softwarecrypto.SoftwareCrypto;
import org.crysil.commons.Module;
import org.crysil.commons.OneToOneInterlink;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.gatekeeperwithsessions.Configuration;
import org.crysil.gatekeeperwithsessions.Gatekeeper;

/**
 * exemplary element builder without the need for specifying the whole CrySIL node with xml
 */
public class ElementBuilder {

	/**
	 * creates the rest of the CrySIL node
	 *
	 * @return the entry module to the CrySIL node
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws KeyStoreUnavailableException 
	 */
	public static Module build() throws ClassNotFoundException, SQLException, KeyStoreUnavailableException {
		Configuration config = new GateKeeperConfiguration();
		OneToOneInterlink gatekeeper = new Gatekeeper(config);
		gatekeeper.attach(new SoftwareCrypto(
				new CloudKSKeyStore("jdbc:mysql://localhost/cloudks_dev", "cloudks", "cloudkspassword")));
		return (Module) gatekeeper;
	}

}
