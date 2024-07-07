package io.github.zwieback.familyfinance.business.account.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityAsParentDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Account;
import io.requery.Persistable;
import io.requery.meta.QueryExpression;
import io.requery.reactivex.ReactiveEntityStore;

public class AccountAsParentDestroyer extends EntityAsParentDestroyer<Account> {

    public AccountAsParentDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Account> next() {
        return new AccountFromExpenseOperationsDestroyer(context, data);
    }

    @Override
    protected Class<Account> getEntityClass() {
        return Account.class;
    }

    @Override
    protected QueryExpression<Integer> getParentIdExpression() {
        return Account.PARENT_ID;
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.accounts_with_parent_exists;
    }
}
