package io.github.zwieback.familyfinance.core.filter;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class EntityFilter implements Parcelable {

    protected EntityFilter() {
        init();
    }

    @SuppressWarnings("UnusedParameters")
    protected EntityFilter(EntityFilter filter) {
        this();
    }

    protected EntityFilter(Parcel in) {
        readFromParcel(in);
    }

    protected void init() {
        // stub
    }

    protected void readFromParcel(Parcel in) {
        // stub
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // stub
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
