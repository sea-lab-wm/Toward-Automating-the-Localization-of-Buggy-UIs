package io.github.zwieback.familyfinance.business.operation.service.provider;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mikepenz.iconics.typeface.IIcon;

import io.github.zwieback.familyfinance.business.operation.service.provider.exception.UndefinedOperationProviderException;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.OperationView;

public class FlowOfFundsOperationViewProvider extends EntityProvider<OperationView> {

    private final IncomeOperationViewProvider incomeProvider;
    private final ExpenseOperationViewProvider expenseProvider;
    private final TransferOperationViewProvider transferProvider;

    public FlowOfFundsOperationViewProvider(Context context) {
        super(context);
        incomeProvider = new IncomeOperationViewProvider(context);
        expenseProvider = new ExpenseOperationViewProvider(context);
        transferProvider = new TransferOperationViewProvider(context);
    }

    @NonNull
    @Override
    public IIcon provideDefaultIcon(OperationView operation) {
        return determineProvider(operation).provideDefaultIcon(operation);
    }

    @Override
    public int provideDefaultIconColor(OperationView operation) {
        return determineProvider(operation).provideDefaultIconColor(operation);
    }

    @Override
    public int provideTextColor(OperationView operation) {
        return determineProvider(operation).provideTextColor(operation);
    }

    private EntityProvider<OperationView> determineProvider(OperationView operation) {
        switch (operation.getType()) {
            case EXPENSE_OPERATION:
                return expenseProvider;
            case INCOME_OPERATION:
                return incomeProvider;
            case TRANSFER_EXPENSE_OPERATION:
            case TRANSFER_INCOME_OPERATION:
                return transferProvider;
        }
        throw new UndefinedOperationProviderException(operation.getType());
    }
}
