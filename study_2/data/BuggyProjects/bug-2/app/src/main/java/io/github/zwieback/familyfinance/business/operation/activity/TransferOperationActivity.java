package io.github.zwieback.familyfinance.business.operation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.operation.activity.helper.TransferOperationHelper;
import io.github.zwieback.familyfinance.business.operation.dialog.TransferOperationFilterDialog;
import io.github.zwieback.familyfinance.business.operation.filter.TransferOperationFilter;
import io.github.zwieback.familyfinance.business.operation.fragment.TransferOperationFragment;
import io.github.zwieback.familyfinance.business.operation.lifecycle.destroyer.TransferOperationForceDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.OperationView;

import static io.github.zwieback.familyfinance.business.operation.filter.TransferOperationFilter.TRANSFER_OPERATION_FILTER;

public class TransferOperationActivity
        extends OperationActivity<TransferOperationFragment, TransferOperationFilter> {

    private TransferOperationHelper operationHelper;

    @Override
    protected int getTitleStringId() {
        return R.string.transfer_operation_activity_title;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        operationHelper = new TransferOperationHelper(this, data);
    }

    @NonNull
    @Override
    protected String getFilterName() {
        return TRANSFER_OPERATION_FILTER;
    }

    @NonNull
    @Override
    protected TransferOperationFilter createDefaultFilter() {
        TransferOperationFilter filter = new TransferOperationFilter();
        filter.setArticleId(databasePrefs.getTransferArticleId());
        return filter;
    }

    @Override
    protected TransferOperationFragment createFragment() {
        return TransferOperationFragment.newInstance(filter);
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
        return new TransferOperationForceDestroyer(this, data);
    }

    @Override
    protected void showFilterDialog() {
        DialogFragment dialog = TransferOperationFilterDialog.newInstance(filter);
        dialog.show(getSupportFragmentManager(), "TransferOperationFilterDialog");
    }
}
