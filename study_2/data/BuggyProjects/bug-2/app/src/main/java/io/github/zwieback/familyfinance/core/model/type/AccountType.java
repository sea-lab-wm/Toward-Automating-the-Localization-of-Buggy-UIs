package io.github.zwieback.familyfinance.core.model.type;

import java.util.Arrays;
import java.util.List;

/**
 * After changing this enum, you also need to change the strings/account_type array too.
 */
public enum AccountType {
    UNDEFINED_ACCOUNT,
    CASH_ACCOUNT,
    DEPOSIT_ACCOUNT,
    CREDIT_ACCOUNT,
    DEBIT_CARD_ACCOUNT,
    CREDIT_CARD_ACCOUNT,
    VIRTUAL_DEBIT_CARD_ACCOUNT,
    VIRTUAL_CREDIT_CARD_ACCOUNT;

    private static final List<AccountType> CARD_LIST = Arrays.asList(DEBIT_CARD_ACCOUNT, CREDIT_CARD_ACCOUNT,
            VIRTUAL_DEBIT_CARD_ACCOUNT, VIRTUAL_CREDIT_CARD_ACCOUNT);

    public boolean isCard() {
        return CARD_LIST.contains(this);
    }
}
