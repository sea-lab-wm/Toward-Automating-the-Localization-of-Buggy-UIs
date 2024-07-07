package io.github.zwieback.familyfinance.business.operation.service.provider;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.typeface.IIcon;

import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.core.model.type.OperationType;

public class TransferOperationViewProvider extends EntityProvider<OperationView> {

    private final IncomeOperationViewProvider incomeProvider;
    private final ExpenseOperationViewProvider expenseProvider;

    public TransferOperationViewProvider(Context context) {
        super(context);
        incomeProvider = new IncomeOperationViewProvider(context);
        expenseProvider = new ExpenseOperationViewProvider(context);
    }

    @NonNull
    @Override
    public IIcon provideDefaultIcon(OperationView operation) {
        return FontAwesome.Icon.faw_exchange_alt;
    }

    @Override
    public int provideDefaultIconColor(OperationView operation) {
        if (operation.getType() == OperationType.TRANSFER_EXPENSE_OPERATION) {
            return expenseProvider.provideDefaultIconColor(operation);
        }
        return incomeProvider.provideDefaultIconColor(operation);
    }

    @Override
    public int provideTextColor(OperationView operation) {
        if (operation.getType() == OperationType.TRANSFER_EXPENSE_OPERATION) {
            return expenseProvider.provideTextColor(operation);
        }
        return incomeProvider.provideTextColor(operation);
    }
}
