package io.github.zwieback.familyfinance.business.operation.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import org.threeten.bp.LocalDate;

import io.github.zwieback.familyfinance.business.account.activity.AccountActivity;
import io.github.zwieback.familyfinance.business.currency.activity.CurrencyActivity;
import io.github.zwieback.familyfinance.business.exchange_rate.activity.ExchangeRateActivity;
import io.github.zwieback.familyfinance.business.exchange_rate.helper.ExchangeRateFinder;
import io.github.zwieback.familyfinance.business.person.activity.PersonActivity;
import io.github.zwieback.familyfinance.core.activity.EntityEditActivity;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.core.model.ExchangeRate;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.Person;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.github.zwieback.familyfinance.widget.ClearableEditText;
import io.reactivex.functions.Consumer;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.CURRENCY_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.EXCHANGE_RATE_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.PERSON_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_CURRENCY_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_EXCHANGE_RATE_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_PERSON_ID;
import static io.github.zwieback.familyfinance.core.activity.EntityActivity.INPUT_READ_ONLY;
import static io.github.zwieback.familyfinance.util.DateUtils.calendarDateToLocalDate;
import static io.github.zwieback.familyfinance.util.DateUtils.isTextAnLocalDate;
import static io.github.zwieback.familyfinance.util.DateUtils.localDateToString;
import static io.github.zwieback.familyfinance.util.DateUtils.now;
import static io.github.zwieback.familyfinance.util.DateUtils.stringToLocalDate;
import static io.github.zwieback.familyfinance.util.DialogUtils.showDatePickerDialog;
import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;
import static io.github.zwieback.familyfinance.util.NumberUtils.stringToBigDecimal;

abstract class OperationEditActivity<B extends ViewDataBinding>
        extends EntityEditActivity<Operation, B>
        implements DatePickerDialog.OnDateSetListener {

    @Override
    protected Class<Operation> getEntityClass() {
        return Operation.class;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PERSON_CODE:
                int ownerId = extractOutputId(resultIntent, RESULT_PERSON_ID);
                loadOwner(ownerId);
                break;
            case CURRENCY_CODE:
                int currencyId = extractOutputId(resultIntent, RESULT_CURRENCY_ID);
                loadCurrency(currencyId);
                break;
            case EXCHANGE_RATE_CODE:
                int exchangeRateId = extractOutputId(resultIntent, RESULT_EXCHANGE_RATE_ID);
                loadExchangeRate(exchangeRateId);
                break;
        }
    }

    public void onDateClick(View view) {
        LocalDate date = determineDate();
        showDatePickerDialog(getSupportFragmentManager(), date);
    }

    /**
     * Don't check for {@code null} because the check was completed in {@link #isCorrectDate()}.
     */
    @SuppressWarnings("ConstantConditions")
    @NonNull
    private LocalDate determineDate() {
        if (isCorrectDate()) {
            return stringToLocalDate(getDateEdit().getText().toString());
        }
        return entity.getDate();
    }

    private boolean isCorrectDate() {
        return isTextAnLocalDate(getDateEdit().getText().toString());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        LocalDate date = calendarDateToLocalDate(year, month, day);
        getDateEdit().setText(localDateToString(date));
    }

    public void onOwnerClick(View view) {
        Intent intent = new Intent(this, PersonActivity.class);
        startActivityForResult(intent, PERSON_CODE);
    }

    public void onCurrencyClick(View view) {
        Intent intent = new Intent(this, CurrencyActivity.class);
        intent.putExtra(INPUT_READ_ONLY, false);
        startActivityForResult(intent, CURRENCY_CODE);
    }

    public void onExchangeRateClick(View view) {
        Intent intent = new Intent(this, ExchangeRateActivity.class);
        intent.putExtra(ExchangeRateActivity.INPUT_CURRENCY_ID, determineCurrencyId());
        intent.putExtra(INPUT_READ_ONLY, false);
        startActivityForResult(intent, EXCHANGE_RATE_CODE);
    }

    final void startAccountActivity(int requestCode) {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra(AccountActivity.INPUT_ONLY_ACTIVE, true);
        startActivityForResult(intent, requestCode);
    }

    private int determineCurrencyId() {
        if (entity.getExchangeRate() == null) {
            return ID_AS_NULL;
        }
        return entity.getExchangeRate().getCurrency().getId();
    }

    @Nullable
    private ExchangeRate findLastExchangeRate(int currencyId) {
        return new ExchangeRateFinder(data).findLastExchangeRate(currencyId);
    }

    private Consumer<Person> onSuccessfulOwnerFound() {
        return foundOwner -> entity.setOwner(foundOwner);
    }

    private Consumer<Currency> onSuccessfulCurrencyFound() {
        return foundCurrency -> {
            ExchangeRate exchangeRate = findLastExchangeRate(foundCurrency.getId());
            entity.setExchangeRate(exchangeRate);
        };
    }

    private Consumer<ExchangeRate> onSuccessfulExchangeRateFound() {
        return foundExchangeRate -> entity.setExchangeRate(foundExchangeRate);
    }

    final void loadOwner(int ownerId) {
        loadEntity(Person.class, ownerId, onSuccessfulOwnerFound());
    }

    final void loadCurrency(int currencyId) {
        loadEntity(Currency.class, currencyId, onSuccessfulCurrencyFound());
    }

    final void loadExchangeRate(int exchangeRateId) {
        loadEntity(ExchangeRate.class, exchangeRateId, onSuccessfulExchangeRateFound());
    }

    @Override
    protected void createEntity() {
        Operation operation = createOperation();
        bind(operation);
    }

    Operation createOperation() {
        Operation operation = new Operation();
        operation.setType(getOperationType());
        operation.setDate(now());
        return operation;
    }

    @CallSuper
    @Override
    protected void setupBindings() {
        getOwnerEdit().setOnClearTextListener(() -> entity.setOwner(null));
        getCurrencyEdit().setOnClearTextListener(() -> entity.setExchangeRate(null));
        getExchangeRateEdit().setOnClearTextListener(() -> entity.setExchangeRate(null));
    }

    @Override
    protected void updateEntityProperties(Operation operation) {
        operation.setDate(stringToLocalDate(getDateEdit().getText().toString()));
        operation.setValue(stringToBigDecimal(getValueEdit().getText().toString()));
        operation.setDescription(getDescriptionEdit().getText().toString());
        operation.setUrl(getUrlEdit().getText().toString());
    }

    abstract OperationType getOperationType();

    abstract ClearableEditText getOwnerEdit();

    abstract ClearableEditText getCurrencyEdit();

    abstract ClearableEditText getExchangeRateEdit();

    abstract EditText getDateEdit();

    abstract EditText getValueEdit();

    abstract EditText getDescriptionEdit();

    abstract EditText getUrlEdit();
}
