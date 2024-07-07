package io.github.zwieback.familyfinance.business.currency.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityFromDestroyer;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.requery.Persistable;
import io.requery.query.Condition;
import io.requery.reactivex.ReactiveEntityStore;

public class CurrencyFromAccountsDestroyer extends EntityFromDestroyer<Currency, Account> {

    public CurrencyFromAccountsDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Currency> next() {
        return new CurrencyFromExchangeRatesDestroyer(context, data);
    }

    @Override
    protected Class<Account> getFromClass() {
        return Account.class;
    }

    @Override
    protected Condition<?, ?> getWhereCondition(Currency currency) {
        return Account.CURRENCY_ID.eq(currency.getId());
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.accounts_with_currency_exists;
    }
}
