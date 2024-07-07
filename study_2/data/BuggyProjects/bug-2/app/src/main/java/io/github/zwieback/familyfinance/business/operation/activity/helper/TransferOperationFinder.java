package io.github.zwieback.familyfinance.business.operation.activity.helper;

import io.github.zwieback.familyfinance.core.model.OperationView;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

final class TransferOperationFinder {

    private TransferOperationFinder() {
    }

    static OperationView findExpenseOperation(ReactiveEntityStore<Persistable> data,
                                              OperationView operation) {
        int expenseOperationId =
                TransferOperationQualifier.determineTransferExpenseOperationId(operation);
        if (expenseOperationId == operation.getId()) {
            return operation;
        }
        return findOperationById(data, expenseOperationId);
    }

    static OperationView findIncomeOperation(ReactiveEntityStore<Persistable> data,
                                             OperationView operation) {
        int incomeOperationId =
                TransferOperationQualifier.determineTransferIncomeOperationId(operation);
        if (incomeOperationId == operation.getId()) {
            return operation;
        }
        return findOperationById(data, incomeOperationId);
    }

    private static OperationView findOperationById(ReactiveEntityStore<Persistable> data,
                                                   int operationId) {
        return data.select(OperationView.class)
                .where(OperationView.ID.eq(operationId))
                .get().first();
    }
}
