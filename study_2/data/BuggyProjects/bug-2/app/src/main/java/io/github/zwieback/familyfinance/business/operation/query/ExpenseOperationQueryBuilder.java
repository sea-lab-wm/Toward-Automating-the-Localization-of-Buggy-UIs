package io.github.zwieback.familyfinance.business.operation.query;

import java.util.Collections;

import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class ExpenseOperationQueryBuilder
        extends OperationQueryBuilder<ExpenseOperationQueryBuilder> {

    public static ExpenseOperationQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new ExpenseOperationQueryBuilder(data)
                .setTypes(Collections.singletonList(OperationType.EXPENSE_OPERATION));
    }

    private ExpenseOperationQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }
}
