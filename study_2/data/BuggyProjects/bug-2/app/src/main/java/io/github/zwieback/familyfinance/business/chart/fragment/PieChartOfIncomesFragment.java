package io.github.zwieback.familyfinance.business.chart.fragment;

import android.support.v4.app.DialogFragment;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.chart.dialog.PieChartDisplayDialog;
import io.github.zwieback.familyfinance.business.operation.dialog.IncomeOperationFilterDialog;
import io.github.zwieback.familyfinance.business.operation.filter.IncomeOperationFilter;
import io.github.zwieback.familyfinance.business.operation.query.IncomeOperationQueryBuilder;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.requery.query.Result;

public class PieChartOfIncomesFragment extends PieChartFragment<IncomeOperationFilter> {

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

    @Override
    protected int getDataSetLabel() {
        return R.string.data_set_incomes;
    }

    @Override
    public void showFilterDialog() {
        DialogFragment dialog = IncomeOperationFilterDialog.newInstance(filter,
                R.string.pie_chart_of_incomes_filter_title);
        dialog.show(getChildFragmentManager(), "IncomeOperationFilterDialog");
    }

    @Override
    public void showDisplayDialog() {
        DialogFragment dialog = PieChartDisplayDialog.newInstance(display,
                R.string.pie_chart_of_incomes_display_title);
        dialog.show(getChildFragmentManager(), "PieChartDisplayDialog");
    }
}
