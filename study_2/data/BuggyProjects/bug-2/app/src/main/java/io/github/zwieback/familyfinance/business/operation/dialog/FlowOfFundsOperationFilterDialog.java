package io.github.zwieback.familyfinance.business.operation.dialog;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.widget.EditText;

import com.johnpetitto.validator.ValidatingTextInputLayout;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.article.activity.AllArticleActivity;
import io.github.zwieback.familyfinance.business.operation.filter.FlowOfFundsOperationFilter;
import io.github.zwieback.familyfinance.databinding.DialogFilterFlowOfFundsOperationBinding;
import io.github.zwieback.familyfinance.widget.ClearableEditText;

import static io.github.zwieback.familyfinance.business.operation.filter.FlowOfFundsOperationFilter.FLOW_OF_FUNDS_OPERATION_FILTER;

public class FlowOfFundsOperationFilterDialog extends OperationWithArticleFilterDialog<
        FlowOfFundsOperationFilter, DialogFilterFlowOfFundsOperationBinding, AllArticleActivity> {

    public static FlowOfFundsOperationFilterDialog newInstance(FlowOfFundsOperationFilter filter) {
        FlowOfFundsOperationFilterDialog fragment = new FlowOfFundsOperationFilterDialog();
        Bundle args = createArguments(FLOW_OF_FUNDS_OPERATION_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    public static FlowOfFundsOperationFilterDialog newInstance(FlowOfFundsOperationFilter filter,
                                                               @StringRes int dialogTitleId) {
        FlowOfFundsOperationFilterDialog fragment = new FlowOfFundsOperationFilterDialog();
        Bundle args = createArguments(FLOW_OF_FUNDS_OPERATION_FILTER, filter);
        args.putInt(DIALOG_TITLE, dialogTitleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected final FlowOfFundsOperationFilter
    createCopyOfFilter(FlowOfFundsOperationFilter filter) {
        return new FlowOfFundsOperationFilter(filter);
    }

    @Override
    protected final String getInputFilterName() {
        return FLOW_OF_FUNDS_OPERATION_FILTER;
    }

    @Override
    protected final int getDialogTitle() {
        return R.string.flow_of_funds_operation_filter_title;
    }

    @Override
    protected final int getDialogLayoutId() {
        return R.layout.dialog_filter_flow_of_funds_operation;
    }

    @Override
    protected final void bind(FlowOfFundsOperationFilter filter) {
        binding.setFilter(filter);
        super.bind(filter);
    }

    @Override
    final Class<AllArticleActivity> getArticleActivityClass() {
        return AllArticleActivity.class;
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
