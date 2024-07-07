package com.johnpetitto.validator;

import android.util.Patterns;

import java.util.List;

import io.github.zwieback.familyfinance.util.DateUtils;
import io.github.zwieback.familyfinance.util.NumberUtils;
import io.github.zwieback.familyfinance.util.StringUtils;

/**
 * Collection of utilities for working with {@link ValidatingTextInputLayout}.
 */
public final class Validators {
    /**
     * Validates input for email formatting.
     */
    public static final Validator EMAIL;
    /**
     * Validates input for phone number formatting.
     */
    public static final Validator PHONE;
    /**
     * Validates input for Integer number formatting.
     */
    public static final Validator INTEGER;
    /**
     * Validates input for BigDecimal number formatting.
     */
    public static final Validator BIG_DECIMAL;
    /**
     * Validates input for LocalDate date formatting.
     */
    public static final Validator DATE;
    /**
     * Validates input for not empty String.
     */
    public static final Validator NOT_EMPTY;
    /**
     * Validates input for number formatting.
     */
    public static final Validator SIGNED_NUMBER;
    /**
     * Validates input for account number formatting.
     */
    public static final Validator ACCOUNT_NUMBER;

    static {
        EMAIL = input -> Patterns.EMAIL_ADDRESS.matcher(input).matches();
        PHONE = input -> Patterns.PHONE.matcher(input).matches();
        INTEGER = NumberUtils::isTextAnInteger;
        BIG_DECIMAL = NumberUtils::isTextABigDecimal;
        DATE = DateUtils::isTextAnLocalDate;
        NOT_EMPTY = StringUtils::isTextNotEmpty;
        SIGNED_NUMBER = NumberUtils::isTextASignedNumber;
        ACCOUNT_NUMBER = NumberUtils::isTextAnAccountNumber;
    }

    private Validators() {
        throw new AssertionError("No instances");
    }

    /**
     * Validates multiple inputs at once and returns {@code true} if all inputs are valid.
     */
    public static boolean validate(List<ValidatingTextInputLayout> layouts) {
        boolean allInputsValid = true;
        for (ValidatingTextInputLayout layout : layouts) {
            if (!layout.validate()) {
                allInputsValid = false;
            }
        }
        return allInputsValid;
    }

    /**
     * Validates input for meeting a minimum number of characters. For validating against trimmed
     * input, {@link #minimum(int, boolean)}.
     */
    public static Validator minimum(int length) {
        return minimum(length, false);
    }

    /**
     * Validates input for meeting a minimum number of characters. Pass {@literal true} for
     * {@code trim} to validate against trimmed input.
     */
    public static Validator minimum(final int length, final boolean trim) {
        return input -> {
            if (trim) {
                input = input.trim();
            }
            return length <= input.length();
        };
    }
}
