package io.github.zwieback.familyfinance.util;

import android.support.annotation.Nullable;

import java.math.BigDecimal;

public final class BigDecimalConverterUtils {

    private static final int WORTH_POWER = 2;
    private static final int EXCHANGE_RATE_POWER = 8;

    @Nullable
    public static BigDecimal worthToBigDecimal(@Nullable Long value) {
        return longToBigDecimal(value, -WORTH_POWER);
    }

    @Nullable
    public static Long bigDecimalToWorth(@Nullable BigDecimal value) {
        return bigDecimalToLong(value, WORTH_POWER);
    }

    @Nullable
    public static BigDecimal exchangeRateToBigDecimal(@Nullable Long value) {
        return longToBigDecimal(value, -EXCHANGE_RATE_POWER);
    }

    @Nullable
    public static Long bigDecimalToExchangeRate(@Nullable BigDecimal value) {
        return bigDecimalToLong(value, EXCHANGE_RATE_POWER);
    }

    @Nullable
    public static BigDecimal balanceInNativeCurrencyToBigDecimal(@Nullable Long value) {
        return worthToBigDecimal(value);
    }

    @Nullable
    public static BigDecimal balanceInForeignCurrencyToBigDecimal(@Nullable Long value) {
        return longToBigDecimal(value, -(WORTH_POWER + EXCHANGE_RATE_POWER));
    }

    @Nullable
    private static BigDecimal longToBigDecimal(@Nullable Long value, int power) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value).scaleByPowerOfTen(power);
    }

    @Nullable
    private static Long bigDecimalToLong(@Nullable BigDecimal value, int power) {
        if (value == null) {
            return null;
        }
        return value.scaleByPowerOfTen(power).longValue();
    }

    private BigDecimalConverterUtils() {
    }
}
