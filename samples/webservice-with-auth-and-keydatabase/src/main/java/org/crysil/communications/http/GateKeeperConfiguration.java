package org.crysil.communications.http;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.AuthorizationProcess;
import org.crysil.gatekeeperwithsessions.Configuration;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.IdentifierAuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.credentials.SecretAuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.misc.NoAuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.CountLimit;
import org.crysil.gatekeeperwithsessions.configuration.FeatureSet;
import org.crysil.gatekeeperwithsessions.configuration.Operation;
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
		AuthPlugin plugin = null;

		if (features.containKey("Operation")
				&& ((Operation) features.get("Operation")).getOperation().equals("discoverKeys"))
			plugin = new IdentifierAuthPlugin();
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

					// assemble plugins
					if (auth.contains("PIN")) {
						plugin = new SecretAuthPlugin();
						plugin.setExpectedValue(auth.substring(auth.indexOf("\"secret\":") + 11,
								auth.indexOf("\",", auth.indexOf("\"secret\":") + 11)));
					} else
						plugin = new NoAuthPlugin();

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
		if (null == plugin)
			throw new AuthenticationFailedException();

		// return the complete auth process
		return new AuthorizationProcess(new CountLimit(5), new AuthPlugin[] { plugin });
	}

}
