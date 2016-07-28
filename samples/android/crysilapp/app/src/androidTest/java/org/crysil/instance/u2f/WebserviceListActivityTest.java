package org.crysil.instance.u2f;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import org.crysil.instance.u2f.database.webservice.WebserviceEntry;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.CursorMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Test for WebserviceListActivity
 */
public class WebserviceListActivityTest extends BaseActivityTest {

    @Rule
    public ActivityTestRule<WebserviceListActivity> mActivityRule = new ActivityTestRule<WebserviceListActivity>(
            WebserviceListActivity.class);

    /**
     * Creates and removes a webservice entry
     */
    @Test
    public void testAddAndRemoveWebservice() {
        String title = randomString(8);
        String address = "localhost:8080";
        onView(withId(R.id.webservicelist_addWebservice)).perform(click());
        onView(withId(R.id.edDialogWebserviceTitle)).perform(clearText()).perform(typeText(title));
        onView(withId(R.id.edDialogWebserviceAddress)).perform(clearText()).perform(typeText(address));
        onView(withId(android.R.id.button1)).perform(click());

        onData(withRowString(WebserviceEntry.COLUMN_NAME_TITLE, title)).inAdapterView(
                withId(R.id.lvWebservice)).perform(longClick());
        onView(withId(R.id.action_delete)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.lvWebservice)).check(
                matches(not(withAdaptedData(withRowString(WebserviceEntry.COLUMN_NAME_TITLE, title)))));
    }
}
