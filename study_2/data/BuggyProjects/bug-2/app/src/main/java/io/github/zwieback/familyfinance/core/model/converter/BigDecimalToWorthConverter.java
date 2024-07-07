package io.github.zwieback.familyfinance.core.model.converter;

import android.support.annotation.Nullable;

import java.math.BigDecimal;

import io.requery.Converter;

import static io.github.zwieback.familyfinance.util.BigDecimalConverterUtils.bigDecimalToWorth;
import static io.github.zwieback.familyfinance.util.BigDecimalConverterUtils.worthToBigDecimal;

/**
 * Workaround for reading of a DECIMAL value.
 */
public class BigDecimalToWorthConverter implements Converter<BigDecimal, Long> {

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
        return bigDecimalToWorth(value);
    }

    @Override
    public BigDecimal convertToMapped(Class<? extends BigDecimal> type, @Nullable Long value) {
        return worthToBigDecimal(value);
    }
}
