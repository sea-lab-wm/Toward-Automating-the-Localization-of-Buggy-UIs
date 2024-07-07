package io.github.zwieback.familyfinance.business.chart.fragment;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.chart.display.PieChartDisplay;
import io.github.zwieback.familyfinance.business.chart.exception.UnsupportedPieChartGroupByTypeException;
import io.github.zwieback.familyfinance.business.chart.exception.UnsupportedPieChartGroupingTypeException;
import io.github.zwieback.familyfinance.business.chart.marker.PieChartMarkerView;
import io.github.zwieback.familyfinance.business.chart.service.converter.OperationConverter;
import io.github.zwieback.familyfinance.business.chart.service.converter.pie.OperationPieLimitConverter;
import io.github.zwieback.familyfinance.business.chart.service.converter.pie.OperationPieSimpleConverter;
import io.github.zwieback.familyfinance.business.chart.service.formatter.LocalizedValueFormatter;
import io.github.zwieback.familyfinance.business.chart.service.grouper.OperationGrouper;
import io.github.zwieback.familyfinance.business.chart.service.grouper.pie.OperationGrouperByArticle;
import io.github.zwieback.familyfinance.business.chart.service.grouper.pie.OperationGrouperByArticleParent;
import io.github.zwieback.familyfinance.business.operation.filter.OperationFilter;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.util.ColorUtils;
import io.github.zwieback.familyfinance.util.ConfigurationUtils;

public abstract class PieChartFragment<F extends OperationFilter> extends ChartFragment<PieChart,
        PieEntry, F, PieChartDisplay> {

    private static final float SLICE_SPACE = 2f;

    private static final int Y_AXIS_ANIMATION_DURATION = 500;

    private float pieValueTextSize;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pieValueTextSize = getResources().getDimension(R.dimen.pie_value_text_size);
    }

    @Override
    protected int getFragmentChartLayout() {
        return R.layout.fragment_chart_pie;
    }

    @Override
    protected int getChartId() {
        return R.id.pie_chart;
    }

    @Override
    protected String getDisplayName() {
        return PieChartDisplay.PIE_CHART_DISPLAY;
    }

    @Override
    protected PieChartDisplay createDefaultDisplay() {
        return new PieChartDisplay();
    }

    @Override
    protected void setupChart() {
        chart.setUsePercentValues(display.isUsePercentValues());
        chart.getDescription().setEnabled(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(pieValueTextSize);

        setupLegend();
        setupMarker();
    }

    private void setupLegend() {
        Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setOrientation(determineLegendOrientation());
        legend.setWordWrapEnabled(true);
    }

    private Legend.LegendOrientation determineLegendOrientation() {
        return ConfigurationUtils.getOrientation() == Configuration.ORIENTATION_PORTRAIT
                ? Legend.LegendOrientation.HORIZONTAL
                : Legend.LegendOrientation.VERTICAL;
    }

    private void setupMarker() {
        PieChartMarkerView mv = new PieChartMarkerView(extractContext(),
                new LocalizedValueFormatter());
        mv.setChartView(chart);
        chart.setMarker(mv);
    }

    @Override
    protected void showData(Map<Float, List<OperationView>> groupedOperations) {
        if (groupedOperations.isEmpty()) {
            clearData(R.string.chart_no_data);
            return;
        }

        PieDataSet expenseSet = buildPieDataSet(groupedOperations, getDataSetLabel());

        PieData data = new PieData(expenseSet);
        data.setValueFormatter(determineFormatter());
        data.setDrawValues(display.isViewValues());

        chart.setUsePercentValues(display.isUsePercentValues());
        chart.setData(data);
        chart.animateY(Y_AXIS_ANIMATION_DURATION, Easing.EasingOption.EaseInOutQuad);
    }

    @StringRes
    protected abstract int getDataSetLabel();

    private PieDataSet buildPieDataSet(Map<Float, List<OperationView>> operations,
                                       @StringRes int dataSetLabel) {
        List<Integer> colors = collectDataColors();
        List<PieEntry> pieEntries = convertOperations(operations);
        PieDataSet dataSet = new PieDataSet(pieEntries, getString(dataSetLabel));
        dataSet.setDrawIcons(false);
        dataSet.setColors(colors);
        dataSet.setSliceSpace(SLICE_SPACE);
        dataSet.setValueTextSize(pieValueTextSize);
        return dataSet;
    }

    private List<Integer> collectDataColors() {
        List<Integer> colors = ColorUtils.collectMaterialDesignColors(extractContext());
        Collections.shuffle(colors);
        return colors;
    }

    @Override
    public void onApplyFilter(F filter) {
        this.filter = filter;
        refreshData();
    }

    @Override
    public void onApplyDisplay(PieChartDisplay display) {
        if (this.display.needRefreshData(display)) {
            this.display = display;
            operationConverter = determineOperationConverter();
            operationGrouper = determineOperationGrouper();
            refreshData();
        } else {
            this.display = display;
            chart.getData().setDrawValues(display.isViewValues());
            chart.getData().setValueFormatter(determineFormatter());
            chart.setUsePercentValues(display.isUsePercentValues());
            chart.invalidate();
        }
    }

    @Override
    protected OperationConverter<PieEntry> determineOperationConverter() {
        switch (display.getGroupingType()) {
            case SIMPLE:
                return new OperationPieSimpleConverter(extractContext(), display.getGroupByType());
            case LIMIT:
                return new OperationPieLimitConverter(extractContext(), display.getGroupByType());
        }
        throw new UnsupportedPieChartGroupingTypeException();
    }

    @Override
    protected OperationGrouper determineOperationGrouper() {
        switch (display.getGroupByType()) {
            case ARTICLE:
                return new OperationGrouperByArticle();
            case ARTICLE_PARENT:
                return new OperationGrouperByArticleParent();
        }
        throw new UnsupportedPieChartGroupByTypeException();
    }

    private IValueFormatter determineFormatter() {
        return display.isUsePercentValues()
                ? new PercentFormatter()
                : new LargeValueFormatter();
    }
}
