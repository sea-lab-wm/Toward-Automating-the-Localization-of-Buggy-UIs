package io.github.zwieback.familyfinance.util;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;
import java.util.Random;

public final class RandomGenerator {

    private final Random random;

    public RandomGenerator() {
        random = new Random();
    }

    public int getRandomInt() {
        return random.nextInt();
    }

    public int getRandomInt(int minInclusive, int maxInclusive) {
        return random.nextInt(maxInclusive + 1 - minInclusive) + minInclusive;
    }

    public LocalDate getRandomLocalDate() {
        int year = getRandomInt(1917, 2017);
        int month = getRandomInt(1, 12);
        int day = getRandomInt(1, 28);
        return LocalDate.of(year, month, day);
    }

    public BigDecimal getRandomBigDecimal() {
        int unscaledValue = getRandomInt();
        int scale = getRandomInt();
        return BigDecimal.valueOf(unscaledValue, scale);
    }
}
