package org.crysil.commons;

/**
 * The 1:1 Interlink implementation allows exactly one outgoing path.
 */
public class OneToOneInterlink implements Interlink {

	/**
	 * The module to pass commands on to.
	 */
	private Module module;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.crysil.commons.Interlink#attach(org.crysil.commons.Module)
	 */
	@Override
	public void attach(Module module) {
		this.module = module;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.crysil.commons.Interlink#detach(org.crysil.commons.Module)
	 */
	@Override
	public void detach(Module module) {
		module = null;
	}

	/**
	 * Get the configured next module.
	 * 
	 * @return module
	 */
	public Module getAttachedModule() {
		return module;
	}
}
