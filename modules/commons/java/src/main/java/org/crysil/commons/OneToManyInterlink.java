package org.crysil.commons;

import java.util.List;

/**
 * The 1:n Interlink implementation allows exactly multiple outgoing paths.
 */
public class OneToManyInterlink implements Interlink {

	/**
	 * The list of modules. This list is only accessible to implementing classes by its getter.
	 */
	private List<Module> modules;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.crysil.commons.Interlink#attach(org.crysil.commons.Module)
	 */
	@Override
	public void attach(Module module) {
		modules.add(module);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.crysil.commons.Interlink#detach(org.crysil.commons.Module)
	 */
	@Override
	public void detach(Module module) {
		modules.remove(module);
	}

	/**
	 * Get all attached modules.
	 * 
	 * @return the modules
	 */
	public List<Module> getAttachedModules() {
		return modules;
	}
}
