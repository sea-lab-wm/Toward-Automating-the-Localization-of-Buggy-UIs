package io.github.zwieback.familyfinance.business.template.activity.helper;

import android.content.Context;
import android.support.annotation.NonNull;

import io.github.zwieback.familyfinance.business.operation.activity.helper.ExpenseOperationHelper;
import io.github.zwieback.familyfinance.business.operation.activity.helper.IncomeOperationHelper;
import io.github.zwieback.familyfinance.business.operation.activity.helper.OperationHelper;
import io.github.zwieback.familyfinance.business.operation.activity.helper.TransferOperationHelper;
import io.github.zwieback.familyfinance.business.template.exception.UnsupportedTemplateTypeException;
import io.github.zwieback.familyfinance.core.model.TemplateView;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public final class TemplateQualifier {

    @NonNull
    private final IncomeOperationHelper incomeOperationHelper;
    @NonNull
    private final ExpenseOperationHelper expenseOperationHelper;
    @NonNull
    private final TransferOperationHelper transferOperationHelper;

    public TemplateQualifier(@NonNull Context context, @NonNull ReactiveEntityStore<Persistable> data) {
        this.incomeOperationHelper = new IncomeOperationHelper(context, data);
        this.expenseOperationHelper = new ExpenseOperationHelper(context, data);
        this.transferOperationHelper = new TransferOperationHelper(context, data);
    }

    @NonNull
    public OperationHelper<?> determineHelper(@NonNull TemplateView template) {
        switch (template.getType()) {
            case EXPENSE_OPERATION:
                return expenseOperationHelper;
            case INCOME_OPERATION:
                return incomeOperationHelper;
            case TRANSFER_OPERATION:
                return transferOperationHelper;
            default:
                throw new UnsupportedTemplateTypeException(template.getId(), template.getType());
        }
    }
}
