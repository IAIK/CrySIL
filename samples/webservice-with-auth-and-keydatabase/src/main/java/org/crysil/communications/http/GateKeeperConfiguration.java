package org.crysil.communications.http;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeper.AuthPlugin;
import org.crysil.gatekeeper.AuthProcess;
import org.crysil.gatekeeper.Configuration;
import org.crysil.gatekeeper.Gatekeeper;
import org.crysil.protocol.Request;

public class GateKeeperConfiguration implements Configuration {

	@Override
	public AuthProcess getAuthProcess(Request request, Gatekeeper gatekeeper) throws AuthenticationFailedException {
		// create database connection
		try {
			// create our mysql database connection
			String myDriver = "com.mysql.jdbc.Driver";
			String myUrl = "jdbc:mysql://localhost/cloudks_dev";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "cloudks", "cloudkspassword");

			// our SQL SELECT query.
			// if you only need a few columns, specify them by name instead of
			// using "*"
			String query = "SELECT * FROM keyslots";

			// create the java statement
			Statement st = conn.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(query);

			// iterate through the java resultset
			while (rs.next()) {
				int id = rs.getInt("id");
				String firstName = rs.getString("name");
				String lastName = rs.getString("description");

				// print the results
				System.out.format("%s, %s, %s\n", id, firstName, lastName);
			}
			st.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}
		// find appropriate auth information
		// assemble plugins
		// order plugins
		// return the complete auth process
		return new AuthProcess(request, new AuthPlugin[] {});
	}

}
