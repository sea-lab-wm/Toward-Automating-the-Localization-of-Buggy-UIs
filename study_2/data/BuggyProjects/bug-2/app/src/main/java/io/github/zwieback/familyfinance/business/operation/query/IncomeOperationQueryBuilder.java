package io.github.zwieback.familyfinance.business.operation.query;

import java.util.Collections;

import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class IncomeOperationQueryBuilder
        extends OperationQueryBuilder<IncomeOperationQueryBuilder> {

    public static IncomeOperationQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new IncomeOperationQueryBuilder(data)
                .setTypes(Collections.singletonList(OperationType.INCOME_OPERATION));
    }

    private IncomeOperationQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }
}
