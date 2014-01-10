package at.iaik.skytrust.element.actors.gatekeeper.session;

import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.UserBean;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.AuthenticationPeriod;
import at.iaik.skytrust.element.actors.gatekeeper.configuration.FeatureSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SessionTokenBean {

    private String sessionId;
    private UserBean user;
	private Map<FeatureSet, AuthenticationPeriod> featureSets = new HashMap<>();
	private FeatureSet userFeatureSet;

	public SessionTokenBean() {
		sessionId = UUID.randomUUID().toString();
	}

	public void addEntry(FeatureSet features, AuthenticationPeriod period) {
		featureSets.put(features, period);

		if (features.containsKey("user"))
			userFeatureSet = features;
	}

	public boolean checkFeatureSet(FeatureSet features) {
		return featureSets.get(features).valid();
	}

	public boolean checkUser() {
		return checkFeatureSet(userFeatureSet);
	}

    public UserBean getUser() {
        return user;
    }

	public void setUser(UserBean user) {
		this.user = user;
	}

    public String getSessionID() {
        return sessionId;
    }

    public boolean isExpired() {
		List<FeatureSet> remove = new ArrayList<>();
		for (FeatureSet set : featureSets.keySet())
			if (!featureSets.get(set).valid())
				remove.add(set);

		for (FeatureSet current : remove)
			featureSets.remove(current);

		return featureSets.isEmpty();
    }
}
