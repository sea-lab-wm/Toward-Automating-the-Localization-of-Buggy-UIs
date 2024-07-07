package io.github.zwieback.familyfinance.core.model.converter;

import android.support.annotation.Nullable;

import java.math.BigDecimal;

import io.requery.Converter;

import static io.github.zwieback.familyfinance.util.BigDecimalConverterUtils.bigDecimalToExchangeRate;
import static io.github.zwieback.familyfinance.util.BigDecimalConverterUtils.exchangeRateToBigDecimal;

/**
 * Workaround for reading of a DECIMAL value.
 */
public class BigDecimalToExchangeRateConverter implements Converter<BigDecimal, Long> {

    @Override
    public Class<BigDecimal> getMappedType() {
        return BigDecimal.class;
    }

    @Override
    public Class<Long> getPersistedType() {
        return Long.class;
    }

    @Nullable
    @Override
    public Integer getPersistedSize() {
        return null;
    }

    @Override
    public Long convertToPersisted(BigDecimal value) {
        return bigDecimalToExchangeRate(value);
    }

    @Override
    public BigDecimal convertToMapped(Class<? extends BigDecimal> type, @Nullable Long value) {
        return exchangeRateToBigDecimal(value);
    }
}
