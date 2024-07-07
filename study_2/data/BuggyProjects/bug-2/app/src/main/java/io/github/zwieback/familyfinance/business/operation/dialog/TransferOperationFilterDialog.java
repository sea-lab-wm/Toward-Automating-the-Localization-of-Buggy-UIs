package io.github.zwieback.familyfinance.business.operation.dialog;

import android.os.Bundle;
import android.widget.EditText;

import com.johnpetitto.validator.ValidatingTextInputLayout;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.operation.filter.TransferOperationFilter;
import io.github.zwieback.familyfinance.databinding.DialogFilterTransferOperationBinding;
import io.github.zwieback.familyfinance.widget.ClearableEditText;

import static io.github.zwieback.familyfinance.business.operation.filter.TransferOperationFilter.TRANSFER_OPERATION_FILTER;

public class TransferOperationFilterDialog extends OperationFilterDialog<TransferOperationFilter,
        DialogFilterTransferOperationBinding> {

    public static TransferOperationFilterDialog newInstance(TransferOperationFilter filter) {
        TransferOperationFilterDialog fragment = new TransferOperationFilterDialog();
        Bundle args = createArguments(TRANSFER_OPERATION_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected final TransferOperationFilter createCopyOfFilter(TransferOperationFilter filter) {
        return new TransferOperationFilter(filter);
    }

    @Override
    protected final String getInputFilterName() {
        return TRANSFER_OPERATION_FILTER;
    }

    @Override
    protected final int getDialogTitle() {
        return R.string.transfer_operation_filter_title;
    }

    @Override
    protected final int getDialogLayoutId() {
        return R.layout.dialog_filter_transfer_operation;
    }

    @Override
    protected void bind(TransferOperationFilter filter) {
        binding.setFilter(filter);
        loadArticle(databasePrefs.getTransferArticleId());
        super.bind(filter);
    }

    @Override
    final ClearableEditText getArticleEdit() {
        return binding.article;
    }

    @Override
    final ClearableEditText getAccountEdit() {
        return binding.account;
    }

    @Override
    final ClearableEditText getOwnerEdit() {
        return binding.owner;
    }

    @Override
    final ClearableEditText getCurrencyEdit() {
        return binding.currency;
    }

    @Override
    final EditText getStartDateEdit() {
        return binding.startDate;
    }

    @Override
    final EditText getEndDateEdit() {
        return binding.endDate;
    }

    @Override
    final EditText getStartValueEdit() {
        return binding.startValue;
    }

    @Override
    final EditText getEndValueEdit() {
        return binding.endValue;
    }

    @Override
    final ValidatingTextInputLayout getStartDateLayout() {
        return binding.startDateLayout;
    }

    @Override
    final ValidatingTextInputLayout getEndDateLayout() {
        return binding.endDateLayout;
    }

    @Override
    final ValidatingTextInputLayout getStartValueLayout() {
        return binding.startValueLayout;
    }

    @Override
    final ValidatingTextInputLayout getEndValueLayout() {
        return binding.endValueLayout;
    }
}
