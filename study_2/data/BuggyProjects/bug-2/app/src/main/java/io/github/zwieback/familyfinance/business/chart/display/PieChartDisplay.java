package io.github.zwieback.familyfinance.business.chart.display;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.annimon.stream.Objects;

import io.github.zwieback.familyfinance.business.chart.display.type.PieChartGroupByType;
import io.github.zwieback.familyfinance.business.chart.display.type.PieChartGroupingType;
import io.github.zwieback.familyfinance.util.BooleanUtils;

public class PieChartDisplay extends ChartDisplay<PieChartDisplay> {

    public static final String PIE_CHART_DISPLAY = "pieChartDisplay";

    public static final Creator<PieChartDisplay> CREATOR = new Creator<PieChartDisplay>() {

        @Override
        public PieChartDisplay createFromParcel(Parcel in) {
            return new PieChartDisplay(in);
        }

        @Override
        public PieChartDisplay[] newArray(int size) {
            return new PieChartDisplay[size];
        }
    };

    /**
     * Initialized in the {@link #init} method.
     */
    @SuppressWarnings("NullableProblems")
    @NonNull
    private PieChartGroupingType groupingType;
    /**
     * Initialized in the {@link #init} method.
     */
    @SuppressWarnings("NullableProblems")
    @NonNull
    private PieChartGroupByType groupByType;
    private boolean viewValues;
    private boolean usePercentValues;

    public PieChartDisplay() {
        super();
    }

    public PieChartDisplay(PieChartDisplay display) {
        super(display);
        groupingType = display.groupingType;
        groupByType = display.groupByType;
        viewValues = display.viewValues;
        usePercentValues = display.usePercentValues;
    }

    private PieChartDisplay(Parcel in) {
        super(in);
    }

    @Override
    protected void init() {
        groupingType = PieChartGroupingType.LIMIT;
        groupByType = PieChartGroupByType.ARTICLE_PARENT;
        viewValues = true;
        usePercentValues = false;
    }

    @Override
    protected void readFromParcel(Parcel in) {
        groupingType = PieChartGroupingType.valueOf(in.readString());
        groupByType = PieChartGroupByType.valueOf(in.readString());
        viewValues = BooleanUtils.readBooleanFromParcel(in);
        usePercentValues = BooleanUtils.readBooleanFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(groupingType.name());
        out.writeString(groupByType.name());
        BooleanUtils.writeBooleanToParcel(out, viewValues);
        BooleanUtils.writeBooleanToParcel(out, usePercentValues);
    }

    @Override
    public boolean needRefreshData(PieChartDisplay newDisplay) {
        return getGroupingType() != newDisplay.getGroupingType() ||
                getGroupByType() != newDisplay.getGroupByType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PieChartDisplay)) return false;
        PieChartDisplay that = (PieChartDisplay) o;
        return getGroupingType() == that.getGroupingType() &&
                getGroupByType() == that.getGroupByType() &&
                isViewValues() == that.isViewValues() &&
                isUsePercentValues() == that.isUsePercentValues();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGroupingType(), getGroupByType(), isViewValues(),
                isUsePercentValues());
    }

    @NonNull
    public PieChartGroupingType getGroupingType() {
        return groupingType;
    }

    public void setGroupingType(@NonNull PieChartGroupingType groupingType) {
        this.groupingType = groupingType;
    }

    @NonNull
    public PieChartGroupByType getGroupByType() {
        return groupByType;
    }

    public void setGroupByType(@NonNull PieChartGroupByType groupByType) {
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
