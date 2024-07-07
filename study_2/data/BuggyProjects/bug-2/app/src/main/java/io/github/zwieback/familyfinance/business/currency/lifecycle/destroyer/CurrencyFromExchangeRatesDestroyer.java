package io.github.zwieback.familyfinance.business.currency.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityFromDestroyer;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.core.model.ExchangeRate;
import io.requery.Persistable;
import io.requery.query.Condition;
import io.requery.reactivex.ReactiveEntityStore;

class CurrencyFromExchangeRatesDestroyer extends EntityFromDestroyer<Currency, ExchangeRate> {

    CurrencyFromExchangeRatesDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Currency> next() {
        return new CurrencyFromPreferencesDestroyer(context, data);
    }

    @Override
    protected Class<ExchangeRate> getFromClass() {
        return ExchangeRate.class;
    }

    @Override
    protected Condition<?, ?> getWhereCondition(Currency currency) {
        return ExchangeRate.CURRENCY_ID.eq(currency.getId());
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.exchange_rates_with_currency_exists;
    }
}
