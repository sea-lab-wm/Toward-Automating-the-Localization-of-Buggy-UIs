package io.github.zwieback.familyfinance.business.operation.fragment;

import android.os.Bundle;

import io.github.zwieback.familyfinance.business.operation.adapter.FlowOfFundsOperationAdapter;
import io.github.zwieback.familyfinance.business.operation.filter.FlowOfFundsOperationFilter;

import static io.github.zwieback.familyfinance.business.operation.filter.FlowOfFundsOperationFilter.FLOW_OF_FUNDS_OPERATION_FILTER;

public class FlowOfFundsOperationFragment extends OperationFragment<FlowOfFundsOperationFilter> {

    public static FlowOfFundsOperationFragment newInstance(FlowOfFundsOperationFilter filter) {
        FlowOfFundsOperationFragment fragment = new FlowOfFundsOperationFragment();
        Bundle args = createArguments(FLOW_OF_FUNDS_OPERATION_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FlowOfFundsOperationAdapter createEntityAdapter() {
        FlowOfFundsOperationFilter filter = extractFilter(FLOW_OF_FUNDS_OPERATION_FILTER);
        return new FlowOfFundsOperationAdapter(context, clickListener, data, filter);
    }
}
