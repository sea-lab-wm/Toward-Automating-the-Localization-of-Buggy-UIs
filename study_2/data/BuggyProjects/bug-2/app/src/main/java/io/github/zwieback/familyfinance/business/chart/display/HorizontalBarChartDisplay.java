package io.github.zwieback.familyfinance.business.chart.display;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.annimon.stream.Objects;

import io.github.zwieback.familyfinance.business.chart.display.type.HorizontalBarChartGroupByType;
import io.github.zwieback.familyfinance.util.BooleanUtils;

public class HorizontalBarChartDisplay extends ChartDisplay<HorizontalBarChartDisplay> {

    public static final String HORIZONTAL_BAR_CHART_DISPLAY = "horizontalBarChartDisplay";

    public static final Creator<HorizontalBarChartDisplay> CREATOR =
            new Creator<HorizontalBarChartDisplay>() {

                @Override
                public HorizontalBarChartDisplay createFromParcel(Parcel in) {
                    return new HorizontalBarChartDisplay(in);
                }

                @Override
                public HorizontalBarChartDisplay[] newArray(int size) {
                    return new HorizontalBarChartDisplay[size];
                }
            };

    /**
     * Initialized in the {@link #init} method.
     */
    @SuppressWarnings("NullableProblems")
    @NonNull
    private HorizontalBarChartGroupByType groupByType;
    private boolean viewValues;
    private boolean usePercentValues;

    public HorizontalBarChartDisplay() {
        super();
    }

    public HorizontalBarChartDisplay(HorizontalBarChartDisplay display) {
        super(display);
        groupByType = display.groupByType;
        viewValues = display.viewValues;
        usePercentValues = display.usePercentValues;
    }

    private HorizontalBarChartDisplay(Parcel in) {
        super(in);
    }

    @Override
    protected void init() {
        groupByType = HorizontalBarChartGroupByType.ARTICLE;
        viewValues = true;
        usePercentValues = false;
    }

    @Override
    protected void readFromParcel(Parcel in) {
        groupByType = HorizontalBarChartGroupByType.valueOf(in.readString());
        viewValues = BooleanUtils.readBooleanFromParcel(in);
        usePercentValues = BooleanUtils.readBooleanFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(groupByType.name());
        BooleanUtils.writeBooleanToParcel(out, viewValues);
        BooleanUtils.writeBooleanToParcel(out, usePercentValues);
    }

    @Override
    public boolean needRefreshData(HorizontalBarChartDisplay newDisplay) {
        return getGroupByType() != newDisplay.getGroupByType() ||
                isUsePercentValues() != newDisplay.isUsePercentValues();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HorizontalBarChartDisplay)) return false;
        HorizontalBarChartDisplay that = (HorizontalBarChartDisplay) o;
        return getGroupByType() == that.getGroupByType() &&
                isViewValues() == that.isViewValues() &&
                isUsePercentValues() == that.isUsePercentValues();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGroupByType(), isViewValues(), isUsePercentValues());
    }

    @NonNull
    public HorizontalBarChartGroupByType getGroupByType() {
        return groupByType;
    }

    public void setGroupByType(@NonNull HorizontalBarChartGroupByType groupByType) {
        this.groupByType = groupByType;
    }

    public boolean isViewValues() {
        return viewValues;
    }

    public void setViewValues(boolean viewValues) {
        this.viewValues = viewValues;
    }

    public boolean isUsePercentValues() {
        return usePercentValues;
    }

    public void setUsePercentValues(boolean usePercentValues) {
        this.usePercentValues = usePercentValues;
    }
}
