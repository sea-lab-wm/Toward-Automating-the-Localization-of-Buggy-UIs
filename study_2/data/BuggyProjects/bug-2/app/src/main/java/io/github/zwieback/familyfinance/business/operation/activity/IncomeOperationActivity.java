package io.github.zwieback.familyfinance.business.operation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.operation.activity.helper.IncomeOperationHelper;
import io.github.zwieback.familyfinance.business.operation.dialog.IncomeOperationFilterDialog;
import io.github.zwieback.familyfinance.business.operation.filter.IncomeOperationFilter;
import io.github.zwieback.familyfinance.business.operation.fragment.IncomeOperationFragment;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.OperationView;

import static io.github.zwieback.familyfinance.business.operation.filter.IncomeOperationFilter.INCOME_OPERATION_FILTER;

public class IncomeOperationActivity
        extends OperationActivity<IncomeOperationFragment, IncomeOperationFilter> {

    private IncomeOperationHelper operationHelper;

    @Override
    protected int getTitleStringId() {
        return R.string.income_operation_activity_title;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        operationHelper = new IncomeOperationHelper(this, data);
    }

    @NonNull
    @Override
    protected String getFilterName() {
        return INCOME_OPERATION_FILTER;
    }

    @NonNull
    @Override
    protected IncomeOperationFilter createDefaultFilter() {
        IncomeOperationFilter filter = new IncomeOperationFilter();
        filter.setArticleId(databasePrefs.getIncomesArticleId());
        return filter;
    }

    @Override
    protected IncomeOperationFragment createFragment() {
        return IncomeOperationFragment.newInstance(filter);
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
        DialogFragment dialog = IncomeOperationFilterDialog.newInstance(filter);
        dialog.show(getSupportFragmentManager(), "IncomeOperationFilterDialog");
    }
}
