package io.github.zwieback.familyfinance.util;

import android.support.v4.view.ViewCompat;
import android.view.View;

public final class ViewUtils {

    public static boolean isLeftToRightLayoutDirection(View view) {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_LTR;
    }

    private ViewUtils() {
    }
}
