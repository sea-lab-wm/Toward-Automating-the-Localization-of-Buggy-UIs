package io.github.zwieback.familyfinance.business.chart.fragment;

import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.chart.dialog.HorizontalBarChartDisplayDialog;
import io.github.zwieback.familyfinance.business.operation.dialog.IncomeOperationFilterDialog;
import io.github.zwieback.familyfinance.business.operation.filter.IncomeOperationFilter;
import io.github.zwieback.familyfinance.business.operation.query.IncomeOperationQueryBuilder;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.requery.query.Result;

public class HorizontalBarChartOfIncomesFragment
        extends HorizontalBarChartFragment<IncomeOperationFilter> {

    @Override
    protected String getFilterName() {
        return IncomeOperationFilter.INCOME_OPERATION_FILTER;
    }

    @Override
    protected IncomeOperationFilter createDefaultFilter() {
        return new IncomeOperationFilter();
    }

    @Override
    protected Result<OperationView> buildOperations() {
        return IncomeOperationQueryBuilder.create(data)
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

    @StringRes
    protected int getDataSetLabel() {
        return R.string.data_set_incomes;
    }

    @ColorRes
    protected int getDataSetColor() {
        return R.color.colorIncome;
    }

    @Override
    public void onApplyFilter(IncomeOperationFilter filter) {
        this.filter = filter;
        this.operationConverter = determineOperationConverter();
        refreshData();
    }

    @Override
    public void showFilterDialog() {
        DialogFragment dialog = IncomeOperationFilterDialog.newInstance(filter,
                R.string.horizontal_bar_chart_of_incomes_filter_title);
        dialog.show(getChildFragmentManager(), "IncomeOperationFilterDialog");
    }

    @Override
    public void showDisplayDialog() {
        DialogFragment dialog = HorizontalBarChartDisplayDialog.newInstance(display,
                R.string.horizontal_bar_chart_of_incomes_display_title);
        dialog.show(getChildFragmentManager(), "HorizontalBarChartDisplayDialog");
    }
}
