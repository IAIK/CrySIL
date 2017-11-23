package at.tugraz.iaik.skytrust.database.accounts;

/**
 * Specifies the type of one account info, as there are OAuth or username/password accounts
 */
public enum AccountInfoType {

    // do not refactor this order, as the ordinal value is stored in the database
    GOOGLE_ACCOUNT, USERNAME_ACCOUNT;

    public static String getTableNameForType(AccountInfoType type) {
        switch (type) {
            case GOOGLE_ACCOUNT:
                return AccountGoogleEntry.TABLE_NAME;
            case USERNAME_ACCOUNT:
                return AccountUsernameEntry.TABLE_NAME;
            default:
                throw new RuntimeException("Add the new account type to the switch statement in AccountInfoType");
        }
    }
}
