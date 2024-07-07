package io.github.zwieback.familyfinance.business.account.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityForceDestroyer;
import io.github.zwieback.familyfinance.core.model.Account;
import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.reactivex.ReactiveEntityStore;

class AccountForceDestroyer extends EntityForceDestroyer<Account> {

    AccountForceDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected Class<Account> getEntityClass() {
        return Account.class;
    }

    @Override
    protected QueryAttribute<Account, Integer> getIdAttribute() {
        return Account.ID;
    }
}
