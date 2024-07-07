package io.github.zwieback.familyfinance.business.operation.adapter;

import android.content.Context;

import io.github.zwieback.familyfinance.business.operation.filter.ExpenseOperationFilter;
import io.github.zwieback.familyfinance.business.operation.listener.OnOperationClickListener;
import io.github.zwieback.familyfinance.business.operation.query.ExpenseOperationQueryBuilder;
import io.github.zwieback.familyfinance.business.operation.service.provider.ExpenseOperationViewProvider;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.reactivex.ReactiveEntityStore;

public class ExpenseOperationAdapter extends OperationAdapter<ExpenseOperationFilter> {

    public ExpenseOperationAdapter(Context context,
                                   OnOperationClickListener clickListener,
                                   ReactiveEntityStore<Persistable> data,
                                   ExpenseOperationFilter filter) {
        super(context, clickListener, data, filter);
    }

    @Override
    protected EntityProvider<OperationView> createProvider(Context context) {
        return new ExpenseOperationViewProvider(context);
    }

    @Override
    public Result<OperationView> performQuery() {
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
}
