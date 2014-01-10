package at.iaik.skytrust.element.actors.gatekeeper.configuration;

import java.util.Calendar;
import java.util.Date;

/**
 * holds a timestamp in the future.
 */
public class TimeLimit implements AuthenticationPeriod {

	/** The limit. */
	Date limit;

	/**
	 * Instantiates a new time limit.
	 * 
	 * @param field
	 *            Calendar.MINUTE for example
	 * @param amount
	 *            the amount
	 */
	public TimeLimit(int field, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(field, amount);

		this.limit = cal.getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.iaik.skytrust.element.actors.gatekeeper.configuration.AuthenticationPeriod#valid()
	 */
	@Override
	public boolean valid() {
		return new Date().before(limit);
	}

}
