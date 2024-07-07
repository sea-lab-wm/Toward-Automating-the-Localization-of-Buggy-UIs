package io.github.zwieback.familyfinance.business.operation.dialog;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.widget.EditText;

import com.johnpetitto.validator.ValidatingTextInputLayout;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.article.activity.IncomeArticleActivity;
import io.github.zwieback.familyfinance.business.operation.filter.IncomeOperationFilter;
import io.github.zwieback.familyfinance.databinding.DialogFilterIncomeOperationBinding;
import io.github.zwieback.familyfinance.widget.ClearableEditText;

import static io.github.zwieback.familyfinance.business.operation.filter.IncomeOperationFilter.INCOME_OPERATION_FILTER;

public class IncomeOperationFilterDialog extends OperationWithArticleFilterDialog<
        IncomeOperationFilter, DialogFilterIncomeOperationBinding, IncomeArticleActivity> {

    public static IncomeOperationFilterDialog newInstance(IncomeOperationFilter filter) {
        IncomeOperationFilterDialog fragment = new IncomeOperationFilterDialog();
        Bundle args = createArguments(INCOME_OPERATION_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    public static IncomeOperationFilterDialog newInstance(IncomeOperationFilter filter,
                                                          @StringRes int dialogTitleId) {
        IncomeOperationFilterDialog fragment = new IncomeOperationFilterDialog();
        Bundle args = createArguments(INCOME_OPERATION_FILTER, filter);
        args.putInt(DIALOG_TITLE, dialogTitleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected final IncomeOperationFilter createCopyOfFilter(IncomeOperationFilter filter) {
        return new IncomeOperationFilter(filter);
    }

    @Override
    protected final String getInputFilterName() {
        return INCOME_OPERATION_FILTER;
    }

    @Override
    protected final int getDialogTitle() {
        return R.string.income_operation_filter_title;
    }

    @Override
    protected final int getDialogLayoutId() {
        return R.layout.dialog_filter_income_operation;
    }

    @Override
    protected final void bind(IncomeOperationFilter filter) {
        binding.setFilter(filter);
        super.bind(filter);
    }

    @Override
    final Class<IncomeArticleActivity> getArticleActivityClass() {
        return IncomeArticleActivity.class;
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
