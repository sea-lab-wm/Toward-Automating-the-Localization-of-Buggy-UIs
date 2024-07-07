package io.github.zwieback.familyfinance.business.chart.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.chart.display.PieChartDisplay;
import io.github.zwieback.familyfinance.business.chart.display.type.PieChartGroupByType;
import io.github.zwieback.familyfinance.business.chart.display.type.PieChartGroupingType;
import io.github.zwieback.familyfinance.business.chart.exception.UnsupportedPieChartGroupByTypeException;
import io.github.zwieback.familyfinance.business.chart.exception.UnsupportedPieChartGroupingTypeException;
import io.github.zwieback.familyfinance.databinding.DialogDisplayChartPieBinding;

import static io.github.zwieback.familyfinance.business.chart.display.PieChartDisplay.PIE_CHART_DISPLAY;

public class PieChartDisplayDialog extends ChartDisplayDialog<PieChartDisplay,
        DialogDisplayChartPieBinding> {

    public static PieChartDisplayDialog newInstance(PieChartDisplay display,
                                                    @StringRes int dialogTitleId) {
        PieChartDisplayDialog fragment = new PieChartDisplayDialog();
        Bundle args = createArguments(PIE_CHART_DISPLAY, display, dialogTitleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected PieChartDisplay createCopyOfDisplay(PieChartDisplay display) {
        return new PieChartDisplay(display);
    }

    protected String getInputDisplayName() {
        return PIE_CHART_DISPLAY;
    }

    @Override
    protected int getDialogTitle() {
        return R.string.pie_chart_of_expenses_display_title;
    }

    @Override
    protected int getDialogLayoutId() {
        return R.layout.dialog_display_chart_pie;
    }

    protected void bind(PieChartDisplay display) {
        binding.setDisplay(display);
    }

    protected void updateDisplayProperties() {
        display.setGroupingType(determineGroupingType());
        display.setGroupByType(determineGroupByType());
    }

    @NonNull
    private PieChartGroupingType determineGroupingType() {
        if (binding.groupingSimple.isChecked()) {
            return PieChartGroupingType.SIMPLE;
        } else if (binding.groupingLimit.isChecked()) {
            return PieChartGroupingType.LIMIT;
        }
        throw new UnsupportedPieChartGroupingTypeException();
    }

    @NonNull
    private PieChartGroupByType determineGroupByType() {
        if (binding.groupByArticle.isChecked()) {
            return PieChartGroupByType.ARTICLE;
        } else if (binding.groupByArticleParent.isChecked()) {
            return PieChartGroupByType.ARTICLE_PARENT;
        }
        throw new UnsupportedPieChartGroupByTypeException();
    }
}
