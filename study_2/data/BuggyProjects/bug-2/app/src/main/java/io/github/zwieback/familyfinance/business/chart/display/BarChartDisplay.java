package io.github.zwieback.familyfinance.business.chart.display;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.annimon.stream.Objects;

import io.github.zwieback.familyfinance.business.chart.display.type.BarChartGroupType;
import io.github.zwieback.familyfinance.util.BooleanUtils;

public class BarChartDisplay extends ChartDisplay<BarChartDisplay> {

    public static final String BAR_CHART_DISPLAY = "barChartDisplay";

    public static final Creator<BarChartDisplay> CREATOR = new Creator<BarChartDisplay>() {

        @Override
        public BarChartDisplay createFromParcel(Parcel in) {
            return new BarChartDisplay(in);
        }

        @Override
        public BarChartDisplay[] newArray(int size) {
            return new BarChartDisplay[size];
        }
    };

    /**
     * Initialized in the {@link #init} method.
     */
    @SuppressWarnings("NullableProblems")
    @NonNull
    private BarChartGroupType groupType;
    private boolean viewIncomeValues;
    private boolean viewExpenseValues;
    private boolean viewIncomes;
    private boolean viewExpenses;
    private boolean includeTransfers;

    public BarChartDisplay() {
        super();
    }

    public BarChartDisplay(BarChartDisplay display) {
        super(display);
        groupType = display.groupType;
        viewIncomeValues = display.viewIncomeValues;
        viewExpenseValues = display.viewExpenseValues;
        viewIncomes = display.viewIncomes;
        viewExpenses = display.viewExpenses;
        includeTransfers = display.includeTransfers;
    }

    private BarChartDisplay(Parcel in) {
        super(in);
    }

    @Override
    protected void init() {
        groupType = BarChartGroupType.DAYS;
        viewIncomeValues = true;
        viewExpenseValues = true;
        viewIncomes = true;
        viewExpenses = true;
        includeTransfers = false;
    }

    @Override
    protected void readFromParcel(Parcel in) {
        groupType = BarChartGroupType.valueOf(in.readString());
        viewIncomeValues = BooleanUtils.readBooleanFromParcel(in);
        viewExpenseValues = BooleanUtils.readBooleanFromParcel(in);
        viewIncomes = BooleanUtils.readBooleanFromParcel(in);
        viewExpenses = BooleanUtils.readBooleanFromParcel(in);
        includeTransfers = BooleanUtils.readBooleanFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(groupType.name());
        BooleanUtils.writeBooleanToParcel(out, viewIncomeValues);
        BooleanUtils.writeBooleanToParcel(out, viewExpenseValues);
        BooleanUtils.writeBooleanToParcel(out, viewIncomes);
        BooleanUtils.writeBooleanToParcel(out, viewExpenses);
        BooleanUtils.writeBooleanToParcel(out, includeTransfers);
    }

    @Override
    public boolean needRefreshData(BarChartDisplay newDisplay) {
        return getGroupType() != newDisplay.getGroupType() ||
                isViewIncomes() != newDisplay.isViewIncomes() ||
                isViewExpenses() != newDisplay.isViewExpenses() ||
                isIncludeTransfers() != newDisplay.isIncludeTransfers();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BarChartDisplay)) return false;
        BarChartDisplay that = (BarChartDisplay) o;
        return getGroupType() == that.getGroupType() &&
                isViewIncomeValues() == that.isViewIncomeValues() &&
                isViewExpenseValues() == that.isViewExpenseValues() &&
                isViewIncomes() == that.isViewIncomes() &&
                isViewExpenses() == that.isViewExpenses() &&
                isIncludeTransfers() == that.isIncludeTransfers();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGroupType(), isViewIncomeValues(), isViewExpenseValues(),
                isViewIncomes(), isViewExpenses(), isIncludeTransfers());
    }

    @NonNull
    public BarChartGroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(@NonNull BarChartGroupType groupType) {
        this.groupType = groupType;
    }

    public boolean isViewIncomeValues() {
        return viewIncomeValues;
    }

    public void setViewIncomeValues(boolean viewIncomeValues) {
        this.viewIncomeValues = viewIncomeValues;
    }

    public boolean isViewExpenseValues() {
        return viewExpenseValues;
    }

    public void setViewExpenseValues(boolean viewExpenseValues) {
        this.viewExpenseValues = viewExpenseValues;
    }

    public boolean isViewIncomes() {
        return viewIncomes;
    }

    public void setViewIncomes(boolean viewIncomes) {
        this.viewIncomes = viewIncomes;
    }

    public boolean isViewExpenses() {
        return viewExpenses;
    }

    public void setViewExpenses(boolean viewExpenses) {
        this.viewExpenses = viewExpenses;
    }

    public boolean isIncludeTransfers() {
        return includeTransfers;
    }

    public void setIncludeTransfers(boolean includeTransfers) {
        this.includeTransfers = includeTransfers;
    }
}
