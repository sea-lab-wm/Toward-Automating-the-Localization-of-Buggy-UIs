package io.github.zwieback.familyfinance.business.chart.fragment;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.chart.display.BarChartDisplay;
import io.github.zwieback.familyfinance.business.chart.display.HorizontalBarChartDisplay;
import io.github.zwieback.familyfinance.business.chart.exception.UnsupportedHorizontalBarChartGroupByTypeException;
import io.github.zwieback.familyfinance.business.chart.marker.HorizontalBarChartMarkerView;
import io.github.zwieback.familyfinance.business.chart.service.builder.IdIndexMapStatefulBuilder;
import io.github.zwieback.familyfinance.business.chart.service.converter.OperationConverter;
import io.github.zwieback.familyfinance.business.chart.service.converter.bar.OperationHorizontalBarConverter;
import io.github.zwieback.familyfinance.business.chart.service.converter.bar.OperationHorizontalBarPercentConverter;
import io.github.zwieback.familyfinance.business.chart.service.formatter.LocalizedValueFormatter;
import io.github.zwieback.familyfinance.business.chart.service.grouper.OperationGrouper;
import io.github.zwieback.familyfinance.business.chart.service.grouper.pie.OperationGrouperByArticle;
import io.github.zwieback.familyfinance.business.chart.service.grouper.pie.OperationGrouperByArticleParent;
import io.github.zwieback.familyfinance.business.operation.filter.OperationFilter;
import io.github.zwieback.familyfinance.core.model.Article;
import io.github.zwieback.familyfinance.core.model.OperationView;

public abstract class HorizontalBarChartFragment<F extends OperationFilter>
        extends ChartFragment<HorizontalBarChart, BarEntry, F, HorizontalBarChartDisplay>
        implements OnChartValueSelectedListener {

    private static final float NORMAL_GRANULARITY = 1f;

    private static final int Y_AXIS_ANIMATION_DURATION = 500;

    private int maxBarCountOnScreen;
    private float barValueTextSize;
    private RectF onValueSelectedRectF;
    private IdIndexMapStatefulBuilder idIndexMapStatefulBuilder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maxBarCountOnScreen =
                getResources().getInteger(R.integer.max_horizontal_bar_count_on_screen);
        barValueTextSize = getResources().getDimension(R.dimen.bar_value_text_size);
        onValueSelectedRectF = new RectF();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshData();
    }

    @Override
    protected int getFragmentChartLayout() {
        return R.layout.fragment_chart_bar_horizontal;
    }

    @Override
    protected int getChartId() {
        return R.id.horizontal_bar_chart;
    }

    @Override
    protected String getDisplayName() {
        return BarChartDisplay.BAR_CHART_DISPLAY;
    }

    @Override
    protected HorizontalBarChartDisplay createDefaultDisplay() {
        return new HorizontalBarChartDisplay();
    }

    @Override
    protected void setupChart() {
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);

        int yAxisMinimum = getResources().getInteger(R.integer.y_axis_minimum);

        XAxis leftAxis = chart.getXAxis();
        leftAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularity(NORMAL_GRANULARITY);
        leftAxis.setLabelCount(maxBarCountOnScreen);

        YAxis topAxis = chart.getAxisLeft();
        topAxis.setDrawGridLines(true);
        topAxis.setAxisMinimum(yAxisMinimum);

        YAxis bottomAxis = chart.getAxisRight();
        bottomAxis.setDrawGridLines(false);
        bottomAxis.setAxisMinimum(yAxisMinimum);

        disableLegend();
    }

    private void disableLegend() {
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
    }

    private void setupXAxisValueFormatter(@NonNull IAxisValueFormatter xAxisFormatter) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
    }

    private void setupMarker(@NonNull IAxisValueFormatter xAxisFormatter,
                             @NonNull IAxisValueFormatter yAxisFormatter) {
        HorizontalBarChartMarkerView mv = new HorizontalBarChartMarkerView(extractContext(),
                xAxisFormatter, yAxisFormatter);
        mv.setChartView(chart);
        chart.setMarker(mv);
    }

    @Override
    protected void showData(Map<Float, List<OperationView>> groupedOperations) {
        if (groupedOperations.isEmpty()) {
            clearData(R.string.chart_no_data);
            return;
        }

        List<BarEntry> barEntries = convertOperations(groupedOperations);
        Map<Float, Float> idIndexMap = idIndexMapStatefulBuilder.build();

        String[] articleNames = convertToArticleNames(idIndexMap);
        IAxisValueFormatter xAxisFormatter = determineXAxisFormatter(articleNames);
        setupXAxisValueFormatter(xAxisFormatter);
        setupMarker(xAxisFormatter, determineYAxisFormatter());

        BarDataSet expenseSet = buildBarDataSet(barEntries, getDataSetLabel(), getDataSetColor(),
                display.isViewValues());

        BarData data = new BarData(expenseSet);
        data.setValueTextSize(barValueTextSize);
        data.setValueFormatter(determineDataSetValueFormatter());

        chart.setData(data);
        chart.setVisibleXRangeMaximum(maxBarCountOnScreen);
        fixChartWidth(groupedOperations.size());
        chart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
        chart.animateY(Y_AXIS_ANIMATION_DURATION);
    }

    @StringRes
    protected abstract int getDataSetLabel();

    @ColorRes
    protected abstract int getDataSetColor();

    private String[] convertToArticleNames(Map<Float, Float> idIndexMap) {
        Set<Integer> articleIds = Stream.of(idIndexMap.keySet())
                .map(Float::intValue)
                .collect(Collectors.toSet());
        List<Article> articles = data
                .select(Article.class, Article.ID, Article.NAME)
                .where(Article.ID.in(articleIds))
                .get().toList();
        String[] articleNames = new String[articles.size()];
        Stream.of(articles)
                .forEach(article -> {
                    float articleId = article.getId();
                    int barIndex = idIndexMap.get(articleId).intValue();
                    articleNames[barIndex] = article.getName();
                });
        return articleNames;
    }

    private BarDataSet buildBarDataSet(List<BarEntry> barEntries,
                                       @StringRes int dataSetLabel,
                                       @ColorRes int dataSetColor,
                                       boolean drawValuesEnabled) {
        BarDataSet dataSet = new BarDataSet(barEntries, getString(dataSetLabel));
        dataSet.setDrawIcons(false);
        dataSet.setColors(ContextCompat.getColor(extractContext(), dataSetColor));
        dataSet.setDrawValues(drawValuesEnabled);
        return dataSet;
    }

    private void fixChartWidth(int numberOfEntries) {
        if (numberOfEntries < maxBarCountOnScreen) {
            chart.fitScreen();
        } else {
            scaleToAcceptableSize(numberOfEntries);
        }
    }

    private void scaleToAcceptableSize(int numberOfEntries) {
        chart.getViewPortHandler().setMaximumScaleX(numberOfEntries / 2);
    }

    private void updateDrawValues() {
        chart.getData().setDrawValues(display.isViewValues());
        chart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null) {
            return;
        }
        RectF bounds = onValueSelectedRectF;
        chart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = chart.getPosition(e,
                chart.getData().getDataSetByIndex(h.getDataSetIndex()).getAxisDependency());
        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {
        // do nothing
    }

    @Override
    public void onApplyDisplay(HorizontalBarChartDisplay display) {
        if (this.display.needRefreshData(display)) {
            this.display = display;
            operationGrouper = determineOperationGrouper();
            operationConverter = determineOperationConverter();
            refreshData();
        } else {
            this.display = display;
            updateDrawValues();
        }
    }

    @Override
    protected OperationConverter<BarEntry> determineOperationConverter() {
        idIndexMapStatefulBuilder = IdIndexMapStatefulBuilder.create();
        if (display.isUsePercentValues()) {
            return new OperationHorizontalBarPercentConverter(extractContext(),
                    idIndexMapStatefulBuilder);
        }
        return new OperationHorizontalBarConverter(extractContext(), idIndexMapStatefulBuilder);
    }

    @Override
    protected OperationGrouper determineOperationGrouper() {
        switch (display.getGroupByType()) {
            case ARTICLE:
                return new OperationGrouperByArticle();
            case ARTICLE_PARENT:
                return new OperationGrouperByArticleParent();
        }
        throw new UnsupportedHorizontalBarChartGroupByTypeException();
    }

    @NonNull
    private IAxisValueFormatter determineXAxisFormatter(String[] articleNames) {
        return new IndexAxisValueFormatter(articleNames);
    }

    @NonNull
    private IAxisValueFormatter determineYAxisFormatter() {
        if (display.isUsePercentValues()) {
            return new PercentFormatter();
        }
        return new LocalizedValueFormatter();
    }

    @NonNull
    private IValueFormatter determineDataSetValueFormatter() {
        if (display.isUsePercentValues()) {
            return new PercentFormatter();
        }
        return new LargeValueFormatter();
    }
}
