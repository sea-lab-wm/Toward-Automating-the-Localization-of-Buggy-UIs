package io.github.zwieback.familyfinance.business.account.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityFromPreferencesDestroyer;
import io.github.zwieback.familyfinance.core.model.Account;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

class AccountFromPreferencesDestroyer extends EntityFromPreferencesDestroyer<Account> {

    AccountFromPreferencesDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Account> next() {
        return new AccountForceDestroyer(context, data);
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.preferences_contains_account;
    }

    @Override
    protected boolean preferencesContainsEntity(Account account) {
        int accountId = databasePrefs.getAccountId();
        return accountId == account.getId();
    }
}
