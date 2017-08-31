package org.crysil.communications.http;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.AuthorizationProcess;
import org.crysil.gatekeeperwithsessions.Configuration;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.IdentifierAuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.SecretAuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.UsernamePasswordAuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.FeatureSet;
import org.crysil.gatekeeperwithsessions.configuration.Operation;
import org.crysil.gatekeeperwithsessions.configuration.TimeLimit;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

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
		List<AuthPlugin> plugins = new ArrayList<>();

		if (features.containKey("Operation")
				&& ((Operation) features.get("Operation")).getOperation().equals("discoverKeys"))
			plugins.add(new IdentifierAuthPlugin());
		else if (features.containKey("Operation")
				&& ((Operation) features.get("Operation")).getOperation().equals("encrypt")) {
			PreparedStatement st = null;
			try {
				// find appropriate auth information
				KeyHandle key = (KeyHandle) ((org.crysil.gatekeeperwithsessions.configuration.Key) features.get("Key"))
						.getKeyObject();

				st = conn.prepareStatement(
						"SELECT keyslots.auth FROM keyslots INNER JOIN users ON keyslots.user_id=users.id WHERE users.username=? AND keyslots.name=?");
				st.setString(1, key.getId());
				st.setString(2, key.getSubId());

				ResultSet rs = st.executeQuery();

				// iterate through the java resultset
				while (rs.next()) {
					String auth = rs.getString("auth");

					// iterate through the auth json
					while (auth.contains("\"type\": ")) {
						// extract authblob with id
						int idlocation = auth.indexOf("\"type\": ");
						String authblob = auth.substring(auth.lastIndexOf("{", idlocation),
								auth.indexOf("}", idlocation + 1));

						// assemble plugins
						AuthPlugin plugin;
						if (authblob.contains("PIN")) {
							plugin = new SecretAuthPlugin();
							plugin.setExpectedValue(authblob.substring(authblob.indexOf("\"secret\":") + 11,
									authblob.indexOf("\",", authblob.indexOf("\"secret\":") + 11)));
						} else if (authblob.contains("PASSWORD")) {
							plugin = new UsernamePasswordAuthPlugin();
							String expectedValue = authblob.substring(authblob.indexOf("\"username\":") + 13,
									authblob.indexOf("\",", authblob.indexOf("\"username\":") + 13));
							expectedValue += authblob.substring(authblob.indexOf("\"secret\":") + 11,
									authblob.indexOf("\",", authblob.indexOf("\"secret\":") + 11));
							plugin.setExpectedValue(expectedValue);
						} else
							// if we do not know how to handle a given auth
							// method
							throw new AuthenticationFailedException();

						plugins.add(plugin);
						auth = auth.replace(authblob, "");
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != st)
					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}

		// trigger an unknown error
		if (plugins.isEmpty())
			throw new AuthenticationFailedException();

		// return the complete auth process
		return new AuthorizationProcess(new TimeLimit(Calendar.MINUTE, 30), plugins);
	}

}
