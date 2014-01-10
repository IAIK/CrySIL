package at.iaik.skytrust.element.actors.gatekeeper.configuration;

import at.iaik.skytrust.element.actors.gatekeeper.authentication.AuthPlugin;

/**
 * A generic representation of info required for authenticating something. Holds an {@link AuthenticationPeriod} as well as an
 * {@link AuthenticationMethod}.
 */
public class AuthenticationInfo {
	private AuthPlugin method;
	private AuthenticationPeriod period;

	public AuthenticationInfo(AuthPlugin method, AuthenticationPeriod period) {
		this.method = method;
		this.period = period;
	}

	public AuthPlugin getMethod() {
		return method;
	}

	public AuthenticationPeriod getDuration() {
		return period;
	}
}
