package io.github.zwieback.familyfinance.business.operation.fragment;

import android.os.Bundle;

import io.github.zwieback.familyfinance.business.operation.adapter.ExpenseOperationAdapter;
import io.github.zwieback.familyfinance.business.operation.filter.ExpenseOperationFilter;

import static io.github.zwieback.familyfinance.business.operation.filter.ExpenseOperationFilter.EXPENSE_OPERATION_FILTER;

public class ExpenseOperationFragment extends OperationFragment<ExpenseOperationFilter> {

    public static ExpenseOperationFragment newInstance(ExpenseOperationFilter filter) {
        ExpenseOperationFragment fragment = new ExpenseOperationFragment();
        Bundle args = createArguments(EXPENSE_OPERATION_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected ExpenseOperationAdapter createEntityAdapter() {
        ExpenseOperationFilter filter = extractFilter(EXPENSE_OPERATION_FILTER);
        return new ExpenseOperationAdapter(context, clickListener, data, filter);
    }
}
