package org.crysil.communications.http;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.crysil.actor.softwarecrypto.SoftwareCrypto;
import org.crysil.commons.Module;
import org.crysil.commons.OneToOneInterlink;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.gatekeeperwithsessions.Configuration;

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
	 * @throws IOException 
	 */
	public static Module build()
			throws ClassNotFoundException, SQLException, KeyStoreUnavailableException, IOException {
		Properties props = new Properties();
		InputStream in = ElementBuilder.class.getClassLoader().getResourceAsStream("settings.properties");
		props.load(in);
		in.close();

		Configuration config = new GateKeeperConfiguration(props.getProperty("url"), props.getProperty("domainPrefix"),
				props.getProperty("adminuser"), props.getProperty("adminpassword"),
				props.getProperty("searchRoot"), props.getProperty("searchFilter"),
				props.getProperty("targetAttribute"));
		OneToOneInterlink gatekeeper = new MyGatekeeper(config);
		try {
			gatekeeper.attach(new SoftwareCrypto(
					new CreateKeyOnDemandFileKeyStore(props.getProperty("keystorefile"),
							props.getProperty("keystorepassword").toCharArray(),
							props.getProperty("capsoconfigfile"))));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (Module) gatekeeper;
	}
}
