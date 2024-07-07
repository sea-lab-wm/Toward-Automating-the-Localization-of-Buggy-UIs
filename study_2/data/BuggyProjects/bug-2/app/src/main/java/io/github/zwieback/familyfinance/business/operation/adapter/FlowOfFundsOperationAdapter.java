package io.github.zwieback.familyfinance.business.operation.adapter;

import android.content.Context;

import io.github.zwieback.familyfinance.business.operation.filter.FlowOfFundsOperationFilter;
import io.github.zwieback.familyfinance.business.operation.listener.OnOperationClickListener;
import io.github.zwieback.familyfinance.business.operation.query.FlowOfFundsOperationQueryBuilder;
import io.github.zwieback.familyfinance.business.operation.service.provider.FlowOfFundsOperationViewProvider;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.reactivex.ReactiveEntityStore;

public class FlowOfFundsOperationAdapter extends OperationAdapter<FlowOfFundsOperationFilter> {

    public FlowOfFundsOperationAdapter(Context context,
                                       OnOperationClickListener clickListener,
                                       ReactiveEntityStore<Persistable> data,
                                       FlowOfFundsOperationFilter filter) {
        super(context, clickListener, data, filter);
    }

    @Override
    protected EntityProvider<OperationView> createProvider(Context context) {
        return new FlowOfFundsOperationViewProvider(context);
    }

    @Override
    public Result<OperationView> performQuery() {
        return FlowOfFundsOperationQueryBuilder.create(data)
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
}
