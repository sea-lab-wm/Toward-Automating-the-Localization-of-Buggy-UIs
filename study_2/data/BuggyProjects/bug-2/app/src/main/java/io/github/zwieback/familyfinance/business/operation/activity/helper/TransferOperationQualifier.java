package io.github.zwieback.familyfinance.business.operation.activity.helper;

import io.github.zwieback.familyfinance.business.operation.activity.exception.IllegalOperationTypeException;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.core.model.type.OperationType;

final class TransferOperationQualifier {

    private TransferOperationQualifier() {
    }

    static int determineTransferExpenseOperationId(OperationView operation) {
        if (operation.getType() == OperationType.TRANSFER_EXPENSE_OPERATION) {
            return operation.getId();
        }
        if (operation.getType() == OperationType.TRANSFER_INCOME_OPERATION) {
            return operation.getLinkedTransferOperationId();
        }
        throw IllegalOperationTypeException.notTransferOperation(operation);
    }

    static int determineTransferIncomeOperationId(OperationView operation) {
        if (operation.getType() == OperationType.TRANSFER_EXPENSE_OPERATION) {
            return operation.getLinkedTransferOperationId();
        }
        if (operation.getType() == OperationType.TRANSFER_INCOME_OPERATION) {
            return operation.getId();
        }
        throw IllegalOperationTypeException.notTransferOperation(operation);
    }
}
