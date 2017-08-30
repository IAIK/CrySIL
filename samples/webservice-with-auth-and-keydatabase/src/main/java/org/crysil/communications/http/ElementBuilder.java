package org.crysil.communications.http;

import java.sql.SQLException;

import org.crysil.actor.staticKeyEncryption.StaticKeyEncryptionActor;
import org.crysil.commons.Module;
import org.crysil.commons.OneToOneInterlink;
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
	 */
	public static Module build() throws ClassNotFoundException, SQLException {
		Configuration config = new GateKeeperConfiguration();
		OneToOneInterlink gatekeeper = new Gatekeeper(config);
		gatekeeper.attach(new StaticKeyEncryptionActor());
		return (Module) gatekeeper;
	}

}
