package io.github.zwieback.familyfinance.business.account.activity.helper;

import android.support.v7.widget.AppCompatSpinner;

import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.type.AccountType;

public class AccountTypeHelper {

    public static int getAccountTypeIndex(Account account) {
        if (account.getType() == null) {
            return AccountType.UNDEFINED_ACCOUNT.ordinal();
        }
        return account.getType().ordinal();
    }

    public static AccountType getAccountType(AppCompatSpinner typeSpinner) {
        int typeIndex = typeSpinner.getSelectedItemPosition();
        return AccountType.values()[typeIndex];
    }
}
