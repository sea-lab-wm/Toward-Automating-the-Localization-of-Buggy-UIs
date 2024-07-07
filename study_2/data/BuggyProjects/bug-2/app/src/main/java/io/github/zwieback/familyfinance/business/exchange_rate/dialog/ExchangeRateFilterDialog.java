package io.github.zwieback.familyfinance.business.exchange_rate.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.johnpetitto.validator.ValidatingTextInputLayout;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.currency.activity.CurrencyActivity;
import io.github.zwieback.familyfinance.business.exchange_rate.filter.ExchangeRateFilter;
import io.github.zwieback.familyfinance.core.dialog.EntityFilterDialog;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.databinding.DialogFilterExchangeRateBinding;
import io.github.zwieback.familyfinance.widget.ClearableEditText;
import io.reactivex.functions.Consumer;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.CURRENCY_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_CURRENCY_ID;
import static io.github.zwieback.familyfinance.business.exchange_rate.filter.ExchangeRateFilter.EXCHANGE_RATE_FILTER;
import static io.github.zwieback.familyfinance.util.DateUtils.calendarDateToLocalDate;
import static io.github.zwieback.familyfinance.util.DateUtils.localDateToString;
import static io.github.zwieback.familyfinance.util.DateUtils.stringToLocalDate;
import static io.github.zwieback.familyfinance.util.DialogUtils.showDatePickerDialog;
import static io.github.zwieback.familyfinance.util.NumberUtils.stringToBigDecimal;
import static io.github.zwieback.familyfinance.util.StringUtils.isTextNotEmpty;

public class ExchangeRateFilterDialog
        extends EntityFilterDialog<ExchangeRateFilter, DialogFilterExchangeRateBinding> {

    public static ExchangeRateFilterDialog newInstance(ExchangeRateFilter filter) {
        ExchangeRateFilterDialog fragment = new ExchangeRateFilterDialog();
        Bundle args = createArguments(EXCHANGE_RATE_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CURRENCY_CODE:
                int currencyId = extractId(resultIntent, RESULT_CURRENCY_ID);
                loadCurrency(currencyId);
                break;
        }
    }

    @Override
    protected ExchangeRateFilter createCopyOfFilter(ExchangeRateFilter filter) {
        return new ExchangeRateFilter(filter);
    }

    @Override
    protected String getInputFilterName() {
        return EXCHANGE_RATE_FILTER;
    }

    @Override
    protected int getDialogTitle() {
        return R.string.exchange_rate_filter_title;
    }

    @Override
    protected int getDialogLayoutId() {
        return R.layout.dialog_filter_exchange_rate;
    }

    @Override
    protected void bind(ExchangeRateFilter filter) {
        binding.setFilter(filter);

        getCurrencyEdit().setOnClickListener(this::onCurrencyClick);
        getCurrencyEdit().setOnClearTextListener(this::onCurrencyRemoved);
        getStartDateEdit().setOnClickListener(this::onStartDateClick);
        getEndDateEdit().setOnClickListener(this::onEndDateClick);

        loadCurrency(filter.getCurrencyId());

        super.bind(filter);
    }

    private void onCurrencyClick(View view) {
        Intent intent = new Intent(getContext(), CurrencyActivity.class);
        startActivityForResult(intent, CURRENCY_CODE);
    }

    private void onStartDateClick(View view) {
        LocalDate startDate = determineDate(getStartDateEdit(), filter.getStartDate());
        showDatePickerDialog(getContext(), startDate, (datePicker, year, month, day) -> {
            LocalDate date = calendarDateToLocalDate(year, month, day);
            loadStartDate(date);
        });
    }

    private void onEndDateClick(View view) {
        LocalDate endDate = determineDate(getEndDateEdit(), filter.getEndDate());
        showDatePickerDialog(getContext(), endDate, (datePicker, year, month, day) -> {
            LocalDate date = calendarDateToLocalDate(year, month, day);
            loadEndDate(date);
        });
    }

    private void onCurrencyRemoved() {
        filter.setCurrencyId(null);
    }

    private Consumer<Currency> onSuccessfulCurrencyFound() {
        return foundCurrency -> {
            filter.setCurrencyId(foundCurrency.getId());
            getCurrencyEdit().setText(foundCurrency.getName());
        };
    }

    private void loadCurrency(@Nullable Integer currencyId) {
        if (currencyId != null) {
            loadEntity(Currency.class, currencyId, onSuccessfulCurrencyFound());
        }
    }

    private void loadStartDate(@Nullable LocalDate date) {
        filter.setStartDate(date);
        getStartDateEdit().setText(localDateToString(date));
    }

    private void loadEndDate(@Nullable LocalDate date) {
        filter.setEndDate(date);
        getEndDateEdit().setText(localDateToString(date));
    }

    @Override
    protected List<ValidatingTextInputLayout> getLayoutsForValidation() {
        List<ValidatingTextInputLayout> layouts = new ArrayList<>();
        if (isTextNotEmpty(getStartDateEdit().getText().toString())) {
            layouts.add(getStartDateLayout());
        }
        if (isTextNotEmpty(getEndDateEdit().getText().toString())) {
            layouts.add(getEndDateLayout());
        }
        if (isTextNotEmpty(getStartValueEdit().getText().toString())) {
            layouts.add(getStartValueLayout());
        }
        if (isTextNotEmpty(getEndValueEdit().getText().toString())) {
            layouts.add(getEndValueLayout());
        }
        return layouts;
    }

    /**
     * Update filter.
     * Don't check for {@code null} because the check was completed in {@link #noneErrorFound()}.
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void updateFilterProperties() {
        filter.setStartDate(stringToLocalDate(getStartDateEdit().getText().toString()));
        filter.setEndDate(stringToLocalDate(getEndDateEdit().getText().toString()));
        filter.setStartValue(stringToBigDecimal(getStartValueEdit().getText().toString()));
        filter.setEndValue(stringToBigDecimal(getEndValueEdit().getText().toString()));
    }

    private ClearableEditText getCurrencyEdit() {
        return binding.currency;
    }

    private EditText getStartDateEdit() {
        return binding.startDate;
    }

    private EditText getEndDateEdit() {
        return binding.endDate;
    }

    private EditText getStartValueEdit() {
        return binding.startValue;
    }

    private EditText getEndValueEdit() {
        return binding.endValue;
    }

    private ValidatingTextInputLayout getStartDateLayout() {
        return binding.startDateLayout;
    }

    private ValidatingTextInputLayout getEndDateLayout() {
        return binding.endDateLayout;
    }

    private ValidatingTextInputLayout getStartValueLayout() {
        return binding.startValueLayout;
    }

    private ValidatingTextInputLayout getEndValueLayout() {
        return binding.endValueLayout;
    }
}
