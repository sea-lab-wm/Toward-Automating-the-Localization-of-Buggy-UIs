package io.github.zwieback.familyfinance.business.operation.fragment;

import android.os.Bundle;

import io.github.zwieback.familyfinance.business.operation.adapter.IncomeOperationAdapter;
import io.github.zwieback.familyfinance.business.operation.filter.IncomeOperationFilter;

import static io.github.zwieback.familyfinance.business.operation.filter.IncomeOperationFilter.INCOME_OPERATION_FILTER;

public class IncomeOperationFragment extends OperationFragment<IncomeOperationFilter> {

    public static IncomeOperationFragment newInstance(IncomeOperationFilter filter) {
        IncomeOperationFragment fragment = new IncomeOperationFragment();
        Bundle args = createArguments(INCOME_OPERATION_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected IncomeOperationAdapter createEntityAdapter() {
        IncomeOperationFilter filter = extractFilter(INCOME_OPERATION_FILTER);
        return new IncomeOperationAdapter(context, clickListener, data, filter);
    }
}
