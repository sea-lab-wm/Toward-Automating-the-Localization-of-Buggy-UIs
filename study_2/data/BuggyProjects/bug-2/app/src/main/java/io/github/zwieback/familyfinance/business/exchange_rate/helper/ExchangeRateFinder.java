package io.github.zwieback.familyfinance.business.exchange_rate.helper;

import android.support.annotation.Nullable;

import io.github.zwieback.familyfinance.core.model.ExchangeRate;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public final class ExchangeRateFinder {

    private final ReactiveEntityStore<Persistable> data;

    public ExchangeRateFinder(ReactiveEntityStore<Persistable> data) {
        this.data = data;
    }

    @Nullable
    public ExchangeRate findLastExchangeRate(int currencyId) {
        return data
                .select(ExchangeRate.class)
                .where(ExchangeRate.CURRENCY_ID.eq(currencyId))
                .orderBy(ExchangeRate.DATE.desc())
                .get().firstOrNull();
    }
}
