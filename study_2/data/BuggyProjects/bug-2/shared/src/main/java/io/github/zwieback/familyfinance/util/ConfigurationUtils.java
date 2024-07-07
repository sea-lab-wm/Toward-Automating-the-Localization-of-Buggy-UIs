package io.github.zwieback.familyfinance.util;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.os.ConfigurationCompat;
import android.support.v4.os.LocaleListCompat;

import java.util.Locale;

public final class ConfigurationUtils {

    /**
     * @return system locale
     * @see <a href="https://stackoverflow.com/questions/14389349/android-get-current-locale-not-default#comment79669939_39639103">
     * Android get current Locale, not default
     * </a>
     */
    public static Locale getSystemLocale() {
        Configuration configuration = Resources.getSystem().getConfiguration();
        LocaleListCompat locales = ConfigurationCompat.getLocales(configuration);
        if (locales.isEmpty()) {
            throw new RuntimeException("Locale list is empty");
        }
        return locales.get(0);
    }

    /**
     * Overall orientation of the screen.
     *
     * @return {@link Configuration#ORIENTATION_LANDSCAPE} or {@link Configuration#ORIENTATION_PORTRAIT}
     */
    public static int getOrientation() {
        return Resources.getSystem().getConfiguration().orientation;
    }

    private ConfigurationUtils() {
    }
}
