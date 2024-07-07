package io.github.zwieback.familyfinance.business.operation.query;

import java.util.Arrays;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

import static io.github.zwieback.familyfinance.core.model.type.OperationType.TRANSFER_EXPENSE_OPERATION;
import static io.github.zwieback.familyfinance.core.model.type.OperationType.TRANSFER_INCOME_OPERATION;

public class TransferOperationQueryBuilder
        extends OperationQueryBuilder<TransferOperationQueryBuilder> {

    public static TransferOperationQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new TransferOperationQueryBuilder(data)
                .setTypes(Arrays.asList(TRANSFER_EXPENSE_OPERATION, TRANSFER_INCOME_OPERATION));
    }

    private TransferOperationQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }
}
