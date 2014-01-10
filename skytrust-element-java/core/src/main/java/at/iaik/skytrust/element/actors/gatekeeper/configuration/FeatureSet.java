package at.iaik.skytrust.element.actors.gatekeeper.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * holds a set of features used to decide upon authorization.
 */
public class FeatureSet {

	/** The features. */
	List<Feature> features = new ArrayList<>();

	/**
	 * Instantiates a new feature set.
	 * 
	 * @param feature
	 *            the feature
	 */
	public FeatureSet(Feature... feature) {
		features.addAll(Arrays.asList(feature));
	}

	/**
	 * checks for a certain key
	 * 
	 * @param string
	 *            the string
	 * @return true, if yes
	 */
	public boolean containsKey(String string) {
		for (Feature current : features)
			if (current.getKey().equals(string))
				return true;
		return false;
	}

	public Feature get(String string) {
		for (Feature current : features)
			if (current.getKey().equals(string))
				return current;
		return null;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((features == null) ? 0 : features.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeatureSet other = (FeatureSet) obj;
		if (features == null) {
			if (other.features != null)
				return false;
		} else if (!features.equals(other.features))
			return false;
		return true;
	}
}
