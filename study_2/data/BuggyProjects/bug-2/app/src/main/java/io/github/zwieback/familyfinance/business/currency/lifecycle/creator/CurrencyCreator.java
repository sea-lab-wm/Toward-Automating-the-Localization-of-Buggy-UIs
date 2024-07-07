package io.github.zwieback.familyfinance.business.currency.lifecycle.creator;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Set;
import java.util.TreeSet;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityCreator;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class CurrencyCreator extends EntityCreator<Currency> {

    public CurrencyCreator(@NonNull Context context,
                           @NonNull ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @NonNull
    @Override
    protected Iterable<Currency> buildEntities() {
        Set<Currency> currencies = new TreeSet<>(this);
        currencies.add(createCurrency(
                getString(R.string.default_currency_name),
                getString(R.string.default_currency_description)));
        return currencies;
    }

    @Override
    public int compare(Currency left, Currency right) {
        return left.getName().compareTo(right.getName());
    }

    @NonNull
    private static Currency createCurrency(@NonNull String name,
                                           @NonNull String description) {
        return new Currency()
                .setName(name)
                .setDescription(description);
    }
}
