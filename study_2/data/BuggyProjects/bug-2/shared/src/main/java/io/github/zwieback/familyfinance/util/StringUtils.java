package io.github.zwieback.familyfinance.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public final class StringUtils {

    public static final String EMPTY = "";
    public static final String UNDEFINED = "undefined";
    public static final String QUESTION = "?";

    public static boolean isTextEmpty(@Nullable CharSequence text) {
        return TextUtils.isEmpty(text);
    }

    public static boolean isTextNotEmpty(@Nullable CharSequence text) {
        return !isTextEmpty(text);
    }

    @NonNull
    public static String deleteLastChar(@Nullable String text) {
        if (isTextNotEmpty(text)) {
            return text.substring(0, text.length() - 1);
        }
        return EMPTY;
    }

    @NonNull
    public static String addChar(@Nullable String text, @NonNull String character) {
        if (isTextNotEmpty(text)) {
            return text + character;
        }
        return character;
    }

    @NonNull
    public static String addUniqueChar(@Nullable String text, @NonNull String character) {
        if (isTextNotEmpty(text)) {
            if (text.contains(character)) {
                return text;
            }
            return text + character;
        }
        return character;
    }

    private StringUtils() {
    }
}
