package org.crysil.communications.http;

import org.crysil.commons.Module;

/**
 * Create your own implementation and name it {@code ConfigurationImpl}.
 */
public interface Configuration {

	/**
	 * Returns the attached module. Might be a router or an actor or something else.
	 * 
	 * @return the attached module.
	 */
	public Module getAttachedModule();

	/**
	 * knows whether the schema should be validated or not
	 * 
	 * @return if the JSON schema is to be validated
	 */
	public boolean isValidateSchema();
}
