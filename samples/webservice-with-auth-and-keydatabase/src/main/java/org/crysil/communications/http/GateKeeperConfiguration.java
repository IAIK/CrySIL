package org.crysil.communications.http;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.AuthorizationProcess;
import org.crysil.gatekeeperwithsessions.Configuration;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.SecretAuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.CountLimit;
import org.crysil.gatekeeperwithsessions.configuration.FeatureSet;

public class GateKeeperConfiguration implements Configuration {

	@Override
	public AuthorizationProcess getAuthorizationProcess(FeatureSet features) throws AuthenticationFailedException {
		AuthPlugin plugin = null;
		// create database connection
		try {
			// create our mysql database connection
			String myDriver = "com.mysql.jdbc.Driver";
			String myUrl = "jdbc:mysql://localhost/cloudks_dev";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "cloudks", "cloudkspassword");

			// find appropriate auth information
			String query = "SELECT * FROM keyslots";

			// create the java statement
			Statement st = conn.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(query);

			// iterate through the java resultset
			while (rs.next()) {
				String id = rs.getString("name");
				String auth = rs.getString("auth");

				// print the results
				System.out.format("%s: %s\n", id, auth);
				
				// assemble plugins
				if(auth.contains("PIN")) {
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

		// return the complete auth process
		return new AuthorizationProcess(new CountLimit(5), new AuthPlugin[] { plugin });
	}

}
