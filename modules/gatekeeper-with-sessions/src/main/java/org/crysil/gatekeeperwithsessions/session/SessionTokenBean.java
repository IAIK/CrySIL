package org.crysil.gatekeeperwithsessions.session;

import java.util.*;

import org.crysil.gatekeeperwithsessions.configuration.AuthenticationPeriod;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.gatekeeperwithsessions.configuration.FeatureSet;

public class SessionTokenBean {
    private String sessionId;
    private Map<FeatureSet, AuthenticationPeriod> featureSets = new HashMap<>();

    public SessionTokenBean() {
        sessionId = UUID.randomUUID().toString();
    }

    public void addEntry(FeatureSet features, AuthenticationPeriod period) {
        featureSets.put(features, period);
    }

    public boolean checkFeatureSet(FeatureSet features) {
        AuthenticationPeriod match = featureSets.get(features);
        if (null != match)
            return match.valid();
        else
            return false;
    }

    public String getSessionID() {
        return sessionId;
    }

    public boolean isExpired() {
        List<FeatureSet> remove = new ArrayList<>();
        for (FeatureSet set : featureSets.keySet()) {
            if (!featureSets.get(set).valid()) {
                remove.add(set);
            }
        }

        for (FeatureSet current : remove) {
            featureSets.remove(current);
        }

        return featureSets.isEmpty();
    }

    /**
     * Gets the features.
     *
     * @return the features
     */
    public List<Feature> getFeatures() {
        List<Feature> result = new ArrayList<Feature>();
        for (FeatureSet current : featureSets.keySet())
            result.addAll(current.getFeatures());
        return result;
    }
}
