package org.crysil.gatekeeperwithsessions.configuration;

/**
 * holds the number of uses until expired. Decreases at validation.
 */
public class CountLimit implements AuthenticationPeriod {
    /**
     * The limit.
     */
    int limit;

    /**
     * Instantiates a new time limit.
     *
     * @param amount the amount
     */
    public CountLimit(int amount) {
        limit = amount - 1;
    }

    @Override
    public boolean valid() {
        limit--;
        return (limit + 1) > 0;
    }
}
