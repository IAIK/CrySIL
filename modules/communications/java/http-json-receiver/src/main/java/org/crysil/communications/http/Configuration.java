package org.crysil.communications.http;

import org.crysil.commons.Module;

/**
 * The configuration bean to be filled by spring.
 */
public class Configuration {

	private Module attachedModule;
	private boolean isValidateSchema;

	/**
	 * Returns the attached module. Might be a router or an actor or something else.
	 * 
	 * @return the attached module.
	 */
	public Module getAttachedModule() {
		return attachedModule;
	}

	/**
	 * Setter for the attached CrySIL node.
	 * 
	 * @param newModule the entry module to the CrySIL node.
	 */
	public void setAttachedModule(Module newModule) {
		attachedModule = newModule;
	}

	/**
	 * knows whether the JSON data should be validated against the schema or not
	 * 
	 * @return if the JSON schema is to be validated
	 */
	public boolean isValidateSchema() {
		return isValidateSchema;
	}

	/**
	 * sets whether the JSON data should be validated against the schema or not
	 * 
	 * @param isValidateSchema true if validation should be performed
	 */
	public void setValidateSchema(boolean isValidateSchema) {
		this.isValidateSchema = isValidateSchema;
	}
}
