package io.github.zwieback.familyfinance.business.chart.fragment;

import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.chart.dialog.HorizontalBarChartDisplayDialog;
import io.github.zwieback.familyfinance.business.operation.dialog.ExpenseOperationFilterDialog;
import io.github.zwieback.familyfinance.business.operation.filter.ExpenseOperationFilter;
import io.github.zwieback.familyfinance.business.operation.query.ExpenseOperationQueryBuilder;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.requery.query.Result;

public class HorizontalBarChartOfExpensesFragment
        extends HorizontalBarChartFragment<ExpenseOperationFilter> {

    @Override
    protected String getFilterName() {
        return ExpenseOperationFilter.EXPENSE_OPERATION_FILTER;
    }

    @Override
    protected ExpenseOperationFilter createDefaultFilter() {
        return new ExpenseOperationFilter();
    }

    @Override
    protected Result<OperationView> buildOperations() {
        return ExpenseOperationQueryBuilder.create(data)
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
        return R.string.data_set_expenses;
    }

    @ColorRes
    protected int getDataSetColor() {
        return R.color.colorExpense;
    }

    @Override
    public void onApplyFilter(ExpenseOperationFilter filter) {
        this.filter = filter;
        this.operationConverter = determineOperationConverter();
        refreshData();
    }

    @Override
    public void showFilterDialog() {
        DialogFragment dialog = ExpenseOperationFilterDialog.newInstance(filter,
                R.string.horizontal_bar_chart_of_expenses_filter_title);
        dialog.show(getChildFragmentManager(), "ExpenseOperationFilterDialog");
    }

    @Override
    public void showDisplayDialog() {
        DialogFragment dialog = HorizontalBarChartDisplayDialog.newInstance(display,
                R.string.horizontal_bar_chart_of_expenses_display_title);
        dialog.show(getChildFragmentManager(), "HorizontalBarChartDisplayDialog");
    }
}
