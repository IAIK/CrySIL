package at.tugraz.iaik.skytrust;

import android.content.res.Resources;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import at.tugraz.iaik.skytrust.database.keys.KeyEntry;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.CursorMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests for KeyListActivity
 */
public class KeyListActivityTest extends BaseActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private ExpandableAdapterViewProtocol expandableAdapterViewProtocol;

    @Before
    public void before() {
        expandableAdapterViewProtocol = new ExpandableAdapterViewProtocol();
    }

    /**
     * First add a new key, then delete the same key
     */
    @Test
    public void testAddAndRemoveKey() {
        String keyAlias = randomString(8);
        onView(withId(R.id.bt_main_manage_keys)).perform(click());
        createKey(keyAlias);
        deleteKey(keyAlias);
    }

    /**
     * Create two accounts and a key; create a mapping; finally delete mapping, key and accounts
     */
    @Test
    public void testAddAndRemoveMapping() {
        String keyAlias = randomString(8);
        String mailAddress = String.format("%s@%s.com", randomString(8), randomString(8));
        String username = randomString(8);
        String password = randomString(8);
        // Create accounts
        onView(withId(R.id.bt_main_manage_accounts)).perform(click());
        createAccountMail(mailAddress);
        createAccountUsername(username, password);
        // Create keys
        Espresso.pressBack();
        onView(withId(R.id.bt_main_manage_keys)).perform(click());
        createKey(keyAlias);
        // Perform assigning
        onData(withRowString(KeyEntry.COLUMN_NAME_ALIAS, keyAlias)).inAdapterView(withId(R.id.lvKeys)).perform(
                longClick());
        onView(withId(R.id.action_key_assign_account)).perform(click());
        int listId = Resources.getSystem().getIdentifier("select_dialog_listview", "id", "android");
        onData(hasToString(containsString(username))).inAdapterView(withId(listId)).perform(click());
        onData(hasToString(containsString(mailAddress))).inAdapterView(withId(listId)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        // Check assignment
        onData(withRowString(KeyEntry.COLUMN_NAME_ALIAS, keyAlias)).inAdapterView(withId(R.id.lvKeys)).perform(
                longClick());
        onView(withId(R.id.action_key_assign_account)).perform(click());
        onData(hasToString(containsString(username))).inAdapterView(withId(listId)).check(matches(isChecked()));
        onData(hasToString(containsString(mailAddress))).inAdapterView(withId(listId)).check(matches(isChecked()));
        // Delete assignment
        onData(hasToString(containsString(username))).inAdapterView(withId(listId)).perform(click());
        onData(hasToString(containsString(mailAddress))).inAdapterView(withId(listId)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        // Check assignment
        onData(withRowString(KeyEntry.COLUMN_NAME_ALIAS, keyAlias)).inAdapterView(withId(R.id.lvKeys)).perform(
                longClick());
        onView(withId(R.id.action_key_assign_account)).perform(click());
        onData(hasToString(containsString(username))).inAdapterView(withId(listId)).check(matches(isNotChecked()));
        onData(hasToString(containsString(mailAddress))).inAdapterView(withId(listId)).check(matches(isNotChecked()));
        onView(withId(android.R.id.button2)).perform(click());
        // Delete key
        onView(withId(R.id.action_delete)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        // Delete accounts
        Espresso.pressBack();
        onView(withId(R.id.bt_main_manage_accounts)).perform(click());
        deleteAccountMail(expandableAdapterViewProtocol, mailAddress);
        deleteAccountUsername(expandableAdapterViewProtocol, username);
    }
}
