package org.crysil.communications.http;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.AuthorizationProcess;
import org.crysil.gatekeeperwithsessions.Configuration;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.IdentifierAuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.SecretAuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.CountLimit;
import org.crysil.gatekeeperwithsessions.configuration.FeatureSet;
import org.crysil.gatekeeperwithsessions.configuration.Operation;

public class GateKeeperConfiguration implements Configuration {

	private Connection conn;

	public GateKeeperConfiguration() throws ClassNotFoundException, SQLException {
		// create database connection
		String myDriver = "com.mysql.jdbc.Driver";
		String myUrl = "jdbc:mysql://localhost/cloudks_dev";
		Class.forName(myDriver);
		conn = DriverManager.getConnection(myUrl, "cloudks", "cloudkspassword");
	}

	@Override
	public AuthorizationProcess getAuthorizationProcess(FeatureSet features) throws AuthenticationFailedException {
		AuthPlugin plugin = null;

		if (features.containKey("Operation")
				&& ((Operation) features.get("Operation")).getOperation().equals("discoverKeys"))
			plugin = new IdentifierAuthPlugin();
		else {
			try {
				// find appropriate auth information
				String query = "SELECT * FROM keyslots";

				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);

				// iterate through the java resultset
				while (rs.next()) {
					String id = rs.getString("name");
					String auth = rs.getString("auth");

					// print the results
					System.out.format("%s: %s\n", id, auth);

					// assemble plugins
					if (auth.contains("PIN")) {
						plugin = new SecretAuthPlugin();
						plugin.setExpectedValue(auth.substring(auth.indexOf("\"secret\":") + 11,
								auth.indexOf("\",", auth.indexOf("\"secret\":") + 11)));
					}

						}
				st.close();
			} catch (Exception e) {
				System.err.println("Got an exception! ");
				System.err.println(e.getMessage());
					}
				}

		// return the complete auth process
		return new AuthorizationProcess(new CountLimit(5), new AuthPlugin[] { plugin });
	}

}
