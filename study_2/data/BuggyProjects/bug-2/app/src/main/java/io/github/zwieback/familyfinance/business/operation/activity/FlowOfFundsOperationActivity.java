package io.github.zwieback.familyfinance.business.operation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.MenuItem;

import java.util.Collections;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.operation.activity.exception.IllegalOperationTypeException;
import io.github.zwieback.familyfinance.business.operation.activity.helper.ExpenseOperationHelper;
import io.github.zwieback.familyfinance.business.operation.activity.helper.IncomeOperationHelper;
import io.github.zwieback.familyfinance.business.operation.activity.helper.OperationHelper;
import io.github.zwieback.familyfinance.business.operation.activity.helper.TransferOperationHelper;
import io.github.zwieback.familyfinance.business.operation.dialog.FlowOfFundsOperationFilterDialog;
import io.github.zwieback.familyfinance.business.operation.filter.FlowOfFundsOperationFilter;
import io.github.zwieback.familyfinance.business.operation.fragment.FlowOfFundsOperationFragment;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.OperationView;

import static io.github.zwieback.familyfinance.business.operation.filter.FlowOfFundsOperationFilter.FLOW_OF_FUNDS_OPERATION_FILTER;

public class FlowOfFundsOperationActivity
        extends OperationActivity<FlowOfFundsOperationFragment, FlowOfFundsOperationFilter> {

    private IncomeOperationHelper incomeOperationHelper;
    private ExpenseOperationHelper expenseOperationHelper;
    private TransferOperationHelper transferOperationHelper;

    @Override
    protected List<Integer> collectMenuIds() {
        if (!readOnly) {
            return Collections.singletonList(R.menu.menu_entity_flow_of_funds_operation);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_expense:
                addExpenseOperation();
                return true;
            case R.id.action_add_income:
                addIncomeOperation();
                return true;
            case R.id.action_add_transfer:
                addTransferOperation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getTitleStringId() {
        return R.string.flow_of_funds_activity_title;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        incomeOperationHelper = new IncomeOperationHelper(this, data);
        expenseOperationHelper = new ExpenseOperationHelper(this, data);
        transferOperationHelper = new TransferOperationHelper(this, data);
    }

    @NonNull
    @Override
    protected String getFilterName() {
        return FLOW_OF_FUNDS_OPERATION_FILTER;
    }

    @NonNull
    @Override
    protected FlowOfFundsOperationFilter createDefaultFilter() {
        return new FlowOfFundsOperationFilter();
    }

    @Override
    protected FlowOfFundsOperationFragment createFragment() {
        return FlowOfFundsOperationFragment.newInstance(filter);
    }

    private void addExpenseOperation() {
        super.addEntity();
        Intent intent = expenseOperationHelper.getIntentToAdd();
        startActivity(intent);
    }

    private void addIncomeOperation() {
        super.addEntity();
        Intent intent = incomeOperationHelper.getIntentToAdd();
        startActivity(intent);
    }

    private void addTransferOperation() {
        super.addEntity();
        Intent intent = transferOperationHelper.getIntentToAdd();
        startActivity(intent);
    }

    @Override
    protected void editEntity(OperationView operation) {
        super.editEntity(operation);
        Intent intent = determineHelper(operation).getIntentToEdit(operation);
        startActivity(intent);
    }

    @Override
    protected void duplicateEntity(OperationView operation) {
        super.duplicateEntity(operation);
        Intent intent = determineHelper(operation).getIntentToDuplicate(operation);
        startActivity(intent);
    }

    @Override
    protected EntityDestroyer<Operation> createDestroyer(OperationView operation) {
        return determineHelper(operation).createDestroyer(operation);
    }

    @Override
    protected void showFilterDialog() {
        DialogFragment dialog = FlowOfFundsOperationFilterDialog.newInstance(filter);
        dialog.show(getSupportFragmentManager(), "FlowOfFundsOperationFilterDialog");
    }

    private OperationHelper<?> determineHelper(OperationView operation) {
        switch (operation.getType()) {
            case EXPENSE_OPERATION:
                return expenseOperationHelper;
            case INCOME_OPERATION:
                return incomeOperationHelper;
            case TRANSFER_EXPENSE_OPERATION:
            case TRANSFER_INCOME_OPERATION:
                return transferOperationHelper;
            default:
                throw IllegalOperationTypeException.unsupportedOperationType(operation);
        }
    }
}
