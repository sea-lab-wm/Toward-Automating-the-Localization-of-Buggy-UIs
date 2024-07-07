package io.github.zwieback.familyfinance.business.currency.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityForceDestroyer;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.reactivex.ReactiveEntityStore;

class CurrencyForceDestroyer extends EntityForceDestroyer<Currency> {

    CurrencyForceDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected Class<Currency> getEntityClass() {
        return Currency.class;
    }

    @Override
    protected QueryAttribute<Currency, Integer> getIdAttribute() {
        return Currency.ID;
    }
}
