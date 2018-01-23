package org.crysil.communications.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crysil.gatekeeperwithsessions.configuration.Feature;
import org.crysil.logging.Logger;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.header.SessionHeader;

public class FeatureSetHeader extends SessionHeader {

	private static final long serialVersionUID = -7658756826292193839L;
	private List<Feature> features = new ArrayList<>();

	@Override
	public String getType() {
		return "attributeHeader";
	}

	public void setFeatureSet(List<Feature> features) {
		this.features = features;
	}

	public Feature getFeature(String name) {
		for (Feature currentFeature : features) {
			if (name.equals(currentFeature.getKey()))
				return currentFeature;
		}
		return null;
	}

	@Override
	public Header getBlankedClone() {
		final FeatureSetHeader result = new FeatureSetHeader();
		result.commandId = Logger.isInfoEnabled() ? commandId : "*****";
		result.sessionId = Logger.isDebugEnabled() ? sessionId : "*****";
		return result;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] { type, commandId, sessionId });
	}

	@Override
	public Header clone() {
		final FeatureSetHeader h = new FeatureSetHeader();
		h.setCommandId(getCommandId() == null ? null : new String(getCommandId()));
		h.requestPath.addAll(getRequestPath());
		h.responsePath.addAll(getResponsePath());
		h.setSessionId(getSessionId() == null ? null : new String(getSessionId()));
		return h;
	}
}
