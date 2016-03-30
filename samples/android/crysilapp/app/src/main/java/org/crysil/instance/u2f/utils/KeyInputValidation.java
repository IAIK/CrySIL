package org.crysil.instance.u2f.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to handle input validation
 */
public class KeyInputValidation {

    /**
     * This is a very simple check, basically: (anything)@(anything).(anythingWith2Chars)
     * @return true if the <code>emailAddress</code> is a valid one
     */
    public static boolean isValidEmail(String emailAddress){
        String expression = "^\\S+@([^\\s\\.]+\\.)+[^\\s\\.]{2,}$";
        CharSequence emailStr = emailAddress;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(emailStr);
        return matcher.matches();
    }
}
