package io.github.zwieback.familyfinance.business.chart.display;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class ChartDisplay<D extends ChartDisplay> implements Parcelable {

    ChartDisplay() {
        init();
    }

    @SuppressWarnings("UnusedParameters")
    ChartDisplay(ChartDisplay display) {
        this();
    }

    ChartDisplay(Parcel in) {
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

    public abstract boolean needRefreshData(D newDisplay);
}
