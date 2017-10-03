package org.crysil.communications.http;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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

	private static Connection connection;

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

		InputStream in = ElementBuilder.class.getClassLoader().getResourceAsStream("database.properties");
		props.load(in);
		in.close();

		// create database connection
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(props.getProperty("connectionstring"),
				props.getProperty("username"),
				props.getProperty("password"));

		Configuration config = new GateKeeperConfiguration(connection);
		OneToOneInterlink gatekeeper = new Gatekeeper(config);
		gatekeeper.attach(new SoftwareCrypto(new CloudKSKeyStore(connection)));
		return (Module) gatekeeper;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!connection.isClosed())
			connection.close();
		super.finalize();
	}

}
