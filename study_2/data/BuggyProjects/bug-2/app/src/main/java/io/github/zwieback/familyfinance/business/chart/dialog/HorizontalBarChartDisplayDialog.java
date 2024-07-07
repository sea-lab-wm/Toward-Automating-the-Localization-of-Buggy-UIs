package io.github.zwieback.familyfinance.business.chart.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.chart.display.HorizontalBarChartDisplay;
import io.github.zwieback.familyfinance.business.chart.display.type.HorizontalBarChartGroupByType;
import io.github.zwieback.familyfinance.business.chart.exception.UnsupportedHorizontalBarChartGroupByTypeException;
import io.github.zwieback.familyfinance.databinding.DialogDisplayChartBarHorizontalBinding;

import static io.github.zwieback.familyfinance.business.chart.display.HorizontalBarChartDisplay.HORIZONTAL_BAR_CHART_DISPLAY;

public class HorizontalBarChartDisplayDialog extends ChartDisplayDialog<HorizontalBarChartDisplay,
        DialogDisplayChartBarHorizontalBinding> {

    public static HorizontalBarChartDisplayDialog newInstance(HorizontalBarChartDisplay display,
                                                              @StringRes int dialogTitleId) {
        HorizontalBarChartDisplayDialog fragment = new HorizontalBarChartDisplayDialog();
        Bundle args = createArguments(HORIZONTAL_BAR_CHART_DISPLAY, display, dialogTitleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected HorizontalBarChartDisplay createCopyOfDisplay(HorizontalBarChartDisplay display) {
        return new HorizontalBarChartDisplay(display);
    }

    protected String getInputDisplayName() {
        return HORIZONTAL_BAR_CHART_DISPLAY;
    }

    @Override
    protected int getDialogTitle() {
        return R.string.horizontal_bar_chart_of_expenses_display_title;
    }

    @Override
    protected int getDialogLayoutId() {
        return R.layout.dialog_display_chart_bar_horizontal;
    }

    protected void bind(HorizontalBarChartDisplay display) {
        binding.setDisplay(display);
    }

    protected void updateDisplayProperties() {
        display.setGroupByType(determineGroupByType());
    }

    @NonNull
    private HorizontalBarChartGroupByType determineGroupByType() {
        if (binding.groupByDays.isChecked()) {
            return HorizontalBarChartGroupByType.ARTICLE;
        } else if (binding.groupByWeeks.isChecked()) {
            return HorizontalBarChartGroupByType.ARTICLE_PARENT;
        }
        throw new UnsupportedHorizontalBarChartGroupByTypeException();
    }
}
