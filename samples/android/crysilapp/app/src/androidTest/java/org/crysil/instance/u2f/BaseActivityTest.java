package org.crysil.instance.u2f;

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
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Random;

/**
 * Base class for espresso tests: Insert security provider
 */
@RunWith(AndroidJUnit4.class)
@Ignore
public class BaseActivityTest {

    @BeforeClass
    public static void beforeClass() {
        Security.insertProviderAt(new BouncyCastleProvider(), Security.getProviders().length + 1);
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
}
