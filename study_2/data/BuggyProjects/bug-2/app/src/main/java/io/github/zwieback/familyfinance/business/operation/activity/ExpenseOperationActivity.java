package io.github.zwieback.familyfinance.business.operation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.operation.activity.helper.ExpenseOperationHelper;
import io.github.zwieback.familyfinance.business.operation.dialog.ExpenseOperationFilterDialog;
import io.github.zwieback.familyfinance.business.operation.filter.ExpenseOperationFilter;
import io.github.zwieback.familyfinance.business.operation.fragment.ExpenseOperationFragment;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.OperationView;

import static io.github.zwieback.familyfinance.business.operation.filter.ExpenseOperationFilter.EXPENSE_OPERATION_FILTER;

public class ExpenseOperationActivity
        extends OperationActivity<ExpenseOperationFragment, ExpenseOperationFilter> {

    private ExpenseOperationHelper operationHelper;

    @Override
    protected int getTitleStringId() {
        return R.string.expense_operation_activity_title;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        operationHelper = new ExpenseOperationHelper(this, data);
    }

    @NonNull
    @Override
    protected String getFilterName() {
        return EXPENSE_OPERATION_FILTER;
    }

    @NonNull
    @Override
    protected ExpenseOperationFilter createDefaultFilter() {
        ExpenseOperationFilter filter = new ExpenseOperationFilter();
        filter.setArticleId(databasePrefs.getExpensesArticleId());
        return filter;
    }

    @Override
    protected ExpenseOperationFragment createFragment() {
        return ExpenseOperationFragment.newInstance(filter);
    }

    @Override
    protected void addEntity() {
        super.addEntity();
        Intent intent = operationHelper.getIntentToAdd(filter);
        startActivity(intent);
    }

    @Override
    protected void editEntity(OperationView operation) {
        super.editEntity(operation);
        Intent intent = operationHelper.getIntentToEdit(operation);
        startActivity(intent);
    }

    @Override
    protected void duplicateEntity(OperationView operation) {
        super.duplicateEntity(operation);
        Intent intent = operationHelper.getIntentToDuplicate(operation);
        startActivity(intent);
    }

    @Override
    protected EntityDestroyer<Operation> createDestroyer(OperationView operation) {
        return operationHelper.createDestroyer(operation);
    }

    @Override
    protected void showFilterDialog() {
        DialogFragment dialog = ExpenseOperationFilterDialog.newInstance(filter);
        dialog.show(getSupportFragmentManager(), "ExpenseOperationFilterDialog");
    }
}
