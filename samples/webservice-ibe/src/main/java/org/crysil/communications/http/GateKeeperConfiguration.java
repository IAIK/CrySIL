package org.crysil.communications.http;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.crysil.errorhandling.AuthenticationFailedException;
import org.crysil.gatekeeperwithsessions.AuthorizationProcess;
import org.crysil.gatekeeperwithsessions.Configuration;
import org.crysil.gatekeeperwithsessions.authentication.AuthPlugin;
import org.crysil.gatekeeperwithsessions.authentication.plugins.misc.NoAuthPlugin;
import org.crysil.gatekeeperwithsessions.configuration.FeatureSet;
import org.crysil.gatekeeperwithsessions.configuration.Operation;
import org.crysil.gatekeeperwithsessions.configuration.TimeLimit;

public class GateKeeperConfiguration implements Configuration {

	private String url;
	private String adminuser;
	private String adminpassword;
	private String searchRoot;
	private String searchFilter;
	private String targetAttribute;

	public GateKeeperConfiguration(String url, String adminuser, String adminpassword, String searchRoot,
			String searchFilter, String targetAttribute) {
		this.url = url;
		this.adminuser = adminuser;
		this.adminpassword = adminpassword;
		this.searchRoot = searchRoot;
		this.searchFilter = searchFilter;
		this.targetAttribute = targetAttribute;
	}

	@Override
	public AuthorizationProcess getAuthorizationProcess(FeatureSet features) throws AuthenticationFailedException {
		List<AuthPlugin> plugins = new ArrayList<>();

		String operation = "";
		if (features.containKey("Operation"))
			operation = ((Operation) features.get("Operation")).getOperation();
		try {
			if ("discoverKeys".equals(operation)) {
			} else if ("sign".equals(operation) || "encrypt".equals(operation)) {
				// plugins.add(new ActiveDirectoryAuthPlugin(url,
				// features.get("username")), element);
			} else if ("encrypt".equals(operation)) {
				plugins.add(new NoAuthPlugin());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// trigger an unknown error
		if (plugins.isEmpty())
			throw new AuthenticationFailedException();

		// return the complete auth process
		return new AuthorizationProcess(new TimeLimit(Calendar.MINUTE, 30), plugins);
	}

}
