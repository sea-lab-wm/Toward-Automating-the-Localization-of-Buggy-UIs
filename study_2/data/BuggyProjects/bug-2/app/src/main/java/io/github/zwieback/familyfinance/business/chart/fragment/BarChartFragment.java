package io.github.zwieback.familyfinance.business.chart.fragment;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.annimon.stream.Stream;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.chart.dialog.BarChartDisplayDialog;
import io.github.zwieback.familyfinance.business.chart.display.BarChartDisplay;
import io.github.zwieback.familyfinance.business.chart.exception.UnsupportedBarChartGroupTypeException;
import io.github.zwieback.familyfinance.business.chart.marker.BarChartMarkerView;
import io.github.zwieback.familyfinance.business.chart.service.converter.OperationConverter;
import io.github.zwieback.familyfinance.business.chart.service.converter.bar.OperationBarConverter;
import io.github.zwieback.familyfinance.business.chart.service.formatter.DayValueFormatter;
import io.github.zwieback.familyfinance.business.chart.service.formatter.LocalizedValueFormatter;
import io.github.zwieback.familyfinance.business.chart.service.formatter.MonthValueFormatter;
import io.github.zwieback.familyfinance.business.chart.service.formatter.QuarterValueFormatter;
import io.github.zwieback.familyfinance.business.chart.service.formatter.WeekValueFormatter;
import io.github.zwieback.familyfinance.business.chart.service.formatter.YearValueFormatter;
import io.github.zwieback.familyfinance.business.chart.service.grouper.OperationGrouper;
import io.github.zwieback.familyfinance.business.chart.service.grouper.bar.OperationGrouperByDay;
import io.github.zwieback.familyfinance.business.chart.service.grouper.bar.OperationGrouperByMonth;
import io.github.zwieback.familyfinance.business.chart.service.grouper.bar.OperationGrouperByQuarter;
import io.github.zwieback.familyfinance.business.chart.service.grouper.bar.OperationGrouperByWeek;
import io.github.zwieback.familyfinance.business.chart.service.grouper.bar.OperationGrouperByYear;
import io.github.zwieback.familyfinance.business.chart.service.sieve.OperationSieve;
import io.github.zwieback.familyfinance.business.operation.dialog.FlowOfFundsOperationFilterDialog;
import io.github.zwieback.familyfinance.business.operation.filter.FlowOfFundsOperationFilter;
import io.github.zwieback.familyfinance.business.operation.query.FlowOfFundsOperationQueryBuilder;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.requery.query.Result;

public class BarChartFragment extends ChartFragment<BarChart, BarEntry, FlowOfFundsOperationFilter,
        BarChartDisplay> implements OnChartValueSelectedListener {

    private static final float NORMAL_GRANULARITY = 1f;
    private static final float X_AXIS_MAXIMUM_FIX = 1f;

    // (barWidth + barSpace) * 2 + groupSpace = 1.00 -> interval per "group"
    private static final float GROUP_SPACE = 0.08f;
    private static final float BAR_SPACE = 0.06f; // x2 DataSet
    private static final float BAR_WIDTH = 0.4f; // x2 DataSet

    private static final int INCOME_BAR_SET = 0;
    private static final int EXPENSE_BAR_SET = 1;
    private static final int Y_AXIS_ANIMATION_DURATION = 500;

    private int maxBarCountOnScreen;
    private float barValueTextSize;
    private RectF onValueSelectedRectF;
    private OperationSieve operationSieve;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maxBarCountOnScreen = getResources().getInteger(R.integer.max_bar_count_on_screen);
        barValueTextSize = getResources().getDimension(R.dimen.bar_value_text_size);
        onValueSelectedRectF = new RectF();
        operationSieve = new OperationSieve();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshData();
    }

    @Override
    protected int getFragmentChartLayout() {
        return R.layout.fragment_chart_bar;
    }

    @Override
    protected int getChartId() {
        return R.id.bar_chart;
    }

    @Override
    protected String getFilterName() {
        return FlowOfFundsOperationFilter.FLOW_OF_FUNDS_OPERATION_FILTER;
    }

    @Override
    protected FlowOfFundsOperationFilter createDefaultFilter() {
        return new FlowOfFundsOperationFilter();
    }

    @Override
    protected String getDisplayName() {
        return BarChartDisplay.BAR_CHART_DISPLAY;
    }

    @Override
    protected BarChartDisplay createDefaultDisplay() {
        return new BarChartDisplay();
    }

    @Override
    protected void setupChart() {
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);

        IAxisValueFormatter xAxisFormatter = determineXAxisFormatter();
        IAxisValueFormatter yAxisFormatter = new LargeValueFormatter();
        float xAxisYOffset = getResources().getDimension(R.dimen.x_axis_y_offset);
        int yAxisMinimum = getResources().getInteger(R.integer.y_axis_minimum);
        int xAxisRotationAngle = getResources().getInteger(R.integer.x_axis_label_rotation_angle);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(NORMAL_GRANULARITY);
        xAxis.setLabelCount(maxBarCountOnScreen);
        xAxis.setLabelRotationAngle(xAxisRotationAngle);
        xAxis.setYOffset(xAxisYOffset);
        xAxis.setValueFormatter(xAxisFormatter);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(yAxisMinimum);
        leftAxis.setValueFormatter(yAxisFormatter);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setAxisMinimum(yAxisMinimum);
        rightAxis.setValueFormatter(yAxisFormatter);

        setupMarker(xAxisFormatter);
    }

    private void setupXAxisValueFormatter(IAxisValueFormatter xAxisFormatter) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
    }

    private void setupMarker(IAxisValueFormatter xAxisFormatter) {
        BarChartMarkerView mv = new BarChartMarkerView(extractContext(), xAxisFormatter,
                new LocalizedValueFormatter());
        mv.setChartView(chart);
        chart.setMarker(mv);
    }

    @Override
    protected Result<OperationView> buildOperations() {
        return FlowOfFundsOperationQueryBuilder.create(data)
                .setTypes(determineOperationTypes())
                .setStartDate(filter.getStartDate())
                .setEndDate(filter.getEndDate())
                .setStartValue(filter.getStartValue())
                .setEndValue(filter.getEndValue())
                .setOwnerId(filter.getOwnerId())
                .setCurrencyId(filter.getCurrencyId())
                .setArticleId(filter.getArticleId())
                .setAccountId(filter.getAccountId())
                .build();
    }

    private Map<Float, List<OperationView>> filterOperations(
            Map<Float, List<OperationView>> operations,
            List<OperationType> types) {
        return operationSieve.filterByTypes(operations, types);
    }

    @Override
    protected void showData(Map<Float, List<OperationView>> groupedOperations) {
        if (groupedOperations.isEmpty()) {
            clearData(R.string.chart_no_data);
            return;
        }

        BarDataSet incomeSet = buildBarDataSet(groupedOperations, OperationType.getIncomeTypes(),
                R.string.data_set_incomes, R.color.colorIncome, display.isViewIncomeValues(),
                display.isViewIncomes());

        BarDataSet expenseSet = buildBarDataSet(groupedOperations, OperationType.getExpenseTypes(),
                R.string.data_set_expenses, R.color.colorExpense, display.isViewExpenseValues(),
                display.isViewExpenses());

        BarData data = new BarData(incomeSet, expenseSet);
        data.setValueTextSize(barValueTextSize);
        data.setValueFormatter(new LargeValueFormatter());

        float minX = Stream.of(groupedOperations.keySet()).min(Float::compareTo).get();
        float maxX = Stream.of(groupedOperations.keySet()).max(Float::compareTo).get();

        chart.setData(data);
        chart.getBarData().setBarWidth(BAR_WIDTH);
        chart.getXAxis().setAxisMinimum(minX);
        chart.getXAxis().setAxisMaximum(maxX + X_AXIS_MAXIMUM_FIX);
        chart.groupBars(minX, GROUP_SPACE, BAR_SPACE);
        chart.setVisibleXRangeMaximum(maxBarCountOnScreen);
        fixChartWidth(groupedOperations.size());
        chart.animateY(Y_AXIS_ANIMATION_DURATION);
    }

    private BarDataSet buildBarDataSet(Map<Float, List<OperationView>> groupedOperations,
                                       List<OperationType> types,
                                       @StringRes int dataSetLabel,
                                       @ColorRes int dataSetColor,
                                       boolean drawValuesEnabled,
                                       boolean visible) {
        Map<Float, List<OperationView>> operations = filterOperations(groupedOperations, types);
        List<BarEntry> barEntries = convertOperations(operations);
        BarDataSet dataSet = new BarDataSet(barEntries, getString(dataSetLabel));
        dataSet.setDrawIcons(false);
        dataSet.setColors(ContextCompat.getColor(extractContext(), dataSetColor));
        dataSet.setDrawValues(drawValuesEnabled);
        dataSet.setVisible(visible);
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
        updateDrawValues(INCOME_BAR_SET, display.isViewIncomeValues());
        updateDrawValues(EXPENSE_BAR_SET, display.isViewExpenseValues());
        chart.invalidate();
    }

    private void updateDrawValues(int index, boolean enabled) {
        chart.getBarData().getDataSetByIndex(index).setDrawValues(enabled);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null) {
            return;
        }
        RectF bounds = onValueSelectedRectF;
        chart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = chart.getPosition(e, YAxis.AxisDependency.LEFT);
        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {
        // do nothing
    }

    @Override
    public void onApplyFilter(FlowOfFundsOperationFilter filter) {
        this.filter = filter;
        refreshData();
    }

    @Override
    public void onApplyDisplay(BarChartDisplay display) {
        if (this.display.needRefreshData(display)) {
            this.display = display;
            operationGrouper = determineOperationGrouper();
            IAxisValueFormatter xAxisFormatter = determineXAxisFormatter();
            setupXAxisValueFormatter(xAxisFormatter);
            setupMarker(xAxisFormatter);
            refreshData();
        } else {
            this.display = display;
            updateDrawValues();
        }
    }

    @Override
    public void showFilterDialog() {
        DialogFragment dialog = FlowOfFundsOperationFilterDialog.newInstance(filter,
                R.string.bar_chart_filter_title);
        dialog.show(getChildFragmentManager(), "FlowOfFundsOperationFilterDialog");
    }

    @Override
    public void showDisplayDialog() {
        DialogFragment dialog = BarChartDisplayDialog.newInstance(display);
        dialog.show(getChildFragmentManager(), "BarChartDisplayDialog");
    }

    @Override
    protected OperationConverter<BarEntry> determineOperationConverter() {
        return new OperationBarConverter(extractContext());
    }

    @Override
    protected OperationGrouper determineOperationGrouper() {
        switch (display.getGroupType()) {
            case DAYS:
                return new OperationGrouperByDay();
            case WEEKS:
                return new OperationGrouperByWeek();
            case MONTHS:
                return new OperationGrouperByMonth();
            case QUARTERS:
                return new OperationGrouperByQuarter();
            case YEARS:
                return new OperationGrouperByYear();
        }
        throw new UnsupportedBarChartGroupTypeException();
    }

    private IAxisValueFormatter determineXAxisFormatter() {
        switch (display.getGroupType()) {
            case DAYS:
                return new DayValueFormatter();
            case WEEKS:
                return new WeekValueFormatter();
            case MONTHS:
                return new MonthValueFormatter();
            case QUARTERS:
                return new QuarterValueFormatter();
            case YEARS:
                return new YearValueFormatter();
        }
        throw new UnsupportedBarChartGroupTypeException();
    }

    private List<OperationType> determineOperationTypes() {
        List<OperationType> types = new ArrayList<>();
        if (display.isViewIncomes()) {
            types.add(OperationType.INCOME_OPERATION);
            if (display.isIncludeTransfers()) {
                types.add(OperationType.TRANSFER_INCOME_OPERATION);
            }
        }
        if (display.isViewExpenses()) {
            types.add(OperationType.EXPENSE_OPERATION);
            if (display.isIncludeTransfers()) {
                types.add(OperationType.TRANSFER_EXPENSE_OPERATION);
            }
        }
        return types;
    }
}
