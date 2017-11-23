package at.tugraz.iaik.skytrust;

import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import java.security.Security;
import java.util.Random;

import at.tugraz.iaik.skytrust.database.accounts.AccountGoogleEntry;
import at.tugraz.iaik.skytrust.database.accounts.AccountInfoType;
import at.tugraz.iaik.skytrust.database.accounts.AccountUsernameEntry;
import at.tugraz.iaik.skytrust.database.keys.KeyEntry;
import iaik.security.provider.IAIK;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.CursorMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Base class for espresso tests: Insert IAIK Security provider
 */
@RunWith(AndroidJUnit4.class)
@Ignore
public class BaseActivityTest {

    @BeforeClass
    public static void beforeClass() {
        Security.insertProviderAt(IAIK.getInstance(), Security.getProviders().length + 1);
    }

    // from https://code.google.com/p/android-test-kit/wiki/EspressoSamples#Asserting_that_a_data_item_is_not_in_an_adapter
    protected static Matcher<View> withAdaptedData(final Matcher<Object> dataMatcher) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with class name: ");
                dataMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof AdapterView)) {
                    return false;
                }
                @SuppressWarnings("rawtypes") Adapter adapter = ((AdapterView) view).getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (dataMatcher.matches(adapter.getItem(i))) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    protected static String randomString(int length) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        Random generator = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars[generator.nextInt(chars.length)]);
        }
        return sb.toString();
    }

    protected void createKey(String keyAlias) {
        onView(withId(R.id.keylist_addKey)).perform(click());
        onView(withId(R.id.edAlias)).perform(clearText()).perform(typeText(keyAlias));
        onView(withId(R.id.btCreateKey)).perform(click());
    }

    protected void deleteKey(String keyAlias) {
        onData(withRowString(KeyEntry.COLUMN_NAME_ALIAS, keyAlias)).inAdapterView(withId(R.id.lvKeys)).perform(
                longClick());
        onView(withId(R.id.action_delete)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.lvKeys)).check(matches(not(withAdaptedData(hasToString(containsString(keyAlias))))));
    }

    protected void createAccountMail(String mailAddress) {
        onView(allOf(withId(R.id.accountlist_section_username_password_button),
                withTagValue(hasToString(AccountInfoType.getTableNameForType(AccountInfoType.GOOGLE_ACCOUNT))))).perform(
                click());
        onView(withId(R.id.dialog_add_gmail_edit_text)).perform(clearText()).perform(typeText(mailAddress));
        onView(withId(android.R.id.button1)).perform(click());
    }

    protected void deleteAccountMail(ExpandableAdapterViewProtocol expandableAdapterViewProtocol, String mailAddress) {
        onData(withRowString(AccountGoogleEntry.COLUMN_NAME_GMAIL_ADDRESS, mailAddress).withStrictColumnChecks(
                false)).usingAdapterViewProtocol(expandableAdapterViewProtocol).perform(longClick());
        onView(withId(R.id.action_delete)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(android.R.id.list)).check(matches(not(withAdaptedData(
                withRowString(AccountGoogleEntry.COLUMN_NAME_GMAIL_ADDRESS, mailAddress).withStrictColumnChecks(
                        false)))));
    }

    protected void createAccountUsername(String username, String password) {
        onView(allOf(withId(R.id.accountlist_section_username_password_button), withTagValue(
                hasToString(AccountInfoType.getTableNameForType(AccountInfoType.USERNAME_ACCOUNT))))).perform(
                click());
        onView(withId(R.id.dialog_add_up_username)).perform(clearText()).perform(typeText(username));
        onView(withId(R.id.dialog_add_up_password)).perform(clearText()).perform(typeText(password));
        onView(withId(android.R.id.button1)).perform(click());
    }

    protected void deleteAccountUsername(ExpandableAdapterViewProtocol expandableAdapterViewProtocol, String username) {
        onData(withRowString(AccountUsernameEntry.COLUMN_NAME_USERNAME, username).withStrictColumnChecks(
                false)).usingAdapterViewProtocol(expandableAdapterViewProtocol).perform(longClick());
        onView(withId(R.id.action_delete)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(android.R.id.list)).check(matches(not(withAdaptedData(
                withRowString(AccountUsernameEntry.COLUMN_NAME_USERNAME, username).withStrictColumnChecks(false)))));
    }

}
