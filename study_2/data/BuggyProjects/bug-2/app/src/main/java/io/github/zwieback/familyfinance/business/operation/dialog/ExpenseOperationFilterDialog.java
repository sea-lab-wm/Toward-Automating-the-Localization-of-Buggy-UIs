package io.github.zwieback.familyfinance.business.operation.dialog;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.widget.EditText;

import com.johnpetitto.validator.ValidatingTextInputLayout;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.article.activity.ExpenseArticleActivity;
import io.github.zwieback.familyfinance.business.operation.filter.ExpenseOperationFilter;
import io.github.zwieback.familyfinance.databinding.DialogFilterExpenseOperationBinding;
import io.github.zwieback.familyfinance.widget.ClearableEditText;

import static io.github.zwieback.familyfinance.business.operation.filter.ExpenseOperationFilter.EXPENSE_OPERATION_FILTER;

public class ExpenseOperationFilterDialog extends OperationWithArticleFilterDialog<
        ExpenseOperationFilter, DialogFilterExpenseOperationBinding, ExpenseArticleActivity> {

    public static ExpenseOperationFilterDialog newInstance(ExpenseOperationFilter filter) {
        ExpenseOperationFilterDialog fragment = new ExpenseOperationFilterDialog();
        Bundle args = createArguments(EXPENSE_OPERATION_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    public static ExpenseOperationFilterDialog newInstance(ExpenseOperationFilter filter,
                                                           @StringRes int dialogTitleId) {
        ExpenseOperationFilterDialog fragment = new ExpenseOperationFilterDialog();
        Bundle args = createArguments(EXPENSE_OPERATION_FILTER, filter);
        args.putInt(DIALOG_TITLE, dialogTitleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected final ExpenseOperationFilter createCopyOfFilter(ExpenseOperationFilter filter) {
        return new ExpenseOperationFilter(filter);
    }

    @Override
    protected final String getInputFilterName() {
        return EXPENSE_OPERATION_FILTER;
    }

    @Override
    protected final int getDialogTitle() {
        return R.string.expense_operation_filter_title;
    }

    @Override
    protected final int getDialogLayoutId() {
        return R.layout.dialog_filter_expense_operation;
    }

    @Override
    protected final void bind(ExpenseOperationFilter filter) {
        binding.setFilter(filter);
        super.bind(filter);
    }

    @Override
    final Class<ExpenseArticleActivity> getArticleActivityClass() {
        return ExpenseArticleActivity.class;
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
