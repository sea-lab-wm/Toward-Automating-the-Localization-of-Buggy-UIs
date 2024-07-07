package io.github.zwieback.familyfinance.business.exchange_rate.lifecycle.creator;

import android.content.Context;
import android.support.annotation.NonNull;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityCreator;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.core.model.ExchangeRate;
import io.github.zwieback.familyfinance.core.model.ICurrency;
import io.github.zwieback.familyfinance.util.DateUtils;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

public class ExchangeRateCreator extends EntityCreator<ExchangeRate> {

    public ExchangeRateCreator(@NonNull Context context,
                               @NonNull ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @NonNull
    @Override
    protected Iterable<ExchangeRate> buildEntities() {
        Currency defaultCurrency = findCurrency(databasePrefs.getCurrencyId());

        Set<ExchangeRate> exchangeRates = new TreeSet<>(this);
        exchangeRates.add(createExchangeRate(defaultCurrency, BigDecimal.ONE, DateUtils.now()));
        return exchangeRates;
    }

    @Override
    public int compare(ExchangeRate left, ExchangeRate right) {
        return left.getDate().compareTo(right.getDate());
    }

    @NonNull
    private Currency findCurrency(int currencyId) {
        ReactiveResult<Currency> currencies = data
                .select(Currency.class)
                .where(Currency.ID.eq(currencyId))
                .get();
        return currencies.first();
    }

    @NonNull
    private static ExchangeRate createExchangeRate(@NonNull ICurrency currency,
                                                   @NonNull BigDecimal value,
                                                   @NonNull LocalDate date) {
        return new ExchangeRate()
                .setCurrency(currency)
                .setValue(value)
                .setDate(date);
    }
}
