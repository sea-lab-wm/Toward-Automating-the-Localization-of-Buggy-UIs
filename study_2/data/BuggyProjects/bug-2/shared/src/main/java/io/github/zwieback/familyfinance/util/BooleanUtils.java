package io.github.zwieback.familyfinance.util;

import android.os.Parcel;

public final class BooleanUtils {

    public static void writeBooleanToParcel(Parcel out, boolean value) {
        out.writeInt(value ? 1 : 0);
    }

    public static boolean readBooleanFromParcel(Parcel in) {
        return in.readInt() == 1;
    }

    private BooleanUtils() {
    }
}
