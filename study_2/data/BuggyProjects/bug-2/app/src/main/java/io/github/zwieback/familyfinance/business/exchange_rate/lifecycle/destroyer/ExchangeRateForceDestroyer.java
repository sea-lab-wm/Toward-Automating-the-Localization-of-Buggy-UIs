package io.github.zwieback.familyfinance.business.exchange_rate.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityForceDestroyer;
import io.github.zwieback.familyfinance.core.model.ExchangeRate;
import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.reactivex.ReactiveEntityStore;

class ExchangeRateForceDestroyer extends EntityForceDestroyer<ExchangeRate> {

    ExchangeRateForceDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected Class<ExchangeRate> getEntityClass() {
        return ExchangeRate.class;
    }

    @Override
    protected QueryAttribute<ExchangeRate, Integer> getIdAttribute() {
        return ExchangeRate.ID;
    }
}
