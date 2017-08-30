package org.crysil.gatekeeperwithsessions;

import java.util.List;

import org.crysil.gatekeeperwithsessions.configuration.AuthenticationPeriod;
import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.protocol.Request;

/**
 * Holds the result of the gatekeeper processes.
 */
public class Result {

    /** The request. */
    private final Request request;

    /** The features. */
    private final List<Feature> features;

    private AuthenticationPeriod period;

    /**
     * Instantiates a new result.
     *
     * @param originalRequest
     *            the original request
     * @param features
     *            the features
     * @param period
     */
    Result(Request originalRequest, List<Feature> features) {
        request = originalRequest;
        this.features = features;
    }

    public Result(Request originalRequest, List<Feature> featureSet, AuthenticationPeriod period) {
        request = originalRequest;
        this.features = featureSet;
        this.period = period;
    }

    /**
     * Gets the original request.
     *
     * @return the original request
     */
    public Request getOriginalRequest() {
        return request;
    }

    /**
     * Gets the session features.
     *
     * @return the session features
     */
    public List<Feature> getSessionFeatures() {
        return features;
    }

    /**
     * Gets the period.
     *
     * @return the period
     */
    public AuthenticationPeriod getPeriod() {
        return period;
    }
}
