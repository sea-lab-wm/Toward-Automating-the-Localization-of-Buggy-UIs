package io.github.zwieback.familyfinance.business.chart.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.chart.display.BarChartDisplay;
import io.github.zwieback.familyfinance.business.chart.display.type.BarChartGroupType;
import io.github.zwieback.familyfinance.business.chart.exception.UnsupportedBarChartGroupTypeException;
import io.github.zwieback.familyfinance.databinding.DialogDisplayChartBarBinding;

import static io.github.zwieback.familyfinance.business.chart.display.BarChartDisplay.BAR_CHART_DISPLAY;

public class BarChartDisplayDialog extends ChartDisplayDialog<BarChartDisplay,
        DialogDisplayChartBarBinding> {

    public static BarChartDisplayDialog newInstance(BarChartDisplay display) {
        BarChartDisplayDialog fragment = new BarChartDisplayDialog();
        Bundle args = createArguments(BAR_CHART_DISPLAY, display);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BarChartDisplay createCopyOfDisplay(BarChartDisplay display) {
        return new BarChartDisplay(display);
    }

    protected String getInputDisplayName() {
        return BAR_CHART_DISPLAY;
    }

    @Override
    protected int getDialogTitle() {
        return R.string.bar_chart_display_title;
    }

    @Override
    protected int getDialogLayoutId() {
        return R.layout.dialog_display_chart_bar;
    }

    protected void bind(BarChartDisplay display) {
        binding.setDisplay(display);
    }

    protected void updateDisplayProperties() {
        display.setGroupType(determineGroupType());
    }

    @NonNull
    private BarChartGroupType determineGroupType() {
        if (binding.groupByDays.isChecked()) {
            return BarChartGroupType.DAYS;
        } else if (binding.groupByWeeks.isChecked()) {
            return BarChartGroupType.WEEKS;
        } else if (binding.groupByMonths.isChecked()) {
            return BarChartGroupType.MONTHS;
        } else if (binding.groupByQuarters.isChecked()) {
            return BarChartGroupType.QUARTERS;
        } else if (binding.groupByYears.isChecked()) {
            return BarChartGroupType.YEARS;
        }
        throw new UnsupportedBarChartGroupTypeException();
    }
}
