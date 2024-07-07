package io.github.zwieback.familyfinance.business.operation.query;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class FlowOfFundsOperationQueryBuilder
        extends OperationQueryBuilder<FlowOfFundsOperationQueryBuilder> {

    public static FlowOfFundsOperationQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new FlowOfFundsOperationQueryBuilder(data);
    }

    private FlowOfFundsOperationQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }
}
