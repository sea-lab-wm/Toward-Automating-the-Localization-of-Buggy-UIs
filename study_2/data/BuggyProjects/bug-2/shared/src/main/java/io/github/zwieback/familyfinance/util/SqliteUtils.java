package io.github.zwieback.familyfinance.util;

import android.os.Build;

public final class SqliteUtils {

    /**
     * CTEs are available only in SQLite 3.8.3 or later
     *
     * @see <a href="https://stackoverflow.com/a/22093357/8035065">
     * Android SQLite database recursive query
     * </a>
     */
    public static boolean cteSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private SqliteUtils() {
    }
}
