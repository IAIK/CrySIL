package at.tugraz.iaik.skytrust;

import android.content.res.Resources;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import at.tugraz.iaik.skytrust.database.accounts.AccountUsernameEntry;
import at.tugraz.iaik.skytrust.database.keys.KeyEntry;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests for AccountListActivity
 */
public class AccountListActivityTest extends BaseActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(
            MainActivity.class);

    private ExpandableAdapterViewProtocol expandableAdapterViewProtocol;

    @Before
    public void before() {
        expandableAdapterViewProtocol = new ExpandableAdapterViewProtocol();
    }

    /**
     * Creates and deletes a OAuth-Account
     */
    @Test
    public void testAddAndDeleteOAuthAccount() {
        String mailAddress = String.format("%s@%s.com", randomString(8), randomString(8));
        onView(withId(R.id.bt_main_manage_accounts)).perform(click());
        createAccountMail(mailAddress);
        deleteAccountMail(expandableAdapterViewProtocol, mailAddress);
    }

    /**
     * Creates and deletes a username-password account
     */
    @Test
    public void testAddAndDeleteUsernamePasswordAccount() {
        String username = randomString(8);
        String password = randomString(8);
        onView(withId(R.id.bt_main_manage_accounts)).perform(click());
        createAccountUsername(username, password);
        deleteAccountUsername(expandableAdapterViewProtocol, username);
    }

    /**
     * Creates a key and an account; create a mapping; finally delete mapping, key and account
     */
    @Test
    public void testAddAndRemoveMapping() {
        String keyAlias = randomString(8);
        String username = randomString(8);
        String password = randomString(8);
        // Create keys
        onView(withId(R.id.bt_main_manage_keys)).perform(click());
        createKey(keyAlias);
        // Create accounts
        Espresso.pressBack();
        onView(withId(R.id.bt_main_manage_accounts)).perform(click());
        createAccountUsername(username, password);
        // Perform assigning
        onData(withRowString(AccountUsernameEntry.COLUMN_NAME_USERNAME, username).withStrictColumnChecks(
                false)).usingAdapterViewProtocol(expandableAdapterViewProtocol).perform(longClick());
        onView(withId(R.id.action_account_assign_key)).perform(click());
        int listId = Resources.getSystem().getIdentifier("select_dialog_listview", "id", "android");
        onData(hasToString(containsString(keyAlias))).inAdapterView(withId(listId)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        // Check assignment
        onData(withRowString(AccountUsernameEntry.COLUMN_NAME_USERNAME, username).withStrictColumnChecks(
                false)).usingAdapterViewProtocol(expandableAdapterViewProtocol).perform(longClick());
        onView(withId(R.id.action_account_assign_key)).perform(click());
        onData(hasToString(containsString(keyAlias))).inAdapterView(withId(listId)).check(matches(isChecked()));
        // Delete assignment
        onData(hasToString(containsString(keyAlias))).inAdapterView(withId(listId)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        // Check assignment
        onData(withRowString(AccountUsernameEntry.COLUMN_NAME_USERNAME, username).withStrictColumnChecks(
                false)).usingAdapterViewProtocol(expandableAdapterViewProtocol).perform(longClick());
        onView(withId(R.id.action_account_assign_key)).perform(click());
        onData(hasToString(containsString(keyAlias))).inAdapterView(withId(listId)).check(matches(isNotChecked()));
        onView(withId(android.R.id.button2)).perform(click());
        // Delete account
        onView(withId(R.id.action_delete)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        // Delete key
        Espresso.pressBack();
        onView(withId(R.id.bt_main_manage_keys)).perform(click());
        deleteKey(keyAlias);
    }

}
