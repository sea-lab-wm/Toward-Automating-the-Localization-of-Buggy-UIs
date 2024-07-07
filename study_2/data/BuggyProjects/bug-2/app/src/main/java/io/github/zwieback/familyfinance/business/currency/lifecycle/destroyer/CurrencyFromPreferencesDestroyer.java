package io.github.zwieback.familyfinance.business.currency.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityFromPreferencesDestroyer;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

class CurrencyFromPreferencesDestroyer extends EntityFromPreferencesDestroyer<Currency> {

    CurrencyFromPreferencesDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Currency> next() {
        return new CurrencyForceDestroyer(context, data);
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.preferences_contains_currency;
    }

    @Override
    protected boolean preferencesContainsEntity(Currency currency) {
        int currencyId = databasePrefs.getCurrencyId();
        return currencyId == currency.getId();
    }
}
