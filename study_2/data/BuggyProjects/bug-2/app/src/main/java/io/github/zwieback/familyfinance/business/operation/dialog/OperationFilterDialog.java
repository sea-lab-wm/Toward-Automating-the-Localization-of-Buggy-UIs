package io.github.zwieback.familyfinance.business.operation.dialog;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.johnpetitto.validator.ValidatingTextInputLayout;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.github.zwieback.familyfinance.business.account.activity.AccountActivity;
import io.github.zwieback.familyfinance.business.currency.activity.CurrencyActivity;
import io.github.zwieback.familyfinance.business.operation.filter.OperationFilter;
import io.github.zwieback.familyfinance.business.person.activity.PersonActivity;
import io.github.zwieback.familyfinance.core.dialog.EntityFilterDialog;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Article;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.core.model.Person;
import io.github.zwieback.familyfinance.widget.ClearableEditText;
import io.reactivex.functions.Consumer;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ACCOUNT_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.CURRENCY_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.PERSON_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ACCOUNT_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_CURRENCY_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_PERSON_ID;
import static io.github.zwieback.familyfinance.util.DateUtils.calendarDateToLocalDate;
import static io.github.zwieback.familyfinance.util.DateUtils.localDateToString;
import static io.github.zwieback.familyfinance.util.DateUtils.stringToLocalDate;
import static io.github.zwieback.familyfinance.util.DialogUtils.showDatePickerDialog;
import static io.github.zwieback.familyfinance.util.NumberUtils.bigDecimalToString;
import static io.github.zwieback.familyfinance.util.NumberUtils.stringToBigDecimal;
import static io.github.zwieback.familyfinance.util.StringUtils.isTextNotEmpty;

abstract class OperationFilterDialog<F extends OperationFilter, B extends ViewDataBinding>
        extends EntityFilterDialog<F, B> {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ACCOUNT_CODE:
                int accountId = extractId(resultIntent, RESULT_ACCOUNT_ID);
                loadAccount(accountId);
                break;
            case PERSON_CODE:
                int ownerId = extractId(resultIntent, RESULT_PERSON_ID);
                loadOwner(ownerId);
                break;
            case CURRENCY_CODE:
                int currencyId = extractId(resultIntent, RESULT_CURRENCY_ID);
                loadCurrency(currencyId);
                break;
        }
    }

    @Override
    protected void bind(F filter) {
        getAccountEdit().setOnClickListener(this::onAccountClick);
        getAccountEdit().setOnClearTextListener(this::onAccountRemoved);
        getOwnerEdit().setOnClickListener(this::onOwnerClick);
        getOwnerEdit().setOnClearTextListener(this::onOwnerRemoved);
        getCurrencyEdit().setOnClickListener(this::onCurrencyClick);
        getCurrencyEdit().setOnClearTextListener(this::onCurrencyRemoved);
        getStartDateEdit().setOnClickListener(this::onStartDateClick);
        getEndDateEdit().setOnClickListener(this::onEndDateClick);

        loadAccount(filter.getAccountId());
        loadOwner(filter.getOwnerId());
        loadCurrency(filter.getCurrencyId());
        loadStartDate(filter.getStartDate());
        loadEndDate(filter.getEndDate());
        loadStartValue(filter.getStartValue());
        loadEndValue(filter.getEndValue());

        super.bind(filter);
    }

    @SuppressWarnings("unused")
    private void onAccountClick(View view) {
        Intent intent = new Intent(getContext(), AccountActivity.class);
        startActivityForResult(intent, ACCOUNT_CODE);
    }

    @SuppressWarnings("unused")
    private void onOwnerClick(View view) {
        Intent intent = new Intent(getContext(), PersonActivity.class);
        startActivityForResult(intent, PERSON_CODE);
    }

    @SuppressWarnings("unused")
    private void onCurrencyClick(View view) {
        Intent intent = new Intent(getContext(), CurrencyActivity.class);
        startActivityForResult(intent, CURRENCY_CODE);
    }

    @SuppressWarnings("unused")
    private void onStartDateClick(View view) {
        LocalDate startDate = determineDate(getStartDateEdit(), filter.getStartDate());
        showDatePickerDialog(getContext(), startDate, (datePicker, year, month, day) -> {
            LocalDate date = calendarDateToLocalDate(year, month, day);
            loadStartDate(date);
        });
    }

    @SuppressWarnings("unused")
    private void onEndDateClick(View view) {
        LocalDate endDate = determineDate(getEndDateEdit(), filter.getEndDate());
        showDatePickerDialog(getContext(), endDate, (datePicker, year, month, day) -> {
            LocalDate date = calendarDateToLocalDate(year, month, day);
            loadEndDate(date);
        });
    }

    private void onAccountRemoved() {
        filter.setAccountId(null);
    }

    private void onOwnerRemoved() {
        filter.setOwnerId(null);
    }

    private void onCurrencyRemoved() {
        filter.setCurrencyId(null);
    }

    private Consumer<Article> onSuccessfulArticleFound() {
        return foundArticle -> {
            filter.setArticleId(foundArticle.getId());
            getArticleEdit().setText(foundArticle.getName());
        };
    }

    private Consumer<Account> onSuccessfulAccountFound() {
        return foundAccount -> {
            filter.setAccountId(foundAccount.getId());
            getAccountEdit().setText(foundAccount.getName());
        };
    }

    private Consumer<Person> onSuccessfulOwnerFound() {
        return foundOwner -> {
            filter.setOwnerId(foundOwner.getId());
            getOwnerEdit().setText(foundOwner.getName());
        };
    }

    private Consumer<Currency> onSuccessfulCurrencyFound() {
        return foundCurrency -> {
            filter.setCurrencyId(foundCurrency.getId());
            getCurrencyEdit().setText(foundCurrency.getName());
        };
    }

    final void loadArticle(@Nullable Integer articleId) {
        if (articleId != null) {
            loadEntity(Article.class, articleId, onSuccessfulArticleFound());
        }
    }

    private void loadAccount(@Nullable Integer accountId) {
        if (accountId != null) {
            loadEntity(Account.class, accountId, onSuccessfulAccountFound());
        }
    }

    private void loadOwner(@Nullable Integer ownerId) {
        if (ownerId != null) {
            loadEntity(Person.class, ownerId, onSuccessfulOwnerFound());
        }
    }

    private void loadCurrency(@Nullable Integer currencyId) {
        if (currencyId != null) {
            loadEntity(Currency.class, currencyId, onSuccessfulCurrencyFound());
        }
    }

    private void loadStartDate(@NonNull LocalDate date) {
        filter.setStartDate(date);
        getStartDateEdit().setText(localDateToString(date));
    }

    private void loadEndDate(@NonNull LocalDate date) {
        filter.setEndDate(date);
        getEndDateEdit().setText(localDateToString(date));
    }

    private void loadStartValue(@Nullable BigDecimal value) {
        getStartValueEdit().setText(bigDecimalToString(value));
    }

    private void loadEndValue(@Nullable BigDecimal value) {
        getEndValueEdit().setText(bigDecimalToString(value));
    }

    @Override
    protected List<ValidatingTextInputLayout> getLayoutsForValidation() {
        List<ValidatingTextInputLayout> layouts = new ArrayList<>();
        layouts.add(getStartDateLayout());
        layouts.add(getEndDateLayout());
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

    abstract ClearableEditText getArticleEdit();

    abstract ClearableEditText getAccountEdit();

    abstract ClearableEditText getOwnerEdit();

    abstract ClearableEditText getCurrencyEdit();

    abstract EditText getStartDateEdit();

    abstract EditText getEndDateEdit();

    abstract EditText getStartValueEdit();

    abstract EditText getEndValueEdit();

    abstract ValidatingTextInputLayout getStartDateLayout();

    abstract ValidatingTextInputLayout getEndDateLayout();

    abstract ValidatingTextInputLayout getStartValueLayout();

    abstract ValidatingTextInputLayout getEndValueLayout();
}
