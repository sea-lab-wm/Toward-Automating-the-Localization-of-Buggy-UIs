package io.github.zwieback.familyfinance.core.model.type;

import java.util.Arrays;
import java.util.List;

public enum OperationType {
    EXPENSE_OPERATION,
    INCOME_OPERATION,
    TRANSFER_EXPENSE_OPERATION,
    TRANSFER_INCOME_OPERATION;

    private static List<OperationType> INCOME_TYPES = Arrays.asList(INCOME_OPERATION,
            TRANSFER_INCOME_OPERATION);
    private static List<OperationType> EXPENSE_TYPES = Arrays.asList(EXPENSE_OPERATION,
            TRANSFER_EXPENSE_OPERATION);

    public static List<OperationType> getIncomeTypes() {
        return INCOME_TYPES;
    }

    public static List<OperationType> getExpenseTypes() {
        return EXPENSE_TYPES;
    }
}
