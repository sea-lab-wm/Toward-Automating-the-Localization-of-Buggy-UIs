package io.github.zwieback.familyfinance.business.operation.service.provider;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.typeface.IIcon;

import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.type.OperationType;

public class TransferOperationProvider extends EntityProvider<Operation> {

    private final IncomeOperationProvider incomeProvider;
    private final ExpenseOperationProvider expenseProvider;

    public TransferOperationProvider(Context context) {
        super(context);
        incomeProvider = new IncomeOperationProvider(context);
        expenseProvider = new ExpenseOperationProvider(context);
    }

    @NonNull
    @Override
    public IIcon provideDefaultIcon(Operation operation) {
        return FontAwesome.Icon.faw_exchange_alt;
    }

    @Override
    public int provideDefaultIconColor(Operation operation) {
        if (operation.getType() == OperationType.TRANSFER_EXPENSE_OPERATION) {
            return expenseProvider.provideDefaultIconColor(operation);
        }
        return incomeProvider.provideDefaultIconColor(operation);
    }

    @Override
    public int provideTextColor(Operation operation) {
        if (operation.getType() == OperationType.TRANSFER_EXPENSE_OPERATION) {
            return expenseProvider.provideTextColor(operation);
        }
        return incomeProvider.provideTextColor(operation);
    }
}
