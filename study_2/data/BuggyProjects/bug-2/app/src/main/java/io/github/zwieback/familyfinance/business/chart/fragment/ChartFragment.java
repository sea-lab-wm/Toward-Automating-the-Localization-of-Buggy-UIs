package io.github.zwieback.familyfinance.business.chart.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.Entry;

import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.app.FamilyFinanceApplication;
import io.github.zwieback.familyfinance.business.chart.display.ChartDisplay;
import io.github.zwieback.familyfinance.business.chart.listener.ChartDisplayListener;
import io.github.zwieback.familyfinance.business.chart.service.converter.OperationConverter;
import io.github.zwieback.familyfinance.business.chart.service.grouper.OperationGrouper;
import io.github.zwieback.familyfinance.business.operation.filter.OperationFilter;
import io.github.zwieback.familyfinance.business.operation.listener.OperationFilterListener;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class ChartFragment<
        C extends Chart,
        E extends Entry,
        F extends OperationFilter,
        D extends ChartDisplay>
        extends Fragment
        implements OperationFilterListener<F>, ChartDisplayListener<D> {

    protected ReactiveEntityStore<Persistable> data;
    protected F filter;
    protected D display;
    protected C chart;

    protected OperationConverter<E> operationConverter;
    protected OperationGrouper operationGrouper;

    private boolean dataLoaded;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = ((FamilyFinanceApplication) ((Activity) extractContext()).getApplication()).getData();
        filter = loadFilter(savedInstanceState);
        display = loadDisplay(savedInstanceState);

        operationConverter = determineOperationConverter();
        operationGrouper = determineOperationGrouper();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(getFragmentChartLayout(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        chart = view.findViewById(getChartId());
        setupChart();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getFilterName(), filter);
        outState.putParcelable(getDisplayName(), display);
    }

    private F loadFilter(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return createDefaultFilter();
        }
        return savedInstanceState.getParcelable(getFilterName());
    }

    private D loadDisplay(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return createDefaultDisplay();
        }
        return savedInstanceState.getParcelable(getDisplayName());
    }

    protected abstract String getFilterName();

    protected abstract F createDefaultFilter();

    protected abstract String getDisplayName();

    protected abstract D createDefaultDisplay();

    @LayoutRes
    protected abstract int getFragmentChartLayout();

    @IdRes
    protected abstract int getChartId();

    protected abstract void setupChart();

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    public void refreshData() {
        dataLoaded = true;
        clearData(R.string.chart_loading);

        Observable.fromCallable(this::buildOperations)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(this::groupOperations)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showData);
    }

    protected void clearData(@StringRes int noDataTextRes) {
        chart.setNoDataText(getString(noDataTextRes));
        chart.clear();
    }

    protected abstract Result<OperationView> buildOperations();

    private Map<Float, List<OperationView>> groupOperations(Result<OperationView> operations) {
        return operationGrouper.group(operations.toList(),
                filter.getStartDate(), filter.getEndDate());
    }

    protected List<E> convertOperations(Map<Float, List<OperationView>> groupedOperations) {
        return operationConverter.convertToEntries(groupedOperations);
    }

    protected abstract void showData(Map<Float, List<OperationView>> groupedOperations);

    public abstract void showFilterDialog();

    public abstract void showDisplayDialog();

    protected abstract OperationConverter<E> determineOperationConverter();

    protected abstract OperationGrouper determineOperationGrouper();

    @NonNull
    protected Context extractContext() {
        Context context = super.getContext();
        if (context != null) {
            return context;
        }
        throw new IllegalStateException("Context is null");
    }
}
