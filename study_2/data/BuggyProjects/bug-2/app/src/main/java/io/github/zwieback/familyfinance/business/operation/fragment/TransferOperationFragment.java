package io.github.zwieback.familyfinance.business.operation.fragment;

import android.os.Bundle;

import io.github.zwieback.familyfinance.business.operation.adapter.TransferOperationAdapter;
import io.github.zwieback.familyfinance.business.operation.filter.TransferOperationFilter;

import static io.github.zwieback.familyfinance.business.operation.filter.TransferOperationFilter.TRANSFER_OPERATION_FILTER;

public class TransferOperationFragment extends OperationFragment<TransferOperationFilter> {

    public static TransferOperationFragment newInstance(TransferOperationFilter filter) {
        TransferOperationFragment fragment = new TransferOperationFragment();
        Bundle args = createArguments(TRANSFER_OPERATION_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected TransferOperationAdapter createEntityAdapter() {
        TransferOperationFilter filter = extractFilter(TRANSFER_OPERATION_FILTER);
        return new TransferOperationAdapter(context, clickListener, data, filter);
    }
}
